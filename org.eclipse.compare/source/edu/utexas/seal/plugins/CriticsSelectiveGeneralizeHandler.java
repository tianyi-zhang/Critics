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
package edu.utexas.seal.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import edu.utexas.seal.plugins.ast.UTASTVisitor;
import edu.utexas.seal.plugins.ast.UTCriticsAST;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerAbstract;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerForStatementEnh;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerField;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerForStatement;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerLocalVariable;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerParameter;
import edu.utexas.seal.plugins.ast.util.UTASTManipulator;
import edu.utexas.seal.plugins.util.UTCriticsPairFileInfo;
import edu.utexas.seal.plugins.util.UTRepository;

/**
 * @author Myoungkyu Song
 * @date Oct 25, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsSelectiveGeneralizeHandler extends CriticsAbstract {
	boolean																__$d__					= false;
	private CriticsSelectionHandler										selectionHandler		= null;
	private List<UTASTBindingManagerAbstract>							bindingManagerList		= null;
	private Map<IVariableBinding, UTASTBindingManagerField>				fieldManagers			= null;
	private Map<IVariableBinding, UTASTBindingManagerLocalVariable>		localVariableManagers	= null;
	private Map<IVariableBinding, UTASTBindingManagerParameter>			parameterManagers		= null;
	private Map<IVariableBinding, UTASTBindingManagerForStatementEnh>	forStatementEnhManagers	= null;
	private Map<IVariableBinding, UTASTBindingManagerForStatement>		forStatementManagers	= null;

	/**
	 * 
	 * @return
	 */
	protected boolean initiate() {
		if (super.initiate()) {
			__$d__ = true;
			System.out.println("DBG__________________________________________");
			System.out.println("[DBG] SELECTIVE GENERALIZATION");
			System.out.println("DBG__________________________________________");
			selectionHandler = new CriticsSelectionHandler();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param event
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (!initiate()) {
			return null;
		}
		String projectName = null, packageName = null, className = null, source = null;
		UTRepository.createVirtualProjects(projectName, packageName, className, source);
		leftSRViewer.getDocument().get();

		if (event.getCommand().getId().endsWith("Begin")) {
			selectionHandler.getEditManager().initiate();
		}

		selectionHandler.printSelectionInfo();
		// generalizeHelper.ccfinder();
		System.out.println("DBG__________________________________________");
		System.out.println("[DBG] DIFF B/W TWO SELECTIONS:");
		System.out.println("DBG__________________________________________");
		selectionHandler.differentiateSelectionOfComparisonView();
		System.out.println("DBG__________________________________________");
		System.out.println("[DBG] GENERALIZE @LEFT SELECTION:");
		System.out.println("DBG__________________________________________");
		generalizeSelectionOfLeftView();
		System.out.println("DBG__________________________________________");
		System.out.println("[DBG] GENERALIZE #RIGHT SELECTION:");
		System.out.println("DBG__________________________________________");
		generalizeSelectionOfRightView();
		System.out.println("DBG__________________________________________");
		return null;
	}

	/**
	 * left view
	 */
	void generalizeSelectionOfLeftView() {
		bindingManagerList = new ArrayList<UTASTBindingManagerAbstract>();
		try {
			String className = UTCriticsPairFileInfo.getLeftFilePath().substring(
					UTCriticsPairFileInfo.getLeftFilePath().lastIndexOf('/') + 1);
			generalizeSelection(UTCriticsPairFileInfo.getLeftProjectName(), //
					UTCriticsPairFileInfo.getLeftIPackageFragment().getElementName(), //
					className, UTCriticsPairFileInfo.getLeftICompilationUnit().getSource(), true);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * right view
	 */
	void generalizeSelectionOfRightView() {
		bindingManagerList = new ArrayList<UTASTBindingManagerAbstract>();
		if (__$d__) {
			try {
				String className = UTCriticsPairFileInfo.getRightFilePath().substring(
						UTCriticsPairFileInfo.getRightFilePath().lastIndexOf('/') + 1);
				generalizeSelection(UTCriticsPairFileInfo.getRightProjectName(), //
						UTCriticsPairFileInfo.getRightIPackageFragment().getElementName(), className, //
						UTCriticsPairFileInfo.getRightICompilationUnit().getSource(), false);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param projectName
	 * @param packageName
	 * @param className
	 * @param source
	 * @param isLeft
	 * @throws CoreException
	 */
	void generalizeSelection(String projectName, String packageName, String className, //
			String source, boolean isLeft) throws CoreException {
		ICompilationUnit cpUnit = UTCriticsAST.copy(projectName, packageName, className, source, isLeft);
		CompilationUnit unit = parse(cpUnit);
		UTASTVisitor visitor = null;
		visitor = new UTASTVisitor(leftSelectedRegion, rightSelectedRegion, isLeft);
		visitor.process(unit);
		rewrite(unit, visitor, isLeft);
		displayMappingInfo(cpUnit);
		displayGeneralizationInfo(cpUnit, isLeft);
	}

	/**
	 * 
	 * @param cpUnit
	 * @throws JavaModelException
	 */
	void displayMappingInfo(ICompilationUnit cpUnit) throws JavaModelException {
		// if (!__$d__) {
		// System.out.println("=#========================================");
		// System.out.println(cpUnit.getSource());
		// System.out.println("=#========================================");
		// }
		for (int i = 0; i < bindingManagerList.size(); i++) {
			UTASTBindingManagerAbstract m = bindingManagerList.get(i);
			if (m instanceof UTASTBindingManagerField) {
				displayMappingInfo((UTASTBindingManagerField) m, //
						((UTASTBindingManagerField) m).getFieldDeclaration());
			} else if (m instanceof UTASTBindingManagerLocalVariable) {
				displayMappingInfo((UTASTBindingManagerLocalVariable) m, //
						((UTASTBindingManagerLocalVariable) m).getVariableDeclarationFragment());
			} else if (m instanceof UTASTBindingManagerParameter) {
				displayMappingInfo((UTASTBindingManagerParameter) m, //
						((UTASTBindingManagerParameter) m).getParameterDeclaration());
			} else if (m instanceof UTASTBindingManagerForStatementEnh) {
				displayMappingInfo((UTASTBindingManagerForStatementEnh) m, //
						((UTASTBindingManagerForStatementEnh) m).getParameterDeclaration());
			} else if (m instanceof UTASTBindingManagerForStatement) {
				displayMappingInfo((UTASTBindingManagerForStatement) m, //
						((UTASTBindingManagerForStatement) m).getVariableDeclarationFragment());
			}
		}
		System.out.println("DBG__________________________________________");
	}

	/**
	 * 
	 * @param manager
	 * @param varDeclFrag
	 */
	void displayMappingInfo(UTASTBindingManagerAbstract manager, VariableDeclaration varDeclFrag) {
		SimpleName newVarNode = manager.getNewAbstractNode();
		if (newVarNode == null)
			return;
		String oldName = varDeclFrag.getName().getFullyQualifiedName();
		String newName = newVarNode.getFullyQualifiedName();
		String fullyQualifiedOldName = varDeclFrag.getName().toString();
		int offsetBgnOfOldName = varDeclFrag.getName().getStartPosition();
		int offsetEndOfOldName = offsetBgnOfOldName + varDeclFrag.getName().getLength();
		String msg = "%s <---> %s (%s, %d, %d)";
		msg = String.format(msg, newName, oldName, fullyQualifiedOldName, offsetBgnOfOldName, offsetEndOfOldName);
		System.out.println("[DBG] " + msg);
	}

	/**
	 * 
	 * @param cpUnit
	 * @param isLeft
	 * @throws CoreException
	 */
	void displayGeneralizationInfo(ICompilationUnit cpUnit, boolean isLeft) throws CoreException {
		int copylen = cpUnit.getSource().length();
		if (isLeft) {
			int difflen = copylen - leftSRViewer.getDocument().get().length();
			System.out.println("[DBG] "
					+ cpUnit.getSource().substring(this.leftSelectedRegion.x,
							this.leftSelectedRegion.x + this.leftSelectedRegion.y + difflen));
			UTCriticsAST.getLeftCopyIProject().delete(true, null);
		} else {
			int difflen = copylen - rightSRViewer.getDocument().get().length();
			if (__$d__)
				System.out.println("[DBG] "
						+ cpUnit.getSource().substring(this.rightSelectedRegion.x,
								this.rightSelectedRegion.x + this.rightSelectedRegion.y + difflen));
			UTCriticsAST.getRightCopyIProject().delete(true, null);
		}
	}

	/**
	 * 
	 * @param lwUnit
	 * @return
	 */
	CompilationUnit parse(ICompilationUnit lwUnit) {
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(lwUnit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null);
	}

	/**
	 * 
	 * @param unit
	 * @param visitor
	 * @param localVariableManagers
	 */
	void rewrite(CompilationUnit unit, UTASTVisitor visitor, boolean isLeft) {
		fieldManagers = visitor.getFieldManagers();
		localVariableManagers = visitor.getLocalVariableManagers();
		parameterManagers = visitor.getParameterManagers();
		forStatementEnhManagers = visitor.getForStatEnhManagers();
		forStatementManagers = visitor.getForStatManagers();

		bindingManagerList.addAll(fieldManagers.values());
		bindingManagerList.addAll(localVariableManagers.values());
		bindingManagerList.addAll(parameterManagers.values());
		bindingManagerList.addAll(forStatementEnhManagers.values());
		bindingManagerList.addAll(forStatementManagers.values());
		new UTASTManipulator(isLeft).manipulate(unit, bindingManagerList);
	}
}
