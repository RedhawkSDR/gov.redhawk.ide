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
package gov.redhawk.ide.debug;

import gov.redhawk.model.sca.ScaService;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IDynamicVariableResolver;

/**
 * 
 */
public class UniqueServiceNameBindingResolver implements IDynamicVariableResolver {

	public static final String VARIABLE_NAME = "uniqueServiceName";

	/**
	 * {@inheritDoc}
	 */
	public String resolveValue(final IDynamicVariable variable, String argument) throws CoreException {
		if (argument == null) {
			argument = "service";
		}

		if (findService(argument) == null) {
			return argument;
		}

		String retVal = null;
		for (int i = 2; retVal == null; i++) {

			final String testName = argument + "_" + i;
			if (findService(argument) == null) {
				retVal = testName;
			}

		}
		return retVal;
	}

	private Object findService(final String argument) {
		final LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca();
		for (final ScaService service : localSca.getSandboxDeviceManager().getServices()) {
			if (service.getName().equals(argument)) {
				return service;
			}
		}
		return null;
	}

	public static String getProgramArgument(final String argument) {
		if (argument != null) {
			return "${" + UniqueServiceNameBindingResolver.VARIABLE_NAME + ":" + argument + "}";
		} else {
			return "${" + UniqueServiceNameBindingResolver.VARIABLE_NAME + "}";
		}
	}

}
