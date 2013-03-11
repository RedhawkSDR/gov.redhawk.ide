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

import gov.redhawk.ide.spd.internal.ui.editor.wizard.DependencyWizardPage;
import mil.jpeojtrs.sca.spd.Dependency;

import org.eclipse.jface.wizard.Wizard;

public class DependencyWizard extends Wizard {

	private final DependencyWizardPage page = new DependencyWizardPage();

	public DependencyWizard() {
		setNeedsProgressMonitor(false);
		setWindowTitle("Dependency Wizard");
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
	 * Gets the dependency.
	 * 
	 * @return the dependency
	 */
	public Dependency getDependency() {
		return this.page.getDependency();
	}

	/**
	 * Sets the dependency.
	 * 
	 * @param processor the dependency
	 */
	public void setDependency(final Dependency dependency) {
		this.page.setDependency(dependency);
	}
}
