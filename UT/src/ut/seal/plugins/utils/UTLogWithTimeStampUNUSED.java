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
package ut.seal.plugins.utils;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;

import ut.seal.plugins.utils.UTCfg;

public class UTLogWithTimeStampUNUSED {
	static Logger	log			= Logger.getLogger(ut.seal.plugins.utils.UTLogWithTimeStampUNUSED.class.getName());
	static String	filePath	= null;
	static String	S			= System.getProperty("line.separator");

	/**
	 * 
	 * @param msg
	 * @param clazz
	 */
	static public void log(String msg, Class<?> clazz) {
		log = Logger.getLogger(clazz);
		log.debug(msg);
	}

	/**
	 * 
	 * @param msg
	 */
	static public void log(String msg) {
		// System.out.println(msg);
		log.debug(msg + S);
	}

	static public void print(String msg) {
		// System.out.print(msg);
		log.debug(msg);
	}

	/**
	 * 
	 * @param msg
	 */
	static public void log(String msg, boolean isPrintable) {
		// if (isPrintable) {
		// System.out.println(msg);
		// }
		log.debug(msg + S);
	}

	static public void print(String msg, boolean isPrintable) {
		// if (isPrintable) {
		// System.out.print(msg);
		// }
		log.debug(msg);
	}

	/**
	 * 
	 * @param fp
	 */
	static public void where(String fp) {
		try {
			filePath = new File(fp).getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return
	 */
	static public String where() {
		return filePath;
	}

	/**
	 * 
	 * @param logFileName
	 */
	static public void init(String logFileName) {
		PropertyConfigurator.configure(UTCfg.getInst().getConfig().LOG4JPROPERTIES);
		Logger.getRootLogger().getLoggerRepository().resetConfiguration();
		FileAppender fa = new FileAppender();
		fa.setName("FileLogger");
		fa.setFile(logFileName);
		where(logFileName);
		fa.setLayout(new PatternLayout("%d{dd MMM yyyy HH:mm:ss,SSS} %m"));
		fa.setThreshold(Level.DEBUG);
		fa.setAppend(true);
		fa.activateOptions();
		ConsoleAppender ca = new ConsoleAppender();
		ca.setName("ConsoleLogger");
		ca.setLayout(new PatternLayout(" %m"));
		ca.setThreshold(Level.DEBUG);
		ca.activateOptions();
		// Logger.getRootLogger().addAppender(ca);
		Logger.getLogger(ut.seal.plugins.utils.UTLogWithTimeStampUNUSED.class).addAppender(fa);
		Logger.getLogger(ut.seal.plugins.utils.UTLogWithTimeStampUNUSED.class).addAppender(ca);
	}
}
