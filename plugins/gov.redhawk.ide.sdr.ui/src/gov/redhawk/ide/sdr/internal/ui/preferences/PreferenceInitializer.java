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

import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.ide.sdr.ui.preferences.SdrUiPreferenceConstants;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

/**
 * 
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/**
	 * 
	 */
	public PreferenceInitializer() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initializeDefaultPreferences() {
		SdrUiPlugin.getDefault().getPreferenceStore().setDefault(SdrUiPreferenceConstants.PREF_DEFAULT_DEVICE_MANAGER_NAME, "Device Manager");
		SdrUiPlugin.getDefault().getPreferenceStore().setDefault(SdrUiPreferenceConstants.PREF_DEFAULT_DOMAIN_MANAGER_NAME, "Domain Manager");

		SdrUiPlugin.getDefault().getPreferenceStore().setDefault(SdrUiPreferenceConstants.PREF_AUTO_CONNECT_ATTEMPT_RETRY_INTERVAL_MSEC, 3000); // SUPPRESS CHECKSTYLE MAGIC NUMBER
		SdrUiPlugin.getDefault().getPreferenceStore().setDefault(SdrUiPreferenceConstants.PREF_AUTO_CONNECT_MAX_CONNECTION_ATTEMPTS, 3); // SUPPRESS CHECKSTYLE MAGIC NUMBER

		SdrUiPlugin.getDefault().getPreferenceStore().setDefault(SdrUiPreferenceConstants.SCA_LOCAL_SDR_PATH_PREFERENCE, "${SDRROOT}");
		SdrUiPlugin.getDefault().getPreferenceStore().setDefault(SdrUiPreferenceConstants.TARGET_SDR_DEV_PATH, "dev");
		SdrUiPlugin.getDefault().getPreferenceStore().setDefault(SdrUiPreferenceConstants.TARGET_SDR_DOM_PATH, "dom");
	}

}
