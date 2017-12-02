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
package edu.utexas.seal.plugins.crystal.internal;

import java.io.Serializable;

@SuppressWarnings("rawtypes")
public class SourceCodeRange implements Serializable, Comparable {

	private static final long	serialVersionUID	= 1L;
	public int					startPosition;
	public int					length;

	public static SourceCodeRange getDefaultScr() {
		return new SourceCodeRange(0, 0);
	}

	public SourceCodeRange(SourceCodeRange scr) {
		this.startPosition = scr.startPosition;
		this.length = scr.length;
	}

	public SourceCodeRange(int startPosition, int length) {
		this.startPosition = startPosition;
		this.length = length;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof SourceCodeRange))
			return false;
		SourceCodeRange other = (SourceCodeRange) obj;
		if (this.startPosition != other.startPosition)
			return false;
		if (this.length != other.length)
			return false;
		return true;
	}

	public int hashCode() {
		return this.startPosition * 1000 + this.length;
	}

	public boolean isInside(SourceCodeRange other) {
		return startPosition >= other.startPosition && length <= other.length;
	}

	public String toString() {
		return "start position = " + this.startPosition + "  length = " + this.length;
	}

	/**
	 * This method does not handle the containment relations between two objects
	 */
	@Override
	public int compareTo(Object obj) {
		SourceCodeRange other = (SourceCodeRange) obj;
		if (this.startPosition < other.startPosition && this.startPosition + this.length < other.startPosition + other.length)
			return -1;
		if (this.startPosition > other.startPosition && this.startPosition + this.length > other.startPosition + other.length)
			return 1;
		return 0;
	}

}
