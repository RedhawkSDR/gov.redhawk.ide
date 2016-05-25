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
package gov.redhawk.ide.debug;

import gov.redhawk.ide.debug.impl.TerminateJob;
import gov.redhawk.ide.debug.internal.LaunchConfigurationFactoryRegistry;
import gov.redhawk.ide.debug.internal.LauncherVariableRegistry;
import gov.redhawk.ide.debug.internal.LaunchLogger;
import gov.redhawk.ide.debug.internal.ScaDebugInstance;
import gov.redhawk.model.sca.commands.ScaModelCommand;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.annotation.Nullable;
import org.jacorb.JacorbActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import ExtendedCF.Sandbox;

public class ScaDebugPlugin extends Plugin {

	public static final String ID = "gov.redhawk.ide.debug";

	/**
	 * Time to wait for jobs to complete that are terminating local launches. The wait occurs when the plugin is stopped.
	 */
	private static final long TERMINATE_JOB_WAIT_TIME_MS = 5000;

	private static ScaDebugPlugin instance;

	private static ServiceTracker<ILaunchLogger, ILaunchLogger> launchLoggerTracker;

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);

		ScaDebugPlugin.instance = this;

		launchLoggerTracker = new ServiceTracker<ILaunchLogger, ILaunchLogger>(context, ILaunchLogger.class, LaunchLogger.INSTANCE);
		launchLoggerTracker.open();

		JacorbActivator.getDefault().init();
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		// Dispose the local model
		ScaModelCommand.execute(getLocalSca(), new ScaModelCommand() {
			@Override
			public void execute() {
				getLocalSca().dispose();
			}
		});
		ScaDebugPlugin.instance = null;

		// Wait for termination jobs to complete
		IJobManager jobManager = Job.getJobManager();
		Job[] terminateJobs = jobManager.find(TerminateJob.class);
		long endTime = System.currentTimeMillis() + TERMINATE_JOB_WAIT_TIME_MS;
		boolean alldone = false;
		while (!alldone && endTime > System.currentTimeMillis()) {
			alldone = true;
			for (Job terminateJob : terminateJobs) {
				if (terminateJob.getResult() == null) {
					alldone = false;
					try {
						Thread.sleep(100);
					} catch (InterruptedException ex) {
						// PASS
					}
					break;
				}
			}
		}

		launchLoggerTracker.close();
		launchLoggerTracker = null;

		super.stop(context);
	}

	public LocalSca getLocalSca() {
		return ScaDebugInstance.INSTANCE.getLocalSca();
	}
	
	/**
	 * Get the instance of the Local SCA tool.  This method block until the tool is available.
	 * @since 6.0
	 */
	public LocalSca getLocalSca(IProgressMonitor monitor) throws CoreException {
		ScaDebugInstance.INSTANCE.init(null);
		return ScaDebugInstance.INSTANCE.getLocalSca();
	}
	

	/**
	 * Return the sandbox object.  It is recommend to use {@link #getSandbox(IProgressMonitor)} instead. Since this may return null.
	 * @since 4.0
	 * @return Sandbox may be null if the sandbox is not yet initialized
	 */
	@Nullable
	public Sandbox getSandbox() {
		return getLocalSca().getObj();
	}
	
	/**
	 * @since 6.0
	 * @return Sandbox may be null if the sandbox is not yet initialized
	 */
	public Sandbox getSandbox(IProgressMonitor monitor) throws CoreException {
		return getLocalSca(monitor).getObj();
	}
	

	public static ScaDebugPlugin getInstance() {
		return ScaDebugPlugin.instance;
	}

	/**
	 * @since 4.0
	 */
	public ILaunchConfigurationFactoryRegistry getLaunchConfigurationFactoryRegistry() {
		return LaunchConfigurationFactoryRegistry.INSTANCE;
	}

	/**
	 * @since 4.0
	 */
	public ILauncherVariableRegistry getLauncherVariableRegistry() {
		return LauncherVariableRegistry.INSTANCE;
	}

	/**
	 * @since 7.0
	 */
	public static void logWarning(final String msg, final Throwable e) {
		ScaDebugPlugin.instance.getLog().log(new Status(IStatus.WARNING, ScaDebugPlugin.ID, msg, e));
	}

	public static void logError(final String msg, final Throwable e) {
		ScaDebugPlugin.instance.getLog().log(new Status(IStatus.ERROR, ScaDebugPlugin.ID, msg, e));
	}
}
