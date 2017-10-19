package ch.uzh.ifi.seal.changedistiller.treedifferencing;

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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import rted.util.DistanceByLevenshtein;
import ut.seal.plugins.utils.UTCfg;
import ut.seal.plugins.utils.UTStr;
import ut.seal.plugins.utils.ast.UTASTNodeExpressHandler;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.EntityType;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerForStatement;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerLocalVariable;
import edu.utexas.seal.plugins.ast.binding.UTASTBindingManagerParameter;
import edu.utexas.seal.plugins.util.UTCriticsDiffUtil;
import edu.utexas.seal.plugins.util.UTCriticsHTML.HTMLEntityType;
import edu.utexas.seal.plugins.util.visitor.UTGeneralVisitor;
import edu.utexas.seal.plugins.util.visitor.UTIGeneralVisitor;

/**
 * General tree node.
 * 
 * <p>
 * {@link TreeDifferencer} can only apply the matching and edit script generation if the two trees are made out of such nodes.
 * 
 * @author Beat Fluri
 * @see TreeDifferencer
 */
public class Node extends DefaultMutableTreeNode {
	private static final long			serialVersionUID		= 42L;
	private boolean						mAnomaly				= false;
	private List<Node>					mAssociatedNodes		= new ArrayList<Node>();
	private boolean						mBQueryTree				= false;
	private String						mClassName				= null;
	private CompilationUnit				mCunit					= null;
	private List<Node>					mDataDependentor		= new ArrayList<Node>();
	private SourceCodeEntity			mEntity					= null;
	private boolean						mExcludedByUser;
	private File						mFilePath				= null;
	private boolean						mHtmlShow				= true;
	private String						mHtmlStyle				= null;
	private int							mIndexOverlayColor		= -1;
	private boolean						mInsert, mDelete, mUpdate;
	private boolean						mInsertLikeQTree, mDeleteLikeQTree, mUpdateLikeQTree;
	private boolean						mIsLeft					= false;
	private EntityType					mLabel					= null;
	private Integer						mLevel					= null;
	private List<NodeValueArg>			mLstNodeValueArg		= new ArrayList<NodeValueArg>();
	private boolean						mMarkAlive				= false;
	private boolean						mMatched;
	private Node						mMatchedNode			= null;
	private MethodDeclaration			mMethodDecl				= null;
	private String						mNewValue				= null;
	private boolean						mOrdered;
	private Map<SimpleName, Boolean>	mParamMap				= new HashMap<SimpleName, Boolean>();
	private String						mPkgName				= null;
	private int							mPreorderId				= 0;
	private boolean						mSelectedByUser			= false;
	private int							mSimilarityThreshold	= -1;
	private boolean						mSummary				= false;
	private NodeType					mType					= NodeType.Unrelevant;
	private String						mValue					= null;
	private String						mValueReplica			= null;
	private String						mValueOrg				= null;
	private String						mValuePartialGen		= null;								/* for target node */
	private String						mValueParm				= null;								/* for query node */
	private NodeForStmt					mForStmt				= null;
	private final String				RESET_STR_KEY			= "-7+8*7#2!7";
	private final String				RESET_STR_VAL			= "_RESET_";
	private final String				PARM_VAR_TOKEN			= "\\^7\\@8\\#7\\$2\\&7";				// "\\+\\@";

	/**
	 * Instantiates a new node.
	 * 
	 * @param label the label
	 * @param value the value
	 */
	public Node(EntityType label, String value) {
		super();
		mLabel = label;
		mValue = value;
		mValueReplica = new String(value);
		mValueOrg = new String(value);
		mMatchedNode = null;
	}

	/**
	 * Disable matched.
	 */
	public void disableMatched() {
		mMatched = false;
	}

	/**
	 * Eq.
	 * 
	 * @param node the node
	 * @return true, if successful
	 */
	public boolean eq(Node node) {
		if (getLabel() != HTMLEntityType.HTML && //
				getMethodDeclaration() != null && node.getMethodDeclaration() != null && //
				node.getEntity().getType().isMethod() && getEntity().getType().isMethod()) {
			String myClassName = null;
			String yourClassName = null;
			Object o1 = getMethodDeclaration().getParent();
			Object o2 = node.getMethodDeclaration().getParent();
			while (!(o1 instanceof TypeDeclaration)) {
				o1 = ((ASTNode) o1).getParent();
			}
			while (!(o2 instanceof TypeDeclaration)) {
				o2 = ((ASTNode) o2).getParent();
			}
			if (o1 instanceof TypeDeclaration && o2 instanceof TypeDeclaration) {
				TypeDeclaration myClassDecl = (TypeDeclaration) o1;
				TypeDeclaration yourClassDecl = (TypeDeclaration) o2;
				myClassName = myClassDecl.getName().getFullyQualifiedName();
				yourClassName = yourClassDecl.getName().getFullyQualifiedName();
			}
			String myNode = getMethodDeclaration().getName().getFullyQualifiedName();
			String yourNode = node.getMethodDeclaration().getName().getFullyQualifiedName();
			int bgn17 = getMethodDeclaration().getStartPosition();
			int end17 = getMethodDeclaration().getLength();
			int bgn14 = node.getMethodDeclaration().getStartPosition();
			int end14 = node.getMethodDeclaration().getLength();
			if (bgn17 == bgn14 && end17 == end14 && myNode.equals(yourNode) && myClassName.equals(yourClassName)) {
				return true;
			} else {
				return false;
			}
		}
		if (this.getValue().equals(node.getValue()) && //
				this.getLabel().equals(node.getLabel()) && //
				this.getEntity().getSourceRange().equals(node.getEntity().getSourceRange())) {
			return true;
		}
		return false;
	}

	public boolean eqValueWoStr(Node aNode) {
		String qNodeValue = getValue();
		String tNodeValue = aNode.getValue();
		qNodeValue = UTStr.removeStrInQuotMark(qNodeValue);
		tNodeValue = UTStr.removeStrInQuotMark(tNodeValue);
		boolean flag = qNodeValue.equals(tNodeValue);
		return flag;
	}

	/**
	 * Eq name.
	 * 
	 * @param aNode the a node
	 * @return true, if successful
	 */
	public boolean eqName(Node aNode) {
		String label = aNode.getEntity().getLabel();
		String value = aNode.getValue();
		String pkgName = aNode.getPackageName();
		String className = aNode.getClassName();
		String selfLabel = getEntity().getLabel();
		String selfValue = getValue();
		String selfPkgName = getPackageName();
		String selfClassName = getClassName();
		if (selfLabel.equals(label) && selfValue.equals(value) && //
				selfPkgName.equals(pkgName) && selfClassName.equals(className)) {
			return true;
		}
		return false;
	}

	/**
	 * Enable matched.
	 */
	public void enableMatched() {
		mMatched = true;
	}

	/**
	 * Returns whether this node is matched with another node.
	 * 
	 * @return <code>true</code> if this node is match with another node, <code>false</code> otherwise
	 */
	public boolean isMatched() {
		return mMatched;
	}

	public void cleanEditOperationAll() {
		Enumeration<?> e = preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			iNode.cleanEditOperation();
		}
	}

	public void cleanEditOperation() {
		this.setInsert(false);
		this.setInsertLikeQTree(false);
		this.setDelete(false);
		this.setDeleteLikeQTree(false);
		this.setUpdate(false);
		this.setUpdateLikeQTree(false);
		this.setMatchedNode(null);
	}

	public boolean isInsert() {
		return mInsert;
	}

	public void setInsert(boolean bf) {
		mInsert = bf;
	}

	public boolean isDelete() {
		return mDelete;
	}

	public void setDelete(boolean bf) {
		mDelete = bf;
	}

	public boolean isUpdate() {
		return mUpdate;
	}

	public void setUpdate(boolean bf) {
		mUpdate = bf;
	}

	public boolean isInsertLikeQTree() {
		return mInsertLikeQTree;
	}

	public void setInsertLikeQTree(boolean mInsertLikeQTree) {
		this.mInsert = mInsertLikeQTree;
		this.mInsertLikeQTree = mInsertLikeQTree;
	}

	public boolean isDeleteLikeQTree() {
		return mDeleteLikeQTree;
	}

	public void setDeleteLikeQTree(boolean mDeleteLikeQTree) {
		this.mDeleteLikeQTree = mDeleteLikeQTree;
	}

	public boolean isUpdateLikeQTree() {
		return mUpdateLikeQTree;
	}

	public void setUpdateLikeQTree(boolean mUpdateLikeQTree) {
		this.mUpdateLikeQTree = mUpdateLikeQTree;
	}

	public boolean isExcludedByUser() {
		return mExcludedByUser;
	}

	public void setExcludedByUser(boolean bf) {
		mExcludedByUser = bf;
	}

	/**
	 * Enable out of order.
	 */
	public void enableOutOfOrder() {
		mOrdered = false;
	}

	/**
	 * Enable in order.
	 */
	public void enableInOrder() {
		mOrdered = true;
	}

	/**
	 * Checks for child.
	 * 
	 * @param child the child
	 * @return true, if successful
	 */
	public boolean hasChild(Node child) {
		Enumeration<?> e = preorderEnumeration();
		int cnNode = -1;
		while (e.hasMoreElements()) {
			cnNode++;
			Node n = (Node) e.nextElement();
			if (cnNode == 0) {
				continue;
			}
			if (n.eq(child))
				return true;
		}
		return false;
	}

	/**
	 * Returns whether this node is in order with its siblings.
	 * 
	 * @return <code>true</code> if this node is in order with its siblings, <code>false</code> otherwise
	 */
	public boolean isInOrder() {
		return mOrdered;
	}

	public List<Node> getChildren() {
		List<Node> list = new ArrayList<Node>();
		Enumeration<?> e = children();
		while (e.hasMoreElements()) {
			Object o = e.nextElement();
			if (o instanceof Node) {
				list.add((Node) o);
			}
		}
		return list;
	}

	public EntityType getLabel() {
		return mLabel;
	}

	public void setLabel(EntityType label) {
		mLabel = label;
	}

	public int sizeOfExcludedNode() {
		int cnt = 0;
		Enumeration<?> e = preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			if (iNode.getLabel() == JavaEntityType.EXCLUDED) {
				cnt++;
			}
		}
		return cnt;
	}

	public int getLevel() {
		if (mLevel == null) {
			return super.getLevel();
		}
		return mLevel;
	}

	public void setLevel(int aLevel) {
		mLevel = new Integer(aLevel);
	}

	public List<NodeValueArg> getValueArgList() {
		return mLstNodeValueArg;
	}

	public void setValueArgList() {
		UTCriticsDiffUtil diffUtil = new UTCriticsDiffUtil();
		String valueOrg = getValue();
		String valueAllGen = getValueNew();
		if (valueOrg == null || valueAllGen == null) {
			return;
		}
		List<NodeValueArg> lstNodeValueArg = diffUtil.getNodeValueArg(valueOrg, valueAllGen);
		setValueArgList(lstNodeValueArg);
	}

	public void setValueArgList(List<NodeValueArg> mLstNodeValueArg) {
		this.mLstNodeValueArg = mLstNodeValueArg;
	}

	public String getValue() {
		if (mValue.equals(RESET_STR_KEY))
			return RESET_STR_VAL;
		return mValue;
	}

	public void setValue(String value) {
		if (value.equals(RESET_STR_KEY)) {
			setValueReplica(mValue);
			mValue = RESET_STR_KEY;
			return;
		}
		mValue = value;
		setValueReplica(value);
	}

	public void setValueOrg(String valueOrg) {
		mValueOrg = valueOrg; // should be taken care of in using this API.
	}

	public String getValueOrg() {
		return mValueOrg; // should be taken care of in using this API.
	}

	public String getValueReplica() {
		return mValueReplica;
	}

	public String getValueReplacedParmWithToken() {
		String replaced = getValue().replaceAll(UTCfg.PARM_VAR_NAME, PARM_VAR_TOKEN);
		return replaced;
	}

	public String getValuePartialGen() {
		return mValuePartialGen;
	}

	public String convertValuePartialGen(Node aQNodePartialGen) {
		UTCriticsDiffUtil diffUtil = new UTCriticsDiffUtil();
		String valueOrgQNode = aQNodePartialGen.getValueReplacedParmWithToken();
		String valueParGenQNode = aQNodePartialGen.getValueOrg();
		List<NodeValueArg> lstDiffQNode = diffUtil.getNodeValueArg(valueParGenQNode, valueOrgQNode);
		List<NodeValueArg> valueArgListQNode = aQNodePartialGen.getValueArgList();
		boolean[] arIndex = new boolean[valueArgListQNode.size()];
		for (int i = 0; i < valueArgListQNode.size(); i++) {
			NodeValueArg iValArg = valueArgListQNode.get(i);
			for (int j = 0; j < lstDiffQNode.size(); j++) {
				NodeValueArg jValArg = lstDiffQNode.get(j);
				if (iValArg.getBegin() == jValArg.getBegin()) {
					arIndex[i] = true;
				}
			}
		}
		String valuePartialGeneralized = setValueArgParm(arIndex);
		mValuePartialGen = valuePartialGeneralized;
		String updatedValueQNode = aQNodePartialGen.setValueArgParm(arIndex);
		aQNodePartialGen.setValue(updatedValueQNode);
		return mValuePartialGen;
	}

	public void setValueReplica(String aValue) {
		if (!aValue.equals(RESET_STR_KEY))
			mValueReplica = new String(aValue);
	}

	public String setValueArgParm(boolean[] aArrIndex) {
		String valueOrg = getValueOrg();
		List<Character> lstValueOrg = UTStr.convertString(valueOrg);
		List<NodeValueArg> valueArgList = getValueArgList();
		List<String> lstValueUpdated = new ArrayList<String>();
		for (int i = 0, cnt = 0; i < lstValueOrg.size(); i++) {
			Character elem = lstValueOrg.get(i);
			boolean flag = false;
			for (int j = 0; j < valueArgList.size(); j++) {
				NodeValueArg iValueArg = valueArgList.get(j);
				int bgn = iValueArg.getBegin();
				if (bgn == i && aArrIndex[cnt++] == false) {
					break;
				} else if (bgn == i) {
					lstValueUpdated.add(iValueArg.getArgNew());
					i = iValueArg.getEnd() - 1;
					flag = true;
					break;
				}
			}
			if (!flag) {
				lstValueUpdated.add(String.valueOf(elem));
			}
		}
		String updatedStr = UTStr.convertStringArr(lstValueUpdated);
		return updatedStr;
	}

	public String setValueArgParm(int aIndex) {
		String valueOrg = getValueOrg();
		List<Character> lstValueOrg = UTStr.convertString(valueOrg);
		List<NodeValueArg> valueArgList = getValueArgList();
		List<String> lstValueUpdated = new ArrayList<String>();
		for (int i = 0; i < lstValueOrg.size(); i++) {
			Character elem = lstValueOrg.get(i);
			boolean flag = false;
			for (int j = 0; j < valueArgList.size(); j++) {
				NodeValueArg iValueArg = valueArgList.get(j);
				int bgn = iValueArg.getBegin();
				if (bgn == i && j == aIndex) {
					lstValueUpdated.add(iValueArg.getArgNew());
					i = iValueArg.getEnd();
					flag = true;
					break;
				}
			}
			if (!flag) {
				lstValueUpdated.add(String.valueOf(elem));
			}
		}
		String updatedStr = UTStr.convertStringArr(lstValueUpdated);
		return updatedStr;
	}

	public List<Node> getAssociatedNodes() {
		return mAssociatedNodes;
	}

	public List<Node> getAllNodes() {
		Enumeration<?> eQTreeOldRev = preorderEnumeration();
		List<Node> lstNodes = new ArrayList<Node>();
		while (eQTreeOldRev.hasMoreElements()) {
			lstNodes.add((Node) eQTreeOldRev.nextElement());
		}
		return lstNodes;
	}

	/**
	 * Adds the associated node.
	 * 
	 * @param node the node
	 */
	public void addAssociatedNode(Node node) {
		mAssociatedNodes.add(node);
		getEntity().addAssociatedEntity(node.getEntity());
	}

	/**
	 * Adds the data dependentor.
	 * 
	 * @param node the node
	 */
	public void addDataDependentor(Node node) {
		if (!mDataDependentor.contains(node)) {
			mDataDependentor.add(node);
		}
	}

	public List<Node> getDataDependentor() {
		return this.mDataDependentor;
	}

	/**
	 * Adds the param var.
	 * 
	 * @param name the name
	 */
	public void addParamVar(SimpleName name) {
		if (!mParamMap.containsKey(name)) {
			mParamMap.put(name, false);
		}
	}

	public Map<SimpleName, Boolean> getParamMap() {
		return mParamMap;
	}

	/**
	 * Parameterize.
	 * 
	 * @param name the name
	 */
	public void parameterize(SimpleName name) {
		if (mParamMap.containsKey(name)) {
			mParamMap.put(name, true);
		}
	}

	/**
	 * Disparameterize.
	 * 
	 * @param name the name
	 */
	public void disParameterize(SimpleName name) {
		if (mParamMap.containsKey(name)) {
			mParamMap.put(name, false);
		}
	}

	/**
	 * Checks if is any value-argument parameterized.
	 * 
	 * @return true, if is any value-argument parameterized
	 */
	public boolean isAnyValueArgParameterized() {
		if (getValue().equals(RESET_STR_KEY)) {
			return false;
		} else if (!getValue().equals(getValueOrg())) {
			return true;
		}
		return false;
	}

	public boolean isAnyValueArgParameterizedInQeury() {
		String v = getValue();
		if (v.contains("$var")) {
			String vorg = getValueOrg();
			if (vorg == null) {
				return false;
			}
			if (v.equals(RESET_STR_KEY)) {
				return false;
			} else if (!v.equals(vorg)) {
				return true;
			}
		} else {
			String vp = getValueParm();
			if (vp == null || vp.isEmpty()) {
				return false;
			}
			if (v.equals(RESET_STR_KEY)) {
				return false;
			} else if (!v.equals(vp)) {
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.DefaultMutableTreeNode#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(value: ");

		if (mValue == null || mValue.equals("")) {
			sb.append("none)");
		} else if (mValue.equals(RESET_STR_KEY)) {
			sb.append(RESET_STR_VAL).append(')');
		} else {
			sb.append(mValue).append(')');
		}

		sb.append("(label: ");

		if (mLabel == null) {
			sb.append("none)");
		} else {
			sb.append(mLabel.toString()).append(')');
		}
		return sb.toString();
	}

	public String toNewString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(value: ");

		if (mNewValue == null || mNewValue.equals("")) {
			sb.append("none)");
		} else {
			sb.append(mNewValue).append(')');
		}

		sb.append("(label: ");

		if (mLabel == null) {
			sb.append("none)");
		} else {
			sb.append(mLabel.toString()).append(')');
		}
		return sb.toString();
	}

	public String toStringPartialGen() {
		StringBuilder sb = new StringBuilder();
		sb.append("(value: ");

		if (mValue == null || mValue.equals("")) {
			sb.append("none)");
		} else if (mValuePartialGen != null) {
			sb.append(mValuePartialGen).append(')');
		} else {
			sb.append(mValue).append(')');
		}

		sb.append("(label: ");

		if (mLabel == null) {
			sb.append("none)");
		} else {
			sb.append(mLabel.toString()).append(')');
		}
		return sb.toString();
	}

	public SourceCodeEntity getEntity() {
		return mEntity;
	}

	public void setEntity(SourceCodeEntity entity) {
		mEntity = entity;
	}

	public int getSize() {
		Enumeration<?> e = preorderEnumeration();
		int szSubTree = 0;
		while (e.hasMoreElements()) {
			e.nextElement();
			szSubTree++;
		}
		return szSubTree;
	}

	public int getSimilarityThreshold() {
		if (mSimilarityThreshold == -1) {
			mSimilarityThreshold = UTCfg.getInst().readConfig().SIMILARITY_THRESHOLD;
		}
		return mSimilarityThreshold;
	}

	/**
	 * Prints the.
	 * 
	 * @param output the output
	 * @return the string builder
	 */
	@SuppressWarnings("unchecked")
	public StringBuilder print(StringBuilder output) {
		output.append(getValue());
		if (!isLeaf()) {
			output.append(" { ");
			for (Iterator<Node> it = children.iterator(); it.hasNext();) {
				Node child = it.next();
				child.print(output);
				if (it.hasNext()) {
					output.append(",");
				}
			}
			output.append(" }");
		}
		return output;
	}

	/**
	 * Checks if is leaf descendant.
	 * 
	 * @param candidate the candidate
	 * @return true, if is leaf descendant
	 */
	public boolean isLeafDescendant(Node candidate) {
		return candidate.isLeaf() && isNodeDescendant(candidate);
	}

	public boolean isMarkAliveNode() {
		return mMarkAlive;
	}

	/**
	 * Sets the mark alive node.
	 */
	public void setMarkAliveNode() {
		this.mMarkAlive = true;
	}

	public boolean isSelectedByUser() {
		return mSelectedByUser;
	}

	/**
	 * Checks if is similar value.
	 * 
	 * @param aNode the a node
	 * @return true, if is similar value
	 */
	public boolean isSimilarValue(Node aNode) {
		String urNewValue = aNode.getValueNew();
		List<NodeValueArg> urValueArgList = aNode.getValueArgList();
		String myNewValue = getValueNew();
		List<NodeValueArg> myValueArgList = getValueArgList();
		if (myNewValue == null || urNewValue == null || urValueArgList.size() != myValueArgList.size()) {
			return false;
		}
		int similarityThreshold = urValueArgList.size() * getSimilarityThreshold();
		String urNewValueWoNum = urNewValue.replaceAll("\\d", "");
		String myNewValueWoNum = myNewValue.replaceAll("\\d", "");
		urNewValueWoNum = UTStr.removeStrInQuotMark(urNewValueWoNum);
		myNewValueWoNum = UTStr.removeStrInQuotMark(myNewValueWoNum);
		if (urNewValueWoNum.equals(myNewValueWoNum)) {
			return true;
		}
		int distance = DistanceByLevenshtein.compute(urNewValueWoNum, myNewValueWoNum);
		return (similarityThreshold >= distance);
	}

	public boolean isSameSizeOfParameter(Node aNode) {
		String urNewValue = aNode.getValueNew();
		List<NodeValueArg> urValueArgList = aNode.getValueArgList();
		String myNewValue = getValueNew();
		List<NodeValueArg> myValueArgList = getValueArgList();
		if (myNewValue == null || urNewValue == null || urValueArgList.size() != myValueArgList.size()) {
			return false;
		}
		return true;
	}

	public void setSelectedByUser(boolean aSelectedByUser) {
		this.mSelectedByUser = aSelectedByUser;
	}

	public void setFilePath(File filePath) {
		this.mFilePath = filePath;
	}

	public File getFilePath() {
		return this.mFilePath;
	}

	public int getPreorderId() {
		return mPreorderId;
	}

	public void setPreorderId(int preorderId) {
		this.mPreorderId = preorderId;
	}

	public int getIndexOverlayColor() {
		return mIndexOverlayColor;
	}

	public void setIndexOverlayColor(int aIndexOverlayColor) {
		mIndexOverlayColor = aIndexOverlayColor;
	}

	public boolean isLeft() {
		return mIsLeft;
	}

	public void setIsLeft(boolean isLeft) {
		this.mIsLeft = isLeft;
	}

	public void setNodeType(NodeType type) {
		this.mType = type;
	}

	public NodeType getNodeType() {
		return this.mType;
	}

	public void setMethodDeclaration(MethodDeclaration aMethodDecl) {
		mMethodDecl = aMethodDecl;
	}

	/**
	 * Enable html.
	 * 
	 * @return true, if successful
	 */
	public boolean enableHTML() {
		return mHtmlShow;
	}

	public void setHTMLEnable(boolean aHtmlShow) {
		mHtmlShow = aHtmlShow;
	}

	public void setChildHTMLShow(boolean aHtmlShow) {
		List<Node> workList = new ArrayList<Node>();
		getNodeFromEnum(workList, this.children());
		while (!workList.isEmpty()) {
			Node iNode = workList.remove(0);
			iNode.setHTMLEnable(aHtmlShow);
			if (iNode.getChildCount() != 0) {
				getNodeFromEnum(workList, iNode.children());
			}
		}
	}

	public String getHtmlStyle() {
		return mHtmlStyle;
	}

	public void setHtmlStyle(String aHtmlStyle) {
		mHtmlStyle = aHtmlStyle;
	}

	/**
	 * Gets the node from enum.
	 * 
	 * @param aLst the a lst
	 * @param aEnum the a enum
	 * @return the node from enum
	 */
	void getNodeFromEnum(List<Node> aLst, Enumeration<?> aEnum) {
		while (aEnum.hasMoreElements()) {
			Object iObj = aEnum.nextElement();
			if (iObj instanceof Node) {
				aLst.add((Node) iObj);
			}
		}
	}

	public MethodDeclaration getMethodDeclaration() {
		if (this.getEntity().getType().isMethod())
			return mMethodDecl;
		else
			return null;
	}

	/**
	 * Gets the matched node.
	 * 
	 * @param aIndex the a index
	 * @return the matched node
	 */
	public Node getMatchedNode() {
		return mMatchedNode;
	}

	/**
	 * Adds the matched node.
	 * 
	 * @param mMatchedNode the m matched node
	 */
	public void setMatchedNode(Node mMatchedNode) {
		this.mMatchedNode = mMatchedNode;
	}

	/**
	 * Copy.
	 * 
	 * @return the node
	 */
	public Node copy() {
		Enumeration<?> e = this.preorderEnumeration();
		int id = 0;
		while (e.hasMoreElements()) {
			Object o = e.nextElement();
			Node n = (Node) o;
			n.setPreorderId(id++);
		}
		Node[] nodes = new Node[id];
		int[] parentIndex = new int[id];
		id = 0;
		e = this.preorderEnumeration();
		while (e.hasMoreElements()) {
			Object o = e.nextElement();
			Node n = (Node) o;
			Node p = (Node) n.getParent();
			if (p == null) {
				parentIndex[n.getPreorderId()] = 0;
			} else {
				parentIndex[n.getPreorderId()] = p.getPreorderId();
			}
			nodes[n.getPreorderId()] = (Node) n.clone();
		}
		for (int i = 0; i < parentIndex.length; i++) {
			int parentNodeIndex = parentIndex[i];
			Node parentNode = nodes[parentNodeIndex];
			for (int j = 1; j < nodes.length; j++) {
				int myParentNodeIndex = parentIndex[j];
				Node childNode = nodes[j];

				if (myParentNodeIndex == parentNodeIndex) {
					parentNode.add(childNode);
				}
			}
		}
		return nodes[0];
	}

	/**
	 * Prints the.
	 */
	public void print() {
		List<Object> list = new ArrayList<Object>();
		List<Object> subLblTreeNodeList = new ArrayList<Object>();
		Node root = this;
		UTASTNodeExpressHandler.preoderTraverse(root, list);
		UTASTNodeExpressHandler.postorderTraverse(root, list, null);
		UTASTNodeExpressHandler.representData(subLblTreeNodeList, list);
		for (int i = 0; i < subLblTreeNodeList.size(); i++) {
			Object elem = subLblTreeNodeList.get(i);
			System.out.print(elem);
		}
//		System.out.println("**************   " + this.getAllNodes().size() + "   **************");
	}

	public void printNewValue() {
		Enumeration<?> e = preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			System.out.println(UTStr.getIndent(iNode.getLevel()) + iNode.toNewString());
		}
	}

	public void printLabel() {
		Enumeration<?> e = preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			System.out.println(UTStr.getIndent(iNode.getLevel()) + iNode.getLabel().name());
		}
	}

	/**
	 * Prints the.
	 * 
	 * @param aIncludedByUser the a included by user
	 */
	public void print(boolean aIncludedByUser) {
		List<Object> list = new ArrayList<Object>();
		List<Object> subLblTreeNodeList = new ArrayList<Object>();
		Node root = this.copy();
		Enumeration<?> e = root.postorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			if (iNode.isExcludedByUser()) {
				iNode.removeFromParent();
			}
		}
		UTASTNodeExpressHandler.preoderTraverse(root, list);
		UTASTNodeExpressHandler.postorderTraverse(root, list, null);
		UTASTNodeExpressHandler.representData(subLblTreeNodeList, list);
		for (int i = 0; i < subLblTreeNodeList.size(); i++) {
			Object elem = subLblTreeNodeList.get(i);
			System.out.print(elem);
		}
	}

	@Deprecated
	public void setCompilationUnit(CompilationUnit cUnit) {
		this.mCunit = cUnit;
		String pkgName = cUnit.getPackage().getName().getFullyQualifiedName();
		setPackageName(pkgName);
		UTIGeneralVisitor<TypeDeclaration> mVisitor = new UTGeneralVisitor<TypeDeclaration>() {
			public boolean visit(TypeDeclaration node) {
				int bgnClass = node.getStartPosition();
				int endClass = bgnClass + node.getLength();
				int bgnNode = getEntity().getStartPosition();
				if (bgnClass < bgnNode && bgnNode < endClass)
					results.add(node);
				return true;
			}
		};
		cUnit.accept(mVisitor);
		List<TypeDeclaration> types = mVisitor.getResults();
		setClassName(types.get(0).getName().toString());
	}

	public void setClassName(String aClassName) {
		this.mClassName = aClassName;
	}

	public String getClassName() {
		return this.mClassName;
	}

	@Deprecated
	public CompilationUnit getCompilationUnit() {
		return this.mCunit;
	}

	public void setPackageName(String pkgName) {
		this.mPkgName = pkgName;
	}

	public String getPackageName() {
		return this.mPkgName;
	}

	public void setSummary(boolean aSummary) {
		mSummary = aSummary;
	}

	public boolean isSummary() {
		return mSummary;
	}

	public void setAnomaly(boolean aAnomaly) {
		mAnomaly = aAnomaly;
	}

	public boolean isAnomaly() {
		return mAnomaly;
	}

	public void resetValue() {
		Enumeration<?> e = preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			String org = iNode.getValue();
			iNode.setValue(RESET_STR_KEY);
			iNode.setValueReplica(org);
		}
	}

	public void restoreValue() {
		Enumeration<?> e = preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			iNode.setValue(iNode.getValueReplica());
		}
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////////////////////

	public boolean isVarDecl() {
		return this.getEntity().getType().equals(JavaEntityType.VARIABLE_DECLARATION_STATEMENT);
	}

	public boolean isIfStmt() {
		return this.getEntity().getType().equals(JavaEntityType.IF_STATEMENT);
	}

	public boolean isThenStmt() {
		return this.getEntity().getType().equals(JavaEntityType.THEN_STATEMENT);
	}

	public boolean isElseStmt() {
		return this.getEntity().getType().equals(JavaEntityType.ELSE_STATEMENT);
	}

	public boolean isForStmt() {
		return this.getEntity().getType().equals(JavaEntityType.FOR_STATEMENT);
	}

	public boolean isForEachStmt() {
		return this.getEntity().getType().equals(JavaEntityType.FOREACH_STATEMENT);
	}

	/**
	 * @return
	 */
	public boolean isBodyBlockStmt() {
		if (getEntity().getType().isMethod() || //
				getEntity().getType().equals(JavaEntityType.IF_STATEMENT) || //
				getEntity().getType().equals(JavaEntityType.ELSE_STATEMENT) || //
				getEntity().getType().equals(JavaEntityType.THEN_STATEMENT) || //
				getEntity().getType().equals(JavaEntityType.FOR_STATEMENT) || //
				getEntity().getType().equals(JavaEntityType.FOREACH_STATEMENT) || //
				getEntity().getType().equals(JavaEntityType.TRY_STATEMENT) || //
				getEntity().getType().equals(JavaEntityType.CATCH_CLAUSE) || //
				getEntity().getType().equals(JavaEntityType.DO_STATEMENT) || //
				getEntity().getType().equals(JavaEntityType.WHILE_STATEMENT) || //
				getEntity().getType().equals(JavaEntityType.SWITCH_STATEMENT) || //
				getEntity().getType().equals(JavaEntityType.SWITCH_CASE)) {
			return true;
		}
		return false;
	}

	/**
	 * @return
	 */
	public boolean isSkip() {
		if (getLevel() == 1) {
			if (((Node) getParent()) != null && ((Node) getParent()).getEntity().getType().isMethod()) {
				if (getEntity().getType().equals(JavaEntityType.MODIFIERS) || //
						getEntity().getType().equals(JavaEntityType.SINGLE_TYPE) || //
						getEntity().getType().equals(JavaEntityType.TYPE_PARAMETERS) || //
						getEntity().getType().equals(JavaEntityType.PARAMETERS) || //
						getEntity().getType().equals(JavaEntityType.THROW)) {
					return true;
				}
			}
		} else if (getLevel() == 2) {
			Object o1 = getParent();
			if (o1 == null)
				return false;
			Object o2 = getParent().getParent();
			if (o2 == null)
				return false;
			if (((Node) o2).getEntity().getType().isMethod() && //
					getEntity().getType().equals(JavaEntityType.PARAMETER)) {
				return true;
			}
		} else if (getLevel() == 3) {
			Object o1 = getParent();
			if (o1 == null)
				return false;
			Object o2 = getParent().getParent();
			if (o2 == null)
				return false;
			Object o3 = getParent().getParent().getParent();
			if (o3 == null)
				return false;
			if (((Node) o3).getEntity().getType().isMethod() && //
					getEntity().getType().equals(JavaEntityType.SINGLE_TYPE) || //
					getEntity().getType().equals(JavaEntityType.PARAMETERIZED_TYPE)) {
				return true;
			}
		}
		return false;
	}

	public boolean isSkipInMethodSignature() {
		Node iNode = this;
		if (iNode.getEntity().getType() == JavaEntityType.MODIFIERS || //
				iNode.getEntity().getType() == JavaEntityType.SINGLE_TYPE || //
				iNode.getEntity().getType() == JavaEntityType.TYPE_PARAMETERS || //
				iNode.getEntity().getType() == JavaEntityType.THROW) {
			return true;
		}
		return false;
	}

	/**
	 * @param aNewName
	 * @param aOldName
	 * @param aSrc
	 */
	public void setNewValue_localValDecl(SimpleName aNewName, SimpleName aOldName, String aSrc) {
		String oldName = aOldName.getFullyQualifiedName();
		String newName = aNewName.getFullyQualifiedName();
		int bgn14 = aOldName.getStartPosition();
		int end14 = bgn14 + aOldName.getLength();
		String value = getValue();
		int bgn17 = getEntity().getStartPosition();
		int end17 = getEntity().getEndPosition();

		if (bgn17 < bgn14 && end14 < end17 && value.contains(oldName)) {
			String newSrcFirstPart = aSrc.substring(bgn17, bgn14);
			String newSrcLastPart = aSrc.substring(end14, end17 + 1);
			String newValue = newSrcFirstPart + newName + newSrcLastPart;
			mNewValue = newValue;
		}
	}

	/**
	 * Sets the new value.
	 * 
	 * @param aMngr the new new value
	 */
	public void setNewValue(UTASTBindingManagerLocalVariable aMngr, String aSrc) {
		SimpleName newVarNode = aMngr.getNewAbstractNode();
		if (newVarNode == null) {
			return;
		}
		VariableDeclaration varDecl = aMngr.getVariableDeclarationFragment();
		String oldName = varDecl.getName().getFullyQualifiedName();
		String newName = newVarNode.getFullyQualifiedName();
		int bgn14 = varDecl.getName().getStartPosition();
		int end14 = bgn14 + varDecl.getName().getLength();

		String value = getValue();
		int bgn17 = getEntity().getStartPosition();
		int end17 = getEntity().getEndPosition();
		if (bgn17 < bgn14 && end14 < end17 && value.contains(oldName)) {
			String newSrcFirstPart = aSrc.substring(bgn17, bgn14);
			String newSrcLastPart = aSrc.substring(end14, end17 + 1);
			String newValue = newSrcFirstPart + newName + newSrcLastPart;
			mNewValue = newValue;
			// mNewValue = value.replace(oldVal, newVal);
		}
	}

	/**
	 * Sets the new value.
	 * 
	 * @param aMngr the a mngr
	 * @param aSrc the a src
	 */
	public void setNewValue(UTASTBindingManagerForStatement aMngr, String aSrc) {
		SimpleName newVarNode = aMngr.getNewAbstractNode();
		if (newVarNode == null) {
			return;
		}
		VariableDeclaration varDecl = aMngr.getVariableDeclarationFragment();
		// String oldName = varDecl.getName().getFullyQualifiedName();
		String newName = newVarNode.getFullyQualifiedName();
		int bgn14 = varDecl.getName().getStartPosition();
		int end14 = bgn14 + varDecl.getName().getLength();

		// String value = getValue();
		int bgn17 = getEntity().getStartPosition();
		int end17 = getEntity().getEndPosition();

		if (bgn14 > end17) {
			return;
		}
		String newSrcFirstPart = aSrc.substring(bgn17, bgn14);
		String newSrcLastPart = aSrc.substring(end14, end17 + 1);
		String newForInit = newSrcFirstPart + newName + newSrcLastPart;
		mNewValue = newForInit;
	}

	/**
	 * Sets the new value.
	 * 
	 * @param aMngr the a mngr
	 * @param aSrc the a src
	 */
	public void setNewValue(UTASTBindingManagerParameter aMngr, String aSrc) {
		SimpleName newVarNode = aMngr.getNewAbstractNode();
		if (newVarNode == null) {
			return;
		}
		VariableDeclaration varDecl = aMngr.getParameterDeclaration();
		String oldName = varDecl.getName().getFullyQualifiedName();
		String newName = newVarNode.getFullyQualifiedName();
		int bgn14 = varDecl.getName().getStartPosition();
		int end14 = bgn14 + varDecl.getName().getLength();

		String value = getValue();
		int bgn17 = getEntity().getStartPosition();
		int end17 = getEntity().getEndPosition();

		if (bgn17 == bgn14 && end14 == end17 + 1 && value.equals(oldName)) {
			mNewValue = newName;
		}
	}

	/**
	 * Sets the new value.
	 * 
	 * @param aNewName the a new name
	 * @param aOldName the a old name
	 * @param aSrc the a src
	 */
	@SuppressWarnings("unused")
	public void setNewValue(SimpleName aNewName, SimpleName aOldName, String aSrc) {
		String oldName = aOldName.getFullyQualifiedName();
		String newName = aNewName.getFullyQualifiedName();
		int bgn14 = aOldName.getStartPosition();
		int end14 = bgn14 + aOldName.getLength();
		String value = getValue();
		int bgn17 = getEntity().getStartPosition();
		int end17 = getEntity().getEndPosition();
		// if (bgn17 <= bgn14 && end14 <= end17 && oldName.equals(value)) {
		// System.out.print("");
		// }
		if (bgn17 <= bgn14 && end14 <= end17) {
			String newSrcFirstPart = aSrc.substring(bgn17, bgn14);
			String newSrcLastPart = aSrc.substring(end14, end17 + 1);
			String newValue = newSrcFirstPart + newName + newSrcLastPart;
			mNewValue = newValue;
		}
	}

	/**
	 * @param aNewName
	 * @param aOldName
	 * @param aSrc
	 */
	@SuppressWarnings("unused")
	public void setNewValueForEachStmt(SimpleName aNewName, SimpleName aOldName, String aSrc) {
		String oldName = aOldName.getFullyQualifiedName();
		String newName = aNewName.getFullyQualifiedName();
		int bgn14 = aOldName.getStartPosition();
		int end14 = bgn14 + aOldName.getLength();

		String value = getValue();
		int bgn17 = getEntity().getStartPosition();
		int end17 = getEntity().getEndPosition();

		int bgnPar17 = aSrc.indexOf("(", bgn17) + 1;
		int bgnBlock17 = aSrc.indexOf("{", bgn17);
		int endPar17 = aSrc.lastIndexOf(")", bgnBlock17);

		if (bgnPar17 <= bgn14 && end14 <= endPar17) {
			String forEachValue = aSrc.substring(bgnPar17, endPar17);
			String firstPart = aSrc.substring(bgnPar17, bgn14);
			String lastPart = aSrc.substring(end14, endPar17);
			String newValue = firstPart + newName + lastPart;
			mNewValue = newValue;
		}
	}

	/**
	 * @param aNewName
	 * @param aOldName
	 * @param aSrc
	 */
	@SuppressWarnings("unused")
	public void setNewValueForStmt(SimpleName aNewName, SimpleName aOldName, String aSrc) {
		String oldName = aOldName.getFullyQualifiedName();
		String newName = aNewName.getFullyQualifiedName();
		int bgn14 = aOldName.getStartPosition();
		int end14 = bgn14 + aOldName.getLength();

		String value = getValue();
		int bgn17 = getEntity().getStartPosition();
		int end17 = getEntity().getEndPosition();

		int bgnPar17 = aSrc.indexOf("(", bgn17) + 1;
		int bgnBlock17 = aSrc.indexOf("{", bgn17);
		int endPar17 = aSrc.lastIndexOf(")", bgnBlock17);

		int bgnSemicolon17 = aSrc.indexOf(";", bgnPar17) + 1;
		int endSemicolon17 = aSrc.lastIndexOf(";", endPar17);

		if (bgnSemicolon17 <= bgn14 && end14 <= endSemicolon17) {
			String forConditon = aSrc.substring(bgnSemicolon17, endSemicolon17);
			String firstPart = aSrc.substring(bgnSemicolon17, bgn14);
			String lastPart = aSrc.substring(end14, endSemicolon17);
			String newValue = "(" + firstPart + newName + lastPart + ")";
			String newValueNoSpace = newValue.replaceAll("\\s", "");
			mNewValue = newValueNoSpace;
		}
	}

	/**
	 * @param aNewName
	 * @param aOldName
	 * @param aSrc
	 */
	@SuppressWarnings("unused")
	public void setNewValueIfStmt(SimpleName aNewName, SimpleName aOldName, String aSrc) {
		String oldName = aOldName.getFullyQualifiedName();
		String newName = aNewName.getFullyQualifiedName();
		int bgn14 = aOldName.getStartPosition();
		int end14 = bgn14 + aOldName.getLength();

		String value = getValue();
		int bgn17 = getEntity().getStartPosition();
		int end17 = getEntity().getEndPosition();

		int bgnPar17 = aSrc.indexOf("(", bgn17) + 1;
		int bgnBlock17 = aSrc.indexOf("{", bgn17);
		int endPar17 = aSrc.lastIndexOf(")", bgnBlock17);

		if (bgnPar17 <= bgn14 && end14 <= endPar17) {
			String ifValue = aSrc.substring(bgnPar17, endPar17);
			String firstPart = aSrc.substring(bgnPar17, bgn14);
			String lastPart = aSrc.substring(end14, endPar17);
			String newValue = firstPart + newName + lastPart;
			mNewValue = newValue;
			// /////////////////////////////////////////////////
			Enumeration<?> e = preorderEnumeration();
			while (e.hasMoreElements()) {
				Node iNode = (Node) e.nextElement();
				if (iNode.isThenStmt() || iNode.isElseStmt()) {
					if (iNode.getValue().equals(this.mValue)) {
						iNode.setNewValue(this.mNewValue);
					}
				}
			}
		}
	}

	/**
	 * Sets the new value2.
	 * 
	 * @param aMngr the a mngr
	 * @param aSrc the a src
	 */
	public void setNewValue2(UTASTBindingManagerForStatement aMngr, String aSrc) {
		SimpleName newVarNode = aMngr.getNewAbstractNode();
		if (newVarNode == null) {
			return;
		}
		VariableDeclaration varDecl = aMngr.getVariableDeclarationFragment();
		// String oldName = varDecl.getName().getFullyQualifiedName();
		String newName = newVarNode.getFullyQualifiedName();
		int bgn14 = varDecl.getName().getStartPosition();
		int end14 = bgn14 + varDecl.getName().getLength();

		// String value = getValue();
		int bgn17 = getEntity().getStartPosition();
		int end17 = getEntity().getEndPosition();

		List<Node> children = getChildren();
		for (int i = 0; i < children.size(); i++) {
			Node elem = children.get(i);
			if (elem.getEntity().getType().equals(JavaEntityType.FOR_INIT)) {
				String newSrcFirstPart = aSrc.substring(bgn17, bgn14);
				String newSrcLastPart = aSrc.substring(end14, end17);
				// String newSrc = newSrcFirstPart + newName + newSrcLastPart;
				// String forBlock = aSrc.substring(bgn17, end17);
				int bgn_init = newSrcFirstPart.indexOf("(");
				int end_init = newSrcLastPart.indexOf(";", bgn_init);

				String newForInitFirstPart = newSrcFirstPart.substring(bgn_init + 1);
				String newForInitLastPart = newSrcLastPart.substring(0, end_init);
				String newForInit = newForInitFirstPart + newName + newForInitLastPart;
				mNewValue = newForInit;
			}
		}
	}

	public String getValueNew() {
		return mNewValue;
	}

	public void setNewValue(String aNewValue) {
		mNewValue = aNewValue;
	}

	public NodeForStmt getForStmt() {
		if (mForStmt == null)
			this.mForStmt = new NodeForStmt();
		return mForStmt;
	}

	public void setForStmt(NodeForStmt mForStmt) {
		this.mForStmt = mForStmt;
	}

	public Node getChild(JavaEntityType anEntity) {
		Enumeration<?> e = children();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			EntityType entityType = iNode.getEntity().getType();
			if (entityType == null) {
				continue;
			}
			if (entityType == anEntity)
				return iNode;
		}
		return null;
	}

	public String getValueParm() {
		return mValueParm;
	}

	public void setValueParm(String aValueParm) {
		this.mValueParm = aValueParm;
	}

	public void setQTree(boolean aBQueryTree) {
		mBQueryTree = aBQueryTree;
	}

	public boolean isQTree() {
		return mBQueryTree;
	}

	// Removing hashCode and equals fixes issues #1, #8, #9. See https://bitbucket.org/sealuzh/tools-changedistiller/issue/1.

	// @Override
	// public int hashCode() {
	// final int prime = 31;
	// int result = 1;
	// result = prime * result + ((fLabel == null) ? 0 : fLabel.hashCode());
	// result = prime * result + ((fValue == null) ? 0 : fValue.hashCode());
	// return result;
	// }
	//
	// @Override
	// public boolean equals(Object obj) {
	// if (this == obj) {
	// return true;
	// }
	// if (obj == null) {
	// return false;
	// }
	// if (getClass() != obj.getClass()) {
	// return false;
	// }
	// Node other = (Node) obj;
	// if (fLabel == null) {
	// if (other.fLabel != null) {
	// return false;
	// }
	// } else if (!fLabel.equals(other.fLabel)) {
	// return false;
	// }
	// if (fValue == null) {
	// if (other.fValue != null) {
	// return false;
	// }
	// } else if (!fValue.equals(other.fValue)) {
	// return false;
	// }
	// return true;
	// }
}
