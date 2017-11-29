package ut.learner;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SpecializationOperator {

	List<String> positiveExamples;
	Stack<String> negativeExamples;
	Stack<String> predicatesAllowed;
	QueryProlog prolog = new QueryProlog();
	List<String> antecedents = new ArrayList<String>();
	
	
	
	public SpecializationOperator(List<String> positiveExamples,
			Stack<String> negativeExamples, Stack<String> predicatesAllowed) {
		super();
		this.positiveExamples = positiveExamples;
		this.negativeExamples = negativeExamples;
		this.predicatesAllowed = predicatesAllowed;
	}

	public void specizlize(){		
		prolog.hasConsulted = true;
		while(!negativeExamples.isEmpty()){
			int index = gainMetric();
			antecedents.add(predicatesAllowed.get(index));
			removeUnSatisfiedExample(predicatesAllowed.get(index));
			predicatesAllowed.remove(index);			
		}		
		System.out.println(antecedents.toString());
		String query = prolog.getQueryString(antecedents);
		
		List<ResultInfo> matchedResults = prolog.executeSelectedQuery(query, antecedents);
		markOldRecords();
		Learner.RESULTS.getSearchInfo().addAll(matchedResults);
		Learner.RESULTS.initializeExampleType();
	}
	
	public void markOldRecords(){
		for(int i=0;i<Learner.RESULTS.getSearchInfo().size();i++){
			Learner.RESULTS.getSearchInfo().get(i).setOld(true);
		}
	}
	private void removeUnSatisfiedExample(String query) {

		List<String> solutions  = prolog.executeSelectedQueryForLiteral(query);
		for(int i=0;i<negativeExamples.size();i++){
			if(!solutions.contains(negativeExamples.get(i))){
				negativeExamples.remove(i);
			}
		}
		for(int i=0;i<positiveExamples.size();i++){
			if(!solutions.contains(positiveExamples.get(i))){
				positiveExamples.remove(i);
			}
		}
	}
	

	public String LearnClauseBody(int count){
		String literal="";
		while(negativeExamples.isEmpty()){
			literal = literal+predicatesAllowed.pop();
			String query = literal+".";
			List<String> solutions  = prolog.executeSelectedQueryForLiteral(query);
			for(int i=0;i<negativeExamples.size();i++){
				if(!solutions.contains(negativeExamples.get(i))){
					negativeExamples.remove(i);
				}
			}
		}
		return null;
	}
	
	public int gainMetric(){
		Stack<String> predicates =new Stack<String>();
		predicates.addAll(predicatesAllowed);
		int literalIndex=0;
		double maxGain = 0;
		for(int i=0;i<predicates.size();i++){
			
			List<String> solutions  = prolog.executeSelectedQueryForLiteral(predicates.pop());
			int noPos = noOfPositives(solutions);
			int noNeg = noOfNegatives(solutions);
			double gain = gainMetric(noPos,noNeg);
			if(maxGain<gain){
				maxGain = gain;
				literalIndex = i;
			}
		}
		return literalIndex;
	}
	
	public double gainMetric(int noPos,int noNeg){
		double division1 = (positiveExamples.size()*1.0)/(positiveExamples.size()+negativeExamples.size()*1.0);
		double division2 = (noPos*1.0)/(noPos+noNeg*1.0);
			return noPos*(Math.log(division2)-Math.log(division1));
	}
	
	
	public int noOfPositives(List<String> solutions){
		int count=0;
		for(int i=0;i<positiveExamples.size();i++){
			if(solutions.contains(positiveExamples.get(i))){
				count++;
			}
		}
		return count;
	}
	

	public int noOfNegatives(List<String> solutions){
		int count=0;
		for(int i=0;i<negativeExamples.size();i++){
			if(solutions.contains(negativeExamples.get(i))){
				count++;
			}
		}
		return count;
	}
}
