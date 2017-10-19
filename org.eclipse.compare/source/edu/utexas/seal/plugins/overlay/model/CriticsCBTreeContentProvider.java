/*
 * @(#) CriticsCBTreeContentProvider.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.overlay.model;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Myoungkyu Song
 * @date Dec 26, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsCBTreeContentProvider implements ITreeContentProvider {

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return ((CriticsCBTreeNode) parentElement).getChildren().toArray();
	}

	@Override
	public Object getParent(Object element) {
		return ((CriticsCBTreeNode) element).getParent();
	}

	@Override
	public boolean hasChildren(Object element) {
		return !((CriticsCBTreeNode) element).getChildren().isEmpty();
	}
}
