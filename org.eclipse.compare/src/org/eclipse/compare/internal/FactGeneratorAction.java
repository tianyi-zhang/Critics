package org.eclipse.compare.internal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import ut.seal.plugins.utils.UTFile;
import ut.seal.plugins.utils.ast.UTASTParser;
import ut.seal.plugins.utils.visitor.UTASTFactGeneratorVisitor;

public class FactGeneratorAction extends BaseCompareAction{

	List<IResource> allJavaFiles = new ArrayList<IResource>();
	
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
			for(int i=0;i<10;i++){
				UTASTFactGeneratorVisitor visitor = new UTASTFactGeneratorVisitor();
				UTASTParser parser = new UTASTParser();
				IResource res = allJavaFiles.get(i);
				final CompilationUnit unit = parser.parse(UTFile.getContents(res.getRawLocation().toOSString()));
				unit.accept(visitor);
				System.out.println();
				builder.append(visitor.builder.toString());				
			}
			output.write(builder.toString());
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}	
	
	public void getAllFiles(IPath path,IWorkspaceRoot iWorkspaceRoot){
		IContainer  container =  iWorkspaceRoot.getContainerForLocation(path);
		
		try{
			IResource[] iResources;
	        iResources = container.members();
	        for (IResource iR : iResources){
	            // for c files
	            if ("java".equalsIgnoreCase(iR.getFileExtension()))
	            	allJavaFiles.add(iR);
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
