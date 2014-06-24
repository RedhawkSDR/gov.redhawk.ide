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
package gov.redhawk.ide.dcd.ui;

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
public class DcdUiActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "gov.redhawk.ide.dcd.ui";

	// The shared instance
	private static DcdUiActivator plugin;

	/**
	 * The constructor
	 */
	public DcdUiActivator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		DcdUiActivator.plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		DcdUiActivator.plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static DcdUiActivator getDefault() {
		return DcdUiActivator.plugin;
	}

	/**
	 * @return
	 */
	public static String getPluginId() {
		return DcdUiActivator.getDefault().getBundle().getSymbolicName();
	}

	/**
	 * Log exception.
	 * 
	 * @param e the e
	 */
	public static void logException(final Throwable e) {
		DcdUiActivator.logException(e, null);
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
			status = new Status(((CoreException) e).getStatus().getSeverity(), DcdUiActivator.PLUGIN_ID, e.getLocalizedMessage(), e);
		} else {
			if (message == null) {
				message = e.getMessage();
			}
			if (message == null) {
				message = e.toString();
			}
			status = new Status(IStatus.ERROR, DcdUiActivator.getPluginId(), IStatus.OK, message, e);
		}
		StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.SHOW);
	}
	
	public static final void logError(final String msg, final Throwable e) {
		DcdUiActivator.getDefault().getLog().log(new Status(IStatus.ERROR, DcdUiActivator.PLUGIN_ID, msg, e));
	}

}
