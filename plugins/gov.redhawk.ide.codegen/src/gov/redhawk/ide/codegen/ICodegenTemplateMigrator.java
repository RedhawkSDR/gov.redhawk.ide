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

import mil.jpeojtrs.sca.spd.Implementation;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @since 10.0
 * 
 */
public interface ICodegenTemplateMigrator {

	void migrate(IProgressMonitor monitor, ITemplateDesc template, Implementation impl, ImplementationSettings implSettings) throws CoreException;

}
