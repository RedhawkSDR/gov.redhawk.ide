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
import gov.redhawk.ide.spd.internal.ui.handlers.EditPortHandler;
import gov.redhawk.ide.spd.internal.ui.handlers.PortsHandlerUtil;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.ui.editor.EMFTableViewerElementSelector;
import gov.redhawk.ui.editor.EMFViewerElementSelector;
import gov.redhawk.ui.editor.ScaSection;
import gov.redhawk.ui.util.ControlCommandBinder;
import gov.redhawk.ui.util.ControlCommandBinding;
import gov.redhawk.ui.util.ResizeTableColumnControlAdapter;
import gov.redhawk.ui.util.SCAEditorUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mil.jpeojtrs.sca.scd.AbstractPort;
import mil.jpeojtrs.sca.scd.Ports;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.scd.SupportsInterface;
import mil.jpeojtrs.sca.scd.Uses;
import mil.jpeojtrs.sca.scd.provider.ScdItemProviderAdapterFactory;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.WrapperItemProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;

/**
 * The Class PortsSection.
 */
public class PortsSection extends ScaSection {

	private static final int NUM_COLUMNS = 3;

	private TableViewer portsViewer;
	private ComposedAdapterFactory adapterFactory;
	private Resource resource;

	private Button addButton;
	private Button editButton;
	private Button removeButton;
	private final List<ControlCommandBinding> controlBindings = new ArrayList<ControlCommandBinding>();

	private boolean editable;

	private EMFViewerElementSelector viewerSelector;

	public enum PortsColumnInfo {
		NAME(0, "Name"), REP_ID(1, "RepID");
		private String name;
		private int index;

		private PortsColumnInfo(final int index, final String name) {
			this.index = index;
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public int getIndex() {
			return this.index;
		}
	}

	/**
	 * Instantiates a new ports section.
	 * 
	 * @param page the page
	 * @param parent the parent
	 */
	public PortsSection(final ComponentOverviewPage page, final Composite parent) {
		super(page, parent, Section.DESCRIPTION);
		createClient(getSection(), page.getEditor().getToolkit());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createClient(final Section section, final FormToolkit toolkit) {
		section.setText("Ports");
		section.setLayout(FormLayoutFactory.createClearTableWrapLayout(false, 1));
		final TableWrapData data = new TableWrapData(TableWrapData.FILL_GRAB);
		section.setLayoutData(data);

		section.setDescription("This section configures the ports of the component.");
		final Composite client = toolkit.createComposite(section);
		client.setLayout(FormLayoutFactory.createSectionClientGridLayout(false, PortsSection.NUM_COLUMNS));
		section.setClient(client);

		final IActionBars actionBars = getPage().getEditor().getEditorSite().getActionBars();

		createPortsArea(client, toolkit, actionBars);

		toolkit.paintBordersFor(client);
	}

	/**
	 * Creates the ports area.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createPortsArea(final Composite client, final FormToolkit toolkit, final IActionBars actionBars) {

		final Composite tableComp = toolkit.createComposite(client, SWT.NULL);
		final GridLayout layout = new GridLayout(2, false);
		tableComp.setLayout(layout);
		tableComp.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).grab(true, true).create());
		final Table table = toolkit.createTable(tableComp, SWT.MULTI | SWT.BORDER);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		this.portsViewer = new TableViewer(table);
		final TableLayout tableLayout = new TableLayout();
		table.setLayoutData(GridDataFactory.fillDefaults().span(1, 3).hint(285, 150).grab(true, true).create()); // SUPPRESS CHECKSTYLE MagicNumber
		tableLayout.addColumnData(new ColumnWeightData(60, 120, false)); // SUPPRESS CHECKSTYLE MagicNumber
		tableLayout.addColumnData(new ColumnWeightData(40, 180, false)); // SUPPRESS CHECKSTYLE MagicNumber
		table.setLayout(tableLayout);

		for (final PortsColumnInfo info : PortsColumnInfo.values()) {
			final TableColumn column = new TableColumn(table, SWT.NULL, info.getIndex());
			column.setText(info.getName());
			column.setResizable(true);
			column.addControlListener(new ResizeTableColumnControlAdapter());
		}

		this.addButton = toolkit.createButton(tableComp, "Add...", SWT.PUSH);
		this.addButton.setEnabled(true);
		this.addButton.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).create());
		this.controlBindings.add(ControlCommandBinder.bindButton(this.addButton, PortsHandlerUtil.ADD_COMMAND));

		this.editButton = toolkit.createButton(tableComp, "Edit", SWT.PUSH);
		this.editButton.setEnabled(false);
		this.editButton.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).create());
		this.controlBindings.add(ControlCommandBinder.bindButton(this.editButton, PortsHandlerUtil.EDIT_COMMAND));

		this.removeButton = toolkit.createButton(tableComp, "Remove", SWT.PUSH);
		this.removeButton.setEnabled(false);
		this.removeButton.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).create());
		this.controlBindings.add(ControlCommandBinder.bindButton(this.removeButton, PortsHandlerUtil.REMOVE_COMMAND));

		this.portsViewer.setContentProvider(new AdapterFactoryContentProvider(getAdapterFactory()));
		this.portsViewer.setLabelProvider(new AdapterFactoryLabelProvider(getAdapterFactory()));
		this.portsViewer.addFilter(new PropertyChannelFilter());
		this.portsViewer.setFilters(new ViewerFilter[] {
		        new ViewerFilter() {

			        @Override
			        public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
				        final Object wrappedElement = AdapterFactoryEditingDomain.unwrap(element);
				        if (wrappedElement instanceof AbstractPort) {
					        final AbstractPort port = (AbstractPort) wrappedElement;
					        if (port.getName().equals(PrfListener.PROPERTY_EVENT)) {
						        return false;
					        }
				        }
				        return true;
			        }

		        }, new ViewerFilter() {

			        @Override
			        public boolean select(final Viewer viewer, final Object parentElement, Object element) {
				        element = AdapterFactoryEditingDomain.unwrap(element);
				        if (element instanceof Uses) {
					        final Uses pp = (Uses) element;
					        if (pp.isBiDirectional()) {
						        return false;
					        }
				        }
				        return true;
			        }

		        }
		});
		this.portsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				getPage().setSelection(event.getSelection());
			}
		});

		this.portsViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(final DoubleClickEvent event) {
				final EditingDomain editingDomain = getPage().getEditingDomain();
				final ComponentEditor editor = (ComponentEditor) getPage().getEditor();
				final Resource editResource = editor.getMainResource();
				final EditPortHandler handler = new EditPortHandler(editor, editingDomain, editResource, ModelUtil.getSoftPkg(editResource));
				handler.displayEditWizard(event.getSelection());
			}
		});
	}

	/**
	 * Gets the adapter factory.
	 * 
	 * @return the adapter factory
	 */
	private AdapterFactory getAdapterFactory() {
		if (this.adapterFactory == null) {
			this.adapterFactory = new ComposedAdapterFactory();

			//			this.adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
			this.adapterFactory.addAdapterFactory(new ScdItemProviderAdapterFactory());
			//			this.adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
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
			this.portsViewer.setInput(getPorts());
			if (this.viewerSelector == null) {
				this.viewerSelector = new EMFTableViewerElementSelector(this.portsViewer);
			}
			if (!getPorts().eAdapters().contains(this.viewerSelector)) {
				getPorts().eAdapters().add(this.viewerSelector);
			}
		} catch (final Exception e) { // SUPPRESS CHECKSTYLE Fallback
			// Some problem occurred while trying to set the viewer input,
			// therefore set to empty
			this.portsViewer.setInput(Collections.EMPTY_LIST);
		}
		this.setEditable();
	}

	@Override
	public void dispose() {
		final Ports ports = getPorts();
		if (ports != null) {
			ports.eAdapters().remove(this.viewerSelector);
		}
		this.viewerSelector = null;
		for (final ControlCommandBinding binding : this.controlBindings) {
			binding.dispose();
		}
		super.dispose();
	}

	/**
	 * Sets this sections widget enablement based on whether the resource is editable.
	 */
	private void setEditable() {
		this.editable = SCAEditorUtil.isEditableResource(getPage(), this.resource);
		if (!isPortSupplier()) {
			this.editable = false;
		}
		this.portsViewer.getTable().setEnabled(this.editable);
		this.addButton.setEnabled(this.editable);
	}

	/**
	 * @return true if the softpkg is a port supplier
	 */
	public boolean isPortSupplier() {
		final SoftwareComponent softwareComponent = SoftwareComponent.Util.getSoftwareComponent(this.resource);
		if (softwareComponent != null) {
			for (final SupportsInterface si : softwareComponent.getComponentFeatures().getSupportsInterface()) {
				if ("IDL:CF/PortSupplier:1.0".equals(si.getRepId())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Gets the ports associated with the SoftwareComponent.
	 * 
	 * @return the {@link Ports} associated with the {@link SoftwareComponent}
	 */
	public Ports getPorts() {
		final SoftwareComponent softwareComponent = SoftwareComponent.Util.getSoftwareComponent(this.resource);
		if (softwareComponent != null) {
			return softwareComponent.getComponentFeatures().getPorts();
		}
		return null;
	}

	public Viewer getViewer() {
		return this.portsViewer;
	}

	private class PropertyChannelFilter extends ViewerFilter {

		@Override
		public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
			if (element instanceof WrapperItemProvider) {
				final Object val = AdapterFactoryEditingDomain.unwrap(element);

				if (val instanceof Uses) {
					if (PrfListener.PROPERTY_EVENT.equals(((Uses) val).getUsesName())) {
						return false;
					}
				}
			}
			return true;
		}

	}
}
