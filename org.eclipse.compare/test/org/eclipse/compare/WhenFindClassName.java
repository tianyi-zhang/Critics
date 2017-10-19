package org.eclipse.compare;

import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.junit.Test;

import edu.utexas.seal.plugins.util.visitor.UTGeneralVisitor;
import edu.utexas.seal.plugins.util.visitor.UTIGeneralVisitor;
import ut.seal.plugins.utils.UTFile;
import ut.seal.plugins.utils.ast.UTASTParser;

public class WhenFindClassName {

	@Test
	public void testWhenFindClassName() {
		UTIGeneralVisitor<MethodDeclaration> mVisitor = new UTGeneralVisitor<MethodDeclaration>() {
			public boolean visit(MethodDeclaration node) {
				results.add(node);
				return true;
			}
		};

		String fileName = "/Users/mksong/workspaceCritics/org.eclipse.compare/test/org/eclipse/compare/TestClassA.java";
		String fileContents = UTFile.getContents(fileName);
		UTASTParser astParser = new UTASTParser();
		CompilationUnit parser = astParser.parse(fileContents);

		List<MethodDeclaration> lstMethods = mVisitor.getResults();
		lstMethods.clear();
		parser.accept(mVisitor);
	}
}
