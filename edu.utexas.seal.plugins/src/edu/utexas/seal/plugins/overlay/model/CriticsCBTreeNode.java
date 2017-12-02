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