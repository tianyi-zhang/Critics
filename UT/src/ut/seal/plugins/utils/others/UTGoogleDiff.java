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
package ut.seal.plugins.utils.others;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ut.seal.plugins.utils.others.DiffMatchPatch.Diff;

/**
 * @author Myoungkyu Song
 * @date Oct 21, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTGoogleDiff {

	public static void diff(String textOld, String textNew) {
		UTGoogleDiff m = new UTGoogleDiff();
		DiffMatchPatch dmp = new DiffMatchPatch();
		List<Diff> diffs = dmp.diff_main(textOld, textNew);
		try {
			System.out.println("DBG__________________________________________");
			for (int i = 0; i < diffs.size(); i++) {
				Diff elem = diffs.get(i);
				if (elem.operation == Operation.EQUAL)
					continue;
				System.out.println("       * " + elem.operation);
				System.out.println(elem.text);
				System.out.println();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("DBG__________________________________________");
		System.out.println(dmp.diff_toDelta((LinkedList<Diff>) diffs));
		List<Character> textOldDeleted = new ArrayList<Character>();
		List<Character> textNewInserted = new ArrayList<Character>();
		m.getEdit(diffs, textOldDeleted, textOld, false);
		m.getEdit(diffs, textNewInserted, textNew, true);
		System.out.println("DBG__________________________________________");
		System.out.println("[DBG] CHANGES - DELETED:");
		System.out.println("DBG__________________________________________");
		m.print(textOldDeleted);
		System.out.println();
		System.out.println("DBG__________________________________________");
		System.out.println("[DBG] INSERT: ");
		System.out.println("DBG__________________________________________");
		m.print(textNewInserted);
		System.out.println();
	}

	/**
	 * 
	 * @param diffs
	 * @param editText
	 * @param orgText
	 * @param isNew
	 */
	public void getEdit(List<Diff> diffs, List<Character> editText, String orgText, boolean isNew) {
		convertStrToList(orgText, editText);
		int offset = 0;
		for (int i = 0; i < diffs.size(); i++) {
			Diff elem = diffs.get(i);

			if (elem.operation == Operation.EQUAL) {
				replaceEqualWithNull(editText, offset, offset + elem.text.length());
				offset += elem.text.length();
			} else if (isNew && elem.operation == Operation.INSERT) {
				offset += elem.text.length();
			} else if (!isNew && elem.operation == Operation.DELETE) {
				offset += elem.text.length();
			}
		}
	}

	/**
	 * 
	 * @param org
	 * @param bgn
	 * @param end
	 */
	public void replaceEqualWithNull(List<Character> org, int bgn, int end) {
		Character LSC = new Character('\n');
		for (int i = 0; i < org.size(); i++) {
			if (bgn <= i && i < end) {
				if (org.get(i).equals(LSC))
					org.set(i, LSC);
				else
					org.set(i, null);
			}
		}
	}

	/**
	 * 
	 * @param org
	 * @param cur
	 */
	public void convertStrToList(String org, List<Character> cur) {
		for (int i = 0; i < org.length(); i++) {
			cur.add(org.charAt(i));
		}
	}

	/**
	 * 
	 * @param charList
	 */
	public void print(List<Character> charList) {
		for (int i = 0; i < charList.size(); i++) {
			Character elem = charList.get(i);
			if (elem == null)
				System.out.print("*");
			else
				System.out.print(elem);
		}
	}

	/**
	 * 
	 * @param list
	 * @return
	 */
	public String toStringfromList(List<String> list) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i) + System.getProperty("line.separator"));
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param filename
	 * @return
	 */
	public List<String> fileread(String filename) {
		BufferedReader in = null;
		List<String> contents = new ArrayList<String>();
		try {
			in = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = in.readLine()) != null) {
				contents.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return contents;
	}
}
