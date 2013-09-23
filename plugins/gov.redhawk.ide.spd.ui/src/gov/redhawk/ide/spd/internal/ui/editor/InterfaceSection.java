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
package gov.redhawk.ide.spd.internal.ui.editor;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.ui.editor.ScaSection;

import java.util.Collections;

import mil.jpeojtrs.sca.scd.ComponentFeatures;
import mil.jpeojtrs.sca.scd.Interface;
import mil.jpeojtrs.sca.scd.Interfaces;
import mil.jpeojtrs.sca.scd.Ports;
import mil.jpeojtrs.sca.scd.Provides;
import mil.jpeojtrs.sca.scd.ScdFactory;
import mil.jpeojtrs.sca.scd.ScdPackage;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.scd.SupportsInterface;
import mil.jpeojtrs.sca.scd.Uses;
import mil.jpeojtrs.sca.scd.util.ScdAdapterFactory;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;

/**
 * The Class InterfaceSection.
 * 
 * @since 2.1
 */
public class InterfaceSection extends ScaSection {
	private static final int NUM_COLUMNS = 3;

	private static final String AGGREGATE_DEVICE_REPID = "IDL:CF/AggregateDevice:1.0";
	private static final String AGGREGATE_DEVICE = "AggregateDevice";

	private Button aggregateButton;
	private TableViewer interfaceViewer;
	private ComposedAdapterFactory adapterFactory;
	private Resource resource;

	/**
	 * Instantiates a new Interface section.
	 * 
	 * @param page the page
	 * @param parent the parent
	 */
	public InterfaceSection(final ComponentOverviewPage page, final Composite parent) {
		super(page, parent, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.COMPACT);
		createClient(getSection(), page.getEditor().getToolkit());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createClient(final Section section, final FormToolkit toolkit) {
		section.setText("Interfaces");
		section.setLayout(FormLayoutFactory.createClearTableWrapLayout(false, 1));
		final TableWrapData data = new TableWrapData(TableWrapData.FILL_GRAB);
		section.setLayoutData(data);

		section.setDescription("This section configures the interfaces of the component.");
		final Composite client = toolkit.createComposite(section);
		client.setLayout(FormLayoutFactory.createSectionClientGridLayout(false, InterfaceSection.NUM_COLUMNS));
		section.setClient(client);

		final IActionBars actionBars = getPage().getEditor().getEditorSite().getActionBars();

		createInterfacesArea(client, toolkit, actionBars);

		toolkit.paintBordersFor(client);
	}

	/**
	 * Creates the ports area.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createInterfacesArea(final Composite client, final FormToolkit toolkit, final IActionBars actionBars) {
		final Composite tableComp = toolkit.createComposite(client, SWT.NULL);
		final GridLayout layout = new GridLayout(2, false);
		tableComp.setLayout(layout);
		tableComp.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).grab(true, true).create());

		final Table table = toolkit.createTable(tableComp, SWT.SINGLE | SWT.BORDER);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		final TableLayout tableLayout = new TableLayout();
		tableLayout.addColumnData(new ColumnWeightData(20, 30, true)); // SUPPRESS CHECKSTYLE MagicNumber
		tableLayout.addColumnData(new ColumnWeightData(30, 70, true)); // SUPPRESS CHECKSTYLE MagicNumber
		table.setLayout(tableLayout);

		TableColumn column = new TableColumn(table, SWT.NULL);
		column.setText("Name");
		column = new TableColumn(table, SWT.NULL);
		column.setText("RepID");

		this.interfaceViewer = new TableViewer(table);
		this.interfaceViewer.setColumnProperties(new String[] {
		        ScdPackage.Literals.INTERFACE__NAME.getName(), ScdPackage.Literals.INTERFACE__REPID.getName()
		});

		table.setLayoutData(GridDataFactory.fillDefaults().span(1, 3).hint(100, 100).grab(true, true).create()); // SUPPRESS CHECKSTYLE MagicNumber
		// final Button addButton = toolkit.createButton(tableComp, "Add...",
		// SWT.PUSH);
		// addButton.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL,
		// SWT.TOP).create());
		// addButton.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(final SelectionEvent e) {
		// handleAddInterface();
		// }
		// });
		// final Button editButton = toolkit.createButton(tableComp, "Edit",
		// SWT.PUSH);
		// editButton.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL,
		// SWT.TOP).create());
		// editButton.setEnabled(false);
		// editButton.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(final SelectionEvent e) {
		// handleEditInterface();
		// }
		// });
		// final Button removeButton = toolkit.createButton(tableComp, "Remove",
		// SWT.PUSH);
		// removeButton.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL,
		// SWT.TOP).create());
		// removeButton.setEnabled(false);
		// removeButton.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(final SelectionEvent e) {
		// handleRemoveInterface();
		// }
		// });

		this.interfaceViewer.setContentProvider(new AdapterFactoryContentProvider(getAdapterFactory()));
		this.interfaceViewer.setLabelProvider(new AdapterFactoryLabelProvider(getAdapterFactory()));
		this.interfaceViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				// removeButton.setEnabled(!event.getSelection().isEmpty());
				// editButton.setEnabled(!event.getSelection().isEmpty());
			}
		});

		this.aggregateButton = new Button(tableComp, SWT.CHECK);
		this.aggregateButton.setText("Aggregate Device");
		this.aggregateButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.TOP, true, false, 2, 1));
		this.aggregateButton.setVisible(false);
		this.aggregateButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				aggregateClicked(InterfaceSection.this.aggregateButton.getSelection());
			}

			@Override
			public void widgetSelected(final SelectionEvent e) {
				aggregateClicked(InterfaceSection.this.aggregateButton.getSelection());
			}

		});

	}

	protected void aggregateClicked(final boolean selection) {
		final Interfaces ifs = getInterfaces();
		final ComponentFeatures cf = getComponentFeatures();

		if (selection) {
			boolean addAggregate = true;
			for (final Interface iface : ifs.getInterface()) {
				if (InterfaceSection.AGGREGATE_DEVICE.equals(iface.getName())) {
					addAggregate = false;
					break;
				}
			}
			if (addAggregate) {
				final Interface i = ScdFactory.eINSTANCE.createInterface();
				i.setName(InterfaceSection.AGGREGATE_DEVICE);
				i.setRepid(InterfaceSection.AGGREGATE_DEVICE_REPID);

				final SupportsInterface si = ScdFactory.eINSTANCE.createSupportsInterface();
				si.setSupportsName(InterfaceSection.AGGREGATE_DEVICE);
				si.setRepId(InterfaceSection.AGGREGATE_DEVICE_REPID);

				execute(AddCommand.create(getEditingDomain(), cf, ScdPackage.Literals.COMPONENT_FEATURES__SUPPORTS_INTERFACE, si));
				execute(AddCommand.create(getEditingDomain(), ifs, ScdPackage.Literals.INTERFACES__INTERFACE, i));
			}
		} else {
			for (final SupportsInterface si : cf.getSupportsInterface()) {
				if (InterfaceSection.AGGREGATE_DEVICE.equals(si.getSupportsName())) {
					execute(RemoveCommand.create(getEditingDomain(), cf, ScdPackage.Literals.COMPONENT_FEATURES__SUPPORTS_INTERFACE, si));
					break;
				}
			}
			for (final Interface i : ifs.getInterface()) {
				if (InterfaceSection.AGGREGATE_DEVICE_REPID.equals(i.getRepid())) {
					execute(RemoveCommand.create(getEditingDomain(), ifs, ScdPackage.Literals.INTERFACES__INTERFACE, i));
					break;
				}
			}
		}
	}

	/**
	 * Handle remove port.
	 */
	protected void handleRemoveInterface() {
		final Interface iface = (Interface) ((IStructuredSelection) this.interfaceViewer.getSelection()).getFirstElement();
		final String repId = iface.getRepid();

		if (!containsRepId(repId, iface)) {
			execute(RemoveCommand.create(getEditingDomain(), this.getInterfaces(), ScdPackage.Literals.INTERFACES__INTERFACE, iface));
		} else {
			final Shell shell = getPage().getEditorSite().getShell();
			MessageDialog.openWarning(shell, "Cannot Remove Interface", "The interface \"" + iface.getName() + "\" is in use by a port.  It cannot be removed.");
		}
	}

	private boolean containsRepId(final String testRep, final EObject obj) {
		final Ports ports = this.getPorts();
		boolean found = false;
		for (final Provides p : ports.getProvides()) {
			if ((p != obj) && p.getRepID().equals(testRep)) {
				found = true;
				break;
			}
		}

		if (!found) {
			for (final Uses u : ports.getUses()) {
				if ((u != obj) && u.getRepID().equals(testRep)) {
					found = true;
					break;
				}
			}
		}

		return found;
	}

	/**
	 * Execute.
	 * 
	 * @param command the command
	 */
	private void execute(final Command command) {
		getEditingDomain().getCommandStack().execute(command);
	}

	/**
	 * Gets the editing domain.
	 * 
	 * @return the editing domain
	 */
	private EditingDomain getEditingDomain() {
		return getPage().getEditor().getEditingDomain();
	}

	private ComponentFeatures getComponentFeatures() {
		final SoftwareComponent scd = SoftwareComponent.Util.getSoftwareComponent(this.resource);
		return scd.getComponentFeatures();
	}

	private Interfaces getInterfaces() {
		final SoftwareComponent scd = SoftwareComponent.Util.getSoftwareComponent(this.resource);
		return scd.getInterfaces();
	}

	private Ports getPorts() {
		final SoftwareComponent scd = SoftwareComponent.Util.getSoftwareComponent(this.resource);
		return scd.getComponentFeatures().getPorts();
	}

	/**
	 * Gets the adapter factory.
	 * 
	 * @return the adapter factory
	 */
	private AdapterFactory getAdapterFactory() {
		if (this.adapterFactory == null) {
			this.adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);

			this.adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
			this.adapterFactory.addAdapterFactory(new ScdAdapterFactory());
			this.adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
		}
		return this.adapterFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh(final Resource resource) {
		this.resource = resource;
		try {
			boolean foundAggregate = false;
			this.interfaceViewer.setInput(getInterfaces());
			for (final SupportsInterface i : getComponentFeatures().getSupportsInterface()) {
				if ("Device".equals(i.getSupportsName())) {
					this.aggregateButton.setVisible(true);
				} else if (InterfaceSection.AGGREGATE_DEVICE.equals(i.getSupportsName())) {
					foundAggregate = true;
				}
			}
			this.aggregateButton.setSelection(foundAggregate);
		} catch (final Exception e) {
			// Some problem occurred while trying to set the viewer input,
			// therefore set to empty
			this.interfaceViewer.setInput(Collections.EMPTY_LIST);
		}
	}

}
