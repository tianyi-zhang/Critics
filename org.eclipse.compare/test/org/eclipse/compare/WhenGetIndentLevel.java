/*
 * @(#) WhenGetIndentLevel.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package org.eclipse.compare;

import java.util.Enumeration;

import org.junit.Test;

import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ut.seal.plugins.utils.UTStr;
import ut.seal.plugins.utils.ast.UTASTNodeConverter;

/**
 * @author Myoungkyu Song
 * @date Jan 2, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class WhenGetIndentLevel {

	@Test
	public void testGetIndentLevel() {
		String path = "/Users/mksong/runtime-Critics/Case3_Old_Insert.Remove/src/pkg1/ClassA.java";
		int startPosition = 299;

		Node ndMethod = UTASTNodeConverter.convertMethod(path, startPosition);
		Enumeration<?> e = ndMethod.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			int iStartPosition = iNode.getEntity().getStartPosition();
			System.out.println("[DBG] " + UTStr.getIndent(iNode, iStartPosition) + iNode.getValue());
		}
	}
}
