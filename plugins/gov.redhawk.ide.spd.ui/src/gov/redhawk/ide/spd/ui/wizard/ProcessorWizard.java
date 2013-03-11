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
import gov.redhawk.ide.spd.internal.ui.editor.wizard.ProcessorWizardPage;
import mil.jpeojtrs.sca.spd.Processor;

import org.eclipse.jface.wizard.Wizard;

public class ProcessorWizard extends Wizard {

	private final ProcessorWizardPage page;

	private final ImplementationSettings implSettings;

	/**
	 * @since 2.0
	 */
	public ProcessorWizard(final ImplementationSettings implementationSettings) {
		this.implSettings = implementationSettings;
		this.page = new ProcessorWizardPage(this.implSettings);
		this.setWindowTitle("Processor Wizard");
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
	 * Gets the processor.
	 * 
	 * @return the processor
	 */
	public Processor getProcessor() {
		return this.page.getProcessor();
	}

	/**
	 * Sets the processor.
	 * 
	 * @param processor the processor
	 */
	public void setProcessor(final Processor processor) {
		this.page.setProcessor(processor);
	}
}
