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

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.debug.core.ILaunch;

import gov.redhawk.ide.sdr.IdeSdrActivator;

public class DeviceManagerLauncherUtil {

	private static final int LAUNCH_WAIT_TIME = 1000;

	private DeviceManagerLauncherUtil() {
	}

	/**
	 * See {@link #launchDeviceManager(DeviceManagerLaunchConfiguration, IProgressMonitor)}
	 * @param devMgrs
	 * @param monitor
	 * @return
	 */
	public static IStatus launchDeviceManagers(List<DeviceManagerLaunchConfiguration> devMgrs, IProgressMonitor monitor) {
		final SubMonitor progress = SubMonitor.convert(monitor, "Launch Device Managers", devMgrs.size());
		final MultiStatus retVal = new MultiStatus(IdeSdrActivator.PLUGIN_ID, IStatus.OK, "Failed to launch Device Managers...", null);

		for (DeviceManagerLaunchConfiguration devMgr : devMgrs) {
			IStatus status = launchDeviceManager(devMgr, progress.newChild(1));
			if (!status.isOK()) {
				retVal.add(status);
			}
		}

		if (retVal.isOK()) {
			return Status.OK_STATUS;
		}
		return retVal;
	}

	/**
	 * Uses the Eclipse debug system to launch nodeBooter to start a device manager.
	 * @param devMgr Details on the device manager to start
	 * @param monitor
	 * @return
	 */
	public static IStatus launchDeviceManager(DeviceManagerLaunchConfiguration devMgr, IProgressMonitor monitor) {
		SubMonitor progress = SubMonitor.convert(monitor, "Launching device manager", 2);
		try {
			final StringBuilder arguments = new StringBuilder();
			arguments.append("-d ");
			arguments.append(devMgr.getDcd().eResource().getURI().path());

			if (devMgr.getDomainName() != null) {
				arguments.append(" --domainname \"" + devMgr.getDomainName() + "\"");
			}

			if (devMgr.getDebugLevel() != null && devMgr.getDebugLevel() != DebugLevel.Info) { // SUPPRESS CHECKSTYLE
																								// MagicNumber
				arguments.append(" -debug " + devMgr.getDebugLevel().getNodeBooterString());
			}

			if (devMgr.getAdditionalArguments() != null && !devMgr.getAdditionalArguments().trim().isEmpty()) {
				arguments.append(" ");
				arguments.append(devMgr.getAdditionalArguments().trim());
			}

			NodeBooterLauncherUtil launcher = NodeBooterLauncherUtil.getInstance();
			final ILaunch launch = launcher.launch(devMgr.getLaunchConfigName(), arguments.toString(), progress.newChild(1));

			// Give things a second to ensure they started correctly
			Thread.sleep(LAUNCH_WAIT_TIME);
			progress.worked(1);

			if (launch.isTerminated()) {
				launch.getLaunchConfiguration().getName();
				int exitValue = launch.getProcesses()[0].getExitValue();
				if (exitValue != 0) {
					String errorMsg = String.format("%s failed to launch. Check console output. Exit code %d.", devMgr.getLaunchConfigName(), exitValue);
					return new Status(IStatus.ERROR, IdeSdrActivator.PLUGIN_ID, errorMsg);
				}
			}
			return Status.OK_STATUS;
		} catch (final CoreException e) {
			return new Status(e.getStatus().getSeverity(), IdeSdrActivator.PLUGIN_ID, e.getLocalizedMessage(), e);
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			return Status.CANCEL_STATUS;
		} finally {
			progress.done();
		}
	}
}
