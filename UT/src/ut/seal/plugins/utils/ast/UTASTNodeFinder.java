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
package ut.seal.plugins.utils.ast;

import java.io.File;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.swt.graphics.Point;
import org.junit.Test;

import ut.seal.plugins.utils.UTFile;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.SourceRange;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ch.uzh.ifi.seal.changedistiller.util.CompilationUtils;
import edu.utexas.seal.plugins.util.visitor.UTGeneralVisitor;
import edu.utexas.seal.plugins.util.visitor.UTIGeneralVisitor;

/**
 * The Class UTASTNodeFinder.
 * 
 * @author Myoungkyu Song
 * @date Nov 20, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTASTNodeFinder {
	private UTASTParser	astParser	= new UTASTParser();
	private String		mSource;
	private File		mFile;

	public AbstractMethodDeclaration findMethod(JavaCompilation aCompileSource, MethodDeclaration aMethod) {
		String methodName = aMethod.getName().getFullyQualifiedName();
		int bgnMethod = aMethod.getStartPosition();
		List<?> parameters = aMethod.parameters();
		// /////////////////////////////////////////////////////////////////
		MethodVisitorByInternalCompiler visitor = new MethodVisitorByInternalCompiler();
		CompilationUnitDeclaration unit = aCompileSource.getCompilationUnit();
		unit.traverse(visitor, (CompilationUnitScope) null);
		List<AbstractMethodDeclaration> mMethodList = visitor.mMethodList;
		for (int i = 0; i < mMethodList.size(); i++) {
			AbstractMethodDeclaration iMethod = mMethodList.get(i);
			String iMethodName = String.valueOf(iMethod.selector);
			int iBgnMethod = iMethod.declarationSourceStart;

			if (iBgnMethod == bgnMethod && iMethodName.equals(methodName)) {
				return iMethod;
			}
		}
		// /////////////////////////////////////////////////////////////////
		for (int i = 0; i < mMethodList.size(); i++) {
			AbstractMethodDeclaration iMethod = mMethodList.get(i);
			String iMethodName = String.valueOf(iMethod.selector);
			Argument[] iParameters = iMethod.arguments;
			if (compareParameters(parameters, iParameters) && //
					String.valueOf(iMethodName).equals(methodName)) {
				return iMethod;
			}
		}
		return null;
	}

	public List<MethodDeclaration> findMethods(String aFile) {
		UTIGeneralVisitor<MethodDeclaration> mVisitor = new UTGeneralVisitor<MethodDeclaration>() {
			public boolean visit(MethodDeclaration node) {
				results.add(node);
				return true;
			}
		};
		UTASTParser astParser = new UTASTParser();
		String codeText = UTFile.getContents(aFile);
		CompilationUnit unit = astParser.parse(codeText);
		unit.accept(mVisitor);
		return mVisitor.getResults();
	}

	public MethodDeclaration findMethod(Node aNode, List<MethodDeclaration> lstMethods) {
		for (int i = 0; i < lstMethods.size(); i++) {
			MethodDeclaration iMethodDecl = lstMethods.get(i);
			if (iMethodDecl.getStartPosition() == aNode.getEntity().getStartPosition()) {
				return iMethodDecl;
			}
		}
		throw new RuntimeException("MethodDeclaration findMethod(String aFile, List<MethodDeclaration> lstMethods)");
	}

	public Node findMethod(ICompilationUnit iunit, int bgnPos, int endPos, String src, UTASTNodeConverter c, File aFile) {
		MethodDeclaration m = findMethod(iunit, new Point(bgnPos, endPos - bgnPos), false);
		Node rootNode = c.convertMethod(m, src, aFile);
		return rootNode;
	}

	public MethodDeclaration findMethod(ICompilationUnit icu, Point selectedRegion, boolean isPrintable) {
		ASTNode node = findCoveringMethodDeclaration(astParser.parse(icu), selectedRegion);
		if (node != null && node instanceof MethodDeclaration) {
			if (isPrintable) {
				System.out.println("[DBG] A ROOT NODE OF METHOD: " + //
						((MethodDeclaration) node).getName().getFullyQualifiedName());
			}
			return (MethodDeclaration) node;
		}
		return null;
	}

	public AbstractMethodDeclaration findMethod(String sourceCode, MethodDeclaration coveringMethod) {
		String methodName = coveringMethod.getName().getFullyQualifiedName();
		CompilationUnitDeclaration cu = CompilationUtils.compileSource(sourceCode).getCompilationUnit();
		for (TypeDeclaration type : cu.types) {
			for (AbstractMethodDeclaration method : type.methods) {
				int bgn1 = coveringMethod.getStartPosition();
				int end1 = bgn1 + coveringMethod.getLength();
				int bgn2 = method.declarationSourceStart;
				int end2 = method.declarationSourceEnd;
				if (bgn1 <= bgn2 && end1 >= end2 && String.valueOf(method.selector).equals(methodName)) {
					return method;
				}
			}
		}
		return null;
	}

	/**
	 * Compare parameters.
	 * 
	 * @param p1 the p1
	 * @param p2 the p2
	 * @return true, if successful
	 */
	boolean compareParameters(List<?> p1, Argument[] p2) {
		if (p1 == null && p2 == null) {
			return true;
		} else if (p1 == null && p2.length == 0) {
			return true;
		} else if (p2 == null && p1.size() == 0) {
			return true;
		}
		if (p1 != null && p2 != null && p1.size() == p2.length) {
			for (int i = 0; i < p2.length; i++) {
				String elem1 = p1.get(i).toString().replace("[]", "");
				String elem2 = p2[i].toString().replace("[]", "");
				if (!elem1.equals(elem2))
					return false;
			}
		}
		return true;
	}

	@Test
	public void test_compareParameters() {
		String str = "int[] params, int[] params";
		System.out.println("[DBG0] " + str);

		String tmp = str.replace("[]", "");
		System.out.println("[DBG1] " + tmp);
	}

	public org.eclipse.jdt.core.dom.TypeDeclaration findClass(ICompilationUnit icu, Point selectedRegion, boolean isPrintable) {
		ASTNode node = findingCoveringTypeDeclaration(astParser.parse(icu), selectedRegion);

		if (node != null && node instanceof org.eclipse.jdt.core.dom.TypeDeclaration) {
			if (isPrintable) {
				System.out.println("[DBG] A ROOT NODE OF CLASS: " + //
						((org.eclipse.jdt.core.dom.TypeDeclaration) node).getName().getFullyQualifiedName());
			}
			return (org.eclipse.jdt.core.dom.TypeDeclaration) node;
		}
		return null;
	}

	private ASTNode findingCoveringTypeDeclaration(CompilationUnit cu, Point selectedRegion) {
		NodeFinder nodeFinder = new NodeFinder(cu, selectedRegion.x, selectedRegion.y);
		return nodeFinder == null ? null : findClassASTNode(nodeFinder.getCoveringNode());
	}

	/**
	 * Find method.
	 * 
	 * @param aSource the a source
	 * @param aRegion the a region
	 * @param isPrintable the is printable
	 * @return the method declaration
	 */
	public MethodDeclaration findMethod(String aSource, SourceRange aRegion, boolean isPrintable) {
		ASTNode node = findCoveringMethodDeclaration(astParser.parse(aSource), //
				new Point(aRegion.getStart(), aRegion.getEnd() - aRegion.getStart()));
		if (node != null && node instanceof MethodDeclaration) {
			if (isPrintable) {
				System.out.println("[DBG] A ROOT NODE OF METHOD: " + //
						((MethodDeclaration) node).getName().getFullyQualifiedName());
			}
			return (MethodDeclaration) node;
		}
		return null;
	}

	/**
	 * Find method.
	 * 
	 * @param aSource the a source
	 * @param aRegion the a region
	 * @param isPrintable the is printable
	 * @return the method declaration
	 */
	public MethodDeclaration findMethod(String path, Point aRegion, boolean isPrintable) {
		ASTNode node = findCoveringMethodDeclaration(astParser.parse(UTFile.getContents(path)), aRegion);
		if (node != null && node instanceof MethodDeclaration) {
			if (isPrintable) {
				System.out.println("[DBG] A ROOT NODE OF METHOD: " + //
						((MethodDeclaration) node).getName().getFullyQualifiedName());
			}
			return (MethodDeclaration) node;
		}
		return null;
	}

	/**
	 * Find covering i method.
	 * 
	 * @param icu the icu
	 * @param selectedRegion the selected region
	 * @return the i method
	 */
	public IMethod findCoveringIMethod(ICompilationUnit icu, Point selectedRegion) {
		try {
			IType[] types = icu.getTypes();
			for (IType type : types) {
				IMethod[] methods = type.getMethods();
				for (IMethod method : methods) {
					ISourceRange rangeOfTheMethod = method.getSourceRange();
					int xRangeOfTheMethod = rangeOfTheMethod.getOffset();
					int yRangeOfTheMethod = xRangeOfTheMethod + rangeOfTheMethod.getLength();
					if (xRangeOfTheMethod <= selectedRegion.x && //
							(selectedRegion.x + selectedRegion.y <= yRangeOfTheMethod)) {
						return method;
					}
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Find covering AST node of method declaration.
	 * 
	 * @param cu the compilation unit
	 * @param selectedRegion the selected region
	 * @return the AST node
	 */
	public MethodDeclaration findCoveringMethodDeclaration(CompilationUnit cu, Point selectedRegion) {
		NodeFinder nodeFinder = new NodeFinder(cu, selectedRegion.x, selectedRegion.y);
		return nodeFinder == null ? null : findASTNode(nodeFinder.getCoveringNode());
	}

	/**
	 * Find method declaration.
	 * 
	 * @param node the node
	 * @return the aST node
	 */
	public MethodDeclaration findASTNode(ASTNode node) {
		if (node != null) {
			if (node instanceof MethodDeclaration) {
				return (MethodDeclaration) node;
			} else {
				return findASTNode(node.getParent());
			}
		}
		return null;
	}

	/**
	 * Find class declaration
	 * 
	 * @param node
	 * @return
	 */
	public org.eclipse.jdt.core.dom.TypeDeclaration findClassASTNode(ASTNode node) {
		if (node != null) {
			if (node instanceof org.eclipse.jdt.core.dom.TypeDeclaration) {
				return (org.eclipse.jdt.core.dom.TypeDeclaration) node;
			} else {
				return findClassASTNode(node.getParent());
			}
		}
		return null;
	}

	/**
	 * Find ast node.
	 * 
	 * @param refRootNode the ref root node
	 * @param aSrcEntity the a src entity
	 * @return the node
	 */
	public Node findASTNode(Node refRootNode, SourceCodeEntity aSrcEntity) {
		Enumeration<?> e = refRootNode.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node node = (Node) e.nextElement();
			SourceCodeEntity iSrcEntity = node.getEntity();
			int posBgn = iSrcEntity.getStartPosition();
			int posEnd = iSrcEntity.getEndPosition();
			if (aSrcEntity.getStartPosition() == posBgn && aSrcEntity.getEndPosition() == posEnd) {
				// System.out.println("[DBG3] (" + posBgn + ", " + posEnd + ")");
				return node;
			}
		}
		return null;
	}

	/**
	 * Find covering ast node.
	 * 
	 * @param icu the icu
	 * @param selectedRegion the selected region
	 * @return the aST node
	 */
	public ASTNode findCoveringASTNode(ICompilationUnit icu, Point selectedRegion) {
		NodeFinder nodeFinder = new NodeFinder(astParser.parse(icu), selectedRegion.x, selectedRegion.y);
		return nodeFinder.getCoveringNode();
	}

	/**
	 * Find covered ast node.
	 * 
	 * @param icu the icu
	 * @param selectedRegion the selected region
	 * @return the aST node
	 */
	public ASTNode findCoveredASTNode(ICompilationUnit icu, Point selectedRegion) {
		NodeFinder nodeFinder = new NodeFinder(astParser.parse(icu), selectedRegion.x, selectedRegion.y);
		return nodeFinder.getCoveredNode();
	}

	/**
	 * Find covering ast node.
	 * 
	 * @param icu the icu
	 * @param selectedRegion the selected region
	 * @return the aST node
	 */
	public ASTNode findCoveringASTNode(String path, Point selectedRegion) {
		NodeFinder nodeFinder = new NodeFinder(astParser.parse(UTFile.getContents(path)), selectedRegion.x, selectedRegion.y);
		return nodeFinder.getCoveringNode();
	}

	/**
	 * Find covered ast node.
	 * 
	 * @param icu the icu
	 * @param selectedRegion the selected region
	 * @return the aST node
	 */
	public ASTNode findCoveredASTNode(String path, Point selectedRegion) {
		NodeFinder nodeFinder = new NodeFinder(astParser.parse(UTFile.getContents(path)), selectedRegion.x, selectedRegion.y);
		return nodeFinder.getCoveredNode();
	}

	public String getSource() {
		return mSource;
	}

	public void setSource(String aSource) {
		mSource = aSource;
	}

	public File getFile() {
		return mFile;
	}

	public void setFile(File aFile) {
		mFile = aFile;
	}
}
