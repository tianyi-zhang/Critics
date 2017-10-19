/*
 * @(#) UTCmdLocalFileHistory.java
 *
 */
package util;

import java.util.List;

import ut.seal.plugins.utils.UTLocalFileHistory;

/**
 * @author Myoungkyu Song
 * @date Oct 18, 2013
 * @since JDK1.7
 */
public class UTCmdLocalFileHistory {

	/**
	 * 
	 * @param monitor
	 */
	public static void getLocalHistoryInfo() {
		List<String> localFileHistoryList = UTLocalFileHistory.getLocalFileHistoryList();
		for (int i = 0; i < localFileHistoryList.size(); i++) {
			String elem = localFileHistoryList.get(i);
			System.out.println(elem);
		}
	}
}
