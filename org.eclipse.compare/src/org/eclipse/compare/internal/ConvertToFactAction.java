package org.eclipse.compare.internal;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import edu.utexas.seal.plugins.overlay.view.CriticsOverlaySearchPredicate;
import ut.seal.plugins.utils.ast.UTASTNodeFinder;
import ut.seal.plugins.utils.ast.UTASTSearchParser;
import ut.seal.plugins.utils.visitor.UTASTSearchTypeVisitor;
import ut.seal.plugins.utils.visitor.UTASTSearchVisitor;

public class ConvertToFactAction extends BaseCompareAction {

	public static UTASTSearchVisitor visitor;
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
	
	public void processSelection(TextSelection s,String path){		
		UTASTSearchParser searchParser = new UTASTSearchParser();
		CompilationUnit cu = searchParser.parseBlock(s.getText());
		visitor = new UTASTSearchVisitor();
		cu.accept(visitor);
		visitor.getPredicates();		
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
