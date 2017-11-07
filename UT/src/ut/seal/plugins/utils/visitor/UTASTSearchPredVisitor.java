package ut.seal.plugins.utils.visitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

public class UTASTSearchPredVisitor extends ASTVisitor{			
	int count;
	String currentVar;
	boolean isAssignment;
	String visitingMethod;
	boolean isCheckRet;
	boolean isCheckPreCondition;
	boolean isMethodInvocation;
	List<org.eclipse.jdt.core.dom.Expression> args;
	org.eclipse.jdt.core.dom.Expression ifConditionExpression;
	List<String> callStack;
	
	
	public List<String> getCallStack() {
		return callStack;
	}

	public void setCallStack(List<String> callStack) {
		this.callStack = callStack;
	}

	public UTASTSearchPredVisitor(String methodName) {		
		this.visitingMethod = visitingMethod;
		callStack = new ArrayList<>();
	}

	public boolean visit(){
		return true;
	}
	
	public boolean visit(FieldAccess node){		
		callStack.add("FieldAccess:"+node.getName().toString());
		return true;
	}
	
	public boolean visit(SimpleName node){		
		if(UTASTSearchTypeVisitor.variableTypes.containsKey(node.getIdentifier().toString())){
			if(isMethodInvocation){
				callStack.add("MethodParam:"+node.getIdentifier());
			}else{
				callStack.add("VariableAccess:"+node.getIdentifier());
			}						
		}		
		return true;
	}
	public boolean visit(VariableDeclarationStatement node){
		callStack.add("VariableDeclaration");
		return true;
	}
	
	public void endVisit(VariableDeclarationStatement node){
		callStack.add("EndVariableDeclaration");		
	}
	
	public boolean visit(IfStatement node){				
		ifConditionExpression = node.getExpression();
		if(ifConditionExpression.getNodeType()== 27  || ifConditionExpression.getNodeType() == 36 || ifConditionExpression.getNodeType() == 37){
			InfixExpression exp = (InfixExpression) ifConditionExpression;
			exp.getLeftOperand();
		}
		if(isMethodInvocation){
			//get list of method params and variable in the expressiona nd check if they are the same
			//look at the return value expression and compare it against the ifconditionexpression
			
		} else{
			//check the inside method invocation
//			for(int  i=0;i<args.size();i++){
//				if(ifConditionExpression.toString().contains(args.get(i).toString())){
//					
//				}
//			}
		}
		callStack.add("if");		
		return true;
	}
	
	public void endVisit(IfStatement node){
		callStack.add("Endif");
	}
	
	public boolean visit(MethodInvocation node){
		isMethodInvocation = true;
		count=0;		 
		callStack.add("MethodCall:"+node.getName().getFullyQualifiedName());
		return true;
	}	
	
	public void endVisit(MethodInvocation node){
		isMethodInvocation = false;
		callStack.add("EndMethodCall:"+node.getName().getFullyQualifiedName());
		count=0;
	}
	public boolean visit(org.eclipse.jdt.core.dom.Expression node) {
		count = count+1;
		return true;
	}
	
	public boolean visit(ReturnStatement node){		
		return true;
	}

	public boolean visit(org.eclipse.jdt.core.dom.Assignment node){
		isAssignment = true;	
		callStack.add("Assignment");		
		return true;
	}
	
	public void endVisit(org.eclipse.jdt.core.dom.Assignment node){
		isAssignment = false;				
		callStack.add("EndAssignment");
	}
	
	public boolean visit(WhileStatement node){
		
		return true;
	}
	
	public boolean visit(ForStatement node){						
		
		return true;
	}
}


