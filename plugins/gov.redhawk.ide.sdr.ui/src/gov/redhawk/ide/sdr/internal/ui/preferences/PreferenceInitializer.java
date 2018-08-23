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
package gov.redhawk.ide.sdr.internal.ui.preferences;

import gov.redhawk.ide.sdr.IdeSdrActivator;
import gov.redhawk.ide.sdr.preferences.IdeSdrPreferenceConstants;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * Old preference initializer. The new preferences are now in the plug-in gov.redhawk.ide.sdr.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public PreferenceInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore ideSdrPrefs = new ScopedPreferenceStore(InstanceScope.INSTANCE, IdeSdrActivator.PLUGIN_ID);
		IPreferenceStore sdrUiPrefs = SdrUiPlugin.getDefault().getPreferenceStore();

		// Defaults for this plugin
		sdrUiPrefs.setDefault(IdeSdrPreferenceConstants.PREF_DEFAULT_DEVICE_MANAGER_NAME, "Device Manager");
		sdrUiPrefs.setDefault(IdeSdrPreferenceConstants.PREF_DEFAULT_DOMAIN_MANAGER_NAME, "Domain Manager");
		sdrUiPrefs.setDefault(IdeSdrPreferenceConstants.PREF_AUTO_CONNECT_ATTEMPT_RETRY_INTERVAL_MSEC, 3000);
		sdrUiPrefs.setDefault(IdeSdrPreferenceConstants.PREF_AUTO_CONNECT_MAX_CONNECTION_ATTEMPTS, 3);
		sdrUiPrefs.setDefault(IdeSdrPreferenceConstants.SCA_LOCAL_SDR_PATH_PREFERENCE, "${SDRROOT}");
		sdrUiPrefs.setDefault(IdeSdrPreferenceConstants.TARGET_SDR_DEV_PATH, "dev");
		sdrUiPrefs.setDefault(IdeSdrPreferenceConstants.TARGET_SDR_DOM_PATH, "dom");

		// Transfer any preference values to gov.redhawk.ide.sdr and reset them to default here
		for (String prefName : new String[] { IdeSdrPreferenceConstants.PREF_DEFAULT_DEVICE_MANAGER_NAME, IdeSdrPreferenceConstants.PREF_DEFAULT_DOMAIN_MANAGER_NAME,
			IdeSdrPreferenceConstants.SCA_LOCAL_SDR_PATH_PREFERENCE, IdeSdrPreferenceConstants.TARGET_SDR_DEV_PATH,
			IdeSdrPreferenceConstants.TARGET_SDR_DOM_PATH }) {
			if (sdrUiPrefs.contains(prefName) && !sdrUiPrefs.isDefault(prefName)) {
				ideSdrPrefs.setValue(prefName, sdrUiPrefs.getString(prefName));
				sdrUiPrefs.setToDefault(prefName);
			}
		}
		for (String prefName : new String[] { IdeSdrPreferenceConstants.PREF_AUTO_CONNECT_ATTEMPT_RETRY_INTERVAL_MSEC,
			IdeSdrPreferenceConstants.PREF_AUTO_CONNECT_MAX_CONNECTION_ATTEMPTS }) {
			if (sdrUiPrefs.contains(prefName) && !sdrUiPrefs.isDefault(prefName)) {
				ideSdrPrefs.setValue(prefName, sdrUiPrefs.getInt(prefName));
				sdrUiPrefs.setToDefault(prefName);
			}
		}
	}

}
