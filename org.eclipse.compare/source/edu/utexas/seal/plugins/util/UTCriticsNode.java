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
package edu.utexas.seal.plugins.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreeNode;

import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

/**
 * @author Myoungkyu Song
 * @date Dec 5, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTCriticsNode {

	public boolean contains(Node aNode, List<Node> aNodeList) {
		if (aNode == null || aNodeList == null || aNodeList.isEmpty())
			return false;
		for (int k = 0; k < aNodeList.size(); k++) {
			Node elem = aNodeList.get(k);
			if (elem.getValue().equals(aNode.getValue()) && //
					elem.getLabel().equals(aNode.getLabel()) && //
					elem.getEntity().getSourceRange().equals(aNode.getEntity().getSourceRange())) {
				return true;
			}
		}
		return false;
	}

	public List<Node> getNodeList(TreeNode[] arTreeNodes) {
		List<Node> result = new ArrayList<Node>();
		for (int j = 0; j < arTreeNodes.length; j++) {
			result.add((Node) arTreeNodes[j]);
		}
		return result;
	}

	/**
	 * 
	 * @param nodes
	 * @param rootNodeList
	 * @return
	 */
	public List<Node> getRootNodes(List<Node> nodes, List<Node> rootNodeList) {
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			boolean hasParentInNodeList = false;
			for (int j = 0; j < nodes.size(); j++) {
				Node nodeNested = nodes.get(j);
				if (node.getParent().equals(nodeNested)) {
					hasParentInNodeList = true;
					break;
				}
			}
			if (!hasParentInNodeList) {
				rootNodeList.add(node);
			}
		}
		return rootNodeList;
	}

	public List<Node> getUpperNodes(List<Node> nodes) {
		List<Node> lTmpNodes = new ArrayList<Node>();
		for (int i = 0; i < nodes.size(); i++) {
			lTmpNodes.add(nodes.get(i));
		}
		for (int i = 0; i < nodes.size(); i++) {
			Node iNode = nodes.get(i);
			boolean bfChild = false;
			for (int j = 0; j < nodes.size(); j++) {
				Node iMayParent = nodes.get(j);
				if (iMayParent.hasChild(iNode)) {
					bfChild = true;
					break;
				}
			}
			if (bfChild) {
				lTmpNodes.remove(iNode);
			}
		}
		return lTmpNodes;
	}

	public boolean isChild(Node child, Node rootNode) {
		Enumeration<?> e = rootNode.preorderEnumeration();
		int cnNode = -1;
		while (e.hasMoreElements()) {
			cnNode++;
			Node n = (Node) e.nextElement();
			if (cnNode == 0) {
				continue;
			}
			if (n.eq(child))
				return true;
		}
		return false;
	}

	public void printMapping(List<int[]> aList, Map<Integer, Node> aMap1, Map<Integer, Node> aMap2) {
		int cn = 0;
		for (int[] nodeAlignment : aList) {
			Node iNode1 = aMap1.get(nodeAlignment[0]);
			Node iNode2 = aMap2.get(nodeAlignment[1]);
			if (iNode1 != null && iNode2 != null) {
				System.out.println("[DBG" + (cn % 2) + "] " + iNode1);
				System.out.println("[DBG" + (cn % 2) + "] " + iNode2);
			}
			cn++;
		}
	}

	/**
	 * 
	 * @param node
	 * @return
	 */
	public int sizeOfSubTree(Node node) {
		int size = 0;
		Enumeration<?> postOrder = node.postorderEnumeration();
		while (postOrder.hasMoreElements()) {
			postOrder.nextElement();
			size++;
		}
		return size;
	}
}
