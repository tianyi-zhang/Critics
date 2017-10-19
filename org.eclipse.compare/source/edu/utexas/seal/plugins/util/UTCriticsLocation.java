/*
 * @(#) UTCriticsLocation.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.util;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.graphics.Point;

/**
 * @author Myoungkyu Song
 * @date Oct 23, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTCriticsLocation {

	/**
	 * 
	 * @param region
	 * @param doc
	 * @return
	 */
	public static Point getLineRange(Point region, IDocument doc) {
		Point lineRange = new Point(0, 0);
		if (doc == null) {
			return lineRange;
		}
		try {
			lineRange.x = doc.getLineOfOffset(region.x) + 1;
			lineRange.y = doc.getLineOfOffset(region.x + region.y) + 1;
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return lineRange;
	}

	/**
	 * 
	 * @param region
	 * @param doc
	 * @return
	 */
	public static Point getLineRange(IRegion region, IDocument doc) {
		Point lineRange = new Point(0, 0);
		if (doc == null) {
			return lineRange;
		}
		try {
			lineRange.x = doc.getLineOfOffset(region.getOffset()) + 1;
			lineRange.y = doc.getLineOfOffset(region.getOffset() + region.getLength()) + 1;
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return lineRange;
	}

	/**
	 * 
	 * @param left_offset_bgn
	 * @param left_offset_end
	 * @param doc
	 * @return
	 */
	public static Point getLineRange(int left_offset_bgn, int left_offset_end, IDocument doc) {
		Point lineRange = new Point(0, 0);
		if (doc == null) {
			return lineRange;
		}
		try {
			lineRange.x = doc.getLineOfOffset(left_offset_bgn) + 1;
			lineRange.y = doc.getLineOfOffset(left_offset_end) + 1;
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return lineRange;
	}

}
