package edu.utexas.seal.plugins.analyzer;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;



/**
 * @author troy
 * Will finish at the end of first phase implementation, not now.
 */
public interface IUTASTAnalyzer {
	// method level
	public ASTNode getMethodNode();
	public List<ASTNode> getStatements();

	// class level
	public ASTNode getClassNode();
	public List<ASTNode> getMethodNodes();
	public List<ASTNode> getStatement(ASTNode method);
	
}
