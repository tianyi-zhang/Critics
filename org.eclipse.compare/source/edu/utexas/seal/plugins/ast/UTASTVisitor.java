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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.swt.graphics.Point;

import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerForStatementEnh;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerField;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerForStatement;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerLocalVariable;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerMethod;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerParameter;

/**
 * VariableDeclarationFragment: is the plain variable declaration part. Example:
 * "int x=0, y=0;" contains two VariableDeclarationFragments, "x=0" and "y=0"
 * 
 * @author Myoungkyu Song
 * @date Oct 27, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTASTVisitor extends ASTVisitor {
	private Map<IVariableBinding, UTASTBindingManagerField>				fieldManagers			= null;
	private Map<IVariableBinding, UTASTBindingManagerLocalVariable>		variableManagers		= null;
	private Map<IVariableBinding, UTASTBindingManagerParameter>			parameterManagers		= null;
	private Map<IVariableBinding, UTASTBindingManagerForStatementEnh>	forStatementEnhManagers	= null;
	private Map<IVariableBinding, UTASTBindingManagerForStatement>		forStatementManagers	= null;
	private Map<IMethodBinding, UTASTBindingManagerMethod>				methodManagers			= null;
	private List<MethodInvocation>										methodInvocationList	= null;
	private boolean														isLeft					= false;
	private Point														leftSelectedRegion		= null;
	private Point														rightSelectedRegion		= null;
	private boolean														__d__					= false;

	public UTASTVisitor(Point leftSelectedRegion, Point rightSelectedRegion, boolean isLeft) {
		this();
		this.isLeft = isLeft;
		this.leftSelectedRegion = leftSelectedRegion;
		this.rightSelectedRegion = rightSelectedRegion;
	}

	public UTASTVisitor() {
		fieldManagers = new HashMap<IVariableBinding, UTASTBindingManagerField>();
		variableManagers = new HashMap<IVariableBinding, UTASTBindingManagerLocalVariable>();
		parameterManagers = new HashMap<IVariableBinding, UTASTBindingManagerParameter>();
		forStatementEnhManagers = new HashMap<IVariableBinding, UTASTBindingManagerForStatementEnh>();
		forStatementManagers = new HashMap<IVariableBinding, UTASTBindingManagerForStatement>();
		methodManagers = new HashMap<IMethodBinding, UTASTBindingManagerMethod>();
		methodInvocationList = new ArrayList<MethodInvocation>();
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		if (__d__)
			System.out.println("[DBG] #decl- Field - " + node.toString().trim());
		VariableDeclarationFragment f = null;
		IVariableBinding b = null;
		UTASTBindingManagerField m = null;
		for (Iterator<?> iter = node.fragments().iterator(); iter.hasNext();) {
			f = (VariableDeclarationFragment) iter.next();
			b = f.resolveBinding();
			m = new UTASTBindingManagerField(f); // create the manager for the fragment
			fieldManagers.put(b, m);
		}
		return true;
	}

	@Override
	public boolean visit(VariableDeclarationStatement node) {
		if (__d__)
			System.out.println("[DBG] #decl- Local Variable - " + node.toString().trim());
		if (rearSelection(node)) {
			return true;
		}
		VariableDeclarationFragment f = null;
		IVariableBinding b = null;
		UTASTBindingManagerLocalVariable m = null;
		for (Iterator<?> iter = node.fragments().iterator(); iter.hasNext();) {
			f = (VariableDeclarationFragment) iter.next();
			b = f.resolveBinding();
			m = new UTASTBindingManagerLocalVariable(f); // create the manager for the fragment
			variableManagers.put(b, m);
		}
		return true;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		if (__d__) {
			System.out.println("[DBG] #decl- Method - " + node.getName().toString());
			System.out.println("[DBG] #decl- Parameter - " + toStringArray(node.parameters().toArray()));
		}
		// if (hasSelection(node) || (withinSelection(node) && !rearSelection(node))) {
		SingleVariableDeclaration v = null;
		IVariableBinding vb = null;
		UTASTBindingManagerParameter mp = null;
		for (Iterator<?> i = node.parameters().iterator(); i.hasNext();) {
			Object o = i.next();
			if (o instanceof SingleVariableDeclaration) {
				v = (SingleVariableDeclaration) o;
				vb = v.resolveBinding();
				mp = new UTASTBindingManagerParameter(v);
				parameterManagers.put(vb, mp);
			}

		}
		// }
		IMethodBinding mb = null;
		UTASTBindingManagerMethod mm = null;
		mb = node.resolveBinding();
		mm = new UTASTBindingManagerMethod(node);
		methodManagers.put(mb, mm);
		return true;
	}

	@Override
	public boolean visit(MethodInvocation node) {
		if (__d__) {
			System.out.println("[DBG] #invocation- " + node.getName().toString());
		}
		// if (hasSelection(node) || (withinSelection(node) && !rearSelection(node))) {
		methodInvocationList.add(node);
		// }
		return true;
	}

	@Override
	public boolean visit(ForStatement node) {
		VariableDeclarationFragment f = null;
		IVariableBinding b = null;
		UTASTBindingManagerForStatement m = null;
		for (Iterator<?> iter = node.initializers().iterator(); iter.hasNext();) {
			Object o = iter.next();
			if (o instanceof VariableDeclarationExpression) {
				VariableDeclarationExpression varDeclExp = (VariableDeclarationExpression) o;
				for (Iterator<?> jter = varDeclExp.fragments().iterator(); jter.hasNext();) {
					f = (VariableDeclarationFragment) jter.next();
					b = f.resolveBinding();
					m = new UTASTBindingManagerForStatement(f);
					forStatementManagers.put(b, m);
				}
			}
		}
		return true;
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		if (__d__)
			System.out.println("[DBG] + EnForStatement " + node);
		// if (hasSelection(node) || (withinSelection(node) && !rearSelection(node))) {
		SingleVariableDeclaration v = node.getParameter();
		IVariableBinding b = v.resolveBinding();
		UTASTBindingManagerForStatementEnh m = new UTASTBindingManagerForStatementEnh(v);
		forStatementEnhManagers.put(b, m);
		// }
		return true;
	}

	/**
	 * Visits {@link Assignment} AST nodes (e.g. {@code x = 7 + 8} ). Resolves
	 * the binding of the left hand side (in the example: {@code x}). If the
	 * binding is found in the {@link #variableManagers} map, we have an
	 * assignment of a local variable. The variable binding manager of this
	 * local variable then has to be informed about this assignment.
	 * 
	 * @param node
	 */
	public boolean visit(Assignment node) {
		// if (__d__)
		// System.out.println("[DBG] asg - " + node);
		// Object l = node.getLeftHandSide();
		// Object r = node.getRightHandSide();
		//
		// if (l instanceof SimpleName) {
		// addAssignmentNode(node, ((SimpleName) l).resolveBinding(), true);
		// }
		// if (r instanceof SimpleName) {
		// addAssignmentNode(node, ((SimpleName) r).resolveBinding(), false);
		// }
		return true;
	}

	/**
	 * 
	 * @param node
	 * @param binding
	 */
	void addAssignmentNode(Assignment node, IBinding binding, boolean isLeft) {
		if (isLeft) {
			if (parameterManagers.containsKey(binding)) {
				parameterManagers.get(binding).addLeftHandVarAssignmentNodeList(node);
			} else if (variableManagers.containsKey(binding)) {
				variableManagers.get(binding).addLeftHandVarAssignmentNodeList(node);
			} else if (fieldManagers.containsKey(binding)) {
				fieldManagers.get(binding).addLeftHandVarAssignmentNodeList(node);
			}
		} else {
			if (parameterManagers.containsKey(binding)) {
				parameterManagers.get(binding).addRightHandVarAssignmentNodeList(node);
			} else if (variableManagers.containsKey(binding)) {
				variableManagers.get(binding).addRightHandVarAssignmentNodeList(node);
			} else if (fieldManagers.containsKey(binding)) {
				fieldManagers.get(binding).addRightHandVarAssignmentNodeList(node);
			}
		}
	}

	/**
	 * Visits {@link SimpleName} AST nodes. Resolves the binding of the simple
	 * name and looks for it in the {@link #variableManagers} map. If the
	 * binding is found, this is a reference to a local variable. The variable
	 * binding manager of this local variable then has to be informed about that
	 * reference.
	 * 
	 * @param node
	 */
	public boolean visit(SimpleName node) {
		// Jan 30, 2014 9:50:55 PM
		// (Problem) It was unable to visit a right-hand reference of "VariableDeclaration",
		// eg., "someName" in the following code.
		// void foo (String someName) { String myName = someName; }
		/* 
		if (node.getParent() instanceof VariableDeclarationFragment) {
			return true;
		} else
		*/
		if (node.getParent() instanceof SingleVariableDeclaration) {
			return true;
		}

		IBinding binding = node.resolveBinding();

		// Some SimpleName like object type Server doesn't have binding information, returns null
		// But all managers has a null key, so they will be binded
		if (binding == null) {
			return true;
		}
		if (fieldManagers.containsKey(binding)) {
			fieldManagers.get(binding).addReferenceNodeList(node);
			if (__d__)
				System.out.println("[DBG] * ref - Field -" + node.getParent().toString().trim());
		} else if (variableManagers.containsKey(binding)) {
			variableManagers.get(binding).addReferenceNodeList(node);
			if (__d__)
				System.out.println("[DBG] * ref - Local Variable -" + node.getParent().toString().trim());
		} else if (parameterManagers.containsKey(binding)) {
			parameterManagers.get(binding).addReferenceNodeList(node);
			if (__d__)
				System.out.println("[DBG] * ref - Parameter - " + node.getParent().toString().trim());
		} else if (forStatementEnhManagers.containsKey(binding)) {
			forStatementEnhManagers.get(binding).addReferenceNodeList(node);
			if (__d__)
				System.out.println("[DBG] * ref - Enhanced For -" + node.getParent().toString().trim());
		} else if (forStatementManagers.containsKey(binding)) {
			forStatementManagers.get(binding).addReferenceNodeList(node);
			if (__d__)
				System.out.println("[DBG] * ref - For - " + node.getParent().toString().trim());
		}
		return true;
	}

	/**
	 * Getter for the resulting map.
	 * 
	 * @return a map with variable bindings as keys and values
	 */
	public Map<IVariableBinding, UTASTBindingManagerField> getFieldManagers() {
		return fieldManagers;
	}

	public Map<IVariableBinding, UTASTBindingManagerLocalVariable> getLocalVariableManagers() {
		return variableManagers;
	}

	public Map<IVariableBinding, UTASTBindingManagerParameter> getParameterManagers() {
		return parameterManagers;
	}

	public Map<IVariableBinding, UTASTBindingManagerForStatementEnh> getForStatEnhManagers() {
		return forStatementEnhManagers;
	}

	public Map<IVariableBinding, UTASTBindingManagerForStatement> getForStatManagers() {
		return forStatementManagers;
	}

	public Map<IMethodBinding, UTASTBindingManagerMethod> getMethodManagers() {
		return methodManagers;
	}

	/**
	 * 
	 * @param unit
	 *            the AST root node. Bindings have to have been resolved.
	 * @param leftSelectedRegion
	 * @param rightSelectedRegion
	 */
	public void process(CompilationUnit unit) {
		unit.accept(this);
	}

	public void invocAnaysis() {
		for (int i = 0; i < methodInvocationList.size(); i++) {
			MethodInvocation m = methodInvocationList.get(i);
			IMethodBinding b = null;
			b = m.resolveMethodBinding();
			if (methodManagers.containsKey(b)) {
				// methodManagers.get(b).addReferenceNodeList(m);
				if (__d__)
					System.out.println("[DBG] * ref - MethodInvocation - " + m.toString());
			}
		}
	}

	String toStringArray(Object[] array) {
		String s = "";
		for (int i = 0; i < array.length; i++) {
			s += array[i].toString();
		}
		return s;
	}

	boolean withinSelection(ASTNode node) {
		int offset_bgn = -1, offset_end = -1, offset_sel_bgn = -1, offset_sel_end = -1;
		if (leftSelectedRegion != null && rightSelectedRegion != null) {
			offset_bgn = node.getStartPosition();
			offset_end = offset_bgn + node.getLength();
			if (isLeft) {
				offset_sel_bgn = leftSelectedRegion.x;
				offset_sel_end = leftSelectedRegion.x + leftSelectedRegion.y;
			} else {
				offset_sel_bgn = rightSelectedRegion.x;
				offset_sel_end = rightSelectedRegion.x + rightSelectedRegion.y;
			}
			if ((offset_sel_bgn <= offset_bgn && offset_sel_bgn <= offset_end) && //
					(offset_sel_end >= offset_bgn && offset_sel_end >= offset_end)) {
				return true;
			}
		}
		return false;
	}

	boolean hasSelection(ASTNode node) {
		int offset_bgn = -1, offset_end = -1, offset_sel_bgn = -1, offset_sel_end = -1;
		if (leftSelectedRegion != null && rightSelectedRegion != null) {
			offset_bgn = node.getStartPosition();
			offset_end = offset_bgn + node.getLength();
			if (isLeft) {
				offset_sel_bgn = leftSelectedRegion.x;
				offset_sel_end = leftSelectedRegion.x + leftSelectedRegion.y;
			} else {
				offset_sel_bgn = rightSelectedRegion.x;
				offset_sel_end = rightSelectedRegion.x + rightSelectedRegion.y;
			}
			if (offset_bgn <= offset_sel_bgn && offset_bgn <= offset_sel_end && //
					offset_end >= offset_sel_bgn && offset_end >= offset_sel_end) {
				return true;
			}
		}
		return false;
	}

	boolean frontSelection(ASTNode node) {
		int offset_sel_bgn = -1, offset_end = -1;
		if (leftSelectedRegion != null && rightSelectedRegion != null) {
			offset_end = node.getStartPosition() + node.getLength();
			if (isLeft) {
				offset_sel_bgn = leftSelectedRegion.x;
			} else {
				offset_sel_bgn = rightSelectedRegion.x;
			}
			if (offset_end < offset_sel_bgn) {
				return true;
			}
		}
		return false;
	}

	boolean rearSelection(ASTNode node) {
		int offset_sel_end = -1, offset_bgn = -1;
		if (leftSelectedRegion != null && rightSelectedRegion != null) {
			offset_bgn = node.getStartPosition();
			// offset_end = node.getStartPosition() + node.getLength();
			if (isLeft) {
				// offset_sel_bgn = leftSelectedRegion.x;
				offset_sel_end = leftSelectedRegion.x + leftSelectedRegion.y;
			} else {
				// offset_sel_bgn = rightSelectedRegion.x;
				offset_sel_end = rightSelectedRegion.x + rightSelectedRegion.y;
			}
			if (offset_sel_end < offset_bgn) {
				return true;
			}
		}
		return false;
	}

	public ASTNode[] getSelectedNode() {

		return null;
	}
}