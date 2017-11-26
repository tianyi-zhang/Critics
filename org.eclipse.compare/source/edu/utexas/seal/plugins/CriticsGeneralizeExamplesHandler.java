package edu.utexas.seal.plugins;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import edu.utexas.seal.plugins.overlay.view.CriticsOverlaySearchPredicate;



public class CriticsGeneralizeExamplesHandler extends Action implements IWorkbenchAction{

private static final String ID = "edu.utexas.seal.plugins.CriticsGeneralizeExamplesHandler";

public CriticsGeneralizeExamplesHandler(){
	setId(ID);
}

public void run() {	
	System.out.println("Hello world");
	getCheckedExamples();
}

public void dispose() {}

public void getCheckedExamples(){
	final TableItem [] items =  CriticsOverlaySearchPredicate.viewer.getTable().getItems();
	TableEditor editor =  new TableEditor(CriticsOverlaySearchPredicate.viewer.getTable());
	for(int i=0;i<items.length;i++){
		System.out.println(editor.getColumn());
		System.out.println(items[i].getText(2));
		
	}
}
}