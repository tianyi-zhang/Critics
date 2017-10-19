/*
 * @(#) CriticsAbstract.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
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
import edu.utexas.seal.plugins.util.UTCriticsEnable;
import edu.utexas.seal.plugins.util.UTCriticsLocation;
import edu.utexas.seal.plugins.util.UTCriticsPairFileInfo;
import edu.utexas.seal.plugins.util.UTCriticsTextSelection;

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
