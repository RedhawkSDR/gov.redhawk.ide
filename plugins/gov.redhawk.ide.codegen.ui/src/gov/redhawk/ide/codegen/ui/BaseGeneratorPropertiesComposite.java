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

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.common.ui.parts.FormEntry;
import gov.redhawk.ide.codegen.CodegenPackage;
import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.ITemplateDesc;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ui.parts.FormEntryBindingFactory;
import gov.redhawk.ui.util.EMFEmptyStringToNullUpdateValueStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mil.jpeojtrs.sca.spd.Implementation;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.EMFUpdateValueStrategy;
import org.eclipse.emf.databinding.edit.EMFEditObservables;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @since 6.0
 */
public abstract class BaseGeneratorPropertiesComposite extends Composite implements ICodegenComposite {

	private static final int NUM_COLUMNS = 3;

	private final FormToolkit toolkit;
	private FormEntry outputDirEntry;
	private ComboViewer generatorViewer;
	private ComboViewer templateViewer;
	private DataBindingContext context = new EMFDataBindingContext();
	private ImplementationSettings implSettings;
	private ITemplateDesc selectedTemplate;
	private EditingDomain domain;
	private Implementation impl;

	public BaseGeneratorPropertiesComposite(final Composite parent, final int style, final FormToolkit toolkit) {
		super(parent, style);

		this.toolkit = toolkit;

		setLayout(FormLayoutFactory.createSectionClientGridLayout(false, BaseGeneratorPropertiesComposite.NUM_COLUMNS));
	}

	protected void initialize() {
		createGeneratorEntry();

		createTemplateEntry();

		createOutputDirEntry();

		createPropertiesArea();

		this.toolkit.paintBordersFor(this);
	}

	protected FormToolkit getToolkit() {
		return this.toolkit;
	}

	protected ImplementationSettings getImplSettings() {
		return this.implSettings;
	}

	protected EditingDomain getEditingDomain() {
		return this.domain;
	}

	public DataBindingContext getContext() {
		return this.context;
	}

	/**
	 * Creates the generator entry.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createGeneratorEntry() {
		final Label label = this.toolkit.createLabel(this, "Generator:");
		label.setForeground(this.toolkit.getColors().getColor(IFormColors.TITLE));
		this.generatorViewer = new ComboViewer(this, SWT.SINGLE | SWT.READ_ONLY | SWT.DROP_DOWN);
		this.generatorViewer.getControl().addListener(SWT.MouseVerticalWheel, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				event.doit = false;
			}
		});
		this.generatorViewer.setContentProvider(new ArrayContentProvider());
		this.generatorViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				final ICodeGeneratorDescriptor desc = (ICodeGeneratorDescriptor) element;
				return desc.getName();
			}
		});
		this.generatorViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().span(2, 1).grab(true, false).create());
		this.generatorViewer.setInput(getCodegens());
		this.generatorViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				final ICodeGeneratorDescriptor desc = (ICodeGeneratorDescriptor) ((IStructuredSelection) event.getSelection()).getFirstElement();
				if (desc == null) {
					BaseGeneratorPropertiesComposite.this.templateViewer.setInput(Collections.EMPTY_LIST);
				} else {
					ITemplateDesc[] templates = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry().findTemplatesByCodegen(desc.getId());
					BaseGeneratorPropertiesComposite.this.templateViewer.setInput(templates);
				}
			}

		});
	}

	/**
	 * @since 10.1
	 */
	protected ICodeGeneratorDescriptor[] getCodegens() {
		return RedhawkCodegenActivator.getCodeGeneratorsRegistry().getCodegens();
	}

	/**
	 * Creates the output dir entry.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createOutputDirEntry() {
		this.outputDirEntry = new FormEntry(this, this.toolkit, "Output Dir:", SWT.SINGLE, "Browse...", false);
		this.outputDirEntry.getText().setToolTipText("Directory where generated code will be created.");
	}

	/**
	 * Creates the template entry.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createTemplateEntry() {
		final Label label = this.toolkit.createLabel(this, "Template:");
		label.setForeground(this.toolkit.getColors().getColor(IFormColors.TITLE));
		label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).create());
		this.templateViewer = new ComboViewer(this, SWT.READ_ONLY | SWT.SINGLE | SWT.DROP_DOWN);
		this.templateViewer.getControl().addListener(SWT.MouseVerticalWheel, new Listener() {

			@Override
			public void handleEvent(Event event) {
				event.doit = false;
			}

		});
		this.templateViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().span(2, 1).grab(true, false).create());
		this.templateViewer.getControl().setToolTipText("Template for the code generator");
		this.templateViewer.setContentProvider(new ArrayContentProvider());
		this.templateViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				final ITemplateDesc desc = (ITemplateDesc) element;
				return desc.getName();
			}
		});

		this.templateViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				final ITemplateDesc desc = (ITemplateDesc) ((IStructuredSelection) event.getSelection()).getFirstElement();
				final EditingDomain dom = BaseGeneratorPropertiesComposite.this.domain;
				if ((dom != null) && (desc != null) && (desc != BaseGeneratorPropertiesComposite.this.selectedTemplate)) {
					// Save the selected Template and update the ImplementationSettings
					BaseGeneratorPropertiesComposite.this.selectedTemplate = desc;

					// Check if the template has actually changed
					// - this gets called when selecting implementations in the
					//   implementation list and we don't want change properties
					// - this gets called when you actually click the dropdown,
					//   here we actually want to change the properties
					if (!desc.getId().equals(BaseGeneratorPropertiesComposite.this.implSettings.getTemplate())) {
						final Command command = SetCommand.create(dom, BaseGeneratorPropertiesComposite.this.implSettings,
							CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__TEMPLATE, desc.getId());
						dom.getCommandStack().execute(command);

						// Update the properties display and rebind
						templateSelected(desc);
					}
				}
			}
		});
	}

	@Override
	public void dispose() {
		this.context.dispose();
		super.dispose();
	}

	/**
	 * This returns the outputDirEntry field
	 * 
	 * @return the outputDirEntry field
	 * @since 7.0
	 */
	@Override
	public FormEntry getOutputDirEntry() {
		return this.outputDirEntry;
	}

	/**
	 * This returns the selected generator.
	 * 
	 * @return the currently selected code generator descriptor
	 */
	public ICodeGeneratorDescriptor getSelectedGenerator() {
		return (ICodeGeneratorDescriptor) ((IStructuredSelection) this.generatorViewer.getSelection()).getFirstElement();
	}

	/**
	 * This returns the selected template.
	 * 
	 * @return the currently selected template descriptor
	 */
	public ITemplateDesc getSelectedTemplate() {
		return (ITemplateDesc) ((IStructuredSelection) this.templateViewer.getSelection()).getFirstElement();
	}

	@Override
	public void bind(final ArrayList<Binding> bindList, final EditingDomain editingDomain, final DataBindingContext context, final Implementation impl,
		final ImplementationSettings implSettings) {
		this.impl = impl;
		this.implSettings = implSettings;
		this.context = context;
		this.domain = editingDomain;

		preBind(this.impl, this.implSettings, bindList);

		bindList.add(context.bindValue(ViewersObservables.observeSingleSelection(this.generatorViewer),
			EMFEditObservables.observeValue(editingDomain, implSettings, CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__GENERATOR_ID),
			createGeneratorTargetToModel(), createGeneratorModelToTarget()));
		bindList.add(FormEntryBindingFactory.bind(context, this.outputDirEntry, editingDomain, CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__OUTPUT_DIR,
			implSettings, null, null));
		bindList.add(context.bindValue(ViewersObservables.observeSingleSelection(this.templateViewer),
			EMFEditObservables.observeValue(editingDomain, implSettings, CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__TEMPLATE),
			createTemplateTargetToModel(), createTemplateModelToTarget()));

		this.createPropertyBinding();
	}

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

	private UpdateValueStrategy createGeneratorTargetToModel() {
		final EMFEmptyStringToNullUpdateValueStrategy strategy = new EMFEmptyStringToNullUpdateValueStrategy();
		strategy.setConverter(new Converter(ICodeGeneratorDescriptor.class, String.class) {

			@Override
			public Object convert(final Object fromObject) {
				if (fromObject == null) {
					return null;
				}
				final ICodeGeneratorDescriptor desc = (ICodeGeneratorDescriptor) fromObject;
				return desc.getId();
			}

		});
		return strategy;
	}

	private UpdateValueStrategy createGeneratorModelToTarget() {
		final EMFUpdateValueStrategy strategy = new EMFUpdateValueStrategy();
		strategy.setConverter(new Converter(String.class, ICodeGeneratorDescriptor.class) {

			@Override
			public Object convert(final Object fromObject) {
				if (fromObject == null) {
					return null;
				}
				final String generatorId = fromObject.toString();
				final ICodeGeneratorDescriptor generator = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen(generatorId);
				return generator;
			}

		});
		return strategy;
	}

	/**
	 * This returns the template specified in the ImplementationSettings or the
	 * default one that corresponds to the generator passed in
	 * 
	 * @param generator the generator to get templates for
	 * @return the specified template or a default one if it's not found
	 */
	protected ITemplateDesc getTemplateDesc(final ICodeGeneratorDescriptor generator) {
		if (generator == null) {
			return null;
		}
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

	/**
	 * This is called after a new template is selected that doesn't match the
	 * previously selected one. When this is called, the stored template has
	 * been updated.
	 * 
	 * @param desc the newly selected template descriptor
	 */
	protected abstract void templateSelected(ITemplateDesc desc);

	/**
	 * Creates the properties entry.
	 */
	protected abstract void createPropertiesArea();

	/**
	 * This is called when bind is called on the composite, but before any
	 * bindings are created.
	 * 
	 * @param impl the current implementation
	 * @param implSettings the current implementation settings
	 * @param bindList the binding list used by the bind() method
	 */
	protected abstract void preBind(final Implementation impl, final ImplementationSettings implSettings, final List<Binding> bindList);

	/**
	 * Creates the property binding.
	 */
	protected abstract void createPropertyBinding();

}
