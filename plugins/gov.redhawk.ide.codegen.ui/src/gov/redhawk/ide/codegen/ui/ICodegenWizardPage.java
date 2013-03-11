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
package gov.redhawk.ide.codegen.ui;

import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.ImplementationSettings;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.jface.wizard.IWizardPage;

/**
 * @since 2.0
 */
public interface ICodegenWizardPage extends IWizardPage {

	/**
	 * @since 7.0
	 */
	public void configure(SoftPkg softpkg, Implementation impl, ICodeGeneratorDescriptor desc, ImplementationSettings implSettings, String componentType);

	public ImplementationSettings getSettings();

	/**
	 * @since 4.0
	 */
	public boolean canFinish();

	/**
	 * @since 4.0
	 */
	public void setCanFlipToNextPage(final boolean canFlip);

	/**
	 * @since 4.0
	 */
	public void setCanFinish(final boolean canFinish);
}
