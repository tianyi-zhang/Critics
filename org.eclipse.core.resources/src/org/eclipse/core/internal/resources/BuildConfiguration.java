/*******************************************************************************
 * Copyright (c) 2010, 2012 Broadcom Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Broadcom Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.internal.resources;

import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.PlatformObject;

/**
 * Concrete implementation of a build configuration.
 *<p>
 * This class can both be used as a real build configuration in a project.
 * As well as the reference to a build configuration in another project.
 *</p>
 *<p>
 * When being used as a reference, core.resources <strong>must</strong> call
 * {@link #getBuildConfig()} to dereference the build configuration to the
 * the actual build configuration on the referenced project.
 *</p>
 */
public class BuildConfiguration extends PlatformObject implements IBuildConfiguration {

	/** Project on which this build configuration is set */
	private final IProject project;
	/** Unique human readable name of the configuration in the project */
	private final String name;

	public BuildConfiguration(IProject project) {
		this(project, IBuildConfiguration.DEFAULT_CONFIG_NAME);
	}

	public BuildConfiguration(IProject project, String configName) {
		this.project = project;
		this.name = configName;
	}

	/**
	 * @return the concrete build configuration referred to by this IBuildConfiguration
	 *         when it's being used as a reference
	 */
	public IBuildConfiguration getBuildConfig() throws CoreException {
		return project.getBuildConfig(name);
	}

	/*
	 * (non-Javadoc)
	 * @see IBuildConfiguration#getName()
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * @see IBuildConfiguration#getProject()
	 */
	public IProject getProject() {
		return project;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BuildConfiguration other = (BuildConfiguration) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (project == null) {
			if (other.project != null)
				return false;
		} else if (!project.equals(other.project))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((project == null) ? 0 : project.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer result = new StringBuffer();
		if (project != null)
			result.append(project.getName());
		else
			result.append("?"); //$NON-NLS-1$
		result.append(";"); //$NON-NLS-1$
		if (name != null)
			result.append(" [").append(name).append(']'); //$NON-NLS-1$
		else
			result.append(" [active]"); //$NON-NLS-1$
		return result.toString();
	}

	@Override
	public Object getAdapter(Class adapter) {
		if (adapter.isInstance(project))
			return project;
		return super.getAdapter(adapter);
	}

}
