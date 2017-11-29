package ut.learner;

import java.util.ArrayList;
import java.util.List;

public class SearchResuts {
	
	List<String> matchedMethods;
	List<String> predicateList;
	List<ResultInfo> searchInfo;
	Boolean[] exampleType;
	
	
	public SearchResuts(List<String> matchedMethods, List<String> predicateList,List<ResultInfo> searchInfo) {
		super();
		this.matchedMethods = matchedMethods;
		this.predicateList = predicateList;
		this.searchInfo = searchInfo;
		initializeExampleType();
		
	}
	public List<String> getMatchedMethods() {
		return matchedMethods;
	}
	public void setMatchedMethods(List<String> matchedMethods) {
		this.matchedMethods = matchedMethods;
	}
	public List<String> getPredicateList() {
		return predicateList;
	}
	public void setPredicateList(List<String> predicateList) {
		this.predicateList = predicateList;
	}	
	
	public List<ResultInfo> populateSearchInfoObjects(){
		List<ResultInfo> results = new ArrayList<ResultInfo>();
		for(int i=0;i<this.matchedMethods.size();i++){
			String className = this.matchedMethods.get(i).split(",")[0];
			String methodName = this.matchedMethods.get(i).split(",")[1];
			ClassInfo classInfo = PackageInfo.classInfoMap.get(className);
			ResultInfo result;
			if(classInfo!=null){
			List<MethodInfo> methods = classInfo.methods;
			for(int k=0;k<methods.size();k++){
				if(methods.get(k).methodName.equalsIgnoreCase(methodName.trim())){
					result = new ResultInfo(classInfo.path, className, methodName, methods.get(k).startLineNumber);
					results.add(result);
					break;
				}
			}
			}
			
		}
		
		return results;
	}
	
	public void initializeExampleType(){
		exampleType = new Boolean[searchInfo.size()];
		for(int i=0;i<exampleType.length;i++){
			exampleType[i] = false;
		}
	}
	public void extendExampleType(){
		Boolean[] localExampleType  = new Boolean[searchInfo.size()];
		int i=0;
		for( i=0;i<exampleType.length;i++){
			localExampleType[i] = exampleType[i];
		}
		for(;i<localExampleType.length;i++){
			localExampleType[i] = false;
		}
		exampleType = localExampleType;
	}
	
	public List<ResultInfo> getSearchInfo() {
		return searchInfo;
	}
	public void setSearchInfo(List<ResultInfo> searchInfo) {
		this.searchInfo = searchInfo;
	}	
	
	public void setExampleType(boolean isPositive,int index){
		this.exampleType[index] = isPositive;
	}
	
	public boolean getExampleTypeAtIndex(int index){
		return this.exampleType[index];
	}
	
	public void setExampleTypeToNUll(){
		exampleType = null;
	}
}
