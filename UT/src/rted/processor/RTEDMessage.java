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
/*
 * @(#) RTEDCommandLineMsg.java
 *
 */
package rted.processor;

/**
 * @modified Myoungkyu Song
 * @date Nov 8, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public interface RTEDMessage {
	String	helpMessage				= "\n"
											+ "Compute the edit distance between two trees.\n"
											+ "\n"
											+ "SYNTAX\n"
											+ "\n"
											+ "  Simple Syntax -- use default algorithm (RTED):\n"
											+ "\n"
											+ "    java -jar RTED.jar {-t TREE1 TREE2 | -f FILE1 FILE2} [-c CD CI CR] [-v] [-m]\n"
											+ "\n" + "    java -jar RTED.jar -h\n" + "\n"
											+ "  Advanced Syntax -- use other algorithms or user-defined strategies:\n" + "\n"
											+ "    java -jar RTED.jar {-t TREE1 TREE2 | -f FILE1 FILE2} -s {left|right|heavy}\n"
											+ "        [-sw] [-c CD CI CR] [-v] [-m]\n"
											+ "    java -jar RTED.jar {-t TREE1 TREE2 | -f FILE1 FILE2} -a FILE\n"
											+ "        [-c CD CI CR] [-v] [-m]\n"
											+ "    java -jar RTED.jar {-t TREE1 TREE2 | -f FILE1 FILE2} \n"
											+ "        [-l | -r | -k | -d | -o] [-c CD CI CR] [-v] [-m]\n" + "\n"
											+ "DESCRIPTION\n" + "\n"
											+ "    Compute the edit distance between two trees. If not otherwise\n"
											+ "    specified, the RTED algorithm by Pawlik and Augsten [1] is\n"
											+ "    used. This algorithm uses the optimal path strategy.\n" + "\n"
											+ "    Optionally, the tree edit distance can be computed using the\n"
											+ "    strategies by Zhang and Shasha [2], Klein [3], Demaine et al. [4],\n"
											+ "    or a combination of thereof. The trees are either specified on the\n"
											+ "    command line (-t) or read from files (-f). The default output only\n"
											+ "    prints the tree edit distance, the verbose output (-v) adds\n"
											+ "    additional information such as runtime and strategy statistics.\n" + "\n"
											+ "    In additon to the tree edit distance, the minimal edit mapping between\n"
											+ "    two trees can be computed (-m). There might be multiple minimal edit\n"
											+ "    mappings. This option computes only one of them.\n" + "\n" + "OPTIONS\n"
											+ "\n" + "    -h, --help \n" + "        print this help message.\n" + "\n"
											+ "    -t TREE1 TREE2,\n" + "    --trees TREE1 TREE2\n"
											+ "        compute the tree edit distance between TREE1 and TREE2. The\n"
											+ "        trees are encoded in the bracket notation, for example, in tree\n"
											+ "        {A{B{X}{Y}{F}}{C}} the root node has label A and two children\n"
											+ "        with labels B and C. B has three children with labels X, Y, F.\n" + "\n"
											+ "    -f FILE1 FILE2, \n" + "    --files FILE1 FILE2\n"
											+ "        compute the tree edit distance between the two trees stored in\n"
											+ "        the files FILE1 and FILE2. The trees are encoded in bracket\n"
											+ "        notation.\n" + "\n" + "    -c CD CI CR, \n" + "    --costs CD CI CR\n"
											+ "        set custom cost for edit operations. Default is -c 1 1 1.\n"
											+ "        CD - cost of node deletion\n" + "        CI - cost of node insertion\n"
											+ "        CR - cost of node renaming\n" + "\n" + "    -s {left|right|heavy}, \n"
											+ "    --strategy {left|right|heavy}\n"
											+ "        set custom strategy that uses exclusively left, right, or\n"
											+ "        heavy paths.\n" + "\n" + "    -w, --switch\n"
											+ "        force to switch trees if the left-hand tree is smaller than\n"
											+ "        the right-hand tree.\n" + "\n" + "    -a FILE\n"
											+ "    --strategy-array FILE\n"
											+ "        read the strategy from FILE. Rows in the file represent\n"
											+ "        subtrees of the left-hand tree in postorder, columns represent\n"
											+ "        subtrees of the right-hand tree in postorder. Strategies are\n"
											+ "        separated with space.  Use digits 0, 1, 2 for left, right, and\n"
											+ "        heavy path in the left-hand tree and 4, 5, 6 for left, right,\n"
											+ "        and heavy path in the right-hand tree. Example strategy for\n"
											+ "        two trees with three nodes each:\n" + "            0 1 2\n"
											+ "            4 5 6\n" + "            2 6 0\n" + "\n" + "    -v, --verbose\n"
											+ "        print verbose output, including tree edit distance, runtime,\n"
											+ "        number of relevant subproblems and strategy statistics.\n" + "\n"
											+ "    -l, --ZhangShashaLeft\n"
											+ "        like \"-s left\". Use the algorithm by Zhang and Shasha [2] with\n"
											+ "        left paths.\n" + "\n" + "    -r, --ZhangShashaRight\n"
											+ "        like \"-s right\". Use the algorithm by Zhang and Shasha [2] with\n"
											+ "        right paths.\n" + "\n" + "    -k, --Klein\n"
											+ "        like \"-s heavy\". Use the algorithm by Klein [3], which uses\n"
											+ "        heavy paths.\n" + "\n" + "    -d, --Demaine\n"
											+ "        like \"-s heavy -w\". Use the algorithm by Demaine [4], which\n"
											+ "        uses heavy paths and always decomposes the larger tree.\n" + "\n"
											+ "    -o, --RTED\n"
											+ "        use the RTED algorithm by Pawlik and Augsten [1]. This is the\n"
											+ "        default strategy.\n" + "\n" + "    -m, --mapping\n"
											+ "        compute the minimal edit mapping between two trees. There might\n"
											+ "        be multiple minimal edit mappings. This option computes only one\n"
											+ "        of them. The frst line of the output is the cost of the mapping.\n"
											+ "        The following lines represent the edit operations. n and m are\n"
											+ "        postorder IDs (beginning with 1) of nodes in the left-hand and\n"
											+ "        the rigt-hand trees respectively.\n"
											+ "            n->m - rename node n to m\n" + "            n->0 - delete node n\n"
											+ "            0->m - insert node m\n" + "\n" + "EXAMPLES\n" + "\n"
											+ "    java -jar RTED_v0.1.jar -t {a{b}{c}} {a{b{d}}} -c 1 1 0.5 -s heavy --switch\n"
											+ "    java -jar RTED_v0.1.jar -f 1.tree 2.tree -s left\n"
											+ "    java -jar RTED_v0.1.jar -t {a{b}{c}} {a{b{d}}} --ZhangShashaLeft -v\n" + "\n"
											+ "REFERENCES\n" + "\n"
											+ "    [1] M. Pawlik and N. Augsten. RTED: a robust algorithm for the\n"
											+ "        tree edit distance. Proceedings of the VLDB Endowment\n"
											+ "        (PVLDB). 2012 (To appear).\n"
											+ "    [2] K.Zhang and D.Shasha. Simple fast algorithms for the editing\n"
											+ "        distance between trees and related problems. SIAM\n"
											+ "        J. Computing. 1989.\n"
											+ "    [3] P.N. Klein. Computing the edit-distance between unrooted\n"
											+ "        ordered trees.  European Symposium on Algorithms (ESA). 1998.\n"
											+ "    [4] E.D. Demaine, S. Mozes, B. Rossman and O. Weimann. An optimal\n"
											+ "        decomposition algorithm for tree edit distance. ACM Trans. on\n"
											+ "        Algorithms. 2009.\n" + "\n" + "AUTHORS\n" + "\n"
											+ "    Mateusz Pawlik, Nikolaus Augsten";

	String	wrongArgumentsMessage	= "Wrong arguments. Try \"java -jar RTED.jar --help\" for help.";
}
