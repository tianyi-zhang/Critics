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
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;

/**
 * @author troy
 * 
 */
public class UTASTBindingManagerMethod {
	private MethodDeclaration	methodDeclaration;
	private List<Assignment>	leftHandVarAssignmentNodeList	= null;
	private List<Assignment>	rightHandVarAssignmentNodeList	= null;
	private List<SimpleName>	methodInvocList					= null;

	public UTASTBindingManagerMethod(MethodDeclaration method) {
		leftHandVarAssignmentNodeList = new ArrayList<Assignment>();
		rightHandVarAssignmentNodeList = new ArrayList<Assignment>();
		methodInvocList = new ArrayList<SimpleName>();
		this.methodDeclaration = method;
	}

	public MethodDeclaration getMethodDeclaration() {
		return methodDeclaration;
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
		methodInvocList.add(n);
	}

	public List<SimpleName> getMethodInvocationList() {
		return methodInvocList;
	}

	public void clearMethodInvocationList() {
		this.methodInvocList.clear();
	}
}
