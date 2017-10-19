/*
 * @(#) SourceCodeRange.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.crystal.internal;

import java.io.Serializable;

@SuppressWarnings("rawtypes")
public class SourceCodeRange implements Serializable, Comparable {

	private static final long	serialVersionUID	= 1L;
	public int					startPosition;
	public int					length;

	public static SourceCodeRange getDefaultScr() {
		return new SourceCodeRange(0, 0);
	}

	public SourceCodeRange(SourceCodeRange scr) {
		this.startPosition = scr.startPosition;
		this.length = scr.length;
	}

	public SourceCodeRange(int startPosition, int length) {
		this.startPosition = startPosition;
		this.length = length;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof SourceCodeRange))
			return false;
		SourceCodeRange other = (SourceCodeRange) obj;
		if (this.startPosition != other.startPosition)
			return false;
		if (this.length != other.length)
			return false;
		return true;
	}

	public int hashCode() {
		return this.startPosition * 1000 + this.length;
	}

	public boolean isInside(SourceCodeRange other) {
		return startPosition >= other.startPosition && length <= other.length;
	}

	public String toString() {
		return "start position = " + this.startPosition + "  length = " + this.length;
	}

	/**
	 * This method does not handle the containment relations between two objects
	 */
	@Override
	public int compareTo(Object obj) {
		SourceCodeRange other = (SourceCodeRange) obj;
		if (this.startPosition < other.startPosition && this.startPosition + this.length < other.startPosition + other.length)
			return -1;
		if (this.startPosition > other.startPosition && this.startPosition + this.length > other.startPosition + other.length)
			return 1;
		return 0;
	}

}
