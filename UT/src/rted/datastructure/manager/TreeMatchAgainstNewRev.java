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

import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import rted.distance.RTEDInfoSubTree;

/**
 * @author Myoungkyu Song
 * @date Dec 5, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class TreeMatchAgainstNewRev extends TreeMatch {
	private TreeConvert				oldQueryTree_T_converter	= new TreeConvert();
	private TreeConvert				newVerTree_converter		= new TreeConvert();
	private List<RTEDInfoSubTree>	subTreesNewVer				= new ArrayList<RTEDInfoSubTree>();

	/**
	 * Instantiates a new tree match against new rev.
	 */
	public TreeMatchAgainstNewRev() {
		mConverterQTree = oldQueryTree_T_converter;
		mConverterTTree = newVerTree_converter;
		mSubTrees = subTreesNewVer;
	}

	/* (non-Javadoc)
	 * @see rted.datastructure.manager.TreeMatch#matchEditMappingEntry(java.lang.Integer[], org.eclipse.jdt.core.dom.CompilationUnit, ch.uzh.ifi.seal.changedistiller.treedifferencing.Node, ch.uzh.ifi.seal.changedistiller.treedifferencing.Node, java.io.File, boolean)
	 */
	public void matchEditMappingEntry(Node aQTreeNewRev, Node aNewVerTree, boolean aTrace) {
		if (isSkip(aQTreeNewRev, aNewVerTree)) {
			return;
		}
		mTrace = aTrace;
		Enumeration<?> e = aNewVerTree.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iTtree = (Node) e.nextElement();
			if (isContinue(aQTreeNewRev, iTtree)) {
				continue;
			}
			cutDownMatchCost(iTtree);
			matchEditMapping(aQTreeNewRev, iTtree);
			iTtree.setFilePath(mFilePath);
			// iTtree.setCompilationUnit(mUnit);
		}
	}

	/* (non-Javadoc)
	 * @see rted.datastructure.manager.TreeMatch#matchEditMappingEntry(java.lang.Integer[], ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation, ch.uzh.ifi.seal.changedistiller.treedifferencing.Node, ch.uzh.ifi.seal.changedistiller.treedifferencing.Node, java.io.File, boolean)
	 */
	public void matchEditMappingEntry(Integer cntMatched[], JavaCompilation compilation, //
			Node qTreeNewRev, Node newVerTree, //
			File fullFilePath, boolean isPrintable) {
		mTrace = isPrintable;
		Enumeration<?> e = newVerTree.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			matchEditMapping(cntMatched, compilation, qTreeNewRev, iNode, fullFilePath);
		}
	}
}
