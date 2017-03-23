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

import gov.redhawk.ide.debug.ILauncherVariableResolver;
import gov.redhawk.ide.debug.LocalScaDeviceManager;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.variables.AbstractLauncherResolver;
import gov.redhawk.model.sca.ScaDevice;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.collections.FeatureMapList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * Provides a unique device name within the IDE's sandbox device manager
 */
public class DeviceLabelVariableResolver extends AbstractLauncherResolver implements ILauncherVariableResolver {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String resolveValue(String arg, final ILaunch launch, final ILaunchConfiguration config, final SoftPkg spd, final Implementation impl)
		throws CoreException {
		return getDeviceUniqueName(spd);
	}

	private String getDeviceUniqueName(final SoftPkg spd) {
		final LocalScaDeviceManager devMgr = ScaDebugPlugin.getInstance().getLocalSca().getSandboxDeviceManager();
		String name;
		for (int i = 1; true; i++) {
			name = spd.getName() + "_" + i;
			boolean contains = false;
			for (final ScaDevice< ? > d : new FeatureMapList<>(devMgr.getDevices(), ScaDevice.class)) {
				if (name.equals(d.fetchLabel(null))) {
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
