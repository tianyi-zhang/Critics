/*
 * @(#) UTASTManipulatorHelper.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.ast.util;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

/**
 * @author Myoungkyu Song
 * @date Oct 27, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTASTManipulatorHelper {
	private UTASTManipulatorHelper() {
	}

	/**
	 * Implementing classes provide a {@link TextEdit} object. The way to obtain
	 * such an instance differs when the AST is directly manipulated or changes
	 * are logged to an {@link ASTRewrite} instance. To avoid to write two save
	 * methods, this interface has been created.
	 */
	public interface TextEditProvider {

		/**
		 * Provides a {@link TextEdit} document.
		 * 
		 * @param document
		 *            the document the {@link TextEdit} object will be applied
		 * @return the {@link TextEdit} instance
		 */
		TextEdit getTextEdit(IDocument document);
	}

	/**
	 * Writes changes to Java source. This method does not distinguish how the
	 * AST was changed.
	 * 
	 * @param unit
	 *            the AST node of the compilation unit that has been changed
	 * @param textEditProvider
	 *            provides the change information
	 * @throws CoreException
	 *             thrown if file paths cannot be connected or disconnected
	 */
	@SuppressWarnings("deprecation")
	public static void save(CompilationUnit unit, TextEditProvider textEditProvider) throws CoreException {
		ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
		IPath path = unit.getJavaElement().getPath();
		try {
			bufferManager.connect(path, null);
			ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(path);
			IDocument document = textFileBuffer.getDocument();
			TextEdit edit = textEditProvider.getTextEdit(document);
			edit.apply(document);
			textFileBuffer.commit(null, false);
		} catch (MalformedTreeException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		} finally {
			bufferManager.disconnect(path, null);
		}
	}

	/**
	 * saves changes that have been made directly to an AST.
	 * 
	 * @param unit
	 *            the unit that contains changes.
	 * @throws CoreException
	 */
	public static void saveDirectlyModifiedUnit(final CompilationUnit unit) throws CoreException {
		save(unit, new TextEditProvider() {
			public TextEdit getTextEdit(IDocument document) {
				return unit.rewrite(document, null);
			}
		});
	}

	/**
	 * saves changes to an AST that have been recorded in by an instance of {@link ASTRewrite}.
	 * 
	 * @param unit
	 *            the unit (in its original state)
	 * @param rewrite
	 *            contains the rewrite instructions
	 * @throws CoreException
	 */
	public static void saveASTRewriteContents(CompilationUnit unit, final ASTRewrite rewrite) throws CoreException {
		save(unit, new TextEditProvider() {
			public TextEdit getTextEdit(IDocument document) {
				return rewrite.rewriteAST(document, null);
			}
		});

	}

}
