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
package edu.utexas.seal.plugins.overlay.event;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;

import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.NodePair;
import rted.distance.RTEDInfoSubTree;
import ut.seal.plugins.utils.UTFile;
import ut.seal.plugins.utils.change.UTChangeDistiller;
import edu.utexas.seal.plugins.overlay.model.CriticsCBTreeElemType;
import edu.utexas.seal.plugins.overlay.model.CriticsCBTreeNode;
import edu.utexas.seal.plugins.overlay.view.CriticsOverlayNewView;
import edu.utexas.seal.plugins.overlay.view.CriticsOverlayView;
import edu.utexas.seal.plugins.util.UTCriticsEditor;
import edu.utexas.seal.plugins.util.UTCriticsPairFileInfo;

/**
 * @author Myoungkyu Song
 * @date Feb 7, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsOverlayViewEvent extends CriticsOverlayViewCommonEvent {

	/**
	 * Instantiates a new critics overlay view event.
	 * 
	 * @param overlayView the critics overlay view
	 */
	public CriticsOverlayViewEvent(CriticsOverlayView overlayView) {
		mCriticsOverlayView = overlayView;
		mSummaryTableViewer = overlayView.getSummaryTableViewer();
		mAnomalyTableViewer = overlayView.getAnomalyTableViewer();
		mHTMLLeftBrowser = overlayView.getHTMLLeftBrowser();
		mHTMLRightBrowser = overlayView.getHTMLRightBrowser();

		mTVSimilarContext = overlayView.getTVSimilarContext();
		mBtnRadioButton = overlayView.getRadioButton();
		mTxtSearch = overlayView.getTxtSearch();
		mCBTreeHelper = overlayView.getCBTreeHelper();
	}

	public CriticsOverlayViewEvent(CriticsOverlayNewView overlayView) {
		mCriticsOverlayView = overlayView;
		mSummaryTableViewer = overlayView.getSummaryTableViewer();
		mAnomalyTableViewer = overlayView.getAnomalyTableViewer();
		mNewDiffBrowser = overlayView.getNewDiffBrowser();
	}

	/**
	 * Adds the event to radio button.
	 */
	public void addEventToRadioButton() {
		mBtnRadioButton.addMouseListener(new CriticsOverlayRadioButtonMouseAdapter(this));
	}

	/**
	 * Adds the event to text field.
	 */
	public void addEventToTextField() {
		CriticsOverlayTextFieldAdapter tfAdapter = new CriticsOverlayTextFieldAdapter(this);
		mTxtSearch.addFocusListener(tfAdapter.new TextFieldFocusAdapter());
		mTxtSearch.addMouseListener(tfAdapter.new TextFieldMouseListener());
		mTxtSearch.addMouseMoveListener(tfAdapter.new TextFieldMouseMoveListener());
		mTxtSearch.addModifyListener(tfAdapter.new TextFieldModifyListener());
	}

	/**
	 * Adds the event to summary table viewer.
	 */
	public void addEventToSummaryTableViewer() {
		mSummaryTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				actionOverlaySourceSecondNode(event);
			}
		});
		final Table table = mSummaryTableViewer.getTable();
		table.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseHover(MouseEvent e) {
				// actionOverlaySourceSecondNode(table, e);
			}
		});
		mSummaryTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				ISelection selElem = event.getSelection();
				if (selElem instanceof StructuredSelection) {
					StructuredSelection selTreeNode = (StructuredSelection) selElem;
					Object selObj = selTreeNode.getFirstElement();
					if (selObj instanceof CriticsCBTreeNode) {
						CriticsCBTreeNode cbtNode = (CriticsCBTreeNode) selObj;
						actionOpenCompareView(cbtNode);
					}
				}
			}
		});
	}
	
	/**
	 * Adds the event to summary table viewer.
	 */
	public void addEventToAnomalyTableViewer() {
		mAnomalyTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				actionOverlaySourceSecondNode(event);
			}
		});
		final Table table = mAnomalyTableViewer.getTable();
		table.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseHover(MouseEvent e) {
				// actionOverlaySourceSecondNode(table, e);
			}
		});
		mAnomalyTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				ISelection selElem = event.getSelection();
				if (selElem instanceof StructuredSelection) {
					StructuredSelection selTreeNode = (StructuredSelection) selElem;
					Object selObj = selTreeNode.getFirstElement();
					if (selObj instanceof CriticsCBTreeNode) {
						CriticsCBTreeNode cbtNode = (CriticsCBTreeNode) selObj;
						actionOpenCompareView(cbtNode);
					}
				}
			}
		});
	}

	/**
	 * Action overlay source second node.
	 * 
	 * @param event the event
	 */
	void actionOverlaySourceSecondNode(SelectionChangedEvent event) {
		Object selObj = null;
		ISelection selElem = event.getSelection();
		if (selElem instanceof StructuredSelection) {
			StructuredSelection selItem = (StructuredSelection) selElem;
			selObj = selItem.getFirstElement();
		} else if (selElem instanceof TreeSelection) {
			TreeSelection selTreeNode = (TreeSelection) selElem;
			selObj = selTreeNode.getFirstElement();
		}
		if (selObj instanceof CriticsCBTreeNode) {
			CriticsCBTreeNode tSelected = (CriticsCBTreeNode) selObj;
			if (tSelected.type() != CriticsCBTreeElemType.BLOCK) {
				return;
			}
			setSourceSecondNode(tSelected);
		}
	}

	/**
	 * Action overlay source second node.
	 * 
	 * @param table the table
	 * @param e the e
	 */
	void actionOverlaySourceSecondNode(Table table, MouseEvent e) {
		TableItem item = table.getItem(new Point(e.x, e.y));
		if (item == null) {
			return;
		}
		Object data = item.getData();
		if (data instanceof CriticsCBTreeNode) {
			CriticsCBTreeNode tSelected = (CriticsCBTreeNode) data;
			setSourceSecondNode(tSelected);
		}
	}

	/**
	 * Action open compare view.
	 * 
	 * @param cbtNode the cbt node
	 */
	private void actionOpenCompareView(CriticsCBTreeNode cbtNode) {
		File f1117 = cbtNode.getFile();
		String f1117Name = f1117.getAbsolutePath();
		String prjNameRight = UTCriticsPairFileInfo.getRightProjectName();
		String prjNameLeft = UTCriticsPairFileInfo.getLeftProjectName();
		String f1114Name = f1117Name.replace(prjNameRight, prjNameLeft);
		File f1114 = new File(f1114Name);
		/* UTCriticsEditor.openEditor(f1117); */
		UTCriticsEditor.openComparisonEditor(f1114, f1117);
	}

	protected void setSourceSecondNode(CriticsCBTreeNode aTSelected) {
		mQTreeLeftRev = mCriticsOverlayView.getQTreeNewRev();
		mQTreeRightRev = mCriticsOverlayView.getQTreeOldRev();
		Node ndSelectedRightRev = aTSelected.getNode();
		Node ndSelectedLeftRev = getOppositeNode(aTSelected);
		ndSelectedLeftRev.cleanEditOperationAll();
		ndSelectedRightRev.cleanEditOperationAll();
		if (ndSelectedRightRev.isAnomaly()) {
			findNodeOfAnomaly(ndSelectedLeftRev);
		}
		// ////////////////////////////////////////////////////////
		mBaseCodeNodeList = mNewDiffBrowser.getBaseCodeNodeList();
		UTChangeDistiller diffQTree = getDiffQTree();
		UTChangeDistiller diffTTree = new UTChangeDistiller();
		diffTTree.diffBlock(ndSelectedRightRev.copy(), ndSelectedLeftRev.copy()); // right -> left
		mNewDiffBrowser.setupSourceBaseNode(diffTTree, ndSelectedLeftRev, ndSelectedRightRev);
		markInsertNodeList(diffTTree, ndSelectedLeftRev);
		mNewDiffBrowser.putLeftQTree(diffQTree, ndSelectedLeftRev); // right (old) -> left (new), new is base.
		mNewDiffBrowser.putEditQTree(diffQTree, diffTTree);
		for (int i = 0; i < mBaseCodeNodeList.size(); i++) {
			Node iNode = mBaseCodeNodeList.get(i);
			iNode.setQTree(true);
			iNode.setHtmlStyle(HTML_BGN_SPAN_LBASE);
		}
		mNewDiffBrowser.setSourceNode(mBaseCodeNodeList);
		print(ndSelectedRightRev, ndSelectedLeftRev, diffTTree, true);
	}

	void findNodeOfAnomaly(Node ndSelectedLeftRev) {
		mQTreeLeftRev = mCriticsOverlayView.getQTreeNewRev();
		UTChangeDistiller diff_Qt_Tt = new UTChangeDistiller();
		diff_Qt_Tt.diffBlock(ndSelectedLeftRev.copy(), mQTreeLeftRev.copy());
		List<Node> insertNodeList_T_Q = diff_Qt_Tt.getInsertNodeList(); // tt-new <=> qt-new, exist in qtree.
		List<Node> deleteNodeList_T_Q = diff_Qt_Tt.getDeleteNodeList();
		// ///////////////////////////////////////////
		UTChangeDistiller diffQTree = getDiffQTree();
		List<Node> insertNodeList_Q_Q = diffQTree.getInsertNodeList(); // qt-old <=> qt-new, exist in qtree.
		List<Node> deleteNodeList_Q_Q = diffQTree.getDeleteNodeList();
		// ///////////////////////////////////////////
		List<Node> missingNodes = new ArrayList<Node>();
		for (Node iNode : insertNodeList_Q_Q) {
			for (Node jNode : insertNodeList_T_Q) {
				if (iNode.eq(jNode)) {
					missingNodes.add(iNode);
					break;
				}
			}
		}
		// ///////////////////////////////////////////
		List<Node> remainingNodes = new ArrayList<Node>();
		for (Node iNode : deleteNodeList_Q_Q) {
			for (Node jNode : deleteNodeList_T_Q) {
				if (checkEQ(iNode, jNode)) {
					remainingNodes.add(jNode);
					break;
				}
			}
		}
		System.out.println("------------------------------------------");
		for (Node node : missingNodes) {
			System.out.println("[DBG5] " + node);
			node.setAnomaly(true);
		}
		System.out.println("------------------------------------------");
		for (Node iNode : remainingNodes) {
			System.out.println("[DBG6] " + iNode);
			Enumeration<?> e = ndSelectedLeftRev.preorderEnumeration();
			while (e.hasMoreElements()) {
				Node jNode = (Node) e.nextElement();
				if (iNode.eq(jNode)) {
					jNode.setAnomaly(true);
					break;
				}
			}
		}
		System.out.println("------------------------------------------");
	}

	boolean checkEQ(Node aQNode, Node aTNode) {
		if (aQNode.getEntity().getType() != aTNode.getEntity().getType()) {
			return false;
		}
		if (aQNode.getValue().equals(aTNode.getValue())) {
			return true;
		}
		return false;
	}

	UTChangeDistiller getDiffQTree() {
		mQTreeLeftRev = mCriticsOverlayView.getQTreeNewRev();
		mQTreeRightRev = mCriticsOverlayView.getQTreeOldRev();
		UTChangeDistiller diffQTree = new UTChangeDistiller();
		diffQTree.diffBlock(mQTreeRightRev.copy(), mQTreeLeftRev.copy()); // right -> left
		return diffQTree;
	}

	private void markInsertNodeList(UTChangeDistiller diffTTree, Node ndSelectedLeftRev) {
		UTChangeDistiller diffQTree = getDiffQTree();
		List<Node> insertNodeList_Q = diffQTree.getInsertNodeList();
		// /////////////////////////////////////////////////////////
		List<Node> insertNodeList_T = diffTTree.getInsertNodeList();
		Enumeration<?> e = ndSelectedLeftRev.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			for (Node jNode : insertNodeList_T) {
				if (iNode.eq(jNode)) { // T <=> T
					iNode.setInsert(true);
					for (Node kNode : insertNodeList_Q) {
						if (jNode.eq(kNode)) { // T <=> Q
							iNode.setInsertLikeQTree(true);
						}
					}
				}
			}
		}
	}

	Node getOppositeNode(CriticsCBTreeNode aTSelected) {
		File fSelectedRight = aTSelected.getFile();
		String fnSelectedRight = fSelectedRight.getAbsolutePath();
		Node ndSelectedRightRev = aTSelected.getNode();
		String prjNameRight = UTCriticsPairFileInfo.getRightProjectName();
		String prjNameLeft = UTCriticsPairFileInfo.getLeftProjectName();
		String fnSelectedLeft = fnSelectedRight.replace(prjNameRight, prjNameLeft);
		MethodDeclaration methodSelectedRight = findMethod(fnSelectedRight, ndSelectedRightRev);
		Node ndSelectedLeftRev = getOppositeNode(fnSelectedLeft, methodSelectedRight);
		return ndSelectedLeftRev;
	}

	private void print(Node ndSelectedRightRev, Node ndSelectedLeftRev, UTChangeDistiller diffTTree, boolean t) {
		if (!t) {
			return;
		}
		System.out.println("------------------------------------------");
		System.out.println("[DBG] * LEFT SELECTED NODE");
		System.out.println("------------------------------------------");
		ndSelectedLeftRev.print();
		System.out.println("------------------------------------------");
		System.out.println("[DBG] * RIGHT SELECTED NODE");
		System.out.println("------------------------------------------");
		ndSelectedRightRev.print();
		System.out.println("------------------------------------------");
		System.out.println("DEL");
		System.out.println("------------------------------------------");
		List<Node> deleteNodeList = diffTTree.getDeleteNodeList();
		for (Node iNode : deleteNodeList) {
			System.out.println("[DBG1] " + iNode);
		}
		System.out.println("------------------------------------------");
		System.out.println("INS");
		System.out.println("------------------------------------------");
		List<Node> insertNodeList = diffTTree.getInsertNodeList();
		for (Node iNode : insertNodeList) {
			System.out.println("[DBG2] " + iNode);
		}
		System.out.println("------------------------------------------");
		System.out.println("MATCH");
		System.out.println("------------------------------------------");
		List<NodePair> matches = diffTTree.getMatches();
		for (NodePair nodePair : matches) {
			System.out.println("[DBG3] " + nodePair);
		}
		System.out.println("------------------------------------------");
	}

	/**
	 * Show.
	 */
	public void show() {
		mCriticsOverlayView.makeOriginalCBTree();
		updateTreeView();
	}

	/**
	 * Update tree view.
	 */
	public void updateTreeView() {
		if (!mBfExpand) {
			mTVSimilarContext.expandAll();
		} else {
			mTVSimilarContext.collapseAll();
		}
	}

	/**
	 * Gets the all items.
	 * 
	 * @param aItem the a item
	 * @param aLstItems the a lst items
	 * @return the all items
	 */
	void getAllItems(TreeItem aItem, List<TreeItem> aLstItems) {
		if (aItem == null)
			return;
		aLstItems.add(aItem);
		TreeItem[] items = aItem.getItems();
		for (int i = 0; i < items.length; i++) {
			getAllItems(items[i], aLstItems);
		}
	}

	/**
	 * Make new cb tree.
	 * 
	 * @param lstNodes the lst nodes
	 */
	void makeNewCBTree(List<CriticsCBTreeNode> lstNodes) {
		CriticsCBTreeNode root = new CriticsCBTreeNode("root", CriticsCBTreeElemType.NONE);
		Map<File, CriticsCBTreeNode> mapFileNode = new TreeMap<File, CriticsCBTreeNode>();
		Map<String, CriticsCBTreeNode> mapMethodNode = new TreeMap<String, CriticsCBTreeNode>();
		CriticsCBTreeNode iFileCBTNode = null, iMethodCBTNode = null;
		for (int i = 0; i < lstNodes.size(); i++) {
			CriticsCBTreeNode iBlockCBTNode = lstNodes.get(i);
			RTEDInfoSubTree iSubtreeInfo = iBlockCBTNode.getSubtreeInfo();
			File iFile = iSubtreeInfo.getFullFilePath();
			String iSource = UTFile.getContents(iFile.getAbsolutePath());
			String iMethodName = mCriticsOverlayView.getMethodName(iSource, iSubtreeInfo.getSubTree());
			if (mapFileNode.containsKey(iFile)) {
				iFileCBTNode = mapFileNode.get(iFile);
			} else {
				iFileCBTNode = new CriticsCBTreeNode(iFile.getName(), CriticsCBTreeElemType.FILE);
				mapFileNode.put(iFile, iFileCBTNode);
			}
			if (mapMethodNode.containsKey(iMethodName)) {
				iMethodCBTNode = mapMethodNode.get(iMethodName);
			} else {
				iMethodCBTNode = new CriticsCBTreeNode(iMethodName, CriticsCBTreeElemType.METHOD);
				mapMethodNode.put(iMethodName, iMethodCBTNode);
				iFileCBTNode.addChild(iMethodCBTNode);
			}
			iMethodCBTNode.addChild(iBlockCBTNode);
		}
		for (Map.Entry<File, CriticsCBTreeNode> e : mapFileNode.entrySet()) {
			root.addChild(e.getValue());
		}
		mTVSimilarContext.setInput(root);
		if (!mBfExpand) {
			mTVSimilarContext.expandAll();
		} else {
			mTVSimilarContext.collapseAll();
		}
	}

	/**
	 * Refresh cb tree by group size.
	 * 
	 * @param szGroup the sz group
	 */
	void refreshCBTreeByGroupSize(int szGroup) {
		/*
		mCriticsOverlayView.makeOriginalCBTree();
		mTVSimilarContext.expandAll();
		TreeItem[] arItems = mTVSimilarContext.getTree().getItems();
		List<TreeItem> lstAllItems = new ArrayList<TreeItem>();
		for (TreeItem iItem : arItems) {
			List<TreeItem> lstItems = new ArrayList<TreeItem>();
			getAllItems(iItem, lstItems);
			lstAllItems.addAll(lstItems);
		}
		List<CriticsCBTreeNode> lstNodes = new ArrayList<CriticsCBTreeNode>();
		for (int i = 0; i < lstAllItems.size(); i++) {
			TreeItem iItem = lstAllItems.get(i);
			CriticsCBTreeNode iCBTNode = (CriticsCBTreeNode) iItem.getData();
			if (iCBTNode.getSubtreeInfo() != null) {
				if (iCBTNode.getSubtreeInfo().getGroupIDBySimilarity() <= szGroup) {
					lstNodes.add(iCBTNode);
				}
			}
		}
		makeNewCBTree(lstNodes);
		*/
	}

	/**
	 * Adds the event to tree.
	 */
	public void addEventToTree() {
		mTVSimilarContext.getTree().addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseHover(MouseEvent e) {
				/*
				Object source = e.getSource();
				System.out.println("[DBG2]  " + source);
				if (source instanceof Tree) {
					Tree aTree = (Tree) source;
					Cursor cursor = aTree.getCursor();
					Object data = aTree.getData();
					Object orientation = aTree.getLocation();
					Point point = aTree.getLocation();
					Object obj = aTree.getItem(point);
					System.out.println("[DBG5]" + cursor + ", " + data + ", " + orientation + ", " + obj);

				}
				TreeItem item = mTVSimilarContext.getTree().getItem(new Point(e.x, e.y));
				if (item != null) {
					System.out.println("[DBG] " + item);
				}
				*/
			}
		});

		mTVSimilarContext.getTree().addMouseMoveListener(new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent e) {
			}
		});

		mTVSimilarContext.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getChecked()) {
					mTVSimilarContext.setSubtreeChecked(event.getElement(), true);
				}
			}
		});

		mTVSimilarContext.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				actionOverlaySourceSecondNode(event);
			}
		});
		/* mTVSimilarContext.getTree().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				System.out.println("[DBG] widgetSelected: " + event);
				// ///////////////////////////////////////////////////////////////
				List<Object> arSelected = mCBTreeHelper.getCheckedElements();
				mHTMLLeftBrowser.setCheckedElements(arSelected);
				mHTMLRightBrowser.setCheckedElements(arSelected);
			}
		}); */
	}

}
