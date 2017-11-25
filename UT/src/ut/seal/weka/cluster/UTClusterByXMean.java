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
