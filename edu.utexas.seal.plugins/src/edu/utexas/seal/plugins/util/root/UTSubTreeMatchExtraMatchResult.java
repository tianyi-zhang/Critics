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
package edu.utexas.seal.plugins.util.root;

import java.util.List;

import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import edu.utexas.seal.plugins.ast.UTSourceFragment;
import rted.distance.RTEDInfoSubTree;
import rted.util.UTSort;
import ut.seal.plugins.utils.UTCfg;
import ut.seal.plugins.utils.UTLog;
import ut.seal.plugins.utils.UTStr;
import ut.seal.plugins.utils.UTTime;

/**
 * @author Myoungkyu Song
 * @date Dec 5, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTSubTreeMatchExtraMatchResult {
	Double	numNode;

	public void processMatchResults(List<RTEDInfoSubTree> subTrees) {
		procMatchResultsByTreeEditDistance(subTrees);
		extraResult(subTrees, false);
	}

	void extraResult(List<RTEDInfoSubTree> subTrees, boolean isPrintable) {
		if (isPrintable) {
			procMathResultsByTEDWithSimilarityScoreOfLabel(subTrees);
			procMathResultsByTEDWithSimilarityScoreOfValue(subTrees);
		}
	}

	/**
	 * 
	 * @param subTreeMngr
	 */
	public void procMathResultsByTEDWithSimilarityScoreOfLabel(List<RTEDInfoSubTree> subTreeMngr) {
		UTSubTreeMatch parm = new UTSubTreeMatch() {
			@Override
			public void callback(RTEDInfoSubTree t, String label, String value, int postorderId, //
					Double similarityScore, int similarityScoreOfValue, //
					Double similarityScoreOfLabel, Double dist, String hlink, boolean isPrintable) {
				String msg = "TID: " + UTStr.getIndentR(postorderId + ", ", 6) //
						+ "SML: " + UTStr.getIndentR(String.format("%.5f", similarityScoreOfLabel) + ", ", 6) //
						+ "DST: " + UTStr.getIndentR(dist + ", ", 6) //
						+ "(" + label + ")(" + value + ")";
				String details = ", " + "PATH: " + hlink;
				UTLog.println(isPrintable, msg, details);
			}
		};
		UTSort.sortBySimilarityScoreOfLabel(subTreeMngr);
		adaptX(subTreeMngr, parm);
		UTLog.println(true, "DBG__________________________________________");
	}

	/**
	 * 
	 * @param subTreeMngr
	 */
	public void procMathResultsByTEDWithSimilarityScoreOfValue(List<RTEDInfoSubTree> subTreeMngr) {
		UTSubTreeMatch parm = new UTSubTreeMatch() {
			@Override
			public void callback(RTEDInfoSubTree t, String label, String value, int postorderId, //
					Double similarityScore, int similarityScoreOfValue, //
					Double similarityScoreOfLabel, Double dist, String hlink, boolean isPrintable) {
				String msg = "TID: " + UTStr.getIndentR(postorderId + ", ", 6) //
						+ "SMV: " + UTStr.getIndentR(similarityScoreOfValue + ", ", 6) //
						+ "SML: " + UTStr.getIndentR(String.format("%.5f", similarityScoreOfLabel) + ", ", 6) //
						+ "DST: " + UTStr.getIndentR(dist + ", ", 6) //
						+ "(" + label + ")(" + value + ")";
				String details = ", " + "PATH: " + hlink;
				UTLog.println(isPrintable, msg, details);
			}
		};
		UTSort.sortBySimilarityScoreOfValue(subTreeMngr);
		adaptX(subTreeMngr, parm);
		UTLog.println(true, "DBG__________________________________________");
	}

	/**
	 * 
	 * @param subTrees
	 */
	public void procMatchResultsByTreeEditDistance(List<RTEDInfoSubTree> subTrees) {
		UTSubTreeMatch parm = new UTSubTreeMatch() {
			@Override
			public void callback(RTEDInfoSubTree t, String label, String value, int postorderId, //
					Double similarityScore, int similarityScoreOfValue, //
					Double similarityScoreOfLabel, Double dist, String hlink, boolean isPrintable) {
				String msg = "TID: " + UTStr.getIndentR(postorderId + ", ", 6) //
						+ "DST: " + UTStr.getIndentR(dist + ", ", 6) //
						+ "(" + label + ")(" + value + ")";
				String details = ", " + "PATH: " + hlink;
				UTLog.println(false, msg, details);

				procMapping(t);
			}
		};
		UTSort.sortByTreeEditDistance(subTrees);
		adaptX(subTrees, parm);
		UTLog.println(true, "DBG__________________________________________");
	}

	/**
	 * 
	 * @param subTree
	 */
	void procMapping(RTEDInfoSubTree subTree) {
		/*
		List<int[]> editMapping = subTree.getEditMapping();
		Map<Integer, Node> queryTreeIDMngr = subTree.getQueryTreeIDMngr();
		Map<Integer, Node> subTreeIDMngr = subTree.getSubTreeIDMngr();
		Double similarityScoreOfLabel = 0.0D;
		int similarityScoreOfValue = 0;
		LblValueNode tQNode = new LblValueNode();
		LblValueNode tTNode = new LblValueNode();
		for (int[] nodeAlignment : editMapping) {
			int qID = nodeAlignment[0];
			int tID = nodeAlignment[1];
			Node qNode = queryTreeIDMngr.get(qID);
			Node tNode = subTreeIDMngr.get(tID);
			tQNode.setNode(qNode);
			tTNode.setNode(tNode);
			if (qNode != null && tNode != null) {
				if (qNode.getLabel().name().equals(tNode.getLabel().name())) {
					similarityScoreOfLabel++;
				}
				similarityScoreOfValue += DistanceByLevenshtein.compute(qNode.getValue(), tNode.getValue());
			}
		}
		similarityScoreOfLabel = 1 - similarityScoreOfLabel / numNode;
		subTree.setSimilarityScoreOfLabel(similarityScoreOfLabel);
		subTree.setSimilarityScoreOfValue(similarityScoreOfValue);
		String strFormat = String.format("%d.%d", Integer.valueOf((int) (similarityScoreOfLabel * 100000)),
				similarityScoreOfValue);
		subTree.setSimilarityScore(Double.valueOf(strFormat));
		*/
	}

	/**
	 * 
	 * @param subTrees
	 */
	@Deprecated
	public void computeSimilarityScore(List<RTEDInfoSubTree> subTrees) {
		UTLog.init(UTCfg.getInst().readConfig().LOG4JPATH + "logResults_" + UTTime.curTime() + ".html");
		UTSubTreeMatch parm = new UTSubTreeMatch() {
			@Override
			public void callback(RTEDInfoSubTree t, String label, String value, int postorderId, //
					Double similarityScore, int similarityScoreOfValue, //
					Double similarityScoreOfLabel, Double dist, String hlink, boolean isPrintable) {
				String msg = "TID: " + UTStr.getIndentR(postorderId + ", ", 6) //
						+ "SIM: " + UTStr.getIndentR(similarityScore + ", ", 10) //
						// + "SML: " + UTStr.getIndentR(String.format("%.5f", similarityScoreOfLabel) + ", ", 6) //
						// + "SMV: " + UTStr.getIndentR(similarityScoreOfValue + ", ", 6) //
						// + "DST: " + UTStr.getIndentR(dist + ", ", 6) //
						+ "(" + label + ")(" + value + ")";
				String details = ", " + "PATH: " + hlink;
				UTLog.println(isPrintable, msg, details);
				// UTLog.println(false, this.sourceFragment.getFragment());
			}

			@Override
			public void javaCompilationHandler(JavaCompilation javaCompilation, Node node) {
				sourceFragment = new UTSourceFragment(javaCompilation, node, //
						node.getEntity().getSourceRange());
			}
		};
		UTSort.sortBySimilarityScore(subTrees);
		adaptX(subTrees, parm);
		UTLog.println(true, "DBG__________________________________________");
	}

	/**
	 * 
	 * @param subTrees
	 * @param parm
	 */
	void adaptX(List<RTEDInfoSubTree> subTrees, UTSubTreeMatch parm) {
		/*
		boolean isPrintable = true;
		for (int i = 0; i < subTrees.size(); i++) {
			RTEDInfoSubTree astInfo = subTrees.get(i);
			if (i > 55) {
				isPrintable = false;
			}
			Node node = astInfo.getSubTree();
			String label = node.getLabel().name().trim();
			String value = node.getValue().trim();
			int postorderId = astInfo.getPostorderID();
			Double similarityScore = astInfo.getSimilarityScore();
			int similarityScoreOfValue = astInfo.getSimilarityScoreOfValue();
			Double similarityScoreOfLabel = astInfo.getSimilarityScoreOfLabel();
			Double dist = astInfo.getDistance();
			File filePath = astInfo.getFullFilePath();
			String hlink = "<a href=file://" + filePath + ">" + filePath + "</a>";
			parm.javaCompilationHandler(astInfo.getJavaCompilation(), node);
			parm.callback(astInfo, label, value, postorderId, similarityScore, //
					similarityScoreOfValue, similarityScoreOfLabel, //
					dist, hlink, isPrintable);
		} */
	}

	public void setNumNode(Double numNode) {
		this.numNode = numNode;
	}

}
