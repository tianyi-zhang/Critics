/*
 * @(#) UTCriticsHTML.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.util;

import java.util.List;

import ch.uzh.ifi.seal.changedistiller.model.classifiers.EntityType;
import edu.utexas.seal.plugins.overlay.ICriticsHTMLKeyword;

/**
 * @author Myoungkyu Song
 * @date Jan 18, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTCriticsHTML implements ICriticsHTMLKeyword {

	public enum HTMLEntityType implements EntityType {
		HTML, CSS;

		@Override
		public boolean isUsableForChangeExtraction() {
			return false;
		}

		@Override
		public boolean isComment() {
			return false;
		}

		@Override
		public boolean isStructureEntityType() {
			return false;
		}

		@Override
		public boolean isType() {
			return false;
		}

		@Override
		public boolean isStatement() {
			return false;
		}

		@Override
		public boolean isField() {
			return false;
		}

		@Override
		public boolean isClass() {
			return false;
		}

		@Override
		public boolean isMethod() {
			return false;
		}

	}

	public static class HTML {
		public static void L1(List<String> aLst, String aStr) {
			aLst.add(HTML_BGN_SPAN_LINSERT);
			aLst.add(T1(aStr));
			aLst.add(HTML_SPAN_END);
		}

		public static void L2(List<String> aLst, String aStr) {
			aLst.add(HTML_BGN_SPAN_LDELETE);
			aLst.add(T1(aStr));
			aLst.add(HTML_SPAN_END);
		}

		public static void REGULAR(List<String> aLst, String aStr) {
			aLst.add(HTML_BGN_SPAN_REGULAR);
			aLst.add(T1(aStr));
			aLst.add(HTML_SPAN_END);
		}

		public static void COMMENT(List<String> aLst, String aStr) {
			aLst.add(HTML_BGN_SPAN_COMMENT);
			aLst.add(T1(aStr));
			aLst.add(HTML_SPAN_END);
		}

		public static String HEADER2(String aStr) {
			return String.format(HTML_HEADER2, aStr);
		}

		public static String T(String aStr, int index) {
			switch (index) {
			case 1:
				return String.format(HTML_T1, aStr);
			case 2:
				return String.format(HTML_T2, aStr);
			case 3:
				return String.format(HTML_T3, aStr);
			case 4:
				return String.format(HTML_T4, aStr);
			case 5:
				return String.format(HTML_T5, aStr);
			}
			return String.format(HTML_T1, aStr);
		}

		public static String T1(String aStr) {
			return String.format(HTML_T1, aStr);
		}

		public static String T2(String aStr) {
			return String.format(HTML_T2, aStr);
		}

		public static String T3(String aStr) {
			return String.format(HTML_T3, aStr);
		}

		public static String T4(String aStr) {
			return String.format(HTML_T4, aStr);
		}

		public static String T5(String aStr) {
			return String.format(HTML_T5, aStr);
		}
	}
}
