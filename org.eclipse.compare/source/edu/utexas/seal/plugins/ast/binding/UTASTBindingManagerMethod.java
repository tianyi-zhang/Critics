/*
 * @(#) UTASTBindingManagerMethod.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.ast.binding;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;

/**
 * @author troy
 * 
 */
public class UTASTBindingManagerMethod {
	private MethodDeclaration	methodDeclaration;
	private List<Assignment>	leftHandVarAssignmentNodeList	= null;
	private List<Assignment>	rightHandVarAssignmentNodeList	= null;
	private List<SimpleName>	methodInvocList					= null;

	public UTASTBindingManagerMethod(MethodDeclaration method) {
		leftHandVarAssignmentNodeList = new ArrayList<Assignment>();
		rightHandVarAssignmentNodeList = new ArrayList<Assignment>();
		methodInvocList = new ArrayList<SimpleName>();
		this.methodDeclaration = method;
	}

	public MethodDeclaration getMethodDeclaration() {
		return methodDeclaration;
	}

	public void clearAssignmentNodeList() {
		this.leftHandVarAssignmentNodeList.clear();
		this.rightHandVarAssignmentNodeList.clear();
	}

	public List<Assignment> getLeftHandVarAssignmentNodeList() {
		return leftHandVarAssignmentNodeList;
	}

	public void addLeftHandVarAssignmentNodeList(Assignment e) {
		this.leftHandVarAssignmentNodeList.add(e);
	}

	public List<Assignment> getRightHandVarAssignmentNodeList() {
		return rightHandVarAssignmentNodeList;
	}

	public void addRightHandVarAssignmentNodeList(Assignment e) {
		this.rightHandVarAssignmentNodeList.add(e);
	}

	public void addReferenceNodeList(SimpleName n) {
		methodInvocList.add(n);
	}

	public List<SimpleName> getMethodInvocationList() {
		return methodInvocList;
	}

	public void clearMethodInvocationList() {
		this.methodInvocList.clear();
	}
}
