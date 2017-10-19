/*******************************************************************************
 * Copyright (c) 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Serge Beauchamp (Freescale Semiconductor) - initial API and implementation
 *     James Blackburn (Broadcom Corp.) - ongoing development
 *******************************************************************************/
package org.eclipse.core.internal.resources;

import org.eclipse.core.runtime.Assert;

/**
 * 
 */
public class VariableDescription implements Comparable<VariableDescription> {

	private String name;
	private String value;

	public VariableDescription() {
		this.name = ""; //$NON-NLS-1$
		this.value = ""; //$NON-NLS-1$
	}

	public VariableDescription(String name, String value) {
		super();
		Assert.isNotNull(name);
		Assert.isNotNull(value);
		this.name = name;
		this.value = value;
	}

	public boolean equals(Object o) {
		if (!(o.getClass() == VariableDescription.class))
			return false;
		VariableDescription other = (VariableDescription) o;
		return name.equals(other.name) && value == other.value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public int hashCode() {
		return name.hashCode() + value.hashCode();
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Compare string descriptions in a way that sorts them topologically by
	 * name.
	 */
	public int compareTo(VariableDescription that) {
		return name.compareTo(that.name);
	}
}
