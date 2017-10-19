/*
 * @(#) JavaMethodBodyConverterHelper.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package ch.uzh.ifi.seal.changedistiller.ast.java;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.CompoundAssignment;
import org.eclipse.jdt.internal.compiler.ast.DoStatement;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.ForStatement;
import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.JavaNewNamePrint;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.PostfixExpression;
import org.eclipse.jdt.internal.compiler.ast.PrefixExpression;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.Reference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.WhileStatement;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerAbstract;

public class JavaMethodBodyConverterHelper {
	private static final String					COLON	= ":";
	public List<UTASTBindingManagerAbstract>	fBindingManagerList;
	public StringBuffer							fNewNameBuf;

	public JavaMethodBodyConverterHelper() {
	}

	public JavaMethodBodyConverterHelper(List<UTASTBindingManagerAbstract> aBindingManagerList) {
		fBindingManagerList = aBindingManagerList;
	}

	// //////////////////////////////////////////////////////////////////
	// LOCAL VAR DECL, FOR VAR DECL
	// //////////////////////////////////////////////////////////////////

	@SuppressWarnings("unused")
	boolean setNewValueLocalDeclaration(LocalDeclaration localDeclaration, BlockScope scope) {
		boolean result = false;
		if (fBindingManagerList != null) {
			for (int i = 0; i < fBindingManagerList.size(); i++) {
				UTASTBindingManagerAbstract mngr = fBindingManagerList.get(i);
				SimpleName newName = mngr.getNewAbstractNode();
				SimpleName oldName = mngr.getDeclaration().getName();
				int bgn14 = oldName.getStartPosition();
				int end14 = bgn14 + oldName.getLength() - 1;
				int bgn17 = localDeclaration.sourceStart;
				int end17 = localDeclaration.sourceEnd;
				if (bgn14 == bgn17 && end14 == end17) {
					String name = String.valueOf(localDeclaration.name);
					localDeclaration.toString();
					String value = localDeclaration.toString();
					localDeclaration.setNewName(newName.getFullyQualifiedName());
					// fNewNameBuf = new StringBuffer(30); // INIT
					// fNewNameBuf = localDeclaration.printStatement(0, fNewNameBuf); // GET
					// JavaNewNamePrint.printWithNewName = false; // RESET
					// result = true;
					// break;
				}
			}

			JavaRefASTVisitor refVisitor = new JavaRefASTVisitor();
			localDeclaration.traverse(refVisitor, scope);
			result = setNewValue(refVisitor.getReferenceList());
			if (JavaNewNamePrint.printWithNewName) {
				fNewNameBuf = new StringBuffer(); // INIT
				fNewNameBuf = localDeclaration.printStatement(0, fNewNameBuf); // GET
				JavaNewNamePrint.printWithNewName = false; // RESET
				result = true;
			}
		}
		return result;
	}

	// //////////////////////////////////////////////////////////////////
	// FOR - POSTFIX
	// //////////////////////////////////////////////////////////////////

	boolean setNewValue(PostfixExpression postfixExpression, BlockScope scope) {
		boolean result = false;
		if (fBindingManagerList != null) {
			JavaRefASTVisitor refVisitor = new JavaRefASTVisitor();
			postfixExpression.traverse(refVisitor, scope);
			result = setNewValue(refVisitor.getReferenceList());
			if (JavaNewNamePrint.printWithNewName) {
				// WILL INIT, GET, & RESET
			}
		}
		return result;
	}

	// //////////////////////////////////////////////////////////////////
	// FOR - PREFIX
	// //////////////////////////////////////////////////////////////////

	boolean setNewValue(PrefixExpression prefixExpression, BlockScope scope) {
		boolean result = false;
		if (fBindingManagerList != null) {
			JavaRefASTVisitor refVisitor = new JavaRefASTVisitor();
			prefixExpression.traverse(refVisitor, scope);
			result = setNewValue(refVisitor.getReferenceList());
			if (JavaNewNamePrint.printWithNewName) {
				// WILL INIT, GET, & RESET
			}
		}
		return result;
	}

	// //////////////////////////////////////////////////////////////////
	// FOR - COMPOUND ASSIGNMENT
	// //////////////////////////////////////////////////////////////////

	boolean setNewValue(CompoundAssignment compoundAssignment, BlockScope scope) {
		boolean result = false;
		if (fBindingManagerList != null) {
			JavaRefASTVisitor refVisitor = new JavaRefASTVisitor();
			compoundAssignment.traverse(refVisitor, scope);
			result = setNewValue(refVisitor.getReferenceList());
			if (JavaNewNamePrint.printWithNewName) {
				// WILL INIT, GET, & RESET
			}
		}
		return result;
	}

	// //////////////////////////////////////////////////////////////////
	// FOR - ASSIGNMENT
	// //////////////////////////////////////////////////////////////////

	boolean setNewValue(Assignment assignment, BlockScope scope) {
		boolean result = false;
		if (fBindingManagerList != null) {
			JavaRefASTVisitor refVisitor = new JavaRefASTVisitor();
			assignment.traverse(refVisitor, scope);
			result = setNewValue(refVisitor.getReferenceList());
			if (JavaNewNamePrint.printWithNewName) {
				// WILL INIT, GET, & RESET
			}
		}
		return result;
	}

	// //////////////////////////////////////////////////////////////////
	// FOR - INCREMENTS
	// //////////////////////////////////////////////////////////////////

	boolean setNewIncrements(Statement statement, BlockScope scope) {
		boolean result = false;
		if (fBindingManagerList != null) {
			JavaRefASTVisitor refVisitor = new JavaRefASTVisitor();
			statement.traverse(refVisitor, scope);
			result = setNewValue(refVisitor.getReferenceList());
			if (JavaNewNamePrint.printWithNewName) {
				fNewNameBuf = new StringBuffer(30); // INIT
				fNewNameBuf = statement.printStatement(0, fNewNameBuf); // GET
				JavaNewNamePrint.printWithNewName = false; // RESET
			}
		}
		return result;
	}

	// //////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////

	boolean setNewValue(Statement statement, BlockScope scope) {
		PostfixExpression postfixExpr = null;
		PrefixExpression prefixExpr = null;

		boolean check = false;
		if (fBindingManagerList != null) {
			int bgn14 = statement.sourceStart();
			int end14 = statement.sourceEnd();
			for (int i = 0; i < fBindingManagerList.size(); i++) {
				UTASTBindingManagerAbstract mngr = fBindingManagerList.get(i);
				SimpleName newName = mngr.getNewAbstractNode();
				SimpleName oldName = mngr.getDeclaration().getName();
				int bgn17 = oldName.getStartPosition();
				int end17 = bgn17 + oldName.getLength() - 1;
				// ////////////////////////////////////////////////////
				if (statement instanceof LocalDeclaration) {
					if (bgn17 == bgn14 && end17 == end14) {
						LocalDeclaration stmtLocalDecl = (LocalDeclaration) statement;
						stmtLocalDecl.setNewName(newName.getFullyQualifiedName());
						fNewNameBuf = new StringBuffer(30);
						fNewNameBuf = stmtLocalDecl.printStatement(0, fNewNameBuf);
						check = true;
					}
				}
				// ////////////////////////////////////////////////////
				if (statement instanceof PostfixExpression) {
					postfixExpr = (PostfixExpression) statement;
				} else if (statement instanceof PrefixExpression) {
					prefixExpr = (PrefixExpression) statement;
				}

				List<SimpleName> lstRef = mngr.getReferenceNodeList();
				for (SimpleName oldRefName : lstRef) {
					bgn17 = oldRefName.getStartPosition();
					end17 = bgn17 + oldRefName.getLength() + 1;
					if (bgn14 == bgn17 && end14 == end17) {
						if (statement instanceof PostfixExpression) {
							postfixExpr.setNewName(newName.getFullyQualifiedName());
							fNewNameBuf = new StringBuffer(30);
							fNewNameBuf = postfixExpr.printStatement(0, fNewNameBuf);
							check = true;
						} else if (statement instanceof PrefixExpression) {
							prefixExpr.setNewName(newName.getFullyQualifiedName());
							fNewNameBuf = new StringBuffer(30);
							fNewNameBuf = prefixExpr.printStatement(0, fNewNameBuf);
							check = true;
						}
					}
				}
			}
		}
		return check;
	}

	// //////////////////////////////////////////////////////////////////
	// FOREACH
	// //////////////////////////////////////////////////////////////////

	boolean setNewValue(ForeachStatement foreachStatement, BlockScope scope) {
		String newElemVar = null;
		String newCollection = null;
		if (fBindingManagerList == null) {
			return false;
		}
		int bgn14 = foreachStatement.elementVariable.sourceStart();
		int end14 = foreachStatement.elementVariable.sourceEnd();

		for (int i = 0; i < fBindingManagerList.size(); i++) {
			UTASTBindingManagerAbstract mngr = fBindingManagerList.get(i);
			SimpleName newName = mngr.getNewAbstractNode();
			SimpleName oldName = mngr.getDeclaration().getName();
			int bgn17 = oldName.getStartPosition();
			int end17 = bgn17 + oldName.getLength() - 1;
			if (bgn14 == bgn17 && end14 == end17) {
				foreachStatement.elementVariable.setNewName(newName.getFullyQualifiedName());
				newElemVar = foreachStatement.elementVariable.printAsExpression(0, new StringBuffer()).toString(); // GET
			}
		}
		// ///////////////////////////////////////////////////////////
		Expression expr = foreachStatement.collection;
		if (expr instanceof SingleNameReference) {
			SingleNameReference ref = (SingleNameReference) expr;
			List<SingleNameReference> lstRef = new ArrayList<SingleNameReference>();
			lstRef.add(ref);
			for (int i = 0; i < fBindingManagerList.size(); i++) {
				UTASTBindingManagerAbstract m = fBindingManagerList.get(i);
				newCollection = setNewValueForEach(m, lstRef); // GET
				if (newCollection != null) {
					break;
				}
			}
		}
		if (JavaNewNamePrint.printWithNewName) {
			fNewNameBuf = new StringBuffer(newElemVar + COLON + newCollection); // COMBINE
			JavaNewNamePrint.printWithNewName = false; // RESET
		}
		return false;
	}

	private String setNewValueForEach(UTASTBindingManagerAbstract aAbsMngr, List<SingleNameReference> aLstRef) {
		SimpleName newName = aAbsMngr.getNewAbstractNode();
		List<SimpleName> lstRef = aAbsMngr.getReferenceNodeList();

		for (SingleNameReference iRef : aLstRef) {
			int bgn14 = iRef.sourceStart();
			int end14 = iRef.sourceEnd();

			for (SimpleName oldName : lstRef) {
				int bgn17 = oldName.getStartPosition();
				int end17 = bgn17 + oldName.getLength();

				if (bgn14 == bgn17 && end14 == end17 - 1) {
					iRef.setNewName(newName.getFullyQualifiedName());
					return iRef.printStatement(0, new StringBuffer()).toString();
				}
			}
		}
		return null;
	}

	// //////////////////////////////////////////////////////////////////
	// METHOD CALL
	// //////////////////////////////////////////////////////////////////

	boolean setNewValue(MessageSend messageSend, BlockScope scope) {
		boolean result = false;
		if (fBindingManagerList != null) {
			JavaRefASTVisitor refVisitor = new JavaRefASTVisitor();
			messageSend.traverse(refVisitor, scope);
			result = setNewValue(refVisitor.getReferenceList()); // SET
			if (result) {
				;// WILL INIT, GET, RESET
			}
		}
		return result;
	}

	// //////////////////////////////////////////////////////////////////
	// IF
	// //////////////////////////////////////////////////////////////////

	boolean setNewValue(IfStatement ifStatement, BlockScope scope) {
		boolean result = false;
		if (fBindingManagerList != null) {
			JavaRefASTVisitor refVisitor = new JavaRefASTVisitor();
			ifStatement.condition.traverse(refVisitor, scope);
			// ///////////////////////////////////////////////////////
			result = setNewValue(refVisitor.getReferenceList());
			if (JavaNewNamePrint.printWithNewName) {
				fNewNameBuf = new StringBuffer(30); // INIT
				fNewNameBuf = ifStatement.condition.printStatement(0, fNewNameBuf); // GET
				JavaNewNamePrint.printWithNewName = false; // RESET
			}
		}
		return result;
	}

	// //////////////////////////////////////////////////////////////////
	// FOR
	// //////////////////////////////////////////////////////////////////

	boolean setNewValue(ForStatement forStatement, BlockScope scope) {
		boolean result = false;
		if (fBindingManagerList != null) {
			JavaRefASTVisitor refVisitor = new JavaRefASTVisitor();
			forStatement.condition.traverse(refVisitor, scope);
			// ///////////////////////////////////////////////////////
			result = setNewValue(refVisitor.getReferenceList()); // SET
			if (JavaNewNamePrint.printWithNewName) {
				fNewNameBuf = new StringBuffer(30); // INIT
				fNewNameBuf = forStatement.condition.printStatement(0, fNewNameBuf); // GET
				JavaNewNamePrint.printWithNewName = false; // RESET
			}
		}
		return result;
	}

	// //////////////////////////////////////////////////////////////////
	// WHILE
	// //////////////////////////////////////////////////////////////////

	boolean setNewValue(WhileStatement whileStatement, BlockScope scope) {
		boolean result = false;
		if (fBindingManagerList != null) {
			JavaRefASTVisitor refVisitor = new JavaRefASTVisitor();
			whileStatement.condition.traverse(refVisitor, scope);
			// ///////////////////////////////////////////////////////
			result = setNewValue(refVisitor.getReferenceList()); // SET
			if (JavaNewNamePrint.printWithNewName) {
				fNewNameBuf = new StringBuffer(30); // INIT
				fNewNameBuf = whileStatement.condition.printStatement(0, fNewNameBuf); // GET
				JavaNewNamePrint.printWithNewName = false; // RESET
			}
		}
		return result;
	}

	// //////////////////////////////////////////////////////////////////
	// WHILE
	// //////////////////////////////////////////////////////////////////

	boolean setNewValue(DoStatement doStatement, BlockScope scope) {
		boolean result = false;
		if (fBindingManagerList != null) {
			JavaRefASTVisitor refVisitor = new JavaRefASTVisitor();
			doStatement.condition.traverse(refVisitor, scope);
			// ///////////////////////////////////////////////////////
			result = setNewValue(refVisitor.getReferenceList()); // SET
			if (JavaNewNamePrint.printWithNewName) {
				fNewNameBuf = new StringBuffer(30); // INIT
				fNewNameBuf = doStatement.condition.printStatement(0, fNewNameBuf); // GET
				JavaNewNamePrint.printWithNewName = false; // RESET
			}
		}
		return result;
	}

	// //////////////////////////////////////////////////////////////////
	// RETURN
	// //////////////////////////////////////////////////////////////////

	boolean setNewValue(ReturnStatement returnStatement, BlockScope scope) {
		boolean result = false;
		if (fBindingManagerList != null && returnStatement.expression != null) {
			JavaRefASTVisitor refVisitor = new JavaRefASTVisitor();
			returnStatement.expression.traverse(refVisitor, scope);
			result = setNewValue(refVisitor.getReferenceList()); // SET
			if (result) {
				fNewNameBuf = new StringBuffer(30); // INIT
				fNewNameBuf = returnStatement.expression.printStatement(0, fNewNameBuf); // GET
				JavaNewNamePrint.printWithNewName = false; // RESET
			}
		}
		return result;
	}

	// //////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////

	/**
	 * @param manager
	 * @param varDeclFrag
	 * @param expression
	 */
	private boolean setNewValue(List<Reference> aLstRef) {
		boolean result = false;
		SimpleName newName = null;

		for (Reference iRef : aLstRef) {
			int bgn14 = iRef.sourceStart();
			int end14 = iRef.sourceEnd();

			BINDINGMANAGERSEARCH: //
			for (int i = 0; i < fBindingManagerList.size(); i++) {
				UTASTBindingManagerAbstract mngr = fBindingManagerList.get(i);
				newName = mngr.getNewAbstractNode();
				SimpleName oldName = mngr.getDeclaration().getName();
				int bgn17 = oldName.getStartPosition();
				int end17 = bgn17 + oldName.getLength() - 1;

				if (bgn14 == bgn17 && end14 == end17 - 1) {
					iRef.setNewName(newName.getFullyQualifiedName());
					result = true;
					break;
				}
				List<SimpleName> lstRef = mngr.getReferenceNodeList();
				for (SimpleName oldRefName : lstRef) {
					bgn17 = oldRefName.getStartPosition();
					end17 = bgn17 + oldRefName.getLength();

					if (bgn14 == bgn17 && (iRef instanceof SingleNameReference) && end14 == end17 - 1) {
						iRef.setNewName(newName.getFullyQualifiedName());
						result = true;
						break BINDINGMANAGERSEARCH;
					} else if (bgn14 == bgn17 && (iRef instanceof QualifiedNameReference)) {
						QualifiedNameReference qualRef = (QualifiedNameReference) iRef;
						String tokenFirstName = String.valueOf(qualRef.tokens[0]);
						int endTFN = bgn14 + tokenFirstName.length();
						if (end17 == endTFN) {
							iRef.setNewName(newName.getFullyQualifiedName());
							result = true;
							break BINDINGMANAGERSEARCH;
						}
					} else if (iRef instanceof FieldReference) {
						String iRefName = iRef.toString();
						if (iRefName.startsWith("this.")) {
							bgn14 = iRef.sourceStart();
							bgn14 += "this.".length();
						}
						if (bgn14 == bgn17 && end14 == end17 - 1) {
							iRef.setNewName(newName.getFullyQualifiedName());
							result = true;
							break BINDINGMANAGERSEARCH;
						}
					}
				}
			}
		}
		return result;
	}

	// //////////////////////////////////////////////////////////////////
	// METHOD DECLARATION - ARGUMENT
	// //////////////////////////////////////////////////////////////////

	public boolean setNewVaule(Argument node, BlockScope scope) {
		boolean result = false;

		if (fBindingManagerList == null) {
			return result;
		}
		int bgn14 = node.sourceStart();
		int end14 = node.sourceEnd();

		for (int i = 0; i < fBindingManagerList.size(); i++) {
			UTASTBindingManagerAbstract mngr = fBindingManagerList.get(i);
			SimpleName newName = mngr.getNewAbstractNode();
			SimpleName oldName = mngr.getDeclaration().getName();
			int bgn17 = oldName.getStartPosition();
			int end17 = bgn17 + oldName.getLength() - 1;

			if (bgn17 == bgn14 && end17 == end14) {
				fNewNameBuf = new StringBuffer(newName.getFullyQualifiedName()); // INIT & GET
				JavaNewNamePrint.printWithNewName = false; // RESET
				break;
			}
		}
		return result;
	}
}
