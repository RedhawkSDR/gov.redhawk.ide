/*******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.eclipsecorba.library.ui;

import gov.redhawk.eclipsecorba.idl.IdlInterfaceDcl;
import gov.redhawk.eclipsecorba.idl.Module;
import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.eclipsecorba.library.RepositoryModule;
import gov.redhawk.eclipsecorba.library.util.RefreshIdlLibraryJob;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.FilteredTree;

/**
 * A FilteredTree composite that display items from an IDL library.
 * 
 * @since 1.1
 */
public class IdlFilteredTree extends FilteredTree {
	
	private RefreshIdlLibraryJob refreshJob;



	public IdlFilteredTree(final Composite parent, final boolean useNewLook, final IdlLibrary library) {
		super(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, new IdlPatternFilter(), false);
		Assert.isNotNull(library);
		TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(library);
		Assert.isNotNull(editingDomain); // The library must be in an editing domain
		
		refreshJob = new RefreshIdlLibraryJob(library);
		refreshJob.setUser(true);
		
		getPatternFilter().setIncludeLeadingWildcard(false);
		
		this.getFilterControl().addModifyListener(new ModifyListener() {

			public void modifyText(final ModifyEvent e) {
				getPatternFilter().setPattern(((Text) e.widget).getText());
			}
		});

		TreeViewer idlTreeViewer = this.getViewer();
		final IdlRepositoryContentProvider contentProvider = new IdlRepositoryContentProvider(editingDomain);
		idlTreeViewer.setContentProvider(contentProvider);
		idlTreeViewer.setLabelProvider(new IdlRepositoryLabelProvider(contentProvider.getAdapterFactory()));
		idlTreeViewer.setSorter(new ViewerSorter());
		idlTreeViewer.addFilter(new ViewerFilter() {

			@Override
			public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
				return element instanceof Module || element instanceof IdlInterfaceDcl || element instanceof RepositoryModule
				        || element instanceof IdlRepositoryPendingUpdateAdapter;
			}
		});
		
		idlTreeViewer.setInput(library);
		
		if (library.getLoadStatus() == null) {
			refresh();
		}
		
	}
	
	@Override
	protected Text doCreateFilterText(Composite parent) {
		return new Text(parent, SWT.SINGLE | SWT.BORDER);
	}

	@Override
	protected Composite createFilterControls(Composite parent) {
		super.createFilterControls(parent);

		if (this.filterToolBar != null) {
			
			IAction refreshAction= new Action("", IAction.AS_PUSH_BUTTON) {//$NON-NLS-1$
				/*
				 * (non-Javadoc)
				 * 
				 * @see org.eclipse.jface.action.Action#run()
				 */
				public void run() {
					refresh();
				}
			};
			refreshAction.setToolTipText("Refresh IDL Library");
			ImageDescriptor desc = LibraryUIPlugin.getDefault().getImageDescriptor("icons/view-refresh.png");
			refreshAction.setImageDescriptor(desc);
			this.filterToolBar.add(refreshAction);
			
			filterToolBar.update(false);
			// initially there is no text to clear
			filterToolBar.getControl().setVisible(true);
		}

		return parent;
	}

	/**
     * @since 1.1
     */
	protected void refresh() {
	    refreshJob.schedule();
    }

	@Override
	protected void updateToolbar(boolean visible) {
		
	}
	
}
