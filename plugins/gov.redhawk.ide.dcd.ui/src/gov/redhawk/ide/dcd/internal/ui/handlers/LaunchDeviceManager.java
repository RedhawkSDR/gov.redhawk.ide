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

import gov.redhawk.ide.sdr.ui.util.LaunchDeviceManagersHelper;
import gov.redhawk.model.sca.ScaDomainManager;
import gov.redhawk.model.sca.provider.ScaItemProviderAdapterFactory;
import gov.redhawk.sca.ScaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import mil.jpeojtrs.sca.dcd.DeviceConfiguration;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.progress.UIJob;

public class LaunchDeviceManager extends AbstractHandler implements IHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		final Map<DeviceConfiguration, Integer> domainMap = new HashMap<DeviceConfiguration, Integer>();
		
		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection ss = (IStructuredSelection) selection;
			for (final Object obj : ss.toArray()) {
				if (obj instanceof DeviceConfiguration) {
					domainMap.put((DeviceConfiguration) obj, null);  // The debug level will be correctly placed in the map later.
				}
			}

			if (domainMap.size() > 0) {

				final LaunchDeviceManagerDialog dialog = new LaunchDeviceManagerDialog(HandlerUtil.getActiveShell(event));
				final ScaItemProviderAdapterFactory factory = new ScaItemProviderAdapterFactory();

				dialog.setTitle("Launch Device Manager");
				dialog.setMessage("Select the Domain on which to launch the device manager(s):");
				dialog.setLabelProvider(new AdapterFactoryLabelProvider(factory));
				dialog.setContentProvider(new ArrayContentProvider());
				final ArrayList<Object> input = new ArrayList<Object>();
				input.addAll(ScaPlugin.getDefault().getDomainManagerRegistry().getDomains());
				final String defaultSelection = "<Default>";
				input.add(defaultSelection);
				dialog.setInput(input);

				if (dialog.open() == Window.OK) {
					final Object[] result = dialog.getResult();
					ScaDomainManager tmpDomMgr = null;
					int debugLevel = -1;

					if (result.length > 0 && result[0] instanceof ScaDomainManager) {
						tmpDomMgr = (ScaDomainManager) result[0];
					}
					
					debugLevel = dialog.getDebugLevel();
					// If Default was chosen then tmpDomMgr is null.
					final ScaDomainManager domMgr = tmpDomMgr;
					
					// Go through and set the debug Level for them all.  Currently the GUI only allows a single debug level to be applied to all of them.
					for (Entry<DeviceConfiguration, Integer> entry : domainMap.entrySet()) {
					    entry.setValue(debugLevel);
					}
					
					final Job launchDeviceManagerJob = new UIJob("Launching Device Manager(s)") {

						@Override
						public IStatus runInUIThread(final IProgressMonitor monitor) {
							String domainName = (domMgr == null) ? "" : domMgr.getName(); 
							return LaunchDeviceManagersHelper.launchDeviceManagersWithDebug(monitor, domainName, domainMap);
						}
					};
					if (domMgr != null) {
						final Job refreshJob = new Job("Refreshing Device Managers of " + domMgr.getName()) {

							@Override
							protected IStatus run(final IProgressMonitor monitor) {
								domMgr.fetchDeviceManagers(monitor);
								return Status.OK_STATUS;
							}

						};
						launchDeviceManagerJob.addJobChangeListener(new JobChangeAdapter() {
							@Override
							public void done(final IJobChangeEvent event) {
								refreshJob.schedule();
							}
						});
					}
					launchDeviceManagerJob.setPriority(Job.LONG);
					launchDeviceManagerJob.schedule();
				}

			}
		}
		return null;
	}

}
