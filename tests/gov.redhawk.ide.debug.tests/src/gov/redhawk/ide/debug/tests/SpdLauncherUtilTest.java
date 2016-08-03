/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package gov.redhawk.ide.debug.tests;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.IStatus;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.debug.SpdLauncherUtil;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.util.SdrPluginLoader;
import mil.jpeojtrs.sca.spd.SoftPkg;

public class SpdLauncherUtilTest {

	private static final String PLUGIN_ID = "gov.redhawk.ide.debug.tests";
	private static final String TEST_SDR_PATH = "testFiles/sdr";

	@Test
	public void validateAllXML() throws URISyntaxException, IOException {
		SdrRoot sdrRoot = SdrPluginLoader.getSdrRoot(PLUGIN_ID, TEST_SDR_PATH);

		SoftPkg spd = getSpd(sdrRoot, "SpdMissingPrfAndScd");
		IStatus status = SpdLauncherUtil.validateAllXML(spd);
		Assert.assertEquals(IStatus.ERROR, status.getSeverity());
		Assert.assertEquals(2, status.getChildren().length);

		spd = getSpd(sdrRoot, "SpdWithErrors");
		status = SpdLauncherUtil.validateAllXML(spd);
		Assert.assertEquals(IStatus.ERROR, status.getSeverity());
		Assert.assertEquals(0, status.getChildren().length);

		spd = getSpd(sdrRoot, "SpdWithPrfAndScdErrors");
		status = SpdLauncherUtil.validateAllXML(spd);
		Assert.assertEquals(IStatus.ERROR, status.getSeverity());
		Assert.assertEquals(2, status.getChildren().length);
	}

	private SoftPkg getSpd(SdrRoot sdrRoot, String name) {
		for (SoftPkg spd : sdrRoot.getComponentsContainer().getComponents()) {
			if (name.equals(spd.getName())) {
				return spd;
			}
		}
		return null;
	}
}
