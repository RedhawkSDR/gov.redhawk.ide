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
package gov.redhawk.ide.codegen.ui.internal;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.ide.codegen.PortRepToGeneratorMap;
import gov.redhawk.ide.codegen.provider.CodegenItemProviderAdapterFactory;
import gov.redhawk.ui.editor.IScaComposite;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * 
 */
public class PortGeneratorComposite extends Composite implements IScaComposite {
	private static final int NUM_COLUMNS = 3;
	private static final int NUM_ROWS = 3;

	private final FormToolkit toolkit;
	private TableViewer portMapViewer;
	private Button addPropertyButton;
	private Button editPropertyButton;
	private Button removePropertyButton;
	private ComposedAdapterFactory adapterFactory;

	/**
	 * @param parent
	 * @param style
	 * @param toolkit
	 */
	public PortGeneratorComposite(final Composite parent, final int style, final FormToolkit toolkit) {
		super(parent, style);

		this.toolkit = toolkit;

		setLayout(FormLayoutFactory.createSectionClientGridLayout(false, PortGeneratorComposite.NUM_COLUMNS));

		createPropertiesArea();

		toolkit.paintBordersFor(this);
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
	 * Gets the adapter factory.
	 * 
	 * @return the adapter factory
	 */
	protected AdapterFactory getAdapterFactory() {
		if (this.adapterFactory == null) {
			this.adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);

			this.adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
			this.adapterFactory.addAdapterFactory(new CodegenItemProviderAdapterFactory());
			this.adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
		}
		return this.adapterFactory;
	}

	/**
	 * Creates the properties entry.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createPropertiesArea() {
		final Label label = this.toolkit.createLabel(this, "Port\nGenerators:");
		label.setForeground(this.toolkit.getColors().getColor(IFormColors.TITLE));
		label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).span(1, PortGeneratorComposite.NUM_ROWS).create());
		final Table table = new Table(this, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		final TableLayout tableLayout = new TableLayout();
		tableLayout.addColumnData(new ColumnWeightData(20, 165, true)); // SUPPRESS CHECKSTYLE MagicNumber
		tableLayout.addColumnData(new ColumnWeightData(30, 100, true)); // SUPPRESS CHECKSTYLE MagicNumber
		table.setLayout(tableLayout);
		table.setLayoutData(GridDataFactory.fillDefaults().span(1, PortGeneratorComposite.NUM_ROWS).hint(SWT.DEFAULT, 100).grab(true, true).create()); // SUPPRESS CHECKSTYLE MagicNumber

		TableColumn column = new TableColumn(table, SWT.NULL);
		column.setText("RepID");

		column = new TableColumn(table, SWT.NULL);
		column.setText("Generator");

		this.portMapViewer = new TableViewer(table);
		this.portMapViewer.setLabelProvider(new AdapterFactoryLabelProvider(getAdapterFactory()));
		this.portMapViewer.setContentProvider(new AdapterFactoryContentProvider(getAdapterFactory()));
		this.portMapViewer.setFilters(createPortMapViewerFilter());
		this.portMapViewer.setComparator(createPortMapViewerComparator());

		this.addPropertyButton = this.toolkit.createButton(this, "Add...", SWT.PUSH);
		this.addPropertyButton.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).create());
		this.editPropertyButton = this.toolkit.createButton(this, "Edit", SWT.PUSH);
		this.editPropertyButton.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).create());
		this.editPropertyButton.setEnabled(false);
		this.removePropertyButton = this.toolkit.createButton(this, "Remove", SWT.PUSH);
		this.removePropertyButton.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).create());
		this.removePropertyButton.setEnabled(false);
	}

	/**
	 * Creates the port mapping viewer filter.
	 * 
	 * @return the viewer filter[]
	 */
	private ViewerFilter[] createPortMapViewerFilter() {
		return new ViewerFilter[] {
			new ViewerFilter() {
				@Override
				public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
					return element instanceof PortRepToGeneratorMap;
				}
			}
		};
	}

	/**
	 * Creates the port mapping viewer comparator.
	 * 
	 * @return the viewer comparator
	 */
	private ViewerComparator createPortMapViewerComparator() {
		return new ViewerComparator() {
			@Override
			public int compare(final Viewer viewer, final Object e1, final Object e2) {
				if ((e1 == null) || !(e1 instanceof PortRepToGeneratorMap)) {
					return 1;
				} else if ((e2 == null) || !(e2 instanceof PortRepToGeneratorMap)) {
					return -1;
				}
				return ((PortRepToGeneratorMap) e1).getRepId().compareTo(((PortRepToGeneratorMap) e1).getRepId());
			}
		};
	}

	/**
	 * @return the portMapViewer
	 */
	public TableViewer getPortMapViewer() {
		return this.portMapViewer;
	}

	/**
	 * @return the addPropertyButton
	 */
	public Button getAddPropertyButton() {
		return this.addPropertyButton;
	}

	/**
	 * @return the editPropertyButton
	 */
	public Button getEditPropertyButton() {
		return this.editPropertyButton;
	}

	/**
	 * @return the removePropertyButton
	 */
	public Button getRemovePropertyButton() {
		return this.removePropertyButton;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setEditable(final boolean canEdit) {
		this.portMapViewer.getTable().setEnabled(canEdit);
	}

	@Override
	public void setEnabled(final boolean enabled) {
		this.portMapViewer.getControl().setEnabled(enabled);
		super.setEnabled(enabled);
	}

}
