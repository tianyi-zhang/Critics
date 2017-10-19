/*
 * @(#) UTEditDifferencer.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.edit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;

import ut.seal.plugins.utils.UTFile;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.SourceRange;
import ch.uzh.ifi.seal.changedistiller.model.entities.Delete;
import ch.uzh.ifi.seal.changedistiller.model.entities.Insert;
import ch.uzh.ifi.seal.changedistiller.model.entities.Move;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.model.entities.Update;

public class UTEditDifferencer {
	private ArrayList<UTAbstractEdit>	edits	= new ArrayList<UTAbstractEdit>();

	/**
	 * @param javaText1
	 * @param javaText2
	 *            Normal differencer without storing edits to Database
	 */
	public void diff(File javaText1, File javaText2) {
		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
		try {
			distiller.extractClassifiedSourceCodeChanges(javaText2, javaText1);
		} catch (Exception e) {
			/* An exception most likely indicates a bug in ChangeDistiller. Please file a
			   bug report at https://bitbucket.org/sealuzh/tools-changedistiller/issues and
			   attach the full stack trace along with the two files that you tried to distill. */
			System.err.println("Warning: error while change distilling. " + e.getMessage());
		}

		List<SourceCodeChange> changes = distiller.getSourceCodeChanges();

		// Convert sourceCodeChanges to UTEdit type
		if (changes != null) {
			for (int i = 0; i < changes.size(); i++) {
				int eid = generateUniqueId();
				SourceCodeChange change = changes.get(i);
				SourceCodeEntity newEntity = null, changedEntity = null;

				changedEntity = change.getChangedEntity();
				if (change instanceof Insert) {
					String leftContext = getContext(javaText1, changedEntity);
					UTAddEdit add = new UTAddEdit(eid, change, leftContext);
					edits.add(add);
				}
				if (change instanceof Delete) {
					String rightContext = getContext(javaText2, changedEntity);
					UTRemoveEdit delete = new UTRemoveEdit(eid, change, rightContext);
					edits.add(delete);
				}
				if (change instanceof Update) {
					changedEntity = change.getChangedEntity();
					newEntity = ((Update) change).getNewEntity();
					String leftContext = getContext(javaText1, newEntity);
					String rightContext = getContext(javaText2, changedEntity);
					UTUpdateEdit update = new UTUpdateEdit(eid, change, leftContext, rightContext);
					edits.add(update);
				}
				if (change instanceof Move) {
					changedEntity = change.getChangedEntity();
					newEntity = ((Move) change).getNewEntity();
					String leftContext = getContext(javaText1, newEntity);
					String rightContext = getContext(javaText2, changedEntity);
					UTUpdateEdit update = new UTUpdateEdit(eid, change, leftContext, rightContext);
					edits.add(update);
				}
			}
		}
	}

	public String getContext(File file, SourceCodeEntity entity) {
		String context = null;
		try {
			IDocument doc = new Document(UTFile.readEntireFile(file.getAbsolutePath()));
			SourceRange range = entity.getSourceRange();
			int offset = range.getStart();
			int length = range.getEnd() - offset + 1;
			context = doc.get(offset, length);
		} catch (IOException e) {
			System.out.println("Cannot load file to Document Object.");
			e.printStackTrace();
		} catch (BadLocationException e) {
			System.out.println("Illegal offset or length, cannot read strings from file");
			e.printStackTrace();
		}
		return context;
	}

	public ArrayList<UTAbstractEdit> getEdits() {
		return edits;
	}

	public static int generateUniqueId() {
		UUID idOne = UUID.randomUUID();
		String str = "" + idOne;
		int uid = str.hashCode();
		String filterStr = "" + uid;
		str = filterStr.replaceAll("-", "");
		return Integer.parseInt(str);
	}
}
