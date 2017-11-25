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
package edu.utexas.seal.plugins;

import org.eclipse.compare.internal.MergeSourceViewer;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Point;

import edu.utexas.seal.plugins.util.UTCriticsDiff;
import edu.utexas.seal.plugins.util.UTCriticsEnable;
import edu.utexas.seal.plugins.util.UTCriticsTextSelection;

/**
 * @author Myoungkyu Song
 * @date Oct 21, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsCompareHandler extends AbstractHandler {
	private MergeSourceViewer	leftMSViewer		= null;
	private MergeSourceViewer	rightMSViewer		= null;
	private ISourceViewer		leftSRViewer		= null;
	private ISourceViewer		rightSRViewer		= null;
	private Point				leftSelectedRegion	= null;
	private Point				rightSelectedRegion	= null;
	private Point				leftSelectedLine	= null;
	private Point				rightSelectedLine	= null;
	private Position			leftDiffPosition	= null;
	private Position			rightDiffPosition	= null;

	/**
	 * 
	 * @return
	 */
	boolean initiate() {
		leftMSViewer = UTCriticsTextSelection.leftMergeSourceViewer;
		rightMSViewer = UTCriticsTextSelection.rightMergeSourceViewer;

		if (leftMSViewer == null || rightMSViewer == null)
			return false;

		leftSRViewer = leftMSViewer.getSourceViewer();
		rightSRViewer = rightMSViewer.getSourceViewer();
		leftSelectedRegion = leftSRViewer.getSelectedRange();
		rightSelectedRegion = rightSRViewer.getSelectedRange();
		leftSelectedLine = getLineRange(leftSelectedRegion, leftSRViewer.getDocument());
		rightSelectedLine = getLineRange(rightSelectedRegion, rightSRViewer.getDocument());
		if (UTCriticsEnable.autoSelection) {
			leftDiffPosition = UTCriticsTextSelection.leftDiffPosition;
			rightDiffPosition = UTCriticsTextSelection.rightDiffPosition;
		}
		if (leftSelectedRegion.x < 0 || leftSelectedRegion.y < 0) {
			return false;
		}
		return true;
	}

	/**
	 * @param event
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (!initiate()) {
			System.out.println("DBG__________________________________________");
			System.out.println("[USG] OPEN A COMPARE VIEW.");
			System.out.println("DBG__________________________________________");
			return null;
		}
		if (UTCriticsEnable.autoSelection) {
			printAutoSelectionInfo();
		} else {
			printManualSelectionInfo();
		}
		return null;
	}

	/**
	 * 
	 * @return
	 */
	String getLeftContents() {
		return leftSRViewer.getDocument().get();
	}

	/**
	 * 
	 * @return
	 */
	String getRightContents() {
		return rightSRViewer.getDocument().get();
	}

	/**
	 * 
	 * @param region
	 * @return
	 */
	Point getLineRange(Point region, IDocument doc) {
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
	 */
	void printAutoSelectionInfo() {
		if (leftDiffPosition == null && rightDiffPosition == null) {
			System.out.println("DBG__________________________________________");
			System.out.println("[USG] PUT THE MOUSE CURSOR, AND PLEASE TRY AGAIN.");
			System.out.println("DBG__________________________________________");
			return;
		}

		int left_offset_bgn = leftDiffPosition.getOffset();
		int left_offset_end = left_offset_bgn + leftDiffPosition.getLength();

		int right_offset_bgn = rightDiffPosition.getOffset();
		int right_offset_end = right_offset_bgn + rightDiffPosition.getLength();

		int left_bgn_line, right_bgn_line, left_end_line, right_end_line;
		try {
			IDocument leftDoc = leftSRViewer.getDocument();
			left_bgn_line = leftDoc.getLineOfOffset(left_offset_bgn) + 1;
			left_end_line = leftDoc.getLineOfOffset(left_offset_end);

			IDocument rightDoc = rightSRViewer.getDocument();
			right_bgn_line = rightDoc.getLineOfOffset(right_offset_bgn) + 1;
			right_end_line = rightDoc.getLineOfOffset(right_offset_end);

			String leftSelectedText, rightSelectedText;
			leftSelectedText = leftDoc.get().substring(left_offset_bgn, left_offset_end);
			rightSelectedText = rightDoc.get().substring(right_offset_bgn, right_offset_end);
			System.out.println("DBG__________________________________________");
			System.out.println("     * LEFT " + "\n");
			System.out.println("[DBG] OFFSET: " + left_offset_bgn + ", " + left_offset_end);
			System.out.println("[DBG] LINE #: " + left_bgn_line + ", " + left_end_line);
			System.out.println(leftSelectedText);
			System.out.println("DBG__________________________________________");
			System.out.println("     * RIGHT " + "\n");
			System.out.println("[DBG] OFFSET: " + right_offset_bgn + ", " + right_offset_end);
			System.out.println("[DBG] LINE #: " + right_bgn_line + ", " + right_end_line);
			System.out.println(rightSelectedText);
			System.out.println("DBG__________________________________________");
			UTCriticsDiff.findDiff(leftMSViewer, left_offset_bgn, leftDiffPosition.getLength(), //
					rightMSViewer, right_offset_bgn, rightDiffPosition.getLength());

			System.out.println("DBG__________________________________________");
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	void printManualSelectionInfo() {
		String leftSelectedText, rightSelectedText;
		leftSelectedText = getLeftContents().substring(leftSelectedRegion.x,//
				leftSelectedRegion.x + leftSelectedRegion.y);
		rightSelectedText = getRightContents().substring(rightSelectedRegion.x, //
				rightSelectedRegion.x + rightSelectedRegion.y);
		System.out.println("DBG__________________________________________");
		System.out.println("     * LEFT " + "\n");
		System.out.println("[DBG] OFFSET: " + leftSelectedRegion.x + ", " + (leftSelectedRegion.x + leftSelectedRegion.y));
		System.out.println("[DBG] LINE #: " + leftSelectedLine);
		System.out.println(leftSelectedText);
		System.out.println("DBG__________________________________________");
		System.out.println("     * RIGHT " + "\n");
		System.out.println("[DBG] OFFSET: " + rightSelectedRegion.x + ", " + (rightSelectedRegion.x + rightSelectedRegion.y));
		System.out.println("[DBG] LINE #: " + rightSelectedLine);
		System.out.println(rightSelectedText);
		System.out.println("DBG__________________________________________");
		UTCriticsDiff.findDiff(leftMSViewer, leftSelectedRegion.x, leftSelectedRegion.y,//
				rightMSViewer, rightSelectedRegion.x, rightSelectedRegion.y);
		System.out.println("DBG__________________________________________");
	}
}
