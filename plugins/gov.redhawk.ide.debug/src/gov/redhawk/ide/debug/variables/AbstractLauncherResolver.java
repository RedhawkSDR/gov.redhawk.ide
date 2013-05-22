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
package gov.redhawk.ide.debug.variables;

import gov.redhawk.ide.debug.ILauncherVariableResolver;
import gov.redhawk.ide.debug.ScaDebugLaunchConstants;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IDynamicVariableResolver;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * @since 3.0
 * 
 */
public abstract class AbstractLauncherResolver implements ILauncherVariableResolver, IExecutableExtension, IDynamicVariableResolver {

	private String name;

	/**
	 * {@inheritDoc}
	 */
	public final String resolveValue(String arg, final SoftPkg spd, final ILaunch launch, final ILaunchConfiguration config) throws CoreException {
		String retVal = launch.getAttribute(this.name);
		if (retVal == null) {
			if (config.hasAttribute(this.name)) {
				retVal = config.getAttribute(this.name, "");
			} else {
				final String implID = config.getAttribute(ScaDebugLaunchConstants.ATT_IMPL_ID, "");
				final Implementation impl = spd.getImplementation(implID);

				retVal = resolveValue(arg, launch, config, spd, impl);
			}
		}
		launch.setAttribute(this.name, retVal);
		return retVal;
	}

	protected abstract String resolveValue(String arg, final ILaunch launch, ILaunchConfiguration config, SoftPkg spd, Implementation impl) throws CoreException;

	public String getName() {
		return this.name;
	}

	public void setInitializationData(final IConfigurationElement config, final String propertyName, final Object data) throws CoreException {
		this.name = config.getAttribute("name");
	}

	public String resolveValue(IDynamicVariable variable, String argument) throws CoreException {
		throw new CoreException(new Status(Status.ERROR, ScaDebugPlugin.ID, "The variable " + variable.getName()
		        + " should only be used within component launches."));
	}

}
