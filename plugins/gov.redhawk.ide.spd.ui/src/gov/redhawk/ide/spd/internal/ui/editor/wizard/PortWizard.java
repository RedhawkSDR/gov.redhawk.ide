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

import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.ui.editor.SCAFormEditor;
import mil.jpeojtrs.sca.scd.AbstractPort;
import mil.jpeojtrs.sca.scd.Ports;

import org.eclipse.jface.wizard.Wizard;

/**
 * The Class PortWizard.
 */
public class PortWizard extends Wizard {

	private final PortWizardPage portPage;

	public PortWizard(final Ports ports, final SCAFormEditor editor) {
		this.portPage = new PortWizardPage(ports, editor);
		this.setNeedsProgressMonitor(false);
	}

	public PortWizard(final AbstractPort port, final Ports ports, final SCAFormEditor editor) {
		this.portPage = new PortWizardPage(port, ports, editor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPages() {
		this.addPage(this.portPage);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {
		return true;
	}

	/**
	 * Gets the value.
	 * 
	 * @return the value
	 */
	public PortWizardPage.PortWizardModel getValue() {
		return this.portPage.getValue();
	}

	/**
	 * Obtains the {@link IdlLibrary} from the {@link PortWizardPage}.
	 * 
	 * @return the {@link IdlLibrary}
	 */
	public IdlLibrary getIdlLibrary() {
		return this.portPage.getIdlLibrary();
	}

}
