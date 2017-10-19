/*******************************************************************************
 * Copyright (c) 2008, 2010 Freescale Semiconductor and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Freescale Semiconductor - initial API and implementation
 *     IBM Corporation - ongoing development
 *******************************************************************************/
package org.eclipse.core.internal.resources.projectvariables;

import java.net.URISyntaxException;
import java.net.URL;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.variableresolvers.PathVariableResolver;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.URIUtil;

/**
 * ECLIPSE_HOME project variable, pointing to the location of the eclipse install directory.
 * 
 */
public class EclipseHomeProjectVariable extends PathVariableResolver {

	public static String NAME = "ECLIPSE_HOME"; //$NON-NLS-1$

	public EclipseHomeProjectVariable() {
		// nothing to do.
	}

	public String[] getVariableNames(String variable, IResource resource) {
		return new String[] {NAME};
	}

	public String getValue(String variable, IResource resource) {
		URL installURL = Platform.getInstallLocation().getURL();
		try {
			return URIUtil.toURI(installURL).toASCIIString();
		} catch (URISyntaxException e) {
			return null;
		}
	}
}
