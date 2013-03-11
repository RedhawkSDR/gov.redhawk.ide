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

import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.spd.internal.ui.editor.wizard.OsWizardPage;
import mil.jpeojtrs.sca.spd.Os;

import org.eclipse.jface.wizard.Wizard;

/**
 * 
 */
public class OsWizard extends Wizard {

	private final ImplementationSettings implSettings;

	private final OsWizardPage page;

	/**
	 * @since 2.0
	 */
	public OsWizard(final ImplementationSettings implementationSettings) {
		this.implSettings = implementationSettings;
		this.page = new OsWizardPage(this.implSettings);
		this.setWindowTitle("OS Wizard");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPages() {
		this.addPage(this.page);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {
		return true;
	}

	/**
	 * Gets the os.
	 * 
	 * @return the os
	 */
	public Os getOs() {
		return this.page.getOs();
	}

	/**
	 * Sets the os.
	 * 
	 * @param os the os
	 */
	public void setOs(final Os os) {
		this.page.setOs(os);
	}

}
