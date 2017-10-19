/*
 * @(#) UTCriticsChangeReporter.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugin.preprocess.update;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;

public class UTCriticsChangeReporter implements IResourceChangeListener {

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		IResource res = event.getResource();
		switch (event.getType()) {
        	case IResourceChangeEvent.PRE_CLOSE:
        		System.out.print("Project ");
        		System.out.print(res.getFullPath());
        		System.out.println(" is about to close.");
        		break;
        	case IResourceChangeEvent.PRE_DELETE:
        		System.out.print("Project ");
        		System.out.print(res.getFullPath());
        		System.out.println(" is about to be deleted.");
        		break;
        	case IResourceChangeEvent.POST_CHANGE:
        		System.out.println("Resources have changed.");
        		try {
        			event.getDelta().accept(new UTCriticsChangeVisitor());
        		} catch (CoreException e) {
        			e.printStackTrace();
        		}
        		break;
        	case IResourceChangeEvent.PRE_BUILD:
        		System.out.println("Build about to run.");
        		break;
        	case IResourceChangeEvent.POST_BUILD:
        		System.out.println("Build complete.");
        		break;
		}
	}
}
