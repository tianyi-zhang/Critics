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
package edu.utexas.seal.plugins.util.root;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.graphics.Point;

/**
 * @author Myoungkyu Song
 * @date Oct 23, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTCriticsLocation {

	/**
	 * 
	 * @param region
	 * @param doc
	 * @return
	 */
	public static Point getLineRange(Point region, IDocument doc) {
		Point lineRange = new Point(0, 0);
		if (doc == null) {
			return lineRange;
		}
		try {
			lineRange.x = doc.getLineOfOffset(region.x) + 1;
			lineRange.y = doc.getLineOfOffset(region.x + region.y) + 1;
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return lineRange;
	}

	/**
	 * 
	 * @param region
	 * @param doc
	 * @return
	 */
	public static Point getLineRange(IRegion region, IDocument doc) {
		Point lineRange = new Point(0, 0);
		if (doc == null) {
			return lineRange;
		}
		try {
			lineRange.x = doc.getLineOfOffset(region.getOffset()) + 1;
			lineRange.y = doc.getLineOfOffset(region.getOffset() + region.getLength()) + 1;
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return lineRange;
	}

	/**
	 * 
	 * @param left_offset_bgn
	 * @param left_offset_end
	 * @param doc
	 * @return
	 */
	public static Point getLineRange(int left_offset_bgn, int left_offset_end, IDocument doc) {
		Point lineRange = new Point(0, 0);
		if (doc == null) {
			return lineRange;
		}
		try {
			lineRange.x = doc.getLineOfOffset(left_offset_bgn) + 1;
			lineRange.y = doc.getLineOfOffset(left_offset_end) + 1;
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return lineRange;
	}

}
