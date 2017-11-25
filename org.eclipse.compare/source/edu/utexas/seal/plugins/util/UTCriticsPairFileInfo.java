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
