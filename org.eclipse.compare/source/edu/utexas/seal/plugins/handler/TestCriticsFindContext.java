/*
 * @(#) TestA.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.handler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import edu.utexas.seal.plugins.util.UTCriticsDiffUtil;
import edu.utexas.seal.plugins.util.UTCriticsDiffUtil.Diff;
import edu.utexas.seal.plugins.util.UTCriticsDiffUtil.Patch;

/**
 * @author Myoungkyu Song
 * @date Jan 31, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class TestCriticsFindContext {

	@Test
	public void testDetectAnomalies() {
		List<List<String>> grpMngr1 = new ArrayList<List<String>>();
		List<List<String>> grpMngr2 = new ArrayList<List<String>>();
		List<String> someGrp = new ArrayList<String>();
		List<String> otherGrp = new ArrayList<String>();
		someGrp.add("a");
		someGrp.add("a");
		otherGrp.add("a");
		// 1st group
		grpMngr1.add(someGrp);
		grpMngr2.add(someGrp);
		// 2nd group
		grpMngr1.add(otherGrp);
		grpMngr2.add(otherGrp);
		// 3rd group
		grpMngr1.add(someGrp);
		grpMngr2.add(otherGrp);

		List<String> grp1 = grpMngr1.get(0);
		List<String> grp2 = grpMngr2.get(0);

		if (grp1.size() == grp2.size()) {

		}
	}

	void getNext(List<List<String>> grpMngr1, List<List<String>> grpMngr2, //
			List<String> grp1, List<String> grp2, int index1, int index2) {
		for (int i = 0; i < grp1.size(); i++) {
			grp1 = grpMngr1.get(i);
			grp2 = grpMngr2.get(i);

		}
	}

	// @Test
	public void testGetLocationParameters1() {
		String str1 = "(value: String personName = customerName;)(label: VARIABLE_DECLARATION_STATEMENT)";
		String str2 = "(value: String $v2 = $p0;)(label: VARIABLE_DECLARATION_STATEMENT)";
		UTCriticsDiffUtil match = new UTCriticsDiffUtil();
		LinkedList<Patch> lstPatch = match.patch_make(str1, str2);
		for (int i = 0; i < lstPatch.size(); i++) {
			Patch elem = lstPatch.get(i);

			System.out.println("[DBG] " + elem);

		}
		System.out.println("==========================================");
	}

	// @Test
	public void testGetLocationParameters11() {
		String str1 = "(value: String personName = customerName;)(label: VARIABLE_DECLARATION_STATEMENT)";
		String str2 = "(value: String $v2 = $p0;)(label: VARIABLE_DECLARATION_STATEMENT)";
		UTCriticsDiffUtil match = new UTCriticsDiffUtil();
		LinkedList<Diff> lstPatch = match.diff_main(str1, str2);
		for (int i = 0; i < lstPatch.size(); i++) {
			Diff elem = lstPatch.get(i);

			System.out.println("[DBG] " + elem);

		}
		System.out.println("==========================================");
	}

	// @Test
	public void testGetLocationParameters2() {
		String str1 = "(value: System.err.println(((personName + \", \") + personAddress));)(label: METHOD_INVOCATION)";
		String str2 = "(value: System.err.println((($v2 + \", \") + $v1));)(label: METHOD_INVOCATION)";
		UTCriticsDiffUtil match = new UTCriticsDiffUtil();
		LinkedList<Patch> lstPatch = match.patch_make(str1, str2);
		for (int i = 0; i < lstPatch.size(); i++) {
			Patch elem = lstPatch.get(i);
			System.out.println(elem);
		}
		System.out.println("==========================================");
	}
}
