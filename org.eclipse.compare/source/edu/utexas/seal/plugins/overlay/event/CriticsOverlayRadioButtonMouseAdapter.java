/*
 * @(#) RadioButtonMouseAdapter.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.overlay.event;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

/**
 * @author Myoungkyu Song
 * @date Feb 7, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsOverlayRadioButtonMouseAdapter extends MouseAdapter {

	private CriticsOverlayViewEvent	mCriticsOverlayViewEvent;

	public CriticsOverlayRadioButtonMouseAdapter() {
	}

	public CriticsOverlayRadioButtonMouseAdapter(CriticsOverlayViewEvent criticsOverlayViewEvent) {
		mCriticsOverlayViewEvent = criticsOverlayViewEvent;
	}

	@Override
	public void mouseDown(MouseEvent e) {
		if (mCriticsOverlayViewEvent.mBfExpand) {
			mCriticsOverlayViewEvent.mTVSimilarContext.expandAll();
		} else {
			mCriticsOverlayViewEvent.mTVSimilarContext.collapseAll();
		}
		mCriticsOverlayViewEvent.mBfExpand = !mCriticsOverlayViewEvent.mBfExpand;
	}
}
