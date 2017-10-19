//    Copyright (C) 2012  Mateusz Pawlik and Nikolaus Augsten
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Affero General Public License as
//    published by the Free Software Foundation, either version 3 of the
//    License, or (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Affero General Public License for more details.
//
//    You should have received a copy of the GNU Affero General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package rted.processor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import rted.datastructure.LblTree;
import rted.datastructure.LblValueNode;
import rted.distance.RTEDInfoTreeOpt;
import ut.seal.plugins.utils.UTFile;
import ut.seal.plugins.utils.UTLog;

/**
 * 
 * @author Mateusz Pawlik
 * @modified Myoungkyu Song
 */
public class RTEDProcessor implements RTEDMessage {
	private LblTree				lt1, lt2;
	private int					size1, size2;
	public boolean				run, custom, array, strategy, ifSwitch, sota, verbose, demaine;
	private boolean				mapping	= true;
	private int					sotaStrategy;
	private String				customStrategy, customStrategyArrayFile;
	private RTEDInfoTreeOpt		rted;
	private double				ted;
	public long					timeCompute	= 0, timeInput = 0, timeTot = 0;
	private boolean				__d__		= true;
	private LinkedList<int[]>	editMapping;

	public LblTree getQueryLblTree() {
		return lt1;
	}

	public LblTree getSubLblTree() {
		return lt2;
	}

	public LblTree getLeftLblTree() {
		return lt1;
	}

	public LblTree getRightLblTree() {
		return lt2;
	}

	/**
	 * 
	 * @return
	 */
	public LinkedList<int[]> getEditMapping() {
		return editMapping;
	}

	/**
	 * 
	 * @param tree1
	 * @param tree2
	 * @param prnt
	 */
	public void initiate(String tree1, String tree2, boolean prnt) {
		long t1 = (new Date()).getTime();
		rted = new RTEDInfoTreeOpt(1, 1, 1);
		parseTreesFromString(tree1, tree2, prnt);
		timeInput += ((new Date()).getTime() - t1);
	}

	/**
	 * 
	 */
	public void match() {
		long t1 = (new Date()).getTime();
		rted.computeOptimalStrategy();
		ted = rted.nonNormalizedTreeDist();
		timeCompute += ((new Date()).getTime() - t1);
	}

	public Double getDistance() {
		return this.ted;
	}

	/**
	 * 
	 * @param m1 new revision
	 * @param m2 old revision
	 * @param prnt
	 */
	public void mapping(Map<Integer, Node> m1, Map<Integer, Node> m2, boolean prnt) {
		if (!this.mapping) {
			return;
		}
		editMapping = rted.computeEditMapping();
		if (!prnt) {
			return;
		}
		LblValueNode oldNode = new LblValueNode();
		LblValueNode newNode = new LblValueNode();
		for (int[] nodeAlignment : editMapping) {
			int oldNodeId = nodeAlignment[0];
			int newNodeId = nodeAlignment[1];
			oldNode.setNode(m1.get(oldNodeId));
			newNode.setNode(m2.get(newNodeId));
			UTLog.println(true, oldNodeId + "->" + newNodeId + "\t" + oldNode + " -> " + newNode);
		}
		UTLog.println(false, "DBG__________________________________________");
	}

	public void mappingDebugging(Map<Integer, Node> query, Map<Integer, Node> target, boolean prnt) {
		if (this.mapping) {
			LblValueNode tQNode = new LblValueNode();
			LblValueNode tTNode = new LblValueNode();
			editMapping = rted.computeEditMapping();
			for (int[] nodeAlignment : editMapping) {
				int qID = nodeAlignment[0];
				int tID = nodeAlignment[1];
				Node qNode = query.get(qID);
				Node tNode = target.get(tID);
				tQNode.setNode(qNode);
				tTNode.setNode(tNode);
				UTLog.println(prnt, qID + "->" + tID + "\t" + tQNode + " -> " + tTNode);
			}
			UTLog.println(prnt, "DBG__________________________________________");
		}
	}

	public int mappingSize() {
		int count = 0;
		for (int[] nodeAlignment : editMapping) {
			int qID = nodeAlignment[0];
			int tID = nodeAlignment[1];
			if (qID != 0 && tID != 0) {
				count++;
			}
		}
		return count;
	}

	/**
	 * 
	 */
	public void printTED() {
		if (verbose) {
			System.out.println("[DBG] distance:             " + ted);
			System.out.println("[DBG] relevant subproblems: " + rted.counter);
			System.out.println("[DBG] recurence steps:      " + rted.strStat[3]);
			System.out.println("[DBG] left paths:           " + rted.strStat[0]);
			System.out.println("[DBG] right paths:          " + rted.strStat[1]);
			System.out.println("[DBG] heavy paths:          " + rted.strStat[2]);
		}
		System.out.println("[DBG] Tree edit distance:         " + this.ted);
	}

	/**
	 * 
	 * @param msg
	 * @param millis
	 */
	public void printTimeStamp(String msg, long millis) {
		System.out.println("\t" + msg + String.format("%d min, %d sec", //
				TimeUnit.MILLISECONDS.toMinutes(millis), //
				TimeUnit.MILLISECONDS.toSeconds(millis) - //
						TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))));
	}

	/**
	 * Run the command line with given arguments.
	 * 
	 * @param args
	 */
	public void runCommandLine(String[] args) {
		long extime1 = (new Date()).getTime();
		rted = new RTEDInfoTreeOpt(1, 1, 1);
		try {
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("--help") || args[i].equals("-h")) {
					System.out.println(helpMessage);
					System.exit(0);
				} else if (args[i].equals("-t") || args[i].equals("--trees")) {
					parseTreesFromString(args[i + 1], args[i + 2], true);
					i = i + 2;
					run = true;
				} else if (args[i].equals("-f") || args[i].equals("--files")) {
					parseTreesFromFiles(args[i + 1], args[i + 2]);
					i = i + 2;
					run = true;
				} else if (args[i].equals("-l") || args[i].equals("--ZhangShashaLeft")) {
					sota = true;
					sotaStrategy = 0;
					strategy = true;
				} else if (args[i].equals("-r") || args[i].equals("--ZhangShashaRight")) {
					sota = true;
					sotaStrategy = 1;
					strategy = true;
				} else if (args[i].equals("-k") || args[i].equals("--Klein")) {
					sota = true;
					sotaStrategy = 2;
					strategy = true;
				} else if (args[i].equals("-d") || args[i].equals("--Demaine")) {
					sota = true;
					demaine = true;
					sotaStrategy = 2;
					strategy = true;
				} else if (args[i].equals("-o") || args[i].equals("--RTED")) {
					// do nothing - this is the default option
				} else if (args[i].equals("-s") || args[i].equals("--strategy")) {
					custom = true;
					customStrategy = args[i + 1];
					i = i + 1;
					strategy = true;
				} else if (args[i].equals("-a") || args[i].equals("--strategy-array")) {
					array = true;
					customStrategyArrayFile = args[i + 1];
					i = i + 1;
					strategy = true;
				} else if (args[i].equals("-w") || args[i].equals("--switch")) {
					ifSwitch = true;
				} else if (args[i].equals("-c") || args[i].equals("--costs")) {
					setCosts(args[i + 1], args[i + 2], args[i + 3]);
					i = i + 3;
				} else if (args[i].equals("-v") || args[i].equals("--verbose")) {
					verbose = true;
				} else if (args[i].equals("-m") || args[i].equals("--mapping")) {
					mapping = true;
				} else {
					System.out.println(wrongArgumentsMessage);
					System.exit(0);
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Too few arguments.");
			System.exit(0);
		}

		if (!run) {
			System.out.println(wrongArgumentsMessage);
			System.exit(0);
		}
		long extime2 = (new Date()).getTime();
		timeInput += (extime2 - extime1);

		long time1 = (new Date()).getTime();
		if (strategy) {
			if (sota) {
				if (demaine) {
					setStrategy(sotaStrategy, true);
				} else {
					setStrategy(sotaStrategy, false);
				}
				ted = rted.nonNormalizedTreeDist();
			} else if (custom) {
				setStrategy(customStrategy, ifSwitch);
				ted = rted.nonNormalizedTreeDist();
			} else if (array) {
				setStrategy(customStrategyArrayFile);
				ted = rted.nonNormalizedTreeDist();
			}
		} else {
			rted.computeOptimalStrategy();
			ted = rted.nonNormalizedTreeDist();
		}
		long time2 = (new Date()).getTime();
		timeCompute += (time2 - time1);

		if (verbose) {
			System.out.println("distance:             " + ted);
			System.out.println("relevant subproblems: " + rted.counter);
			System.out.println("recurence steps:      " + rted.strStat[3]);
			System.out.println("left paths:           " + rted.strStat[0]);
			System.out.println("right paths:          " + rted.strStat[1]);
			System.out.println("heavy paths:          " + rted.strStat[2]);
		}
		// else {
		// System.out.println(ted);
		// }
	}

	/**
	 * Parse two input trees from the command line.
	 * 
	 * @param ts1
	 * @param ts2
	 * @param prnt
	 */
	private void parseTreesFromString(String ts1, String ts2, boolean prnt) {
		try {
			lt1 = LblTree.fromString(ts1);
			if (prnt) {
				// UTLog.init(UTCfg.getInst().readConfig().LOG4JPATH + "log.txt");
				System.out.println("DBG__________________________________________");
				System.out.println("[DBG] QUERY TREE:");
				System.out.println("DBG__________________________________________");
				lt1.prettyPrint();
				// String buf = lt1.getPrintBuf();
				// UTLog.log(buf);
				System.out.println("DBG__________________________________________");
			}
			size1 = lt1.getNodeCount();
		} catch (Exception e) {
			System.out.println("TREE1 argument has wrong format");
			System.exit(0);
		}
		try {
			lt2 = LblTree.fromString(ts2);
			if (prnt) {
				System.out.println("[DBG] TARGET TREE: ");
				System.out.println("DBG__________________________________________");
				lt2.prettyPrint();
				// UTLog.log(lt2.getPrintBuf());
				System.out.println("DBG__________________________________________");
			}
			size2 = lt2.getNodeCount();
		} catch (Exception e) {
			System.out.println("TREE2 argument has wrong format");
			System.exit(0);
		}
		rted.init(lt1, lt2);
	}

	/**
	 * Parse two input trees from given files.
	 * 
	 * @param fs1
	 * @param fs2
	 */
	private void parseTreesFromFiles(String fs1, String fs2) {
		try {
			// mksong Nov 8, 2013 10:03:28 PM
			// lt1 = LblTree.fromString((new BufferedReader(new FileReader(fs1))).readLine());
			System.out.println("DBG__________________________________________");
			System.out.println(UTFile.readFileWithoutSpace(fs1));
			System.out.println("DBG__________________________________________");
			lt1 = LblTree.fromString(UTFile.readFileWithoutSpace(fs1));
			if (__d__) {
				System.out.println("DBG__________________________________________");
				System.out.println("[DBG] TREE 1");
				System.out.println("DBG__________________________________________");
				lt1.prettyPrint();
				System.out.println("DBG__________________________________________");
			}
			size1 = lt1.getNodeCount();
		} catch (Exception e) {
			System.out.println("TREE1 argument has wrong format");
			System.exit(0);
		}
		try {
			// mksong Nov 8, 2013 10:05:40 PM
			// lt2 = LblTree.fromString((new BufferedReader(new FileReader(fs2))).readLine());
			lt2 = LblTree.fromString(UTFile.readFileWithoutSpace(fs2));
			if (__d__) {
				System.out.println("[DBG] TREE 2");
				System.out.println("DBG__________________________________________");
				lt2.prettyPrint();
				System.out.println("DBG__________________________________________");
			}
			size2 = lt2.getNodeCount();
		} catch (Exception e) {
			System.out.println("TREE2 argument has wrong format");
			System.exit(0);
		}
		rted.init(lt1, lt2);
	}

	/**
	 * Set custom costs.
	 * 
	 * @param cds
	 * @param cis
	 * @param cms
	 */
	private void setCosts(String cds, String cis, String cms) {
		try {
			rted.setCustomCosts(Double.parseDouble(cds), Double.parseDouble(cis), Double.parseDouble(cms));
		} catch (Exception e) {
			System.out.println("One of the costs has wrong format.");
			System.exit(0);
		}
	}

	/**
	 * Set the strategy to be entirely of the type given by str.
	 * 
	 * @param str
	 *            strategy type
	 * @param ifSwitch
	 *            if set to true the strategy will be applied to the currently bigger tree
	 */
	private void setStrategy(String str, boolean ifSwitch) {
		if (str.equals("left")) {
			rted.setCustomStrategy(0, ifSwitch);
		} else if (str.equals("right")) {
			rted.setCustomStrategy(1, ifSwitch);
		} else if (str.equals("heavy")) {
			rted.setCustomStrategy(2, ifSwitch);
		} else {
			System.out.println("Wrong strategy.");
			System.exit(0);
		}
	}

	/**
	 * Set the strategy to be entirely of the type given by str.
	 * 
	 * @param str
	 *            strategy type
	 * @param ifSwitch
	 *            if set to true the strategy will be applied to the currently bigger tree
	 */
	private void setStrategy(int str, boolean ifSwitch) {
		try {
			rted.setCustomStrategy(str, ifSwitch);
		} catch (Exception e) {
			System.out.println("Strategy has wrong format.");
			System.exit(0);
		}
	}

	/**
	 * Set the strategy to the one given in strArrayFile.
	 * 
	 * @param strArrayFile
	 *            path to the file with the strategy
	 */
	private void setStrategy(String strArrayFile) {
		try {
			rted.setCustomStrategy(parseStrategyArrayString(strArrayFile));
		} catch (Exception e) {
			System.out.println("Strategy has wrong format.");
			System.exit(0);
		}
	}

	/**
	 * Parse the strategy array.
	 * 
	 * Array String format:
	 * ? ? ? ?
	 * ? ? ? ?
	 * ? ? ? ?
	 * 
	 * @param strategyArray
	 * @return
	 */
	private int[][] parseStrategyArrayString(String fileWithStrategyArray) {
		int[][] str = null;
		Vector<int[]> strVector = new Vector<int[]>();
		int[] strLine;
		String line;
		Scanner s = null;
		int value;
		BufferedReader br;

		try {
			br = new BufferedReader(new FileReader(fileWithStrategyArray));
			line = br.readLine();
			int index = 0;
			while (line != null) {
				s = new Scanner(line);
				strLine = new int[(line.length() + 1) / 2];
				if (strLine.length != size2) {
					System.err.println("Trees sizes differ from the strategy array dimensions.");
					System.exit(0);
				}
				int i = 0;
				while (s.hasNextInt()) {
					value = s.nextInt();
					if (value != 0 && value != 1 && value != 2 && value != 4 && value != 5 && value != 6) {
						System.out.println("Strategy value at position " + index + " in the strategy array file is wrong.");
						System.exit(0);
					}
					index++;
					strLine[i] = value;
					i++;
				}
				strVector.add(strLine);
				line = br.readLine();
			}
			str = new int[strVector.size()][];
			int i = 0;
			for (int[] l : strVector) {
				str[i] = l;
				i++;
			}
			if (str.length != size1) {
				System.err.println("Trees sizes differ from the strategy array dimensions.");
				System.exit(0);
			}
			if (s != null)
				s.close();
			br.close();
		} catch (Exception e) {
			System.out.println("Something is wrong with strategy array file.");
			System.exit(0);
		}

		return str;
	}
	/**
	 * Main method
	 * 
	 * @param args
	 */
	// public static void main(String[] args2) {
	// RTEDProcessor rtedProc = new RTEDProcessor();
	//
	// String queryOfFile = "input/mypkg01/A.java";
	// String queryOfMethod = "foo";
	// MethodTreeManager queryTreeMngr = new MethodTreeManager();
	// String queryTree = queryTreeMngr.converter(queryOfFile, queryOfMethod);
	//
	// String targetOfFile[] = { "input/mypkg01/B.java" };
	// String targetOfMethod[] = {
	// // "copyListWithFilter"
	// // ,
	// // "removeListWithFilter" //
	// "foo" //
	// };
	// MethodTreeManager targetTreeMngr;
	// rtedProc.timeTot = (new Date()).getTime();
	// for (int i = 0; i < targetOfFile.length; i++) {
	// for (int j = 0; j < targetOfMethod.length; j++) {
	// String elem = targetOfMethod[j];
	// targetTreeMngr = new MethodTreeManager();
	// rtedProc.initiate(queryTree, targetTreeMngr.converter(targetOfFile[i], elem));
	// rtedProc.match();
	// rtedProc.mapping(queryTreeMngr.getPostorderIDManager(), targetTreeMngr.getPostorderIDManager());
	// }
	// }
	// rtedProc.timeTot = (new Date()).getTime() - rtedProc.timeTot;
	// rtedProc.printTimeStamp("runtime (total):            ", rtedProc.timeTot);
	// rtedProc.printTimeStamp("runtime (in):               ", rtedProc.timeInput);
	// rtedProc.printTimeStamp("runtime (compute):          ", rtedProc.timeCompute);
	// rtedProc.printTED();
	// System.out.println("DBG__________________________________________");
	// }
}
