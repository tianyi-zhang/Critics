package ut.learner;

import java.util.ArrayList;
import java.util.List;

public class ClassInfo {
	
	String className;
	String path;
	List<MethodInfo> methods;
	
	public ClassInfo() {
		this.methods = new ArrayList<MethodInfo>();
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public List<MethodInfo> getMethods() {
		return methods;
	}
	public void setMethods(List<MethodInfo> methods) {
		this.methods = methods;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
		
	
}
