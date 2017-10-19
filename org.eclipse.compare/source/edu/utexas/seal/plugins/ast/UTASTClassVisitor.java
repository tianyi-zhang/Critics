/*
 * @(#) UTASTClassVisitor.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.ast;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerClass;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerField;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerForStatement;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerForStatementEnh;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerLocalVariable;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerMethod;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerParameter;

public class UTASTClassVisitor extends ASTVisitor {
	private Map<IVariableBinding, UTASTBindingManagerField>				fieldManagers			= null;
	private Map<IVariableBinding, UTASTBindingManagerLocalVariable>		variableManagers		= null;
	private Map<IVariableBinding, UTASTBindingManagerParameter>			parameterManagers		= null;
	private Map<IVariableBinding, UTASTBindingManagerForStatementEnh>	forStatementEnhManagers	= null;
	private Map<IVariableBinding, UTASTBindingManagerForStatement>		forStatementManagers	= null;
	private Map<IMethodBinding, UTASTBindingManagerMethod>				methodManagers			= null;
	private Map<ITypeBinding, UTASTBindingManagerClass>					inheritanceMangagers		= null;
	private boolean														__d__					= false;
	
	public UTASTClassVisitor() {
		fieldManagers = new HashMap<IVariableBinding, UTASTBindingManagerField>();
		variableManagers = new HashMap<IVariableBinding, UTASTBindingManagerLocalVariable>();
		parameterManagers = new HashMap<IVariableBinding, UTASTBindingManagerParameter>();
		forStatementEnhManagers = new HashMap<IVariableBinding, UTASTBindingManagerForStatementEnh>();
		forStatementManagers = new HashMap<IVariableBinding, UTASTBindingManagerForStatement>();
		methodManagers = new HashMap<IMethodBinding, UTASTBindingManagerMethod>();
		inheritanceMangagers = new HashMap<ITypeBinding, UTASTBindingManagerClass>();
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		if (__d__)
			System.out.println("[DBG] #decl- Class - " + node.toString().trim());
		Type superClassType = node.getSuperclassType();
		if (superClassType != null) {
			ITypeBinding binding = superClassType.resolveBinding();
			if (binding != null) {
				UTASTBindingManagerClass superClass = new UTASTBindingManagerClass(superClassType);
				inheritanceMangagers.put(binding, superClass);
			}
		}
		return true;
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

		VariableDeclarationFragment f = null;
		IVariableBinding b = null;
		UTASTBindingManagerLocalVariable m = null;
		for (Iterator<?> iter = node.fragments().iterator(); iter.hasNext();) {
			f = (VariableDeclarationFragment) iter.next();
			b = f.resolveBinding();
			m = new UTASTBindingManagerLocalVariable(f); // create the manager for the fragment
			variableManagers.put(b, m);
		}
		return true;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		if (__d__) {
			System.out.println("[DBG] #decl- Method - " + node.getName().toString());
			System.out.println("[DBG] #decl- Parameter - " + toStringArray(node.parameters().toArray()));
		}

		SingleVariableDeclaration v = null;
		IVariableBinding vb = null;
		UTASTBindingManagerParameter mp = null;
		for (Iterator<?> i = node.parameters().iterator(); i.hasNext();) {
			Object o = i.next();
			if (o instanceof SingleVariableDeclaration) {
				v = (SingleVariableDeclaration) o;
				vb = v.resolveBinding();
				mp = new UTASTBindingManagerParameter(v);
				parameterManagers.put(vb, mp);
			}
		}

		IMethodBinding mb = null;
		UTASTBindingManagerMethod mm = null;
		mb = node.resolveBinding();
		mm = new UTASTBindingManagerMethod(node);
		methodManagers.put(mb, mm);
		return true;
	}

	@Override
	public boolean visit(ForStatement node) {
		VariableDeclarationFragment f = null;
		IVariableBinding b = null;
		UTASTBindingManagerForStatement m = null;
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
		return true;
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		if (__d__)
			System.out.println("[DBG] + EnForStatement " + node);

		SingleVariableDeclaration v = node.getParameter();
		IVariableBinding b = v.resolveBinding();
		UTASTBindingManagerForStatementEnh m = new UTASTBindingManagerForStatementEnh(v);
		forStatementEnhManagers.put(b, m);
		return true;
	}

	@Override
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
		} else if (methodManagers.containsKey(binding)) {
			methodManagers.get(binding).addReferenceNodeList(node);
			if (__d__)
				System.out.println("[DBG] * ref - MethodInvoc" + node.getParent().toString().trim());
		} else if (binding instanceof IMethodBinding) {
			ITypeBinding typeBinding = ((IMethodBinding) binding).getDeclaringClass();
			if (inheritanceMangagers.containsKey(typeBinding)) {
				inheritanceMangagers.get(typeBinding).addReferenceNodeList(node);
			}
			if (__d__)
				System.out.println("[DBG] * ref - InheritedMethodInvoc" + node.getParent().toString().trim());
		}
		return true;
	}

	String toStringArray(Object[] array) {
		String s = "";
		for (int i = 0; i < array.length; i++) {
			s += array[i].toString();
		}
		return s;
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

	public Map<IVariableBinding, UTASTBindingManagerForStatementEnh> getForStatementEnhManagers() {
		return forStatementEnhManagers;
	}

	public Map<IVariableBinding, UTASTBindingManagerForStatement> getForStatementManagers() {
		return forStatementManagers;
	}

	public Map<IMethodBinding, UTASTBindingManagerMethod> getMethodManagers() {
		return methodManagers;
	}
	
	public Map<ITypeBinding, UTASTBindingManagerClass> getInheritanceMangagers() {
		return inheritanceMangagers;
	}
}
