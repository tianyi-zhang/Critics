/*
 * @(#) UTCfgInfo.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package ut.seal.plugins.utils;

/**
 * @author Myoungkyu Song
 * @date Feb 1, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTCfgInfo {
	private static UTCfgInfo	cfgInfo		= null;

	public String				ARFFFILENAME;
	public String				C_CONFIG_DR	= "UT.CONFIG";
	public String				C_CONFIG_FL	= "config.txt";
	public int					CLUSTER_LIMIT_SIZE;
	public String				CSVFILENAME;
	public String				IMG_EX_PATH;
	public String				IMG_IN_PATH;
	public String				INPUTDIRPATH;
	public String				LOG4JPATH;
	public String				LOG4JPROPERTIES;
	public String				S			= System.getProperty("file.separator");
	public int					SIMILARITY_THRESHOLD;
	public String				TEXTDRAG_JS;
	public String				WORKSPACE;

	public static UTCfgInfo getInst() {
		if (cfgInfo == null) {
			cfgInfo = new UTCfgInfo();
		}
		return cfgInfo;
	}
}
