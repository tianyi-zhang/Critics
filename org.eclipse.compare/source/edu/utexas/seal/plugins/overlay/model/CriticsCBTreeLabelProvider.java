/*
 * @(#) CriticsLabelProvider.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.overlay.model;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import edu.utexas.seal.plugins.util.UTPlugin;

/**
 * @author Myoungkyu Song
 * @date Dec 27, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsCBTreeLabelProvider extends DecoratingLabelProvider {

	/**
	 * @param provider
	 * @param decorator
	 */
	public CriticsCBTreeLabelProvider(ILabelProvider provider, ILabelDecorator decorator) {
		super(provider, decorator);
	}

	public Image getImage(Object element) {
		if (element instanceof CriticsCBTreeNode) {
			CriticsCBTreeNode node = (CriticsCBTreeNode) element;
			Image image = null;
			if (node.type() == CriticsCBTreeElemType.FILE) {
				image = UTPlugin.getImage(UTPlugin.IMG_JFILE);
			} else if (node.type() == CriticsCBTreeElemType.METHOD) {
				image = UTPlugin.getImage(UTPlugin.IMG_METHOD);
			} else if (node.type() == CriticsCBTreeElemType.BLOCK) {
				if (node.getNode().isAnomaly()) {
					image = UTPlugin.getImage(UTPlugin.IMG_DEL);
				} else {
					image = UTPlugin.getImage(UTPlugin.IMG_BLOCK);
				}
			}
			return image;
		}
		return null;
	}

	@Override
	public Color getForeground(Object element) {
		if (element instanceof CriticsCBTreeNode) {
			CriticsCBTreeNode cbtNode = (CriticsCBTreeNode) element;
			Node node = cbtNode.getNode();
			if (cbtNode.type() == CriticsCBTreeElemType.FILE) {
				;
			} else if (cbtNode.type() == CriticsCBTreeElemType.METHOD) {
				;
			} else if (cbtNode.type() == CriticsCBTreeElemType.BLOCK) {
				if (node.isAnomaly()) {
					return ColorConstants.red;
				}
			}
		}
		return super.getForeground(element);
	}

	@Override
	public Color getBackground(Object element) {
		if (element instanceof CriticsCBTreeNode) {
			CriticsCBTreeNode cbtNode = (CriticsCBTreeNode) element;
			Node node = cbtNode.getNode();
			if (cbtNode.type() == CriticsCBTreeElemType.FILE) {
				;
			} else if (cbtNode.type() == CriticsCBTreeElemType.METHOD) {
				;
			} else if (cbtNode.type() == CriticsCBTreeElemType.BLOCK) {
				if (node.isAnomaly()) {
					return ColorConstants.yellow;
				}
			}
		}
		return super.getBackground(element);
	}
}
