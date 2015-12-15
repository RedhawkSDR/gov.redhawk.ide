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

import gov.redhawk.ide.sdr.nodebooter.DeviceManagerLaunchConfiguration;
import gov.redhawk.ide.sdr.nodebooter.DeviceManagerLauncherUtil;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.ide.sdr.ui.preferences.SdrUiPreferenceConstants;
import gov.redhawk.model.sca.RefreshDepth;
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

public class LaunchDeviceManager extends AbstractHandler implements IHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		if (!(selection instanceof IStructuredSelection)) {
			return null;
		}
		final IStructuredSelection ss = (IStructuredSelection) selection;

		List<DeviceConfiguration> dcds = new ArrayList<DeviceConfiguration>();
		for (final Object obj : ss.toArray()) {
			if (obj instanceof DeviceConfiguration) {
				dcds.add((DeviceConfiguration) obj);
			}
		}
		if (dcds.size() == 0) {
			return null;
		}

		final LaunchDeviceManagerDialog dialog = new LaunchDeviceManagerDialog(HandlerUtil.getActiveShell(event));
		if (dialog.open() != Window.OK) {
			return null;
		}

		// Determine domain manager
		final Object[] result = dialog.getResult();
		ScaDomainManager tmpDomMgr = null;
		if (result.length > 0 && result[0] instanceof ScaDomainManager) {
			tmpDomMgr = (ScaDomainManager) result[0];
		}
		final ScaDomainManager domMgr = tmpDomMgr;
		String domainName = (domMgr == null) ? "" : domMgr.getLabel();

		// Create launch configs
		final List<DeviceManagerLaunchConfiguration> launchConfigs = new ArrayList<DeviceManagerLaunchConfiguration>();
		for (DeviceConfiguration dcd : dcds) {
			String devMgrLaunchConfigName = getDevMgrLaunchConfigName(dcd);
			launchConfigs.add(new DeviceManagerLaunchConfiguration(domainName, dcd, dialog.getDebugLevel(), dialog.getArguments(), devMgrLaunchConfigName));
		}

		final Job refreshJob;
		if (domMgr != null) {
			refreshJob = new Job("Refreshing Device Managers of " + domMgr.getLabel()) {
				@Override
				protected IStatus run(final IProgressMonitor monitor) {
					domMgr.fetchDeviceManagers(monitor, RefreshDepth.SELF);
					return Status.OK_STATUS;
				}

			};
		} else {
			refreshJob = null;
		}

		final Job launchDeviceManagerJob = new Job("Launching Device Manager(s)") {
			@Override
			public IStatus run(final IProgressMonitor monitor) {
				IStatus retVal = DeviceManagerLauncherUtil.launchDeviceManagers(launchConfigs, monitor);
				if (retVal.isOK() && refreshJob != null) {
					refreshJob.schedule();
				}
				return retVal;
			}
		};

		launchDeviceManagerJob.setPriority(Job.LONG);
		launchDeviceManagerJob.schedule();
		return null;
	}

	private static String getDevMgrLaunchConfigName(final DeviceConfiguration dcd) {
		String name = (dcd.getName() != null) ? dcd.getName() : dcd.getId();
		return SdrUiPlugin.getDefault().getPreferenceStore().getString(SdrUiPreferenceConstants.PREF_DEFAULT_DEVICE_MANAGER_NAME) + " " + name;
	}
}
