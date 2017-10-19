/*
 * @(#) CriticsCmdHandler.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import edu.utexas.seal.plugins.util.UTCriticsEnable;

/**
 * @author Myoungkyu Song
 * @date Oct 18, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsEnableHandler extends AbstractHandler implements IHandler {

	public CriticsEnableHandler() {
	}

	/**
	 * @param event
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("DBG__________________________________________");
		System.out.println("[DBG] SELECTED ENABLE HANDLER");
		System.out.println("DBG__________________________________________");

		UTCriticsEnable.autoSelection = !UTCriticsEnable.autoSelection;
		System.out.println("[DBG] " + UTCriticsEnable.autoSelection);
		return null;
	}
}
