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
package edu.utexas.seal.plugins.ast.util.others;

import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;

import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerLocalVariable;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerParameter;

/**
 * @author Myoungkyu Song
 * @date Oct 31, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTASTSimpleName extends ASTVisitor {
	Map<IVariableBinding, UTASTBindingManagerLocalVariable>	variableManagers	= null;
	Map<IVariableBinding, UTASTBindingManagerParameter>		parameterManagers	= null;

	public UTASTSimpleName() {
	}

	public UTASTSimpleName(Map<IVariableBinding, UTASTBindingManagerLocalVariable> vManagers,
			Map<IVariableBinding, UTASTBindingManagerParameter> pManagers) {
		variableManagers = vManagers;
		parameterManagers = pManagers;
	}

	/**
	 * Visits {@link SimpleName} AST nodes. Resolves the binding of the simple
	 * name and looks for it in the {@link #variableManagers} map. If the
	 * binding is found, this is a reference to a local variable. The variable
	 * binding manager of this local variable then has to be informed about that
	 * reference.
	 * 
	 * @param node
	 */
	public boolean visit(SimpleName node) {
		if (node.getFullyQualifiedName().contains("users"))
			System.out.println("[DBG] * var - " + node);

		IBinding binding = node.resolveBinding();
		if (variableManagers.containsKey(binding)) {
			variableManagers.get(binding).addReferenceNodeList(node);
		}
		if (parameterManagers.containsKey(binding)) {
			parameterManagers.get(binding).addReferenceNodeList(node);
		}
		return true;
	}

	/**
	 * Getter for the resulting map.
	 * 
	 * @return a map with variable bindings as keys and {@link UTASTBindingManagerParameter} as values
	 */
	public Map<IVariableBinding, UTASTBindingManagerLocalVariable> getLocalVariableManagers() {
		return variableManagers;
	}

	public Map<IVariableBinding, UTASTBindingManagerParameter> getParameterManagers() {
		return parameterManagers;
	}

	/**
	 * 
	 * @param unit
	 *            the AST root node. Bindings have to have been resolved.
	 * @param leftSelectedRegion
	 * @param rightSelectedRegion
	 */
	public void process(CompilationUnit unit) {
		unit.accept(this);
	}
}