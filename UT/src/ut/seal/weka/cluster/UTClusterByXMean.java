/*
 * @(#) XMeanMain.java 
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
import weka.clusterers.XMeans;

/**
 * @author Myoungkyu Song
 * @date Nov 29, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTClusterByXMean extends UTClusterTreeEditsWithWeka {

	public UTClusterByXMean() {
		clusteredGroupMngr = new TreeMap<Integer, List<Object>>();
	}

	@Override
	public Map<Integer, List<Object>> getClusteredGroupMngr() {
		return clusteredGroupMngr;
	}

	/**
	 * 
	 * @param arff
	 * @param maxCluster
	 * @throws Exception
	 */
	@Override
	public void clusterDataByXMean(String arff, int maxCluster) throws Exception {
		source = new DataSource(arff);
		data = source.getDataSet();
		xMeans = new XMeans();
		xMeans.setMaxNumClusters(maxCluster);
		xMeans.setMinNumClusters(2);
		xMeans.buildClusterer(data);
		centers = xMeans.getClusterCenters();
		handleClusteredGroupMngr(getGroupMngr((RandomizableClusterer) xMeans));
		/*		
		for (int i = 0; i < centers.numInstances(); i++) {
			Instance elem = centers.instance(i);
			System.out.println("[DBG] " + (i + 1) + ": " + elem);
		}
		System.out.println("------------------------------------------");
		*/
		/*
		szClusteredGroup = 0;
		List<UTGroup> groupMngr = new ArrayList<UTGroup>();
		int prv = -1, cur = -1;
		for (int i = 0; i < data.numInstances(); i++) {
			Instance elem = data.instance(i);
			cur = xmeans.clusterInstance(elem) + 1;
			if (prv != cur) {
				szClusteredGroup++;
			}
			groupMngr.add(new UTGroup(szClusteredGroup, elem));
			prv = cur;
			// System.out.println("[DBG] " + elem + " is in " + clusterInst); 
		}
		*/
	}
}
