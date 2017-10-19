package edu.utexas.seal.plugins;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.ResourceNode;
import org.eclipse.compare.internal.MergeSourceViewer;
//import org.eclipse.compare.internal.merge.DocumentMerger;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;

import ut.seal.plugins.utils.change.UTChangeDistillerFile;
import edu.utexas.seal.plugins.util.UTCriticsStructureSelection;
import edu.utexas.seal.plugins.util.UTCriticsTextSelection;

/**
 * @author Tianyi Zhang
 * @date Oct 25, 2013 11:31:30 AM
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CompareProjectsHandler extends AbstractHandler {

	private MergeSourceViewer	leftMSViewer			= null;
	private MergeSourceViewer	rightMSViewer			= null;
	// private DocumentMerger docMerger = null;
	private ISelection			currentSel				= null;
	private String				leftFullPath			= null;
	private String				rightFullPath			= null;
	private String				leftContributorName		= null;
	private String				rightContributorName	= null;
	private String				leftRelPath				= null;
	private String				rightRelPath			= null;

	/**
	 * @return
	 * @throws BadLocationException
	 * @throws IOException
	 * 
	 */
	boolean initiate() throws BadLocationException, IOException {
		// docMerger = UTCriticsTextSelection.docMerger;
		leftMSViewer = UTCriticsTextSelection.leftMergeSourceViewer;
		rightMSViewer = UTCriticsTextSelection.rightMergeSourceViewer;

		if (leftMSViewer == null || rightMSViewer == null)
			return false;

		setCurrentSelection();
		setSourceInfo();

		System.out.println("------------------------------------------");
		System.out.println("[DBG] Comparing Difference between " + leftContributorName + " and " + rightContributorName + ".");
		System.out.println("[DBG] Left Comparator Relative Path: " + leftRelPath);
		System.out.println("[DBG] Right Comparator Relative Path: " + rightRelPath);
		System.out.println("[DBG] Left Comparator Full Path: " + leftFullPath);
		System.out.println("[DBG] Right Comparator Full Path: " + rightFullPath);
		System.out.println("==========================================");

		System.out.println("Here is the diff output of change distiller");
		System.out.println();
		UTChangeDistillerFile differencer = new UTChangeDistillerFile();
		File f1 = new File(leftFullPath);
		File f2 = new File(rightFullPath);
		differencer.diff(f1, f2);
		return true;
	}

	/**
	 * @param event
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			if (!initiate()) {
				System.out.println("------------------------------------------");
				System.out.println("[USG] OPEN A COMPARE VIEW.");
				System.out.println("==========================================");
				return null;
			}
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void setCurrentSelection() {
		currentSel = UTCriticsStructureSelection.currentSel;
	}

	public ISelection getCurrentSelection() {
		return currentSel;
	}

	public void setSourceInfo() {
		// Get relative path
		final String S = System.getProperty("file.separator");
		String pathStr = "";
		List<String> parents = new ArrayList<String>();
		if (currentSel instanceof TreeSelection) {
			org.eclipse.jface.viewers.TreeSelection treeSel = (org.eclipse.jface.viewers.TreeSelection) currentSel;
			TreePath treePath = treeSel.getPaths()[0];
			int segmentSZ = treePath.getSegmentCount();
			for (int i = 0; i < segmentSZ; i++) {
				Object segment = treePath.getSegment(i);
				if (segment instanceof DiffNode) {
					DiffNode diffNode = (DiffNode) segment;
					if (parents.isEmpty())
						parents = Arrays.asList(diffNode.getParent().getName().split("/"));
					String diffNodeName = diffNode.getName();
					if (diffNode != null && diffNodeName != null)
						pathStr += diffNode.getName();
					if (i != (segmentSZ - 1))
						pathStr += S;
				}

			}

			leftRelPath = parents.get(0).trim() + S + pathStr;
			rightRelPath = parents.get(1).trim() + S + pathStr;

			// Get Full Path and Name
			ICompareInput input = (ICompareInput) getElement(currentSel);

			ITypedElement leftTypedElement = input.getLeft();
			ResourceNode left = (ResourceNode) leftTypedElement;
			IResource leftResource = left.getResource();
			IPath leftpath = leftResource.getLocation();
			leftFullPath = leftpath.toString();
			leftContributorName = leftResource.getName();

			ITypedElement rightTypedElement = input.getRight();
			ResourceNode right = (ResourceNode) rightTypedElement;
			IResource rightResource = right.getResource();
			IPath rightpath = rightResource.getLocation();
			rightFullPath = rightpath.toString();
			rightContributorName = rightResource.getName();
		}
	}

	private static Object getElement(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			if (ss.size() == 1)
				return ss.getFirstElement();
		}
		return null;
	}

	// Tianyi Zhang Oct 24, 2013 6:17:23 PM
	// obsolete methods.
	// Use methods above(setCurrentSelections, setRelativePath, setFullPath, setName()) to get left and right comparator source info
	/*private void getFiles(){
		currentSel = UTCriticsStructureSelection.currentSel;
		ICompareInput input = (ICompareInput) getElement(currentSel);
		
		ITypedElement leftTypedElement = input.getLeft();
		ResourceNode left = (ResourceNode)leftTypedElement;
		IResource leftResource = left.getResource();
		IPath leftpath = leftResource.getLocation();
		leftPath = leftpath.toString();
		leftName = leftResource.getName();
		
		ITypedElement rightTypedElement = input.getRight();
		ResourceNode right = (ResourceNode)rightTypedElement;
		IResource rightResource = right.getResource();
		IPath rightpath = rightResource.getLocation();
		rightPath = rightpath.toString();
		rightName = rightResource.getName();
		
	}
	*/
}
