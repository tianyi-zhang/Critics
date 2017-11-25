/**
 * Copyright (c) 2017, UCLA Software Engineering and Analysis Laboratory (SEAL)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
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
