/*
 * @(#) CriticsListViewerData.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.overlay.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import rted.distance.RTEDInfoSubTree;

/**
 * @author Myoungkyu Song
 * @date February 10, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsTableViewerDataList {
	List<CriticsCBTreeNode>	elements	= new ArrayList<CriticsCBTreeNode>();

	public CriticsTableViewerDataList(List<RTEDInfoSubTree> list) {
		for (RTEDInfoSubTree iSubTreeInfo : list) {
			Node iNode = iSubTreeInfo.getSubTree();
			CriticsCBTreeNode iElement = new CriticsCBTreeNode(iNode.getLabel().name(), CriticsCBTreeElemType.BLOCK);
			iElement.setSubtreeInfo(iSubTreeInfo);
			elements.add(iElement);
		}
	}

	public List<CriticsCBTreeNode> getDataElements() {
		return Collections.unmodifiableList(elements);
	}
}
