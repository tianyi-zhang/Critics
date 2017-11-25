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

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ch.uzh.ifi.seal.changedistiller.treedifferencing.NodeValueArg;
import edu.utexas.seal.plugins.util.UTCriticsDiffUtil;
import edu.utexas.seal.plugins.util.UTCriticsDiffUtil.Diff;
import edu.utexas.seal.plugins.util.UTCriticsDiffUtil.Patch;

/**
 * @author Myoungkyu Song
 * @date Jan 31, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class TestCriticsFindContext2 {

	public void testGetLocationParameters1() {
		System.out.println("[DBG0] TEST 1");
		String str1 = "(value: String personName customerName;)(label: VARIABLE_DECLARATION_STATEMENT)";
		String str2 = "(value: String $v2 $p0;)(label: VARIABLE_DECLARATION_STATEMENT)";

		String updateStr1 = str1; // .replaceAll(" ", delimeter);
		String updateStr2 = str2; // .replaceAll(" ", delimeter);

		UTCriticsDiffUtil match = new UTCriticsDiffUtil();
		LinkedList<Patch> lstPatch = match.patch_make(updateStr1, updateStr2);
		for (int i = 0; i < lstPatch.size(); i++) {
			Patch elem = lstPatch.get(i);
			System.out.println("[DBG] " + updateStr1);
			System.out.println("[DBG] " + elem);

			for (Diff aDiff : elem.diffs) {
				switch (aDiff.operation) {
				case DELETE:
					break;
				default:
					break;
				}
			}
		}
		System.out.println("------------------------------------------");
	}

	public void testGetLocationParameters2() {
		System.out.println("[DBG0] TEST 2");
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

	@Test
	public void testGetLocationParameters3() {
		System.out.println("[DBG0] TEST 3");
		String str1 = "(value: String personName = customerName;)(label: VARIABLE_DECLARATION_STATEMENT)";
		String str2 = "(value: String $v2 = $p0;)(label: VARIABLE_DECLARATION_STATEMENT)";

		UTCriticsDiffUtil diff = new UTCriticsDiffUtil();
		List<NodeValueArg> lstNodeValueArg = diff.getNodeValueArg(str1, str2);

		for (int i = 0; i < lstNodeValueArg.size(); i++) {
			NodeValueArg iArg = lstNodeValueArg.get(i);
			System.out.println(iArg.toString());
		}
		System.out.println("==========================================");
	}
}
