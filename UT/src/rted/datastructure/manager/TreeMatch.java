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
package rted.datastructure.manager;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jdt.core.dom.CompilationUnit;

import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.NodeValueArg;
import edu.utexas.seal.plugins.util.UTCriticsDiffUtil;
import rted.datastructure.LblValueNode;
import rted.distance.RTEDInfoSubTree;
import rted.processor.RTEDProcessor;
import rted.util.DistanceByLevenshtein;
import ut.seal.plugins.utils.UTChange;
import ut.seal.plugins.utils.UTLog;
import ut.seal.plugins.utils.UTStr;

/**
 * @author Myoungkyu Song
 * @date Nov 12, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class TreeMatch implements ITreeMatch {
	protected boolean				mTrace		= true;
	protected RTEDProcessor			mRtedProc	= new RTEDProcessor();
	protected TreeConvert			mConverterQTree;
	protected TreeConvert			mConverterTTree;
	protected List<RTEDInfoSubTree>	mSubTrees;
	protected Map<Integer, Node>	mMapQTree	= null;
	protected Map<Integer, Node>	mMapTTree	= null;
	protected Integer				mId[];
	protected CompilationUnit		mUnit;
	protected File					mFilePath;

	/* (non-Javadoc)
	 * @see rted.datastructure.manager.ITreeMatch#matchEditMapping(java.lang.Integer[], org.eclipse.jdt.core.dom.CompilationUnit, ch.uzh.ifi.seal.changedistiller.treedifferencing.Node, ch.uzh.ifi.seal.changedistiller.treedifferencing.Node, java.io.File)
	 */
	@Override
	public void matchEditMapping(Node qTree, Node tTree) {

		trace1(mId[0], qTree, tTree);
		if (!treeEditMatch(qTree, tTree)) {
			return;
		}
		trace2(mId[0], false);
		for (int[] nodeAlignment : mRtedProc.getEditMapping()) {
			int qTreeId = nodeAlignment[0];
			int tTreeId = nodeAlignment[1];
			Node qNode = mMapQTree.get(qTreeId);
			Node tNode = mMapTTree.get(tTreeId);
			if (qNode == null || tNode == null || (qNode.getLabel() == JavaEntityType.EXCLUDED) || //
					qNode.getEntity().getType().isMethod() || tNode.getEntity().getType().isMethod()) {
				continue;
			}
			if (qNode.getEntity().getType() != tNode.getEntity().getType()) {
				return;
			}
			if (qNode.isAnyValueArgParameterizedInQeury() ){
				if(UTChange.checkEQByDiff(qNode, tNode)) {
					continue;
				}else{
					// corner case: when comparing pizza.$var("Hello pizza") with pizza.setName("Hello world")
					// we need first remove string in quote then compare
					String qNodeValue = UTStr.removeStrInQuotMark(qNode.getValue());
					String tNodeValue = UTStr.removeStrInQuotMark(tNode.getValue());
					Node fakeQNode = new Node(JavaEntityType.METHOD_INVOCATION, qNodeValue);
					Node fakeTNode = new Node(JavaEntityType.METHOD_INVOCATION, tNodeValue);
					if(UTChange.checkEQByDiff(fakeQNode, fakeTNode)){
						continue;
					}
				}
			}
			if (!qNode.eqValueWoStr(tNode)) {
				return;
			}
		}
		double ted = mRtedProc.getDistance();
		mSubTrees.add(new RTEDInfoSubTree(mId[0], qTree, tTree, ted, 0d, mFilePath));
		mId[0]++;
	}

	/**
	 * Checks if is skip.
	 * 
	 * @param aQTree the a q tree
	 * @param aRevTree the a rev tree
	 * @return true, if is skip
	 */
	public boolean isSkip(Node aQTree, Node aRevTree) {
		int d1 = aQTree.getDepth();
		int d2 = aRevTree.getDepth();
		int szOfExNode = aQTree.sizeOfExcludedNode();
		d1 -= szOfExNode;
		if ((d1 > d2) || aQTree.eq(aRevTree)) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if is continue.
	 * 
	 * @param aQNode the a q node
	 * @param aTNode the a t node
	 * @return true, if is continue
	 */
	public boolean isContinue(Node aQNode, Node aTNode) {
		String valueQNode = aQNode.getValue();
		String valueTNode = aTNode.getValue();
		String labelQNode = aQNode.getLabel().name();
		String labelTNode = aTNode.getLabel().name();
		if (aTNode.getEntity().getType().isMethod()) {
			return false;
		}
		if (labelQNode.equals(labelTNode) && //
				aQNode.isAnyValueArgParameterized() && //
				aQNode.isSameSizeOfParameter(aTNode)) {
			aTNode.convertValuePartialGen(aQNode);
			String valParGenQNode = aQNode.getValue();
			String valParGenTNode = aTNode.getValuePartialGen();
			if (valParGenQNode.equals(valParGenTNode)) {
				return false;
			}
		} else if (!labelQNode.equals(labelTNode) || !valueQNode.equals(valueTNode) || aTNode.isSkip()) {
			return true;
		}
		return false;
	}

	void matchNodes(Node qTree, Node tTree) {
		int cnt = 0;
		boolean trace1 = false, trace3 = !false;
		if (trace1) {
			qTree.printNewValue();
			System.out.println("------------------------------------------");
			tTree.printNewValue();
		}
		matchNodes(qTree, tTree, MatchType.LABEL);
		if (trace3) {
			t.trace5(trace3, cnt);
		}
		UTLog.println(trace1, "[DBG2] MATCH TREE DISTANCE: " + mRtedProc.getDistance());
	}

	private void matchNodes(Node qTree, Node tTree, MatchType kind) {
		boolean trace2 = false;
		mMapQTree = new TreeMap<Integer, Node>();
		mMapTTree = new TreeMap<Integer, Node>();
		mConverterQTree.convert(qTree, mMapQTree, trace2, kind);
		mConverterTTree.convert(tTree, mMapTTree, trace2, kind);
		String qLblTree = mConverterQTree.getSubLblTree();
		String tLblTree = mConverterTTree.getSubLblTree();
		mRtedProc.initiate(qLblTree, tLblTree, trace2);
		mRtedProc.match();
		mRtedProc.mapping(mMapQTree, mMapTTree, false);
		LblValueNode oldNode = new LblValueNode();
		LblValueNode newNode = new LblValueNode();
		for (int[] nodeAlignment : mRtedProc.getEditMapping()) {
			int oldNodeId = nodeAlignment[0];
			int newNodeId = nodeAlignment[1];
			oldNode.setNode(mMapQTree.get(oldNodeId));
			newNode.setNode(mMapTTree.get(newNodeId));
			UTLog.println(trace2, oldNodeId + "->" + newNodeId + "\t" + oldNode.toStringAllGen() + " -> " + newNode.toStringAllGen());
		}
	}

	int	cnt1	= 0;

	/**
	 * Match all generalized nodes.
	 * 
	 * @param qTree the q tree
	 * @param tTree the t tree
	 */
	boolean treeEditMatch(Node qTree, Node tTree) {
		boolean trace1 = false, trace3 = !false;
		if (trace1) {
			qTree.printNewValue();
			System.out.println("------------------------------------------");
			tTree.printNewValue();
		}
		matchNodes(qTree, tTree, MatchType.ALLGEN);
		LinkedList<int[]> editMapping = mRtedProc.getEditMapping();
		int matchCnter = 0;
		for (int[] nodeAlignment : editMapping) {
			int oldNodeId = nodeAlignment[0];
			int newNodeId = nodeAlignment[1];
			Node pairNodeOldRev = mMapQTree.get(oldNodeId);
			if (oldNodeId != 0 && pairNodeOldRev.getLabel() == JavaEntityType.EXCLUDED) {
				continue;
			}
			if (oldNodeId != 0 && newNodeId != 0) {
				matchCnter++;
			}
			// System.out.println("[DBG] " + oldNodeId + ", " + newNodeId);
		}
		// System.out.println("[DBG1] " + matchCnter);
		int szQtree = qTree.getAllNodes().size();
		int szOfExNode = qTree.sizeOfExcludedNode();
		szQtree -= szOfExNode;
		if (trace3) {
			t.trace5(true, cnt1);
		}
		if (szQtree == matchCnter) {
			return true;
		}
		return false;
	}

	/**
	 * Gets the distance par gen two nodes.
	 * 
	 * @param aQNode the a q node
	 * @param aTNode the a t node
	 * @return the distance par gen two nodes
	 */
	double getDistanceParGenTwoNodes(Node aQNode, Node aTNode) {
		aTNode.convertValuePartialGen(aQNode);
		String valParGenQNode = aQNode.getValue();
		String valParGenTNode = aTNode.getValuePartialGen();
		int simScore = DistanceByLevenshtein.compute(valParGenQNode.replaceAll("\\d", ""), valParGenTNode.replaceAll("\\d", ""));
		UTLog.println(false, "[DBG1] valParGenQNode: " + valParGenQNode);
		UTLog.println(false, "[DBG1] valParGenTNode: " + valParGenTNode);
		return Double.valueOf(simScore);
	}

	boolean equalParGenTwoNodes(Node aQNode, Node aTNode) {
		aTNode.convertValuePartialGen(aQNode);
		String valParGenQNode = aQNode.getValue().replaceAll("\\d", "");
		String valParGenTNode = aTNode.getValuePartialGen().replaceAll("\\d", "");
		boolean eq = valParGenQNode.equals(valParGenTNode);
		return eq;
	}

	/**
	 * Gets the distance two nodes.
	 * 
	 * @param aQNode the a q node
	 * @param aTNode the a t node
	 * @return the distance two nodes
	 */
	double getDistanceTwoNodes(Node aQNode, Node aTNode) {
		String valQNode = "";
		String valTNode = "";
		if (aQNode != null) {
			valQNode = aQNode.getValue();
			valQNode += aQNode.getEntity().getLabel();
		}
		if (aTNode != null) {
			valTNode = aTNode.getValue();
			valTNode += aTNode.getEntity().getLabel();
		}
		int simScore = DistanceByLevenshtein.compute(valQNode, valTNode);
		return Double.valueOf(simScore);
	}

	/**
	 * Match partial generalized nodes.
	 * 
	 * @param qTree the q tree
	 * @param tTree the t tree
	 */
	void matchPartialGeneralizedNodes(Node qTree, Node tTree) {
		mMapQTree = new TreeMap<Integer, Node>();
		mMapTTree = new TreeMap<Integer, Node>();
		mConverterQTree.convert(qTree, mMapQTree, true, MatchType.REGULAR);
		mConverterTTree.convert(tTree, mMapTTree, true, MatchType.REGULAR);
		String qLblTree = mConverterQTree.getSubLblTree();
		String tLblTree = mConverterTTree.getSubLblTree();
		mRtedProc.initiate(qLblTree, tLblTree, true);
		mRtedProc.match();
		mRtedProc.mapping(mMapQTree, mMapTTree, true);
		System.out.println("[RST] MATCH PARTIAL GENERALIZED NODES DISTANCE: " + mRtedProc.getDistance());
	}

	/**
	 * Match struct entity.
	 * 
	 * @param qTree the q tree
	 * @param tTree the t tree
	 */
	void matchStructEntity(Node qTree, Node tTree) {
		boolean changedAlgorithm = false;
		if (changedAlgorithm) {
			qTree.resetValue();
			tTree.resetValue();
			mMapQTree = new TreeMap<Integer, Node>();
			mMapTTree = new TreeMap<Integer, Node>();
			mConverterQTree.convert(qTree, mMapQTree, true, MatchType.REGULAR);
			mConverterTTree.convert(tTree, mMapTTree, true, MatchType.REGULAR);
			String qLblTree = mConverterQTree.getSubLblTree();
			String tLblTree = mConverterTTree.getSubLblTree();
			mRtedProc.initiate(qLblTree, tLblTree, true);
			mRtedProc.match();
			mRtedProc.mapping(mMapQTree, mMapTTree, true);
			System.out.println("[RST] MATCH STRUCTURE ENTITY DISTANCE: " + mRtedProc.getDistance());
			qTree.restoreValue();
			tTree.restoreValue();

		}
	}

	/**
	 * Cut down match cost.
	 * 
	 * @param aNode the a node
	 */
	protected void cutDownMatchCost(Node aNode) {
		List<Node> children = aNode.getChildren();
		for (Node iNode : children) {
			boolean isRemove = iNode.isSkipInMethodSignature();
			if (isRemove) {
				aNode.remove(iNode);
			}
		}
	}

	/*	void traceA(boolean trace) {
		if (trace) {
			System.out.println("[DBG" + mId[0] % 6 + "] QT PAR GEN: " + qNode.getValue());
			System.out.println("[DBG" + mId[0] % 6 + "] TT ORG    : " + tNode.getValueReplica());
			// System.out.println("[DBG" + mId[0] % 6 + "] TT ALL GEN: " + tNode.getNewValue());
			System.out.println("[DBG" + mId[0] % 6 + "] TT PAR GEN: " + valuePartialGenTNode);
			System.out.println("------------------------------------------");
		}
	} */

	/**
	 * Sets the value arg list.
	 * 
	 * @param aTNode the new value arg list
	 */
	void setValueArgList(Node aTNode) {
		UTCriticsDiffUtil diffUtil = new UTCriticsDiffUtil();
		String valueOrg = aTNode.getValue();
		String valueAllGen = aTNode.getValueNew();
		if (valueOrg == null || valueAllGen == null) {
			return;
		}
		List<NodeValueArg> lstNodeValueArg = diffUtil.getNodeValueArg(valueOrg, valueAllGen);
		aTNode.setValueArgList(lstNodeValueArg);
	}

	/* (non-Javadoc)
	 * @see rted.datastructure.manager.ITreeMatch#matchEditMapping(java.lang.Integer[], ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation, ch.uzh.ifi.seal.changedistiller.treedifferencing.Node, ch.uzh.ifi.seal.changedistiller.treedifferencing.Node, java.io.File)
	 */
	public void matchEditMapping(Integer id[], JavaCompilation c, Node qTree1, Node tTree2, File path) {
		int d1 = qTree1.getDepth();
		int d2 = tTree2.getDepth();
		if (d1 > d2) {
			return;
		}
		Map<Integer, Node> m1 = new TreeMap<Integer, Node>();
		Map<Integer, Node> m2 = new TreeMap<Integer, Node>();
		mConverterQTree.convert(qTree1, m1, true, MatchType.REGULAR);
		mConverterTTree.convert(tTree2, m2, true, MatchType.REGULAR);
		String qLblTree = mConverterQTree.getSubLblTree();
		String tLblTree = mConverterTTree.getSubLblTree();
		mRtedProc.initiate(qLblTree, tLblTree, false);
		mRtedProc.match();
		mRtedProc.mapping(m1, m2, false);
		trace2(id[0], false);
		Double ted = mRtedProc.getDistance();
		mSubTrees.add(new RTEDInfoSubTree(mId[0], qTree1, tTree2, ted, 0d, mFilePath));
		id[0]++;
	}

	@Deprecated
	public void matchEditMapping2(Node qTree, Node tTree) {
		trace1(mId[0], qTree, tTree);
		if (!treeEditMatch(qTree, tTree)) {
			return;
		}
		trace2(mId[0], false);
		for (int[] nodeAlignment : mRtedProc.getEditMapping()) {
			int qTreeId = nodeAlignment[0];
			int tTreeId = nodeAlignment[1];
			Node qNode = mMapQTree.get(qTreeId);
			Node tNode = mMapTTree.get(tTreeId);
			if (qNode == null || tNode == null || qNode.getLabel() == JavaEntityType.EXCLUDED) {
				continue;
			}
			if (qNode.getEntity().getType().isMethod() || tNode.getEntity().getType().isMethod()) {
				continue;
			}
			JavaEntityType entityQNode = (JavaEntityType) qNode.getEntity().getType();
			JavaEntityType entityTNode = (JavaEntityType) tNode.getEntity().getType();
			if (entityQNode != entityTNode) {
				return;
			}
			if (qNode.isAnyValueArgParameterizedInQeury() && UTChange.checkEQByDiff(qNode, tNode)) {
				continue;
			}
			if (!qNode.eqValueWoStr(tNode)) {
				return;
			}
			// tNode.setValueArgList();
			// if (qNode.isSameSizeOfParameter(tNode) && entityQNode == entityTNode) {
			// if (qNode.isAnyValueArgParameterized()) {
			// if (equalParGenTwoNodes(qNode, tNode)) {
			// ; // GOOD
			// } else {
			// return;
			// }
			// } else {
			// if (qNode.eqValueWoStr(tNode)) {
			// ; // GOOD
			// } else {
			// return;
			// }
			// }
			// } else if (entityQNode != entityTNode || !qNode.eqValueWoStr(tNode)) {
			// return;
			// }
		}
		double ted = mRtedProc.getDistance();
		mSubTrees.add(new RTEDInfoSubTree(mId[0], qTree, tTree, ted, 0d, mFilePath));
		mId[0]++;
	}

	/* (non-Javadoc)
	 * @see rted.datastructure.manager.ITreeMatch#getSubTrees()
	 */
	@Override
	public List<RTEDInfoSubTree> getSubTrees() {
		return mSubTrees;
	}

	/* (non-Javadoc)
	 * @see rted.datastructure.manager.ITreeMatch#getRTEDProc()
	 */
	public RTEDProcessor getRTEDProc() {
		return mRtedProc;
	}

	/**
	 * Gets the distance.
	 * 
	 * @param d the d
	 * @return the distance
	 */
	private String getDistance(Double d) {
		if (d < 10) {
			return " " + String.valueOf(d);
		}
		return String.valueOf(d);
	}

	/* (non-Javadoc)
	 * @see rted.datastructure.manager.ITreeMatch#matchEditMappingEntry(java.lang.Integer[], ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation, ch.uzh.ifi.seal.changedistiller.treedifferencing.Node, ch.uzh.ifi.seal.changedistiller.treedifferencing.Node, java.io.File, boolean)
	 */
	@Override
	public void matchEditMappingEntry(Integer[] id, JavaCompilation c, Node t1, Node t2, File f, boolean p) {
	}

	/* (non-Javadoc)
	 * @see rted.datastructure.manager.ITreeMatch#matchEditMappingEntry(ch.uzh.ifi.seal.changedistiller.treedifferencing.Node, ch.uzh.ifi.seal.changedistiller.treedifferencing.Node, boolean)
	 */
	@Override
	public void matchEditMappingEntry(Node t1, Node t2, boolean p) {
	}

	/**
	 * Prints the.
	 * 
	 * @param isPrintable the is printable
	 */
	public void print(boolean isPrintable) {
		if (isPrintable) {
			mRtedProc.timeTot = (new Date()).getTime() - mRtedProc.timeTot;
			mRtedProc.printTimeStamp("runtime (total):            ", mRtedProc.timeTot);
			mRtedProc.printTimeStamp("runtime (in):               ", mRtedProc.timeInput);
			mRtedProc.printTimeStamp("runtime (compute):          ", mRtedProc.timeCompute);
			mRtedProc.printTED();
			System.out.println("DBG__________________________________________");
		}
	}

	/**
	 * Prints the.
	 * 
	 * @param subTreeMngr the sub tree mngr
	 */
	public void print(List<RTEDInfoSubTree> subTreeMngr) {
		for (RTEDInfoSubTree t : subTreeMngr) {
			System.out.println("\t DISTANCE: " + getDistance(t.getDistance()) + ", " //
					+ "POST-ORDER-ID: " + t.getPostorderID() + ", " //
					+ "[" + t.getSubTree().getLabel() + "], " //
					+ t.getSubTree().getValue());
		}
	}

	// ****************************************************
	// ****************************************************
	// ****************************************************

	/**
	 * Trace1.
	 * 
	 * @param id the id
	 * @param d1 the d1
	 * @param d2 the d2
	 * @param node the node
	 */
	void trace1(Integer id, Node qNode, Node tNode) {
		t.trace1(id, qNode, tNode);
	}

	/**
	 * Trace2.
	 * 
	 * @param postorderID the postorder id
	 * @param isPrintable the is printable
	 */
	void trace2(Integer postorderID, boolean isPrintable) {
		t.trace2(postorderID, isPrintable);
	}

	/**
	 * Trace3.
	 * 
	 * @param isPrintable the is printable
	 * @param tc1 the tc1
	 * @param tc2 the tc2
	 */
	void trace3(boolean isPrintable, TreeConvert tc1, TreeConvert tc2) {
		t.trace3(isPrintable, tc1, tc2);
	}

	private Trace	t	= new Trace();

	class Trace {

		/**
		 * Trace1.
		 * 
		 * @param id the id
		 * @param d1 the d1
		 * @param d2 the d2
		 * @param node the node
		 */
		void trace1(Integer id, Node qNode, Node tNode) {
			UTLog.println(mTrace, "[DBG] ID: " + String.valueOf(id) + ", HEAD: " + tNode + ", " + mFilePath.getAbsolutePath()); //
			System.out.println("------------------------------------------");
			System.out.println("[DBG] TARGET: ");
			tNode.print();
			System.out.println("------------------------------------------");
			System.out.println("[DBG] QUERY");
			qNode.print();
			System.out.println("------------------------------------------");
		}

		/**
		 * Trace2.
		 * 
		 * @param postorderID the postorder id
		 * @param isPrintable the is printable
		 */
		void trace2(Integer postorderID, boolean isPrintable) {
			if (!isPrintable)
				return;
			print(isPrintable);
			UTLog.println(isPrintable, "QUERY TREE: ");
			UTLog.println(isPrintable, "DBG__________________________________________");
			mRtedProc.getQueryLblTree().prettyLog();
			UTLog.println(isPrintable, "DBG__________________________________________");
			UTLog.println(isPrintable, "SUB TREE: ");
			UTLog.println(isPrintable, "DBG__________________________________________");
			mRtedProc.getSubLblTree().prettyLog();
			UTLog.println(isPrintable, "DBG__________________________________________");
			UTLog.println(isPrintable, "TID: " + String.valueOf(postorderID) + ", DST: " + mRtedProc.getDistance());
			UTLog.println(isPrintable, "DBG__________________________________________");
		}

		/**
		 * Trace3.
		 * 
		 * @param isPrintable the is printable
		 * @param leftTreeMngr the left tree mngr
		 * @param rightTreeMngr the right tree mngr
		 */
		void trace3(boolean isPrintable, TreeConvert leftTreeMngr, TreeConvert rightTreeMngr) {
			if (isPrintable) {
				System.out.println("DBG0__________________________________________");
				System.out.println(leftTreeMngr.getSubTree().toString());
				List<Object> resultLeft = leftTreeMngr.getSubLblTreeNodeList();
				for (int i = 0; i < resultLeft.size(); i++) {
					System.out.print(resultLeft.get(i));
				}
				System.out.println("DBG0__________________________________________");
				System.out.println(rightTreeMngr.getSubTree().toString());
				List<Object> resultRight = rightTreeMngr.getSubLblTreeNodeList();
				for (int i = 0; i < resultRight.size(); i++) {
					System.out.print(resultRight.get(i));
				}
			}
		}

		/**
		 * Trace5.
		 * 
		 * @param trace1 the trace1
		 * @param cnt the cnt
		 */
		void trace5(boolean trace1, int cnt) {
			for (int[] nodeAlignment : mRtedProc.getEditMapping()) {
				int qTreeId = nodeAlignment[0];
				int tTreeId = nodeAlignment[1];
				Node qNode = mMapQTree.get(qTreeId);
				Node tNode = mMapTTree.get(tTreeId);
				if (trace1) {
					if (qNode != null || tNode != null) {
						System.out.println("[DBG" + cnt1 % 7 + "] " + qNode + " => " + tNode);
					}
				} else {
					if (qNode == null) {
						// System.out.println("[DBG" + cnt1 % 7 + "] " + " null => ");
						// System.out.println("[DBG" + cnt1 % 7 + "] " + tNode.toNewString());
					} else if (tNode == null) {
						// System.out.println("[DBG" + cnt1 % 7 + "] " + qNode.toNewString() + " => ");
						// System.out.println("[DBG" + cnt1 % 7 + "] null");
					} else {
						System.out.println("[DBG" + cnt1 % 7 + "] " + qNode.toNewString() + " => " + tNode.toNewString());
					}
				}
			}
			cnt1++;
		}
	}

	/* (non-Javadoc)
	 * @see rted.datastructure.manager.ITreeMatch#setMatchCounter(java.lang.Integer[])
	 */
	@Override
	public void setMatchCounter(Integer[] cntMatchedOldRev) {
		this.mId = cntMatchedOldRev;
	}

	/* (non-Javadoc)
	 * @see rted.datastructure.manager.ITreeMatch#setCompilationUnit(org.eclipse.jdt.core.dom.CompilationUnit)
	 */
	@Override
	public void setCompilationUnit(CompilationUnit aUnit) {
		this.mUnit = aUnit;
	}

	/* (non-Javadoc)
	 * @see rted.datastructure.manager.ITreeMatch#setFilePath(java.io.File)
	 */
	@Override
	public void setFilePath(File aFilePath) {
		this.mFilePath = aFilePath;
	}

}
// protected TreeConvert query_converter;
// protected TreeConvert target_converter;
//
// protected TreeConvert leftTreeMngr;
// protected TreeConvert rightTreeMngr;

// protected List<RTEDInfoSubTree> subTrees = new ArrayList<RTEDInfoSubTree>();

// public void matchEditMappingEntry(Integer cntMatched[], JavaCompilation javaCompilation, //
// Node queryRootNode, Node targetRootNode, //
// File fullFilePath, boolean isPrintable) {
// }

// /**
// * @param queryTreeMngr
// * @param targetTreeMngr
// * @param rtedProc
// * @param isPrintable
// */
// public TreeMatch(TreeConvert queryTreeMngr, TreeConvert targetTreeMngr) {
// this.query_converter = queryTreeMngr;
// this.target_converter = targetTreeMngr;
// this.leftTreeMngr = queryTreeMngr;
// this.rightTreeMngr = targetTreeMngr;
// }
//
// public TreeMatch() {
// query_converter = new TreeConvert();
// target_converter = new TreeConvert();
// leftTreeMngr = new TreeConvert();
// rightTreeMngr = new TreeConvert();
// }

// /**
// * @param cntMatched
// * @param javaCompilation
// * @param targetRootNode
// * @param queryRootNode
// * @param subTreeMngr
// * @param isPrintable
// */
// public void matchEditMappingEntry(Integer cntMatched[], //
// JavaCompilation javaCompilation, //
// Node queryRootNode, //
// Node targetRootNode, //
// File fullFilePath, boolean isPrintable) {
// $ = isPrintable;
// Enumeration<?> e = targetRootNode.preorderEnumeration();
// while (e.hasMoreElements()) {
// matchEditMapping(cntMatched, //
// javaCompilation, //
// queryRootNode, (Node) e.nextElement(), //
// fullFilePath);
// }
// }

// /**
// *
// * @param javaCompilation
// * @param firstTree
// * @param secondTree
// * @param subTreeMngr
// */
// private void matchEditMapping(Integer postorderID[], JavaCompilation javaCompilation, //
// Node firstTree, Node secondTree, File fullFilePath) {
// int queryTreeDepth = firstTree.getDepth();
// int subTreeDepth = secondTree.getDepth();
// if (queryTreeDepth > subTreeDepth) {
// return;
// }
// trace1(postorderID[0], queryTreeDepth, subTreeDepth);
//
// // queryTree.print();
// // System.out.println("------------------------------------------");
// // subTree.print();
// // System.out.println("------------------------------------------");
//
// Map<Integer, Node> queryTreeIDMngr = new TreeMap<Integer, Node>();
// Map<Integer, Node> subTreeIDMngr = new TreeMap<Integer, Node>();
//
// query_converter.convert(firstTree, queryTreeIDMngr, true);
// target_converter.convert(secondTree, subTreeIDMngr, true);
//
// String firstLblTree = query_converter.getSubLblTree();
// String secondLblTree = target_converter.getSubLblTree();
//
// rtedProc.initiate(firstLblTree, secondLblTree, false);
// rtedProc.match();
// rtedProc.mapping(queryTreeIDMngr, subTreeIDMngr, false);
//
// trace2(postorderID[0], false);
//
// subTrees.add(new RTEDInfoSubTree(postorderID[0], javaCompilation, //
// firstTree, secondTree, //
// rtedProc.getSubLblTree(), rtedProc.getQueryLblTree(), //
// rtedProc.getDistance(), rtedProc.getEditMapping(), //
// queryTreeIDMngr, subTreeIDMngr, fullFilePath));
// postorderID[0]++;
// }

// /**
// *
// */
// public void matchEditMapping() {
// trace3(false);
//
// Node leftSubTree = leftTreeMngr.getSubTree();
// Node rightSubTree = rightTreeMngr.getSubTree();
//
// leftTreeMngr.convert(leftSubTree, leftSubTreeIDMngr, false);
// rightTreeMngr.convert(rightSubTree, rightSubTreeIDMngr, false);
//
// String leftSubLblTree = leftTreeMngr.getSubLblTree();
// String rightSubLblTree = rightTreeMngr.getSubLblTree();
//
// rtedProc.initiate(rightSubLblTree, leftSubLblTree, false);
// rtedProc.match();
// rtedProc.mapping(rightSubTreeIDMngr, leftSubTreeIDMngr, false);
//
// trace3_A(false);
// }

// public Map<Integer, Node> getRightSubTreeIDMngr() {
// return rightSubTreeIDMngr;
// }
//
// public Map<Integer, Node> getLeftSubTreeIDMngr() {
// return leftSubTreeIDMngr;
// }
//
// public List<RTEDInfoSubTree> getSubTrees() {
// return subTrees;
// }
//
// public TreeConvert getLeftTreeMngr() {
// return leftTreeMngr;
// }
//
// public void setLeftTreeMngr(TreeConvert leftTreeMngr) {
// this.leftTreeMngr = leftTreeMngr;
// }
//
// public TreeConvert getRightTreeMngr() {
// return rightTreeMngr;
// }
//
// public void setRightTreeMngr(TreeConvert rightTreeMngr) {
// this.rightTreeMngr = rightTreeMngr;
// }