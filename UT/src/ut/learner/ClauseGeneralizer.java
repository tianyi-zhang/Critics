package ut.learner;

import java.util.ArrayList;
import java.util.List;

public class ClauseGeneralizer {

	List<String> predicates;
	List<String> firstOrderPredicate;
	String methodVariableName;
	String variableVarName;

	public ClauseGeneralizer(List<String> predicates) {
		super();
		this.predicates = predicates;
		firstOrderPredicate = new ArrayList<String>();
		this.methodVariableName = "X";
		this.variableVarName = "_";
		
	}
	
	public List<String> getFirstOrderPredicate() {
		return firstOrderPredicate;
	}

	public void setFirstOrderPredicate(List<String> firstOrderPredicate) {
		this.firstOrderPredicate = firstOrderPredicate;
	}

	public void constructFirstOrderPredicates(){
		for(int i=0;i<predicates.size();i++){
			if(predicates.get(i).contains("methodcall(")){
				constructVariableForMethodCall(predicates.get(i));
			} else{
				if(predicates.get(i).contains("containstype(")){
					constructVariableForVariableNameInType(predicates.get(i));
				} else{
					if(predicates.get(i).contains("containsiterator(")){
						constructVariableForIterator(predicates.get(i));
					} else{
						if(predicates.get(i).contains("containsif(")){
							constructVariableForIfCondition(predicates.get(i));
						} else{
							if(predicates.get(i).contains("catch(")){
								constructVariableForCatch(predicates.get(i));
							} 
						}
					}
				}
			}
		}
	}
	
	public void constructVariableForMethodCall(String methodCall){
		StringBuilder builder = new StringBuilder();
		String[] str = methodCall.split("\\(");
		builder.append(str[0]);
		builder.append("(");
		builder.append(this.methodVariableName);
		builder.append(",");
		builder.append(str[1].split(",")[1]);		
		this.firstOrderPredicate.add(builder.toString());
	}
	
	public void constructVariableForVariableNameInType(String type){
		StringBuilder builder = new StringBuilder();
		String[] str = type.split("\\(");		
		builder.append(str[0]);
		builder.append("(");
		builder.append(this.methodVariableName);
		builder.append(",");
		builder.append(this.variableVarName);
		builder.append(",");
		builder.append(str[1].split(",")[2]);
		this.firstOrderPredicate.add(builder.toString());
	}
	
	public void constructVariableForIterator(String iterator){
		StringBuilder builder = new StringBuilder();
		String str = iterator.split("\\(")[0];
		builder.append(str);
		builder.append("(");
		builder.append(this.methodVariableName);
		builder.append(")");		
		this.firstOrderPredicate.add(builder.toString());
	}
	
	public void constructVariableForIfCondition(String ifCondition){
		StringBuilder builder = new StringBuilder();
		String str = ifCondition.split("\\(")[0];
		builder.append(str);
		builder.append("(");
		builder.append(this.methodVariableName);
		builder.append(")");		
		this.firstOrderPredicate.add(builder.toString());
	}
	
	public void constructVariableForCatch(String catchClause){
		StringBuilder builder = new StringBuilder();
		String[] str = catchClause.split("\\(");		
		builder.append(str[0]);
		builder.append("(");
		builder.append(this.methodVariableName);
		builder.append(",");
		builder.append(str[1].split(",")[1]);
		this.firstOrderPredicate.add(builder.toString());
	}
	
	public List<String> dropTypes(){
		List<String> droppedTypePredicate = new ArrayList<>();
		for(int i=0;i<firstOrderPredicate.size();i++){
			if(!firstOrderPredicate.get(i).contains("containstype(")){
				droppedTypePredicate.add(firstOrderPredicate.get(i));
			}
		}
		return droppedTypePredicate;
	}
	
	public List<String> dropPredicates(List<String> predicateList, int n){
		if(n>1){
			return predicateList.subList(0,n);
		} 
		return predicateList;
	}
}
