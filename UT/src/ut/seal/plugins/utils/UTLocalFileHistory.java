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
/*
 * @(#) UTLocalHistory.java
 *
 */
package ut.seal.plugins.utils;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IPath;

/**
 * @author Myoungkyu Song
 * @date Oct 18, 2013
 * @since JDK1.7
 */
public class UTLocalFileHistory {
	private static List<String>	localFileHistoryList	= new ArrayList<String>();
	private static String		localFileRevisionContents;
	private static IPath		selectedlocalFilePath;
	private static long			selectedLocalFileModTime;
	private static IPath		currentFilePath;

	public static List<String> getLocalFileHistoryList() {
		return localFileHistoryList;
	}

	public static void clearLocalFileHistoryList() {
		localFileHistoryList.clear();
	}

	public static String getSelectedLocalFileContents() {
		return localFileRevisionContents;
	}

	public static void setSelectedLocalFileContents(String contents) {
		UTLocalFileHistory.localFileRevisionContents = contents;
	}

	public static String getSelectedLocalFileModTime() {
		return DateFormat.getDateTimeInstance().format(new Date(selectedLocalFileModTime));
	}

	public static void setSelectedLocalFileModTime(long time) {
		UTLocalFileHistory.selectedLocalFileModTime = time;
	}

	public static IPath getSelectedLocalFilePath() {
		return selectedlocalFilePath;
	}

	public static void setSelectedLocalFilePath(IPath path) {
		selectedlocalFilePath = path;
	}

	public static IPath getCurrentFilePath() {
		return currentFilePath;
	}

	public static void setCurrentFilePath(IPath path) {
		currentFilePath = path;
	}
}
