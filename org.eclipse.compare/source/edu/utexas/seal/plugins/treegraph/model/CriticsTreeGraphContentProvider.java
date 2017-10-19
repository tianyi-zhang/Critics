/*
 * @(#) ZestNodeContentProvider.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.treegraph.model;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;

import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

/**
 * @author Myoungkyu Song
 * @date Dec 18, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsTreeGraphContentProvider extends ArrayContentProvider implements IGraphEntityContentProvider {

	@Override
	public Object[] getConnectedTo(Object entity) {
		if (entity instanceof Node) {
			Node node = (Node) entity;
			Enumeration<?> eChildren = node.children();
			List<Node> sinks = new ArrayList<Node>();
			while (eChildren.hasMoreElements()) {
				Node iNode = (Node) eChildren.nextElement();
				sinks.add(iNode);
			}
			for(Node decl : node.getDataDependentor()){
				sinks.add(decl);
			}
			return sinks.toArray();
		}
		throw new RuntimeException("[RuntimeException] CriticsNodeContentProvider - Type not supported");
	}

}
