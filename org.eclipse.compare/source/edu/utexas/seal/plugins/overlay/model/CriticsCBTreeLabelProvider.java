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
