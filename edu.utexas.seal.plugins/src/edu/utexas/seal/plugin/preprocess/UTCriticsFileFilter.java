/*
 * @(#) UTCriticsFileFilter.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugin.preprocess;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import ut.seal.plugins.utils.ast.UTASTParser;
import edu.utexas.seal.plugins.util.visitor.UTGeneralVisitor;
import edu.utexas.seal.plugins.util.visitor.UTIGeneralVisitor;

/**
 * The Class UTCriticsFileFilter.
 */
public class UTCriticsFileFilter implements Serializable {

	private static final long				serialVersionUID	= 101L;

	private HashMap<String, Set<String>>	filter				= null;

	public UTCriticsFileFilter() {
		filter = new HashMap<String, Set<String>>();
	}

	public UTCriticsFileFilter(HashMap<String, Set<String>> filter) {
		this.filter = filter;
	}

	/**
	 * Process.
	 * 
	 * @param iPackageFragments the i package fragments
	 * @throws Exception the exception
	 */
	public void process(IPackageFragment[] iPackageFragments) throws Exception {
		UTIGeneralVisitor<VariableDeclaration> mVisitor = new UTGeneralVisitor<VariableDeclaration>() {
			public boolean visit(VariableDeclarationFragment node) {
				results.add(node);
				return true;
			}

			public boolean visit(SingleVariableDeclaration node) {
				results.add(node);
				return true;
			}
		};
		for (int i = 0; i < iPackageFragments.length; i++) {
			IPackageFragment pkgElem = iPackageFragments[i];
			if (pkgElem.getKind() == IPackageFragmentRoot.K_SOURCE) {
				for (ICompilationUnit icu : pkgElem.getCompilationUnits()) {
					String filePath = getFile(icu);
					System.out.println("[DBG] preprocess: " + filePath);
					UTASTParser astParser = new UTASTParser();
					List<VariableDeclaration> vars = mVisitor.getResults();
					vars.clear();
					CompilationUnit parser = astParser.parse(icu);
					parser.accept(mVisitor);
					for (VariableDeclaration var : vars) {
						String typeName = "";
						IVariableBinding binding = var.resolveBinding();
						if(binding != null){
							typeName = binding.getType().getName();
						}
						String query = var.getName() + ":" + typeName;
						if (filter.containsKey(query)) {
							filter.get(query).add(filePath);
						} else {
							HashSet<String> set = new HashSet<String>();
							set.add(filePath);
							filter.put(query, set);
						}
					}
				}
			}
		}
	}

	public void process(ICompilationUnit unit) {
		UTIGeneralVisitor<VariableDeclaration> mVisitor = new UTGeneralVisitor<VariableDeclaration>() {
			public boolean visit(VariableDeclarationFragment node) {
				results.add(node);
				return true;
			}

			public boolean visit(SingleVariableDeclaration node) {
				results.add(node);
				return true;
			}
		};

		String filePath = getFile(unit);
		System.out.println("[DBG] preprocess: " + filePath);
		UTASTParser astParser = new UTASTParser();
		List<VariableDeclaration> vars = mVisitor.getResults();
		vars.clear();
		CompilationUnit parser = astParser.parse(unit);
		parser.accept(mVisitor);
		for (VariableDeclaration var : vars) {
			String typeName = "";
			IVariableBinding binding = var.resolveBinding();
			if(binding != null){
				typeName = binding.getType().getName();
			}
			String query = var.getName() + ":" + typeName;
			if (filter.containsKey(query)) {
				filter.get(query).add(filePath);
			} else {
				HashSet<String> set = new HashSet<String>();
				set.add(filePath);
				filter.put(query, set);
			}
		}
	}

	/**
	 * Gets the file.
	 * 
	 * @param icu the icu
	 * @return the file
	 */
	private String getFile(ICompilationUnit icu) {
		File file = icu.getPath().toFile();
		String path = file.getAbsolutePath();
		return path;
	}

	/**
	 * Gets the filter.
	 * 
	 * @return the filter
	 */
	public HashMap<String, Set<String>> getFilter() {
		return filter;
	}
}
