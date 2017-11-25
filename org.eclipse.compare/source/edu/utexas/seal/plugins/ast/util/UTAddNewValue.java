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

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.swt.graphics.Point;

import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ut.seal.plugins.utils.UTFile;
import ut.seal.plugins.utils.UTStr;
import ut.seal.plugins.utils.ast.UTASTNodeConverter;
import ut.seal.plugins.utils.ast.UTASTParser;
import edu.utexas.seal.plugins.ast.UTASTVisitor;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerAbstract;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerForStatementEnh;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerField;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerForStatement;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerLocalVariable;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerParameter;
import edu.utexas.seal.plugins.ast.util.UTASTManipulator;
import edu.utexas.seal.plugins.util.UTPlugin;
import edu.utexas.seal.plugins.util.visitor.UTGeneralVisitor;
import edu.utexas.seal.plugins.util.visitor.UTIGeneralVisitor;

/**
 * @author Myoungkyu Song
 * @date Oct 25, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTAddNewValue {
	boolean																__$d__					= false;
	private List<UTASTBindingManagerAbstract>							bindingManagerList		= null;
	private Map<IVariableBinding, UTASTBindingManagerField>				fieldManagers			= null;
	private Map<IVariableBinding, UTASTBindingManagerLocalVariable>		localVariableManagers	= null;
	private Map<IVariableBinding, UTASTBindingManagerParameter>			parameterManagers		= null;
	private Map<IVariableBinding, UTASTBindingManagerForStatementEnh>	forStatementEnhManagers	= null;
	private Map<IVariableBinding, UTASTBindingManagerForStatement>		forStatementManagers	= null;
	private CompilationUnit												mUnit					= null;
	private UTASTNodeConverter											nodeConverter			= new UTASTNodeConverter();

	/**
	 * Instantiates a new uT add new value.
	 */
	public UTAddNewValue() {
		bindingManagerList = new ArrayList<UTASTBindingManagerAbstract>();
	}

	public UTAddNewValue(ICompilationUnit rightICompilationUnit) {
		this();
		try {
			manipulate(rightICompilationUnit);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Manipulate.
	 * 
	 * @param aFilePath the a file path
	 * @throws Exception the exception
	 */
	public void manipulate(File aFilePath) throws Exception {
		ICompilationUnit icUnit = UTPlugin.getICompilationUnit(aFilePath);
		CompilationUnit unit = (new UTASTParser()).parse(icUnit);
		Point leftSelectedRegion = null;
		Point rightSelectedRegion = null;
		UTASTVisitor visitor = new UTASTVisitor(leftSelectedRegion, rightSelectedRegion, false);
		visitor.process(unit);
		manipulate(unit, visitor);
	}

	/**
	 * Manipulate.
	 * 
	 * @param aIUnit the a i unit
	 * @throws Exception the exception
	 */
	public void manipulate(ICompilationUnit aIUnit) throws Exception {
		mUnit = (new UTASTParser()).parse(aIUnit);
		UTASTVisitor visitor = new UTASTVisitor(null, null, false);
		visitor.process(mUnit);
		manipulate(mUnit, visitor);
	}

	/**
	 * Manipulate.
	 * 
	 * @param aUnit the a unit
	 * @throws Exception the exception
	 */
	public void manipulate(CompilationUnit aUnit) throws Exception {
		mUnit = aUnit;
		UTASTVisitor visitor = new UTASTVisitor(null, null, false);
		visitor.process(mUnit);
		manipulate(mUnit, visitor);
	}

	/**
	 * Adds the new value method level.
	 * 
	 * @param aICUnit the a ic unit
	 * @throws Exception the exception
	 */
	public void addNewValueMethodLevel(ICompilationUnit aICUnit) throws Exception {
		UTIGeneralVisitor<MethodDeclaration> mVisitor = new UTGeneralVisitor<MethodDeclaration>() {
			public boolean visit(MethodDeclaration node) {
				results.add(node);
				return true;
			}
		};
		File f = UTPlugin.getFullFilePath(aICUnit);
		String src = UTFile.getContents(f.getAbsolutePath());
		mUnit.accept(mVisitor);
		List<MethodDeclaration> lstMethods = mVisitor.getResults();
		for (int i = 0; i < lstMethods.size(); i++) {
			MethodDeclaration iMethodDecl = lstMethods.get(i);
			setNewValue(iMethodDecl, src);
		}
		System.out.println("[DBG2] GENERALIZE OLD REV.");
		displayMappingInfo();
	}

	/**
	 * Adds the new value method level.
	 * 
	 * @param aMethod the a method
	 * @param aSrc the a src
	 * @return the node
	 */
	public Node addNewValueMethodLevel(MethodDeclaration aMethod, String aSrc) {
		Node ndMethod = nodeConverter.convertMethod(aMethod, aSrc, bindingManagerList);
		return ndMethod;
	}

	/**
	 * Sets the new value.
	 * 
	 * @param aMethod the aMethod
	 * @param aSrc the aSrc
	 */
	private void setNewValue(MethodDeclaration aMethod, String aSrc) {
		Node ndMethod = nodeConverter.convertMethod(aMethod, aSrc, bindingManagerList);
		System.out.println("[DBG1] " + ndMethod);
		ndMethod.print();
		System.out.println("------------------------------------------");
		ndMethod.printNewValue();
		System.out.println("==========================================");
		boolean __D = false;
		if (__D) {
			Enumeration<?> eNode = ndMethod.preorderEnumeration();
			while (eNode.hasMoreElements()) {
				Node iNode = (Node) eNode.nextElement();
				// if (iNode.isSkip()) {
				// continue;
				// }
				String newValue = iNode.getValueNew();
				if (newValue == null)
					newValue = iNode.toString();
				System.out.println(UTStr.getIndent(iNode.getLevel()) + newValue + " <=> " + iNode);
			}
		}
	}

	/**
	 * Sets the new value_local val decl.
	 * 
	 * @param aNodeMethod the a node method
	 * @param aSrc the a src
	 */
	void setNewValue_localValDecl(Node aNodeMethod, String aSrc) {
		Object[] arMngr = localVariableManagers.values().toArray();
		for (Object object : arMngr) {
			UTASTBindingManagerLocalVariable iObj = (UTASTBindingManagerLocalVariable) object;
			SimpleName iNewName = iObj.getNewAbstractNode();
			SimpleName iOldName = iObj.getVariableDeclarationFragment().getName();

			Enumeration<?> eNode = aNodeMethod.preorderEnumeration();
			while (eNode.hasMoreElements()) {
				Node iNode = (Node) eNode.nextElement();
				iNode.setNewValue_localValDecl(iNewName, iOldName, aSrc);
			}
		}
	}

	/**
	 * Sets the new value val decl.
	 * 
	 * @param aNode the a node
	 * @param aSrc the a src
	 */
	public void setNewValueValDecl(Node aNode, String aSrc) {
		Object[] arMngr = localVariableManagers.values().toArray();
		for (Object object : arMngr) {
			UTASTBindingManagerLocalVariable iObj = (UTASTBindingManagerLocalVariable) object;
			aNode.setNewValue(iObj, aSrc);
		}
		Object[] arForStmtMngr = forStatementManagers.values().toArray();
		for (Object object : arForStmtMngr) {
			UTASTBindingManagerForStatement iObj = (UTASTBindingManagerForStatement) object;
			aNode.setNewValue(iObj, aSrc);
		}
	}

	/**
	 * Sets the new value method parm.
	 * 
	 * @param aNode the a node
	 * @param aSrc the a src
	 */
	void setNewValueMethodParm(Node aNode, String aSrc) {
		List<Node> lstFirstLevelKids = aNode.getChildren();
		for (Node iNode : lstFirstLevelKids) {
			if (iNode.getEntity().getType().equals(JavaEntityType.PARAMETERS)) {
				List<Node> ilstParm = iNode.getChildren();
				for (Node nodeElem : ilstParm) {
					setNewValueParm(nodeElem, aSrc);
				}
			}
		}
	}

	/**
	 * Sets the new value if stmt.
	 * 
	 * @param aNode the a node
	 * @param aSrc the a src
	 */
	void setNewValueIfStmt(Node aNode, String aSrc) {
		Object[] arMngr = parameterManagers.values().toArray();
		for (Object object : arMngr) {
			UTASTBindingManagerParameter iObj = (UTASTBindingManagerParameter) object;
			SimpleName iNewName = iObj.getNewAbstractNode();
			List<SimpleName> lstSimpleName = iObj.getReferenceNodeList();
			for (SimpleName jOldName : lstSimpleName) {
				aNode.setNewValueIfStmt(iNewName, jOldName, aSrc);
			}
		}

		arMngr = forStatementEnhManagers.values().toArray();
		for (Object object : arMngr) {
			UTASTBindingManagerForStatementEnh iObj = (UTASTBindingManagerForStatementEnh) object;
			SimpleName iNewName = iObj.getNewAbstractNode();
			List<SimpleName> lstSimpleName = iObj.getReferenceNodeList();
			for (SimpleName jOldName : lstSimpleName) {
				aNode.setNewValueIfStmt(iNewName, jOldName, aSrc);
			}
		}

	}

	/**
	 * Sets the new value for each stmt.
	 * 
	 * @param aNode the a node
	 * @param aSrc the a src
	 */
	void setNewValueForEachStmt(Node aNode, String aSrc) {
		Object[] arMngr = forStatementEnhManagers.values().toArray();
		for (Object object : arMngr) {
			UTASTBindingManagerForStatementEnh iObj = (UTASTBindingManagerForStatementEnh) object;
			SimpleName iNewName = iObj.getNewAbstractNode();
			SimpleName iOldName = iObj.getParameterDeclaration().getName();
			aNode.setNewValueForEachStmt(iNewName, iOldName, aSrc);
		}
	}

	/**
	 * Sets the new value for stmt.
	 * 
	 * @param aNode the a node
	 * @param aSrc the a src
	 */
	void setNewValueForStmt(Node aNode, String aSrc) {
		Object[] arMngr = forStatementManagers.values().toArray();
		for (Object object : arMngr) {
			UTASTBindingManagerForStatement iObj = (UTASTBindingManagerForStatement) object;
			SimpleName iNewName = iObj.getNewAbstractNode();
			List<SimpleName> lstSimpleName = iObj.getReferenceNodeList();
			for (SimpleName jOldName : lstSimpleName) {
				aNode.setNewValueForStmt(iNewName, jOldName, aSrc);
			}
		}
	}

	/**
	 * Sets the new value parm.
	 * 
	 * @param aNode the a node
	 * @param aSrc the a src
	 */
	private void setNewValueParm(Node aNode, String aSrc) {
		Object[] arMngr = parameterManagers.values().toArray();
		for (Object object : arMngr) {
			UTASTBindingManagerParameter iObj = (UTASTBindingManagerParameter) object;
			aNode.setNewValue(iObj, aSrc);
		}
	}

	/**
	 * Sets the new value ref.
	 * 
	 * @param aNode the a node
	 * @param aSrc the a src
	 */
	void setNewValueRef(Node aNode, String aSrc) {
		if (aNode.isBodyBlockStmt()) {
			return;
		}
		Object[] arMngr = localVariableManagers.values().toArray();
		for (Object object : arMngr) {
			UTASTBindingManagerLocalVariable iObj = (UTASTBindingManagerLocalVariable) object;
			SimpleName iNewName = iObj.getNewAbstractNode();
			List<SimpleName> iLstRef = iObj.getReferenceNodeList();
			for (SimpleName jOldName : iLstRef) {
				aNode.setNewValue(iNewName, jOldName, aSrc);
			}
		}

		arMngr = forStatementEnhManagers.values().toArray();
		for (Object object : arMngr) {
			UTASTBindingManagerForStatementEnh iObj = (UTASTBindingManagerForStatementEnh) object;
			SimpleName iNewName = iObj.getNewAbstractNode();
			List<SimpleName> lstSimpleName = iObj.getReferenceNodeList();
			for (SimpleName jOldName : lstSimpleName) {
				aNode.setNewValue(iNewName, jOldName, aSrc);
			}
		}
	}

	/**
	 * Manipulate.
	 * 
	 * @param unit the unit
	 * @param visitor the visitor
	 */
	void manipulate(CompilationUnit unit, UTASTVisitor visitor) {
		fieldManagers = visitor.getFieldManagers();
		localVariableManagers = visitor.getLocalVariableManagers();
		parameterManagers = visitor.getParameterManagers();
		forStatementEnhManagers = visitor.getForStatEnhManagers();
		forStatementManagers = visitor.getForStatManagers();

		bindingManagerList.addAll(fieldManagers.values());
		bindingManagerList.addAll(localVariableManagers.values());
		bindingManagerList.addAll(parameterManagers.values());
		bindingManagerList.addAll(forStatementEnhManagers.values());
		bindingManagerList.addAll(forStatementManagers.values());
		new UTASTManipulator().manipulate(unit, bindingManagerList);
	}

	/**
	 * Display mapping info.
	 * 
	 * @throws JavaModelException the java model exception
	 */
	public void displayMappingInfo() throws JavaModelException {
		for (int i = 0; i < bindingManagerList.size(); i++) {
			UTASTBindingManagerAbstract m = bindingManagerList.get(i);
			if (m instanceof UTASTBindingManagerField) {
				displayMappingInfoDecl((UTASTBindingManagerField) m, //
						((UTASTBindingManagerField) m).getFieldDeclaration());
			} else if (m instanceof UTASTBindingManagerLocalVariable) {
				displayMappingInfoDecl((UTASTBindingManagerLocalVariable) m, //
						((UTASTBindingManagerLocalVariable) m).getVariableDeclarationFragment());
			} else if (m instanceof UTASTBindingManagerParameter) {
				displayMappingInfoDecl((UTASTBindingManagerParameter) m, //
						((UTASTBindingManagerParameter) m).getParameterDeclaration());
			} else if (m instanceof UTASTBindingManagerForStatementEnh) {
				displayMappingInfoDecl((UTASTBindingManagerForStatementEnh) m, //
						((UTASTBindingManagerForStatementEnh) m).getParameterDeclaration());
			} else if (m instanceof UTASTBindingManagerForStatement) {
				displayMappingInfoDecl((UTASTBindingManagerForStatement) m, //
						((UTASTBindingManagerForStatement) m).getVariableDeclarationFragment());
			}
			displayMappingInfoRef(m);
		}
	}

	/**
	 * Display mapping info decl.
	 * 
	 * @param manager the manager
	 * @param varDeclFrag the var decl frag
	 */
	private void displayMappingInfoDecl(UTASTBindingManagerAbstract manager, VariableDeclaration varDeclFrag) {
		SimpleName newVarNode = manager.getNewAbstractNode();
		if (newVarNode == null)
			return;
		String oldName = varDeclFrag.getName().getFullyQualifiedName();
		String newName = newVarNode.getFullyQualifiedName();
		String fullyQualifiedOldName = varDeclFrag.getName().toString();
		int offsetBgnOfOldName = varDeclFrag.getName().getStartPosition();
		int offsetEndOfOldName = offsetBgnOfOldName + varDeclFrag.getName().getLength();
		String msg = "%s <---> %s (%s, %d, %d)";
		msg = String.format(msg, newName, oldName, fullyQualifiedOldName, offsetBgnOfOldName, offsetEndOfOldName);
		System.out.println("[DBG0] " + msg);
	}

	/**
	 * Display mapping info ref.
	 * 
	 * @param m the m
	 */
	private void displayMappingInfoRef(UTASTBindingManagerAbstract m) {
		List<SimpleName> lstRef = m.getReferenceNodeList();
		for (int j = 0; j < lstRef.size(); j++) {
			SimpleName oldName = lstRef.get(j);
			int pBgn = oldName.getStartPosition();
			int pEnd = pBgn + oldName.getLength();

			ASTNode ndParent = oldName.getParent();
			if (ndParent == null)
				continue;
			ASTNode ndParParent = ndParent.getParent();

			String data = null;
			if (ndParent.getNodeType() == ASTNode.IF_STATEMENT) {
				IfStatement ifStmt = (IfStatement) ndParent;
				String expr = ifStmt.getExpression().toString();
				data = "IF (" + expr + ")";
			} else if (ndParent.getNodeType() == ASTNode.ENHANCED_FOR_STATEMENT) {
				EnhancedForStatement enhForStmt = (EnhancedForStatement) ndParent;
				String para = enhForStmt.getParameter().toString();
				data = "FOR_ENH (" + para + " : " + oldName + ")";
			} else if (ndParParent != null && ndParParent.getNodeType() == ASTNode.FOR_STATEMENT) {
				ForStatement forStmt = (ForStatement) ndParParent;
				List<?> lstForInit = forStmt.initializers();
				List<?> lstForUpdater = forStmt.updaters();
				Expression cond = forStmt.getExpression();
				data = "FOR (" + lstForInit + " : " + cond + " : " + lstForUpdater + ")";
			} else {
				data = ndParent.toString();
			}
			System.out.println("\t" + data + " (" + pBgn + ", " + pEnd + ")");
		}
	}

	/**
	 * Gets the binding manager list.
	 * 
	 * @return the binding manager list
	 */
	public List<UTASTBindingManagerAbstract> getBindingManagerList() {
		return bindingManagerList;
	}
}
