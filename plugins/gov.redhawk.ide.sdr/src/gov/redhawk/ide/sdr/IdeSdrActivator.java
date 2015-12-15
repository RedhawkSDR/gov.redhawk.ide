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
// BEGIN GENERATED CODE
package gov.redhawk.ide.sdr;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import gov.redhawk.ide.sdr.nodebooter.NodeBooterLauncherUtil;
import gov.redhawk.ide.sdr.util.IEnvMap;

/**
 * The activator class controls the plug-in life cycle
 */
public class IdeSdrActivator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "gov.redhawk.ide.sdr";
	// The shared instance
	private static IdeSdrActivator plugin;
	private ServiceTracker<IEnvMap, IEnvMap> envMapServices;

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
		envMapServices = new ServiceTracker<IEnvMap, IEnvMap>(context, IEnvMap.class, null);
		envMapServices.open(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		if (envMapServices != null) {
			envMapServices.close();
			envMapServices = null;
		}
		NodeBooterLauncherUtil.getInstance().terminateAll();

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
	 * @since 8.2
	 */
	public IEnvMap[] getEnvMapServices() {
		return envMapServices.getServices(new IEnvMap[envMapServices.size()]);
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
