package ut.learner;

import java.util.List;

public class Predicate {

	public String name;
	public List<PredicateValue> arguments;
	String nameAsString;
		
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<PredicateValue> getArguments() {
		return arguments;
	}
	public void setArguments(List<PredicateValue> arguments) {
		this.arguments = arguments;
	}
	public String getNameAsString() {
		return nameAsString;
	}
	public void setNameAsString(String nameAsString) {
		this.nameAsString = nameAsString;
	}
	public String printPredicate(){
		StringBuilder builder = new StringBuilder();
		int i=0;
		builder.append(name);
		builder.append("(");
		for(i=0;i<arguments.size()-1;i++){
			
			builder.append(arguments.get(i).getValue());
			builder.append(",");
		}
		builder.append(arguments.get(i).getValue());
		builder.append(")");
		return builder.toString();
	}
	
}
