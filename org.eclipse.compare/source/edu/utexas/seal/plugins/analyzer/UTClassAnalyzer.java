/*
 * @(#) UTClassAnalyzer.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.analyzer;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.swt.graphics.Point;

import ch.uzh.ifi.seal.changedistiller.model.classifiers.SourceRange;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.NodeType;
import edu.utexas.seal.plugins.ast.UTASTClassVisitor;
import edu.utexas.seal.plugins.ast.UTASTSelectionVisitor;
import edu.utexas.seal.plugins.crystal.PostDominateAnalysisFactory;
import edu.utexas.seal.plugins.crystal.PostDominateElementResult;
import edu.utexas.seal.plugins.crystal.internal.SourceCodeRange;
import edu.utexas.seal.plugins.crystal.internal.UTASTNodeSearcher;
import edu.utexas.seal.plugins.crystal.internal.UTASTNodesCollector;
import edu.utexas.seal.plugins.util.UTCriticsPairFileInfo;

// TODO: Auto-generated Javadoc
public class UTClassAnalyzer extends ASTVisitor {
	Point						region	= null;
	MethodDeclaration			method	= null;
	ArrayList<TypeDeclaration>	types	= null;
	ArrayList<ASTNode>			nodes;
	ArrayList<ASTNode>			localDecl;
	ArrayList<ASTNode>			fieldDecl;
	ArrayList<ASTNode>			methodDecl;
	ArrayList<ASTNode>			sameNames;
	ArrayList<ASTNode>			containers;
	ArrayList<ASTNode>			dependentors;
	ArrayList<ASTNode>			relevantNodes;
	Map<ASTNode, List<ASTNode>>	data_maps;
	Map<ASTNode, List<ASTNode>>	control_maps;
	Map<ASTNode, Node>			node_maps;
	PostDominateElementResult	result;
	ICompilationUnit			icu;
	private boolean				isLeft;

	/**
	 * Instantiates a new uT class analyzer.
	 * 
	 * @param region the region
	 * @param isLeft the is left
	 */
	public UTClassAnalyzer(Point region, boolean isLeft) {
		this.region = region;
		types = new ArrayList<TypeDeclaration>();
		nodes = new ArrayList<ASTNode>();
		localDecl = new ArrayList<ASTNode>();
		fieldDecl = new ArrayList<ASTNode>();
		methodDecl = new ArrayList<ASTNode>();
		sameNames = new ArrayList<ASTNode>();
		dependentors = new ArrayList<ASTNode>();
		relevantNodes = new ArrayList<ASTNode>();
		containers = new ArrayList<ASTNode>();
		data_maps = new HashMap<ASTNode, List<ASTNode>>();
		control_maps = new HashMap<ASTNode, List<ASTNode>>();
		node_maps = new HashMap<ASTNode, Node>();
		if (isLeft) {
			icu = UTCriticsPairFileInfo.getLeftICompilationUnit();
		} else {
			icu = UTCriticsPairFileInfo.getRightICompilationUnit();
		}
	}

	/**
	 * Analyze.
	 * 
	 * @param visitor the visitor
	 */
	public void analyze(UTASTClassVisitor visitor) {
		// 1. Get Selected Nodes
		getSelectedNodes();

		System.out.println();

		// 2. Get Declaration Statements or Expressions
		// 3. TRACK DOWN every same reference if declaration is changed
		for (ASTNode node : nodes) {
			UTASTSelectionVisitor selVisitor = new UTASTSelectionVisitor(visitor, isLeft);
			node.accept(selVisitor);
			ArrayList<ASTNode> decls = new ArrayList<ASTNode>();
			for (ASTNode local : selVisitor.getLocalDecl()) {
				decls.add(local);
				localDecl.add(local);
			}

			for (ASTNode field : selVisitor.getFieldDecl()) {
				decls.add(field);
				fieldDecl.add(field);
			}

			for (ASTNode method : selVisitor.getMethodDecl()) {
				decls.add(method);
				methodDecl.add(method);
			}

			// Build mappings between user selected node and data dominators
			// This map will be used when constructing graph later
			data_maps.put(node, decls);
			ArrayList<ASTNode> names = selVisitor.getSameNames();
			for (ASTNode n : names) {
				if (!sameNames.contains(n)) {
					sameNames.add(n);
				}
			}
		}

		// 4. Analyze Control Dependency
		UTASTNodesCollector collector = new UTASTNodesCollector(region);
		method.accept(collector);
		List<ASTNode> allNodes = collector.getNodes();
		for (ASTNode node : allNodes) {
			result = PostDominateAnalysisFactory.getInstance().getAnalysisResultForMethod(icu, method);
			List<SourceCodeRange> dependRanges = result.getUpStreamControlDependency(node);
			for (SourceCodeRange range : dependRanges) {
				UTASTNodeSearcher searcher = new UTASTNodeSearcher(range);
				ASTNode target = searcher.search(method);
				if (target == null) {
					System.out.println("Error: Each Dependentor(SourceCodeRange) should have corresponding ASTNode.");
				} else {
					if (!dependentors.contains(target)) {
						dependentors.add(target);
					}
				}
			}
			// for(SourceCodeRange dependentor : dependentors){
			// printer(dependentor);
			// }
		}

		// 5. Analyze Containment Dependency
		for (ASTNode node : nodes) {
			getContainers(node);
		}

		// 6. Merge these three analysis results to a single list
		mergeAnalysisResults();

		// Print Declaration Statements or Expressions
		System.out.println("********  2. Get Declarations ********* ");
		for (ASTNode node : localDecl) {
			System.out.println("-----------------");
			System.out.println(node);
			System.out.println("-----------------");
		}
		System.out.println("********  2. End *********************** ");

		// Print Same References
		System.out.println("********  3. TRACK DOWN Same References");
		for (ASTNode node : sameNames) {
			System.out.println("-----------------");
			System.out.println(node);
			System.out.println("-----------------");
		}
		System.out.println("********  3. End *********************** ");

		// Print Dependentors
		System.out.println("********  4. Control Dependency ********");
		for (ASTNode node : dependentors) {
			System.out.println("-----------------");
			System.out.println(node);
			System.out.println("-----------------");
		}
		System.out.println("********  4. End *********************** ");

		// Print Containers
		System.out.println("********  5. Containment Dependency ********");
		for (ASTNode node : containers) {
			System.out.println("-----------------");
			System.out.println(node);
			System.out.println("-----------------");
		}
		System.out.println("********  5. End *********************** ");
	}

	/**
	 * Get containers for a node recursively
	 */
	private void getContainers(ASTNode node) {

		if (node.getParent() instanceof IfStatement) {
			if (!containers.contains(node.getParent())) {
				containers.add((IfStatement) node.getParent());
			}
		} else if (node.getParent() instanceof WhileStatement) {
			if (!containers.contains(node.getParent())) {
				containers.add((WhileStatement) node.getParent());
			}
		} else if (node.getParent() instanceof ForStatement) {
			if (!containers.contains(node.getParent())) {
				containers.add((ForStatement) node.getParent());
			}
		} else if (node.getParent() instanceof EnhancedForStatement) {
			if (!containers.contains(node.getParent())) {
				containers.add((EnhancedForStatement) node.getParent());
			}
		} else if (node.getParent() instanceof DoStatement) {
			if (!containers.contains(node.getParent())) {
				containers.add((DoStatement) node.getParent());
			}
		} else if (node.getParent() instanceof SwitchStatement) {
			if (!containers.contains(node.getParent())) {
				containers.add((SwitchStatement) node.getParent());
			}
		} else if (node.getParent() instanceof TryStatement) {
			if (!containers.contains(node.getParent())) {
				containers.add((TryStatement) node.getParent());
			}
		} else if (node.getParent() instanceof Block) {
			if (!containers.contains(node.getParent())) {
				containers.add((Block) node.getParent());
			}
		} else if (node.getParent() instanceof CatchClause) {
			if (!containers.contains(node.getParent())) {
				containers.add((CatchClause) node.getParent());
			}
		}

		if (node instanceof MethodDeclaration) {
			return;
		} else {
			getContainers(node.getParent());
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MethodDeclaration)
	 */
	public boolean visit(MethodDeclaration node) {
		// Get method which contains selection
		if (hasSelection(node)) {
			this.method = node;
		}
		return true;
	}

	/**
	 * Gets the selected nodes.
	 * 
	 * @return the selected nodes
	 */
	private void getSelectedNodes() {
		ASTNode root = (ASTNode) this.method;
		getMinimalNodes(root);
		System.out.println("********  1. Get Selected Nodes ********* ");
		for (ASTNode node : nodes) {
			System.out.println("-----------------");
			System.out.println(node);
			System.out.println("-----------------");
		}
		System.out.println("********  1. End *********************** ");
	}

	/**
	 * Gets the minimal nodes.
	 * 
	 * @param node the node
	 * @return the minimal nodes
	 */
	private boolean getMinimalNodes(ASTNode node) {
		boolean all = true;
		if (!hasSelection(node) && withinSelection(node)) {
			// a subnode of user selection
			return true;
		} else if (hasSelection(node) && !withinSelection(node)) {
			// a supernode of user selection
			// check children
			List<?> list = node.structuralPropertiesForType();
			ArrayList<ASTNode> selectedChildren = new ArrayList<ASTNode>();
			for (int i = 0; i < list.size(); i++) {
				StructuralPropertyDescriptor curr = (StructuralPropertyDescriptor) list.get(i);
				Object child = node.getStructuralProperty(curr);
				if (child instanceof ASTNode) {
					if (getMinimalNodes((ASTNode) child)) {
						// child is subnode of user selection
						// keep 'all' to be true;
						selectedChildren.add((ASTNode) child);
					} else {
						all = false;
					}
				} else if (child instanceof List) {
					for (int j = 0; j < ((List<?>) child).size(); j++) {
						ASTNode n = (ASTNode) ((List<?>) child).get(j);
						if (getMinimalNodes(n)) {
							// child is subnode of user selection
							// keep 'all' to be true;
							selectedChildren.add(n);
						} else {
							all = false;
						}
					}
				}
			}
			if (all == false) {
				// This node is the least node that covers user selection
				// But not all children are selected
				for (ASTNode n : selectedChildren) {
					nodes.add(n);
				}
			} else {
				// This node is exactly the least node that covers user
				// selection
				// User selection is a single node
				nodes.add(node);
			}
			return false;
		} else if (!hasSelection(node) && !withinSelection(node)) {
			// rule out
			return false;
		} else if (hasSelection(node) && withinSelection(node)) {
			nodes.add(node);
			return false;
		} else {
			return false;
		}
	}

	/**
	 * Checks for selection.
	 * 
	 * @param node the node
	 * @return true, if successful
	 */
	private boolean hasSelection(ASTNode node) {
		int offset_bgn = -1, offset_end = -1, offset_sel_bgn = -1, offset_sel_end = -1;

		if (region != null) {
			offset_bgn = node.getStartPosition();
			offset_end = offset_bgn + node.getLength();
			offset_sel_bgn = region.x;
			offset_sel_end = region.x + region.y;
			if ((offset_bgn <= offset_sel_bgn) && (offset_end >= offset_sel_end)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Within selection.
	 * 
	 * @param node the node
	 * @return true, if successful
	 */
	private boolean withinSelection(ASTNode node) {
		int offset_bgn = -1, offset_end = -1, offset_sel_bgn = -1, offset_sel_end = -1;

		if (region != null) {
			offset_bgn = node.getStartPosition();
			offset_end = offset_bgn + node.getLength();
			offset_sel_bgn = region.x;
			offset_sel_end = region.x + region.y;
			if ((offset_bgn >= offset_sel_bgn) && (offset_end <= offset_sel_end)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Merge analysis results.
	 */
	public void mergeAnalysisResults() {
		relevantNodes.clear();

		for (ASTNode node : localDecl) {
			if (!relevantNodes.contains(node)) {
				relevantNodes.add(node);
			}
		}

		for (ASTNode node : sameNames) {
			if (!relevantNodes.contains(node)) {
				relevantNodes.add(node);
			}
		}

		for (ASTNode node : dependentors) {
			if (!relevantNodes.contains(node)) {
				relevantNodes.add(node);
			}
		}

		for (ASTNode node : containers) {
			if (!relevantNodes.contains(node)) {
				// relevantNodes.add(node);
			}
		}
	}

	public List<ASTNode> getRelevantNodes() {
		return this.relevantNodes;
	}

	/**
	 * 
	 * This method is to mark all context nodes, including user selected nodes and analysis results
	 * in the query tree.
	 * This method should be called after completing analysis.
	 * 
	 * @param root
	 */
	public void convertToNode(Node root) {
		// We didn't convert any fields at the beginning
		// So now we need to add the related fields to the root
		for (ASTNode field : fieldDecl) {
			if (field instanceof FieldDeclaration) {
				field = (FieldDeclaration) field;
				Iterator<?> iter = ((FieldDeclaration) field).fragments().iterator();
				while (iter.hasNext()) {
					VariableDeclarationFragment f = (VariableDeclarationFragment) iter.next();
					SimpleName fieldName = f.getName();
					Node fieldNode = new Node(JavaEntityType.FIELD_DECLARATION, fieldName.getIdentifier());
					fieldNode.setEntity(new SourceCodeEntity(fieldName.getIdentifier(), JavaEntityType.FIELD_DECLARATION, new SourceRange(f.getStartPosition(), f.getStartPosition() + f.getLength() - 1)));
					fieldNode.setMarkAliveNode();
					fieldNode.setSelectedByUser(true);
					fieldNode.setNodeType(NodeType.Data_Dependency);
					root.add(fieldNode);
					if (!node_maps.containsKey(field)) {
						node_maps.put(field, fieldNode);
					}
				}
			}
		}

		// So are the related methods
		for (ASTNode method : methodDecl) {
			if (method instanceof MethodDeclaration) {
				String methodName = ((MethodDeclaration) method).getName().getIdentifier();
				Node methodNode = new Node(JavaEntityType.METHOD_DECLARATION, methodName);
				methodNode.setEntity(new SourceCodeEntity(methodName, JavaEntityType.METHOD_DECLARATION, new SourceRange(method.getStartPosition(), method.getStartPosition() + method.getLength() - 1)));
				methodNode.setMarkAliveNode();
				methodNode.setSelectedByUser(true);
				methodNode.setNodeType(NodeType.Data_Dependency);
				root.add(methodNode);
				if (!node_maps.containsKey(method)) {
					node_maps.put(method, methodNode);
				}
			}
		}

		// Travel query tree in post order
		Enumeration<?> postOrder = root.postorderEnumeration();
		while (postOrder.hasMoreElements()) {
			Node curNode = (Node) postOrder.nextElement();
			int curBgn = curNode.getEntity().getStartPosition();
			int curEnd = curNode.getEntity().getEndPosition();

			// If this node is selected by user, mark it alive and set node type as User_Selection
			for (ASTNode node : nodes) {
				int bgn = node.getStartPosition();
				int end = node.getLength() + bgn;

				// The length of variable declaration statement in for init ast node is 1 character
				// less than corresponding Node object. So I add another possible condition 'curEnd == end'.
				if (curBgn == bgn && (curEnd + 1 == end || curEnd == end)) {
					curNode.setSelectedByUser(true);
					curNode.setMarkAliveNode();
					curNode.setNodeType(NodeType.User_Selection);
					checkAllChildren(curNode, NodeType.User_Selection);
					checkAllParents(curNode);
					if (!node_maps.containsKey(node)) {
						node_maps.put(node, curNode);
					}
					break;
				}
			}

			// If this node is the data dependentor, mark it alive and set node type as Data_Dependency
			for (ASTNode node : localDecl) {
				int bgn = node.getStartPosition();
				int end = node.getLength() + bgn;

				// The structure for method parameters is very weird
				// For example, the structure for void foo(int x, int y) is like
				// METHOD(foo)
				// |
				// PARAMATERS(int x, int y)
				// / \
				// PARAMETER(x) PARAMETER(y)
				// | |
				// SINGLE_TYPE(int) SINGLE_TYPE(int)
				// In that case, some declaration ast node like 'int x' will not find a matched
				// Node instance.

				if (curNode.getLabel() == JavaEntityType.PARAMETERS) {
					break;
				}

				if (curNode.getLabel() == JavaEntityType.PARAMETER) {
					if (curBgn >= bgn && curEnd <= end) {
						curNode.setSelectedByUser(true);
						curNode.setMarkAliveNode();
						curNode.setNodeType(NodeType.Data_Dependency);
						checkAllChildren(curNode, NodeType.Data_Dependency);
						checkAllParents(curNode);
						if (!node_maps.containsKey(node)) {
							node_maps.put(node, curNode);
						}
						break;
					}
				} else {
					// The length of variable declaration statement in for init ast node is 1 character
					// less than corresponding Node object. So I add another possible condition 'curEnd == end'.
					if (curBgn == bgn && (curEnd + 1 == end || curEnd == end)) {
						curNode.setSelectedByUser(true);
						curNode.setMarkAliveNode();
						curNode.setNodeType(NodeType.Data_Dependency);
						checkAllChildren(curNode, NodeType.Data_Dependency);
						checkAllParents(curNode);
						if (!node_maps.containsKey(node)) {
							node_maps.put(node, curNode);
						}
						break;
					}
				}

			}

			// If this node is the control dependentor, mark it alive and set node type as Control_Dependency
			// Sometimes the dependentor is likely to be the expression of for, while statement.
			// In that case, we have to mark the for or while statement alive, but we don't have to check all its children.
			for (ASTNode node : dependentors) {
				// If this dependentor is an expression, get parent of this node
				if (node instanceof Expression) {
					node = node.getParent();
				}

				int bgn = node.getStartPosition();
				int end = node.getLength() + bgn;

				if (curBgn == bgn && (curEnd + 1 == end || curEnd == end)) {
					curNode.setSelectedByUser(true);
					curNode.setMarkAliveNode();
					curNode.setNodeType(NodeType.Control_Dependency);
					checkAllParents(curNode);
					if (!node_maps.containsKey(node)) {
						node_maps.put(node, curNode);
					}
					break;
				}
			}

			// If this node is the container, mark it alive and set node type as Containment_Dependency
			for (ASTNode node : containers) {
				int bgn = node.getStartPosition();
				int end = node.getLength() + bgn;

				// The length of variable declaration statement in for init ast node is 1 character
				// less than corresponding Node object. So I add another possible condition 'curEnd == end'.
				if (curBgn == bgn && (curEnd + 1 == end || curEnd == end)) {
					curNode.setSelectedByUser(true);
					curNode.setMarkAliveNode();
					if (curNode.getNodeType() != NodeType.Control_Dependency) {
						curNode.setNodeType(NodeType.Containment_Dependency);
					}
					checkAllParents(curNode);
					if (!node_maps.containsKey(node)) {
						node_maps.put(node, curNode);
					}
					break;
				}
			}
		}

		// Specify the data dependentor for user-selected nodes
		for (ASTNode ast_node : nodes) {
			Node node = node_maps.get(ast_node);
			if (node != null) {
				if (data_maps.containsKey(ast_node)) {
					for (ASTNode decl : data_maps.get(ast_node)) {
						Node declNode = node_maps.get(decl);
						if (declNode != null) {
							node.addDataDependentor(declNode);
						}
					}
				}
			}
		}
	}

	/**
	 * Check all children.
	 * 
	 * @param rootNode the root node
	 * @param type the type
	 */
	public void checkAllChildren(Node rootNode, NodeType type) {
		List<Node> workList = new ArrayList<Node>();
		workList.addAll(rootNode.getChildren());
		while (!workList.isEmpty()) {
			Node nodeElem = workList.remove(0);
			nodeElem.setMarkAliveNode();
			nodeElem.setNodeType(type);
			if (nodeElem.getChildCount() != 0) {
				workList.addAll(nodeElem.getChildren());
			}
		}
	}

	/**
	 * Check all parents.
	 * 
	 * @param node the node
	 */
	public void checkAllParents(Node node) {
		if (node != null) {
			node.setSelectedByUser(true);
			node.setMarkAliveNode();
			if (node.getNodeType() == NodeType.Unrelevant) {
				node.setNodeType(NodeType.Containment_Dependency);
			}
			if (((Node) node.getParent()) == null || ((Node) node.getParent()).isMarkAliveNode())
				return;
			checkAllParents((Node) node.getParent());
		}
	}
}
