/*
 * @(#) CriticsSelectiveGeneralizeHelper.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.compare.internal.MergeSourceViewer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Point;

import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ut.seal.plugins.utils.UTCCFinder;
import ut.seal.plugins.utils.change.UTChangeDistillerFile;
import edu.utexas.seal.plugins.ast.util.others.ASTAbstract;
import edu.utexas.seal.plugins.ast.util.others.ASTMoveVariable;
import edu.utexas.seal.plugins.edit.UTWiseEditManager;
import edu.utexas.seal.plugins.util.UTCriticsPairFileInfo;
import edu.utexas.seal.plugins.util.UTCriticsTextSelection;

/**
 * @author Myoungkyu Song
 * @date Oct 28, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsSelectionHandler {
	private MergeSourceViewer	leftMSViewer		= null;
	private MergeSourceViewer	rightMSViewer		= null;
	private ISourceViewer		leftSRViewer		= null;
	private ISourceViewer		rightSRViewer		= null;
	private Point				leftSelectedRegion	= null;
	private Point				rightSelectedRegion	= null;

	private UTWiseEditManager	editManager			= new UTWiseEditManager();

	public CriticsSelectionHandler() {
		leftMSViewer = UTCriticsTextSelection.leftMergeSourceViewer;
		rightMSViewer = UTCriticsTextSelection.rightMergeSourceViewer;
		leftSRViewer = leftMSViewer.getSourceViewer();
		rightSRViewer = rightMSViewer.getSourceViewer();
		leftSelectedRegion = leftSRViewer.getSelectedRange();
		rightSelectedRegion = rightSRViewer.getSelectedRange();
	}

	public UTWiseEditManager getEditManager() {
		return this.editManager;
	}

	/**
	 * 
	 */
	public void differentiateSelectionOfComparisonView() {
		File leftFile = UTCriticsPairFileInfo.getLeftFile();
		File rightFile = UTCriticsPairFileInfo.getRightFile();
		UTChangeDistillerFile changeDistiller = new UTChangeDistillerFile();

		// changeDistiller.printChanges();
		List<SourceCodeChange> changeList = new ArrayList<SourceCodeChange>(); // changeDistiller.getChanges();
		changeDistiller.diff(rightFile, leftFile, changeList);
		for (int i = 0; i < changeList.size(); i++) {
			SourceCodeChange change = changeList.get(i);
			// skip comment changes.
			if (change.getChangedEntity().getType().isComment()) {
				continue;
			}
			// left
			if (change.getClass().getSimpleName().equalsIgnoreCase("insert")) {
				editManager.additional_functionality(change);
			} // right
			else if (change.getClass().getSimpleName().equalsIgnoreCase("delete")) {
				editManager.removed_functionality(change);
			} // left & right
			else {
				editManager.condition_expression_change(change);
			}
		}
		editManager.printEditManagerList();
	}

	void printSelectionInfo() {
		int left_offset_bgn = this.leftSelectedRegion.x;
		int left_offset_end = this.leftSelectedRegion.x + this.leftSelectedRegion.y;
		int right_offset_bgn = this.rightSelectedRegion.x;
		int right_offset_end = this.rightSelectedRegion.x + this.rightSelectedRegion.y;
		int left_line_bgn = 0, right_line_bgn = 0;
		int left_line_end = 0, right_line_end = 0;
		try {
			left_line_bgn = leftSRViewer.getDocument().getLineOfOffset(left_offset_bgn) + 1;
			left_line_end = leftSRViewer.getDocument().getLineOfOffset(left_offset_end) + 1;
			right_line_bgn = rightSRViewer.getDocument().getLineOfOffset(right_offset_bgn) + 1;
			right_line_end = rightSRViewer.getDocument().getLineOfOffset(right_offset_end) + 1;
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		System.out.println("[DBG] LEFT SELECTION " + //
				"OFFSET: (" + left_offset_bgn + " ~ " + left_offset_end + "), " + //
				"LINE: (" + left_line_bgn + ", " + left_line_end + ")");

		System.out.println("[DBG] RIGHT SELECTION " + //
				"OFFSET: (" + right_offset_bgn + " ~ " + right_offset_end + "), " + //
				"LINE: (" + right_line_bgn + ", " + right_line_end + ")");
	}

	void ccfinder() {
		String ccfinder = "/Users/mksong/programs/ccfinder/ubuntu32/ccfx";
		String target = "/Users/mksong/workspaceLocalHistory/UT-ccfinder-standalone/target/";
		String lFile = "A.java";
		String rFile = "B.java";
		String oFile = "a.ccfxd";
		// String lCcfxprep = lFile + ".java.2_0_0_0.default.ccfxprep";
		// String rCcfxprep = rFile + ".java.2_0_0_0.default.ccfxprep";
		UTCCFinder.runCcfinder(ccfinder, oFile, target, lFile, rFile);
		List<String> list = UTCCFinder.getOutputList();
		for (int i = 0; i < list.size(); i++) {
			String elem = list.get(i);
			System.out.println(elem);
		}
	}

	void getProjectInfo(IProject project) throws CoreException, JavaModelException {
		System.out.println("[DBG] Working in project " + project.getName());
		if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
			IJavaProject javaProject = JavaCore.create(project);
			printPackageInfos(javaProject);
		}
	}

	void printPackageInfos(IJavaProject javaProject) throws JavaModelException {
		IPackageFragment[] packages = javaProject.getPackageFragments();
		for (IPackageFragment mypackage : packages) {
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				System.out.println("Package " + mypackage.getElementName());
				printICompilationUnitInfo(mypackage);
			}
		}
	}

	private void printICompilationUnitInfo(IPackageFragment mypackage) throws JavaModelException {
		for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
			printCompilationUnitDetails(unit);

		}
	}

	private void printCompilationUnitDetails(ICompilationUnit unit) throws JavaModelException {
		System.out.println("Source file " + unit.getElementName());
		Document doc = new Document(unit.getSource());
		System.out.println("Has number of lines: " + doc.getNumberOfLines());
		printIMethods(unit);
	}

	private void printIMethods(ICompilationUnit unit) throws JavaModelException {
		IType[] allTypes = unit.getAllTypes();
		for (IType type : allTypes) {
			printIMethodDetails(type);
		}
	}

	private void printIMethodDetails(IType type) throws JavaModelException {
		IMethod[] methods = type.getMethods();
		for (IMethod method : methods) {
			System.out.println("Method name " + method.getElementName());
			System.out.println("Signature " + method.getSignature());
			System.out.println("Return Type " + method.getReturnType());
		}
	}

	ASTAbstract createActionExuecutable() {
		return new ASTMoveVariable();
	}

}
