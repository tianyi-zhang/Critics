/*
 * @(#) UTMatchScore.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package rted.util;

import org.junit.Test;

/**
 * @author Myoungkyu Song
 * @date Nov 25, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTMatchScore {
	public static double compute(String in1, String in2) {
		double v = DistanceByLevenshtein.compute(in1, in2);
		double rho = 0.0D;
		rho = 1 - v / Math.max(in1.length(), in2.length());
		return rho;
	}

	@Test
	public void main() {
		System.out.println("[DBG] " + compute("abc", "abc"));
		System.out.println("[DBG] " + compute("abc", "abxdef"));
		
		System.out.println("[DBG] " + DistanceByLevenshtein.compute("abc", "abc"));
		System.out.println("[DBG] " + DistanceByLevenshtein.compute("abc", "abxdef"));
	}
}
