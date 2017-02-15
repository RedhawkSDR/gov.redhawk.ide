/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.debug.internal.variables;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import gov.redhawk.ide.debug.variables.AbstractLauncherResolver;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

public class RHDeploymentRootResolver extends AbstractLauncherResolver {

	@Override
	protected String resolveValue(String arg, ILaunch launch, ILaunchConfiguration config, SoftPkg spd, Implementation impl) throws CoreException {
		switch (impl.getCode().getType()) {
		case SHARED_LIBRARY:
			// Shared address space components don't need a deployment root
			return "";
		default:
			return "RH::DEPLOYMENT_ROOT /";
		}
	}

}
