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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;

import rted.datastructure.manager.ITreeMatch;
import rted.datastructure.manager.TreeMatchAgainstOldRev;
import rted.datastructure.manager.TreeMatchBetweenQueryTrees;
import rted.distance.RTEDInfoSubTree;
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
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.NodeValueArg;
import edu.utexas.seal.plugin.preprocess.UTCriticsFileFilter;
import edu.utexas.seal.plugin.preprocess.UTCriticsPreprocessor;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerAbstract;
import edu.utexas.seal.plugins.ast.util.UTAddNewValue;
import edu.utexas.seal.plugins.generization.CriticsGeneralizeNodeValue;
import edu.utexas.seal.plugins.overlay.view.CriticsOverlayNewView;
import edu.utexas.seal.plugins.treegraph.view.CriticsContextSelectionView;
import edu.utexas.seal.plugins.util.UTCriticsPairFileInfo;
import edu.utexas.seal.plugins.util.root.UTCriticsTransform;
import edu.utexas.seal.plugins.util.root.UTPlugin;
import edu.utexas.seal.plugins.util.root.UTSubTreeMatchResult;
import edu.utexas.seal.plugins.util.visitor.UTGeneralVisitor;
import edu.utexas.seal.plugins.util.visitor.UTIGeneralVisitor;

/**
 * The Class CriticsFindContext.
 * 
 * @author Myoungkyu Song
 * @date Oct 25, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsFindContext extends CriticsCommonHandler {
	/* (non-Javadoc)
	 * @see edu.utexas.seal.plugins.handler.CriticsCommonHandler#initiate()
	 */
	protected boolean initiate() {
		if (!super.initiate()) {
			return false;
		}
		UTLog.println(true, "DBG__________________________________________");
		UTLog.println(true, "[DBG] *** [CRITICS] FIND CONTEXT GROUP ******* ");
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
		long begin = (new Date()).getTime();
		// ////////////////////////////////////////////////////////////////////
		try {
			startExecute(event);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// ////////////////////////////////////////////////////////////////////
		long tot = (new Date()).getTime() - begin;
		UTTime.timeStamp(tot);
		// ////////////////////////////////////////////////////////////////////
		UTLog.println(false, "[RST] # OF MATCHES: " + String.valueOf(cntMatchedOldRev[0] - 1));
		UTLog.println(false, "[RST] # OF CLASSES: " + (cntClass - 1));
		UTLog.println(false, "[RST] # OF METHODS: " + (cntMethod - 1));
		UTLog.println(false, "DBG__________________________________________");
		return null;
	}

	/**
	 * Start execute.
	 * 
	 * @param event the event
	 * @throws Exception the exception
	 */
	private void startExecute(ExecutionEvent event) throws Exception {
		boolean trace1 = true;
		CriticsGeneralizeNodeValue genNodeValue = new CriticsGeneralizeNodeValue();
		genNodeValue.setGeneralizationInfo();
		genNodeValue.printGeneralizationInfo(false, false);
		qTreeOldRev = genNodeValue.getQTreeOldRev();
		qTreeParGenOldRev = genNodeValue.getQTreeSelectiveGenOldRev();
		qTreeAllGenOldRev = genNodeValue.getQTreeAllGenOldRev();
		qTreeNewRev = genNodeValue.getQTreeNewRev();
		qTreeParGenNewRev = genNodeValue.getQTreeSelectiveGenNewRev();
		qTreeAllGenNewRev = genNodeValue.getQTreeAllGenNewRev();
		if (trace1) {
			System.out.println("[DBG] QTree Old Rev");
			qTreeOldRev.print();
			System.out.println("------------------------------------------");
			System.out.println("[DBG] QTree New Rev");
			qTreeNewRev.print();
			System.out.println("**************   " + qTreeNewRev.getAllNodes().size() + "   **************");
			System.out.println("==========================================");
		}
		String leftPrjName = UTCriticsPairFileInfo.getLeftProjectName();
		String rightPrjName = UTCriticsPairFileInfo.getRightProjectName();
		UTCriticsFileFilter filterLeft = UTCriticsPreprocessor.preprocessor.get(leftPrjName);
		UTCriticsFileFilter filterRight = UTCriticsPreprocessor.preprocessor.get(rightPrjName);
		if (filterLeft == null || filterRight == null) {
			findSimilarContexts(UTCriticsPairFileInfo.getLeftIPackages(), LR.LEFT);
			findSimilarContexts(UTCriticsPairFileInfo.getRightIPackages(), LR.RIGHT);
		} else {
			HashMap<String, Set<String>> leftFilter = filterLeft.getFilter();
			HashMap<String, Set<String>> rightFilter = filterRight.getFilter();
			List<String> leftQueries = getQueries(qTreeNewRev);
			List<String> rightQueries = getQueries(qTreeOldRev);
			Set<String> leftFiles = filterFiles(leftQueries, leftFilter);
			Set<String> rightFiles = filterFiles(rightQueries, rightFilter);
			if (leftFiles == null || rightFiles == null) {
				findSimilarContexts(UTCriticsPairFileInfo.getLeftIPackages(), LR.LEFT);
				findSimilarContexts(UTCriticsPairFileInfo.getRightIPackages(), LR.RIGHT);
			} else {
				List<ICompilationUnit> icusLeft = getICUnits(UTCriticsPairFileInfo.getLeftIPackages(), leftFiles);
				List<ICompilationUnit> icusRight = getICUnits(UTCriticsPairFileInfo.getRightIPackages(), rightFiles);
				findSimilarContexts(icusLeft, LR.LEFT);
				findSimilarContexts(icusRight, LR.RIGHT);
			}
		}
		processResultSimilarSubTrees();
		detectAnomalies();
		showCheckBoxTree(matcherOldRev.getSubTrees());
	}

	List<ICompilationUnit> getICUnits(IPackageFragment[] iPackageFragments, Set<String> set) throws JavaModelException {
		ArrayList<ICompilationUnit> result = new ArrayList<ICompilationUnit>();
		for (int i = 0; i < iPackageFragments.length; i++) {
			IPackageFragment pkgElem = iPackageFragments[i];
			if (pkgElem.getKind() == IPackageFragmentRoot.K_SOURCE) {
				for (ICompilationUnit unit : pkgElem.getCompilationUnits()) {
					String path = unit.getPath().toFile().getAbsolutePath();
					if (set.contains(path) && !result.contains(unit)) {
						result.add(unit);
					}
				}
			}
		}
		return result;
	}

	Set<String> filterFiles(List<String> queries, HashMap<String, Set<String>> filter) {
		if (queries == null || queries.size() == 0)
			return null;
		Set<String> result = filter.get(queries.get(0));
		// create a copy of hash set to avoid corrupting the original one when retaining elements
		Set<String> resultCopy = new HashSet<String>(result);
		for (String query : queries) {
			if(filter.get(query) == null){
				// this query is a method invocation query, continue
				continue;
			}else{
				resultCopy.retainAll(filter.get(query));
			}
		}
		return resultCopy;
	}

	List<String> getQueries(Node root) {
		List<String> queries = new ArrayList<String>();
		List<Node> nodes = root.getAllNodes();
		for (Node node : nodes) {
			Map<SimpleName, Boolean> map = node.getParamMap();
			for (SimpleName var : map.keySet()) {
				if (!map.get(var)) {
					String query = var.getIdentifier() + ":" + var.resolveTypeBinding().getName();
					if (!queries.contains(query)) {
						queries.add(query);
					}
				}
			}
		}
		return queries;
	}

	void findSimilarContexts(List<ICompilationUnit> icus, LR aLR) throws Exception {
		System.out.println("[DBG1] " + aLR);
		List<UTASTBindingManagerAbstract> bindingManagerList;
		UTIGeneralVisitor<MethodDeclaration> mVisitor = new UTGeneralVisitor<MethodDeclaration>() {
			public boolean visit(MethodDeclaration node) {
				results.add(node);
				return true;
			}
		};
		for (ICompilationUnit unit : icus) {
			cntClass++;
			File filePath = UTPlugin.getFullFilePath(unit);
			String sourceCode = UTFile.getContents(filePath.getAbsolutePath());
			UTASTParser astParser = new UTASTParser();
			CompilationUnit parser = astParser.parse(unit);
			List<MethodDeclaration> lstMethods = mVisitor.getResults();
			lstMethods.clear();
			parser.accept(mVisitor);
			cntMethod += lstMethods.size();
			// ///////////////////////////////////////////////
			UTAddNewValue newValueHandler = new UTAddNewValue();
			newValueHandler.manipulate(parser);
			bindingManagerList = newValueHandler.getBindingManagerList();
			// ///////////////////////////////////////////////
			for (int j = 0; j < lstMethods.size(); j++) {
				MethodDeclaration iMethod = lstMethods.get(j);
				Block body = iMethod.getBody();
				if (body == null) {
					continue;
				}
				Node iNode = nodeConverter.convertMethod(iMethod, sourceCode, bindingManagerList);
				if (iNode == null) {
					System.out.println("------------------------------------------");
					System.out.println("[DBG] FILE: " + filePath);
					System.out.println("[DBG] METHOD: " + iMethod.getName());
					System.out.println("[DBG] OFFSET: " + iMethod.getStartPosition());
					System.out.println("" + iMethod);
					System.out.println("==========================================");
					throw new RuntimeException("");
				}
				System.out.println("[DBG6] PATH: " + filePath.getAbsolutePath().replace(System.getProperty("user.home"), ""));
				if (aLR == LR.RIGHT) {
					findSimilarContextsExtra(matcherOldRev, parser, filePath);
					matcherOldRev.matchEditMappingEntry(qTreeParGenOldRev, iNode, true);
				} else {
					findSimilarContextsExtra(matcherNewRev, parser, filePath);
					matcherNewRev.matchEditMappingEntry(qTreeParGenNewRev, iNode, true);
				}
			}
			// ///////////////////////////////////////////////
		}
		System.out.println("[DBG7] DONE: findSimilarContexts - " + aLR);
	}

	/**
	 * Find similar sub trees.
	 * 
	 * @param iPackageFragments the i package fragments
	 * @param isOldRev
	 * @throws Exception the exception
	 */
	void findSimilarContexts(IPackageFragment[] iPackageFragments, LR aLR) throws Exception {
		System.out.println("[DBG1] " + aLR);
		List<UTASTBindingManagerAbstract> bindingManagerList;
		UTIGeneralVisitor<MethodDeclaration> mVisitor = new UTGeneralVisitor<MethodDeclaration>() {
			public boolean visit(MethodDeclaration node) {
				results.add(node);
				return true;
			}
		};
		for (int i = 0; i < iPackageFragments.length; i++) {
			IPackageFragment pkgElem = iPackageFragments[i];
			if (pkgElem.getKind() == IPackageFragmentRoot.K_SOURCE) {
				System.out.println("[DBG] BGN: findSimilarContexts - " + pkgElem.getElementName());
				for (ICompilationUnit unit : pkgElem.getCompilationUnits()) {
					cntClass++;
					File filePath = UTPlugin.getFullFilePath(unit);
					String sourceCode = UTFile.getContents(filePath.getAbsolutePath());
					UTASTParser astParser = new UTASTParser();
					CompilationUnit parser = astParser.parse(unit);
					List<MethodDeclaration> lstMethods = mVisitor.getResults();
					lstMethods.clear();
					parser.accept(mVisitor);
					cntMethod += lstMethods.size();
					UTAddNewValue newValueHandler = new UTAddNewValue();
					newValueHandler.manipulate(parser);
					bindingManagerList = newValueHandler.getBindingManagerList();
					for (int j = 0; j < lstMethods.size(); j++) {
						MethodDeclaration iMethod = lstMethods.get(j);
						Block body = iMethod.getBody();
						if (body == null) {
							continue;
						}
						Node iNode = nodeConverter.convertMethod(iMethod, sourceCode, bindingManagerList);
						if (iNode == null) {
							System.out.println("------------------------------------------");
							System.out.println("[DBG] FILE: " + filePath);
							System.out.println("[DBG] METHOD: " + iMethod.getName());
							System.out.println("[DBG] OFFSET: " + iMethod.getStartPosition());
							System.out.println("" + iMethod);
							System.out.println("==========================================");
							throw new RuntimeException("");
						}
						if (aLR == LR.RIGHT) {
							findSimilarContextsExtra(matcherOldRev, parser, filePath);
							matcherOldRev.matchEditMappingEntry(qTreeParGenOldRev, iNode, true);
						} else {
							findSimilarContextsExtra(matcherNewRev, parser, filePath);
							matcherNewRev.matchEditMappingEntry(qTreeParGenNewRev, iNode, true);
						}
					}
				}
				System.out.println("[DBG] END: findSimilarContexts - " + pkgElem.getElementName());
			}
		}
		System.out.println("[DBG7] DONE: findSimilarContexts - " + aLR);
	}

	/**
	 * Find similar contexts extra.
	 * 
	 * @param aMatcher the a matcher
	 * @param aUnit the a unit
	 * @param aFilePath the a file path
	 */
	void findSimilarContextsExtra(ITreeMatch aMatcher, CompilationUnit aUnit, File aFilePath) {
		if (aMatcher instanceof TreeMatchAgainstOldRev) {
			matcherOldRev.setMatchCounter(cntMatchedOldRev);
			// matcherOldRev.setCompilationUnit(aUnit);
			matcherOldRev.setFilePath(aFilePath);
		} else {
			matcherNewRev.setMatchCounter(cntMatchedNewRev);
			// matcherNewRev.setCompilationUnit(aUnit);
			matcherNewRev.setFilePath(aFilePath);
		}
	}

	/**
	 * Process result similar sub trees.
	 */
	void processResultSimilarSubTrees() {
		UTLog.println(true, "DBG__________________________________________");
		UTLog.println(true, "[RST] THE SUBTREES IN OLD REVISION, SIMILAR TO OLD QUERY TREE");
		UTLog.println(true, "DBG__________________________________________");
		UTSubTreeMatchResult.computeSimilarity(matcherOldRev.getSubTrees());
		UTSubTreeMatchResult.computeSimilarity(matcherNewRev.getSubTrees());
		System.out.println("[DBG] * FOUND SIMILAR SUBTREES FROM OLD REVISION, SIZE: #" + matcherOldRev.getSubTrees().size() + ")");
		UTSubTreeMatchResult.print(matcherOldRev.getSubTrees(), true);
		System.out.println("[DBG] * FOUND SIMILAR SUBTREES FROM NEW REVISION, SIZE: #" + matcherNewRev.getSubTrees().size() + ")");
		UTSubTreeMatchResult.print(matcherNewRev.getSubTrees(), true);
		System.out.println("------------------------------------------");
		System.out.println("[DBG] * OLD REV");
		qTreeParGenOldRev.print();
		System.out.println("------------------------------------------");
		System.out.println("[DBG] * NEW REV");
		qTreeParGenNewRev.print();
		System.out.println("------------------------------------------");
		/*
		clustererOldRev.groupByClusterer(matcherOldRev.getSubTrees());
		clustererNewRev.groupByClusterer(matcherNewRev.getSubTrees());
		*/
	}

	/**
	 * Show check box tree.
	 * 
	 * @throws Exception the exception
	 */
	void showCheckBoxTree() throws Exception {
		/*
		List<RTEDInfoSubTree> groupClusteredBySim = clustererOldRev.getGroups(matcherOldRev.getSubTrees(), 2);
		UTCfg.getInst().readConfig().CLUSTER_LIMIT_SIZE); // clusterer.getFirstGroup(matcherOldRev.getSubTrees());
		showCheckBoxTree(groupClusteredBySim);
		*/
	}

	/**
	 * Show check box tree.
	 * 
	 * @param aGroup the a group clustered by sim
	 * @throws Exception the exception
	 */
	private void showCheckBoxTree(List<RTEDInfoSubTree> aGroup) throws Exception {
		CriticsOverlayNewView vwOverlay = (CriticsOverlayNewView) UTPlugin.getView(UTPlugin.ID_VIEW_NEW_OVERLAY);
		vwOverlay.setData(aGroup);

		List<Node> lstQTreeParGenNewRev = qTreeParGenNewRev.getAllNodes();
		List<Node> lstQTreeNewRev = qTreeNewRev.getAllNodes();
		int szParGenNewRev = lstQTreeParGenNewRev.size();
		int szNewRev = lstQTreeNewRev.size();
		if (szParGenNewRev != szNewRev) {
			throw new RuntimeException();
		}
		for (int i = 0; i < lstQTreeNewRev.size(); i++) {
			Node iNode = lstQTreeNewRev.get(i);
			Node iNodeParGen = lstQTreeParGenNewRev.get(i);
			String valNodeParGen = iNodeParGen.getValue();
			String valNew = iNodeParGen.getValueNew();
			if (valNew == null || !valNodeParGen.contains(PARM_VAR_NAME)) {
				iNode.setValueParm(valNodeParGen);
			} else {
				//iNode.setValueParm(valNew);
				List<NodeValueArg> lstNodeValueArg = iNodeParGen.getValueArgList();
				//TODO: Need to check which identifier is generalized here
				String parGenValue = valNodeParGen;
				for(NodeValueArg arg : lstNodeValueArg){
					String orgID = arg.getArgOrg();
					Map<SimpleName, Boolean> map = iNode.getParamMap();
					if(map != null){
						for(SimpleName name : map.keySet()){
							if(name.getIdentifier().equals(orgID) && map.get(name)){
								String newValue = arg.getArgNew().replaceAll("\\$", "\\\\\\$");
								String pattern = PARM_VAR_NAME.replaceAll("\\$", "\\\\\\$");
								parGenValue = parGenValue.replaceAll(pattern, newValue);
							}
						}
					}
				}
				iNode.setValueParm(parGenValue);
			}
		}
		List<Node> lstQTreeParGenOldRev = qTreeParGenOldRev.getAllNodes();
		List<Node> lstQTreeOldRev = qTreeOldRev.getAllNodes();
		int szParGenOldRev = lstQTreeParGenOldRev.size();
		int szOldRev = lstQTreeOldRev.size();
		if (szParGenOldRev != szOldRev) {
			throw new RuntimeException();
		}
		for (int i = 0; i < lstQTreeOldRev.size(); i++) {
			Node iNode = lstQTreeOldRev.get(i);
			Node iNodeParGen = lstQTreeParGenOldRev.get(i);
			String valNodeParGen = iNodeParGen.getValue();
			String valNew = iNodeParGen.getValueNew();
			if (valNew == null || !valNodeParGen.contains(PARM_VAR_NAME)) {
				iNode.setValueParm(valNodeParGen);
			} else {
				//iNode.setValueParm(valNew);
				List<NodeValueArg> lstNodeValueArg = iNodeParGen.getValueArgList();
				//TODO: Need to check which identifier is generalized here
				String parGenValue = valNodeParGen;
				for(NodeValueArg arg : lstNodeValueArg){
					String orgID = arg.getArgOrg();
					Map<SimpleName, Boolean> map = iNode.getParamMap();
					if(map != null){
						for(SimpleName name : map.keySet()){
							if(name.getIdentifier().equals(orgID) && map.get(name)){
								String newValue = arg.getArgNew().replaceAll("\\$", "\\\\\\$");
								String pattern = PARM_VAR_NAME.replaceAll("\\$", "\\\\\\$");
								parGenValue = parGenValue.replaceAll(pattern, newValue);
							}
						}
					}
				}
				iNode.setValueParm(parGenValue);
			}
		}
		// vwOverlay.setQueryTree(qTreeParGenNewRev, qTreeParGenOldRev);
		vwOverlay.setQueryTree(qTreeNewRev, qTreeOldRev);
		vwOverlay.show();
	}

	/**
	 * Gets the method name.
	 * 
	 * @param aSource the a source
	 * @param aSubTree the a sub tree
	 * @return the method name
	 */
	String getMethodName(String aSource, Node aSubTree) {
		UTASTNodeFinder finder = new UTASTNodeFinder();
		MethodDeclaration iMethod = finder.findMethod(aSource, aSubTree.getEntity().getSourceRange(), false);
		return iMethod.getName().getFullyQualifiedName();
	}

	/**
	 * Check change pattern violation.
	 * 
	 * @throws Exception the exception
	 */
	void detectAnomalies() throws Exception {
		boolean isPrintable = true;
		UTLog.println(isPrintable, "DBG__________________________________________");
		UTLog.println(isPrintable, "[RST] DETECT ANOMALIES");
		UTLog.println(isPrintable, "DBG__________________________________________");
		List<RTEDInfoSubTree> lstSubTreeInfoOldRev = matcherOldRev.getSubTrees();
		List<RTEDInfoSubTree> lstSubTreeInfoNewRev = matcherNewRev.getSubTrees();
		List<Node> lstMethodLvOldRev = new ArrayList<Node>();
		List<Node> lstMethodLvNewRev = new ArrayList<Node>();
		System.out.println("[DBG1] GROUP FROM OLD REV ==========================================");
		for (int i = 0; i < lstSubTreeInfoOldRev.size(); i++) {
			RTEDInfoSubTree iSubTreeInfo = lstSubTreeInfoOldRev.get(i);
			Node iNode = iSubTreeInfo.getSubTree();
			if (iNode.getEntity().getType().isMethod()) {
				System.out.println("[DBG] " + iNode + ", " + iNode.getClassName());
				lstMethodLvOldRev.add(iNode);
			}
		}
		System.out.println("[DBG2] GROUP FROM NEW REV ==========================================");
		for (int i = 0; i < lstSubTreeInfoNewRev.size(); i++) {
			RTEDInfoSubTree iSubTreeInfo = lstSubTreeInfoNewRev.get(i);
			Node iNode = iSubTreeInfo.getSubTree();
			if (iNode.getEntity().getType().isMethod()) {
				System.out.println("[DBG] " + iNode + ", " + iNode.getClassName());
				lstMethodLvNewRev.add(iNode);
			}
		}
		HashMap<Node, Node> map = detectAnomalyMethodLevel(lstMethodLvNewRev, lstMethodLvOldRev);
		for (int i = 0; i < lstSubTreeInfoOldRev.size(); i++) {
			RTEDInfoSubTree iSubTreeInfo = lstSubTreeInfoOldRev.get(i);
			Node iNode = iSubTreeInfo.getSubTree();
			if (iNode.getEntity().getType().isMethod()) {
				for (Map.Entry<Node, Node> e : map.entrySet()) {
					Node jNode = e.getKey();
					if (iNode.eq(jNode) && jNode.isAnomaly()) {
						iSubTreeInfo.setAnomaly(true);
						break;
					}
				}
			}
		}
	}

	/**
	 * Detect anomaly method level.
	 * 
	 * @param aLstMethodLvNewRev the a lst method lv new rev
	 * @param aLstMethodLvOldRev the a lst method lv old rev
	 */
	HashMap<Node, Node> detectAnomalyMethodLevel(List<Node> aLstMethodLvNewRev, List<Node> aLstMethodLvOldRev) {
		Node dummy = new Node(JavaEntityType.METHOD, "DUMMYELEMENT");
		dummy.setEntity(new SourceCodeEntity("DUMMYELEMENT", JavaEntityType.METHOD, new SourceRange(0, 1)));
		HashMap<Node, Node> mapTwoGroup = new HashMap<Node, Node>();
		for (int i = 0; i < aLstMethodLvOldRev.size(); i++) {
			Node iNode = aLstMethodLvOldRev.get(i);
			mapTwoGroup.put(iNode, dummy);
			for (int j = 0; j < aLstMethodLvNewRev.size(); j++) {
				Node jNode = aLstMethodLvNewRev.get(j);
				if (iNode.eqName(jNode)) {
					mapTwoGroup.put(iNode, jNode);
					break;
				}
			}
		}
		for (Map.Entry<Node, Node> e : mapTwoGroup.entrySet()) {
			Node kNode = e.getKey();
			Node vNode = e.getValue();
			if (vNode.eq(dummy)) {
				kNode.setSummary(true);
				kNode.setAnomaly(true);
			} else {
				kNode.setSummary(true);
			}
		}
		return mapTwoGroup;
	}

	void detectAnomalyClassLevel() {
	}

	void detectAnomalyBlockLevel() {
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
		// UTLog.println(true, "[DBG0] INSERT");
		// UTChange.printNode(mInsertNodeList, true);
		// System.out.println("------------------------------------------");
		// UTLog.println(true, "[DBG1] DELETE");
		// UTChange.printNode(mDeleteNodeList, true);
		// System.out.println("------------------------------------------");
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

	void prepareToStart() {
		CriticsContextSelectionView vwIntCxtSelOldRev = (CriticsContextSelectionView) UTPlugin.getView(UTPlugin.ID_VIEW_SELECTCTX_OLDREV);
		CriticsContextSelectionView vwIntCxtSelNewRev = (CriticsContextSelectionView) UTPlugin.getView(UTPlugin.ID_VIEW_SELECTCTX_NEWREV);
		if (vwIntCxtSelOldRev.getRootNode() == null || vwIntCxtSelNewRev.getRootNode() == null) {
			System.out.println("[USG] PLEASE OPEN CONTEXT SELECTION VIEW");
			return;
		}
		qTreeOldRev = (Node) vwIntCxtSelOldRev.getRootNode().getData();
		qTreeNewRev = (Node) vwIntCxtSelNewRev.getRootNode().getData();
		System.out.println("[DBG0] NODES SELECTED IN CONTEXT SELECTION VIEW (OLD REV.)");
		qTreeOldRev.print(true);
		System.out.println("[DBG1] NODES SELECTED IN CONTEXT SELECTION VIEW (NEW REV.)");
		qTreeNewRev.print(true);
		getEditsFromQTree();
		matcherQueryTree.matchEditMapping(qTreeOldRev, qTreeNewRev);
	}

	/**
	 * Find tree similar to transformed tree.
	 * 
	 * @param transformedTree the transformed tree
	 * @throws JavaModelException the java model exception
	 */
	void findTreeSimilarToTransformedTree(Node transformedTree) throws JavaModelException {
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
					findSimilarContextsExtra(matcherNewRev, parser, f);
					matcherNewRev.matchEditMappingEntry(transformedTree, iNode, true);
				}
			}
		}
		UTSubTreeMatchResult.computeSimilarity(matcherNewRev.getSubTrees());
		UTSubTreeMatchResult.print(matcherNewRev.getSubTrees(), false);
	}

	void detectAnomalies2() throws Exception {
		boolean isPrintable = true;
		UTLog.println(isPrintable, "DBG__________________________________________");
		UTLog.println(isPrintable, "[RST] DETECT ANOMALIES");
		UTLog.println(isPrintable, "DBG__________________________________________");
		// int szGroup = UTCfg.getInst().readConfig().CLUSTER_LIMIT_SIZE;
		List<RTEDInfoSubTree> lstSubTreeInfoOldRev = matcherOldRev.getSubTrees();
		List<RTEDInfoSubTree> lstSubTreeInfoNewRev = matcherNewRev.getSubTrees();
		// List<RTEDInfoSubTree> groupOldRev1st = clustererOldRev.getGroups(lstSubTreeInfoOldRev, 1, 1);
		// List<RTEDInfoSubTree> groupNewRev1st = clustererNewRev.getGroups(lstSubTreeInfoNewRev, 1, 1);
		// int szGroupOldRev1st = groupOldRev1st.size();
		// int szGroupNewRev1st = groupNewRev1st.size();
		List<Node> lstMethodLvOldRev = new ArrayList<Node>();
		List<Node> lstMethodLvNewRev = new ArrayList<Node>();
		// if (szGroupOldRev1st > szGroupNewRev1st) {
		// ; // Good.
		// } else if (szGroupOldRev1st < szGroupNewRev1st) {
		// groupOldRev1st = clustererOldRev.getGroups(lstSubTreeInfoOldRev, 1, 2);
		// } else {
		// ; // Same.
		// }
		System.out.println("[DBG1] GROUP FROM OLD REV ==========================================");
		for (int i = 0; i < lstSubTreeInfoOldRev.size(); i++) {
			RTEDInfoSubTree iSubTreeInfo = lstSubTreeInfoOldRev.get(i);
			Node iNode = iSubTreeInfo.getSubTree();
			if (iNode.getEntity().getType().isMethod()) {
				System.out.println("[DBG] " + iNode);
				lstMethodLvOldRev.add(iNode);
			}
		}
		System.out.println("[DBG2] GROUP FROM NEW REV ==========================================");
		for (int i = 0; i < lstSubTreeInfoNewRev.size(); i++) {
			RTEDInfoSubTree iSubTreeInfo = lstSubTreeInfoNewRev.get(i);
			Node iNode = iSubTreeInfo.getSubTree();
			if (iNode.getEntity().getType().isMethod()) {
				System.out.println("[DBG] " + iNode);
				lstMethodLvNewRev.add(iNode);
			}
		}
		// List<RTEDInfoSubTree> groupOldRev = clustererOldRev.getGroups(lstSubTreeInfoOldRev, 2); // clusterer.getFirstGroup(matcherOldRev.getSubTrees());
		// List<RTEDInfoSubTree> groupNewRev = clustererNewRev.getGroups(lstSubTreeInfoNewRev, szGroup); // clusterer.getFirstGroup(matcherNewRev.getSubTrees());
		detectAnomalyMethodLevel(lstMethodLvNewRev, lstMethodLvOldRev);
	}

	// /**
	// *
	// */
	// void detectAnomalies_oldVer() {
	// transformOldRev.transformByChangeEdits(firstGroupClusteredBySim, mInsertNodeList, mDeleteNodeList, isPrintable);
	// for (int i = 0; i < firstGroupClusteredBySim.size(); i++) {
	// RTEDInfoSubTree iSubTree = firstGroupClusteredBySim.get(i);
	// Node iNode = iSubTree.getSubTree();
	// UTLog.println(false, "[DBG1] [THE FIRST GROUP] " + iSubTree.toString());
	// TreeNode[] iPath = iNode.getPath();
	// StringBuilder iBuf = new StringBuilder();
	// for (int j = 0; j < iPath.length; j++) {
	// Node jNode = (Node) iPath[j];
	// iBuf.append(jNode + " ");
	// }
	// UTLog.println(isPrintable, "[DBG1]   " + iBuf.toString());
	// // iSubTree.getSubTree().print();
	// UTLog.println(isPrintable, "------------------------------------------");
	// findTreeSimilarToTransformedTree(iSubTree.getSubTree());
	// UTLog.println(isPrintable, "------------------------------------------");
	// }
	// }
	// ////////////////////
	// /**
	// * Sets the change q tree.
	// *
	// * @param aOffset the a offset
	// * @param aEdit the a edit
	// * @param isOldRev the is old rev
	// */
	// void setChangeQTree(int aOffset, UTEnumEdit aEdit, boolean isOldRev) {
	// Enumeration<?> eQTree;
	// if (isOldRev)
	// eQTree = queryTreeOldRev.preorderEnumeration();
	// else
	// eQTree = queryTreeNewRev.preorderEnumeration();
	// setChangeQTree(eQTree, aOffset, aEdit);
	// }
	// ////////////////////
	// /**
	// * Sets the change q tree.
	// *
	// * @param eQTree the e q tree
	// * @param aOffset the a offset
	// * @param aEdit the a edit
	// */
	// void setChangeQTree(Enumeration<?> eQTree, int aOffset, UTEnumEdit aEdit) {
	// while (eQTree.hasMoreElements()) {
	// Node iNode = (Node) eQTree.nextElement();
	// int iOffset = iNode.getEntity().getStartPosition();
	// if (iOffset == aOffset) {
	// switch (aEdit) {
	// case INSERT:
	// iNode.setInsert(true);
	// break;
	// case DELETE:
	// iNode.setDelete(true);
	// break;
	// default:
	// break;
	// }
	// }
	// }
	// }
	// ////////////////////
	// /**
	// * What change.
	// *
	// * @param aChange the a change
	// * @return the uT enum edit
	// */
	// UTEnumEdit whatChange(SourceCodeChange aChange) {
	// UTEnumEdit aEChange;
	// if (aChange.getClass().getSimpleName().equalsIgnoreCase("insert")) {
	// aEChange = UTEnumEdit.INSERT;
	// } else if (aChange.getClass().getSimpleName().equalsIgnoreCase("delete")) {
	// aEChange = UTEnumEdit.DELETE;
	// } else {
	// aEChange = UTEnumEdit.UPDATE;
	// }
	// return aEChange;
	// }

	// void generalize_dummyData(boolean isOldRev) {
	// System.out.println("FIND SIMILAR CONTEXTS(" + isOldRev + ") ------------------------------------------");
	// if (isOldRev) {
	// Enumeration<?> e = queryTreeOldRev.preorderEnumeration();
	// while (e.hasMoreElements()) {
	// Node iNode = (Node) e.nextElement();
	// if (iNode.getValue().contains("userData")) { // for testing...
	// iNode.setValue("$v0");
	// }
	// }
	// queryTreeOldRev.print();
	// } else {
	// Enumeration<?> e = queryTreeNewRev.preorderEnumeration();
	// while (e.hasMoreElements()) {
	// Node iNode = (Node) e.nextElement();
	// if (iNode.getValue().equals("userData")) { // for testing...
	// iNode.setValue("$v0");
	// }
	// }
	// queryTreeOldRev.print();
	// }
	// System.out.println("------------------------------------------");
	// }
}
