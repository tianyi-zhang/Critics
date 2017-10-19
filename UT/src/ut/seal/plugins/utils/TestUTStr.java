/*
 * @(#) TestUTStr.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
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
