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
package edu.utexas.seal.plugins;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.tree.TreeNode;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.handlers.HandlerUtil;

import rted.datastructure.manager.ITreeMatch;
import rted.datastructure.manager.TreeMatchAgainstNewRev;
import rted.datastructure.manager.TreeMatchAgainstOldRev;
import rted.datastructure.manager.TreeMatchBetweenQueryTrees;
import rted.distance.RTEDInfoSubTree;
import ut.seal.plugins.utils.UTFile;
import ut.seal.plugins.utils.UTLog;
import ut.seal.plugins.utils.UTTime;
import ut.seal.plugins.utils.ast.UTASTNodeConverter;
import ut.seal.plugins.utils.ast.UTASTNodeFinder;
import ut.seal.plugins.utils.ast.UTASTNodeOperation;
import ut.seal.plugins.utils.ast.UTASTParser;
import ut.seal.plugins.utils.change.UTChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import edu.utexas.seal.plugins.edit.UTEnumEdit;
import edu.utexas.seal.plugins.treegraph.view.CriticsContextSelectionView;
import edu.utexas.seal.plugins.util.UTCriticsPairFileInfo;
import edu.utexas.seal.plugins.util.root.UTCriticsTransform;
import edu.utexas.seal.plugins.util.root.UTPlugin;
import edu.utexas.seal.plugins.util.root.UTSubTreeCluster;
import edu.utexas.seal.plugins.util.root.UTSubTreeMatchResult;
import edu.utexas.seal.plugins.util.visitor.UTGeneralVisitor;
import edu.utexas.seal.plugins.util.visitor.UTIGeneralVisitor;

/**
 * @author Myoungkyu Song
 * @date Oct 25, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsMatchTreeEditDistance extends CriticsAbstract {
	private UTASTNodeFinder				nodeFinder				= null;

	private List<ASTNode>				contextASTNodesOldRev	= new ArrayList<ASTNode>();
	private List<ASTNode>				contextASTNodesNewRev	= new ArrayList<ASTNode>();

	private UTASTNodeConverter			nodeConverter			= null;
	private UTASTNodeOperation			nodeOperation			= null;

	private ITreeMatch					matcherOldRev			= null;
	private ITreeMatch					matcherNewRev			= null;
	private TreeMatchBetweenQueryTrees	matcherQueryTree		= null;

	private UTChangeDistiller			changeDistiller			= null;
	List<SourceCodeChange>				changes					= null;
	private UTSubTreeCluster			clusterer				= null;

	private UTCriticsTransform			transformOldRev			= null;

	private Node						queryTreeOldRev			= null;
	private Node						queryTreeNewRev			= null;

	private Integer						cntMatchedOldRev[]		= { 1 };
	private Integer						cntMatchedNewRev[]		= { 1 };
	private int							cntMethod				= 1;
	private int							cntClass				= 1;

	/**
	 * 
	 * @return
	 */
	protected boolean initiate() {
		if (!super.initiate()) {
			return false;
		}
		UTLog.println(true, "DBG__________________________________________");
		UTLog.println(true, "[DBG] TREE MATCHING..");
		UTLog.println(true, "DBG__________________________________________");
		cntMatchedOldRev[0] = 1;
		cntMatchedNewRev[0] = 1;
		nodeFinder = new UTASTNodeFinder();
		nodeConverter = new UTASTNodeConverter();
		nodeOperation = new UTASTNodeOperation();
		changeDistiller = new UTChangeDistiller();
		matcherOldRev = new TreeMatchAgainstOldRev();
		matcherNewRev = new TreeMatchAgainstNewRev();
		matcherQueryTree = new TreeMatchBetweenQueryTrees(queryTreeOldRev, queryTreeNewRev);
		clusterer = new UTSubTreeCluster();
		transformOldRev = new UTCriticsTransform(this);
		return true;
	}

	/**
	 * 
	 * @param event
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
		UTLog.println(true, "[RST] # OF MATCHES: " + String.valueOf(cntMatchedOldRev[0] - 1));
		UTLog.println(true, "[RST] # OF CLASSES: " + (cntClass - 1));
		UTLog.println(true, "[RST] # OF METHODS: " + (cntMethod - 1));
		UTLog.println(true, "DBG__________________________________________");
		UTLog.println(false, "</pre>");
		return null;
	}

	/**
	 * 
	 * 1ST SCENARIO: Discovering Omission Errors Users Missed Changes, see the 2nd slide at:
	 * https://docs.google.com/presentation/d/15p9uet_DOfg_UKlhgLQd2ESVS5GUxPN3diR98nmMqOI/edit#slide=id.g1747ece38_00
	 * 
	 * @param event
	 */
	private void startExecute(ExecutionEvent event) throws Exception {
		analyzeQueryTree(event);
		findSimilarSubTreesOldRevision(UTCriticsPairFileInfo.getRightIPackages());
		processResult();
		checkChangePatternViolation();
	}

	void processResult() {
		UTLog.println(true, "DBG__________________________________________");
		UTLog.println(true, "[RST] THE SUBTREES IN OLD REVISION, SIMILAR TO OLD QUERY TREE");
		UTLog.println(true, "DBG__________________________________________");
		UTSubTreeMatchResult.computeSimilarity(matcherOldRev.getSubTrees());
		UTSubTreeMatchResult.print(matcherOldRev.getSubTrees(), true);
		clusterer.groupByClusterer(matcherOldRev.getSubTrees());
	}

	/**
	 * @desc subtree - in clustered by similarity in old revision
	 * 
	 * @throws Exception
	 */
	private void checkChangePatternViolation() throws Exception {
		boolean isPrintable = false;
		UTLog.println(true, "DBG__________________________________________");
		UTLog.println(true, "[RST] THE SUBTREES IN NEW REVISION, SIMILAR TO OLD QUERY TREE TRANSFORMED BY EDITS");
		UTLog.println(true, "DBG__________________________________________");
		List<RTEDInfoSubTree> firstGroupClusteredBySim = clusterer.getFirstGroup(matcherOldRev.getSubTrees());
		transformOldRev.transformByChangeEdits(firstGroupClusteredBySim, null, null, isPrintable);
		System.out.println("==========================================");
		for (int i = 0; i < firstGroupClusteredBySim.size(); i++) {
			RTEDInfoSubTree iSubTree = firstGroupClusteredBySim.get(i);
			Node iNode = iSubTree.getSubTree();
			UTLog.println(true, "[DBG1] [THE FIRST GROUP] " + iSubTree.toString());
			TreeNode[] iPath = iNode.getPath();
			StringBuilder iBuf = new StringBuilder();
			for (int j = 0; j < iPath.length; j++) {
				Node jNode = (Node) iPath[j];
				iBuf.append(jNode + " ");
			}
			UTLog.println(true, "[DBG1]   " + iBuf.toString());
			// iSubTree.getSubTree().print();
			System.out.println("------------------------------------------");
			findTreeSimilarToTransformedTree(iSubTree.getSubTree());
			System.out.println("------------------------------------------");
		}
	}

	/**
	 * 
	 * @param transformedTree
	 * @throws JavaModelException
	 */

	private void findTreeSimilarToTransformedTree(Node transformedTree) throws JavaModelException {
		UTIGeneralVisitor<MethodDeclaration> mVisitor = new UTGeneralVisitor<MethodDeclaration>() {
			public boolean visit(MethodDeclaration node) {
				results.add(node);
				return true;
			}
		};
		matcherNewRev.getSubTrees().clear();
		for (IPackageFragment pkgElem : UTCriticsPairFileInfo.getLeftIPackages()) {
			for (ICompilationUnit u : pkgElem.getCompilationUnits()) {
				File f = UTPlugin.getFullFilePath(u);
				String sourceCode = UTFile.getContents(f.getAbsolutePath());
				UTASTParser astParser = new UTASTParser();
				CompilationUnit parser = astParser.parse(u);
				List<MethodDeclaration> lstMethods = mVisitor.getResults();
				lstMethods.clear();
				parser.accept(mVisitor);
				for (int i = 0; i < lstMethods.size(); i++) {
					MethodDeclaration iMethod = lstMethods.get(i);
					Node iNode = nodeConverter.convertMethod(iMethod, sourceCode, f);
//					matcherNewRev.setCompilationUnit(parser);
					matcherNewRev.setFilePath(f);
					matcherNewRev.setMatchCounter(cntMatchedNewRev);
					matcherNewRev.matchEditMappingEntry(transformedTree, iNode, true);
				}
				// List<UTASTNodeConverterManager> nodeConverterMngr = //
				// nodeConverter.convertMethods(new UTASTParser().parse(u), f);
				// for (int i = 0; i < nodeConverterMngr.size(); i++) {
				// matcherNewRev.matchEditMappingEntry( //
				// cntMatchedNewRev, nodeConverterMngr.get(i).getJavaCompilation(), //
				// transformedTree, nodeConverterMngr.get(i).getNode(), f, true);
				// }
			}
		}
		UTSubTreeMatchResult.computeSimilarity(matcherNewRev.getSubTrees());
		UTSubTreeMatchResult.print(matcherNewRev.getSubTrees(), true);
	}

	/**
	 * @param event
	 * 
	 */
	private void analyzeQueryTree(ExecutionEvent event) {
		getRelevantContextNodesFromUserSelection();
		processQueryTreeOldRev(false);
		processQueryTreeNewRev(false);
		useChangeDistiller();
		matcherQueryTree.matchEditMapping(queryTreeOldRev, queryTreeNewRev);

		String idViewOldRev = "edu.utexas.seal.plugins.treegraph.view.CriticsContextSelectionView.OldRev";
		String idViewNewRev = "edu.utexas.seal.plugins.treegraph.view.CriticsContextSelectionView.NewRev";

		IViewPart vwFinderOldRev = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(idViewOldRev);
		IViewPart vwFinderNewRev = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(idViewNewRev);

		CriticsContextSelectionView vwIntCxtSelOldRev, vwIntCxtSelNewRev;

		vwIntCxtSelOldRev = (CriticsContextSelectionView) vwFinderOldRev;
		vwIntCxtSelNewRev = (CriticsContextSelectionView) vwFinderNewRev;

		vwIntCxtSelOldRev.displayIntCxtSelView(queryTreeOldRev.getAllNodes());
		vwIntCxtSelNewRev.displayIntCxtSelView(queryTreeNewRev.getAllNodes());

		vwIntCxtSelOldRev.setLayoutManagerZestGraph();
		vwIntCxtSelNewRev.setLayoutManagerZestGraph();
	}

	private void useChangeDistiller() {
		changes = changeDistiller.diff(queryTreeOldRev.copy(), queryTreeNewRev.copy());
		UTEnumEdit eChange = UTEnumEdit.INVALID;
		List<Integer> lstSkipChange = new ArrayList<Integer>();
		for (int i = 0; i < changes.size(); i++) {
			SourceCodeChange iChange = changes.get(i);
			int pOffSetChange = iChange.getChangedEntity().getStartPosition();
			eChange = whatChange(iChange);
			switch (eChange) {
			case INSERT:
				int pOffsetNewRev = this.leftSelectedRegion.x;
				if (pOffSetChange < pOffsetNewRev) {
					lstSkipChange.add(i);
				}
				setChangeQTree(pOffSetChange, UTEnumEdit.INSERT, false);
				break;
			case DELETE:
				pOffsetNewRev = this.rightSelectedRegion.x;
				if (pOffSetChange < pOffsetNewRev) {
					lstSkipChange.add(i);
				}
				setChangeQTree(pOffSetChange, UTEnumEdit.DELETE, true);
				break;
			case UPDATE:
				break;
			default:
				break;
			}

		}
		for (int i = 0; i < lstSkipChange.size(); i++) {
			Integer iSkip = lstSkipChange.get(i);
			changes.set(iSkip, null);
		}
		List<SourceCodeChange> lstChangesUpdated = new ArrayList<SourceCodeChange>();
		for (int i = 0; i < changes.size(); i++) {
			SourceCodeChange iChange = changes.get(i);
			if (iChange != null) {
				lstChangesUpdated.add(iChange);
			}
		}
		changes = lstChangesUpdated;
		changeDistiller.setChanges(changes);
	}

	private void setChangeQTree(int aOffset, UTEnumEdit aEdit, boolean isOldRev) {
		Enumeration<?> eQTree;
		if (isOldRev)
			eQTree = queryTreeOldRev.preorderEnumeration();
		else
			eQTree = queryTreeNewRev.preorderEnumeration();
		setChangeQTree(eQTree, aOffset, aEdit);
	}

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

	private UTEnumEdit whatChange(SourceCodeChange aChange) {
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
	 * @Assumption old version
	 */
	private void processQueryTreeOldRev(boolean isPrintable) {
		ICompilationUnit iunit = UTCriticsPairFileInfo.getRightICompilationUnit();
		Point region = rightSelectedRegion;
		String src = rightSRViewer.getDocument().get();
		MethodDeclaration m = nodeFinder.findMethod(iunit, region, isPrintable);
		File fileOldRev = UTCriticsPairFileInfo.getRightFile();
		queryTreeOldRev = nodeConverter.convertMethod(m, src, fileOldRev);
		UTLog.println(isPrintable, "[DBG] LEFT TREE QUERY REPRESENTATION");
		nodeOperation.markAliveToTreeNode(queryTreeOldRev, contextASTNodesOldRev);
		nodeOperation.pruneDensityOfTreeNode(queryTreeOldRev, isPrintable);
		Map<Integer, Node> mNodeDepth = new TreeMap<Integer, Node>();
		Enumeration<?> e = queryTreeOldRev.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			if (iNode.isSelectedByUser()) {
				mNodeDepth.put(iNode.getLevel(), iNode);
			}
		}
		Node curTopNode = mNodeDepth.entrySet().iterator().next().getValue();
		Node curParNode = (Node) curTopNode.getParent();
		if (curParNode == null) {
			queryTreeOldRev = curTopNode;
		} else {
			queryTreeOldRev = curParNode;
		}
	}

	/**
	 * @Assumption new version
	 */
	private void processQueryTreeNewRev(boolean isPrintable) {
		ICompilationUnit iunit = UTCriticsPairFileInfo.getLeftICompilationUnit();
		Point region = leftSelectedRegion;
		String src = leftSRViewer.getDocument().get();
		MethodDeclaration m = nodeFinder.findMethod(iunit, region, isPrintable);
		File fileNewRev = UTCriticsPairFileInfo.getLeftFile();
		queryTreeNewRev = nodeConverter.convertMethod(m, src, fileNewRev);
		UTLog.println(isPrintable, "[DBG] RIGHT TREE QUERY REPRESENTATION");
		nodeOperation.markAliveToTreeNode(queryTreeNewRev, contextASTNodesNewRev);
		nodeOperation.pruneDensityOfTreeNode(queryTreeNewRev, isPrintable);
		Map<Integer, Node> mNodeDepth = new TreeMap<Integer, Node>();
		Enumeration<?> e = queryTreeNewRev.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			if (iNode.isSelectedByUser()) {
				mNodeDepth.put(iNode.getLevel(), iNode);
			}
		}
		Node curTopNode = mNodeDepth.entrySet().iterator().next().getValue();
		Node curParNode = (Node) curTopNode.getParent();
		if (curParNode == null) {
			queryTreeNewRev = curTopNode;
		} else {
			queryTreeNewRev = curParNode;
		}

	}

	/**
	 * 
	 * @param iPackageFragments
	 * @return
	 * @throws Exception
	 */
	private void findSimilarSubTreesOldRevision(IPackageFragment[] iPackageFragments) throws Exception {
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
						matcherOldRev.setCompilationUnit(parser);
						matcherOldRev.setFilePath(f);
						matcherOldRev.setMatchCounter(cntMatchedOldRev);
						matcherOldRev.matchEditMappingEntry(queryTreeOldRev, iNode, true);
					}
					// List<UTASTNodeConverterManager> nodeMngrs = //
					// nodeConverter.convertMethods(new UTASTParser().parse(unit), f);
					// cntMethod += nodeMngrs.size();
					// for (UTASTNodeConverterManager ndMngr : nodeMngrs) {
					// matcherOldRev.matchEditMappingEntry( //
					// cntMatchedOldRev, ndMngr.getJavaCompilation(), //
					// queryTreeOldRev, ndMngr.getNode(), f, true);
					// }
				}
			}
		}
	}

	// /**
	// * @param unit
	// * @throws JavaModelException
	// */
	// void traceCompilationToMatchSubTree(ICompilationUnit unit) throws JavaModelException {
	// File f = UTIO.getFullFilePath(unit);
	// List<UTASTNodeConverterManager> nodeMngr = nodeConverter.convertMethods(new UTASTParser().parse(unit), f);
	// /*
	// TEST CODE Dec 1, 2013 10:29:43 AM
	// CriticsMatchTreeEditDistanceTestSubject.visitMethodDeclaration(fullFileName, //
	// compilationUnit, nodeConverterMngr);
	// */
	// cntMethod += nodeMngr.size();
	// for (int i = 0; i < nodeMngr.size(); i++) {
	// matcherOldRev.matchEditMappingEntry( //
	// cntMatchedOldRev, nodeMngr.get(i).getJavaCompilation(), //
	// queryTreeOldRev, nodeMngr.get(i).getNode(), //
	// f, true);
	// }
	// }

	/**
	 * TEST NOV. 21, 2013 5:44:01 PM - DUMMY DATA
	 */
	private void getRelevantContextNodesFromUserSelection() {
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

	public UTSubTreeCluster getClusterer() {
		return clusterer;
	}

	public UTCriticsTransform getTransformOldRev() {
		return transformOldRev;
	}

	public Node getQueryTreeOldRev() {
		return queryTreeOldRev;
	}

	public Node getQueryTreeNewRev() {
		return queryTreeNewRev;
	}

	public UTChangeDistiller getUTChangeDistiller() {
		return changeDistiller;
	}
}
