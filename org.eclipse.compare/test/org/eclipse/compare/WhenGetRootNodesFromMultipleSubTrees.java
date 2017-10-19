/*
 * @(#) WhenGetRootNodesFromMultipleSubTrees.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package org.eclipse.compare;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import edu.utexas.seal.plugins.util.UTCriticsNode;

/**
 * @author Myoungkyu Song
 * @date Dec 7, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class WhenGetRootNodesFromMultipleSubTrees extends WhenBase {

	@Before
	public void setup() {
		init();
	}

	@Test
	public void testIsChild() {
		n0.add(n1);
		n0.add(n4);
		n1.add(n2);
		n1.add(n3);
		n4.add(n5);
		n4.add(n6);
		n6.add(n7);
		nodes.add(n1);
		nodes.add(n2);
		nodes.add(n3);
		nodes.add(n4);
		nodes.add(n5);
		nodes.add(n6);

		// UTCriticsNode utNode = new UTCriticsNode();
		boolean bf1 = n0.hasChild(n1);
		assertEquals(bf1, true);
	}

	@Test
	public void testGetRootNodes() {
		n0.add(n1);
		n0.add(n4);
		n1.add(n2);
		n1.add(n3);
		n4.add(n5);
		n4.add(n6);
		n6.add(n7);
		nodes.add(n1);
		nodes.add(n2);
		nodes.add(n3);
		nodes.add(n4);
		nodes.add(n5);
		nodes.add(n6);

		UTCriticsNode utNode = new UTCriticsNode();
		List<Node> rootNodes = utNode.getUpperNodes(nodes);
		print(rootNodes);
		assertEquals(2, rootNodes.size());
	}

	@Test
	public void testGetRootNodesWithOneSkip() {
		n0.add(n1);
		n0.add(n4);
		n1.add(n2);
		n1.add(n3);
		n4.add(n5);
		n4.add(n6);
		n6.add(n7);
		nodes.add(n1);
		nodes.add(n2);
		nodes.add(n3);
		nodes.add(n4);
		nodes.add(n5);
		// nodes.add(n6);
		nodes.add(n7);

		UTCriticsNode utNode = new UTCriticsNode();
		List<Node> rootNodes = utNode.getUpperNodes(nodes);
		print(rootNodes);
		assertEquals(2, rootNodes.size());
	}

	@Test
	public void testGetRootNodesWithOneSkip2() {
		n0.add(n1);
		n0.add(n4);
		n1.add(n2);
		n1.add(n3);
		n4.add(n5);
		n4.add(n6);
		n6.add(n7);
		nodes.add(n1);
		nodes.add(n2);
		nodes.add(n3);
		// nodes.add(n4);
		nodes.add(n5);
		// nodes.add(n6);
		nodes.add(n7);

		UTCriticsNode utNode = new UTCriticsNode();
		List<Node> rootNodes = utNode.getUpperNodes(nodes);
		print(rootNodes);
		assertEquals(3, rootNodes.size());
	}

	public void print(List<Node> aNodes) {
		for (int i = 0; i < aNodes.size(); i++) {
			Node elem = aNodes.get(i);
			System.out.println(elem);
		}
		System.out.println("------------------------------------------");
	}
}
