/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.sdr.preferences;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.Preferences;

import gov.redhawk.ide.sdr.IdeSdrActivator;

/**
 * Provides various SDR-related paths based on the current preferences.
 */
public class IdeSdrPreferences {

	private IdeSdrPreferences() {
	}

	private static Preferences[] getPreferenceNodes() {
		return new IEclipsePreferences[] { InstanceScope.INSTANCE.getNode(IdeSdrActivator.PLUGIN_ID),
			DefaultScope.INSTANCE.getNode(IdeSdrActivator.PLUGIN_ID) };
	}

	/**
	 * @return The directory segment following $SDRROOT for the "dom" portion of the SDR root
	 */
	public static String getDomPath() {
		return Platform.getPreferencesService().get(IdeSdrPreferenceConstants.TARGET_SDR_DOM_PATH, "dom", getPreferenceNodes());
	}

	/**
	 * @return The directory segment following $SDRROOT for the "dev" portion of the SDR root
	 */
	public static String getDevPath() {
		return Platform.getPreferencesService().get(IdeSdrPreferenceConstants.TARGET_SDR_DEV_PATH, "dev", getPreferenceNodes());
	}

	/**
	 * @return The absolute path to $SDRROOT/dom
	 */
	public static IPath getTargetSdrDomPath() {
		IPath targetSdrPath = getTargetSdrPath();
		return (targetSdrPath == null) ? null : targetSdrPath.append(getDomPath());
	}

	/**
	 * @return The absolute path to $SDRROOT/dev
	 */
	public static IPath getTargetSdrDevPath() {
		IPath targetSdrPath = getTargetSdrPath();
		return (getTargetSdrPath() == null) ? null : targetSdrPath.append(getDevPath());
	}

	/**
	 * @return The absolute path to the SDR root
	 */
	public static IPath getTargetSdrPath() {
		String runtimePath = Platform.getPreferencesService().get(IdeSdrPreferenceConstants.SCA_LOCAL_SDR_PATH_PREFERENCE, "", getPreferenceNodes()).trim();
		if (runtimePath.isEmpty()) {
			return null;
		}

		if (runtimePath.startsWith("${") && runtimePath.endsWith("}")) {
			final String envName = runtimePath.substring(2, runtimePath.length() - 1);
			runtimePath = System.getenv(envName);
			if (runtimePath == null) {
				return null;
			}
		}
		return new Path(runtimePath);
	}

}
