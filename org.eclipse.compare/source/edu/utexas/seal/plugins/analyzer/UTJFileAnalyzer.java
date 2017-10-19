package edu.utexas.seal.plugins.analyzer;

import java.util.List;

import org.eclipse.compare.internal.MergeSourceViewer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Point;

import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import edu.utexas.seal.plugins.ast.UTASTClassVisitor;
import edu.utexas.seal.plugins.ast.UTASTMethodVisitor;
import edu.utexas.seal.plugins.util.UTCriticsPairFileInfo;
import edu.utexas.seal.plugins.util.UTCriticsTextSelection;

/**
 * @author troy
 *
 */
public class UTJFileAnalyzer {
	MergeSourceViewer leftMSViewer;
	MergeSourceViewer rightMSViewer;
	ISourceViewer leftSRViewer;
	ISourceViewer rightSRViewer;
	Point leftSelectedRegion;
	Point rightSelectedRegion;
	ICompilationUnit cpunit_left;
	ICompilationUnit cpUnit_right;
	
	
	/**
	 * Analyzer Constructor for file in Compare View
	 * leverage UTCriticsPairFileInfo
	 */
	public UTJFileAnalyzer(){
		leftMSViewer = UTCriticsTextSelection.leftMergeSourceViewer;
		rightMSViewer = UTCriticsTextSelection.rightMergeSourceViewer;
		leftSRViewer = leftMSViewer.getSourceViewer();
		rightSRViewer = rightMSViewer.getSourceViewer();
		leftSelectedRegion = leftSRViewer.getSelectedRange();
		rightSelectedRegion = rightSRViewer.getSelectedRange();
		cpunit_left = UTCriticsPairFileInfo.getLeftICompilationUnit();
		cpUnit_right = UTCriticsPairFileInfo.getRightICompilationUnit();
	}
	
	CompilationUnit parse(ICompilationUnit lwUnit) {
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(lwUnit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null /* IProgressMonitor */);
	}
	
	
	/**
	 * Context Analysis within a single method in old version
	 */
	public List<ASTNode> analyzeNewMethod(Node root){
		CompilationUnit unit_left = parse(cpunit_left);
		UTMethodAnalyzer analyzer_left = new UTMethodAnalyzer(leftSelectedRegion, true);
		unit_left.accept(analyzer_left);
		UTASTMethodVisitor visitor_left = new UTASTMethodVisitor(analyzer_left.method);
		unit_left.accept(visitor_left);
		analyzer_left.analyze(visitor_left);
		analyzer_left.convertToNode(root);
		return analyzer_left.getRelevantNodes();
	}
	
	/**
	 * Context Analysis within a single method in new version
	 */
	public List<ASTNode> analyzeOldMethod(Node root){
		CompilationUnit unit_right = parse(cpUnit_right);
		UTMethodAnalyzer analyzer_right = new UTMethodAnalyzer(rightSelectedRegion, false);
		unit_right.accept(analyzer_right);
		UTASTMethodVisitor visitor_right = new UTASTMethodVisitor(analyzer_right.method);
		unit_right.accept(visitor_right);
		analyzer_right.analyze(visitor_right);
		analyzer_right.convertToNode(root);
		return analyzer_right.getRelevantNodes();
	}
	
	public List<ASTNode> analyzeNewClass(Node root){
		CompilationUnit unit_left = parse(cpunit_left);
		UTClassAnalyzer analyzer_left = new UTClassAnalyzer(leftSelectedRegion, true);
		unit_left.accept(analyzer_left);
		UTASTClassVisitor visitor_left = new UTASTClassVisitor();
		unit_left.accept(visitor_left);
		analyzer_left.analyze(visitor_left);
		analyzer_left.convertToNode(root);
		return analyzer_left.getRelevantNodes();
	}
	
	public List<ASTNode> analyzeOldClass(Node root){
		CompilationUnit unit_right = parse(cpUnit_right);
		UTClassAnalyzer analyzer_right = new UTClassAnalyzer(rightSelectedRegion, false);
		unit_right.accept(analyzer_right);
		UTASTClassVisitor visitor_right = new UTASTClassVisitor();
		unit_right.accept(visitor_right);
		analyzer_right.analyze(visitor_right);
		analyzer_right.convertToNode(root);
		return analyzer_right.getRelevantNodes();
	}
	
	/**
	 * Context Analysis within a Single Method
	 */
	public void methodLevel(){
		// Parse and Analyze New Version
		System.out.println("================  New Version(Method Level)  =================");
		CompilationUnit unit_left = parse(cpunit_left);
		UTMethodAnalyzer analyzer_left = new UTMethodAnalyzer(leftSelectedRegion, true);
		unit_left.accept(analyzer_left);
		UTASTMethodVisitor visitor_left = new UTASTMethodVisitor(analyzer_left.method);
		unit_left.accept(visitor_left);
		analyzer_left.analyze(visitor_left);
		System.out.println("=============================================================");
		
		System.out.println();
		System.out.println();
		System.out.println();
		
		// Parse and Analyze Old Version
		System.out.println("================  Old Version(Method Level)  =================");
		CompilationUnit unit_right = parse(cpUnit_right);
		UTMethodAnalyzer analyzer_right = new UTMethodAnalyzer(rightSelectedRegion, false);
		unit_right.accept(analyzer_right);
		UTASTMethodVisitor visitor_right = new UTASTMethodVisitor(analyzer_right.method);
		unit_right.accept(visitor_right);
		analyzer_right.analyze(visitor_right);
		System.out.println("==============================================================");
	}
	
	/**
	 * Context Analysis cross Entire Class
	 */
	public void classLevel(){
		// Parse and Analyze New Version
				System.out.println("================  New Version(Class Level)  =================");
				CompilationUnit unit_left = parse(cpunit_left);
				UTClassAnalyzer analyzer_left = new UTClassAnalyzer(leftSelectedRegion, true);
				unit_left.accept(analyzer_left);
				UTASTClassVisitor visitor_left = new UTASTClassVisitor();
				unit_left.accept(visitor_left);
				analyzer_left.analyze(visitor_left);
				System.out.println("=============================================================");
				
				System.out.println();
				System.out.println();
				System.out.println();
				
				// Parse and Analyze Old Version
				System.out.println("================  Old Version(Class Level)  =================");
				CompilationUnit unit_right = parse(cpUnit_right);
				UTClassAnalyzer analyzer_right = new UTClassAnalyzer(rightSelectedRegion, false);
				unit_right.accept(analyzer_right);
				UTASTClassVisitor visitor_right = new UTASTClassVisitor();
				unit_right.accept(visitor_right);
				analyzer_right.analyze(visitor_right);
				System.out.println("==============================================================");
	}
}
