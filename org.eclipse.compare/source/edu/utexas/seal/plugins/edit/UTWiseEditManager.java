/*
 * @(#) UTWiseEdit.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.edit;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.compare.internal.MergeSourceViewer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Point;

import ch.uzh.ifi.seal.changedistiller.model.entities.Move;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.model.entities.Update;
import edu.utexas.seal.plugins.util.UTCriticsTextSelection;

/**
 * @author Myoungkyu Song
 * @date Oct 28, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTWiseEditManager {
	private MergeSourceViewer		leftMSViewer		= null;
	private MergeSourceViewer		rightMSViewer		= null;
	private ISourceViewer			leftSRViewer		= null;
	private ISourceViewer			rightSRViewer		= null;
	private Point					leftSelectedRegion	= null;
	private Point					rightSelectedRegion	= null;
	private int						editId				= 0;
	private List<UTAddEdit>			additionEditManager	= new ArrayList<UTAddEdit>();
	private List<UTRemoveEdit>		removeEditManager	= new ArrayList<UTRemoveEdit>();
	private List<UTUpdateEdit>		updateEditManager	= new ArrayList<UTUpdateEdit>();
	private List<UTAbstractEdit>	editManagerList		= new ArrayList<UTAbstractEdit>();

	public UTWiseEditManager() {
		leftMSViewer = UTCriticsTextSelection.leftMergeSourceViewer;
		rightMSViewer = UTCriticsTextSelection.rightMergeSourceViewer;
		leftSRViewer = leftMSViewer.getSourceViewer();
		rightSRViewer = rightMSViewer.getSourceViewer();
		leftSelectedRegion = leftSRViewer.getSelectedRange();
		rightSelectedRegion = rightSRViewer.getSelectedRange();
	}

	public void initiate() {
		editId = 0;
		additionEditManager.clear();
		removeEditManager.clear();
		updateEditManager.clear();
	}

	/**
	 * 
	 * @param leftSelectedRegion
	 * @param changeDistiller
	 * @param change
	 * @param change_bgn
	 * @param change_end
	 */
	public void additional_functionality(SourceCodeChange change) {
		int left_offset_sel_bgn = leftSelectedRegion.x;
		int left_offset_sel_end = left_offset_sel_bgn + leftSelectedRegion.y;
		int editOffsetBgn = change.getChangedEntity().getSourceRange().getStart();
		int editOffsetEnd = change.getChangedEntity().getSourceRange().getEnd();

		if (left_offset_sel_bgn <= editOffsetBgn && editOffsetEnd <= left_offset_sel_end) {
			String addedContext = leftSRViewer.getDocument().get().substring(editOffsetBgn, editOffsetEnd + 1);
			additionEditManager.add(new UTAddEdit(increaseEditId(), change, addedContext));
		}
	}

	public void removed_functionality(SourceCodeChange change) {
		int right_offset_sel_bgn = rightSelectedRegion.x;
		int right_offset_sel_end = right_offset_sel_bgn + rightSelectedRegion.y;
		int editOffsetBgn = change.getChangedEntity().getSourceRange().getStart();
		int editOffsetEnd = change.getChangedEntity().getSourceRange().getEnd();

		if (right_offset_sel_bgn <= editOffsetBgn && editOffsetEnd <= right_offset_sel_end) {
			String removedContext = rightSRViewer.getDocument().get().substring(editOffsetBgn, editOffsetEnd);
			removeEditManager.add(new UTRemoveEdit(increaseEditId(), change, removedContext));
		}
	}

	public void condition_expression_change(SourceCodeChange change) {
		// TODO test
		int leftSelectionBgn = leftSelectedRegion.x;
		int leftSelectionEnd = leftSelectionBgn + leftSelectedRegion.y;
		int rightSelectionBgn = rightSelectedRegion.x;
		int rightSelectionEnd = rightSelectionBgn + rightSelectedRegion.y;

		SourceCodeEntity newEntity = null;
		SourceCodeEntity changedEntity = change.getChangedEntity();

		if (change instanceof Update) {
			Update update = (Update) change;
			newEntity = update.getNewEntity();
		} else if (change instanceof Move) {
			Move move = (Move) change;
			newEntity = move.getNewEntity();
		}
		int leftNewEditOffsetBgn = newEntity.getSourceRange().getStart();
		int leftNewEditOffsetEnd = newEntity.getSourceRange().getEnd();
		int rightOldEditOffsetBgn = changedEntity.getSourceRange().getStart();
		int rightOldEditOffsetEnd = changedEntity.getSourceRange().getEnd();

		if (leftSelectionBgn <= leftNewEditOffsetBgn && leftNewEditOffsetEnd <= leftSelectionEnd && //
				rightSelectionBgn <= rightOldEditOffsetBgn && rightOldEditOffsetEnd <= rightSelectionEnd) //
		{
			String leftEditContext = leftSRViewer.getDocument().get().substring(leftNewEditOffsetBgn, leftNewEditOffsetEnd);
			String rightEditContext = rightSRViewer.getDocument().get().substring(rightOldEditOffsetBgn, rightOldEditOffsetEnd);
			updateEditManager.add(new UTUpdateEdit(increaseEditId(), change, leftEditContext, rightEditContext));
		}
	}

	public int increaseEditId() {
		return editId++;
	}

	public List<? extends UTAbstractEdit> getAddEditManager() {
		return additionEditManager;
	}

	public List<? extends UTAbstractEdit> getRemoveEditManager() {
		return removeEditManager;
	}

	public List<? extends UTAbstractEdit> getUpdateEditManager() {
		return updateEditManager;
	}

	public List<UTAbstractEdit> getEditManagerList() {
		for (int i = 0; i < additionEditManager.size(); i++) {
			UTAbstractEdit elem = additionEditManager.get(i);
			editManagerList.add(elem);
		}
		for (int i = 0; i < removeEditManager.size(); i++) {
			UTAbstractEdit elem = removeEditManager.get(i);
			editManagerList.add(elem);
		}
		for (int i = 0; i < updateEditManager.size(); i++) {
			UTAbstractEdit elem = updateEditManager.get(i);
			editManagerList.add(elem);
		}
		return editManagerList;
	}

	public void printEditManagerList() {
		List<UTAbstractEdit> editManagerList = getEditManagerList();
		for (int i = 0; i < editManagerList.size(); i++) {
			UTAbstractEdit elem = editManagerList.get(i);
			System.out.println(elem);
			System.out.println("DBG__________________________________________");
		}
	}
}
