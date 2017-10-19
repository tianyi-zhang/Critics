/*
 * @(#) UTASTBindingManagerAbstract.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.ast.binding;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclaration;

/**
 * @author Myoungkyu Song
 * @date Oct 31, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public abstract class UTASTBindingManagerAbstract {
	protected List<Assignment>	leftHandVarAssignmentNodeList	= null;
	protected List<Assignment>	rightHandVarAssignmentNodeList	= null;
	protected List<SimpleName>	variableReferenceNodeList		= null;
	protected SimpleName		newAbstractNode					= null;

	public UTASTBindingManagerAbstract() {
		leftHandVarAssignmentNodeList = new ArrayList<Assignment>();
		rightHandVarAssignmentNodeList = new ArrayList<Assignment>();		
		variableReferenceNodeList = new ArrayList<SimpleName>();
	}

	public abstract VariableDeclaration getDeclaration();

	public SimpleName getNewAbstractNode() {
		return newAbstractNode;
	}

	public void setNewAbstractNode(SimpleName newAbstractNode) {
		this.newAbstractNode = newAbstractNode;
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
		variableReferenceNodeList.add(n);
	}

	public List<SimpleName> getReferenceNodeList() {
		return this.variableReferenceNodeList;
	}

	public void clearReferenceNodeList() {
		this.variableReferenceNodeList.clear();
	}
}
