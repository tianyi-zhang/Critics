/*
 * @(#) UTASTNodeConverterManager.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package ut.seal.plugins.utils.ast;

import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

/**
 * @author Myoungkyu Song
 * @date Nov 26, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTASTNodeConverterManager {
	Node			node;
	JavaCompilation	javaCompilation;

	public UTASTNodeConverterManager(JavaCompilation javaCompilation, Node node) {
		this.javaCompilation = javaCompilation;
		this.node = node;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public JavaCompilation getJavaCompilation() {
		return javaCompilation;
	}

	public void setJavaCompilation(JavaCompilation javaCompilation) {
		this.javaCompilation = javaCompilation;
	}
}
