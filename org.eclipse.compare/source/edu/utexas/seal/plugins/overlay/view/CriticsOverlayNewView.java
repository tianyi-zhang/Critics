/*
 * @(#) CriticsOverlayNewView.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.overlay.view;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wb.swt.SWTResourceManager;

import ut.seal.plugins.utils.change.UTChangeDistiller;
import edu.utexas.seal.plugins.overlay.event.CriticsOverlayViewEvent;
import edu.utexas.seal.plugins.overlay.model.CriticsTableViewerContentProvider;
import edu.utexas.seal.plugins.overlay.model.CriticsTableViewerLabelProvider;
import org.eclipse.swt.layout.FillLayout;

/**
 * @author Myoungkyu Song
 * @date Feb 27, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsOverlayNewView extends CriticsOverlayView {
	public static final String	ID			= "edu.utexas.seal.plugins.overlay.view.CriticsOverlayNewView"; //$NON-NLS-1$
	CriticsOverlayNewBrowser	mNewDiffBrowser	= null;
	CriticsOverlayNewBrowser    mNewTempBrowser    = null;

	public CriticsOverlayNewView() {
		mTableViewerCP = new CriticsTableViewerContentProvider();
		mTableViewerLP = new CriticsTableViewerLabelProvider(new LabelProvider(), //
				PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator());
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm mSashFormMain = new SashForm(container, SWT.NONE);
		SashForm mSashFormResult = new SashForm(mSashFormMain, SWT.VERTICAL);
		Group mGrpSummary = new Group(mSashFormResult, SWT.NONE);
		mGrpSummary.setText("Matching Locations");
		mGrpSummary.setLayout(new GridLayout(1, false));
		// /////////////////////////////////////////////////////////////////////////////////
		// Table viewer
		// /////////////////////////////////////////////////////////////////////////////////
		mSummaryTableViewer = new TableViewer(mGrpSummary, SWT.BORDER | SWT.FULL_SELECTION);
		Table mSummaryList = mSummaryTableViewer.getTable();
		mSummaryList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		mSummaryTableViewer.setContentProvider(mTableViewerCP);
		mSummaryTableViewer.setLabelProvider(mTableViewerLP);
		
		Group mGrpAnomalies = new Group(mSashFormResult, SWT.NONE);
		mGrpAnomalies.setText("Inconsistent Locations");
		mGrpAnomalies.setLayout(new GridLayout(1, false));
		mAnomalyTableViewer = new TableViewer(mGrpAnomalies, SWT.BORDER | SWT.FULL_SELECTION);
		Table mAnomalyList = mAnomalyTableViewer.getTable();
		mAnomalyList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		mAnomalyTableViewer.setContentProvider(mTableViewerCP);
		mAnomalyTableViewer.setLabelProvider(mTableViewerLP);
		mSashFormResult.setWeights(new int[] {184, 119});
		
		SashForm mSashFormTemplate = new SashForm(mSashFormMain, SWT.NONE);
		// /////////////////////////////////////////////////////////////////////////////////
		// Browser
		// /////////////////////////////////////////////////////////////////////////////////
		Group mGrpRightOverlay = new Group(mSashFormTemplate, SWT.NONE);
		mGrpRightOverlay.setText("Diff Details");
		mGrpRightOverlay.setLayout(new GridLayout(1, false));
		mMatchingBrowser = new Browser(mGrpRightOverlay, SWT.BORDER);
		mMatchingBrowser.setBounds(7, 22, 66, 66);
		mMatchingBrowser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		mMatchingBrowser.setBackground(SWTResourceManager.getColor(204, 204, 204));
		mNewDiffBrowser = new CriticsOverlayNewBrowser(mMatchingBrowser);

		Group mGrpTemplate = new Group(mSashFormTemplate, SWT.NONE);
		mGrpTemplate.setText("Diff Template");
		mGrpTemplate.setLayout(new GridLayout(1, false));
		
		mTemplateBrowser = new Browser(mGrpTemplate, SWT.BORDER);
		mTemplateBrowser.setBounds(10, 24, 64, 64);
		mTemplateBrowser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		mTemplateBrowser.setBackground(SWTResourceManager.getColor(204, 204, 204));
		mNewTempBrowser = new CriticsOverlayNewBrowser(mTemplateBrowser);
		
		mSashFormTemplate.setWeights(new int[] {1, 1});
		mSashFormMain.setWeights(new int[] {1, 1});
		// /////////////////////////////////////////////////////////////////////////////////
		// Event & Menu
		// /////////////////////////////////////////////////////////////////////////////////
		mEventHandler = new CriticsOverlayViewEvent(this);
		mEventHandler.addEventToSummaryTableViewer();
		mEventHandler.addEventToAnomalyTableViewer();
		mMenuHandler = new CriticsOverlayViewMenu(this);
		mMenuHandler.addMenuToSummaryTableViewer();
		mMenuHandler.addMenuToAnomalyTableViewer();
	}

	/**
	 * Show.
	 */
	public void show() {
		makeOriginalListView();
		UTChangeDistiller changeDistiller = diffQTree();
		mNewTempBrowser.setSourceBaseNode(changeDistiller, mQTreeRightRev, mQTreeLeftRev);
	}

	public CriticsOverlayNewBrowser getNewDiffBrowser() {
		return mNewDiffBrowser;
	}
	
	public CriticsOverlayNewBrowser getNewTempBrowser() {
		return mNewTempBrowser;
	}
}
