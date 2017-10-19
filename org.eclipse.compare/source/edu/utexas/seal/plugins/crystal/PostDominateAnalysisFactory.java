/*
 * @(#) PostDominateAnalysisFactory.java
 *
 * Copyright 2013 2014 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.crystal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.annotations.AnnotationDatabase;
import edu.cmu.cs.crystal.IAnalysisInput;
import edu.cmu.cs.crystal.internal.StandardAnalysisReporter;
import edu.cmu.cs.crystal.tac.eclipse.CompilationUnitTACs;
import edu.cmu.cs.crystal.util.Option;
import edu.utexas.seal.plugins.ast.util.UTASTHelper;

public class PostDominateAnalysisFactory {
	private static PostDominateAnalysisFactory	instance	= new PostDominateAnalysisFactory();

	public static PostDominateAnalysisFactory getInstance() {
		return instance;
	}

	public PostDominateElementResult getAnalysisResultForMethod(ICompilationUnit icu, MethodDeclaration md) {
		CompilationUnit cu = (CompilationUnit) UTASTHelper.getASTNodeFromCompilationUnit(icu);

		final CompilationUnitTACs compUnitTacs = new CompilationUnitTACs();
		IAnalysisInput input = new IAnalysisInput() {
			private Option<IProgressMonitor>	mon	= Option.wrap(null);

			public AnnotationDatabase getAnnoDB() {
				return null;
			}

			public Option<CompilationUnitTACs> getComUnitTACs() {
				return Option.some(compUnitTacs);
			}

			public Option<IProgressMonitor> getProgressMonitor() {
				return mon;
			}
		};
		PostDominateAnalysis analysis = new PostDominateAnalysis(md);
		analysis.runAnalysis(new StandardAnalysisReporter(), input, icu, cu);
		cu = null;
		return analysis.getResult();
	}
}
