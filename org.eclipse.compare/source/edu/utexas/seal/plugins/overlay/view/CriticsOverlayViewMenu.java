/*
 * @(#) CriticsOverlayViewHelper.java
 *
 * Copyright 2013 2014 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.overlay.view;

import java.io.File;
import java.util.List;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import ut.seal.plugins.utils.change.UTChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import edu.utexas.seal.plugins.overlay.model.CriticsCBTreeHelper;
import edu.utexas.seal.plugins.overlay.model.CriticsCBTreeNode;
import edu.utexas.seal.plugins.util.UTCriticsEditor;
import edu.utexas.seal.plugins.util.UTCriticsPairFileInfo;

/**
 * @author Myoungkyu Song
 * @date Feb 7, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsOverlayViewMenu {
	protected CriticsOverlayView		mCriticsOverlayView	= null;
	private CheckboxTreeViewer			mTVSimilarContext	= null;
	private Text						mTxtSearch			= null;
	private Browser						mLeftBrowser		= null;
	private Browser						mRightBrowser		= null;
	private CriticsOverlayBrowser		mHTMLLeftBrowser	= null;
	private CriticsOverlayBrowser		mHTMLRightBrowser	= null;
	private CriticsOverlayNewBrowser	mNewBrowser			= null;
	private CriticsCBTreeHelper			mCBTreeHelper		= null;
	private TableViewer					mSummaryTableViewer		= null;
	private TableViewer 				mAnomalyTableViewer     = null;

	public CriticsOverlayViewMenu(CriticsOverlayView criticsOverlayView) {
		mCriticsOverlayView = criticsOverlayView;
		mTVSimilarContext = criticsOverlayView.getTVSimilarContext();
		mTxtSearch = criticsOverlayView.getTxtSearch();
		mLeftBrowser = criticsOverlayView.getLeftBrowser();
		mRightBrowser = criticsOverlayView.getRightBrowser();
		mHTMLLeftBrowser = criticsOverlayView.getHTMLLeftBrowser();
		mHTMLRightBrowser = criticsOverlayView.getHTMLRightBrowser();
		mCBTreeHelper = criticsOverlayView.getCBTreeHelper();
		mSummaryTableViewer = criticsOverlayView.getSummaryTableViewer();
		mAnomalyTableViewer = criticsOverlayView.getAnomalyTableViewer();
	}

	public CriticsOverlayViewMenu(CriticsOverlayNewView overlayView) {
		mCriticsOverlayView = overlayView;
		mSummaryTableViewer = overlayView.getSummaryTableViewer();
		mAnomalyTableViewer = overlayView.getAnomalyTableViewer();
		mNewBrowser = overlayView.getNewDiffBrowser();
	}

	void addMenuToSummaryTableViewer() {
		Table table = mSummaryTableViewer.getTable();
		Menu itemActions = new Menu(table);
		table.setMenu(itemActions);

		MenuItem itemActionShowTemplate = new MenuItem(itemActions, SWT.NONE);
		itemActionShowTemplate.setText("Show Template");
		itemActionShowTemplate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				UTChangeDistiller changeDistiller = mCriticsOverlayView.diffQTree();
				Node qTreeLeftRev = mCriticsOverlayView.getQTreeNewRev();
				Node qTreeRightRev = mCriticsOverlayView.getQTreeOldRev();
				mNewBrowser.setSourceBaseNode(changeDistiller, qTreeRightRev, qTreeLeftRev);
				// System.out.println("[DBG] Show Template " + e);
				// if (mHTMLLeftBrowser == null || mHTMLRightBrowser == null) {
				// return;
				// }
				// mHTMLLeftBrowser.setSourceBaseNode(qTreeLeftRev, UTCriticsPairFileInfo.getLeftFile());
				// mHTMLRightBrowser.setSourceBaseNode(qTreeRightRev, UTCriticsPairFileInfo.getLeftFile());
			}
		});
	}
	
	void addMenuToAnomalyTableViewer() {
		Table table = mAnomalyTableViewer.getTable();
		Menu itemActions = new Menu(table);
		table.setMenu(itemActions);

		MenuItem itemActionShowTemplate = new MenuItem(itemActions, SWT.NONE);
		itemActionShowTemplate.setText("Show Template");
		itemActionShowTemplate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				UTChangeDistiller changeDistiller = mCriticsOverlayView.diffQTree();
				Node qTreeLeftRev = mCriticsOverlayView.getQTreeNewRev();
				Node qTreeRightRev = mCriticsOverlayView.getQTreeOldRev();
				mNewBrowser.setSourceBaseNode(changeDistiller, qTreeRightRev, qTreeLeftRev);
			}
		});
	}

	/**
	 * Adds the menu to checkbox tree viewer.
	 */
	void addMenuToCheckboxTreeViewer() {
		Menu mnSimilarContext = new Menu(mTVSimilarContext.getTree());
		mTVSimilarContext.getTree().setMenu(mnSimilarContext);

		MenuItem mntmExpand = new MenuItem(mnSimilarContext, SWT.NONE);
		mntmExpand.setText("Expand All");
		mntmExpand.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("[DBG] expand " + e);
				mTVSimilarContext.expandAll();
			}
		});

		MenuItem mntmCollapse = new MenuItem(mnSimilarContext, SWT.NONE);
		mntmCollapse.setText("Collapse All");
		mntmCollapse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("[DBG] collapse " + e);
				mTVSimilarContext.collapseAll();
			}
		});

		MenuItem mntmOpenCompare = new MenuItem(mnSimilarContext, SWT.NONE);
		mntmOpenCompare.setText("Open Compare");
		mntmOpenCompare.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ISelection selElem = mTVSimilarContext.getSelection();
				if (selElem instanceof TreeSelection) {
					TreeSelection selTreeNode = (TreeSelection) selElem;
					Object selObj = selTreeNode.getFirstElement();
					if (selObj instanceof CriticsCBTreeNode) {
						CriticsCBTreeNode cbtNode = (CriticsCBTreeNode) selObj;
						File f1117 = cbtNode.getFile();
						String f1117Name = f1117.getAbsolutePath();
						String prjNameRight = UTCriticsPairFileInfo.getRightProjectName();
						String prjNameLeft = UTCriticsPairFileInfo.getLeftProjectName();
						String f1114Name = f1117Name.replace(prjNameRight, prjNameLeft);
						File f1114 = new File(f1114Name);
						UTCriticsEditor.openComparisonEditor(f1114, f1117);
					}
				}
			}
		});

		MenuItem mntmOpenEditorOldRev = new MenuItem(mnSimilarContext, SWT.NONE);
		mntmOpenEditorOldRev.setText("Open Editor (Old Rev.)");
		mntmOpenEditorOldRev.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ISelection selElem = mTVSimilarContext.getSelection();
				if (selElem instanceof TreeSelection) {
					TreeSelection selTreeNode = (TreeSelection) selElem;
					Object selObj = selTreeNode.getFirstElement();
					if (selObj instanceof CriticsCBTreeNode) {
						CriticsCBTreeNode cbtNode = (CriticsCBTreeNode) selObj;
						File f1117 = cbtNode.getFile();
						UTCriticsEditor.openEditor(f1117);
					}
				}
			}
		});

		MenuItem mntmOpenEditorNewRev = new MenuItem(mnSimilarContext, SWT.NONE);
		mntmOpenEditorNewRev.setText("Open Editor (New Rev.)");
		mntmOpenEditorNewRev.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ISelection selElem = mTVSimilarContext.getSelection();
				if (selElem instanceof TreeSelection) {
					TreeSelection selTreeNode = (TreeSelection) selElem;
					Object selObj = selTreeNode.getFirstElement();
					if (selObj instanceof CriticsCBTreeNode) {
						CriticsCBTreeNode cbtNode = (CriticsCBTreeNode) selObj;
						File f1117 = cbtNode.getFile();
						String f1117Name = f1117.getAbsolutePath();
						String prjNameRight = UTCriticsPairFileInfo.getRightProjectName();
						String prjNameLeft = UTCriticsPairFileInfo.getLeftProjectName();
						String f1114Name = f1117Name.replace(prjNameRight, prjNameLeft);
						File f1114 = new File(f1114Name);
						UTCriticsEditor.openEditor(f1114);
					}
				}
			}
		});
	}

	/**
	 * Adds the menu to browser.
	 */
	void addMenuToBrowser() {
		// Menu menu = new Menu(mRightBrowser);
		// mRightBrowser.setMenu(menu);
		// MenuItem menuItem = new MenuItem(menu, SWT.NONE);
		// menuItem.setText("Refresh");
		// MenuItem menuItem_1 = new MenuItem(menu, SWT.NONE);
		// menuItem_1.setText("Open");
		// MenuItem menuItem_2 = new MenuItem(menu, SWT.NONE);
		// menuItem_2.setText("Save");
		Menu mnBrowser = new Menu(mLeftBrowser);
		mLeftBrowser.setMenu(mnBrowser);
		mRightBrowser.setMenu(mnBrowser);

		MenuItem mntmRefresh = new MenuItem(mnBrowser, SWT.NONE);
		mntmRefresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String txtSearch = mTxtSearch.getText();
				if (txtSearch.equals("t1") || txtSearch.equals("test1")) {
					mHTMLRightBrowser.sampleOverlay();
					mHTMLLeftBrowser.sampleOverlay();
				} else {
					List<Object> arSelected = mCBTreeHelper.getCheckedElements();
					mHTMLRightBrowser.drawBrowser(arSelected);
					mHTMLLeftBrowser.drawBrowser(arSelected);
				}
			}
		});
		mntmRefresh.setText("Refresh");

		MenuItem mntmOpen = new MenuItem(mnBrowser, SWT.NONE);
		mntmOpen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("[DBG] open " + e);
			}
		});
		mntmOpen.setText("Open");

		MenuItem mntmSave = new MenuItem(mnBrowser, SWT.NONE);
		mntmSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("[DBG] save " + e);
			}
		});
		mntmSave.setText("Save");
	}

}
