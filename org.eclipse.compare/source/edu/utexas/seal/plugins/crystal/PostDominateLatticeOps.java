/*
 * @(#) PostDominateLatticeOps.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.crystal;

import edu.cmu.cs.crystal.simple.SimpleLatticeOperations;
import edu.utexas.seal.plugins.crystal.internal.DominateLE;

public class PostDominateLatticeOps extends SimpleLatticeOperations<DominateLE> {
	@Override
	public boolean atLeastAsPrecise(DominateLE left, DominateLE right) {
		return left.ranges.containsAll(right.ranges);
	}

	@Override
	public DominateLE bottom() {
		return DominateLE.bottom();
	}

	@Override
	public DominateLE copy(DominateLE original) {
		return new DominateLE(original);
	}

	/**
	 * Get the intersection between two DominateLE as the join function
	 */
	@Override
	public DominateLE join(DominateLE left, DominateLE right) {
		DominateLE copy = new DominateLE(left);
		copy.ranges.retainAll(right.ranges);
		return copy;
	}
}
