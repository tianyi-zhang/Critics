/*
 * @(#) NoteFigure.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.treegraph.model;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;

/**
 * @author Myoungkyu Song
 * @date Dec 21, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsTreeGraphNoteFigure extends Label {
	public CriticsTreeGraphNoteFigure(String label) {
		super(label);
		setOpaque(true);
		setBackgroundColor(ColorConstants.white);
	}
}
