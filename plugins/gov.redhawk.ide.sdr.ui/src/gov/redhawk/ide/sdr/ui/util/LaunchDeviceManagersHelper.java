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
package gov.redhawk.ide.sdr.ui.util;

import gov.redhawk.ide.sdr.IdeSdrActivator;
import gov.redhawk.ide.sdr.preferences.IdeSdrPreferenceConstants;
import gov.redhawk.ide.sdr.ui.NodeBooterLauncherUtil;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;

import java.util.List;

import mil.jpeojtrs.sca.dcd.DeviceConfiguration;

import org.eclipse.core.externaltools.internal.IExternalToolConstants;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * @since 1.3
 * @deprecated Move to the non-UI bundle
 */
@Deprecated
public class LaunchDeviceManagersHelper {
	/**
	 * @since 2.0
	 */
	private LaunchDeviceManagersHelper() {
	}

	private static IStatus launchDeviceManager(final IProgressMonitor monitor, DeviceManagerLaunchConfiguration deviceManager) {
		try {
			DeviceConfiguration devConfig = deviceManager.getDcd();
			String domainName = deviceManager.getDomainName();
			DebugLevel debugLevel = deviceManager.getDebugLevel();
			String additionalArguments = deviceManager.getAdditionalArguments();

			final String launcherName = LaunchDeviceManagersHelper.getLauncherName(devConfig);
			final ILaunchConfigurationWorkingCopy config = NodeBooterLauncherUtil.createNodeBooterLaunchConfig(launcherName);
			final StringBuilder builder = new StringBuilder();
			builder.append("-d ");
			builder.append(devConfig.eResource().getURI().path());

			if (domainName != null) {
				builder.append(" --domainname \"" + domainName + "\"");
			}

			if (debugLevel != null && debugLevel != DebugLevel.Info) { // SUPPRESS CHECKSTYLE MagicNumber
				builder.append(" -debug " + debugLevel.ordinal());
			}

			if (additionalArguments != null && !additionalArguments.trim().isEmpty()) {
				builder.append(" ");
				builder.append(additionalArguments.trim());
			}

			config.setAttribute(IExternalToolConstants.ATTR_TOOL_ARGUMENTS, builder.toString());
			final ILaunch launch = NodeBooterLauncherUtil.launch(config);
			// Give things a second to ensure they started correctly
			try {
				Thread.sleep(1000); // SUPPRESS CHECKSTYLE MagicNumber
			} catch (final InterruptedException e) {
				// PASS
			}
			if (launch.isTerminated()) {
				int exitValue = launch.getProcesses()[0].getExitValue();
				if (exitValue != 0) {
					return new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID,
						"Device Manager failed to launch. Check console output. Exit status = " + exitValue);
				}
			}
			return Status.OK_STATUS;
		} catch (final CoreException e) {
			return new Status(e.getStatus().getSeverity(), SdrUiPlugin.PLUGIN_ID, e.getLocalizedMessage(), e);
		}
	}

	/**
	 * @since 3.3
	 */
	public static IStatus launchDeviceManagers(final IProgressMonitor monitor, final List<DeviceManagerLaunchConfiguration> deviceManagers) {
		Assert.isNotNull(deviceManagers);
		final SubMonitor subMonitor = SubMonitor.convert(monitor, "Launch Device Managers", deviceManagers.size());
		final MultiStatus retVal = new MultiStatus(SdrUiPlugin.PLUGIN_ID, IStatus.OK, "Failed to launch Device Managers...", null);

		for (DeviceManagerLaunchConfiguration deviceManagerConf : deviceManagers) {
			retVal.add(LaunchDeviceManagersHelper.launchDeviceManager(subMonitor.newChild(1), deviceManagerConf));
		}
		if (retVal.isOK()) {
			return Status.OK_STATUS;
		}

		return retVal;
	}

	private static String getLauncherName(final DeviceConfiguration devConfig) {
		return new ScopedPreferenceStore(InstanceScope.INSTANCE, IdeSdrActivator.PLUGIN_ID).getString(IdeSdrPreferenceConstants.PREF_DEFAULT_DEVICE_MANAGER_NAME) + " " + devConfig.getName();
	}
}
