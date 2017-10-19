/*
 * @(#) UTStr.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package ut.seal.plugins.utils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.swt.graphics.Point;

import ut.seal.plugins.utils.ast.UTASTNodeConverter;
import ut.seal.plugins.utils.ast.UTASTNodeFinder;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.NodeValueArg;
import edu.utexas.seal.plugins.util.UTCriticsDiffUtil;
import static org.junit.Assert.assertEquals;

/**
 * The Class UTStr.
 * 
 * @author Myoungkyu Song
 * @date Nov 29, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTStr {

	/**
	 * Gets the indent f.
	 * 
	 * @param str the str
	 * @param indent the indent
	 * @return the indent f
	 */
	public static String getIndentF(String str, int indent) {
		int len = str.length();
		indent -= len;
		String indentStr = "";
		for (int i = 0; i < indent; i++) {
			indentStr += " ";
		}
		if (indent <= 0)
			return "   " + str;
		return indentStr + str;
	}

	/**
	 * Gets the indent r.
	 * 
	 * @param o the o
	 * @param indent the indent
	 * @return the indent r
	 */
	public static String getIndentR(Object o, int indent) {
		String str = String.valueOf(o);
		int len = str.length();
		indent -= len;
		String indentStr = "";
		for (int i = 0; i < indent; i++) {
			indentStr += " ";
		}
		if (indent <= 0)
			return str + "   ";
		return str + indentStr;
	}

	/**
	 * Gets the indent.
	 * 
	 * @param size the size
	 * @return the indent
	 */
	public static String getIndent(int size) {
		String indent = "  ";
		if (size == 0)
			return "";
		for (int i = 0; i < size; i++)
			indent += "  ";
		return indent;
	}

	/**
	 * Gets the indent.
	 * 
	 * @param aMethodNode the a method node
	 * @param aStartPosition the a start position
	 * @return the indent
	 */
	public static String getIndent(Node aMethodNode, int aStartPosition) {
		Enumeration<?> e = aMethodNode.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			if (iNode.getEntity().getStartPosition() >= aStartPosition) {
				return UTStr.getIndent(iNode.getLevel());
			}
		}
		return null;
	}

	/**
	 * Gets the indent.
	 * 
	 * @param aUnit the a unit
	 * @param aStartPosition the a start position
	 * @return the indent
	 */
	public static String getIndent(CompilationUnit aUnit, int aStartPosition) {
		int defaultLength = 1;
		UTASTNodeFinder finder = new UTASTNodeFinder();
		UTASTNodeConverter converter = new UTASTNodeConverter();
		MethodDeclaration methodDecl = finder.findCoveringMethodDeclaration(aUnit, new Point(aStartPosition, defaultLength));
		String message = "[WRN] UTStr.getIndent - null point";
		if (methodDecl == null)
			throw new RuntimeException(message);
		Node node = converter.convertMethod(methodDecl, aUnit);
		if (node == null)
			throw new RuntimeException(message);
		String indent = getIndent(node, aStartPosition);
		return indent;
	}

	// @Test
	public void testGetLongestSubstr() {
		String prjLeft = "Case3_New_Insert.Remove";
		String prjRight = "Case3_Old_Insert.Remove";

		String fLeft = "/Users/mksong/runtime-Critics/Case3_New_Insert.Remove/src/pkg3/ClassC.java";
		String fRight = "/Users/mksong/runtime-Critics/Case3_Old_Insert.Remove/src/pkg1/ClassA.java";

		String fRightTemp = fRight.replace(prjRight, prjLeft);
		String fCommon = getLongestSubstr(fLeft, fRightTemp);
		String fRightSuffix = fRightTemp.replace(fCommon, "");
		fLeft = fCommon + fRightSuffix;

		// String fRightName = getShortFileName(fRight);
		// String fLeftName = getShortFileName(fLeft);
		// String FS = System.getProperty("file.separator");
		// fLeft = fCommon + fLeftSuffix.replace(FS + fLeftName, FS + fRightName);

		System.out.println("[DBG] fResult:\t" + fCommon);
		System.out.println("[DBG] fRight:\t" + fRight);
		System.out.println("[DBG] fLeft:\t" + fLeft);

		assertEquals("test 1: ", fCommon, "/Users/mksong/runtime-Critics/Case3_New_Insert.Remove/src/pkg");
		assertEquals("test 2: ", fLeft.trim(), //
				"/Users/mksong/runtime-Critics/Case3_New_Insert.Remove/src/pkg1/ClassA.java");
	}

	public static String getLongestSubstr(String s, String t) {
		if (s.isEmpty() || t.isEmpty()) {
			return "";
		}
		int m = s.length();
		int n = t.length();
		int cost = 0;
		int maxLen = 0;
		int[] p = new int[n];
		int[] d = new int[n];
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				if (s.charAt(i) != t.charAt(j)) {
					cost = 0;
				} else {
					if ((i == 0) || (j == 0)) {
						cost = 1;
					} else {
						cost = p[j - 1] + 1;
					}
				}
				d[j] = cost;

				if (cost > maxLen) {
					maxLen = cost;
				}
			}
			int[] swap = p;
			p = d;
			d = swap;
		}
		return s.substring(0, maxLen);
	}

	public static String getShortFileName(String aFullPath) {
		String FS = System.getProperty("file.separator");
		int index = aFullPath.lastIndexOf(FS);
		if (index != -1) {
			return aFullPath.substring(index + 1, aFullPath.length());
		}
		return aFullPath;
	}

	public static boolean isNumeric(String str) {
		return str.matches("-?\\d+(\\.\\d+)?"); // match a number with optional '-' and decimal.
	}

	public static String removeStrInQuotMark(String str) {
		String result1 = removeStrInQuotMarkSub(str, 'A');
		String result2 = removeStrInQuotMarkSub(str, 'B');
		if (result1 == null || result2 == null) {
			return str;
		}
		List<Character> lstStr = new ArrayList<Character>();
		for (int i = 0; i < result1.length(); i++) {
			lstStr.add(result1.charAt(i));
		}
		// System.out.println("[" + result1 + "]");
		// System.out.println("[" + result2 + "]");
		UTCriticsDiffUtil diffUtil = new UTCriticsDiffUtil();
		List<NodeValueArg> diffs = diffUtil.getListDiffBlock(result1, result2);
		for (int i = 0; i < diffs.size(); i++) {
			NodeValueArg nodeValueArg = diffs.get(i);
			int bgn = nodeValueArg.getBegin();
			if (i % 2 == 0)
				lstStr.set(bgn, '<');
			else
				lstStr.set(bgn, '>');
		}
		return convertString(lstStr);
	}

	public static String removeStrInQuotMarkSub(String str, Character sChar) {
		HashMap<Integer, Character> mapChar = new HashMap<Integer, Character>();
		List<Character> lstStr = new ArrayList<Character>();
		List<Integer> lstQuotMark = new ArrayList<Integer>();
		for (int i = 0; i < str.length(); i++) {
			Character c = str.charAt(i);
			if (c.equals('\"')) {
				lstQuotMark.add(i);
			} else if (c.equals('<') || c.equals('>')) {
				mapChar.put(i, c);
			}
			lstStr.add(c);
		}
		if (lstQuotMark.isEmpty() || lstQuotMark.size() % 2 != 0) {
			return null;
		}
		for (Map.Entry<Integer, Character> e : mapChar.entrySet()) {
			int index = e.getKey();
			lstStr.set(index, sChar);
		}
		for (int i = 0; i < lstQuotMark.size(); i++) {
			int index = lstQuotMark.get(i);
			if (i % 2 == 0) {
				lstStr.set(index, '<');
			} else {
				lstStr.set(index, '>');
			}
		}
		String updatedStr = convertString(lstStr);
		String newstr = updatedStr.replaceAll("<[^>]*>", "");
		if (newstr == null) {
			return null;
		}
		return newstr;
	}

	public static String replacement(int len) {
		String rep = "$";
		for (int i = 0; i < len; i++) {
			rep += "^";
		}
		return rep;
	}

	public static String replace(String org, String rep, int start, int end) {
		if (org.length() < org.length() || start > end)
			return null;
		if (start == end)
			return org;

		String first = org.substring(0, start);
		String last = org.substring(end);
		String result = first + rep + last;
		return result;
	}

	public static List<Character> convertString(String str) {
		List<Character> list = new ArrayList<Character>();
		for (int i = 0; i < str.length(); i++) {
			list.add(str.charAt(i));
		}
		return list;
	}

	public static String convertString(List<Character> lstStr) {
		final Character[] charactersArray = lstStr.toArray(new Character[lstStr.size()]);
		final char[] charsArray = ArrayUtils.toPrimitive(charactersArray);
		String updatedStr = String.valueOf(charsArray);
		return updatedStr;
	}

	public static String convertStringArr(List<String> lstStr) {
		String str = "";
		for (int i = 0; i < lstStr.size(); i++) {
			String elem = lstStr.get(i);
			str += elem;
		}
		return str;
	}
}
