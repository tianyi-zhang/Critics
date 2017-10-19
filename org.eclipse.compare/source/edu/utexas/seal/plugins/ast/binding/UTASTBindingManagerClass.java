/*
 * @(#) UTASTBindingManagerClass.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.ast.binding;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Type;

public class UTASTBindingManagerClass {
	private Type type;
	private List<SimpleName> methods = null;
	
	public UTASTBindingManagerClass(Type type){
		this.type = type;
		this.methods = new ArrayList<SimpleName>();
	}
	
	public Type getType(){
		return type;
	}
	
	public void addReferenceNodeList(SimpleName n) {
		methods.add(n);
	}
	
	public List<SimpleName> getInheritedMethodInvocList(){
		return methods;
	}
	
	public void clearMethodInvocationList(){
		this.methods.clear();
	} 
}
