/*
 * @(#) PostDominateAnalysis.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.crystal;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.AbstractCrystalMethodAnalysis;
import edu.cmu.cs.crystal.simple.TupleLatticeElement;
import edu.cmu.cs.crystal.tac.TACFlowAnalysis;
import edu.cmu.cs.crystal.tac.eclipse.CompilationUnitTACs;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.utexas.seal.plugins.crystal.internal.DominateLE;
import edu.utexas.seal.plugins.crystal.internal.MethodUtilities;

public class PostDominateAnalysis extends AbstractCrystalMethodAnalysis {
	private MethodDeclaration											method;
	private TACFlowAnalysis<TupleLatticeElement<Variable, DominateLE>>	fa;
	PostDominateElementResult											result;

	public PostDominateAnalysis() {
		super();
	}

	public PostDominateAnalysis(MethodDeclaration d) {
		super();
		this.method = d;
	}

	@Override
	public void analyzeMethod(MethodDeclaration d) {
		if (MethodUtilities.compareMethods(method, d)) {
			CompilationUnitTACs tac = this.analysisInput.getComUnitTACs().unwrap();
			PostDominateTransferFunction tf = new PostDominateTransferFunction(tac, d);
			fa = new TACFlowAnalysis<TupleLatticeElement<Variable, DominateLE>>(tf, this.analysisInput.getComUnitTACs().unwrap());
			result = new PostDominateElementResult(d, fa, tac);
		}
	}

	public PostDominateElementResult getResult() {
		return this.result;
	}
}
