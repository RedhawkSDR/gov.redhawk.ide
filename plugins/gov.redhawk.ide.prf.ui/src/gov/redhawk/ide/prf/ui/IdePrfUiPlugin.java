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
package gov.redhawk.ide.prf.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.statushandlers.StatusManager;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class IdePrfUiPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "gov.redhawk.ide.prf.ui";

	// The shared instance
	private static IdePrfUiPlugin plugin;

	/**
	 * The constructor
	 */
	public IdePrfUiPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		IdePrfUiPlugin.plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		IdePrfUiPlugin.plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static IdePrfUiPlugin getDefault() {
		return IdePrfUiPlugin.plugin;
	}

	/**
	 * Log exception.
	 * 
	 * @param e the e
	 */
	public static void logException(final Throwable e) {
		IdePrfUiPlugin.logException(e, null);
	}

	/**
	 * Gets the plugin id.
	 * 
	 * @return the plugin id
	 */
	public static String getPluginId() {
		return IdePrfUiPlugin.getDefault().getBundle().getSymbolicName();
	}

	/**
	 * Log exception.
	 * 
	 * @param e the e
	 * @param message the message
	 */
	public static void logException(Throwable e, String message) {
		if (e instanceof InvocationTargetException) {
			e = ((InvocationTargetException) e).getTargetException();
		}
		IStatus status = null;
		if (e instanceof CoreException) {
			status = ((CoreException) e).getStatus();
		} else {
			if (message == null) {
				message = e.getMessage();
			}
			if (message == null) {
				message = e.toString();
			}
			status = new Status(IStatus.ERROR, IdePrfUiPlugin.getPluginId(), IStatus.OK, message, e);
		}
		StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.SHOW);
	}

}
