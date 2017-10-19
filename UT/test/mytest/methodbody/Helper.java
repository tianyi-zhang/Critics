/*
 * @(#) Helper.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package mytest.methodbody;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Myoungkyu Song
 * @date Nov 10, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class Helper {
	public static String readFile(String fileName) {
		BufferedReader in = null;
		StringBuilder sb = new StringBuilder();
		try {
			in = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}
}