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
