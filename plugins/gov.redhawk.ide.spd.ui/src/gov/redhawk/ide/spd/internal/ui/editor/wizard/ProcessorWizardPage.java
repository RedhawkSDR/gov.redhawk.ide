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
package gov.redhawk.ide.spd.internal.ui.editor.wizard;

import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.IProcessor;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ui.util.EMFEmptyStringToNullUpdateValueStrategy;
import gov.redhawk.ui.validation.EmfValidationStatusProvider;

import java.util.List;

import mil.jpeojtrs.sca.spd.Processor;
import mil.jpeojtrs.sca.spd.SpdFactory;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.spd.registry.ProcessorRegistry;

import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.EMFObservables;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ProcessorWizardPage extends WizardPage {

	private static final String PAGE_NAME = "processorConfigPage";

	private Processor processor = SpdFactory.eINSTANCE.createProcessor();

	private final EMFDataBindingContext context = new EMFDataBindingContext();

	private WizardPageSupport pageSupport;

	private ComposedAdapterFactory adapterFactory;

	private final ImplementationSettings implSettings;

	private ComboViewer comboViewer;

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 * @since 2.0
	 */
	public ProcessorWizardPage(final ImplementationSettings implSettings) {
		super(ProcessorWizardPage.PAGE_NAME, "New Processor", null);
		this.implSettings = implSettings;
		this.setDescription("Set values of new Processor.");
		setPageComplete(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		super.dispose();
		this.pageSupport.dispose();
		this.context.dispose();
	}

	/**
	 * @return the processor
	 */
	public Processor getProcessor() {
		return this.processor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createControl(final Composite parent) {

		// Create an adapter factory that yields item providers.
		//
		this.adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);

		this.adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
		this.adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());

		Label label;
		final Composite client = new Composite(parent, SWT.NULL);
		client.setLayout(new GridLayout(2, false));

		label = new Label(client, SWT.NULL);
		label.setText("Name:");
		this.comboViewer = new ComboViewer(client, SWT.DROP_DOWN | SWT.BORDER);
		this.comboViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(1, 1).create());
		this.comboViewer.setContentProvider(new ArrayContentProvider());

		prepareProcessors();

		this.comboViewer.setComparator(new ViewerComparator());

		// Bind and validate
		this.context.bindValue(WidgetProperties.text().observe(this.comboViewer.getCombo()),
		        EMFObservables.observeValue(this.processor, SpdPackage.Literals.PROCESSOR__NAME), new EMFEmptyStringToNullUpdateValueStrategy(), null);
		this.context.addValidationStatusProvider(new EmfValidationStatusProvider(this.processor, this.context, this.adapterFactory));
		this.pageSupport = WizardPageSupport.create(this, this.context);
		this.setControl(client);
	}

	/**
	 * Sets the os.
	 * 
	 * @param os2 the os2
	 */
	public void setProcessor(final Processor processor2) {
		this.processor = EcoreUtil.copy(processor2);
		this.setTitle("Edit Processor");
		this.setDescription("Edit Processor Value");
	}

	/**
	 * @since 2.0
	 */
	private void prepareProcessors() {
		if (this.implSettings != null) {
			final ICodeGeneratorDescriptor codeGenDesc = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen(this.implSettings.getGeneratorId());
			final IProcessor[] processors = codeGenDesc.getProcessors();
			final List<String> tempProc = ProcessorRegistry.getProcessorNames();

			for (final IProcessor temp : processors) {
				if (!tempProc.contains(temp.getProcessorType())) {
					tempProc.add(temp.getProcessorType());
				}
			}
			this.comboViewer.setInput(tempProc);
		} else {
			this.comboViewer.setInput(ProcessorRegistry.getProcessorNames());
		}
	}
}
