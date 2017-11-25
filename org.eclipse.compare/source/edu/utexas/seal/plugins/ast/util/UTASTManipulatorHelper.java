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
