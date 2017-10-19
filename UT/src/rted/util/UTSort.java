/*
 * @(#) UTSort.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package rted.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rted.distance.RTEDInfoSubTree;

/**
 * @author Myoungkyu Song
 * @date Nov 12, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTSort {
	/**
	 * 
	 * @param list
	 */
	public static void sortByTreeEditDistance(List<RTEDInfoSubTree> list) {
		Collections.sort(list, new Comparator<RTEDInfoSubTree>() {
			@Override
			public int compare(RTEDInfoSubTree o1, RTEDInfoSubTree o2) {
				return o1.getDistance().compareTo(o2.getDistance());
			}
		});
	}

	/**
	 * 
	 * @param list
	 */
	public static void sortBySimilarityScore(List<RTEDInfoSubTree> list) {
		Collections.sort(list, new Comparator<RTEDInfoSubTree>() {
			@Override
			public int compare(RTEDInfoSubTree o1, RTEDInfoSubTree o2) {
				return o1.getSimilarityScore().compareTo(o2.getSimilarityScore());
			}
		});
	}

	/**
	 * 
	 * @param list
	 */
	public static void sortBySimilarityScoreOfLabel(List<RTEDInfoSubTree> list) {
		/*
		Collections.sort(list, new Comparator<RTEDInfoSubTree>() {
			@Override
			public int compare(RTEDInfoSubTree o1, RTEDInfoSubTree o2) {
				return o1.getSimilarityScoreOfLabel().compareTo(o2.getSimilarityScoreOfLabel());
			}
		});
		*/
	}

	/**
	 * 
	 * @param list
	 */
	public static void sortBySimilarityScoreOfValue(List<RTEDInfoSubTree> list) {
		/*
		Collections.sort(list, new Comparator<RTEDInfoSubTree>() {
			@Override
			public int compare(RTEDInfoSubTree o1, RTEDInfoSubTree o2) {
				return o1.getSimilarityScoreOfValue() - o2.getSimilarityScoreOfValue();
			}
		});
		*/
	}
}
