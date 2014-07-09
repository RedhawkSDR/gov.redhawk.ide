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
package gov.redhawk.ide.spd.internal.ui.editor.composite;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.ide.codegen.provider.CodegenItemProviderAdapterFactory;
import gov.redhawk.ide.spd.internal.ui.editor.provider.ImplementationDetailsSectionDependencyItemProvider;
import gov.redhawk.ide.spd.internal.ui.editor.provider.ImplementationDetailsSectionImplementationItemProvider;
import gov.redhawk.ide.spd.internal.ui.editor.provider.ImplementationDetailsSectionOsItemProvider;
import gov.redhawk.ide.spd.internal.ui.editor.provider.ImplementationDetailsSectionProcessorItemProvider;
import gov.redhawk.ide.spd.internal.ui.editor.provider.ImplementationDetailsSectionPropertyRefItemProvider;
import gov.redhawk.ide.spd.internal.ui.editor.provider.SpdItemProviderAdapterFactoryAdapter;
import gov.redhawk.ui.editor.IScaComposite;
import gov.redhawk.ui.util.SWTUtil;
import mil.jpeojtrs.sca.spd.Dependency;
import mil.jpeojtrs.sca.spd.Os;
import mil.jpeojtrs.sca.spd.Processor;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * 
 */
public class ComponentDependencyComposite extends Composite implements IScaComposite {
	private static final int NUM_COLUMNS = 3;

	private static final int NUM_DEPENDENCY_EDIT_BUTTONS = 3;

	private final FormToolkit toolkit;
	private ComposedAdapterFactory adapterFactory;

	private TableViewer osViewer;
	private TableViewer processorViewer;
	private TableViewer dependencyViewer;
	private Button addOsButton;
	private Button editOsButton;
	private Button removeOsButton;
	private Button addProcessorButton;
	private Button editProcessorButton;
	private Button removeProcessorButton;
	private Button addDependencyButton;
	private Button editDependencyButton;
	private Button removeDependencyButton;

	/**
	 * @param parent
	 * @param style
	 */
	public ComponentDependencyComposite(final Composite parent, final int style, final FormToolkit toolkit) {
		super(parent, style);
		this.toolkit = toolkit;

		this.setLayout(FormLayoutFactory.createSectionClientGridLayout(false, ComponentDependencyComposite.NUM_COLUMNS));

		createOsArea();

		createProcessorArea();

		createDependencyArea();

		toolkit.paintBordersFor(this);
	}

	/**
	 * Creates the os entry.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createOsArea() {
		final Label label = this.toolkit.createLabel(this, "OS:");
		label.setForeground(this.toolkit.getColors().getColor(IFormColors.TITLE));
		label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).create());
		final Composite tableComp = this.toolkit.createComposite(this, SWT.NULL);
		final GridLayout layout = SWTUtil.TABLE_ENTRY_LAYOUT_FACTORY.create();
		tableComp.setLayout(layout);
		tableComp.setLayoutData(GridDataFactory.fillDefaults().span(ComponentDependencyComposite.NUM_COLUMNS - 1, 1).grab(true, true).create());
		final Table table = this.toolkit.createTable(tableComp, SWT.SINGLE | SWT.BORDER);
		this.osViewer = new TableViewer(table);
		this.osViewer.setLabelProvider(new AdapterFactoryLabelProvider(getAdapterFactory()));
		this.osViewer.setContentProvider(new AdapterFactoryContentProvider(getAdapterFactory()));
		this.osViewer.setComparator(createOsViewerComparator());
		this.osViewer.setFilters(createOsViewerFilter());
		table.setLayoutData(GridDataFactory.fillDefaults().span(1, ComponentDependencyComposite.NUM_COLUMNS).hint(SWT.DEFAULT, 50).grab(true, true).create()); // SUPPRESS CHECKSTYLE MagicNumber
		this.addOsButton = this.toolkit.createButton(tableComp, "Add...", SWT.PUSH);
		this.addOsButton.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).create());
		this.editOsButton = this.toolkit.createButton(tableComp, "Edit", SWT.PUSH);
		this.editOsButton.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).create());
		this.editOsButton.setEnabled(false);
		this.removeOsButton = this.toolkit.createButton(tableComp, "Remove", SWT.PUSH);
		this.removeOsButton.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).create());
		this.removeOsButton.setEnabled(!this.osViewer.getSelection().isEmpty());
		this.osViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				ComponentDependencyComposite.this.removeOsButton.setEnabled(!event.getSelection().isEmpty());
				ComponentDependencyComposite.this.editOsButton.setEnabled(!event.getSelection().isEmpty());
			}
		});
	}

	/**
	 * Creates the os viewer filter.
	 * 
	 * @return the viewer filter[]
	 */
	private ViewerFilter[] createOsViewerFilter() {
		return new ViewerFilter[] { new ViewerFilter() {

			@Override
			public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
				return element instanceof Os;
			}
		} };
	}

	/**
	 * Creates the os viewer comparator.
	 * 
	 * @return the viewer comparator
	 */
	private ViewerComparator createOsViewerComparator() {
		return new ViewerComparator();
	}

	/**
	 * Gets the adapter factory.
	 * 
	 * @return the adapter factory
	 */
	protected AdapterFactory getAdapterFactory() {
		if (this.adapterFactory == null) {
			this.adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);

			this.adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
			final SpdItemProviderAdapterFactoryAdapter spdFactory = new SpdItemProviderAdapterFactoryAdapter();
			spdFactory.setImplementationAdapter(new ImplementationDetailsSectionImplementationItemProvider(spdFactory));
			spdFactory.setOsAdapter(new ImplementationDetailsSectionOsItemProvider(spdFactory));
			spdFactory.setProcessorAdapter(new ImplementationDetailsSectionProcessorItemProvider(spdFactory));
			spdFactory.setDependencyAdapter(new ImplementationDetailsSectionDependencyItemProvider(spdFactory));
			spdFactory.setPropertyRefAdapter(new ImplementationDetailsSectionPropertyRefItemProvider(spdFactory));
			this.adapterFactory.addAdapterFactory(spdFactory);
			this.adapterFactory.addAdapterFactory(new CodegenItemProviderAdapterFactory());
			this.adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
		}
		return this.adapterFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		if (this.adapterFactory != null) {
			this.adapterFactory.dispose();
			this.adapterFactory = null;
		}
		super.dispose();
	}

	/**
	 * Creates the processor entry.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createProcessorArea() {
		final Label label = this.toolkit.createLabel(this, "Processor(s):");
		label.setForeground(this.toolkit.getColors().getColor(IFormColors.TITLE));
		label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).create());
		final Composite tableComp = this.toolkit.createComposite(this, SWT.NULL);
		final GridLayout layout = SWTUtil.TABLE_ENTRY_LAYOUT_FACTORY.create();
		tableComp.setLayout(layout);
		tableComp.setLayoutData(GridDataFactory.fillDefaults().span(ComponentDependencyComposite.NUM_COLUMNS - 1, 1).grab(true, true).create());
		final Table table = this.toolkit.createTable(tableComp, SWT.SINGLE | SWT.BORDER);
		this.processorViewer = new TableViewer(table);
		this.processorViewer.setLabelProvider(new AdapterFactoryLabelProvider(getAdapterFactory()));
		this.processorViewer.setContentProvider(new AdapterFactoryContentProvider(getAdapterFactory()));
		this.processorViewer.setComparator(createProcessorViewerComparator());
		this.processorViewer.setFilters(createProcessorViewerFilter());
		table.setLayoutData(GridDataFactory.fillDefaults().span(1, ComponentDependencyComposite.NUM_COLUMNS).hint(SWT.DEFAULT, 50).grab(true, true).create()); // SUPPRESS CHECKSTYLE MagicNumber
		this.addProcessorButton = this.toolkit.createButton(tableComp, "Add...", SWT.PUSH);
		this.addProcessorButton.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).create());
		this.editProcessorButton = this.toolkit.createButton(tableComp, "Edit", SWT.PUSH);
		this.editProcessorButton.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).create());
		this.editProcessorButton.setEnabled(false);
		this.removeProcessorButton = this.toolkit.createButton(tableComp, "Remove", SWT.PUSH);
		this.removeProcessorButton.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).create());

		this.processorViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				ComponentDependencyComposite.this.removeProcessorButton.setEnabled(!event.getSelection().isEmpty());
				ComponentDependencyComposite.this.editProcessorButton.setEnabled(!event.getSelection().isEmpty());
			}
		});

		this.removeProcessorButton.setEnabled(!this.processorViewer.getSelection().isEmpty());
	}

	/**
	 * Creates the processor viewer comparator.
	 * 
	 * @return the viewer comparator
	 */
	private ViewerComparator createProcessorViewerComparator() {
		return new ViewerComparator();
	}

	/**
	 * Creates the processor viewer filter.
	 * 
	 * @return the viewer filter[]
	 */
	private ViewerFilter[] createProcessorViewerFilter() {
		return new ViewerFilter[] { new ViewerFilter() {
			@Override
			public boolean select(final Viewer viewer, final Object parentElement, final Object element) {

				return element instanceof Processor;
			}
		} };
	}

	/**
	 * Creates the os entry.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createDependencyArea() {
		final Label label = this.toolkit.createLabel(this, "Dependency:");
		label.setForeground(this.toolkit.getColors().getColor(IFormColors.TITLE));
		label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).create());
		final Composite tableComp = this.toolkit.createComposite(this, SWT.NULL);
		final GridLayout layout = SWTUtil.TABLE_ENTRY_LAYOUT_FACTORY.create();
		tableComp.setLayout(layout);
		tableComp.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).grab(true, true).create());
		final Table table = this.toolkit.createTable(tableComp, SWT.SINGLE | SWT.BORDER);
		this.dependencyViewer = new TableViewer(table);
		this.dependencyViewer.setLabelProvider(new AdapterFactoryLabelProvider(getAdapterFactory()));
		this.dependencyViewer.setContentProvider(new AdapterFactoryContentProvider(getAdapterFactory()));
		// this.dependencyViewer.setComparator(createDepednecViewerComparator());
		this.dependencyViewer.setFilters(createDependencyViewerFilter());

		table.setLayoutData(GridDataFactory.fillDefaults().span(1, ComponentDependencyComposite.NUM_DEPENDENCY_EDIT_BUTTONS).hint(SWT.DEFAULT, 50) // SUPPRESS CHECKSTYLE MagicNumber
		        .grab(true, true).create());
		this.addDependencyButton = this.toolkit.createButton(tableComp, "Add...", SWT.PUSH);
		this.addDependencyButton.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).create());
		this.editDependencyButton = this.toolkit.createButton(tableComp, "Edit", SWT.PUSH);
		this.editDependencyButton.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).create());
		this.editDependencyButton.setEnabled(false);
		this.removeDependencyButton = this.toolkit.createButton(tableComp, "Remove", SWT.PUSH);
		this.removeDependencyButton.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).create());
		this.removeDependencyButton.setEnabled(!this.dependencyViewer.getSelection().isEmpty());
		this.dependencyViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				ComponentDependencyComposite.this.removeDependencyButton.setEnabled(!event.getSelection().isEmpty());
				ComponentDependencyComposite.this.editDependencyButton.setEnabled(!event.getSelection().isEmpty());
			}
		});
	}

	/**
	 * Creates the processor viewer filter.
	 * 
	 * @return the viewer filter[]
	 */
	private ViewerFilter[] createDependencyViewerFilter() {
		return new ViewerFilter[] { new ViewerFilter() {
			@Override
			public boolean select(final Viewer viewer, final Object parentElement, final Object element) {

				return element instanceof Dependency;
			}
		} };
	}

	/**
	 * @return the osViewer
	 */
	public TableViewer getOsViewer() {
		return this.osViewer;
	}

	/**
	 * @return the processorViewer
	 */
	public TableViewer getProcessorViewer() {
		return this.processorViewer;
	}

	/**
	 * @return the dependencyViewer
	 */
	public TableViewer getDependencyViewer() {
		return this.dependencyViewer;
	}

	/**
	 * @return the addOsButton
	 */
	public Button getAddOsButton() {
		return this.addOsButton;
	}

	/**
	 * @return the editOsButton
	 */
	public Button getEditOsButton() {
		return this.editOsButton;
	}

	/**
	 * @return the removeOsButton
	 */
	public Button getRemoveOsButton() {
		return this.removeOsButton;
	}

	/**
	 * @return the addProcessorButton
	 */
	public Button getAddProcessorButton() {
		return this.addProcessorButton;
	}

	/**
	 * @return the editProcessorButton
	 */
	public Button getEditProcessorButton() {
		return this.editProcessorButton;
	}

	/**
	 * @return the removeProcessorButton
	 */
	public Button getRemoveProcessorButton() {
		return this.removeProcessorButton;
	}

	/**
	 * @return the addProcessorButton
	 */
	public Button getAddDependencyButton() {
		return this.addDependencyButton;
	}

	/**
	 * @return the editProcessorButton
	 */
	public Button getEditDependencyButton() {
		return this.editDependencyButton;
	}

	/**
	 * @return the removeProcessorButton
	 */
	public Button getRemoveDependencyButton() {
		return this.removeDependencyButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEditable(final boolean canEdit) {
		this.osViewer.getTable().setEnabled(canEdit);
		// Don't set enabled on properties viewer since this will disable scrolling
//		this.processorViewer.getTable().setEnabled(canEdit);
//		this.dependencyViewer.getTable().setEnabled(canEdit);
		this.addDependencyButton.setEnabled(canEdit);
		this.addOsButton.setEnabled(canEdit);
		this.addProcessorButton.setEnabled(canEdit);
	}

}
