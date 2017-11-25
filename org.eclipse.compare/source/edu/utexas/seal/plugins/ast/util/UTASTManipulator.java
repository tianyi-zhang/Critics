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
package edu.utexas.seal.plugins.ast.util;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.swt.graphics.Point;

import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerAbstract;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerForStatementEnh;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerField;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerForStatement;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerLocalVariable;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerParameter;
import edu.utexas.seal.plugins.util.UTCriticsTextSelection;

/**
 * @author Myoungkyu Song
 * @date Oct 27, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTASTManipulator extends UTASTManipulatorAbstract {
	private ASTRewrite	rewrite;

	public UTASTManipulator(boolean isLeft) {
		this.isLeft = isLeft;
	}

	public UTASTManipulator() {
	}

	/**
	 * @param unit
	 */
	@Override
	protected void beforeManipulate(CompilationUnit unit) {
		super.beforeManipulate(unit);
		rewrite = ASTRewrite.create(unit.getAST());
	}

	/**
	 * @param unit
	 */
	@Override
	protected void afterManipulate(CompilationUnit unit) {
		super.afterManipulate(unit);
		try {
			UTASTManipulatorHelper.saveASTRewriteContents(unit, rewrite);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The implementing class is by {@link UTASTBindingManagerField}.
	 * 
	 * @param manager
	 */
	@Override
	protected void replaceDeclaration(UTASTBindingManagerField manager, String newVarName) {
		replaceDeclaration(manager.getFieldDeclaration(), newVarName, manager);
	}

	@Override
	protected void replaceReference(UTASTBindingManagerField manager, String newVarName) {
		replaceReference(manager.getReferenceNodeList(), newVarName);
	}

	@Deprecated
	protected void replaceAssignment(UTASTBindingManagerField manager, String newVarName) {
		replaceAssignment(manager.getLeftHandVarAssignmentNodeList(), newVarName, true);
		replaceAssignment(manager.getRightHandVarAssignmentNodeList(), newVarName, false);
	}

	/**
	 * The implementing class is by {@link UTASTBindingManagerLocalVariable}.
	 * 
	 * @param manager
	 */
	@Override
	protected void replaceDeclaration(UTASTBindingManagerLocalVariable manager, String newVarName) {
		replaceDeclaration(manager.getVariableDeclarationFragment(), newVarName, manager);
	}

	@Override
	protected void replaceReference(UTASTBindingManagerLocalVariable manager, String newVarName) {
		replaceReference(manager.getReferenceNodeList(), newVarName);
	}

	@Deprecated
	protected void replaceAssignment(UTASTBindingManagerLocalVariable manager, String newVarName) {
		replaceAssignment(manager.getLeftHandVarAssignmentNodeList(), newVarName, true);
		replaceAssignment(manager.getRightHandVarAssignmentNodeList(), newVarName, false);
	}

	/**
	 * The implementing class is by {@link UTASTBindingManagerParameter}.
	 * 
	 * @param manager
	 */
	@Override
	protected void replaceDeclaration(UTASTBindingManagerParameter manager, String newVarName) {
		replaceDeclaration(manager.getParameterDeclaration(), newVarName, manager);
	}

	@Override
	protected void replaceReference(UTASTBindingManagerParameter manager, String newVarName) {
		replaceReference(manager.getReferenceNodeList(), newVarName);
	}

	@Deprecated
	protected void replaceAssignment(UTASTBindingManagerParameter manager, String newVarName) {
		replaceAssignment(manager.getLeftHandVarAssignmentNodeList(), newVarName, true);
		replaceAssignment(manager.getRightHandVarAssignmentNodeList(), newVarName, false);
	}

	/**
	 * The implementing class is by {@link UTASTBindingManagerForStatementEnh}.
	 * 
	 * @param manager
	 */
	@Override
	protected void replaceDeclaration(UTASTBindingManagerForStatementEnh manager, String newVarName) {
		replaceDeclaration(manager.getParameterDeclaration(), newVarName, manager);
	}

	@Override
	protected void replaceReference(UTASTBindingManagerForStatementEnh manager, String newVarName) {
		replaceReference(manager.getReferenceNodeList(), newVarName);
	}

	/**
	 * The implementing class is by {@link UTASTBindingManagerForStatement}.
	 * 
	 * @param manager
	 */
	@Override
	protected void replaceDeclaration(UTASTBindingManagerForStatement manager, String newVarName) {
		replaceDeclaration(manager.getVariableDeclarationFragment(), newVarName, manager);
	}

	@Override
	protected void replaceReference(UTASTBindingManagerForStatement manager, String newVarName) {
		replaceReference(manager.getReferenceNodeList(), newVarName);
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////

	private void replaceDeclaration(VariableDeclaration oldDeclFragment, //
			String newVarName, UTASTBindingManagerAbstract manager) {
		// SimpleName oldDeclVarName = oldDeclFragment.getName();
		SimpleName newDeclVarNode = oldDeclFragment.getAST().newSimpleName(newVarName);
		manager.setNewAbstractNode(newDeclVarNode);

		// if (!withinSelection(oldDeclVarName)) {
		// return;
		// }
		// try {
		// rewrite.replace(oldDeclVarName, newDeclVarNode, null);
		// } catch (IllegalArgumentException e) {
		// // e.printStackTrace();
		// }
	}

	@SuppressWarnings("unused")
	private void replaceReference(List<SimpleName> varRefNodeList, String newVarName) {
		for (int i = 0; i < varRefNodeList.size(); i++) {
			SimpleName oldVarRefNode = varRefNodeList.get(i);
			SimpleName newVarRefNode = oldVarRefNode.getAST().newSimpleName(newVarName);
			// TODO

			// if (!withinSelection(oldVarRefNode)) {
			// continue;
			// }
			// try {
			// rewrite.replace(oldVarRefNode, newVarRefNode, null);
			// } catch (IllegalArgumentException e) {
			// // e.printStackTrace();
			// }
		}
	}

	private void replaceAssignment(List<Assignment> varAsgmNodeList, String newVarName, boolean isLeft) {
		for (int i = 0; i < varAsgmNodeList.size(); i++) {
			Assignment oldVarAsgmNode = varAsgmNodeList.get(i);

			if (!withinSelection(oldVarAsgmNode)) {
				continue;
			}
			Expression oldExpr = null;
			if (isLeft) {
				oldExpr = oldVarAsgmNode.getLeftHandSide();
			} else {
				oldExpr = oldVarAsgmNode.getRightHandSide();
			}
			Expression newExpr = oldExpr.getAST().newSimpleName(newVarName);
			rewrite.replace(oldExpr, newExpr, null);
		}
	}

	private boolean withinSelection(ASTNode node) {
		Point leftSelectedRegion = UTCriticsTextSelection.leftMergeSourceViewer.getSourceViewer().getSelectedRange();
		Point rightSelectedRegion = UTCriticsTextSelection.rightMergeSourceViewer.getSourceViewer().getSelectedRange();

		int offset_bgn = -1, offset_end = -1, offset_sel_bgn = -1, offset_sel_end = -1;
		if (leftSelectedRegion != null && rightSelectedRegion != null) {
			offset_bgn = node.getStartPosition();
			offset_end = offset_bgn + node.getLength();
			if (isLeft) {
				offset_sel_bgn = leftSelectedRegion.x;
				offset_sel_end = leftSelectedRegion.x + leftSelectedRegion.y;
			} else {
				offset_sel_bgn = rightSelectedRegion.x;
				offset_sel_end = rightSelectedRegion.x + rightSelectedRegion.y;
			}
			if ((offset_sel_bgn <= offset_bgn && offset_sel_bgn <= offset_end) && //
					(offset_sel_end >= offset_bgn && offset_sel_end >= offset_end)) {
				return true;
			}
		}
		return false;
	}
}
