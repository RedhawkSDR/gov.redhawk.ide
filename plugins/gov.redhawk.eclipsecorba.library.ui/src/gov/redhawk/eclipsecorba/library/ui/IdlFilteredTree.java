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

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.FilteredTree;

import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.eclipsecorba.library.util.RefreshIdlLibraryJob;

/**
 * A FilteredTree composite that display items from an IDL library.
 * 
 * @since 1.1
 */
public class IdlFilteredTree extends FilteredTree {
	
	private RefreshIdlLibraryJob refreshJob;

	/**
	 * @deprecated Use {@link #IdlFilteredTree(Composite, IdlLibrary)}
	 */
	@Deprecated
	public IdlFilteredTree(final Composite parent, final boolean useNewLook, final IdlLibrary library) {
		this(parent, library, IdlFilter.ALL_WITH_MODULE);
	}

	/**
	 * Creates a {@link FilteredTree} that displays an {@link IdlLibrary}.
	 * @param parent The parent composite
	 * @param library The IDL library
	 * @param filter The filtered set of IDLs to display
	 * @since 1.2
	 */
	public IdlFilteredTree(final Composite parent, final IdlLibrary library, final IdlFilter filter) {
		super(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, new IdlPatternFilter(), false);
		Assert.isNotNull(library);
		TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(library);
		Assert.isNotNull(editingDomain); // The library must be in an editing domain
		
		refreshJob = new RefreshIdlLibraryJob(library);
		refreshJob.setUser(true);
		
		getPatternFilter().setIncludeLeadingWildcard(false);
		
		this.getFilterControl().addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				getPatternFilter().setPattern(((Text) e.widget).getText());
			}
		});

		TreeViewer idlTreeViewer = this.getViewer();
		final IdlRepositoryContentProvider contentProvider = new IdlRepositoryContentProvider(editingDomain);
		idlTreeViewer.setContentProvider(contentProvider);
		idlTreeViewer.setLabelProvider(new IdlRepositoryLabelProvider(contentProvider.getAdapterFactory()));
		idlTreeViewer.setComparator(new ViewerComparator());
		idlTreeViewer.addFilter(filter.getFilter());

		idlTreeViewer.setInput(library);

		if (library.getLoadStatus() == null) {
			refresh();
		}
	}

	/**
	 * Changes the IDL selection filter applied to the tree.
	 * @param filter The filter to apply
	 * @since 1.2
	 */
	public void setFilter(IdlFilter filter) {
		this.getViewer().setFilters(new ViewerFilter[] { filter.getFilter() });
	}

	@Override
	protected Text doCreateFilterText(Composite parent) {
		return new Text(parent, SWT.SINGLE | SWT.BORDER);
	}

	@Override
	protected Composite createFilterControls(Composite parent) {
		super.createFilterControls(parent);

		if (this.filterToolBar != null) {
			
			IAction refreshAction = new Action("", IAction.AS_PUSH_BUTTON) { //$NON-NLS-1$
				/*
				 * (non-Javadoc)
				 * 
				 * @see org.eclipse.jface.action.Action#run()
				 */
				@Override
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
