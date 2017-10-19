/*
 * @(#) CriticsListViewerLabelProvider.java
 *
 * Copyright 2013 2014 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.overlay.model;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;

import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import edu.utexas.seal.plugins.util.UTPlugin;

/**
 * @author Myoungkyu Song
 * @date Feb 10, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsTableViewerLabelProvider extends DecoratingLabelProvider {

	public CriticsTableViewerLabelProvider(ILabelProvider provider, ILabelDecorator decorator) {
		super(provider, decorator);
	}

	public Image getImage(Object element) {
		Image image = null;
		if (element instanceof CriticsCBTreeNode) {
			CriticsCBTreeNode node = (CriticsCBTreeNode) element;
			if (node.getNode().isAnomaly()) {
				image = UTPlugin.getImage(UTPlugin.IMG_DEL);
			} else {
				image = UTPlugin.getImage(UTPlugin.IMG_METHOD);
			}
		}
		return image;
	}

	public String getText(Object element) {
		if (element instanceof CriticsCBTreeNode) {
			CriticsCBTreeNode tNode = (CriticsCBTreeNode) element;
			Node node = tNode.getNode();
			String value = node.getValue();
			String className = node.getClassName();
			String packageName = node.getPackageName();
			System.out.println("[" + packageName + "]" + "[" + className + "]" + "[" + value + "]");
			return "[" + packageName + "]" + "[" + className + "]" + "[" + value + "]";
		}
		throw new RuntimeException();
	}
}
