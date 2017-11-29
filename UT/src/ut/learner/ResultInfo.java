package ut.learner;

public class ResultInfo {

	String classPath;
	String className;
	String methodName;
	int startLineNumber;
	int endLineNumber;
	int length;
	String query;
	boolean isOld;
	
	public ResultInfo(String classPath, String className, String methodName,
			int startLineNumber) {
		super();
		this.classPath = classPath;
		this.className = className;
		this.methodName = methodName;
		this.startLineNumber = startLineNumber;
	}
	public String getClassPath() {
		return classPath;
	}
	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public int getStartLineNumber() {
		return startLineNumber;
	}
	public void setStartLineNumber(int startLineNumber) {
		this.startLineNumber = startLineNumber;
	}
	public int getEndLineNumber() {
		return endLineNumber;
	}
	public void setEndLineNumber(int endLineNumber) {
		this.endLineNumber = endLineNumber;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public boolean isOld() {
		return isOld;
	}
	public void setOld(boolean isOld) {
		this.isOld = isOld;
	}	
	
}
