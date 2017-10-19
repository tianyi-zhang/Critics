/*
 * @(#) CriticsAbstract.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.handler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.compare.internal.MergeSourceViewer;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Point;

import rted.datastructure.manager.ITreeMatch;
import rted.datastructure.manager.TreeMatchAgainstNewRev;
import rted.datastructure.manager.TreeMatchAgainstOldRev;
import rted.datastructure.manager.TreeMatchBetweenQueryTrees;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.NodePair;
import ut.seal.plugins.utils.UTCfg;
import ut.seal.plugins.utils.UTLog;
import ut.seal.plugins.utils.UTTime;
import ut.seal.plugins.utils.ast.UTASTNodeConverter;
import ut.seal.plugins.utils.ast.UTASTNodeFinder;
import ut.seal.plugins.utils.ast.UTASTNodeOperation;
import ut.seal.plugins.utils.change.UTChangeDistiller;
import edu.utexas.seal.plugins.util.UTCriticsEnable;
import edu.utexas.seal.plugins.util.UTCriticsLocation;
import edu.utexas.seal.plugins.util.UTCriticsPairFileInfo;
import edu.utexas.seal.plugins.util.UTCriticsTextSelection;
import edu.utexas.seal.plugins.util.UTCriticsTransform;
import edu.utexas.seal.plugins.util.UTSubTreeCluster;

/**
 * @author Myoungkyu Song
 * @date Nov 26, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public abstract class CriticsCommonHandler extends AbstractHandler {
	protected boolean						$						= false;
	protected final String					PARM_VAR_NAME			= "$var";

	protected MergeSourceViewer				leftMSViewer			= null;
	protected MergeSourceViewer				rightMSViewer			= null;
	protected ISourceViewer					leftSRViewer			= null;
	protected ISourceViewer					rightSRViewer			= null;
	protected Point							leftSelectedRegion		= null;
	protected Point							rightSelectedRegion		= null;
	protected Point							leftSelectedLine		= null;
	protected Point							rightSelectedLine		= null;
	protected Position						leftDiffPosition		= null;
	protected Position						rightDiffPosition		= null;

	protected UTASTNodeFinder				nodeFinder				= null;
	protected List<ASTNode>					contextASTNodesOldRev	= new ArrayList<ASTNode>();
	protected List<ASTNode>					contextASTNodesNewRev	= new ArrayList<ASTNode>();
	protected UTASTNodeConverter			nodeConverter			= null;
	protected UTASTNodeOperation			nodeOperation			= null;
	protected ITreeMatch					matcherOldRev			= null;
	protected ITreeMatch					matcherNewRev			= null;
	protected TreeMatchBetweenQueryTrees	matcherQueryTree		= null;
	protected UTChangeDistiller				mChangeDistiller		= null;
	protected List<SourceCodeChange>		mChanges				= null;
	protected List<NodePair>				mMatches				= null;
	protected List<SourceCodeChange>		mInsertList				= new ArrayList<SourceCodeChange>();
	protected List<Node>					mInsertNodeList			= new ArrayList<Node>();
	protected List<SourceCodeChange>		mDeleteList				= new ArrayList<SourceCodeChange>();
	protected List<Node>					mDeleteNodeList			= new ArrayList<Node>();
	protected List<SourceCodeChange>		mMoveList				= new ArrayList<SourceCodeChange>();
	protected List<Node>					mMoveNodeList			= new ArrayList<Node>();
	protected List<SourceCodeChange>		mUpdateList				= new ArrayList<SourceCodeChange>();
	protected List<Node>					mUpdateNodeList			= new ArrayList<Node>();
	protected UTSubTreeCluster				clustererOldRev			= null;
	protected UTSubTreeCluster				clustererNewRev			= null;
	protected UTCriticsTransform			transformOldRev			= null;
	protected Node							qTreeOldRev				= null;
	protected Node							qTreeNewRev				= null;
	protected Node							qTreeParGenOldRev		= null;
	protected Node							qTreeAllGenOldRev		= null;
	protected Node							qTreeParGenNewRev		= null;
	protected Node							qTreeAllGenNewRev		= null;
	protected Integer						cntMatchedOldRev[]		= { 1 };
	protected Integer						cntMatchedNewRev[]		= { 1 };
	protected int							cntMethod				= 1;
	protected int							cntClass				= 1;

	protected enum LR {
		LEFT, RIGHT;
	};

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public abstract Object execute(ExecutionEvent event) throws ExecutionException;

	/**
	 * Initiate.
	 * 
	 * @return true, if successful
	 */
	protected boolean initiate() {
		if (UTCriticsPairFileInfo.getLeftIPackageFragment() == null) {
			System.out.println("[USG] PLEASE OPEN A COMPARE VIEW & EDITOR.");
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
		if (leftSelectedRegion.y == 0 || rightSelectedRegion.y == 0) {
			System.out.println("DBG__________________________________________");
			System.out.println("[USG] PLEASE SELECT BOTH CODE BLOCKS.");
			System.out.println("DBG__________________________________________");
			return false;
		}
		leftSelectedLine = UTCriticsLocation.getLineRange(leftSelectedRegion, leftSRViewer.getDocument());
		rightSelectedLine = UTCriticsLocation.getLineRange(rightSelectedRegion, rightSRViewer.getDocument());
		if (UTCriticsEnable.autoSelection) {
			leftDiffPosition = UTCriticsTextSelection.leftDiffPosition;
			rightDiffPosition = UTCriticsTextSelection.rightDiffPosition;
			if (leftDiffPosition.offset < 0 || rightDiffPosition.offset < 0) {
				System.out.println("DBG__________________________________________");
				System.out.println("[USG] PUT THE MOUSE CURSOR, AND PLEASE TRY AGAIN.");
				System.out.println("DBG__________________________________________");
				return false;
			}
		}
		if (leftSelectedRegion.x < 0 || leftSelectedRegion.y < 0) {
			System.out.println("DBG__________________________________________");
			System.out.println("[USG] PUT THE MOUSE CURSOR, AND PLEASE TRY AGAIN.");
			System.out.println("DBG__________________________________________");
			return false;
		}
		if (leftSelectedRegion.x >= 0 && leftSelectedRegion.y == 0 && //
				rightSelectedRegion.x >= 0 && rightSelectedRegion.y == 0 && //
				(leftSelectedLine.x >= 1 && leftSelectedLine.y >= 1) && //
				(rightSelectedLine.x >= 1 && rightSelectedLine.y >= 1)) {
			System.out.println("DBG__________________________________________");
			System.out.println("[USG] PLEASE SELECT BOTH CODE BLOCKS.");
			System.out.println("DBG__________________________________________");
			return false;
		}
		$ = true;

		cntMatchedOldRev[0] = 1;
		cntMatchedNewRev[0] = 1;
		nodeFinder = new UTASTNodeFinder();
		nodeConverter = new UTASTNodeConverter();
		nodeOperation = new UTASTNodeOperation();
		mChangeDistiller = new UTChangeDistiller();
		matcherOldRev = new TreeMatchAgainstOldRev();
		matcherNewRev = new TreeMatchAgainstNewRev();
		matcherQueryTree = new TreeMatchBetweenQueryTrees(qTreeOldRev, qTreeNewRev);
		clustererOldRev = new UTSubTreeCluster();
		clustererNewRev = new UTSubTreeCluster();
		transformOldRev = new UTCriticsTransform(this);

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
