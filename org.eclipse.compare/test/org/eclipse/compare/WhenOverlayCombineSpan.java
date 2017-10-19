/*
 * @(#) WhenOverlayCombineSpan.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package org.eclipse.compare;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ut.seal.plugins.utils.UTFile;

/*
 * @(#) WhenOverlayCombineSpan.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
public class WhenOverlayCombineSpan {
	@Test
	public void testWhenOverlayCombineSpan() {
		String fileName = "/Users/mksong/workspaceCritics/org.eclipse.compare/test/org/eclipse/compare/test1.html";
		String fileName2 = "/Users/mksong/workspaceCritics/org.eclipse.compare/test/org/eclipse/compare/test2.html";
		List<String> list = UTFile.readFileToList(fileName);

		String pre = null, cur = null;
		Integer iPre = null, iCur = null;
		List<Integer> lstDuplicatedLine = new ArrayList<Integer>();
		List<Integer> lstLastLine = new ArrayList<Integer>();
		List<Integer> lstFirstLine = new ArrayList<Integer>();
		List<Integer> lstFirstLineDup = new ArrayList<Integer>();
		for (int i = 0; i < list.size(); i++) {
			String line = list.get(i);
			if (line.trim().startsWith("<span class")) {
				String key = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
				pre = cur;
				cur = key;
				iPre = iCur;
				iCur = i;
				System.out.println("[DBG] " + i + ", " + key);
				if (pre != null && pre.equals(cur)) {
					lstDuplicatedLine.add(i);
				}
				if (pre != null && !pre.equals(cur)) {
					lstFirstLine.add(iCur);
				}
				if (pre != null && !pre.equals(cur)) {
					lstLastLine.add(iPre);
				}
			}
		}
		lstLastLine.add(iCur);
		System.out.println("------------------------------------------");
		for (int i = 0; i < lstFirstLine.size(); i++) {
			Integer elem = lstFirstLine.get(i);
			System.out.println("[DBG2] " + elem);
		}
		System.out.println("------------------------------------------");
		for (int i = 0; i < lstLastLine.size(); i++) {
			Integer elem = lstLastLine.get(i);
			System.out.println("[DBG3] " + elem);
		}
		System.out.println("------------------------------------------");
		String nextKey = null, curKey = null;
		for (int i = 0; i < list.size(); i++) {
			String line = list.get(i);
			if (line.trim().startsWith("<span class")) {
				String key = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
				curKey = key;
				nextKey = getNextSpanBegin(list, i);
				if (checkFirstLine(lstFirstLine, i) && nextKey != null && nextKey.equals(curKey)) {
					lstFirstLineDup.add(i);
					System.out.println("[DBG0] " + i);
				}
			}
		}
		System.out.println("------------------------------------------");
		for (int i = 0; i < lstDuplicatedLine.size(); i++) {
			Integer iLineNum = lstDuplicatedLine.get(i);
			Integer pair = getPair(list, iLineNum);
			// System.out.println("[DBG] " + iLineNum + ", " + pair);
			list.set(iLineNum, "");
			if (isRemovable(lstLastLine, iLineNum))
				list.set(pair, "");
		}
		for (int i = 0; i < lstFirstLineDup.size(); i++) {
			Integer iLineNum = lstFirstLineDup.get(i);
			Integer pair = getPair(list, iLineNum);
			list.set(pair, "");
		}
		UTFile.writeFile(fileName2, list);
	}

	private String getNextSpanBegin(List<String> list, int index) {
		for (int i = index + 1; i < list.size(); i++) {
			String iLine = list.get(i);
			if (iLine.trim().startsWith("<span class")) {
				String key = iLine.substring(iLine.indexOf("\"") + 1, iLine.lastIndexOf("\""));
				return key;
			}
		}
		return null;
	}

	private boolean checkFirstLine(List<Integer> aLst, int index) {
		for (int i = 0; i < aLst.size(); i++) {
			Integer iLineNum = aLst.get(i);
			if (iLineNum == index) {
				return true;
			}
		}
		return false;
	}

	private boolean isRemovable(List<Integer> aLst, Integer aLineNum) {
		for (int i = 0; i < aLst.size(); i++) {
			Integer iLineNumNotRemove = aLst.get(i);
			if (iLineNumNotRemove == aLineNum)
				return false;
		}
		return true;
	}

	int getPair(List<String> aLst, int index) {
		for (int i = index; i < aLst.size(); i++) {
			String elem = aLst.get(i);
			if (elem.equals("</span>")) {
				return i;
			}
		}
		return -1;
	}
}
