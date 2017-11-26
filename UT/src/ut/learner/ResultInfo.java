package ut.learner;

public class ResultInfo {

	String classPath;
	String className;
	String methodName;
	int startLineNumber;
	int endLineNumber;
	String query;
	
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
	
	
	
}
