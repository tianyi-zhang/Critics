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
package edu.utexas.seal.plugins.ast.binding;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclaration;

/**
 * @author Myoungkyu Song
 * @date Oct 31, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public abstract class UTASTBindingManagerAbstract {
	protected List<Assignment>	leftHandVarAssignmentNodeList	= null;
	protected List<Assignment>	rightHandVarAssignmentNodeList	= null;
	protected List<SimpleName>	variableReferenceNodeList		= null;
	protected SimpleName		newAbstractNode					= null;

	public UTASTBindingManagerAbstract() {
		leftHandVarAssignmentNodeList = new ArrayList<Assignment>();
		rightHandVarAssignmentNodeList = new ArrayList<Assignment>();		
		variableReferenceNodeList = new ArrayList<SimpleName>();
	}

	public abstract VariableDeclaration getDeclaration();

	public SimpleName getNewAbstractNode() {
		return newAbstractNode;
	}

	public void setNewAbstractNode(SimpleName newAbstractNode) {
		this.newAbstractNode = newAbstractNode;
	}

	public void clearAssignmentNodeList() {
		this.leftHandVarAssignmentNodeList.clear();
		this.rightHandVarAssignmentNodeList.clear();
	}
	
	public List<Assignment> getLeftHandVarAssignmentNodeList() {
		return leftHandVarAssignmentNodeList;
	}

	public void addLeftHandVarAssignmentNodeList(Assignment e) {
		this.leftHandVarAssignmentNodeList.add(e);
	}

	public List<Assignment> getRightHandVarAssignmentNodeList() {
		return rightHandVarAssignmentNodeList;
	}

	public void addRightHandVarAssignmentNodeList(Assignment e) {
		this.rightHandVarAssignmentNodeList.add(e);
	}


	public void addReferenceNodeList(SimpleName n) {
		variableReferenceNodeList.add(n);
	}

	public List<SimpleName> getReferenceNodeList() {
		return this.variableReferenceNodeList;
	}

	public void clearReferenceNodeList() {
		this.variableReferenceNodeList.clear();
	}
}
