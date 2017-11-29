package ut.seal.plugins.utils.visitor;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.WhileStatement;

public class UTASTSelectionVisitor extends ASTVisitor{

	public int startLineNumber;
	public int endLineNumber;
	public int returnedLineNumber;
	public String methodName;
	public boolean isInCurrentMethod;
	public String currentPredicate;
	public CompilationUnit unit;
	public boolean isLastPredicate;
	public boolean isUpdatedOnce;
	int count;
	List<String> predicates;
	
	public int getReturnedLineNumber() {
		return returnedLineNumber;
	}

	public void setReturnedLineNumber(int returnedLineNumber) {
		this.returnedLineNumber = returnedLineNumber;
	}

	public boolean isInCurrentMethod() {
		return isInCurrentMethod;
	}

	public void setInCurrentMethod(boolean isInCurrentMethod) {
		this.isInCurrentMethod = isInCurrentMethod;
	}

	public String getCurrentPredicate() {
		return currentPredicate;
	}

	public void setCurrentPredicate(String currentPredicate) {
		this.currentPredicate = currentPredicate;
	}

	public boolean visit(IfStatement node){		
		if(isInCurrentMethod && currentPredicate.contains("containsif")){
			int current = unit.getLineNumber(node.getStartPosition());
			if(startLineNumber==0){
				startLineNumber = current;
			}
			if(endLineNumber<current && !isUpdatedOnce){
				endLineNumber = unit.getLineNumber(node.getStartPosition()+node.getLength());
				isUpdatedOnce = true;
			}
		}				
		return true;
	}		
	
	public boolean visit(WhileStatement node){
		if(isInCurrentMethod && currentPredicate.contains("containsiterator")){
			int current = unit.getLineNumber(node.getStartPosition());
			if(startLineNumber==0){
				startLineNumber = current;
			}
			if(endLineNumber<current && !isUpdatedOnce){
				endLineNumber = unit.getLineNumber(node.getStartPosition()+node.getLength());
				isUpdatedOnce = true;
			}
		}				
		return true;
	}
	
	public boolean visit(ForStatement node){			
		if(isInCurrentMethod && currentPredicate.contains("containsiterator")){
			int current = unit.getLineNumber(node.getStartPosition());
			if(startLineNumber==0){
				startLineNumber = current;
			}
			if(endLineNumber<current && !isUpdatedOnce){
				endLineNumber = unit.getLineNumber(node.getStartPosition()+node.getLength());
				isUpdatedOnce = true;
			}
		}
		return true; 
	}

	public boolean visit(MethodInvocation node){
		if(isInCurrentMethod && currentPredicate.contains("methodcall")){
			int current = unit.getLineNumber(node.getStartPosition());
			if(startLineNumber==0){
				startLineNumber = current;
			}
			if(endLineNumber<current && !isUpdatedOnce){
				endLineNumber = unit.getLineNumber(node.getStartPosition()+node.getLength());
				isUpdatedOnce = true;
			}
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
