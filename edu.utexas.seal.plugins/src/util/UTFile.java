/*
 * @(#) UTFile.java
 *
 */
package util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Myoungkyu Song
 * @date Oct 17, 2013
 * @since JDK1.6
 */
public class UTFile {
	private static final int	BUF_SIZE	= 8192;

	public static long copyTo(InputStream is, OutputStream os) {
		byte[] buf = new byte[BUF_SIZE];
		long tot = 0;
		int len = 0;
		try {
			while (-1 != (len = is.read(buf))) {
				os.write(buf, 0, len);
				tot += len;
			}
		} catch (IOException ioe) {
			throw new RuntimeException("error - ", ioe);
		}
		return tot;
	}

}