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
package edu.utexas.seal.plugins.ast;

import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.SourceRange;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

public class UTSourceFragment {
	JavaCompilation	javaCompilation;
	Node			node;
	SourceRange		sourceRange;
	String			fragment;

	/**
	 * @param javaCompilation
	 * @param node
	 * @param sourceRange
	 */
	public UTSourceFragment(JavaCompilation javaCompilation, Node node, SourceRange sourceRange) {
		this.javaCompilation = javaCompilation;
		this.node = node;
		this.sourceRange = sourceRange;
		this.fragment = javaCompilation.getSource().substring(sourceRange.getStart(), sourceRange.getEnd() + 1);
	}

	/**
	 * @return the javaCompilation
	 */
	public JavaCompilation getJavaCompilation() {
		return javaCompilation;
	}

	/**
	 * @param javaCompilation
	 *            the javaCompilation to set
	 */
	public void setJavaCompilation(JavaCompilation javaCompilation) {
		this.javaCompilation = javaCompilation;
	}

	/**
	 * @return the node
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * @param node
	 *            the node to set
	 */
	public void setNode(Node node) {
		this.node = node;
	}

	/**
	 * @return the sourceRange
	 */
	public SourceRange getSourceRange() {
		return sourceRange;
	}

	/**
	 * @param sourceRange
	 *            the sourceRange to set
	 */
	public void setSourceRange(SourceRange sourceRange) {
		this.sourceRange = sourceRange;
	}

	/**
	 * @return the fragment
	 */
	public String getFragment() {
		return fragment;
	}

	/**
	 * @param fragment
	 *            the fragment to set
	 */
	public void setFragment(String fragment) {
		this.fragment = fragment;
	}

}
