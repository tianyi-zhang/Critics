package ut.seal.plugins.utils.visitor;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.WhileStatement;

import sun.security.util.Length;

public class UTASTLineNumberVisitor extends ASTVisitor{

	String startPredicate;
	String endPredicate;
	int startLineNumber;
	int endLineNumber;	
	CompilationUnit unit;
	boolean isBlock;
	String methodName;
	boolean isInCurrentMethod;
	boolean foundEndLineNumber;
	boolean isEndPredicateDone;
	
	
	public UTASTLineNumberVisitor(String startPredicate, String endPredicate,
			CompilationUnit unit,String methodName) {
		super();
		this.startPredicate = startPredicate;
		this.endPredicate = endPredicate;
		this.unit = unit;
		this.methodName = methodName;
		startLineNumber = 0;
		endLineNumber = 0;
		
	}

	public int getStartLineNumber() {
		return startLineNumber;
	}

	public void setStartLineNumber(int startLineNumber) {
		this.startLineNumber = startLineNumber;
	}

	public int getEndLineNumber() {
		return endLineNumber;
	}
	public void setEndLineNumber(int endLineNumber) {
		this.endLineNumber = endLineNumber;
	}

	public boolean visit(IfStatement node){		
		if(isInCurrentMethod && startPredicate.contains("containsif") && startLineNumber == 0){
			startLineNumber = unit.getLineNumber(node.getStartPosition());
			isBlock = true;
		}
		
		if(isInCurrentMethod && endPredicate.contains("containsif") && !foundEndLineNumber){
			endLineNumber = unit.getLineNumber(node.getStartPosition());
			foundEndLineNumber = false;
			isBlock = true;
		}
		return true;
	}	
	
	public void endVisit(IfStatement node){
		if(isInCurrentMethod && isBlock && !foundEndLineNumber && !isEndPredicateDone){			
			endLineNumber = unit.getLineNumber(node.getStartPosition());
			foundEndLineNumber = true;
		}
	}
	
	public boolean visit(WhileStatement node){
		if(isInCurrentMethod && startPredicate.contains("containsiterator") && startLineNumber == 0){
			startLineNumber = unit.getLineNumber(node.getStartPosition());
			isBlock = true;
		}
		if(isInCurrentMethod && endPredicate.contains("containsif") && endLineNumber == 0){
			endLineNumber = unit.getLineNumber(node.getStartPosition()+node.getLength());	
			foundEndLineNumber = true;
		}
		return true;
	}
	
	public void endVisit(WhileStatement node){
		if(isInCurrentMethod && isBlock && !foundEndLineNumber){			
			endLineNumber = unit.getLineNumber(node.getStartPosition()+ node.getLength());
			foundEndLineNumber = true;
		}
	}
	
	public boolean visit(ForStatement node){			
		if(isInCurrentMethod && startPredicate.contains("containsiterator") && startLineNumber == 0){
			startLineNumber = unit.getLineNumber(node.getStartPosition());
			isBlock = true;
		}
		if(isInCurrentMethod && endPredicate.contains("containsif") && endLineNumber == 0){
			if(startLineNumber!=0){
			endLineNumber = unit.getLineNumber(node.getStartPosition()+node.getLength());
			foundEndLineNumber = true;
			}
		}
		return true; 
	}

	public void endVisit(ForStatement node){
		if(isInCurrentMethod && isBlock && !foundEndLineNumber){			
			endLineNumber = unit.getLineNumber(node.getStartPosition()+ node.getLength());
			foundEndLineNumber = true;
		}
	}
	
	public boolean visit(MethodInvocation node){
		if(isInCurrentMethod &&startPredicate.contains("methodcall") && startLineNumber == 0){
			String methodCallName = startPredicate.split(",")[1].split(")")[0];
			if(node.getName().getFullyQualifiedName().toLowerCase().equals(methodCallName)){
				startLineNumber = unit.getLineNumber(node.getStartPosition());
			}
		}		
		if(isInCurrentMethod &&endPredicate.contains("containsif") && !foundEndLineNumber){
			endLineNumber = unit.getLineNumber(node.getStartPosition());			
		}
		
		return true;
	}
	
	public boolean visit(MethodDeclaration node){
		if(node.getName().getFullyQualifiedName().toLowerCase().trim().equals(this.methodName)){
			isInCurrentMethod = true;
		}
		return true;
	}
	
	public void endVisit(MethodDeclaration node){
		isInCurrentMethod = false;		
	}
		
}
