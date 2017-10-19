/*
 * @(#) CriticsOverlayViewCommonEvent.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.overlay.event;

import java.io.File;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

import ut.seal.plugins.utils.UTFile;
import ut.seal.plugins.utils.ast.UTASTNodeConverter;
import ut.seal.plugins.utils.ast.UTASTNodeFinder;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import edu.utexas.seal.plugins.overlay.ICriticsHTMLKeyword;
import edu.utexas.seal.plugins.overlay.model.CriticsCBTreeHelper;
import edu.utexas.seal.plugins.overlay.model.CriticsCBTreeNode;
import edu.utexas.seal.plugins.overlay.view.CriticsOverlayBrowser;
import edu.utexas.seal.plugins.overlay.view.CriticsOverlayNewBrowser;
import edu.utexas.seal.plugins.overlay.view.CriticsOverlayView;
import edu.utexas.seal.plugins.util.UTCriticsPairFileInfo;

/**
 * @author Myoungkyu Song
 * @date Feb 28, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsOverlayViewCommonEvent implements ICriticsHTMLKeyword {

	protected CriticsOverlayView		mCriticsOverlayView	= null;
	protected CriticsOverlayNewBrowser	mNewDiffBrowser	    = null;
	protected TableViewer				mSummaryTableViewer = null;
	protected TableViewer 				mAnomalyTableViewer = null;
	protected Node						mQTreeLeftRev		= null;
	protected Node						mQTreeRightRev		= null;
	protected List<Node>				mBaseCodeNodeList	= null;

	// /////////////////////////////////////////////////////////////////////
	CriticsOverlayBrowser				mHTMLLeftBrowser	= null;
	CriticsOverlayBrowser				mHTMLRightBrowser	= null;
	CheckboxTreeViewer					mTVSimilarContext	= null;
	Button								mBtnRadioButton		= null;
	Text								mTxtSearch			= null;
	CriticsCBTreeHelper					mCBTreeHelper		= null;
	// /////////////////////////////////////////////////////////////////////
	boolean								mBfExpand			= true;

	protected void setSourceSecondNode(CriticsCBTreeNode aTSelected) {
		Node qTreeLeftRev = mCriticsOverlayView.getQTreeNewRev();
		Node qTreeRightRev = mCriticsOverlayView.getQTreeOldRev();
		File fSelectedRight = aTSelected.getFile();
		String fnSelectedRight = fSelectedRight.getAbsolutePath();
		Node ndSelectedRight = aTSelected.getNode();
		MethodDeclaration methodSelectedRight = findMethod(fnSelectedRight, ndSelectedRight);
		ndSelectedRight.print();
		mHTMLRightBrowser.setSourceBaseNode(qTreeRightRev, UTCriticsPairFileInfo.getRightFile());
		mHTMLRightBrowser.setSourceSecondNode(ndSelectedRight, fSelectedRight);
		// ////////////////////////////////////////////////////////////////////////
		String prjNameRight = UTCriticsPairFileInfo.getRightProjectName();
		String prjNameLeft = UTCriticsPairFileInfo.getLeftProjectName();
		String fnSelectedLeft = fnSelectedRight.replace(prjNameRight, prjNameLeft);
		Node ndSelectedLeft = getOppositeNode(fnSelectedLeft, methodSelectedRight);
		mHTMLLeftBrowser.setSourceBaseNode(qTreeLeftRev, UTCriticsPairFileInfo.getLeftFile());
		mHTMLLeftBrowser.setSourceSecondNode(ndSelectedLeft, new File(fnSelectedLeft));
	}

	/**
	 * Find method.
	 * 
	 * @param aFile the a file
	 * @param aNode the a node
	 * @return the method declaration
	 */
	protected MethodDeclaration findMethod(String aFile, Node aNode) {
		UTASTNodeFinder finder = new UTASTNodeFinder();
		List<MethodDeclaration> methods = finder.findMethods(aFile);
		MethodDeclaration method = finder.findMethod(aNode, methods);
		return method;
	}

	/**
	 * Gets the opposite node.
	 * 
	 * @param aFile the a file
	 * @param aMethodDecl the a method decl
	 * @return the opposite node
	 */
	protected Node getOppositeNode(String aFile, MethodDeclaration aMethodDecl) {
		String methodName = aMethodDecl.getName().getFullyQualifiedName();
		List<?> parameters = aMethodDecl.parameters();
		// ////////////////////////////////////////////////////////////////
		String prjNameRight = UTCriticsPairFileInfo.getRightProjectName();
		String prjNameLeft = UTCriticsPairFileInfo.getLeftProjectName();
		String fileNameOpposite = aFile.replace(prjNameRight, prjNameLeft);
		String codeTextOpposite = UTFile.getContents(fileNameOpposite);
		// ////////////////////////////////////////////////////////////////
		UTASTNodeFinder finder = new UTASTNodeFinder();
		List<MethodDeclaration> methods = finder.findMethods(fileNameOpposite);
		for (int i = 0; i < methods.size(); i++) {
			MethodDeclaration iMethod = methods.get(i);
			String iMethodName = iMethod.getName().getFullyQualifiedName();
			List<?> iParameters = iMethod.parameters();
			if (iMethodName.equals(methodName) && compareParameters(parameters, iParameters)) {
				UTASTNodeConverter nodeConverter = new UTASTNodeConverter();
				Node node = nodeConverter.convertMethod(iMethod, codeTextOpposite, new File(fileNameOpposite));
				return node;
			}
		}
		throw new RuntimeException("Node getOppositeNode(String aFile, MethodDeclaration aMethodDecl)");
	}

	/**
	 * Compare parameters.
	 * 
	 * @param aParm1 the a parm1
	 * @param aParm2 the a parm2
	 * @return true, if successful
	 */
	protected boolean compareParameters(List<?> aParm1, List<?> aParm2) {
		if (aParm1 == null && aParm2 == null) {
			return true;
		}
		if (aParm1 != null && aParm2 != null && aParm1.size() == aParm2.size()) {
			for (int i = 0; i < aParm1.size(); i++) {
				String[] str1 = aParm1.get(i).toString().split("\\s");
				String[] str2 = aParm2.get(i).toString().split("\\s");
				String elem1 = str1[0];
				String elem2 = str2[0];
				if (!elem1.equals(elem2))
					return false;
			}
			return true;
		}
		return false;
	}
}
