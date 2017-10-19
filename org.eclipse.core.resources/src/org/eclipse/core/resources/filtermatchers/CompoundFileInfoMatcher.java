/*******************************************************************************
 * Copyright (c) 2008, 2010 Freescale Semiconductor and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Serge Beauchamp (Freescale Semiconductor) - [252996] initial API and implementation
 *     IBM Corporation - ongoing implementation
 *******************************************************************************/
package org.eclipse.core.resources.filtermatchers;

import org.eclipse.core.internal.resources.FilterDescriptor;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;

/**
 * Resource Filter Type allowing serializing sub filters as the arguments
 * @since 3.6
 */
public abstract class CompoundFileInfoMatcher extends AbstractFileInfoMatcher {

	protected AbstractFileInfoMatcher[] matchers;

	private AbstractFileInfoMatcher instantiate(IProject project, FileInfoMatcherDescription filter) throws CoreException {
		IFilterMatcherDescriptor desc = project.getWorkspace().getFilterMatcherDescriptor(filter.getId());
		if (desc != null) {
			AbstractFileInfoMatcher matcher = ((FilterDescriptor) desc).createFilter();
			matcher.initialize(project, filter.getArguments());
			return matcher;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.resources.AbstractFileInfoMatcher#initialize(org.eclipse
	 * .core.resources.IProject, java.lang.Object)
	 */
	public final void initialize(IProject project, Object arguments) throws CoreException {
		FileInfoMatcherDescription[] filters = (FileInfoMatcherDescription[]) arguments;
		matchers = new AbstractFileInfoMatcher[filters != null ? filters.length : 0];
		for (int i = 0; i < matchers.length; i++)
			matchers[i] = instantiate(project, filters[i]);
	}
}