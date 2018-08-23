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
package gov.redhawk.ide.internal.sdr.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.osgi.service.prefs.Preferences;

import gov.redhawk.ide.sdr.IdeSdrActivator;
import gov.redhawk.ide.sdr.preferences.IdeSdrPreferenceConstants;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public PreferenceInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
		Preferences ideSdrPrefs = DefaultScope.INSTANCE.getNode(IdeSdrActivator.PLUGIN_ID);

		ideSdrPrefs.put(IdeSdrPreferenceConstants.PREF_DEFAULT_DEVICE_MANAGER_NAME, "Device Manager");
		ideSdrPrefs.put(IdeSdrPreferenceConstants.PREF_DEFAULT_DOMAIN_MANAGER_NAME, "Domain Manager");

		ideSdrPrefs.putInt(IdeSdrPreferenceConstants.PREF_AUTO_CONNECT_ATTEMPT_RETRY_INTERVAL_MSEC, 3000);
		ideSdrPrefs.putInt(IdeSdrPreferenceConstants.PREF_AUTO_CONNECT_MAX_CONNECTION_ATTEMPTS, 3);

		ideSdrPrefs.put(IdeSdrPreferenceConstants.SCA_LOCAL_SDR_PATH_PREFERENCE, "${SDRROOT}");
		ideSdrPrefs.put(IdeSdrPreferenceConstants.TARGET_SDR_DEV_PATH, "dev");
		ideSdrPrefs.put(IdeSdrPreferenceConstants.TARGET_SDR_DOM_PATH, "dom");
	}

}
