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

import gov.redhawk.codegen.validation.OutputDirectoryValidator;
import gov.redhawk.ide.codegen.CodegenFactory;
import gov.redhawk.ide.codegen.CodegenPackage;
import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.IPropertyDescriptor;
import gov.redhawk.ide.codegen.ITemplateDesc;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.Property;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.provider.CodegenItemProviderAdapterFactory;
import gov.redhawk.ide.codegen.ui.internal.CustomPropertyItemProvider;
import gov.redhawk.ide.codegen.ui.internal.PropertyDialog;
import gov.redhawk.ide.codegen.ui.internal.WaveDevItemProviderAdapterFactoryAdapter;
import gov.redhawk.ide.codegen.util.CodegenFileHelper;
import gov.redhawk.ui.util.EMFEmptyStringToNullUpdateValueStrategy;
import gov.redhawk.ui.util.SWTUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.ValidationStatusProvider;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.EMFObservables;
import org.eclipse.emf.databinding.EMFUpdateValueStrategy;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

/**
 * The Class CodeGneratorWizardPage.
 * 
 * @since 2.1
 */
public class DefaultGeneratorPropertiesWizardPage extends WizardPage implements ICodegenWizardPage {

	private static final int TOOLTIP_DELAY_MILLIS = 300;
	private static final int TOOLTIP_DISPLAY_TIME_MILLIS = 5000;

	private class AllRequiredPropertiesValidator extends ValidationStatusProvider {

		private final WritableValue<IStatus> status;
		private final IObservableList< ? > targets;
		private final IObservableList< ? > models;

		public AllRequiredPropertiesValidator() {
			this.status = new WritableValue<IStatus>();
			this.status.setValue(Status.OK_STATUS);
			this.targets = Observables.emptyObservableList();
			this.models = Observables.emptyObservableList();
			DefaultGeneratorPropertiesWizardPage.this.implSettings.eAdapters().add(new EContentAdapter() {
				/**
				 * {@inheritDoc}
				 */
				@Override
				public void notifyChanged(final Notification msg) {
					super.notifyChanged(msg);
					if (DefaultGeneratorPropertiesWizardPage.this.selectedTemplate == null) {
						return;
					}
					final IPropertyDescriptor[] properties = DefaultGeneratorPropertiesWizardPage.this.selectedTemplate.getPropertyDescriptors();
					final ArrayList<IPropertyDescriptor> missing = new ArrayList<IPropertyDescriptor>();
					for (final IPropertyDescriptor propDesc : properties) {
						if (propDesc.isRequired()) {
							boolean found = false;
							final Iterator<Property> propIterator = DefaultGeneratorPropertiesWizardPage.this.implSettings.getProperties().iterator();
							while (propIterator.hasNext()) {
								if (propDesc.getKey().equals(propIterator.next().getId())) {
									found = true;
								}
							}
							if (!found) {
								missing.add(propDesc);
							}
						}
					}
					IStatus retVal = Status.OK_STATUS;
					if (!missing.isEmpty()) {
						final StringBuilder builder = new StringBuilder();
						for (final IPropertyDescriptor propDesc : missing) {
							builder.append(", " + propDesc.getKey());
						}
						retVal = new Status(IStatus.ERROR, RedhawkCodegenUiActivator.getPluginId(), "Missing property" + builder.toString(), null);
					}
					final IStatus finalRetVal = retVal;
					if (!status.isDisposed()) {
						AllRequiredPropertiesValidator.this.status.getRealm().exec(new Runnable() {

							@Override
							public void run() {
								if (status.isDisposed()) {
									return;
								}
								AllRequiredPropertiesValidator.this.status.setValue(finalRetVal);
							}

						});
					}
				}
			});
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IObservableList< ? > getModels() {
			return this.models;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IObservableList< ? > getTargets() {
			return this.targets;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IObservableValue<IStatus> getValidationStatus() {
			return this.status;
		}

	}

	private static final ImageDescriptor TITLE_IMAGE = null;

	private final EMFDataBindingContext context = new EMFDataBindingContext();

	private ImplementationSettings implSettings;

	private ICodeGeneratorDescriptor codegen = null;

	private ITemplateDesc selectedTemplate = null;

	private WizardPageSupport support;

	private TableViewer propertiesViewer;

	private ComposedAdapterFactory adapterFactory;

	private Text generatorLabel = null;

	private Text outputDirText = null;

	private ComboViewer templateViewer;

	private boolean configured;

	private boolean created;

	private boolean canFlip = false;

	private boolean canFinish = false;
	private SoftPkg softPkg;
	private String componentType;

	/**
	 * The Constructor.
	 */
	public DefaultGeneratorPropertiesWizardPage() {
		super("codeGeneratorPage", "Setup Code Generation", DefaultGeneratorPropertiesWizardPage.TITLE_IMAGE);
		setDescription("Set configuration values of this implementation's code generation properties.");
		setPageComplete(false);
	}

	/**
	 * @since 7.0
	 */
	@Override
	public void configure(final SoftPkg softPkg, final Implementation spd, final ICodeGeneratorDescriptor desc, final ImplementationSettings implSettings,
		final String componentType) {
		this.softPkg = softPkg;
		this.implSettings = implSettings;
		this.codegen = desc;
		this.componentType = componentType;
		if (desc != null) {
			final ITemplateDesc[] temps = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry().findTemplatesByCodegen(desc.getId(), componentType);
			if (this.created) {
				this.templateViewer.setInput(temps);
			}

			final boolean selectDefault = (implSettings.getTemplate() == null) || (implSettings.getTemplate().length() == 0);
			// Select the appropriate template
			for (final ITemplateDesc temp : temps) {
				if ((selectDefault && !temp.notDefaultableGenerator()) || temp.getId().equals(implSettings.getTemplate())) {
					this.selectedTemplate = temp;
					if (this.created) {
						this.templateViewer.setSelection(new StructuredSelection(temp));
						this.templateViewer.getCombo().setToolTipText(temp.getDescription());
					}
					break;
				}
			}
		} else {
			this.templateViewer.setInput(new ITemplateDesc[0]);
			this.selectedTemplate = null;
			this.templateViewer.getCombo().setToolTipText("");
		}

		if ((this.implSettings.getProperties().size() == 0) && (this.selectedTemplate != null)) {
			for (final IPropertyDescriptor prop : this.selectedTemplate.getPropertyDescriptors()) {
				if (prop.isRequired() && !prop.isDeprecated()) {
					final Property p = CodegenFactory.eINSTANCE.createProperty();
					p.setId(prop.getKey());
					p.setValue(prop.getDefaultValue());
					this.implSettings.getProperties().add(p);
				}
			}
		}

		if (!this.context.getBindings().isEmpty()) {
			this.context.dispose();
		}

		if (this.created) {
			bind();
		}
		this.configured = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		if (visible) {
			setPageComplete(true);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createControl(final Composite parent) { // SUPPRESS CHECKSTYLE MethodLength
		final Composite client = new Composite(parent, SWT.NULL);
		client.setLayout(new GridLayout(2, false));

		Label label;

		final GridDataFactory labelFactory = GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.FILL);

		label = new Label(client, SWT.NULL);
		label.setText("Generator:");
		label.setLayoutData(labelFactory.create());
		this.generatorLabel = new Text(client, SWT.READ_ONLY | SWT.SINGLE | SWT.BORDER);
		this.generatorLabel.setEnabled(false);
		this.generatorLabel.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		label = new Label(client, SWT.NULL);
		label.setText("Template:");
		this.templateViewer = new ComboViewer(client, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY | SWT.DROP_DOWN);
		this.templateViewer.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		this.templateViewer.setContentProvider(new ArrayContentProvider());
		this.templateViewer.setLabelProvider(new LabelProvider() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public String getText(final Object element) {
				if (element instanceof ITemplateDesc) {
					return ((ITemplateDesc) element).getName();
				}
				return super.getText(element);
			}
		});
		this.templateViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				final ITemplateDesc desc = (ITemplateDesc) ((IStructuredSelection) event.getSelection()).getFirstElement();
				if (desc != null) {
					if (desc != DefaultGeneratorPropertiesWizardPage.this.selectedTemplate) {
						DefaultGeneratorPropertiesWizardPage.this.selectedTemplate = desc;

						// Remove the old templates properties
						final EList<Property> properties = DefaultGeneratorPropertiesWizardPage.this.implSettings.getProperties();
						if (properties.size() != 0) {
							properties.clear();
						}

						// Add the new templates properties
						for (final IPropertyDescriptor value : desc.getPropertyDescriptors()) {
							if (!value.isDeprecated()) {
								final Property p = CodegenFactory.eINSTANCE.createProperty();
								p.setId(value.getKey());
								p.setValue(value.getDefaultValue());
								properties.add(p);
							}
						}

						// Update the properties displayed
						DefaultGeneratorPropertiesWizardPage.this.propertiesViewer.setInput(desc.getPropertyDescriptors());
					}

					// Save the new template and update the tooltip
					DefaultGeneratorPropertiesWizardPage.this.implSettings.setTemplate(desc.getId());
					DefaultGeneratorPropertiesWizardPage.this.templateViewer.getCombo().setToolTipText(desc.getDescription());
				} else {
					DefaultGeneratorPropertiesWizardPage.this.implSettings.setTemplate(null);
				}
			}

		});

		label = new Label(client, SWT.NULL);
		label.setText("Output Directory:");
		this.outputDirText = new Text(client, SWT.BORDER);
		this.outputDirText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

		createExtraArea(client, labelFactory, ((GridLayout) client.getLayout()).numColumns);

		label = new Label(client, SWT.NULL);
		label.setText("Properties:");
		label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).create());
		final Composite tableComp = new Composite(client, SWT.NULL);
		final GridLayout layout = SWTUtil.TABLE_ENTRY_LAYOUT_FACTORY.create();
		tableComp.setLayout(layout);
		tableComp.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).grab(true, true).create());

		final Table table = new Table(tableComp, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(GridDataFactory.fillDefaults().span(1, 3).grab(true, true).create()); // SUPPRESS CHECKSTYLE
																									// MagicNumber
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		final TableLayout tableLayout = new TableLayout();
		tableLayout.addColumnData(new ColumnWeightData(40, 100, true)); // SUPPRESS CHECKSTYLE MagicNumber
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
							DefaultGeneratorPropertiesWizardPage.this.implSettings.getGeneratorId());
						final ITemplateDesc template = getTemplateDesc(generator);

						for (final IPropertyDescriptor propDesc : template.getPropertyDescriptors()) {
							if (propDesc.getKey().equals(((Property) element).getId())) {
								text = propDesc.getName();
								break;
							}
						}
					}
				}
				return text;
			};

			@Override
			public String getToolTipText(final Object element) {
				String text = "No description available for this property";
				if (element instanceof Property) {
					if (((Property) element).getId().length() != 0) {
						final ICodeGeneratorDescriptor generator = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen(
							DefaultGeneratorPropertiesWizardPage.this.implSettings.getGeneratorId());
						final ITemplateDesc template = getTemplateDesc(generator);

						for (final IPropertyDescriptor propDesc : template.getPropertyDescriptors()) {
							if (propDesc.getKey().equals(((Property) element).getId())) {
								text = propDesc.getDescription();
								break;
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
				return DefaultGeneratorPropertiesWizardPage.TOOLTIP_DELAY_MILLIS;
			}

			@Override
			public int getToolTipTimeDisplayed(final Object object) {
				return DefaultGeneratorPropertiesWizardPage.TOOLTIP_DISPLAY_TIME_MILLIS;
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
							DefaultGeneratorPropertiesWizardPage.this.implSettings.getGeneratorId());
						final ITemplateDesc template = getTemplateDesc(generator);

						for (final IPropertyDescriptor propDesc : template.getPropertyDescriptors()) {
							if (propDesc.getKey().equals(prop.getId())) {
								text = propDesc.getDefaultValue();
								for (final Property tempProp : DefaultGeneratorPropertiesWizardPage.this.implSettings.getProperties()) {
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
				return text;
			};
		});

		this.propertiesViewer.setContentProvider(new AdapterFactoryContentProvider(getAdapterFactory()));
		this.propertiesViewer.setComparator(createPropertiesViewerComparator());
		this.propertiesViewer.setFilters(createPropertiesViewerFilter());
		this.propertiesViewer.setColumnProperties(new String[] { CodegenPackage.Literals.PROPERTY__ID.getName(),
			CodegenPackage.Literals.PROPERTY__VALUE.getName() });

		final Button addButton = new Button(tableComp, SWT.PUSH);
		addButton.setText("Add...");
		addButton.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).create());
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleAddProperty();
			}
		});
		final Button editButton = new Button(tableComp, SWT.PUSH);
		editButton.setText("Edit");
		editButton.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).create());
		editButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleEditProperty();
			}
		});
		editButton.setEnabled(false);
		final Button removeButton = new Button(tableComp, SWT.PUSH);
		removeButton.setText("Remove");
		removeButton.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).create());
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleRemoveProperty();
			}
		});
		removeButton.setEnabled(!this.propertiesViewer.getSelection().isEmpty());
		this.propertiesViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				removeButton.setEnabled(!event.getSelection().isEmpty());
				editButton.setEnabled(!event.getSelection().isEmpty());
			}
		});

		if (this.configured) {
			bind();
		}
		this.created = true;

		setControl(client);
		setPageComplete(false);
	}

	/**
	 * This method is used to add extra Generator Settings information for
	 * subclasses.
	 * 
	 * @param parent the parent composite
	 * @param labelFactory the GridDataFactory for creating labels
	 * @param numColumns the number of columns for the current layout
	 */
	protected void createExtraArea(final Composite parent, final GridDataFactory labelFactory, final int numColumns) {
	}

	private void bind() {
		initFields();

		this.context.bindValue(ViewersObservables.observeSingleSelection(this.templateViewer),
			EMFObservables.observeValue(this.implSettings, CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__TEMPLATE), createTemplateTargetToModel(),
			createTemplateModelToTarget());

		this.context.bindValue(WidgetProperties.text(SWT.Modify).observe(this.outputDirText),
			EMFObservables.observeValue(this.implSettings, CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__OUTPUT_DIR),
			new UpdateValueStrategy().setAfterConvertValidator(new OutputDirectoryValidator(this.softPkg)), null);

		if ((this.implSettings.getOutputDir() == null) || "".equals(this.implSettings.getOutputDir().trim())) {
			setDefaults();
		} else {
			selectInitialTemplate(false);
		}

		this.propertiesViewer.setInput(this.implSettings);
		this.context.addValidationStatusProvider(new AllRequiredPropertiesValidator());

		this.support = WizardPageSupport.create(this, this.context);
	}

	private void initFields() {
		this.generatorLabel.setText(this.codegen.getName());
		final ITemplateDesc[] temps = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry().findTemplatesByCodegen(this.codegen.getId(),
			this.componentType);
		this.templateViewer.setInput(temps);
	}

	private void selectInitialTemplate(final boolean setDefault) {
		final ITemplateDesc[] temps = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry().findTemplatesByCodegen(this.codegen.getId(),
			this.componentType);
		final ITemplateDesc[] input = temps;
		String templateId = this.implSettings.getTemplate();
		// Use this to select the legacy code generator template
		if (setDefault && "src/".equals(templateId) && (this.implSettings.getProperties().size() > 0)) {
			templateId = this.implSettings.getGeneratorId();
		}
		if (templateId != null) {
			for (final ITemplateDesc temp : input) {
				if (temp.getId().equals(templateId)) {
					this.implSettings.setTemplate(templateId);
					this.templateViewer.setSelection(new StructuredSelection(temp));
					this.templateViewer.getCombo().setToolTipText(temp.getDescription());
					break;
				}
			}
		} else {
			for (final ITemplateDesc temp : input) {
				if (!temp.notDefaultableGenerator()) {
					this.implSettings.setTemplate(temp.getId());
					this.templateViewer.setSelection(new StructuredSelection(temp));
					this.templateViewer.getCombo().setToolTipText(temp.getDescription());
					break;
				}
			}
		}
	}

	private void setDefaults() {
		if (this.codegen != null) {
			if (this.generatorLabel != null) {
				this.generatorLabel.setText(this.codegen.getName());
			}
			if (this.templateViewer != null) {
				this.selectInitialTemplate(true);
			}
			this.setPageComplete(!this.selectedTemplate.hasSettings());

			this.implSettings.setOutputDir(CodegenFileHelper.createDefaultOutputDir(this.softPkg, this.codegen));
			if (this.outputDirText != null) {
				this.outputDirText.setText(this.implSettings.getOutputDir());
			}
			this.implSettings.setGeneratorId(this.codegen.getId());
		} else {
			if (this.generatorLabel != null) {
				this.generatorLabel.setText("");
			}
			if (this.templateViewer != null) {
				this.templateViewer.setInput(Collections.EMPTY_LIST);
				this.templateViewer.setSelection(new StructuredSelection());
				this.selectedTemplate = null;
			}

			if (this.outputDirText != null) {
				this.outputDirText.setText("");
			}
			this.implSettings.setGeneratorId(null);
		}
	}

	/**
	 * Handle remove property.
	 */
	protected void handleRemoveProperty() {
		this.implSettings.getProperties().remove(((IStructuredSelection) this.propertiesViewer.getSelection()).getFirstElement());
	}

	/**
	 * Handle edit property.
	 */
	protected void handleEditProperty() {
		final Property oldValue = (Property) ((IStructuredSelection) this.propertiesViewer.getSelection()).getFirstElement();
		final Property propNewValue = EcoreUtil.copy(oldValue);
		final PropertyDialog dialog = new PropertyDialog(getShell(), "Edit Property", propNewValue, this.getTemplateDesc(this.codegen));
		if (dialog.open() == Window.OK) {
			EcoreUtil.replace(this.implSettings, CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__PROPERTIES, oldValue, dialog.getValue());
		}
	}

	/**
	 * Handle add property.
	 */
	protected void handleAddProperty() {
		final PropertyDialog dialog = new PropertyDialog(getShell(), "Add Property", null, this.getTemplateDesc(this.codegen));
		if (dialog.open() == Window.OK) {
			this.implSettings.getProperties().add(dialog.getValue());
		}
	}

	/**
	 * Creates the properties viewer filter.
	 * 
	 * @return the viewer filter[]
	 */
	private ViewerFilter[] createPropertiesViewerFilter() {
		final ViewerFilter[] filters = new ViewerFilter[1];
		filters[0] = new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof IPropertyDescriptor) {
					return !((IPropertyDescriptor) element).isDeprecated();
				}
				return true;
			}

		};
		return filters;
	}

	/**
	 * Creates the properties viewer comparator.
	 * 
	 * @return the viewer comparator
	 */
	private ViewerComparator createPropertiesViewerComparator() {
		return new ViewerComparator();
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
			final WaveDevItemProviderAdapterFactoryAdapter adapter = new WaveDevItemProviderAdapterFactoryAdapter();
			adapter.setPropertyAdapter(new CustomPropertyItemProvider(adapter));
			this.adapterFactory.addAdapterFactory(adapter);
			this.adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
			this.adapterFactory.addAdapterFactory(new CodegenItemProviderAdapterFactory());
		}
		return this.adapterFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		if (this.support != null) {
			this.support.dispose();
		}
		if (this.adapterFactory != null) {
			this.adapterFactory.dispose();
		}
		if (this.context != null) {
			this.context.dispose();
		}
		super.dispose();
	}

	/**
	 * Gets the implSettings.
	 * 
	 * @return the implSettings
	 */
	@Override
	public ImplementationSettings getSettings() {
		return this.implSettings;
	}

	/**
	 * Set the implSettings.
	 * 
	 * @param tempImpl
	 * @since 3.0
	 */

	public void setSettings(final ImplementationSettings tempImpl) {
		this.implSettings = tempImpl;
	}

	/**
	 * @return
	 */
	private UpdateValueStrategy createTemplateModelToTarget() {
		final EMFUpdateValueStrategy strategy = new EMFUpdateValueStrategy();
		strategy.setConverter(new Converter(String.class, ITemplateDesc.class) {

			@Override
			public Object convert(final Object fromObject) {
				if (fromObject == null) {
					return null;
				}
				final String templateId = fromObject.toString();
				final ITemplateDesc template = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry().findTemplate(templateId);
				return template;
			}

		});
		return strategy;
	}

	/**
	 * @return
	 */
	private UpdateValueStrategy createTemplateTargetToModel() {
		final EMFEmptyStringToNullUpdateValueStrategy strategy = new EMFEmptyStringToNullUpdateValueStrategy();
		strategy.setConverter(new Converter(ITemplateDesc.class, String.class) {

			@Override
			public Object convert(final Object fromObject) {
				if (fromObject == null) {
					return null;
				}
				final ITemplateDesc desc = (ITemplateDesc) fromObject;
				return desc.getId();
			}

		});
		return strategy;
	}

	@Override
	public boolean canFlipToNextPage() {
		return this.canFlip && notLastPage();
	}

	/**
	 * Checks to see if this Wizard page is the last one in the wizard pages list.
	 * @return True if this page is not the last page
	 */
	private boolean notLastPage() {
		IWizardPage[] pages = getWizard().getPages();
		return pages[pages.length - 1] != this;
	}

	/**
	 * @since 3.0
	 */
	@Override
	public boolean canFinish() {
		return this.canFinish;
	}

	/**
	 * @since 3.0
	 */
	@Override
	public void setCanFlipToNextPage(final boolean canFlip) {
		this.canFlip = canFlip;
	}

	/**
	 * @since 3.0
	 */
	@Override
	public void setCanFinish(final boolean canFinish) {
		this.canFinish = canFinish;
	}

	private ITemplateDesc getTemplateDesc(final ICodeGeneratorDescriptor generator) {
		String templateId = this.implSettings.getTemplate();
		// If the templateId is null or old style, select the legacy code
		// generator template if the implementationSettings has existing props
		if (((templateId == null) || ("src/".equals(templateId))) && (this.implSettings.getProperties().size() > 0)) {
			templateId = this.implSettings.getGeneratorId();
		}
		ITemplateDesc template = null;
		ITemplateDesc genTemplate = null;
		final ITemplateDesc[] temps = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry().findTemplatesByCodegen(generator.getId());
		for (final ITemplateDesc temp : temps) {
			// Keep track of the default generator template
			if (temp.getId().equals(generator.getId())) {
				genTemplate = temp;
			}
			if (temp.getId().equals(templateId)) {
				template = temp;
				break;
			}
		}

		// If we didn't find the template for some reason, return the default
		// generator template
		return (template != null) ? template : genTemplate; // SUPPRESS CHECKSTYLE AvoidInline
	}

}
