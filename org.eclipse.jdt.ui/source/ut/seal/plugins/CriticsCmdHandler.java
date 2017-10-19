/*
 * @(#) CriticsCmdHandler.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package ut.seal.plugins;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import ut.seal.plugins.commands.CriticsLocalFileHistoryCmd;
import ut.seal.plugins.commands.CriticsSelectionCmd;
import ut.seal.plugins.commands.ICriticsCmd;
import ut.seal.plugins.utils.UTCfg;
import ut.seal.plugins.utils.UTFile;
import ut.seal.plugins.utils.UTLocalFileHistory;
import ut.seal.plugins.utils.change.UTChangeDistillerFile;

/**
 * @author Myoungkyu Song
 * @date Oct 18, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsCmdHandler extends AbstractHandler implements IHandler {
	private UTChangeDistillerFile	changeDistiller		= null;
	private ICriticsCmd				selectionCmd		= null;
	private ICriticsCmd				localhistoryCmd		= null;
	private String					fileNamePrv			= null;
	private String					fileNameCur			= null;
	private String					fileName			= null;
	private String					localFileHistoryCur	= null;
	private String					localFileHistoryPrv	= null;
	private String					localFileHistoryTme	= null;

	public CriticsCmdHandler() {
		this.selectionCmd = new CriticsSelectionCmd();
		this.localhistoryCmd = new CriticsLocalFileHistoryCmd();
	}

	/**
	 * @param event
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (selectionCmd.getResult() != null && UTLocalFileHistory.getSelectedLocalFileContents() != null) {
			localFileHistoryCur = selectionCmd.getResult().toString();
			localFileHistoryPrv = UTLocalFileHistory.getSelectedLocalFileContents();
			localFileHistoryTme = UTLocalFileHistory.getSelectedLocalFileModTime();
			fileName = UTLocalFileHistory.getCurrentFilePath().toFile().getName();

			System.out.println("DBG__________________________________________");
			System.out.println("[DBG] TIME: " + localFileHistoryTme);
			System.out.println(localFileHistoryPrv);
			System.out.println("DBG__________________________________________");
			System.out.println(localFileHistoryCur);
			System.out.println("DBG__________________________________________");
			writeTmpFile();
			changeDistiller = new UTChangeDistillerFile();
			changeDistiller.diff(new File(fileNamePrv), new File(fileNameCur));
			changeDistiller.printChanges();
			System.out.println("DBG__________________________________________");
		} else {
			System.out.println("[USG] PLEASE SELECT ONE FILE IN LOCAL HISTORY TABLE VIEW.");
			System.out.println("[USG] OR, PLEASE CONFIRM THE SELECTED MENU ITEM IS CORRECT.");
		}
		return null;
	}

	/**
	 * 
	 */
	void writeTmpFile() {
		String dirPath = UTCfg.getInst().readConfig().INPUTDIRPATH;
		if (!(new File(dirPath).exists())) {
			new File(dirPath).mkdir();
		}
		fileNamePrv = dirPath + fileName.replace(fileName, "_PRV.java");
		fileNameCur = dirPath + fileName.replace(fileName, "_CUR.java");
		UTFile.write(fileNamePrv, localFileHistoryPrv);
		UTFile.write(fileNameCur, localFileHistoryCur);
	}

	/**
	 *
	 */
	void showLocalFileHistory() {
		((CriticsLocalFileHistoryCmd) localhistoryCmd).printLocalFileHistory();
	}
}
