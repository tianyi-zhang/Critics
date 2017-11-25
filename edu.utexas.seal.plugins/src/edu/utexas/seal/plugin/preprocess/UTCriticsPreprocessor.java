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
					filter.process(packages);
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
