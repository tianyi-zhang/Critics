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
import org.eclipse.jdt.core.dom.WhileStatement;

public class UTASTSearchPredVisitor extends ASTVisitor{		
	StringBuilder predicate;
	List<Predicate> predicates = new ArrayList<>();
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
	public boolean visit(IfStatement node){		
		Conditional cond = new Conditional();
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
		predicates.add(cond);
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
		return true;
	}
	
	public void endVisit(org.eclipse.jdt.core.dom.Assignment node){
		isAssignment = false;
		Assignment assignment = new Assignment();		
		assignment.rightExpression = ASTNode.nodeClassForType(node.getRightHandSide().getNodeType()).getSimpleName();
		
		predicates.add(assignment);
	
	}
	
	public boolean visit(WhileStatement node){
		Iteration iter = new Iteration();
		iter.conditionType = ASTNode.nodeClassForType(node.getExpression().getNodeType()).getSimpleName();	
		predicates.add(iter);
		return true;
	}
	
	public boolean visit(ForStatement node){		
		Iteration iter = new Iteration();
		iter.conditionType = ASTNode.nodeClassForType(node.getExpression().getNodeType()).getSimpleName();		
		predicates.add(iter);
		return true;
	}
	
	public String getPredicates(){			
		for(int i=0;i<predicates.size();i++){			
			predicates.get(i).print();			
		}
		return Predicate.predicate.toString();
		
	}
	public void clearPredicates(){
		Predicate.predicate = new StringBuilder();
	}
}


class Predicate{
	static StringBuilder predicate = new StringBuilder();
	void print() {
	}	
}

class FunctionCall extends Predicate{
	String methodName;
	int noOfParam;	

	@Override
	void print() {
		// TODO Auto-generated method stub
		predicate.append("has_functioncall("+methodName+","+noOfParam+")");
		predicate.append("^");		
		predicate.append("\n");
	}	
}

class Conditional extends Predicate{
	boolean hasVariable;
	boolean hasFunctionCall;
	boolean hasLiteral;	
	boolean hasExpression;
	
	void print(){
		if(hasFunctionCall){
			predicate.append("has_if(functioncall)");
			predicate.append("^");
			predicate.append("\n");
		}
		if(hasVariable){
			predicate.append("has_if(variable)");
			predicate.append("^");
			predicate.append("\n");
		}
		if(hasFunctionCall){
			predicate.append("has_if(literal)");
			predicate.append("^");
			predicate.append("\n");
		}
		if(hasFunctionCall){
			predicate.append("has_if(expression)");
			predicate.append("^");
			predicate.append("\n");
		}
	}

		
}

class Declaration extends Predicate{
	String name;

	@Override
	void print() {
		// TODO Auto-generated method stub
		
	}
}

class Assignment extends Predicate{
	String name;
	String type;
	String rightExpression;
	@Override
	void print() {
		// TODO Auto-generated method stub
		predicate.append("has_assign("+name+","+type+","+rightExpression+")");
		predicate.append("^");
		predicate.append("\n");
	}
}

class Iteration extends Predicate{
	String conditionType;

	@Override
	void print() {
		// TODO Auto-generated method stub
		predicate.append("has_iter("+conditionType+")");
		predicate.append("^");
		predicate.append("\n");
	}	
}

class Expression extends Predicate{
	String type;

	@Override
	void print() {
		// TODO Auto-generated method stub
		
	}	
}
