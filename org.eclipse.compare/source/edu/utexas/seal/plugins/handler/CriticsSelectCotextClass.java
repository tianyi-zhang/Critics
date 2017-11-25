/**
 * Copyright (c) 2017, UCLA Software Engineering and Analysis Laboratory (SEAL)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */
package edu.utexas.seal.plugins.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.swt.graphics.Point;

import rted.datastructure.manager.ITreeMatch;
import rted.datastructure.manager.TreeMatchBetweenQueryTrees;
import ut.seal.plugins.utils.UTChange;
import ut.seal.plugins.utils.UTFile;
import ut.seal.plugins.utils.UTLog;
import ut.seal.plugins.utils.UTTime;
import ut.seal.plugins.utils.ast.UTASTNodeConverter;
import ut.seal.plugins.utils.ast.UTASTNodeFinder;
import ut.seal.plugins.utils.ast.UTASTNodeOperation;
import ut.seal.plugins.utils.ast.UTASTParser;
import ut.seal.plugins.utils.change.UTChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.SourceRange;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import edu.utexas.seal.plugins.CriticsMatchTreeEditDistanceTestSubject;
import edu.utexas.seal.plugins.analyzer.UTJFileAnalyzer;
import edu.utexas.seal.plugins.analyzer.UTJFileDifferencer;
import edu.utexas.seal.plugins.ast.util.UTASTVariableCollector;
import edu.utexas.seal.plugins.crystal.internal.UTASTNodeSearcher;
import edu.utexas.seal.plugins.edit.UTEnumEdit;
import edu.utexas.seal.plugins.treegraph.view.CriticsContextSelectionView;
import edu.utexas.seal.plugins.util.UTCriticsPairFileInfo;
import edu.utexas.seal.plugins.util.UTCriticsTransform;
import edu.utexas.seal.plugins.util.UTPlugin;
import edu.utexas.seal.plugins.util.visitor.UTGeneralVisitor;
import edu.utexas.seal.plugins.util.visitor.UTIGeneralVisitor;

/**
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsSelectCotextClass extends CriticsCommonHandler {
	private UTJFileAnalyzer	analyzer	= null;

	/* (non-Javadoc)
	 * @see edu.utexas.seal.plugins.handler.CriticsCommonHandler#initiate()
	 */
	protected boolean initiate() {
		if (!super.initiate()) {
			return false;
		}
		UTLog.println(true, "DBG__________________________________________");
		UTLog.println(true, "[DBG] *** [CRITICS] SELECT CONTEXT *** ");
		UTLog.println(true, "DBG__________________________________________");
		return true;
	}

	/* (non-Javadoc)
	 * @see edu.utexas.seal.plugins.handler.CriticsCommonHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (!initiate()) {
			return null;
		}
		try {
			long begin = (new Date()).getTime();
			startExecute(event);
			matcherOldRev.getRTEDProc().timeTot = (new Date()).getTime() - begin;
		} catch (Exception e) {
			e.printStackTrace();
		}
		UTTime.timeStamp(matcherOldRev.getRTEDProc().timeTot);
		UTLog.println(false, "[RST] # OF MATCHES: " + String.valueOf(cntMatchedOldRev[0] - 1));
		UTLog.println(false, "[RST] # OF CLASSES: " + (cntClass - 1));
		UTLog.println(false, "[RST] # OF METHODS: " + (cntMethod - 1));
		UTLog.println(false, "DBG__________________________________________");
		return null;
	}

	/**
	 * Start execute.
	 * 
	 * @param event
	 *            the event
	 * @throws Exception
	 *             the exception
	 */
	private void startExecute(ExecutionEvent event) throws Exception {
		analyzeQueryTree();
		showTreeGraph();
	}

	/**
	 * Analyze query tree.
	 */
	private void analyzeQueryTree() {
		// 1. Static Program Analysis
		// getRelevantContextNodesFromUserSelection();
		// The difference result will be used to determine whether to track down every reference of a variable
		new UTJFileDifferencer();
		analyzer = new UTJFileAnalyzer();
		processQueryTreeOldRev(false);
		processQueryTreeNewRev(false);
		getEditsFromQTree();
		matcherQueryTree.matchEditMapping(qTreeOldRev, qTreeNewRev);

		// Set isLeft property as true for nodes in queryTreeNewRev
		// Don't have to set queryTreeOldRev because the default value for isLeft is false
		for (Node node : qTreeNewRev.getAllNodes()) {
			node.setIsLeft(true);
		}

		// Prepare mParamMap for each Node in each tree
		prepareTreeForDisplay(qTreeOldRev);
		prepareTreeForDisplay(qTreeNewRev);
	}

	private void prepareTreeForDisplay(Node root) {
		// Traverse all nodes in this query tree
		Enumeration<?> e = root.postorderEnumeration();
		Node temp = null;
		while (e.hasMoreElements()) {
			temp = (Node) e.nextElement();
			// Use collector to get all variables in each node
			List<SimpleName> vars = collectVariables(temp);
			if (vars == null)
				continue;
			for (SimpleName var : vars) {
				temp.addParamVar(var);
			}
		}

	}

	private List<SimpleName> collectVariables(Node node) {
		List<SimpleName> vars = null;
		// choose a proper scenario to collect variables in this node
		switch (node.getLabel().name()) {
		case "FOREACH_STATEMENT":
			vars = getVarsInForeach(node);
			break;
		case "FOR_STATEMENT":
			vars = getVarsInFor(node);
			break;
		case "BLOCK":
			break; // Do nothing
		case "BODY":
			break; // Do nothing
		case "DO_STATEMENT":
			break; // Do nothing
		case "ELSE_STATEMENT":
			break; // Do nothing
		case "IF_STATEMENT":
			vars = getVarsInIf(node);
			break;
		case "SWITCH_CASE":
			vars = getVarsInSwitch(node);
			break;
		case "THEN_STATEMENT":
			break; // Do nothing
		case "TRY_STATEMENT":
			break; // Do nothing
		case "WHILE_STATEMENT":
			vars = getVarsInWhile(node);
			break;
		default:
			vars = getVariables(node);
			break;
		}

		return vars;
	}

	private List<SimpleName> getVarsInWhile(Node node) {
		ASTNode root = getRoot(node);
		ASTNode src = getASTNode(node, root);
		List<SimpleName> vars = new ArrayList<SimpleName>();

		// Get the expression in WHILE STATEMENT
		if (src instanceof SwitchStatement) {
			Expression expr = ((SwitchStatement) src).getExpression();
			UTASTVariableCollector collector = new UTASTVariableCollector(new SourceRange(expr.getStartPosition(), expr.getStartPosition() + expr.getLength()));
			root.accept(collector);
			for (SimpleName var : collector.getIdentifiers()) {
				if (!vars.contains(var)) {
					vars.add(var);
				}
			}
		}

		return vars;
	}

	private List<SimpleName> getVarsInSwitch(Node node) {
		ASTNode root = getRoot(node);
		ASTNode src = getASTNode(node, root);
		List<SimpleName> vars = new ArrayList<SimpleName>();

		// Get the expression in SWITCH STATEMENT
		if (src instanceof SwitchStatement) {
			Expression expr = ((SwitchStatement) src).getExpression();
			UTASTVariableCollector collector = new UTASTVariableCollector(new SourceRange(expr.getStartPosition(), expr.getStartPosition() + expr.getLength()));
			root.accept(collector);
			for (SimpleName var : collector.getIdentifiers()) {
				if (!vars.contains(var)) {
					vars.add(var);
				}
			}
		}
		return vars;
	}

	private List<SimpleName> getVarsInIf(Node node) {
		ASTNode root = getRoot(node);
		ASTNode src = getASTNode(node, root);
		List<SimpleName> vars = new ArrayList<SimpleName>();

		// Get the expression node of guard condition in IF STATEMENT
		if (src instanceof IfStatement) {
			Expression expr = ((IfStatement) src).getExpression();
			UTASTVariableCollector exprCollector = new UTASTVariableCollector(new SourceRange(expr.getStartPosition(), expr.getStartPosition() + expr.getLength()));
			root.accept(exprCollector);
			for (SimpleName var : exprCollector.getIdentifiers()) {
				if (!vars.contains(var)) {
					vars.add(var);
				}
			}
		}
		return vars;
	}

	/**
	 * If user select For Statement, the ast node contains the entire for block.
	 * But we just want to fetch variables in the header of this for statement.
	 * This method helps to collect variables in the initializers, guard
	 * expression and updaters in the normal for statement
	 * 
	 * @param node
	 * @return
	 */
	private List<SimpleName> getVarsInFor(Node node) {
		ASTNode root = getRoot(node);
		ASTNode src = getASTNode(node, root);
		List<SimpleName> vars = new ArrayList<SimpleName>();

		// Get initializers, expression and updaters in normal for statement
		if (src instanceof ForStatement) {
			// List<?> inits = ((ForStatement) src).initializers();
			Expression expr = ((ForStatement) src).getExpression();
			// List<?> updaters = ((ForStatement) src).updaters();

			// Get variables in initializers
			// for (Object obj : inits) {
			// if (obj instanceof ASTNode) {
			// SourceRange range = new SourceRange(((ASTNode) obj).getStartPosition(), ((ASTNode) obj).getStartPosition()
			// + ((ASTNode) obj).getLength());
			// UTASTVariableCollector collector = new UTASTVariableCollector(range);
			// root.accept(collector);
			//
			// for (SimpleName var : collector.getIdentifiers()) {
			// if (!vars.contains(var)) {
			// vars.add(var);
			// }
			// }
			// }
			// }

			// Get variables in guard condition
			UTASTVariableCollector exprCollector = new UTASTVariableCollector(new SourceRange(expr.getStartPosition(), expr.getStartPosition() + expr.getLength()));
			root.accept(exprCollector);
			for (SimpleName var : exprCollector.getIdentifiers()) {
				if (!vars.contains(var)) {
					vars.add(var);
				}
			}

			// Get variables in updaters
			// for (Object obj : updaters) {
			// if (obj instanceof ASTNode) {
			// SourceRange range = new SourceRange(((ASTNode) obj).getStartPosition(), ((ASTNode) obj).getStartPosition()
			// + ((ASTNode) obj).getLength());
			// UTASTVariableCollector collector = new UTASTVariableCollector(range);
			// root.accept(collector);
			//
			// for (SimpleName var : collector.getIdentifiers()) {
			// if (!vars.contains(var)) {
			// vars.add(var);
			// }
			// }
			// }
			// }
		}
		return vars;
	}

	/**
	 * If user select EnhancedFor statement, the ast node contains the entire
	 * for block. But we just want to fetch variables in the header of this for
	 * statement. This method helps to collect variables in the normal parameter
	 * declaration and guard expression in the enhanced for statement
	 * 
	 * @param node
	 * @return
	 */
	private List<SimpleName> getVarsInForeach(Node node) {
		ASTNode root = getRoot(node);
		ASTNode src = getASTNode(node, root);
		List<SimpleName> vars = new ArrayList<SimpleName>();

		// Get expression and parameter declaration in enhanced for statement
		if (src instanceof EnhancedForStatement) {
			Expression expr = ((EnhancedForStatement) src).getExpression();
			SingleVariableDeclaration decl = ((EnhancedForStatement) src).getParameter();

			// Collect variables in enhanced for expression
			UTASTVariableCollector exprCollector = new UTASTVariableCollector(new SourceRange(expr.getStartPosition(), expr.getStartPosition() + expr.getLength()));
			root.accept(exprCollector);
			for (SimpleName var : exprCollector.getIdentifiers()) {
				if (!vars.contains(var)) {
					vars.add(var);
				}
			}

			// Collect variables in enhanced for parameter declariation
			UTASTVariableCollector paramCollector = new UTASTVariableCollector(new SourceRange(decl.getStartPosition(), decl.getStartPosition() + decl.getLength()));
			root.accept(paramCollector);
			for (SimpleName var : paramCollector.getIdentifiers()) {
				if (!vars.contains(var)) {
					vars.add(var);
				}
			}
		}

		return vars;
	}

	/**
	 * Get variables(identifiers) in the corresponding statement of the node
	 * 
	 * @param node
	 * @return
	 */
	private List<SimpleName> getVariables(Node node) {
		ASTNode root = getRoot(node);
		SourceCodeEntity entity = node.getEntity();
		SourceRange range = entity.getSourceRange();
		UTASTVariableCollector collector = new UTASTVariableCollector(range);
		root.accept(collector);
		return collector.getIdentifiers();
	}

	ASTNode getASTNode(Node node, ASTNode root) {
		UTASTNodeSearcher searcher = new UTASTNodeSearcher(node);
		return searcher.search(root);
	}

	ASTNode getRoot(Node node) {
		ICompilationUnit cpu = null;

		if (node.isLeft()) {
			// The selected node is from the new version
			cpu = UTCriticsPairFileInfo.getLeftICompilationUnit();
		} else {
			// The selected node is from the old version
			cpu = UTCriticsPairFileInfo.getRightICompilationUnit();
		}

		// Get AST root node
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(cpu);
		parser.setResolveBindings(true);
		ASTNode root = (CompilationUnit) parser.createAST(null /* IProgressMonitor */);
		return root;
	}

	/**
	 * Show tree graph.
	 */
	private void showTreeGraph() {
		List<Node> lstNodeOldRev = qTreeOldRev.getAllNodes();
		for (int i = 0; i < lstNodeOldRev.size(); i++) {
			lstNodeOldRev.get(i).setFilePath(UTCriticsPairFileInfo.getRightFile());
		}
		List<Node> lstNodeNewRev = qTreeNewRev.getAllNodes();
		for (int i = 0; i < lstNodeNewRev.size(); i++) {
			lstNodeNewRev.get(i).setFilePath(UTCriticsPairFileInfo.getLeftFile());
		}

		CriticsContextSelectionView vwIntCxtSelOldRev, vwIntCxtSelNewRev;
		vwIntCxtSelOldRev = (CriticsContextSelectionView) UTPlugin.getView(UTPlugin.ID_VIEW_SELECTCTX_OLDREV);
		vwIntCxtSelNewRev = (CriticsContextSelectionView) UTPlugin.getView(UTPlugin.ID_VIEW_SELECTCTX_NEWREV);
		vwIntCxtSelOldRev.displayIntCxtSelView(lstNodeOldRev); // queryTreeOldRev.getAllNodes());
		vwIntCxtSelNewRev.displayIntCxtSelView(lstNodeNewRev); // queryTreeNewRev.getAllNodes());
		vwIntCxtSelOldRev.setLayoutManagerZestGraph();
		vwIntCxtSelNewRev.setLayoutManagerZestGraph();
	}

	/**
	 * Gets the edits from q tree.
	 * 
	 * @return the edits from q tree
	 */
	private void getEditsFromQTree() {
		mChanges = mChangeDistiller.diffBlock(qTreeOldRev.copy(), qTreeNewRev.copy());
		mMatches = mChangeDistiller.getMatches();

		File fOldRev = UTCriticsPairFileInfo.getRightFile();
		String srcOldRev = UTFile.getContents(fOldRev.getAbsolutePath());
		CompilationUnit unitOldRev = new UTASTParser().parse(srcOldRev);

		File fNewRev = UTCriticsPairFileInfo.getLeftFile();
		String srcNewRev = UTFile.getContents(fNewRev.getAbsolutePath());
		CompilationUnit unitNewRev = new UTASTParser().parse(srcNewRev);

		mInsertList = mChangeDistiller.getInsertList();
		mDeleteList = mChangeDistiller.getDeleteList();

		mInsertNodeList = UTChange.getNodeListMethodLevel(mInsertList, unitNewRev, srcNewRev, fNewRev);
		mDeleteNodeList = UTChange.getNodeListMethodLevel(mDeleteList, unitOldRev, srcOldRev, fOldRev);

		UTLog.println(false, "[DBG0] INSERT");
		UTChange.printNode(mInsertNodeList, false);
		UTLog.println(false, "[DBG1] DELETE");
		UTChange.printNode(mDeleteNodeList, false);

		for (int i = 0; i < mInsertNodeList.size(); i++) {
			Node iNode = mInsertNodeList.get(i);
			Enumeration<?> eNodes = qTreeNewRev.preorderEnumeration();
			while (eNodes.hasMoreElements()) {
				Node jNode = (Node) eNodes.nextElement();
				if (jNode.eq(iNode)) {
					jNode.setInsert(true);
					doMoreSet(jNode, UTEnumEdit.INSERT, true);
					break;
				}
			}
		}
		for (int i = 0; i < mDeleteNodeList.size(); i++) {
			Node iNode = mDeleteNodeList.get(i);
			Enumeration<?> eNodes = qTreeOldRev.preorderEnumeration();
			while (eNodes.hasMoreElements()) {
				Node jNode = (Node) eNodes.nextElement();
				if (jNode.eq(iNode)) {
					jNode.setDelete(true);
					doMoreSet(jNode, UTEnumEdit.DELETE, true);
					break;
				}
			}
		}
	}

	private void doMoreSet(Node aNode, UTEnumEdit edit, boolean bf) {
		if (aNode.getEntity().getType() == JavaEntityType.IF_STATEMENT) {
			Enumeration<?> eChild = aNode.children();
			while (eChild.hasMoreElements()) {
				Node iNode = (Node) eChild.nextElement();
				if (iNode.getEntity().getType() == JavaEntityType.THEN_STATEMENT) {
					switch (edit) {
					case DELETE:
						iNode.setDelete(bf);
						break;
					case INSERT: {
						iNode.setInsert(bf);
						break;
					}
					default:
						break;
					}
				}
			}
		}
	}

	/**
	 * Sets the change q tree.
	 * 
	 * @param aOffset
	 *            the a offset
	 * @param aEdit
	 *            the a edit
	 * @param isOldRev
	 *            the is old rev
	 */
	void setChangeQTree(int aOffset, UTEnumEdit aEdit, boolean isOldRev) {
		Enumeration<?> eQTree;
		if (isOldRev)
			eQTree = qTreeOldRev.preorderEnumeration();
		else
			eQTree = qTreeNewRev.preorderEnumeration();
		setChangeQTree(eQTree, aOffset, aEdit);
	}

	/**
	 * Sets the change q tree.
	 * 
	 * @param eQTree
	 *            the e q tree
	 * @param aOffset
	 *            the a offset
	 * @param aEdit
	 *            the a edit
	 */
	private void setChangeQTree(Enumeration<?> eQTree, int aOffset, UTEnumEdit aEdit) {
		while (eQTree.hasMoreElements()) {
			Node iNode = (Node) eQTree.nextElement();
			int iOffset = iNode.getEntity().getStartPosition();
			if (iOffset == aOffset) {
				switch (aEdit) {
				case INSERT:
					iNode.setInsert(true);
					break;
				case DELETE:
					iNode.setDelete(true);
					break;
				default:
					break;
				}
			}
		}
	}

	/**
	 * What change.
	 * 
	 * @param aChange
	 *            the a change
	 * @return the uT enum edit
	 */
	UTEnumEdit whatChange(SourceCodeChange aChange) {
		UTEnumEdit aEChange;
		if (aChange.getClass().getSimpleName().equalsIgnoreCase("insert")) {
			aEChange = UTEnumEdit.INSERT;
		} else if (aChange.getClass().getSimpleName().equalsIgnoreCase("delete")) {
			aEChange = UTEnumEdit.DELETE;
		} else {
			aEChange = UTEnumEdit.UPDATE;
		}
		return aEChange;
	}

	/**
	 * Process query tree old rev.
	 * 
	 * @param isPrintable
	 *            the is printable
	 */
	private void processQueryTreeOldRev(boolean isPrintable) {
		ICompilationUnit iunit = UTCriticsPairFileInfo.getRightICompilationUnit();
		Point region = rightSelectedRegion;
		String src = rightSRViewer.getDocument().get();
		TypeDeclaration c = nodeFinder.findClass(iunit, region, true);
		MethodDeclaration m = nodeFinder.findMethod(iunit, region, isPrintable);

		// Construct query tree
		qTreeOldRev = nodeConverter.convertClass(c, src);
		File fileOldRev = UTCriticsPairFileInfo.getRightFile();
		Node method = nodeConverter.convertMethod(m, src, fileOldRev);
		// Add selected method as a child of the class node of this query tree
		qTreeOldRev.add(method);
		// TODO: Add fields as children of the class node of this query tree

		UTLog.println(isPrintable, "[DBG] RIGHT TREE QUERY REPRESENTATION");
		// nodeOperation.markAliveToTreeNode(queryTreeOldRev, contextASTNodesOldRev);
		analyzer.analyzeOldClass(qTreeOldRev);
		nodeOperation.pruneDensityOfTreeNode(qTreeOldRev, isPrintable);
		Map<Integer, Node> mNodeDepth = new TreeMap<Integer, Node>();
		Enumeration<?> e = qTreeOldRev.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			if (iNode.isSelectedByUser()) {
				mNodeDepth.put(iNode.getLevel(), iNode);
			}
		}
		Node curTopNode = mNodeDepth.entrySet().iterator().next().getValue();
		Node curParNode = (Node) curTopNode.getParent();
		if (curParNode == null) {
			qTreeOldRev = curTopNode;
		} else {
			qTreeOldRev = curParNode;
		}
	}

	/**
	 * Process query tree new rev.
	 * 
	 * @param isPrintable
	 *            the is printable
	 */
	@SuppressWarnings("unused")
	private void processQueryTreeNewRev(boolean isPrintable) {
		ICompilationUnit iunit = UTCriticsPairFileInfo.getLeftICompilationUnit();
		Point region = leftSelectedRegion;
		String src = leftSRViewer.getDocument().get();
		TypeDeclaration c = nodeFinder.findClass(iunit, region, true);
		MethodDeclaration m = nodeFinder.findMethod(iunit, region, isPrintable);

		// Construct query tree
		qTreeNewRev = nodeConverter.convertClass(c, src);
		File fileNewRev = UTCriticsPairFileInfo.getLeftFile();
		Node method = nodeConverter.convertMethod(m, src, fileNewRev);
		// Add selected method as a child of the class node of this query tree
		qTreeNewRev.add(method);
		Node parent = (Node) method.getParent();
		// TODO: Add fields as children of the class node of this query tree UTLog.println(isPrintable, "[DBG] RIGHT TREE QUERY REPRESENTATION");

		UTLog.println(isPrintable, "[DBG] LEFT TREE QUERY REPRESENTATION");
		// nodeOperation.markAliveToTreeNode(queryTreeNewRev, contextASTNodesNewRev);
		analyzer.analyzeNewClass(qTreeNewRev);
		nodeOperation.pruneDensityOfTreeNode(qTreeNewRev, isPrintable);
		Map<Integer, Node> mNodeDepth = new TreeMap<Integer, Node>();
		Enumeration<?> e = qTreeNewRev.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			if (iNode.isSelectedByUser()) {
				mNodeDepth.put(iNode.getLevel(), iNode);
			}
		}
		Node curTopNode = mNodeDepth.entrySet().iterator().next().getValue();
		Node curParNode = (Node) curTopNode.getParent();
		if (curParNode == null) {
			qTreeNewRev = curTopNode;
		} else {
			qTreeNewRev = curParNode;
		}

	}

	/**
	 * Find similar sub trees old revision.
	 * 
	 * @param iPackageFragments
	 *            the i package fragments
	 * @throws Exception
	 *             the exception
	 */
	void findSimilarSubTreesOldRevision(IPackageFragment[] iPackageFragments) throws Exception {
		UTIGeneralVisitor<MethodDeclaration> mVisitor = new UTGeneralVisitor<MethodDeclaration>() {
			public boolean visit(MethodDeclaration node) {
				results.add(node);
				return true;
			}
		};
		for (int i = 0; i < iPackageFragments.length; i++) {
			IPackageFragment pkgElem = iPackageFragments[i];
			if (pkgElem.getKind() == IPackageFragmentRoot.K_SOURCE) {
				for (ICompilationUnit unit : pkgElem.getCompilationUnits()) {
					cntClass++;
					File f = UTPlugin.getFullFilePath(unit);
					String sourceCode = UTFile.getContents(f.getAbsolutePath());
					UTASTParser astParser = new UTASTParser();
					CompilationUnit parser = astParser.parse(unit);
					List<MethodDeclaration> lstMethods = mVisitor.getResults();
					lstMethods.clear();
					parser.accept(mVisitor);
					cntMethod += lstMethods.size();
					for (int j = 0; j < lstMethods.size(); j++) {
						MethodDeclaration iMethod = lstMethods.get(j);
						Node iNode = nodeConverter.convertMethod(iMethod, sourceCode, f);
//						matcherOldRev.setCompilationUnit(parser);
						matcherOldRev.setFilePath(f);
						matcherOldRev.setMatchCounter(cntMatchedOldRev);
						matcherOldRev.matchEditMappingEntry(qTreeOldRev, iNode, true);
					}
				}
			}
		}
	}

	/**
	 * Gets the relevant context nodes from user selection. * TEST NOV. 21, 2013 5:44:01 PM - DUMMY DATA
	 * 
	 * @return the relevant context nodes from user selection
	 */
	void getRelevantContextNodesFromUserSelection() {
		contextASTNodesOldRev.clear();
		Point[] otherRegion1 = { new Point(-1, -1) };// { new Point(346, 10) };
		CriticsMatchTreeEditDistanceTestSubject.getRelevantContextNodes( //
				UTCriticsPairFileInfo.getRightICompilationUnit(), nodeFinder, //
				contextASTNodesOldRev, rightSelectedRegion, otherRegion1);

		contextASTNodesNewRev.clear();
		Point[] otherRegion2 = { new Point(-1, -1) };// { new Point(240, 65), new Point(462, 10) };
		CriticsMatchTreeEditDistanceTestSubject.getRelevantContextNodes( //
				UTCriticsPairFileInfo.getLeftICompilationUnit(), nodeFinder, //
				contextASTNodesNewRev, leftSelectedRegion, otherRegion2);
	}

	public UTASTNodeFinder getNodeFinder() {
		return nodeFinder;
	}

	public List<ASTNode> getContextASTNodesOldRev() {
		return contextASTNodesOldRev;
	}

	public List<ASTNode> getContextASTNodesNewRev() {
		return contextASTNodesNewRev;
	}

	public UTASTNodeConverter getNodeConverter() {
		return nodeConverter;
	}

	public UTASTNodeOperation getNodeOperation() {
		return nodeOperation;
	}

	public ITreeMatch getMatcherOldRev() {
		return matcherOldRev;
	}

	public ITreeMatch getMatcherNewRev() {
		return matcherNewRev;
	}

	public TreeMatchBetweenQueryTrees getMatcherQueryTree() {
		return matcherQueryTree;
	}

	public UTCriticsTransform getTransformOldRev() {
		return transformOldRev;
	}

	public Node getQueryTreeOldRev() {
		return qTreeOldRev;
	}

	public Node getQueryTreeNewRev() {
		return qTreeNewRev;
	}

	public UTChangeDistiller getUTChangeDistiller() {
		return mChangeDistiller;
	}
}
