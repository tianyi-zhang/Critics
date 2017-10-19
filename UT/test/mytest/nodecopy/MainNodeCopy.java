package mytest.nodecopy;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import ut.seal.plugins.utils.ast.UTASTNodeExpressHandler;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

public class MainNodeCopy {
	public static void main(String[] args) {
		Node rooto = new Node(null, "0");
		Node root = new Node(null, "1");
		Node sub2 = new Node(null, "2");
		Node sub3 = new Node(null, "3");
		Node lf4 = new Node(null, "4");
		Node lf5 = new Node(null, "5");
		Node lf6 = new Node(null, "6");
		Node lf7 = new Node(null, "7");

		rooto.add(root);
		root.add(sub2);
		root.add(sub3);
		sub2.add(lf4);
		sub3.add(lf5);
		sub3.add(lf6);
		sub3.add(lf7);
		print(rooto);
		
		Node newRootNode = root.copy();
		print(newRootNode);
	}

	static void print(Enumeration<?> e) {
		while (e.hasMoreElements()) {
			System.out.println("[DBG] " + e.nextElement());
		}
	}

	static void print(Node root) {
		// print(root.breadthFirstEnumeration());

		List<Object> list = new ArrayList<Object>();
		List<Object> subLblTreeNodeList = new ArrayList<Object>();
		UTASTNodeExpressHandler.preoderTraverse(root, list);
		UTASTNodeExpressHandler.postorderTraverse(root, list, null);
		UTASTNodeExpressHandler.representData(subLblTreeNodeList, list);
		for (int i = 0; i < subLblTreeNodeList.size(); i++) {
			Object elem = subLblTreeNodeList.get(i);
			System.out.print(elem);
		}
		System.out.println("DBG------------------------------------------");
	}
}
