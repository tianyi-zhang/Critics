/*
 * @(#) UTCriticsDiff.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.compare.internal.MergeSourceViewer;
import org.eclipse.compare.internal.MergeViewerContentProvider;
import org.eclipse.compare.internal.merge.DocumentMerger;
import org.eclipse.compare.internal.merge.DocumentMerger.Diff;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.graphics.Point;

/**
 * @author Myoungkyu Song
 * @date Oct 21, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTCriticsDiff {
	private static char			LEFT_CONTRIBUTOR	= MergeViewerContentProvider.LEFT_CONTRIBUTOR;
	private static char			RIGHT_CONTRIBUTOR	= MergeViewerContentProvider.RIGHT_CONTRIBUTOR;
	private static IDocument	leftDoc;
	private static IDocument	rightDoc;

	static List<UTChangeInfo>	changeInfoSet		= new ArrayList<UTChangeInfo>();

	/**
	 * @param msViewer
	 */
	public static void findDiff(MergeSourceViewer leftMSViewer, int leftOffset, int leftLength, //
			MergeSourceViewer rightMSViewer, int rightOffset, int rightLength) {
		changeInfoSet.clear();
		leftDoc = leftMSViewer.getSourceViewer().getDocument();
		rightDoc = rightMSViewer.getSourceViewer().getDocument();

		IRegion leftRegion = new Region(leftOffset, leftLength);
		IRegion rightRegion = new Region(rightOffset, rightLength);
		getChangeDiff(MergeViewerContentProvider.LEFT_CONTRIBUTOR, leftRegion);
		getChangeDiff(MergeViewerContentProvider.RIGHT_CONTRIBUTOR, rightRegion);
		System.out.println("DBG__________________________________________");
		print();
	}

	/**
	 * 
	 * @param region
	 * @param contributor
	 * @param msViewer
	 * @return
	 */
	private static void getChangeDiff(char contributor, IRegion region) {
		DocumentMerger fMerger = UTCriticsTextSelection.docMerger;
		Diff[] changeDiffs = fMerger.getChangeDiffs(contributor, region);
		for (int i = 0; i < changeDiffs.length; i++) {
			Diff diff = changeDiffs[i];
			int reset = diff.getKind();
			diff.setKind((reset == 2) ? 3 : 2);
			String changeType = diff.changeType().toUpperCase();
			diff.setKind(reset);

			Position rightPos = diff.getPosition(RIGHT_CONTRIBUTOR);
			Position leftPos = diff.getPosition(LEFT_CONTRIBUTOR);

			addPair(changeType, rightPos, leftPos);
		}
	}

	/**
	 * 
	 * @param changeType
	 * @param rightPos
	 * @param leftPos
	 */
	private static void addPair(String changeType, Position rightPos, Position leftPos) {
		if (!duplicated(changeType, rightPos, leftPos)) {
			changeInfoSet.add(new UTChangeInfo(changeType, rightPos, leftPos));
		}
	}

	/**
	 * 
	 * @param changeType
	 * @param rightPos
	 * @param leftPos
	 * @return
	 */
	private static boolean duplicated(String changeType, Position rightPos, Position leftPos) {
		for (int i = 0; i < changeInfoSet.size(); i++) {
			UTChangeInfo changeInfo = changeInfoSet.get(i);
			if (changeInfo.changeType.equals(changeType) && changeInfo.rightPos.equals(rightPos)
					&& changeInfo.leftPos.equals(leftPos)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 */
	private static void print() {
		for (UTChangeInfo changeInfo : changeInfoSet) {
			String changeType = changeInfo.changeType;
			Position rightPos = changeInfo.rightPos;
			Position leftPos = changeInfo.leftPos;
			int right_offset_bgn = rightPos.offset;
			int right_offset_end = right_offset_bgn + rightPos.length;
			int left_offset_bgn = leftPos.offset;
			int left_offset_end = left_offset_bgn + leftPos.length;
			Point rightLineRange = UTCriticsLocation.getLineRange(right_offset_bgn, right_offset_end, rightDoc);
			Point leftLineRange = UTCriticsLocation.getLineRange(left_offset_bgn, left_offset_end, leftDoc);

			System.out.println("     * " + changeType);
			System.out.println();
			System.out.println("[DBG] OFFSET: " + right_offset_bgn + ", " + right_offset_end);
			System.out.println("[DBG] # LINE: " + rightLineRange.x + ", " + rightLineRange.y);
			System.out.println("[DBG]     :");
			System.out.println("[DBG]     V");
			System.out.println("[DBG] OFFSET: " + left_offset_bgn + ", " + left_offset_end);
			System.out.println("[DBG] # LINE: " + leftLineRange.x + ", " + leftLineRange.y);
			System.out.println();
		}
	}
}
