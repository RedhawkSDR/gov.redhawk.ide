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
package gov.redhawk.ide.graphiti.sad.ui.project.wizards;

import gov.redhawk.ide.ui.wizard.ScaProjectPropertiesWizardPage;
import gov.redhawk.model.sca.util.ModelUtil;

import java.io.File;

import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.swt.widgets.Composite;

// TODO: Auto-generated Javadoc
/**
 * The Class ScaWaveformProjectPropertiesWizardPage.
 */
public class ScaWaveformProjectPropertiesWizardPage extends ScaProjectPropertiesWizardPage {

	/**
	 * The Class SpdFileValidator.
	 */
	private static final class SadFileValidator implements IValidator {
		// Rules:
		// The file must exist
		// It must have the DOCTYPE for SPD
		// It must pass a DTD check

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IStatus validate(final Object value) {
			final String s = ((String) value).trim(); // Project names are
			// always stripped of
			// whitespace (see the
			// Java Project Wizard)
			if ((s == null) || (s.length() == 0)) {
				return ValidationStatus.error("Enter a SAD file.");
			}
			final File sadFile = new File(s);
			if (!sadFile.exists()) {
				return ValidationStatus.error("SAD file does not exist.");
			}

			final URI fileURI = URI.createFileURI(sadFile.getAbsolutePath());
			try {
				final SoftwareAssembly sad = ModelUtil.loadSoftwareAssembly(fileURI);
				if (sad == null) {
					return ValidationStatus.error("Invalid SAD file selected.");
				}
			} catch (final Exception e) { // SUPPRESS CHECKSTYLE Logged Error
				return ValidationStatus.error("Unable to parse SAD file.");
			}

			return ValidationStatus.ok();
		}
	}

	/**
	 * Instantiates a new sca waveform project properties wizard page.
	 * 
	 * @param pageName the page name
	 * @since 4.0
	 */
	public ScaWaveformProjectPropertiesWizardPage(final String pageName) {
		super(pageName, "Waveform", "SAD");
		setTitle("Create a REDHAWK Waveform Project");
		setDescription("Choose to either create a Waveform or import an existing one.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createControl(final Composite parent) {
		super.createControl(parent);
		if (getContentsGroup() != null) {
			getContentsGroup().setValidator(new SadFileValidator());
		}
	}

}
