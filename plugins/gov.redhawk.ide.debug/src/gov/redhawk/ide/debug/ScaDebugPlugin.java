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

import gov.redhawk.ide.debug.internal.LaunchConfigurationFactoryRegistry;
import gov.redhawk.ide.debug.internal.LauncherVariableRegistry;
import gov.redhawk.ide.debug.internal.ScaDebugInstance;
import gov.redhawk.model.sca.commands.ScaModelCommand;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.annotation.Nullable;
import org.jacorb.JacorbActivator;
import org.osgi.framework.BundleContext;

import ExtendedCF.Sandbox;


/**
 * 
 */
public class ScaDebugPlugin extends Plugin {
	public static final String ID = "gov.redhawk.ide.debug";

	private static ScaDebugPlugin instance;

	@Override
	public void start(final BundleContext context) throws Exception {
		ScaDebugPlugin.instance = this;
		super.start(context);
		JacorbActivator.getDefault().init();
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		super.stop(context);
		ScaModelCommand.execute(getLocalSca(), new ScaModelCommand() {
			
			@Override
			public void execute() {
				getLocalSca().dispose();
			}
		});
		ScaDebugPlugin.instance = null;
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
