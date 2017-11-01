package edu.utexas.seal.plugins.overlay.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.compare.internal.ConvertToFactAction;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;


public class CriticsOverlaySearchPredicate extends ViewPart {

	private static TableViewer viewer;
	
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		createViewer(parent);          
	}
	
	
	public static void updateViewer(){				
		String[] value = ConvertToFactAction.visitor.getPredicates().split("\\n");	
		ConvertToFactAction.visitor.clearPredicates();
		ModelProvider.INSTANCE.setResults(Arrays.asList(value));
		viewer.setInput(ModelProvider.INSTANCE.getResults());		
		viewer.refresh();
		
	}
	
	private void createViewer(Composite parent) {
        viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
              
        createColumns(parent, viewer);
        final Table table = viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
              
        viewer.setContentProvider(new ArrayContentProvider());
        // get the content for the viewer, setInput will call getElements in the
        // contentProvider
        viewer.setInput(ModelProvider.INSTANCE.getResults());
        // make the selection available to other views
        getSite().setSelectionProvider(viewer);
        // set the sorter for the table

        // define layout for the viewer
        GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.horizontalSpan = 2;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        viewer.getControl().setLayoutData(gridData);
}

private void createColumns(final Composite parent, final TableViewer viewer) {
       String[] titles = { "Predicate"};
       int[] bounds = {800};
       TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
       col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {                
                return element.toString();
            }
        });      
       
}

private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
    final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
    final TableColumn column = viewerColumn.getColumn();
    column.setText(title);
    column.setWidth(bound);
    column.setResizable(true);
    column.setMoveable(true);
    return viewerColumn;
}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

}

enum ModelProvider {
	INSTANCE;
	private List<String> results;
	private ModelProvider(){
		results = new ArrayList<String>();
	}
	
	public List<String> getResults(){		
		return results;
	}

	public void setResults(List<String> results) {
		this.results = results;
	}		
}

