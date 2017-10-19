/*
 * @(#) UTCmdTextSelection.java
 *
 */
package util;

//import org.eclipse.jdt.core.ICompilationUnit;
//import org.eclipse.jdt.internal.ui.JavaPlugin;
//import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
//import org.eclipse.jdt.ui.IWorkingCopyManager;
import org.eclipse.jface.text.source.ISourceViewer;
//import org.eclipse.swt.graphics.Point;
//import org.eclipse.ui.IEditorPart;

/**
 * @author Myoungkyu Song
 * @date Oct 18, 2013
 * @since JDK1.7
 */
public class UTCmdTextSelection {

	public static ISourceViewer leftViewer;
	
	// /**
	// *
	// */
	public void procExampleAction() {
		// System.out.println("DBG__________________________________________");
		// System.out.println("[DBG] SELECTED CODE BLOCK");
		// System.out.println("DBG__________________________________________");
		// try {
		// IEditorPart editorPart = UTPlugin.getActiveEditor();
		// IWorkingCopyManager manager = JavaPlugin.getDefault().getWorkingCopyManager();
		// ICompilationUnit unit = manager.getWorkingCopy(editorPart.getEditorInput());
		//
		// ISourceViewer viewer = (ISourceViewer) ((JavaEditor) editorPart).getViewer();
		// Point offset = viewer.getSelectedRange();
		//
		// if (offset.y == 0) {
		// System.err.println("[USG] SELECT CODE BLOCK AND SAVE A FILE (i.e., CTRL + S)");
		// } else {
		// String src = unit.getSource();
		// System.out.println(src.substring(offset.x, offset.x + offset.y));
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

}
