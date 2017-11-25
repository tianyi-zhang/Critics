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
package edu.utexas.seal.plugins.handler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import edu.utexas.seal.plugins.util.UTCriticsDiffUtil;
import edu.utexas.seal.plugins.util.UTCriticsDiffUtil.Diff;
import edu.utexas.seal.plugins.util.UTCriticsDiffUtil.Patch;

/**
 * @author Myoungkyu Song
 * @date Jan 31, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class TestCriticsFindContext {

	@Test
	public void testDetectAnomalies() {
		List<List<String>> grpMngr1 = new ArrayList<List<String>>();
		List<List<String>> grpMngr2 = new ArrayList<List<String>>();
		List<String> someGrp = new ArrayList<String>();
		List<String> otherGrp = new ArrayList<String>();
		someGrp.add("a");
		someGrp.add("a");
		otherGrp.add("a");
		// 1st group
		grpMngr1.add(someGrp);
		grpMngr2.add(someGrp);
		// 2nd group
		grpMngr1.add(otherGrp);
		grpMngr2.add(otherGrp);
		// 3rd group
		grpMngr1.add(someGrp);
		grpMngr2.add(otherGrp);

		List<String> grp1 = grpMngr1.get(0);
		List<String> grp2 = grpMngr2.get(0);

		if (grp1.size() == grp2.size()) {

		}
	}

	void getNext(List<List<String>> grpMngr1, List<List<String>> grpMngr2, //
			List<String> grp1, List<String> grp2, int index1, int index2) {
		for (int i = 0; i < grp1.size(); i++) {
			grp1 = grpMngr1.get(i);
			grp2 = grpMngr2.get(i);

		}
	}

	// @Test
	public void testGetLocationParameters1() {
		String str1 = "(value: String personName = customerName;)(label: VARIABLE_DECLARATION_STATEMENT)";
		String str2 = "(value: String $v2 = $p0;)(label: VARIABLE_DECLARATION_STATEMENT)";
		UTCriticsDiffUtil match = new UTCriticsDiffUtil();
		LinkedList<Patch> lstPatch = match.patch_make(str1, str2);
		for (int i = 0; i < lstPatch.size(); i++) {
			Patch elem = lstPatch.get(i);

			System.out.println("[DBG] " + elem);

		}
		System.out.println("==========================================");
	}

	// @Test
	public void testGetLocationParameters11() {
		String str1 = "(value: String personName = customerName;)(label: VARIABLE_DECLARATION_STATEMENT)";
		String str2 = "(value: String $v2 = $p0;)(label: VARIABLE_DECLARATION_STATEMENT)";
		UTCriticsDiffUtil match = new UTCriticsDiffUtil();
		LinkedList<Diff> lstPatch = match.diff_main(str1, str2);
		for (int i = 0; i < lstPatch.size(); i++) {
			Diff elem = lstPatch.get(i);

			System.out.println("[DBG] " + elem);

		}
		System.out.println("==========================================");
	}

	// @Test
	public void testGetLocationParameters2() {
		String str1 = "(value: System.err.println(((personName + \", \") + personAddress));)(label: METHOD_INVOCATION)";
		String str2 = "(value: System.err.println((($v2 + \", \") + $v1));)(label: METHOD_INVOCATION)";
		UTCriticsDiffUtil match = new UTCriticsDiffUtil();
		LinkedList<Patch> lstPatch = match.patch_make(str1, str2);
		for (int i = 0; i < lstPatch.size(); i++) {
			Patch elem = lstPatch.get(i);
			System.out.println(elem);
		}
		System.out.println("==========================================");
	}
}
