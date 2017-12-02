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
package edu.utexas.seal.plugins.generization;

import java.util.Enumeration;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import edu.utexas.seal.plugins.crystal.internal.UTASTNodeSearcher;
import edu.utexas.seal.plugins.treegraph.view.CriticsContextSelectionView;
import edu.utexas.seal.plugins.util.UTCriticsPairFileInfo;
import edu.utexas.seal.plugins.util.root.UTPlugin;

public class CriticsGenerizationOld {
	Node queryTreeOldRev = null;
	Node queryTreeNewRev = null;
	ASTNode rootOld		 = null;
	ASTNode rootNew		 = null;
	
	public CriticsGenerizationOld(){
		CriticsContextSelectionView vwIntCxtSelOldRev = (CriticsContextSelectionView) UTPlugin.getView(UTPlugin.ID_VIEW_SELECTCTX_OLDREV);
		CriticsContextSelectionView vwIntCxtSelNewRev = (CriticsContextSelectionView) UTPlugin.getView(UTPlugin.ID_VIEW_SELECTCTX_NEWREV);
		if (vwIntCxtSelOldRev.getRootNode() == null || vwIntCxtSelNewRev.getRootNode() == null) {
			System.out.println("[USG] PLEASE OPEN CONTEXT SELECTION VIEW");
			return;
		}
		
		queryTreeOldRev = (Node) vwIntCxtSelOldRev.getRootNode().getData();
		queryTreeNewRev = (Node) vwIntCxtSelNewRev.getRootNode().getData();
		
		rootOld = getRoot(false);
		rootNew = getRoot(true);
	}
	
	ASTNode getRoot(boolean isLeft) {
		ICompilationUnit cpu = null;

		if (isLeft) {
			// The selected node is from the new version
			cpu = UTCriticsPairFileInfo.getLeftICompilationUnit();
		} else {
			// The selected node is from the old version
			cpu = UTCriticsPairFileInfo.getRightICompilationUnit();
		}

		// Get AST root node
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(cpu);
		parser.setResolveBindings(true);
		ASTNode root = (CompilationUnit) parser.createAST(null /* IProgressMonitor */);
		return root;
	}
	
	
	public void generizeOld(){
		Node rootOld = queryTreeOldRev.copy();
		
		// get all variables that need parameterized
		Enumeration<?> e = rootOld.postorderEnumeration();
		Node temp = null;
		while (e.hasMoreElements()) {
			temp = (Node) e.nextElement();
			if (temp.isExcludedByUser()) {
				temp.removeFromParent();
			}else{
				Map<SimpleName, Boolean> map = temp.getParamMap();
				for(SimpleName var : map.keySet()){
					if(map.get(var)){
						replace(temp, var);
					}
				}
			}
		}
		
		rootOld.print();
	}
	
	public void generizeNew(){
		Node rootNew = queryTreeNewRev.copy();
		
		// parameterize all variables with "$var"
		Enumeration<?> e = rootNew.postorderEnumeration();
		Node temp = null;
		while (e.hasMoreElements()) {
			temp = (Node) e.nextElement();
			if (temp.isExcludedByUser()) {
				temp.removeFromParent();
			}else{
				Map<SimpleName, Boolean> map = temp.getParamMap();
				for(SimpleName var : map.keySet()){
					if(map.get(var)){
						replace(temp, var);
					}
				}
			}
		}
		
		rootNew.print();
	}
	
	private void replace(Node node, SimpleName var){
		switch(node.getLabel().name()){
		case "METHOD":
			replaceInMethodDecl(node, var);
			break;
		case "FOREACH_STATEMENT":
			replaceInEnhancedFor(node, var);
			break;
		case "FOR_STATEMENT":
			replaceInFor(node, var);
			break;
		case "BLOCK":
			break; // Do nothing
		case "BODY":
			break; // Do nothing
		case "DO_STATEMENT":
			break; // Do nothing
		case "ELSE_STATEMENT":
			break; // Do nothing
		case "IF_STATEMENT":
			replaceInIf(node, var);
			break;
		case "SWITCH_CASE":
			replaceInSwitch(node, var);
			break;
		case "THEN_STATEMENT":
			break; // Do nothing
		case "TRY_STATEMENT":
			break; // Do nothing
		case "WHILE_STATEMENT":
			replaceInWhile(node, var);
			break;
		case "PARAMETERS":
			break; // Do nothing
		case "ARRAY_TYPE":
			replaceInArrayType(node, var);
			break; 
		case "FOR_INIT" :
			replaceInForInit(node, var);
			break;
		default:
			replaceInOthers(node, var);
			break;
		}
	}

	private void replaceInArrayType(Node node, SimpleName var) {
		String value = node.getValue();
		String type = (value.split(";"))[1];
		value = "$var" + ":" + type;
		node.setValue(value);
	}

	/**
	 * For Init is very special. It only represents the comma(;), but the value of it is the entire variable declaration.
	 * 
	 * @param node
	 * @param var
	 */
	private void replaceInForInit(Node node, SimpleName var) {
		Node child = (Node) node.getChildAt(0);
		String value = child.getValue();
		node.setValue(value + ";");
		return;
	}

	private void replaceInOthers(Node node, SimpleName var) {
		ASTNode src = null;
		if(node.isLeft()){
			src = getASTNode(node, rootNew);
		}else{
			src = getASTNode(node, rootOld);
		}
		
		String value = src.toString();
		int offset = var.getStartPosition() - node.getEntity().getStartPosition();
		String before = value.substring(0, offset);
		String after = value.substring(offset + var.getLength());
		value = before + "$var" + after;
		node.setValue(value);
	}

	private void replaceInWhile(Node node, SimpleName var) {
		ASTNode src = null;
		if(node.isLeft()){
			src = getASTNode(node, rootNew);
		}else{
			src = getASTNode(node, rootOld);
		}
		
		if(src instanceof WhileStatement){
			WhileStatement s = (WhileStatement)src;
			// generally in the form of (expr)
			String value = node.getValue();
			
			// get expression 
			Expression expr = s.getExpression();
			if(isCoveredBy(expr, var)){
				int offset = var.getStartPosition() - expr.getStartPosition();
				String before = value.substring(0, offset + 1);
				String after = value.substring(offset + var.getLength() + 1);
				value = before + "$var" + after;
				node.setValue(value);
//				System.out.println(value);
			}
		}
	}

	private void replaceInSwitch(Node node, SimpleName var) {
		ASTNode src = null;
		if(node.isLeft()){
			src = getASTNode(node, rootNew);
		}else{
			src = getASTNode(node, rootOld);
		}
		
		if(src instanceof SwitchStatement){
			SwitchStatement s = (SwitchStatement)src;
			// generally in the form of (expr)
			String value = node.getValue();
			
			// get expression 
			Expression expr = s.getExpression();
			if(isCoveredBy(expr, var)){
				int offset = var.getStartPosition() - expr.getStartPosition();
				String before = value.substring(0, offset + 1);
				String after = value.substring(offset + var.getLength() + 1);
				value = before + "$var" + after;
				node.setValue(value);
				// System.out.println(value);
			}
		}
		
	}

	private void replaceInIf(Node node, SimpleName var) {
		ASTNode src = null;
		if(node.isLeft()){
			src = getASTNode(node, rootNew);
		}else{
			src = getASTNode(node, rootOld);
		}
		
		if(src instanceof IfStatement){
			IfStatement s = (IfStatement)src;
			// generally in the form of (expr)
			String value = node.getValue();
			
			// get expression 
			Expression expr = s.getExpression();
			if(isCoveredBy(expr, var)){
				int offset = var.getStartPosition() - expr.getStartPosition();
				String before = value.substring(0, offset + 1);
				String after = value.substring(offset + var.getLength() + 1);
				value = before + "$var" + after;
				node.setValue(value);
				// System.out.println(value);
			}
		}
	}

	private void replaceInFor(Node node, SimpleName var) {
		ASTNode src = null;
		if(node.isLeft()){
			src = getASTNode(node, rootNew);
		}else{
			src = getASTNode(node, rootOld);
		}
		
		if(src instanceof ForStatement){
			ForStatement s = (ForStatement)src;
			// value is generally in the form of (expr)
			String value = node.getValue();
			
			// get expression
			Expression expr = s.getExpression();
			if(isCoveredBy(expr, var)){
				int offset = var.getStartPosition() - expr.getStartPosition();
				String before = value.substring(0, offset + 1);
				String after = value.substring(offset + var.getLength() + 1);
				value = before + "$var" + after;
				node.setValue(value);
			}
		}
	}

	private void replaceInEnhancedFor(Node node, SimpleName var) {
		ASTNode src = null;
		if(node.isLeft()){
			src = getASTNode(node, rootNew);
		}else{
			src = getASTNode(node, rootOld);
		}
		
		if( src instanceof EnhancedForStatement){
			EnhancedForStatement s = (EnhancedForStatement)src;
			String value = node.getValue();
			String e = (value.split(":"))[1];
			String p = (value.split(":"))[0];
			String newExpr = e;
			String newParam = p;
			
			// get expression 
			Expression expr = s.getExpression();
			if(isCoveredBy(expr, var)){
				int offset = var.getStartPosition() - expr.getStartPosition();
				String before = e.substring(0, offset);
				String after = e.substring(offset + var.getLength());
				newExpr = before + "$var" + after;
				// System.out.println(newExpr);
			}
			
			// get parameter declarations
			SingleVariableDeclaration decl = s.getParameter();
			if(isCoveredBy(decl, var)){
				int offset = var.getStartPosition() - decl.getStartPosition();
				String before = p.substring(0, offset);
				String after = p.substring(offset + var.getLength());
				newParam = before + "$var" + after;
			}
			
			node.setValue(newParam + ":" + newExpr);
		}
	}
	
	/**
	 * Method Node only has a single variable to parameterize, method name
	 * So we don't need to pinpoint which substring needs to be replaced
	 * 
	 * @param node
	 * @param var
	 */
	private void replaceInMethodDecl(Node node, SimpleName var) {
		node.setValue("$method");
	}
	
	private ASTNode getASTNode(Node node, ASTNode root) {
		UTASTNodeSearcher searcher = new UTASTNodeSearcher(node);
		return searcher.search(root);
	}
	
	private boolean isCoveredBy(ASTNode container, ASTNode node){
		int cBgn = container.getStartPosition();
		int cEnd = cBgn + container.getLength();
		int bgn = node.getStartPosition();
		int end = bgn + node.getLength();
		
		if(cBgn <= bgn && cEnd >= end){
			return true;
		}else{
			return false;
		}
	}
}
