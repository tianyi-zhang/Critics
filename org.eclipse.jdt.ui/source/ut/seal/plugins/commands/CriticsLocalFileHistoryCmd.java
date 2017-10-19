/*
 * @(#) CriticsLocalFileHistory.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package ut.seal.plugins.commands;

import java.util.List;

import ut.seal.plugins.utils.UTLocalFileHistory;

/**
 * @author Myoungkyu Song
 * @date Oct 19, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsLocalFileHistoryCmd implements ICriticsCmd {
	List<String>	localFileHistoryList	= null;

	/**
	 * 
	 */
	public Object getResult() {
		getLocalFileHistory();
		return localFileHistoryList;
	}

	/**
	 * 
	 */
	public void getLocalFileHistory() {
		localFileHistoryList = UTLocalFileHistory.getLocalFileHistoryList();
	}

	/**
	 * 
	 */
	public void printLocalFileHistory() {
		for (int i = 0; i < localFileHistoryList.size(); i++) {
			String elem = localFileHistoryList.get(i);
			System.out.println(elem);
		}
	}

}
