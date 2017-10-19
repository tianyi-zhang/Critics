/*******************************************************************************
 * Copyright (c) 2004, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     James Blackburn (Broadcom Corp.) - ongoing development
 *******************************************************************************/
package org.eclipse.core.internal.resources;

import java.util.*;
import org.eclipse.core.internal.utils.Messages;
import org.eclipse.core.internal.utils.Policy;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.Bundle;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Manages user-defined encodings as preferences in the project content area.
 * 
 * @since 3.0
 */
public class CharsetManager implements IManager {
	/**
	 * This job implementation is used to allow the resource change listener
	 * to schedule operations that need to modify the workspace. 
	 */
	private class CharsetManagerJob extends Job {
		private static final int CHARSET_UPDATE_DELAY = 500;
		private List<Map.Entry<IProject, Boolean>> asyncChanges = new ArrayList<Map.Entry<IProject, Boolean>>();

		public CharsetManagerJob() {
			super(Messages.resources_charsetUpdating);
			setSystem(true);
			setPriority(Job.INTERACTIVE);
		}

		public void addChanges(Map<IProject, Boolean> newChanges) {
			if (newChanges.isEmpty())
				return;
			synchronized (asyncChanges) {
				asyncChanges.addAll(newChanges.entrySet());
				asyncChanges.notify();
			}
			schedule(CHARSET_UPDATE_DELAY);
		}

		public Map.Entry<IProject, Boolean> getNextChange() {
			synchronized (asyncChanges) {
				return asyncChanges.isEmpty() ? null : asyncChanges.remove(asyncChanges.size() - 1);
			}
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.internal.jobs.InternalJob#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		protected IStatus run(IProgressMonitor monitor) {
			MultiStatus result = new MultiStatus(ResourcesPlugin.PI_RESOURCES, IResourceStatus.FAILED_SETTING_CHARSET, Messages.resources_updatingEncoding, null);
			monitor = Policy.monitorFor(monitor);
			try {
				monitor.beginTask(Messages.resources_charsetUpdating, Policy.totalWork);
				final ISchedulingRule rule = workspace.getRuleFactory().modifyRule(workspace.getRoot());
				try {
					workspace.prepareOperation(rule, monitor);
					workspace.beginOperation(true);
					Map.Entry<IProject, Boolean> next;
					while ((next = getNextChange()) != null) {
						//just exit if the system is shutting down or has been shut down
						//it is too late to change the workspace at this point anyway
						if (systemBundle.getState() != Bundle.ACTIVE)
							return Status.OK_STATUS;
						IProject project = next.getKey();
						try {
							if (project.isAccessible()) {
								boolean shouldDisableCharsetDeltaJob = next.getValue().booleanValue();
								// flush preferences for non-derived resources
								flushPreferences(getPreferences(project, false, false, true), shouldDisableCharsetDeltaJob);
								// flush preferences for derived resources
								flushPreferences(getPreferences(project, false, true, true), shouldDisableCharsetDeltaJob);
							}
						} catch (BackingStoreException e) {
							// we got an error saving					
							String detailMessage = Messages.resources_savingEncoding;
							result.add(new ResourceStatus(IResourceStatus.FAILED_SETTING_CHARSET, project.getFullPath(), detailMessage, e));
						}
					}
					monitor.worked(Policy.opWork);
				} catch (OperationCanceledException e) {
					workspace.getWorkManager().operationCanceled();
					throw e;
				} finally {
					workspace.endOperation(rule, true, Policy.subMonitorFor(monitor, Policy.endOpWork));
				}
			} catch (CoreException ce) {
				return ce.getStatus();
			} finally {
				monitor.done();
			}
			return result;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.jobs.Job#shouldRun()
		 */
		public boolean shouldRun() {
			synchronized (asyncChanges) {
				return !asyncChanges.isEmpty();
			}
		}
	}

	private class ResourceChangeListener implements IResourceChangeListener {
		public ResourceChangeListener() {
		}

		private boolean moveSettingsIfDerivedChanged(IResourceDelta parent, IProject currentProject, Preferences projectPrefs, String[] affectedResources) {
			boolean resourceChanges = false;

			if ((parent.getFlags() & IResourceDelta.DERIVED_CHANGED) != 0) {
				// if derived changed, move encoding to correct preferences
				IPath parentPath = parent.getResource().getProjectRelativePath();
				for (int i = 0; i < affectedResources.length; i++) {
					IPath affectedPath = new Path(affectedResources[i]);
					// if parentPath is an ancestor of affectedPath
					if (parentPath.isPrefixOf(affectedPath)) {
						IResource member = currentProject.findMember(affectedPath);
						if (member != null) {
							Preferences targetPrefs = getPreferences(currentProject, true, member.isDerived(IResource.CHECK_ANCESTORS));
							// if new preferences are different than current
							if (!projectPrefs.absolutePath().equals(targetPrefs.absolutePath())) {
								// remove encoding from old preferences and save in correct preferences
								String currentValue = projectPrefs.get(affectedResources[i], null);
								projectPrefs.remove(affectedResources[i]);
								targetPrefs.put(affectedResources[i], currentValue);
								resourceChanges = true;
							}
						}
					}
				}
			}

			IResourceDelta[] children = parent.getAffectedChildren();
			for (int i = 0; i < children.length; i++) {
				resourceChanges = moveSettingsIfDerivedChanged(children[i], currentProject, projectPrefs, affectedResources) || resourceChanges;
			}
			return resourceChanges;
		}

		private void processEntryChanges(IResourceDelta projectDelta, Map<IProject, Boolean> projectsToSave) {
			// check each resource with user-set encoding to see if it has
			// been moved/deleted or if derived state has been changed
			IProject currentProject = (IProject) projectDelta.getResource();
			Preferences projectRegularPrefs = getPreferences(currentProject, false, false, true);
			Preferences projectDerivedPrefs = getPreferences(currentProject, false, true, true);
			Map<Boolean, String[]> affectedResourcesMap = new HashMap<Boolean, String[]>();
			try {
				// no regular preferences for this project
				if (projectRegularPrefs == null)
					affectedResourcesMap.put(Boolean.FALSE, new String[0]);
				else
					affectedResourcesMap.put(Boolean.FALSE, projectRegularPrefs.keys());
				// no derived preferences for this project
				if (projectDerivedPrefs == null)
					affectedResourcesMap.put(Boolean.TRUE, new String[0]);
				else
					affectedResourcesMap.put(Boolean.TRUE, projectDerivedPrefs.keys());
			} catch (BackingStoreException e) {
				// problems with the project scope... we will miss the changes (but will log)
				String message = Messages.resources_readingEncoding;
				Policy.log(new ResourceStatus(IResourceStatus.FAILED_GETTING_CHARSET, currentProject.getFullPath(), message, e));
				return;
			}
			for (Iterator<Boolean> it = affectedResourcesMap.keySet().iterator(); it.hasNext();) {
				Boolean isDerived = it.next();
				String[] affectedResources = affectedResourcesMap.get(isDerived);
				Preferences projectPrefs = isDerived.booleanValue() ? projectDerivedPrefs : projectRegularPrefs;
				for (int i = 0; i < affectedResources.length; i++) {
					IResourceDelta memberDelta = projectDelta.findMember(new Path(affectedResources[i]));
					// no changes for the given resource
					if (memberDelta == null)
						continue;
					if (memberDelta.getKind() == IResourceDelta.REMOVED) {
						boolean shouldDisableCharsetDeltaJobForCurrentProject = false;
						// remove the setting for the original location - save its value though
						String currentValue = projectPrefs.get(affectedResources[i], null);
						projectPrefs.remove(affectedResources[i]);
						if ((memberDelta.getFlags() & IResourceDelta.MOVED_TO) != 0) {
							IPath movedToPath = memberDelta.getMovedToPath();
							IResource resource = workspace.getRoot().findMember(movedToPath);
							if (resource != null) {
								Preferences encodingSettings = getPreferences(resource.getProject(), true, resource.isDerived(IResource.CHECK_ANCESTORS));
								if (currentValue == null || currentValue.trim().length() == 0)
									encodingSettings.remove(getKeyFor(movedToPath));
								else
									encodingSettings.put(getKeyFor(movedToPath), currentValue);
								IProject targetProject = workspace.getRoot().getProject(movedToPath.segment(0));
								if (targetProject.equals(currentProject))
									// if the file was moved inside the same project disable charset listener
									shouldDisableCharsetDeltaJobForCurrentProject = true;
								else
									projectsToSave.put(targetProject, Boolean.FALSE);
							}
						}
						projectsToSave.put(currentProject, Boolean.valueOf(shouldDisableCharsetDeltaJobForCurrentProject));
					}
				}
				if (moveSettingsIfDerivedChanged(projectDelta, currentProject, projectPrefs, affectedResources)) {
					// if settings were moved between preferences files disable charset listener so we don't react to changes made by ourselves
					projectsToSave.put(currentProject, Boolean.TRUE);
				}
			}
		}

		/**
		 * For any change to the encoding file or any resource with encoding
		 * set, just discard the cache for the corresponding project.
		 */
		public void resourceChanged(IResourceChangeEvent event) {
			IResourceDelta delta = event.getDelta();
			if (delta == null)
				return;
			IResourceDelta[] projectDeltas = delta.getAffectedChildren();
			// process each project in the delta
			Map<IProject, Boolean> projectsToSave = new HashMap<IProject, Boolean>();
			for (int i = 0; i < projectDeltas.length; i++)
				//nothing to do if a project has been added/removed/moved				
				if (projectDeltas[i].getKind() == IResourceDelta.CHANGED && (projectDeltas[i].getFlags() & IResourceDelta.OPEN) == 0)
					processEntryChanges(projectDeltas[i], projectsToSave);
			job.addChanges(projectsToSave);
		}
	}

	private static final String PROJECT_KEY = "<project>"; //$NON-NLS-1$
	private CharsetDeltaJob charsetListener;
	CharsetManagerJob job;
	private IResourceChangeListener resourceChangeListener;
	protected final Bundle systemBundle = Platform.getBundle("org.eclipse.osgi"); //$NON-NLS-1$
	Workspace workspace;

	public CharsetManager(Workspace workspace) {
		this.workspace = workspace;
	}

	void flushPreferences(Preferences projectPrefs, boolean shouldDisableCharsetDeltaJob) throws BackingStoreException {
		if (projectPrefs != null) {
			try {
				if (shouldDisableCharsetDeltaJob)
					charsetListener.setDisabled(true);
				projectPrefs.flush();
			} finally {
				if (shouldDisableCharsetDeltaJob)
					charsetListener.setDisabled(false);
			}
		}
	}

	/**
	 * Returns the charset explicitly set by the user for the given resource, 
	 * or <code>null</code>. If no setting exists for the given resource and 
	 * <code>recurse</code> is <code>true</code>, every parent up to the 
	 * workspace root will be checked until a charset setting can be found.
	 * 
	 * @param resourcePath the path for the resource
	 * @param recurse whether the parent should be queried
	 * @return the charset setting for the given resource
	 */
	public String getCharsetFor(IPath resourcePath, boolean recurse) {
		Assert.isLegal(resourcePath.segmentCount() >= 1);
		IProject project = workspace.getRoot().getProject(resourcePath.segment(0));

		Preferences prefs = getPreferences(project, false, false);
		Preferences derivedPrefs = getPreferences(project, false, true);

		if (prefs == null && derivedPrefs == null)
			// no preferences found - for performance reasons, short-circuit 
			// lookup by falling back to workspace's default setting			
			return recurse ? ResourcesPlugin.getEncoding() : null;

		return internalGetCharsetFor(prefs, derivedPrefs, resourcePath, recurse);
	}

	static String getKeyFor(IPath resourcePath) {
		return resourcePath.segmentCount() > 1 ? resourcePath.removeFirstSegments(1).toString() : PROJECT_KEY;
	}

	Preferences getPreferences(IProject project, boolean create, boolean isDerived) {
		return getPreferences(project, create, isDerived, isDerivedEncodingStoredSeparately(project));
	}

	Preferences getPreferences(IProject project, boolean create, boolean isDerived, boolean isDerivedEncodingStoredSeparately) {
		boolean localIsDerived = isDerivedEncodingStoredSeparately ? isDerived : false;
		String qualifier = localIsDerived ? ProjectPreferences.PREFS_DERIVED_QUALIFIER : ProjectPreferences.PREFS_REGULAR_QUALIFIER;
		if (create)
			// create all nodes down to the one we are interested in
			return new ProjectScope(project).getNode(qualifier).node(ResourcesPlugin.PREF_ENCODING);
		// be careful looking up for our node so not to create any nodes as side effect
		Preferences node = Platform.getPreferencesService().getRootNode().node(ProjectScope.SCOPE);
		try {
			//TODO once bug 90500 is fixed, should be as simple as this:
			//			String path = project.getName() + IPath.SEPARATOR + ResourcesPlugin.PI_RESOURCES + IPath.SEPARATOR + ENCODING_PREF_NODE;
			//			return node.nodeExists(path) ? node.node(path) : null;
			// for now, take the long way
			if (!node.nodeExists(project.getName()))
				return null;
			node = node.node(project.getName());
			if (!node.nodeExists(qualifier))
				return null;
			node = node.node(qualifier);
			if (!node.nodeExists(ResourcesPlugin.PREF_ENCODING))
				return null;
			return node.node(ResourcesPlugin.PREF_ENCODING);
		} catch (BackingStoreException e) {
			// nodeExists failed
			String message = Messages.resources_readingEncoding;
			Policy.log(new ResourceStatus(IResourceStatus.FAILED_GETTING_CHARSET, project.getFullPath(), message, e));
		}
		return null;
	}

	private String internalGetCharsetFor(Preferences prefs, Preferences derivedPrefs, IPath resourcePath, boolean recurse) {
		String charset = null;

		// try to find the encoding in regular and then derived preferences
		if (prefs != null)
			charset = prefs.get(getKeyFor(resourcePath), null);
		// derivedPrefs may be not null, only if #isDerivedEncodingStoredSeparately returns true
		// so the explicit check against #isDerivedEncodingStoredSeparately is not required
		if (charset == null && derivedPrefs != null)
			charset = derivedPrefs.get(getKeyFor(resourcePath), null);

		if (!recurse)
			return charset;

		while (charset == null && resourcePath.segmentCount() > 1) {
			resourcePath = resourcePath.removeLastSegments(1);
			// try to find the encoding in regular and then derived preferences
			if (prefs != null)
				charset = prefs.get(getKeyFor(resourcePath), null);
			if (charset == null && derivedPrefs != null)
				charset = derivedPrefs.get(getKeyFor(resourcePath), null);
		}

		// ensure we default to the workspace encoding if none is found
		return charset == null ? ResourcesPlugin.getEncoding() : charset;
	}

	private boolean isDerivedEncodingStoredSeparately(IProject project) {
		// be careful looking up for our node so not to create any nodes as side effect
		Preferences node = Platform.getPreferencesService().getRootNode().node(ProjectScope.SCOPE);
		try {
			//TODO once bug 90500 is fixed, should be as simple as this:
			//			String path = project.getName() + IPath.SEPARATOR + ResourcesPlugin.PI_RESOURCES;
			//			return node.nodeExists(path) ? node.node(path).getBoolean(ResourcesPlugin.PREF_SEPARATE_DERIVED_ENCODINGS, false) : false;
			// for now, take the long way
			if (!node.nodeExists(project.getName()))
				return false;
			node = node.node(project.getName());
			if (!node.nodeExists(ResourcesPlugin.PI_RESOURCES))
				return false;
			node = node.node(ResourcesPlugin.PI_RESOURCES);
			return node.getBoolean(ResourcesPlugin.PREF_SEPARATE_DERIVED_ENCODINGS, false);
		} catch (BackingStoreException e) {
			// nodeExists failed
			String message = Messages.resources_readingEncoding;
			Policy.log(new ResourceStatus(IResourceStatus.FAILED_GETTING_CHARSET, project.getFullPath(), message, e));
			return false;
		}
	}

	protected void mergeEncodingPreferences(IProject project) {
		Preferences projectRegularPrefs = null;
		Preferences projectDerivedPrefs = getPreferences(project, false, true, true);
		if (projectDerivedPrefs == null)
			return;
		try {
			boolean prefsChanged = false;
			String[] affectedResources;
			affectedResources = projectDerivedPrefs.keys();
			for (int i = 0; i < affectedResources.length; i++) {
				String path = affectedResources[i];
				String value = projectDerivedPrefs.get(path, null);
				projectDerivedPrefs.remove(path);
				// lazy creation of non-derived preferences
				if (projectRegularPrefs == null)
					projectRegularPrefs = getPreferences(project, true, false, false);
				projectRegularPrefs.put(path, value);
				prefsChanged = true;
			}
			if (prefsChanged) {
				Map<IProject, Boolean> projectsToSave = new HashMap<IProject, Boolean>();
				// this is internal change so do not notify charset delta job
				projectsToSave.put(project, Boolean.TRUE);
				job.addChanges(projectsToSave);
			}
		} catch (BackingStoreException e) {
			// problems with the project scope... we will miss the changes (but will log)
			String message = Messages.resources_readingEncoding;
			Policy.log(new ResourceStatus(IResourceStatus.FAILED_GETTING_CHARSET, project.getFullPath(), message, e));
		}
	}

	public void projectPreferencesChanged(IProject project) {
		charsetListener.charsetPreferencesChanged(project);
	}

	public void setCharsetFor(IPath resourcePath, String newCharset) throws CoreException {
		// for the workspace root we just set a preference in the instance scope
		if (resourcePath.segmentCount() == 0) {
			IEclipsePreferences resourcesPreferences = InstanceScope.INSTANCE.getNode(ResourcesPlugin.PI_RESOURCES);
			if (newCharset != null)
				resourcesPreferences.put(ResourcesPlugin.PREF_ENCODING, newCharset);
			else
				resourcesPreferences.remove(ResourcesPlugin.PREF_ENCODING);
			try {
				resourcesPreferences.flush();
			} catch (BackingStoreException e) {
				IProject project = workspace.getRoot().getProject(resourcePath.segment(0));
				String message = Messages.resources_savingEncoding;
				throw new ResourceException(IResourceStatus.FAILED_SETTING_CHARSET, project.getFullPath(), message, e);
			}
			return;
		}
		// for all other cases, we set a property in the corresponding project
		IResource resource = workspace.getRoot().findMember(resourcePath);
		if (resource != null) {
			try {
				// disable the listener so we don't react to changes made by ourselves
				Preferences encodingSettings = getPreferences(resource.getProject(), true, resource.isDerived(IResource.CHECK_ANCESTORS));
				if (newCharset == null || newCharset.trim().length() == 0)
					encodingSettings.remove(getKeyFor(resourcePath));
				else
					encodingSettings.put(getKeyFor(resourcePath), newCharset);
				flushPreferences(encodingSettings, true);
			} catch (BackingStoreException e) {
				IProject project = workspace.getRoot().getProject(resourcePath.segment(0));
				String message = Messages.resources_savingEncoding;
				throw new ResourceException(IResourceStatus.FAILED_SETTING_CHARSET, project.getFullPath(), message, e);
			}
		}
	}

	public void shutdown(IProgressMonitor monitor) {
		workspace.removeResourceChangeListener(resourceChangeListener);
		if (charsetListener != null)
			charsetListener.shutdown();
	}

	protected void splitEncodingPreferences(IProject project) {
		Preferences projectRegularPrefs = getPreferences(project, false, false, false);
		Preferences projectDerivedPrefs = null;
		if (projectRegularPrefs == null)
			return;
		try {
			boolean prefsChanged = false;
			String[] affectedResources;
			affectedResources = projectRegularPrefs.keys();
			for (int i = 0; i < affectedResources.length; i++) {
				String path = affectedResources[i];
				IResource resource = project.findMember(path);
				if (resource != null) {
					if (resource.isDerived(IResource.CHECK_ANCESTORS)) {
						String value = projectRegularPrefs.get(path, null);
						projectRegularPrefs.remove(path);
						// lazy creation of derived preferences
						if (projectDerivedPrefs == null)
							projectDerivedPrefs = getPreferences(project, true, true, true);
						projectDerivedPrefs.put(path, value);
						prefsChanged = true;
					}
				}
			}
			if (prefsChanged) {
				Map<IProject, Boolean> projectsToSave = new HashMap<IProject, Boolean>();
				// this is internal change so do not notify charset delta job
				projectsToSave.put(project, Boolean.TRUE);
				job.addChanges(projectsToSave);
			}
		} catch (BackingStoreException e) {
			// problems with the project scope... we will miss the changes (but will log)
			String message = Messages.resources_readingEncoding;
			Policy.log(new ResourceStatus(IResourceStatus.FAILED_GETTING_CHARSET, project.getFullPath(), message, e));
		}
	}

	public void startup(IProgressMonitor monitor) {
		job = new CharsetManagerJob();
		resourceChangeListener = new ResourceChangeListener();
		workspace.addResourceChangeListener(resourceChangeListener, IResourceChangeEvent.POST_CHANGE);
		charsetListener = new CharsetDeltaJob(workspace);
		charsetListener.startup();
	}
}
