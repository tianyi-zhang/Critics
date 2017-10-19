/*
 * @(#) CriticsHTMLBrowser.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.overlay.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.tree.TreeNode;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.swt.browser.Browser;

import ch.uzh.ifi.seal.changedistiller.model.classifiers.EntityType;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.NodePair;
import ut.seal.plugins.utils.UTCfg;
import ut.seal.plugins.utils.UTChange;
import ut.seal.plugins.utils.UTFile;
import ut.seal.plugins.utils.UTLog;
import ut.seal.plugins.utils.UTStr;
import ut.seal.plugins.utils.ast.UTASTParser;
import ut.seal.plugins.utils.change.UTChangeDistiller;
import edu.utexas.seal.plugins.overlay.ICriticsHTMLKeyword;
import edu.utexas.seal.plugins.overlay.ILayer;
import edu.utexas.seal.plugins.overlay.LayerDeleteBg;
import edu.utexas.seal.plugins.overlay.LayerInsert;
import edu.utexas.seal.plugins.overlay.LayerDelete;
import edu.utexas.seal.plugins.overlay.LayerBase;
import edu.utexas.seal.plugins.overlay.LayerInsertBg;
import edu.utexas.seal.plugins.overlay.LayerMissBg;
import edu.utexas.seal.plugins.overlay.LayerParm;
import edu.utexas.seal.plugins.overlay.LayerRecommendBg;
import edu.utexas.seal.plugins.overlay.LayerSynCol;
import edu.utexas.seal.plugins.overlay.LayerType;
import edu.utexas.seal.plugins.overlay.model.CriticsCBTreeElemType;
import edu.utexas.seal.plugins.overlay.model.CriticsCBTreeNode;
import edu.utexas.seal.plugins.util.UTHtml;
import edu.utexas.seal.plugins.util.UTCriticsHTML.HTML;
import edu.utexas.seal.plugins.util.UTCriticsHTML.HTMLEntityType;
import edu.utexas.seal.plugins.util.UTPlugin;

/**
 * @author Myoungkyu Song
 * @date Dec 28, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsOverlayBrowser implements ICriticsHTMLKeyword {
	protected List<Node>					mBaseCodeNodeList	= new ArrayList<Node>();
	protected CriticsOverlayBrowserHelper	mHelper				= null;
	protected TreeMap<LayerType, ILayer>	mLayers				= new TreeMap<LayerType, ILayer>();

	private boolean							__D					= false;
	private Node							mBaseCodeNode		= null;
	private File							mBaseCodeNodeFile	= null;
	private Browser							mBrowser			= null;
	private UTChangeDistiller				mChangeDistiller	= null;
	private List<Object[]>					mlstDiff			= new ArrayList<Object[]>();
	private List<String>					mLstSrcHtml			= new ArrayList<String>();
	private Node							mSecondNode			= null;
	private File							mSecondNodeFile		= null;

	enum Scope {
		BGN, END
	};

	/**
	 * Instantiates a new critics overlay browser.
	 * 
	 * @param aBrowser the a browser
	 */
	public CriticsOverlayBrowser(Browser aBrowser) {
		mBrowser = aBrowser;
		mHelper = new CriticsOverlayBrowserHelper(mlstDiff, aBrowser);
		mLayers.put(LayerType.BASE, new LayerBase());
		mLayers.put(LayerType.INS, new LayerInsert());
		mLayers.put(LayerType.DEL, new LayerDelete());
		mLayers.put(LayerType.PARM, new LayerParm());
		mLayers.put(LayerType.SYNCOL, new LayerSynCol());
		mLayers.put(LayerType.INSBG, new LayerInsertBg());
		mLayers.put(LayerType.DELBG, new LayerDeleteBg());
		mLayers.put(LayerType.MISSBG, new LayerMissBg());
		mLayers.put(LayerType.RECOMMENDBG, new LayerRecommendBg());
	}

	void initHtmlHeader() {
		mLstSrcHtml.clear();
		mLstSrcHtml.add(HTML_BGN_HEAD);
		mLstSrcHtml.add(HTML_BGN_STYLE);
		for (Map.Entry<LayerType, ILayer> e : mLayers.entrySet()) {
			ILayer value = e.getValue();
			mLstSrcHtml.add(value.getStyleDecl());
		}
		mLstSrcHtml.add(HTML_END_STYLE);
		mLstSrcHtml.add(HTML_END_HEAD);
	}

	/**
	 * Gets the base node list.
	 * 
	 * @param aMatch the a match
	 */
	void getBaseNodeList(Object[] aMatch, List<Node> aBaseCodeNodeList) {
		UTChangeDistiller changeDist = (UTChangeDistiller) aMatch[2];
		List<NodePair> lstMatches = changeDist.getMatches();
		for (NodePair iNodePair : lstMatches) {
			Node iBaseNode = iNodePair.getRight();

			if (iBaseNode.isAnyValueArgParameterized()) {
				System.out.print("");
			}
			Node iMatchedNode = iNodePair.getLeft();
			UTLog.println(__D, "[DBG0] " + iMatchedNode + " <=> " + iBaseNode + " (" + iBaseNode.getEntity().getStartPosition());
			for (Node iNode : aBaseCodeNodeList) {
				if (iNode.eq(iBaseNode)) {
					iNode.setMatchedNode(iMatchedNode);
					break;
				}
			}
		}
	}

	List<Node> getDeleteNodeList() {
		List<Node> lstDeleteNode = new ArrayList<Node>();
		List<SourceCodeChange> lstDelete = mChangeDistiller.getDeleteList();
		Collections.sort(lstDelete, new Comparator<SourceCodeChange>() {
			@Override
			public int compare(SourceCodeChange o1, SourceCodeChange o2) {
				return o1.getChangedEntity().getStartPosition() - o2.getChangedEntity().getStartPosition();
			}
		});
		String srcSecNode = UTFile.getContents(mSecondNodeFile.getAbsolutePath());
		CompilationUnit unitSecNode = new UTASTParser().parse(srcSecNode);
		List<Node> lstDeleteNodeCopy = UTChange.getNodeListMethodLevel(lstDelete, unitSecNode, srcSecNode, mSecondNodeFile);

		List<Integer> lstIndexToSkip = mHelper.getListToSkipInForStmt(lstDelete, lstDeleteNodeCopy);
		for (int i = 0; i < lstDelete.size(); i++) {
			if (lstIndexToSkip.contains(i)) {
				continue;
			}
			SourceCodeChange iDelete = lstDelete.get(i);
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

	List<Node> getInsertNodeList() {
		List<Node> lstInsertNode = new ArrayList<Node>();
		File fileBaseNode = mBaseCodeNodeFile;
		String srcBaseNode = UTFile.getContents(fileBaseNode.getAbsolutePath());
		CompilationUnit unitBaseNode = new UTASTParser().parse(srcBaseNode);
		List<SourceCodeChange> lstInsert = mChangeDistiller.getInsertList();
		List<Node> lstInsertNodeCopy = UTChange.getNodeListMethodLevel(lstInsert, unitBaseNode, srcBaseNode, fileBaseNode);
		for (int i = 0; i < lstInsert.size(); i++) {
			SourceCodeChange iInsert = lstInsert.get(i);
			SourceCodeEntity entity = iInsert.getChangedEntity();
			EntityType entityType = entity.getType();
			String value = entity.getUniqueName();
			Node iNewNode = new Node(entityType, value);
			iNewNode.setEntity(entity);
			iNewNode.setInsert(true);
			for (int j = 0; j < lstInsertNodeCopy.size(); j++) {
				Node jNode = lstInsertNodeCopy.get(j);
				if (jNode.getEntity().getStartPosition() == iInsert.getChangedEntity().getStartPosition()) {
					iNewNode.setLevel(jNode.getLevel());
				}
			}
			lstInsertNode.add(iNewNode);
		}
		return lstInsertNodeCopy;
	}

	private String getInitValueForStmt(List<Node> aLstforInit) {
		String forInit = "";
		for (int i = 0; i < aLstforInit.size(); i++) {
			Node iNode = aLstforInit.get(i);
			forInit += iNode.getValue() + " ";
		}
		return forInit.trim();
	}

	private String getIncrValueForStmt(List<Node> aLstforInit) {
		String forIncr = "";
		for (int i = 0; i < aLstforInit.size(); i++) {
			Node iNode = aLstforInit.get(i);
			forIncr += iNode.getValue() + " ";
		}
		return forIncr.trim().replaceAll("\\s", "");
	}

	String getHtml() {
		System.out.println("------------------------------------------");
		String buf = "";
		for (int i = 0; i < mLstSrcHtml.size(); i++) {
			String elem = mLstSrcHtml.get(i);
			boolean bfAppendDragBgn = false;
			if (elem.trim().startsWith(HTML_BGN_SPAN_LINSERT) || //
					elem.trim().startsWith(HTML_BGN_SPAN_LDELETE)) {
				bfAppendDragBgn = true;
				buf += elem;
				// buf += HTML_BGN_DRAG;
			} else if (elem.trim().startsWith(HTML_SPAN_END) && bfAppendDragBgn) {
				bfAppendDragBgn = false;
				// buf += HTML_BGN_DRAG;
				buf += elem;
			} else {
				buf += elem;
			}
		}
		/* String textdrag_JS = UTCfg.getInst().readConfig().TEXTDRAG_JS;
		String fileContents = UTFile.readFileWithNewLine(textdrag_JS);
		buf += fileContents; */
		return buf;
	}

	/**
	 * Gets the label.
	 * 
	 * @param aNode the a node
	 * @return the label
	 */
	private String getLabel(Node aNode) {
		final String SPACE = " ";
		String label = getLabel(aNode.getLabel().name()).toLowerCase();
		if (label.isEmpty()) {
			return "";
		}
		label = mLayers.get(LayerType.SYNCOL).getStyle() + label + HTML_SPAN_END;
		if (aNode.getEntity().getType() == JavaEntityType.FOR_STATEMENT || //
				aNode.getEntity().getType() == JavaEntityType.FOREACH_STATEMENT || //
				aNode.getEntity().getType() == JavaEntityType.IF_STATEMENT || //
				aNode.getEntity().getType() == JavaEntityType.RETURN_STATEMENT) {
			label = label + SPACE;
		}
		return label;
	}

	/**
	 * Gets the label.
	 * 
	 * @param aLabel the a label
	 * @return the label
	 */
	private String getLabel(String aLabel) {
		if (aLabel.equals("VARIABLE_DECLARATION_STATEMENT") || aLabel.equals("ASSIGNMENT") || //
				aLabel.equals("METHOD_INVOCATION") || aLabel.equals("POSTFIX_EXPRESSION")) {
			return "";
		}
		if (aLabel.endsWith("_STATEMENT")) {
			aLabel = aLabel.replace("_STATEMENT", " ");
		} else if (aLabel.endsWith("_CLAUSE")) {
			aLabel = aLabel.replace("_CLAUSE", " ");
		}
		return aLabel;
	}

	String getMethodSignature(Node aNode) {
		String code = UTStr.getIndent(aNode.getLevel()) + aNode.getValue();
		List<Node> lstParms = new ArrayList<Node>();
		Node ndParms = aNode.getChild(JavaEntityType.PARAMETERS);
		if (ndParms != null) {
			List<Node> children = ndParms.getChildren();
			for (Node iNode : children) {
				if (iNode.getEntity().getType() == JavaEntityType.PARAMETER) {
					lstParms.add(iNode);
				}
			}
		}
		MethodDeclaration methodDecl = aNode.getMethodDeclaration();
		String strParam = "";
		List<?> params = methodDecl.parameters();
		for (int j = 0; j < params.size(); j++) {
			Object elem = params.get(j);
			strParam += getParm(elem.toString(), lstParms) + "-";
		}
		if (!strParam.isEmpty()) {
			strParam = strParam.substring(0, strParam.length() - 1);
			code += "(" + strParam.replace("-", ",") + ")";
		} else {
			code += "(" + ")";
		}
		Type type = methodDecl.getReturnType2();
		code = type.toString() + " " + code;
		return code;
	}

	String getParm(String aParmDecl, List<Node> aParmList) {
		String result = "";
		for (Node iNode : aParmList) {
			String valOrg = iNode.getValue();
			String valParm = iNode.getValueParm();

			if (valParm == null || valOrg == null || valOrg.equals(valParm)) {
				continue;
			}
			String[] arParmDecl = aParmDecl.split("\\s");
			for (String iElem : arParmDecl) {
				if (iElem.equals(valOrg)) {
					result += valParm + " ";
				} else {
					result += iElem + " ";
				}
			}
		}
		if (result.isEmpty()) {
			return aParmDecl;
		} else {
			result = setSourceNodeParm(result.trim());
			return result;
		}
	}

	protected void initiateBaseCodeNode(Node aNode) {
		mBaseCodeNodeList.clear();
		mBaseCodeNodeList.add(new Node(HTMLEntityType.HTML, HTML_BGN_SPAN_LBASE));
		Enumeration<?> e = aNode.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			iNode.setMatchedNode(null);
			iNode.setQTree(true);
			iNode.setHtmlStyle(HTML_BGN_SPAN_LBASE);
			mBaseCodeNodeList.add(iNode);
		}
	}

	public void setSourceBaseNode(Node aNode, File aFile) {
		// aNode.print();
		mBaseCodeNode = aNode;
		mBaseCodeNodeFile = aFile;
		mHelper.setAllDisableShow(mBaseCodeNode);
		initiateBaseCodeNode(mBaseCodeNode);
		setSourceNode(mBaseCodeNodeList);
	}

	public void setSourceNode(List<Node> alstSource) {
		initHtmlHeader();
		int prvLevel = 0, curLevel = 0;
		String curIndent = "";
		for (int i = 0; i < alstSource.size(); i++) {
			Node iNode = alstSource.get(i);
			if (addHtmlNode(iNode)/*should first*/|| !iNode.enableHTML() || isSkipForStmt(iNode)) {
				continue;
			}
			setBackGround(iNode, Scope.BGN);
			setBegin(iNode);
			String code = "";
			if (iNode.getEntity().getType().isMethod()) {
				code = setSourceNodeMethod(iNode) + HTML_BR;
				prvLevel = curLevel;
				curLevel = iNode.getLevel();
			} else {
				prvLevel = curLevel;
				curLevel = iNode.getLevel();
				if (curLevel != 0 && prvLevel != 0 && curLevel - prvLevel > 1) {
					curIndent = UTStr.getIndent(curLevel - 1);
				} else {
					curIndent = UTStr.getIndent(iNode.getLevel());
				}
				curIndent = addDelInsMark(curIndent, iNode);
				code = curIndent.replaceAll(" ", HTML_Space);
				code += getLabel(iNode);
				String codeValue = "";
				if (iNode.isAnyValueArgParameterizedInQeury()) {
					codeValue = iNode.getValueParm();
				} else {
					codeValue = (iNode.isExcludedByUser()) ? "($EXCLUDED)" : iNode.getValue();
				}
				codeValue = getCodeValueForStmt(iNode, codeValue);
				List<String> reduceCodeValue = reduceLength(codeValue, UTStr.getIndent(iNode.getLevel()));
				codeValue = reduceCodeValue.get(0);
				codeValue = codeValue.replaceAll("\\<", HTML_ANGLE_L).replaceAll("\\>", HTML_ANGLE_R);
				codeValue = codeValue.replaceAll(" ", HTML_Space);
				if (reduceCodeValue.size() == 2) {
					codeValue = reduceCodeValue.get(0) + reduceCodeValue.get(1);
				}
				codeValue += HTML_BR;
				code += codeValue;
			}
			if (iNode.isAnyValueArgParameterizedInQeury()) {
				code = setSourceNodeParm(code);
			}
			mLstSrcHtml.add(code);
			setEnd(iNode);
			setBackGround(iNode, Scope.END);
		}
		// mHelper.new HTMLSpanGroup().startGroup(mLstSrcHtml);
		String buf = getHtml();
		String test_html = UTCfg.getInst().readConfig().INPUTDIRPATH + System.getProperty("file.separator") + "test.html";
		UTFile.writeFile(test_html, mLstSrcHtml);
		System.out.println(buf);
		mBrowser.setText(buf);
	}

	private List<String> reduceLength(String codeValue, String curIndent) {
		List<String> list = new ArrayList<String>();
		if (codeValue.length() + curIndent.length() > 101) {
			list.add(codeValue.substring(0, 100 - curIndent.length()));
			list.add("<span title=\"" + codeValue + "\">...</span>");
			return list;
		} else {
			list.add(codeValue);
		}
		return list;
	}

	private String addDelInsMark(String aVal, Node aNode) {
		if (aNode.getLabel() == HTMLEntityType.HTML) {
			return aVal;
		}
		if (aVal.startsWith(" ")) {
			String newVal = "";
			if (aNode.isInsert()) {
				newVal = "+" + aVal.substring(1);
			} else if (aNode.isDelete()) {
				newVal = "-" + aVal.substring(1);
			}
			if (!newVal.isEmpty()) {
				return newVal;
			}
		}
		return aVal;
	}

	private boolean addHtmlNode(Node aNode) {
		if (aNode.getLabel() == HTMLEntityType.HTML) {
			mLstSrcHtml.add(aNode.getValue());
			return true;
		}
		return false;
	}

	private boolean isSkipForStmt(Node iNode) {
		if (iNode.getEntity() == null) {
			return false;
		}
		TreeNode parent = iNode.getParent();
		if (iNode.getEntity().getType() == JavaEntityType.FOR_INIT || //
				iNode.getEntity().getType() == JavaEntityType.FOR_INCR || //
				(parent != null && ((Node) parent).getEntity().getType() == JavaEntityType.FOR_INIT) || //
				(parent != null && ((Node) parent).getEntity().getType() == JavaEntityType.FOR_INCR)) {
			return true;
		}
		return false;
	}

	private void setBegin(Node aNode) {
		if (aNode.getHtmlStyle() != null) {
			mLstSrcHtml.add(aNode.getHtmlStyle());
		}
	}

	private void setEnd(Node aNode) {
		if (aNode.getHtmlStyle() != null) {
			mLstSrcHtml.add(HTML_SPAN_END);
		}
	}

	private void setBackGround(Node aNode, Scope scope) {
		if (aNode.getHtmlStyle() == null) {
			return;
		} else if (scope == Scope.END && aNode.isQTree() && //
				(aNode.isInsert() || aNode.isDelete() || aNode.isAnomaly())) {
			mLstSrcHtml.add(HTML_SPAN_END);
		} else if (aNode.isInsertLikeQTree() && aNode.isQTree()) {
			mLstSrcHtml.add(mLayers.get(LayerType.INSBG).getStyle());
		} else if (aNode.isDeleteLikeQTree() && aNode.isQTree()) {
			mLstSrcHtml.add(mLayers.get(LayerType.DELBG).getStyle());
		} else if (aNode.isAnomaly() && aNode.isQTree()) {
			mLstSrcHtml.add(mLayers.get(LayerType.MISSBG).getStyle());
		}
		// mLstSrcHtml.add(HTML_BR);
	}

	private String getCodeValueForStmt(Node aNode, String aCodeValue) {
		final String LPAREN = "(";
		final String RPAREN = ")";
		final String SEMICON = ";";
		final String SPACE = " ";
		String result = aCodeValue;
		JavaEntityType entityType = (JavaEntityType) aNode.getEntity().getType();
		if (entityType == JavaEntityType.FOR_STATEMENT) {
			String forInitValue = getInitValueForStmt(aNode.getForStmt().forInit);
			String forIncrValue = getIncrValueForStmt(aNode.getForStmt().forIncr);
			if (!aCodeValue.isEmpty() && aCodeValue.charAt(0) == '(') {
				aCodeValue = aCodeValue.substring(1, aCodeValue.length() - 1) + SEMICON + SPACE;
			}
			result = LPAREN + forInitValue + aCodeValue + forIncrValue + RPAREN;
		}
		return result;
	}

	public String setSourceNodeParm(String value) {
		boolean flag = false;
		List<String> lstVal = new ArrayList<String>();
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (!flag && c == '$') {
				lstVal.add(mLayers.get(LayerType.PARM).getStyle());
				lstVal.add(String.valueOf(c));
				flag = true;
				continue;
			} else if (flag && (c == '&' || c == '.' || c == ']' || c == '[' || //
					c == '(' || c == ')' || c == '+' || c == '|' || c == '\\' || c == '^')) {
				lstVal.add(HTML_SPAN_END + c);
				flag = false;
				continue;
			} else {
				lstVal.add(String.valueOf(c));
			}
		}
		String strVal = "";
		for (int i = 0; i < lstVal.size(); i++) {
			strVal += lstVal.get(i);
		}
		return strVal;
	}

	private String setSourceNodeMethod(Node aNode) {
		String code;
		if (aNode.children() == Collections.emptyEnumeration()) {
			code = aNode.getValue();
		} else {
			code = getMethodSignature(aNode);
		}
		return code;
	}

	public void setSourceSecondNode(Node aNode, File aFile) {
		initiateBaseCodeNode(mBaseCodeNode);
		mlstDiff.clear();
		mSecondNode = aNode;
		mSecondNodeFile = aFile;
		mHelper.setAllDisableShow(mSecondNode);
		mChangeDistiller = new UTChangeDistiller();
		mChangeDistiller.diffBlock(aNode.copy(), mBaseCodeNode.copy());
		List<NodePair> lstMatches = mChangeDistiller.getMatches();
		for (NodePair iNodePair : lstMatches) {
			Node iBaseNode = iNodePair.getRight();
			Node iMatchedNode = iNodePair.getLeft();
			// System.out.println("[DBG0] " + iMatchedNode + " <=> " + iBaseNode + " (" + iBaseNode.getEntity().getStartPosition());
			for (Node iNode : mBaseCodeNodeList) {
				if (iNode.eq(iBaseNode)) {
					iNode.setMatchedNode(iMatchedNode);
					break;
				}
			}
		}
		if (aFile.getAbsolutePath().contains("Case9_New_Parameterize")) { // debug
			lstMatches = mChangeDistiller.getMatches();
			for (NodePair iNodePair : lstMatches) {
				Node iBaseNode = iNodePair.getRight();
				Node iMatchedNode = iNodePair.getLeft();
				System.out.println("[DBG0] " + iBaseNode + " <=> " + iMatchedNode);
			}
			System.out.println("------------------------------------------");
			for (int i = 0; i < mBaseCodeNodeList.size(); i++) {
				Node iNode = mBaseCodeNodeList.get(i);
				if (iNode.getMatchedNode() != null) {
					System.out.println("[DBG1] " + iNode + " <=> " + iNode.getMatchedNode());
				}
			}
			System.out.println("------------------------------------------");
		}

		/* overlayTwoListForMethodSignature(mSecondNode, mBaseCodeNodeList); */
		List<Node> deleteNodeList = getDeleteNodeList();
		mHelper.setAllDisableShow(deleteNodeList);
		overlayTwoListForDelete(mBaseCodeNodeList, deleteNodeList);
		List<Node> insertNodeList = getInsertNodeList();
		mHelper.setAllDisableShow(insertNodeList);
		overlayTwoListForInsert(mBaseCodeNodeList, insertNodeList);
		mBaseCodeNodeList.add(new Node(HTMLEntityType.HTML, HTML_SPAN_END));
		setSourceNode(mBaseCodeNodeList);
	}

	/**
	 * Overlay two list for delete.
	 * 
	 * @param aBaseList the a base list
	 * @param aList the a list
	 * @param index the index
	 */
	void overlayTwoListForDelete(List<Node> aBaseList, List<Node> aList) {
		int startIndex = 0;
		int curPos = -1;
		for (int i = 0; i < aBaseList.size(); i++) {
			Node iNode = aBaseList.get(i);
			Node iMatchedNode = iNode.getMatchedNode();
			if (iMatchedNode == null) {
				continue;
			}
			curPos = iMatchedNode.getEntity().getStartPosition();
			for (int j = startIndex; j < aList.size(); j++) {
				Node jNode = aList.get(j);
				int jPos = jNode.getEntity().getStartPosition();
				if (curPos > jPos && jNode.isSkip() == false) {
					jNode.setHtmlStyle(mLayers.get(LayerType.DEL).getStyle()); // HTML_BGN_SPAN_L2);
					aBaseList.add(i, jNode);
					startIndex++;
					break;
				}
			}
		}
		for (int i = startIndex; i < aList.size(); i++) {
			Node iNode = aList.get(i);
			int iPos = iNode.getEntity().getStartPosition();
			if (iPos > curPos) {
				iNode.setHtmlStyle(mLayers.get(LayerType.DEL).getStyle()); // HTML_BGN_SPAN_L2);
				aBaseList.add(iNode);
			}
		}
	}

	/**
	 * Overlay two list.
	 * 
	 * @param aBaseList the a base node list
	 * @param aList the a insert node list
	 */
	void overlayTwoListForInsert(List<Node> aBaseList, List<Node> aList) {
		int startIndex = 0;
		for (int i = 0; i < aBaseList.size(); i++) {
			Node iNode = aBaseList.get(i);
			for (int j = startIndex; j < aList.size(); j++) {
				Node jNode = aList.get(j);
				if (iNode.eq(jNode)) {
					iNode.setHtmlStyle(mLayers.get(LayerType.INS).getStyle()); // HTML_BGN_SPAN_L1);
					mHelper.setColorNode(iNode, aBaseList, mLayers.get(LayerType.INS).getStyle(), i);
					startIndex++;
					break;
				}
			}
		}
	}

	public CriticsOverlayBrowserHelper getHelper() {
		return mHelper;
	}

	// ///////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////

	/**
	 * Clean browser.
	 */
	public void cleanBrowser() {
		mBrowser.setText("<html><body>" //
				+ "<font color=\"blue\"> *** Checkbox Unchecked " //
				+ "<img src=\"file:///" + UTPlugin.getFilePath(UTPlugin.IMG_CHK) + "\">  *** </font>" //
				+ "</body></html>");
	}

	/**
	 * Draw browser.
	 * 
	 * @param mCBTreeHelper
	 */
	public void drawBrowser(List<Object> aArSelected) {
		List<Object> arSelected = aArSelected; // mCBTreeHelper.getCheckedElements();
		if (arSelected.isEmpty()) {
			cleanBrowser();
			return;
		}
		// setCheckedElements(arSelected);
		setCheckedElements(arSelected);
	}

	/**
	 * Sets the checked elements.
	 * 
	 * @param arSelected the new checked elements
	 */
	public void setCheckedElements(List<Object> arSelected) {
		if (arSelected == null || arSelected.isEmpty()) {
			return;
		}
		mlstDiff.clear();
		List<CriticsCBTreeNode> lstBlockNodes = new ArrayList<CriticsCBTreeNode>();
		for (int i = 0; i < arSelected.size(); i++) {
			CriticsCBTreeNode iCBTNode = (CriticsCBTreeNode) arSelected.get(i);
			if (iCBTNode.type() != CriticsCBTreeElemType.BLOCK)
				continue;
			lstBlockNodes.add(iCBTNode);
		}
		CriticsCBTreeNode baseCBTNode = lstBlockNodes.get(0);
		Node baseNode = baseCBTNode.getNode();
		baseNode.print();
		// System.out.println("==========================================");
		for (int i = 1; i < lstBlockNodes.size(); i++) {
			CriticsCBTreeNode iMatchedCBTNode = lstBlockNodes.get(i);
			iMatchedCBTNode.getNode().print();
			// System.out.println("------------------------------------------");
			mChangeDistiller = new UTChangeDistiller();
			mChangeDistiller.diffBlock(iMatchedCBTNode.getNode().copy(), baseNode.copy());
			Object[] item = { iMatchedCBTNode, baseCBTNode, mChangeDistiller };
			mlstDiff.add(item);
		}
		mHelper.setAllDisableShow(baseNode);
		// //////////////////////////////////////////////////////
		List<Node> lstBaseCode = new ArrayList<Node>();
		lstBaseCode.add(new Node(HTMLEntityType.HTML, HTML_BGN_SPAN_LBASE));
		Enumeration<?> e = baseNode.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			iNode.setHtmlStyle(HTML_BGN_SPAN_LBASE);
			lstBaseCode.add(iNode);
		}
		// //////////////////////////////////////////////////////
		if (mlstDiff.isEmpty()) {
			setSourceNode(lstBaseCode);
		} else {
			inspectComparison(lstBaseCode);
			lstBaseCode.add(new Node(HTMLEntityType.HTML, HTML_SPAN_END));
			setSourceNode(lstBaseCode);
		}
	}

	/**
	 * Inspect comparison.
	 */
	void inspectComparison(List<Node> aBaseCodeNodeList) {
		for (int i = 0; i < mlstDiff.size(); i++) {
			Object iMatch[] = mlstDiff.get(i);
			getBaseNodeList(iMatch, aBaseCodeNodeList);
			// ///////////////////////////////////////
			List<Node> deleteNodeList = getDeleteNodeList(iMatch);
			overlayTwoListForDelete(aBaseCodeNodeList, deleteNodeList);
			// ///////////////////////////////////////
			List<Node> insertNodeList = getInsertNodeList(iMatch);
			overlayTwoListForInsert(aBaseCodeNodeList, insertNodeList);
		}
	}

	/**
	 * Gets the insert node list.
	 * 
	 * @param aMatch the a match
	 * @return the insert node list
	 */
	List<Node> getInsertNodeList(Object[] aMatch) {
		CriticsCBTreeNode baseCBTNode = (CriticsCBTreeNode) aMatch[1];
		File fileBaseNode = baseCBTNode.getSubtreeInfo().getFullFilePath();
		String srcBaseNode = UTFile.getContents(fileBaseNode.getAbsolutePath());
		CompilationUnit unitBaseNode = new UTASTParser().parse(srcBaseNode);

		UTChangeDistiller changeDist = (UTChangeDistiller) aMatch[2];
		List<SourceCodeChange> lstInsert = changeDist.getInsertList();
		UTLog.println(__D, "INSERT ------------------------------------------");
		List<Node> lstInsertNode = UTChange.getNodeListMethodLevel(lstInsert, unitBaseNode, srcBaseNode, fileBaseNode);
		UTChange.printChange(lstInsert, false);
		return lstInsertNode;
	}

	/**
	 * Gets the delete node list.
	 * 
	 * @param aMatch the a match
	 * @return the delete node list
	 */
	List<Node> getDeleteNodeList(Object[] aMatch) {
		CriticsCBTreeNode matchedCBTNode = (CriticsCBTreeNode) aMatch[0];
		File fileMatchedNode = matchedCBTNode.getSubtreeInfo().getFullFilePath();
		String srcMatchedNode = UTFile.getContents(fileMatchedNode.getAbsolutePath());
		CompilationUnit unitMatchedNode = new UTASTParser().parse(srcMatchedNode);

		UTChangeDistiller changeDist = (UTChangeDistiller) aMatch[2];
		List<SourceCodeChange> lstDelete = changeDist.getDeleteList();
		UTLog.println(__D, "DELETE ------------------------------------------");
		List<Node> lstDeleteNode = UTChange.getNodeListMethodLevel(lstDelete, unitMatchedNode, srcMatchedNode, fileMatchedNode);
		UTChange.printChange(lstDelete, false);
		return lstDeleteNode;
	}

	/**
	 * Sample overlay.
	 */
	public void sampleOverlay() {
		List<String> lstHTML = new ArrayList<String>();
		lstHTML.add(HTML_BGN_HEAD);
		lstHTML.add(HTML_BGN_STYLE);
		lstHTML.add(CSS_DRAG);
		lstHTML.add(CSS_SPAN_LINSERT);
		lstHTML.add(CSS_SPAN_LDELETE);
		lstHTML.add(CSS_SPAN_LINSERTBG);
		lstHTML.add(CSS_SPAN_REGULAR);
		lstHTML.add(CSS_SPAN_COMMENT);
		lstHTML.add(HTML_END_STYLE);
		lstHTML.add(HTML_END_HEAD);
		// ////////////////////////////////////////////////////
		lstHTML.add(HTML_BGN_BODY);
		lstHTML.add(HTML.HEADER2("What's Code Overlay?"));

		lstHTML.add(HTML_BGN_SPAN_COMMENT);
		lstHTML.add(HTML.T("/* Regular, base code */", 1));
		lstHTML.add(HTML_SPAN_END);

		lstHTML.add(HTML_BGN_SPAN_REGULAR);
		lstHTML.add(HTML.T("if (flag == null)", 1));
		lstHTML.add(HTML.T("return;", 2));
		lstHTML.add(HTML.T("...", 1));
		lstHTML.add(HTML_SPAN_END);
		lstHTML.add(HTML_BR);

		lstHTML.add(HTML_BGN_SPAN_COMMENT);
		lstHTML.add(HTML.T("/* Differential, overlaid code */", 1));
		lstHTML.add(HTML_SPAN_END);

		lstHTML.add(HTML_BGN_SPAN_LINSERT);
		lstHTML.add(HTML_BGN_DRAG);
		lstHTML.add(HTML.T("for (int i = 0; i < list.size(); i++) { .. } ", 1));
		lstHTML.add(HTML_END_DRAG);
		lstHTML.add(HTML_SPAN_END);

		lstHTML.add(HTML_BGN_SPAN_LDELETE);
		lstHTML.add(HTML_BGN_DRAG);
		lstHTML.add(HTML.T("for (int i = 0; i < data.size(); i++) { ..... }", 1));
		lstHTML.add(HTML_END_DRAG);
		lstHTML.add(HTML_SPAN_END);

		lstHTML.add(HTML_BGN_SPAN_LINSERTBG);
		lstHTML.add(HTML_BGN_DRAG);
		lstHTML.add(HTML.T("for (int i = 0; i < users.size(); i++) { .......... }", 1));
		lstHTML.add(HTML_END_DRAG);
		lstHTML.add(HTML_SPAN_END);

		UTHtml.HTML_L_REGULAR(lstHTML, "...");
		lstHTML.add(HTML_BR);

		UTHtml.HTML_L_COMMENT(lstHTML, "/* Regular, base code */");
		lstHTML.add(HTML_BGN_SPAN_REGULAR);
		lstHTML.add(HTML.T("if (data.size() < 1024*2)", 1));
		lstHTML.add(HTML.T("data.append(buf);", 2));
		lstHTML.add(HTML.T("...", 1));
		lstHTML.add(HTML_SPAN_END);
		String textdrag_JS = UTCfg.getInst().readConfig().TEXTDRAG_JS;
		String fileContents = UTFile.readFileWithNewLine(textdrag_JS);
		lstHTML.add(fileContents);
		lstHTML.add(HTML_END_BODY);
		// ////////////////////////////////////////////////////
		String basicCodeSample = "";
		for (int i = 0; i < lstHTML.size(); i++) {
			String elem = lstHTML.get(i);
			System.out.println(elem);
			basicCodeSample += elem;
		}
		mBrowser.setText(basicCodeSample);
	}

	/**
	 * Overlay two list for method signature.
	 * 
	 * @param aSecondNode the a second node
	 * @param aBaseCodeNodeList the a base code node list
	 */
	void overlayTwoListForMethodSignature(Node aSecondNode, List<Node> aBaseCodeNodeList) {
		Node secNodeMethodSignature = null;
		int i = 0;
		if (mBaseCodeNode.getEntity().getType().isMethod() && //
				aSecondNode.getEntity().getType().isMethod()) {
			for (; i < aBaseCodeNodeList.size(); i++) {
				Node iNode = aBaseCodeNodeList.get(i);
				if (iNode.getLabel() != HTMLEntityType.HTML && iNode.getEntity().getType().isMethod()) {
					String value = getMethodSignature(aSecondNode);
					secNodeMethodSignature = new Node(JavaEntityType.METHOD, value);
					secNodeMethodSignature.setEntity(aSecondNode.getEntity());
					secNodeMethodSignature.setHtmlStyle(mLayers.get(LayerType.DEL).getStyle());
					break;
				}
			}
		}
		if (secNodeMethodSignature != null) {
			mBaseCodeNodeList.add(i + 1, secNodeMethodSignature);
		}
	}
	// /**
	// * Gets the child node.
	// *
	// * @param aChange the a change
	// * @param aMethodNode the a method node
	// * @return the child node
	// */
	// Node getChildNode(SourceCodeChange aChange, Node aMethodNode) {
	// String label = aChange.getChangedEntity().getLabel();
	// int startPosition = aChange.getChangedEntity().getStartPosition();
	// Enumeration<?> e = aMethodNode.preorderEnumeration();
	// while (e.hasMoreElements()) {
	// Node iNode = (Node) e.nextElement();
	// String iLabel = iNode.getEntity().getLabel();
	// int iBgnOffset = iNode.getEntity().getStartPosition();
	// if (iBgnOffset >= startPosition && label.equals(iLabel))
	// return iNode;
	// }
	// return null;
	// }
	//
	// /**
	// * Gets the method node.
	// *
	// * @param change the change
	// * @param aUnit the a unit
	// * @param aSource the a source
	// * @return the method node
	// */
	// Node getMethodNode(SourceCodeChange change, CompilationUnit aUnit, String aSource) {
	// String message = "[WRN] null pointing";
	// int startPosition = change.getChangedEntity().getStartPosition();
	// int defaultLength = 1;
	// UTASTNodeFinder finder = new UTASTNodeFinder();
	// UTASTNodeConverter converter = new UTASTNodeConverter();
	// MethodDeclaration methodDecl = finder.findCoveringMethodDeclaration(aUnit, new Point(startPosition, defaultLength));
	// if (methodDecl == null)
	// throw new RuntimeException(message);
	// Node resultNodeConverted = converter.convertMethod(methodDecl, aSource);
	// if (resultNodeConverted == null)
	// throw new RuntimeException(message);
	// return resultNodeConverted;
	// }
}
