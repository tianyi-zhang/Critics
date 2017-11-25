/**
 * Copyright (c) 2017, UCLA Software Engineering and Analysis Laboratory (SEAL)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
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
