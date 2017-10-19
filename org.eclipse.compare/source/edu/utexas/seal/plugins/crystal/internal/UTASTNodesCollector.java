package edu.utexas.seal.plugins.crystal.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.swt.graphics.Point;

public class UTASTNodesCollector extends ASTVisitor{
	SourceCodeRange src;
	List<ASTNode> nodes;
	
	public UTASTNodesCollector(Point region){
		src = new SourceCodeRange(region.x, region.y);
		nodes = new ArrayList<ASTNode>();
	}
	
	@Override
	public void preVisit(ASTNode node){
		if(withinSelection(node)){
			nodes.add(node);
		}
	}
	
	private boolean withinSelection(ASTNode node) {
		int offset_bgn = -1, offset_end = -1, offset_sel_bgn = -1, offset_sel_end = -1;

		if (src != null) {
			offset_bgn = node.getStartPosition();
			offset_end = offset_bgn + node.getLength();
			offset_sel_bgn = src.startPosition;
			offset_sel_end = src.startPosition + src.length;
			if ((offset_bgn >= offset_sel_bgn)
					&& (offset_end <= offset_sel_end)) {
				return true;
			}
		}
		return false;
	}
	
	public List<ASTNode> getNodes(){
		return nodes;
	}
}
