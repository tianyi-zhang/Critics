/*
 * @(#) ITreeMatch.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package rted.datastructure.manager;

import java.io.File;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;

import rted.distance.RTEDInfoSubTree;
import rted.processor.RTEDProcessor;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

/**
 * @author Myoungkyu Song
 * @date Dec 5, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public interface ITreeMatch {
	public void matchEditMappingEntry(Node t1, Node t2, boolean p);

	public void matchEditMappingEntry(Integer id[], JavaCompilation c, Node t1, Node t2, File f, boolean p);

	public void matchEditMapping(Integer id[], JavaCompilation c, Node t1, Node t2, File path);

	public void matchEditMapping(Node t1, Node t2);

	public RTEDProcessor getRTEDProc();

	public List<RTEDInfoSubTree> getSubTrees();

	public void setMatchCounter(Integer[] cntMatchedOldRev);

	public void setCompilationUnit(CompilationUnit parser);

	public void setFilePath(File filePath);
}
