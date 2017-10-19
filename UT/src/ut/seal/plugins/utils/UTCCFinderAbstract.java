/*
 * @(#) UTCCFinderAbstract.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package ut.seal.plugins.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.exec.PumpStreamHandler;

/**
 * @author Myoungkyu Song
 * @date Oct 28, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTCCFinderAbstract {
	private static List<String>	outputList	= new ArrayList<String>();

	public static List<String> getOutputList() {
		return outputList;
	}

	/**
	 * Some time later the result handler callback was invoked so we
	 * can safely request the exit value
	 * 
	 * @param cmdLine
	 */
	protected static void invokeCmd(CommandLine cmdLine) {
		outputList.clear();
		LogOutputStream output = new LogOutputStream() {
			@Override
			protected void processLine(String line, int level) {
				outputList.add(">>>" + line);
			}
		};
		PumpStreamHandler streamHandler = new PumpStreamHandler(output);
		DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
		ExecuteWatchdog watchdog = new ExecuteWatchdog(60 * 5000);
		Executor executor = new DefaultExecutor();
		executor.setExitValue(1);
		executor.setWatchdog(watchdog);
		try {
			System.out.println("DBG__________________________________________");
			System.out.println(cmdLine);
			System.out.println("DBG__________________________________________");
			executor.setStreamHandler(streamHandler);
			executor.execute(cmdLine, resultHandler);
			resultHandler.waitFor();
			// int exitValue = resultHandler.getExitValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
