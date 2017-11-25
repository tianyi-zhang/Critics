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
package edu.utexas.seal.plugins.database;

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
import edu.utexas.seal.plugins.edit.UTAbstractEdit;
import edu.utexas.seal.plugins.edit.UTAddEdit;
import edu.utexas.seal.plugins.edit.UTRemoveEdit;
import edu.utexas.seal.plugins.edit.UTUpdateEdit;

public class UTChangeDistillerDB {
	private UTCriticsDBServer			server;
	private ArrayList<UTAbstractEdit>	edits	= new ArrayList<UTAbstractEdit>();

	public UTChangeDistillerDB(UTCriticsDBServer s) {
		this.server = s;
	}

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
					server.insertOldContext(add);
					server.insertNewContext(add);
					server.insertEdit(add, javaText1.getAbsolutePath(), javaText2.getAbsolutePath());
				}
				if (change instanceof Delete) {
					String rightContext = getContext(javaText2, changedEntity);
					UTRemoveEdit delete = new UTRemoveEdit(eid, change, rightContext);
					edits.add(delete);
					server.insertNewContext(delete);
					server.insertOldContext(delete);
					server.insertEdit(delete, javaText1.getAbsolutePath(), javaText2.getAbsolutePath());
				}
				if (change instanceof Update) {
					changedEntity = change.getChangedEntity();
					newEntity = ((Update) change).getNewEntity();
					String leftContext = getContext(javaText1, newEntity);
					String rightContext = getContext(javaText2, changedEntity);
					UTUpdateEdit update = new UTUpdateEdit(eid, change, leftContext, rightContext);
					edits.add(update);
					server.insertOldContext(update);
					server.insertNewContext(update);
					server.insertEdit(update, javaText1.getAbsolutePath(), javaText2.getAbsolutePath());
				}
				if (change instanceof Move) {
					changedEntity = change.getChangedEntity();
					newEntity = ((Move) change).getNewEntity();
					String leftContext = getContext(javaText1, newEntity);
					String rightContext = getContext(javaText2, changedEntity);
					UTUpdateEdit update = new UTUpdateEdit(eid, change, leftContext, rightContext);
					edits.add(update);
					server.insertOldContext(update);
					server.insertNewContext(update);
					server.insertEdit(update, javaText1.getAbsolutePath(), javaText2.getAbsolutePath());
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
