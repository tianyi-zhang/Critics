/*
 * @(#) UTCriticsTextSelection.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.util;

import org.eclipse.compare.internal.MergeSourceViewer;
import org.eclipse.compare.internal.merge.DocumentMerger;
import org.eclipse.jface.text.Position;

/**
 * @author Myoungkyu Song
 * @date Oct 21, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTCriticsTextSelection {
	public static MergeSourceViewer	leftMergeSourceViewer;
	public static MergeSourceViewer	rightMergeSourceViewer;
	public static Position			leftDiffPosition;
	public static Position			rightDiffPosition;
	public static String			leftSelectedDiff;
	public static String			rightSelectedDiff;
	public static DocumentMerger	docMerger;
}
