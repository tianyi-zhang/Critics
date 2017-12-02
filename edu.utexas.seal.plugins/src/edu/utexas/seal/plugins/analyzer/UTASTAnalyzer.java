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
package edu.utexas.seal.plugins.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import edu.utexas.seal.plugins.ast.UTASTVisitor;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerField;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerForStatement;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerForStatementEnh;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerLocalVariable;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerMethod;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerParameter;

public class UTASTAnalyzer extends ASTVisitor {
	private int															start;
	private int															end;
	private List<ASTNode>												relatedNodes;
	private Map<IVariableBinding, UTASTBindingManagerField>				fieldManagers			= null;
	private Map<IVariableBinding, UTASTBindingManagerLocalVariable>		variableManagers		= null;
	private Map<IVariableBinding, UTASTBindingManagerParameter>			parameterManagers		= null;
	private Map<IVariableBinding, UTASTBindingManagerForStatementEnh>	forStatementEnhManagers	= null;
	private Map<IVariableBinding, UTASTBindingManagerForStatement>		forStatementManagers	= null;
	private Map<IMethodBinding, UTASTBindingManagerMethod>				methodManagers			= null;
	private boolean														__d__					= false;

	/**
	 * Instantiates a new uTAST analyzer.
	 * 
	 * @param selected the selected
	 * @param visitor the visitor
	 */
	public UTASTAnalyzer(ASTNode selected, UTASTVisitor visitor) {
		start = selected.getStartPosition();
		end = start + selected.getLength();
		relatedNodes = new ArrayList<ASTNode>();
		fieldManagers = visitor.getFieldManagers();
		variableManagers = visitor.getLocalVariableManagers();
		parameterManagers = visitor.getParameterManagers();
		forStatementEnhManagers = visitor.getForStatEnhManagers();
		forStatementManagers = visitor.getForStatManagers();
		methodManagers = visitor.getMethodManagers();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SimpleName)
	 */
	public boolean visit(SimpleName node) {
		if (node.getParent() instanceof VariableDeclarationFragment) {
			return true;
		} else if (node.getParent() instanceof SingleVariableDeclaration) {
			return true;
		}

		IBinding binding = node.resolveBinding();

		// Some SimpleName like object type Server doesn't have binding information, returns null
		// But all managers has a null key, so they will be binded
		if (binding == null) {
			return true;
		}

		if (fieldManagers.containsKey(binding)) {
			ASTNode o = fieldManagers.get(binding).getDeclaration().getParent();
			if (!relatedNodes.contains(o))
				relatedNodes.add(o);
			if (__d__)
				System.out.println("[DBG] * ref - add Field -" + node.getParent().toString().trim());
		} else if (variableManagers.containsKey(binding)) {
			ASTNode o = variableManagers.get(binding).getDeclaration().getParent();
			if (!relatedNodes.contains(o))
				relatedNodes.add(o);
			if (__d__)
				System.out.println("[DBG] * ref - add Local Variable -" + node.getParent().toString().trim());
		} else if (parameterManagers.containsKey(binding)) {
			ASTNode o = parameterManagers.get(binding).getDeclaration();
			relatedNodes.add(o);
			if (__d__)
				System.out.println("[DBG] * ref - add Parameter - " + node.getParent().toString().trim());
		} else if (forStatementEnhManagers.containsKey(binding)) {
			ASTNode o = forStatementEnhManagers.get(binding).getDeclaration();
			if (!relatedNodes.contains(o))
				relatedNodes.add(o);
			if (__d__)
				System.out.println("[DBG] * ref - add Enhanced For -" + node.getParent().toString().trim());
		} else if (forStatementManagers.containsKey(binding)) {
			ASTNode o = forStatementManagers.get(binding).getDeclaration();
			if (!relatedNodes.contains(o))
				relatedNodes.add(o);
			if (__d__)
				System.out.println("[DBG] * ref - add For - " + node.getParent().toString().trim());
		} else if (methodManagers.containsKey(binding)) {
			ASTNode o = methodManagers.get(binding).getMethodDeclaration();
			if (!relatedNodes.contains(o))
				relatedNodes.add(o);
			if (__d__)
				System.out.println("[DBG] * ref - add Method - " + node.getParent().toString().trim());
		}
		return true;
	}

	public List<ASTNode> getRelatedNodes() {
		return relatedNodes;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}
}
