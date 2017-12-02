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
package edu.utexas.seal.plugins.util.root;

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

import edu.utexas.seal.plugins.util.UTCriticsTextSelection;

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
