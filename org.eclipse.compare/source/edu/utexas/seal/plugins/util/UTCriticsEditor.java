/*
 * @(#) UTCriticsEditor.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.util;

import java.io.File;
import java.io.IOException;

import org.eclipse.compare.CompareUI;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import edu.utexas.seal.plugins.compare.util.CompareInput;
import edu.utexas.seal.plugins.compare.util.CompareItem;

/**
 * @author Myoungkyu Song
 * @date Jan 23, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTCriticsEditor {
	/**
	 * Open Editor.
	 * 
	 * @param aFileToOpen the a file to open
	 */
	public static void openEditor(File aFileToOpen) {
		if (aFileToOpen.exists() && aFileToOpen.isFile()) {
			IFileStore fileStore = EFS.getLocalFileSystem().getStore(aFileToOpen.toURI());
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			try {
				IDE.openEditorOnFileStore(page, fileStore);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Open comparison editor.
	 * 
	 * @param fLeft the f left
	 * @param fRight the f right
	 * @throws IOException
	 */
	public static void openComparisonEditor(File fLeft, File fRight) {
		CompareItem left = new CompareItem(fLeft);
		CompareItem right = new CompareItem(fRight);
		CompareInput comInput = new CompareInput(left, right);
		CompareUI.openCompareEditor(comInput);
	}
}
