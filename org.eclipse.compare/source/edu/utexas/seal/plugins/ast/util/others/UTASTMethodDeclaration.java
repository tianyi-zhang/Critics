/*
 * @(#) UTASTLocalVariable.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.ast.util.others;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.swt.graphics.Point;

import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerParameter;

/**
 * @author Myoungkyu Song
 * @date Oct 31, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTASTMethodDeclaration extends ASTVisitor {
	Map<IVariableBinding, UTASTBindingManagerParameter>	parameterManagers	= null;
	private boolean										isLeft				= false;
	private Point										leftSelectedRegion	= null;
	private Point										rightSelectedRegion	= null;

	public UTASTMethodDeclaration(Point leftSelectedRegion, Point rightSelectedRegion, boolean isLeft) {
		this();
		this.isLeft = isLeft;
		this.leftSelectedRegion = leftSelectedRegion;
		this.rightSelectedRegion = rightSelectedRegion;
	}

	public UTASTMethodDeclaration() {
		parameterManagers = new HashMap<IVariableBinding, UTASTBindingManagerParameter>();
	}

	private boolean withinSelection(ASTNode node) {
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

	/**
	 * VariableDeclarationFragment: is the plain variable declaration part. Example:
	 * "int x=0, y=0;" contains two VariableDeclarationFragments, "x=0" and "y=0"
	 * 
	 * @param node
	 * @return static {@code false} to prevent that the simple name in the
	 *         declaration is understood by {@link #visit(SimpleName)} as
	 *         reference
	 */
	@Override
	public boolean visit(MethodDeclaration node) {
		if (withinSelection(node)) {
			SingleVariableDeclaration v = null;
			IVariableBinding b = null;
			UTASTBindingManagerParameter m = null;
			for (Iterator<?> i = node.parameters().iterator(); i.hasNext();) {
				Object o = i.next();
				if (o instanceof SingleVariableDeclaration) {
					v = (SingleVariableDeclaration) o;
					b = v.resolveBinding();
					m = new UTASTBindingManagerParameter(v);
					parameterManagers.put(b, m);
				}
			}
		}
		return false;
	}

	/**
	 * Getter for the resulting map.
	 * 
	 * @return a map with variable bindings as keys and {@link UTASTBindingManagerParameter} as values
	 */
	public Map<IVariableBinding, UTASTBindingManagerParameter> getParameterManagers() {
		return parameterManagers;
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
}