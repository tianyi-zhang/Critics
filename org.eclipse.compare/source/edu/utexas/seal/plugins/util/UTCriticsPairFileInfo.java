/*
 * @(#) UTCriticsPairFileInfo.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.util;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;

/**
 * @author Myoungkyu Song
 * @date Oct 24, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTCriticsPairFileInfo extends UTCriticsAbsractPairFileInfo {
	public static IProject getLeftIProject() {
		return leftIProject;
	}

	public static IProject getRightIProject() {
		return rightIProject;
	}

	public static IPackageFragment[] getLeftIPackages() {
		return leftIPackages;
	}

	public static IPackageFragment[] getRightIPackages() {
		return rightIPackages;
	}

	public static IPackageFragment getLeftIPackageFragment() {
		return leftIPackageFragment;
	}

	public static IPackageFragment getRightIPackageFragment() {
		return rightIPackageFragment;
	}

	public static ICompilationUnit getLeftICompilationUnit() {
		return leftICompilationUnit;
	}

	public static ICompilationUnit getRightICompilationUnit() {
		return rightICompilationUnit;
	}

	public static String getLeftFilePath() {
		return leftFilePath;
	}

	public static void setLeftFilePath(String l) {
		leftFilePath = l;
		UTCriticsAbsractPairFileInfo.setLeftFilePath(l);
	}

	public static String getRightFilePath() {
		return rightFilePath;
	}

	public static void setRightFilePath(String r) {
		rightFilePath = r;
		UTCriticsAbsractPairFileInfo.setRightFilePath(r);
	}

	public static File getLeftFile() {
		return leftFile;
	}

	public static void setLeftFile(File leftFile) {
		UTCriticsPairFileInfo.leftFile = leftFile;
	}

	public static File getRightFile() {
		return rightFile;
	}

	public static void setRightFile(File rightFile) {
		UTCriticsPairFileInfo.rightFile = rightFile;
	}

	public static String getLeftProjectName() {
		return leftProjectName;
	}

	public static void setLeftProjectName(String leftProjectName) {
		UTCriticsPairFileInfo.leftProjectName = leftProjectName;
	}

	public static String getRightProjectName() {
		return rightProjectName;
	}

	public static void setRightProjectName(String rightProjectName) {
		UTCriticsPairFileInfo.rightProjectName = rightProjectName;
	}
}
