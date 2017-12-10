package ut.seal.plugins.utils.visitor;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import ut.learner.Constant;
import ut.learner.Predicate;
import ut.learner.PredicateValue;

public class UTASTQueryGeneratorVisitor extends ASTVisitor{

	
	public StringBuilder builder = new StringBuilder();
	public List<String> predicatesForMethod = new ArrayList<>();
	public List<Predicate> predicates = new ArrayList<Predicate>();
	String methodName = null;
	String className = null;
	HashMap<String, String> variableTypes = new HashMap<>();

	public String getMethodName() {
		return methodName;
	}


	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}


	public String getClassName() {
		return className;
	}


	public void setClassName(String className) {
		this.className = className;
	}


	public HashMap<String, String> getVariableTypes() {
		return variableTypes;
	}


	public void setVariableTypes(HashMap<String, String> variableTypes) {
		this.variableTypes = variableTypes;
	}
	
	public void combineClassAndMethodName(){
		this.methodName = this.className + ":"+this.methodName;
	}


	public boolean visit(TypeDeclaration node){
		this.className = node.getName().toString().toLowerCase();
		return true;
	}
	

	public boolean visit(){
		return true;
	}
	
	public boolean visit(FieldAccess node){		
		return true;
	}
	
	public boolean visit(SimpleName node){	
		if(this.methodName != null){
			String name = node.getIdentifier().toString();
			if(variableTypes.get(name)!= null){
				builder.append("containstype(");
				builder.append(this.methodName.toLowerCase());
				builder.append(",");
				
				builder.append(name.toLowerCase());
				builder.append(",");	
				String replacingBrackets = variableTypes.get(name).toLowerCase();
				if(replacingBrackets.contains("[")){
					replacingBrackets = replacingBrackets.replaceAll("[\\[\\]]", "");					
				}
				
				builder.append(replacingBrackets);
				builder.append(")");
//				builder.append("\n");
				if(!predicatesForMethod.contains(builder.toString().toLowerCase())){
					predicatesForMethod.add(builder.toString().toLowerCase());
					
					Predicate pred = new Predicate();
					pred.setName("containstype");
					List<PredicateValue> values = new ArrayList<PredicateValue>();
					Constant arg = new Constant(this.methodName.toLowerCase());
					values.add(arg);
					arg = new Constant(replacingBrackets);
					values.add(arg);
					pred.setArguments(values);
					predicates.add(pred);
				}
				builder = new StringBuilder();
			}
		}
		return true;
	}
	
	public void endVisit(VariableDeclarationStatement node){
//		callStack.add("EndVariableDeclaration");		
	}
	
	public boolean visit(IfStatement node){		
		if(this.methodName!=null){
		builder.append("containsif(");
		builder.append(this.methodName.toLowerCase());
		builder.append(")");
//		builder.append("\n");
		if(!predicatesForMethod.contains(builder.toString().toLowerCase())){
			predicatesForMethod.add(builder.toString().toLowerCase());
			
			Predicate pred = new Predicate();
			pred.setName("containsif");
			List<PredicateValue> values = new ArrayList<PredicateValue>();
			Constant arg = new Constant(this.methodName.toLowerCase());
			values.add(arg);		
			pred.setArguments(values);
			predicates.add(pred);
		}
		
		
		builder = new StringBuilder();
		}
		return true;
	}	
	
	public boolean visit(MethodInvocation node){
		if(this.methodName != null){
			builder.append("methodcall(");
			builder.append(this.methodName.toLowerCase());
			builder.append(",");
			
			builder.append(node.getName().getFullyQualifiedName().toLowerCase());	
			builder.append(")");
//			builder.append("\n");
			if(!predicatesForMethod.contains(builder.toString().toLowerCase())){
				predicatesForMethod.add(builder.toString().toLowerCase());
				
				Predicate pred = new Predicate();
				pred.setName("methodcall");
				List<PredicateValue> values = new ArrayList<PredicateValue>();
				Constant arg = new Constant(this.methodName.toLowerCase());
				values.add(arg);
				arg = new Constant(node.getName().getFullyQualifiedName().toLowerCase());
				values.add(arg);
				pred.setArguments(values);
				predicates.add(pred);
			}
			
			builder = new StringBuilder();
		}
		
		return true;
	}	
		
	public boolean visit(Assignment node){
		//maybe useful later to add what type of assignment
					
		return true;
	}
		
	public boolean visit(WhileStatement node){
		if(this.methodName!=null){
		builder.append("containsiterator(");
		builder.append(this.methodName.toLowerCase());
		builder.append(")");
//		builder.append("\n");
		
		if(!predicatesForMethod.contains(builder.toString().toLowerCase())){
			predicatesForMethod.add(builder.toString().toLowerCase());
			
			Predicate pred = new Predicate();
			pred.setName("containsiterator");
			List<PredicateValue> values = new ArrayList<PredicateValue>();
			Constant arg = new Constant(this.methodName.toLowerCase());
			values.add(arg);		
			pred.setArguments(values);
			predicates.add(pred);
		}
		
		builder = new StringBuilder();
		}
		return true;
	}
	
	public boolean visit(ForStatement node){			
		if(this.methodName != null){
		builder.append("containsiterator(");
		builder.append(this.methodName.toLowerCase());
		builder.append(")");
//		builder.append("\n");
		if(!predicatesForMethod.contains(builder.toString().toLowerCase())){
			predicatesForMethod.add(builder.toString().toLowerCase());
			
			Predicate pred = new Predicate();
			pred.setName("containsiterator");
			List<PredicateValue> values = new ArrayList<PredicateValue>();
			Constant arg = new Constant(this.methodName.toLowerCase());
			values.add(arg);		
			pred.setArguments(values);
			predicates.add(pred);
		}
		builder = new StringBuilder();
		}
		return true;
	}
	
	
	public boolean visit(TryStatement node){
//		builder.append("trystatement(");
//		builder.append(this.currentMethod);
//		builder.append(").");
//		builder.append("\n");
		return true;
	}
		
	public boolean visit(CatchClause node){
		if(this.methodName!=null){
		builder.append("catch(");
		builder.append(this.methodName.toLowerCase());
		builder.append(",");
		builder.append(node.getException().getType().toString().toLowerCase());
		builder.append(")");
		if(!predicatesForMethod.contains(builder.toString().toLowerCase())){
			predicatesForMethod.add(builder.toString().toLowerCase());
			
			Predicate pred = new Predicate();
			pred.setName("catch");
			List<PredicateValue> values = new ArrayList<PredicateValue>();
			Constant arg = new Constant(this.methodName.toLowerCase());
			values.add(arg);		
			arg = new Constant(node.getException().getType().toString().toLowerCase());
			values.add(arg);
			pred.setArguments(values);
			predicates.add(pred);
		}
		builder = new StringBuilder();
		}
		return true;
	}
	
	public boolean visit(MethodDeclaration node){		
		this.methodName = this.className+":"+node.getName().getFullyQualifiedName().toLowerCase();
		return true;
	}
	
	public void endVisit(MethodDeclaration node){
		this.methodName = null;
	}
}
