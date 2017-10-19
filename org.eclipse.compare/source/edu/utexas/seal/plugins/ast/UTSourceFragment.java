/*
 * @(#) UTSourceFragment.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.ast;

import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.SourceRange;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

public class UTSourceFragment {
	JavaCompilation	javaCompilation;
	Node			node;
	SourceRange		sourceRange;
	String			fragment;

	/**
	 * @param javaCompilation
	 * @param node
	 * @param sourceRange
	 */
	public UTSourceFragment(JavaCompilation javaCompilation, Node node, SourceRange sourceRange) {
		this.javaCompilation = javaCompilation;
		this.node = node;
		this.sourceRange = sourceRange;
		this.fragment = javaCompilation.getSource().substring(sourceRange.getStart(), sourceRange.getEnd() + 1);
	}

	/**
	 * @return the javaCompilation
	 */
	public JavaCompilation getJavaCompilation() {
		return javaCompilation;
	}

	/**
	 * @param javaCompilation
	 *            the javaCompilation to set
	 */
	public void setJavaCompilation(JavaCompilation javaCompilation) {
		this.javaCompilation = javaCompilation;
	}

	/**
	 * @return the node
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * @param node
	 *            the node to set
	 */
	public void setNode(Node node) {
		this.node = node;
	}

	/**
	 * @return the sourceRange
	 */
	public SourceRange getSourceRange() {
		return sourceRange;
	}

	/**
	 * @param sourceRange
	 *            the sourceRange to set
	 */
	public void setSourceRange(SourceRange sourceRange) {
		this.sourceRange = sourceRange;
	}

	/**
	 * @return the fragment
	 */
	public String getFragment() {
		return fragment;
	}

	/**
	 * @param fragment
	 *            the fragment to set
	 */
	public void setFragment(String fragment) {
		this.fragment = fragment;
	}

}
