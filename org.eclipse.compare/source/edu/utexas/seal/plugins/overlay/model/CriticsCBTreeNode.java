/*
 * @(#) CriticsCBTreeNode.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.overlay.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rted.distance.RTEDInfoSubTree;
import ut.seal.plugins.utils.UTFile;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

/**
 * @author Myoungkyu Song
 * @date Dec 26, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsCBTreeNode {
	private String					mData;
	private List<CriticsCBTreeNode>	mChildren	= new ArrayList<CriticsCBTreeNode>();
	private CriticsCBTreeNode		mParent;
	private CriticsCBTreeElemType	mType;
	private File					mFile;
	private int						mOffset;
	private int						mLength;
	private String					mSource;
	private RTEDInfoSubTree			mSubtreeInfo;
	private Node					mNode;

	public CriticsCBTreeNode(String aData, CriticsCBTreeElemType aType) {
		this.mData = aData;
		this.mType = aType;
	}

	public List<CriticsCBTreeNode> getChildren() {
		return mChildren;
	}

	public void addChild(CriticsCBTreeNode child) {
		mChildren.add(child);
		child.setParent(this);
	}

	public CriticsCBTreeNode getParent() {
		return mParent;
	}

	public void setParent(CriticsCBTreeNode parent) {
		this.mParent = parent;
	}

	public String getData() {
		return mData;
	}

	@Override
	public String toString() {
		return mData;
	}

	public CriticsCBTreeElemType type() {
		return mType;
	}

	public void setPath(File aFile) {
		this.mFile = aFile;
	}

	public void setOffSet(int aOffset) {
		this.mOffset = aOffset;
	}

	public void setLength(int aLength) {
		this.mLength = aLength;
	}

	public void setNode(Node aNode) {
		this.mNode = aNode;
	}

	public void setSource(String aSource) {
		this.mSource = aSource;
	}

	public void setSubtreeInfo(RTEDInfoSubTree aSubtreeInfo) {
		this.mSubtreeInfo = aSubtreeInfo;
		Node subtree = aSubtreeInfo.getSubTree();
		int beginIndex = subtree.getEntity().getStartPosition();
		int endIndex = subtree.getEntity().getEndPosition() + 2;
		File file = aSubtreeInfo.getFullFilePath();
		String source = UTFile.getContents(file.getAbsolutePath());
		String codeblock = source.substring(beginIndex, endIndex);
		setPath(file);
		setOffSet(beginIndex);
		setLength(endIndex - beginIndex);
		setSource(codeblock);
		setNode(subtree);
	}

	public File getFile() {
		return mFile;
	}

	public int getOffset() {
		return mOffset;
	}

	public int getLength() {
		return mLength;
	}

	public Node getNode() {
		return mNode;
	}

	public String getSource() {
		return mSource;
	}

	public RTEDInfoSubTree getSubtreeInfo() {
		return mSubtreeInfo;
	}
}