/**
 * Copyright (c) 2017, UCLA Software Engineering and Analysis Laboratory (SEAL)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */
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
