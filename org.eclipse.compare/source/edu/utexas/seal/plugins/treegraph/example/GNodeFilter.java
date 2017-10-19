/*
 * @(#) NodeFilter.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.treegraph.example;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * @author Myoungkyu Song
 * @date Dec 18, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class GNodeFilter extends ViewerFilter {

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof GNode) {
			GNode node = (GNode) element;
			return !node.getName().contains("Frankfurt");
		}
		return true;
	}
}
