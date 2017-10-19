/*
 * @(#) UTCriticsProjectDiffs.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import ut.seal.plugins.utils.change.UTChangeDistillerFile;
import edu.utexas.seal.plugins.database.UTChangeDistillerDB;
import edu.utexas.seal.plugins.database.UTCriticsDBServer;

public class UTCriticsProjectDiffs {
	private ISelection					fSelection;
	private ArrayList<String>			allNames	= new ArrayList<String>();
	private ArrayList<IResource>		allOldFiles	= new ArrayList<IResource>();
	private ArrayList<IResource>		allNewFiles	= new ArrayList<IResource>();
	private ArrayList<IPath>			allPaths	= new ArrayList<IPath>();
	private Map<IResource, IResource>	oMatchNew	= new HashMap<IResource, IResource>(allOldFiles.size());
	private Map<IResource, IResource>	nMatchOld	= new HashMap<IResource, IResource>(allOldFiles.size());
	private UTCriticsDBServer			server;

	public UTCriticsProjectDiffs(ISelection selection) {
		fSelection = selection;
		server = new UTCriticsDBServer();
		server.start();
	}

	public void findAll() {
		IStructuredSelection structured = (IStructuredSelection) fSelection;
		Iterator<?> itr = structured.iterator();
		while (itr.hasNext()) {
			IProject project = (IProject) itr.next();
			allNames.add(project.getName());
			allPaths.add(project.getLocation());
		}

		// UTCritics only supports comparing two projects now.
		if (allPaths.size() != 2) {
			System.out.println("------------------------------------------");
			System.out.println("[USG] PLEASE CHOOSE TWO DIFFERENT PROJECTS.");
			System.out.println("==========================================");
			return;
		}

		// Create Tables
		String newPName = allNames.get(0);
		String oldPName = allNames.get(1);
		server.connect();
		server.createTables(newPName, oldPName);
		server.disconnect();

		// Currently UTCritics assume the first selected project is the old version, and the second new one.
		// Currently UTCritics only supports differencing java files
		IWorkspaceRoot myWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		findFiles(allNewFiles, allPaths.get(0), myWorkspaceRoot, "java");
		findFiles(allOldFiles, allPaths.get(1), myWorkspaceRoot, "java");

		// Get pairs of matched files based on their names
		// Use two maps, one from old files to new one, and the other reversely
		// It helps locate matched file from any side
		for (int i = 0; i < allOldFiles.size(); i++) {
			Boolean flag = false;
			IResource oldIR = allOldFiles.get(i);

			for (int j = 0; j < allNewFiles.size(); j++) {
				IResource newIR = allNewFiles.get(j);
				if (oldIR.getName() == newIR.getName()) {
					oMatchNew.put(oldIR, newIR);
					flag = true;
					break;
				}
			}

			if (flag == false) {
				oMatchNew.put(oldIR, null);
			}
		}

		for (int i = 0; i < allNewFiles.size(); i++) {
			IResource newIR = allNewFiles.get(i);
			for (int j = 0; j < allOldFiles.size(); j++) {
				IResource oldIR = allOldFiles.get(j);
				if (oldIR.getName() == newIR.getName()) {
					nMatchOld.put(newIR, oldIR);
					break;
				}
			}
		}

		// Currently UTCritics only cares about matched file
		// And treat unmatched files in old project as deletion ones
		// And treat unmatched files in new project as addition ones
		// Print file edits
		System.out.println("------------------------------------------");
		System.out.println("[DBG] REMOVED FILES");
		for (int i = 0; i < allOldFiles.size(); i++) {
			IResource oldIR = allOldFiles.get(i);
			IResource match = oMatchNew.get(oldIR);
			if (match == null) {
				System.out.println(oldIR.getName());
			}
		}
		System.out.println("==========================================");

		System.out.println("------------------------------------------");
		System.out.println("[DBG] ADDED FILES");
		for (int i = 0; i < allNewFiles.size(); i++) {
			IResource newIR = allNewFiles.get(i);
			IResource match = nMatchOld.get(newIR);
			if (match == null) {
				System.out.println(newIR.getName());
			}
		}
		System.out.println("==========================================");

		System.out.println("------------------------------------------");
		System.out.println("[DBG] CHANGED FILES");
		for (int i = 0; i < allOldFiles.size(); i++) {
			IResource oldIR = allOldFiles.get(i);
			IResource match = oMatchNew.get(oldIR);
			if (match != null) {
				System.out.println("******************************************");
				System.out.println("[DBG] Comparing Difference between " + oldIR.getName() + " and " + match.getName() + ".");
				findDiffs(match, oldIR);
				System.out.println("******************************************");
			}
		}
		System.out.println("==========================================");
	}

	private static void findFiles(ArrayList<IResource> allFiles, IPath path, IWorkspaceRoot myWorkspaceRoot, String Type) {
		IContainer container = myWorkspaceRoot.getContainerForLocation(path);

		try {
			IResource[] iResources;
			iResources = container.members();
			for (IResource iR : iResources) {
				// for java files
				if (Type.equalsIgnoreCase(iR.getFileExtension()))
					allFiles.add(iR);
				if (iR.getType() == IResource.FOLDER) {
					IPath tempPath = iR.getLocation();
					findFiles(allFiles, tempPath, myWorkspaceRoot, Type);
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private void findDiffs(IResource leftResource, IResource rightResource) {
		IPath leftPath = leftResource.getLocation();
		IPath rightPath = rightResource.getLocation();
		String leftPathS = leftPath.toString();
		String rightPathS = rightPath.toString();

		File leftFile = new File(leftPathS);
		File rightFile = new File(rightPathS);

		UTChangeDistillerFile differencer = new UTChangeDistillerFile();
		differencer.diff(leftFile, rightFile);

		// Connect to server
		server.connect();
		UTChangeDistillerDB newDifferencer = new UTChangeDistillerDB(server);
		newDifferencer.diff(leftFile, rightFile);
		server.disconnect();

		// write diffs and contexts to file
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		String path = workspace.getRoot().getLocation().toString();
		String leftName = leftResource.getName();
		String rightName = rightResource.getName();
		// differencer.writeToFiles(path, leftName, rightName);
	}

}
