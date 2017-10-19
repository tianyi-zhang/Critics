/*
 * @(#) UTClusterByKMean.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package ut.seal.weka.cluster;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import weka.core.converters.ConverterUtils.DataSource;
import weka.clusterers.RandomizableClusterer;
import weka.clusterers.SimpleKMeans;

/**
 * @author Myoungkyu Song
 * @date Nov 29, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTClusterByKMean extends UTClusterTreeEditsWithWeka {

	public UTClusterByKMean() {
		clusteredGroupMngr = new TreeMap<Integer, List<Object>>();
	}

	@Override
	public Map<Integer, List<Object>> getClusteredGroupMngr() {
		return clusteredGroupMngr;
	}

	/**
	 * RandomizableClusterer
	 * 
	 * @param arff
	 * @param numCluster
	 * @throws Exception
	 */
	@Override
	public void clusterDataByKMean(String arff, int numCluster) throws Exception {
		source = new DataSource(arff);
		data = source.getDataSet();
		kMeans = new SimpleKMeans();
		kMeans.setNumClusters(numCluster);
		kMeans.buildClusterer(data);
		handleClusteredGroupMngr(getGroupMngr((RandomizableClusterer) kMeans));
		/*
		List<UTGroup> groupMngr = getGroupMngr((RandomizableClusterer) kMeans);
		for (int i = 0; i < groupMngr.size(); i++) {
			UTGroup group = groupMngr.get(i);
			int key = group.getId();
			Object val = group.getElem();
			List<Object> clusteredGroup = clusteredGroupMngr.get(key);
			if (clusteredGroup == null) {
				clusteredGroup = new ArrayList<Object>();
				clusteredGroup.add(val);
				clusteredGroupMngr.put(key, clusteredGroup);
			} else {
				clusteredGroup.add(val);
			}
		}
		 */
		// Instances centroids = kMeans.getClusterCentroids();
		// for (int i = 0; i < centroids.numInstances(); i++) {
		// System.out.println("[DBG] " + (i + 1) + ": " + centroids.instance(i));
		// }
		// System.out.println("------------------------------------------");
		// for (int i = 0; i < data.numInstances(); i++) {
		// Instance elem = data.instance(i);
		// int inst = kMeans.clusterInstance(elem) + 1;
		// System.out.println("[DBG] " + elem + " is in " + inst);
		// }
	}
}
