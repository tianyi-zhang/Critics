/*
 * @(#) TextFieldAdapter.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.overlay.event;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolTip;

/**
 * @author Myoungkyu Song
 * @date Feb 7, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsOverlayTextFieldAdapter {
	CriticsOverlayViewEvent	mCriticsOverlayViewEvent	= null;
	ToolTip					tip							= null;
	String					mCmdInfo					= "[info] r(refresh), c(clear), z(zoom):#num, t + #num: testing, simple: simple test";

	public CriticsOverlayTextFieldAdapter(CriticsOverlayViewEvent criticsOverlayViewEvent) {
		mCriticsOverlayViewEvent = criticsOverlayViewEvent;
		tip = new ToolTip(mCriticsOverlayViewEvent.mTxtSearch.getShell(), SWT.BALLOON);
		tip.setMessage(mCmdInfo);
	}

	public class TextFieldFocusAdapter extends FocusAdapter {
		@Override
		public void focusLost(FocusEvent e) {
			// private boolean bfFocused = false;
			//
			// @Override
			// public void focusGained(FocusEvent e) {
			// if (!bfFocused) {
			// bfFocused = true;
			// mTxtSearch.setText("");
			// }
			// tip.setVisible(true);
			// }
			tip.setVisible(false);
		}
	}

	public class TextFieldMouseListener implements MouseListener {
		@Override
		public void mouseUp(MouseEvent e) {
		}

		@Override
		public void mouseDown(MouseEvent e) {
			tip.setVisible(true);
		}

		@Override
		public void mouseDoubleClick(MouseEvent e) {
			tip.setVisible(false);
		}
	}

	public class TextFieldMouseMoveListener implements MouseMoveListener {
		@Override
		public void mouseMove(MouseEvent e) {
			tip.setVisible(false);
		}
	}

	public class TextFieldModifyListener implements ModifyListener {
		@Override
		public void modifyText(ModifyEvent e) {
			mCriticsOverlayViewEvent.mBtnRadioButton.setSelection(false);
			tip.setVisible(false);
			Text t = (Text) e.widget;
			String strCmd = t.getText().trim();
			if (strCmd.equalsIgnoreCase("r")) {
				refreshCBTree();
			} else if (strCmd.equalsIgnoreCase("c")) {
				cleanAllView();
			} else if (strCmd.equalsIgnoreCase("o")) {
				mCriticsOverlayViewEvent.show();
			} else if (strCmd.startsWith("test1") || strCmd.startsWith("t1")) {
				testSampleOverlay();
			} else if (strCmd.startsWith("help")) {
				tip.setVisible(true);
				tip.setMessage(mCmdInfo);
			} else if (strCmd.startsWith("simple")) {
				tip.setVisible(true);
				tip.setMessage("Testing Simple Overlay..");
			} else if (strCmd.startsWith("z:")) {
				try {
					int szGroup = Integer.valueOf(strCmd.trim().replace("g:", "").trim());
					mCriticsOverlayViewEvent.refreshCBTreeByGroupSize(szGroup);
				} catch (NumberFormatException e1) {
					// silence e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * Test sample overlay.
	 */
	void testSampleOverlay() {
		refreshCBTree();
		mCriticsOverlayViewEvent.mHTMLLeftBrowser.sampleOverlay();
		mCriticsOverlayViewEvent.mHTMLRightBrowser.sampleOverlay();
	}

	/**
	 * Clean all view.
	 */
	void cleanAllView() {
		refreshCBTree();
		mCriticsOverlayViewEvent.mHTMLLeftBrowser.cleanBrowser();
		mCriticsOverlayViewEvent.mHTMLRightBrowser.cleanBrowser();
	}

	/**
	 * Refresh cb tree.
	 */
	void refreshCBTree() {
		Object[] arObj = mCriticsOverlayViewEvent.mTVSimilarContext.getCheckedElements();
		for (int i = 0; i < arObj.length; i++) {
			mCriticsOverlayViewEvent.mTVSimilarContext.setChecked(arObj[i], false);
		}
		if (!mCriticsOverlayViewEvent.mBfExpand) {
			mCriticsOverlayViewEvent.mTVSimilarContext.expandAll();
		} else {
			mCriticsOverlayViewEvent.mTVSimilarContext.collapseAll();
		}
	}
}
