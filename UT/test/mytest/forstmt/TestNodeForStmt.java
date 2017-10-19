/*
 * @(#) TestNodeForStmt.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package mytest.forstmt;

import java.util.Enumeration;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.junit.Before;
import org.junit.Test;

import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.NodeForStmt;
import ut.seal.plugins.utils.UTCompilationUtils;
import ut.seal.plugins.utils.ast.UTASTNodeConverter;
import ut.seal.plugins.utils.ast.UTASTParser;

/**
 * @author Myoungkyu Song
 * @date Feb 13, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class TestNodeForStmt {

	@Before
	public void init() {
	}

	@Test
	public void testNodeForStmt() {
		String code = //
		"class classA { " + //
				"void foo() { " + //
				"	for (int i = 0, j = 0; i < 10 || j < 10; i++, j++) {" + //
				"		int elem = i;              " + //
				"		System.out.println(elem);  " + //
				"	}" + //
				"	for (Strange i2 = 0, j2 = 0; i2 < 10 || j2 < 10; i2++, j23333++) {" + //
				"		int elem = i;              " + //
				"		System.out.println(elem);  " + //
				"	}" + //
				" }" + //
				"}";

		String methodName = "foo";
		Node root = compile(code, methodName);
		Enumeration<?> e = root.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			if (iNode.getEntity().getType() == JavaEntityType.FOR_STATEMENT) {
				System.out.println("[DBG] " + iNode.getForStmt());
			}
		}
		System.out.println("------------------------------------------");

		compile2(code);
		
		System.out.println("------------------------------------------");
		code = //
		"class classA { " + //
				"void foo(int[] data) { " + //
				"	for (int i : data) {" + //
				"		int elem = i;              " + //
				"		System.out.println(elem);  " + //
				"	}" + //
				" }" + //
				"}";
		System.out.println("------------------------------------------");
		root = compile(code, methodName);
		e = root.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			if (iNode.getEntity().getType() == JavaEntityType.FOR_STATEMENT) {
				System.out.println("[DBG] " + iNode.getForStmt());
			}
		}
		System.out.println("------------------------------------------");

		compile2(code);

		System.out.println("==========================================");
		// System.out.println("[DBG] " + compile(code, methodName));
		//
		// code = //
		// "class classA { " + //
		// "void foo() { " + //
		// "	for (int i = 0; i < 10; i+=2) {" + //
		// "		int elem = i;              " + //
		// "		System.out.println(elem);  " + //
		// "	}" + //
		// " }" + //
		// "}";
		// System.out.println("[DBG] " + compile(code, methodName));
		//
		// code = //
		// "class classA { " + //
		// "void foo() { " + //
		// "	for (int i = 0; i < 10; i=i+2) {" + //
		// "		int elem = i;              " + //
		// "		System.out.println(elem);  " + //
		// "	}" + //
		// " }" + //
		// "}";
		// System.out.println("[DBG] " + compile(code, methodName));
	}

	Node compile(String code, String methodName) {
		JavaCompilation compilation = UTCompilationUtils.compileSource(code);
		UTASTNodeConverter convert = new UTASTNodeConverter();
		Node root = convert.convertMethodBody(methodName, compilation);
		root.print();
		Enumeration<?> e = root.breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			if (iNode.getEntity().getType() == JavaEntityType.FOR_STATEMENT) {
				NodeForStmt iNodeForStmt = new NodeForStmt(iNode);
				iNode.setForStmt(iNodeForStmt);
			}
		}
		return root;
	}

	void compile2(String code) {
		UTASTParser parser = new UTASTParser();
		CompilationUnit parse = parser.parse(code);
		parse.accept(new ASTVisitor() {
			public boolean visit(MethodDeclaration node) {
				System.out.println("[DBG1] " + node);
				return true;
			}
		});
	}
}
