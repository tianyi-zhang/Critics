/*
 * @(#) UTTime.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package ut.seal.plugins.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * @author Myoungkyu Song
 * @date Oct 20, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTTime {
	public static String curTime() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm");
		return sdf.format(cal.getTime());
	}

	public static void timeStamp(long millis) {
		System.out.println("[RST] TIME: " + String.format("%d min, %d sec", //
				TimeUnit.MILLISECONDS.toMinutes(millis), //
				TimeUnit.MILLISECONDS.toSeconds(millis) - //
						TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))));
	}
}
