package ch.uzh.ifi.seal.changedistiller.distilling;

/*
 * #%L
 * ChangeDistiller
 * %%
 * Copyright (C) 2011 - 2013 Software Architecture and Evolution Lab, Department of Informatics, UZH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.Test;

import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;
import ch.uzh.ifi.seal.changedistiller.distilling.Distiller;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.SourceRange;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.Move;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.model.entities.StructureEntityVersion;
import ch.uzh.ifi.seal.changedistiller.model.entities.Update;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ch.uzh.ifi.seal.changedistiller.util.CompilationUtils;

// simple test cases. exhaustive test cases with classification check in separate tests suite
public class WhenMethodBodyChangesAreExtracted extends WhenChangesAreExtracted {

	private final static String	TEST_DATA	= "src_change/";

	// @Test
	public void unchangedMethodBodyShouldNotHaveAnyChanges() throws Exception {
		JavaCompilation compilation = CompilationUtils.compileFile(TEST_DATA + "TestLeft.java");
		Node rootLeft = convertMethodBody("foo", compilation);
		Node rootRight = convertMethodBody("foo", compilation);
		StructureEntityVersion structureEntity = new StructureEntityVersion(JavaEntityType.METHOD, "foo", 0);
		Distiller distiller = getDistiller(structureEntity);
		distiller.extractClassifiedSourceCodeChanges(rootLeft, rootRight);
		assertThat(structureEntity.getSourceCodeChanges().isEmpty(), is(true));
	}

	// @Test
	public void changedMethodBodyShouldHaveChanges() throws Exception {
		JavaCompilation compilationLeft = CompilationUtils.compileFile(TEST_DATA + "TestLeft.java");
		JavaCompilation compilationRight = CompilationUtils.compileFile(TEST_DATA + "TestRight.java");
		Node rootLeft = convertMethodBody("foo", compilationLeft);
		Node rootRight = convertMethodBody("foo", compilationRight);
		StructureEntityVersion structureEntity = new StructureEntityVersion(JavaEntityType.METHOD, "foo", 0);
		Distiller distiller = getDistiller(structureEntity);
		distiller.extractClassifiedSourceCodeChanges(rootLeft, rootRight);
		assertThat(structureEntity.getSourceCodeChanges().size(), is(11));
	}

	// *********************************************************************
	// *********************************************************************
	// *********************************************************************
	@Test
	public void changedMethodBodyShouldHaveChanges3() throws Exception {
		String p = "/Users/mksong/workspaceCritics/UT-ChangeDistiller/";
		JavaCompilation compilationNew = CompilationUtils.compileFile(p, "A7New.java");
		JavaCompilation compilationOld = CompilationUtils.compileFile(p, "A7Old.java");
		Node rootNew = convertMethodBody("checkSecurity", compilationNew);
		Node rootOld = convertMethodBody("checkSecurity", compilationOld);

		System.out.println("==========================================");
		rootOld.print();
		System.out.println("------------------------------------------");
		rootNew.print();
		System.out.println("==========================================");

		for (int i = 0; i < 3; i++) {
			diff(rootOld, rootNew);
			System.out.println("[DBG] " + i);
		}
		System.out.println("==========================================");
		rootOld.print();
		System.out.println("------------------------------------------");
		rootNew.print();
		System.out.println("==========================================");
	}

	public void diff(Node aNode1, Node aNode2) {
		StructureEntityVersion structureEntity = new StructureEntityVersion(JavaEntityType.METHOD, "foo", 0);
		Distiller distiller = getDistiller(structureEntity);
		distiller.extractClassifiedSourceCodeChanges(aNode1.copy(), aNode2.copy());
	}

	public void changedMethodBodyShouldHaveChanges2() throws Exception {
		String p = "/Users/mksong/workspaceCritics/UT-ChangeDistiller/";
		JavaCompilation compilationNew = CompilationUtils.compileFile(p, "A7New.java");
		JavaCompilation compilationOld = CompilationUtils.compileFile(p, "A7Old.java");
		Node rootNew = convertMethodBody("checkSecurity", compilationNew);
		Node rootOld = convertMethodBody("checkSecurity", compilationOld);

		// WhenCompareNode cn = new WhenCompareNode();

		StructureEntityVersion structureEntity = new StructureEntityVersion(JavaEntityType.METHOD, "foo", 0);
		Distiller distiller = getDistiller(structureEntity);
		distiller.extractClassifiedSourceCodeChanges(rootOld.copy(), rootNew.copy());

		// cn.testGetChanges(rootOld, rootNew);

		// for (int i = 0; i < 3; i++) {
		// List<Node> restoreNode = cd.diff2(rootOld, rootNew);
		// cd.restore(restoreNode);
		// System.out.println("=1=========================================");
		// }

		// for (int i = 0; i < 3; i++) {
		// Node[] nodes = { rootOld, rootNew };
		// Node nodesCopy[] = cd.diff(nodes);
		// // cd.print(cd.getChanges());
		// rootOld = nodesCopy[0];
		// rootNew = nodesCopy[1];
		// System.out.println("=2=========================================");
		// }
	}

	// private Node rootOld;
	// private Node rootNew;
	//
	// public void testGetChanges(Node o1, Node n1) {
	// rootOld = o1;
	// rootNew = n1;
	//
	// UTChangeDistiller cd = new UTChangeDistiller() {
	// @Override
	// public void diff() {
	// List<Node> restoreNode = diff2(rootOld, rootNew);
	// rootOld = restoreNode.get(0);
	// rootNew = restoreNode.get(1);
	// }
	// };
	//
	// for (int i = 0; i < 3; i++) {
	// cd.diff();
	// cd.print(cd.getChanges());
	// System.out.println("=1=========================================");
	// }
	// }

	public void restore(Node n1, Node n2, List<Node> nodes) {
		n1 = nodes.get(0);
		n2 = nodes.get(1);
	}

	public void restore(Node[] nodes, Node[] nodesCopy) {
		nodes[0] = nodesCopy[0];
		nodes[1] = nodesCopy[1];
	}

	public void getChanges(Node rootOld, Node rootNew) {
		StructureEntityVersion structureEntity = new StructureEntityVersion(JavaEntityType.METHOD, "A", 0);
		Distiller distiller = getDistiller(structureEntity);
		distiller.extractClassifiedSourceCodeChanges(rootOld, rootNew);
		// assertThat(structureEntity.getSourceCodeChanges().size(), is(11));

		List<SourceCodeChange> changes = structureEntity.getSourceCodeChanges();
		if (changes != null) {
			for (int i = 0; i < changes.size(); i++) {
				SourceCodeChange change = changes.get(i);
				SourceCodeEntity newEntity = null, changedEntity = null;

				if (change instanceof Update) {
					Update update = (Update) change;
					newEntity = update.getNewEntity();
				} else if (change instanceof Move) {
					Move move = (Move) change;
					newEntity = move.getNewEntity();
				}
				System.out.println("   * " + change.getChangeType());
				changedEntity = change.getChangedEntity();
				getChangedInfo(changedEntity, change);
				System.out.println();
				getChangedInfo(newEntity, change);
				System.out.println("------------------------------------------");
			}
		}
	}

	public void getChangedInfo(SourceCodeEntity changedEntity, SourceCodeChange change) {
		if (changedEntity == null) {
			return;
		}
		SourceRange changedRange = changedEntity.getSourceRange();
		String changeName = changedEntity.getUniqueName();
		// SourceCodeEntity parent = change.getParentEntity();
		// String parentName = parent.getUniqueName();
		// SourceRange parentChangedRange = parent.getSourceRange();
		System.out.println("[DBG] OFFSET: " + changedRange + ", CHANGE: [" + changeName + "]"//
				/*
				 * + ", PARENT: " + parent.getType() + parent.getSourceRange()
				 */);
	}
}
