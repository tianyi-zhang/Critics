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

import org.eclipse.jface.viewers.ILabelDecorator;
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
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.SWTResourceManager;

import edu.utexas.seal.plugins.overlay.model.CriticsTableViewerContentProvider;
import edu.utexas.seal.plugins.overlay.model.CriticsTableViewerLabelProvider;

/**
 * @author Myoungkyu Song
 * @date Feb 27, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class TestCriticsOverlayView2 extends ViewPart {

	public static final String					ID				= "edu.utexas.seal.plugins.overlay.TestCriticsOverlayView2";	//$NON-NLS-1$
	private TableViewer							mTableViewer	= null;
	private CriticsTableViewerContentProvider	mTableViewerCP	= null;
	private CriticsTableViewerLabelProvider		mTableViewerLP	= null;
	private Browser								mBrowserRight	= null;

	public TestCriticsOverlayView2() {
		mTableViewerCP = new CriticsTableViewerContentProvider();
		ILabelDecorator decorator = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
		mTableViewerLP = new CriticsTableViewerLabelProvider(new LabelProvider(), decorator);
	}

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
		mGrpSummary.setText("Summarization/Anomalies:");
		mGrpSummary.setLayout(new GridLayout(1, false));
		// /////////////////////////////////////////////////////////////////////////////////
		// Table viewer
		// /////////////////////////////////////////////////////////////////////////////////
		mTableViewer = new TableViewer(mGrpSummary, SWT.BORDER | SWT.FULL_SELECTION);
		Table mContextList = mTableViewer.getTable();
		mContextList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		mTableViewer.setContentProvider(mTableViewerCP);
		mTableViewer.setLabelProvider(mTableViewerLP);
		// /////////////////////////////////////////////////////////////////////////////////
		// Browser
		// /////////////////////////////////////////////////////////////////////////////////
		Group mGrpRightOverlay = new Group(mSashFormMain, SWT.NONE);
		mGrpRightOverlay.setText("Overlay:");
		mGrpRightOverlay.setLayout(new GridLayout(1, false));
		mBrowserRight = new Browser(mGrpRightOverlay, SWT.BORDER);
		mBrowserRight.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		mBrowserRight.setBackground(SWTResourceManager.getColor(204, 204, 204));
		mSashFormMain.setWeights(new int[] { 168, 413 });
	}

	@Override
	public void setFocus() {
	}
}
