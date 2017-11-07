package ut.seal.plugins.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.compare.internal.ConvertToFactAction;

import ut.seal.plugins.utils.visitor.UTASTSearchTypeVisitor;

public class UTParseCallList {

	public String parseForCheckPreCondition(List<String> callStack){
		boolean isCheckIf = false;
		List<String> varAccess = new ArrayList<String>();
		List<String> paramChecked = new ArrayList<>();
		int methodCallCount =0;
		StringBuilder builder = new StringBuilder();
		
		for(int i=0;i<callStack.size();i++){
			if(callStack.get(i).contains("if")){
				isCheckIf = true;
			}
			if(callStack.get(i).contains("Endif")){
				isCheckIf = false;				
			}
			if(callStack.get(i).contains("MethodCall")){
				
				methodCallCount++;
			}
			
			if(callStack.get(i).contains("EndMethodCall")){
				if(isCheckIf){
					builder.append(convertToPreConditionPredicate(paramChecked, callStack.get(i).split(":")[1]));
					paramChecked.clear();					
				}	
			}
			
			if(callStack.get(i).contains("VariableAccess")|| callStack.get(i).contains("FieldAccess")){
				varAccess.add(callStack.get(i).split(":")[1]);
			}
			if(callStack.get(i).contains("MethodParam")){
				if(varAccess.contains(callStack.get(i).split(":")[1])){
					paramChecked.add(callStack.get(i).split(":")[1]);
				}
			}
		}
				
		return builder.toString();
	}
	
	public String convertToPreConditionPredicate(List<String> paramChecked,String methodName){
		StringBuilder builder = new StringBuilder();
		if(paramChecked.size()>0){
			for(int i=0;i<paramChecked.size();i++){				
				builder.append("check_pre_cond(");
				builder.append(ConvertToFactAction.methodName);
				builder.append(",");
				builder.append(methodName);
				builder.append(",");
				builder.append(paramChecked.get(i));
				builder.append(")");			
				builder.append("^");
				builder.append("type(");
				builder.append(paramChecked.get(i));
				builder.append(",");
				builder.append(UTASTSearchTypeVisitor.variableTypes.get(paramChecked.get(i)));
				builder.append(")");
			}
			
		}
		return builder.toString();
	}
	
	public String parseForCheckPostCondition(List<String> callStack){
		StringBuilder builder = new StringBuilder();
		boolean isAssignment = false;
		boolean isCheckIf = false;
		String varToCheck = null;
		String currentVar = null;
		String methodName =  null;
		boolean isWithinAssignment = false;
		List<String> ifCheckParams = new ArrayList<>();
		
		for(int i=0;i<callStack.size();i++){
			if(callStack.get(i).contains("Assignment") || callStack.get(i).contains("VariableDeclaration")){
				isAssignment = true;
				isWithinAssignment = true;
			} 
						
			if(callStack.get(i).contains("EndAssignment") || callStack.get(i).contains("EndVariableDeclaration")){
				isWithinAssignment = false;
				varToCheck = currentVar;
			} 
						
			if(callStack.get(i).contains("MethodCall")){
				if(isWithinAssignment){
					methodName = callStack.get(i).split(":")[1];
				}											
			}
			if(callStack.get(i).contains("VariableAccess")|| callStack.get(i).contains("FieldAccess")){
				currentVar = callStack.get(i).split(":")[1];
				ifCheckParams.add(currentVar);
			}
								
			if(callStack.get(i).contains("if")){
				isCheckIf = true;
			}
			
			
			if(callStack.get(i).contains("Endif")){
				if(isAssignment && isCheckIf && ifCheckParams.contains(varToCheck)){
					builder.append("check_ret_value(");
					builder.append(ConvertToFactAction.methodName);
					builder.append(",");
					builder.append(methodName);
					builder.append(")");
				}
				isCheckIf = false;
				isAssignment = false;
			}							
		}
		
		return builder.toString();		
	}
}

