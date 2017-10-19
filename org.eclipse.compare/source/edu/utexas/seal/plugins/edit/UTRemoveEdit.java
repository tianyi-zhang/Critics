/*
 * @(#) UTDeletionEdit.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.edit;

import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;

/**
 * @author Myoungkyu Song
 * @date Oct 28, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTRemoveEdit extends UTAbstractEdit {
	/**
	 * 
	 * @param editId
	 * @param editOffsetBgn
	 * @param editOffsetEnd
	 * @param editOperation
	 * @param editContext
	 */
	public UTRemoveEdit(int editId, SourceCodeChange rightEditChange, String editContext) {
		this.editId = editId;
		this.editKind = rightEditChange.getChangeType().toString();
		// old version
		this.rightEditChange = rightEditChange;
		this.rightEditOffsetBgn = rightEditChange.getChangedEntity().getSourceRange().getStart();
		this.rightEditOffsetEnd = rightEditChange.getChangedEntity().getSourceRange().getEnd();
		this.rightEditStatement = rightEditChange.getChangedEntity().getUniqueName();
		this.rightEditContext = editContext;
	}
}
