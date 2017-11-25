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
package ut.seal.plugins.utils.change;

import java.io.File;
import java.util.List;

import org.junit.Test;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.SourceRange;
import ch.uzh.ifi.seal.changedistiller.model.entities.Move;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.model.entities.Update;

/**
 * @author Myoungkyu Song
 * @date Dec 8, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTChangeDistillerFile {
	private List<SourceCodeChange>	changes;

	/**
	 * Diff.
	 * 
	 * @param javaText1 the java text1
	 * @param javaText2 the java text2
	 */
	public void diff(File javaText1, File javaText2) {
		try {
			FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
			distiller.extractClassifiedSourceCodeChanges(javaText1, javaText2);
			changes = distiller.getSourceCodeChanges();
		} catch (Exception e) {
			System.err.println("Warning: error while change distilling. " + e.getMessage());
		}
	}

	/**
	 * Diff.
	 * 
	 * @param f1 the f1
	 * @param f2 the f2
	 * @param changes the changes
	 */
	public void diff(File f1, File f2, List<SourceCodeChange> changes) {
		try {
			FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
			distiller.extractClassifiedSourceCodeChanges(f1, f2);
			changes = distiller.getSourceCodeChanges();
		} catch (Exception e) {
			System.err.println("Warning: error while change distilling. " + e.getMessage());
		}
	}

	/**
	 * Prints the changes.
	 */
	public void printChanges() {
		if (changes == null) {
			System.out.println("[INFO] THERE IS NO CHANGE.");
		} else {
			for (int i = 0; i < changes.size(); i++) {
				SourceCodeChange change = changes.get(i);
				SourceCodeEntity newEntity = null, changedEntity = null;

				if (change instanceof Update) {
					Update update = (Update) change;
					newEntity = update.getNewEntity();
				} else if (change instanceof Move) {
					Move move = (Move) change;
					newEntity = move.getNewEntity();
				}
				System.out.println("   * " + change.getChangeType());
				changedEntity = change.getChangedEntity();
				getChangedInfo(changedEntity, change);
				if (newEntity != null) {
					System.out.println("        :     ");
					System.out.println("        V     ");
				} else
					System.out.println();
				getChangedInfo(newEntity, change);
				System.out.println("DBG__________________________________________");
			}
		}
		System.out.println("==========================================");
	}

	/**
	 * Gets the changed info.
	 * 
	 * @param changedEntity the changed entity
	 * @param change the change
	 * @return the changed info
	 */
	public void getChangedInfo(SourceCodeEntity changedEntity, SourceCodeChange change) {
		if (changedEntity == null) {
			return;
		}
		SourceRange changedRange = changedEntity.getSourceRange();
		String changeName = changedEntity.getUniqueName();
		String changeType = changedEntity.getType().toString();
		// SourceCodeEntity parent = change.getParentEntity();
		// String parentName = parent.getUniqueName();
		// SourceRange parentChangedRange = parent.getSourceRange();
		System.out.println("[DBG] " + changedRange + ", (" + changeType + ")" + "(" + changeName + ")"//
				/*+ ", PARENT: " + parent.getType() + parent.getSourceRange()*/);
	}

	/**
	 * Test basic.
	 */
	@Test
	public void testBasic() {
		File left = new File("/Users/mksong/workspaceCritics/UT-ChangeDistiller/A5.java");
		File right = new File("/Users/mksong/workspaceCritics/UT-ChangeDistiller/A6.java");
		diff(left, right);
		printChanges();
	}
}
