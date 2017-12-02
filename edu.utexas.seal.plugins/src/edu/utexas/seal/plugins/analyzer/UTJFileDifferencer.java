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
package edu.utexas.seal.plugins.analyzer;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

import edu.utexas.seal.plugins.edit.UTAbstractEdit;
import edu.utexas.seal.plugins.edit.UTEditDifferencer;
import edu.utexas.seal.plugins.util.UTCriticsPairFileInfo;

public class UTJFileDifferencer {
	private static final String				S	= System.getProperty("file.separator");
	public static ArrayList<UTAbstractEdit>	edits;

	public UTJFileDifferencer() {
		if (edits != null) {
			edits.clear();
		}

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
	}
}
