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

public final class IdeSdrPreferenceConstants {

	public static final String PREF_DEFAULT_DEVICE_MANAGER_NAME = "defaultDeviceManagerName";
	public static final String PREF_DEFAULT_DOMAIN_MANAGER_NAME = "defaultDomainManagerName";

	public static final String PREF_AUTO_CONNECT_ATTEMPT_RETRY_INTERVAL_MSEC = "autoConnectAttemptRetryInterval";
	public static final String PREF_AUTO_CONNECT_MAX_CONNECTION_ATTEMPTS = "autoConnectMaxConnectionAttempts";

	public static final String SCA_LOCAL_SDR_PATH_PREFERENCE = "sdrRoot";
	public static final String TARGET_SDR_DEV_PATH = "devPath";
	public static final String TARGET_SDR_DOM_PATH = "domPath";

	private IdeSdrPreferenceConstants() {
	}
}
