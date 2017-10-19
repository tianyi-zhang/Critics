/*
 * @(#) UTAbstractEdit.java
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
public abstract class UTAbstractEdit {
	int					editId;
	String				editKind;

	// new version
	SourceCodeChange	leftEditChange;
	int					leftEditOffsetBgn;
	int					leftEditOffsetEnd;
	String				leftEditStatement;
	String				leftEditContext;

	// old version
	SourceCodeChange	rightEditChange;
	int					rightEditOffsetBgn;
	int					rightEditOffsetEnd;
	String				rightEditStatement;
	String				rightEditContext;

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ID: " + editId + ", ");
		sb.append("TYPE: " + editKind + ", ");
		if (leftEditChange != null)
			sb.append(leftEditChange.getChangedEntity().getType() + " " + leftEditChange.toString());
		if (rightEditChange != null)
			sb.append(rightEditChange.getChangedEntity().getType() + " " + rightEditChange.toString());
		return sb.toString();
	}

	public int getEditId() {
		return editId;
	}

	public String getEditKind() {
		return editKind;
	}

	public SourceCodeChange getLeftEditChange() {
		return leftEditChange;
	}

	public int getLeftEditOffsetBgn() {
		return leftEditOffsetBgn;
	}

	public int getLeftEditOffsetEnd() {
		return leftEditOffsetEnd;
	}

	public String getLeftEditStatement() {
		return leftEditStatement;
	}

	public String getLeftEditContext() {
		return leftEditContext;
	}

	public SourceCodeChange getRightEditChange() {
		return rightEditChange;
	}

	public int getRightEditOffsetBgn() {
		return rightEditOffsetBgn;
	}

	public int getRightEditOffsetEnd() {
		return rightEditOffsetEnd;
	}

	public String getRightEditStatement() {
		return rightEditStatement;
	}

	public String getRightEditContext() {
		return rightEditContext;
	}

}
