/*
 * @(#) CriticsMatchTreeEditDistanceTestSubject.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins;

import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.swt.graphics.Point;

import edu.utexas.seal.plugins.util.visitor.UTGeneralVisitor;
import edu.utexas.seal.plugins.util.visitor.UTIGeneralVisitor;
import ut.seal.plugins.utils.ast.UTASTNodeConverterManager;
import ut.seal.plugins.utils.ast.UTASTNodeFinder;

/**
 * @author Myoungkyu Song
 * @date Nov 21, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsMatchTreeEditDistanceTestSubject {
	/**
	 * 
	 * @param coveredNode
	 * @param nodeList
	 * @param nodeFinder
	 * @param selectedRegion
	 * @param iCompilationUnit
	 */
	public static void getRelevantContextNodes( //
			List<ASTNode> nodeList, UTASTNodeFinder nodeFinder, //
			Point selectedRegion, ICompilationUnit iCompilationUnit) {
		ASTNode coveredNode = nodeFinder.findCoveredASTNode( //
				iCompilationUnit, selectedRegion);
		ASTNode coveredNodeTmp = nodeFinder.findCoveringASTNode( // /*- Testing Code -*/
				iCompilationUnit, new Point(346, 10));
		nodeList.add(coveredNode);
		nodeList.add(coveredNodeTmp);
	}

	public static void getRelevantContextNodes(ICompilationUnit c, UTASTNodeFinder finder, //
			List<ASTNode> nodes, Point selectedRegion, Point[] otherRegion) {
		int endPoint = selectedRegion.x + selectedRegion.y;
		Point selection = new Point(selectedRegion.x, selectedRegion.y);
		int cursor = -1;
		while (cursor <= endPoint) {
			ASTNode coveredNode = finder.findCoveredASTNode(c, selection);
			if (coveredNode == null)
				break;
			cursor = coveredNode.getStartPosition() + coveredNode.getLength();
			if (coveredNode != null)
				nodes.add(coveredNode);
			selection.x = cursor;
			selection.y = endPoint - cursor;
		}
		for (int i = 0; i < otherRegion.length; i++) {
			ASTNode n = finder.findCoveringASTNode(c, otherRegion[i]);
			if (n != null)
				nodes.add(n);
		}
	}

	public static void visitMethodDeclaration(String fullFileName, //
			ASTNode compilationUnit, List<UTASTNodeConverterManager> nodeConverterMngr) {
		if (fullFileName.equals("/Users/mksong/runtime-Critics/" //
				+ "org.eclipse.jdt.core-R4_3_1/" //
				+ "antadapter/org/eclipse/jdt/internal/antadapter/" //
				+ "AntAdapterMessages.java")) {
			System.out.println("[DBG] " + fullFileName);
		}
		UTIGeneralVisitor<MethodDeclaration> myVisitor = //
		new UTGeneralVisitor<MethodDeclaration>() {
			@Override
			public boolean visit(MethodDeclaration node) {
				this.results.add(node);
				return true;
			}
		};
		compilationUnit.accept(myVisitor);
		List<MethodDeclaration> mList = myVisitor.getResults();
		if (mList.size() != nodeConverterMngr.size()) {
			System.out.println("[DBG] " + mList.size());
			System.out.println("[DBG] " + nodeConverterMngr.size());
			System.out.println("[DBG] " + fullFileName);
			System.out.println("==========================================");
		}
	}
}
