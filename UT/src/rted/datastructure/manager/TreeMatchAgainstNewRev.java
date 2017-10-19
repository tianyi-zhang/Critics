/*
 * @(#) TreeMatchAgainstNewVer.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package rted.datastructure.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import rted.distance.RTEDInfoSubTree;

/**
 * @author Myoungkyu Song
 * @date Dec 5, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class TreeMatchAgainstNewRev extends TreeMatch {
	private TreeConvert				oldQueryTree_T_converter	= new TreeConvert();
	private TreeConvert				newVerTree_converter		= new TreeConvert();
	private List<RTEDInfoSubTree>	subTreesNewVer				= new ArrayList<RTEDInfoSubTree>();

	/**
	 * Instantiates a new tree match against new rev.
	 */
	public TreeMatchAgainstNewRev() {
		mConverterQTree = oldQueryTree_T_converter;
		mConverterTTree = newVerTree_converter;
		mSubTrees = subTreesNewVer;
	}

	/* (non-Javadoc)
	 * @see rted.datastructure.manager.TreeMatch#matchEditMappingEntry(java.lang.Integer[], org.eclipse.jdt.core.dom.CompilationUnit, ch.uzh.ifi.seal.changedistiller.treedifferencing.Node, ch.uzh.ifi.seal.changedistiller.treedifferencing.Node, java.io.File, boolean)
	 */
	public void matchEditMappingEntry(Node aQTreeNewRev, Node aNewVerTree, boolean aTrace) {
		if (isSkip(aQTreeNewRev, aNewVerTree)) {
			return;
		}
		mTrace = aTrace;
		Enumeration<?> e = aNewVerTree.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iTtree = (Node) e.nextElement();
			if (isContinue(aQTreeNewRev, iTtree)) {
				continue;
			}
			cutDownMatchCost(iTtree);
			matchEditMapping(aQTreeNewRev, iTtree);
			iTtree.setFilePath(mFilePath);
			// iTtree.setCompilationUnit(mUnit);
		}
	}

	/* (non-Javadoc)
	 * @see rted.datastructure.manager.TreeMatch#matchEditMappingEntry(java.lang.Integer[], ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation, ch.uzh.ifi.seal.changedistiller.treedifferencing.Node, ch.uzh.ifi.seal.changedistiller.treedifferencing.Node, java.io.File, boolean)
	 */
	public void matchEditMappingEntry(Integer cntMatched[], JavaCompilation compilation, //
			Node qTreeNewRev, Node newVerTree, //
			File fullFilePath, boolean isPrintable) {
		mTrace = isPrintable;
		Enumeration<?> e = newVerTree.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			matchEditMapping(cntMatched, compilation, qTreeNewRev, iNode, fullFilePath);
		}
	}
}
