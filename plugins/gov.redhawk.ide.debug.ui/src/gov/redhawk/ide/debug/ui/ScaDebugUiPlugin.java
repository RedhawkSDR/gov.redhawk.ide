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

package gov.redhawk.ide.debug.ui;

import gov.redhawk.ide.debug.internal.ui.LocalScaObjectLocator;
import gov.redhawk.model.sca.services.IScaObjectLocator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * The activator class controls the plug-in life cycle
 */
public class ScaDebugUiPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "gov.redhawk.ide.debug.ui"; //$NON-NLS-1$

	// The shared instance
	private static ScaDebugUiPlugin plugin;

	private ServiceRegistration< IScaObjectLocator > reg;


	/**
	 * The constructor
	 */
	public ScaDebugUiPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		ScaDebugUiPlugin.plugin = this;
		this.reg = context.registerService(IScaObjectLocator.class, new LocalScaObjectLocator(), null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		if (this.reg != null) {
			this.reg.unregister();
			this.reg = null;
		}
		ScaDebugUiPlugin.plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ScaDebugUiPlugin getDefault() {
		return ScaDebugUiPlugin.plugin;
	}

	/**
	 * Logs the specified status with this plug-in's log.
	 * 
	 * @param status
	 *            status to log
	 */
	public static void log(final IStatus status) {
		ScaDebugUiPlugin.getDefault().getLog().log(status);
	}

	/**
	 * Logs an internal error with the specified message.
	 * 
	 * @param message
	 *            the error message to log
	 */
	public static void logErrorMessage(final String message) {
		ScaDebugUiPlugin.log(new Status(IStatus.ERROR, ScaDebugUiPlugin.getUniqueIdentifier(), IStatus.ERROR, message, null));
	}

	/**
	 * Logs an internal error with the specified throwable
	 * 
	 * @param e
	 *            the exception to be logged
	 */
	public static void log(final Throwable e) {
		ScaDebugUiPlugin.log(new Status(IStatus.ERROR, ScaDebugUiPlugin.getUniqueIdentifier(), IStatus.ERROR, e.getMessage(), e));
	}

	/**
	 * Convenience method which returns the unique identifier of this plugin.
	 */
	public static String getUniqueIdentifier() {
		if (ScaDebugUiPlugin.getDefault() == null) {
			// If the default instance is not yet initialized,
			// return a static identifier. This identifier must
			// match the plugin id defined in plugin.xml
			return ScaDebugUiPlugin.PLUGIN_ID;
		}
		return ScaDebugUiPlugin.getDefault().getBundle().getSymbolicName();
	}

}
