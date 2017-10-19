/*
 * @(#) UTDiffAddition.java
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
public class UTAddEdit extends UTAbstractEdit {

	/**
	 * 
	 * @param editId
	 * @param editOffsetBgn
	 * @param editOffsetEnd
	 * @param editKind
	 * @param editContext
	 */
	public UTAddEdit(int editId, SourceCodeChange leftEditChange, String editContext) {
		this.editId = editId;
		this.editKind = leftEditChange.getChangeType().toString();
		// new version
		this.leftEditChange = leftEditChange;
		this.leftEditOffsetBgn = leftEditChange.getChangedEntity().getSourceRange().getStart();
		this.leftEditOffsetEnd = leftEditChange.getChangedEntity().getSourceRange().getEnd();
		this.leftEditStatement = leftEditChange.getChangedEntity().getUniqueName();
		this.leftEditContext = editContext;
	}
}
