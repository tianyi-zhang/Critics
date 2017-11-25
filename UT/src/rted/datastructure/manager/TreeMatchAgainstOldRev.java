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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import rted.distance.RTEDInfoSubTree;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

/**
 * @author Myoungkyu Song
 * @date Dec 5, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class TreeMatchAgainstOldRev extends TreeMatch {
	private TreeConvert				oldQueryTree_converter	= new TreeConvert();
	private TreeConvert				oldVerTree_converter	= new TreeConvert();
	private List<RTEDInfoSubTree>	subTreesOldVer			= new ArrayList<RTEDInfoSubTree>();

	/**
	 * Instantiates a new tree match against old rev.
	 */
	public TreeMatchAgainstOldRev() {
		mConverterQTree = oldQueryTree_converter;
		mConverterTTree = oldVerTree_converter;
		mSubTrees = subTreesOldVer;
	}

	/* (non-Javadoc)
	 * @see rted.datastructure.manager.TreeMatch#matchEditMappingEntry(java.lang.Integer[], org.eclipse.jdt.core.dom.CompilationUnit, ch.uzh.ifi.seal.changedistiller.treedifferencing.Node, ch.uzh.ifi.seal.changedistiller.treedifferencing.Node, java.io.File, boolean)
	 */
	public void matchEditMappingEntry(Node aQTreeOldRev, Node aOldVerTree, boolean aTrace) {
		if (aOldVerTree.getEntity().getType().isMethod() && //
				aOldVerTree.getValue().equals("getNextChar")) { // for tracing
			System.out.print("");
		}
		if (isSkip(aQTreeOldRev, aOldVerTree)) {
			return;
		}
		mTrace = aTrace;
		Enumeration<?> e = null;
		e = aOldVerTree.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iTtree = (Node) e.nextElement();
			if (isContinue(aQTreeOldRev, iTtree)) {
				continue;
			}
			cutDownMatchCost(iTtree);
			matchEditMapping(aQTreeOldRev, iTtree);
			iTtree.setFilePath(mFilePath);
			// iTtree.setCompilationUnit(mUnit);
		}
	}

	/* (non-Javadoc)
	 * @see rted.datastructure.manager.TreeMatch#matchEditMappingEntry(java.lang.Integer[], ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation, ch.uzh.ifi.seal.changedistiller.treedifferencing.Node, ch.uzh.ifi.seal.changedistiller.treedifferencing.Node, java.io.File, boolean)
	 */
	public void matchEditMappingEntry(Integer cntMatched[], JavaCompilation compilation, //
			Node qTreeOldRev, Node oldVerTree, //
			File fullFilePath, boolean isPrintable) {
		mTrace = isPrintable;
		Enumeration<?> e = oldVerTree.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			matchEditMapping(cntMatched, compilation, qTreeOldRev, iNode, fullFilePath);
		}
	}

}
