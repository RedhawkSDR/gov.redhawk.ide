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
package gov.redhawk.ide.sdr.ui;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.TargetSdrRoot;
import gov.redhawk.ide.sdr.preferences.IdeSdrPreferences;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @since 1.1
 */
public class SdrUiPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "gov.redhawk.ide.sdr.ui";

	/**
	 * @deprecated Use TargetSdrRoot#EDITING_DOMAIN_ID
	 * @since 3.1
	 */
	@Deprecated
	public static final String EDITING_DOMAIN_ID = TargetSdrRoot.EDITING_DOMAIN_ID;

	/**
	 * The job group for a job that exports project(s) to the SDR root.
	 * @since 5.0
	 */
	public static final Object FAMILY_EXPORT_TO_SDR = new Object();

	/**
	 * @deprecated Use {@link TargetSdrRoot#FAMILY_REFRESH_SDR}
	 * @since 5.0
	 */
	@Deprecated
	public static final Object FAMILY_REFRESH_SDR = TargetSdrRoot.FAMILY_REFRESH_SDR;

	// The shared instance
	private static SdrUiPlugin plugin;

	public SdrUiPlugin() {
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		SdrUiPlugin.plugin = this;
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		SdrUiPlugin.plugin = null;
		super.stop(context);
	}

	/**
	 * @deprecated Use {@link TargetSdrRoot#getSdrRoot()}
	 * @since 3.1
	 */
	@Deprecated
	public SdrRoot getTargetSdrRoot() {
		return TargetSdrRoot.getSdrRoot();
	}

	/**
	 * @deprecated Use {@link TargetSdrRoot#scheduleRefresh()}
	 * @since 4.1
	 */
	@Deprecated
	public void scheduleSdrRootRefresh() {
		TargetSdrRoot.scheduleRefresh();
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static SdrUiPlugin getDefault() {
		return SdrUiPlugin.plugin;
	}

	/**
	 * @deprecated Use {@link IdeSdrPreferences#getDomPath()}
	 * @since 3.1
	 */
	@Deprecated
	public String getDomPath() {
		return IdeSdrPreferences.getDomPath();
	}

	/**
	 * @deprecated Use {@link IdeSdrPreferences#getDevPath()}
	 * @since 3.1
	 */
	@Deprecated
	public String getDevPath() {
		return IdeSdrPreferences.getDevPath();
	}

	/**
	 * @deprecated Use {@link IdeSdrPreferences#getTargetSdrDomPath()}
	 * @since 3.1
	 */
	@Deprecated
	public IPath getTargetSdrDomPath() {
		return IdeSdrPreferences.getTargetSdrDomPath();
	}

	/**
	 * @deprecated Use {@link IdeSdrPreferences#getTargetSdrDevPath()}
	 * @since 3.1
	 */
	@Deprecated
	public IPath getTargetSdrDevPath() {
		return IdeSdrPreferences.getTargetSdrDevPath();
	}

	/**
	 * @deprecated Use {@link IdeSdrPreferences#getTargetSdrPath()}
	 * @since 3.1
	 */
	@Deprecated
	public IPath getTargetSdrPath() {
		return IdeSdrPreferences.getTargetSdrPath();
	}

	/**
	 * @since 3.1
	 */
	public void logError(final String error) {
		logError(error, null);
	}

	/**
	 * @since 3.1
	 */
	public void logError(String error, final Throwable throwable) {
		if (error == null && throwable != null) {
			error = throwable.getMessage();
		}
		getLog().log(new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, IStatus.OK, error, throwable));
	}
}
