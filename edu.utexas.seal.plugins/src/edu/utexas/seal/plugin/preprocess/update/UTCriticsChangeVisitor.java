/*
 * @(#) UTCriticsChangeVisitor.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugin.preprocess.update;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import edu.utexas.seal.plugin.preprocess.UTCriticsFileFilter;
import edu.utexas.seal.plugin.preprocess.UTCriticsPreprocessor;

public class UTCriticsChangeVisitor implements IResourceDeltaVisitor {

	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource res = delta.getResource();
		String path = res.getFullPath().toString();

		if (!path.matches(".*\\.java$")) {
			// Project import, delete, package rename and file move can also be captured by change to java files.
			// To avoid duplication and redundant analysis, we only handle java changes here.
			return true;
		}

		switch (delta.getKind()) {
		case IResourceDelta.ADDED:
			// java file has been added.(Project Import, New java class)
			// System.out.print("Resource ");
			// System.out.print(res.getFullPath());
			// System.out.println(" was added.");
			update(res);
			break;
		case IResourceDelta.REMOVED:
			// java file has been removed.
			// System.out.print("Resource ");
			// System.out.print(res.getFullPath());
			// System.out.println(" was removed.");
			remove(res);
			break;
		case IResourceDelta.CHANGED:
			int flags = delta.getFlags();
			if ((flags & IResourceDelta.CONTENT) != 0) {
				// content of java file has been changed.
				// System.out.println("--> Content Change");
				update(res);
			}
			if ((flags & IResourceDelta.REPLACED) != 0) {
				// java file has been replaced.
				// System.out.println("--> Content Replaced");
				update(res);
			}
			if ((flags & IResourceDelta.MARKERS) != 0) {
				// we don't care about markers change, do nothing
			}
			break;
		}
		return true; // visit the children
	}

	private void remove(IResource res) {
		IProject prj = res.getProject();
		UTCriticsFileFilter filter = UTCriticsPreprocessor.preprocessor.get(prj.getName());
		if (filter == null) {
			// already removed
			return;
		} else {
			// traverse the filter
			HashMap<String, Set<String>> map = filter.getFilter();
			for (Set<String> files : map.values()) {
				for (Iterator<String> i = files.iterator(); i.hasNext();) {
					String name = i.next();
					if (name.equals(res.getFullPath().toString())) {
						i.remove();
					}
				}
			}
		}
	}

	private void update(IResource res) {
		IProject prj = res.getProject();
		UTCriticsFileFilter filter = UTCriticsPreprocessor.preprocessor.get(prj.getName());
		if (filter == null) {
			// this project is newly imported or created
			IJavaProject javaProject = JavaCore.create(prj);
			filter = new UTCriticsFileFilter();
			try {
				filter.process(javaProject.getPackageFragments());
			} catch (Exception e) {
				e.printStackTrace();
			}
			UTCriticsPreprocessor.preprocessor.put(prj.getName(), filter);
		} else {
			// java class file is newly added or created
			// 1. get compilation unit for this java file
			String filePath = res.getFullPath().toString();
			ICompilationUnit icu = getICU(prj, filePath);
			// 2. process
			if (icu == null) {
				// do nothing
				System.out.print(filePath + "   Not found");
			} else {
				filter.process(icu);
			}
		}
	}

	private ICompilationUnit getICU(IProject prj, String target) {
		IJavaProject javaProject = JavaCore.create(prj);
		IPackageFragment[] pkgs;

		try {
			pkgs = javaProject.getPackageFragments();
			for (IPackageFragment pkg : pkgs) {
				if (pkg.getKind() == IPackageFragmentRoot.K_SOURCE) {
					for (ICompilationUnit icu : pkg.getCompilationUnits()) {
						String path = icu.getResource().getFullPath().toString();
						if (path.equals(target)) {
							return icu;
						}
					}
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}
}
