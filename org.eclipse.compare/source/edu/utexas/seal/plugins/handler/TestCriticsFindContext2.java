/*
 * @(#) TestA.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.handler;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ch.uzh.ifi.seal.changedistiller.treedifferencing.NodeValueArg;
import edu.utexas.seal.plugins.util.UTCriticsDiffUtil;
import edu.utexas.seal.plugins.util.UTCriticsDiffUtil.Diff;
import edu.utexas.seal.plugins.util.UTCriticsDiffUtil.Patch;

/**
 * @author Myoungkyu Song
 * @date Jan 31, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class TestCriticsFindContext2 {

	public void testGetLocationParameters1() {
		System.out.println("[DBG0] TEST 1");
		String str1 = "(value: String personName customerName;)(label: VARIABLE_DECLARATION_STATEMENT)";
		String str2 = "(value: String $v2 $p0;)(label: VARIABLE_DECLARATION_STATEMENT)";

		String updateStr1 = str1; // .replaceAll(" ", delimeter);
		String updateStr2 = str2; // .replaceAll(" ", delimeter);

		UTCriticsDiffUtil match = new UTCriticsDiffUtil();
		LinkedList<Patch> lstPatch = match.patch_make(updateStr1, updateStr2);
		for (int i = 0; i < lstPatch.size(); i++) {
			Patch elem = lstPatch.get(i);
			System.out.println("[DBG] " + updateStr1);
			System.out.println("[DBG] " + elem);

			for (Diff aDiff : elem.diffs) {
				switch (aDiff.operation) {
				case DELETE:
					break;
				default:
					break;
				}
			}
		}
		System.out.println("------------------------------------------");
	}

	public void testGetLocationParameters2() {
		System.out.println("[DBG0] TEST 2");
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

	@Test
	public void testGetLocationParameters3() {
		System.out.println("[DBG0] TEST 3");
		String str1 = "(value: String personName = customerName;)(label: VARIABLE_DECLARATION_STATEMENT)";
		String str2 = "(value: String $v2 = $p0;)(label: VARIABLE_DECLARATION_STATEMENT)";

		UTCriticsDiffUtil diff = new UTCriticsDiffUtil();
		List<NodeValueArg> lstNodeValueArg = diff.getNodeValueArg(str1, str2);

		for (int i = 0; i < lstNodeValueArg.size(); i++) {
			NodeValueArg iArg = lstNodeValueArg.get(i);
			System.out.println(iArg.toString());
		}
		System.out.println("==========================================");
	}
}
