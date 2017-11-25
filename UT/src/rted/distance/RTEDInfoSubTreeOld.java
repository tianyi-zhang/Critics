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
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jdt.core.dom.CompilationUnit;

import rted.datastructure.LblTree;
import ut.seal.plugins.utils.UTStr;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

/**
 * @author Myoungkyu Song
 * @date Nov 12, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class RTEDInfoSubTreeOld {
	private Integer				postorderID;
	private JavaCompilation		javaCompilation;
	private CompilationUnit		cunit;
	private Node				subTree;
	private Node				queryTree;
	private Double				distance;												// sorted by distance
	private File				fullFilePath;
	private Double				similarityScoreOfLabel;
	private int					similarityScoreOfValue;
	private Double				similarityScore;
	private LinkedList<int[]>	editMapping				= new LinkedList<int[]>();
	private Map<Integer, Node>	queryTreeIDMngr			= new TreeMap<Integer, Node>();
	private Map<Integer, Node>	subTreeIDMngr			= new TreeMap<Integer, Node>();
	private LblTree				subLblTree;
	private LblTree				queryLblTree;
	private int					mGroupIDBySimilarity	= -1;

	public RTEDInfoSubTreeOld(Integer postorderID, //
			CompilationUnit javaCompilation, //
			Node firstTree, Node secondTree, //
			LblTree subLblTree, LblTree queryLblTree, //
			Double distance, //
			Double similarityScore, //
			LinkedList<int[]> editMapping, //
			Map<Integer, Node> queryTreeIDMngr, //
			Map<Integer, Node> subTreeIDMngr,//
			File fullFilePath) {
		this.postorderID = postorderID;
		this.cunit = javaCompilation;
		this.queryTree = firstTree;
		this.subTree = secondTree;
		this.subLblTree = subLblTree;
		this.queryLblTree = queryLblTree;
		this.distance = distance;
		this.similarityScore = similarityScore;
		copyListIntArray(editMapping);
		this.queryTreeIDMngr = queryTreeIDMngr;
		this.subTreeIDMngr = subTreeIDMngr;
		this.fullFilePath = fullFilePath;
	}

	public RTEDInfoSubTreeOld(Integer postorderID, //
			JavaCompilation javaCompilation, //
			Node firstTree, Node secondTree, //
			LblTree subLblTree, LblTree queryLblTree, //
			Double distance, //
			LinkedList<int[]> editMapping, //
			Map<Integer, Node> queryTreeIDMngr, //
			Map<Integer, Node> subTreeIDMngr, //
			File fullFilePath) {
		this.postorderID = postorderID;
		this.javaCompilation = javaCompilation;
		this.queryTree = firstTree;
		this.subTree = secondTree;
		this.subLblTree = subLblTree;
		this.queryLblTree = queryLblTree;
		this.distance = distance;
		copyListIntArray(editMapping);
		this.queryTreeIDMngr = queryTreeIDMngr;
		this.subTreeIDMngr = subTreeIDMngr;
		this.fullFilePath = fullFilePath;
	}

	public RTEDInfoSubTreeOld(Integer postorderID, //
			CompilationUnit javaCompilation, //
			Node firstTree, Node secondTree, //
			LblTree subLblTree, LblTree queryLblTree, //
			Double distance, //
			LinkedList<int[]> editMapping, //
			Map<Integer, Node> queryTreeIDMngr, //
			Map<Integer, Node> subTreeIDMngr,//
			File fullFilePath) {
		this.postorderID = postorderID;
		this.cunit = javaCompilation;
		this.queryTree = firstTree;
		this.subTree = secondTree;
		this.subLblTree = subLblTree;
		this.queryLblTree = queryLblTree;
		this.distance = distance;
		copyListIntArray(editMapping);
		this.queryTreeIDMngr = queryTreeIDMngr;
		this.subTreeIDMngr = subTreeIDMngr;
		this.fullFilePath = fullFilePath;
	}

	void copyListIntArray(LinkedList<int[]> srcList) {
		for (int[] elem : srcList) {
			editMapping.add(elem);
		}
	}

	public Integer getPostorderID() {
		return postorderID;
	}

	public JavaCompilation getJavaCompilation() {
		return javaCompilation;
	}

	public Node getSubTree() {
		return subTree;
	}

	public Node getQueryTree() {
		return queryTree;
	}

	public Double getDistance() {
		return distance;
	}

	public File getFullFilePath() {
		return fullFilePath;
	}

	public LinkedList<int[]> getEditMapping() {
		return editMapping;
	}

	public Map<Integer, Node> getQueryTreeIDMngr() {
		return queryTreeIDMngr;
	}

	public Map<Integer, Node> getSubTreeIDMngr() {
		return subTreeIDMngr;
	}

	public Double getSimilarityScoreOfLabel() {
		return similarityScoreOfLabel;
	}

	public void setSimilarityScoreOfLabel(Double similarityScoreOfLabel) {
		this.similarityScoreOfLabel = similarityScoreOfLabel;
	}

	public int getSimilarityScoreOfValue() {
		return this.similarityScoreOfValue;
	}

	public void setSimilarityScoreOfValue(int similarityScoreOfValue) {
		this.similarityScoreOfValue = similarityScoreOfValue;
	}

	public Double getSimilarityScore() {
		return this.similarityScore;
	}

	public void setSimilarityScore(Double similarityScore) {
		this.similarityScore = similarityScore;
	}

	public LblTree getSubLblTree() {
		return subLblTree;
	}

	public LblTree getQueryLblTree() {
		return queryLblTree;
	}

	public String toString() {
		return "TID:" + UTStr.getIndentR(postorderID + ", ", 6) + //
				"  SIM: " + UTStr.getIndentR(similarityScore + ", ", 10) + //
				"(" + subTree.getLabel() + ")(" + subTree.getValue() + ")";
	}

	public void setGroupIDBySimilarity(int aGroupID) {
		mGroupIDBySimilarity = aGroupID;
	}

	public int getGroupIDBySimilarity() {
		return mGroupIDBySimilarity;
	}

	public CompilationUnit getCunit() {
		return cunit;
	}

	public void setCunit(CompilationUnit cunit) {
		this.cunit = cunit;
	}
}
