/*
 * @(#) NodeForStmt.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package ch.uzh.ifi.seal.changedistiller.treedifferencing;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;

/**
 * @author Myoungkyu Song
 * @date Feb 13, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class NodeForStmt {
	public List<Node>	forInit	= new ArrayList<Node>();
	public List<Node>	forIncr	= new ArrayList<Node>();

	public NodeForStmt() {
	}

	public NodeForStmt(Node aForStmt) {
		int curlevel = aForStmt.getLevel();
		Enumeration<?> e = aForStmt.breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			int iLevel = iNode.getLevel();
			if (iLevel - curlevel > 2) {
				break;
			}
			if (iLevel - curlevel == 1) {
				if (iNode.getEntity().getType() == JavaEntityType.FOR_INIT) {
					forInit.add(iNode);
				} else if (iNode.getEntity().getType() == JavaEntityType.FOR_INCR) {
					forIncr.add(iNode);
				}
			}
		}
	}

	public String toString() {
		String result = "";
		if (forInit != null)
			result += forInit.toString() + "  ";
		if (forIncr != null)
			result += forIncr.toString() + "  ";
		return result;
	}
}
