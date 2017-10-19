/*
 * @(#) UTGeneralVisitor.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package ut.seal.plugins.utils.visitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;

/**
 * @author Myoungkyu Song
 * @date Nov 24, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTGeneralVisitor extends UTIGeneralVisitor {
	public List<AbstractMethodDeclaration>	methodDeclList	= null;

	public UTGeneralVisitor() {
		methodDeclList = new ArrayList<AbstractMethodDeclaration>();
	}

	public void add(MethodDeclaration methodDeclaration) {
		methodDeclList.add(methodDeclaration);
	}

	public void add(ConstructorDeclaration methodDeclaration) {
		methodDeclList.add(methodDeclaration);
	}

	public List<AbstractMethodDeclaration> getMethodDeclList() {
		return methodDeclList;
	}

	public void getMethodDeclListClear() {
		methodDeclList.clear();
	}
}