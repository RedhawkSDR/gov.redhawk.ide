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
import gov.redhawk.ide.sdr.ui.util.DebugLevel;
import gov.redhawk.ide.sdr.ui.util.DeviceManagerLaunchConfiguration;
import gov.redhawk.ide.sdr.ui.util.DomainManagerLaunchConfiguration;
import gov.redhawk.ide.sdr.ui.util.LaunchDeviceManagersHelper;
import gov.redhawk.model.sca.DomainConnectionState;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.model.sca.ScaDomainManager;
import gov.redhawk.model.sca.ScaDomainManagerRegistry;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.sca.ScaPlugin;
import gov.redhawk.sca.preferences.ScaPreferenceConstants;
import gov.redhawk.sca.ui.ScaUiPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import mil.jpeojtrs.sca.dmd.DomainManagerConfiguration;
import mil.jpeojtrs.sca.util.CorbaUtils;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.externaltools.internal.IExternalToolConstants;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.statushandlers.StatusManager;

public class LaunchDomainManagerWithOptions extends AbstractHandler implements IHandler {

	private static final int LAUNCH_WAIT_TIME = 1000;
	private List<DeviceManagerLaunchConfiguration> deviceManagers = new ArrayList<DeviceManagerLaunchConfiguration>();
	private DomainManagerLaunchConfiguration model = new DomainManagerLaunchConfiguration();

	private Shell displayContext;

	private ScaDomainManagerRegistry dmReg;

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		displayContext = HandlerUtil.getActiveShell(event);
		dmReg = ScaPlugin.getDefault().getDomainManagerRegistry(displayContext);
		final Shell shell = HandlerUtil.getActiveShell(event);
		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection ss = (IStructuredSelection) selection;
			for (final Object obj : ss.toArray()) {
				if (obj instanceof SdrRoot) {
					final SdrRoot sdrRoot = (SdrRoot) obj;
					Assert.isNotNull(sdrRoot);
					if (sdrRoot.getLoadStatus() == null) {
						ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
						try {
							dialog.run(true, true, new IRunnableWithProgress() {
								
								@Override
								public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
									monitor.beginTask("Loading Target SDR..", IProgressMonitor.UNKNOWN);
									try {
										CorbaUtils.invoke(new Callable<Object>() {

											@Override
											public Object call() throws Exception {
												sdrRoot.load(monitor);
												return null;
											}
											
										}, monitor);
									} catch (CoreException e) {
										throw new InvocationTargetException(e);
									}
									
								}
							});
						} catch (InvocationTargetException e) {
							StatusManager.getManager().handle(new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, "Failed to load SDR.", e.getCause()),
								StatusManager.SHOW | StatusManager.LOG);
						} catch (InterruptedException e) {
							return null;
						}
					}

					final DomainManagerConfiguration domain = sdrRoot.getDomainConfiguration();
					if (domain == null) {
						StatusManager.getManager().handle(new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, "No Domain Configuration available."),
							StatusManager.SHOW);
						continue;
					}
					model.setDomainName(domain.getName());

					final LaunchDomainManagerWithOptionsDialog dialog = new LaunchDomainManagerWithOptionsDialog(shell, model,
						sdrRoot);
					if (dialog.open() == IStatus.OK) {
						deviceManagers = dialog.getDeviceManagerLaunchConfigurations();
						prepareDomainManager(domain, event);
					}
				}
			}
		}

		return null;
	}

	private void prepareDomainManager(final DomainManagerConfiguration incomingDomain, final ExecutionEvent event) {
		final String namingService = ScaUiPlugin.getDefault().getScaPreferenceStore().getString(ScaPreferenceConstants.SCA_DEFAULT_NAMING_SERVICE);

		final Map<String, String> connectionProperties = Collections.singletonMap(ScaDomainManager.NAMING_SERVICE_PROP, namingService);

		final Job launchJob = new Job("Launch Domain: " + model.getDomainName()) {

			@Override
			protected IStatus run(final IProgressMonitor parentMonitor) {
				SubMonitor subMonitor = SubMonitor.convert(parentMonitor, "Launching Domain " + model.getDomainName(), 2);
				try {
					if (ScaPlugin.isDomainOnline(model.getDomainName(), subMonitor.newChild(1))) {
						return new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, "Refusing to launch domain that already exists on name server");
					}
				} catch (CoreException e) {
					return new Status(e.getStatus().getSeverity(), SdrUiPlugin.PLUGIN_ID, e.getLocalizedMessage(), e);
				} catch (InterruptedException e) {
					return Status.CANCEL_STATUS;
				}

				final ScaDomainManager connection = dmReg.findDomain(model.getDomainName());

				if (connection == null) {
					ScaModelCommand.execute(dmReg, new ScaModelCommand() {
						@Override
						public void execute() {
							dmReg.createDomain(model.getDomainName(), false, connectionProperties);
						}
					});
				} else {
					if (connection.getState() == DomainConnectionState.CONNECTED) {
						connection.disconnect();
					}
				}

				final ScaDomainManager config = dmReg.findDomain(model.getDomainName());

				final String domainName = config.getName();
				subMonitor.newChild(1).beginTask("Launching domain " + domainName, 2);
				try {
					final UIJob launcherJob = new UIJob("Domain Launcher") {

						@Override
						public IStatus runInUIThread(final IProgressMonitor monitor) {
							final IStatus retVal = launchDomainManager(event, incomingDomain, new SubProgressMonitor(monitor, 1));
							if (retVal != null && retVal.isOK()) {
								final Job connectJob = new Job("Connect to Domain: " + domainName) {

									@Override
									protected IStatus run(final IProgressMonitor monitor) {
										final IStatus retVal = connectToDomain(config, new SubProgressMonitor(monitor, 1));
										if (retVal != null && retVal.isOK()) {
											final UIJob launchDeviceManagers = new UIJob("Launching Device Managers...") {

												@Override
												public IStatus runInUIThread(final IProgressMonitor monitor) {
													for (DeviceManagerLaunchConfiguration conf : deviceManagers) {
														conf.setDomainName(domainName);
													}
													return LaunchDeviceManagersHelper.launchDeviceManagers(monitor, deviceManagers);
												}

											};
											launchDeviceManagers.setPriority(Job.LONG);
											launchDeviceManagers.schedule();
										}
										return retVal;
									}
								};
								connectJob.setPriority(Job.LONG);
								// TODO A bit of a hack
								connectJob.schedule(2000);
							}
							return retVal;
						}

					};
					launcherJob.setPriority(Job.LONG);
					launcherJob.schedule();
					return Status.OK_STATUS;
				} finally {
					subMonitor.done();
				}
			}
		};

		launchJob.setPriority(Job.LONG);
		launchJob.schedule();
	}

	private IStatus launchDomainManager(final ExecutionEvent event, final DomainManagerConfiguration domain, final IProgressMonitor monitor) {
		final String launchConfigName;
		launchConfigName = getLaunchConfigName(model.getDomainName());

		monitor.beginTask("Launching NodeBooter (DomainManager) Process", 2);
		try {
			try {
				final StringBuilder arguments = new StringBuilder();

				monitor.subTask("Seting up launch configuration...");
				final ILaunchConfigurationWorkingCopy config = NodeBooterLauncherUtil.createNodeBooterLaunchConfig(launchConfigName);

				arguments.append("-D ").append(domain.getDomainManagerSoftPkg().getLocalFile().getName());

				if (model.getDomainName() != null) {
					arguments.append(" --domainname \"" + model.getDomainName() + "\"");
				}

				if (model.getDebugLevel() != DebugLevel.Info) {
					arguments.append(" -debug ").append(model.getDebugLevel().ordinal());
				}

				arguments.append(" --nopersist");

				if (model.getArguments() != null && !model.getArguments().trim().isEmpty()) {
					arguments.append(" ").append(model.getArguments().trim());
				}

				config.setAttribute(IExternalToolConstants.ATTR_TOOL_ARGUMENTS, arguments.toString());
				monitor.worked(1);

				monitor.subTask("Launching process...");
				final ILaunch launched = NodeBooterLauncherUtil.launch(config);
				// Give things a second to ensure they started correctly
				try {
					Thread.sleep(LaunchDomainManagerWithOptions.LAUNCH_WAIT_TIME);
				} catch (final InterruptedException e) {
					// PASS
				}
				if (launched.isTerminated()) {
					int exitValue = launched.getProcesses()[0].getExitValue();
					if (exitValue != 0) {
						return new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID,
							"Domain Manager failed to launch. Check console output. Exit status = " + exitValue);
					}
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

	private long getConnectionAttemptInterval() {
		return SdrUiPlugin.getDefault().getPreferenceStore().getLong(SdrUiPreferenceConstants.PREF_AUTO_CONNECT_ATTEMPT_RETRY_INTERVAL_MSEC);
	}

	private int getMaxConnectionAttempts() {
		return SdrUiPlugin.getDefault().getPreferenceStore().getInt(SdrUiPreferenceConstants.PREF_AUTO_CONNECT_MAX_CONNECTION_ATTEMPTS);
	}

	private String getLaunchConfigName(final String domainName) {
		return SdrUiPlugin.getDefault().getPreferenceStore().getString(SdrUiPreferenceConstants.PREF_DEFAULT_DOMAIN_MANAGER_NAME) + " " + domainName;
	}
}
