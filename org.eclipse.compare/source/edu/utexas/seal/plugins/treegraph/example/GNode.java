/*
 * @(#) MyNode.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.treegraph.example;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Myoungkyu Song
 * @date Dec 18, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class GNode {
	private final String	id;
	private final String	name;
	private List<GNode>	connections;

	public GNode(String id, String name) {
		this.id = id;
		this.name = name;
		this.connections = new ArrayList<GNode>();
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<GNode> getConnectedTo() {
		return connections;
	}
}
