package mytest.nodehash;

import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

public class MainNodeHash {
	public static void main(String[] args) {
		Node root = new Node(JavaEntityType.METHOD, "A");
		Node sub1 = new Node(JavaEntityType.METHOD, "B");
		Node sub2 = new Node(JavaEntityType.METHOD, "B");
		Node lf1 = new Node(JavaEntityType.METHOD, "C");
		Node lf2 = new Node(JavaEntityType.METHOD, "C");

		root.add(sub1);
		root.add(sub2);

		sub1.add(lf1);
		sub2.add(lf2);

		System.out.println("[DBG] lf1.hashCode(): " + lf1.hashCode());
		System.out.println("[DBG] lf2.hashCode(): " + lf2.hashCode());

		if (lf1.equals(lf2)) {
			System.out.println("[DBG] EQ.");
		} else {
			System.out.println("[DBG] NEQ.");
		}
		if (lf1.getParent().getParent().equals(lf2.getParent().getParent())) {
			System.out.println("[DBG] EQ.");
		} else {
			System.out.println("[DBG] NEQ.");
		}
	}
}
