package org.eclipse.compare.internal;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class SearchAction extends BaseCompareAction implements IObjectActionDelegate{

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void run(ISelection selection) {
		// TODO Auto-generated method stub
		System.out.println("Well Hello handsome!");
	}

}
