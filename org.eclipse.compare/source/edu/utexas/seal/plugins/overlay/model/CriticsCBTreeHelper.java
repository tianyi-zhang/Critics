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
package edu.utexas.seal.plugins.overlay.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeSelection;

import edu.utexas.seal.plugins.util.UTCriticsEditor;
import edu.utexas.seal.plugins.util.UTCriticsPairFileInfo;

/**
 * @author Myoungkyu Song
 * @date Dec 26, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsCBTreeHelper {
	private CheckboxTreeViewer		viewer;
	private ITreeContentProvider	contentProvider;

	/**
	 * @param viewer
	 * @param contentProvider
	 */
	private CriticsCBTreeHelper(CheckboxTreeViewer viewer, ITreeContentProvider contentProvider) {
		this.viewer = viewer;
		if (contentProvider == null) {
			throw new IllegalArgumentException("Content provider is required");
		}
		this.contentProvider = contentProvider;
		init();
	}

	private void init() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				ISelection selElem = event.getSelection();
				if (selElem instanceof TreeSelection) {
					TreeSelection selTreeNode = (TreeSelection) selElem;
					Object selObj = selTreeNode.getFirstElement();
					if (selObj instanceof CriticsCBTreeNode) {
						CriticsCBTreeNode cbtNode = (CriticsCBTreeNode) selObj;
						File f1117 = cbtNode.getFile();
						String f1117Name = f1117.getAbsolutePath();
						String prjNameRight = UTCriticsPairFileInfo.getRightProjectName();
						String prjNameLeft = UTCriticsPairFileInfo.getLeftProjectName();
						String f1114Name = f1117Name.replace(prjNameRight, prjNameLeft);
						File f1114 = new File(f1114Name);
						// UTCriticsEditor.openEditor(f1117);
						UTCriticsEditor.openComparisonEditor(f1114, f1117);
					}
				}
			}
		});

		viewer.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(final CheckStateChangedEvent event) {
				Object node = event.getElement();
				if (viewer.getGrayed(node)) {
					viewer.setGrayChecked(node, false);
				}
				List<Object> checkedElements = getCheckedElements();
				updateTree(node, checkedElements, event.getChecked());
			}
		});
		if (viewer.getContentProvider() != contentProvider) {
			viewer.setContentProvider(contentProvider);
		}
	}

	/**
	 * @param viewer
	 * @param contentProvider
	 * @return the checkbox tree selection helper
	 */
	public static CriticsCBTreeHelper attach(CheckboxTreeViewer viewer, ITreeContentProvider contentProvider) {
		return new CriticsCBTreeHelper(viewer, contentProvider);
	}

	/**
	 * @param node
	 * @param checkedElements
	 * @param checked
	 */
	private void updateTree(Object node, List<Object> checkedElements, boolean checked) {
		List<Object> descendants = getDescendants(node);
		Set<Object> checkedSet = new HashSet<Object>(checkedElements);
		for (Object n : descendants) {
			viewer.setGrayChecked(n, false);
			viewer.setChecked(n, checked);
			if (checked)
				checkedSet.add(n);
			else
				checkedSet.remove(n);
		}
		updateAncestors(node, checkedSet);
	}

	/**
	 * Update ancestors.
	 * 
	 * @param child
	 * @param checkedElements
	 */
	private void updateAncestors(Object child, Set<Object> checkedElements) {
		Object parent = contentProvider.getParent(child);
		if (parent == null)
			return;

		boolean isGreyed = viewer.getChecked(child) && viewer.getGrayed(child);
		if (isGreyed) {
			viewer.setGrayChecked(parent, true);
		} else {
			Object[] children = contentProvider.getChildren(parent);
			List<Object> cloned = new ArrayList<Object>();
			cloned.addAll(Arrays.asList(children));
			cloned.removeAll(checkedElements);
			if (cloned.isEmpty()) {
				// every child is checked
				viewer.setGrayed(parent, false);
				viewer.setChecked(parent, true);
				checkedElements.add(parent);
			} else {
				if (viewer.getChecked(parent) && !viewer.getGrayed(parent)) {
					checkedElements.remove(parent);
				}
				viewer.setGrayChecked(parent, false);

				// some children selected but not all
				if (cloned.size() < children.length) {
					viewer.setGrayChecked(parent, true);
				}
			}
		}
		updateAncestors(parent, checkedElements);
	}

	/**
	 * Gets the descendants.
	 * 
	 * @param node
	 * @return the descendants
	 */
	private List<Object> getDescendants(Object node) {
		List<Object> desc = new ArrayList<Object>();
		getDescendantsHelper(desc, node);
		return desc;
	}

	/**
	 * @param descendants
	 * @param node
	 * @return the descendants helper
	 */
	private void getDescendantsHelper(List<Object> descendants, Object node) {
		Object[] children = contentProvider.getChildren(node);
		if (children == null || children.length == 0)
			return;
		descendants.addAll(Arrays.asList(children));
		for (Object child : children) {
			getDescendantsHelper(descendants, child);
		}
	}

	/**
	 * @return the checked elements
	 */
	public List<Object> getCheckedElements() {
		List<Object> checkedElements = new ArrayList<Object>(Arrays.asList(viewer.getCheckedElements()));
		checkedElements.removeAll(getGrayedElements());
		return checkedElements;
	}

	/**
	 * @return the grayed elements
	 */
	public List<Object> getGrayedElements() {
		return Arrays.asList(viewer.getGrayedElements());
	}
}
