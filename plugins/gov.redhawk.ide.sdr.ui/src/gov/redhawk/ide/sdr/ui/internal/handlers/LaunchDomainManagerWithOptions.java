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

import gov.redhawk.ide.sdr.IdeSdrActivator;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.nodebooter.DeviceManagerLaunchConfiguration;
import gov.redhawk.ide.sdr.nodebooter.DeviceManagerLauncherUtil;
import gov.redhawk.ide.sdr.nodebooter.DomainManagerLauncherUtil;
import gov.redhawk.ide.sdr.preferences.IdeSdrPreferenceConstants;
import gov.redhawk.ide.sdr.nodebooter.DomainManagerLaunchConfiguration;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.model.sca.DomainConnectionState;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.model.sca.ScaDomainManager;
import gov.redhawk.model.sca.ScaDomainManagerRegistry;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.sca.ScaPlugin;
import gov.redhawk.sca.preferences.ScaPreferenceConstants;
import gov.redhawk.sca.ui.ScaUiPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.dmd.DomainManagerConfiguration;
import mil.jpeojtrs.sca.util.CorbaUtils;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.ui.statushandlers.StatusManager;

public class LaunchDomainManagerWithOptions extends AbstractHandler implements IHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection ss = (IStructuredSelection) selection;
			for (final Object obj : ss.toArray()) {
				if (obj instanceof SdrRoot) {
					showDialogAndLaunch(event, (SdrRoot) obj);
				}
			}
		}
		return null;
	}

	private void showDialogAndLaunch(ExecutionEvent event, final SdrRoot sdrRoot) {
		final Shell shell = HandlerUtil.getActiveShell(event);

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
				return;
			}
		}

		final DomainManagerConfiguration dmd = sdrRoot.getDomainConfiguration();
		if (dmd == null) {
			String msg = "No domain manager configuration (DMD) file was found. Ensure both the domain manager and its descriptor are in the SDRROOT.";
			StatusManager.getManager().handle(new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, msg), StatusManager.SHOW);
			return;
		}

		DomainManagerLaunchConfiguration domMgrLaunchConfig = new DomainManagerLaunchConfiguration();
		domMgrLaunchConfig.setDomainName(dmd.getName());
		final LaunchDomainManagerWithOptionsDialog dialog = new LaunchDomainManagerWithOptionsDialog(shell, domMgrLaunchConfig, sdrRoot);
		if (dialog.open() == IStatus.OK) {
			List<DeviceManagerLaunchConfiguration> devMgrLaunchConfigs = dialog.getDeviceManagerLaunchConfigurations();
			for (DeviceManagerLaunchConfiguration devMgrLaunchConfig : devMgrLaunchConfigs) {
				devMgrLaunchConfig.setLaunchConfigName(getDevMgrLaunchConfigName(devMgrLaunchConfig.getDcd()));
			}
			launch(domMgrLaunchConfig, devMgrLaunchConfigs, dmd, event);
		}
	}

	private void launch(final DomainManagerLaunchConfiguration domMgrLaunchConfig, final List<DeviceManagerLaunchConfiguration> devMgrLaunchConfigs,
		final DomainManagerConfiguration dmd, final ExecutionEvent event) {
		Shell displayContext = HandlerUtil.getActiveShell(event);
		final ScaDomainManagerRegistry dmReg = ScaPlugin.getDefault().getDomainManagerRegistry(displayContext);

		final String namingService = ScaUiPlugin.getDefault().getScaPreferenceStore().getString(ScaPreferenceConstants.SCA_DEFAULT_NAMING_SERVICE);
		final Map<String, String> connectionProperties = Collections.singletonMap(ScaDomainManager.NAMING_SERVICE_PROP, namingService);

		final Job launchJob = new Job("Launch Domain: " + domMgrLaunchConfig.getDomainName()) {

			@Override
			protected IStatus run(final IProgressMonitor parentMonitor) {
				SubMonitor subMonitor = SubMonitor.convert(parentMonitor, "Launching Domain " + domMgrLaunchConfig.getDomainName(), 2);
				try {
					if (ScaPlugin.isDomainOnline(domMgrLaunchConfig.getDomainName(), subMonitor.newChild(1))) {
						return new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, "Refusing to launch domain that already exists on name server");
					}
				} catch (CoreException e) {
					return new Status(e.getStatus().getSeverity(), SdrUiPlugin.PLUGIN_ID, e.getLocalizedMessage(), e);
				} catch (InterruptedException e) {
					return Status.CANCEL_STATUS;
				}

				final ScaDomainManager connection = dmReg.findDomain(domMgrLaunchConfig.getDomainName());

				if (connection == null) {
					ScaModelCommand.execute(dmReg, new ScaModelCommand() {
						@Override
						public void execute() {
							dmReg.createDomain(domMgrLaunchConfig.getLocalDomainName(), domMgrLaunchConfig.getDomainName(), false, connectionProperties);
						}
					});
				} else {
					if (connection.getState() == DomainConnectionState.CONNECTED) {
						connection.disconnect();
					}
				}

				final ScaDomainManager config = dmReg.findDomain(domMgrLaunchConfig.getDomainName());

				final String domainName = config.getLabel();
				final String launchConfigName = getDomMgrLaunchConfigName(domainName);
				final String spdPath = dmd.getDomainManagerSoftPkg().getLocalFile().getName();
				domMgrLaunchConfig.setLaunchConfigName(launchConfigName);
				domMgrLaunchConfig.setSpdPath(spdPath);
				subMonitor.newChild(1).beginTask("Launching domain " + domainName, 2);
				try {
					final Job launcherJob = new Job("Domain Launcher") {

						@Override
						public IStatus run(final IProgressMonitor monitor) {
							final IStatus retVal = DomainManagerLauncherUtil.launchDomainManager(domMgrLaunchConfig, new SubProgressMonitor(monitor, 1));
							if (retVal != null && retVal.isOK()) {
								final Job connectJob = new Job("Connect to Domain: " + domainName) {

									@Override
									protected IStatus run(final IProgressMonitor monitor) {
										final IStatus retVal = connectToDomain(config, new SubProgressMonitor(monitor, 1));
										if (retVal != null && retVal.isOK()) {
											final Job launchDeviceManagers = new Job("Launching Device Managers...") {

												@Override
												public IStatus run(final IProgressMonitor monitor) {
													for (DeviceManagerLaunchConfiguration devMgrLaunchConfig : devMgrLaunchConfigs) {
														final String launchConfigName = getDevMgrLaunchConfigName(devMgrLaunchConfig.getDcd());
														devMgrLaunchConfig.setDomainName(domainName);
														devMgrLaunchConfig.setLaunchConfigName(launchConfigName);
													}
													return DeviceManagerLauncherUtil.launchDeviceManagers(devMgrLaunchConfigs, monitor);
												}

											};
											launchDeviceManagers.setPriority(Job.LONG);
											launchDeviceManagers.schedule();
										}
										return retVal;
									}
								};
								connectJob.setPriority(Job.LONG);
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

	private IStatus connectToDomain(final ScaDomainManager connection, final IProgressMonitor monitor) {
		try {
			monitor.beginTask("Connecting to domain " + connection.getLabel(), 1);
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
						return new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, "Failed to connect to domain: " + connection.getLabel(), e);
					}
				}
			}
			return Status.OK_STATUS;
		} finally {
			monitor.done();
		}
	}

	private static long getConnectionAttemptInterval() {
		return new ScopedPreferenceStore(InstanceScope.INSTANCE, IdeSdrActivator.PLUGIN_ID).getLong(IdeSdrPreferenceConstants.PREF_AUTO_CONNECT_ATTEMPT_RETRY_INTERVAL_MSEC);
	}

	private static int getMaxConnectionAttempts() {
		return new ScopedPreferenceStore(InstanceScope.INSTANCE, IdeSdrActivator.PLUGIN_ID).getInt(IdeSdrPreferenceConstants.PREF_AUTO_CONNECT_MAX_CONNECTION_ATTEMPTS);
	}

	private static String getDomMgrLaunchConfigName(final String domainName) {
		return new ScopedPreferenceStore(InstanceScope.INSTANCE, IdeSdrActivator.PLUGIN_ID).getString(IdeSdrPreferenceConstants.PREF_DEFAULT_DOMAIN_MANAGER_NAME) + " " + domainName;
	}

	private static String getDevMgrLaunchConfigName(final DeviceConfiguration dcd) {
		String name = (dcd.getName() != null) ? dcd.getName() : dcd.getId();
		return new ScopedPreferenceStore(InstanceScope.INSTANCE, IdeSdrActivator.PLUGIN_ID).getString(IdeSdrPreferenceConstants.PREF_DEFAULT_DEVICE_MANAGER_NAME) + " " + name;
	}
}
