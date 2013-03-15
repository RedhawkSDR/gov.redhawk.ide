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
import gov.redhawk.ide.debug.internal.cf.extended.impl.SandboxImpl;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.osgi.framework.BundleContext;

import ExtendedCF.Sandbox;
import ExtendedCF.SandboxHelper;
import ExtendedCF.SandboxPOATie;


/**
 * 
 */
public class ScaDebugPlugin extends Plugin {
	public static final String ID = "gov.redhawk.ide.debug";

	private static ScaDebugPlugin instance;

	private Sandbox sandbox;

	@Override
	public void start(final BundleContext context) throws Exception {
		ScaDebugPlugin.instance = this;
		super.start(context);
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		super.stop(context);
		ScaDebugInstance.INSTANCE.getLocalSca().dispose();
		ScaDebugPlugin.instance = null;
	}

	public LocalSca getLocalSca() {
		return ScaDebugInstance.INSTANCE.getLocalSca();
	}

	/**
	 * @since 2.0
	 */
	public synchronized Sandbox getSandbox() {
		if (this.sandbox == null) {
			LocalSca localSca = getLocalSca();
			final SandboxImpl impl = new SandboxImpl(localSca);
			final SandboxPOATie poaTie = new SandboxPOATie(impl);
			final POA poa = localSca.getPoa();
			org.omg.CORBA.Object ref;
			try {
				ref = poa.servant_to_reference(poaTie);
			} catch (final ServantNotActive e) {
				getLog().log(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Couldn't activate sandbox", e));
				return null;
			} catch (final WrongPolicy e) {
				getLog().log(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Sandbox Wrong Policy", e));
				return null;
			}
			this.sandbox = SandboxHelper.narrow(ref);
		}
		return this.sandbox;
	}

	public static ScaDebugPlugin getInstance() {
		return ScaDebugPlugin.instance;
	}

	/**
	 * @since 2.0
	 */
	public ILaunchConfigurationFactoryRegistry getLaunchConfigurationFactoryRegistry() {
		return LaunchConfigurationFactoryRegistry.INSTANCE;
	}

	/**
	 * @since 2.0
	 */
	public ILauncherVariableRegistry getLauncherVariableRegistry() {
		return LauncherVariableRegistry.INSTANCE;
	}

	public static void logError(final String msg, final Throwable e) {
		ScaDebugPlugin.instance.getLog().log(new Status(IStatus.ERROR, ScaDebugPlugin.ID, msg, e));
	}
}
