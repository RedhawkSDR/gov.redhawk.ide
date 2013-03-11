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
package gov.redhawk.ide.sdr;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class IdeSdrActivator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "gov.redhawk.ide.sdr";
	// The shared instance
	private static IdeSdrActivator plugin;

	/**
	 * The constructor
	 */
	public IdeSdrActivator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
	
		IdeSdrActivator.plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		IdeSdrActivator.plugin = null;
		
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static IdeSdrActivator getDefault() {
		return IdeSdrActivator.plugin;
	}

	/**
	 * @param format
	 */
	public void logWarning(final String message) {
		getLog().log(new Status(IStatus.WARNING, IdeSdrActivator.PLUGIN_ID, message, null));
	}

	/**
	 * @param string
	 * @param e
	 * @since 1.1
	 */
	public void logWarning(final String msg, final Throwable e) {
		getLog().log(new Status(IStatus.WARNING, IdeSdrActivator.PLUGIN_ID, msg, e));
	}

}
