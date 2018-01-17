package ut.learner;

import java.util.ArrayList;
import java.util.List;

public class HornClause {

	public List<Predicate> predicates = new ArrayList<Predicate>();
	public List<String> predicatesAsString = new ArrayList<String>();
	

	public List<Predicate> getPredicates() {
		return predicates;
	}

	public void setPredicates(List<Predicate> predicates) {
		this.predicates = predicates;
	}
	
	
	public List<String> getPredicatesAsString() {
		return predicatesAsString;
	}

	public void setPredicatesAsString(List<String> predicatesAsString) {
		this.predicatesAsString = predicatesAsString;
	}

	public String getClauseAsString(){
		StringBuilder builder = new StringBuilder();
		for(int j=0;j<predicates.size();j++){
			Predicate pred = predicates.get(j);
			builder.append(pred.name);
			builder.append("(");
			for(int i=0;i<pred.arguments.size();i++){
				builder.append(pred.arguments.get(i).value);
				if(i<pred.arguments.size()-1)
					builder.append(",");
			}
			builder.append(")");
			if(j<predicates.size()-1)
				builder.append(",");
		}
		return builder.toString();		
	}
	 
	public String getClauseAsStringWithDelimiter(){
		StringBuilder builder = new StringBuilder();
		for(int j=0;j<predicates.size();j++){
			Predicate pred = predicates.get(j);
			builder.append(pred.name);
			builder.append("(");
			for(int i=0;i<pred.arguments.size();i++){
				builder.append(pred.arguments.get(i).value);
				if(i<pred.arguments.size()-1)
					builder.append(",");
			}
			builder.append(")");
			if(j<predicates.size()-1)
				builder.append(".");
		}
		return builder.toString();		
	}
	
}
