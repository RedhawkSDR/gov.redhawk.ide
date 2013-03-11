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
package gov.redhawk.ide.sad.internal.ui.composite;

import mil.jpeojtrs.sca.sad.provider.SadItemProviderAdapterFactory;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * 
 */
public class ExternalPortsComposite extends Composite {

	private final AdapterFactory adapterFactory;
	private final FormToolkit toolkit;
	private final IActionBars actionBars;
	private TableViewer tableViewer;
	private Button addButton;
	private Button removeButton;

	public ExternalPortsComposite(final Composite parent, final int style, final FormToolkit toolkit, final IActionBars actionBars) {
		super(parent, style);
		this.adapterFactory = createAdapterFactory();
		this.toolkit = toolkit;
		this.actionBars = actionBars;
		this.setLayout(new GridLayout(2, false));

		createTable(this);
		createAddButton(this);
		createRemoveButton(this);
		createVerticalSpacer(this);

	}

	private void createVerticalSpacer(final ExternalPortsComposite externalPortsComposite) {
		this.toolkit.createLabel(this, "").setLayoutData(GridDataFactory.fillDefaults().grab(false, true).create());
	}

	private void createRemoveButton(final Composite parent) {
		this.removeButton = this.toolkit.createButton(parent, "Remove", SWT.PUSH);
		this.removeButton.setLayoutData(GridDataFactory.fillDefaults().create());
	}

	private void createAddButton(final Composite parent) {
		this.addButton = this.toolkit.createButton(parent, "Add", SWT.PUSH);
		this.addButton.setLayoutData(GridDataFactory.fillDefaults().create());
	}

	private void createTable(final Composite parent) {

		final Table portTable = this.toolkit.createTable(parent, SWT.SINGLE | SWT.FULL_SELECTION);
		portTable.setHeaderVisible(true);
		portTable.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(1, 3).create()); // SUPPRESS CHECKSTYLE MagicNumber
		final TableLayout tableLayout = new TableLayout();
		tableLayout.addColumnData(new ColumnWeightData(70, 10, true)); // SUPPRESS CHECKSTYLE MagicNumber
		tableLayout.addColumnData(new ColumnWeightData(30, 10, true)); // SUPPRESS CHECKSTYLE MagicNumber
		portTable.setLayout(tableLayout);
		this.tableViewer = new TableViewer(portTable);
		final TableViewerColumn column1 = new TableViewerColumn(this.tableViewer, SWT.LEFT);
		column1.getColumn().setText("Component");
		column1.getColumn().setResizable(true);
		final TableViewerColumn column2 = new TableViewerColumn(this.tableViewer, SWT.LEFT);
		column2.getColumn().setText("Port");
		column2.getColumn().setResizable(true);
		this.tableViewer.setContentProvider(new AdapterFactoryContentProvider(this.adapterFactory));
		this.tableViewer.setLabelProvider(new AdapterFactoryLabelProvider(this.adapterFactory));
	}

	@Override
	public void dispose() {
		super.dispose();
		if (this.adapterFactory instanceof IDisposable) {
			((IDisposable) this.adapterFactory).dispose();
		}
	}

	private AdapterFactory createAdapterFactory() {
		return new SadItemProviderAdapterFactory();
	}

	public Viewer getPortViewer() {
		return this.tableViewer;
	}

	public Button getAddButton() {
		return this.addButton;
	}

	public Button getRemoveButton() {
		return this.removeButton;
	}
}
