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
package gov.redhawk.ide.sdr.ui.internal.handlers;

import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.ui.NodeBooterLauncherUtil;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.ide.sdr.ui.preferences.SdrUiPreferenceConstants;
import gov.redhawk.ide.sdr.ui.util.LaunchDeviceManagersHelper;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.model.sca.ScaDomainManager;
import gov.redhawk.sca.ScaPlugin;
import gov.redhawk.sca.preferences.ScaPreferenceConstants;
import gov.redhawk.sca.ui.ScaUiPlugin;

import java.util.Collections;
import java.util.Map;

import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.dcd.provider.DcdItemProviderAdapterFactory;
import mil.jpeojtrs.sca.dmd.DomainManagerConfiguration;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.externaltools.internal.model.IExternalToolConstants;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.statushandlers.StatusManager;

public class LaunchDomainManager extends AbstractHandler implements IHandler {

	private String newDomainName;

	private int debugLevel;

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection ss = (IStructuredSelection) selection;
			for (final Object obj : ss.toArray()) {
				if (obj instanceof SdrRoot) {
					final SdrRoot sdrRoot = (SdrRoot) obj;

					final DomainManagerConfiguration domain = sdrRoot.getDomainConfiguration();
					if (domain == null) {
						StatusManager.getManager().handle(new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, "No Domain Configuration available."),
						        StatusManager.SHOW);
						continue;
					}

					prepareDomainManager(domain, event);
				}
			}
		}
		return null;
	}

	public void executeWithDomainManagerConfiguration(final DomainManagerConfiguration domain, final ExecutionEvent event, final boolean nameChanged,
	        final int debugLevel) {
		if (nameChanged) {
			this.newDomainName = domain.getName();
		}

		this.debugLevel = debugLevel;

		prepareDomainManager(domain, event);
	}

	private void prepareDomainManager(final DomainManagerConfiguration incomingDomain, final ExecutionEvent event) {
		final String namingService = ScaUiPlugin.getDefault().getScaPreferenceStore().getString(ScaPreferenceConstants.SCA_DEFAULT_NAMING_SERVICE);

		final boolean domainOnline = LaunchDomainManager.isDomainOnline(incomingDomain.getName(), namingService);
		if (domainOnline) {
			final MessageDialog dialog = new MessageDialog(HandlerUtil.getActiveShell(event), "Launch Domain", null,
			        "An object is already registered with the naming service of the name: \"" + incomingDomain.getName() + "\", would you like to continue?",
			        0, new String[] { "Yes", "No", "Rename Domain" }, 1);
			final InputDialog renameDialog = new InputDialog(HandlerUtil.getActiveShell(event), "Rename Conflicting Domain", "The " + incomingDomain.getName()
			        + " already exists, please rename it to continue:", incomingDomain.getName(), new IInputValidator() {

				@Override
				public String isValid(final String newText) {
					if (!newText.equals(incomingDomain.getName())) {
						return null;
					}
					return newText;
				}
			});

			int result = dialog.open();

			if (result == 1) {
				return;
			} else if (result == 2) {
				result = renameDialog.open();

				if (result == 0) {
					this.newDomainName = renameDialog.getValue();
					incomingDomain.setName(renameDialog.getValue());
				} else {
					return;
				}
			}
		}

		final Map<String, String> connectionProperties = Collections.singletonMap(ScaDomainManager.NAMING_SERVICE_PROP, namingService);

		final Job launchJob = new Job("Launch Domain: " + incomingDomain.getName()) {

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				ScaDomainManager connection = ScaPlugin.getDefault().getDomainManagerRegistry().findDomain(incomingDomain.getName());

				if (connection == null) {
					connection = ScaPlugin.getDefault().getDomainManagerRegistry().createDomain(incomingDomain.getName(), false, connectionProperties);
				}
				final ScaDomainManager config = connection;
				final String domainName = config.getName();
				monitor.beginTask("Launching domain " + domainName, 2);
				try {
					final UIJob launchJob = new UIJob("Launch Domain " + domainName) {

						@Override
						public IStatus runInUIThread(final IProgressMonitor monitor) {
							return launchDomainManager(event, incomingDomain, new SubProgressMonitor(monitor, 1));
						}
					};
					final Job connectJob = new Job("Connecting to domain " + domainName) {

						@Override
						protected IStatus run(final IProgressMonitor monitor) {
							return connectToDomain(config, new SubProgressMonitor(monitor, 1));
						}

					};
					final UIJob launchDeviceJob = new UIJob("Launch Device Manager ") {

						@Override
						public IStatus runInUIThread(final IProgressMonitor monitor) {
							return startDeviceManager(monitor, domainName, event);
						}
					};
					launchJob.addJobChangeListener(new JobChangeAdapter() {
						@Override
						public void done(final org.eclipse.core.runtime.jobs.IJobChangeEvent event) {
							if (event.getJob().getResult() != null && event.getJob().getResult().isOK()) {
								connectJob.schedule();
							}
						}
					});
					connectJob.addJobChangeListener(new JobChangeAdapter() {
						@Override
						public void done(final org.eclipse.core.runtime.jobs.IJobChangeEvent event) {
							if (event.getJob().getResult() != null && event.getJob().getResult().isOK()) {
								launchDeviceJob.schedule();
							}
						}
					});
					return Status.OK_STATUS;
				} finally {
					monitor.done();
				}
			}
		};

		launchJob.setPriority(Job.LONG);
		launchJob.schedule();
	}

	private IStatus launchDomainManager(final ExecutionEvent event, final DomainManagerConfiguration domain, final IProgressMonitor monitor) {
		final String launchConfigName;
		launchConfigName = getLaunchConfigName(domain);

		monitor.beginTask("Launching NodeBooter Process", 2);
		try {
			try {
				final StringBuilder arguments = new StringBuilder();

				monitor.subTask("Setup launch config...");
				final ILaunchConfigurationWorkingCopy config = NodeBooterLauncherUtil.createNodeBooterLaunchConfig(launchConfigName);

				arguments.append("-D " + domain.getDomainManagerSoftPkg().getLocalFile().getName());

				if (this.newDomainName != null) {
					arguments.append(" --domainname \"" + this.newDomainName + "\"");
				}

				if (this.debugLevel != 3) { // SUPPRESS CHECKSTYLE MagicNumber
					arguments.append(" -debug " + this.debugLevel);
				}

				arguments.append(" --nopersist");

				config.setAttribute(IExternalToolConstants.ATTR_TOOL_ARGUMENTS, arguments.toString());
				monitor.worked(1);

				monitor.subTask("Launching process...");
				final ILaunch launched = NodeBooterLauncherUtil.launch(config);
				// Give things a second to ensure they started correctly
				try {
					Thread.sleep(1000); // SUPPRESS CHECKSTYLE MagicNumber
				} catch (final InterruptedException e) {
					// PASS
				}
				if (launched.isTerminated()) {
					return new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, "Domain manager failed to launch.  Check console output");
				}
				monitor.worked(1);
			} catch (final CoreException e) {
				return new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, e.getStatus().getMessage(), e);
			}

			return Status.OK_STATUS;
		} finally {
			monitor.done();
		}

	}

	private IStatus connectToDomain(final ScaDomainManager connection, final IProgressMonitor monitor) {
		try {
			monitor.beginTask("Connecting to domain " + connection.getName(), 1);
			for (int tries = 0; tries < getMaxConnectionAttempts(); tries++) {
				try {
					connection.connect(new SubProgressMonitor(monitor, 1), RefreshDepth.SELF);
					break;
				} catch (final Exception e) { // SUPPRESS CHECKSTYLE Logged Catch all exception
					monitor.worked(-1);
					if (tries + 1 < getMaxConnectionAttempts()) {
						try {
							Thread.sleep(getConnectionAttemptInterval());
						} catch (final InterruptedException e1) {
							// PASS
						}
					} else {
						return new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, "Failed to connect to domain: " + connection.getName(), e);
					}
				}
			}
			return Status.OK_STATUS;
		} finally {
			monitor.done();
		}
	}

	@SuppressWarnings("unchecked")
	private IStatus startDeviceManager(final IProgressMonitor monitor, final String domainName, final ExecutionEvent event) {
		// auto connect to launched domain
		final SdrRoot sdrRoot = SdrUiPlugin.getDefault().getTargetSdrRoot();
		if (!sdrRoot.getNodesContainer().getNodes().isEmpty()) {
			final DcdItemProviderAdapterFactory factory = new DcdItemProviderAdapterFactory();
			final LaunchDeviceManagerDialog dialog = new LaunchDeviceManagerDialog(HandlerUtil.getActiveShell(event), factory);
			dialog.setInput(sdrRoot);
			if (dialog.open() == IStatus.OK) {
				final Object[] dialogResult = dialog.getResult();
				final Map<DeviceConfiguration, Integer> devicesMap = (Map<DeviceConfiguration, Integer>) dialogResult[0];
				return LaunchDeviceManagersHelper.launchDeviceManagersWithDebug(monitor, domainName, devicesMap);
			}
			factory.dispose();
		}
		return Status.OK_STATUS;
	}

	private long getConnectionAttemptInterval() {
		return SdrUiPlugin.getDefault().getPreferenceStore().getLong(SdrUiPreferenceConstants.PREF_AUTO_CONNECT_ATTEMPT_RETRY_INTERVAL_MSEC);
	}

	private int getMaxConnectionAttempts() {
		return SdrUiPlugin.getDefault().getPreferenceStore().getInt(SdrUiPreferenceConstants.PREF_AUTO_CONNECT_MAX_CONNECTION_ATTEMPTS);
	}

	private String getLaunchConfigName(final DomainManagerConfiguration domain) {
		return SdrUiPlugin.getDefault().getPreferenceStore().getString(SdrUiPreferenceConstants.PREF_DEFAULT_DOMAIN_MANAGER_NAME) + " " + domain.getName();
	}

	/**
	 * @deprecated use ScaPlugin.isDomainOnline()
	 * 
	 * @param domainName
	 * @param namingService
	 * @return
	 */
	@Deprecated
	private static boolean isDomainOnline(final String domainName, final String namingService) {
		return ScaPlugin.isDomainOnline(domainName, namingService);
	}
}
