/*
 * @(#) MethodBodyConverter.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package mytest.methodbody;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;

import com.google.inject.Guice;
import com.google.inject.Injector;

import ch.uzh.ifi.seal.changedistiller.JavaChangeDistillerModule;
import ch.uzh.ifi.seal.changedistiller.ast.java.Comment;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaDeclarationConverter;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaMethodBodyConverter;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.SourceRange;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ch.uzh.ifi.seal.changedistiller.util.CompilationUtils;

/**
 * @author Myoungkyu Song
 * @date Nov 10, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class MethodBodyConverter {

	protected final Injector	sInjector;

	public MethodBodyConverter() {
		sInjector = Guice.createInjector(new JavaChangeDistillerModule());
	}

	protected JavaDeclarationConverter	sDeclarationConverter;
	protected JavaMethodBodyConverter	sMethodBodyConverter;

	public void initialize() throws Exception {
		sDeclarationConverter = sInjector.getInstance(JavaDeclarationConverter.class);
		sMethodBodyConverter = sInjector.getInstance(JavaMethodBodyConverter.class);
	}

	@SuppressWarnings("rawtypes")
	public void testBasic01() {
		String methodName = "foo";
		String sourceCode = Helper.readFile("input/mypkg/A.java"); // "class A { void foo() {int i; int j; i = j;}}";
		Node methodNode = convertMethodBody(methodName, CompilationUtils.compileSource(sourceCode));

		System.out.println("DBG__________________________________________");
		List<Object> list = new ArrayList<Object>();

		for (Enumeration preOrder = methodNode.preorderEnumeration(); preOrder.hasMoreElements();) {
			Node curNode = (Node) preOrder.nextElement();
			if (curNode.isLeaf()) {
				list.add(getIndent(curNode.getLevel()) + "{" + curNode.toString() + "}");
			} else {
				list.add(getIndent(curNode.getLevel()) + "{" + curNode);
			}
		}
		for (Enumeration postOrder = methodNode.postorderEnumeration(); postOrder.hasMoreElements();) {
			Node curNode = (Node) postOrder.nextElement();
			if (!curNode.isLeaf()) {
				list.add(getIndent(curNode.getLevel()) + "}");
			}
		}
		for (int i = 0; i < list.size(); i++) {
			Object elem = list.get(i);
			System.out.println(elem);
		}
	}

	public Node convertMethodBody(String methodName, JavaCompilation compilation) {
		AbstractMethodDeclaration method = CompilationUtils.findMethod(compilation.getCompilationUnit(), methodName);
		Node root = new Node(JavaEntityType.METHOD, methodName);
		root.setEntity(new SourceCodeEntity(methodName, JavaEntityType.METHOD, new SourceRange(method.declarationSourceStart,
				method.declarationSourceEnd)));
		List<Comment> comments = CompilationUtils.extractComments(compilation);
		sMethodBodyConverter.initialize(root, method, comments, compilation.getScanner());
		method.traverse(sMethodBodyConverter, (ClassScope) null);
		return root;
	}

	String getIndent(int size) {
		if (size == 0)
			return "";
		String indent = "  ";
		for (int i = 0; i < size; i++)
			indent += "  ";
		return indent;
	}

	public static void main(String[] args) {
		MethodBodyConverter m = new MethodBodyConverter();
		try {
			m.initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
		m.testBasic01();
	}

}
