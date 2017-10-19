/*
 * @(#) UTASTNodeSearcher.java
 *
 * Copyright 2013 2014 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.crystal.internal;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;

import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

public class UTASTNodeSearcher extends ASTVisitor {
	private SourceCodeRange	range;
	private ASTNode			target	= null;

	public UTASTNodeSearcher(SourceCodeRange range) {
		this.range = range;
	}

	public UTASTNodeSearcher(ASTNode node) {
		this.range = new SourceCodeRange(node.getStartPosition(), node.getLength());
	}

	public UTASTNodeSearcher(Node node) {
		SourceCodeEntity entity = node.getEntity();
		range = new SourceCodeRange(entity.getStartPosition(), entity.getEndPosition() - entity.getStartPosition() + 1);
	}

	@Override
	@SuppressWarnings("unused")
	public void preVisit(ASTNode node) {
		int start = node.getStartPosition();
		int length = node.getLength();
		if ((node.getStartPosition() == range.startPosition) && //
				((node.getLength() == range.length) || (node.getLength() + 1 == range.length))) {
			target = node;
		}
	}

	public ASTNode search(ASTNode node) {
		node.accept(this);
		return this.target;
	}
}
