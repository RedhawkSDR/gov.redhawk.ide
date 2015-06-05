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
package gov.redhawk.ide.codegen.ui.internal;

import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.IScaComponentCodegen;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;

import org.eclipse.core.runtime.CoreException;

public class GeneratorUtil {

	private GeneratorUtil() {
	}

	public static IScaComponentCodegen getGenerator(ImplementationSettings settings) throws CoreException {
		final String codegenId = settings.getGeneratorId();
		final ICodeGeneratorDescriptor codeGenDesc = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen(codegenId);
		final IScaComponentCodegen generator = codeGenDesc.getGenerator();
		return generator;
	}
}
