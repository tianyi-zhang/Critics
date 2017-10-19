/*
 * @(#) MethodBodyConverter.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package rted.datastructure.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import rted.datastructure.LblValueNode;
import ut.seal.plugins.utils.UTLog;
import ut.seal.plugins.utils.ast.UTASTNodeConverter;
import ut.seal.plugins.utils.ast.UTASTNodeExpressHandler;

import com.google.inject.Guice;
import com.google.inject.Injector;

import ch.uzh.ifi.seal.changedistiller.JavaChangeDistillerModule;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaMethodBodyConverter;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ch.uzh.ifi.seal.changedistiller.util.CompilationUtils;

/**
 * @author Myoungkyu Song
 * @date Nov 10, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class TreeConvert {
	private boolean					$					= false;
	private final Injector			sInjector;
	private JavaMethodBodyConverter	sMethodBodyConverter;
	private Node					subTree;
	private String					subLblTree;
	private List<Object>			subLblTreeNodeList	= new ArrayList<Object>();
	private Map<Integer, Node>		postorderIDManager	= new TreeMap<Integer, Node>();

	/**
	 * Instantiates a new tree convert.
	 */
	public TreeConvert() {
		sInjector = Guice.createInjector(new JavaChangeDistillerModule());
		try {
			sMethodBodyConverter = sInjector.getInstance(JavaMethodBodyConverter.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Node getSubTree() {
		return subTree;
	}

	public void setSubTree(Node subTree) {
		this.subTree = subTree;
	}

	public String getSubLblTree() {
		return subLblTree;
	}

	public List<Object> getSubLblTreeNodeList() {
		return subLblTreeNodeList;
	}

	public Map<Integer, Node> getPostorderIDManager() {
		return postorderIDManager;
	}

	/**
	 * Converter.
	 * 
	 * @param fileContents the file contents
	 * @param node the node
	 */
	public void converter(String fileContents, MethodDeclaration node) {
		String methodName = node.getName().getFullyQualifiedName();
		subTree = UTASTNodeConverter.convertMethodBody(methodName, CompilationUtils. //
				compileSource(fileContents), sMethodBodyConverter); // //
	}

	/**
	 * Convert.
	 * 
	 * @param root the root
	 * @param iDMngr the i d mngr
	 * @param isPrintable the is printable
	 * @param kind the kind
	 */
	public void convert(Node root, Map<Integer, Node> iDMngr, boolean isPrintable, MatchType kind) {
		$ = isPrintable;
		List<Object> list = new ArrayList<Object>();

		setSubTree(root);
		UTASTNodeExpressHandler.preoderTraverse(root, list);
		UTASTNodeExpressHandler.postorderTraverse(root, list, iDMngr);
		subLblTreeNodeList.clear();
		UTASTNodeExpressHandler.representData(subLblTreeNodeList, list);

		LblValueNode tNode = new LblValueNode();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < subLblTreeNodeList.size(); i++) {
			Object elem = subLblTreeNodeList.get(i);
			if (elem instanceof Node) {
				Node n = (Node) elem;
				tNode.setNode(n);
				if ($)
					UTLog.print(false, tNode.toString()); // System.out.print(tNode.toString());
			} else {
				if ($)
					UTLog.print(false, elem.toString()); // System.out.print(elem);
			}
			tNode.setNode(elem);
			switch (kind) {
			case REGULAR:
				sb.append(tNode.toString().trim());
				break;
			case ALLGEN:
				String text = tNode.toStringAllGen().trim();
				String onlyAlph = text.replaceAll("\\d", "");
				sb.append(onlyAlph);
				break;
			case LABEL:
				String label = tNode.getLabel();
				sb.append(label);
				break;
			default:
				break;
			}
		}
		this.subLblTree = sb.toString();
	}

	// public void converter(String fileName, String methodName) {
	// this.subTree = UTASTNodeConverter.convertMethodBody(methodName, CompilationUtils. //
	// compileSource(UTFile.readFile(fileName)), sMethodBodyConverter);
	// convert(subTree, this.postorderIDManager, $);
	// }
	//
	// public void converter(String fileName, Node methodNode, boolean isPrintable) {
	// this.$ = isPrintable;
	// this.subTree = methodNode;
	// convert(methodNode, this.postorderIDManager, $);
	// }
	//
	// public static void main(String[] args) {
	// TreeConvert m = new TreeConvert();
	// m.converter("/Users/mksong/workspaceCritics/UT.RTED-standalone/input/mypkg01/A.java", "foo");
	// System.out.println(m.getSubLblTree());
	// }
}
