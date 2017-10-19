/*
 * @(#) TestTreeMatch.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package rted.datastructure.manager;

import org.junit.Test;

import ch.uzh.ifi.seal.changedistiller.treedifferencing.matching.measure.LevenshteinSimilarityCalculator;
import rted.util.DistanceByLevenshtein;

/**
 * @author Myoungkyu Song
 * @date Feb 2, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class TestTreeMatch {
	String	str1	= "String $v2 = aABCName;";
	String	str2	= "String $v2 = aName;";

	// @Test
	public void testLevenshteinSimilarityCalculator() {
		LevenshteinSimilarityCalculator lsc = new LevenshteinSimilarityCalculator();
		double calculateSimilarity = lsc.calculateSimilarity(str1, str2);
		System.out.println("[DBG] LevenshteinSimilarityCalculator1: " + calculateSimilarity);
		System.out.println("------------------------------------------");

		lsc = new LevenshteinSimilarityCalculator();
		calculateSimilarity = lsc.calculateSimilarity(str1, str1);
		System.out.println("[DBG] LevenshteinSimilarityCalculator2: " + calculateSimilarity);
		System.out.println("------------------------------------------");
	}

	@Test
	public void testDistanceByLevenshtein() {
		str1 = "String userElement:userDataFOREACH_STATEMENT";
		str2 = "String userAAAA:usersFOREACH_STATEMENT";

		int compute = DistanceByLevenshtein.compute(str1, str2);
		System.out.println("[DBG] DistanceByLevenshtein1: " + compute);
		System.out.println("------------------------------------------");

		str1 = "String userElement:userDataFOREACH_STATEMENT";
		str2 = "String user:BBBBusersBCDEFggggFOREACH_STATEMENT";

		compute = DistanceByLevenshtein.compute(str1, str2);
		System.out.println("[DBG] DistanceByLevenshtein2: " + compute);
		System.out.println("------------------------------------------");
	}
}
