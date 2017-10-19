/*
 * @(#) ICriticsCmd.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package ut.seal.plugins.commands;

/**
 * @author Myoungkyu Song
 * @date Oct 19, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public interface ICriticsCmd {
	final String	MSG_SELECTION_CMD_USAGE	= "[USG] SELECT CODE BLOCK AND SAVE A FILE (i.e., CTRL + S)";

	public Object getResult();
}
