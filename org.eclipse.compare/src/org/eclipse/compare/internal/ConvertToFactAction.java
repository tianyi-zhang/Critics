package org.eclipse.compare.internal;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import edu.utexas.seal.plugins.overlay.view.CriticsOverlaySearchPredicate;
import ut.seal.plugins.utils.UTFile;
import ut.seal.plugins.utils.UTParseCallList;
import ut.seal.plugins.utils.ast.UTASTNodeFinder;
import ut.seal.plugins.utils.ast.UTASTParser;
import ut.seal.plugins.utils.ast.UTASTSearchParser;
import ut.seal.plugins.utils.visitor.UTASTSearchPredVisitor;
import ut.seal.plugins.utils.visitor.UTASTSearchTypeVisitor;

public class ConvertToFactAction extends BaseCompareAction {

	public static UTASTSearchPredVisitor visitor;
	public static String methodName;
	
	@Override
	protected void run(ISelection selection) {
		// TODO Auto-generated method stub
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null)
	    {	        
			StructuredSelection structuredSelection = (StructuredSelection) selection;
			Object obj  = structuredSelection.getFirstElement();
			IFile file  = (IFile) Platform.getAdapterManager().getAdapter(obj, IFile.class);			
	        TextSelection s = (TextSelection) window.getSelectionService().getSelection();	
	        
	        buildTypes(file.getRawLocation().toOSString(), s.getStartLine(), s.getLength());
	        getCoveringMethod(file,file.getRawLocation().toOSString(), s.getStartLine(), s.getLength());
	        processSelection(s,file.getRawLocation().toOSString());	        
	    }				
		CriticsOverlaySearchPredicate.updateViewer();
	}

	public void buildTypes(String path, int start,int length){
		UTASTNodeFinder finder = new UTASTNodeFinder();
		Point point = new Point(start,length);
		ASTNode node = finder.findCoveringASTNode(path, point);
		node.accept(new UTASTSearchTypeVisitor());		
	}
	
	public void getCoveringMethod(IFile file,String path,final int start,int length){
		UTASTParser parser = new UTASTParser();
		final CompilationUnit unit = parser.parse(UTFile.getContents(path));		
		unit.accept(new ASTVisitor() {
			public boolean visit(MethodDeclaration node){
				int lineNumber = unit.getLineNumber(node.getStartPosition());					
				if(lineNumber<=start){
					ConvertToFactAction.methodName = node.getName().getFullyQualifiedName();
				}
				return true;
			}
		});	
	}
	
	
	public void processSelection(TextSelection s,String path){		
		UTASTSearchParser searchParser = new UTASTSearchParser();
		CompilationUnit cu = searchParser.parseBlock(s.getText());
		visitor = new UTASTSearchPredVisitor(this.methodName);
		cu.accept(visitor);
		UTParseCallList parse = new UTParseCallList();
		String predicate = parse.parseForCheckPreCondition(visitor.getCallStack());
		predicate = parse.parseForCheckPostCondition(visitor.getCallStack());
//		visitor = new UTASTSearchVisitor(this.methodName);
//		cu.accept(visitor);
//		visitor.getPredicates();		
	}
	
	protected boolean isEnabled(ISelection s) {		
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null)
	    {	        
	        TextSelection selection = (TextSelection) window.getSelectionService().getSelection();
	        System.out.println(s.toString());
	        if(selection.isEmpty()){
	        	return false;
	        }
	    }
		
		return true;
	}	
}
