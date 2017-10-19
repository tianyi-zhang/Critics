/*
 * @(#) UTSubTreeCluster.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rted.distance.RTEDInfoSubTree;
import ut.seal.plugins.utils.UTCfg;
import ut.seal.plugins.utils.UTFile;
import ut.seal.plugins.utils.UTLog;
import ut.seal.plugins.utils.UTStr;
import ut.seal.weka.cluster.UTClusterByKMean;
import ut.seal.weka.cluster.UTClusterByXMean;
import ut.seal.weka.cluster.UTClusterTreeEditsWithWeka;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

/**
 * @author Myoungkyu Song
 * @date Dec 5, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTSubTreeCluster {
	private Double						numNode;
	private UTClusterTreeEditsWithWeka	kMeans	= new UTClusterByKMean();
	private UTClusterTreeEditsWithWeka	xMeans	= new UTClusterByXMean();

	/**
	 * Group by clusterer.
	 * 
	 * @param subTrees the sub trees
	 */
	public void groupByClusterer(List<RTEDInfoSubTree> subTrees) {
		List<String> dataExtractedWithPostorderId = new ArrayList<String>();
		List<String> dataExtracted = new ArrayList<String>();
		dataExtracted.add("DATA");
		for (int i = 0; i < subTrees.size(); i++) {
			RTEDInfoSubTree subTreeAstInfo = subTrees.get(i);
			int postorderId = subTreeAstInfo.getPostorderID();
			Double simScore = subTreeAstInfo.getSimilarityScore();
			dataExtractedWithPostorderId.add(String.valueOf(postorderId) + "," + String.valueOf(simScore));
			dataExtracted.add(String.valueOf(simScore));
		}
		String csvFile = UTCfg.getInst().readConfig().CSVFILENAME;
		String arffFile = UTCfg.getInst().readConfig().ARFFFILENAME;
		UTFile.writeFile(csvFile, dataExtracted);
		UTLog.println(true, "[DBG0] XMeans:");
		clusterByXMeans(csvFile, arffFile, subTrees);
		UTLog.println(true, "DBG__________________________________________");
		UTLog.println(true, "[DBG0] KMeans:");
		clusterByKMeans(csvFile, arffFile, subTrees);
		UTLog.println(true, "DBG__________________________________________");
		boolean isPrintable = true;
		printClusteredGroup(xMeans.getClusteredGroupMngr(), subTrees, false);
		printClusteredGroup(kMeans.getClusteredGroupMngr(), subTrees, isPrintable);
	}

	/**
	 * Cluster by x means.
	 * 
	 * @param csvFile the csv file
	 * @param arffFile the arff file
	 * @param subTrees the sub trees
	 */
	void clusterByXMeans(String csvFile, String arffFile, //
			List<RTEDInfoSubTree> subTrees) {
		xMeans.convertCSV(csvFile, arffFile);
		int szSubTrees = subTrees.size();
		double x = Math.ceil(Math.sqrt(szSubTrees / 2));
		int maxCluster = (int) x;
		if (maxCluster == 0) {
			maxCluster = 1;
		}
		try {
			xMeans.clusterDataByXMean(arffFile, maxCluster);
			System.out.println("[DBG] MaxNumClusters: " + xMeans.getXMeans().getMaxNumClusters());
			System.out.println("[DBG] NumberOfClusters: " + xMeans.getXMeans().numberOfClusters());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Cluster by k means.
	 * 
	 * @param csvFile the csv file
	 * @param arffFile the arff file
	 * @param subTrees the sub trees
	 */
	void clusterByKMeans(String csvFile, String arffFile, //
			List<RTEDInfoSubTree> subTrees) {
		kMeans.convertCSV(csvFile, arffFile);
		if (subTrees.size() < 2)
			return;

		double x = Math.ceil(Math.sqrt(subTrees.size() / 2)); // http://en.wikipedia.org/wiki/Determining_the_number_of_clusters_in_a_data_set#cite_note-1
		int numCluster = (int) x;
		try {
			kMeans.clusterDataByKMean(arffFile, numCluster);
			System.out.println("[DBG] NumberOfClusters: " + kMeans.getKMeans().numberOfClusters());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the first group.
	 * 
	 * @param sortedData the sorted data
	 * @return the first group
	 * @throws Exception the exception
	 */
	public List<RTEDInfoSubTree> getFirstGroup(List<RTEDInfoSubTree> sortedData) throws Exception {
		List<RTEDInfoSubTree> result = new ArrayList<RTEDInfoSubTree>();

		UTClusterTreeEditsWithWeka c = null;
		UTClusterTreeEditsWithWeka clustererKMeans = getInstOfKMeans();
		UTClusterTreeEditsWithWeka clustererXMeans = getInstOfXMeans();
		int k = clustererKMeans.getKMeans().numberOfClusters();
		int x = clustererXMeans.getXMeans().numberOfClusters();
		if (k <= x) {
			c = clustererXMeans;
		} else {
			c = clustererKMeans;
		}
		Map<Integer, List<Object>> clusteredGroups = c.getClusteredGroupMngr();
		trace1(clusteredGroups, sortedData, false);

		int sizeFirstGroup = clusteredGroups.entrySet().iterator().next().getValue().size();
		for (int i = 0; i < sizeFirstGroup; i++) {
			result.add(sortedData.get(i));
		}
		return result;
	}

	/**
	 * Gets the groups.
	 * 
	 * @param aSortedData the a sorted data
	 * @param size the size
	 * @return the groups
	 * @throws Exception the exception
	 */
	public List<RTEDInfoSubTree> getGroups(List<RTEDInfoSubTree> aSortedData, int start, int size) throws Exception {
		/*
		List<RTEDInfoSubTree> result = new ArrayList<RTEDInfoSubTree>();
		UTClusterTreeEditsWithWeka c = null;
		UTClusterTreeEditsWithWeka clustererKMeans = getInstOfKMeans();
		UTClusterTreeEditsWithWeka clustererXMeans = getInstOfXMeans();
		int k = clustererKMeans.getKMeans().numberOfClusters();
		int x = clustererXMeans.getXMeans().numberOfClusters();
		if (k <= x) {
			c = clustererXMeans;
		} else {
			c = clustererKMeans;
		}
		int cntGroup = 0, cntElem = 0;
		Map<Integer, List<Object>> clusteredGroups = c.getClusteredGroupMngr();
		for (Map.Entry<Integer, List<Object>> e : clusteredGroups.entrySet()) {
			cntGroup += 1;
			List<Object> iResult = e.getValue();
			if (cntGroup < start) {
				cntElem += iResult.size();
				continue;
			}
			if (cntGroup >= start + size) {
				break;
			}
			for (int i = 0; i < iResult.size(); i++, cntElem++) {
				RTEDInfoSubTree iSubtree = aSortedData.get(cntElem);
				iSubtree.setGroupIDBySimilarity(cntGroup);
				result.add(aSortedData.get(cntElem));
			}
		} 
		return result;
		 */
		return null;
	}

	/**
	 * Gets the groups.
	 * 
	 * @param aSortedData the a sorted data
	 * @param size the size
	 * @return the groups
	 * @throws Exception the exception
	 */
	public List<RTEDInfoSubTree> getGroups(List<RTEDInfoSubTree> aSortedData, int size) throws Exception {
		/*
		List<RTEDInfoSubTree> result = new ArrayList<RTEDInfoSubTree>();
		UTClusterTreeEditsWithWeka c = null;
		UTClusterTreeEditsWithWeka clustererKMeans = getInstOfKMeans();
		UTClusterTreeEditsWithWeka clustererXMeans = getInstOfXMeans();
		int k = clustererKMeans.getKMeans().numberOfClusters();
		int x = clustererXMeans.getXMeans().numberOfClusters();
		if (k <= x) {
			c = clustererXMeans;
		} else {
			c = clustererKMeans;
		}
		int cntGroup = 1, cntElem = 0;
		Map<Integer, List<Object>> clusteredGroups = c.getClusteredGroupMngr();
		for (Map.Entry<Integer, List<Object>> e : clusteredGroups.entrySet()) {
			if (cntGroup > size) {
				break;
			}
			List<Object> iResult = e.getValue();
			for (int i = 0; i < iResult.size(); i++, cntElem++) {
				RTEDInfoSubTree iSubtree = aSortedData.get(cntElem);
				iSubtree.setGroupIDBySimilarity(cntGroup);
				result.add(aSortedData.get(cntElem));
			}
			cntGroup++;
		}
		return result; 
		*/
		return null;
	}

	/**
	 * Prints the clustered group.
	 * 
	 * @param clusteredGroups the clustered groups
	 * @param subTrees the sub trees
	 * @param isPrintable the is printable
	 */
	void printClusteredGroup(Map<Integer, List<Object>> clusteredGroups, //
			List<RTEDInfoSubTree> subTrees, boolean isPrintable) {
		if (!isPrintable) {
			return;
		}
		int i = 0, j = 0;
		for (Map.Entry<Integer, List<Object>> e : clusteredGroups.entrySet()) {
			int id = e.getKey();
			List<Object> group = e.getValue();
			UTLog.println(isPrintable, "CID: " + id);
			for (j = 0; j < group.size(); i++, j++) {
				RTEDInfoSubTree subTree = subTrees.get(i);
				int postorderId = subTree.getPostorderID();
				Node node = subTree.getSubTree();
				String label = node.getLabel().name().trim();
				String value = node.getValue().trim();
				Object similarityScore = group.get(j);
				UTLog.println(isPrintable, "  TID:" + UTStr.getIndentR(postorderId + ", ", 6) + //
						"  SIM: " + UTStr.getIndentR(similarityScore + ", ", 10) + //
						"  " + UTStr.getIndentR(node.getEntity().getSourceRange() + ", ", 10) + //
						"(" + label + ")(" + value + ")");
			}
			int cnt = UTCfg.getInst().readConfig().CLUSTER_LIMIT_SIZE;
			if (id >= cnt) {
				break;
			}
		}
		UTLog.println(isPrintable, "DBG__________________________________________");
	}

	public Double getNumNode() {
		return numNode;
	}

	public void setNumNode(Double numNode) {
		this.numNode = numNode;
	}

	public UTClusterTreeEditsWithWeka getInstOfXMeans() {
		return xMeans;
	}

	public UTClusterTreeEditsWithWeka getInstOfKMeans() {
		return kMeans;
	}

	// ********************************************************
	// ********************************************************
	// ********************************************************

	/**
	 * Trace1.
	 * 
	 * @param clusteredGroups the clustered groups
	 * @param subtrees the subtrees
	 * @param isPrintable the is printable
	 */
	void trace1(Map<Integer, List<Object>> clusteredGroups, List<RTEDInfoSubTree> subtrees, boolean isPrintable) {
		t.trace1(clusteredGroups, subtrees, isPrintable);
	}

	private Trace	t	= new Trace();

	class Trace {

		/**
		 * Trace1.
		 * 
		 * @param clusteredGroups the clustered groups
		 * @param subtrees the subtrees
		 * @param isPrintable the is printable
		 */
		public void trace1(Map<Integer, List<Object>> clusteredGroups, List<RTEDInfoSubTree> subtrees, boolean isPrintable) {
			if (!isPrintable) {
				return;
			}
			int indexSubTree = 0;
			for (Map.Entry<Integer, List<Object>> e : clusteredGroups.entrySet()) {
				UTLog.println(true, e.getKey());
				List<Object> group = e.getValue();
				for (int i = 0; i < group.size(); i++, indexSubTree++) {
					RTEDInfoSubTree subTree = subtrees.get(indexSubTree);
					UTLog.println(true, "  " + subTree.toString());
					// int postorderId = subTree.getPostorderID();
					// Node node = subTree.getSubTree();
					// String label = node.getLabel().name().trim();
					// String value = node.getValue().trim();
					// Object similarityScore = group.get(i);
				}
			}
		}
	}
}
