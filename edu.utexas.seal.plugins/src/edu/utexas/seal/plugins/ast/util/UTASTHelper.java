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
