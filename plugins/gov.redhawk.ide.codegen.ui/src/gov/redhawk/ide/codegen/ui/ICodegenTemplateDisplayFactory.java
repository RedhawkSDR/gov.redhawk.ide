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

import gov.redhawk.sca.util.SubMonitor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;


/**
 * @since 8.1
 */
public interface ICodegenTemplateDisplayFactory extends ICodegenDisplayFactory {


	
	/**
	 * Modify the project before the Project creation Wizard finishes.  Usually where the results of
	 * the ICodegenWizardPages will get contributed back into the project.
	 * @param project The project created using the standard pages and the supplied ICodegenWizardPages
	 * @param spdFile The corresponding spd file for the project
	 * @param newChild Progress monitor
	 * @since 8.0
	 */
	void modifyProject(IProject project, IFile spdFile, SubMonitor newChild) throws CoreException;
	
	/**
	 * Create the specified wizard pages.
	 * 
	 * @return a wizard page
	 */
	ICodegenWizardPage[] createPages();
}
