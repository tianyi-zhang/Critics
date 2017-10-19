/*
 * @(#) UTASTNodeOperation.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
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
