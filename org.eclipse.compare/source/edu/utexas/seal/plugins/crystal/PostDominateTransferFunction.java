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

import edu.cmu.cs.crystal.flow.AnalysisDirection;
import edu.cmu.cs.crystal.flow.ILatticeOperations;
import edu.cmu.cs.crystal.simple.AbstractingTransferFunction;
import edu.cmu.cs.crystal.simple.TupleLatticeElement;
import edu.cmu.cs.crystal.simple.TupleLatticeOperations;
import edu.cmu.cs.crystal.tac.eclipse.CompilationUnitTACs;
import edu.cmu.cs.crystal.tac.eclipse.EclipseTAC;
import edu.cmu.cs.crystal.tac.model.TACInstruction;
import edu.cmu.cs.crystal.tac.model.TempVariable;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.utexas.seal.plugins.crystal.internal.DominateLE;
import edu.utexas.seal.plugins.crystal.internal.SourceCodeRange;

public class PostDominateTransferFunction extends AbstractingTransferFunction<TupleLatticeElement<Variable, DominateLE>> {

	private MethodDeclaration	d;
	private CompilationUnitTACs	cTac;
	private EclipseTAC			tac;

	private TempVariable		DEFAULT_KEY	= new TempVariable(null);

	public PostDominateTransferFunction(CompilationUnitTACs tac, MethodDeclaration d) {
		this.d = d;
		this.cTac = tac;
		this.tac = tac.getMethodTAC(d);
	}

	private final TupleLatticeOperations<Variable, DominateLE>	ops	= new TupleLatticeOperations<Variable, DominateLE>(new PostDominateLatticeOps(), DominateLE.bottom());

	@Override
	public ILatticeOperations<TupleLatticeElement<Variable, DominateLE>> getLatticeOperations() {
		return ops;
	}

	@Override
	public TupleLatticeElement<Variable, DominateLE> createEntryValue(MethodDeclaration method) {
		return ops.getDefault();
	}

	@Override
	public AnalysisDirection getAnalysisDirection() {
		return AnalysisDirection.BACKWARD_ANALYSIS;
	}

	@Override
	public TupleLatticeElement<Variable, DominateLE> transfer(TACInstruction instr, TupleLatticeElement<Variable, DominateLE> value) {
		SourceCodeRange range = new SourceCodeRange(instr.getNode().getStartPosition(), instr.getNode().getLength());
		DominateLE le = null;
		if (value.getKeySet().size() == 0) {
			le = new DominateLE();
			value.put(DEFAULT_KEY, le);
		} else {
			le = value.get(DEFAULT_KEY);
		}
		le.add(range);
		return value;
	}

	public MethodDeclaration getMethodDeclaration() {
		return d;
	}

	public CompilationUnitTACs getCompilationUnitTACs() {
		return cTac;
	}

	public EclipseTAC getEclipseTAC() {
		return tac;
	}
}
