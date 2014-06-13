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
package gov.redhawk.ide.codegen;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
/**
 * Allows a code generator to perform system tests during project creation to ensure the
 * system will support development of the component type in question.
 * @since 10.1
 */
public interface IScaComponentCodegenSetup extends IScaComponentCodegen {

	/**
	 * Perform various system tests to ensure the system supports development of the given template.
	 * 
	 * For example test could include, checking for header files, architecture, cross platform support etc.
	 * @param templateId The template to check for
	 * @param monitor Monitor to report progress to
	 * @throws CoreException If the system does not support requested template or other errors occur during testing 
	 */
	public void checkSystem(IProgressMonitor monitor, String templateId) throws CoreException;
}
