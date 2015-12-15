/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package gov.redhawk.ide.sdr.nodebooter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.externaltools.internal.IExternalToolConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;

import gov.redhawk.ide.sdr.IdeSdrActivator;
import gov.redhawk.ide.sdr.util.ScaEnvironmentUtil;

public class NodeBooterLauncherUtil {

	private static final NodeBooterLauncherUtil INSTANCE = new NodeBooterLauncherUtil();

	private List<ILaunch> launches;

	private NodeBooterLauncherUtil() {
		launches = new ArrayList<ILaunch>();
	}

	public static NodeBooterLauncherUtil getInstance() {
		return INSTANCE;
	}

	/**
	 * Launches a new nodeBooter process
	 * @param configName The launch configuration name to use
	 * @param arguments The command-line arguments to nodeBooter
	 * @param monitor
	 * @throws CoreException
	 */
	public ILaunch launch(String configName, String arguments, IProgressMonitor monitor) throws CoreException {
		ILaunchConfigurationWorkingCopy launchConfig = createNodeBooterLaunchConfig(configName, arguments);
		final ILaunch launch = launchConfig.launch(ILaunchManager.RUN_MODE, monitor);
		synchronized (launches) {
			launches.add(launch);
		}
		return launch;
	}

	/**
	 * Attempts to terminate all nodeBooters launched up to this point.
	 */
	public void terminateAll() {
		List<ILaunch> launchesCopy;
		synchronized (launches) {
			launchesCopy = new ArrayList<ILaunch>(launches);
			launches.clear();
		}
		for (ILaunch launch : launchesCopy) {
			if (launch.canTerminate()) {
				try {
					launch.terminate();
				} catch (DebugException e) {
					IdeSdrActivator.getDefault().logWarning("Unable to shutdown a nodeBooter process", e);
				}
			}
		}
	}

	@SuppressWarnings("restriction")
	private static ILaunchConfigurationWorkingCopy createNodeBooterLaunchConfig(final String name, String arguments) throws CoreException {
		final ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		final ILaunchConfigurationType type = manager.getLaunchConfigurationType(IExternalToolConstants.ID_PROGRAM_LAUNCH_CONFIGURATION_TYPE);

		final ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(null, name);

		final Map<String, String> environmentMap = ScaEnvironmentUtil.getLauncherEnvMap(null);

		workingCopy.setAttribute(IExternalToolConstants.ATTR_LOCATION, "${OssieHome}/bin/nodeBooter");
		workingCopy.setAttribute(IExternalToolConstants.ATTR_TOOL_ARGUMENTS, arguments);
		workingCopy.setAttribute(IExternalToolConstants.ATTR_WORKING_DIRECTORY, "${SdrRoot}");
		workingCopy.setAttribute(IExternalToolConstants.ATTR_SHOW_CONSOLE, true);
		workingCopy.setAttribute(IExternalToolConstants.ATTR_BUILDER_ENABLED, false);
		workingCopy.setAttribute(IExternalToolConstants.ATTR_BUILD_SCOPE, "${none}");
		workingCopy.setAttribute(IExternalToolConstants.ATTR_INCLUDE_REFERENCED_PROJECTS, (String) null);
		workingCopy.setAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, environmentMap);

		return workingCopy;
	}

}
