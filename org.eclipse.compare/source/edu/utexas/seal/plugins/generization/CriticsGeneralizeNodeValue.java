/*
 * @(#) CriticsGeneralizeNodeValue.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.generization;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.SimpleName;

import ut.seal.plugins.utils.UTFile;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.NodeValueArg;
import edu.utexas.seal.plugins.ast.util.UTAddNewValue;
import edu.utexas.seal.plugins.util.UTCriticsDiffUtil;
import edu.utexas.seal.plugins.util.UTCriticsPairFileInfo;

/**
 * @author Myoungkyu Song
 * @date Feb 1, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsGeneralizeNodeValue {
	private Node					mQTreeNewRev, mQTreeOldRev;
	private Node					mQTreeSelectiveGenNewRev, mQTreeSelectiveGenOldRev;
	private Node					mQTreeAllGenNewRev, mQTreeAllGenOldRev;

	private CriticsGeneralization	mGeneralize;
	private UTAddNewValue			mNewValueHandlerNewRev, mNewValueHandlerOldRev;

	public CriticsGeneralizeNodeValue() {
		mGeneralize = new CriticsGeneralization();
		mGeneralize.generalizeQTreeOldRev();
		mGeneralize.generalizeQTreeNewRev();
		mNewValueHandlerOldRev = new UTAddNewValue(UTCriticsPairFileInfo.getRightICompilationUnit());
		mNewValueHandlerNewRev = new UTAddNewValue(UTCriticsPairFileInfo.getLeftICompilationUnit());
		mQTreeOldRev = mGeneralize.getQTreeOldRev();
		mQTreeSelectiveGenOldRev = mGeneralize.getQTreeGenOldRev();
		mQTreeNewRev = mGeneralize.getQTreeNewRev();
		mQTreeSelectiveGenNewRev = mGeneralize.getQTreeGenNewRev();
		mQTreeAllGenOldRev = mNewValueHandlerOldRev.addNewValueMethodLevel(mQTreeOldRev.getMethodDeclaration(), //
				UTFile.getContents(UTCriticsPairFileInfo.getRightFile().getAbsolutePath()));
		mQTreeAllGenNewRev = mNewValueHandlerNewRev.addNewValueMethodLevel(mQTreeNewRev.getMethodDeclaration(), //
				UTFile.getContents(UTCriticsPairFileInfo.getLeftFile().getAbsolutePath()));
	}

	/**
	 * get generalization info
	 */
	public void setGeneralizationInfo() {
		List<Node> lstQTreeOldRev = mQTreeOldRev.getAllNodes();
		List<Node> lstQTreeSomeGenOldRev = mQTreeSelectiveGenOldRev.getAllNodes();
		List<Node> lstQTreeAllGenOldRev = mQTreeAllGenOldRev.getAllNodes();
		List<Node> lstQTreeNewRev = mQTreeNewRev.getAllNodes();
		List<Node> lstQTreeSomeGenNewRev = mQTreeSelectiveGenNewRev.getAllNodes();
		List<Node> lstQTreeAllGenNewRev = mQTreeAllGenNewRev.getAllNodes();
		setGeneralizationInfo(lstQTreeSomeGenOldRev, lstQTreeOldRev, lstQTreeAllGenOldRev);
		setGeneralizationInfo(lstQTreeSomeGenNewRev, lstQTreeNewRev, lstQTreeAllGenNewRev);
	}

	/**
	 * Gets the generalization info.
	 * 
	 * @param lstQTreeSomeGen the list qTreeSomeGen
	 * @param lstQTree the listQtree
	 * @param lstQTreeAllGen the listQtreeAllGen
	 * @return the generalizationInfo
	 */
	void setGeneralizationInfo(List<Node> lstQTreeSomeGen, //
			List<Node> lstQTree, //
			List<Node> lstQTreeAllGen) {
		UTCriticsDiffUtil diffUtil = new UTCriticsDiffUtil();
		for (int i = 0; i < lstQTreeSomeGen.size(); i++) {
			Node iNode = lstQTree.get(i);
			Node iNodePartialGen = lstQTreeSomeGen.get(i);
			Node iNodeAllGen = null;
			for (int j = 0; j < lstQTreeAllGen.size(); j++) {
				iNodeAllGen = lstQTreeAllGen.get(j);
				if (iNodeAllGen.eq(iNode)) {
					break;
				}
			}
			String valueOrg = iNode.getValue();
			String valueAllGen = iNodeAllGen.getValueNew();
			if (valueAllGen != null) {
				List<NodeValueArg> lstNodeValueArg = diffUtil.getNodeValueArg(valueOrg, valueAllGen);
				iNodePartialGen.setValueArgList(lstNodeValueArg);
//				//TODO: Need to check which identifier is generalized here
//				String parGenValue = valueOrg;
//				for(NodeValueArg arg : lstNodeValueArg){
//					String orgID = arg.getArgOrg();
//					Map<SimpleName, Boolean> map = iNode.getParamMap();
//					if(map != null){
//						for(SimpleName name : map.keySet()){
//							if(name.getIdentifier().equals(orgID) && map.get(name)){
//								parGenValue = parGenValue.replace(orgID, arg.getArgNew());
//							}
//						}
//					}
//				}
//				iNodePartialGen.setNewValue(parGenValue);
				iNodePartialGen.setNewValue(valueAllGen);
			}
			iNodePartialGen.setValueOrg(valueOrg);
			boolean trace = false;
			if (trace) {
				System.out.println("[DBG" + i % 7 + "] " + iNode);
				System.out.println("[DBG" + i % 7 + "] " + iNodeAllGen.toNewString());
				System.out.println("[DBG" + i % 7 + "] " + iNodePartialGen);
			}
		}
	}

	/**
	 * Prints the generalization info.
	 */
	public void printGeneralizationInfo(boolean trace1, boolean trace2) {
		if (trace1) {
			System.out.println("------------------------------------------");
			int cnt = 0;
			Enumeration<?> e = this.mQTreeSelectiveGenOldRev.preorderEnumeration();
			while (e.hasMoreElements()) {
				Node iNode = (Node) e.nextElement();
				if (iNode.isAnyValueArgParameterized()) {
					String valueSomeGen = iNode.getValue();
					String valueOrg = iNode.getValueOrg();
					System.out.println("[DBG" + cnt % 4 + "] " + valueOrg);
					System.out.println("[DBG" + cnt % 4 + "] " + valueSomeGen);
					List<NodeValueArg> valueArgList = iNode.getValueArgList();
					for (int i = 0; i < valueArgList.size(); i++) {
						NodeValueArg elem = valueArgList.get(i);
						System.out.println("[DBG" + cnt % 4 + "]     " + elem);
					}
					cnt++;
				}
			}
		}
		if (trace2) {
			System.out.println("------------------------------------------");
			int cnt = 0;
			Enumeration<?> e = this.mQTreeSelectiveGenNewRev.preorderEnumeration();
			while (e.hasMoreElements()) {
				Node iNode = (Node) e.nextElement();
				if (iNode.isAnyValueArgParameterized()) {
					String valueSomeGen = iNode.getValue();
					String valueOrg = iNode.getValueOrg();
					System.out.println("[DBG" + cnt % 4 + "] " + valueOrg);
					System.out.println("[DBG" + cnt % 4 + "] " + valueSomeGen);
					List<NodeValueArg> valueArgList = iNode.getValueArgList();
					for (int i = 0; i < valueArgList.size(); i++) {
						NodeValueArg elem = valueArgList.get(i);
						System.out.println("[DBG" + cnt % 4 + "]     " + elem);
					}
					cnt++;
				}
			}
		}
	}

	public Node getQTreeNewRev() {
		return mQTreeNewRev;
	}

	public Node getQTreeOldRev() {
		return mQTreeOldRev;
	}

	public Node getQTreeSelectiveGenNewRev() {
		return mQTreeSelectiveGenNewRev;
	}

	public Node getQTreeSelectiveGenOldRev() {
		return mQTreeSelectiveGenOldRev;
	}

	public Node getQTreeAllGenNewRev() {
		return mQTreeAllGenNewRev;
	}

	public Node getQTreeAllGenOldRev() {
		return mQTreeAllGenOldRev;
	}

	public CriticsGeneralization getGeneralize() {
		return mGeneralize;
	}

	public void setGeneralize(CriticsGeneralization generalize) {
		this.mGeneralize = generalize;
	}

	public UTAddNewValue getNewValueHandlerNewRev() {
		return mNewValueHandlerNewRev;
	}

	public void setNewValueHandlerNewRev(UTAddNewValue newValueHandlerNewRev) {
		this.mNewValueHandlerNewRev = newValueHandlerNewRev;
	}

	public UTAddNewValue getNewValueHandlerOldRev() {
		return mNewValueHandlerOldRev;
	}

	public void setNewValueHandlerOldRev(UTAddNewValue newValueHandlerOldRev) {
		this.mNewValueHandlerOldRev = newValueHandlerOldRev;
	}
}
