package edu.utexas.seal.plugins.overlay.view;



import org.eclipse.compare.internal.search.ConvertToFactAction;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import ut.learner.Learner;
import ut.learner.SearchResuts;

public class CriticsOverlayQueryBrowser extends ViewPart {
	
	public static Text text;
	public static void updateViewer(){		
		StringBuilder builder = new StringBuilder();
		int i=0;
		String toDisplayOriginal = "Original Query : "+ConvertToFactAction.prolog.getQueryString(ConvertToFactAction.prolog.getOrginalPredicateList());
		String toDisplayGeneralised="\n\n\nGeneralised Query : "+ConvertToFactAction.prolog.getQueryString(ConvertToFactAction.prolog.getGeneralisedPredicate());
		for( i=0;i<Learner.queries.size()-1;i++){
			if(i==0){
				builder.append("Original Query : "+Learner.queries.get(i));
				builder.append("\n");
				builder.append("\n");
			}
			builder.append("At Iter "+i+": "+Learner.queries.get(i));		
			builder.append("\n");
			builder.append("\n");
		}
		builder.append("Generalised Query : "+Learner.queries.get(i));	
		builder.append("\n");
		text.setText(builder.toString());						
	}
	
	public static void updateViewAfterUserSelection(String query){
		String current = text.getText();
		StringBuilder builder = new StringBuilder();
		builder.append(current);
		builder.append("\n");
		builder.append("\n");
		builder.append("After User Selection : "+query);	
		builder.append("\n");
		text.setText(builder.toString());
	}
	
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		this.text = new Text(parent, SWT.WRAP| SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION |SWT.BORDER);
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

}
