/*
 * @(#) WhenBase.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package org.eclipse.compare;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import ch.uzh.ifi.seal.changedistiller.model.classifiers.SourceRange;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

/**
 * @author Myoungkyu Song
 * @date Dec 7, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class WhenBase {
	List<Node>	nodes	= new ArrayList<Node>();

	Node		n0		= new Node(JavaEntityType.METHOD, "0");
	Node		n1		= new Node(JavaEntityType.METHOD, "1");
	Node		n2		= new Node(JavaEntityType.METHOD, "2");
	Node		n3		= new Node(JavaEntityType.METHOD, "3");
	Node		n4		= new Node(JavaEntityType.METHOD, "4");
	Node		n5		= new Node(JavaEntityType.METHOD, "5");
	Node		n6		= new Node(JavaEntityType.METHOD, "6");
	Node		n7		= new Node(JavaEntityType.METHOD, "7");
	Node		n8		= new Node(JavaEntityType.METHOD, "8");
	Node		n9		= new Node(JavaEntityType.METHOD, "9");
	Node		n10		= new Node(JavaEntityType.METHOD, "10");
	Node		n11		= new Node(JavaEntityType.METHOD, "11");
	Node		n12		= new Node(JavaEntityType.METHOD, "12");

	public void init() {
		n0.setEntity(new SourceCodeEntity("0", JavaEntityType.METHOD, new SourceRange(0, 1)));
		n1.setEntity(new SourceCodeEntity("1", JavaEntityType.METHOD, new SourceRange(1, 2)));
		n2.setEntity(new SourceCodeEntity("2", JavaEntityType.METHOD, new SourceRange(2, 3)));
		n3.setEntity(new SourceCodeEntity("3", JavaEntityType.METHOD, new SourceRange(3, 4)));
		n4.setEntity(new SourceCodeEntity("4", JavaEntityType.METHOD, new SourceRange(4, 5)));
		n5.setEntity(new SourceCodeEntity("5", JavaEntityType.METHOD, new SourceRange(5, 6)));
		n6.setEntity(new SourceCodeEntity("6", JavaEntityType.METHOD, new SourceRange(6, 7)));
		n7.setEntity(new SourceCodeEntity("7", JavaEntityType.METHOD, new SourceRange(7, 8)));
		n8.setEntity(new SourceCodeEntity("7", JavaEntityType.METHOD, new SourceRange(8, 9)));
		n9.setEntity(new SourceCodeEntity("7", JavaEntityType.METHOD, new SourceRange(9, 10)));
		n10.setEntity(new SourceCodeEntity("7", JavaEntityType.METHOD, new SourceRange(10, 11)));
		n11.setEntity(new SourceCodeEntity("7", JavaEntityType.METHOD, new SourceRange(11, 12)));
		n12.setEntity(new SourceCodeEntity("7", JavaEntityType.METHOD, new SourceRange(12, 13)));
	}

	public void add(Node parent, Node... children) {
		for (Node child : children) {
			parent.add(child);
		}
	}

	public void printTree(Node aNode) {
		Enumeration<?> e = aNode.breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			System.out.println("LEVEL[" + iNode.getLevel() + "] " + iNode.getValue());
		}
		System.out.println("------------------------------------------");
	}
}
