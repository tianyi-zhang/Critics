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
package edu.utexas.seal.plugins.util.root;

import java.util.List;

import ut.seal.plugins.utils.ICriticsHTMLKeyword;

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
