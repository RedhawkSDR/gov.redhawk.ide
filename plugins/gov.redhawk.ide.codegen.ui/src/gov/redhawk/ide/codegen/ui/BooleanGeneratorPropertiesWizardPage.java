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
import gov.redhawk.ide.codegen.util.CodegenFileHelper;
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
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.EMFObservables;
import org.eclipse.emf.databinding.EMFUpdateValueStrategy;
import org.eclipse.jface.databinding.swt.SWTObservables;
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
 * The Class CodeGneratorWizardPage.
 * 
 * @since 2.0
 */
public class BooleanGeneratorPropertiesWizardPage extends WizardPage implements ICodegenWizardPage {

	private static final int TOOLTIP_DELAY_MILLIS = 300;
	private static final int TOOLTIP_DISPLAY_TIME_MILLIS = 5000;

	private static final ImageDescriptor TITLE_IMAGE = null;

	private ImplementationSettings implSettings;

	private final EMFDataBindingContext context = new EMFDataBindingContext();

	private ICodeGeneratorDescriptor codegen = null;

	private WizardPageSupport support;

	private Text generatorLabel = null;

	private Text outputDirText = null;

	private ComboViewer templateViewer;

	private HashMap<ImplementationSettings, WritableSet> setMap;

	private CheckboxTableViewer propertiesViewer;

	private boolean created;

	private boolean configured;

	private boolean canFlip = false;

	private boolean canFinish = false;

	private final ArrayList<Binding> bindings;

	private ITemplateDesc selectedTemplate = null;

	private Binding propBinding;
	private SoftPkg softPkg;
	private String componentType;

	/**
	 * The Constructor.
	 * 
	 * @param desc the Code Generator descriptor for default implSettings
	 * @param implSettings the ImplementationSettings to store the values in
	 */
	public BooleanGeneratorPropertiesWizardPage() {
		super("codeGeneratorPage", "Setup Code Generation", BooleanGeneratorPropertiesWizardPage.TITLE_IMAGE);
		setDescription("Set configuration values of this implementation's code generation properties.");
		setPageComplete(false);
		this.bindings = new ArrayList<Binding>();
	}

	/**
	 * @since 7.0
	 */
	public void configure(final SoftPkg softPkg, final Implementation impl, final ICodeGeneratorDescriptor desc, final ImplementationSettings implSettings,
	        final String componentType) {
		this.softPkg = softPkg;
		this.implSettings = implSettings;
		this.codegen = desc;
		this.componentType = componentType;
		final boolean selectDefault = (implSettings.getTemplate() == null) || (implSettings.getTemplate().length() == 0);
		boolean clearProps = false;

		if (desc != null) {
			final ITemplateDesc[] temps = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry().findTemplatesByCodegen(desc.getId(), componentType);
			if (this.created) {
				this.templateViewer.setInput(temps);
			}

			// Select the appropriate template
			for (final ITemplateDesc temp : temps) {
				if ((selectDefault && !temp.notDefaultableGenerator()) || temp.getId().equals(implSettings.getTemplate())) {
					clearProps = ((this.selectedTemplate == null) || !temp.getId().equals(this.selectedTemplate.getId()));
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

		final EList<Property> properties = implSettings.getProperties();
		if (clearProps || properties.size() == 0) {
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
						p.setValue(value.getDefaultValue());
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
			public void selectionChanged(final SelectionChangedEvent event) {
				final ITemplateDesc desc = (ITemplateDesc) ((IStructuredSelection) event.getSelection()).getFirstElement();
				if (desc != null) {
					if (desc != BooleanGeneratorPropertiesWizardPage.this.selectedTemplate) {
						BooleanGeneratorPropertiesWizardPage.this.selectedTemplate = desc;

						// Remove the old templates properties
						final EList<Property> properties = BooleanGeneratorPropertiesWizardPage.this.implSettings.getProperties();
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
						BooleanGeneratorPropertiesWizardPage.this.propertiesViewer.setInput(desc.getPropertyDescriptors());
						
						// Unbind the old properties and bind the new ones
						if (BooleanGeneratorPropertiesWizardPage.this.propBinding != null) {
							BooleanGeneratorPropertiesWizardPage.this.bindings.remove(BooleanGeneratorPropertiesWizardPage.this.propBinding);
						}
						BooleanGeneratorPropertiesWizardPage.this.propBinding = createPropertyBinding();
						BooleanGeneratorPropertiesWizardPage.this.bindings.add(BooleanGeneratorPropertiesWizardPage.this.propBinding);
					}

					// Save the new template and update the tooltip
					BooleanGeneratorPropertiesWizardPage.this.implSettings.setTemplate(desc.getId());
					BooleanGeneratorPropertiesWizardPage.this.templateViewer.getCombo().setToolTipText(desc.getDescription());
				} else {
					BooleanGeneratorPropertiesWizardPage.this.implSettings.setTemplate(null);
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

		this.propertiesViewer = new CheckboxTableViewer(new Table(client, SWT.CHECK | SWT.READ_ONLY | SWT.BORDER));
		ColumnViewerToolTipSupport.enableFor(this.propertiesViewer);
		this.propertiesViewer.setContentProvider(new ArrayContentProvider());
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
		this.propertiesViewer.setFilters(filters);
		
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
					if (((IPropertyDescriptor) element).getDescription().length() != 0) {
						text = ((IPropertyDescriptor) element).getDescription();
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
				return BooleanGeneratorPropertiesWizardPage.TOOLTIP_DELAY_MILLIS;
			}

			@Override
			public int getToolTipTimeDisplayed(final Object object) {
				return BooleanGeneratorPropertiesWizardPage.TOOLTIP_DISPLAY_TIME_MILLIS;
			}

			@Override
			public void update(final ViewerCell cell) {
				cell.setText(getText(cell.getElement()));
			}
		};

		this.propertiesViewer.setLabelProvider(labelProvider);
		this.propertiesViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().span(1, 2).grab(false, true).create());

		if (this.configured) {
			bind();
		}
		this.created = true;

		setControl(client);
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

		initFields();

		this.bindings.add(this.context.bindValue(ViewersObservables.observeSingleSelection(this.templateViewer),
		        EMFObservables.observeValue(this.implSettings, CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__TEMPLATE),
		        createTemplateTargetToModel(),
		        createTemplateModelToTarget()));

		this.bindings.add(this.context.bindValue(SWTObservables.observeText(this.outputDirText, SWT.Modify),
		        EMFObservables.observeValue(this.implSettings, CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__OUTPUT_DIR),
		        new UpdateValueStrategy().setAfterConvertValidator(new OutputDirectoryValidator(this.softPkg)),
		        null));

		if (this.implSettings.getOutputDir() == null || this.implSettings.getOutputDir().trim().equals("")) {
			setDefaults();
		} else {
			selectInitialTemplate(false);
		}

		this.propertiesViewer.setInput(this.selectedTemplate.getPropertyDescriptors());
		this.propBinding = createPropertyBinding();
		this.bindings.add(this.propBinding);

		this.support = WizardPageSupport.create(this, this.context);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
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
	public ImplementationSettings getSettings() {
		return this.implSettings;
	}

	/**
	 * Set the implSettings.
	 * 
	 * @param implSettings
	 * @since 3.0
	 */
	public void setSettings(final ImplementationSettings settings) {
		this.implSettings = settings;
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
			if (this.selectedTemplate != null) {
				this.setPageComplete(!this.selectedTemplate.hasSettings());
			} else {
				this.setPageComplete(true);
			}

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
	 * Creates the property binding.
	 * 
	 * @since 3.0
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
			public void handleSetChange(final SetChangeEvent event) {
				final EList<Property> properties = BooleanGeneratorPropertiesWizardPage.this.implSettings.getProperties();
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
		return this.canFlip && this.isPageComplete();
	}

	/**
	 * @since 3.0
	 */
	public boolean canFinish() {
		return this.canFinish;
	}

	/**
	 * @since 3.0
	 */
	public void setCanFlipToNextPage(final boolean canFlip) {
		this.canFlip = canFlip;
	}

	/**
	 * @since 3.0
	 */
	public void setCanFinish(final boolean canFinish) {
		this.canFinish = canFinish;
	}

}
