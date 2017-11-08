package ut.seal.plugins.utils.visitor;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

public class UTASTFactGeneratorVisitor extends ASTVisitor{

	
	public StringBuilder builder = new StringBuilder();
	String currentMethod = null;
	
	public boolean visit(){
		return true;
	}
	
	public boolean visit(FieldAccess node){		
		return true;
	}
	
	public boolean visit(SimpleName node){		
		return true;
	}
	
	public void endVisit(VariableDeclarationStatement node){
//		callStack.add("EndVariableDeclaration");		
	}
	
	public boolean visit(IfStatement node){				
		builder.append("contains_if(");
		builder.append(this.currentMethod);
		builder.append(").");
		builder.append("\n");
		return true;
	}	
	
	public boolean visit(MethodInvocation node){
		builder.append("method_call(");
		builder.append(this.currentMethod);
		builder.append(",");
		
		builder.append(node.getName().getFullyQualifiedName());	
		builder.append(").");
		builder.append("\n");
		return true;
	}	
		
	public boolean visit(Assignment node){
		//maybe useful later to add what type of assignment
					
		return true;
	}
		
	public boolean visit(WhileStatement node){
		builder.append("iterator(");
		builder.append(this.currentMethod);
		builder.append(").");
		builder.append("\n");
		return true;
	}
	
	public boolean visit(ForStatement node){						
		builder.append("iterator(");
		builder.append(this.currentMethod);
		builder.append(").");
		builder.append("\n");
		return true;
	}
	
	
	public boolean visit(TryStatement node){
		builder.append("trystatement(");
		builder.append(this.currentMethod);
		builder.append(").");
		builder.append("\n");
		return true;
	}
		
	public boolean visit(CatchClause node){
		builder.append("catch(");
		builder.append(this.currentMethod);
		builder.append(",");
		builder.append(node.getException().getType().toString());
		builder.append(").");
		builder.append("\n");		
		return true;
	}
	
	public boolean visit(MethodDeclaration node){
		this.currentMethod = node.getName().getFullyQualifiedName();
		return true;
	}
	
	public void endVisit(MethodDeclaration node){
		this.currentMethod = null;
	}
}
