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
package gov.redhawk.ide.ui;

import gov.redhawk.ide.RedhawkIdeActivator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class RedhawkIDEUiPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "gov.redhawk.ide.ui";

	// The shared instance
	private static RedhawkIDEUiPlugin plugin;

	private ScopedPreferenceStore idePreferenceStore;

	/**
	 * The constructor
	 */
	public RedhawkIDEUiPlugin() {
	}

	/**
	 * @since 5.0
	 */
	public IPreferenceStore getRedhawkIdePreferenceStore() {
		if (this.idePreferenceStore == null) {
			this.idePreferenceStore = new ScopedPreferenceStore(new InstanceScope(), RedhawkIdeActivator.PLUGIN_ID);
		}
		return this.idePreferenceStore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		RedhawkIDEUiPlugin.plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		if (this.idePreferenceStore != null) {
			this.idePreferenceStore = null;
		}
		RedhawkIDEUiPlugin.plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static RedhawkIDEUiPlugin getDefault() {
		return RedhawkIDEUiPlugin.plugin;
	}
	
	/**
	 * Logging functionality
	 * 
	 * @param msg
	 * @param e
	 * @since 6.0
	 */
	public static final void logError(final String msg, final Throwable e) {
		RedhawkIDEUiPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, RedhawkIDEUiPlugin.PLUGIN_ID, msg, e));
	}
}
