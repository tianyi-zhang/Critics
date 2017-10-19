/*
 * @(#) UTASTHelper.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.ast.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;

/**
 * @author Myoungkyu Song
 * @date Oct 27, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTASTHelper {
	public static final String						SEPARATOR_BETWEEN_PARAMETERS	= ", ";

	private static final int						LIMIT							= 10;
	private static Map<ICompilationUnit, ASTNode>	knownMap						= new HashMap<ICompilationUnit, ASTNode>(LIMIT);

	/**
	 * Compare methods.
	 * 
	 * @param md1 the md1
	 * @param md2 the md2
	 * @return true, if successful
	 */
	public static boolean compareMethods(MethodDeclaration md1, MethodDeclaration md2) {
		String sig1 = getMethodSignatureFromASTNode(md1);
		String sig2 = getMethodSignatureFromASTNode(md2);
		int start1 = md1.getStartPosition();
		int start2 = md2.getStartPosition();
		int length1 = md1.getLength();
		int length2 = md2.getLength();
		if (sig1.equals(sig2) && start1 == start2 && length1 == length2) {
			return true;
		}
		return false;
	}

	/**
	 * Gets the aST node from compilation unit.
	 * 
	 * @param icu the icu
	 * @return the aST node from compilation unit
	 */
	public static ASTNode getASTNodeFromCompilationUnit(ICompilationUnit icu) {
		if (knownMap.containsKey(icu))
			return knownMap.get(icu);

		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setResolveBindings(true);
		parser.setSource(icu);
		ASTNode cu = parser.createAST(null);
		if (knownMap.size() >= LIMIT) {
			knownMap.clear();
		}
		knownMap.put(icu, cu);
		return cu;
	}

	/**
	 * The method name is the unique ID for the method.
	 * 
	 * @param node the node
	 * @return the method signature from ast node
	 */
	public static String getMethodSignatureFromASTNode(MethodDeclaration node) {
		StringBuffer name = new StringBuffer(node.getName().getIdentifier());
		name.append("(");
		List<?> parameters = node.parameters();
		int numOfComma = node.parameters().size() - 1;
		int counter = 0;
		for (Object parameter : parameters) {
			String typeName = ((SingleVariableDeclaration) parameter).getType().toString();
			name.append(typeName);
			if (counter++ < numOfComma) {
				name.append(SEPARATOR_BETWEEN_PARAMETERS);
			}
		}
		name.append(")");
		return name.toString();
	}

	/**
	 * Gets the parent block.
	 * 
	 * @param s the s
	 * @return the parent block of {@code s}
	 */
	public static Block getParentBlock(Statement s) {
		ASTNode node = s;
		while (!(node instanceof Block)) {
			node = node.getParent();
		}
		return (Block) node;
	}

	/**
	 * Gets the parent statement.
	 * 
	 * @param s the s
	 * @return the parent statement
	 */
	public static Object getParentStatement(SimpleName s) {
		ASTNode node = s;
		while (!(node instanceof Statement) && (node.getParent() != null)) {
			node = node.getParent();
		}
		if (node.getParent() != null)
			return null;
		return node;
	}

}
