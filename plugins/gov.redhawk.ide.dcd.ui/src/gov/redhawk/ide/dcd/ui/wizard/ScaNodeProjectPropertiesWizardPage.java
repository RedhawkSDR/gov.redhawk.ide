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

import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.ide.ui.wizard.ScaProjectPropertiesWizardPage;
import gov.redhawk.model.sca.ScaDomainManager;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.sca.ScaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.dmd.DomainManagerConfiguration;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * The Class ScaNodeProjectPropertiesWizardPage.
 */
public class ScaNodeProjectPropertiesWizardPage extends ScaProjectPropertiesWizardPage {

	/** Device Type Combo */
	private Combo domainCombo;

	/**
	 * The Class DcdFileValidator.
	 */
	private static final class DcdFileValidator implements IValidator {
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
				return ValidationStatus.error("Enter a DCD file.");
			}

			final File dcdFile = new File(s);
			if (!dcdFile.exists()) {
				return ValidationStatus.error("DCD file does not exist.");
			}

			//TODO Load the DCD instead!
			final URI fileURI = URI.createFileURI(dcdFile.getAbsolutePath());
			final DeviceConfiguration devConf = ModelUtil.loadDeviceConfiguration(fileURI);
			if (devConf == null) {
				return ValidationStatus.error("Invalid DCD file selected.");
			}
			return ValidationStatus.ok();
		}
	}

	/**
	 * Instantiates a new sca node project properties wizard page.
	 * 
	 * @param pageName the page name
	 */
	protected ScaNodeProjectPropertiesWizardPage(final String pageName) {
		super(pageName, "Node", "DCD");
		setTitle("Create an SCA Node Project");
		this.setDescription("Choose to either create a new Node or import an existing one.");
	}

	@Override
	public void createControl(final Composite parent) {
		super.createControl(parent);
		getContentsGroup().setValidator(new DcdFileValidator());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void customCreateControl(final Composite parent) {
		// Domain Group
		final Group domainGroup = new Group(parent, SWT.NONE);
		domainGroup.setText("Domain");
		domainGroup.setLayout(new GridLayout(2, false));
		GridDataFactory.generate(domainGroup, 2, 1);

		new Label(domainGroup, SWT.NONE).setText("Domain Manager:");
		this.domainCombo = new Combo(domainGroup, SWT.BORDER);

		final List<String> domainNames = new ArrayList<String>();

		// Get the domain name from the target
		final DomainManagerConfiguration dmd = SdrUiPlugin.getDefault().getTargetSdrRoot().getDomainConfiguration();
		if (dmd != null) {
			domainNames.add(dmd.getName());
		}

		// Get the domain names for any stored domain names
		final EList<ScaDomainManager> domains = ScaPlugin.getDefault().getDomainManagerRegistry().getDomains();
		for (final ScaDomainManager domain : domains) {
			if (!domainNames.contains(domain.getName())) {
				domainNames.add(domain.getName());
			}
		}
		this.domainCombo.setItems(domainNames.toArray(new String[domainNames.size()]));
		GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(this.domainCombo);
		this.domainCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});
	}

	public String getDomain() {
		return this.domainCombo.getText();
	}

	@Override
	protected boolean validatePage() {
		boolean retVal = super.validatePage();

		if (retVal && (this.domainCombo.getText().trim().length() == 0)) {
			setMessage("Enter domain name.", IMessageProvider.ERROR);
			retVal = false;
		}

		return retVal;
	}

}
