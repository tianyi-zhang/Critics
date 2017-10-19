/*
 * @(#) UTASTNodeExpressHandler.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package ut.seal.plugins.utils.ast;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import ut.seal.plugins.utils.UTStr;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

/**
 * @author Myoungkyu Song
 * @date Dec 2, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTASTNodeExpressHandler {
	private static final String	S	= System.getProperty("line.separator");

	/**
	 * 
	 * @param rootNode
	 * @param list
	 */
	public static void preoderTraverse(Node rootNode, List<Object> list) {
		Enumeration<?> preOrder = rootNode.preorderEnumeration();
		while (preOrder.hasMoreElements()) {
			Node curNode = (Node) preOrder.nextElement();
			if (curNode.isLeaf()) {
				list.add(UTStr.getIndent(curNode.getLevel()) + "{");
				list.add(curNode);
				list.add("}");
			} else {
				list.add(UTStr.getIndent(curNode.getLevel()) + "{");
				list.add(curNode);
			}
		}
	}

	/**
	 * 
	 * @param rootNode
	 * @param list
	 * @return
	 */
	public static void postorderTraverse(Node rootNode, List<Object> list, Map<Integer, Node> idMapper) {
		int id = 1;
		Enumeration<?> postOrder = rootNode.postorderEnumeration();
		while (postOrder.hasMoreElements()) {
			Node curNode = (Node) postOrder.nextElement();
			if (idMapper != null) {
				idMapper.put(id++, curNode);
			}
			if (!curNode.isLeaf()) {
				Node lastChild = (Node) curNode.getLastChild();
				Node lastLeaf = (Node) lastChild.getLastLeaf();
				int indexOfLastChild = list.lastIndexOf(lastLeaf) + 1;
				boolean flag = false;
				while (indexOfLastChild < list.size() && list.get(indexOfLastChild).toString().trim().equals("}")) {
					flag = true;
					indexOfLastChild++;
				}
				if (flag) {
					list.add(indexOfLastChild, UTStr.getIndent(curNode.getLevel()) + "}");
				} else {
					list.add(UTStr.getIndent(curNode.getLevel()) + "}");
				}
			}
		}
	}

	/**
	 * 
	 * @param list
	 * @param result
	 * @return
	 */
	public static void representData(List<Object> result, List<Object> list) {
		for (int i = 0; i < list.size(); i++) {
			Object elem = list.get(i);
			String msg = "";
			String level = null;
			if (elem instanceof Node) {
				Node curNode = (Node) elem;
				level = "[" + curNode.getLevel() + "] ";
				msg = level + elem;
			} else {
				msg = elem.toString();
			}
			if (msg.trim().equals("{")) {
				result.add(elem.toString());
			} else if (msg.trim().equals("}")) {
				result.add(elem.toString());
				result.add(S);
			} else if (elem instanceof Node && ((Node) elem).isLeaf()) {
				result.add(elem);
			} else {
				result.add(elem);
				result.add(S);
			}
		}
	}
}
