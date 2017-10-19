/*
 * @(#) UTASTSelectionVisitor.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import edu.utexas.seal.plugins.analyzer.UTJFileDifferencer;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerClass;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerField;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerForStatement;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerForStatementEnh;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerLocalVariable;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerMethod;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerParameter;
import edu.utexas.seal.plugins.edit.UTAbstractEdit;

public class UTASTSelectionVisitor extends ASTVisitor {
	private ArrayList<ASTNode>											localDecl;
	private ArrayList<ASTNode>											sameNames;
	private ArrayList<ASTNode>											fieldDecl;
	private ArrayList<ASTNode>											methodDecl;
	private Map<IVariableBinding, UTASTBindingManagerField>				fieldManagers;
	private Map<IVariableBinding, UTASTBindingManagerLocalVariable>		variableManagers;
	private Map<IVariableBinding, UTASTBindingManagerParameter>			parameterManagers;
	private Map<IVariableBinding, UTASTBindingManagerForStatement>		forStatementManagers;
	private Map<IVariableBinding, UTASTBindingManagerForStatementEnh>	forStatementEnhManagers;
	private Map<IMethodBinding, UTASTBindingManagerMethod>				methodManagers			= null;
	private Map<ITypeBinding, UTASTBindingManagerClass>					inheritanceMangagers	= null;
	private boolean														isLeft;

	public UTASTSelectionVisitor(UTASTMethodVisitor visitor, boolean isLeft) {
		this.fieldManagers = visitor.getFieldManagers();
		this.variableManagers = visitor.getVariableManagers();
		this.parameterManagers = visitor.getParameterManagers();
		this.forStatementManagers = visitor.getForStatementManagers();
		this.forStatementEnhManagers = visitor.getForStatementEnhManagers();
		this.localDecl = new ArrayList<ASTNode>();
		this.sameNames = new ArrayList<ASTNode>();
		this.fieldDecl = new ArrayList<ASTNode>();
		this.isLeft = isLeft;
	}

	public UTASTSelectionVisitor(UTASTClassVisitor visitor, boolean isLeft) {
		this.fieldManagers = visitor.getFieldManagers();
		this.variableManagers = visitor.getVariableManagers();
		this.parameterManagers = visitor.getParameterManagers();
		this.forStatementManagers = visitor.getForStatementManagers();
		this.forStatementEnhManagers = visitor.getForStatementEnhManagers();
		localDecl = new ArrayList<ASTNode>();
		sameNames = new ArrayList<ASTNode>();
		this.isLeft = isLeft;
		// Only for Class Level
		this.methodManagers = visitor.getMethodManagers();
		this.inheritanceMangagers = visitor.getInheritanceMangagers();
		this.fieldDecl = new ArrayList<ASTNode>();
		this.methodDecl = new ArrayList<ASTNode>();
	}

	@Override
	public boolean visit(SimpleName node) {
		IBinding binding = node.resolveBinding();

		if (binding == null) {
			return true;
		}

		if (fieldManagers.containsKey(binding)) {
			// 1. finds identifiers referencing the same field
			UTASTBindingManagerField field = fieldManagers.get(binding);
			FieldDeclaration decl = (FieldDeclaration) field.getFieldDeclaration().getParent();
			if (!fieldDecl.contains(decl)) {
				fieldDecl.add(decl);
			}
			if (isChanged(decl)) {
				// If declaration changed, TRACK DOWN every same references
				List<SimpleName> nodes = field.getReferenceNodeList();
				for (SimpleName n : nodes) {
					if (!sameNames.contains(n)) {
						sameNames.add(n);
					}
				}
			}
			// for(SimpleName node : nodes){
			// System.out.println(node.toString());
			// }
		} else if (variableManagers.containsKey(binding)) {
			// 2. find identifiers referencing the same local variable
			UTASTBindingManagerLocalVariable var = variableManagers.get(binding);
			VariableDeclarationStatement decl = (VariableDeclarationStatement) var.getDeclaration().getParent();
			if (!localDecl.contains(decl)) {
				localDecl.add(decl);
			}
			// System.out.println(decl.toString());
			if (isChanged(decl)) {
				// If declaration changed, TRACK DOWN every same references
				List<SimpleName> nodes = var.getReferenceNodeList();
				for (SimpleName n : nodes) {
					if (!sameNames.contains(n)) {
						sameNames.add(n);
					}
				}
			}
			// for(SimpleName node : nodes){
			// System.out.println(node.getParent());
			// }
		} else if (parameterManagers.containsKey(binding)) {
			// 3. find identifiers referencing the same parameter
			UTASTBindingManagerParameter par = parameterManagers.get(binding);
			SingleVariableDeclaration decl = (SingleVariableDeclaration) par.getParameterDeclaration();
			if (!localDecl.contains(decl)) {
				localDecl.add(decl);
			}
			// System.out.println(decl.toString());
			if (isChanged(decl)) {
				// If declaration changed, TRACK DOWN every same references
				List<SimpleName> nodes = par.getReferenceNodeList();
				for (SimpleName n : nodes) {
					if (!sameNames.contains(n)) {
						sameNames.add(n);
					}
				}
			}
			// for(SimpleName node : nodes){
			// System.out.println(node.toString());
			// }
		} else if (forStatementEnhManagers.containsKey(binding)) {
			// 4. find identifiers referencing the same initializer in enhanced for loop
			UTASTBindingManagerForStatementEnh ini = forStatementEnhManagers.get(binding);
			SingleVariableDeclaration decl = (SingleVariableDeclaration) ini.getParameterDeclaration();
			// EnhancedForStatement s = (EnhancedForStatement) decl.getParent();
			if (!localDecl.contains(decl)) {
				localDecl.add((SingleVariableDeclaration) decl);
			}
			// System.out.println(s.getParameter());
			if (isChanged(decl)) {
				// If declaration changed, TRACK DOWN every same references
				List<SimpleName> nodes = ini.getReferenceNodeList();
				for (SimpleName n : nodes) {
					if (!sameNames.contains(n)) {
						sameNames.add(n);
					}
				}
			}
			// for(SimpleName node : nodes){
			// System.out.println(node.getParent());
			// }
		} else if (forStatementManagers.containsKey(binding)) {
			// 5. find identifiers referencing the same initializer in normal for loop
			UTASTBindingManagerForStatement ini = forStatementManagers.get(binding);
			VariableDeclarationExpression decl = (VariableDeclarationExpression) ini.getVariableDeclarationFragment().getParent();
			// ForStatement s = (ForStatement)decl.getParent();
			// System.out.println(decl);
			if (!localDecl.contains(decl)) {
				localDecl.add(decl);
			}
			if (isChanged(decl)) {
				// If declaration changed, TRACK DOWN every same references
				List<SimpleName> nodes = ini.getReferenceNodeList();
				for (SimpleName n : nodes) {
					if (!sameNames.contains(n)) {
						sameNames.add(n);
					}
				}
			}
			// for(SimpleName node : nodes){
			// System.out.println(node.toString());
			// }
		} else if (methodManagers != null) {
			if (methodManagers.containsKey(binding)) {
				// 6. find identifiers invoking the same method
				UTASTBindingManagerMethod method = methodManagers.get(binding);
				MethodDeclaration decl = (MethodDeclaration) method.getMethodDeclaration();
				if (!methodDecl.contains(decl)) {
					methodDecl.add(decl);
				}
				if (isChanged(decl)) {
					// If declaration changed, TRACK DOWN every same invocation
					List<SimpleName> nodes = method.getMethodInvocationList();
					for (SimpleName n : nodes) {
						if (!sameNames.contains(n)) {
							sameNames.add(n);
						}
					}
				}
			} else if (binding instanceof IMethodBinding) {
				// If this binding is IMethodBinding and hasn't been captured by the previous branches so far
				// Then it is declared in the super class
				ITypeBinding typeBinding = ((IMethodBinding) binding).getDeclaringClass();
				if (inheritanceMangagers.containsKey(typeBinding)) {
					UTASTBindingManagerClass superClass = inheritanceMangagers.get(typeBinding);
					if (isChanged(node)) {
						List<SimpleName> nodes = superClass.getInheritedMethodInvocList();
						for (SimpleName n : nodes) {
							if (!sameNames.contains(n)) {
								sameNames.add(n);
							}
						}
					}
				}
			}
		}
		return true;
	}

	private boolean isChanged(ASTNode node) {
		int edit_bgn = -1, edit_end = -1, node_bgn = -1, node_end = -1;
		node_bgn = node.getStartPosition();
		node_end = node_bgn + node.getLength();

		for (UTAbstractEdit edit : UTJFileDifferencer.edits) {
			if (isLeft) {
				edit_bgn = edit.getLeftEditOffsetBgn();
				edit_end = edit.getLeftEditOffsetEnd();
			} else {
				edit_bgn = edit.getRightEditOffsetBgn();
				edit_end = edit.getRightEditOffsetEnd();
			}
			if (((edit_bgn <= node_bgn) && (edit_end >= node_end)) || ((edit_bgn >= node_bgn) && (edit_end <= node_end))) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<ASTNode> getLocalDecl() {
		return localDecl;
	}
	
	public ArrayList<ASTNode> getFieldDecl() {
		return fieldDecl;
	}
	
	public ArrayList<ASTNode> getMethodDecl() {
		return methodDecl;
	}

	public ArrayList<ASTNode> getSameNames() {
		return sameNames;
	}
}
