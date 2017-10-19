/*
 * @(#) LayerB.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.overlay;

/**
 * @author Myoungkyu Song
 * @date Jan 20, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class LayerDeleteBg implements ILayer, ICriticsHTMLKeyword {

	@Override
	public String getStyle() {
		return HTML_BGN_SPAN_LDELETEBG;
	}

	@Override
	public String getStyleDecl() {
		return CSS_SPAN_LDELETEBG;
	}
}
