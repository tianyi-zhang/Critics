package ut.seal.plugins.utils.ast;

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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.swt.graphics.Point;
import org.junit.Ignore;
import org.junit.Test;

import ut.seal.plugins.utils.UTCompilationUtils;
import ut.seal.plugins.utils.UTFile;
import ut.seal.plugins.utils.visitor.UTGeneralVisitor;
import ut.seal.plugins.utils.visitor.UTIGeneralVisitor;
import ch.uzh.ifi.seal.changedistiller.ast.java.Comment;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaMethodBodyConverter;
import ch.uzh.ifi.seal.changedistiller.distilling.Distiller;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.ChangeType;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.SourceRange;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.model.entities.StructureEntityVersion;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ch.uzh.ifi.seal.changedistiller.util.CompilationUtils;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerAbstract;

public class UTASTNodeConverter extends UTASTNodeCoverterAbstract {

	private static final String		METHOD_NAME	= "method";

	private String					fLeftSnippet;
	private String					fRightSnippet;
	private StructureEntityVersion	structureEntity;

	/**
	 * Instantiates a new uTAST node converter.
	 */
	public UTASTNodeConverter() {
		try {
			initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adding attribute modifiability should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void addingAttributeModifiabilityShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("private final String fAddingAttributeModifiability;");
		fRightSnippet = createSourceCode("private String fAddingAttributeModifiability;");
		extractFieldDeclarationChanges("fAddingAttributeModifiability");
		assertThat(getResultingChangeType(), is(ChangeType.ADDING_ATTRIBUTE_MODIFIABILITY));
	}

	/**
	 * Adding class derivability should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void addingClassDerivabilityShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("public final class AddingClassDerivability {}");
		fRightSnippet = createSourceCode("public class AddingClassDerivability {}");
		extractClassDeclarationChanges("AddingClassDerivability");
		assertThat(getResultingChangeType(), is(ChangeType.ADDING_CLASS_DERIVABILITY));
	}

	/**
	 * Adding method overrideability should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void addingMethodOverrideabilityShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("public final void addingMethodOverridability() {}");
		fRightSnippet = createSourceCode("public void addingMethodOverridability() {}");
		extractMethodDeclarationChanges("addingMethodOverridability");
		assertThat(getResultingChangeType(), is(ChangeType.ADDING_METHOD_OVERRIDABILITY));
	}

	/**
	 * Additional class should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	@Ignore("not yet implemented")
	public void additionalClassShouldBeDetected() throws Exception {
	}

	/**
	 * Additional functionality should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	@Ignore("not yet implemented")
	public void additionalFunctionalityShouldBeDetected() throws Exception {
	}

	/**
	 * Additional object state should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	@Ignore("not yet implemented")
	public void additionalObjectStateShouldBeDetected() throws Exception {
	}

	/**
	 * Alternative part delete should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void alternativePartDeleteShouldBeDetected() throws Exception {
		fLeftSnippet = createMethodSourceCode("if (alternativePartDelete == elseDelete) { alternativePartDelete(elseDelete); } else { }");
		fRightSnippet = createMethodSourceCode("if (alternativePartDelete == elseDelete) { alternativePartDelete(elseDelete); }");
		extractMethodChanges("method");
		assertThat(getResultingChangeType(), is(ChangeType.ALTERNATIVE_PART_DELETE));
	}

	/**
	 * Alternative part insert should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void alternativePartInsertShouldBeDetected() throws Exception {
		fLeftSnippet = createMethodSourceCode("if (alternativePartInsert == elseInsert) { alternativePartInsert(elseInsert); }");
		fRightSnippet = createMethodSourceCode("if (alternativePartInsert == elseInsert) { alternativePartInsert(elseInsert); } else { }");
		extractMethodChanges("method");
		assertThat(getResultingChangeType(), is(ChangeType.ALTERNATIVE_PART_INSERT));
	}

	/**
	 * For init insert should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void forInitInsertShouldBeDetected() throws Exception {
		fLeftSnippet = createMethodSourceCode("for(; i < 10; i++) { System.out.println(i); }");
		fRightSnippet = createMethodSourceCode("for(int i = 0; i < 10; i++) { System.out.println(i); }");
		extractMethodChanges("method");
		assertThat(getResultingChangeType(), is(ChangeType.STATEMENT_INSERT));
	}

	/**
	 * For init delete should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void forInitDeleteShouldBeDetected() throws Exception {
		fLeftSnippet = createMethodSourceCode("for(int i = 0; i < 10; i++) { System.out.println(i); }");
		fRightSnippet = createMethodSourceCode("for( ; i < 10; i++) { System.out.println(i); }");
		extractMethodChanges("method");
		assertThat(getResultingChangeType(), is(ChangeType.STATEMENT_DELETE));
	}

	/**
	 * For increment insert should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void forIncrementInsertShouldBeDetected() throws Exception {
		fLeftSnippet = createMethodSourceCode("for(int i = 0; i < 10;) { System.out.println(i); }");
		fRightSnippet = createMethodSourceCode("for(int i = 0; i < 10; i++) { System.out.println(i); }");
		extractMethodChanges("method");
		assertThat(getResultingChangeType(), is(ChangeType.STATEMENT_INSERT));
	}

	/**
	 * For increment delete should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void forIncrementDeleteShouldBeDetected() throws Exception {
		fLeftSnippet = createMethodSourceCode("for(int i = 0; i < 10; i++) { System.out.println(i); }");
		fRightSnippet = createMethodSourceCode("for(int i = 0; i < 10;) { System.out.println(i); }");
		extractMethodChanges("method");
		assertThat(getResultingChangeType(), is(ChangeType.STATEMENT_DELETE));
	}

	/**
	 * Statement insert into switch should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void statementInsertIntoSwitchShouldBeDetected() throws Exception {
		fLeftSnippet = createMethodSourceCode("switch(var) { case 1: break; default: System.out.println(\"default\"); }");
		fRightSnippet = createMethodSourceCode("switch(var) { case 1: System.out.println(\"first case\"); break; default: System.out.println(\"default\"); }");
		extractMethodChanges("method");
		assertThat(getResultingChangeType(), is(ChangeType.STATEMENT_INSERT));
	}

	/**
	 * Attribute renaming should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	@Ignore("not yet implemented")
	public void attributeRenamingShouldBeDetected() throws Exception {
	}

	/**
	 * Attribute type change should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void attributeTypeChangeShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("private Integer attributeTypeChange;");
		fRightSnippet = createSourceCode("private Object attributeTypeChange;");
		extractFieldDeclarationChanges("attributeTypeChange");
		assertThat(getResultingChangeType(), is(ChangeType.ATTRIBUTE_TYPE_CHANGE));
	}

	/**
	 * Comment delete should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void commentDeleteShouldBeDetected() throws Exception {
		fLeftSnippet = createMethodSourceCode("// comment delete\ncomment.delete();");
		fRightSnippet = createMethodSourceCode("comment.delete();");
		extractMethodChanges("method");
		assertThat(getResultingChangeType(), is(ChangeType.COMMENT_DELETE));
	}

	/**
	 * Comment insert should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void commentInsertShouldBeDetected() throws Exception {
		fLeftSnippet = createMethodSourceCode("comment.insert();");
		fRightSnippet = createMethodSourceCode("// comment insert\ncomment.insert();");
		extractMethodChanges("method");
		assertThat(getResultingChangeType(), is(ChangeType.COMMENT_INSERT));
	}

	/**
	 * Comment move should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void commentMoveShouldBeDetected() throws Exception {
		fLeftSnippet = createMethodSourceCode("if (commentMoveFrom) { /* comment move */ comment.moveFrom(); } if (commentMoveTo) { comment.moveTo(); }");
		fRightSnippet = createMethodSourceCode("if (commentMoveFrom) { comment.moveFrom(); } if (commentMoveTo) { /* comment move */ comment.moveTo(); }");
		extractMethodChanges("method");
		assertThat(getResultingChangeType(), is(ChangeType.COMMENT_MOVE));
	}

	/**
	 * Comment update should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void commentUpdateShouldBeDetected() throws Exception {
		fLeftSnippet = createMethodSourceCode("/* comment that will be updated */ comment.update();");
		fRightSnippet = createMethodSourceCode("/* comment that was updated */ comment.update();");
		extractMethodChanges("method");
		assertThat(getResultingChangeType(), is(ChangeType.COMMENT_UPDATE));
	}

	/**
	 * Condition expression change should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void conditionExpressionChangeShouldBeDetected() throws Exception {
		fLeftSnippet = createMethodSourceCode("if (conditioExpresionChang) {}");
		fRightSnippet = createMethodSourceCode("if (conditionExpressionChange) {}");
		extractMethodChanges("method");
		assertThat(getResultingChangeType(), is(ChangeType.CONDITION_EXPRESSION_CHANGE));
	}

	/**
	 * Decreasing class accessiblity change from public to protected should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void decreasingClassAccessiblityChangeFromPublicToProtectedShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("public class DecreasingAccessibilityChangeFromPublicToProtected {}");
		fRightSnippet = createSourceCode("protected class DecreasingAccessibilityChangeFromPublicToProtected {}");
		extractClassDeclarationChanges("DecreasingAccessibilityChangeFromPublicToProtected");
		assertThat(getResultingChangeType(), is(ChangeType.DECREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Decreasing class accessiblity change from public to package should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void decreasingClassAccessiblityChangeFromPublicToPackageShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("public class DecreasingAccessibilityChangeFromPublicToPackage {}");
		fRightSnippet = createSourceCode("class DecreasingAccessibilityChangeFromPublicToPackage {}");
		extractClassDeclarationChanges("DecreasingAccessibilityChangeFromPublicToPackage");
		assertThat(getResultingChangeType(), is(ChangeType.DECREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Decreasing class accessiblity change from public to private should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void decreasingClassAccessiblityChangeFromPublicToPrivateShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("public class DecreasingAccessibilityChangeFromPublicToPrivate {}");
		fRightSnippet = createSourceCode("private class DecreasingAccessibilityChangeFromPublicToPrivate {}");
		extractClassDeclarationChanges("DecreasingAccessibilityChangeFromPublicToPrivate");
		assertThat(getResultingChangeType(), is(ChangeType.DECREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Decreasing class accessiblity change from protected to package should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void decreasingClassAccessiblityChangeFromProtectedToPackageShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("protected class DecreasingAccessibilityChangeFromProtectedToPackage {}");
		fRightSnippet = createSourceCode("class DecreasingAccessibilityChangeFromProtectedToPackage {}");
		extractClassDeclarationChanges("DecreasingAccessibilityChangeFromProtectedToPackage");
		assertThat(getResultingChangeType(), is(ChangeType.DECREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Decreasing class accessiblity change from protected to private should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void decreasingClassAccessiblityChangeFromProtectedToPrivateShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("protected class DecreasingAccessibilityChangeFromProtectedToPrivate {}");
		fRightSnippet = createSourceCode("private class DecreasingAccessibilityChangeFromProtectedToPrivate {}");
		extractClassDeclarationChanges("DecreasingAccessibilityChangeFromProtectedToPrivate");
		assertThat(getResultingChangeType(), is(ChangeType.DECREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Decreasing class accessiblity change from package to private should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void decreasingClassAccessiblityChangeFromPackageToPrivateShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("class DecreasingAccessibilityChangeFromPackageToPrivate {}");
		fRightSnippet = createSourceCode("private class DecreasingAccessibilityChangeFromPackageToPrivate {}");
		extractClassDeclarationChanges("DecreasingAccessibilityChangeFromPackageToPrivate");
		assertThat(getResultingChangeType(), is(ChangeType.DECREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Decreasing method accessiblity change from public to protected should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void decreasingMethodAccessiblityChangeFromPublicToProtectedShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("public void decreasingAccessibilityChangeFromPublicToProtected() {}");
		fRightSnippet = createSourceCode("protected void decreasingAccessibilityChangeFromPublicToProtected() {}");
		extractMethodDeclarationChanges("decreasingAccessibilityChangeFromPublicToProtected");
		assertThat(getResultingChangeType(), is(ChangeType.DECREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Decreasing method accessiblity change from public to package should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void decreasingMethodAccessiblityChangeFromPublicToPackageShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("public void decreasingAccessibilityChangeFromPublicToPackage() {}");
		fRightSnippet = createSourceCode("void decreasingAccessibilityChangeFromPublicToPackage() {}");
		extractMethodDeclarationChanges("decreasingAccessibilityChangeFromPublicToPackage");
		assertThat(getResultingChangeType(), is(ChangeType.DECREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Decreasing method accessiblity change from public to private should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void decreasingMethodAccessiblityChangeFromPublicToPrivateShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("public void decreasingAccessibilityChangeFromPublicToPrivate() {}");
		fRightSnippet = createSourceCode("private void decreasingAccessibilityChangeFromPublicToPrivate() {}");
		extractMethodDeclarationChanges("decreasingAccessibilityChangeFromPublicToPrivate");
		assertThat(getResultingChangeType(), is(ChangeType.DECREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Decreasing method accessiblity change from protected to package should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void decreasingMethodAccessiblityChangeFromProtectedToPackageShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("protected void decreasingAccessibilityChangeFromProtectedToPackage() {}");
		fRightSnippet = createSourceCode("void decreasingAccessibilityChangeFromProtectedToPackage() {}");
		extractMethodDeclarationChanges("decreasingAccessibilityChangeFromProtectedToPackage");
		assertThat(getResultingChangeType(), is(ChangeType.DECREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Decreasing method accessiblity change from protected to private should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void decreasingMethodAccessiblityChangeFromProtectedToPrivateShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("protected void decreasingAccessibilityChangeFromProtectedToPrivate() {}");
		fRightSnippet = createSourceCode("private void decreasingAccessibilityChangeFromProtectedToPrivate() {}");
		extractMethodDeclarationChanges("decreasingAccessibilityChangeFromProtectedToPrivate");
		assertThat(getResultingChangeType(), is(ChangeType.DECREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Decreasing method accessiblity change from package to private should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void decreasingMethodAccessiblityChangeFromPackageToPrivateShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("void decreasingAccessibilityChangeFromPackageToPrivate() {}");
		fRightSnippet = createSourceCode("private void decreasingAccessibilityChangeFromPackageToPrivate() {}");
		extractMethodDeclarationChanges("decreasingAccessibilityChangeFromPackageToPrivate");
		assertThat(getResultingChangeType(), is(ChangeType.DECREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Decreasing field accessibility change from public to protected should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void decreasingFieldAccessibilityChangeFromPublicToProtectedShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("public Object fDecreasingAccessibilityChangeFromPublicToProtected;");
		fRightSnippet = createSourceCode("protected Object fDecreasingAccessibilityChangeFromPublicToProtected;");
		extractFieldDeclarationChanges("fDecreasingAccessibilityChangeFromPublicToProtected");
		assertThat(getResultingChangeType(), is(ChangeType.DECREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Decreasing field accessibility change from public to package should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void decreasingFieldAccessibilityChangeFromPublicToPackageShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("public Object fDecreasingAccessibilityChangeFromPublicToPackage;");
		fRightSnippet = createSourceCode("Object fDecreasingAccessibilityChangeFromPublicToPackage;");
		extractFieldDeclarationChanges("fDecreasingAccessibilityChangeFromPublicToPackage");
		assertThat(getResultingChangeType(), is(ChangeType.DECREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Decreasing field accessibility change from public to private should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void decreasingFieldAccessibilityChangeFromPublicToPrivateShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("public Object fDecreasingAccessibilityChangeFromPublicToPrivate;");
		fRightSnippet = createSourceCode("private Object fDecreasingAccessibilityChangeFromPublicToPrivate;");
		extractFieldDeclarationChanges("fDecreasingAccessibilityChangeFromPublicToPrivate");
		assertThat(getResultingChangeType(), is(ChangeType.DECREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Decreasing field accessibility change from protected to package should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void decreasingFieldAccessibilityChangeFromProtectedToPackageShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("protected Object fDecreasingAccessibilityChangeFromProtectedToPackage;");
		fRightSnippet = createSourceCode("Object fDecreasingAccessibilityChangeFromProtectedToPackage;");
		extractFieldDeclarationChanges("fDecreasingAccessibilityChangeFromProtectedToPackage");
		assertThat(getResultingChangeType(), is(ChangeType.DECREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Decreasing field accessibility change from protected to private should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void decreasingFieldAccessibilityChangeFromProtectedToPrivateShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("protected Object fDecreasingAccessibilityChangeFromProtectedToPrivate;");
		fRightSnippet = createSourceCode("private Object fDecreasingAccessibilityChangeFromProtectedToPrivate;");
		extractFieldDeclarationChanges("fDecreasingAccessibilityChangeFromProtectedToPrivate");
		assertThat(getResultingChangeType(), is(ChangeType.DECREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Decreasing field accessibility change from package to private should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void decreasingFieldAccessibilityChangeFromPackageToPrivateShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("Object fDecreasingAccessibilityChangeFromPackageToPrivate;");
		fRightSnippet = createSourceCode("private Object fDecreasingAccessibilityChangeFromPackageToPrivate;");
		extractFieldDeclarationChanges("fDecreasingAccessibilityChangeFromPackageToPrivate");
		assertThat(getResultingChangeType(), is(ChangeType.DECREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Class renaming should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	@Ignore("not yet implemented")
	public void classRenamingShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("class ClssRenamin {}");
		fRightSnippet = createSourceCode("class ClassRenaming {}");
		extractClassDeclarationChanges("ClassRenaming");
		assertThat(getResultingChangeType(), is(ChangeType.CLASS_RENAMING));
	}

	/**
	 * Class doc delete should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void classDocDeleteShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("/**\n* Doc to delete\n*/\npublic class ClassToCheckDocDelete {}");
		fRightSnippet = createSourceCode("public class ClassToCheckDocDelete {}");
		extractClassDeclarationChanges("ClassToCheckDocDelete");
		assertThat(getResultingChangeType(), is(ChangeType.DOC_DELETE));
	}

	/**
	 * Class doc insert should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void classDocInsertShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("public class ClassToCheckDocInsert {}");
		fRightSnippet = createSourceCode("/**\n* Doc to insert\n*/\npublic class ClassToCheckDocInsert {}");
		extractClassDeclarationChanges("ClassToCheckDocInsert");
		assertThat(getResultingChangeType(), is(ChangeType.DOC_INSERT));
	}

	/**
	 * Class doc update should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void classDocUpdateShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("/**\n* Doc that will be updated\n*/\npublic class ClassToCheckDocUpdate {}");
		fRightSnippet = createSourceCode("/**\n* Doc that was updated\n*/\npublic class ClassToCheckDocUpdate {}");
		extractClassDeclarationChanges("ClassToCheckDocUpdate");
		assertThat(getResultingChangeType(), is(ChangeType.DOC_UPDATE));
	}

	/**
	 * Method doc delete should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void methodDocDeleteShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("/**\n* Doc to delete\n*/\npublic void methodToCheckDocDelete() {}");
		fRightSnippet = createSourceCode("public void methodToCheckDocDelete() {}");
		extractMethodDeclarationChanges("methodToCheckDocDelete");
		assertThat(getResultingChangeType(), is(ChangeType.DOC_DELETE));
	}

	/**
	 * Method doc insert should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void methodDocInsertShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("public void methodToCheckDocInsert() {}");
		fRightSnippet = createSourceCode("/**\n* Doc to insert\n*/\npublic void methodToCheckDocInsert() {}");
		extractMethodDeclarationChanges("methodToCheckDocInsert");
		assertThat(getResultingChangeType(), is(ChangeType.DOC_INSERT));
	}

	/**
	 * Method doc update should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void methodDocUpdateShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("/**\n* Doc that will be updated\n*/\npublic void methodToCheckDocUpdate() {}");
		fRightSnippet = createSourceCode("/**\n* Doc that was updated\n*/\npublic void methodToCheckDocUpdate() {}");
		extractMethodDeclarationChanges("methodToCheckDocUpdate");
		assertThat(getResultingChangeType(), is(ChangeType.DOC_UPDATE));
	}

	/**
	 * Field doc delete should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void fieldDocDeleteShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("/**\n* Doc to delete\n*/\nprivate Object fAttributeToCheckDocDelete;");
		fRightSnippet = createSourceCode("private Object fAttributeToCheckDocDelete;");
		extractFieldDeclarationChanges("fAttributeToCheckDocDelete");
		assertThat(getResultingChangeType(), is(ChangeType.DOC_DELETE));
	}

	/**
	 * Field doc insert should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void fieldDocInsertShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("private Object fAttributeToCheckDocInsert;");
		fRightSnippet = createSourceCode("/**\n* Doc to insert\n*/\nprivate Object fAttributeToCheckDocInsert;");
		extractFieldDeclarationChanges("fAttributeToCheckDocInsert");
		assertThat(getResultingChangeType(), is(ChangeType.DOC_INSERT));
	}

	/**
	 * Field doc update should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void fieldDocUpdateShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("/**\n* Doc that will be updated\n*/\nprivate Object fAttributeToCheckDocUpdate;");
		fRightSnippet = createSourceCode("/**\n* Doc that was updated\n*/\nprivate Object fAttributeToCheckDocUpdate;");
		extractFieldDeclarationChanges("fAttributeToCheckDocUpdate");
		assertThat(getResultingChangeType(), is(ChangeType.DOC_UPDATE));
	}

	/**
	 * Increasing class accessiblity change from private to protected should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void increasingClassAccessiblityChangeFromPrivateToProtectedShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("private class IncreasingAccessibilityChangeFromPrivateToProtected {}");
		fRightSnippet = createSourceCode("protected class IncreasingAccessibilityChangeFromPrivateToProtected {}");
		extractClassDeclarationChanges("IncreasingAccessibilityChangeFromPrivateToProtected");
		assertThat(getResultingChangeType(), is(ChangeType.INCREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Increasing class accessiblity change from private to package should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void increasingClassAccessiblityChangeFromPrivateToPackageShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("private class IncreasingAccessibilityChangeFromPrivateToPackage {}");
		fRightSnippet = createSourceCode("class IncreasingAccessibilityChangeFromPrivateToPackage {}");
		extractClassDeclarationChanges("IncreasingAccessibilityChangeFromPrivateToPackage");
		assertThat(getResultingChangeType(), is(ChangeType.INCREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Increasing class accessiblity change from private to public should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void increasingClassAccessiblityChangeFromPrivateToPublicShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("private class IncreasingAccessibilityChangeFromPrivateToPublic {}");
		fRightSnippet = createSourceCode("public class IncreasingAccessibilityChangeFromPrivateToPublic {}");
		extractClassDeclarationChanges("IncreasingAccessibilityChangeFromPrivateToPublic");
		assertThat(getResultingChangeType(), is(ChangeType.INCREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Increasing class accessiblity change from package to protected should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void increasingClassAccessiblityChangeFromPackageToProtectedShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("class IncreasingAccessibilityChangeFromPackageToProtected {}");
		fRightSnippet = createSourceCode("protected class IncreasingAccessibilityChangeFromPackageToProtected {}");
		extractClassDeclarationChanges("IncreasingAccessibilityChangeFromPackageToProtected");
		assertThat(getResultingChangeType(), is(ChangeType.INCREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Increasing class accessiblity change from package to public should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void increasingClassAccessiblityChangeFromPackageToPublicShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("class IncreasingAccessibilityChangeFromPackageToPublic {}");
		fRightSnippet = createSourceCode("public class IncreasingAccessibilityChangeFromPackageToPublic {}");
		extractClassDeclarationChanges("IncreasingAccessibilityChangeFromPackageToPublic");
		assertThat(getResultingChangeType(), is(ChangeType.INCREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Increasing class accessiblity change from protected to public should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void increasingClassAccessiblityChangeFromProtectedToPublicShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("protected class IncreasingAccessibilityChangeFromProtectedToProtected {}");
		fRightSnippet = createSourceCode("public class IncreasingAccessibilityChangeFromProtectedToProtected {}");
		extractClassDeclarationChanges("IncreasingAccessibilityChangeFromProtectedToProtected");
		assertThat(getResultingChangeType(), is(ChangeType.INCREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Increasing field accessibility change from private to package should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void increasingFieldAccessibilityChangeFromPrivateToPackageShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("private Object fIncreasingAccessibilityChangeFromPrivateToPackage;");
		fRightSnippet = createSourceCode("Object fIncreasingAccessibilityChangeFromPrivateToPackage;");
		extractFieldDeclarationChanges("fIncreasingAccessibilityChangeFromPrivateToPackage");
		assertThat(getResultingChangeType(), is(ChangeType.INCREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Increasing field accessibility change from private to protected should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void increasingFieldAccessibilityChangeFromPrivateToProtectedShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("private Object fIncreasingAccessibilityChangeFromPrivateToProtected;");
		fRightSnippet = createSourceCode("protected Object fIncreasingAccessibilityChangeFromPrivateToProtected;");
		extractFieldDeclarationChanges("fIncreasingAccessibilityChangeFromPrivateToProtected");
		assertThat(getResultingChangeType(), is(ChangeType.INCREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Increasing field accessibility change from private to public should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void increasingFieldAccessibilityChangeFromPrivateToPublicShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("private Object fIncreasingAccessibilityChangeFromPrivateToPublic;");
		fRightSnippet = createSourceCode("public Object fIncreasingAccessibilityChangeFromPrivateToPublic;");
		extractFieldDeclarationChanges("fIncreasingAccessibilityChangeFromPrivateToPublic");
		assertThat(getResultingChangeType(), is(ChangeType.INCREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Increasing field accessibility change from package to protected should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void increasingFieldAccessibilityChangeFromPackageToProtectedShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("Object fIncreasingAccessibilityChangeFromPackageToProtected;");
		fRightSnippet = createSourceCode("protected Object fIncreasingAccessibilityChangeFromPackageToProtected;");
		extractFieldDeclarationChanges("fIncreasingAccessibilityChangeFromPackageToProtected");
		assertThat(getResultingChangeType(), is(ChangeType.INCREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Increasing field accessibility change from package to public should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void increasingFieldAccessibilityChangeFromPackageToPublicShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("Object fIncreasingAccessibilityChangeFromPackageToPublic;");
		fRightSnippet = createSourceCode("public Object fIncreasingAccessibilityChangeFromPackageToPublic;");
		extractFieldDeclarationChanges("fIncreasingAccessibilityChangeFromPackageToPublic");
		assertThat(getResultingChangeType(), is(ChangeType.INCREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Increasing field accessibility change from protected to public should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void increasingFieldAccessibilityChangeFromProtectedToPublicShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("Object fIncreasingAccessibilityChangeFromProtectedToPublic;");
		fRightSnippet = createSourceCode("public Object fIncreasingAccessibilityChangeFromProtectedToPublic;");
		extractFieldDeclarationChanges("fIncreasingAccessibilityChangeFromProtectedToPublic");
		assertThat(getResultingChangeType(), is(ChangeType.INCREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Increasing method accessiblity change from private to protected should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void increasingMethodAccessiblityChangeFromPrivateToProtectedShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("private void increasingAccessibilityChangeFromPrivateToProtected() {}");
		fRightSnippet = createSourceCode("protected void increasingAccessibilityChangeFromPrivateToProtected() {}");
		extractMethodDeclarationChanges("increasingAccessibilityChangeFromPrivateToProtected");
		assertThat(getResultingChangeType(), is(ChangeType.INCREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Increasing method accessiblity change from private to package should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void increasingMethodAccessiblityChangeFromPrivateToPackageShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("private void increasingAccessibilityChangeFromPrivateToPackage() {}");
		fRightSnippet = createSourceCode("void increasingAccessibilityChangeFromPrivateToPackage() {}");
		extractMethodDeclarationChanges("increasingAccessibilityChangeFromPrivateToPackage");
		assertThat(getResultingChangeType(), is(ChangeType.INCREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Increasing method accessiblity change from private to public should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void increasingMethodAccessiblityChangeFromPrivateToPublicShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("private void increasingAccessibilityChangeFromPrivateToPublic() {}");
		fRightSnippet = createSourceCode("public void increasingAccessibilityChangeFromPrivateToPublic() {}");
		extractMethodDeclarationChanges("increasingAccessibilityChangeFromPrivateToPublic");
		assertThat(getResultingChangeType(), is(ChangeType.INCREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Increasing method accessiblity change from package to protected should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void increasingMethodAccessiblityChangeFromPackageToProtectedShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("void increasingAccessibilityChangeFromPackageToProtected() {}");
		fRightSnippet = createSourceCode("protected void increasingAccessibilityChangeFromPackageToProtected() {}");
		extractMethodDeclarationChanges("increasingAccessibilityChangeFromPackageToProtected");
		assertThat(getResultingChangeType(), is(ChangeType.INCREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Increasing method accessiblity change from package to public should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void increasingMethodAccessiblityChangeFromPackageToPublicShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("void increasingAccessibilityChangeFromPackageToPublic() {}");
		fRightSnippet = createSourceCode("public void increasingAccessibilityChangeFromPackageToPublic() {}");
		extractMethodDeclarationChanges("increasingAccessibilityChangeFromPackageToPublic");
		assertThat(getResultingChangeType(), is(ChangeType.INCREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Increasing method accessiblity change from public to public should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void increasingMethodAccessiblityChangeFromPublicToPublicShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("protected void increasingAccessibilityChangeFromPublicToPublic() {}");
		fRightSnippet = createSourceCode("public void increasingAccessibilityChangeFromPublicToPublic() {}");
		extractMethodDeclarationChanges("increasingAccessibilityChangeFromPublicToPublic");
		assertThat(getResultingChangeType(), is(ChangeType.INCREASING_ACCESSIBILITY_CHANGE));
	}

	/**
	 * Method renaming should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	@Ignore("not yet implemented")
	public void methodRenamingShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("public void metodRenamin() {}");
		fRightSnippet = createSourceCode("public void methodRenaming() {}");
		extractMethodDeclarationChanges("methodRenaming");
		assertThat(getResultingChangeType(), is(ChangeType.METHOD_RENAMING));
	}

	/**
	 * Parameter delete should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void parameterDeleteShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("public void methodToCheckParameterDelete(int aInt) {}");
		fRightSnippet = createSourceCode("public void methodToCheckParameterDelete() {}");
		extractMethodDeclarationChanges("methodToCheckParameterDelete");
		assertThat(getResultingChangeType(), is(ChangeType.PARAMETER_DELETE));
	}

	/**
	 * Parameter insert should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void parameterInsertShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("public void methodToCheckParameterInsert() {}");
		fRightSnippet = createSourceCode("public void methodToCheckParameterInsert(int aInt) {}");
		extractMethodDeclarationChanges("methodToCheckParameterInsert");
		assertThat(getResultingChangeType(), is(ChangeType.PARAMETER_INSERT));
	}

	/**
	 * Parameter ordering change should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void parameterOrderingChangeShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("public void methodToCheckParameterOrderingChange(int first, int second, float aMovedParam) {}");
		fRightSnippet = createSourceCode("public void methodToCheckParameterOrderingChange(int first, float aMovedParam, int second) {}");
		extractMethodDeclarationChanges("methodToCheckParameterOrderingChange");
		assertThat(getResultingChangeType(), is(ChangeType.PARAMETER_ORDERING_CHANGE));
	}

	/**
	 * Parameter renaming should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void parameterRenamingShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("public void methodToCheckParameterRenaming(Object aRenamePara) {}");
		fRightSnippet = createSourceCode("public void methodToCheckParameterRenaming(Object aRenamedParam) {}");
		extractMethodDeclarationChanges("methodToCheckParameterRenaming");
		assertThat(getResultingChangeType(), is(ChangeType.PARAMETER_RENAMING));
	}

	/**
	 * Parameter type change should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void parameterTypeChangeShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("public void methodToCheckParameterTypeChange(Object first, int typeChange) {}");
		fRightSnippet = createSourceCode("public void methodToCheckParameterTypeChange(Object first, String typeChange) {}");
		extractMethodDeclarationChanges("methodToCheckParameterTypeChange");
		assertThat(getResultingChangeType(), is(ChangeType.PARAMETER_TYPE_CHANGE));
	}

	/**
	 * Parent class change should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void parentClassChangeShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("public class ClassToCheckParentClassChange extends Object {}");
		fRightSnippet = createSourceCode("public class ClassToCheckParentClassChange extends String {}");
		extractClassDeclarationChanges("ClassToCheckParentClassChange");
		assertThat(getResultingChangeType(), is(ChangeType.PARENT_CLASS_CHANGE));
	}

	/**
	 * Parent class delete should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void parentClassDeleteShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("public class ClassToCheckParentClassDelete extends ParentToDelete {}");
		fRightSnippet = createSourceCode("public class ClassToCheckParentClassDelete {}");
		extractClassDeclarationChanges("ClassToCheckParentClassDelete");
		assertThat(getResultingChangeType(), is(ChangeType.PARENT_CLASS_DELETE));
	}

	/**
	 * Parent class insert should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void parentClassInsertShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("public class ClassToCheckParentClassInsert {}");
		fRightSnippet = createSourceCode("public class ClassToCheckParentClassInsert extends ParentToInsert {}");
		extractClassDeclarationChanges("ClassToCheckParentClassInsert");
		assertThat(getResultingChangeType(), is(ChangeType.PARENT_CLASS_INSERT));
	}

	/**
	 * Parent interface change should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void parentInterfaceChangeShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("public class ClassToCheckParentInterfaceChange implements IIntefaceChang {}");
		fRightSnippet = createSourceCode("public class ClassToCheckParentInterfaceChange implements IInterfaceChange {}");
		extractClassDeclarationChanges("ClassToCheckParentInterfaceChange");
		assertThat(getResultingChangeType(), is(ChangeType.PARENT_INTERFACE_CHANGE));
	}

	/**
	 * Parent interface delete should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void parentInterfaceDeleteShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("public class ClassToCheckParentInterfaceDelete implements ParentInterfaceToDelete, IInterface {}");
		fRightSnippet = createSourceCode("public class ClassToCheckParentInterfaceDelete implements IInterface {}");
		extractClassDeclarationChanges("ClassToCheckParentInterfaceDelete");
		assertThat(getResultingChangeType(), is(ChangeType.PARENT_INTERFACE_DELETE));
	}

	/**
	 * Parent interface insert should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void parentInterfaceInsertShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("public class ClassToCheckParentInterfaceInsert implements IInterface {}");
		fRightSnippet = createSourceCode("public class ClassToCheckParentInterfaceInsert implements ParentInterfaceToInsert, IInterface {}");
		extractClassDeclarationChanges("ClassToCheckParentInterfaceInsert");
		assertThat(getResultingChangeType(), is(ChangeType.PARENT_INTERFACE_INSERT));
	}

	/**
	 * Removed class should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	@Ignore("not yet implemented")
	public void removedClassShouldBeDetected() throws Exception {
	}

	/**
	 * Removed functionality should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	@Ignore("not yet implemented")
	public void removedFunctionalityShouldBeDetected() throws Exception {
	}

	/**
	 * Removed object state should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	@Ignore("not yet implemented")
	public void removedObjectStateShouldBeDetected() throws Exception {
	}

	/**
	 * Removing attribute modifiability should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void removingAttributeModifiabilityShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("private String fRemovingAttributeModifiability;");
		fRightSnippet = createSourceCode("private final String fRemovingAttributeModifiability;");
		extractFieldDeclarationChanges("fRemovingAttributeModifiability");
		assertThat(getResultingChangeType(), is(ChangeType.REMOVING_ATTRIBUTE_MODIFIABILITY));
	}

	/**
	 * Removing class derivability should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void removingClassDerivabilityShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("public class RemovingClassDerivability {}");
		fRightSnippet = createSourceCode("public final class RemovingClassDerivability {}");
		extractClassDeclarationChanges("RemovingClassDerivability");
		assertThat(getResultingChangeType(), is(ChangeType.REMOVING_CLASS_DERIVABILITY));
	}

	/**
	 * Removing method overrideability should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void removingMethodOverrideabilityShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("public void removingMethodOverridability() {}");
		fRightSnippet = createSourceCode("public final void removingMethodOverridability() {}");
		extractMethodDeclarationChanges("removingMethodOverridability");
		assertThat(getResultingChangeType(), is(ChangeType.REMOVING_METHOD_OVERRIDABILITY));
	}

	/**
	 * Return type change should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void returnTypeChangeShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("public Object methodToCheckReturnTypeChange() {}");
		fRightSnippet = createSourceCode("public String methodToCheckReturnTypeChange() {}");
		extractMethodDeclarationChanges("methodToCheckReturnTypeChange");
		assertThat(getResultingChangeType(), is(ChangeType.RETURN_TYPE_CHANGE));
	}

	/**
	 * Return type delete should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void returnTypeDeleteShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("public int methodToCheckReturnTypeDelete() {}");
		fRightSnippet = createSourceCode("public void methodToCheckReturnTypeDelete() {}");
		extractMethodDeclarationChanges("methodToCheckReturnTypeDelete");
		assertThat(getResultingChangeType(), is(ChangeType.RETURN_TYPE_DELETE));
	}

	/**
	 * Return type insert should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void returnTypeInsertShouldBeDetected() throws Exception {
		fLeftSnippet = createSourceCode("public void methodToCheckReturnTypeInsert() {}");
		fRightSnippet = createSourceCode("public int methodToCheckReturnTypeInsert() {}");
		extractMethodDeclarationChanges("methodToCheckReturnTypeInsert");
		assertThat(getResultingChangeType(), is(ChangeType.RETURN_TYPE_INSERT));
	}

	/**
	 * Statement insert should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void statementInsertShouldBeDetected() throws Exception {
		fLeftSnippet = createMethodSourceCode("System.out.println();");
		fRightSnippet = createMethodSourceCode("System.out.println(); statement.insert();");
		extractMethodChanges("method");
		assertThat(getResultingChangeType(), is(ChangeType.STATEMENT_INSERT));
	}

	/**
	 * Postfix expression insert should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void postfixExpressionInsertShouldBeDetected() throws Exception {
		fLeftSnippet = createMethodSourceCode("System.out.println();");
		fRightSnippet = createMethodSourceCode("System.out.println(); this.insert++;");
		extractMethodChanges("method");
		assertThat(getResultingChangeType(), is(ChangeType.STATEMENT_INSERT));
	}

	/**
	 * Postfix expression update should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void postfixExpressionUpdateShouldBeDetected() throws Exception {
		fLeftSnippet = createMethodSourceCode("System.out.println(); this.counter++;");
		fRightSnippet = createMethodSourceCode("System.out.println(); this.counter--;");
		extractMethodChanges("method");
		assertThat(getResultingChangeType(), is(ChangeType.STATEMENT_UPDATE));
	}

	/**
	 * Prefix expression update should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void prefixExpressionUpdateShouldBeDetected() throws Exception {
		fLeftSnippet = createMethodSourceCode("System.out.println(); ++this.counter;");
		fRightSnippet = createMethodSourceCode("System.out.println(); --this.counter;");
		extractMethodChanges("method");
		assertThat(getResultingChangeType(), is(ChangeType.STATEMENT_UPDATE));
	}

	/**
	 * Prefix expression insert should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void prefixExpressionInsertShouldBeDetected() throws Exception {
		fLeftSnippet = createMethodSourceCode("System.out.println();");
		fRightSnippet = createMethodSourceCode("System.out.println(); ++this.insert;");
		extractMethodChanges("method");
		assertThat(getResultingChangeType(), is(ChangeType.STATEMENT_INSERT));
	}

	/**
	 * Statement delete should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void statementDeleteShouldBeDetected() throws Exception {
		fLeftSnippet = createMethodSourceCode("System.out.println(); statement.delete();");
		fRightSnippet = createMethodSourceCode("System.out.println();");
		extractMethodChanges("method");
		assertThat(getResultingChangeType(), is(ChangeType.STATEMENT_DELETE));
	}

	/**
	 * Postfix expression delete should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void postfixExpressionDeleteShouldBeDetected() throws Exception {
		fLeftSnippet = createMethodSourceCode("System.out.println(); this.delete++;");
		fRightSnippet = createMethodSourceCode("System.out.println();");
		extractMethodChanges("method");
		assertThat(getResultingChangeType(), is(ChangeType.STATEMENT_DELETE));
	}

	/**
	 * Prefix expression delete should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void prefixExpressionDeleteShouldBeDetected() throws Exception {
		fLeftSnippet = createMethodSourceCode("System.out.println(); ++this.delete;");
		fRightSnippet = createMethodSourceCode("System.out.println();");
		extractMethodChanges("method");
		assertThat(getResultingChangeType(), is(ChangeType.STATEMENT_DELETE));
	}

	/**
	 * Statement ordering change should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void statementOrderingChangeShouldBeDetected() throws Exception {
		fLeftSnippet = createMethodSourceCode("statement.ordering(); System.out.println(); aMethod.uberL33t();");
		fRightSnippet = createMethodSourceCode("System.out.println(); aMethod.uberL33t(); statement.ordering();");
		extractMethodChanges("method");
		assertThat(getResultingChangeType(), is(ChangeType.STATEMENT_ORDERING_CHANGE));
	}

	/**
	 * Statement parent change should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void statementParentChangeShouldBeDetected() throws Exception {
		fLeftSnippet = createMethodSourceCode("System.out.println(); statement.parent(); if (daNewParent == true) { aMethod.uberL33t(); foo.bar(); wow.kungen(); }");
		fRightSnippet = createMethodSourceCode("System.out.println(); if (daNewParent == true) { aMethod.uberL33t(); statement.parent(); foo.bar(); wow.kungen(); }");
		extractMethodChanges("method");
		assertThat(getResultingChangeType(), is(ChangeType.STATEMENT_PARENT_CHANGE));
	}

	/**
	 * Statement update should be detected.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void statementUpdateShouldBeDetected() throws Exception {
		fLeftSnippet = createMethodSourceCode("System.out.println(); statment.updae();");
		fRightSnippet = createMethodSourceCode("System.out.println(); statement.update();");
		extractMethodChanges("method");
		assertThat(getResultingChangeType(), is(ChangeType.STATEMENT_UPDATE));
	}

	/**
	 * Gets the resulting change type.
	 * 
	 * @return the resulting change type
	 */
	private ChangeType getResultingChangeType() {
		return structureEntity.getSourceCodeChanges().get(0).getChangeType();
	}

	/**
	 * Creates the method source code.
	 * 
	 * @param methodBody the method body
	 * @return the string
	 */
	private String createMethodSourceCode(String methodBody) {
		StringBuilder methodSource = new StringBuilder();
		methodSource.append("void ");
		methodSource.append(METHOD_NAME);
		methodSource.append("() { ");
		methodSource.append(methodBody);
		methodSource.append(" }");
		return createSourceCode(methodSource.toString());
	}

	/**
	 * Creates the source code.
	 * 
	 * @param snippet the snippet
	 * @return the string
	 */
	private String createSourceCode(String snippet) {
		return "public class Foo { " + snippet + " }";
	}

	/**
	 * Convert method body.
	 * 
	 * @param methodName the method name
	 * @param sourceCode the source code
	 * @return the node
	 */
	public Node convertMethodBody(String methodName, String sourceCode) {
		return convertMethodBody(methodName, UTCompilationUtils.compileSource(sourceCode));
	}

	/**
	 * Convert method.
	 * 
	 * @param methodDecl the method decl
	 * @param sourceCode the source code
	 * @param aFile the a file
	 * @return the node
	 */
	public Node convertMethod(org.eclipse.jdt.core.dom.MethodDeclaration methodDecl, //
			String sourceCode, File aFile) {
		mSource = sourceCode;
		mFile = aFile;
		return convertMethodBody(methodDecl, UTCompilationUtils.compileSource(sourceCode));
	}

	/**
	 * Convert method.
	 * 
	 * @param methodDecl the method decl
	 * @param sourceCode the source code
	 * @param bindingManagerList the binding manager list
	 * @return the node
	 */
	public Node convertMethod(org.eclipse.jdt.core.dom.MethodDeclaration methodDecl, //
			String sourceCode, //
			List<UTASTBindingManagerAbstract> bindingManagerList) {
		return convertMethodBody(methodDecl, UTCompilationUtils.compileSource(sourceCode), bindingManagerList);
	}

	/**
	 * Convert method.
	 * 
	 * @param methodDecl the method decl
	 * @param path the path
	 * @param dummy1 the dummy1
	 * @param dummy2 the dummy2
	 * @return the node
	 */
	public Node convertMethod(org.eclipse.jdt.core.dom.MethodDeclaration methodDecl, String path, String[] dummy1, String[] dummy2) {
		return convertMethodBody(methodDecl, UTCompilationUtils.compileSource(UTFile.getContents(path)));
	}

	/**
	 * Convert method.
	 * 
	 * @param methodDecl the method decl
	 * @param cu the cu
	 * @return the node
	 */
	public Node convertMethod(org.eclipse.jdt.core.dom.MethodDeclaration methodDecl, CompilationUnit cu) {
		return convertMethodBody(methodDecl, UTCompilationUtils.compileSource(cu));
	}

	/**
	 * Convert methods.
	 * 
	 * @param icu the icu
	 * @return the list
	 */
	public List<Node> convertMethods(ICompilationUnit icu) {
		List<Node> nodes = new ArrayList<Node>();
		List<AbstractMethodDeclaration> methods = new ArrayList<AbstractMethodDeclaration>();
		UTGeneralVisitor visitor = new UTGeneralVisitor();
		JavaCompilation compilation = UTCompilationUtils.compileSource(icu);
		CompilationUnitDeclaration comUnitDecl = compilation.getCompilationUnit();
		for (TypeDeclaration type : comUnitDecl.types) {
			type.traverse(visitor, (ClassScope) null);
			methods.addAll(visitor.getMethodDeclList());
		}
		for (int i = 0; i < methods.size(); i++) {
			AbstractMethodDeclaration method = methods.get(i);
			String methodName = String.valueOf(method.selector);
			Node root = new Node(JavaEntityType.METHOD, methodName);
			root.setEntity(new SourceCodeEntity(methodName, JavaEntityType.METHOD, //
					new SourceRange(method.declarationSourceStart, //
							method.declarationSourceEnd)));
			List<Comment> comments = UTCompilationUtils.extractComments(compilation);
			sMethodBodyConverter.initialize(root, method, comments, compilation.getScanner());
			method.traverse(sMethodBodyConverter, (ClassScope) null);
			nodes.add(root);
		}
		return nodes;
	}

	/**
	 * Convert methods.
	 * 
	 * @param cu the cu
	 * @param fullFilePath the full file path
	 * @return the list
	 */
	public List<UTASTNodeConverterManager> convertMethods(CompilationUnit cu, File fullFilePath) {
		UTIGeneralVisitor visitor = new UTGeneralVisitor() {
			@Override
			public boolean visit(MethodDeclaration methodDeclaration, ClassScope scope) {
				add(methodDeclaration);
				return true;
			}

			@Override
			public boolean visit(ConstructorDeclaration methodDeclaration, ClassScope scope) {
				if (!methodDeclaration.isDefaultConstructor())
					add(methodDeclaration);

				return true;
			}
		};
		List<AbstractMethodDeclaration> methods = new ArrayList<AbstractMethodDeclaration>();
		JavaCompilation compilation = UTCompilationUtils.compileSource(cu);
		for (TypeDeclaration type : compilation.getCompilationUnit().types) {
			visitor.getMethodDeclListClear();
			type.traverse(visitor, (ClassScope) null);
			methods.addAll(visitor.getMethodDeclList());
		}
		return convertMethods(methods, compilation, fullFilePath);
	}

	/**
	 * Convert methods.
	 * 
	 * @param methods the methods
	 * @param compilation the compilation
	 * @param fullFilePath the full file path
	 * @return the list
	 */
	private List<UTASTNodeConverterManager> convertMethods(List<AbstractMethodDeclaration> methods, //
			JavaCompilation compilation, File fullFilePath) {
		List<UTASTNodeConverterManager> nodeConverterMngr = new ArrayList<UTASTNodeConverterManager>();
		for (int i = 0; i < methods.size(); i++) {
			AbstractMethodDeclaration method = methods.get(i);
			String methodName = String.valueOf(method.selector);
			Node root = new Node(JavaEntityType.METHOD, methodName);

			root.setEntity(new SourceCodeEntity(methodName, JavaEntityType.METHOD, //
					new SourceRange(method.declarationSourceStart, //
							method.declarationSourceEnd)));
			List<Comment> comments = new ArrayList<Comment>(); // UTCompilationUtils.extractComments(compilation);
			root.setFilePath(fullFilePath);
			sMethodBodyConverter.initialize(root, method, comments, compilation.getScanner());
			// test code mksong Nov 26, 2013 10:06:20 PM
			// sDeclarationConverter.initialize(root, compilation.getScanner());
			method.traverse(sMethodBodyConverter, (ClassScope) null);
			// nodes.add(root);
			nodeConverterMngr.add(new UTASTNodeConverterManager(compilation, root));
		}
		return nodeConverterMngr;
	}

	/*
	public Node convertMethodDeclaration(String methodName, JavaCompilation compilation) {
		AbstractMethodDeclaration method = CompilationUtils.findMethod(compilation.getCompilationUnit(), methodName);
		Node root = new Node(JavaEntityType.METHOD, methodName);
		root.setEntity(new SourceCodeEntity(methodName, JavaEntityType.METHOD, 
				new SourceRange(method.declarationSourceStart,
				method.declarationSourceEnd)));
		sDeclarationConverter.initialize(root, compilation.getScanner());
		method.traverse(sDeclarationConverter, (ClassScope) null);
		return root;
	}
	*/

	/**
	 * Convert method declaration.
	 * 
	 * @param methodName the method name
	 * @param sourceCode the source code
	 * @return the node
	 */
	@Deprecated
	private Node convertMethodDeclaration(String methodName, String sourceCode) {
		return convertMethodDeclaration(methodName, UTCompilationUtils.compileSource(sourceCode));
	}

	/**
	 * Convert method body.
	 * 
	 * @param methodName the method name
	 * @param compilation the compilation
	 * @param sMethodBodyConverter the s method body converter
	 * @return the node
	 */
	public static Node convertMethodBody(String methodName, JavaCompilation compilation, JavaMethodBodyConverter sMethodBodyConverter) {
		AbstractMethodDeclaration method = CompilationUtils.findMethod(compilation.getCompilationUnit(), methodName);
		Node root = new Node(JavaEntityType.METHOD, methodName);
		root.setEntity(new SourceCodeEntity(methodName, JavaEntityType.METHOD, new SourceRange(method.declarationSourceStart, method.declarationSourceEnd)));
		List<Comment> comments = CompilationUtils.extractComments(compilation);
		sMethodBodyConverter.initialize(root, method, comments, compilation.getScanner());
		method.traverse(sMethodBodyConverter, (ClassScope) null);
		return root;
	}

	/**
	 * Convert method.
	 * 
	 * @param aPath the a path
	 * @param aStartPosition the a start position
	 * @return the node
	 */
	public static Node convertMethod(String aPath, int aStartPosition) {
		UTASTNodeFinder finder = new UTASTNodeFinder();
		UTASTNodeConverter converter = new UTASTNodeConverter();
		org.eclipse.jdt.core.dom.MethodDeclaration method = finder.findMethod(aPath, new Point(aStartPosition, aStartPosition + 1), false);
		Node ndMethod = converter.convertMethod(method, aPath, null, null);
		return ndMethod;
	}

	/**
	 * Convert field declaration.
	 * 
	 * @param fieldName the field name
	 * @param sourceCode the source code
	 * @return the node
	 */
	private Node convertFieldDeclaration(String fieldName, String sourceCode) {
		JavaCompilation compilation = UTCompilationUtils.compileSource(sourceCode);
		FieldDeclaration field = UTCompilationUtils.findField(compilation.getCompilationUnit(), fieldName);
		Node root = new Node(JavaEntityType.FIELD_DECLARATION, fieldName);
		root.setEntity(new SourceCodeEntity(fieldName, JavaEntityType.FIELD, new SourceRange(field.declarationSourceStart, field.declarationSourceEnd)));
		sDeclarationConverter.initialize(root, compilation.getScanner());
		field.traverse(sDeclarationConverter, (MethodScope) null);
		return root;
	}

	/**
	 * Convert class declaration.
	 * 
	 * @param className the class name
	 * @param sourceCode the source code
	 * @return the node
	 */
	private Node convertClassDeclaration(String className, String sourceCode) {
		JavaCompilation compilation = UTCompilationUtils.compileSource(sourceCode);
		TypeDeclaration type = UTCompilationUtils.findType(compilation.getCompilationUnit(), className);
		Node root = new Node(JavaEntityType.CLASS, className);
		root.setEntity(new SourceCodeEntity(className, JavaEntityType.CLASS, new SourceRange(type.declarationSourceStart, type.declarationSourceEnd)));
		sDeclarationConverter.initialize(root, compilation.getScanner());
		type.traverse(sDeclarationConverter, (ClassScope) null);
		return root;
	}

	/**
	 * Convert class.
	 * 
	 * @param typeDecl the type decl
	 * @param sourceCode the source code
	 * @return the node
	 */
	public Node convertClass(org.eclipse.jdt.core.dom.TypeDeclaration typeDecl, String sourceCode) {
		String name = typeDecl.getName().getIdentifier();
		return convertClassDeclaration(name, sourceCode);

	}

	/**
	 * Extract class declaration changes.
	 * 
	 * @param className the class name
	 */
	private void extractClassDeclarationChanges(String className) {
		Node leftDeclaration = convertClassDeclaration(className, fLeftSnippet);
		Node rootDeclaration = convertClassDeclaration(className, fRightSnippet);
		structureEntity = new StructureEntityVersion(JavaEntityType.CLASS, className, 0);
		distill(leftDeclaration, rootDeclaration);
	}

	/**
	 * Extract field declaration changes.
	 * 
	 * @param fieldName the field name
	 */
	private void extractFieldDeclarationChanges(String fieldName) {
		Node leftDeclaration = convertFieldDeclaration(fieldName, fLeftSnippet);
		Node rootDeclaration = convertFieldDeclaration(fieldName, fRightSnippet);
		structureEntity = new StructureEntityVersion(JavaEntityType.FIELD, fieldName, 0);
		distill(leftDeclaration, rootDeclaration);
	}

	/**
	 * Extract method declaration changes.
	 * 
	 * @param methodName the method name
	 */
	private void extractMethodDeclarationChanges(String methodName) {
		Node leftDeclaration = convertMethodDeclaration(methodName, fLeftSnippet);
		Node rootDeclaration = convertMethodDeclaration(methodName, fRightSnippet);
		structureEntity = new StructureEntityVersion(JavaEntityType.METHOD, methodName, 0);
		distill(leftDeclaration, rootDeclaration);
	}

	/**
	 * Extract method changes.
	 * 
	 * @param methodName the method name
	 */
	private void extractMethodChanges(String methodName) {
		Node leftMethod = convertMethodBody(methodName, fLeftSnippet);
		Node rightMethod = convertMethodBody(methodName, fRightSnippet);
		structureEntity = new StructureEntityVersion(JavaEntityType.METHOD, methodName, 0);
		distill(leftMethod, rightMethod);
	}

	/**
	 * Distill.
	 * 
	 * @param leftMethod the left method
	 * @param rightMethod the right method
	 */
	private void distill(Node leftMethod, Node rightMethod) {
		Distiller distiller = getDistiller(structureEntity);
		distiller.extractClassifiedSourceCodeChanges(leftMethod, rightMethod);
	}
}
