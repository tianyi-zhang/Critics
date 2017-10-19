/*
 * @(#) TreeMatchAgainstOldVer.java
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

import rted.distance.RTEDInfoSubTree;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

/**
 * @author Myoungkyu Song
 * @date Dec 5, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class TreeMatchAgainstOldRev extends TreeMatch {
	private TreeConvert				oldQueryTree_converter	= new TreeConvert();
	private TreeConvert				oldVerTree_converter	= new TreeConvert();
	private List<RTEDInfoSubTree>	subTreesOldVer			= new ArrayList<RTEDInfoSubTree>();

	/**
	 * Instantiates a new tree match against old rev.
	 */
	public TreeMatchAgainstOldRev() {
		mConverterQTree = oldQueryTree_converter;
		mConverterTTree = oldVerTree_converter;
		mSubTrees = subTreesOldVer;
	}

	/* (non-Javadoc)
	 * @see rted.datastructure.manager.TreeMatch#matchEditMappingEntry(java.lang.Integer[], org.eclipse.jdt.core.dom.CompilationUnit, ch.uzh.ifi.seal.changedistiller.treedifferencing.Node, ch.uzh.ifi.seal.changedistiller.treedifferencing.Node, java.io.File, boolean)
	 */
	public void matchEditMappingEntry(Node aQTreeOldRev, Node aOldVerTree, boolean aTrace) {
		if (aOldVerTree.getEntity().getType().isMethod() && //
				aOldVerTree.getValue().equals("getNextChar")) { // for tracing
			System.out.print("");
		}
		if (isSkip(aQTreeOldRev, aOldVerTree)) {
			return;
		}
		mTrace = aTrace;
		Enumeration<?> e = null;
		e = aOldVerTree.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iTtree = (Node) e.nextElement();
			if (isContinue(aQTreeOldRev, iTtree)) {
				continue;
			}
			cutDownMatchCost(iTtree);
			matchEditMapping(aQTreeOldRev, iTtree);
			iTtree.setFilePath(mFilePath);
			// iTtree.setCompilationUnit(mUnit);
		}
	}

	/* (non-Javadoc)
	 * @see rted.datastructure.manager.TreeMatch#matchEditMappingEntry(java.lang.Integer[], ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation, ch.uzh.ifi.seal.changedistiller.treedifferencing.Node, ch.uzh.ifi.seal.changedistiller.treedifferencing.Node, java.io.File, boolean)
	 */
	public void matchEditMappingEntry(Integer cntMatched[], JavaCompilation compilation, //
			Node qTreeOldRev, Node oldVerTree, //
			File fullFilePath, boolean isPrintable) {
		mTrace = isPrintable;
		Enumeration<?> e = oldVerTree.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			matchEditMapping(cntMatched, compilation, qTreeOldRev, iNode, fullFilePath);
		}
	}

}
