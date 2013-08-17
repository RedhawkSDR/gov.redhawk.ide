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

import java.io.IOException;

import mil.jpeojtrs.sca.spd.Implementation;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;

/**
 * @since 10.0
 * 
 */
public class DefaultCodegenMigrator implements ICodegenTemplateMigrator {

	/* (non-Javadoc)
	 * @see gov.redhawk.ide.codegen.ICodegenTemplateMigrator#migrate(gov.redhawk.ide.codegen.ITemplateDesc, mil.jpeojtrs.sca.spd.Implementation, gov.redhawk.ide.codegen.ImplementationSettings)
	 */
	@Override
	public void migrate(IProgressMonitor monitor, ITemplateDesc template, Implementation impl, ImplementationSettings implSettings) throws CoreException {
		ITemplateDesc newTemplate = template.getNewTemplate();
		ICodeGeneratorDescriptor newGenerator = newTemplate.getCodegen();
		implSettings.setGeneratorId(newGenerator.getId());
		implSettings.setTemplate(newTemplate.getId());
		try {
			implSettings.eResource().save(null);
		} catch (IOException e) {
			throw new CoreException(new Status(Status.ERROR, RedhawkCodegenActivator.PLUGIN_ID, "Failed to update code generator.", e));
		}

	}

}
