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
package gov.redhawk.ide.debug.internal.variables;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import gov.redhawk.ide.debug.ILauncherVariableResolver;
import gov.redhawk.ide.debug.LocalScaDeviceManager;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.variables.AbstractLauncherResolver;
import gov.redhawk.model.sca.ScaService;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

/**
 * Provides a unique service name within naming context.
 */
public class ServiceNameVariableResolver extends AbstractLauncherResolver implements ILauncherVariableResolver {

	@Override
	protected String resolveValue(String arg, final ILaunch launch, final ILaunchConfiguration config, final SoftPkg spd, final Implementation impl)
		throws CoreException {
		return getServiceUniqueName(spd);
	}

	private String getServiceUniqueName(SoftPkg spd) {
		final LocalScaDeviceManager devMgr = ScaDebugPlugin.getInstance().getLocalSca().getSandboxDeviceManager();
		String name;
		for (int i = 1; true; i++) {
			name = spd.getName() + "_" + i;
			boolean contains = false;
			for (final ScaService s : devMgr.getServices()) {
				if (name.equals(s.getName())) {
					contains = true;
				}
			}
			if (!contains) {
				break;
			}
		}
		return name;
	}

}
