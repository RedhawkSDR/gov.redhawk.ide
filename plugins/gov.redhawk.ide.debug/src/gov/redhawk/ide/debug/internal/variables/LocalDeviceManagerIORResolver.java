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


import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.ScaDebugPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IDynamicVariableResolver;

/**
 * 
 */
public class LocalDeviceManagerIORResolver implements IDynamicVariableResolver {

	public static final String VARIABLE_NAME = "LocalDeviceManagerIOR";

	public String resolveValue(final IDynamicVariable variable, final String argument) throws CoreException {
		final LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca();
		final String retVal = localSca.getSandboxDeviceManager().getCorbaObj().toString();
		return retVal;
	}

	public static String getProgramArgument() {
		return "${" + LocalDeviceManagerIORResolver.VARIABLE_NAME + "}";
	}

}
