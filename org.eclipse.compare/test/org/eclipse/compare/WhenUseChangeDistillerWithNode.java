/*
 * @(#) WhenUseChangeDistillerWithNode.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package org.eclipse.compare;

import org.junit.Before;
import org.junit.Test;

import ut.seal.plugins.utils.change.UTChangeDistiller;

/**
 * @author Myoungkyu Song
 * @date Dec 8, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class WhenUseChangeDistillerWithNode extends WhenBase {

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
	public void testChangeDistillerWithNodes() {
		UTChangeDistiller cd = new UTChangeDistiller();
		cd.diff(n1.copy(), n7.copy());
		cd.printChanges();

		// UTChangeDistillerStatic.diff(n8, n8);
		// queryTreeOldRev = n1;
		// queryTreeNewRev = n7;
		//
		// UTChangeDistiller changeDistiller = new UTChangeDistiller() {
		// @Override
		// public void diff() {
		// List<Node> restoreNode = diff(queryTreeOldRev, queryTreeNewRev);
		// queryTreeOldRev = restoreNode.get(0);
		// queryTreeNewRev = restoreNode.get(1);
		// }
		// };
		// changeDistiller.diff();
		// List<SourceCodeChange> changes = changeDistiller.getChanges();
		// changeDistiller.print(changes);
		//
	}
}
