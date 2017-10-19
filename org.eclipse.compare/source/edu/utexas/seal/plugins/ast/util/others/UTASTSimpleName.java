/*
 * @(#) UTASTLocalVariable.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.ast.util.others;

import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;

import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerLocalVariable;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerParameter;

/**
 * @author Myoungkyu Song
 * @date Oct 31, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTASTSimpleName extends ASTVisitor {
	Map<IVariableBinding, UTASTBindingManagerLocalVariable>	variableManagers	= null;
	Map<IVariableBinding, UTASTBindingManagerParameter>		parameterManagers	= null;

	public UTASTSimpleName() {
	}

	public UTASTSimpleName(Map<IVariableBinding, UTASTBindingManagerLocalVariable> vManagers,
			Map<IVariableBinding, UTASTBindingManagerParameter> pManagers) {
		variableManagers = vManagers;
		parameterManagers = pManagers;
	}

	/**
	 * Visits {@link SimpleName} AST nodes. Resolves the binding of the simple
	 * name and looks for it in the {@link #variableManagers} map. If the
	 * binding is found, this is a reference to a local variable. The variable
	 * binding manager of this local variable then has to be informed about that
	 * reference.
	 * 
	 * @param node
	 */
	public boolean visit(SimpleName node) {
		if (node.getFullyQualifiedName().contains("users"))
			System.out.println("[DBG] * var - " + node);

		IBinding binding = node.resolveBinding();
		if (variableManagers.containsKey(binding)) {
			variableManagers.get(binding).addReferenceNodeList(node);
		}
		if (parameterManagers.containsKey(binding)) {
			parameterManagers.get(binding).addReferenceNodeList(node);
		}
		return true;
	}

	/**
	 * Getter for the resulting map.
	 * 
	 * @return a map with variable bindings as keys and {@link UTASTBindingManagerParameter} as values
	 */
	public Map<IVariableBinding, UTASTBindingManagerLocalVariable> getLocalVariableManagers() {
		return variableManagers;
	}

	public Map<IVariableBinding, UTASTBindingManagerParameter> getParameterManagers() {
		return parameterManagers;
	}

	/**
	 * 
	 * @param unit
	 *            the AST root node. Bindings have to have been resolved.
	 * @param leftSelectedRegion
	 * @param rightSelectedRegion
	 */
	public void process(CompilationUnit unit) {
		unit.accept(this);
	}
}