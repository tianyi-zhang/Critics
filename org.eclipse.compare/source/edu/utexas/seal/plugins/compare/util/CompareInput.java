/*
 * @(#) CompareInput.java
 *
 * Copyright 2013 2014 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.compare.util;

import java.util.Date;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Myoungkyu Song
 * @date Jan 23, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CompareInput extends CompareEditorInput {
	CompareItem	ancestor	= null;
	CompareItem	left		= null; // new CompareItem("Left", "new contents", new Date().getTime());
	CompareItem	right		= null; // new CompareItem("Right", "old contents", new Date().getTime());

	public CompareInput() {
		super(new CompareConfiguration());
	}

	public CompareInput(CompareItem left2, CompareItem right2) {
		this();
		ancestor = new CompareItem("Common", "contents", new Date().getTime());
		left = left2;
		right = right2;
		init();
	}

	private void init() {
		getCompareConfiguration().setLeftEditable(true);
		getCompareConfiguration().setRightEditable(true);
		getCompareConfiguration().setLeftLabel(left.getName());
		getCompareConfiguration().setRightLabel(right.getName());
	}

	protected Object prepareInput(IProgressMonitor pm) {
		return new DiffNode(left, right);
	}

	public CompareItem getAncestor() {
		return ancestor;
	}

	public void setAncestor(CompareItem ancestor) {
		this.ancestor = ancestor;
	}

	public CompareItem getLeft() {
		return left;
	}

	public void setLeft(CompareItem left) {
		this.left = left;
	}

	public CompareItem getRight() {
		return right;
	}

	public void setRight(CompareItem right) {
		this.right = right;
	}
}