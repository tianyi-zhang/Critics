/*
 * @(#) MyConnection.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.treegraph.example;

/**
 * @author Myoungkyu Song
 * @date Dec 18, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class GConnection {
	final String	id;
	final String	label;
	final GNode	source;
	final GNode	destination;

	public GConnection(String id, String label, GNode source, GNode destination) {
		this.id = id;
		this.label = label;
		this.source = source;
		this.destination = destination;
	}

	public String getLabel() {
		return label;
	}

	public GNode getSource() {
		return source;
	}

	public GNode getDestination() {
		return destination;
	}
}
