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
package gov.redhawk.ide.spd.ui.wizard;

import gov.redhawk.ide.ui.wizard.ScaProjectPropertiesWizardPage;
import gov.redhawk.model.sca.util.ModelUtil;

import java.io.File;

import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.swt.widgets.Composite;

/**
 * The Class ScaResourceProjectPropertiesWizardPage.
 */
public class ScaResourceProjectPropertiesWizardPage extends ScaProjectPropertiesWizardPage {

	/**
	 * The Class SpdFileValidator.
	 * @since 8.0
	 */
	public static final class SpdFileValidator implements IValidator {
		// Rules:
		// The file must exist
		// It must have the DOCTYPE for SPD
		// It must pass a DTD check

		/**
		 * {@inheritDoc}
		 */
		@Override
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
			try {
				final SoftPkg softPkg = ModelUtil.loadSoftPkg(fileURI);
				if (softPkg == null) {
					return ValidationStatus.error("Invalid SPD file selected.");
				}
			} catch (final Exception e) { // SUPPRESS CHECKSTYLE Logged Error
				return ValidationStatus.error("Unable to parse SPD file.");
			}

			return ValidationStatus.ok();
		}
	}

	/**
	 * Instantiates a new sca resource project properties wizard page.
	 * 
	 * @param pageName the page name
	 * @since 8.1
	 */
	public ScaResourceProjectPropertiesWizardPage(final String pageName, final String type) {
		super(pageName, type, "SPD");
		setTitle("Create a SCA " + type + " Project");
		this.setDescription("Choose to either create a new Component or import an existing one.");
	}
	
	@Override
	public void createControl(final Composite parent) {
		super.createControl(parent);
		getContentsGroup().setValidator(new SpdFileValidator());
	}

	@Override
	public void setVisible(final boolean visible) {
		if (!visible && getWizard() instanceof NewScaResourceWizard) {
			((NewScaResourceWizard) this.getWizard()).switchingResourcePage();
		}
		super.setVisible(visible);
	}

}
