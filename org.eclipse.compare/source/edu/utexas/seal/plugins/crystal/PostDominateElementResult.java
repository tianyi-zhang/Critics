/*
 * @(#) PostDominateElementResult.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.crystal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import edu.cmu.cs.crystal.cfg.eclipse.EclipseCFG;
import edu.cmu.cs.crystal.cfg.eclipse.EclipseCFGEdge;
import edu.cmu.cs.crystal.cfg.eclipse.EclipseCFGNode;
import edu.cmu.cs.crystal.simple.TupleLatticeElement;
import edu.cmu.cs.crystal.tac.TACFlowAnalysis;
import edu.cmu.cs.crystal.tac.eclipse.CompilationUnitTACs;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.utexas.seal.plugins.crystal.internal.DominateLE;
import edu.utexas.seal.plugins.crystal.internal.SourceCodeRange;
import edu.utexas.seal.plugins.util.UTCriticsTextSelection;

public class PostDominateElementResult {
	private Map<ASTNode, EclipseCFGNode>	astTOcfg;
	private List<SourceCodeRange>			astNodeRanges;
	private List<EclipseCFGNode>			cfgNodes;
	private MethodDeclaration				md;
	private List<Set<SourceCodeRange>>		dominatorsList; // given a node, all of
															// the nodes dominating
															// it
	private List<Set<SourceCodeRange>>		dominateesList; // given a node, all of
															// the nodes dominated
															// by it

	public PostDominateElementResult(MethodDeclaration d, TACFlowAnalysis<TupleLatticeElement<Variable, DominateLE>> analysis, CompilationUnitTACs tac) {
		this.setMd(d);
		analysis.getEndResults(d);
		EclipseCFG cfg = new EclipseCFG(d);
		astTOcfg = cfg.getNodeMap();
		astNodeRanges = new ArrayList<SourceCodeRange>();
		cfgNodes = new ArrayList<EclipseCFGNode>();

		ASTNode astNode = null;
		EclipseCFGNode cfgNode = null;
		for (Entry<ASTNode, EclipseCFGNode> entry : astTOcfg.entrySet()) {
			astNode = entry.getKey();
			cfgNode = entry.getValue();
			astNodeRanges.add(new SourceCodeRange(astNode.getStartPosition(), astNode.getLength()));
			cfgNodes.add(cfgNode);
		}
		dominatorsList = new ArrayList<Set<SourceCodeRange>>();
		d.accept(new ResultASTVisitor(analysis, dominatorsList, astNodeRanges));
		setDominatees();
	}

	/**
	 * For each source code range(ast node) in astNodeRanges, find its dominatees(set of ast nodes).
	 */
	public void setDominatees() {
		dominateesList = new ArrayList<Set<SourceCodeRange>>();
		for (int i = 0; i < astNodeRanges.size(); i++) {
			dominateesList.add(new HashSet<SourceCodeRange>());
		}
		Set<Integer> indexesForNullDominators = new HashSet<Integer>();
		for (int i = 0; i < dominatorsList.size(); i++) {
			Set<SourceCodeRange> dominators = dominatorsList.get(i);
			if (dominators == null) {
				indexesForNullDominators.add(i);
				continue;
			}
			SourceCodeRange dominatee = astNodeRanges.get(i);
			for (SourceCodeRange scr : dominators) {
				dominateesList.get(astNodeRanges.indexOf(scr)).add(dominatee);
			}
		}

		Set<SourceCodeRange> emptyDominatees = Collections.emptySet();
		for (Integer index : indexesForNullDominators) {
			dominateesList.set(index, emptyDominatees);
		}
	}

	public List<SourceCodeRange> getUpStreamControlDependency(ASTNode node) {
		EclipseCFGNode sink = null;
		List<SourceCodeRange> dependentors = new ArrayList<SourceCodeRange>();

		// 1. Look for dominatees List of current node
		int index = -1;
		index = astNodeRanges.indexOf(new SourceCodeRange(node.getStartPosition(), node.getLength()));

		Set<SourceCodeRange> dominatees = null;
		if (index != -1)
			dominatees = dominateesList.get(index);

		// 2. for each post dominated node A, find all its source nodes
		// VisitedCFGNodes is used to identify back edges
		List<EclipseCFGNode> visitedCFGNodes = new ArrayList<EclipseCFGNode>();
		for (SourceCodeRange dominatee : dominatees) {
			// print(dominatee);
			sink = cfgNodes.get(astNodeRanges.indexOf(dominatee));
			travel(sink, dominatees, dependentors, visitedCFGNodes);
			// Clear visited node list for next iteration
			visitedCFGNodes.clear();
		}
		return dependentors;
	}

	public void travel(EclipseCFGNode sink, Set<SourceCodeRange> dominatees, List<SourceCodeRange> dependentors, List<EclipseCFGNode> visitedNodes) {
		assert sink != null;
		if (visitedNodes.contains(sink)) {
			// This node has already been visited for current iteration
			return;
		} else {
			visitedNodes.add(sink);
		}
		Set<EclipseCFGEdge> cfgEdges = sink.getInputs();
		if (cfgEdges == null || cfgEdges.size() == 0) {
			return;
		}
		for (EclipseCFGEdge cfgEdge : cfgEdges) {
			EclipseCFGNode sourceCFG = cfgEdge.getSource();
			ASTNode sourceAST = sourceCFG.getASTNode();
			if (sourceAST == null) {
				// For those who don't have corresponding AST node, need to move on to find its source.
				// System.out.println("Oops!!!It doesn't have source AST node!\n" + sourceCFG.toString());
				travel(sourceCFG, dominatees, dependentors, visitedNodes);
			} else {
				// If source node is not dominatee for sink, then sink is control dependent on source
				if (!dominatees.contains(new SourceCodeRange(sourceAST.getStartPosition(), sourceAST.getLength()))) {
					if (sourceCFG.getName() != "{}") {
						// System.out.println("Yes!!!It is control dependent on current node!");
						// print(new SourceCodeRange(sourceAST.getStartPosition(), sourceAST.getLength()));
						SourceCodeRange range = new SourceCodeRange(sourceAST.getStartPosition(), sourceAST.getLength());
						if (!dependentors.contains(range)) {
							dependentors.add(range);
						}
					}
				} else {
					travel(sourceCFG, dominatees, dependentors, visitedNodes);
				}
			}
		}
	}

	public void print(SourceCodeRange range) {
		IDocument doc = UTCriticsTextSelection.rightMergeSourceViewer.getSourceViewer().getDocument();
		try {
			System.out.println(doc.get(range.startPosition, range.length));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public MethodDeclaration getMd() {
		return md;
	}

	public void setMd(MethodDeclaration md) {
		this.md = md;
	}
}

class DFSEngine {
	private Set<EclipseCFGNode>	visited;
	private EclipseCFG			cfg;
	private Set<EclipseCFGEdge>	backEdges;

	public DFSEngine(EclipseCFG cfg) {
		this.cfg = cfg;
		visited = new HashSet<EclipseCFGNode>();
		backEdges = new HashSet<EclipseCFGEdge>();
	}

	public void identifyLoop() {
		visit((EclipseCFGNode) cfg.getStartNode());
	}

	public boolean visit(EclipseCFGNode node) {
		if (!visited.contains(node)) {
			// unvisited node
			visited.add(node);
			System.out.println(node);
			Set<EclipseCFGEdge> outputs = node.getOutputs();
			for (EclipseCFGEdge edge : outputs) {
				System.out.println(edge);
				EclipseCFGNode sink = edge.getSink();
				if (visit(sink)) {
					// this edge is the back edge
					if (!backEdges.contains(edge)) {
						backEdges.add(edge);
					}
				} else {
					// this edge is a forward edge
				}
			}
			return true;
		} else {
			// visited node, the input edge is a back edge
			return false;
		}
	}

	public Set<EclipseCFGEdge> getBackEdges() {
		return this.backEdges;
	}
}

class ResultASTVisitor extends ASTVisitor {
	TACFlowAnalysis<TupleLatticeElement<Variable, DominateLE>>	analysis;
	List<Set<SourceCodeRange>>									dominatorsList;
	TupleLatticeElement<Variable, DominateLE>					temp			= null;
	List<SourceCodeRange>										astNodeRanges	= null;

	ResultASTVisitor(TACFlowAnalysis<TupleLatticeElement<Variable, DominateLE>> analysis, List<Set<SourceCodeRange>> dominatorsList,// pass by reference
			List<SourceCodeRange> astNodeRanges) {
		this.analysis = analysis;
		this.astNodeRanges = astNodeRanges;
		this.dominatorsList = dominatorsList;
		for (int i = 0; i < astNodeRanges.size(); i++) {
			dominatorsList.add(null);// to take up the space for further setting
										// operations
		}
	}

	@Override
	public void preVisit(ASTNode node) {
		if (node instanceof Javadoc)
			return;
		try {
			temp = analysis.getResultsBefore(node);
		} catch (Exception e) {
			System.out.println(e);
			// just ignore
		}
		SourceCodeRange scr = null;
		scr = new SourceCodeRange(node.getStartPosition(), node.getLength());
		int index = astNodeRanges.indexOf(scr);
		if (index != -1) {
			if (temp != null && temp.getElements() != null) {
				Set<SourceCodeRange> dominators = new HashSet<SourceCodeRange>();
				// There is a single element, so the for loop should be executed
				// only once
				for (DominateLE le : temp.getElements().values()) {
					dominators.addAll(le.ranges);
				}
				dominatorsList.set(index, dominators);
			} else {
				// still null;
			}
		}
	}
}
