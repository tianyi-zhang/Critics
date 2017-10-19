/*
 * @(#) UTASTAnalyzer.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import edu.utexas.seal.plugins.ast.UTASTVisitor;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerField;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerForStatement;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerForStatementEnh;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerLocalVariable;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerMethod;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerParameter;

public class UTASTAnalyzer extends ASTVisitor {
	private int															start;
	private int															end;
	private List<ASTNode>												relatedNodes;
	private Map<IVariableBinding, UTASTBindingManagerField>				fieldManagers			= null;
	private Map<IVariableBinding, UTASTBindingManagerLocalVariable>		variableManagers		= null;
	private Map<IVariableBinding, UTASTBindingManagerParameter>			parameterManagers		= null;
	private Map<IVariableBinding, UTASTBindingManagerForStatementEnh>	forStatementEnhManagers	= null;
	private Map<IVariableBinding, UTASTBindingManagerForStatement>		forStatementManagers	= null;
	private Map<IMethodBinding, UTASTBindingManagerMethod>				methodManagers			= null;
	private boolean														__d__					= false;

	/**
	 * Instantiates a new uTAST analyzer.
	 * 
	 * @param selected the selected
	 * @param visitor the visitor
	 */
	public UTASTAnalyzer(ASTNode selected, UTASTVisitor visitor) {
		start = selected.getStartPosition();
		end = start + selected.getLength();
		relatedNodes = new ArrayList<ASTNode>();
		fieldManagers = visitor.getFieldManagers();
		variableManagers = visitor.getLocalVariableManagers();
		parameterManagers = visitor.getParameterManagers();
		forStatementEnhManagers = visitor.getForStatEnhManagers();
		forStatementManagers = visitor.getForStatManagers();
		methodManagers = visitor.getMethodManagers();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SimpleName)
	 */
	public boolean visit(SimpleName node) {
		if (node.getParent() instanceof VariableDeclarationFragment) {
			return true;
		} else if (node.getParent() instanceof SingleVariableDeclaration) {
			return true;
		}

		IBinding binding = node.resolveBinding();

		// Some SimpleName like object type Server doesn't have binding information, returns null
		// But all managers has a null key, so they will be binded
		if (binding == null) {
			return true;
		}

		if (fieldManagers.containsKey(binding)) {
			ASTNode o = fieldManagers.get(binding).getDeclaration().getParent();
			if (!relatedNodes.contains(o))
				relatedNodes.add(o);
			if (__d__)
				System.out.println("[DBG] * ref - add Field -" + node.getParent().toString().trim());
		} else if (variableManagers.containsKey(binding)) {
			ASTNode o = variableManagers.get(binding).getDeclaration().getParent();
			if (!relatedNodes.contains(o))
				relatedNodes.add(o);
			if (__d__)
				System.out.println("[DBG] * ref - add Local Variable -" + node.getParent().toString().trim());
		} else if (parameterManagers.containsKey(binding)) {
			ASTNode o = parameterManagers.get(binding).getDeclaration();
			relatedNodes.add(o);
			if (__d__)
				System.out.println("[DBG] * ref - add Parameter - " + node.getParent().toString().trim());
		} else if (forStatementEnhManagers.containsKey(binding)) {
			ASTNode o = forStatementEnhManagers.get(binding).getDeclaration();
			if (!relatedNodes.contains(o))
				relatedNodes.add(o);
			if (__d__)
				System.out.println("[DBG] * ref - add Enhanced For -" + node.getParent().toString().trim());
		} else if (forStatementManagers.containsKey(binding)) {
			ASTNode o = forStatementManagers.get(binding).getDeclaration();
			if (!relatedNodes.contains(o))
				relatedNodes.add(o);
			if (__d__)
				System.out.println("[DBG] * ref - add For - " + node.getParent().toString().trim());
		} else if (methodManagers.containsKey(binding)) {
			ASTNode o = methodManagers.get(binding).getMethodDeclaration();
			if (!relatedNodes.contains(o))
				relatedNodes.add(o);
			if (__d__)
				System.out.println("[DBG] * ref - add Method - " + node.getParent().toString().trim());
		}
		return true;
	}

	public List<ASTNode> getRelatedNodes() {
		return relatedNodes;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}
}
