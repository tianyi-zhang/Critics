/*
 * @(#) UTCriticsPreprocessor.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugin.preprocess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IStartup;

import edu.utexas.seal.plugin.Activator;
import edu.utexas.seal.plugin.preprocess.save.UTCriticsSaveParticipant;
import edu.utexas.seal.plugin.preprocess.update.UTCriticsChangeReporter;
import ut.seal.plugins.utils.UTTime;

public class UTCriticsPreprocessor implements IStartup {
	public static HashMap<String, UTCriticsFileFilter>	preprocessor	= new HashMap<String, UTCriticsFileFilter>();
	public static String								filePath		= null;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	@Override
	public void earlyStartup() {
		// construct the local path for serialized map
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		String rootPath = root.getLocation().toOSString();
		String s = System.getProperty("file.separator");
		filePath = rootPath + s + ".metadata" + s + "HashMap.txt";

		File file = new File(filePath);
		if (file.exists()) {
			// if exists, load from local file system
			long begin = (new Date()).getTime();
			loadFilter();
			long timeslot = (new Date()).getTime() - begin;
			System.out.println("Time for loading map :");
			UTTime.timeStamp(timeslot);
		} else {
			// if not, create this map
			IProject[] projects = root.getProjects();

			for (IProject project : projects) {
				String prjName = project.getName();
				IJavaProject javaProject = JavaCore.create(project);
				UTCriticsFileFilter filter = new UTCriticsFileFilter();
				try {
					IPackageFragment[] packages = javaProject.getPackageFragments();
//					filter.process(packages);
				} catch (Exception e) {
					e.printStackTrace();
				}

				preprocessor.put(prjName, filter);
			}
		}

		// add resource change listener
		IResourceChangeListener listener = new UTCriticsChangeReporter();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_CHANGE);

		// register as a save participant
		UTCriticsSaveParticipant saveParticipant = new UTCriticsSaveParticipant();
		try {
			ResourcesPlugin.getWorkspace().addSaveParticipant(Activator.PLUGIN_ID, saveParticipant);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load filter.
	 */
	@SuppressWarnings("unchecked")
	public void loadFilter() {
		FileInputStream fis;
		try {
			fis = new FileInputStream(filePath);
			ObjectInputStream ois = new ObjectInputStream(fis);
			preprocessor = (HashMap<String, UTCriticsFileFilter>) ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
}
