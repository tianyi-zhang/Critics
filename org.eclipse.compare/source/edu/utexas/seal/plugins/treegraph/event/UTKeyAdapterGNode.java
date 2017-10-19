/*
 * @(#) UTKeyAdapter.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.treegraph.event;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

/**
 * @author Myoungkyu Song
 * @date Dec 24, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTKeyAdapterGNode extends KeyAdapter {
	private boolean	isCtrKey;

	@Override
	public void keyPressed(KeyEvent e) {
		if ((/*e.stateMask &*/SWT.CONTROL) != 0) {
			isCtrKey = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if ((/*e.stateMask &*/SWT.CONTROL) != 0) {
			isCtrKey = false;
		}
	}

	public boolean isCtrKey() {
		return isCtrKey;
	}

	public void setCtrKey(boolean isCtrKey) {
		this.isCtrKey = isCtrKey;
	}
}
