/*
 * @(#) UTHtml.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.util;

import java.util.List;

import edu.utexas.seal.plugins.overlay.ICriticsHTMLKeyword;

/**
 * @author Myoungkyu Song
 * @date Feb 7, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTHtml implements ICriticsHTMLKeyword {

	/**
	 * HTM l_ l1.
	 * 
	 * @param aLst the a lst
	 * @param aStr the a str
	 */
	public static void HTML_L1(List<String> aLst, String aStr) {
		aLst.add(HTML_BGN_SPAN_LINSERT);
		aLst.add(HTML_T1(aStr));
		aLst.add(HTML_SPAN_END);
	}

	/**
	 * HTM l_ l2.
	 * 
	 * @param aLst the a lst
	 * @param aStr the a str
	 */
	public static void HTML_L2(List<String> aLst, String aStr) {
		aLst.add(HTML_BGN_SPAN_LDELETE);
		aLst.add(HTML_T1(aStr));
		aLst.add(HTML_SPAN_END);
	}

	/**
	 * Html l regular.
	 * 
	 * @param aLst the a lst
	 * @param aStr the a str
	 */
	public static void HTML_L_REGULAR(List<String> aLst, String aStr) {
		aLst.add(HTML_BGN_SPAN_REGULAR);
		aLst.add(HTML_T1(aStr));
		aLst.add(HTML_SPAN_END);
	}

	/**
	 * Html l comment.
	 * 
	 * @param aLst the a lst
	 * @param aStr the a str
	 */
	public static void HTML_L_COMMENT(List<String> aLst, String aStr) {
		aLst.add(HTML_BGN_SPAN_COMMENT);
		aLst.add(HTML_T1(aStr));
		aLst.add(HTML_SPAN_END);
	}

	/**
	 * HTM l_ heade r2.
	 * 
	 * @param aStr the a str
	 * @return the string
	 */
	public static String HTML_HEADER2(String aStr) {
		return String.format(HTML_HEADER2, aStr);
	}

	/**
	 * HTM l_ t1.
	 * 
	 * @param aStr the a str
	 * @return the string
	 */
	public static String HTML_T1(String aStr) {
		return String.format(HTML_T1, aStr);
	}

	/**
	 * HTM l_ t2.
	 * 
	 * @param aStr the a str
	 * @return the string
	 */
	public static String HTML_T2(String aStr) {
		return String.format(HTML_T2, aStr);
	}

	/**
	 * HTM l_ t3.
	 * 
	 * @param aStr the a str
	 * @return the string
	 */
	public static String HTML_T3(String aStr) {
		return String.format(HTML_T3, aStr);
	}

	/**
	 * HTM l_ t4.
	 * 
	 * @param aStr the a str
	 * @return the string
	 */
	public static String HTML_T4(String aStr) {
		return String.format(HTML_T4, aStr);
	}

	/**
	 * HTM l_ t5.
	 * 
	 * @param aStr the a str
	 * @return the string
	 */
	public static String HTML_T5(String aStr) {
		return String.format(HTML_T5, aStr);
	}

}
