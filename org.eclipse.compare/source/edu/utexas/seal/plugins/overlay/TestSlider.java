/*
 * Slider example snippet: print scroll event details
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 */
package edu.utexas.seal.plugins.overlay;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;

public class TestSlider {
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		Slider slider = new Slider(shell, SWT.HORIZONTAL);
		Rectangle clientArea = shell.getClientArea();
		slider.setBounds(clientArea.x + 10, clientArea.y + 10, 200, 32);
		slider.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				String string = "SWT.NONE";
				switch (event.detail) {
				case SWT.DRAG:
					string = "SWT.DRAG";
					break;
				case SWT.HOME:
					string = "SWT.HOME";
					break;
				case SWT.END:
					string = "SWT.END";
					break;
				case SWT.ARROW_DOWN:
					string = "SWT.ARROW_DOWN";
					break;
				case SWT.ARROW_UP:
					string = "SWT.ARROW_UP";
					break;
				case SWT.PAGE_DOWN:
					string = "SWT.PAGE_DOWN";
					break;
				case SWT.PAGE_UP:
					string = "SWT.PAGE_UP";
					break;
				}
				System.out.println("Scroll detail -> " + string);
			}
		});
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}