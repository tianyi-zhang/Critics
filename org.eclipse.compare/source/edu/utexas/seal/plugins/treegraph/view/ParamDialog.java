/*
 * @(#) ParamDialog.java
 *
 * Copyright 2013 2014 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.treegraph.view;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

import edu.utexas.seal.plugin.Activator;

public class ParamDialog extends FilteredItemsSelectionDialog {
	private ArrayList<String>			resources		= new ArrayList<String>();
	private static final String			DIALOG_SETTINGS	= "ParameterizationDialogSetting";
	private CriticsContextSelectionView	view;

	public ParamDialog(Shell shell, boolean multi) {
		super(shell, multi);
		setTitle("Parameterization Dialog");
		setSelectionHistory(new ResourceSelectionHistory());
	}

	public ParamDialog(Shell shell, boolean multi, CriticsContextSelectionView view) {
		super(shell, multi);
		setTitle("Parameterization Dialog");
		setSelectionHistory(new ResourceSelectionHistory());
		this.view = view;
	}

	/**
	 * Call this before showing dialog
	 * 
	 * @param vars
	 */
	public void setResources(ArrayList<SimpleName> vars) {
		for (SimpleName var : vars) {
			String name = var.resolveTypeBinding().getName() + "   " + var.getIdentifier();
			resources.add(name);
		}
	}

	@Override
	protected Control createExtendedContentArea(Composite parent) {
		return null;
	}

	@Override
	protected IDialogSettings getDialogSettings() {
		IDialogSettings settings = Activator.getDefault().getDialogSettings().getSection(DIALOG_SETTINGS);
		if (settings == null) {
			settings = Activator.getDefault().getDialogSettings().addNewSection(DIALOG_SETTINGS);
		}
		return settings;
	}

	@Override
	protected IStatus validateItem(Object item) {
		return Status.OK_STATUS;
	}

	@Override
	protected ItemsFilter createFilter() {
		return new ItemsFilter() {
			public boolean matchItem(Object item) {
				return matches(item.toString());
			}

			public boolean isConsistentItem(Object item) {
				return true;
			}
		};
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Comparator getItemsComparator() {
		return new Comparator() {
			public int compare(Object arg0, Object arg1) {
				return arg0.toString().compareTo(arg1.toString());
			}
		};
	}

	@Override
	protected void fillContentProvider(AbstractContentProvider contentProvider, ItemsFilter itemsFilter, IProgressMonitor progressMonitor) throws CoreException {
		progressMonitor.beginTask("Searching", resources.size());
		for (Iterator<String> iter = resources.iterator(); iter.hasNext();) {
			contentProvider.add(iter.next(), itemsFilter);
			progressMonitor.worked(1);
		}
		progressMonitor.done();
	}

	@Override
	public String getElementName(Object item) {
		return item.toString();
	}

	private class ResourceSelectionHistory extends SelectionHistory {
		protected Object restoreItemFromMemento(IMemento element) {
			return null;
		}

		protected void storeItemToMemento(Object item, IMemento element) {
		}
	}

	protected void okPressed() {
		// notify view to update parameterization info
		view.propagateParam(this);
		super.okPressed();
	}

	protected StructuredSelection getSelectedItems() {
		return super.getSelectedItems();
	}
}
