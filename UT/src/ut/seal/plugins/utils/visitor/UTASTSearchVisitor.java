package ut.seal.plugins.utils.visitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.WhileStatement;

import ut.seal.plugins.utils.*;

public class UTASTSearchVisitor extends ASTVisitor{		
	StringBuilder predicate;
	List<Predicate> predicates = new ArrayList<>();
	int count;
	String currentVar;
	boolean isAssignment;
	String visitingMethod;
		
	public UTASTSearchVisitor(String methodName) {
		// TODO Auto-generated constructor stub
		this.visitingMethod = visitingMethod;
	}

	public boolean visit(){
		return true;
	}
	
	public boolean visit(FieldAccess node){
		if(isAssignment){
			currentVar = node.getName().toString();
		}
		return true;
	}
	
	public boolean visit(SimpleName node){
		if(isAssignment){
			currentVar = node.getIdentifier();
		}
		return true;
	}
	public boolean visit(IfStatement node){		
		Conditional cond = new Conditional();
		org.eclipse.jdt.core.dom.Expression exp = node.getExpression();
		if(exp.getNodeType() == 32){
			cond.hasFunctionCall = true;
		}
		if(exp.getNodeType()== 62){
			cond.hasExpression = true; 
		}
		if(exp.getNodeType()==9){
			cond.hasLiteral = true;
		}
		if(exp.getNodeType()==42){
			cond.hasVariable = true;
		}
		predicates.add(cond);
		return true;
	}
	
	public boolean visit(MethodInvocation node){				
		count=0;		 
		return true;
	}	
	
	public void endVisit(MethodInvocation node){
		FunctionCall call = new FunctionCall();
		call.methodName = node.getName().toString();
		call.noOfParam =node.arguments().size();
		predicates.add(call);
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
