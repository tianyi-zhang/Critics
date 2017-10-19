/*
 * @(#) WhenOvelayTwoLayer.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package org.eclipse.compare;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ch.uzh.ifi.seal.changedistiller.model.classifiers.SourceRange;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

/**
 * @author Myoungkyu Song
 * @date Jan 18, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class WhenOvelayTwoLayer {
	List<Node>		baseNodeList	= new ArrayList<Node>();
	List<Node>		insertNodeList	= new ArrayList<Node>();
	List<Node>		deleteNodeList	= new ArrayList<Node>();
	JavaEntityType	t				= JavaEntityType.METHOD_INVOCATION;

	@Before
	public void initiate() {
		Node[] nodesA = { new Node(t, "a1"), new Node(t, "a2"), new Node(t, "a3"), new Node(t, "a4"), new Node(t, "a5") };
		for (int i = 0; i < nodesA.length; i++) {
			Node iNode = nodesA[i];
			iNode.setEntity(new SourceCodeEntity(iNode.getValue(), //
					JavaEntityType.METHOD_INVOCATION, new SourceRange((i + 1) * 10, (i + 1) * 10 + 5)));
			baseNodeList.add(iNode);
			// System.out.println("[DBG] " + iNode.getValue() + " " + iNode.getEntity().getStartPosition() + ", " + iNode.getEntity().getEndPosition());
		}
		Node[] nodesB = { new Node(t, "b1"), new Node(t, "b2"), new Node(t, "b3"), new Node(t, "b4"), new Node(t, "b5") };
		for (int i = 0; i < nodesB.length; i++) {
			Node iNode = nodesB[i];
			iNode.setEntity(new SourceCodeEntity(iNode.getValue(), //
					JavaEntityType.METHOD_INVOCATION, new SourceRange((i + 1) * 10 + 3, (i + 1) * 10 + 5)));
			insertNodeList.add(iNode);
			// System.out.println("[DBG] " + iNode.getValue() + " " + iNode.getEntity().getStartPosition() + ", " + iNode.getEntity().getEndPosition());
		}
		Arrays.copyOf(nodesA, nodesA.length);
	}

	@Test
	public void testWhenOverlayTwoLayer() {
		for (int i = 0; i < baseNodeList.size(); i++) {
			Node iNode = baseNodeList.get(i);
			System.out.println(iNode.getValue() + " " + iNode.getEntity().getStartPosition());
		}
		System.out.println("------------------------------------------");
		for (int i = 0; i < insertNodeList.size(); i++) {
			Node iNode = insertNodeList.get(i);
			System.out.println(iNode.getValue() + " " + iNode.getEntity().getStartPosition());
		}
		System.out.println("------------------------------------------");
		// int startIndex = 0;
		// for (int i = 0; i < baseNodeList.size(); i++) {
		// Node iNode = baseNodeList.get(i);
		// int iPos = iNode.getEntity().getStartPosition();
		// System.out.println("[DBG1] iPos: " + iPos);
		//
		// for (int j = startIndex; j < insertNodeList.size(); j++) {
		// Node jNode = insertNodeList.get(j);
		// int jPos = jNode.getEntity().getStartPosition();
		// System.out.println("[DBG2] jPos: " + jPos);
		//
		// if (iPos > jPos) {
		// baseNodeList.add(i, jNode);
		// startIndex++;
		// break;
		// }
		// }
		// }
		overlayTwoList(baseNodeList, insertNodeList);

		for (int i = 0; i < baseNodeList.size(); i++) {
			Node iNode = baseNodeList.get(i);
			System.out.println(iNode.getValue() + " " + iNode.getEntity().getStartPosition());
		}
		System.out.println("------------------------------------------");
	}

	public void overlayTwoList(List<Node> aBaseNodeList, List<Node> aInsertNodeList) {
		int startIndex = 0;
		for (int i = 0; i < aBaseNodeList.size(); i++) {
			Node iNode = aBaseNodeList.get(i);
			int iPos = iNode.getEntity().getStartPosition();

			for (int j = startIndex; j < aInsertNodeList.size(); j++) {
				Node jNode = aInsertNodeList.get(j);
				int jPos = jNode.getEntity().getStartPosition();

				if (iPos > jPos) {
					aBaseNodeList.add(i, jNode);
					startIndex++;
					break;
				}
			}
		}
	}
}
