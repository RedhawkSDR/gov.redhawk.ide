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

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * A FilteredTree composite that display items from an IDL library.
 * 
 * @since 1.1
 */
public class IdlFilteredTree extends FilteredTree {
	static final PatternFilter PATTERN_FILTER = new IdlPatternFilter();
	private TreeViewer idlTree;

	private IdlLibrary library;

	public IdlFilteredTree(final Composite parent, final boolean useNewLook, final IdlLibrary library) {
		super(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, IdlFilteredTree.PATTERN_FILTER, useNewLook);
		Assert.isNotNull(library);
		Assert.isNotNull(TransactionUtil.getEditingDomain(library)); // The library must be in an editing domain

		this.library = library;

		IdlFilteredTree.PATTERN_FILTER.setIncludeLeadingWildcard(false);

		this.idlTree = this.getViewer();
		this.getFilterControl().addModifyListener(new ModifyListener() {

			public void modifyText(final ModifyEvent e) {
				IdlFilteredTree.PATTERN_FILTER.setPattern(((Text) e.widget).getText());
			}
		});

		this.getFilterControl().addDisposeListener(new DisposeListener() {

			public void widgetDisposed(final DisposeEvent e) {
				IdlFilteredTree.PATTERN_FILTER.setPattern("");
			}
		});

		final IdlRepositoryContentProvider contentProvider = new IdlRepositoryContentProvider(TransactionUtil.getEditingDomain(library));

		this.idlTree.setContentProvider(contentProvider);
		this.idlTree.setLabelProvider(new IdlRepositoryLabelProvider(contentProvider.getAdapterFactory()));
		this.idlTree.setSorter(new ViewerSorter());
		this.idlTree.addFilter(new ViewerFilter() {

			@Override
			public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
				return element instanceof Module || element instanceof IdlInterfaceDcl || element instanceof RepositoryModule
				        || element instanceof IdlRepositoryPendingUpdateAdapter;
			}
		});
		this.idlTree.setInput(library);
	}

}
