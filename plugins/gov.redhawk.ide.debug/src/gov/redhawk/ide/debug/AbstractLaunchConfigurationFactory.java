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

import gov.redhawk.ide.sdr.util.ScaEnvironmentUtil;
import gov.redhawk.sca.launch.ScaLaunchConfigurationUtil;

import java.util.Map;

import mil.jpeojtrs.sca.scd.ComponentType;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * @since 4.0
 * 
 */
public abstract class AbstractLaunchConfigurationFactory implements ILaunchConfigurationFactory, IExecutableExtension {

	private String launchConfigId;
	private String id;
	
	@Override
	public ILaunchConfigurationWorkingCopy createLaunchConfiguration(String name, final String implId, final SoftPkg spd) throws CoreException {
		if (name == null) {
			name = spd.getName();
		}

		final ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		final String launchConfigName = launchManager.generateLaunchConfigurationName(name);

		final String configTypeId = getLaunchConfigTypeID();
		final ILaunchConfigurationType configType = launchManager.getLaunchConfigurationType(configTypeId);
		if (configType == null) {
			throw new CoreException(new Status(Status.ERROR, ScaDebugPlugin.ID, "Failed to find launch configuration type of: " + configTypeId + " invalid launch configuration factory " + id, null));
		}
		final ILaunchConfigurationWorkingCopy retVal = configType.newInstance(null, launchConfigName);

		retVal.setAttribute(ScaDebugLaunchConstants.ATT_IMPL_ID, implId);
		ScaLaunchConfigurationUtil.setProfileURI(retVal, EcoreUtil.getURI(spd));

		// Setup Environment variables for override locations of OSSIEHOME and SDRROOT
		final Map<String, String> envVar = ScaEnvironmentUtil.getLauncherEnvMap(spd.getImplementation(implId));

		retVal.setAttribute(ILaunchManager.ATTR_APPEND_ENVIRONMENT_VARIABLES, true);
		retVal.setAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, envVar);
		
		if ("linux".equalsIgnoreCase(Platform.getOS())) {
			retVal.setAttribute(DebugPlugin.ATTR_PROCESS_FACTORY_ID, "gov.redhawk.ide.debug.linux.processFactory");
		}

		final String progArgs = getProgramArguments(spd);
		setProgramArguments(progArgs, retVal);

		return retVal;
	}
	

	protected abstract void setProgramArguments(String progArgs, ILaunchConfigurationWorkingCopy config) throws CoreException;

	protected String getProgramArguments(final SoftPkg spd) {
		if (spd.getDescriptor() == null || spd.getDescriptor().getComponent() == null) {
			return "";
		}
		final ComponentType type = SoftwareComponent.Util.getWellKnownComponentType(spd.getDescriptor().getComponent());
		return SpdLauncherUtil.getDefaultProgramArguments(type);
	}

	protected String getLaunchConfigTypeID() {
		return this.launchConfigId;
	}

	@Override
	public void setInitializationData(final IConfigurationElement config, final String propertyName, final Object data) throws CoreException {
		this.launchConfigId = config.getAttribute("launchConfigType");
		this.id = config.getAttribute("id");

	}

}
