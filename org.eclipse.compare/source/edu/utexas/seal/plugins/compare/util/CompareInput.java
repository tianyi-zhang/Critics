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
package edu.utexas.seal.plugins.compare.util;

import java.util.Date;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Myoungkyu Song
 * @date Jan 23, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CompareInput extends CompareEditorInput {
	CompareItem	ancestor	= null;
	CompareItem	left		= null; // new CompareItem("Left", "new contents", new Date().getTime());
	CompareItem	right		= null; // new CompareItem("Right", "old contents", new Date().getTime());

	public CompareInput() {
		super(new CompareConfiguration());
	}

	public CompareInput(CompareItem left2, CompareItem right2) {
		this();
		ancestor = new CompareItem("Common", "contents", new Date().getTime());
		left = left2;
		right = right2;
		init();
	}

	private void init() {
		getCompareConfiguration().setLeftEditable(true);
		getCompareConfiguration().setRightEditable(true);
		getCompareConfiguration().setLeftLabel(left.getName());
		getCompareConfiguration().setRightLabel(right.getName());
	}

	protected Object prepareInput(IProgressMonitor pm) {
		return new DiffNode(left, right);
	}

	public CompareItem getAncestor() {
		return ancestor;
	}

	public void setAncestor(CompareItem ancestor) {
		this.ancestor = ancestor;
	}

	public CompareItem getLeft() {
		return left;
	}

	public void setLeft(CompareItem left) {
		this.left = left;
	}

	public CompareItem getRight() {
		return right;
	}

	public void setRight(CompareItem right) {
		this.right = right;
	}
}