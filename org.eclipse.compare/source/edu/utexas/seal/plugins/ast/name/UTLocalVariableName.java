/*
 * @(#) UTVLocalariableName.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.ast.name;

/**
 * @author Myoungkyu Song
 * @date Oct 27, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTLocalVariableName extends UTAbstractNodeName {
	private int		indexOfName	= 0;
	private String	selfName	= null;

	public UTLocalVariableName() {
		indexOfName = 0;
	}

	public UTLocalVariableName(String nodeName) {
		indexOfName = 0;
		this.selfName = nodeName;
	}

	@Override
	public String getName() {
		if (haveName()) {
			return super.getName().replace("NODENAME", selfName);
		}
		return super.getName().replace("NODENAME", "v");
	}

	@Override
	public void resetIndexOfName() {
		indexOfName = 0;
	}

	@Override
	public int getIndexOfName() {
		return indexOfName++;
	}

	boolean haveName() {
		return selfName != null;
	}
}
