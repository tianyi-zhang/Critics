/*
 * @(#) Test01.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package mytest.diff;

import java.io.File;
import java.util.List;

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
 * @date Oct 22, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class DiffChangeDistiller {

	public static void main(String[] args) {
		DiffChangeDistiller m = new DiffChangeDistiller();
		m.testBasic();
	}

	public void testBasic() {
		File left = new File("A1.java");
		File right = new File("A2.java");
		diff(left, right);
	}

	/**
	 * 
	 * @param javaText1
	 * @param javaText2
	 */
	public void diff(File javaText1, File javaText2) {
		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
		try {
			distiller.extractClassifiedSourceCodeChanges(javaText1, javaText2);
		} catch (Exception e) {
			/*
			 * An exception most likely indicates a bug in ChangeDistiller.
			 * Please file a bug report at
			 * https://bitbucket.org/sealuzh/tools-changedistiller/issues and
			 * attach the full stack trace along with the two files that you
			 * tried to distill.
			 */
			System.err.println("Warning: error while change distilling. " + e.getMessage());
		}

		List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
		if (changes != null) {
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
				System.out.println();
				getChangedInfo(newEntity, change);
				System.out.println("DBG__________________________________________");
			}
		}
	}

	/**
	 * 
	 * @param changedEntity
	 * @param change
	 */
	public void getChangedInfo(SourceCodeEntity changedEntity, SourceCodeChange change) {
		if (changedEntity == null) {
			return;
		}
		SourceRange changedRange = changedEntity.getSourceRange();
		String changeName = changedEntity.getUniqueName();
		// SourceCodeEntity parent = change.getParentEntity();
		// String parentName = parent.getUniqueName();
		// SourceRange parentChangedRange = parent.getSourceRange();
		System.out.println("[DBG] OFFSET: " + changedRange + ", CHANGE: [" + changeName + "]"//
				/*
				 * + ", PARENT: " + parent.getType() + parent.getSourceRange()
				 */);
	}
}
