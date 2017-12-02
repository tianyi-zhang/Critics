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
package edu.utexas.seal.plugins.util.root;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;

import rted.datastructure.manager.TreeMatchBetweenQueryTrees;
import rted.distance.RTEDInfoSubTree;
import rted.processor.RTEDProcessor;
import ut.seal.plugins.utils.UTLog;
import ut.seal.plugins.utils.ast.UTASTNodeConverter;
import ut.seal.plugins.utils.ast.UTASTNodeFinder;
import ut.seal.plugins.utils.change.UTChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import edu.utexas.seal.plugins.CriticsMatchTreeEditDistance;
import edu.utexas.seal.plugins.edit.UTEnumEdit;
import edu.utexas.seal.plugins.handler.CriticsCommonHandler;
import edu.utexas.seal.plugins.handler.CriticsFindContext;
import edu.utexas.seal.plugins.handler.CriticsSelectContext;
import edu.utexas.seal.plugins.util.UTCriticsPairFileInfo;
import edu.utexas.seal.plugins.util.UTCriticsTextSelection;

/**
 * @author Myoungkyu Song
 * @date Dec 4, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTCriticsTransform {
	private CriticsMatchTreeEditDistance	mMatchTED;
	private CriticsSelectContext			mSelectContext;
	private CriticsFindContext				mFindContext;
	private UTASTNodeFinder					nodeFinder			= null;
	private UTASTNodeConverter				nodeConverter		= null;
	private TreeMatchBetweenQueryTrees		matcherQueryTree	= null;
	private UTChangeDistiller				changeDistiller		= null;

	UTCriticsNode							utNode				= new UTCriticsNode();

	public UTCriticsTransform() {
	}

	public UTCriticsTransform(CriticsMatchTreeEditDistance aMatchTED) {
		mMatchTED = aMatchTED;
		nodeFinder = mMatchTED.getNodeFinder();
		nodeConverter = mMatchTED.getNodeConverter();
		matcherQueryTree = mMatchTED.getMatcherQueryTree();
		changeDistiller = mMatchTED.getUTChangeDistiller();
	}

	public UTCriticsTransform(CriticsCommonHandler aCommonHandler) {
		if (aCommonHandler instanceof CriticsFindContext) {
			initiate((CriticsFindContext) aCommonHandler);
		}
		if (aCommonHandler instanceof CriticsSelectContext) {
			initiate((CriticsSelectContext) aCommonHandler);
		}
	}

	private void initiate(CriticsFindContext aFindContext) {
		mFindContext = aFindContext;
		nodeFinder = mFindContext.getNodeFinder();
		nodeConverter = mFindContext.getNodeConverter();
		matcherQueryTree = mFindContext.getMatcherQueryTree();
		changeDistiller = mFindContext.getUTChangeDistiller();
	}

	public void initiate(CriticsSelectContext aSelectContext) {
		mSelectContext = aSelectContext;
		nodeFinder = mSelectContext.getNodeFinder();
		nodeConverter = mSelectContext.getNodeConverter();
		matcherQueryTree = mSelectContext.getMatcherQueryTree();
		changeDistiller = mSelectContext.getUTChangeDistiller();
	}

	/**
	 * 
	 * @param subTree
	 * @param isPrintable
	 */
	public void transformByChangeEdits(List<RTEDInfoSubTree> aFirstGroupClusteredBySim, //
			List<Node> aInsertNodeList, List<Node> aDeleteNodeList, boolean isPrintable) {
		// //////////////////////////////////////////////////////////////////
		// TEST - LOOP BODY
		// //////////////////////////////////////////////////////////////////
		{
			RTEDInfoSubTree iSubTreeInfo = aFirstGroupClusteredBySim.get(0);
			Node iSubTree = iSubTreeInfo.getSubTree().copy();
			System.out.println("DBG0--BEFORE----------------------------------------");
			iSubTree.print();

			for (int i = 0; i < aDeleteNodeList.size(); i++) {
				Node dNode = aDeleteNodeList.get(i);
				Enumeration<?> eSubTree = iSubTree.postorderEnumeration();
				while (eSubTree.hasMoreElements()) {
					Node iNode = (Node) eSubTree.nextElement();
					if (iNode.isLeaf() && dNode.eq(iNode)) {
						iNode.removeFromParent();
					}
				}
			}
			for (int i = 0; i < aDeleteNodeList.size(); i++) {
				Node dNode = aDeleteNodeList.get(i);
				Enumeration<?> eSubTree = iSubTree.postorderEnumeration();
				while (eSubTree.hasMoreElements()) {
					Node iNode = (Node) eSubTree.nextElement();

					if (dNode.getValue().contains("userInfoDisplay.hasUser(userElement)"))
						if (iNode.getValue().contains("userInfoDisplay.hasUser(userElement)"))
							System.out.print("" + iNode.getChildCount());

				}
			}
			System.out.println("DBG1--AFTER----------------------------------------");
			iSubTree.print();
		}
		// //////////////////////////////////////////////////////////////////
		List<SourceCodeChange> changes = changeDistiller.getChanges();
		if (isPrintable) {
			changeDistiller.printChanges();
		}
		File file = UTCriticsPairFileInfo.getLeftFile();
		ICompilationUnit iunit = UTCriticsPairFileInfo.getLeftICompilationUnit();
		String strSrcNewRev = UTCriticsTextSelection.leftMergeSourceViewer.getSourceViewer().getDocument().get(); // mMatchTED.getLeftSRViewer().getDocument().get();
		String strSrcOldRev = UTCriticsTextSelection.rightMergeSourceViewer.getSourceViewer().getDocument().get(); // mMatchTED.getRightSRViewer().getDocument().get();
		List<Node> insertedNodes = new ArrayList<Node>();
		List<Node> deletedNodes = new ArrayList<Node>();
		List<Node[]> updateNodes = new ArrayList<Node[]>();
		UTEnumEdit eChange = UTEnumEdit.INVALID;
		for (int j = 0; j < changes.size(); j++) {
			SourceCodeChange iChange = changes.get(j);
			eChange = whatChange(iChange);
			switch (eChange) {
			case INSERT:
			case DELETE:
				SourceCodeEntity iSrcEntity = iChange.getChangedEntity();
				String strLabel = iChange.getLabel();
				String strEntityLabel = iSrcEntity.getLabel();
				String strVal = iChange.getChangedEntity().getUniqueName();
				int posBgn = iSrcEntity.getStartPosition();
				int posEnd = iSrcEntity.getEndPosition();
				String strCodeAddOrDel = null;
				if (eChange == UTEnumEdit.INSERT) {
					strCodeAddOrDel = strSrcNewRev;
				} else if (eChange == UTEnumEdit.DELETE) {
					strCodeAddOrDel = strSrcOldRev;
				}
				Node refRootNode = nodeFinder.findMethod(iunit, posBgn, posEnd, strCodeAddOrDel, nodeConverter, file);
				UTLog.println(false, "[DBG0] (" + posBgn + ", " + posEnd + ")(" + strLabel + ")(" + strEntityLabel + ")(" + strVal + ")--(" + refRootNode + ")");
				Node refNode = nodeFinder.findASTNode(refRootNode, iSrcEntity);
				if (eChange == UTEnumEdit.INSERT) {
					insertedNodes.add(refNode);
				} else if (eChange == UTEnumEdit.DELETE) {
					deletedNodes.add(refNode);
				}
				break;
			case UPDATE:
				Node[] rNode = { new Node(null, "OLD"), new Node(null, "NEW") };
				updateNodes.add(rNode);
				break;
			default:
				break;
			}
		}
		// ///////////////////////////////////////////////////////////
		for (int i = 0; i < aFirstGroupClusteredBySim.size(); i++) {
			RTEDInfoSubTree subTree = aFirstGroupClusteredBySim.get(i);
			transformByChangeEdits(subTree, insertedNodes, deletedNodes);
		}
	}

	void transformByChangeEdits(RTEDInfoSubTree aSubTree, List<Node> aInsertedNodes, List<Node> aDeletedNodes) {
		deleteSubTree(aSubTree, aDeletedNodes);
		insertSubTree(aSubTree, aInsertedNodes);
	}

	private void deleteSubTree(RTEDInfoSubTree aSubTree, List<Node> deletedNodes) {
		/*
		List<int[]> editMappingSTree = aSubTree.getEditMapping();
		Map<Integer, Node> mQTreeOldRevIDMngr = aSubTree.getQueryTreeIDMngr();
		Map<Integer, Node> mSTreeOldRevIDMngr = aSubTree.getSubTreeIDMngr();
		Map<Integer, Node> mQueryTreeIDMngr = aSubTree.getQueryTreeIDMngr();
		Map<Integer, Node> mSubTreeIDMngr = aSubTree.getSubTreeIDMngr();

		boolean isPrintable = false;
		if (isPrintable) {
			utNode.printMapping(editMappingSTree, mQueryTreeIDMngr, mSubTreeIDMngr);
		}
		List<Node> refUpperNodes = utNode.getUpperNodes(deletedNodes);
		if (isPrintable) {
			System.out.println("DBG0--BEFORE----------------------------------------");
			aSubTree.getSubTree().print();
		}
		for (int[] nodeAlignment : editMappingSTree) {
			if (areMapped(nodeAlignment)) {

				Node ndQTreeOldRev = mQTreeOldRevIDMngr.get(nodeAlignment[0]);
				Node ndSTreeOldRev = mSTreeOldRevIDMngr.get(nodeAlignment[1]);
				deleteSubTree(ndSTreeOldRev, ndQTreeOldRev, refUpperNodes);
			}
		}
		if (isPrintable) {
			System.out.println("DBG0--AFTER----------------------------------------");
			aSubTree.getSubTree().print();
		} */
	}

	void deleteSubTree(Node aNdTarget, Node aNdQTreeOldRev, List<Node> aUpperNodes) {
		if (aNdQTreeOldRev == null)
			return;
		for (int i = 0; i < aUpperNodes.size(); i++) {
			Node iNode = aUpperNodes.get(i);
			Node iParentNode = (Node) iNode.getParent();
			if (aNdQTreeOldRev.eq(iParentNode)) {
				int childIndex = iParentNode.getIndex(iNode);
				aNdTarget.remove(childIndex);
				// Node copiedInsertedNode = iNode.copy();
				// aNdTarget.insert(copiedInsertedNode, childIndex);
			}
		}
	}

	void insertSubTree(RTEDInfoSubTree aSubTree, List<Node> aInsertedNodes) {
		/* 
		List<int[]> editMappingSTree = aSubTree.getEditMapping();
		Map<Integer, Node> mQTreeOldRevIDMngr = aSubTree.getQueryTreeIDMngr();
		Map<Integer, Node> mSTreeOldRevIDMngr = aSubTree.getSubTreeIDMngr();

		RTEDProcessor queryTreeRTED = matcherQueryTree.getRTEDProc();
		List<int[]> editMappingQTree = queryTreeRTED.getEditMapping();
		@SuppressWarnings("unused")
		Map<Integer, Node> mQTreeOldRevIDMngr2 = matcherQueryTree.getQueryTreeIDMngrOldRev();
		Map<Integer, Node> mQTreeNewRevIDMngr = matcherQueryTree.getQueryTreeIDMngrNewRev();

		Map<Integer, Node> mQueryTreeIDMngr = aSubTree.getQueryTreeIDMngr();
		Map<Integer, Node> mSubTreeIDMngr = aSubTree.getSubTreeIDMngr();
		boolean isPrintable = false;
		if (isPrintable) {
			utNode.printMapping(editMappingSTree, mQueryTreeIDMngr, mSubTreeIDMngr);
		}
		List<Node> refUpperNodes = utNode.getUpperNodes(aInsertedNodes);
		if (isPrintable) {
			System.out.println("DBG0--BEFORE----------------------------------------");
			aSubTree.getSubTree().print();
		}
		for (int[] nodeAlignment : editMappingSTree) {
			if (areMapped(nodeAlignment)) {

				Node ndQTreeOldRev = mQTreeOldRevIDMngr.get(nodeAlignment[0]);
				Node ndSTreeOldRev = mSTreeOldRevIDMngr.get(nodeAlignment[1]);
				Node ndQTreeNewRev = getNodeQTreeNewRev(ndQTreeOldRev, editMappingQTree, //
						mQTreeOldRevIDMngr, mQTreeNewRevIDMngr);

				insertSubTree(ndSTreeOldRev, ndQTreeNewRev, refUpperNodes);
			}
		}
		if (isPrintable) {
			System.out.println("DBG0--AFTER----------------------------------------");
			aSubTree.getSubTree().print();
		} */
	}

	void insertSubTree(Node aNdTarget, Node aNdQTreeNewRev, List<Node> aUpperNodes) {
		if (aNdQTreeNewRev == null)
			return;
		for (int i = 0; i < aUpperNodes.size(); i++) {
			Node iNode = aUpperNodes.get(i);
			Node iParentNode = (Node) iNode.getParent();
			if (aNdQTreeNewRev.eq(iParentNode)) {
				int childIndex = iParentNode.getIndex(iNode);
				Node copiedInsertedNode = iNode.copy();
				aNdTarget.insert(copiedInsertedNode, childIndex);
			}
		}
	}

	Node getNodeQTreeNewRev(Node aNdQTreeOldRev, List<int[]> aEditMappingQTree, //
			Map<Integer, Node> aQTreeOldRevIDMngr, Map<Integer, Node> aQTreeNewRevIDMngr) {
		Node result = null;
		if (aNdQTreeOldRev == null) {
			return result;
		}
		for (int[] nodeAlignment : aEditMappingQTree) {
			int iNdIdOldRev = nodeAlignment[0];
			int iNdIdNewRev = nodeAlignment[1];
			Node iNdQTreeOldRev = aQTreeOldRevIDMngr.get(iNdIdOldRev);
			if (iNdQTreeOldRev != null && aNdQTreeOldRev.eq(iNdQTreeOldRev)) {
				result = aQTreeNewRevIDMngr.get(iNdIdNewRev);
				break;
			}
		}
		return result;
	}

	void insertSubTreeIntoOldVerSubTree(RTEDInfoSubTree subTree, int[] nodeAlignment, //
			LinkedList<int[]> editMappingQueryTree, Map<Integer, Node> queryTreeNewRevIDMngr, //
			List<Node> rootNodesOfInsertedNodes) {
		/*
		Node nodeQueryTreeOldRev = subTree.getQueryTreeIDMngr().get(nodeAlignment[0]);
		Node nodeQueryTreeNewRev = getNodeQueryTreeNewRev(nodeQueryTreeOldRev, editMappingQueryTree, //
				subTree.getQueryTreeIDMngr(), queryTreeNewRevIDMngr);

		if (nodeQueryTreeNewRev == null)
			return;
		for (int i = 0; i < rootNodesOfInsertedNodes.size(); i++) {
			Node insertedNode = rootNodesOfInsertedNodes.get(i);
			Node insertedParentNode = (Node) insertedNode.getParent();
			if (nodeQueryTreeNewRev.eq(insertedParentNode)) {
				int childIndex = insertedParentNode.getIndex(insertedNode);
				Node copiedInsertedNode = insertedNode.copy();
				subTree.getSubTree().insert(copiedInsertedNode, childIndex);
			}
		} */
	}

	Node getNodeQueryTreeNewRev(Node node, List<int[]> editMapping, //
			Map<Integer, Node> idMngrOldRev, Map<Integer, Node> idMngrNewRev) {
		Node result = null;
		if (node == null) {
			return result;
		}
		for (int[] nodeAlignment : editMapping) {
			int oldTreeNodeId = nodeAlignment[0];
			int newTreeNodeId = nodeAlignment[1];
			Node oldTreeNode = idMngrOldRev.get(oldTreeNodeId);
			if (oldTreeNode != null && node.equals(oldTreeNode)) {
				result = idMngrNewRev.get(newTreeNodeId);
				break;
			}
		}
		return result;
	}

	/**
	 * 
	 * @param rightNodeOfQueryTree
	 * @param nodeOfSubTree
	 * @param rootNodesOfInsertedNodes
	 * @param subTree - in first group clustered by similarity in old revision
	 * @param editLocation
	 */
	public void insertSubTreeIntoOldVerSubTree(RTEDInfoSubTree subTree, //
			int[] nodeAlignment, List<Node> rootNodesOfInsertedNodes) {
		/*
		Node nodeQueryTreeOldRev = subTree.getQueryTreeIDMngr().get(nodeAlignment[0]);
		Node nodeQueryTreeNewRev = getNodeQueryTreeNewRev(nodeQueryTreeOldRev);
		if (nodeQueryTreeNewRev == null)
			return;
		for (int i = 0; i < rootNodesOfInsertedNodes.size(); i++) {
			Node insertedNode = rootNodesOfInsertedNodes.get(i);
			Node insertedParentNode = (Node) insertedNode.getParent();
			if (nodeQueryTreeNewRev.eq(insertedParentNode)) {
				int childIndex = insertedParentNode.getIndex(insertedNode);
				Node copiedInsertedNode = insertedNode.copy();
				subTree.getSubTree().insert(copiedInsertedNode, childIndex);
			}
		} */
	}

	void transformByEditMapping(RTEDInfoSubTree subTree, boolean isPrintable) {
		/*
		RTEDProcessor queryTreeRTED = matcherQueryTree.getRTEDProc();
		Map<Integer, Node> queryTreeIDMngrNewRev = matcherQueryTree.getQueryTreeIDMngrNewRev();
		Map<Integer, Node> qreeyTreeIDMngrOldRev = matcherQueryTree.getQueryTreeIDMngrOldRev();

		List<Node> insertedNodes = getInsertedNodesFromQueryTree( //
				queryTreeRTED.getEditMapping(), queryTreeIDMngrNewRev);

		@SuppressWarnings("unused")
		List<Node> removedNodes = getRemovedNodesFromQueryTree( //
				queryTreeRTED.getEditMapping(), qreeyTreeIDMngrOldRev);

		List<Node> rootNodesInsertedNodes = new ArrayList<Node>();
		utNode.getRootNodes(insertedNodes, rootNodesInsertedNodes);
		if (isPrintable) {
			System.out.println("DBG0--BEFORE----------------------------------------");
			subTree.getSubTree().print();
		}
		for (int[] nodeAlignment : subTree.getEditMapping()) {
			if (areMapped(nodeAlignment)) {
				insertSubTreeIntoOldVerSubTree(subTree, nodeAlignment, //
						queryTreeRTED.getEditMapping(), queryTreeIDMngrNewRev, //
						rootNodesInsertedNodes);
			}
		}
		if (isPrintable) {
			System.out.println("DBG0--AFTER----------------------------------------");
			subTree.getSubTree().print();
		} */
	}

	/**
	 * 
	 * @param editMapping
	 * @param subTreeIDMngr
	 * @return
	 */
	List<Node> getInsertedNodesFromQueryTree(List<int[]> editMapping, Map<Integer, Node> subTreeIDMngr) {
		List<Node> insertedNodeList = new ArrayList<Node>();
		for (int[] nodeAlignment : editMapping) {
			if (nodeAlignment[0] == 0) {
				insertedNodeList.add(subTreeIDMngr.get(nodeAlignment[1]));
			}
		}
		return insertedNodeList;
	}

	/**
	 * 
	 * @param editMapping
	 * @param subTreeIDMngr
	 * @return
	 */
	List<Node> getRemovedNodesFromQueryTree(List<int[]> editMapping, Map<Integer, Node> subTreeIDMngr) {
		List<Node> removedNodeList = new ArrayList<Node>();
		for (int[] nodeAlignment : editMapping) {
			if (nodeAlignment[1] == 0) {
				removedNodeList.add(subTreeIDMngr.get(nodeAlignment[0]));
			}
		}
		return removedNodeList;
	}

	/**
	 * @param nodeQueryTreeOldRev
	 */
	Node getNodeQueryTreeNewRev(Node nodeQueryTreeOldRev) {
		RTEDProcessor queryTreeRTED = matcherQueryTree.getRTEDProc();
		Map<Integer, Node> idMngrOldRev = matcherQueryTree.getQueryTreeIDMngrOldRev();
		Map<Integer, Node> idMngrNewRev = matcherQueryTree.getQueryTreeIDMngrNewRev();
		Node nodeInLeftQueryTree = getNodeQueryTreeNewRev(nodeQueryTreeOldRev, //
				idMngrOldRev, idMngrNewRev, queryTreeRTED.getEditMapping());
		return nodeInLeftQueryTree;
	}

	/**
	 * 
	 * @param node
	 * @param idMngrOldRev
	 * @param idMngrNewRev
	 * @param editMapping
	 * @return
	 */
	Node getNodeQueryTreeNewRev(Node node, Map<Integer, Node> idMngrOldRev, //
			Map<Integer, Node> idMngrNewRev, List<int[]> editMapping) {
		Node result = null;
		if (node == null) {
			return result;
		}
		for (int[] nodeAlignment : editMapping) {
			int oldTreeNodeId = nodeAlignment[0];
			int newTreeNodeId = nodeAlignment[1];
			Node oldTreeNode = idMngrOldRev.get(oldTreeNodeId);
			if (oldTreeNode != null && node.equals(oldTreeNode)) {
				result = idMngrNewRev.get(newTreeNodeId);
				break;
			}
		}
		return result;
	}

	/**
	 * 
	 * @param node
	 * @param idMngrOldRev
	 * @param idMngrNewRev
	 * @param editMapping
	 * @return
	 */
	Node getNodeOldRevByEditMapping(Node node, Map<Integer, Node> idMngrOldRev, //
			Map<Integer, Node> idMngrNewRev, List<int[]> editMapping) {
		Node result = null;
		if (node == null) {
			return result;
		}
		for (int[] nodeAlignment : editMapping) {
			int oldTreeNodeId = nodeAlignment[0];
			int newTreeNodeId = nodeAlignment[1];
			Node newTreeNode = idMngrNewRev.get(newTreeNodeId);
			if (newTreeNode != null && node.equals(newTreeNode)) {
				result = idMngrOldRev.get(oldTreeNodeId);
				break;
			}
		}
		return result;
	}

	public boolean areMapped(int[] nodeAlignment) {
		return (nodeAlignment[0] != 0 && nodeAlignment[1] != 0);
	}

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
}
