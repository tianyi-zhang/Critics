package org.eclipse.compare.internal.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import ut.learner.Learner;
import ut.learner.SpecializationOperator;
import edu.utexas.seal.plugins.overlay.view.CriticsOverlaySearchPredicate;



public class CriticsGeneralizeExamplesHandler extends Action implements IWorkbenchAction{

private static final String ID = "org.eclipse.compare.internal.search.CriticsGeneralizeExamplesHandler";


public CriticsGeneralizeExamplesHandler(){
	setId(ID);
}

public void run() {	
	System.out.println("Hello world");
	getCheckedExamples();
}

public void dispose() {}

public void getCheckedExamples(){
	Stack<String> positiveExamples = new Stack<String>();
	Stack<String> negativeExamples = new Stack<String>();
	
	
	final TableItem [] items =  CriticsOverlaySearchPredicate.viewer.getTable().getItems();
	TableEditor editor =  new TableEditor(CriticsOverlaySearchPredicate.viewer.getTable());
	for(int i=0;i<items.length;i++){		
		if(Learner.RESULTS.getExampleTypeAtIndex(i)){
			System.out.println("Positive Example : "+items[i].getText(2));	
			System.out.println("Query : "+Learner.RESULTS.getSearchInfo().get(i).getQuery());
			positiveExamples.add(Learner.RESULTS.getSearchInfo().get(i).getClassName().trim()+","+Learner.RESULTS.getSearchInfo().get(i).getMethodName().trim());
		} else{
			System.out.println("Negative Example : "+items[i].getText(2));	
			System.out.println("Query : "+Learner.RESULTS.getSearchInfo().get(i).getQuery());
			negativeExamples.add(Learner.RESULTS.getSearchInfo().get(i).getClassName().trim()+","+Learner.RESULTS.getSearchInfo().get(i).getMethodName().trim());
		}			
	}
	
	Stack<String> predicates = new Stack<String>();
	predicates.addAll(Learner.orginalPredicateList);
	
	SpecializationOperator operator = new SpecializationOperator(positiveExamples,negativeExamples,predicates);
	operator.specizlize();
	
	CriticsOverlaySearchPredicate.updateViewer();
}
}