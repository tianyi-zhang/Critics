/*
 * @(#) WhenInsertNode.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package org.eclipse.compare;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

import rted.distance.RTEDInfoSubTree;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import edu.utexas.seal.plugins.util.UTCriticsNode;
import edu.utexas.seal.plugins.util.UTCriticsTransform;

/**
 * @author Myoungkyu Song
 * @date Dec 7, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class WhenInsertNode extends WhenBase {
	/*
	          ------------------++-------------------++-------------
	  	        new-rev-query   ||    old-rev-query  ||   sub-tree       
	          ------------------++-------------------++-------------
	  	             (7)        ||        (1)        ||     (4)  
	  	             / \        ||        / \        ||     / \  
	               (8) (9)      ||      (2) (3)      ||   (5) (6)
	                |           ||                   ||
	inserted ---> {10}          ||                   ||        
	              /  \          ||                   ||
	inserted -->{11} {12}
	*/
	Map<Integer, Node>	queryTreeOldRevIDMngr	= new TreeMap<Integer, Node>();
	Map<Integer, Node>	subTreeIDMngr			= new TreeMap<Integer, Node>();
	Map<Integer, Node>	queryTreeNewRevIDMngr	= new TreeMap<Integer, Node>();
	LinkedList<int[]>	editMappingSubTree		= new LinkedList<int[]>();
	LinkedList<int[]>	editMappingQueryTree	= new LinkedList<int[]>();

	@Before
	public void setup() {
		init();
		add(n7, n8, n9);
		add(n8, n10);
		add(n10, n11, n12);
		printTree(n7);
		
		n1.add(n2);
		n1.add(n3);
		printTree(n1);

		n4.add(n5);
		n4.add(n6);
		printTree(n4);

		nodes.add(n10);
		nodes.add(n11);
		nodes.add(n12);

		int[] e14 = { 1, 4 };
		editMappingSubTree.add(e14);
		int[] e25 = { 2, 5 };
		editMappingSubTree.add(e25);
		int[] e36 = { 3, 6 };
		editMappingSubTree.add(e36);

		int[] e17 = { 1, 7 };
		editMappingQueryTree.add(e17);
		int[] e28 = { 2, 8 };
		editMappingQueryTree.add(e28);
		int[] e39 = { 3, 9 };
		editMappingQueryTree.add(e39);

		queryTreeOldRevIDMngr.put(1, n1);
		queryTreeOldRevIDMngr.put(2, n2);
		queryTreeOldRevIDMngr.put(3, n3);

		subTreeIDMngr.put(4, n4);
		subTreeIDMngr.put(5, n5);
		subTreeIDMngr.put(6, n6);

		queryTreeNewRevIDMngr.put(7, n7);
		queryTreeNewRevIDMngr.put(8, n8);
		queryTreeNewRevIDMngr.put(9, n9);
	}

	/*
	          ------------------++-------------------++-------------
	            new-rev-query   ||    old-rev-query  ||   sub-tree       
	          ------------------++-------------------++-------------
	                 (7)        ||        (1)        ||     (4)  
	                 / \        ||        / \        ||     / \  
	               (8) (9)      ||      (2) (3)      ||   (5) (6)
	                |           ||                   ||
	inserted ---> {10}          ||                   ||        
	              /  \          ||                   ||
	inserted -->{11} {12}
	*/
	@Test
	public void testIsChild() {
		RTEDInfoSubTree subTree = new RTEDInfoSubTree(1, n1, n4, 0d, 0d, null);
		UTCriticsNode utNode = new UTCriticsNode();
		utNode.printMapping(editMappingSubTree, queryTreeOldRevIDMngr, subTreeIDMngr);
		List<Node> refUpperNodes = utNode.getUpperNodes(nodes);

		int szTree = subTree.getSubTree().getSize();
		assertEquals(szTree, 3);

		UTCriticsTransform transform = new UTCriticsTransform();
		for (int[] nodeAlignment : editMappingSubTree) {
			if (transform.areMapped(nodeAlignment)) {

				Node ndQTreeOldRev = queryTreeOldRevIDMngr.get(nodeAlignment[0]);
				Node ndSTreeOldRev = subTreeIDMngr.get(nodeAlignment[1]);
				Node ndQTreeNewRev = getNodeQTreeNewRev(ndQTreeOldRev, editMappingQueryTree, //
						queryTreeOldRevIDMngr, queryTreeNewRevIDMngr);

				insertSubTree(ndSTreeOldRev, ndQTreeNewRev, refUpperNodes);
			}
		}
		printTree(subTree.getSubTree());
		szTree = subTree.getSubTree().getSize();
		assertEquals(szTree, 6);
	}

	public void insertSubTree(Node aNdTarget, Node aNdQTreeNewRev, List<Node> aUpperNodes) {

		if (aNdQTreeNewRev == null)
			return;
		for (int i = 0; i < aUpperNodes.size(); i++) {
			Node iNode = aUpperNodes.get(i);
			Node iParentNode = (Node) iNode.getParent();
			if (aNdQTreeNewRev.eq(iParentNode)) {
				int childIndex = iParentNode.getIndex(iNode);
				Node copiedInsertedNode = iNode.copy();
				aNdTarget.insert(copiedInsertedNode, childIndex);
			}
		}
	}

	Node getNodeQTreeNewRev(Node aNdQTreeOldRev, List<int[]> aEditMappingQTree, //
			Map<Integer, Node> aQTreeOldRevIDMngr, Map<Integer, Node> aQTreeNewRevIDMngr) {
		Node result = null;
		if (aNdQTreeOldRev == null) {
			return result;
		}
		for (int[] nodeAlignment : aEditMappingQTree) {
			int iNdIdOldRev = nodeAlignment[0];
			int iNdIdNewRev = nodeAlignment[1];
			Node iNdQTreeOldRev = aQTreeOldRevIDMngr.get(iNdIdOldRev);
			if (iNdQTreeOldRev != null && aNdQTreeOldRev.eq(iNdQTreeOldRev)) {
				result = aQTreeNewRevIDMngr.get(iNdIdNewRev);
				break;
			}
		}
		return result;
	}

}
