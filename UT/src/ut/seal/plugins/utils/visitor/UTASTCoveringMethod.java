package ut.seal.plugins.utils.visitor;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class UTASTCoveringMethod extends ASTVisitor{

	String methodName;	
	public boolean visit(MethodDeclaration node){
		
		return true;
	}
}
