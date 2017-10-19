/*
 * @(#) MethodVisitorByInternalCompiler.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package ut.seal.plugins.utils.ast;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;

/**
 * @author Myoungkyu Song
 * @date Feb 5, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class MethodVisitorByInternalCompiler extends ASTVisitor {
	public List<AbstractMethodDeclaration>	mMethodList	= new ArrayList<AbstractMethodDeclaration>();

	@Override
	public boolean visit(ConstructorDeclaration constructorDeclaration, ClassScope scope) {
		mMethodList.add(constructorDeclaration);
		return true; // do nothing by default, keep traversing
	}

	@Override
	public boolean visit(MethodDeclaration methodDeclaration, ClassScope scope) {
		mMethodList.add(methodDeclaration);
		return true;
	}
}
