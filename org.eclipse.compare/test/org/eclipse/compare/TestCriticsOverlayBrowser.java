/*
 * @(#) TestCriticsOverlayBrowser.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package org.eclipse.compare;

import org.junit.Test;

import edu.utexas.seal.plugins.overlay.view.CriticsOverlayBrowser;

/**
 * @author Myoungkyu Song
 * @date Feb 12, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class TestCriticsOverlayBrowser {

	CriticsOverlayBrowser	cb	= new CriticsOverlayBrowser(null);

	@Test
	public void testSetSourceNodeParm() {
		String n1 = "StringBuilder&nbsp;$v0&nbsp;=&nbsp;new&nbsp;StringBuilder();";
		String string = cb.setSourceNodeParm(n1);
		System.out.println("[DBG] " + string);
	}
}
