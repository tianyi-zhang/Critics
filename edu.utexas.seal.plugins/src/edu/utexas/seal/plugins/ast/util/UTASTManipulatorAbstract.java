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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;

import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerAbstract;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerForStatementEnh;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerField;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerForStatement;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerLocalVariable;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerParameter;
import edu.utexas.seal.plugins.ast.name.UTAbstractNodeName;
import edu.utexas.seal.plugins.ast.name.UTFieldName;
import edu.utexas.seal.plugins.ast.name.UTLocalVariableName;
import edu.utexas.seal.plugins.ast.name.UTParameterName;

/**
 * @author Myoungkyu Song
 * @date Oct 27, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public abstract class UTASTManipulatorAbstract {

	protected boolean	isLeft;

	/**
	 * The implementing class is by {@link UTASTBindingManagerField}.
	 * 
	 * @param manager
	 */
	protected abstract void replaceDeclaration(UTASTBindingManagerField manager, String newVarName);

	protected abstract void replaceReference(UTASTBindingManagerField manager, String newVarName);

	// protected abstract void replaceAssignment(UTASTBindingManagerField manager, String newVarName);

	/**
	 * The implementing class is by {@link UTASTBindingManagerLocalVariable}.
	 * 
	 * @param manager
	 */
	protected abstract void replaceDeclaration(UTASTBindingManagerLocalVariable manager, String newVarName);

	protected abstract void replaceReference(UTASTBindingManagerLocalVariable manager, String newVarName);

	// protected abstract void replaceAssignment(UTASTBindingManagerLocalVariable manager, String newVarName);

	/**
	 * The implementing class is by {@link UTASTBindingManagerParameter}.
	 * 
	 * @param manager
	 */
	protected abstract void replaceDeclaration(UTASTBindingManagerParameter manager, String newVarName);

	protected abstract void replaceReference(UTASTBindingManagerParameter manager, String newVarName);

	/**
	 * The implementing class is by {@link UTASTBindingManagerForStatementEnh}.
	 * 
	 * @param manager
	 */
	protected abstract void replaceDeclaration(UTASTBindingManagerForStatementEnh manager, String newVarName);

	protected abstract void replaceReference(UTASTBindingManagerForStatementEnh manager, String newVarName);

	/**
	 * The implementing class is by {@link UTASTBindingManagerForStatement}.
	 * 
	 * @param manager
	 */
	protected abstract void replaceDeclaration(UTASTBindingManagerForStatement manager, String newVarName);

	protected abstract void replaceReference(UTASTBindingManagerForStatement manager, String newVarName);

	// protected abstract void replaceAssignment(UTASTBindingManagerParameter manager, String newVarName);

	/**
	 * Called from {@link #manipulate(CompilationUnit, Collection)} before any
	 * local variable is processed. While without effect in this class,
	 * subclasses may override and for pre-processing instructions.
	 * 
	 * @param unit
	 *            the AST node that will be manipulated
	 */
	protected void afterManipulate(CompilationUnit unit) {
	}

	/**
	 * Called from {@link #manipulate(CompilationUnit, Collection)} after all
	 * local variables have been processed. While without effect in this class,
	 * subclasses may override and for post-processing instructions.
	 * 
	 * @param unit
	 *            the AST node that has been manipulated
	 */
	protected void beforeManipulate(CompilationUnit unit) {
	}

	/**
	 * Calls for every local variable handled by managers the methods {@link #addNewVarDeclaration(UTASTBindingManagerLocalVariable)} and {@link #removeOldVarDeclaration(UTASTBindingManagerLocalVariable)}. This "moves"
	 * the variable declaration statement to its intended place.
	 * 
	 * @param unit
	 *            the compilation unit that has to be manipulated
	 * @param managers
	 *            all variable binding managers for {@code unit}
	 */
	public void manipulate(final CompilationUnit unit, Collection<UTASTBindingManagerLocalVariable> managers) {
		beforeManipulate(unit);
		List<UTASTBindingManagerLocalVariable> managerList = new ArrayList<UTASTBindingManagerLocalVariable>();
		managerList.addAll(managers);
		UTAbstractNodeName nodeName = new UTLocalVariableName();

		for (int i = managerList.size() - 1; i >= 0; i--) {
			UTASTBindingManagerLocalVariable manager = managerList.get(i);
			int indexOfName = nodeName.getIndexOfName();
			String newVarName = String.format(((UTLocalVariableName) nodeName).getName(), indexOfName);
			replaceDeclaration(manager, newVarName);
			replaceReference(manager, newVarName);
			// replaceAssignment(manager, newVarName);
		}
		afterManipulate(unit);
	}

	/**
	 * 
	 * @param unit
	 * @param bindingManagerList
	 */
	public void manipulate(CompilationUnit unit, //
			List<UTASTBindingManagerAbstract> bindingManagerList) {
		UTAbstractNodeName fieldName = new UTFieldName();
		UTAbstractNodeName varName1 = new UTLocalVariableName();
		UTAbstractNodeName parName1 = new UTParameterName();
		UTAbstractNodeName varName2 = new UTLocalVariableName("q");
		UTAbstractNodeName parName2 = new UTParameterName("r");
		String newName = null;
		int idxOfName = -1;
		// beforeManipulate(unit);
		for (int i = 0; i < bindingManagerList.size(); i++) {
			UTASTBindingManagerAbstract o = bindingManagerList.get(i);
			if (o instanceof UTASTBindingManagerField) {
				idxOfName = fieldName.getIndexOfName();
				newName = String.format(fieldName.getName(), idxOfName);
				replaceDeclaration((UTASTBindingManagerField) o, newName);
				replaceReference((UTASTBindingManagerField) o, newName);
				// replaceAssignment((UTASTBindingManagerField) o, newName);
			} else if (o instanceof UTASTBindingManagerLocalVariable) {
				idxOfName = varName1.getIndexOfName();
				newName = String.format(varName1.getName(), idxOfName);
				replaceDeclaration((UTASTBindingManagerLocalVariable) o, newName);
				replaceReference((UTASTBindingManagerLocalVariable) o, newName);
				// replaceAssignment((UTASTBindingManagerLocalVariable) o, newName);
			} else if (o instanceof UTASTBindingManagerParameter) {
				idxOfName = parName1.getIndexOfName();
				newName = String.format(parName1.getName(), idxOfName);
				replaceDeclaration((UTASTBindingManagerParameter) o, newName);
				replaceReference((UTASTBindingManagerParameter) o, newName);
				// replaceAssignment((UTASTBindingManagerParameter) o, newName);
			} else if (o instanceof UTASTBindingManagerForStatementEnh) {
				idxOfName = parName2.getIndexOfName();
				newName = String.format(parName2.getName(), idxOfName);
				replaceDeclaration((UTASTBindingManagerForStatementEnh) o, newName);
				replaceReference((UTASTBindingManagerForStatementEnh) o, newName);
			} else if (o instanceof UTASTBindingManagerForStatement) {
				idxOfName = varName2.getIndexOfName();
				newName = String.format(varName2.getName(), idxOfName);
				replaceDeclaration((UTASTBindingManagerForStatement) o, newName);
				replaceReference((UTASTBindingManagerForStatement) o, newName);
			}
		}
		// afterManipulate(unit);
	}
}
