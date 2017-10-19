package ut.seal.plugins.utils;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;

import ut.seal.plugins.utils.UTCfg;

public class UTLog {
	static Logger	log			= Logger.getLogger(ut.seal.plugins.utils.UTLog.class.getName());
	static String	filePath	= null;
	static String	S			= System.getProperty("line.separator");
	static boolean	test		= true;

	/**
	 * 
	 * @param logFileName
	 */
	static public void init(String logFileName) {
		if (test) {
			return;
		}
		PropertyConfigurator.configure(UTCfg.getInst().readConfig().LOG4JPROPERTIES);
		Logger.getRootLogger().getLoggerRepository().resetConfiguration();
		FileAppender fa = new FileAppender();
		fa.setName("FileLogger");
		fa.setFile(logFileName);
		where(logFileName);
		fa.setLayout(new PatternLayout("%m"));
		fa.setThreshold(Level.DEBUG);
		fa.setAppend(true);
		fa.activateOptions();
		// ConsoleAppender ca = new ConsoleAppender();
		// ca.setName("ConsoleLogger");
		// ca.setLayout(new PatternLayout(" %m"));
		// ca.setThreshold(Level.DEBUG);
		// ca.activateOptions();
		Logger.getLogger(ut.seal.plugins.utils.UTLog.class).addAppender(fa);
		// Logger.getRootLogger().addAppender(ca);
		println(false, "<pre>");
	}

	public static void println(boolean isPrintable, Object message) {
		if (test) {
			return;
		}
		log.debug(message + S);
		if (isPrintable) {
			System.out.println(message);
		}
	}

	public static void println(boolean isPrintable, Object message, Object details) {
		if (test) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		log.debug(sb.append(message).append(details).append(S));
		if (isPrintable) {
			System.out.println(message);
		}
	}

	public static void print(boolean isPrintable, Object message) {
		if (test) {
			return;
		}
		log.debug(message);
		if (isPrintable) {
			System.out.print(message);
		}
	}

	public static void print(boolean isPrintable, Object message, Object details) {
		if (test) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		log.debug(sb.append(message).append(details));
		if (isPrintable) {
			System.out.print(message);
		}
	}

	static public void where(String fp) {
		try {
			filePath = new File(fp).getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static public String where() {
		return filePath;
	}
}