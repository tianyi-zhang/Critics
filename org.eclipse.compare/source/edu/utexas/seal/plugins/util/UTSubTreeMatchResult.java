/*
 * @(#) UTParmClient.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.util;

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
