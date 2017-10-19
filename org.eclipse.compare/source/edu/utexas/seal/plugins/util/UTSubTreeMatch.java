/*
 * @(#) Parm.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.util;

import rted.distance.RTEDInfoSubTree;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import edu.utexas.seal.plugins.ast.UTSourceFragment;

/**
 * @author Myoungkyu Song
 * @date Nov 29, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public abstract class UTSubTreeMatch {
	protected UTSourceFragment	sourceFragment;

	public abstract void callback(RTEDInfoSubTree t, String label, String value, int postorderId, //
			Double similarityScore, int similarityScoreOfValue, Double similarityScoreOfLabel, //
			Double dist, String hlink, boolean isPrintable);

	public void javaCompilationHandler(JavaCompilation javaCompilation, Node node) {
	}
}
