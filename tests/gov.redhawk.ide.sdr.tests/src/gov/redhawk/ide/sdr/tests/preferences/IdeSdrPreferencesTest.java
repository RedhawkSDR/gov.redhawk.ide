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
package gov.redhawk.ide.sdr.tests.preferences;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.osgi.service.prefs.Preferences;

import gov.redhawk.ide.sdr.IdeSdrActivator;
import gov.redhawk.ide.sdr.preferences.IdeSdrPreferenceConstants;
import gov.redhawk.ide.sdr.preferences.IdeSdrPreferences;

public class IdeSdrPreferencesTest {

	private static Preferences prefs = InstanceScope.INSTANCE.getNode(IdeSdrActivator.PLUGIN_ID);

	@Test
	public void getTargetSdrDomPath() {
		prefs.put(IdeSdrPreferenceConstants.SCA_LOCAL_SDR_PATH_PREFERENCE, "/foo");
		prefs.put(IdeSdrPreferenceConstants.TARGET_SDR_DOM_PATH, "a");
		Assert.assertEquals("/foo/a", IdeSdrPreferences.getTargetSdrDomPath().toString());
	}

	@Test
	public void getTargetSdrDevPath() {
		prefs.put(IdeSdrPreferenceConstants.SCA_LOCAL_SDR_PATH_PREFERENCE, "/foo");
		prefs.put(IdeSdrPreferenceConstants.TARGET_SDR_DEV_PATH, "b");
		Assert.assertEquals("/foo/b", IdeSdrPreferences.getTargetSdrDevPath().toString());
	}

	@Test
	public void getTargetSdrPath() {
		prefs.put(IdeSdrPreferenceConstants.SCA_LOCAL_SDR_PATH_PREFERENCE, "/foo");
		Assert.assertEquals("/foo", IdeSdrPreferences.getTargetSdrPath().toString());

		prefs.put(IdeSdrPreferenceConstants.SCA_LOCAL_SDR_PATH_PREFERENCE, "${unknown_var}");
		Assert.assertEquals(null, IdeSdrPreferences.getTargetSdrPath());

		prefs.put(IdeSdrPreferenceConstants.SCA_LOCAL_SDR_PATH_PREFERENCE, "");
		Assert.assertEquals(null, IdeSdrPreferences.getTargetSdrPath());
	}

	@AfterClass
	public static void after() throws CoreException {
		prefs.remove(IdeSdrPreferenceConstants.SCA_LOCAL_SDR_PATH_PREFERENCE);
		prefs.remove(IdeSdrPreferenceConstants.TARGET_SDR_DOM_PATH);
		prefs.remove(IdeSdrPreferenceConstants.TARGET_SDR_DEV_PATH);
	}
}
