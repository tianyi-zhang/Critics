/*
 * @(#) UTCCFinder.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package ut.seal.plugins.utils;

import org.apache.commons.exec.CommandLine;

/**
 * @author Myoungkyu Song
 * @date Oct 28, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTCCFinder extends UTCCFinderAbstract {

	public static void runCcfinder(String ccfinder, String oFile, //
			String target, String lFile, String rFile) {
		CommandLine cmdLine = new CommandLine(ccfinder);
		cmdLine.addArgument("d");
		cmdLine.addArgument("java");
		cmdLine.addArgument("-b"); // The minimum length of the detected code clones
		cmdLine.addArgument("3");
		cmdLine.addArgument("-o");
		cmdLine.addArgument(target + oFile);
		cmdLine.addArgument(target + lFile);
		cmdLine.addArgument("-is");
		cmdLine.addArgument(target + rFile);
		cmdLine.addArgument("-w");
		cmdLine.addArgument("f-w-g+"); // the last -w w-f-g+ means "do not detect code clones within a file", "do not detect code clones between files in the same file group", and "detect code clones between files from the distinct file groups".

		invokeCmd(cmdLine);

		cmdLine = new CommandLine(ccfinder);
		cmdLine.addArgument("p");
		cmdLine.addArgument(target + "a.ccfxd");

		invokeCmd(cmdLine);
	}
}
