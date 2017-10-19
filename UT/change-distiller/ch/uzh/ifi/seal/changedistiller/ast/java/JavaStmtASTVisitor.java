/*
 * @(#) RefASTVisitor.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package ch.uzh.ifi.seal.changedistiller.ast.java;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

/**
 * @author Myoungkyu Song
 * @date Jan 27, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
class JavaStmtASTVisitor extends ASTVisitor {
	List<SingleNameReference>	lstRef	= new ArrayList<SingleNameReference>();

	@Override
	public boolean visit(SingleNameReference singleNameReference, BlockScope scope) {
		lstRef.add(singleNameReference);
		return true; // do nothing by default, keep traversing
	}

	public List<SingleNameReference> getSingleNameReferenceList() {
		return lstRef;
	}
}