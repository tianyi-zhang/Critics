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
package edu.utexas.seal.plugins.overlay.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.swt.browser.Browser;

import ut.seal.plugins.utils.UTChange;
import ut.seal.plugins.utils.UTFile;
import ut.seal.plugins.utils.ast.UTASTParser;
import ut.seal.plugins.utils.change.UTChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.EntityType;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.NodePair;
import edu.utexas.seal.plugins.overlay.LayerType;
import edu.utexas.seal.plugins.util.UTCriticsHTML.HTMLEntityType;
import edu.utexas.seal.plugins.util.UTCriticsPairFileInfo;

/**
 * @author Myoungkyu Song
 * @date Feb 27, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsOverlayNewBrowser extends CriticsOverlayBrowser {
	UTChangeDistiller	mdiffQTree;
	Node				mQTreeRightRev;
	Node				mQTreeLeftRev;

	/**
	 * Instantiates a new critics overlay new browser.
	 * 
	 * @param aBrowser the a browser
	 */
	public CriticsOverlayNewBrowser(Browser aBrowser) {
		super(aBrowser);
	}

	/**
	 * Sets the source base node.
	 * 
	 * @param aChangeDistiller the a change distiller
	 * @param aQTreeRightRev the a q tree right rev
	 * @param aQTreeLeftRev the a q tree left rev
	 */
	public void setSourceBaseNode(UTChangeDistiller aChangeDistiller, Node aQTreeRightRev, Node aQTreeLeftRev) {
		setupSourceBaseNode(aChangeDistiller, aQTreeLeftRev, aQTreeLeftRev);
		putLeftQTree(aChangeDistiller); // right (old) -> left (new), new is base.
		putEditQTree();
		for (int i = 0; i < mBaseCodeNodeList.size(); i++) {
			Node iNode = mBaseCodeNodeList.get(i);
			iNode.setQTree(true);
			iNode.setHtmlStyle(HTML_BGN_SPAN_LBASE);
		}
		setSourceNode(mBaseCodeNodeList);
	}

	/**
	 * Setup source base node.
	 * 
	 * @param aChangeDistiller the a change distiller
	 * @param aQTreeLeftRev the a q tree left rev
	 * @param aQTreeRightRev the a q tree right rev
	 */
	public void setupSourceBaseNode(UTChangeDistiller aChangeDistiller, Node aQTreeLeftRev, Node aQTreeRightRev) {
		this.mdiffQTree = aChangeDistiller;
		this.mQTreeLeftRev = aQTreeLeftRev;
		this.mQTreeRightRev = aQTreeRightRev;
		mHelper.setAllDisableShow(mQTreeLeftRev);
		mHelper.setAllDisableShow(mQTreeRightRev);
		mBaseCodeNodeList.clear();
		mBaseCodeNodeList.add(new Node(HTMLEntityType.HTML, HTML_BGN_SPAN_LBASE));
	}

	/**
	 * Put base code node.
	 * 
	 * @param aNode the a node
	 */
	public void putLeftQTree(UTChangeDistiller aChangeDistiller) {
		List<Node> insertNodeList_Q = aChangeDistiller.getInsertNodeList();
		Enumeration<?> e = mQTreeLeftRev.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			iNode.setMatchedNode(null);
			for (Node jNode : insertNodeList_Q) {
				if (iNode.eq(jNode)) {
					iNode.setInsertLikeQTree(true);
				}
			}
			mBaseCodeNodeList.add(iNode);
		}
	}

	public void putLeftQTree(UTChangeDistiller diffQTree, Node ndSelectedLeftRev) {
		List<Node> insertNodeList_Q = diffQTree.getInsertNodeList();
		Enumeration<?> e = ndSelectedLeftRev.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			iNode.setMatchedNode(null);
			for (Node jNode : insertNodeList_Q) {
				if (checkEQ(jNode, iNode)) {// iNode.getEntity().getType() == jNode.getEntity().getType() && iNode.getValue().equals(jNode.getValue())) {
					iNode.setInsertLikeQTree(true);
					String valueParm = jNode.getValueParm();
					if (valueParm != null) {
						iNode.setValueParm(valueParm);
					}
					break;
				}
			}
			mBaseCodeNodeList.add(iNode);
		}
	}

	public void putEditQTree() {
		List<NodePair> lstMatches = mdiffQTree.getMatches();
		for (NodePair iNodePair : lstMatches) {
			Node iBaseNode = iNodePair.getRight();
			Node iMatchedNode = iNodePair.getLeft();
			System.out.println("[DBG0] " + iMatchedNode + " <=> " + iBaseNode + " (" + iBaseNode.getEntity().getStartPosition());
			for (Node iNode : mBaseCodeNodeList) {
				if (iNode.eq(iBaseNode)) {
					iNode.setMatchedNode(iMatchedNode);
					break;
				}
			}
		}
		// List<SourceCodeChange> lstDelete = mdiffQTree.getDeleteList();
		List<Node> deleteNodeList = mdiffQTree.getDeleteNodeList(); // mHelper.extractDeleteNodeList(lstDelete);
		for (Node iNode : deleteNodeList) {
			iNode.setDeleteLikeQTree(true);
		}
		mHelper.setAllDisableShow(deleteNodeList);
		overlayDeleteEdit(deleteNodeList); // base - new , delete - old - matched
		// List<Node> insertNodeList = extractInsertNodeList();
		// mHelper.setAllDisableShow(insertNodeList);
		// overlayTwoListForInsert(mBaseCodeNodeList, insertNodeList);
		mBaseCodeNodeList.add(new Node(HTMLEntityType.HTML, HTML_SPAN_END));
	}

	public void putEditQTree(UTChangeDistiller diffQTree, UTChangeDistiller diffTTree) {
		List<NodePair> lstMatches = diffTTree.getMatches();
		for (NodePair iNodePair : lstMatches) {
			Node iBaseNode = iNodePair.getRight();
			Node iMatchedNode = iNodePair.getLeft();
			for (Node iNode : mBaseCodeNodeList) {
				if (iNode.eq(iBaseNode)) {
					iNode.setMatchedNode(iMatchedNode);
					break;
				}
			}
		}
		List<Node> deleteNodeList_Q = diffQTree.getDeleteNodeList();
		List<Node> deleteNodeList_T = diffTTree.getDeleteNodeList();
		for (Node iNode : deleteNodeList_T) {
			for (Node jNode : deleteNodeList_Q) {
				if (checkEQ(jNode, iNode)) {
					iNode.setDeleteLikeQTree(true);
					String valueParm = jNode.getValueParm();
					if (valueParm != null) {
						iNode.setValueParm(valueParm);
					}
					break;
				}
			}
		}
		mHelper.setAllDisableShow(deleteNodeList_T);
		overlayDeleteEdit(deleteNodeList_T); // base - new , delete - old - matched
		mBaseCodeNodeList.add(new Node(HTMLEntityType.HTML, HTML_SPAN_END));
	}

	boolean checkEQ(Node aQNode, Node aTrgNode) {
		if (aQNode.getLabel() == JavaEntityType.EXCLUDED) {
			return true;
		}
		EntityType typeQ = aQNode.getEntity().getType();
		EntityType typeT = aTrgNode.getEntity().getType();
		String valueQ = aQNode.getValue();
		String valueT = aTrgNode.getValue();
		if (typeQ == typeT && valueQ.equals(valueT)) {
			return true;
		}
		if (typeQ == typeT && aQNode.isAnyValueArgParameterizedInQeury()) {
			if (UTChange.checkEQByDiff(aQNode, aTrgNode)) {
				return true;
			}
		}
		return false;
	}

	void overlayDeleteEdit(List<Node> aList) {
		int startIndex = 0;
		int curPos = -1;
		for (int i = 0; i < mBaseCodeNodeList.size(); i++) {
			Node iNode = mBaseCodeNodeList.get(i);
			Node iMatchedNode = iNode.getMatchedNode();
			if (iMatchedNode == null) {
				continue;
			}
			curPos = iMatchedNode.getEntity().getStartPosition();
			for (int j = startIndex; j < aList.size(); j++) {
				Node jNode = aList.get(j);
				int jPos = jNode.getEntity().getStartPosition();
				if (curPos > jPos && jNode.isSkip() == false) {
					jNode.setHtmlStyle(mLayers.get(LayerType.DEL).getStyle()); // HTML_BGN_SPAN_L2);
					mBaseCodeNodeList.add(i, jNode);
					startIndex++;
					break;
				}
			}
		}
		for (int i = startIndex; i < aList.size(); i++) {
			Node iNode = aList.get(i);
			int iPos = iNode.getEntity().getStartPosition();
			if (iPos > curPos) {
				iNode.setHtmlStyle(mLayers.get(LayerType.DEL).getStyle()); // HTML_BGN_SPAN_L2);
				mBaseCodeNodeList.add(iNode);
			}
		}
	}

	/**
	 * Extract insert node list.
	 * 
	 * @return the list
	 */
	List<Node> extractInsertNodeList() {
		List<Node> lstInsertNode = new ArrayList<Node>();
		File fileBaseNode = UTCriticsPairFileInfo.getLeftFile();
		String srcBaseNode = UTFile.getContents(fileBaseNode.getAbsolutePath());
		CompilationUnit unitBaseNode = new UTASTParser().parse(srcBaseNode);
		List<SourceCodeChange> lstInsert = mdiffQTree.getInsertList();
		List<Node> lstInsertNodeCopy = UTChange.getNodeListMethodLevel(lstInsert, unitBaseNode, srcBaseNode, fileBaseNode);
		for (int i = 0; i < lstInsert.size(); i++) {
			SourceCodeChange iInsert = lstInsert.get(i);
			SourceCodeEntity entity = iInsert.getChangedEntity();
			EntityType entityType = entity.getType();
			String value = entity.getUniqueName();
			Node iNewNode = new Node(entityType, value);
			iNewNode.setEntity(entity);
			iNewNode.setInsert(true);
			for (int j = 0; j < lstInsertNodeCopy.size(); j++) {
				Node jNode = lstInsertNodeCopy.get(j);
				if (jNode.getEntity().getStartPosition() == iInsert.getChangedEntity().getStartPosition()) {
					iNewNode.setLevel(jNode.getLevel());
				}
			}
			lstInsertNode.add(iNewNode);
		}
		return lstInsertNodeCopy;
	}

	public List<Node> getBaseCodeNodeList() {
		return mBaseCodeNodeList;
	}

	public UTChangeDistiller getMdiffQTree() {
		return mdiffQTree;
	}
}
