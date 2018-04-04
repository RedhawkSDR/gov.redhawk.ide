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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionStatusDialog;

import gov.redhawk.eclipsecorba.idl.IdlInterfaceDcl;
import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;

/**
 * A selection dialog that allows the user to select an IDL interface.
 * @since 1.1
 */
public class IdlInterfaceSelectionDialog extends SelectionStatusDialog {

	private IdlFilteredTree filteredTree;
	private IdlLibrary library;
	private IStatus status;
	private IdlFilter filter;

	/**
	 * @deprecated Use {@link #IdlInterfaceSelectionDialog(Shell, IdlLibrary, IdlFilter)}
	 */
	@Deprecated
	public IdlInterfaceSelectionDialog(Shell parent, IdlLibrary library) {
		this(parent, library, IdlFilter.ALL);
	}

	/**
	 * Construct an IDL selection dialog against a given IdlLibrary.
	 * @param parent The parent shell
	 * @param library The IDL library
	 * @param filter The filtered set of IDLs to display
	 * @since 1.2
	 */
	public IdlInterfaceSelectionDialog(Shell parent, IdlLibrary library, IdlFilter filter) {
		super(parent);
		Assert.isNotNull(library);
		this.library = library;
		this.filter = filter;
		setTitle("Select an interface");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		final Label headerLabel = new Label(composite, SWT.NONE);
		headerLabel.setText("Select an IDL interface (? = any character, * = any string)");
		headerLabel.setLayoutData(new GridData(GridData.FILL_BOTH));

		filteredTree = new IdlFilteredTree(composite, library, filter);
		GridData gd = new GridData(GridData.FILL_BOTH);
		applyDialogFont(filteredTree.getViewer().getTree());
		gd.heightHint = filteredTree.getViewer().getTree().getItemHeight() * 15; // hint to show 15 items
		filteredTree.setLayoutData(gd);
		filteredTree.getViewer().addSelectionChangedListener(event -> {
			StructuredSelection selection = (StructuredSelection) event.getSelection();
			handleSelected(selection);
		});
		filteredTree.getViewer().addDoubleClickListener(event -> {
			filteredTree.getViewer().setSelection(event.getSelection());
			if (getOkButton().isEnabled()) {
				okPressed();
			}
		});
		final Button showAllButton = new Button(composite, SWT.CHECK);
		showAllButton.setText("Show all interfaces");
		showAllButton.setSelection(false);
		showAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (showAllButton.getSelection()) {
					filteredTree.setFilter(IdlFilter.ALL);
				} else {
					filteredTree.setFilter(filter);
				}
			}
		});

		updateStatus(new Status(IStatus.ERROR, LibraryUIPlugin.PLUGIN_ID, IStatus.ERROR, "", null));
		return composite;
	}

	/**
	 * Handle a selection change in the viewer. Ensures that the correct number of items
	 * of the correct type are selected.
	 */
	protected void handleSelected(StructuredSelection selection) {
		IStatus s = new Status(IStatus.OK, LibraryUIPlugin.PLUGIN_ID, IStatus.OK, "", null);

		// You can only select one item and it must be an interface
		if (selection.size() != 1) {
			s = new Status(IStatus.ERROR, LibraryUIPlugin.PLUGIN_ID, IStatus.ERROR, "", null);
		} else {
			if (!(selection.getFirstElement() instanceof IdlInterfaceDcl)) {
				s = new Status(IStatus.ERROR, LibraryUIPlugin.PLUGIN_ID, IStatus.ERROR, "", null);
			}
		}
		updateStatus(s);
	}

	@Override
	protected void updateStatus(IStatus status) {
		this.status = status;
		super.updateStatus(status);
	}

	@Override
	protected void okPressed() {
		if (status != null && (status.isOK() || status.getCode() == IStatus.INFO)) {
			super.okPressed();
		}
	}

	@Override
	protected void computeResult() {
		java.util.List< ? > selectedElements = ((StructuredSelection) filteredTree.getViewer().getSelection()).toList();
		setResult(selectedElements);
	}

	/**
	 * @deprecated Use {@link #open(Shell, IdlFilter)}
	 */
	@Deprecated
	public static IdlInterfaceDcl create(Shell parent) {
		// No library was supplied, using the IDL Library from the SDR Root.
		IdlLibrary library = SdrUiPlugin.getDefault().getTargetSdrRoot().getIdlLibrary();
		return open(parent, library, IdlFilter.ALL);
	}

	/**
	 * @deprecated Use {@link #open(Shell, IdlLibrary, IdlFilter)}
	 */
	@Deprecated
	public static IdlInterfaceDcl create(Shell parent, IdlLibrary library) {
		return open(parent, library, IdlFilter.ALL);
	}

	/**
	 * Display an IDL selection dialog with the contents of the IDE's IDL library.
	 * @param parent The parent shell
	 * @param filter The filtered set of IDLs to display
	 * @return The selected interface, or null if cancel was pressed
	 * @since 1.2
	 */
	public static IdlInterfaceDcl open(Shell parent, IdlFilter filter) {
		IdlLibrary library = SdrUiPlugin.getDefault().getTargetSdrRoot().getIdlLibrary();
		return open(parent, library, filter);
	}

	/**
	 * Display an IDL selection dialog with the contents of the provided interface library.
	 * @param parent The parent shell
	 * @param library The IDL library.
	 * @param filter The filtered set of IDLs to display
	 * @return The selected interface, or null if cancel was pressed
	 * @since 1.2
	 */
	public static IdlInterfaceDcl open(Shell parent, IdlLibrary library, IdlFilter filter) {
		IdlInterfaceSelectionDialog dialog = new IdlInterfaceSelectionDialog(parent, library, filter);
		int result = dialog.open();
		if (result == Dialog.OK) {
			return (IdlInterfaceDcl) dialog.getFirstResult();
		} else {
			return null;
		}
	}
}
