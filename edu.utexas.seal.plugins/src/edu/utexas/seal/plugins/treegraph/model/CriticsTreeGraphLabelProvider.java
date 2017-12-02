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
package edu.utexas.seal.plugins.treegraph.model;

import java.util.Enumeration;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.eclipse.zest.core.viewers.IEntityConnectionStyleProvider;
import org.eclipse.zest.core.viewers.IEntityStyleProvider;
import org.eclipse.zest.core.widgets.ZestStyles;

import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import edu.utexas.seal.plugins.util.root.UTPlugin;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.NodeType;

/**
 * @author Myoungkyu Song
 * @date Dec 18, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsTreeGraphLabelProvider extends LabelProvider implements IEntityStyleProvider, IEntityConnectionStyleProvider {
	private final Color	backgroundColor;

	public CriticsTreeGraphLabelProvider(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof Node) {
			Node node = (Node) element;
			String label = node.getLabel().name().trim();
			return label;
		}
		if (element instanceof EntityConnectionData) {
			@SuppressWarnings("unused")
			EntityConnectionData test = (EntityConnectionData) element;
			return "";
		}
		throw new RuntimeException("[RuntimeException] CriticsLabelProvider - Wrong type: " + element.getClass().toString());
	}

	@Override
	public Color getNodeHighlightColor(Object entity) {
		return getBGColor(entity);
	}

	@Override
	public Color getBorderColor(Object entity) {
		return null;
	}

	@Override
	public Color getBorderHighlightColor(Object entity) {
		return ColorConstants.red;
	}

	@Override
	public int getBorderWidth(Object entity) {
		return 0;
	}

	@Override
	public Color getBackgroundColour(Object entity) {
		return getBGColor(entity);
	}

	Color getBGColor(Object entity) {
		Color color = null;
		if (entity instanceof Node) {
			Node node = (Node) entity;
			switch (node.getNodeType()) {
			case User_Selection:
				color = backgroundColor;
				break;
			case Data_Dependency:
				color = ColorConstants.orange;
				break;
			case Control_Dependency:
				color = ColorConstants.yellow;
				break;
			case Containment_Dependency:
				color = ColorConstants.lightGreen;
				break;
			default:
				color = backgroundColor;
				break;
			}
		}
		return color;
	}

	@Override
	public Color getForegroundColour(Object entity) {
		if (entity instanceof Node) {
			Node node = (Node) entity;
			if (node.isDelete()) {
				return ColorConstants.red;
			} else if (node.isInsert()) {
				return ColorConstants.blue;
			}

			if (node.getLabel().name().equals("THEN_STATEMENT")) {
				Enumeration<?> eChildren = node.children();
				int cntDel = 0;
				while (eChildren.hasMoreElements()) {
					Node iNode = (Node) eChildren.nextElement();
					if (iNode.isDelete())
						cntDel++;
				}
				if (cntDel == node.getChildCount()) {
					node.setDelete(true);
					return ColorConstants.red;
				}

				eChildren = node.children();
				int cntIns = 0;
				while (eChildren.hasMoreElements()) {
					Node iNode = (Node) eChildren.nextElement();
					if (iNode.isInsert())
						cntIns++;
				}
				if (cntIns == node.getChildCount()) {
					node.setInsert(true);
					return ColorConstants.blue;
				}
			}
		}
		return ColorConstants.black;
	}

	@Override
	public IFigure getTooltip(Object entity) {
		if (entity instanceof Node) {
			Node node = (Node) entity;
			return new CriticsTreeGraphNoteFigure(node.getValue());
		}
		throw new RuntimeException("[RuntimeException] CriticsLabelProvider - Wrong type: " + entity.getClass().toString());
	}

	@Override
	public boolean fisheyeNode(Object entity) {
		return false;
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof Node) {
			return UTPlugin.getImage(UTPlugin.IMG_ADD);
		}
		return null;
	}

	@Override
	public Color getColor(Object src, Object dest) {
		if (src instanceof Node && dest instanceof Node) {
			Node srcNode = (Node) src;
			Node destNode = (Node) dest;
			if (srcNode.getNodeType() == NodeType.User_Selection && destNode.getNodeType() == NodeType.Data_Dependency) {
				return ColorConstants.red;
			}
		}
		return ColorConstants.black;
	}

	@Override
	public int getConnectionStyle(Object src, Object dest) {
		if (src instanceof Node && dest instanceof Node) {
			Node srcNode = (Node) src;
			Node destNode = (Node) dest;
			if (srcNode.getNodeType() != NodeType.Containment_Dependency && destNode.getNodeType() == NodeType.Data_Dependency) {
				return ZestStyles.CONNECTIONS_DASH | ZestStyles.CONNECTIONS_DIRECTED;
			}
		}
		return ZestStyles.CONNECTIONS_DIRECTED;
	}

	@Override
	public Color getHighlightColor(Object arg0, Object arg1) {
		return null;
	}

	@Override
	public int getLineWidth(Object arg0, Object arg1) {
		return 0;
	}
}
