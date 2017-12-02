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

import java.util.List;

import ch.uzh.ifi.seal.changedistiller.model.classifiers.EntityType;

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
