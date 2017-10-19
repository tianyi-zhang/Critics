/*
 * @(#) UTASTMethodVisitor.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.ast;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerField;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerForStatement;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerForStatementEnh;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerLocalVariable;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerParameter;

public class UTASTMethodVisitor extends ASTVisitor {
	private MethodDeclaration											method;
	private Map<IVariableBinding, UTASTBindingManagerField>				fieldManagers			= null;
	private Map<IVariableBinding, UTASTBindingManagerLocalVariable>		variableManagers		= null;
	private Map<IVariableBinding, UTASTBindingManagerParameter>			parameterManagers		= null;
	private Map<IVariableBinding, UTASTBindingManagerForStatement>		forStatementManagers	= null;
	private Map<IVariableBinding, UTASTBindingManagerForStatementEnh>	forStatementEnhManagers	= null;
	private boolean														__d__					= false;

	public UTASTMethodVisitor(MethodDeclaration method) {
		fieldManagers = new HashMap<IVariableBinding, UTASTBindingManagerField>();
		variableManagers = new HashMap<IVariableBinding, UTASTBindingManagerLocalVariable>();
		parameterManagers = new HashMap<IVariableBinding, UTASTBindingManagerParameter>();
		forStatementEnhManagers = new HashMap<IVariableBinding, UTASTBindingManagerForStatementEnh>();
		forStatementManagers = new HashMap<IVariableBinding, UTASTBindingManagerForStatement>();
		this.method = method;
		getParameters();
	}

	private void getParameters() {
		if (__d__) {
			System.out.println("[DBG] Getting Paramemter Declaration For Selectd Method.");
		}
		SingleVariableDeclaration v = null;
		IVariableBinding vb = null;
		UTASTBindingManagerParameter mp = null;
		if (method.parameters() == null) {
			return;
		}
		for (Iterator<?> i = method.parameters().iterator(); i.hasNext();) {
			Object o = i.next();
			if (o instanceof SingleVariableDeclaration) {
				v = (SingleVariableDeclaration) o;
				vb = v.resolveBinding();
				mp = new UTASTBindingManagerParameter(v);
				parameterManagers.put(vb, mp);
			}

		}
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		if (__d__)
			System.out.println("[DBG] #decl- Field - " + node.toString().trim());
		VariableDeclarationFragment f = null;
		IVariableBinding b = null;
		UTASTBindingManagerField m = null;
		for (Iterator<?> iter = node.fragments().iterator(); iter.hasNext();) {
			f = (VariableDeclarationFragment) iter.next();
			b = f.resolveBinding();
			m = new UTASTBindingManagerField(f); // create the manager for the fragment
			fieldManagers.put(b, m);
		}
		return true;
	}

	@Override
	public boolean visit(VariableDeclarationStatement node) {
		if (__d__)
			System.out.println("[DBG] #decl- Local Variable - " + node.toString().trim());
		if (withinMethod(node)) {
			VariableDeclarationFragment f = null;
			IVariableBinding b = null;
			UTASTBindingManagerLocalVariable m = null;
			for (Iterator<?> iter = node.fragments().iterator(); iter.hasNext();) {
				f = (VariableDeclarationFragment) iter.next();
				b = f.resolveBinding();
				m = new UTASTBindingManagerLocalVariable(f); // create the manager for the fragment
				variableManagers.put(b, m);
			}
		}
		return true;
	}

	@Override
	public boolean visit(ForStatement node) {
		VariableDeclarationFragment f = null;
		IVariableBinding b = null;
		UTASTBindingManagerForStatement m = null;
		if (withinMethod(node)) {
			for (Iterator<?> iter = node.initializers().iterator(); iter.hasNext();) {
				Object o = iter.next();
				if (o instanceof VariableDeclarationExpression) {
					VariableDeclarationExpression varDeclExp = (VariableDeclarationExpression) o;
					for (Iterator<?> jter = varDeclExp.fragments().iterator(); jter.hasNext();) {
						f = (VariableDeclarationFragment) jter.next();
						b = f.resolveBinding();
						m = new UTASTBindingManagerForStatement(f);
						forStatementManagers.put(b, m);
					}
				}
			}
		}
		return true;
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		if (__d__)
			System.out.println("[DBG] + EnForStatement " + node);
		if (withinMethod(node)) {
			SingleVariableDeclaration v = node.getParameter();
			IVariableBinding b = v.resolveBinding();
			UTASTBindingManagerForStatementEnh m = new UTASTBindingManagerForStatementEnh(v);
			forStatementEnhManagers.put(b, m);
		}
		return true;
	}

	public boolean visit(SimpleName node) {
		if (!withinMethod(node)) {
			return true;
		}

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
			fieldManagers.get(binding).addReferenceNodeList(node);
			if (__d__)
				System.out.println("[DBG] * ref - Field -" + node.getParent().toString().trim());
		} else if (variableManagers.containsKey(binding)) {
			variableManagers.get(binding).addReferenceNodeList(node);
			if (__d__)
				System.out.println("[DBG] * ref - Local Variable -" + node.getParent().toString().trim());
		} else if (parameterManagers.containsKey(binding)) {
			parameterManagers.get(binding).addReferenceNodeList(node);
			if (__d__)
				System.out.println("[DBG] * ref - Parameter - " + node.getParent().toString().trim());
		} else if (forStatementEnhManagers.containsKey(binding)) {
			forStatementEnhManagers.get(binding).addReferenceNodeList(node);
			if (__d__)
				System.out.println("[DBG] * ref - Enhanced For -" + node.getParent().toString().trim());
		} else if (forStatementManagers.containsKey(binding)) {
			forStatementManagers.get(binding).addReferenceNodeList(node);
			if (__d__)
				System.out.println("[DBG] * ref - For - " + node.getParent().toString().trim());
		}
		return true;
	}

	public boolean withinMethod(ASTNode node) {
		int method_bgn = method.getStartPosition();
		int method_end = method_bgn + method.getLength();
		int node_bgn = node.getStartPosition();
		int node_end = node_bgn + node.getLength();

		if ((method_bgn <= node_bgn) && (method_end >= node_end)) {
			return true;
		}
		return false;
	}

	public Map<IVariableBinding, UTASTBindingManagerField> getFieldManagers() {
		return fieldManagers;
	}

	public Map<IVariableBinding, UTASTBindingManagerLocalVariable> getVariableManagers() {
		return variableManagers;
	}

	public Map<IVariableBinding, UTASTBindingManagerParameter> getParameterManagers() {
		return parameterManagers;
	}

	public Map<IVariableBinding, UTASTBindingManagerForStatement> getForStatementManagers() {
		return forStatementManagers;
	}

	public Map<IVariableBinding, UTASTBindingManagerForStatementEnh> getForStatementEnhManagers() {
		return forStatementEnhManagers;
	}
}
