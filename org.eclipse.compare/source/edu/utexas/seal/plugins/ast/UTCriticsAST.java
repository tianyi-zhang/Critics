/*
 * @(#) UTCriticsAST.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.ast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;

import edu.utexas.seal.plugins.util.UTCriticsPairFileInfo;

/**
 * @author Myoungkyu Song
 * @date Oct 26, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTCriticsAST {

	private static ICompilationUnit	leftCopyICompilationUnit;
	private static ICompilationUnit	rightCopyICompilationUnit;
	private static IProject			leftCopyIProject;
	private static IProject			rightCopyIProject;

	public static ICompilationUnit getLeftCopyICompilationpUnit() {
		return leftCopyICompilationUnit;
	}

	public static IProject getLeftCopyIProject() {
		return leftCopyIProject;
	}

	public static ICompilationUnit getRightCopyICompilationUnit() {
		return rightCopyICompilationUnit;
	}

	public static IProject getRightCopyIProject() {
		return rightCopyIProject;
	}

	/**
	 * 
	 * @param project
	 * @param packageName
	 * @param className
	 * @param source
	 * @param projectName
	 * @param isLeft
	 * @return
	 */
	public static ICompilationUnit copy(String projectName, String packageName, String className, String source, boolean isLeft) {
		IProject project = null;
		IProject srcProject = null;
		ICompilationUnit unit = null;

		UTCriticsAST.reset(projectName);
		try {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			if (isLeft) {
				srcProject = root.getProject(projectName);
				leftCopyIProject = root.getProject("__" + projectName);
				project = leftCopyIProject;
			} else {
				srcProject = root.getProject(projectName);
				rightCopyIProject = root.getProject("__" + projectName);
				project = rightCopyIProject;
			}
			IJavaProject javaProject = createIProject(project);
			createFolder(project, javaProject, "bin", true);
			createSystemClassPath(javaProject);
			IFolder sourceFolder = createFolder(project, javaProject, "src", false);
			// createClassPath(javaProject, sourceFolder);
			// Copy source folder from original project to copy
			copySrcFolder(srcProject, project);
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
			setClassPath(javaProject, sourceFolder, isLeft);
			IPackageFragment pack = createPackage(javaProject, sourceFolder, packageName);
			if (isLeft) {
				leftCopyICompilationUnit = createCompilationUnit(source, pack, className);
				unit = leftCopyICompilationUnit;
			} else {
				rightCopyICompilationUnit = createCompilationUnit(source, pack, className);
				unit = rightCopyICompilationUnit;
			}
		} catch (CoreException e) {
			// silently ignore.
		}
		return unit;
	}

	/**
	 * 
	 * @param projectName
	 */
	public static void reset(String projectName) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject("__" + projectName);
		if (project.exists()) {
			try {
				project.delete(true, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * create a simple project of type org.eclipse.core.resources.IProject;
	 * we need a java project, we have to set the Java nature to the created project:
	 * 
	 * @param project
	 * @return
	 * @throws CoreException
	 */
	private static IJavaProject createIProject(IProject project) throws CoreException {
		project.create(null);
		project.open(null);
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] { JavaCore.NATURE_ID });
		project.setDescription(description, null);
		IJavaProject javaProject = JavaCore.create(project);
		return javaProject;
	}

	/**
	 * specify the location of the compiler (src and bin folder):
	 * 
	 * @param project
	 * @param javaProject
	 * @param dirName
	 * @return
	 * @throws CoreException
	 */
	private static IFolder createFolder(IProject project, IJavaProject javaProject, //
			String dirName, boolean isBin) throws CoreException {
		IFolder folder = project.getFolder(dirName);
		folder.create(true, true, null);
		if (isBin) {
			javaProject.setOutputLocation(folder.getFullPath(), null);
		}
		return folder;
	}

	/**
	 * define the class path entries. Class path entries define the roots of package fragments;
	 * might have to include the necessary plugin "org.eclipse.jdt.launching".
	 * 
	 * @param javaProject
	 * @throws JavaModelException
	 */
	private static void createSystemClassPath(IJavaProject javaProject) throws JavaModelException {
		List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
		IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();
		LibraryLocation[] locations = JavaRuntime.getLibraryLocations(vmInstall);
		for (LibraryLocation element : locations) {
			entries.add(JavaCore.newLibraryEntry(element.getSystemLibraryPath(), null, null));
		}
		javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), null); // add libs to project class path
	}

	/**
	 * created source folders should be added to the class entries of the project, otherwise compilation will fail.
	 * 
	 * @param javaProject
	 * @param sourceFolder
	 * @throws JavaModelException
	 */
	static void createClassPath(IJavaProject javaProject, IFolder sourceFolder) throws JavaModelException {
		IPackageFragmentRoot packageFragmentRoot = javaProject.getPackageFragmentRoot(sourceFolder);
		IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
		IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
		System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
		newEntries[oldEntries.length] = JavaCore.newSourceEntry(packageFragmentRoot.getPath());
		javaProject.setRawClasspath(newEntries, null);
		// IClasspathEntry[] entries = javaProject.getRawClasspath();
		for (int i = 0; i < newEntries.length; i++) {
			System.out.print(javaProject.encodeClasspathEntry(newEntries[i]));
		}
	}

	/**
	 * Replacement for createClassPath
	 * Besides, add referenced external jar file path in classpath
	 * 
	 * @param target
	 * @param sourceFolder
	 * @param isLeft
	 * @throws JavaModelException
	 */
	private static void setClassPath(IJavaProject target, IFolder sourceFolder, boolean isLeft) throws JavaModelException {
		IJavaProject jProject;
		if (isLeft) {
			IProject project = UTCriticsPairFileInfo.getLeftIProject();
			jProject = JavaCore.create(project);
		} else {
			IProject project = UTCriticsPairFileInfo.getRightIProject();
			jProject = JavaCore.create(project);
		}

		IClasspathEntry[] oldEntries = jProject.getRawClasspath();

		IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length];
		System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);

		IPackageFragmentRoot packageFragmentRoot = target.getPackageFragmentRoot(sourceFolder);
		for (int i = 0; i < newEntries.length; i++) {
			if (newEntries[i].getEntryKind() == IClasspathEntry.CPE_SOURCE) {
				newEntries[i] = JavaCore.newSourceEntry(packageFragmentRoot.getPath());
				// System.out.println(target.encodeClasspathEntry(newEntries[i]));
			}
		}
		target.setRawClasspath(newEntries, null);
	}

	/**
	 * 
	 * @param javaProject
	 * @param sourceFolder
	 * @param packageName
	 * @return
	 * @throws JavaModelException
	 */
	private static IPackageFragment createPackage(IJavaProject javaProject, //
			IFolder sourceFolder, String packageName) throws JavaModelException {
		return javaProject.getPackageFragmentRoot(sourceFolder). //
				createPackageFragment(packageName, false, null);
	}

	/**
	 * 
	 * @param source
	 * @param pack
	 * @param className
	 * @return
	 * @throws JavaModelException
	 */
	private static ICompilationUnit createCompilationUnit(String source, //
			IPackageFragment pack, String className) throws JavaModelException {
		StringBuffer buffer = new StringBuffer();
		buffer.append(source);
		return pack.createCompilationUnit(className, buffer.toString(), true, null);
	}

	public static void copySrcFolder(IProject src, IProject dst) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath path_root = workspace.getRoot().getLocation();
		IPath path_src = src.getFullPath();
		String spath_src = path_root.toOSString() + path_src.toOSString() + "/src";
		File srcFile = new File(spath_src);
		IPath path_dst = dst.getFullPath();
		String spath_dst = path_root.toOSString() + path_dst.toOSString();
		File dstFile = new File(spath_dst);
		try {
			FileUtils.copyDirectoryToDirectory(srcFile, dstFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
