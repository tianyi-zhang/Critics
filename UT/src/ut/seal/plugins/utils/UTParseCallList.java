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
					builder.append(convertToPredicate(paramChecked, callStack.get(i).split(":")[1]));
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
	
	public String convertToPredicate(List<String> paramChecked,String methodName){
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
}

