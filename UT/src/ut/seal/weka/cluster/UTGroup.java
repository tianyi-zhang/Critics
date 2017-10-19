/*
 * @(#) Group.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package ut.seal.weka.cluster;

import weka.core.Instance;

/**
 * @author Myoungkyu Song
 * @date Nov 30, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTGroup {
	private int		id;
	private Object	elem;

	public UTGroup(int id, Instance elem) {
		this.id = id;
		this.elem = elem;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Object getElem() {
		return elem;
	}

	public void setElem(Object elem) {
		this.elem = elem;
	}

	public String toString() {
		return id + ", " + elem;
	}
}