package edu.utexas.seal.plugins.crystal.internal;

import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

public class MethodUtilities {
	
	public static final String SEPARATOR_BETWEEN_PARAMETERS = ", ";
	
	public static boolean compareMethods(MethodDeclaration md1, MethodDeclaration md2){
		String sig1 = getMethodSignatureFromASTNode(md1);
		String sig2 = getMethodSignatureFromASTNode(md2);
		int start1 = md1.getStartPosition();
		int start2 = md2.getStartPosition();
		int length1 = md1.getLength();
		int length2 = md2.getLength();
		if( sig1.equals(sig2) && start1 == start2 && length1 == length2 ){
			return true;
		}
		return false;
	}
	
	/**
	 * The method name is the unique ID for the method
	 * 
	 * @param node
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getMethodSignatureFromASTNode(MethodDeclaration node) {
		StringBuffer name = new StringBuffer(node.getName().getIdentifier());
		name.append("(");
		List<SingleVariableDeclaration> parameters = node.parameters();
		int numOfComma = node.parameters().size() - 1;
		int counter = 0;
		for (SingleVariableDeclaration parameter : parameters) {
			String typeName = parameter.getType().toString();
			name.append(typeName);
			if (counter++ < numOfComma) {
				name.append(SEPARATOR_BETWEEN_PARAMETERS);
			}
		}
		name.append(")");
		return name.toString();
	}
}
