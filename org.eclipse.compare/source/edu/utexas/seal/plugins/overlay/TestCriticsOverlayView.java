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
package edu.utexas.seal.plugins.overlay;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.SWTResourceManager;

import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import rted.distance.RTEDInfoSubTree;
import ut.seal.plugins.utils.UTCfg;
import ut.seal.plugins.utils.UTFile;
import ut.seal.plugins.utils.ast.UTASTNodeFinder;
import edu.utexas.seal.plugins.overlay.model.CriticsCBTreeElemType;
import edu.utexas.seal.plugins.overlay.model.CriticsCBTreeHelper;
import edu.utexas.seal.plugins.overlay.model.CriticsCBTreeNode;
import edu.utexas.seal.plugins.overlay.view.CriticsOverlayBrowser;
import edu.utexas.seal.plugins.util.UTPlugin;
import edu.utexas.seal.plugins.util.UTCriticsHTML.HTML;

import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;

public class TestCriticsOverlayView extends ViewPart implements ICriticsHTMLKeyword {
	public static final String		ID						= "edu.utexas.seal.plugins.overlay.view.TestCriticsOverlayView";	//$NON-NLS-1$

	private CheckboxTreeViewer		mTVSimilarContext		= null;
	private Browser					mBrowserRight;
	// private Text mTxtSearch = null;
	private boolean					mBfExpand				= true;
	private List<RTEDInfoSubTree>	mGroupClusteredBySim	= null;
	private CriticsCBTreeHelper		mCBTreeHelper			= null;
	private CriticsOverlayBrowser	mHTMLBrowser			= null;

	private TableViewer				mTableViewer			= null;

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
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

		mTableViewer = new TableViewer(mGrpSummary, SWT.BORDER | SWT.FULL_SELECTION);
		Table table = mTableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		SashForm mSashFormSub = new SashForm(mSashFormMain, SWT.NONE);

		Group mGrpLeftOverlay = new Group(mSashFormSub, SWT.NONE);
		mGrpLeftOverlay.setText("Left Overlay:");
		mGrpLeftOverlay.setLayout(new GridLayout(1, false));

		Browser mBrowserLeft = new Browser(mGrpLeftOverlay, SWT.BORDER);
		mBrowserLeft.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		mBrowserLeft.setBackground(SWTResourceManager.getColor(204, 204, 204));

		Group mGrpRightOverlay = new Group(mSashFormSub, SWT.NONE);
		mGrpRightOverlay.setText("Right Overlay:");
		mGrpRightOverlay.setLayout(new GridLayout(1, false));

		mBrowserRight = new Browser(mGrpRightOverlay, SWT.BORDER);
		mBrowserRight.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		mBrowserRight.setBackground(SWTResourceManager.getColor(204, 204, 204));
		mHTMLBrowser = new CriticsOverlayBrowser(mBrowserRight);
		mSashFormSub.setWeights(new int[] { 1, 1 });
		mSashFormMain.setWeights(new int[] { 203, 530 });

		mTableViewer.setContentProvider(new DataElementContentProvider());
		ILabelDecorator decorator = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
		mTableViewer.setLabelProvider(new DataElementLabelProvider(new LabelProvider(), decorator));
		show();
	}

	/**
	 * Sets the input.
	 */
	public void show() {
		mTableViewer.setInput(new DataElemList());
	}

	class HealthyFilter extends ViewerFilter {
		public boolean select(Viewer arg0, Object arg1, Object arg2) {
			return ((DataElement) arg2).isHealthy();
		}
	}

	class DataElement {
		private String	name;

		private boolean	healthy;

		public DataElement(String name, boolean healthy) {
			this.name = name;
			this.healthy = healthy;
		}

		public boolean isHealthy() {
			return healthy;
		}

		public String getName() {
			return name;
		}
	}

	class DataElementLabelProvider extends DecoratingLabelProvider {
		public DataElementLabelProvider(ILabelProvider provider, ILabelDecorator decorator) {
			super(provider, decorator);
		}

		public Image getImage(Object arg0) {
			Image image = UTPlugin.getImage(UTPlugin.IMG_BLOCK);
			return image;
		}

		public String getText(Object arg0) {
			return ((DataElement) arg0).getName();
		}
	}

	class DataElemList {
		private List<DataElement>	dataElements;

		public DataElemList() {
			dataElements = new ArrayList<DataElement>();
			dataElements.add(new DataElement("Broccoli", true));
			dataElements.add(new DataElement("Bundt Cake", false));
			dataElements.add(new DataElement("Cabbage", true));
			dataElements.add(new DataElement("Candy Canes", false));
			dataElements.add(new DataElement("Eggs", true));
			dataElements.add(new DataElement("Potato Chips", false));
			dataElements.add(new DataElement("Milk", true));
			dataElements.add(new DataElement("Soda", false));
			dataElements.add(new DataElement("Chicken", true));
			dataElements.add(new DataElement("Cinnamon Rolls", false));
		}

		public List<DataElement> getDataElements() {
			return Collections.unmodifiableList(dataElements);
		}
	}

	class DataElementContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object arg0) {
			return ((DataElemList) arg0).getDataElements().toArray();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		}
	}

	// //////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////

	void drawBrowser() {
		List<Object> arSelected = mCBTreeHelper.getCheckedElements();
		if (arSelected.isEmpty()) {
			cleanBrowser();
			return;
		}
		mHTMLBrowser.setCheckedElements(arSelected);
	}

	/**
	 * Make new check box tree.
	 * 
	 * @param lstNodes
	 *            the list nodes
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
			String iMethodName = getMethodName(iSource, iSubtreeInfo.getSubTree());
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
	 * Gets the method name.
	 * 
	 * @param aSource
	 *            the a source
	 * @param aSubTree
	 *            the a sub tree
	 * @return the method name
	 */
	String getMethodName(String aSource, Node aSubTree) {
		UTASTNodeFinder finder = new UTASTNodeFinder();
		MethodDeclaration iMethod = finder.findMethod(aSource, aSubTree.getEntity().getSourceRange(), false);
		return iMethod.getName().getFullyQualifiedName();
	}

	/**
	 * Gets the all items.
	 * 
	 * @param aItem
	 *            the item
	 * @param aLstItems
	 *            the list items
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

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
	}

	/**
	 * Refresh check box tree.
	 */
	void refreshCBTree() {
		Object[] arObj = mTVSimilarContext.getCheckedElements();
		for (int i = 0; i < arObj.length; i++) {
			mTVSimilarContext.setChecked(arObj[i], false);
		}
		if (!mBfExpand) {
			mTVSimilarContext.expandAll();
		} else {
			mTVSimilarContext.collapseAll();
		}
	}

	/**
	 * Sets the data.
	 * 
	 * @param aGroupClusteredBySim
	 */
	public void setData(List<RTEDInfoSubTree> aGroupClusteredBySim) {
		mGroupClusteredBySim = aGroupClusteredBySim;
		// for (RTEDInfoSubTree iSubTreeInfo : mGroupClusteredBySim) {
		// Node iSubTree = iSubTreeInfo.getSubTree();
		// iSubTree.print();
		// System.out.println("------------------------------------------");
		// }
		System.out.println("==========================================");
	}

	/**
	 * Make original checkbox tree.
	 */
	void makeOriginalCBTree() {
		CriticsCBTreeNode root = new CriticsCBTreeNode("root", CriticsCBTreeElemType.NONE);
		Map<File, CriticsCBTreeNode> mapFileNode = new TreeMap<File, CriticsCBTreeNode>();
		Map<String, CriticsCBTreeNode> mapMethodNode = new TreeMap<String, CriticsCBTreeNode>();
		CriticsCBTreeNode iFileCBTNode = null, iMethodCBTNode = null, iBlockCBTNode = null;

		for (RTEDInfoSubTree iSubTreeInfo : mGroupClusteredBySim) {
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

	/**
	 * Clean all view.
	 */
	void cleanAllView() {
		refreshCBTree();
		cleanBrowser();
	}

	/**
	 * Clean browser.
	 */
	private void cleanBrowser() {
		mBrowserRight.setText("<html><body>" //
				+ "<font color=\"blue\"> *** Checkbox Unchecked " //
				+ "<img src=\"file:///" + UTPlugin.getFilePath(UTPlugin.IMG_CHK) + "\">  *** </font>" //
				+ "</body></html>");
	}

	void testSampleOverlay() {
		refreshCBTree();
		sampleOverlay();
	}

	private void sampleOverlay() {
		List<String> lstHTML = new ArrayList<String>();
		lstHTML.add(HTML_BGN_HEAD);
		lstHTML.add(HTML_BGN_STYLE);
		lstHTML.add(CSS_DRAG);
		lstHTML.add(CSS_SPAN_LINSERT);
		lstHTML.add(CSS_SPAN_LDELETE);
		lstHTML.add(CSS_SPAN_LINSERTBG);
		lstHTML.add(CSS_SPAN_REGULAR);
		lstHTML.add(CSS_SPAN_COMMENT);
		lstHTML.add(HTML_END_STYLE);
		lstHTML.add(HTML_END_HEAD);
		// ////////////////////////////////////////////////////
		lstHTML.add(HTML_BGN_BODY);
		lstHTML.add(HTML.HEADER2("What's Code Overlay?"));

		lstHTML.add(HTML_BGN_SPAN_COMMENT);
		lstHTML.add(HTML.T("/* Regular, base code */", 1));
		lstHTML.add(HTML_SPAN_END);

		lstHTML.add(HTML_BGN_SPAN_REGULAR);
		lstHTML.add(HTML.T("if (flag == null)", 1));
		lstHTML.add(HTML.T("return;", 2));
		lstHTML.add(HTML.T("...", 1));
		lstHTML.add(HTML_SPAN_END);
		lstHTML.add(HTML_BR);

		lstHTML.add(HTML_BGN_SPAN_COMMENT);
		lstHTML.add(HTML.T("/* Differential, overlaid code */", 1));
		lstHTML.add(HTML_SPAN_END);

		lstHTML.add(HTML_BGN_SPAN_LINSERT);
		lstHTML.add(HTML_BGN_DRAG);
		lstHTML.add(HTML.T("for (int i = 0; i < list.size(); i++) { .. } ", 1));
		lstHTML.add(HTML_END_DRAG);
		lstHTML.add(HTML_SPAN_END);

		lstHTML.add(HTML_BGN_SPAN_LDELETE);
		lstHTML.add(HTML_BGN_DRAG);
		lstHTML.add(HTML.T("for (int i = 0; i < data.size(); i++) { ..... }", 1));
		lstHTML.add(HTML_END_DRAG);
		lstHTML.add(HTML_SPAN_END);

		lstHTML.add(HTML_BGN_SPAN_LINSERTBG);
		lstHTML.add(HTML_BGN_DRAG);
		lstHTML.add(HTML.T("for (int i = 0; i < users.size(); i++) { .......... }", 1));
		lstHTML.add(HTML_END_DRAG);
		lstHTML.add(HTML_SPAN_END);

		HTML_L_REGULAR(lstHTML, "...");
		lstHTML.add(HTML_BR);

		HTML_L_COMMENT(lstHTML, "/* Regular, base code */");
		lstHTML.add(HTML_BGN_SPAN_REGULAR);
		lstHTML.add(HTML.T("if (data.size() < 1024*2)", 1));
		lstHTML.add(HTML.T("data.append(buf);", 2));
		lstHTML.add(HTML.T("...", 1));
		lstHTML.add(HTML_SPAN_END);
		String textdrag_JS = UTCfg.getInst().readConfig().TEXTDRAG_JS;
		String fileContents = UTFile.readFileWithNewLine(textdrag_JS);
		lstHTML.add(fileContents);
		lstHTML.add(HTML_END_BODY);
		// ////////////////////////////////////////////////////
		String basicCodeSample = "";
		for (int i = 0; i < lstHTML.size(); i++) {
			String elem = lstHTML.get(i);
			System.out.println(elem);
			basicCodeSample += elem;
		}
		mBrowserRight.setText(basicCodeSample);

	}

	void HTML_L1(List<String> aLst, String aStr) {
		aLst.add(HTML_BGN_SPAN_LINSERT);
		aLst.add(HTML_T1(aStr));
		aLst.add(HTML_SPAN_END);
	}

	void HTML_L2(List<String> aLst, String aStr) {
		aLst.add(HTML_BGN_SPAN_LDELETE);
		aLst.add(HTML_T1(aStr));
		aLst.add(HTML_SPAN_END);
	}

	void HTML_L_REGULAR(List<String> aLst, String aStr) {
		aLst.add(HTML_BGN_SPAN_REGULAR);
		aLst.add(HTML_T1(aStr));
		aLst.add(HTML_SPAN_END);
	}

	void HTML_L_COMMENT(List<String> aLst, String aStr) {
		aLst.add(HTML_BGN_SPAN_COMMENT);
		aLst.add(HTML_T1(aStr));
		aLst.add(HTML_SPAN_END);
	}

	String HTML_HEADER2(String aStr) {
		return String.format(HTML_HEADER2, aStr);
	}

	String HTML_T1(String aStr) {
		return String.format(HTML_T1, aStr);
	}

	String HTML_T2(String aStr) {
		return String.format(HTML_T2, aStr);
	}

	String HTML_T3(String aStr) {
		return String.format(HTML_T3, aStr);
	}

	String HTML_T4(String aStr) {
		return String.format(HTML_T4, aStr);
	}

	String HTML_T5(String aStr) {
		return String.format(HTML_T5, aStr);
	}
}
