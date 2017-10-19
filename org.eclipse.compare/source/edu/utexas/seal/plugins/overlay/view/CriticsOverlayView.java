/*
 * @(#) CriticsOverlayView.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.overlay.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.SWTResourceManager;

import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import rted.distance.RTEDInfoSubTree;
import ut.seal.plugins.utils.UTFile;
import ut.seal.plugins.utils.ast.UTASTNodeFinder;
import ut.seal.plugins.utils.change.UTChangeDistiller;
import edu.utexas.seal.plugins.overlay.ICriticsHTMLKeyword;
import edu.utexas.seal.plugins.overlay.event.CriticsOverlayViewEvent;
import edu.utexas.seal.plugins.overlay.model.CriticsCBTreeContentProvider;
import edu.utexas.seal.plugins.overlay.model.CriticsCBTreeElemType;
import edu.utexas.seal.plugins.overlay.model.CriticsCBTreeHelper;
import edu.utexas.seal.plugins.overlay.model.CriticsCBTreeLabelProvider;
import edu.utexas.seal.plugins.overlay.model.CriticsCBTreeNode;
import edu.utexas.seal.plugins.overlay.model.CriticsTableViewerContentProvider;
import edu.utexas.seal.plugins.overlay.model.CriticsTableViewerDataList;
import edu.utexas.seal.plugins.overlay.model.CriticsTableViewerLabelProvider;
import edu.utexas.seal.plugins.util.UTCriticsPairFileInfo;

/**
 * @author Myoungkyu Song
 * @date Feb 10, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsOverlayView extends ViewPart implements ICriticsHTMLKeyword {
	public static final String					ID					= "edu.utexas.seal.plugins.overlay.view.CriticsOverlayView";	//$NON-NLS-1$
	protected TableViewer						mSummaryTableViewer	= null;
	protected TableViewer 						mAnomalyTableViewer = null;
	protected CriticsTableViewerContentProvider	mTableViewerCP		= null;
	protected CriticsTableViewerLabelProvider	mTableViewerLP		= null;
	protected Browser							mBrowserLeft		= null;
	protected Browser							mMatchingBrowser	= null;
	protected Browser 							mTemplateBrowser    = null;
	protected List<RTEDInfoSubTree>				mGroup				= null;
	protected CriticsOverlayBrowser				mHTMLLeftBrowser	= null;
	protected CriticsOverlayBrowser				mHTMLRightBrowser	= null;
	protected CriticsOverlayViewMenu			mMenuHandler		= null;
	protected CriticsOverlayViewEvent			mEventHandler		= null;
	protected Node								mQTreeLeftRev		= null;
	protected Node								mQTreeRightRev		= null;

	/**
	 * Instantiates a new critics overlay view.
	 */
	public CriticsOverlayView() {
		mContentProvider = new CriticsCBTreeContentProvider();
		mTableViewerCP = new CriticsTableViewerContentProvider();
		ILabelDecorator decorator = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
		mTableViewerLP = new CriticsTableViewerLabelProvider(new LabelProvider(), decorator);
	}

	/**
	 * Creates the part control.
	 * 
	 * @param parent the parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		SashForm mSashFormMain = new SashForm(container, SWT.NONE);
		GridData gd_mSashFormMain = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_mSashFormMain.widthHint = 680;
		gd_mSashFormMain.heightHint = 498;
		mSashFormMain.setLayoutData(gd_mSashFormMain);
		Group mGrpSummary = new Group(mSashFormMain, SWT.NONE);
		mGrpSummary.setText("Summarization/Anomalies: ");
		mGrpSummary.setLayout(new GridLayout(1, false));
		// /////////////////////////////////////////////////////////////////////////////////
		// Table viewer
		// /////////////////////////////////////////////////////////////////////////////////
		mSummaryTableViewer = new TableViewer(mGrpSummary, SWT.BORDER | SWT.FULL_SELECTION);
		Table mContextList = mSummaryTableViewer.getTable();
		mContextList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		mSummaryTableViewer.setContentProvider(mTableViewerCP);
		mSummaryTableViewer.setLabelProvider(mTableViewerLP);
		// /////////////////////////////////////////////////////////////////////////////////
		// Browser in left
		// /////////////////////////////////////////////////////////////////////////////////
		SashForm mSashFormSub = new SashForm(mSashFormMain, SWT.NONE);
		Group mGrpLeftOverlay = new Group(mSashFormSub, SWT.NONE);
		mGrpLeftOverlay.setText("Left Overlay:");
		mGrpLeftOverlay.setLayout(new GridLayout(1, false));
		mBrowserLeft = new Browser(mGrpLeftOverlay, SWT.BORDER);
		mBrowserLeft.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		mBrowserLeft.setBackground(SWTResourceManager.getColor(204, 204, 204));
		mHTMLLeftBrowser = new CriticsOverlayBrowser(mBrowserLeft);
		// /////////////////////////////////////////////////////////////////////////////////
		// Browser in right
		// /////////////////////////////////////////////////////////////////////////////////
		Group mGrpRightOverlay = new Group(mSashFormSub, SWT.NONE);
		mGrpRightOverlay.setText("Right Overlay:");
		mGrpRightOverlay.setLayout(new GridLayout(1, false));
		mMatchingBrowser = new Browser(mGrpRightOverlay, SWT.BORDER);
		mMatchingBrowser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		mMatchingBrowser.setBackground(SWTResourceManager.getColor(204, 204, 204));
		mHTMLRightBrowser = new CriticsOverlayBrowser(mMatchingBrowser);
		// /////////////////////////////////////////////////////////////////////////////////
		// Event & Menu
		// /////////////////////////////////////////////////////////////////////////////////
		mEventHandler = new CriticsOverlayViewEvent(this);
		mEventHandler.addEventToSummaryTableViewer();
		mMenuHandler = new CriticsOverlayViewMenu(this);
		mMenuHandler.addMenuToSummaryTableViewer();
		mSashFormSub.setWeights(new int[] { 1, 1 });
		mSashFormMain.setWeights(new int[] { 177, 556 });
	}

	/**
	 * Make original list view.
	 */
	public void makeOriginalListView() {
		// set input for summary view
		List<RTEDInfoSubTree> summaryGroup = new ArrayList<RTEDInfoSubTree>();
		List<RTEDInfoSubTree> anomalyGroup = new ArrayList<RTEDInfoSubTree>();
		for( RTEDInfoSubTree elem : mGroup){
			if(elem.isAnomaly()){
				anomalyGroup.add(elem);
			}else{
				summaryGroup.add(elem);
			}
		}
		mSummaryTableViewer.setInput(new CriticsTableViewerDataList(summaryGroup));
		mAnomalyTableViewer.setInput(new CriticsTableViewerDataList(anomalyGroup));
	}

	/**
	 * Show.
	 */
	public void show() {
		if (mSummaryTableViewer != null) {
			makeOriginalListView();
		} else {
			makeOriginalCBTree();
			mEventHandler.updateTreeView();
		}
		diffQTree();
		mHTMLLeftBrowser.setSourceBaseNode(mQTreeLeftRev, UTCriticsPairFileInfo.getLeftFile());
		mHTMLRightBrowser.setSourceBaseNode(mQTreeRightRev, UTCriticsPairFileInfo.getLeftFile());
	}

	public UTChangeDistiller diffQTree() {
		initDiff();
		UTChangeDistiller changeDistiller = new UTChangeDistiller();
		changeDistiller.diffBlock(mQTreeRightRev.copy(), mQTreeLeftRev.copy()); // right -> left
		List<SourceCodeChange> insertList = changeDistiller.getInsertList(); // left
		List<SourceCodeChange> deleteList = changeDistiller.getDeleteList(); // right

		for (int i = 0; i < deleteList.size(); i++) {
			SourceCodeChange iDelete = deleteList.get(i);
			Enumeration<?> e = mQTreeRightRev.preorderEnumeration();
			while (e.hasMoreElements()) {
				Node iNode = (Node) e.nextElement();
				if (iNode.getEntity().getStartPosition() == iDelete.getChangedEntity().getStartPosition()) {
					iNode.setDelete(true);
				}
			}
		}
		for (int i = 0; i < insertList.size(); i++) {
			SourceCodeChange iInsert = insertList.get(i);
			Enumeration<?> e = mQTreeLeftRev.preorderEnumeration();
			while (e.hasMoreElements()) {
				Node iNode = (Node) e.nextElement();
				if (iNode.getEntity().getStartPosition() == iInsert.getChangedEntity().getStartPosition()) {
					iNode.setInsert(true);
				}
			}
		}
		testDiffQTree(true);
		return changeDistiller;
	}

	void initDiff() {
		Enumeration<?> e = mQTreeRightRev.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			iNode.setDelete(false);
			iNode.setInsert(false);
		}
		e = mQTreeLeftRev.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			iNode.setDelete(false);
			iNode.setInsert(false);
		}
	}

	void testDiffQTree(boolean b) {
		if (!b) {
			return;
		}
		System.out.println("------------------------------------------");
		System.out.println("[DBG] * RIGHT OLD REV");
		Enumeration<?> e = mQTreeRightRev.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			if (iNode.isDelete()) {
				System.out.println("[DBG] - " + iNode);
			}
			if (iNode.isInsert()) {
				System.out.println("[DBG] + " + iNode);
			}
		}
		System.out.println("------------------------------------------");
		System.out.println("[DBG] $ LEFT NEW REV");
		e = mQTreeLeftRev.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			if (iNode.isDelete()) {
				System.out.println("[DBG] - " + iNode);
			}
			if (iNode.isInsert()) {
				System.out.println("[DBG] + " + iNode);
			}
		}
	}

	/**
	 * Sets the focus.
	 */
	@Override
	public void setFocus() {
	}

	/**
	 * Sets the data.
	 * 
	 * @param aGroup
	 */
	public void setData(List<RTEDInfoSubTree> aGroup) {
		mGroup = aGroup;
	}

	public void setQueryTree(Node aQTreeLeft, Node aQTreeRight) {
		mQTreeLeftRev = aQTreeLeft;
		mQTreeRightRev = aQTreeRight;
	}

	/**
	 * Gets the method name.
	 * 
	 * @param aSource the a source
	 * @param aSubTree the a sub tree
	 * @return the method name
	 */
	public String getMethodName(String aSource, Node aSubTree) {
		UTASTNodeFinder finder = new UTASTNodeFinder();
		MethodDeclaration iMethod = finder.findMethod(aSource, aSubTree.getEntity().getSourceRange(), false);
		return iMethod.getName().getFullyQualifiedName();
	}

	public TableViewer getSummaryTableViewer() {
		return mSummaryTableViewer;
	}
	
	public TableViewer getAnomalyTableViewer() {
		return mAnomalyTableViewer;
	}

	public CheckboxTreeViewer getTVSimilarContext() {
		return mTVSimilarContext;
	}

	public Browser getLeftBrowser() {
		return mBrowserLeft;
	}

	public Browser getRightBrowser() {
		return mMatchingBrowser;
	}

	public Text getTxtSearch() {
		return mTxtSearch;
	}

	public CriticsCBTreeHelper getCBTreeHelper() {
		return mCBTreeHelper;
	}

	public CriticsOverlayBrowser getHTMLLeftBrowser() {
		return mHTMLLeftBrowser;
	}

	public CriticsOverlayBrowser getHTMLRightBrowser() {
		return mHTMLRightBrowser;
	}

	public Button getRadioButton() {
		return mRadioBtn;
	}

	public Node getQTreeNewRev() {
		return mQTreeLeftRev;
	}

	public Node getQTreeOldRev() {
		return mQTreeRightRev;
	}

	private CheckboxTreeViewer				mTVSimilarContext	= null;
	private CriticsOverlayViewMenu			mMenuHelper			= null;
	private CriticsCBTreeHelper				mCBTreeHelper		= null;
	private Text							mTxtSearch			= null;
	private Button							mRadioBtn			= null;
	private CriticsCBTreeContentProvider	mContentProvider	= null;

	public void createPartControl2(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		// /////////////////////////////////////////////////////////////////////////////
		// Group 1
		// /////////////////////////////////////////////////////////////////////////////
		SashForm sashFormMain = new SashForm(container, SWT.NONE);
		GridData gd_sashForm = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_sashForm.widthHint = 680;
		gd_sashForm.heightHint = 498;
		sashFormMain.setLayoutData(gd_sashForm);
		Group grSimilarContext = new Group(sashFormMain, SWT.NONE);
		grSimilarContext.setToolTipText("Similar Context Group");
		grSimilarContext.setLayout(new GridLayout(17, false));
		grSimilarContext.setText("Context Group:");
		// /////////////////////////////////////////////////////////////////////////////////
		// Checkbox Tree Viewer
		// /////////////////////////////////////////////////////////////////////////////////
		mTVSimilarContext = new CheckboxTreeViewer(grSimilarContext, SWT.NONE);
		mTVSimilarContext.getTree().setBackground(SWTResourceManager.getColor(240, 255, 255));
		GridData gd_trSimilarContext = new GridData(SWT.FILL, SWT.FILL, true, true, 17, 1);
		mTVSimilarContext.getTree().setLayoutData(gd_trSimilarContext);
		gd_trSimilarContext.widthHint = 215;
		mTVSimilarContext.setUseHashlookup(true);
		mTVSimilarContext.setContentProvider(mContentProvider);
		ILabelDecorator decorator = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
		mTVSimilarContext.setLabelProvider(new CriticsCBTreeLabelProvider(new LabelProvider(), decorator));
		mCBTreeHelper = CriticsCBTreeHelper.attach(mTVSimilarContext, mContentProvider);
		// /////////////////////////////////////////////////////////////////////////////
		// Radio button
		// /////////////////////////////////////////////////////////////////////////////
		mRadioBtn = new Button(grSimilarContext, SWT.RADIO);
		mRadioBtn.setSelection(false);
		// /////////////////////////////////////////////////////////////////////////////////
		// Text
		// /////////////////////////////////////////////////////////////////////////////////
		mTxtSearch = new Text(grSimilarContext, SWT.BORDER | SWT.WRAP | SWT.SEARCH);
		mTxtSearch.setForeground(SWTResourceManager.getColor(0, 102, 204));
		mTxtSearch.setBackground(SWTResourceManager.getColor(240, 255, 255));
		mTxtSearch.setText("type command");
		mTxtSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 16, 1));
		// /////////////////////////////////////////////////////////////////////////////
		// Group 2
		// /////////////////////////////////////////////////////////////////////////////
		SashForm sashFormSub = new SashForm(sashFormMain, SWT.NONE);
		// /////////////////////////////////////////////////////////////////////////////////
		// Browser
		// /////////////////////////////////////////////////////////////////////////////////
		mBrowserLeft = new Browser(sashFormSub, SWT.BORDER);
		mBrowserLeft.setBackground(SWTResourceManager.getColor(204, 204, 204));
		mHTMLLeftBrowser = new CriticsOverlayBrowser(mBrowserLeft);
		mMatchingBrowser = new Browser(sashFormSub, SWT.BORDER);
		mMatchingBrowser.setBackground(SWTResourceManager.getColor(204, 204, 204));
		mHTMLRightBrowser = new CriticsOverlayBrowser(mMatchingBrowser);
		// /////////////////////////////////////////////////////////////////////////////////
		// Event & Menu
		// /////////////////////////////////////////////////////////////////////////////////
		mMenuHelper = new CriticsOverlayViewMenu(this);
		mMenuHelper.addMenuToCheckboxTreeViewer();
		mMenuHelper.addMenuToBrowser();
		mEventHandler = new CriticsOverlayViewEvent(this);
		mEventHandler.addEventToRadioButton();
		mEventHandler.addEventToTextField();
		mEventHandler.addEventToTree();
		sashFormSub.setWeights(new int[] { 1, 1 });
		sashFormMain.setWeights(new int[] { 202, 522 });
	}

	/**
	 * Make original checkbox tree.
	 */
	public void makeOriginalCBTree() {
		CriticsCBTreeNode root = new CriticsCBTreeNode("root", CriticsCBTreeElemType.NONE);
		Map<File, CriticsCBTreeNode> mapFileNode = new TreeMap<File, CriticsCBTreeNode>();
		Map<String, CriticsCBTreeNode> mapMethodNode = new TreeMap<String, CriticsCBTreeNode>();
		CriticsCBTreeNode iFileCBTNode = null, iMethodCBTNode = null, iBlockCBTNode = null;

		for (RTEDInfoSubTree iSubTreeInfo : mGroup) {
			File iFile = iSubTreeInfo.getFullFilePath();
			String iSource = UTFile.getContents(iFile.getAbsolutePath());
			Node iSubTree = iSubTreeInfo.getSubTree();
			String iMethodName = getMethodName(iSource, iSubTree);

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
			iBlockCBTNode = new CriticsCBTreeNode(iSubTree.getLabel().name(), CriticsCBTreeElemType.BLOCK);
			iBlockCBTNode.setSubtreeInfo(iSubTreeInfo);
			iMethodCBTNode.addChild(iBlockCBTNode);
		}
		for (Map.Entry<File, CriticsCBTreeNode> e : mapFileNode.entrySet()) {
			root.addChild(e.getValue());
		}
		mTVSimilarContext.setInput(root);
	}

}
