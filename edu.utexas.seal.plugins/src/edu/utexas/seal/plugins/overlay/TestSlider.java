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
package edu.utexas.seal.plugins.overlay;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;

/*
 * Slider example snippet: print scroll event details
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 */
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