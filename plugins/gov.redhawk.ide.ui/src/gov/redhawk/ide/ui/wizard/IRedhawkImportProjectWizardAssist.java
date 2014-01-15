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
package gov.redhawk.ide.ui.wizard;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import gov.redhawk.ide.codegen.ITemplateDesc;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.ui.wizard.RedhawkImportWizardPage1.ProjectRecord;

/**
 * @since 9.1
 */
public interface IRedhawkImportProjectWizardAssist {
	/**
	 * 
	 * @param settings 
	 * @param lang
	 * @param templateDesc
	 * @return True if handled template
	 */
	boolean setTemplate(ProjectRecord record, final ImplementationSettings settings, final String lang, ITemplateDesc templateDesc) throws CoreException;
	
	void setupNatures(List<String> natures, IProject dotProject, IProgressMonitor monitor) throws CoreException;

	void setupNatures(File importSource, IProject project, IProgressMonitor monitor) throws CoreException;

	boolean handlesImplId(String id);
	
	boolean handlesLanguage(String lang);
	
	boolean handlesNature(String nature);

	String getDefaultTemplate();

	void setupWaveDev(String projectName, ImplementationSettings settings);
}
