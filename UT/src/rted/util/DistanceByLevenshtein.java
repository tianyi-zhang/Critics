/*
 * @(#) DistanceByLevenshtein.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package rted.util;

/**
 * @author Myoungkyu Song
 * @date Nov 25, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class DistanceByLevenshtein {
	private static int MIN(int a, int b, int c) {
		return Math.min(Math.min(a, b), c);
	}

	public static int compute(CharSequence str1, CharSequence str2) {
		int[][] distance = new int[str1.length() + 1][str2.length() + 1];
		for (int i = 0; i <= str1.length(); i++) {
			distance[i][0] = i;
		}
		for (int j = 0; j <= str2.length(); j++) {
			distance[0][j] = j;
		}
		for (int i = 1; i <= str1.length(); i++) {
			for (int j = 1; j <= str2.length(); j++)
				distance[i][j] = MIN(distance[(i - 1)][j] + 1, //
						distance[i][(j - 1)] + 1, //
						distance[(i - 1)][(j - 1)] + (str1.charAt(i - 1) == str2.charAt(j - 1) ? 0 : 1));
		}
		return distance[str1.length()][str2.length()];
	}
}
