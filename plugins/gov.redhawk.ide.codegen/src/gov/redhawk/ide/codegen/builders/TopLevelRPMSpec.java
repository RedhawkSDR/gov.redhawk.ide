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
package gov.redhawk.ide.codegen.builders;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import gov.redhawk.ide.codegen.CodegenUtil;

/**
 * An incremental builder that adds a top-level RPM spec file to waveforms and nodes.
 * @deprecated Spec file generation is now handled by invoking either GenerateNodeHandler or GenerateWaveformHandler
 */
@Deprecated
public class TopLevelRPMSpec extends IncrementalProjectBuilder {

	/**
	 * The ID of the top level build script builder.
	 */
	public static final String ID = "gov.redhawk.ide.codegen.builders.TopLevelRPMSpec";

	public TopLevelRPMSpec() {
	}

	/**
	 * @deprecated - TopLevelRPMSpec builder is deprecated.  For REDHAWK 2.1.1+ remove the builder from any referencing project.
	 */
	@Deprecated
	@Override
	protected IProject[] build(final int kind, @SuppressWarnings("rawtypes") final Map args, final IProgressMonitor monitor) throws CoreException {
		CodegenUtil.removeDeprecatedBuilders(getProject(), monitor);
		return null;
	}
}
