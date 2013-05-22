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
import gov.redhawk.ide.debug.variables.AbstractLauncherResolver;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.DceUuidUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * 
 */
public class DeviceIdentifierVariableResolver extends AbstractLauncherResolver implements ILauncherVariableResolver {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String resolveValue(String arg, final ILaunch launch, final ILaunchConfiguration config, final SoftPkg spd, final Implementation impl) throws CoreException {
		return DceUuidUtil.createDceUUID().toString();
	}

}
