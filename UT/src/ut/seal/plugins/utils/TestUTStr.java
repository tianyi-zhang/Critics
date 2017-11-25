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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Myoungkyu Song
 * @date Feb 1, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class TestUTStr extends UTStr {

	@Test
	public void test() {
		String text = "this.$f13[(this.$f48)])";
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			String str = String.valueOf(c);
			if (UTStr.isNumeric(str)) {
				System.out.println("[DBG] NUM YES: " + str);
			} else {
				System.out.println("[DBG] NUM NO: " + str);
			}
		}
	}

	// @Test
	public void testReplace() {
		String test = "0123456789";
		String rep = "XYZ";
		String result = UTStr.replace(test, rep, 3, rep.length() + 3);
		System.out.println("[DBG] " + result);
		assertEquals("test 1: ", "012XYZ6789", result);
	}

	// @Test
	public void testRemoveStringInQuot() {
		String test = "\"[123adf]\" <LIVE>-098!@#$%^&*()_+123 \"!@#$%^&*()_+\"";
		String expect = " <LIVE>-098!@#$%^&*()_+123 ";
		String result1 = removeStrInQuotMark(test);
		System.out.println("[" + expect + "]");
		System.out.println("[" + result1 + "]");
		assertEquals("test 1: ", expect, result1);

		test = " <LIVE>-098!@#$%^&*()_+123 ";
		expect = " <LIVE>-098!@#$%^&*()_+123 ";
		result1 = removeStrInQuotMark(test);
		System.out.println("[" + expect + "]");
		System.out.println("[" + result1 + "]");
		assertEquals("test 1: ", expect, result1);

		test = "\" <LIVE>-098!@#$%^&*()_+123 ";
		expect = "\" <LIVE>-098!@#$%^&*()_+123 ";
		result1 = removeStrInQuotMark(test);
		System.out.println("[" + expect + "]");
		System.out.println("[" + result1 + "]");
		assertEquals("test 1: ", expect, result1);

		test = " <<< ";
		expect = " <<< ";
		result1 = removeStrInQuotMark(test);
		System.out.println("[" + expect + "]");
		System.out.println("[" + result1 + "]");
		assertEquals("test 1: ", expect, result1);

		test = " <<< >>> ";
		expect = " <<< >>> ";
		result1 = removeStrInQuotMark(test);
		System.out.println("[" + expect + "]");
		System.out.println("[" + result1 + "]");
		assertEquals("test 1: ", expect, result1);

		test = " \"\"\" ";
		expect = " \"\"\" ";
		result1 = removeStrInQuotMark(test);
		System.out.println("[" + expect + "]");
		System.out.println("[" + result1 + "]");
		assertEquals("test 1: ", expect, result1);

		test = "  ";
		expect = "  ";
		result1 = removeStrInQuotMark(test);
		System.out.println("[" + expect + "]");
		System.out.println("[" + result1 + "]");
		assertEquals("test 1: ", expect, result1);

		test = "";
		expect = "";
		result1 = removeStrInQuotMark(test);
		System.out.println("[" + expect + "]");
		System.out.println("[" + result1 + "]");
		assertEquals("test 1: ", expect, result1);
	}

}
