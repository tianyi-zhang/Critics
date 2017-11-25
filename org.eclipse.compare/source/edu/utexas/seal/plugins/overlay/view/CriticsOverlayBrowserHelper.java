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
package edu.utexas.seal.plugins.overlay.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import ut.seal.plugins.utils.UTChange;
import ut.seal.plugins.utils.UTFile;
import ut.seal.plugins.utils.ast.UTASTNodeConverter;
import ut.seal.plugins.utils.ast.UTASTNodeFinder;
import ut.seal.plugins.utils.ast.UTASTParser;
import ut.seal.plugins.utils.change.UTChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.EntityType;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.Insert;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.LeafPair;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.NodeForStmt;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.NodePair;
import edu.utexas.seal.plugins.overlay.ICriticsHTMLKeyword;
import edu.utexas.seal.plugins.overlay.model.CriticsCBTreeNode;
import edu.utexas.seal.plugins.util.UTCriticsPairFileInfo;

/**
 * @author Myoungkyu Song
 * @date Jan 19, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsOverlayBrowserHelper implements ICriticsHTMLKeyword {
	private List<String>	mLstSrcHtml	= new ArrayList<String>();
	private List<Object[]>	mlstDiff	= new ArrayList<Object[]>();
	private Browser			mBrowser	= null;

	/**
	 * Instantiates a new critics overlay browser helper.
	 * 
	 * @param aLstDiff the a lst diff
	 * @param aBrowser the a browser
	 */
	public CriticsOverlayBrowserHelper(List<Object[]> aLstDiff, Browser aBrowser) {
		mlstDiff = aLstDiff;
		mBrowser = aBrowser;
	}

	/**
	 * Gets the node.
	 * 
	 * @param change the change
	 * @param aUnit the a unit
	 * @param aSource the a source
	 * @param aFile
	 * @return the node
	 */
	public Node getNode(SourceCodeChange change, CompilationUnit aUnit, String aSource, File aFile) {
		String message = "[WRN] null pointing";
		int startPosition = change.getChangedEntity().getStartPosition();
		int defaultLength = 1;
		UTASTNodeFinder finder = new UTASTNodeFinder();
		UTASTNodeConverter converter = new UTASTNodeConverter();
		MethodDeclaration methodDecl = finder.findCoveringMethodDeclaration(aUnit, new Point(startPosition, defaultLength));
		if (methodDecl == null)
			throw new RuntimeException(message);
		Node node = converter.convertMethod(methodDecl, aSource, aFile);
		if (node == null)
			throw new RuntimeException(message);
		Enumeration<?> e = node.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			int iBgnOffset = iNode.getEntity().getStartPosition();
			if (iBgnOffset >= startPosition)
				return iNode;
		}
		return null;
	}

	/**
	 * Gets the precedent inserted node.
	 * 
	 * @param aCurNode the a cur node
	 * @param aIndexChanges the a index changes
	 * @param aChanges the a changes
	 * @param aUnit the a unit
	 * @param aSource the a source
	 * @return the precedent inserted node
	 */
	// private String getLabel(Insert aEdit) {
	// return getLabel(aEdit.getChangedEntity().getLabel());
	// }

	/**
	 * Gets the precedent inserted node.
	 * 
	 * @param aCurNode the a cur node
	 * @param aIndexChanges the a index changes
	 * @param aChanges the a changes
	 * @param aUnit the a unit
	 * @param aSource the a source
	 * @return the precedent inserted node
	 */
	private Node getPrecedentInsertedNode(Node aCurNode, int aIndexChanges, //
			List<?> aChanges, CompilationUnit aUnit, String aSource, File aFile) {
		int bgnIndexInsertNode = -1;
		int bgnIndexCurNode = aCurNode.getEntity().getStartPosition();
		if (aChanges == null) {
			return null;
		}
		for (int i = aIndexChanges; i < aChanges.size(); i++) {
			SourceCodeChange change = (SourceCodeChange) aChanges.get(i);
			if (change instanceof Insert) {
				Insert iChangeInsert = (Insert) change;
				bgnIndexInsertNode = iChangeInsert.getChangedEntity().getStartPosition();
				if (bgnIndexInsertNode < bgnIndexCurNode) {
					return getNode(change, aUnit, aSource, aFile); // insert; // break SEARCH;
				}
			}
		}
		return null;
	}

	/**
	 * Gets the tab.
	 * 
	 * @param aStr the a str
	 * @return the tab
	 */
	int getTab(String aStr) {
		int cnt = 0;
		for (int i = 0; i < aStr.length(); i++) {
			char c = aStr.charAt(i);
			if (c == '\t') {
				cnt++;
			} else {
				break;
			}
		}
		return cnt;
	}

	/**
	 * Gets the space.
	 * 
	 * @param aSize the a size
	 * @return the space
	 */
	String getSpace(int aSize) {
		String htmlSpace = "&nbsp;" + "&nbsp;" + "&nbsp;" + "&nbsp;";
		String result = "";
		for (int i = 0; i < aSize; i++) {
			result += htmlSpace;
		}
		return result;
	}

	/**
	 * Checks if is method wrapper head.
	 * 
	 * @param aNodeLeft the a node left
	 * @param aNodeRight the a node right
	 * @return true, if is method wrapper head
	 */
	private boolean isMethodWrapperHead(Node aNodeLeft, Node aNodeRight) {
		return (aNodeLeft.getEntity().getStartPosition() < 0 || aNodeRight.getEntity().getStartPosition() < 0);
	}

	/**
	 * Gets the color code.
	 * 
	 * @param aIndexOverlayColor the a index overlay color
	 * @param aCode the a code
	 * @return the color code
	 */
	String getColorCode(int aIndexOverlayColor, String aCode) {
		String result = "", defaultResult = "<span class=\"a\">";
		switch (aIndexOverlayColor) {
		case 0:
			result = defaultResult + aCode.replaceAll(" ", HTML_Space) + "</span>" + HTML_BR;
			break;
		default:
			result = aCode.replaceAll(" ", HTML_Space) + HTML_BR;
			break;
		}
		return result;
	}

	/**
	 * Sets the source.
	 * 
	 * @param aSrc
	 */
	void setSource(List<String> alstSource) {
		mLstSrcHtml.clear();
		mLstSrcHtml.add("<html>");
		mLstSrcHtml.add("<head>");
		mLstSrcHtml.add("<style type=\"text/css\">");
		mLstSrcHtml.add("p.a { color:#4C4C4C; font-weight:bold;font-family:Monaco;font-size: 12px }");
		mLstSrcHtml.add("span.a { color:#FF0000;font-weight:bold;font-family:Monaco;font-size: 12px }");
		mLstSrcHtml.add("</style>");
		mLstSrcHtml.add("</head>");
		mLstSrcHtml.add("<body>");
		mLstSrcHtml.add("<p class=\"a\">");
		mLstSrcHtml.addAll(alstSource);
		mLstSrcHtml.add("</p>");
		mLstSrcHtml.add("</body>");
		mLstSrcHtml.add("</html>");
		String buf = "";
		for (int i = 0; i < mLstSrcHtml.size(); i++) {
			String elem = mLstSrcHtml.get(i);
			buf += elem;
		}
		mBrowser.setText(buf);
	}

	/**
	 * Sets the source.
	 * 
	 * @param aNode
	 *            the new source
	 */
	void setSource(CriticsCBTreeNode aNode) {
		if (aNode.getSource() == null) {
			return;
		}
		String htmlBR = "<br>";
		mLstSrcHtml.clear();
		mLstSrcHtml.add("<html>");
		mLstSrcHtml.add("<head>");
		mLstSrcHtml.add("<style type=\"text/css\">");
		mLstSrcHtml.add("p.a { color:#4C4C4C; font-weight:bold;font-family:Monaco;font-size: 12px }");
		mLstSrcHtml.add("span.a { color:#FF0000;font-weight:bold;font-family:Monaco;font-size: 12px }");
		mLstSrcHtml.add("</style>");
		mLstSrcHtml.add("</head>");
		mLstSrcHtml.add("<body>");
		mLstSrcHtml.add("<p class=\"a\">");

		String src = aNode.getSource();
		String srcFormatted = sourceFormatter(src);
		String[] arSrc = srcFormatted.split(System.getProperty("line.separator"));

		for (int i = 0; i < arSrc.length; i++) {
			String elem = arSrc[i];
			String htmlSpace = getSpace(getTab(elem));
			mLstSrcHtml.add(htmlSpace + elem + htmlBR);
		}
		mLstSrcHtml.add("</p>");
		mLstSrcHtml.add("</body>");
		mLstSrcHtml.add("</html>");
		String buf = "";
		for (int i = 0; i < mLstSrcHtml.size(); i++) {
			String elem = mLstSrcHtml.get(i);
			buf += elem;
		}
		mBrowser.setText(buf);
	}

	/**
	 * Source formatter.
	 * 
	 * @param code the code
	 * @return the string
	 */
	private String sourceFormatter(String code) {
		CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(null);
		TextEdit textEdit = codeFormatter.format(CodeFormatter.K_UNKNOWN, code, 0, code.length(), 0, null);
		IDocument doc = new Document(code);
		try {
			textEdit.apply(doc);
			// System.out.println(doc.get());
		} catch (MalformedTreeException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return doc.get();
	}

	/**
	 * Sets the source.
	 */
	void setSource() {
		List<Node> lstNodes = new ArrayList<Node>();
		for (int i = 0; i < mlstDiff.size(); i++) {
			Object elem[] = mlstDiff.get(i);
			CriticsCBTreeNode iTargetCBTNode = (CriticsCBTreeNode) elem[1];
			File iTargetFile = iTargetCBTNode.getSubtreeInfo().getFullFilePath();
			String iTargetSource = UTFile.getContents(iTargetFile.getAbsolutePath());
			CompilationUnit iUnit = new UTASTParser().parse(iTargetSource);
			List<?> iChanges = (List<?>) ((UTChangeDistiller) elem[2]).getChanges();
			List<?> iMatches = (List<?>) ((UTChangeDistiller) elem[2]).getMatches();

			for (int j = 0, indexChanges = 0; j < iMatches.size(); j++) {
				Node iNodeLeft = null, iNodeRight = null;
				if (iMatches.get(j) instanceof NodePair) {
					NodePair iNodePair = (NodePair) iMatches.get(j);
					iNodeLeft = iNodePair.getLeft();
					iNodeRight = iNodePair.getRight();
				}
				if (iMatches.get(j) instanceof LeafPair) {
					LeafPair iLeafPair = (LeafPair) iMatches.get(j);
					iNodeLeft = iLeafPair.getLeft();
					iNodeRight = iLeafPair.getRight();
				}
				if (isMethodWrapperHead(iNodeLeft, iNodeRight)) {
					continue;
				}
				Node iNodeInsert = getPrecedentInsertedNode(iNodeLeft, indexChanges, iChanges, iUnit, iTargetSource, iTargetFile);
				if (iNodeInsert != null) {
					iNodeInsert.setIndexOverlayColor(i);
					lstNodes.add(iNodeInsert);
					setColorNode_v1(iNodeInsert, lstNodes);
					indexChanges++;
					j--;
				} else {
					iNodeLeft.setIndexOverlayColor(-1);
					lstNodes.add(iNodeLeft);
				}
			}
		}
	}

	/**
	 * Reform node list.
	 * 
	 * @param aNodeInsert the a node insert
	 * @param alstNodes the alst nodes
	 */
	private void setColorNode_v1(Node aNodeInsert, List<Node> alstNodes) {
		if (aNodeInsert.getEntity().getLabel().equals("IF_STATEMENT")) {
			Node ndFirstChild = (Node) aNodeInsert.getFirstChild();
			ndFirstChild.setIndexOverlayColor(aNodeInsert.getIndexOverlayColor());
			alstNodes.add(ndFirstChild);
		}
	}

	public void setColorNode(Node aNode, List<Node> alstNodes, String aSpanValue, int index) {
		if (aNode.getEntity().getLabel().equals("IF_STATEMENT")) {
			Node ndFirstChild = (Node) aNode.getFirstChild();
			ndFirstChild.setHtmlStyle(aSpanValue);
		}
	}

	public void setAllDisableShow(Node aNode) {
		Enumeration<?> eBreadFirst;
		eBreadFirst = aNode.breadthFirstEnumeration();
		while (eBreadFirst.hasMoreElements()) {
			Node iNode = (Node) eBreadFirst.nextElement();
			JavaEntityType entityLabel = (JavaEntityType) iNode.getLabel();
			if (iNode.getLevel() == 1) {
				setDisableShow(iNode);
			} else if (entityLabel == JavaEntityType.THEN_STATEMENT || //
					entityLabel == JavaEntityType.BODY || //
					entityLabel == JavaEntityType.CATCH_CLAUSES) {
				iNode.setHTMLEnable(false);
			}
		}
	}

	public void setAllDisableShow(List<Node> nodeList) {
		for (int i = 0; i < nodeList.size(); i++) {
			Node iNode = nodeList.get(i);
			setDisableShow(iNode);
		}
	}

	public void setDisableShow(Node aNode) {
		JavaEntityType entityLabel = (JavaEntityType) aNode.getLabel();
		if (entityLabel == JavaEntityType.MODIFIER || //
				entityLabel == JavaEntityType.MODIFIERS || //
				entityLabel == JavaEntityType.SINGLE_TYPE || //
				entityLabel == JavaEntityType.TYPE_PARAMETERS || //
				entityLabel == JavaEntityType.PARAMETERS || //
				entityLabel == JavaEntityType.PARAMETER || //
				entityLabel == JavaEntityType.THROW) { //
			aNode.setHTMLEnable(false);
			aNode.setChildHTMLShow(false);
		}
	}

	/**
	 * Extract delete node list.
	 * 
	 * @return the list
	 */
	public List<Node> extractDeleteNodeList(List<SourceCodeChange> aLstDelete) {
		List<Node> lstDeleteNode = new ArrayList<Node>();
		Collections.sort(aLstDelete, new Comparator<SourceCodeChange>() {
			@Override
			public int compare(SourceCodeChange o1, SourceCodeChange o2) {
				return o1.getChangedEntity().getStartPosition() - o2.getChangedEntity().getStartPosition();
			}
		});
		File secondNodeFile = UTCriticsPairFileInfo.getRightFile();
		String srcSecNode = UTFile.getContents(secondNodeFile.getAbsolutePath());
		CompilationUnit unitSecNode = new UTASTParser().parse(srcSecNode);
		List<Node> lstDeleteNodeCopy = UTChange.getNodeListMethodLevel(aLstDelete, unitSecNode, srcSecNode, secondNodeFile);
		List<Integer> lstIndexToSkip = getListToSkipInForStmt(aLstDelete, lstDeleteNodeCopy);
		for (int i = 0; i < aLstDelete.size(); i++) {
			if (lstIndexToSkip.contains(i)) {
				continue;
			}
			SourceCodeChange iDelete = aLstDelete.get(i);
			SourceCodeEntity entity = iDelete.getChangedEntity();
			EntityType entityType = entity.getType();
			String value = entity.getUniqueName();
			Node iNewNode = new Node(entityType, value);
			iNewNode.setEntity(entity);
			iNewNode.setDelete(true);
			for (int j = 0; j < lstDeleteNodeCopy.size(); j++) {
				Node jNode = lstDeleteNodeCopy.get(j);
				if (jNode.getEntity().getStartPosition() == iDelete.getChangedEntity().getStartPosition()) {
					iNewNode.setLevel(jNode.getLevel());
					iNewNode.setForStmt(jNode.getForStmt());
					break;
				}
			}
			lstDeleteNode.add(iNewNode);
		}
		return lstDeleteNode;
	}

	public List<Integer> getListToSkipInForStmt(List<SourceCodeChange> aLstChage, List<Node> aLstNodeCopy) {
		List<NodeForStmt> forStmtList = getForStmt(aLstChage, aLstNodeCopy);
		List<Integer> lstIndexToSkip = new ArrayList<Integer>();
		for (int i = 0; i < aLstChage.size(); i++) {
			SourceCodeChange iChange = aLstChage.get(i);
			for (int j = 0; j < forStmtList.size(); j++) {
				List<Node> lstForIncr = forStmtList.get(j).forIncr;
				for (int k = 0; k < lstForIncr.size(); k++) {
					Node kNode = lstForIncr.get(k);
					if (kNode.getEntity().getStartPosition() == iChange.getChangedEntity().getStartPosition()) {
						lstIndexToSkip.add(i);
					}
					Enumeration<?> children = kNode.children();
					while (children.hasMoreElements()) {
						Node child = (Node) children.nextElement();
						if (child.getEntity().getStartPosition() == iChange.getChangedEntity().getStartPosition()) {
							lstIndexToSkip.add(i);
						}
					}
				}
				List<Node> lstForInit = forStmtList.get(j).forInit;
				for (int k = 0; k < lstForInit.size(); k++) {
					Node kNode = lstForInit.get(k);
					if (kNode.getEntity().getStartPosition() == iChange.getChangedEntity().getStartPosition()) {
						lstIndexToSkip.add(i);
					}
					Enumeration<?> children = kNode.children();
					while (children.hasMoreElements()) {
						Node child = (Node) children.nextElement();
						if (child.getEntity().getStartPosition() == iChange.getChangedEntity().getStartPosition()) {
							lstIndexToSkip.add(i);
						}
					}
				}
			}
		}
		return lstIndexToSkip;
	}

	public List<NodeForStmt> getForStmt(List<SourceCodeChange> aLstChange, List<Node> aLstNodeCopy) {
		boolean isForStmt = false;
		List<NodeForStmt> lstForStmt = new ArrayList<NodeForStmt>();
		for (int i = 0; i < aLstChange.size(); i++) {
			SourceCodeChange iDelete = aLstChange.get(i);
			SourceCodeEntity entity = iDelete.getChangedEntity();
			EntityType entityType = entity.getType();
			if (entityType == JavaEntityType.FOR_STATEMENT) {
				isForStmt = true;
			}
			for (int j = 0; j < aLstNodeCopy.size(); j++) {
				Node jNode = aLstNodeCopy.get(j);
				if (isForStmt && jNode.getEntity().getStartPosition() == //
						iDelete.getChangedEntity().getStartPosition()) {
					isForStmt = false;
					lstForStmt.add(jNode.getForStmt());
				}
			}
		}
		return lstForStmt;
	}

	class HTMLSpanGroup {
		public void startGroup(List<String> lstHtmlContents) {
			String pre = null, cur = null;
			Integer iPre = null, iCur = null;
			List<Integer> lstDuplicatedLine = new ArrayList<Integer>();
			List<Integer> lstLastLine = new ArrayList<Integer>();
			List<Integer> lstFirstLine = new ArrayList<Integer>();
			List<Integer> lstFirstLineDup = new ArrayList<Integer>();
			for (int i = 0; i < lstHtmlContents.size(); i++) {
				String line = lstHtmlContents.get(i);
				if (line.trim().startsWith(HTML_SPAN_PRFIX)) {
					String key = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
					pre = cur;
					cur = key;
					iPre = iCur;
					iCur = i;
					if (pre != null && pre.equals(cur)) {
						lstDuplicatedLine.add(i);
					}
					if (pre != null && !pre.equals(cur)) {
						lstFirstLine.add(iCur);
					}
					if (pre != null && !pre.equals(cur)) {
						lstLastLine.add(iPre);
					}
				}
			}
			lstLastLine.add(iCur);

			String nextKey = null, curKey = null;
			for (int i = 0; i < lstHtmlContents.size(); i++) {
				String line = lstHtmlContents.get(i);
				if (line.trim().startsWith(HTML_SPAN_PRFIX)) {
					String key = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
					curKey = key;
					nextKey = getNextSpanBegin(lstHtmlContents, i);
					if (checkFirstLine(lstFirstLine, i) && nextKey != null && nextKey.equals(curKey)) {
						lstFirstLineDup.add(i);
					}
				}
			}
			for (int i = 0; i < lstDuplicatedLine.size(); i++) {
				Integer iLineNum = lstDuplicatedLine.get(i);
				Integer pair = getPair(lstHtmlContents, iLineNum);
				lstHtmlContents.set(iLineNum, "");
				if (isRemovable(lstLastLine, iLineNum))
					lstHtmlContents.set(pair, "");
			}
			for (int i = 0; i < lstFirstLineDup.size(); i++) {
				Integer iLineNum = lstFirstLineDup.get(i);
				Integer pair = getPair(lstHtmlContents, iLineNum);
				lstHtmlContents.set(pair, "");
			}
		}

		private String getNextSpanBegin(List<String> list, int index) {
			for (int i = index + 1; i < list.size(); i++) {
				String iLine = list.get(i);
				if (iLine.trim().startsWith(HTML_SPAN_PRFIX)) {
					String key = iLine.substring(iLine.indexOf("\"") + 1, iLine.lastIndexOf("\""));
					return key;
				}
			}
			return null;
		}

		private boolean checkFirstLine(List<Integer> aLst, int index) {
			for (int i = 0; i < aLst.size(); i++) {
				Integer iLineNum = aLst.get(i);
				if (iLineNum == index) {
					return true;
				}
			}
			return false;
		}

		private boolean isRemovable(List<Integer> aLst, Integer aLineNum) {
			for (int i = 0; i < aLst.size(); i++) {
				Integer iLineNumNotRemove = aLst.get(i);
				if (iLineNumNotRemove == aLineNum)
					return false;
			}
			return true;
		}

		int getPair(List<String> aLst, int index) {
			for (int i = index; i < aLst.size(); i++) {
				String elem = aLst.get(i);
				if (elem.equals(HTML_SPAN_END)) {
					return i;
				}
			}
			return -1;
		}
	}

	// private void inspectMatches(Object[] aMatch) {
	// CriticsCBTreeNode matchedCBTNode = (CriticsCBTreeNode) aMatch[0];
	// File fileMatchedNode = matchedCBTNode.getSubtreeInfo().getFullFilePath();
	// String srcMatchedNode = UTFile.getContents(fileMatchedNode.getAbsolutePath());
	// CompilationUnit unitMatchedNode = new UTASTParser().parse(srcMatchedNode);
	//
	// CriticsCBTreeNode baseCBTNode = (CriticsCBTreeNode) aMatch[1];
	// File fileBaseNode = baseCBTNode.getSubtreeInfo().getFullFilePath();
	// String srcBaseNode = UTFile.getContents(fileBaseNode.getAbsolutePath());
	// CompilationUnit unitBaseNode = new UTASTParser().parse(srcBaseNode);
	//
	// UTChangeDistiller changeDist = (UTChangeDistiller) aMatch[2];
	// List<NodePair> lstMatches = changeDist.getMatches();
	// List<SourceCodeChange> lstInsert = changeDist.getInsertList();
	// List<SourceCodeChange> lstDelete = changeDist.getDeleteList();
	// List<Node> lstBaseNode = new ArrayList<Node>();
	// List<Node> lstInsertNode = new ArrayList<Node>();
	// List<Node> lstDeleteNode = new ArrayList<Node>();
	//
	// for (int j = 0; j < lstMatches.size(); j++) {
	// Node iBaseNode = null;
	// if (lstMatches.get(j) instanceof NodePair) {
	// NodePair iNodePair = (NodePair) lstMatches.get(j);
	// iBaseNode = iNodePair.getRight();
	// }
	// lstBaseNode.add(iBaseNode);
	// Node iMatchedNode = iBaseNode.getMatchedNodeList().get(0);
	// System.out.println("[DBG1] " + iMatchedNode + " <=> " + iBaseNode);
	// }
	// System.out.println("INSERT ------------------------------------------");
	// // /////////////////////////////////////////////////////////
	// Node methodNode = null;
	// for (int i = 0; i < lstInsert.size(); i++) {
	// SourceCodeChange iInsert = lstInsert.get(i);
	// if (methodNode == null) {
	// methodNode = getMethodNode(iInsert, unitBaseNode, srcBaseNode);
	// }
	// Node iInsertNode = getChildNode(iInsert, methodNode);
	// lstInsertNode.add(iInsertNode);
	// System.out.println("[DBG2]" + iInsertNode);
	// }
	// printChange(lstInsert);
	// System.out.println("DELETE ------------------------------------------");
	// // /////////////////////////////////////////////////////////
	// methodNode = null;
	// for (int i = 0; i < lstDelete.size(); i++) {
	// SourceCodeChange iDelete = lstDelete.get(i);
	// if (methodNode == null) {
	// methodNode = getMethodNode(iDelete, unitMatchedNode, srcMatchedNode);
	// }
	// Node iDeleteNode = getChildNode(iDelete, methodNode);
	// lstDeleteNode.add(iDeleteNode);
	// System.out.println("[DBG3]" + iDeleteNode);
	// }
	// printChange(lstDelete);
	// // /////////////////////////////////////////////////////////
	// }
}
