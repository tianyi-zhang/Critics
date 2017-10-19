/*
 * @(#) LblTreeSubRoot.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
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
