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
