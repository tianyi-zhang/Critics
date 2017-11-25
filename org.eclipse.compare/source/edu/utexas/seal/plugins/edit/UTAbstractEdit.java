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
package edu.utexas.seal.plugins.edit;

import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;

/**
 * @author Myoungkyu Song
 * @date Oct 28, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public abstract class UTAbstractEdit {
	int					editId;
	String				editKind;

	// new version
	SourceCodeChange	leftEditChange;
	int					leftEditOffsetBgn;
	int					leftEditOffsetEnd;
	String				leftEditStatement;
	String				leftEditContext;

	// old version
	SourceCodeChange	rightEditChange;
	int					rightEditOffsetBgn;
	int					rightEditOffsetEnd;
	String				rightEditStatement;
	String				rightEditContext;

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ID: " + editId + ", ");
		sb.append("TYPE: " + editKind + ", ");
		if (leftEditChange != null)
			sb.append(leftEditChange.getChangedEntity().getType() + " " + leftEditChange.toString());
		if (rightEditChange != null)
			sb.append(rightEditChange.getChangedEntity().getType() + " " + rightEditChange.toString());
		return sb.toString();
	}

	public int getEditId() {
		return editId;
	}

	public String getEditKind() {
		return editKind;
	}

	public SourceCodeChange getLeftEditChange() {
		return leftEditChange;
	}

	public int getLeftEditOffsetBgn() {
		return leftEditOffsetBgn;
	}

	public int getLeftEditOffsetEnd() {
		return leftEditOffsetEnd;
	}

	public String getLeftEditStatement() {
		return leftEditStatement;
	}

	public String getLeftEditContext() {
		return leftEditContext;
	}

	public SourceCodeChange getRightEditChange() {
		return rightEditChange;
	}

	public int getRightEditOffsetBgn() {
		return rightEditOffsetBgn;
	}

	public int getRightEditOffsetEnd() {
		return rightEditOffsetEnd;
	}

	public String getRightEditStatement() {
		return rightEditStatement;
	}

	public String getRightEditContext() {
		return rightEditContext;
	}

}
