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
package edu.utexas.seal.plugins.crystal.internal;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.cmu.cs.crystal.flow.ILatticeOperations;

public class SetLatticeOperations<LE> 
	implements ILatticeOperations<SetLatticeElement<LE>> {
	protected final LE theDefault;
	protected final ILatticeOperations<LE> elementOps;

	public SetLatticeOperations(ILatticeOperations<LE> operations,
			LE defaultElement) {
		theDefault = defaultElement;
		elementOps = operations;
	}

	/**
	 * Compares analysis information for precision; more precisely, Notice that
	 * in a lattice, the bottom lattice element will be more precise than
	 * everything, and top element will be less precise than everything. That
	 * is, {@code atLeastAsPrecise(bottom, x) == true} and
	 * {@code atLeastAsPrecise(x, top) == true}. compare lower-lattice with
	 * higher-lattice, return true; otherwise, return false
	 * 
	 * The reverse should be true
	 */
	@Override
	public boolean atLeastAsPrecise(SetLatticeElement<LE> left,
			SetLatticeElement<LE> right, ASTNode node) {
		if (left == null || left.getElements() == null) {
			return true;
		}
		if (right == null || right.getElements() == null) {
			return false;
		}
		Set<LE> lElements = left.getElements();
		for (LE e : right.getElements()) {
			if (lElements.contains(e)) {
				// do nothing
			} else {
				return false;// left contains less elements than right, so
								// atLeastAsPrecise(higher, lower) = false
			}
		}
		// if(right == null || right.getElements() == null){
		// return true;
		// }
		// if(left == null || left.getElements() == null){
		// return false;
		// }
		// if(!right.getElements().containsAll(left.getElements())){
		// return false;
		// }
		return true;
	}

	@Override
	public SetLatticeElement<LE> bottom() {
		return new SetLatticeElement<LE>(elementOps.bottom(),
				elementOps.copy(theDefault), null);
	}

	@Override
	public SetLatticeElement<LE> copy(SetLatticeElement<LE> original) {
		if (original.elements == null)
			return new SetLatticeElement<LE>(elementOps.bottom(),
					elementOps.copy(theDefault), null);
		Set<LE> elemCopy = new HashSet<LE>(original.elements.size());
		for (LE e : original.elements) {
			elemCopy.add(elementOps.copy(e));
		}
		return new SetLatticeElement<LE>(elementOps.bottom(),
				elementOps.copy(theDefault), elemCopy);
	}

	/**
	 * This is a intersection between two known sets
	 */
	@Override
	public SetLatticeElement<LE> join(SetLatticeElement<LE> left,
			SetLatticeElement<LE> right, ASTNode node) {// when joining the
														// current node is added
														// to the
		Set<LE> newSet = null;
		if (left.elements == null && right.elements == null) {
			// do nothing, simply use the null pointer
		} else if (left.elements == null && right.elements != null) {
			newSet = new HashSet<LE>(right.elements);
		} else if (left.elements != null && right.elements == null) {
			newSet = new HashSet<LE>(left.elements);
		} else {// none of the element sets is empty
			newSet = new HashSet<LE>(left.elements);
			newSet.retainAll(right.elements);
		}
		return new SetLatticeElement<LE>(elementOps.bottom(),
				elementOps.copy(theDefault), newSet);
	}

	public SetLatticeElement<LE> getDefault() {
		return new SetLatticeElement<LE>(elementOps.bottom(),
				elementOps.copy(theDefault), null);
	}
}
