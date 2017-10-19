/*
 * @(#) NodeValueArg.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package ch.uzh.ifi.seal.changedistiller.treedifferencing;

/**
 * @author Myoungkyu Song
 * @date Jan 31, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class NodeValueArg {
	int		begin;
	int		end;
	String	argOrg;
	String	argNew;

	public NodeValueArg(int aBegin, int aEnd, String aArgOrg) {
		this.begin = aBegin;
		this.argOrg = aArgOrg;
		this.end = aEnd;
	}

	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public String getArgOrg() {
		return argOrg;
	}

	public void setArgOrg(String argOrg) {
		this.argOrg = argOrg;
	}

	public String getArgNew() {
		return argNew;
	}

	public void setArgNew(String argNew) {
		this.argNew = argNew;
	}

	public String toString() {
		return ("[" + begin + ", " + end + "] (" + argOrg + " <=> " + argNew + ")");
	}
}
