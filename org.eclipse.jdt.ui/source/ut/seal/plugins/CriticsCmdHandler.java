/**
 * Copyright (c) 2017, UCLA Software Engineering and Analysis Laboratory (SEAL)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
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
		String dirPath = UTCfg.getInst().getConfig().INPUTDIRPATH;
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
