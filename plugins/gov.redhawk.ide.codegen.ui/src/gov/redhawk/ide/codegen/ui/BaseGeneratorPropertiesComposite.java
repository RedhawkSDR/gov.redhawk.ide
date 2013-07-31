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
import gov.redhawk.ide.codegen.PortRepToGeneratorMap;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.ui.internal.GeneratorDialog;
import gov.redhawk.ide.codegen.ui.internal.PortGeneratorComposite;
import gov.redhawk.ui.editor.EMFTableViewerElementSelector;
import gov.redhawk.ui.parts.FormEntryBindingFactory;
import gov.redhawk.ui.util.EMFEmptyStringToNullUpdateValueStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import mil.jpeojtrs.sca.scd.Ports;
import mil.jpeojtrs.sca.scd.Provides;
import mil.jpeojtrs.sca.scd.Uses;
import mil.jpeojtrs.sca.spd.Descriptor;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.EMFUpdateValueStrategy;
import org.eclipse.emf.databinding.edit.EMFEditObservables;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
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
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
	private final int style;
	private FormEntry outputDirEntry;
	private ComboViewer generatorViewer;
	private ComboViewer templateViewer;
	private DataBindingContext context = new EMFDataBindingContext();
	private ImplementationSettings implSettings;
	private ITemplateDesc selectedTemplate;
	private EditingDomain domain;
	private PortGeneratorComposite portMapComposite;
	private Implementation impl;
	private EMFTableViewerElementSelector portMapSelector;

	/**
	 * @param parent
	 * @param style
	 * @param toolkit
	 */
	public BaseGeneratorPropertiesComposite(final Composite parent, final int style, final FormToolkit toolkit) {
		super(parent, style);

		this.style = style;
		this.toolkit = toolkit;

		setLayout(FormLayoutFactory.createSectionClientGridLayout(false, BaseGeneratorPropertiesComposite.NUM_COLUMNS));
	}

	protected void initialize() {
		createGeneratorEntry();

		createTemplateEntry();

		createOutputDirEntry();

		createPropertiesArea();

		createExtraArea(this, this.style, this.toolkit);

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
		this.generatorViewer.setInput(RedhawkCodegenActivator.getCodeGeneratorsRegistry().getCodegens());
		this.generatorViewer.addSelectionChangedListener(new ISelectionChangedListener() {

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

						// Change the enablement of the port map if the new
						// template supports it
						BaseGeneratorPropertiesComposite.this.portMapComposite.setEnabled(BaseGeneratorPropertiesComposite.this.getEnablePortMap());

						// Update the properties display and rebind
						templateSelected(desc);
					}
				}
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		if ((this.implSettings != null) && this.implSettings.eAdapters().contains(this.portMapSelector)) {
			this.implSettings.eAdapters().remove(this.portMapSelector);
		}
		this.context.dispose();
		super.dispose();
	}

	/**
	 * This method is used to add extra Generator Settings information for
	 * subclasses.
	 * 
	 * @param parent the parent composite
	 * @param style this composite's style
	 * @param toolkit the toolkit to use
	 */
	protected void createExtraArea(final Composite parent, final int style, final FormToolkit toolkit) {
		this.portMapComposite = new PortGeneratorComposite(parent, SWT.NONE, toolkit);
		this.portMapComposite.setLayoutData(GridDataFactory.fillDefaults().span(BaseGeneratorPropertiesComposite.NUM_COLUMNS, 1).grab(true, false).create());
		this.portMapComposite.getAddPropertyButton().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleAddPortMapping();
			}
		});

		this.portMapComposite.getEditPropertyButton().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleEditPortMapping();
			}
		});

		this.portMapComposite.getRemovePropertyButton().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleRemovePortMapping();
			}
		});
		this.portMapComposite.getPortMapViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(final SelectionChangedEvent event) {
				final boolean enabled = BaseGeneratorPropertiesComposite.this.portMapComposite.isEnabled();
				BaseGeneratorPropertiesComposite.this.portMapComposite.getRemovePropertyButton().setEnabled(enabled && !event.getSelection().isEmpty());
				BaseGeneratorPropertiesComposite.this.portMapComposite.getEditPropertyButton().setEnabled(enabled && !event.getSelection().isEmpty());
				BaseGeneratorPropertiesComposite.this.portMapComposite.getAddPropertyButton().setEnabled(
					enabled && BaseGeneratorPropertiesComposite.this.getUnmappedRepIds(null).size() > 0);
			}
		});
	}

	/**
	 * This returns the outputDirEntry field
	 * 
	 * @return the outputDirEntry field
	 * @since 7.0
	 */
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

		this.portMapComposite.setEnabled(this.getEnablePortMap());

		this.portMapComposite.getPortMapViewer().setInput(implSettings);
		if (this.portMapSelector == null) {
			this.portMapSelector = new EMFTableViewerElementSelector(this.portMapComposite.getPortMapViewer());
		}
		if (!this.implSettings.eAdapters().contains(this.portMapSelector)) {
			this.implSettings.eAdapters().add(this.portMapSelector);
		}
		this.createPropertyBinding();
		this.portMapComposite.getAddPropertyButton().setEnabled((getUnmappedRepIds(null).size() > 0) && (this.portMapComposite.isEnabled()));
	}

	private boolean getEnablePortMap() {
		return ((this.getSelectedTemplate() != null) && this.getSelectedTemplate().delegatePortGeneration());
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

	/**
	 * @return
	 */
	private UpdateValueStrategy createGeneratorTargetToModel() {
		final EMFEmptyStringToNullUpdateValueStrategy strategy = new EMFEmptyStringToNullUpdateValueStrategy();
		strategy.setConverter(new Converter(ICodeGeneratorDescriptor.class, String.class) {

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

	/**
	 * @return
	 */
	private UpdateValueStrategy createGeneratorModelToTarget() {
		final EMFUpdateValueStrategy strategy = new EMFUpdateValueStrategy();
		strategy.setConverter(new Converter(String.class, ICodeGeneratorDescriptor.class) {

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
	 * Handle add mapping.
	 */
	private void handleAddPortMapping() {
		final HashSet<String> repIds = getUnmappedRepIds(null);

		final GeneratorDialog dialog = new GeneratorDialog(getShell(), "Add Generator Mapping", null, repIds, this.impl.getProgrammingLanguage().getName());
		if (dialog.open() == Window.OK) {
			final EObject ref = dialog.getValue();
			this.domain.getCommandStack().execute(
				AddCommand.create(this.domain, this.implSettings, CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__PORT_GENERATORS, ref));
		}
	}

	/**
	 * Handle edit mapping.
	 */
	private void handleEditPortMapping() {
		final PortRepToGeneratorMap curProp = (PortRepToGeneratorMap) ((IStructuredSelection) this.portMapComposite.getPortMapViewer().getSelection()).getFirstElement();
		final HashSet<String> repIds = getUnmappedRepIds(curProp.getRepId());

		final GeneratorDialog dialog = new GeneratorDialog(getShell(), "Edit Generator Mapping", curProp, repIds, this.impl.getProgrammingLanguage().getName());
		if (dialog.open() == Window.OK) {
			final PortRepToGeneratorMap ref = (PortRepToGeneratorMap) dialog.getValue();
			this.domain.getCommandStack().execute(
				SetCommand.create(this.domain, curProp, CodegenPackage.Literals.PORT_REP_TO_GENERATOR_MAP__GENERATOR, ref.getGenerator()));
		}
	}

	/**
	 * Handle remove mapping.
	 */
	private void handleRemovePortMapping() {
		final PortRepToGeneratorMap curProp = (PortRepToGeneratorMap) ((IStructuredSelection) this.portMapComposite.getPortMapViewer().getSelection()).getFirstElement();
		this.domain.getCommandStack().execute(
			RemoveCommand.create(this.domain, this.implSettings, CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__PORT_GENERATORS, curProp));
	}

	/**
	 * This returns a set of repIds representing all the ports for the current
	 * implementation that do not have a generator assigned. If currentRep is
	 * not null, it adds it to the list.
	 * 
	 * @param currentRep the repId to add to the set, or null if none should be
	 *            added
	 * @return set of repIds without generators
	 */
	private HashSet<String> getUnmappedRepIds(final String currentRep) {
		final HashSet<String> repIds = new HashSet<String>();
		final Descriptor descriptor = ((SoftPkg) this.impl.eContainer()).getDescriptor();

		if ((descriptor != null) && (descriptor.getComponent() != null)) {
			final Ports ports = descriptor.getComponent().getComponentFeatures().getPorts();

			// Store the current RepIds
			for (final Provides p : ports.getProvides()) {
				repIds.add(p.getRepID());
			}
			for (final Uses u : ports.getUses()) {
				repIds.add(u.getRepID());
			}

			// filter out the used ones
			for (final PortRepToGeneratorMap r : this.implSettings.getPortGenerators()) {
				repIds.remove(r.getRepId());
			}

			// Add the passed in rep if necessary
			if (currentRep != null) {
				repIds.add(currentRep);
			}
		}

		return repIds;
	}

	public void setEditable(final boolean canEdit) {
		this.portMapComposite.getAddPropertyButton().setEnabled(canEdit && (getUnmappedRepIds(null).size() > 0) && this.portMapComposite.isEnabled());
		this.portMapComposite.setEditable(canEdit);
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
