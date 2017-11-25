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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;

import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

/**
 * @author Myoungkyu Song
 * @date Nov 21, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTASTNodeOperation {
	private boolean	__$__	= false;

	/**
	 * 
	 * @param node
	 */
	public void checkAllParents(Node node) {
		if (node != null) {
			node.setMarkAliveNode();
			if (((Node) node.getParent()) == null || ((Node) node.getParent()).isMarkAliveNode())
				return;
			checkAllParents((Node) node.getParent());
		}
	}

	/**
	 * 
	 * @param rootNode
	 */
	@SuppressWarnings("unchecked")
	public void checkAllChildren(Node rootNode) {
		List<Node> workList = new ArrayList<Node>();
		workList.addAll(Collections.list(rootNode.children()));
		while (!workList.isEmpty()) {
			Node nodeElem = workList.remove(0);
			nodeElem.setMarkAliveNode();
			if (nodeElem.getChildCount() != 0) {
				workList.addAll(Collections.list(nodeElem.children()));
			}
		}
	}

	/**
	 * 
	 * @param rootNode
	 * @param relevantContextNodeList
	 */
	public void markAliveToTreeNode(Node rootNode, List<ASTNode> relevantContextNodeList) {
		Enumeration<?> postOrder = rootNode.postorderEnumeration();
		while (postOrder.hasMoreElements()) {
			Node curNode = (Node) postOrder.nextElement();
			int curBgn = curNode.getEntity().getStartPosition();
			int curEnd = curNode.getEntity().getEndPosition();
			for (int i = 0; i < relevantContextNodeList.size(); i++) {
				ASTNode nodeElem = relevantContextNodeList.get(i);
				int bgnNodeElem = nodeElem.getStartPosition();
				int endNodeElem = nodeElem.getLength() + bgnNodeElem;

				if (bgnNodeElem == curBgn && endNodeElem == curEnd + 1) {
					if (__$__) {
						System.out.println("[DBG] " + curNode + ", " + curBgn + ", " + curEnd);
						System.out.println("DBG__________________________________________");
					}
					curNode.setSelectedByUser(true);
					curNode.setMarkAliveNode();
					checkAllParents(curNode);
					checkAllChildren(curNode);
				}
			}
		}
	}

	/**
	 * 
	 * @param node
	 * @param isPrintable
	 * @return
	 */
	public List<Node> getRemoveList(Node node, boolean isPrintable) {
		List<Node> removeList = new ArrayList<Node>();
		Enumeration<?> postOrder = node.postorderEnumeration();
		while (postOrder.hasMoreElements()) {
			Node curNode = (Node) postOrder.nextElement();
			if (isPrintable) {
				if (curNode.isMarkAliveNode())
					System.out.print("[DBG1]+ ");
				else
					System.out.print("[DBG0]- ");
				System.out.println(curNode);
			}
			if (!curNode.isMarkAliveNode())
				removeList.add(curNode);
		}
		return removeList;
	}

	/**
	 * 
	 * @param node
	 * @param isPrintable
	 */
	public void pruneDensityOfTreeNode(Node node, boolean isPrintable) {
		List<Node> removeList = getRemoveList(node, isPrintable);
		for (Node nodeElem : removeList) {
			nodeElem.removeFromParent();
		}
	}

}
