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

import gov.redhawk.ide.sdr.ui.NodeBooterLauncherUtil;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.ide.sdr.ui.preferences.SdrUiPreferenceConstants;
import gov.redhawk.model.sca.ScaDomainManager;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mil.jpeojtrs.sca.dcd.DeviceConfiguration;

import org.eclipse.core.externaltools.internal.IExternalToolConstants;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

/**
 * @since 1.3
 */
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
				if (launch.getProcesses()[0].getExitValue() == 0) {
					return Status.CANCEL_STATUS;
				}
				return new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, "Device manager failed to launch.  Check console output");
			}
			return Status.OK_STATUS;
		} catch (final CoreException e) {
			return e.getStatus();
		}
	}

	/**
	 * @since 3.0
	 * @deprecated Use {@link #launchDeviceManager(IProgressMonitor, DeviceManagerLaunchConfiguration)}
	 */
	@Deprecated
	public static IStatus launchDeviceManagers(final IProgressMonitor monitor, final ScaDomainManager domain, final DeviceConfiguration... devConfigs) {
		Assert.isNotNull(devConfigs);
		String domainName = null;
		if (domain != null) {
			domainName = domain.getName();
		}
		final SubMonitor subMonitor = SubMonitor.convert(monitor, "Launching device managers...", devConfigs.length);
		final MultiStatus retVal = new MultiStatus(SdrUiPlugin.PLUGIN_ID, IStatus.OK, "Failed to launch Device Managers...", null);
		for (final DeviceConfiguration devConfig : devConfigs) {
			LaunchDeviceManagersHelper.launchDeviceManager(subMonitor.newChild(1), new DeviceManagerLaunchConfiguration(domainName, devConfig,
				(DebugLevel) null, null));
		}
		if (retVal.isOK()) {
			return Status.OK_STATUS;
		}
		return retVal;
	}

	/**
	 * @since 3.0
	 * @deprecated Use {@link #launchDeviceManager(IProgressMonitor, DeviceManagerLaunchConfiguration)}
	 */
	@Deprecated
	public static IStatus launchDeviceManagersWithDebug(final IProgressMonitor monitor, final String domainName,
		final Map<DeviceConfiguration, Integer> devicesMap) {
		Assert.isNotNull(devicesMap);
		final SubMonitor subMonitor = SubMonitor.convert(monitor, "Launch Device Managers", devicesMap.size());
		final MultiStatus retVal = new MultiStatus(SdrUiPlugin.PLUGIN_ID, IStatus.OK, "Failed to launch Device Managers...", null);

		for (final Entry<DeviceConfiguration, Integer> ent : devicesMap.entrySet()) {
			retVal.add(LaunchDeviceManagersHelper.launchDeviceManager(subMonitor.newChild(1), new DeviceManagerLaunchConfiguration(domainName, ent.getKey(),
				ent.getValue(), null)));
		}
		if (retVal.isOK()) {
			return Status.OK_STATUS;
		}

		return retVal;
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
		return SdrUiPlugin.getDefault().getPreferenceStore().getString(SdrUiPreferenceConstants.PREF_DEFAULT_DEVICE_MANAGER_NAME) + " " + devConfig.getName();
	}
}
