package ut.learner;

public class MatchedResult {
	String methodName;
	String associatedQuery;
	
	public MatchedResult(String methodName, String associatedQuery) {
		super();
		this.methodName = methodName;
		this.associatedQuery = associatedQuery;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getAssociatedQuery() {
		return associatedQuery;
	}
	public void setAssociatedQuery(String associatedQuery) {
		this.associatedQuery = associatedQuery;
	}
	
	
}
