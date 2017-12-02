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
