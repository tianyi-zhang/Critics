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
package edu.utexas.seal.plugins.util.root;

import java.io.File;
import java.util.List;

import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import rted.distance.RTEDInfoSubTree;
import rted.util.UTSort;
import ut.seal.plugins.utils.UTLog;
import ut.seal.plugins.utils.UTStr;

/**
 * @author Myoungkyu Song
 * @date Nov 29, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTSubTreeMatchResult {
	/**
	 * 
	 * @param subTrees
	 */
	public static void computeSimilarity(List<RTEDInfoSubTree> subTrees) {
		// for (int i = 0; i < subTrees.size(); i++) {
		// computeSimilarity(subTrees.get(i));
		// }
		UTSort.sortBySimilarityScore(subTrees);
	}

	/**
	 * 
	 * @param subTree
	 */
	static void computeSimilarity(RTEDInfoSubTree subTree) {
		/*
		UTCriticsNode utNode = new UTCriticsNode();
		List<int[]> editMapping = subTree.getEditMapping();
		Map<Integer, Node> queryTreeIDMngr = subTree.getQueryTreeIDMngr();
		Map<Integer, Node> subTreeIDMngr = subTree.getSubTreeIDMngr();
		Double similarityScoreOfLabel = 0.0D;
		int similarityScoreOfValue = 0;
		for (int[] nodeAlignment : editMapping) {
			Node qNode = queryTreeIDMngr.get(nodeAlignment[0]);
			Node tNode = subTreeIDMngr.get(nodeAlignment[1]);
			if (qNode != null && tNode != null) {
				if (qNode.getLabel().name().equals(tNode.getLabel().name())) {
					similarityScoreOfLabel++;
				}
				similarityScoreOfValue += DistanceByLevenshtein.compute(qNode.getValue(), tNode.getValue());
			} else if (qNode != null) {
				similarityScoreOfValue += qNode.getValue().length();
			} else if (tNode != null) {
				similarityScoreOfValue += tNode.getValue().length();
			}
		}
		subTree.setSimilarityScoreOfLabel(1 - similarityScoreOfLabel / Double.valueOf(utNode.sizeOfSubTree(subTree.getQueryTree())));
		subTree.setSimilarityScoreOfValue(similarityScoreOfValue);
		subTree.setSimilarityScore(Double.valueOf(String.format("%d.%d", Integer.valueOf((int) (subTree.getSimilarityScoreOfLabel() * 100000)), similarityScoreOfValue)));
		*/
	}

	/**
	 * 
	 * @param subTrees
	 * @param isPrintable
	 */
	public static void print(List<RTEDInfoSubTree> subTrees, boolean isPrintable) {
		if (subTrees.isEmpty()) {
			System.out.println("------------------------------------------");
			System.out.println("[DBG] NOT FOUND FOR SUMMARIZATION !!!");
			System.out.println("------------------------------------------");
		} else {
			System.out.println("------------------------------------------");
		}
		for (int i = 0; i < subTrees.size(); i++) {
			RTEDInfoSubTree subTree = subTrees.get(i);
			if (i > 55) {
				isPrintable = false;
			}
			Node rootNode = subTree.getSubTree();
			String label = rootNode.getLabel().name().trim();
			String value = rootNode.getValue().trim();
			int id = subTree.getPostorderID();
			Double score = subTree.getSimilarityScore();
			File filePath = subTree.getFullFilePath();
			int offset = rootNode.getEntity().getSourceRange().getStart();
			String hlink = "<a href=file://" + filePath + ">" + filePath + "</a>";
			String msg = "TID: " + UTStr.getIndentR(id + ", ", 6) //
					+ "SIM: " + UTStr.getIndentR(score + ", ", 10) //
					+ "OFFSET: " + UTStr.getIndentR(offset + ", ", 6) //
					+ ", " + filePath //
					+ "(" + label + ")(" + value + ")";

			String details = ", " + "PATH: " + hlink;
			if (isPrintable) {
				System.out.println("[DBG] " + msg);
				UTLog.println(isPrintable, msg, details);
			}
		}
	}
}
