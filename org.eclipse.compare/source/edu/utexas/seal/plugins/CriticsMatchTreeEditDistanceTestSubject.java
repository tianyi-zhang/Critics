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
