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
package ut.seal.plugins.commands;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.IWorkingCopyManager;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;

import ut.seal.plugins.util.UTPlugin;
import ut.seal.plugins.utils.UTLocalFileHistory;

/**
 * @author Myoungkyu Song
 * @date Oct 19, 2013
 * @since JDK1.6
 */
public class CriticsSelectionCmd implements ICriticsCmd {

	/**
	 * 
	 * Oct 21, 2013 9:57:32 PM
	 * i don't think that a user can compare her selected code without 
	 * seeing the code in local history.
	 */
	public Object getResult() {
		String result = null;
		try {
			IEditorPart editorPart = UTPlugin.getActiveEditor();
			if (editorPart instanceof JavaEditor) {
				IWorkingCopyManager manager = JavaPlugin.getDefault().getWorkingCopyManager();
				ICompilationUnit unit = manager.getWorkingCopy(editorPart.getEditorInput());
				result = unit.getSource();
				UTLocalFileHistory.setCurrentFilePath(unit.getPath());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 */
	public Object getResult2() {
		String result = null;
		Point offset = null;
		ISourceViewer viewer = null;
		try {
			IEditorPart editorPart = UTPlugin.getActiveEditor();
			if (editorPart instanceof JavaEditor) {
				IWorkingCopyManager manager = JavaPlugin.getDefault().getWorkingCopyManager();
				ICompilationUnit unit = manager.getWorkingCopy(editorPart.getEditorInput());

				viewer = (ISourceViewer) ((JavaEditor) editorPart).getViewer();
				offset = viewer.getSelectedRange();

				if (offset.y == 0) {
					System.err.println(MSG_SELECTION_CMD_USAGE);
				} else {
					String src = unit.getSource();
					// result = src.substring(offset.x, offset.x + offset.y);
					result = src;
				}
			}
			// mksong Oct 21, 2013 8:50:21 AM - move to "org.eclipse.compare"
			// else if (editorPart instanceof CompareEditor) {
			// CompareEditor comEditor = (CompareEditor) editorPart;
			// if (comEditor.getEditorInput() instanceof CompareFileRevisionEditorInput) {
			// System.out.println("[DBG] " + comEditor);
			// CompareFileRevisionEditorInput frEditorInput = (CompareFileRevisionEditorInput) comEditor.getEditorInput();
			// // LocalResourceTypedElement o = (LocalResourceTypedElement)frEditorInput.getLeft();
			// if (frEditorInput.getLeft() instanceof ISourceViewer) {
			// ISourceViewer new_name = (ISourceViewer) frEditorInput.getLeft();
			// System.out.print("");
			// }
			// System.out.print("");
			// }
			// System.out.println("[DBG] " + comEditor);
			// System.out.println("[DBG] point: " + UTLocalFileHistory.getLeftSelectedRegion());
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
