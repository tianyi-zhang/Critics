/*
 * @(#) TestUTCriticsDiffUtil.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ch.uzh.ifi.seal.changedistiller.treedifferencing.NodeValueArg;
import edu.utexas.seal.plugins.util.UTCriticsDiffUtil.Diff;
import edu.utexas.seal.plugins.util.UTCriticsDiffUtil.Operation;

/**
 * @author Myoungkyu Song
 * @date Feb 5, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class TestUTCriticsDiffUtil {
	// @Test
	public void testGetNodeValueArg1() {
		String str1 = "IClassFileReader classFileReader = ToolFactory.createDefaultClassFileReader(this.file, entry.getName(), IClassFileReader.ALL);";
		String str2 = "IClassFileReader $v1 = ToolFactory.createDefaultClassFileReader(this.$f1, $v6.getName(), IClassFileReader.ALL);";

		String valueOrg = str1;
		String valueAllGen = str2;
		UTCriticsDiffUtil diffUtil = new UTCriticsDiffUtil();
		List<NodeValueArg> lstNodeValueArg = diffUtil.getNodeValueArg(valueOrg, valueAllGen);

		for (int i = 0; i < lstNodeValueArg.size(); i++) {
			NodeValueArg elem = lstNodeValueArg.get(i);
			System.out.println(elem);
		}
	}

	// @Test
	public void testGetNodeValueArg2() {
		String str1 = "c1 = Character.getNumericValue()";
		String str2 = "$v166 = aaa.getNumericValue()";

		String valueOrg = str1;
		String valueAllGen = str2;
		UTCriticsDiffUtil diffUtil = new UTCriticsDiffUtil();
		List<NodeValueArg> lstNodeValueArg = diffUtil.getNodeValueArg(valueOrg, valueAllGen);

		for (int i = 0; i < lstNodeValueArg.size(); i++) {
			NodeValueArg elem = lstNodeValueArg.get(i);
			System.out.println(elem);
		}
	}

	// @Test
	public void testGetNodeValueArg3() {
		String str1 = "((((((c1 = Character.getNumericValue(this.source[(this.currentPosition ++)])) > 15) || (c1 < 0)) || (((c2 = Character.getNumericValue(this.source[(this.currentPosition ++)])) > 15) || (c2 < 0))) || (((c3 = Character.getNumericValue(this.source[(this.currentPosition ++)])) > 15) || (c3 < 0))) || (((c4 = Character.getNumericValue(this.source[(this.currentPosition ++)])) > 15) || (c4 < 0)))";
		String str2 = "(((((($v166 = Character.getNumericValue(this.$f13[(this.$f48 ++)])) > 15) || ($v166 < 0)) || ((($v21 = Character.getNumericValue(this.$f13[(this.$f48 ++)])) > 15) || ($v21 < 0))) || ((($v17 = Character.getNumericValue(this.$f13[(this.$f48 ++)])) > 15) || ($v17 < 0))) || ((($v12 = Character.getNumericValue(this.$f13[(this.$f48 ++)])) > 15) || ($v12 < 0)));";

		String valueOrg = str1;
		String valueAllGen = str2;
		UTCriticsDiffUtil diffUtil = new UTCriticsDiffUtil();
		List<NodeValueArg> lstNodeValueArg = diffUtil.getNodeValueArg(valueOrg, valueAllGen);

		for (int i = 0; i < lstNodeValueArg.size(); i++) {
			NodeValueArg elem = lstNodeValueArg.get(i);
			System.out.println(elem);
		}
	}

	@Test
	public void testGetNodeValueArg4() {
		String str1 = "System.err.println(buf1);";
		String str2 = "System.out.println((($v1 + \", \") + data2));";
		boolean checkEQ = checkEQ(str1, str2);
		System.out.println("[DBG] " + checkEQ);

		String valueOrg = str1;
		String valueAllGen = str2;
		UTCriticsDiffUtil diffUtil = new UTCriticsDiffUtil();
		LinkedList<Diff> list = diffUtil.diff_main(str1, str2);
		for (Diff diff : list) {
			System.out.println("[DBG1] " + diff);
		}
		str1 = "System.out.println(((data1 + \", \") + data2));";
		str2 = "System.out.println((($v1 + \", \") + data2));";

		checkEQ = checkEQ(str1, str2);
		System.out.println("[DBG] " + checkEQ);

		List<NodeValueArg> listDiffBlock = diffUtil.getListDiffBlock(str1, str2);
		for (NodeValueArg iNodeValueArg : listDiffBlock) {
			System.out.println("[DBG] " + iNodeValueArg);
		}
		List<NodeValueArg> lstNodeValueArg = diffUtil.getNodeValueArg(valueOrg, valueAllGen);
		for (int i = 0; i < lstNodeValueArg.size(); i++) {
			NodeValueArg elem = lstNodeValueArg.get(i);
			System.out.println(elem);
		}
	}

	public boolean checkEQ(String str1, String str2) {
		UTCriticsDiffUtil diffUtil = new UTCriticsDiffUtil();
		List<Diff> diffList = diffUtil.diff_main(str1, str2);
		List<String> lstDel = new ArrayList<String>();
		List<String> lstIns = new ArrayList<String>();
		for (Diff diff : diffList) {
			if (diff.operation == Operation.DELETE) {
				lstDel.add(diff.text);
			} else if (diff.operation == Operation.INSERT) {
				lstIns.add(diff.text);
			}
		}
		if (lstDel.size() != lstIns.size()) {
			return false;
		}
		for (String string : lstIns) {
			if (!string.startsWith("$")) {
				return false;
			}
		}
		return true;
	}
}