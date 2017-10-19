/*
 * @(#) UTChangeInfo.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.util;

import org.eclipse.jface.text.Position;

/**
 * @author Myoungkyu Song
 * @date Oct 23, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTChangeInfo {
	public String	changeType;
	public Position	rightPos;
	public Position	leftPos;

	public UTChangeInfo(String changeType, Position rightPos, Position leftPos) {
		this.changeType = changeType;
		this.rightPos = rightPos;
		this.leftPos = leftPos;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UTChangeInfo) {
			UTChangeInfo objChangeInfo = (UTChangeInfo) obj;
			if (this.changeType.equals(objChangeInfo.changeType) && //
					this.rightPos.equals(objChangeInfo.rightPos) && //
					this.leftPos.equals(objChangeInfo.leftPos)) {
				return true;
			}
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return this.changeType + ", " + this.rightPos + ", " + leftPos;
	}
}
