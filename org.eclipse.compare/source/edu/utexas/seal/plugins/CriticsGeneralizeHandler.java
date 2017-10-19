/*
 * @(#) CriticsGeneralizeHandler.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins;

import java.util.Collection;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jface.text.Document;

import edu.utexas.seal.plugins.ast.UTASTVisitor;
import edu.utexas.seal.plugins.ast.UTCriticsAST;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerLocalVariable;
import edu.utexas.seal.plugins.ast.util.UTASTManipulator;
import edu.utexas.seal.plugins.ast.util.others.ASTMoveVariable;
import edu.utexas.seal.plugins.ast.util.others.ASTAbstract;
import edu.utexas.seal.plugins.util.UTCriticsPairFileInfo;

/**
 * @author Myoungkyu Song
 * @date Oct 25, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsGeneralizeHandler extends AbstractHandler {
	/**
	 * @param event
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("DBG__________________________________________");
		System.out.println("[DBG] GENERALIZE");
		System.out.println("DBG__________________________________________");
		execute();
		return null;
	}

	/**
	 * 
	 */
	private void execute() {
		if (UTCriticsPairFileInfo.getLeftIPackageFragment() == null) {
			System.out.println("[USG] PLEASE OPEN A COMPARE VIEW & EDITOR.");
			return;
		}
		try {
			// left view
			String className = UTCriticsPairFileInfo.getLeftFilePath().substring(
					UTCriticsPairFileInfo.getLeftFilePath().lastIndexOf('/') + 1);
			generalize(UTCriticsPairFileInfo.getLeftProjectName(), //
					UTCriticsPairFileInfo.getLeftIPackageFragment().getElementName(), //
					className, UTCriticsPairFileInfo.getLeftICompilationUnit().getSource(), true);
			// right view
			className = UTCriticsPairFileInfo.getRightFilePath().substring(
					UTCriticsPairFileInfo.getRightFilePath().lastIndexOf('/') + 1);
			generalize(UTCriticsPairFileInfo.getRightProjectName(), //
					UTCriticsPairFileInfo.getRightIPackageFragment().getElementName(), className, //
					UTCriticsPairFileInfo.getRightICompilationUnit().getSource(), false);
		} catch (CoreException e) {
			e.printStackTrace();
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
	void generalize(String projectName, String packageName, String className, //
			String source, boolean isLeft) throws CoreException {
		ICompilationUnit cpUnit = UTCriticsAST.copy(projectName, packageName, className, source, isLeft);
		CompilationUnit unit = parse(cpUnit);
		UTASTVisitor localVariableDetector = new UTASTVisitor();
		localVariableDetector.process(unit);
		rewrite(unit, localVariableDetector.getLocalVariableManagers(), isLeft);
		System.out.println("[DBG] " + cpUnit.getSource());
		if (isLeft)
			UTCriticsAST.getLeftCopyIProject().delete(true, null);
		else
			UTCriticsAST.getRightCopyIProject().delete(true, null);
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
		return (CompilationUnit) parser.createAST(null /* IProgressMonitor */);
	}

	/**
	 * 
	 * @param unit
	 * @param localVariableManagers
	 */
	void rewrite(CompilationUnit unit, Map<IVariableBinding, // 
			UTASTBindingManagerLocalVariable> localVariableManagers, boolean isLeft) {
		Collection<UTASTBindingManagerLocalVariable> managers = localVariableManagers.values();
		new UTASTManipulator(isLeft).manipulate(unit, managers);

	}

	void getProjectInfo(IProject project) throws CoreException, JavaModelException {
		System.out.println("[DBG] Working in project " + project.getName());
		if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
			IJavaProject javaProject = JavaCore.create(project);
			printPackageInfos(javaProject);
		}
	}

	void printPackageInfos(IJavaProject javaProject) throws JavaModelException {
		IPackageFragment[] packages = javaProject.getPackageFragments();
		for (IPackageFragment mypackage : packages) {
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				System.out.println("Package " + mypackage.getElementName());
				printICompilationUnitInfo(mypackage);
			}
		}
	}

	private void printICompilationUnitInfo(IPackageFragment mypackage) throws JavaModelException {
		for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
			printCompilationUnitDetails(unit);

		}
	}

	private void printCompilationUnitDetails(ICompilationUnit unit) throws JavaModelException {
		System.out.println("Source file " + unit.getElementName());
		Document doc = new Document(unit.getSource());
		System.out.println("Has number of lines: " + doc.getNumberOfLines());
		printIMethods(unit);
	}

	private void printIMethods(ICompilationUnit unit) throws JavaModelException {
		IType[] allTypes = unit.getAllTypes();
		for (IType type : allTypes) {
			printIMethodDetails(type);
		}
	}

	private void printIMethodDetails(IType type) throws JavaModelException {
		IMethod[] methods = type.getMethods();
		for (IMethod method : methods) {

			System.out.println("Method name " + method.getElementName());
			System.out.println("Signature " + method.getSignature());
			System.out.println("Return Type " + method.getReturnType());

		}
	}

	ASTAbstract createActionExuecutable() {
		return new ASTMoveVariable();
	}

	// @SuppressWarnings("unused")
	// private static CompilationUnit parse(ICompilationUnit unit) {
	// ASTParser parser = ASTParser.newParser(AST.JLS4);
	// parser.setKind(ASTParser.K_COMPILATION_UNIT);
	// parser.setSource(unit);
	// parser.setResolveBindings(true);
	// return (CompilationUnit) parser.createAST(null); // parse
	// }
	//
	// private static CompilationUnit parse(String buf) {
	// ASTParser parser = ASTParser.newParser(AST.JLS4);
	// parser.setKind(ASTParser.K_COMPILATION_UNIT);
	// parser.setSource(buf.toCharArray());
	// parser.setResolveBindings(true);
	// return (CompilationUnit) parser.createAST(null); // parse
	// }
}
// String content = "class A {" + "\n" //
// + "void foo() {" + "\n" //
// + "int x=1, y=2;" + "\n" //
// + "}" + "\n" //
// + "}";
// ICompilationUnit cu = fragment.createCompilationUnit("A.java", content, false, null);
//
// int start = content.indexOf("String");
// int length = "String".length();
// IJavaElement[] declarations = cu.codeSelect(start, length);
// Get the root of the workspace
