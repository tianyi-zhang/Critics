/*
 * @(#) UTPlugin.java
 *
 */
package util;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Myoungkyu Song
 * @date Oct 18, 2013
 * @since JDK1.7
 */
public class UTPlugin {

	/**
	 * 
	 * @return
	 */
	public static Display getDisplay() {
		Display display = Display.getCurrent();
		if (display == null)
			display = Display.getDefault();
		return display;
	}

	/**
	 * 
	 */
	public static IEditorPart getActiveEditor() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	}

}
