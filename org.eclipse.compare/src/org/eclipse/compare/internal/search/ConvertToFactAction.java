package org.eclipse.compare.internal.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.compare.internal.BaseCompareAction;
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

import edu.utexas.seal.plugins.overlay.view.CriticsOverlayQueryBrowser;
import edu.utexas.seal.plugins.overlay.view.CriticsOverlaySearchPredicate;
import ut.learner.ClauseGeneralizer;
import ut.learner.HornClause;
import ut.learner.Learner;
import ut.learner.MatchedResult;
import ut.learner.Node;
import ut.learner.PackageInfo;
import ut.learner.QueryProlog;
import ut.learner.ResultInfo;
import ut.learner.SearchResuts;
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
	public List<String> originalPredicateList;
	public static QueryProlog prolog = new QueryProlog();
	HornClause userSelectedClause = new HornClause();
	
	
	@Override
	protected void run(ISelection selection) {
		// TODO Auto-generated method stub
		originalPredicateList = new ArrayList<String>();
		prolog = new QueryProlog();
		
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null)
	    {	        
			StructuredSelection structuredSelection = (StructuredSelection) selection;
			Object obj  = structuredSelection.getFirstElement();
			IFile file  = (IFile) Platform.getAdapterManager().getAdapter(obj, IFile.class);			
	        TextSelection s = (TextSelection) window.getSelectionService().getSelection();	
	        System.out.println(s.getText());
	        
	        if(PackageInfo.classInfoMap.size()==0){
	        	PackageInfo.readPackageInfo();
	        }
	        
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
	        
	        userSelectedClause.setPredicates(visitor.predicates);
	        
	        generateLattice();
	        
//	        Learner.RESULTS = generalisedQuery();
//	        Learner.orginalPredicateList = this.originalPredicateList;
//	        
//			CriticsOverlaySearchPredicate.updateViewer();
//			CriticsOverlayQueryBrowser.updateViewer();
	    }				

	}
	
	
	public void generateLattice(){
//		Lattice children = LatticeUtil.getChildren("root",userSelectedClause, userSelectedClause.getPredicates().size()-2);
		Node lattice = LatticeUtil.getChildren(userSelectedClause);
	}
	
	public SearchResuts generalisedQuery(){
		
		Learner.queries = new ArrayList<String>();
		ClauseGeneralizer generalise = new ClauseGeneralizer(this.visitor.predicatesForMethod);
        generalise.constructFirstOrderPredicates();
        if(!prolog.isHasConsulted()){
        	prolog.consultFactBase();
        }
        System.out.println("The generalised query is ");
        System.out.println(getQueryString(generalise.getFirstOrderPredicate()));                       
        
        prolog.setOrginalPredicateList(generalise.getFirstOrderPredicate());
        
        List<String> predicateList = new ArrayList<String>();
        List<String> reducedPredicateList = new ArrayList<String>();
        
        //get answers before dropping
        List<ResultInfo> matchedMethods = prolog.executeSelectedQuery(getQueryString(generalise.getFirstOrderPredicate()),generalise.getFirstOrderPredicate());     
        Learner.queries.add(getQueryString(generalise.getFirstOrderPredicate()));
        originalPredicateList.addAll(generalise.getFirstOrderPredicate());
        predicateList = generalise.dropTypes();        
//        predicateList = generalise.getFirstOrderPredicate();
        reducedPredicateList.addAll(predicateList);
        Learner.queries.add(getQueryString(reducedPredicateList));
        List<ResultInfo> generalized= prolog.executeSelectedQuery(getQueryString(predicateList),reducedPredicateList);
//        matchedMethods = appendMethods(matchedMethods, generalized);
        matchedMethods.addAll(generalized);
        int reductionCounter = 1;
        while((predicateList.size()-reductionCounter)>2 && matchedMethods.size()<5){        	
        	reducedPredicateList = generalise.dropPredicates(predicateList, predicateList.size()-reductionCounter);
        	Learner.queries.add(getQueryString(reducedPredicateList));
        	generalized = prolog.executeSelectedQuery(getQueryString(reducedPredicateList),reducedPredicateList);
//        	matchedMethods = appendMethods(matchedMethods, generalized);
        	matchedMethods.addAll(generalized);
        	reductionCounter = reductionCounter+1;
        }
        
        System.out.println("After generalisation");
        System.out.println(matchedMethods);
        prolog.setGeneralisedPredicate(reducedPredicateList);
        
        return new SearchResuts(prolog.getMatchedSolutions(),reducedPredicateList,matchedMethods.subList(0, 5));
	}
	
	public List<String> appendMethods(List<String> previousMethods,List<String> currentMethods){
		List<String> appendedMethods = new ArrayList<String>();
		if(previousMethods.size()>6){
			return previousMethods;
		} else{
			for(int i=0;i<currentMethods.size();i++){
				if(previousMethods.size()>6){
					break;
				} else{
					if(!previousMethods.contains(currentMethods.get(i))){
						previousMethods.add(currentMethods.get(i));
					}
				}
			}
		}
		return previousMethods;
		
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
	
	public List<String> setCurrentMatchedMethods(List<String> matchedMethods){
		List<String> methods = new ArrayList<String>();
		for(String method : matchedMethods){
			methods.add(StringUtils.substringBetween(method,"(", ")"));
		}
		
		return methods;
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
