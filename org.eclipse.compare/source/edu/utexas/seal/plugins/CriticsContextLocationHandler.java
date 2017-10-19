/*
 * @(#) CriticsContextLocationHandler.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

import edu.utexas.seal.plugins.analyzer.UTJFileAnalyzer;
import edu.utexas.seal.plugins.edit.UTAbstractEdit;
import edu.utexas.seal.plugins.edit.UTEditDifferencer;
import edu.utexas.seal.plugins.util.UTCriticsPairFileInfo;

public class CriticsContextLocationHandler extends AbstractHandler {
	private static final String				S	= System.getProperty("file.separator");
	public static ArrayList<UTAbstractEdit>	edits;

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath path_root = workspace.getRoot().getLocation();

		String leftFilePath = path_root.toOSString() + S + UTCriticsPairFileInfo.getLeftFilePath();
		String rightFilePath = path_root.toOSString() + S + UTCriticsPairFileInfo.getRightFilePath();

		// Differencing two files in compare view with UTEditDifferencer
		UTEditDifferencer differencer = new UTEditDifferencer();
		File leftFile = new File(leftFilePath);
		File rightFile = new File(rightFilePath);
		differencer.diff(leftFile, rightFile);
		edits = differencer.getEdits();
		// for(UTAbstractEdit edit : edits){
		// System.out.println(edit.toString());
		// }
		UTJFileAnalyzer analyzer = new UTJFileAnalyzer();
		// analyzer.classLevel();
		analyzer.methodLevel();
		return null;
	}

}
