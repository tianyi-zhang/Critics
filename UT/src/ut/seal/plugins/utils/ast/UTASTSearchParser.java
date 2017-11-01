package ut.seal.plugins.utils.ast;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class UTASTSearchParser {
	
	//as of now we care about statements in a block.
	public CompilationUnit parseBlock(String code){
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_STATEMENTS);
		parser.setSource(code.toCharArray());
		parser.setResolveBindings(true);				
		final Block block =(Block) parser.createAST(null);		
		ASTNode parent = block.getParent();
		while (parent != null && !(parent instanceof CompilationUnit)) {
			parent = parent.getParent();
		}
		final CompilationUnit cu = (CompilationUnit) parent;		
		return cu;
	}
		
}
