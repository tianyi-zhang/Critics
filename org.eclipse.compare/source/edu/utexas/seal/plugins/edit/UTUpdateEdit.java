/*
 * @(#) UTUpdateEdit.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.edit;

import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Point;

import ch.uzh.ifi.seal.changedistiller.model.entities.Move;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.model.entities.Update;
import edu.utexas.seal.plugins.util.UTCriticsLocation;
import edu.utexas.seal.plugins.util.UTCriticsTextSelection;

/**
 * @author Myoungkyu Song
 * @date Oct 28, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTUpdateEdit extends UTAbstractEdit {
	SourceCodeEntity	newChangeEntity	= null;
	SourceCodeEntity	oldChangeEntity	= null;

	/**
	 * 
	 * @param editId
	 * @param editOperation
	 * @param lparmEditOffsetBgn
	 * @param lparmEditOffsetEnd
	 * @param leftEditContext
	 * @param rparmEditOffsetBgn
	 * @param rparmEditOffsetEnd
	 * @param rightEditContext
	 */
	public UTUpdateEdit(int editId, SourceCodeChange change, String leftEditContext, String rightEditContext) {
		this.editId = editId;
		this.editKind = change.getChangeType().toString();
		oldChangeEntity = change.getChangedEntity();

		if (change instanceof Update) {
			Update update = (Update) change;
			newChangeEntity = update.getNewEntity();
		} else if (change instanceof Move) {
			Move move = (Move) change;
			newChangeEntity = move.getNewEntity();
		}
		// new version
		this.leftEditOffsetBgn = newChangeEntity.getSourceRange().getStart();
		this.leftEditOffsetEnd = newChangeEntity.getSourceRange().getEnd();
		this.leftEditStatement = newChangeEntity.getUniqueName();
		this.leftEditContext = leftEditContext;
		// old version
		this.rightEditChange = change;
		this.rightEditOffsetBgn = oldChangeEntity.getSourceRange().getStart();
		this.rightEditOffsetEnd = oldChangeEntity.getSourceRange().getEnd();
		this.rightEditStatement = oldChangeEntity.getUniqueName();
		this.rightEditContext = rightEditContext;
	}

	String	S	= System.getProperty("line.separator");

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ID: " + editId + ", ");
		sb.append("TYPE: " + editKind + ", ");
		if (oldChangeEntity != null) {
			sb.append(S);
			appendChanges(sb, rightEditOffsetBgn, rightEditOffsetEnd, oldChangeEntity, false);
		}
		if (newChangeEntity != null) {
			sb.append(S + "       <==" + S);
			appendChanges(sb, leftEditOffsetBgn, leftEditOffsetEnd, newChangeEntity, true);
		}
		return sb.toString();
	}

	private void appendChanges(StringBuilder sb, int offsetBgn, int offsetEnd, SourceCodeEntity codeEntity,
			boolean isLeft) {
		Point line = getLineNumber(isLeft);
		String o = "OFFSET(" + offsetBgn + " ~ " + offsetEnd + ")";
		String l = "LINE(" + line.x + " ~ " + line.y + "): ";
		sb.append(o + ", " + l + codeEntity.getType() + " " + codeEntity.getUniqueName());
	}

	private Point getLineNumber(boolean isLeft) {
		IDocument doc = null;
		int offset = 0;
		int len = 0;
		if (isLeft) {
			doc = UTCriticsTextSelection.leftMergeSourceViewer.getSourceViewer().getDocument();
			offset = this.leftEditOffsetBgn;
			len = this.leftEditOffsetEnd - offset;
		} else {
			doc = UTCriticsTextSelection.rightMergeSourceViewer.getSourceViewer().getDocument();
			offset = this.rightEditOffsetBgn;
			len = this.rightEditOffsetEnd - offset;
		}
		return UTCriticsLocation.getLineRange(new Point(offset, len), doc);
	}
}
