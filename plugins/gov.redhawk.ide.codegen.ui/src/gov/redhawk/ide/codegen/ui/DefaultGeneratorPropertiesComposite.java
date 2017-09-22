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
package gov.redhawk.ide.codegen.ui;

import gov.redhawk.ide.codegen.CodegenFactory;
import gov.redhawk.ide.codegen.CodegenPackage;
import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.IPropertyDescriptor;
import gov.redhawk.ide.codegen.ITemplateDesc;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.Property;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.provider.CodegenItemProviderAdapterFactory;
import gov.redhawk.ide.codegen.ui.internal.PropertyDialog;
import gov.redhawk.ui.util.SWTUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import mil.jpeojtrs.sca.spd.Implementation;

import org.eclipse.core.databinding.Binding;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.ReplaceCommand;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @since 2.1
 * 
 */
public class DefaultGeneratorPropertiesComposite extends BaseGeneratorPropertiesComposite implements ICodegenComposite {

	private static final int NUM_ROWS = 3;
	private static final int TOOLTIP_DELAY_MILLIS = 300;
	private static final int TOOLTIP_DISPLAY_TIME_MILLIS = 5000;

	private TableViewer propertiesViewer;
	private Button addPropertyButton;
	private Button editPropertyButton;
	private Button removePropertyButton;
	private ComposedAdapterFactory adapterFactory;

	private Implementation impl;
	private String codegenId;

	/**
	 * @param parent
	 * @param style
	 * @param toolkit
	 */
	public DefaultGeneratorPropertiesComposite(final Composite parent, final int style, final FormToolkit toolkit) {
		this(null, null, parent, style, toolkit);
	}

	/**
	 * @since 10.0
	 */
	public DefaultGeneratorPropertiesComposite(Implementation impl, String codegenId, Composite parent, int style, FormToolkit toolkit) {
		super(parent, style, toolkit);
		this.impl = impl;
		this.codegenId = codegenId;
		initialize();
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
	 * @since 6.0
	 */
	@Override
	protected void createPropertiesArea() {
		final Label label = this.getToolkit().createLabel(this, "Properties:");
		label.setForeground(this.getToolkit().getColors().getColor(IFormColors.TITLE));
		label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).create());
		final Composite tableComp = this.getToolkit().createComposite(this, SWT.NULL);
		final GridLayout layout = SWTUtil.TABLE_ENTRY_LAYOUT_FACTORY.create();
		tableComp.setLayout(layout);
		tableComp.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).indent(2, 0).grab(true, true).create());
		final Table table = new Table(tableComp, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(GridDataFactory.fillDefaults().span(1, DefaultGeneratorPropertiesComposite.NUM_ROWS).grab(true, true).create());
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		final TableLayout tableLayout = new TableLayout();
		tableLayout.addColumnData(new ColumnWeightData(40, 180, true)); // SUPPRESS CHECKSTYLE MagicNumber
		tableLayout.addColumnData(new ColumnWeightData(60, 70, true)); // SUPPRESS CHECKSTYLE MagicNumber
		table.setLayout(tableLayout);

		final TableColumn idColumn = new TableColumn(table, SWT.NULL);
		idColumn.setText("Name");

		final TableColumn valueColumn = new TableColumn(table, SWT.NULL);
		valueColumn.setText("Value");

		this.propertiesViewer = new TableViewer(table);

		ColumnViewerToolTipSupport.enableFor(this.propertiesViewer);

		final TableViewerColumn idViewer = new TableViewerColumn(this.propertiesViewer, idColumn);
		idViewer.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(final Object element) {
				String text = "";
				if (element instanceof Property) {
					if (((Property) element).getId().length() != 0) {
						final ICodeGeneratorDescriptor generator = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen(
						        DefaultGeneratorPropertiesComposite.this.getImplSettings().getGeneratorId());
						final ITemplateDesc template = getTemplateDesc(generator);

						if (template != null) {
							for (final IPropertyDescriptor propDesc : template.getPropertyDescriptors()) {
								if (propDesc.getKey().equals(((Property) element).getId())) {
									text = propDesc.getName();
									break;
								}
							}
						}
					} else {
						text = ((Property) element).getId();
					}

					return text.toString();
				}
				return text;
			};

			@Override
			public String getToolTipText(final Object element) {
				String text = "No description available for this property";
				if (element instanceof Property) {
					if (((Property) element).getId().length() != 0) {
						final ICodeGeneratorDescriptor generator = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen(
						        DefaultGeneratorPropertiesComposite.this.getImplSettings().getGeneratorId());
						final ITemplateDesc template = getTemplateDesc(generator);

						if (template != null) {
							for (final IPropertyDescriptor propDesc : template.getPropertyDescriptors()) {
								if (propDesc.getKey().equals(((Property) element).getId())) {
									text = propDesc.getDescription();
									break;
								}
							}
						}
					}
				}
				return text;
			};

			@Override
			public Point getToolTipShift(final Object object) {
				return new Point(5, 5); // SUPPRESS CHECKSTYLE MagicNumber
			}

			@Override
			public int getToolTipDisplayDelayTime(final Object object) {
				return DefaultGeneratorPropertiesComposite.TOOLTIP_DELAY_MILLIS;
			}

			@Override
			public int getToolTipTimeDisplayed(final Object object) {
				return DefaultGeneratorPropertiesComposite.TOOLTIP_DISPLAY_TIME_MILLIS;
			}
		});

		final TableViewerColumn valueViewer = new TableViewerColumn(this.propertiesViewer, valueColumn);
		valueViewer.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(final Object element) {
				String text = "";
				if (element instanceof Property) {
					final Property prop = (Property) element;
					if (prop.getId().length() != 0) {
						final ICodeGeneratorDescriptor generator = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen(
						        DefaultGeneratorPropertiesComposite.this.getImplSettings().getGeneratorId());
						final ITemplateDesc template = getTemplateDesc(generator);
						if (template != null) {
							for (final IPropertyDescriptor propDesc : template.getPropertyDescriptors()) {
								if (propDesc.getKey().equals(prop.getId())) {
									text = propDesc.getDefaultValue();
									for (final Property tempProp : DefaultGeneratorPropertiesComposite.this.getImplSettings().getProperties()) {
										if (tempProp.getId().equals(prop.getId())) {
											text = tempProp.getValue();
											break;
										}
									}
									break;
								}
							}
						}
					}
				}
				return text;
			};
		});

		this.propertiesViewer.setContentProvider(new AdapterFactoryContentProvider(getAdapterFactory()));
		this.propertiesViewer.setComparator(createPropertiesViewerComparator());
		this.propertiesViewer.setFilters(createPropertiesViewerFilter());

		table.setLayoutData(GridDataFactory.fillDefaults().span(1, DefaultGeneratorPropertiesComposite.NUM_ROWS).grab(true, true).create());
		this.addPropertyButton = this.getToolkit().createButton(tableComp, "Add...", SWT.PUSH);
		this.addPropertyButton.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).create());
		this.editPropertyButton = this.getToolkit().createButton(tableComp, "Edit", SWT.PUSH);
		this.editPropertyButton.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).create());
		this.editPropertyButton.setEnabled(false);
		this.removePropertyButton = this.getToolkit().createButton(tableComp, "Remove", SWT.PUSH);
		this.removePropertyButton.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).create());
		this.removePropertyButton.setEnabled(!this.propertiesViewer.getSelection().isEmpty());
		this.propertiesViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				DefaultGeneratorPropertiesComposite.this.removePropertyButton.setEnabled(!event.getSelection().isEmpty());
				DefaultGeneratorPropertiesComposite.this.editPropertyButton.setEnabled(!event.getSelection().isEmpty());
			}
		});

		this.addListeners();
	}

	/**
	 * Creates the properties viewer filter.
	 * 
	 * @return the viewer filter[]
	 */
	private ViewerFilter[] createPropertiesViewerFilter() {
		return new ViewerFilter[] { new ViewerFilter() {
			@Override
			public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
				return element instanceof Property;
			}
		} };
	}

	/**
	 * Creates the properties viewer comparator.
	 * 
	 * @return the viewer comparator
	 */
	private ViewerComparator createPropertiesViewerComparator() {
		return new ViewerComparator() {
			@Override
			public int compare(final Viewer viewer, final Object e1, final Object e2) {
				if ((e1 == null) || !(e1 instanceof Property)) {
					return 1;
				} else if ((e2 == null) || !(e2 instanceof Property)) {
					return -1;
				}
				return ((Property) e1).getId().compareTo(((Property) e1).getId());
			}
		};
	}

	/**
	 * @since 6.0
	 */
	protected Property getPropertyViewerSelection() {
		return (Property) ((IStructuredSelection) this.propertiesViewer.getSelection()).getFirstElement();
	}

	/**
	 * Handle remove property.
	 */
	protected void handleRemoveProperty() {
		this.getEditingDomain().getCommandStack().execute(
		        RemoveCommand.create(this.getEditingDomain(), this.getImplSettings(), CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__PROPERTIES, Collections
		                .singleton(getPropertyViewerSelection())));
	}

	/**
	 * Handle edit property.
	 */
	protected void handleEditProperty() {
		if (this.getImplSettings() == null) {
			return;
		}
		final Property oldValue = getPropertyViewerSelection();
		final Property propNewValue = EcoreUtil.copy(oldValue);
		final PropertyDialog dialog = new PropertyDialog(getShell(), "Edit Property", propNewValue, getSelectedTemplate());
		if (dialog.open() == Window.OK) {
			this.getEditingDomain().getCommandStack().execute(
			        ReplaceCommand.create(this.getEditingDomain(), this.getImplSettings(), CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__PROPERTIES,
			                oldValue, Collections.singleton(dialog.getValue())));
		}

	}

	/**
	 * Handle add property.
	 */
	protected void handleAddProperty() {
		if (this.getImplSettings() == null) {
			return;
		}
		final PropertyDialog dialog = new PropertyDialog(getShell(), "Add Property", null, getSelectedTemplate());
		if (dialog.open() == Window.OK) {
			this.getEditingDomain().getCommandStack().execute(
			        AddCommand.create(this.getEditingDomain(), this.getImplSettings(), CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__PROPERTIES, dialog
			                .getValue()));
		}
	}

	private void addListeners() {
		this.addPropertyButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleAddProperty();
			}
		});

		this.editPropertyButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleEditProperty();
			}
		});
		this.removePropertyButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleRemoveProperty();
			}
		});
	}

	@Override
	protected void createPropertyBinding() {
		// PASS
	}

	@Override
	protected void preBind(final Implementation impl, final ImplementationSettings implSettings, final List<Binding> bindList) {
		this.propertiesViewer.setInput(this.getImplSettings());
	}

	@Override
	protected void templateSelected(final ITemplateDesc desc) {
		// Remove the old properties in the ImplementationSettings
		final EList<Property> properties = this.getImplSettings().getProperties();
		if (!properties.isEmpty()) {
			final Collection<Property> c = new ArrayList<Property>();
			c.addAll(properties);
			final Command command = RemoveCommand.create(this.getEditingDomain(), this.getImplSettings(),
			        CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__PROPERTIES, c);
			this.getEditingDomain().getCommandStack().execute(command);
		}

		// Add the new Properties with their default values
		if (desc.getPropertyDescriptors().length != 0) {
			final CompoundCommand comp = new CompoundCommand();
			for (final IPropertyDescriptor value : desc.getPropertyDescriptors()) {
				final Property p = CodegenFactory.eINSTANCE.createProperty();
				p.setId(value.getKey());
				p.setValue(value.getDefaultValue());
				comp.append(AddCommand.create(this.getEditingDomain(), this.getImplSettings(), CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__PROPERTIES, p));
			}
			this.getEditingDomain().getCommandStack().execute(comp);
		}

		this.propertiesViewer.setInput(this.getImplSettings());
		this.propertiesViewer.refresh(true);
		if (this.getImplSettings().getProperties().size() > 0) {
			this.propertiesViewer.reveal(this.getImplSettings().getProperties().get(this.getImplSettings().getProperties().size() - 1));
		}
	}

	@Override
	protected ICodeGeneratorDescriptor[] getCodegens() {
		if (this.impl == null) {
			return super.getCodegens();
		}

		String language = this.impl.getProgrammingLanguage().getName();
		final ICodeGeneratorDescriptor[] availableCodegens = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegenByLanguage(language);

		// Filter out JET generators
		List<ICodeGeneratorDescriptor> tempCodegens = new ArrayList<ICodeGeneratorDescriptor>();
		for (int i = 0; i < availableCodegens.length; i++) {
			ICodeGeneratorDescriptor codegen = availableCodegens[i];
			if (codegen.isDeprecated() && !codegen.getId().equals(codegenId)) {
				continue;
			} else {
				tempCodegens.add(codegen);
			}
		}

		return tempCodegens.toArray(new ICodeGeneratorDescriptor[0]);
	}
}
