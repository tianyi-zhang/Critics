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
package org.eclipse.core.internal.events;

import java.util.Arrays;
import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.IBuildContext;
import org.eclipse.core.runtime.Assert;

/**
 * Concrete implementation of a build context
 */
public class BuildContext implements IBuildContext {

	/** The Build Configuration currently being built */
	private final IBuildConfiguration buildConfiguration;
	/** Configurations user requested to be built */
	private final IBuildConfiguration[] requestedBuilt;
	/** The configurations built as part of this build invocations*/
	private final IBuildConfiguration[] buildOrder;

	/**
	 * Create an empty build context for the given project configuration.
	 * @param buildConfiguration the project configuration being built, that we need the context for
	 */
	public BuildContext(IBuildConfiguration buildConfiguration) {
		this.buildConfiguration = buildConfiguration;
		requestedBuilt = buildOrder = new IBuildConfiguration[] {buildConfiguration};
	}

	/**
	 * Create a build context for the given project configuration.
	 * @param buildConfiguration the project configuration being built, that we need the context for
	 * @param requestedBuilt an array of configurations the user actually requested to be built
	 * @param buildOrder the build order for the entire build, indicating how cycles etc. have been resolved
	 */
	public BuildContext(IBuildConfiguration buildConfiguration, IBuildConfiguration[] requestedBuilt, IBuildConfiguration[] buildOrder) {
		this.buildConfiguration = buildConfiguration;
		this.requestedBuilt = requestedBuilt;
		this.buildOrder = buildOrder;
	}

	private int findBuildConfigurationIndex() {
		int position = -1;
		for (int i = 0; i < buildOrder.length; i++) {
			if (buildOrder[i].equals(buildConfiguration))
			{
				position = i;
				break;
			}
		}
		Assert.isTrue(0 <= position && position < buildOrder.length);
		return position;
	}

	public IBuildConfiguration[] getRequestedConfigs() {
		return requestedBuilt.clone();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.resources.IBuildContext#getAllReferencedBuildConfigurations()
	 */
	public IBuildConfiguration[] getAllReferencedBuildConfigs() {
		int position = findBuildConfigurationIndex();
		IBuildConfiguration[] builtBefore = new IBuildConfiguration[position];
		System.arraycopy(buildOrder, 0, builtBefore, 0, builtBefore.length);
		return builtBefore;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.resources.IBuildContext#getAllReferencingBuildConfigurations()
	 */
	public IBuildConfiguration[] getAllReferencingBuildConfigs() {
		int position = findBuildConfigurationIndex();
		IBuildConfiguration[] builtAfter = new IBuildConfiguration[buildOrder.length - position - 1];
		System.arraycopy(buildOrder, position + 1, builtAfter, 0, builtAfter.length);
		return builtAfter;
	}

	private static final int hashCode(IBuildConfiguration[] array) {
		final int prime = 31;
		int result = 1;
		for (int i = 0; i < array.length; i++)
			result = prime * result + array[i].hashCode();
		return result;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + buildConfiguration.hashCode();
		result = prime * result + hashCode(requestedBuilt);
		result = prime * result + hashCode(buildOrder);
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BuildContext other = (BuildContext) obj;
		if (!buildConfiguration.equals(other.buildConfiguration))
			return false;
		if (!Arrays.equals(requestedBuilt, other.requestedBuilt))
			return false;
		if (!Arrays.equals(buildOrder, other.buildOrder))
			return false;
		return true;
	}

}
