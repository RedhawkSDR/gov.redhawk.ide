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
package gov.redhawk.ide.ui.tests.projectCreation.stubs;

import gov.redhawk.codegen.validation.OutputDirectoryValidator;
import gov.redhawk.ide.codegen.CodegenFactory;
import gov.redhawk.ide.codegen.CodegenPackage;
import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.IPropertyDescriptor;
import gov.redhawk.ide.codegen.ITemplateDesc;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.Property;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.ui.ICodegenWizardPage;
import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import gov.redhawk.ide.codegen.util.CodegenFileHelper;
import gov.redhawk.ide.spd.ui.wizard.ScaImplementationWizard2;
import gov.redhawk.ui.util.EMFEmptyStringToNullUpdateValueStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.observable.set.ISetChangeListener;
import org.eclipse.core.databinding.observable.set.SetChangeEvent;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.EMFObservables;
import org.eclipse.emf.databinding.EMFUpdateValueStrategy;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

/**
 * The Class JavaJetGeneratorPropertiesWizardPage.
 * 
 * @since 2.0
 */
public class StubGeneratorPropertiesWizardPage extends WizardPage implements ICodegenWizardPage {

	private static final int TOOLTIP_DELAY_MILLIS = 300;
	private static final int TOOLTIP_DISPLAY_TIME_MILLIS = 5000;

	private static final ImageDescriptor TITLE_IMAGE = null;

	private ImplementationSettings implSettings;

	private final EMFDataBindingContext context = new EMFDataBindingContext();

	private SoftPkg softPkg = null;

	private ICodeGeneratorDescriptor codegen = null;

	private WizardPageSupport support;

	private Text generatorLabel = null;

	private Text outputDirText = null;

	private Text packageNameText = null;

	private ComboViewer templateViewer;

	private HashMap<ImplementationSettings, WritableSet> setMap;

	private CheckboxTableViewer propertiesViewer;

	private boolean created;

	private boolean configured;

	private boolean canFlip = false;

	private boolean canFinish = false;

	private final ArrayList<Binding> bindings;

	private Property packageName = null;

	private ITemplateDesc selectedTemplate = null;

	private Binding propBinding;
	private String componentType;
	private Implementation impl;
	// TODO: Marry up with the selectedTemplate
	private String currentTemplate;
	
	private EContentAdapter templateListener = new EContentAdapter() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void notifyChanged(final Notification msg) {
			super.notifyChanged(msg);
			switch (msg.getFeatureID(ImplementationSettings.class)) {
			case CodegenPackage.IMPLEMENTATION_SETTINGS__TEMPLATE:
				if (msg.getNotifier() instanceof ImplementationSettings) {
					ImplementationSettings newSettings = (ImplementationSettings) msg.getNotifier();
					
					// If our current template is null and the template coming in is not then we just need to add pages.
					if (StubGeneratorPropertiesWizardPage.this.currentTemplate == null) {
						if (newSettings.getTemplate() != null) {
							StubGeneratorPropertiesWizardPage.this.currentTemplate = newSettings.getTemplate();
							addCustomPages();
						}
					} else {
						// If our current template is not null and the template coming in is null then we need to remove.
						if (newSettings.getTemplate() == null) {
							removeCustomPages();
							StubGeneratorPropertiesWizardPage.this.currentTemplate = null;
						} else if (!StubGeneratorPropertiesWizardPage.this.currentTemplate.equals(newSettings.getTemplate())) {
							// If our current template is not null and the template coming in is not null and they are not the same we need to remove and replace.
							removeCustomPages();
							StubGeneratorPropertiesWizardPage.this.currentTemplate = newSettings.getTemplate();
							addCustomPages();
						}
					}
				}
			break;
			}
		}
	};

	/**
	 * The Constructor.
	 * 
	 * @param desc the Code Generator descriptor for default implSettings
	 * @param implSettings the ImplementationSettings to store the values in
	 */
	public StubGeneratorPropertiesWizardPage() {
		super("codeGeneratorPage", "Setup Code Generation", StubGeneratorPropertiesWizardPage.TITLE_IMAGE);
		setDescription("Set configuration values of this implementation's code generation properties.");
		setPageComplete(false);
		this.bindings = new ArrayList<Binding>();
	}

	/**
	 * @since 8.1
	 */
	@Override
	public void configure(final SoftPkg softPkg, final Implementation impl, final ICodeGeneratorDescriptor desc, final ImplementationSettings implSettings,
	        final String componentType) {
		this.softPkg = softPkg;
		this.impl = impl;
		this.implSettings = implSettings;
		this.codegen = desc;
		this.componentType = componentType;
		final boolean selectDefault = (implSettings.getTemplate() == null) || (implSettings.getTemplate().length() == 0);
		boolean clearProps = false;

		Assert.isNotNull(desc);
		Assert.isNotNull(this.softPkg);

		final ITemplateDesc[] temps = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry().findTemplatesByCodegen(desc.getId(), componentType);
		Assert.isTrue(temps.length > 0);
		if (this.created) {
			this.templateViewer.setInput(temps);
		}

		// Select the appropriate template
		for (final ITemplateDesc temp : temps) {
			if ((selectDefault && !temp.notDefaultableGenerator()) || temp.getId().equals(implSettings.getTemplate())) {
				clearProps = ((this.selectedTemplate == null) || !temp.getId().equals(this.selectedTemplate.getId()));
				this.selectedTemplate = temp;

				break;
			}
		}

		// If we haven't selected a defaultable template, pick the first one
		if ((this.selectedTemplate == null)) {
			this.selectedTemplate = temps[0];
		}

		if (this.created) {
			this.templateViewer.setSelection(new StructuredSelection(this.selectedTemplate));
			this.templateViewer.getCombo().setToolTipText(this.selectedTemplate.getDescription());
		}

		final EList<Property> properties = implSettings.getProperties();
		if (clearProps || properties.size() == 0) {
			this.packageName = null;

			// Make a list of the property id's for this template
			final List<String> propList = new ArrayList<String>();
			if (this.selectedTemplate != null) {
				for (final IPropertyDescriptor value : this.selectedTemplate.getPropertyDescriptors()) {
					propList.add(value.getKey());
				}
			}

			// If we need to clear old properties, check to see if properties 
			// have already been set for the template, only remove the ones that
			// aren't valid anymore (aka. for a different generator)
			if (clearProps) {
				final List<Property> delProps = new ArrayList<Property>();
				for (final Property prop : properties) {
					// Remove the invalid property from the settings
					if (!propList.contains(prop.getId())) {
						delProps.add(prop);
						// Remove the valid property from the ones to add
					} else {
						if (CodegenUtil.JAVA_PACKAGE.equals(prop.getId())) {
							prop.setValue(StubGeneratorProperties.getPackage(softPkg, impl, implSettings));
							this.packageName = prop;
						}
						propList.remove(prop.getId());
					}
				}
				properties.removeAll(delProps);
			}

			// Check the template for properties to populate
			if (this.selectedTemplate != null) {
				for (final IPropertyDescriptor value : this.selectedTemplate.getPropertyDescriptors()) {
					// Don't override a valid property
					if (propList.contains(value.getKey()) && !value.isDeprecated()) {
						final Property p = CodegenFactory.eINSTANCE.createProperty();
						p.setId(value.getKey());
						if (StubGeneratorProperties.PROP_PACKAGE.equals(p.getId())) {
							p.setValue(StubGeneratorProperties.getPackage(softPkg, impl, implSettings));
							this.packageName = p;
						} else {
							p.setValue(value.getDefaultValue());
						}
						properties.add(p);
					}
				}
			}
		}

		if (!this.context.getBindings().isEmpty()) {
			this.context.dispose();
		}

		if (this.created) {
			bind();
		}
		
		if (!this.implSettings.eAdapters().contains(templateListener)) {
			this.implSettings.eAdapters().add(templateListener);
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
	public void createControl(final Composite parent) {
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
					if (desc != StubGeneratorPropertiesWizardPage.this.selectedTemplate) {
						StubGeneratorPropertiesWizardPage.this.selectedTemplate = desc;

						// Remove the old templates properties
						final EList<Property> properties = StubGeneratorPropertiesWizardPage.this.implSettings.getProperties();
						if (properties.size() != 0) {
							properties.clear();
						}

						StubGeneratorPropertiesWizardPage.this.packageName = null;

						final List<IPropertyDescriptor> propList = new ArrayList<IPropertyDescriptor>();

						// Add the new templates properties
						for (final IPropertyDescriptor value : desc.getPropertyDescriptors()) {
							final Property p = CodegenFactory.eINSTANCE.createProperty();
							if (!value.isDeprecated()) {
								p.setId(value.getKey());
								p.setValue(value.getDefaultValue());
								properties.add(p);
	
								if (!StubGeneratorProperties.PROP_PACKAGE.equals(value.getKey())) {
									propList.add(value);
								} else {
									p.setValue(StubGeneratorProperties.getPackage(StubGeneratorPropertiesWizardPage.this.softPkg,
									        StubGeneratorPropertiesWizardPage.this.impl,
									        StubGeneratorPropertiesWizardPage.this.implSettings));
									StubGeneratorPropertiesWizardPage.this.packageName = p;
								}
							}
						}

						// Update the properties displayed
						StubGeneratorPropertiesWizardPage.this.propertiesViewer.setInput(propList);

						// Unbind the old properties and bind the new ones
						if (StubGeneratorPropertiesWizardPage.this.propBinding != null) {
							StubGeneratorPropertiesWizardPage.this.bindings.remove(StubGeneratorPropertiesWizardPage.this.propBinding);
						}
						StubGeneratorPropertiesWizardPage.this.propBinding = createPropertyBinding();
						StubGeneratorPropertiesWizardPage.this.bindings.add(StubGeneratorPropertiesWizardPage.this.propBinding);
					}

					// Save the new template and update the tooltip
					StubGeneratorPropertiesWizardPage.this.implSettings.setTemplate(desc.getId());
					StubGeneratorPropertiesWizardPage.this.templateViewer.getCombo().setToolTipText(desc.getDescription());
				} else {
					StubGeneratorPropertiesWizardPage.this.implSettings.setTemplate(null);
				}
			}
		});

		label = new Label(client, SWT.NULL);
		label.setText("Output Directory:");
		this.outputDirText = new Text(client, SWT.BORDER);
		this.outputDirText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

		label = new Label(client, SWT.NULL);
		label.setText("Package:");
		label.setLayoutData(labelFactory.create());
		this.packageNameText = new Text(client, SWT.SINGLE | SWT.BORDER);
		this.packageNameText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		createExtraArea(client, labelFactory, ((GridLayout) client.getLayout()).numColumns);

		label = new Label(client, SWT.NULL);
		label.setText("Properties:");
		label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).create());

		this.propertiesViewer = new CheckboxTableViewer(new Table(client, SWT.CHECK | SWT.READ_ONLY | SWT.BORDER));
		ColumnViewerToolTipSupport.enableFor(this.propertiesViewer);
		this.propertiesViewer.setContentProvider(new ArrayContentProvider());

		final CellLabelProvider labelProvider = new CellLabelProvider() {

			public String getText(final Object element) {
				String text = "";
				if (element instanceof IPropertyDescriptor) {
					if (((IPropertyDescriptor) element).getName().length() != 0) {
						text = ((IPropertyDescriptor) element).getName();
					} else {
						text = ((IPropertyDescriptor) element).getKey();
					}
				}
				return text;
			}

			@Override
			public String getToolTipText(final Object element) {
				String text = "No description available for this property";
				if (element instanceof IPropertyDescriptor) {
					final String desc = ((IPropertyDescriptor) element).getDescription();
					if (desc != null && desc.length() != 0) {
						text = desc;
					}
				}
				return text;
			}

			@Override
			public Point getToolTipShift(final Object object) {
				return new Point(5, 5); // SUPPRESS CHECKSTYLE MagicNumber
			}

			@Override
			public int getToolTipDisplayDelayTime(final Object object) {
				return StubGeneratorPropertiesWizardPage.TOOLTIP_DELAY_MILLIS;
			}

			@Override
			public int getToolTipTimeDisplayed(final Object object) {
				return StubGeneratorPropertiesWizardPage.TOOLTIP_DISPLAY_TIME_MILLIS;
			}

			@Override
			public void update(final ViewerCell cell) {
				cell.setText(getText(cell.getElement()));
			}
		};

		this.propertiesViewer.setLabelProvider(labelProvider);
		this.propertiesViewer.setFilters(createPropertiesViewerFilter());
		this.propertiesViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().span(1, 2).grab(false, true).create());

		if (this.configured) {
			bind();
		}
		this.created = true;

		setControl(client);
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
	 * This method is used to add extra Generator Settings information for
	 * subclasses.
	 * 
	 * @param parent the parent composite
	 * @param labelFactory the GridDataFactory for creating labels
	 * @param numColumns the number of columns for the current layout
	 * @since 2.1
	 */
	protected void createExtraArea(final Composite parent, final GridDataFactory labelFactory, final int numColumns) {
	}

	private void bind() {
		for (final Binding b : this.bindings) {
			this.context.removeBinding(b);
		}

		this.support = WizardPageSupport.create(this, this.context);

		initFields();

		this.bindings.add(this.context.bindValue(ViewersObservables.observeSingleSelection(this.templateViewer),
		        EMFObservables.observeValue(this.implSettings, CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__TEMPLATE),
		        createTemplateTargetToModel(),
		        createTemplateModelToTarget()));

		this.bindings.add(this.context.bindValue(WidgetProperties.text(SWT.Modify).observe(this.outputDirText),
		        EMFObservables.observeValue(this.implSettings, CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__OUTPUT_DIR),
		        new UpdateValueStrategy().setAfterConvertValidator(new OutputDirectoryValidator(this.softPkg)),
		        null));

		this.bindings.add(this.context.bindValue(WidgetProperties.text(SWT.Modify).observe(this.packageNameText),
		        EMFObservables.observeValue(StubGeneratorPropertiesWizardPage.this.packageName, CodegenPackage.Literals.PROPERTY__VALUE),
		        new UpdateValueStrategy().setAfterConvertValidator(new StubPackageNameValidator()),
		        null));

		if ((this.implSettings.getOutputDir() == null) || "".equals(this.implSettings.getOutputDir().trim())) {
			setDefaults();
		} else {
			selectInitialTemplate(false);
		}

		final List<IPropertyDescriptor> propList = new ArrayList<IPropertyDescriptor>();
		for (final IPropertyDescriptor prop : this.selectedTemplate.getPropertyDescriptors()) {
			if (!"java_package".equals(prop.getKey())) {
				propList.add(prop);
			}
		}

		this.propertiesViewer.setInput(propList);
		this.propBinding = createPropertyBinding();
		this.bindings.add(this.propBinding);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		
		removeCustomPages();
		
		if (this.implSettings != null && this.implSettings.eAdapters().contains(templateListener)) {
			this.implSettings.eAdapters().remove(templateListener);
		}
		
		if (this.support != null) {
			this.support.dispose();
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
	 * @since 2.1
	 */
	protected EMFDataBindingContext getContext() {
		return this.context;
	}

	/**
	 * @since 2.1
	 */
	protected ICodeGeneratorDescriptor getCodegen() {
		return this.codegen;
	}

	/**
	 * @since 2.1
	 */
	protected WizardPageSupport getSupport() {
		return this.support;
	}

	/**
	 * @since 2.1
	 */
	protected Text getOutputDirText() {
		return this.outputDirText;
	}

	private void initFields() {
		this.generatorLabel.setText(this.codegen.getName());
		final ITemplateDesc[] temps = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry().findTemplatesByCodegen(this.codegen.getId(),
		        this.componentType);
		this.templateViewer.setInput(temps);
		if (this.packageName != null) {
			this.packageNameText.setText(this.packageName.getValue());
		}
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
			if (this.packageNameText != null) {
				if ("".equals(this.packageName.getValue())) {
					this.packageName.setValue(StubGeneratorProperties.getPackage(this.softPkg, this.impl, this.implSettings));
					this.packageNameText.setText(this.packageName.getValue());
				}
			}
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
			if (this.packageNameText != null) {
				this.packageNameText.setText("");
			}
		}
	}

	/**
	 * Creates the property binding.
	 * 
	 * @since 8.0
	 */
	protected Binding createPropertyBinding() {
		if (this.setMap == null) {
			this.setMap = new HashMap<ImplementationSettings, WritableSet>();
		}

		final WritableSet mySet = new WritableSet();
		for (final IPropertyDescriptor prop : this.selectedTemplate.getPropertyDescriptors()) {
			// Initialize to the default value of the property
			boolean val = "TRUE".equalsIgnoreCase(prop.getDefaultValue());

			// Check the ImplementationSettings for the current value of the property
			for (final Property settingsProp : this.implSettings.getProperties()) {
				if (settingsProp.getId().equals(prop.getKey())) {
					val = "TRUE".equalsIgnoreCase(settingsProp.getValue());
					break;
				}
			}
			if (val) {
				mySet.add(prop);
			}
		}
		mySet.addSetChangeListener(new ISetChangeListener() {
			@Override
			public void handleSetChange(final SetChangeEvent event) {
				final EList<Property> properties = StubGeneratorPropertiesWizardPage.this.implSettings.getProperties();
				for (final Object obj : event.diff.getRemovals()) {
					final IPropertyDescriptor cp = (IPropertyDescriptor) obj;
					for (final Property p : properties) {
						if (p.getId().equals(cp.getKey())) {
							p.setValue("FALSE");
							break;
						}
					}
				}
				for (final Object obj : event.diff.getAdditions()) {
					final IPropertyDescriptor cp = (IPropertyDescriptor) obj;
					for (final Property p : properties) {
						if (p.getId().equals(cp.getKey())) {
							p.setValue("TRUE");
							break;
						}
					}
				}
			}

		});

		this.setMap.put(this.implSettings, mySet);

		return this.context.bindSet(ViewersObservables.observeCheckedElements(this.propertiesViewer, IPropertyDescriptor.class),
		        this.setMap.get(this.implSettings),
		        null,
		        null);
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
		return this.canFlip && this.isPageComplete() && notLastPage();
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
	

	private void addCustomPages() {
		if (this.getWizard() instanceof ScaImplementationWizard2) {
			ICodegenWizardPage[] codeGenTemplatePages = RedhawkCodegenUiActivator.getCodeGeneratorsTemplateRegistry().findPageByGeneratorId(this.currentTemplate);
			((ScaImplementationWizard2) this.getWizard()).addTemplatePages(this, codeGenTemplatePages);
		}
		// Otherwise assume the Wizard is taking care of this itself.
	}
	
	private void removeCustomPages() {
		if (this.currentTemplate == null || "".equals(this.currentTemplate)) {
			return;
		}
		
		ICodegenWizardPage[] codeGenTemplatePages = RedhawkCodegenUiActivator.getCodeGeneratorsTemplateRegistry().findPageByGeneratorId(this.currentTemplate);
		((ScaImplementationWizard2) this.getWizard()).removeTemplatePages(this, codeGenTemplatePages);
		
	}
}
