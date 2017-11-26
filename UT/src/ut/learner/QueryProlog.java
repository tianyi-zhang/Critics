package ut.learner;

import java.util.ArrayList;
import java.util.List;

import org.jpl7.Atom;
import org.jpl7.Query;
import org.jpl7.Term;

public class QueryProlog {

	boolean hasConsulted;
	
	List<String> generalisedPredicate = new ArrayList<String>();
	List<String> orginalPredicateList  =new ArrayList<String>();
	
	public void consultFactBase(){
		String t1 = "consult('/home/whirlwind/runtime-Critics_Search/NEW_JDT9801/facts.pl')";	
		System.out.println("Working Directory = " +
	              System.getProperty("user.dir"));
		System.out.println( t1 + (Query.hasSolution(t1) ? "succeeded" : "failed"));		
		hasConsulted = true;
	}
	
	public List<String> executeSelectedQuery(String query){
		List<String> matchedSolutions = new ArrayList<>();		
		Query q = new Query(query);
		while(q.hasMoreSolutions()){
			String currentSolution = q.nextSolution().get("X").toString(); 
			if(!matchedSolutions.contains(currentSolution)){
				matchedSolutions.add(currentSolution);		
			}				
		}				
		return matchedSolutions;
	}

	public boolean isHasConsulted() {
		return hasConsulted;
	}

	public void setHasConsulted(boolean hasConsulted) {
		this.hasConsulted = hasConsulted;
	}

	public List<String> getOrginalPredicateList() {
		return orginalPredicateList;
	}

	public void setOrginalPredicateList(List<String> orginalPredicateList) {
		this.orginalPredicateList = orginalPredicateList;
	}

	public List<String> getGeneralisedPredicate() {
		return generalisedPredicate;
	}

	public void setGeneralisedPredicate(List<String> generalisedPredicate) {
		this.generalisedPredicate = generalisedPredicate;
	}
		
	public String getQueryString(List<String> queryList){
		StringBuilder builder  = new StringBuilder();
		int i=0;
		for(i=0;i<queryList.size()-1;i++){
			builder.append(queryList.get(i));
			builder.append(",");
		}
		builder.append(queryList.get(i));	
		return builder.toString();
	}
}
