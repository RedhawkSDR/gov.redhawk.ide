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

import org.eclipse.jface.wizard.IWizardPage;

import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.ui.ICodegenWizardPage;
import mil.jpeojtrs.sca.spd.Implementation;

/**
 * @since 8.1
 */
public interface ScaImplementationWizard2 extends ScaImplementationWizard {

	/**
	 * This method is called when a generator changes to update the status of
	 * the next/finish buttons.
	 */
	public void generatorChanged(Implementation impl, ICodeGeneratorDescriptor codeGeneratorDescriptor, String previousImplId);
	
	public void addTemplatePages(IWizardPage pageAddingPages, ICodegenWizardPage[] pagesToAdd);
	
	public void removeTemplatePages(IWizardPage pageAddingPages, ICodegenWizardPage[] pageTypesToRemove);
	
}
