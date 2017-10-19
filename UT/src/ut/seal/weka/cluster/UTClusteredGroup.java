/*
 * @(#) ClusteredGroup.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package ut.seal.weka.cluster;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Myoungkyu Song
 * @date Nov 30, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTClusteredGroup {
	int				id;
	List<Object>	group	= new ArrayList<Object>();

	public UTClusteredGroup(int id, List<Object> group) {
		this.id = id;
		this.group = group;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the group
	 */
	public List<Object> getGroup() {
		return group;
	}

	/**
	 * @param group
	 *            the group to set
	 */
	public void setGroup(List<Object> group) {
		this.group = group;
	}
}