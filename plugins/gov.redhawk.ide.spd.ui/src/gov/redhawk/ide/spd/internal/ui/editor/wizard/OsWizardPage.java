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
import gov.redhawk.ide.codegen.IOperatingSystem;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ui.util.EMFEmptyStringToNullUpdateValueStrategy;
import gov.redhawk.ui.validation.EmfValidationStatusProvider;

import java.util.List;

import mil.jpeojtrs.sca.spd.Os;
import mil.jpeojtrs.sca.spd.SpdFactory;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.spd.registry.OsRegistry;

import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.EMFObservables;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * The Class OsWizardPage.
 */
public class OsWizardPage extends WizardPage {

	private static final String PAGE_NAME = "osConfigPage";

	private Os os = SpdFactory.eINSTANCE.createOs();

	private final EMFDataBindingContext context = new EMFDataBindingContext();

	private WizardPageSupport pageSupport;

	private ComposedAdapterFactory adapterFactory;

	private ComboViewer comboViewer;

	private final ImplementationSettings implSettings;

	/**
	 * @param implementationSettings
	 * @param pageName
	 * @param title
	 * @param titleImage
	 * @since 2.0
	 */
	public OsWizardPage(final ImplementationSettings implementationSettings) {
		super(OsWizardPage.PAGE_NAME, "New OS", null);
		this.implSettings = implementationSettings;
		this.setDescription("Set values of new OS.");
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
		this.adapterFactory.dispose();
	}

	/**
	 * @return the os
	 */
	public Os getOs() {
		return this.os;
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
		Text text;

		final Composite client = new Composite(parent, SWT.NULL);
		client.setLayout(new GridLayout(2, false));

		label = new Label(client, SWT.NULL);
		label.setText("Name:");
		this.comboViewer = new ComboViewer(client, SWT.DROP_DOWN | SWT.BORDER);
		this.comboViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(1, 1).create());
		this.comboViewer.setContentProvider(new ArrayContentProvider());

		prepareOperatingSystems();

		this.comboViewer.setComparator(new ViewerComparator());

		// Bind and validate
		this.context.bindValue(SWTObservables.observeText(this.comboViewer.getCombo()), EMFObservables.observeValue(this.os, SpdPackage.Literals.OS__NAME),
		        new EMFEmptyStringToNullUpdateValueStrategy(), null);

		label = new Label(client, SWT.NULL);
		label.setText("Version:");
		text = new Text(client, SWT.BORDER);
		text.setToolTipText("The version of the OS.");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		this.context.bindValue(SWTObservables.observeText(text, SWT.Modify), EMFObservables.observeValue(this.os, SpdPackage.Literals.OS__VERSION), null, null);

		final EmfValidationStatusProvider provider = new EmfValidationStatusProvider(this.os, this.context, this.adapterFactory);
		this.context.addValidationStatusProvider(provider);
		this.pageSupport = WizardPageSupport.create(this, this.context);

		this.setControl(client);
	}

	/**
	 * Sets the os.
	 * 
	 * @param os2 the os2
	 */
	public void setOs(final Os os2) {
		this.os = EcoreUtil.copy(os2);
		this.setTitle("Edit Os");
		this.setDescription("Edit OS Values");
	}

	/**
	 * @since 2.0
	 */
	private void prepareOperatingSystems() {
		if (this.implSettings != null) {
			final ICodeGeneratorDescriptor codeGenDesc = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen(this.implSettings.getGeneratorId());
			final IOperatingSystem[] operatingSystems = codeGenDesc.getOperatingSystems();
			final List<String> tempOs = OsRegistry.getOsNames();

			for (final IOperatingSystem temp : operatingSystems) {
				if (!tempOs.contains(temp.getName())) {
					tempOs.add(temp.getName());
				}
			}

			this.comboViewer.setInput(tempOs);
		} else {
			this.comboViewer.setInput(OsRegistry.getOsNames());
		}
	}
}
