/**
 * Copyright (c) 2017, UCLA Software Engineering and Analysis Laboratory (SEAL)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
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
