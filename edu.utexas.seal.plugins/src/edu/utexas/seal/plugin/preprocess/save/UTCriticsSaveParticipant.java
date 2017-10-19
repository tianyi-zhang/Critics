/*
 * @(#) UTCriticsSaveParticipant.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugin.preprocess.save;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import ut.seal.plugins.utils.UTTime;
import edu.utexas.seal.plugin.preprocess.UTCriticsFileFilter;
import edu.utexas.seal.plugin.preprocess.UTCriticsPreprocessor;

public class UTCriticsSaveParticipant implements ISaveParticipant {

	@Override
	public void doneSaving(ISaveContext context) {
	}

	@Override
	public void prepareToSave(ISaveContext context) throws CoreException {
	}

	@Override
	public void rollback(ISaveContext context) {
	}

	@Override
	public void saving(ISaveContext context) throws CoreException {
		switch (context.getKind()) {
		case ISaveContext.FULL_SAVE:
			HashMap<String, UTCriticsFileFilter> preprocessor = UTCriticsPreprocessor.preprocessor;
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			IProject[] projects = root.getProjects();
			List<String> prjNames = new ArrayList<String>();
			for (IProject prj : projects) {
				prjNames.add(prj.getName());
			}

			// get deleted project
			List<String> deletedPrj = new ArrayList<String>();
			for (String prjName : preprocessor.keySet()) {
				if (!prjNames.contains(prjName)) {
					deletedPrj.add(prjName);
				}
			}

			// delete UTCriticsFilter objects for those deleted projects
			for (String prjName : deletedPrj) {
				preprocessor.remove(prjName);
			}

			// save it
			long begin = (new Date()).getTime();
			saveFilter(preprocessor);
			long timeslot = (new Date()).getTime() - begin;
			System.out.println("Time for saving map :");
			UTTime.timeStamp(timeslot);
			break;
		case ISaveContext.PROJECT_SAVE:
			// do nothing here
			break;
		case ISaveContext.SNAPSHOT:
			// do nothing here
			break;
		}
	}

	/**
	 * Save filter.
	 */
	public void saveFilter(HashMap<String, UTCriticsFileFilter> preprocessor) {
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			String rootPath = root.getLocation().toOSString();
			String s = System.getProperty("file.separator");
			String filePath = rootPath + s + ".metadata" + s + "HashMap.txt";
			FileOutputStream fos = new FileOutputStream(filePath);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(preprocessor);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
