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
