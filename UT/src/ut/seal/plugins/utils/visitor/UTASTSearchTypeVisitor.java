package ut.seal.plugins.utils.visitor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class UTASTSearchTypeVisitor extends ASTVisitor{
	
	public static Map<String,String> variableTypes = new HashMap<>();
	public static Map<String,String> methodTypes = new HashMap<>();
	String currentVar;
	String currentType;
	//variable declarations
	//function return types
	//current class type
	//
	//contextual type in If,While,etc	
	public boolean visit(SingleVariableDeclaration node) {		
		System.out.println("SingleVariableDeclaration Type: "+node.getType());
		System.out.println("Variable Name: "+node.getName().toString());	
		variableTypes.put(node.getName().toString(), node.getType().toString());
		return true;
	} 
	
	public boolean visit(FieldDeclaration node){
		currentType = node.getType().toString();
		return true;
	}
	
	public void endVisit(FieldDeclaration node){
		currentType = null;
	}
	
	public boolean visit(VariableDeclarationExpression node){		
		currentType = node.getType().toString();
		return true;
	}
	
	public void endVisit(VariableDeclarationExpression node){
		currentType = null;
	}
	
	public boolean visit(VariableDeclarationFragment node){
		currentVar = node.getName().toString();
		if(currentType!=null && currentType.length()>1){
			variableTypes.put(currentVar, currentType);
		}				
		System.out.println("VariableDeclarationFragment Type: "+currentType);
		System.out.println("Variable Name: "+currentVar);
		return true;
	}
	
	public boolean visit(VariableDeclarationStatement node){
		currentType = node.getType().toString();		
		return true;
	}
	public void endVisit(VariableDeclarationStatement node){
		currentType = null;			
	}
	
	
	public boolean visit(MethodDeclaration node){
		currentVar = node.getName().toString();
		return true;
	}
}
