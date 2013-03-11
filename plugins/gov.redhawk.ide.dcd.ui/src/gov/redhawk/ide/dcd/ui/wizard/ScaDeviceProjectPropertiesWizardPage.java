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
package gov.redhawk.ide.dcd.ui.wizard;

import gov.redhawk.ide.preferences.RedhawkIdePreferenceConstants;
import gov.redhawk.ide.ui.wizard.ScaProjectPropertiesWizardPage;
import gov.redhawk.model.sca.util.ModelUtil;

import java.io.File;

import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * The Class ScaDeviceProjectPropertiesWizardPage.
 */
public class ScaDeviceProjectPropertiesWizardPage extends ScaProjectPropertiesWizardPage {

	/** Device Type combo box. */
	private Combo deviceTypeCombo;

	/** Aggregate device check box. */
	private Button aggregateButton;

	/**
	 * The Class SpdFileValidator.
	 */
	private static final class SpdFileValidator implements IValidator {
		// Rules:
		// The file must exist
		// It must have the DOCTYPE for SPD
		// It must pass a DTD check

		/**
		 * {@inheritDoc}
		 */
		public IStatus validate(final Object value) {

			// Project names are always stripped of whitespace
			// (see the Java Project Wizard)
			final String s = ((String) value).trim();
			if ((s == null) || (s.length() == 0)) {
				return ValidationStatus.error("Enter a SPD file.");
			}

			final File spdFile = new File(s);
			if (!spdFile.exists()) {
				return ValidationStatus.error("SPD file does not exist.");
			}

			final URI fileURI = URI.createFileURI(spdFile.getAbsolutePath());
			final SoftPkg softPkg = ModelUtil.loadSoftPkg(fileURI);
			if (softPkg == null) {
				return ValidationStatus.error("Invalid SPD file selected.");
			}

			return ValidationStatus.ok();
		}
	}

	/**
	 * Instantiates a new sca resource project properties wizard page.
	 * 
	 * @param pageName the page name
	 */
	protected ScaDeviceProjectPropertiesWizardPage(final String pageName, final String type) {
		super(pageName, type, "SPD");
		setTitle("Create a SCA " + type + " Project");
		this.setDescription("Choose to either create a new Device or import an existing one.");
	}

	@Override
	public void createControl(final Composite parent) {
		super.createControl(parent);
		getContentsGroup().setValidator(new SpdFileValidator());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void customCreateControl(final Composite parent) {
		// Device Group
		final Group deviceGroup = new Group(parent, SWT.NONE);
		deviceGroup.setText(getResourceType());
		deviceGroup.setLayout(new GridLayout(2, false));
		GridDataFactory.generate(deviceGroup, 2, 1);

		this.deviceTypeCombo = new Combo(deviceGroup, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		this.deviceTypeCombo.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());
		this.deviceTypeCombo.setItems(RedhawkIdePreferenceConstants.DEVICE_TYPES);
		this.deviceTypeCombo.select(0);

		this.aggregateButton = new Button(deviceGroup, SWT.CHECK);
		this.aggregateButton.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(1, 1).create());
		this.aggregateButton.setText(RedhawkIdePreferenceConstants.AGGREGATE_DEVICE + " device");
	}

	@Override
	public void setVisible(final boolean visible) {
		if (!visible) {
			((NewScaDeviceCreationProjectWizard) this.getWizard()).switchingResourcePage();
		}
		super.setVisible(visible);
	}

	/**
	 * @return
	 */
	public String getDeviceType() {
		return this.deviceTypeCombo.getText();
	}

	/**
	 * @return
	 */
	public boolean getAggregateDeviceType() {
		return this.aggregateButton.getSelection();
	}

}
