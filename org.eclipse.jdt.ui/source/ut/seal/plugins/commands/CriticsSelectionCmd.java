/*
 * @(#) CriticsSelectionCmd.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
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
