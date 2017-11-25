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

import java.util.Collection;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

import edu.utexas.seal.plugins.ast.UTASTVisitor;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerLocalVariable;
import edu.utexas.seal.plugins.ast.util.UTASTManipulator;

/**
 * Main class of the "Move a variable declaration" example of the article. It
 * moves local variable declarations as near as possible to their first
 * reference. For more details check out in the article the section "Example" or
 * have a look at <a target="_blank"
 * href="http://www.javapractices.com/Topic126.cjp">Java Practices</a>. The
 * process is initiated by calling {@link #run(ICompilationUnit)}.
 * 
 * <p>
 * Project page: <a target="_blank" href="http://sourceforge.net/projects/earticleast">http://sourceforge.net/projects/earticleast</a>
 * </p>
 * 
 * @author Thomas Kuhn
 */
public class ASTMoveVariable extends ASTAbstract {

	/*
	 * (non-Javadoc)
	 *
	 * @see net.sourceforge.earticleast.app.AbstractASTArticle#run(org.eclipse.jdt.core.ICompilationUnit)
	 */
	public void run(ICompilationUnit lwUnit) {
		CompilationUnit unit = parse(lwUnit);
		UTASTVisitor localVariableDetector = new UTASTVisitor();
		localVariableDetector.process(unit);
		rewrite(unit, localVariableDetector.getLocalVariableManagers(), true);

	}

	/**
	 * After all information about the local variables has been collected, the
	 * compilation unit has to be modified. There are two ways to modify the
	 * compilation unit. Both of them lead to the same result. Switch betweeen
	 * one and the other by instantiating eiter {@link UTASTManipulator} or {@link ASTDirectManipulator}:
	 * <ol>
	 * <li>Using {@link ASTRewrite}: instantiate {@link UTASTManipulator}</li>
	 * <li>Directly modifying the node: instantiate {@link ASTDirectManipulator}</li>
	 * </ol>
	 * 
	 * @param unit
	 *            AST root node that has to be modified
	 * @param localVariableManagers
	 *            collected information about the local variables
	 */
	private void rewrite(CompilationUnit unit, Map<IVariableBinding, //
			UTASTBindingManagerLocalVariable> localVariableManagers, boolean isLeft) {
		Collection<UTASTBindingManagerLocalVariable> managers = localVariableManagers.values();

		// new ASTDirectManipulator().manipulate(unit, managers);
		new UTASTManipulator(isLeft).manipulate(unit, managers);

	}
}
