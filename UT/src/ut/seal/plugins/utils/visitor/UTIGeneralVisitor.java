/*
 * @(#) UTIGeneralVisitor.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package ut.seal.plugins.utils.visitor;

import java.util.Collection;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;

/**
 * @author Myoungkyu Song
 * @date Nov 24, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public abstract class UTIGeneralVisitor extends ASTVisitor {
	public abstract void add(MethodDeclaration methodDeclaration);

	public abstract Collection<? extends AbstractMethodDeclaration> getMethodDeclList();

	public abstract void getMethodDeclListClear();
}
