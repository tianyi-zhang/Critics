/*
 * @(#) CriticsGeneralization.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.generization;

import java.io.File;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import ut.seal.plugins.utils.ast.UTASTNodeConverter;
import ut.seal.plugins.utils.ast.UTASTNodeOperation;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.SourceRange;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import edu.utexas.seal.plugins.crystal.internal.UTASTNodeSearcher;
import edu.utexas.seal.plugins.handler.CriticsSelectContext;
import edu.utexas.seal.plugins.treegraph.view.CriticsContextSelectionView;
import edu.utexas.seal.plugins.util.UTCriticsPairFileInfo;
import edu.utexas.seal.plugins.util.UTPlugin;

@SuppressWarnings("restriction")
public class CriticsGeneralization {
	private Node				queryTreeOldRev	= null;
	private Node				queryTreeNewRev	= null;
	private Node				qTreeGenNewRev	= null;
	private Node				qTreeGenOldRev	= null;
	private ASTNode				rootOld			= null;
	private ASTNode				rootNew			= null;
	private ICompilationUnit	cuOld			= null;
	private ICompilationUnit	cuNew			= null;
	private SourceRange			oldRange		= null;
	private SourceRange			newRange		= null;
	private boolean				trace			= true;

	/**
	 * Instantiates a new critics generalization.
	 */
	public CriticsGeneralization() {
		CriticsContextSelectionView vwIntCxtSelOldRev = (CriticsContextSelectionView) UTPlugin.getView(UTPlugin.ID_VIEW_SELECTCTX_OLDREV);
		CriticsContextSelectionView vwIntCxtSelNewRev = (CriticsContextSelectionView) UTPlugin.getView(UTPlugin.ID_VIEW_SELECTCTX_NEWREV);
		if (vwIntCxtSelOldRev.getRootNode() == null || vwIntCxtSelNewRev.getRootNode() == null) {
			System.out.println("[USG] PLEASE OPEN CONTEXT SELECTION VIEW");
			return;
		}

		queryTreeOldRev = (Node) vwIntCxtSelOldRev.getRootNode().getData();
		queryTreeNewRev = (Node) vwIntCxtSelNewRev.getRootNode().getData();

		cuOld = UTCriticsPairFileInfo.getRightICompilationUnit();
		cuNew = UTCriticsPairFileInfo.getLeftICompilationUnit();

		rootOld = CriticsSelectContext.rootOld;
		rootNew = CriticsSelectContext.rootNew;

		// Get methods node
		// Heads-Up: The method node has to be in the rootOld/rootNew
		// Because all our generalization is operated on the rootOld/rootNew, we have to keep identical
		oldRange = queryTreeOldRev.getEntity().getSourceRange();
		newRange = queryTreeNewRev.getEntity().getSourceRange();
	}

	/**
	 * Generalize q tree old rev.
	 */
	public void generalizeQTreeOldRev() {
		// get copy of the AST tree for old version
		ASTNode copy = CriticsSelectContext.getRoot(queryTreeOldRev);
		MethodFinder mfOld = new MethodFinder(oldRange);
		copy.accept(mfOld);
		MethodDeclaration methodOld = mfOld.getMethod();

		// replace all SimpelName with "$var"
		Enumeration<?> e = queryTreeOldRev.postorderEnumeration();
		Node temp = null;
		while (e.hasMoreElements()) {
			temp = (Node) e.nextElement();
			// if (temp.isExcludedByUser()) {
			// // TODO: Remove from the query tree but not exclude its children
			// // TODO: 1. get all children of excluded node 2. remove from parent 3. append all children to its parent
			// // TODO: alternative: replace excluded node label and value with a strange one
			// temp.removeFromParent();
			// } else {
			Map<SimpleName, Boolean> map = temp.getParamMap();
			for (SimpleName var : map.keySet()) {
				if (map.get(var)) {
					UTASTNodeSearcher searcher = new UTASTNodeSearcher(var);
					SimpleName varCopy = (SimpleName) searcher.search(copy);
					generalizeType(varCopy);
					assert varCopy == null;
//					IBinding binding = varCopy.resolveBinding();
//					int type = binding.getJavaElement().getElementType();
					String name = "$var";
//					switch (type) {
//					case JavaElement.LOCAL_VARIABLE:
//						break;
//					case JavaElement.FIELD:
//						name = "$f";
//						break;
//					case JavaElement.TYPE_PARAMETER:
//						name = "$p";
//						break;
//					default:
//						break;
//					}
					varCopy.setIdentifier(name);
				}
			}
			// }
		}

		// re-convert again
		UTASTNodeConverter converter = new UTASTNodeConverter();
		File fileOldRev = UTCriticsPairFileInfo.getRightFile();
		qTreeGenOldRev = converter.convertMethod(methodOld, copy.toString(), fileOldRev);
		// map user configurations in original query tree to this new query tree
		if (trace) {
			queryTreeOldRev.print();
		}
		reConfig(queryTreeOldRev, qTreeGenOldRev);
		UTASTNodeOperation operation = new UTASTNodeOperation();
		operation.pruneDensityOfTreeNode(qTreeGenOldRev, false);
		Map<Integer, Node> mNodeDepth = new TreeMap<Integer, Node>();
		e = qTreeGenOldRev.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			if (iNode.isSelectedByUser()) {
				mNodeDepth.put(iNode.getLevel(), iNode);
			}
		}
		if (!mNodeDepth.isEmpty()) {
			Node curTopNode = mNodeDepth.entrySet().iterator().next().getValue();
			Node curParNode = (Node) curTopNode.getParent();
			if (curParNode == null) {
				qTreeGenOldRev = curTopNode;
			} else {
				qTreeGenOldRev = curParNode;
			}
		}
		if (trace) {
			qTreeGenOldRev.print();
		}
	}

	private void generalizeType(SimpleName varCopy) {
		if(varCopy.isDeclaration()){
			// need to generalize the type and constructor
			ASTNode parent = varCopy.getParent();
			while(parent != null){
				if(parent instanceof SingleVariableDeclaration){
					Type type = ((SingleVariableDeclaration)parent).getType();
					if(type instanceof SimpleType){
						Name name = ((SimpleType) type).getName();
						if(name instanceof SimpleName){
							((SimpleName)name).setIdentifier("$var");
						}
					}
					Expression expr = ((SingleVariableDeclaration)parent).getInitializer();
					if( expr instanceof ClassInstanceCreation){
						Type type2 = ((ClassInstanceCreation)expr).getType();
						if(type2 instanceof SimpleType){
							Name name = ((SimpleType) type2).getName();
							if(name instanceof SimpleName){
								((SimpleName)name).setIdentifier("$var");
							}
						}
					}
					break;
				}
				if(parent instanceof VariableDeclarationFragment){
					ASTNode decl = parent.getParent();
					Type type = ((VariableDeclarationStatement)decl).getType();
					if(type instanceof SimpleType){
						Name name = ((SimpleType) type).getName();
						if(name instanceof SimpleName){
							((SimpleName)name).setIdentifier("$var");
						}
					}
					Expression expr = ((VariableDeclarationFragment)parent).getInitializer();
					if( expr instanceof ClassInstanceCreation){
						Type type2 = ((ClassInstanceCreation)expr).getType();
						if(type2 instanceof SimpleType){
							Name name = ((SimpleType) type2).getName();
							if(name instanceof SimpleName){
								((SimpleName)name).setIdentifier("$var");
							}
						}
					}
					break;
				}
			}
		}
	}

	/**
	 * Generalize q tree new rev.
	 */
	public void generalizeQTreeNewRev() {
		// get copy of the AST tree for old version
		ASTNode copy = CriticsSelectContext.getRoot(queryTreeNewRev);
		MethodFinder mfNew = new MethodFinder(newRange);
		copy.accept(mfNew);
		MethodDeclaration methodNew = mfNew.getMethod();

		// replace all SimpelName with "$var"
		Enumeration<?> e = queryTreeNewRev.postorderEnumeration();
		Node temp = null;
		while (e.hasMoreElements()) {
			temp = (Node) e.nextElement();
			// if (temp.isExcludedByUser()) {
			// temp.removeFromParent();
			// } else {
			Map<SimpleName, Boolean> map = temp.getParamMap();
			for (SimpleName var : map.keySet()) {
				if (map.get(var)) {
					UTASTNodeSearcher searcher = new UTASTNodeSearcher(var);
					SimpleName varCopy = (SimpleName) searcher.search(copy);
					generalizeType(varCopy);
					assert varCopy == null;
					varCopy.setIdentifier("$var");
				}
			}
			// }
		}

		// re-convert again
		UTASTNodeConverter converter = new UTASTNodeConverter();
		File fileNewRev = UTCriticsPairFileInfo.getLeftFile();
		qTreeGenNewRev = converter.convertMethod(methodNew, copy.toString(), fileNewRev);
		// map user configurations in original query tree to this new query tree
		if (trace) {
			queryTreeNewRev.print();
		}
		reConfig(queryTreeNewRev, qTreeGenNewRev);
		UTASTNodeOperation operation = new UTASTNodeOperation();
		operation.pruneDensityOfTreeNode(qTreeGenNewRev, false);
		Map<Integer, Node> mNodeDepth = new TreeMap<Integer, Node>();
		e = qTreeGenNewRev.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			if (iNode.isSelectedByUser()) {
				mNodeDepth.put(iNode.getLevel(), iNode);
			}
		}
		if (!mNodeDepth.isEmpty()) {
			Node curTopNode = mNodeDepth.entrySet().iterator().next().getValue();
			Node curParNode = (Node) curTopNode.getParent();
			if (curParNode == null) {
				qTreeGenNewRev = curTopNode;
			} else {
				qTreeGenNewRev = curParNode;
			}
		}
		if (trace) {
			qTreeGenNewRev.print();
		}
	}

	/**
	 * Reconfig.
	 * 
	 * @param source the source
	 * @param target the target
	 * @return true, if successful
	 */
	private boolean reConfig(Node source, Node target) {
		// TODO perform alignment. Target is always method declaration, but source is likely to be descendant of method

		if (isCorrespondingTo(source, target)) {
			if (source.isExcludedByUser()) {
				target.setLabel(JavaEntityType.EXCLUDED);
				target.setValue("");
				target.setExcludedByUser(true);
			}
			target.setSelectedByUser(source.isSelectedByUser());
			target.setMarkAliveNode();
			List<Node> children = source.getChildren();
			int counter = 0;
			for (Node node : children) {
				for (Node node2 : target.getChildren()) {
					if (reConfig(node, node2))
						counter++;
				}
			}
			if (counter == children.size())
				return true; // all children found a match
			else
				return false;
		} else {
			return false;
		}
	}

	/**
	 * Checks if is corresponding to.
	 * 
	 * @param source the source
	 * @param target the target
	 * @return true, if is corresponding to
	 */
	private boolean isCorrespondingTo(Node source, Node target) {
		if (source.getLabel() == target.getLabel()) {
			// then compare the value
			Map<SimpleName, Boolean> map = source.getParamMap();
			int paramVarCounter = 0;
			for (SimpleName var : map.keySet()) {
				if (map.get(var))
					paramVarCounter++;
			}
			if (paramVarCounter == 0) {
				// no variables are parameterized, value should be the same
				// corner case: parameter type
				if (source.getParent() != null) {
					if (((Node) source.getParent()).getLabel() == JavaEntityType.PARAMETER) {
						return true;
					}
				}
				// corner case: container like BLOCK, BODY, DO_STATEMENT, ELSE_STATEMENT, THEN_STATEMENT
				// TRY_STATEMENT, PARAMETERS and SYNCHRONIZED_STATEMENT have values but don't have variables
				if (source.getLabel() == JavaEntityType.BLOCK || source.getLabel() == JavaEntityType.BODY || source.getLabel() == JavaEntityType.DO_STATEMENT || source.getLabel() == JavaEntityType.ELSE_STATEMENT || source.getLabel() == JavaEntityType.TRY_STATEMENT || source.getLabel() == JavaEntityType.THEN_STATEMENT || source.getLabel() == JavaEntityType.SYNCHRONIZED_STATEMENT) {
					return true;
				}
				String value1 = source.getValue();
				String value2 = target.getValue();
				return value1.equals(value2);
			} else {
				// TODO refine fuzzy match
				String value = target.getValue();
				return value.contains("$var");
			}

		} else {
			return false;
		}
	}

	public Node getQTreeOldRev() {
		return queryTreeOldRev;
	}

	public Node getQTreeNewRev() {
		return queryTreeNewRev;
	}

	public Node getQTreeGenNewRev() {
		return qTreeGenNewRev;
	}

	public Node getQTreeGenOldRev() {
		return qTreeGenOldRev;
	}

	public ASTNode getRootOld() {
		return rootOld;
	}

	public ASTNode getRootNew() {
		return rootNew;
	}

	public ICompilationUnit getCuOld() {
		return cuOld;
	}

	public ICompilationUnit getCuNew() {
		return cuNew;
	}

	public SourceRange getOldRange() {
		return oldRange;
	}

	public SourceRange getNewRange() {
		return newRange;
	}
}

class MethodFinder extends ASTVisitor {
	private SourceRange			range;
	private MethodDeclaration	method	= null;

	public MethodFinder(SourceRange range) {
		this.range = range;
	}

	@Override
	public boolean visit(MethodDeclaration method) {
		int methodBgn = method.getStartPosition();
		int methodEnd = methodBgn + method.getLength();
		int bgn = range.getStart();
		int end = range.getEnd();
		if (methodBgn <= bgn && methodEnd >= end) {
			this.method = method;
		}
		return true;
	}

	public MethodDeclaration getMethod() {
		return this.method;
	}
}
