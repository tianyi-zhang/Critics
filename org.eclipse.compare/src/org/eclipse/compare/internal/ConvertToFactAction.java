package org.eclipse.compare.internal;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
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
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import edu.utexas.seal.plugins.overlay.view.CriticsOverlaySearchPredicate;
import ut.learner.ClauseGeneralizer;
import ut.learner.QueryProlog;
import ut.seal.plugins.utils.UTFile;
import ut.seal.plugins.utils.UTParseCallList;
import ut.seal.plugins.utils.ast.UTASTNodeFinder;
import ut.seal.plugins.utils.ast.UTASTParser;
import ut.seal.plugins.utils.ast.UTASTSearchParser;
import ut.seal.plugins.utils.visitor.UTASTQueryGeneratorVisitor;
import ut.seal.plugins.utils.visitor.UTASTSearchPredVisitor;
import ut.seal.plugins.utils.visitor.UTASTSearchTypeVisitor;

public class ConvertToFactAction extends BaseCompareAction {

	public UTASTQueryGeneratorVisitor visitor = new UTASTQueryGeneratorVisitor();
	public static String methodName;
	public static String className;
	public String queryForSelection;
	
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
	        
	        System.out.println(s.getText());
	        
	        getCoveringMethod(file,file.getRawLocation().toOSString(), s.getStartLine(), s.getLength());
	        
	        generateTypesForCurrentFile(file.getRawLocation().toOSString());
	        
	        this.visitor.setClassName(this.className);
	        this.visitor.setMethodName(this.methodName);
	        this.visitor.combineClassAndMethodName();
	        
	        UTASTSearchParser searchParser = new UTASTSearchParser();
			CompilationUnit cu = searchParser.parseBlock(s.getText());
			cu.accept(this.visitor);
	        	        	       
	        System.out.println("The query is ");
	        System.out.println(getQueryString(this.visitor.predicatesForMethod));
	        
	        ClauseGeneralizer generalise = new ClauseGeneralizer(this.visitor.predicatesForMethod);
	        generalise.constructFirstOrderPredicates();
	        System.out.println("The generalised query is ");
	        System.out.println(getQueryString(generalise.getFirstOrderPredicate()));
	        System.out.println("The dropped type query is ");
	        System.out.println(generalise.dropTypes());
	        QueryProlog prolog = new QueryProlog();
	        prolog.consultFactBase();
	    }				
//		CriticsOverlaySearchPredicate.updateViewer();
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
	
	public void generateTypesForCurrentFile(String path){
		UTASTParser parser = new UTASTParser();		
		final CompilationUnit unit = parser.parse(UTFile.getContents(path));
		
		UTASTSearchTypeVisitor typesOFClass = new UTASTSearchTypeVisitor();
		typesOFClass = buildTypes(unit);								
		
		this.visitor.setVariableTypes(typesOFClass.variableTypes);
	}

	public UTASTSearchTypeVisitor buildTypes(CompilationUnit unit){				
		UTASTSearchTypeVisitor searchTypesVisitor = new UTASTSearchTypeVisitor();
		unit.accept(searchTypesVisitor);
		return searchTypesVisitor;
	}
	
	public void convertSrcToPredicates(){
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	    if (window != null)
	    {
	        IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
	        Object firstElement = selection.getFirstElement();
	        if (firstElement instanceof IAdaptable)
	        {
	            IProject project = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);
	            IPath path = project.getFullPath();
	            System.out.println(path);
	        }
	    }
	}
	
	public UTASTSearchTypeVisitor buildTypes(String path, int start,int length){
		UTASTNodeFinder finder = new UTASTNodeFinder();
		Point point = new Point(start,length);
		ASTNode node = finder.findCoveringASTNode(path, point);
		UTASTSearchTypeVisitor searchTypesVisitor = new UTASTSearchTypeVisitor();
		node.accept(searchTypesVisitor);
		return searchTypesVisitor;
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
			
			public boolean visit(TypeDeclaration node){
				ConvertToFactAction.className = node.getName().getFullyQualifiedName();
				return true;
			}
		});	
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
