/*
 * @(#) UTJDTParser.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package ut.seal.plugins.utils.ast;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * @author Myoungkyu Song
 * @date Nov 20, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTASTParser {
	/**
	 * 
	 * @param icu
	 * @return
	 */
	public CompilationUnit parse(ICompilationUnit icu) {
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(icu);
		parser.setResolveBindings(true);
		CompilationUnit c = (CompilationUnit) parser.createAST(null);
		return c;
	}

	/**
	 * 
	 * @param codeText
	 * @return
	 */
	public CompilationUnit parse(String codeText) {
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(codeText.toCharArray());
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null);
	}

	/**
	 * 
	 * @param icu
	 * @param monitor
	 * @return
	 */
	public CompilationUnit parse(ICompilationUnit icu, IProgressMonitor monitor) {
		final ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(icu);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(monitor);
	}
}
