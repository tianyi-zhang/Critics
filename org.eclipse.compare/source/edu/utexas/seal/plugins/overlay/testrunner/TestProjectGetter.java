/*
 * @(#) TestProjectGetter.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.overlay.testrunner;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import edu.utexas.seal.plugins.overlay.model.CriticsCBTreeNode;
import edu.utexas.seal.plugins.overlay.model.CriticsCBTreeElemType;

/**
 * @author Myoungkyu Song
 * @date Dec 27, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class TestProjectGetter {
	static public CriticsCBTreeNode	root	= new CriticsCBTreeNode("root", CriticsCBTreeElemType.NONE);

	/**
	 * @param index
	 */
	public static void execute(int index) {
		root.getChildren().clear();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject[] projects = root.getProjects();
		for (int i = 0; i < projects.length; i++) {
			if (index == i) {
				System.out.println("Working in project " + projects[i].getName());
				try {
					printProjectInfo((IProject) projects[i]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void printProjectInfo(IProject project) throws Exception {
		System.out.println("Working in project " + project.getName());
		if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
			IJavaProject javaProject = JavaCore.create(project);
			printPackageInfos(javaProject);
		}
	}

	private static void printPackageInfos(IJavaProject javaProject) throws JavaModelException {
		IPackageFragment[] packages = javaProject.getPackageFragments();
		for (IPackageFragment mypackage : packages) {
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
					CriticsCBTreeNode iNode = new CriticsCBTreeNode(unit.getElementName(), CriticsCBTreeElemType.FILE);
					iNode.setPath(unit.getPath().toFile().getAbsoluteFile());
					root.addChild(iNode);

					IType[] allTypes = unit.getAllTypes();
					for (IType type : allTypes) {
						IMethod[] methods = type.getMethods();
						for (IMethod method : methods) {
							CriticsCBTreeNode jNode = new CriticsCBTreeNode(method.getElementName(), CriticsCBTreeElemType.METHOD);
							jNode.setPath(unit.getPath().toFile().getAbsoluteFile());
							jNode.setOffSet(method.getSourceRange().getOffset());
							jNode.setLength(method.getSourceRange().getLength());
							jNode.setSource(method.getSource());
							iNode.addChild(jNode);

							jNode.addChild(new CriticsCBTreeNode("Block1", CriticsCBTreeElemType.BLOCK));
						}
					}
				}
			}
		}
	}

	/*
	CriticsCBTreeNode buildTestData() {
		CriticsCBTreeNode root = new CriticsCBTreeNode("root");
		addChildren(root, 3);
		return root;
	}

	void addChildren(CriticsCBTreeNode parent, int max) {
		for (int i = 0; i < max; i++) {
			CriticsCBTreeNode iNode = new CriticsCBTreeNode("Class" + i);
			parent.addChild(iNode);

			for (int j = 0; j < max; j++) {
				CriticsCBTreeNode jNode = new CriticsCBTreeNode("method" + j);
				iNode.addChild(jNode);

				for (int k = 0; k < max; k++) {
					CriticsCBTreeNode kNode = new CriticsCBTreeNode("Block" + k);
					jNode.addChild(kNode);
				}
			}
		}
	}
	*/
}
