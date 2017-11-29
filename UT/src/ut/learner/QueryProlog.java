package ut.learner;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.jpl7.Atom;
import org.jpl7.Query;
import org.jpl7.Term;

import ut.seal.plugins.utils.UTFile;
import ut.seal.plugins.utils.ast.UTASTParser;
import ut.seal.plugins.utils.visitor.UTASTLineNumberVisitor;
import ut.seal.plugins.utils.visitor.UTASTSelectionVisitor;

public class QueryProlog {

	boolean hasConsulted;
	
	List<String> generalisedPredicate = new ArrayList<String>();
	List<String> orginalPredicateList  =new ArrayList<String>();
	List<String> matchedSolutions = new ArrayList<String>();
	
	
	public void consultFactBase(){
		String t1 = "consult('/home/whirlwind/runtime-Critics_Search/NEW_JDT9801/facts.pl')";	
		System.out.println("Working Directory = " +
	              System.getProperty("user.dir"));
		System.out.println( t1 + (Query.hasSolution(t1) ? "succeeded" : "failed"));		
		hasConsulted = true;
	}
	
	public List<ResultInfo> executeSelectedQuery(String query,List<String> predicateList){			
		List<ResultInfo> matchedResults = new ArrayList<>();		
		Query q = new Query(query);
		while(q.hasMoreSolutions()){
			String currentSolution = q.nextSolution().get("X").toString(); 
			currentSolution = StringUtils.substringBetween(currentSolution,"(", ")");
			if(!matchedSolutions.contains(currentSolution)){
				matchedSolutions.add(currentSolution);	
				ResultInfo info = populateSearchInfoObject(currentSolution,query,predicateList);
				if(info!=null)
					matchedResults.add(info);
			}				
		}				
		return matchedResults;
	}

	public List<String> executeSelectedQueryForLiteral(String query){
		List<String> results = new ArrayList<String>();
		Query q = new Query(query);
		while(q.hasMoreSolutions()){
			String currentSolution = q.nextSolution().get("X").toString(); 
			currentSolution = StringUtils.substringBetween(currentSolution,"(", ")");
			if(!results.contains(currentSolution)){
				results.add(currentSolution.replaceAll("\\s",""));
			}
		}
		return results;
	}

	public ResultInfo populateSearchInfoObject(String solution,String query,List<String> predicateList){	
				
		String className = solution.split(",")[0];
		String methodName = solution.split(",")[1].trim();
		ClassInfo classInfo = PackageInfo.classInfoMap.get(className);
		ResultInfo result = null;		
		
		if(classInfo!=null){
			//get start and end lineNumber
			UTASTParser parser = new UTASTParser();
			final CompilationUnit unit = parser.parse(UTFile.getContents(classInfo.path));	
			
//			UTASTLineNumberVisitor visitor = new UTASTLineNumberVisitor(predicateList.get(0), predicateList.get(predicateList.size()-1), unit, methodName);
			UTASTSelectionVisitor visitor = new UTASTSelectionVisitor();
			visitor.startLineNumber =0;
			visitor.endLineNumber =0;
			visitor.methodName = methodName;
			visitor.unit = unit;
			for(int i=0;i<predicateList.size();i++){
				visitor.currentPredicate = predicateList.get(i);
				visitor.isUpdatedOnce = false;
				unit.accept(visitor);
			}						
			result = new ResultInfo(classInfo.partialPath, className, methodName,visitor.startLineNumber);
			result.setEndLineNumber(visitor.endLineNumber);
			result.setQuery(query);
			result.setOld(false);
		}		
		
		return result;
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

	public List<String> getMatchedSolutions() {
		return matchedSolutions;
	}

	public void setMatchedSolutions(List<String> matchedSolutions) {
		this.matchedSolutions = matchedSolutions;
	}
	
	
}
