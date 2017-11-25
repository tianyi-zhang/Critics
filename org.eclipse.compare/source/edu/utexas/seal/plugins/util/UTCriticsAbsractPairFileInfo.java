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
package edu.utexas.seal.plugins.util;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

/**
 * @author Myoungkyu Song
 * @date Oct 26, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public abstract class UTCriticsAbsractPairFileInfo {
	protected static File				leftFile;
	protected static File				rightFile;
	protected static String				leftFilePath;
	protected static String				rightFilePath;
	protected static String				leftProjectName;
	protected static String				rightProjectName;
	protected static IProject			leftIProject;
	protected static IProject			rightIProject;
	protected static IPackageFragment	leftIPackageFragment;
	protected static IPackageFragment	rightIPackageFragment;
	protected static ICompilationUnit	leftICompilationUnit;
	protected static ICompilationUnit	rightICompilationUnit;
	protected static IPackageFragment[]	leftIPackages;
	protected static IPackageFragment[]	rightIPackages;
 
	private static final String			S	= System.getProperty("file.separator");

	public static File getLeftFile() {
		return leftFile;
	}

	public static String getLeftFilePath() {
		return leftFilePath;
	}

	public static void setLeftFilePath(String l) {
		IPath path = Platform.getLocation();
		leftFile = new File(path.toFile() + S + l);
		leftProjectName = l.substring(0, l.indexOf(S));
		setLeftIProject(leftProjectName);
	}

	public static void setRightFilePath(String r) {
		IPath path = Platform.getLocation();
		rightFile = new File(path.toFile() + S + r);
		rightProjectName = r.substring(0, r.indexOf(S));
		setRightIProject(rightProjectName);
	}

	/**
	 * 
	 * @param lPrjName
	 */
	private static void setLeftIProject(String lPrjName) {
		try {
			setIProjectInfo(lPrjName, leftFilePath, true);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param lPrjName
	 */
	private static void setRightIProject(String lPrjName) {
		try {
			setIProjectInfo(lPrjName, rightFilePath, false);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param prjName
	 * @param filePath
	 * @throws JavaModelException
	 */
	private static void setIProjectInfo(String prjName, String filePath, boolean isLeft) throws JavaModelException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject[] projects = root.getProjects();
		IProject project = null;
		IPackageFragment packageFragment = null;
		ICompilationUnit compilationUnit = null;

		for (int i = 0; i < projects.length; i++) {
			project = projects[i];
			if (prjName.equals(project.getName())) {
				if (isLeft) {
					leftIProject = project;
				} else {
					rightIProject = project;
				}
				break;
			}
		}
		IJavaProject javaProject = JavaCore.create(project);
		IPackageFragment[] packages = javaProject.getPackageFragments();
		for (int i = 0; i < packages.length; i++) {
			packageFragment = packages[i];
			if (packageFragment.getKind() == IPackageFragmentRoot.K_SOURCE) {
				String itrPkgName = packageFragment.getElementName();
				if (itrPkgName.isEmpty())
					continue;
				String pkgName = filePath.replace(S, ".").replace(prjName, "").replace(".java", "");
				pkgName = pkgName.substring(0, pkgName.lastIndexOf('.'));
				if (pkgName.endsWith(itrPkgName)) {
					if (isLeft) {
						leftIPackageFragment = packageFragment;
						leftIPackages = packages;
					} else {
						rightIPackageFragment = packageFragment;
						rightIPackages = packages;
					}
					break;
				}
			}
		}
		if (packageFragment != null) {
			ICompilationUnit[] compilationUnits = packageFragment.getCompilationUnits();
			for (int i = 0; i < compilationUnits.length; i++) {
				compilationUnit = compilationUnits[i];
				String fileName = filePath.replace(".java", "");
				fileName = fileName.substring(fileName.lastIndexOf(S) + 1) + ".java";
				if (fileName.equals(compilationUnit.getElementName())) {
					if (isLeft) {
						leftICompilationUnit = compilationUnit;
					} else {
						rightICompilationUnit = compilationUnit;
					}
					break;
				}
			}
		}
	}

	public static boolean validProject(String name) {
		Object lProject = UTCriticsPairFileInfo.getLeftProjectName();
		Object rProject = UTCriticsPairFileInfo.getRightProjectName();
		if (name.equals(lProject) || name.equals(rProject))
			return true;
		return false;
	}
}
