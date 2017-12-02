/*
 * @(#) TestNode.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package ch.uzh.ifi.seal.changedistiller.treedifferencing;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ut.seal.plugins.utils.UTCriticsDiffUtil;
import ut.seal.plugins.utils.UTStr;

/**
 * @author Myoungkyu Song
 * @date Feb 1, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class TestNode {

	@Test
	public void test() {
		String aValue = "guiDisplay.display(userItem);";
		String aValueWithParms = "$f123.display($r0);";
		List<Integer> lstStart = new ArrayList<Integer>();
		List<String> lstVarNew = new ArrayList<String>();
		List<String> lstVarRep = new ArrayList<String>();
		List<Character> arValueWithParms = UTStr.convertString(aValueWithParms);
		for (int i = 0; i < arValueWithParms.size(); i++) {
			char c = arValueWithParms.get(i);
			if (c == '$') {
				int start = i;
				String newVar = "$" + arValueWithParms.get(++i);
				while (UTStr.isNumeric(String.valueOf(arValueWithParms.get(++i)))) {
					newVar += arValueWithParms.get(i);
				}
				--i;
				System.out.println("[DBG] " + newVar);
				String rep = UTStr.replacement(newVar.length() - 1);
				lstVarRep.add(rep);
				lstVarNew.add(newVar);
				lstStart.add(start);
			}
		}
		for (int i = 0; i < lstStart.size(); i++) {
			Integer start = lstStart.get(i);
			String rep = lstVarRep.get(i);
			aValueWithParms = UTStr.replace(aValueWithParms, rep, start, start + rep.length());
		}

		Node nd1 = new Node(null, aValue);
		Node nd2 = new Node(null, aValueWithParms);

		nd1.setValue(aValue);
		nd2.setNewValue(aValueWithParms);

		String valueOrg = nd1.getValue();
		String valueAllGen = nd2.getValueNew();

		UTCriticsDiffUtil diffUtil = new UTCriticsDiffUtil();
		List<NodeValueArg> lstNodeValueArg = diffUtil.getListDiffBlock(valueOrg, valueAllGen);

		for (int i = 0; i < lstNodeValueArg.size(); i++) {
			NodeValueArg elem = lstNodeValueArg.get(i);
			String iNewVar = lstVarNew.get(i);
			elem.setArgNew(iNewVar);
		}
		for (int i = 0; i < lstNodeValueArg.size(); i++) {
			NodeValueArg elem = lstNodeValueArg.get(i);
			System.out.println(elem);
		}
	}

	// @Test
	public void testGetValueReplacedParmWithToken() {
		String test = "String $var = $var";
		Node n = new Node(null, test);
		String result = n.getValueReplacedParmWithToken(); // test.replaceAll(n.PARM_VAR_NAME, n.PARM_VAR_TOKEN);
		System.out.println("[DBG] " + result);
	}

	// @Test
	public void testSetValueArgParm() {
		String value = "String person = people";
		NodeValueArg nva = new NodeValueArg(7, 12, "person");
		nva.setArgNew("$v0");
		List<NodeValueArg> list = new ArrayList<NodeValueArg>();
		list.add(nva);
		Node n = new Node(null, value);
		n.setValueReplica(value);
		n.setValueArgList(list);
		String setValueArgParm = n.setValueArgParm(1);
		System.out.println("[DBG5] " + setValueArgParm);
		System.out.println("------------------------------------------");

		value = "String per = people";
		nva = new NodeValueArg(7, 9, "per");
		nva.setArgNew("$v0");
		NodeValueArg nva2 = new NodeValueArg(13, 18, "people");
		nva2.setArgNew("$v1");
		list = new ArrayList<NodeValueArg>();
		list.add(nva);
		list.add(nva2);
		n = new Node(null, value);
		n.setValueReplica(value);
		n.setValueArgList(list);
		boolean[] index1 = { false, true };
		setValueArgParm = n.setValueArgParm(index1);
		System.out.println("[DBG5] " + setValueArgParm);
		boolean[] index2 = { true, false };
		setValueArgParm = n.setValueArgParm(index2);
		System.out.println("[DBG5] " + setValueArgParm);
		System.out.println("------------------------------------------");

		value = "String i = people";
		System.out.println("[DBG] " + value);
		nva = new NodeValueArg(7, 7, "i");
		nva.setArgNew("$v0");
		list = new ArrayList<NodeValueArg>();
		list.add(nva);
		n = new Node(null, value);
		n.setValueReplica(value);
		n.setValueArgList(list);
		setValueArgParm = n.setValueArgParm(1);
		System.out.println("[DBG5] " + setValueArgParm);
	}
}
