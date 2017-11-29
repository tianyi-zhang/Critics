package org.eclipse.compare.internal.search;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.compare.internal.BaseCompareAction;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import ut.learner.ClassInfo;
import ut.learner.MethodInfo;
import ut.learner.PackageInfo;
import ut.seal.plugins.utils.UTFile;
import ut.seal.plugins.utils.ast.UTASTNodeFinder;
import ut.seal.plugins.utils.ast.UTASTParser;
import ut.seal.plugins.utils.visitor.UTASTFactGeneratorVisitor;
import ut.seal.plugins.utils.visitor.UTASTSearchTypeVisitor;

public class FactGeneratorAction extends BaseCompareAction{

	List<IResource> allJavaFiles = new ArrayList<IResource>();
	HashMap<String, ClassInfo> classInfoMap= new HashMap<String, ClassInfo>();
	
	@Override
	protected void run(ISelection selection) {
		// TODO Auto-generated method stub
		IStructuredSelection sel = (IStructuredSelection) selection;
        Object firstElement = sel.getFirstElement();
        if (firstElement instanceof IAdaptable)
        {        
            IProject project = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);                        
            IWorkspaceRoot myWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();            
            if(myWorkspaceRoot!=null){
            	getAllFiles(project.getLocation(), myWorkspaceRoot);
            	convertAllJavaFilesToFacts(project.getLocationURI().getRawPath().toString());
            	writePackageInfoToFile(project.getLocationURI().getRawPath().toString());
            }                        
        }
	}
	
	public void convertAllJavaFilesToFacts(String projectPath){
		BufferedWriter output = null;

		String fileLocation = projectPath+"/facts.pl";
		System.out.println(fileLocation);
		File file = new File(fileLocation);
		try {
			output = new BufferedWriter(new FileWriter(file));
			StringBuilder builder = new StringBuilder();			
			for(int i=0;i<allJavaFiles.size();i++){								
				UTASTFactGeneratorVisitor visitor = new UTASTFactGeneratorVisitor();
				UTASTParser parser = new UTASTParser();
				IResource res = allJavaFiles.get(i);
				final CompilationUnit unit = parser.parse(UTFile.getContents(res.getRawLocation().toOSString()));
				visitor.setUnit(unit);
				
				UTASTSearchTypeVisitor typesOFClass = new UTASTSearchTypeVisitor();
				typesOFClass = buildTypes(unit);								
				
				visitor.setVariableTypes(typesOFClass.variableTypes);
				
				unit.accept(visitor);				
				
				ClassInfo info = new ClassInfo();
				info.setClassName(visitor.className);
				info.setMethods(visitor.methods);
				info.setPartialPath(res.getFullPath().toOSString());
				info.setPath(res.getRawLocation().toOSString());
				
				this.classInfoMap.put(visitor.className, info);
				
				HashSet<String> set = new HashSet<>();
				set.addAll(visitor.predicatesForSelection);
				Iterator<String> it = set.iterator();
				while(it.hasNext()){
					String tmp = it.next();
					tmp = tmp.replaceAll("_", "");
					builder.append(tmp);
					builder.append("\n");
				}
				output.write(builder.toString());
				builder = new StringBuilder();				
			}
			
			System.out.println("Done Writing");
			PackageInfo.classInfoMap.clear();
			PackageInfo.classInfoMap.putAll(this.classInfoMap);
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}	
	
	public void writePackageInfoToFile(String projectPath){
		BufferedWriter output = null;
		String fileLocation = projectPath+"/packageinfo.txt";
		System.out.println(fileLocation);
		File file = new File(fileLocation);
		try {
			output = new BufferedWriter(new FileWriter(file));
			StringBuilder builder = new StringBuilder();
			for(String key : PackageInfo.classInfoMap.keySet()){
				String tmp = key+":"+PackageInfo.classInfoMap.get(key).getPath();
				
				List<MethodInfo> methods = PackageInfo.classInfoMap.get(key).getMethods();
				for(int i=0;i<methods.size();i++){
					String tmp2 = ":"+methods.get(i).getMethodName()+":"+ methods.get(i).getStartLineNumber();
					builder.append(tmp+tmp2);
					tmp2 = ":"+PackageInfo.classInfoMap.get(key).getPartialPath();
					builder.append(tmp2);
					builder.append("\n");	
				}								
			}
			output.write(builder.toString());
			output.close();			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public UTASTSearchTypeVisitor buildTypes(CompilationUnit unit){				
		UTASTSearchTypeVisitor searchTypesVisitor = new UTASTSearchTypeVisitor();
		unit.accept(searchTypesVisitor);
		return searchTypesVisitor;
	}
	
	public void getAllFiles(IPath path,IWorkspaceRoot iWorkspaceRoot){
		IContainer  container =  iWorkspaceRoot.getContainerForLocation(path);
		
		try{
			IResource[] iResources;
	        iResources = container.members();
	        for (IResource iR : iResources){
	            // for c files
	            if ("java".equalsIgnoreCase(iR.getFileExtension())){
	            	allJavaFiles.add(iR);
//	            	String className = iR.getName().split("\\.")[0];
//	            	classFilepathMap.put(className.toLowerCase(), iR.getRawLocation().toOSString());

	            }	            	
	            if (iR.getType() == IResource.FOLDER){
	                IPath tempPath = iR.getLocation();
	                getAllFiles(tempPath,iWorkspaceRoot);
	            }
	        }
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	protected boolean isEnabled(ISelection s) {		
		IStructuredSelection sel = (IStructuredSelection) s;
        Object firstElement = sel.getFirstElement();
        if (firstElement instanceof IAdaptable)
        {
            IProject project = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);
            if(project!=null){
            	IPath path =  project.getFullPath();
            	if(path == null){
            		return false;
            	}           	
            } else{
            	return false;
            }                        
        }
		return true;
	}	
}
