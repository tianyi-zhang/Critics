/*
 * @(#) UTCfg.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package ut.seal.plugins.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Myoungkyu Song
 * @date Oct 22, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTCfg {
	private static UTCfg		cfg					= null;
	private static UTCfgInfo	cfgInfo				= null;
	public final static String	PARM_VAR_NAME		= "\\$var";
	public final static String	PARM_VAR_NAME_TMP	= "\\$^^\\$";

	/**
	 * 
	 * @return
	 */
	public static UTCfg getInst() {
		if (cfg == null) {
			cfg = new UTCfg();
		}
		if (cfgInfo == null) {
			cfgInfo = UTCfgInfo.getInst();
		}
		File eclipseLogPath = org.eclipse.core.runtime.Platform.getLogFileLocation().toFile();
		if (eclipseLogPath.exists()) {
			cfgInfo.WORKSPACE = cfg.getWorkDir();
			if (cfgInfo.WORKSPACE == null)
				throw new RuntimeException("CAN NOT FIND ECLIPSE LOG.");
		}
		return cfg;
	}

	/**
	 * 
	 * @return
	 */
	public String getWorkDir() {
		File eclipseLogPath = org.eclipse.core.runtime.Platform.getLogFileLocation().toFile();
		if (eclipseLogPath.exists()) {
			UTFile utFile = new UTFile();
			return utFile.readReverseByToken(eclipseLogPath, "-dev", "/", ".metadata"); // "/home/troy/Critics/project_critics/trunk/code/v03/";
		}
		return null;
	}

	/**
	 * 
	 * @param inputPath
	 * @return
	 */
	public UTCfgInfo readConfig() {
		String cfgFilePath = cfgInfo.WORKSPACE + cfgInfo.S + cfgInfo.C_CONFIG_DR + cfgInfo.S + cfgInfo.C_CONFIG_FL;
		BufferedReader in = null;
		String line = null;
		try {
			in = new BufferedReader(new FileReader(cfgFilePath));
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("#") || line.startsWith("//") || line.length() == 0)
					continue;
				String key = null, value = null;
				if (line.startsWith("@")) {
					int idx = line.indexOf(":");
					key = line.substring(0, idx).trim();
					value = line.substring(idx + 1).trim();
				}
				assignment(key, value);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return cfgInfo;
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	private void assignment(String key, String value) {
		if (key.equalsIgnoreCase("@INPUTDIRPATH")) {
			cfgInfo.INPUTDIRPATH = value;
		} else if (key.equalsIgnoreCase("@LOG4J.PROPERTIES")) {
			cfgInfo.LOG4JPROPERTIES = value;
		} else if (key.equalsIgnoreCase("@LOG4J.PATH")) {
			cfgInfo.LOG4JPATH = value;
		} else if (key.equalsIgnoreCase("@CSVFILENAME")) {
			cfgInfo.CSVFILENAME = value;
		} else if (key.equalsIgnoreCase("@ARFFFILENAME")) {
			cfgInfo.ARFFFILENAME = value;
		} else if (key.equalsIgnoreCase("@CLUSTER.LIMIT.SIZE")) {
			cfgInfo.CLUSTER_LIMIT_SIZE = Integer.valueOf(value);
		} else if (key.equalsIgnoreCase("@IMGINPATH")) {
			cfgInfo.IMG_IN_PATH = value;
		} else if (key.equalsIgnoreCase("@IMGEXPATH")) {
			cfgInfo.IMG_EX_PATH = value;
		} else if (key.equalsIgnoreCase("@TEXTDRAG_JS")) {
			cfgInfo.TEXTDRAG_JS = value;
		} else if (key.equalsIgnoreCase("@SIMILARITY_THRESHOLD")) {
			cfgInfo.SIMILARITY_THRESHOLD = Integer.valueOf(value);
		}
	}
}

// File inputPath = null;
// File outputPath = null;
// String inputDirName = "INPUT";
// String outputDirName = "OUTPUT";
//
// public UTCfg(String path) {
// setPath(path);
// }
//
// /**
// * read the working directory path of the application
// */
// public void readWorkPathOfStandAloneApp() {
// ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
// URL url = classLoader.getResource(".");
// inputPath = new File(url.getPath() + ".." + S + ".." + S + inputDirName);
// outputPath = new File(url.getPath() + ".." + S + ".." + S + outputDirName);
// }
//
// /**
// *
// * @param path
// */
// private void setPath(String path) {
// this.inputPath = new File(path + S + inputDirName);
// this.outputPath = new File(path + S + outputDirName);
// }

