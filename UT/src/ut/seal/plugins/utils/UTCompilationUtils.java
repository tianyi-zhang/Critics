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
/*
 * @(#) UTCompilationUtils.java
 *
 */
package ut.seal.plugins.utils;

/*
 * #%L
 * ChangeDistiller
 * %%
 * Copyright (C) 2011 - 2013 Software Architecture and Evolution Lab, Department of Informatics, UZH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.core.util.CommentRecorderParser;

import ch.uzh.ifi.seal.changedistiller.ast.java.Comment;
import ch.uzh.ifi.seal.changedistiller.ast.java.CommentCollector;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;

/**
 * @modifiedAuthor Myoungkyu Song
 * @date Nov 24, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public final class UTCompilationUtils {

	private static final String	TEST_DATA_BASE_DIR	= "resources/testdata/";

	private UTCompilationUtils() {
	}

	public static JavaCompilation compileSource(String source) {
		CompilerOptions options = getDefaultCompilerOptions();
		Parser parser = createCommentRecorderParser(options);
		ICompilationUnit cu = createCompilationunit(source, "");
		CompilationResult compilationResult = createDefaultCompilationResult(cu, options);
		return new JavaCompilation(parser.parse(cu, compilationResult), parser.scanner);
	}

	public static JavaCompilation compileSource(org.eclipse.jdt.core.dom.CompilationUnit cunit) {
		CompilerOptions options = getDefaultCompilerOptions();
		Parser parser = createCommentRecorderParser(options);
		String source = cunit.toString();
		ICompilationUnit cu = createCompilationunit(source, "");
		CompilationResult compilationResult = createDefaultCompilationResult(cu, options);
		return new JavaCompilation(parser.parse(cu, compilationResult), parser.scanner);
	}

	public static JavaCompilation compileSource(org.eclipse.jdt.core.ICompilationUnit cunit) {
		CompilerOptions options = getDefaultCompilerOptions();
		Parser parser = createCommentRecorderParser(options);
		String source = cunit.toString();
		ICompilationUnit cu = createCompilationunit(source, "");
		CompilationResult compilationResult = createDefaultCompilationResult(cu, options);
		return new JavaCompilation(parser.parse(cu, compilationResult), parser.scanner);
	}

	private static CompilationResult createDefaultCompilationResult(ICompilationUnit cu, CompilerOptions options) {
		CompilationResult compilationResult = new CompilationResult(cu, 0, 0, options.maxProblemsPerUnit);
		return compilationResult;
	}

	private static ICompilationUnit createCompilationunit(String source, String filename) {
		ICompilationUnit cu = new CompilationUnit(source.toCharArray(), filename, null);
		return cu;
	}

	private static Parser createCommentRecorderParser(CompilerOptions options) {
		Parser parser = new CommentRecorderParser(new ProblemReporter(DefaultErrorHandlingPolicies.proceedWithAllProblems(),
				options, new DefaultProblemFactory()), false);
		return parser;
	}

	private static CompilerOptions getDefaultCompilerOptions() {
		CompilerOptions options = new CompilerOptions();
		options.docCommentSupport = true;
		options.complianceLevel = ClassFileConstants.JDK1_6;
		options.sourceLevel = ClassFileConstants.JDK1_6;
		options.targetJDK = ClassFileConstants.JDK1_6;
		return options;
	}

	/**
	 * Returns the generated {@link JavaCompilation} from the file identified by the given filename. This method assumes
	 * that the filename is relative to <code>{@value #TEST_DATA_BASE_DIR}</code>.
	 * 
	 * @param filename
	 *            of the file to compile (relative to {@value #TEST_DATA_BASE_DIR}).
	 * @return the compilation of the file
	 */
	public static JavaCompilation compileFile(String filename) {
		CompilerOptions options = getDefaultCompilerOptions();
		Parser parser = createCommentRecorderParser(options);
		ICompilationUnit cu = createCompilationunit(getContentOfFile(TEST_DATA_BASE_DIR + filename), filename);
		CompilationResult compilationResult = createDefaultCompilationResult(cu, options);
		return new JavaCompilation(parser.parse(cu, compilationResult), parser.scanner);
	}

	private static String getContentOfFile(String filename) {
		char[] b = new char[1024];
		StringBuilder sb = new StringBuilder();
		try {
			FileReader reader = new FileReader(new File(filename));
			int n = reader.read(b);
			while (n > 0) {
				sb.append(b, 0, n);
				n = reader.read(b);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static List<Comment> extractComments(JavaCompilation sCompilationUnit) {
		CommentCollector collector = new CommentCollector(sCompilationUnit.getCompilationUnit(), sCompilationUnit.getSource());
		collector.collect();
		return collector.getComments();
	}

	public static AbstractMethodDeclaration findMethod(CompilationUnitDeclaration cu, String methodName) {
		for (TypeDeclaration type : cu.types) {
			for (AbstractMethodDeclaration method : type.methods) {
				if (String.valueOf(method.selector).equals(methodName)) {
					return method;
				}
			}
		}
		return null;
	}

	public static FieldDeclaration findField(CompilationUnitDeclaration cu, String fieldName) {
		for (TypeDeclaration type : cu.types) {
			for (FieldDeclaration field : type.fields) {
				if (String.valueOf(field.name).equals(fieldName)) {
					return field;
				}
			}
		}
		return null;
	}

	public static TypeDeclaration findType(CompilationUnitDeclaration cu, String typeName) {
		for (TypeDeclaration type : cu.types) {
			if(type.memberTypes != null){
				for (TypeDeclaration memberType : type.memberTypes) {
					if (String.valueOf(memberType.name).equals(typeName)) {
						return memberType;
					}
				}
			}else{
				if (String.valueOf(type.name).equals(typeName)) {
					return type;
				}
			}
			
		}
		return null;
	}

	public static File getFile(String filename) {
		return new File(TEST_DATA_BASE_DIR + filename);
	}

}
