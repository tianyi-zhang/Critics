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
package ut.seal.plugins.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.swt.graphics.Point;
import org.junit.Test;

import ut.seal.plugins.utils.ast.UTASTNodeConverter;
import ut.seal.plugins.utils.ast.UTASTNodeFinder;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import edu.utexas.seal.plugins.util.UTCriticsDiffUtil;
import edu.utexas.seal.plugins.util.UTCriticsDiffUtil.Diff;
import edu.utexas.seal.plugins.util.UTCriticsDiffUtil.Operation;

/**
 * The Class UTChange.
 */
public class UTChange {

	public UTChange() {
	}

	@Test
	public void testCheckEQByDiff() {
		// SourceCodeEntity entity = new SourceCodeEntity("", JavaEntityType.ASSIGNMENT, new SourceRange(1, 2));
		// if (qNode.toString().contains("(value: pizza = new ClamPizza($var);)(label: ASSIGNMENT)") && //
		// tNode.toString().contains("(value: pizza = new ClamPizza(californiaFactory);)(label: ASSIGNMENT)")) {
		// System.out.print("");
		// }
		Node qNode = new Node(JavaEntityType.ASSIGNMENT, "$var = new ClamPizza($var);");
		Node tNode = new Node(JavaEntityType.ASSIGNMENT, "pizza = new ClamPizza(californiaFactory);");
		boolean checkEQByDiff = UTChange.checkEQByDiff(qNode, tNode);
		System.out.println("[DBG] " + checkEQByDiff);

		qNode = new Node(JavaEntityType.ASSIGNMENT, "$var = new ClamPizza($var);");
		tNode = new Node(JavaEntityType.ASSIGNMENT, "pizza = new ClamPizza2(californiaFactory);");
		checkEQByDiff = UTChange.checkEQByDiff(qNode, tNode);
		System.out.println("[DBG] " + checkEQByDiff);
	}

	/**
	 * Check eq by diff.
	 * 
	 * @param aQNode the a q node
	 * @param aTNode the a t node
	 * @return true, if successful
	 */
	public static boolean checkEQByDiff(Node aQNode, Node aTNode) {
		String aQNodeValue = aQNode.getValueParm();
		if (aQNodeValue == null) {
			aQNodeValue = aQNode.getValue();
		}
		aQNodeValue = aQNodeValue.replaceAll(UTCfg.PARM_VAR_NAME, UTCfg.PARM_VAR_NAME_TMP);
		String aTNodeValue = aTNode.getValue();
		UTCriticsDiffUtil diffUtil = new UTCriticsDiffUtil();
		List<Diff> diffList = diffUtil.diff_main(aQNodeValue, aTNodeValue);
		List<String> lstDel = new ArrayList<String>();
		List<String> lstIns = new ArrayList<String>();
		for (Diff diff : diffList) {
			if (diff.operation == Operation.DELETE) {
				lstDel.add(diff.text);
			} else if (diff.operation == Operation.INSERT) {
				lstIns.add(diff.text);
			}
		}
		if (lstDel.size() != lstIns.size()) {
			return false;
		}
		for (String string : lstDel) {
			if (!string.startsWith("$")) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Gets the node list method level.
	 * 
	 * @param aLstChange the a lst change
	 * @param aUnitMatchedNode the a unit matched node
	 * @param aSrcMatchedNode the a src matched node
	 * @param aFile the a file
	 * @return the node list method level
	 */
	public static List<Node> getNodeListMethodLevel(List<SourceCodeChange> aLstChange, //
			CompilationUnit aUnitMatchedNode, String aSrcMatchedNode, File aFile) {
		List<Node> lstNode = new ArrayList<Node>();
		Node methodNode = null;
		for (int i = 0; i < aLstChange.size(); i++) {
			SourceCodeChange iChange = aLstChange.get(i);
			if (methodNode == null) {
				methodNode = getMethodNode(iChange, aUnitMatchedNode, aSrcMatchedNode, aFile);
			}
			Node nodes = getChildNode(iChange, methodNode);
			lstNode.add(nodes);
		}
		return lstNode;
	}

	/**
	 * Gets the method node.
	 * 
	 * @param change the change
	 * @param aUnit the a unit
	 * @param aSource the a source
	 * @param aFile the a file
	 * @return the method node
	 */
	private static Node getMethodNode(SourceCodeChange change, CompilationUnit aUnit, String aSource, File aFile) {
		String message = "[WRN] null pointing";
		int startPosition = change.getChangedEntity().getStartPosition();
		int defaultLength = 1;
		UTASTNodeFinder finder = new UTASTNodeFinder();
		UTASTNodeConverter converter = new UTASTNodeConverter();
		MethodDeclaration methodDecl = finder.findCoveringMethodDeclaration(aUnit, new Point(startPosition, defaultLength));
		if (methodDecl == null)
			throw new RuntimeException(message);
		Node resultNodeConverted = converter.convertMethod(methodDecl, aSource, aFile);
		if (resultNodeConverted == null)
			throw new RuntimeException(message);
		return resultNodeConverted;
	}

	/**
	 * Gets the child node.
	 * 
	 * @param aChange the a change
	 * @param aMethodNode the a method node
	 * @return the child node
	 */
	private static Node getChildNode(SourceCodeChange aChange, Node aMethodNode) {
		String label = aChange.getChangedEntity().getLabel();
		int startPosition = aChange.getChangedEntity().getStartPosition();
		Enumeration<?> e = aMethodNode.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			String iLabel = iNode.getEntity().getLabel();
			int iBgnOffset = iNode.getEntity().getStartPosition();
			if (iBgnOffset >= startPosition && label.equals(iLabel))
				return iNode;
		}
		return null;
	}

	/**
	 * Prints the change.
	 * 
	 * @param aList the a list
	 * @param isPrnt the is prnt
	 */
	public static void printChange(List<SourceCodeChange> aList, boolean isPrnt) {
		for (int i = 0; i < aList.size(); i++) {
			SourceCodeChange change = aList.get(i);
			UTLog.println(isPrnt, "[RST] " + change.getChangedEntity().getLabel() + " " + change.getChangedEntity().getUniqueName());
		}
	}

	/**
	 * Prints the node.
	 * 
	 * @param aList the a list
	 * @param isPrnt the is prnt
	 */
	public static void printNode(List<Node> aList, boolean isPrnt) {
		for (int i = 0; i < aList.size(); i++) {
			Node change = aList.get(i);
			UTLog.println(isPrnt, "[RST] " + change.getEntity().getLabel() + " " + change.getEntity().getUniqueName());
		}
	}
}
