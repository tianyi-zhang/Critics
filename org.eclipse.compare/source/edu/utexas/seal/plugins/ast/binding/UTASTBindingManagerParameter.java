/*
 * @(#) UTASTVariableBindingManager.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.ast.binding;

import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

/**
 * @modified
 * @author Myoungkyu Song
 * @date Oct 27, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTASTBindingManagerParameter extends UTASTBindingManagerAbstract {
	private SingleVariableDeclaration	parameterDeclaration;

	public UTASTBindingManagerParameter(SingleVariableDeclaration variableDeclaration) {
		this.parameterDeclaration = variableDeclaration;
	}

	public SingleVariableDeclaration getParameterDeclaration() {
		return (SingleVariableDeclaration) getDeclaration();
	}

	@Override
	public VariableDeclaration getDeclaration() {
		return parameterDeclaration;
	}
}
