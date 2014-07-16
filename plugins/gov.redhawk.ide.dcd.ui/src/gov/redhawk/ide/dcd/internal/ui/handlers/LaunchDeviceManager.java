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
package gov.redhawk.ide.dcd.internal.ui.handlers;

import gov.redhawk.ide.sdr.ui.util.DebugLevel;
import gov.redhawk.ide.sdr.ui.util.DeviceManagerLaunchConfiguration;
import gov.redhawk.ide.sdr.ui.util.LaunchDeviceManagersHelper;
import gov.redhawk.model.sca.ScaDomainManager;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.dcd.DeviceConfiguration;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.progress.UIJob;

public class LaunchDeviceManager extends AbstractHandler implements IHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		final List<DeviceManagerLaunchConfiguration> launchConfigs = new ArrayList<DeviceManagerLaunchConfiguration>();

		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection ss = (IStructuredSelection) selection;
			for (final Object obj : ss.toArray()) {
				if (obj instanceof DeviceConfiguration) {
					launchConfigs.add(new DeviceManagerLaunchConfiguration(null, (DeviceConfiguration) obj, (DebugLevel) null, null)); // The debug level will be correctly placed in later. 
				}
			}

			if (launchConfigs.size() > 0) {

				final LaunchDeviceManagerDialog dialog = new LaunchDeviceManagerDialog(HandlerUtil.getActiveShell(event));

				if (dialog.open() == Window.OK) {
					final Object[] result = dialog.getResult();
					ScaDomainManager tmpDomMgr = null;

					if (result.length > 0 && result[0] instanceof ScaDomainManager) {
						tmpDomMgr = (ScaDomainManager) result[0];
					}

					DeviceManagerLaunchConfiguration conf = dialog.getConfiguration();
					// If Default was chosen then tmpDomMgr is null.
					final ScaDomainManager domMgr = tmpDomMgr;
					String domainName = (domMgr == null) ? "" : domMgr.getLabel();

					// Go through and set the debug Level for them all.  Currently the GUI only allows a single debug level to be applied to all of them.
					for (DeviceManagerLaunchConfiguration entry : launchConfigs) {
						entry.setDebugLevel(conf.getDebugLevel());
						entry.setAdditionalArguments(conf.getAdditionalArguments());
						entry.setDomainName(domainName);
					}
					final Job refreshJob;
					if (domMgr != null) {
						refreshJob = new Job("Refreshing Device Managers of " + domMgr.getLabel()) {

							@Override
							protected IStatus run(final IProgressMonitor monitor) {
								domMgr.fetchDeviceManagers(monitor);
								return Status.OK_STATUS;
							}

						};
					} else {
						refreshJob = null;
					}

					final Job launchDeviceManagerJob = new UIJob("Launching Device Manager(s)") {

						@Override
						public IStatus runInUIThread(final IProgressMonitor monitor) {
							IStatus retVal = LaunchDeviceManagersHelper.launchDeviceManagers(monitor, launchConfigs);
							if (retVal.isOK() && refreshJob != null) {
								refreshJob.schedule();
							}
							return retVal;
						}
					};

					launchDeviceManagerJob.setPriority(Job.LONG);
					launchDeviceManagerJob.schedule();
				}

			}
		}
		return null;
	}

}
