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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.debug.core.ILaunch;

import gov.redhawk.ide.sdr.IdeSdrActivator;

public class DomainManagerLauncherUtil {

	private static final int LAUNCH_WAIT_TIME = 1000;

	private DomainManagerLauncherUtil() {
	}

	/**
	 * Uses the Eclipse debug system to launch nodeBooter to start a domain manager.
	 * @param domMgr Details on the domain manager to start
	 * @param monitor
	 * @return
	 */
	public static IStatus launchDomainManager(DomainManagerLaunchConfiguration domMgr, final IProgressMonitor monitor) {
		SubMonitor progress = SubMonitor.convert(monitor, "Launching domain manager", 2);
		try {
			final StringBuilder arguments = new StringBuilder();

			arguments.append("-D ").append(domMgr.getSpdPath());

			if (domMgr.getDomainName() != null) {
				arguments.append(" --domainname \"" + domMgr.getDomainName() + "\"");
			}

			if (domMgr.getDebugLevel() != DebugLevel.Info) {
				arguments.append(" -debug ").append(domMgr.getDebugLevel().getNodeBooterString());
			}

			arguments.append(" --nopersist");

			if (domMgr.getArguments() != null && !domMgr.getArguments().trim().isEmpty()) {
				arguments.append(" ").append(domMgr.getArguments().trim());
			}

			NodeBooterLauncherUtil launcher = NodeBooterLauncherUtil.getInstance();
			final ILaunch launch = launcher.launch(domMgr.getLaunchConfigName(), arguments.toString(), progress.newChild(1));

			// Give things a second to ensure they started correctly
			try {
				Thread.sleep(LAUNCH_WAIT_TIME);
			} catch (final InterruptedException e) {
				// PASS
			}
			progress.worked(1);

			if (launch.isTerminated()) {
				int exitValue = launch.getProcesses()[0].getExitValue();
				if (exitValue != 0) {
					String errorMsg = String.format("%s failed to launch. Check console output. Exit code %d.", domMgr.getLaunchConfigName(), exitValue);
					return new Status(IStatus.ERROR, IdeSdrActivator.PLUGIN_ID, errorMsg);
				}
			}
			return Status.OK_STATUS;
		} catch (final CoreException e) {
			return new Status(IStatus.ERROR, IdeSdrActivator.PLUGIN_ID, e.getStatus().getMessage(), e);
		} finally {
			progress.done();
		}
	}

}
