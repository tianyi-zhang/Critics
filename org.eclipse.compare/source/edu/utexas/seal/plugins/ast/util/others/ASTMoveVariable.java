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
