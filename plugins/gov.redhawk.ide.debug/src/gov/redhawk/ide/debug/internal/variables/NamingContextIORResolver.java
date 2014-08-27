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
import gov.redhawk.ide.debug.NotifyingNamingContext;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.variables.AbstractLauncherResolver;
import mil.jpeojtrs.sca.scd.ComponentType;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * 
 */
public class NamingContextIORResolver extends AbstractLauncherResolver implements ILauncherVariableResolver {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String resolveValue(String arg, final ILaunch launch, final ILaunchConfiguration config, final SoftPkg spd, final Implementation impl) throws CoreException {
		final String namingContext;
		final ComponentType type = SoftwareComponent.Util.getWellKnownComponentType(spd.getDescriptor().getComponent());
		switch (type) {
		case DEVICE:
		case EVENT_SERVICE:
		case SERVICE:
			namingContext = resolveService(spd);
			break;
		default: 
			namingContext = resolveResource(spd);
			break;
		}
		return namingContext;
	}

	/**
	 * @param spd
	 * @return
	 * @throws CoreException 
	 */
	private String resolveResource(final SoftPkg spd) throws CoreException {
		final String namingContext;
		final NotifyingNamingContext nc = getDomNamingContext(spd);
		namingContext = nc.getNamingContext().toString();
		return namingContext;
	}

	/**
	 * @param spd
	 * @return
	 * @throws CoreException 
	 */
	private String resolveService(final SoftPkg spd) throws CoreException {
		final String namingContext;
		final NotifyingNamingContext nc = getDevNamingContext(spd);
		namingContext = nc.getNamingContext().toString();
		return namingContext;
	}

	private NotifyingNamingContext getDomNamingContext(final SoftPkg spd) throws CoreException {
		return ScaDebugPlugin.getInstance().getLocalSca(null).getSandboxWaveform().getNamingContext().getResourceContext(spd.eResource().getURI());
	}

	private NotifyingNamingContext getDevNamingContext(final SoftPkg spd) throws CoreException {
		return ScaDebugPlugin.getInstance().getLocalSca(null).getSandboxDeviceManager().getNamingContext().getResourceContext(spd.eResource().getURI());
	}
}
