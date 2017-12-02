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
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Point;

import ut.seal.plugins.utils.UTCfg;
import ut.seal.plugins.utils.UTLog;
import ut.seal.plugins.utils.UTTime;
import edu.utexas.seal.plugins.util.UTCriticsPairFileInfo;
import edu.utexas.seal.plugins.util.UTCriticsTextSelection;
import edu.utexas.seal.plugins.util.root.UTCriticsEnable;
import edu.utexas.seal.plugins.util.root.UTCriticsLocation;

/**
 * @author Myoungkyu Song
 * @date Nov 26, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public abstract class CriticsAbstract extends AbstractHandler {
	protected boolean			$					= false;

	protected MergeSourceViewer	leftMSViewer		= null;
	protected MergeSourceViewer	rightMSViewer		= null;
	protected ISourceViewer		leftSRViewer		= null;
	protected ISourceViewer		rightSRViewer		= null;
	protected Point				leftSelectedRegion	= null;
	protected Point				rightSelectedRegion	= null;
	protected Point				leftSelectedLine	= null;
	protected Point				rightSelectedLine	= null;
	protected Position			leftDiffPosition	= null;
	protected Position			rightDiffPosition	= null;

	@Override
	public abstract Object execute(ExecutionEvent event) throws ExecutionException;

	protected boolean initiate() {
		if (UTCriticsPairFileInfo.getLeftIPackageFragment() == null) {
			UTLog.println(true, "[USG] PLEASE OPEN A COMPARE VIEW & EDITOR.");
			return false;
		}
		leftMSViewer = UTCriticsTextSelection.leftMergeSourceViewer;
		rightMSViewer = UTCriticsTextSelection.rightMergeSourceViewer;
		if (leftMSViewer == null || rightMSViewer == null) {
			return false;
		}
		leftSRViewer = leftMSViewer.getSourceViewer();
		rightSRViewer = rightMSViewer.getSourceViewer();
		leftSelectedRegion = leftSRViewer.getSelectedRange();
		rightSelectedRegion = rightSRViewer.getSelectedRange();
		leftSelectedLine = UTCriticsLocation.getLineRange(leftSelectedRegion, leftSRViewer.getDocument());
		rightSelectedLine = UTCriticsLocation.getLineRange(rightSelectedRegion, rightSRViewer.getDocument());
		if (UTCriticsEnable.autoSelection) {
			leftDiffPosition = UTCriticsTextSelection.leftDiffPosition;
			rightDiffPosition = UTCriticsTextSelection.rightDiffPosition;
			if (leftDiffPosition.offset < 0 || rightDiffPosition.offset < 0) {
				UTLog.println(true, "DBG__________________________________________");
				UTLog.println(true, "[USG] PUT THE MOUSE CURSOR, AND PLEASE TRY AGAIN.");
				UTLog.println(true, "DBG__________________________________________");
				return false;
			}
		}
		if (leftSelectedRegion.x < 0 || leftSelectedRegion.y < 0) {
			UTLog.println(true, "DBG__________________________________________");
			UTLog.println(true, "[USG] PUT THE MOUSE CURSOR, AND PLEASE TRY AGAIN.");
			UTLog.println(true, "DBG__________________________________________");
			return false;
		}
		if (leftSelectedRegion.x >= 0 && leftSelectedRegion.y == 0 && //
				rightSelectedRegion.x >= 0 && rightSelectedRegion.y == 0 && //
				(leftSelectedLine.x >= 1 && leftSelectedLine.y >= 1) && //
				(rightSelectedLine.x >= 1 && rightSelectedLine.y >= 1)) {
			UTLog.println(true, "DBG__________________________________________");
			UTLog.println(true, "[USG] PLEASE SELECT BOTH CODE BLOCKS.");
			UTLog.println(true, "DBG__________________________________________");
			return false;
		}
		$ = true;
		UTLog.init(UTCfg.getInst().readConfig().LOG4JPATH + "logSubTrees_" + //
				UTTime.curTime() + ".html");
		return true;
	}

	public ISourceViewer getLeftSRViewer() {
		return leftSRViewer;
	}

	public ISourceViewer getRightSRViewer() {
		return rightSRViewer;
	}

}
