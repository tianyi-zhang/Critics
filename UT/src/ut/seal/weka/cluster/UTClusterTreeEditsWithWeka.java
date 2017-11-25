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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import weka.clusterers.RandomizableClusterer;
import weka.clusterers.SimpleKMeans;
import weka.clusterers.XMeans;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * @author Myoungkyu Song
 * @date Nov 29, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class UTClusterTreeEditsWithWeka {
	protected Map<Integer, List<Object>>	clusteredGroupMngr	= null;
	protected DataSource					source				= null;
	protected Instances						data				= null;
	protected XMeans						xMeans				= null;
	protected SimpleKMeans					kMeans				= null;
	protected Instances						centers				= null;
	protected int							szClusteredGroup	= 0;

	public DataSource getSource() {
		return source;
	}

	public Instances getData() {
		return data;
	}

	public XMeans getXMeans() {
		return xMeans;
	}

	public SimpleKMeans getKMeans() {
		return kMeans;
	}

	public Instances getCenters() {
		return centers;
	}

	public int getSzClusteredGroup() {
		return szClusteredGroup;
	}

	public Map<Integer, List<Object>> getClusteredGroupMngr() {
		return null;
	}

	public void clusterDataByKMean(String arff, int numCluster) throws Exception {
	}

	public void clusterDataByXMean(String arff, int maxCluster) throws Exception {
	}

	/**
	 * 
	 * @param inputPath
	 * @param inputFile
	 * @param outputFile
	 * @return
	 */
	public String convertCSV(String inputFile, String outputFile) {
		CSVLoader loader = new CSVLoader();
		ArffSaver saver = new ArffSaver();
		try {
			loader.setSource(new File(inputFile));
			Instances data = loader.getDataSet();

			saver.setInstances(data);
			saver.setFile(new File(outputFile));
			saver.setDestination(new File(outputFile));
			saver.writeBatch();
		} catch (IOException e) {
			// ignore silently
			return null;
		}
		return outputFile;
	}

	protected List<UTGroup> getGroupMngr( //
			RandomizableClusterer randomizableClusterer) throws Exception {
		szClusteredGroup = 0;
		List<UTGroup> groupMngr = new ArrayList<UTGroup>();
		int prv = -1, cur = -1;
		for (int i = 0; i < data.numInstances(); i++) {
			Instance elem = data.instance(i);

			if (randomizableClusterer instanceof SimpleKMeans) {
				kMeans = (SimpleKMeans) randomizableClusterer;
				cur = kMeans.clusterInstance(elem) + 1;
			} else if (randomizableClusterer instanceof XMeans) {
				xMeans = (XMeans) randomizableClusterer;
				cur = xMeans.clusterInstance(elem) + 1;
			}

			if (prv != cur) {
				szClusteredGroup++;
			}
			groupMngr.add(new UTGroup(szClusteredGroup, elem));
			prv = cur;
		}
		return groupMngr;
	}

	protected void handleClusteredGroupMngr(List<UTGroup> groupMngr) {
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
	}

	public static void main(String[] args) {
		new UTClusterTreeEditsWithWeka().basic_test();
	}

	private void basic_test() {
		String path = "../UT.INPUT/";
		String inputFile = path + "data1.csv";
		String outputFile = path + "data1.arff";
		UTClusterByKMean kmean = new UTClusterByKMean();
		UTClusterByXMean xmean = new UTClusterByXMean();
		String output = convertCSV(inputFile, outputFile);
		if (output != null) {
			try {
				System.out.println("==========================================");
				System.out.println("[DBG] DATA CONVERTED..");
				System.out.println("------------------------------------------");
				kmean.clusterDataByKMean(outputFile, 3);
				System.out.println("------------------------------------------");
				xmean.clusterDataByXMean(outputFile, 100);
				System.out.println("==========================================");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
