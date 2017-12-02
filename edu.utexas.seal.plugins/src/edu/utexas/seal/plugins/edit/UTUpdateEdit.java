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
package edu.utexas.seal.plugins.edit;

import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Point;

import ch.uzh.ifi.seal.changedistiller.model.entities.Move;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.model.entities.Update;
import edu.utexas.seal.plugins.util.UTCriticsTextSelection;
import edu.utexas.seal.plugins.util.root.UTCriticsLocation;

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
