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
package gov.redhawk.ide.sdr.ui.preferences;

import gov.redhawk.ide.sdr.preferences.IdeSdrPreferenceConstants;

/**
 * @since 1.2
 * @deprecated Use {@link IdeSdrPreferenceConstants}
 */
@Deprecated
public final class SdrUiPreferenceConstants {

	/**
	 * @deprecated Use {@link IdeSdrPreferenceConstants#PREF_DEFAULT_DEVICE_MANAGER_NAME}
	 */
	@Deprecated
	public static final String PREF_DEFAULT_DEVICE_MANAGER_NAME = "defaultDeviceManagerName";

	/**
	 * @deprecated Use {@link IdeSdrPreferenceConstants#PREF_DEFAULT_DOMAIN_MANAGER_NAME}
	 */
	@Deprecated
	public static final String PREF_DEFAULT_DOMAIN_MANAGER_NAME = "defaultDomainManagerName";

	/**
	 * @deprecated Use {@link IdeSdrPreferenceConstants#PREF_AUTO_CONNECT_ATTEMPT_RETRY_INTERVAL_MSEC}
	 */
	@Deprecated
	public static final String PREF_AUTO_CONNECT_ATTEMPT_RETRY_INTERVAL_MSEC = "autoConnectAttemptRetryInterval";

	/**
	 * @deprecated Use {@link IdeSdrPreferenceConstants#PREF_AUTO_CONNECT_MAX_CONNECTION_ATTEMPTS}
	 */
	@Deprecated
	public static final String PREF_AUTO_CONNECT_MAX_CONNECTION_ATTEMPTS = "autoConnectMaxConnectionAttempts";

	/**
	 * @deprecated Use {@link IdeSdrPreferenceConstants#SCA_LOCAL_SDR_PATH_PREFERENCE}
	 * @since 3.1
	 */
	@Deprecated
	public static final String SCA_LOCAL_SDR_PATH_PREFERENCE = "sdrRoot";

	/**
	 * @deprecated Use {@link IdeSdrPreferenceConstants#TARGET_SDR_DEV_PATH}
	 * @since 3.1
	 */
	@Deprecated
	public static final String TARGET_SDR_DEV_PATH = "devPath";

	/**
	 * @deprecated Use {@link IdeSdrPreferenceConstants#TARGET_SDR_DOM_PATH}
	 * @since 3.1
	 */
	@Deprecated
	public static final String TARGET_SDR_DOM_PATH = "domPath";

	private SdrUiPreferenceConstants() {
	}
}
