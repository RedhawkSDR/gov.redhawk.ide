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

import mil.jpeojtrs.sca.spd.SpdFactory;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.spd.UsesDevice;

import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.EMFObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * 
 */
public class UsesDeviceWizardPage extends WizardPage {

	private static final ImageDescriptor TITLE_IMAGE = null;

	private final EMFDataBindingContext context = new EMFDataBindingContext();

	private final UsesDevice device = SpdFactory.eINSTANCE.createUsesDevice();

	/**
	 * The Constructor.
	 */
	protected UsesDeviceWizardPage() {
		super("usesDevicePage", "New Uses Device", UsesDeviceWizardPage.TITLE_IMAGE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createControl(final Composite parent) {
		final Composite client = new Composite(parent, SWT.NULL);
		client.setLayout(new GridLayout(2, false));

		Label label;
		Text text;

		final GridDataFactory labelFactory = GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.FILL);
		final GridDataFactory textFactory = GridDataFactory.fillDefaults().grab(true, false);

		label = new Label(client, SWT.NULL);
		label.setLayoutData(labelFactory.create());
		text = new Text(client, SWT.BORDER);
		text.setLayoutData(textFactory.create());
		label.setText("ID:");
		this.context.bindValue(SWTObservables.observeText(text, SWT.Modify), EMFObservables.observeValue(this.device,
		        SpdPackage.Literals.USES_DEVICE__ID), null, null);

		label = new Label(client, SWT.NULL);
		label.setLayoutData(labelFactory.create());
		text = new Text(client, SWT.BORDER);
		text.setLayoutData(textFactory.create());
		label.setText("Type:");
		this.context.bindValue(SWTObservables.observeText(text, SWT.Modify), EMFObservables.observeValue(this.device,
		        SpdPackage.Literals.USES_DEVICE__TYPE), null, null);

		this.setControl(client);
	}

}
