/*
 * @(#) CriticsCmdHandler.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugin.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
//import org.eclipse.ui.handlers.HandlerUtil;

import util.UTCmdLocalFileHistory;
import util.UTCmdTextSelection;

/**
 * @author Myoungkyu Song
 * @date Oct 18, 2013
 * @since JDK1.6
 */
public class CriticsCmdHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// HandlerUtil.getActiveWorkbenchWindow(event).close();

		UTCmdTextSelection cmdTextSelection = new UTCmdTextSelection();
		cmdTextSelection.procExampleAction();
		System.out.println("DBG__________________________________________");
		UTCmdLocalFileHistory.getLocalHistoryInfo();
		System.out.println("DBG__________________________________________");
		return null;
	}

}
