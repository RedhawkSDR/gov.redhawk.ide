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
package gov.redhawk.ide.sdr.ui;

import gov.redhawk.ide.sdr.util.ScaEnvironmentUtil;

import java.util.Map;

import org.eclipse.core.externaltools.internal.IExternalToolConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;

/**
 * @since 1.1
 * @deprecated Moved to the non-UI bundle
 */
@Deprecated
public class NodeBooterLauncherUtil {

	private NodeBooterLauncherUtil() {
	}

	/**
	 * @since 2.0
	 * @deprecated Use {@link gov.redhawk.ide.sdr.nodebooter.NodeBooterLauncherUtil#launch(String, String, IProgressMonitor)}
	 */
	@Deprecated
	public static ILaunch launch(final ILaunchConfiguration config) throws CoreException {
		final ILaunch launched = config.launch(ILaunchManager.RUN_MODE, null);

		PlatformUI.getWorkbench().addWorkbenchListener(new IWorkbenchListener() {

			@Override
			public boolean preShutdown(final IWorkbench workbench, final boolean forced) {
				if (launched.canTerminate()) {
					try {
						launched.terminate();
					} catch (final DebugException e) {
						// PASS
					}
				}

				return true;
			}

			@Override
			public void postShutdown(final IWorkbench workbench) {
				// PASS
			}
		});
		return launched;
	}

	/**
	 * @deprecated Use {@link gov.redhawk.ide.sdr.nodebooter.NodeBooterLauncherUtil#launch(String, String, IProgressMonitor)}
	 */
	@SuppressWarnings("restriction")
	@Deprecated
	public static ILaunchConfigurationWorkingCopy createNodeBooterLaunchConfig(final String name) throws CoreException {
		final ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		final ILaunchConfigurationType type = manager.getLaunchConfigurationType(IExternalToolConstants.ID_PROGRAM_LAUNCH_CONFIGURATION_TYPE);

		final ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(null, name);

		final Map<String, String> environmentMap = ScaEnvironmentUtil.getLauncherEnvMap(null);

		workingCopy.setAttribute(IExternalToolConstants.ATTR_LOCATION, "${OssieHome}/bin/nodeBooter");
		workingCopy.setAttribute(IExternalToolConstants.ATTR_WORKING_DIRECTORY, "${SdrRoot}");
		workingCopy.setAttribute(IExternalToolConstants.ATTR_SHOW_CONSOLE, true);
		workingCopy.setAttribute(IExternalToolConstants.ATTR_BUILDER_ENABLED, false);
		workingCopy.setAttribute(IExternalToolConstants.ATTR_BUILD_SCOPE, "${none}");
		workingCopy.setAttribute(IExternalToolConstants.ATTR_INCLUDE_REFERENCED_PROJECTS, (String) null);
		workingCopy.setAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, environmentMap);

		return workingCopy;
	}

	/**
	 * @deprecated Do not use. No replacement.
	 */
	@Deprecated
	public static ILaunchConfiguration findLaunchConfig(final String name) throws CoreException {
		final ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		for (final ILaunchConfiguration config : manager.getLaunchConfigurations()) {
			if (config.getName().equals(name)) {
				return config;
			}
		}
		return null;
	}
}
