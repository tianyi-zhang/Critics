/*
 * @(#) TreeMatchBetweenQueryTrees.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package rted.datastructure.manager;

import java.util.Map;
import java.util.TreeMap;

import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

/**
 * @author Myoungkyu Song
 * @date Dec 5, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class TreeMatchBetweenQueryTrees extends TreeMatch {
	private TreeConvert			oldQueryTree_converter;
	private TreeConvert			newQueryTree_converter;

	private Map<Integer, Node>	queryTreeIDMngrOldRev	= new TreeMap<Integer, Node>();
	private Map<Integer, Node>	queryTreeIDMngrNewRev	= new TreeMap<Integer, Node>();

	public TreeMatchBetweenQueryTrees(Node oldQueryTree, Node newQueryTree) {
		oldQueryTree_converter = new TreeConvert();
		newQueryTree_converter = new TreeConvert();
	}

	/**
	 * @param oldQueryTree
	 * @param newQueryTree
	 * 
	 */
	public void matchEditMapping(Node oldQueryTree, Node newQueryTree) {
		trace3(false, oldQueryTree_converter, newQueryTree_converter);
		oldQueryTree_converter.convert(oldQueryTree, queryTreeIDMngrOldRev, false, MatchType.REGULAR);
		newQueryTree_converter.convert(newQueryTree, queryTreeIDMngrNewRev, false, MatchType.REGULAR);
		String oldQueryLblTree = oldQueryTree_converter.getSubLblTree();
		String newQueryLblTree = newQueryTree_converter.getSubLblTree();
		mRtedProc.initiate(oldQueryLblTree, newQueryLblTree, false);
		mRtedProc.match();
		mRtedProc.mapping(queryTreeIDMngrOldRev, queryTreeIDMngrNewRev, false);
	}

	public Map<Integer, Node> getQueryTreeIDMngrOldRev() {
		return queryTreeIDMngrOldRev;
	}

	public Map<Integer, Node> getQueryTreeIDMngrNewRev() {
		return queryTreeIDMngrNewRev;
	}
}
