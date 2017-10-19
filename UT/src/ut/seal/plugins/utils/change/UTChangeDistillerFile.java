/*
 * @(#) UTChangeDistillerStatic.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
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
