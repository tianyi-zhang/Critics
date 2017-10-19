/*
 * @(#) UTASTNodeCoverterAbstract.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package ut.seal.plugins.utils.ast;

/*
 * #%L
 * ChangeDistiller
 * %%
 * Copyright (C) 2011 - 2013 Software Architecture and Evolution Lab, Department of Informatics, UZH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.junit.BeforeClass;

import ch.uzh.ifi.seal.changedistiller.ast.java.Comment;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaDeclarationConverter;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaMethodBodyConverter;
import ch.uzh.ifi.seal.changedistiller.distilling.Distiller;
import ch.uzh.ifi.seal.changedistiller.distilling.DistillerFactory;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.SourceRange;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.model.entities.StructureEntityVersion;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ch.uzh.ifi.seal.changedistiller.util.CompilationUtils;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerAbstract;

/**
 * @author Myoungkyu Song
 * @date Nov 21, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public abstract class UTASTNodeCoverterAbstract extends UTASTNodeCoverterInjector {

	protected static JavaDeclarationConverter	sDeclarationConverter;
	protected static JavaMethodBodyConverter	sMethodBodyConverter;
	protected static UTASTNodeFinder			sASTNodeFinder;
	protected String							mSource;
	protected File								mFile;

	/**
	 * Initialize.
	 * 
	 * @throws Exception the exception
	 */
	@BeforeClass
	public static void initialize() throws Exception {
		sDeclarationConverter = sInjector.getInstance(JavaDeclarationConverter.class);
		sMethodBodyConverter = sInjector.getInstance(JavaMethodBodyConverter.class);
		sASTNodeFinder = sInjector.getInstance(UTASTNodeFinder.class);
	}

	/**
	 * Gets the distiller.
	 * 
	 * @param structureEntity the structure entity
	 * @return the distiller
	 */
	protected Distiller getDistiller(StructureEntityVersion structureEntity) {
		return sInjector.getInstance(DistillerFactory.class).create(structureEntity);
	}

	/**
	 * Convert method body.
	 * 
	 * @param methodDecl the method decl
	 * @param compilation the compilation
	 * @return the node
	 */
	protected Node convertMethodBody(MethodDeclaration methodDecl, JavaCompilation compilation) {
		String methodName = methodDecl.getName().getFullyQualifiedName();
		sASTNodeFinder.setSource(mSource);
		sASTNodeFinder.setFile(mFile);
		AbstractMethodDeclaration method = sASTNodeFinder.findMethod(compilation, methodDecl);
		if (method != null) {
			Node root = convertMethodDeclaration(methodName, compilation, methodDecl);
			// Node root = new Node(JavaEntityType.METHOD, methodName);
			// root.setEntity(new SourceCodeEntity(methodName, JavaEntityType.METHOD, //
			// new SourceRange(method.declarationSourceStart, method.declarationSourceEnd)));
			List<Comment> comments = new ArrayList<Comment>();
			sMethodBodyConverter.initialize(root, method, comments, compilation.getScanner());
			method.traverse(sMethodBodyConverter, (ClassScope) null);
			root.setMethodDeclaration(methodDecl);
			return root;
		}
		return null;
	}

	/**
	 * Convert method body.
	 * 
	 * @param methodDecl the method decl
	 * @param compilation the compilation
	 * @param bindingManagerList the binding manager list
	 * @return the node
	 */
	protected Node convertMethodBody(MethodDeclaration methodDecl, JavaCompilation compilation, //
			List<UTASTBindingManagerAbstract> bindingManagerList) {
		String methodName = methodDecl.getName().getFullyQualifiedName();
		AbstractMethodDeclaration method = sASTNodeFinder.findMethod(compilation, methodDecl);
		if (method != null) {
			Node root = convertMethodDeclaration(methodName, compilation, methodDecl, bindingManagerList);
			// Node root = new Node(JavaEntityType.METHOD, methodName);
			// root.setEntity(new SourceCodeEntity(methodName, JavaEntityType.METHOD, //
			// new SourceRange(method.declarationSourceStart, method.declarationSourceEnd)));
			List<Comment> comments = new ArrayList<Comment>();
			sMethodBodyConverter.initialize(root, method, comments, compilation.getScanner(), bindingManagerList);
			method.traverse(sMethodBodyConverter, (ClassScope) null);
			root.setMethodDeclaration(methodDecl);
			return root;
		}
		return null;
	}

	/**
	 * Convert method body.
	 * 
	 * @param methodDecl the method decl
	 * @param cu the cu
	 * @return the node
	 */
	protected Node convertMethodBody(MethodDeclaration methodDecl, CompilationUnit cu) {
		return null;
	}

	/**
	 * Convert method body.
	 * 
	 * @param methodName the method name
	 * @param compilation the compilation
	 * @return the node
	 */
	public Node convertMethodBody(String methodName, JavaCompilation compilation) {
		AbstractMethodDeclaration method = CompilationUtils.findMethod(compilation.getCompilationUnit(), methodName);
		Node root = new Node(JavaEntityType.METHOD, methodName);
		root.setEntity(new SourceCodeEntity(methodName, JavaEntityType.METHOD, new SourceRange(method.declarationSourceStart, method.declarationSourceEnd)));
		List<Comment> comments = CompilationUtils.extractComments(compilation);
		sMethodBodyConverter.initialize(root, method, comments, compilation.getScanner());
		method.traverse(sMethodBodyConverter, (ClassScope) null);
		return root;
	}

	/**
	 * Convert method declaration.
	 * 
	 * @param methodName the method name
	 * @param compilation the compilation
	 * @param methodDecl the method decl
	 * @param bindingManagerList the binding manager list
	 * @return the node
	 */
	public Node convertMethodDeclaration(String methodName, JavaCompilation compilation, //
			MethodDeclaration methodDecl, //
			List<UTASTBindingManagerAbstract> bindingManagerList) {
		AbstractMethodDeclaration method = sASTNodeFinder.findMethod(compilation, methodDecl);
		Node root = new Node(JavaEntityType.METHOD, methodName);
		root.setEntity(new SourceCodeEntity(methodName, JavaEntityType.METHOD, new SourceRange(method.declarationSourceStart, method.declarationSourceEnd)));
		sDeclarationConverter.initialize(root, compilation.getScanner(), bindingManagerList);
		method.traverse(sDeclarationConverter, (ClassScope) null);
		return root;
	}

	public Node convertMethodDeclaration(String methodName, JavaCompilation compilation, //
			MethodDeclaration methodDecl) {
		// AbstractMethodDeclaration method = CompilationUtils.findMethod(compilation.getCompilationUnit(), methodName);
		AbstractMethodDeclaration method = sASTNodeFinder.findMethod(compilation, methodDecl);
		Node root = new Node(JavaEntityType.METHOD, methodName);
		root.setEntity(new SourceCodeEntity(methodName, JavaEntityType.METHOD, new SourceRange(method.declarationSourceStart, method.declarationSourceEnd)));
		sDeclarationConverter.initialize(root, compilation.getScanner());
		method.traverse(sDeclarationConverter, (ClassScope) null);
		return root;
	}

	/**
	 * Buggy Code Kept for avoiding compilation errors
	 * 
	 * @param methodName the method name
	 * @param compilation the compilation
	 * @return the node
	 */
	@Deprecated
	public Node convertMethodDeclaration(String methodName, JavaCompilation compilation) {
		AbstractMethodDeclaration method = CompilationUtils.findMethod(compilation.getCompilationUnit(), methodName);
		Node root = new Node(JavaEntityType.METHOD, methodName);
		root.setEntity(new SourceCodeEntity(methodName, JavaEntityType.METHOD, new SourceRange(method.declarationSourceStart, method.declarationSourceEnd)));
		sDeclarationConverter.initialize(root, compilation.getScanner());
		method.traverse(sDeclarationConverter, (ClassScope) null);
		return root;
	}

	/**
	 * Convert field declaration.
	 * 
	 * @param fieldName the field name
	 * @param compilation the compilation
	 * @return the node
	 */
	public Node convertFieldDeclaration(String fieldName, JavaCompilation compilation) {
		FieldDeclaration field = CompilationUtils.findField(compilation.getCompilationUnit(), fieldName);
		Node root = new Node(JavaEntityType.FIELD, fieldName);
		root.setEntity(new SourceCodeEntity(fieldName, JavaEntityType.FIELD, new SourceRange(field.declarationSourceStart, field.declarationSourceEnd)));
		sDeclarationConverter.initialize(root, compilation.getScanner());
		field.traverse(sDeclarationConverter, (MethodScope) null);
		return root;
	}
}
