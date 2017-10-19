/*
 * @(#) IUTGeneralVisitor.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.util.visitor;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;

/**
 * @author Myoungkyu Song
 * @param <T>
 * @date Nov 22, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public abstract class UTIGeneralVisitor<T> extends ASTVisitor {
	public abstract List<T> getResults();
}
