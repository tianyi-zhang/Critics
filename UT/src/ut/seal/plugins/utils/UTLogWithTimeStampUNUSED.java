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
		PropertyConfigurator.configure(UTCfg.getInst().readConfig().LOG4JPROPERTIES);
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
