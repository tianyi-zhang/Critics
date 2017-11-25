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
package rted.distance;

import java.io.File;
import java.util.Enumeration;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.swt.graphics.Point;

import ut.seal.plugins.utils.UTFile;
import ut.seal.plugins.utils.UTStr;
import ut.seal.plugins.utils.ast.UTASTNodeConverter;
import ut.seal.plugins.utils.ast.UTASTNodeFinder;
import ut.seal.plugins.utils.ast.UTASTParser;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.EntityType;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

import org.junit.Test;

/**
 * @author Tianyi Zhang
 * @date Feb 12, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class RTEDInfoSubTree {
	private Integer		id;
	private EntityType	qTreeLabel;
	private String		qTreeValue;
	private int			qTreeBgn;
	private int			qTreeEnd;
	private EntityType	tTreeLabel;
	private String		tTreeValue;
	private int			tTreeBgn;
	private int			tTreeEnd;
	private File		tTreeFilePath;
	private Double		distance;
	private Double		similarity;
	private boolean		mAnomaly;

	public RTEDInfoSubTree(Integer postorderID, Node qTree, Node tTree, Double distance, //
			Double similarityScore, File fullFilePath) {
		this.id = postorderID;
		this.qTreeLabel = qTree.getLabel();
		this.qTreeValue = qTree.getValue();
		this.qTreeBgn = qTree.getEntity().getStartPosition();
		this.qTreeEnd = qTree.getEntity().getEndPosition();
		this.tTreeLabel = tTree.getLabel();
		this.tTreeValue = tTree.getValue();
		this.tTreeBgn = tTree.getEntity().getStartPosition();
		this.tTreeEnd = tTree.getEntity().getEndPosition();
		this.distance = distance;
		this.similarity = similarityScore;
		this.tTreeFilePath = fullFilePath;
	}

	public Integer getPostorderID() {
		return id;
	}

	public Double getDistance() {
		return distance;
	}

	public File getFullFilePath() {
		return tTreeFilePath;
	}

	public Double getSimilarityScore() {
		return this.similarity + this.distance;
	}

	public Double getSimilarity() {
		return this.similarity;
	}

	public String getSubTreeLable() {
		return this.tTreeLabel.toString();
	}

	public String getSubTreeValue() {
		return this.tTreeValue;
	}

	public EntityType getQTreeLabel() {
		return qTreeLabel;
	}

	public String getQTreeValue() {
		return qTreeValue;
	}

	public void setSimilarityScore(Double similarityScore) {
		this.similarity = similarityScore;
	}

	public void setAnomaly(boolean anomaly) {
		mAnomaly = anomaly;
	}

	public boolean isAnomaly() {
		return mAnomaly;
	}

	public String toString() {
		return "TID:" + UTStr.getIndentR(id + ", ", 6) + //
				"  SIM: " + UTStr.getIndentR((this.similarity + this.distance) + ", ", 10);
	}

	public CompilationUnit getCunit() {
		String sourceCode = UTFile.getContents(tTreeFilePath.getAbsolutePath());
		UTASTParser astParser = new UTASTParser();
		CompilationUnit unit = astParser.parse(sourceCode);
		return unit;
	}

	public Node getSubTree() {
		Node tTree = transformHelper(this.tTreeBgn, this.tTreeEnd);
		tTree.setFilePath(tTreeFilePath);
		tTree.setClassName(tTreeFilePath.getName());
		tTree.setPackageName(getPackageName(tTreeFilePath));
		tTree.setAnomaly(mAnomaly);
		return tTree;
	}

	public Node getQueryTree() {
		Node qTree = transformHelper(this.qTreeBgn, this.qTreeEnd);
		qTree.setFilePath(tTreeFilePath);
		qTree.setClassName(tTreeFilePath.getName());
		qTree.setPackageName(getPackageName(tTreeFilePath));
		return qTree;
	}

	/**
	 * This method helps to transform the start and end position of source code to Node object
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	private Node transformHelper(int start, int end) {
		UTASTNodeFinder methodFinder = new UTASTNodeFinder();
		MethodDeclaration method = methodFinder.findMethod(this.tTreeFilePath.getAbsolutePath(), new Point(start, end - start + 1), false);
		String src = UTFile.getContents(this.tTreeFilePath.getAbsolutePath());
		UTASTNodeConverter converter = new UTASTNodeConverter();
		Node methodNode = converter.convertMethod(method, src, tTreeFilePath);
		Enumeration<?> e = methodNode.breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			int iBgn = iNode.getEntity().getStartPosition();
			int iEnd = iNode.getEntity().getEndPosition();
			if (iBgn == start && iEnd == end) {
				return iNode;
			}
		}
		throw new RuntimeException();
	}

	private String getPackageName(File file) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		File workspaceDirectory = workspace.getRoot().getLocation().toFile();
		String rootPath = workspaceDirectory.getAbsolutePath();
		String filePath = file.getAbsolutePath();
		int offset = rootPath.length();
		String pkgName = filePath.substring(offset + 1, filePath.length() - file.getName().length() - 1);
		int index = pkgName.indexOf("/");
		return pkgName.substring(index + 1);
	}

	@Test
	public void testGetPackageName() {
		String rootPath = "/home/troy/WorkSpaceEval";
		String filePath = "/home/troy/WorkSpaceEval/JDT9800/compiler/org/eclipse/jdt/internal/compiler/parser/Scanner.java";
		String className = "Scanner.java";
		int offset = rootPath.length();
		String pkgName = filePath.substring(offset + 1, filePath.length() - className.length() - 1);
		int index = pkgName.indexOf("/");
		System.out.println(pkgName.substring(index));
	}
}
