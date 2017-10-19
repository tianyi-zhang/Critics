package edu.utexas.seal.plugins.ast.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import ch.uzh.ifi.seal.changedistiller.model.classifiers.SourceRange;

public class UTASTVariableCollector extends ASTVisitor{
	List<SimpleName> identifiers;
	List<IVariableBinding> variableBindings;
	SourceRange src;
	
	public UTASTVariableCollector(SourceRange range){
		this.src = range;
		identifiers = new ArrayList<SimpleName>();
		variableBindings = new ArrayList<IVariableBinding>();
	}
	
	@Override
	public boolean visit(FieldDeclaration node) {
		VariableDeclarationFragment f = null;
		IVariableBinding b = null;
		
		for (Iterator<?> iter = node.fragments().iterator(); iter.hasNext();) {
			f = (VariableDeclarationFragment) iter.next();
			b = f.resolveBinding();
			if(!variableBindings.contains(b)){
				variableBindings.add(b);
			}
		}
		
		return true;
	}

	@Override
	public boolean visit(VariableDeclarationStatement node) {	
		VariableDeclarationFragment f = null;
		IVariableBinding b = null;

		for (Iterator<?> iter = node.fragments().iterator(); iter.hasNext();) {
			f = (VariableDeclarationFragment) iter.next();
			b = f.resolveBinding();
			if(!variableBindings.contains(b)){
				variableBindings.add(b);
			}
		}
		
		return true;
	}
	
	@Override
	public boolean visit(MethodDeclaration node) {
		SingleVariableDeclaration v = null;
		IVariableBinding vb = null;

		for (Iterator<?> i = node.parameters().iterator(); i.hasNext();) {
			Object o = i.next();
			if (o instanceof SingleVariableDeclaration) {
				v = (SingleVariableDeclaration) o;
				vb = v.resolveBinding();
				if(!variableBindings.contains(vb)){
					variableBindings.add(vb);
				}
			}
		}
		
		return true;
	}
	
	@Override
	public boolean visit(ForStatement node) {
		VariableDeclarationFragment f = null;
		IVariableBinding b = null;

		for (Iterator<?> iter = node.initializers().iterator(); iter.hasNext();) {
			Object o = iter.next();
			if (o instanceof VariableDeclarationExpression) {
				VariableDeclarationExpression varDeclExp = (VariableDeclarationExpression) o;
				for (Iterator<?> jter = varDeclExp.fragments().iterator(); jter.hasNext();) {
					f = (VariableDeclarationFragment) jter.next();
					b = f.resolveBinding();
					if(!variableBindings.contains(b)){
						variableBindings.add(b);
					}
				}
			}
		}
		
		return true;
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		SingleVariableDeclaration v = node.getParameter();
		IVariableBinding b = v.resolveBinding();
		
		if(!variableBindings.contains(b)){
			variableBindings.add(b);
		}
		
		return true;
	}
	
	@Override
	public boolean visit(SimpleName node){
		if(!withinSelection(node)){
			return true;
		}
		if(variableBindings.contains(node.resolveBinding())){
			if(!identifiers.contains(node))	identifiers.add(node);
		}
		return true;
	}
	
	@Override
	public boolean visit(MethodInvocation node){
		if(!withinSelection(node)){
			return true;
		}
		SimpleName name = node.getName();
		if(!identifiers.contains(name)){
			identifiers.add(name);
		}
		return true;
	}
	
	private boolean withinSelection(ASTNode node) {
		int offset_bgn = -1, offset_end = -1, offset_sel_bgn = -1, offset_sel_end = -1;

		if (src != null) {
			offset_bgn = node.getStartPosition();
			offset_end = offset_bgn + node.getLength();
			offset_sel_bgn = src.getStart();
			offset_sel_end = src.getEnd();
			if ((offset_bgn >= offset_sel_bgn)
					&& (offset_end <= offset_sel_end + 1)) {
				return true;
			}
		}
		return false;
	}
	
	public List<SimpleName> getIdentifiers(){
		return identifiers;
	}
}
