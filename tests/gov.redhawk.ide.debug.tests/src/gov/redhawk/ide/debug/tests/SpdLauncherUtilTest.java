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

	/**
	 * IDE-1445 Test that XML validation catches missing PRF and SCD files that are referenced.
	 */
	@Test
	public void validateAllXML_SpdMissingPrfAndScd() throws URISyntaxException, IOException {
		SdrRoot sdrRoot = SdrPluginLoader.getSdrRoot(PLUGIN_ID, TEST_SDR_PATH);

		SoftPkg spd = getSpd(sdrRoot, "SpdMissingPrfAndScd");
		IStatus status = SpdLauncherUtil.validateAllXML(spd);
		Assert.assertEquals(IStatus.ERROR, status.getSeverity());
		Assert.assertEquals("Errors in SPD for component SpdMissingPrfAndScd", status.getMessage());
		Assert.assertEquals(2, status.getChildren().length);

		Assert.assertTrue(status.getChildren()[0].getMessage().contains("Invalid Property File"));

		Assert.assertTrue(status.getChildren()[1].getMessage().contains("Invalid Component File"));
	}

	/**
	 * IDE-1445 Test that XML validation catches errors in the SPD file.
	 */
	@Test
	public void validateAllXML_SpdWithErrors() throws URISyntaxException, IOException {
		SdrRoot sdrRoot = SdrPluginLoader.getSdrRoot(PLUGIN_ID, TEST_SDR_PATH);

		SoftPkg spd = getSpd(sdrRoot, "SpdWithErrors");
		IStatus status = SpdLauncherUtil.validateAllXML(spd);
		Assert.assertEquals(IStatus.ERROR, status.getSeverity());
		Assert.assertEquals("Errors in SPD for component SpdWithErrors", status.getMessage());
		Assert.assertEquals(1, status.getChildren().length);

		Assert.assertTrue(status.getChildren()[0].getMessage().contains("author"));
	}

	/**
	 * IDE-1445 Test that XML validation catches errors in dependencies (PRFs, SCDs).
	 */
	@Test
	public void validateAllXML_SpdWithPrfAndScdErrors() throws URISyntaxException, IOException {
		SdrRoot sdrRoot = SdrPluginLoader.getSdrRoot(PLUGIN_ID, TEST_SDR_PATH);

		SoftPkg spd = getSpd(sdrRoot, "SpdWithPrfAndScdErrors");
		IStatus status = SpdLauncherUtil.validateAllXML(spd);
		Assert.assertEquals(IStatus.ERROR, status.getSeverity());
		Assert.assertEquals("Some XML file(s) have errors", status.getMessage());
		Assert.assertEquals(2, status.getChildren().length);

		Assert.assertEquals("Errors in PRF for component SpdWithPrfAndScdErrors (SpdWithPrfAndScdErrors.prf.xml)", status.getChildren()[0].getMessage());
		Assert.assertEquals(1, status.getChildren()[0].getChildren().length);

		Assert.assertEquals("Errors in SCD for component SpdWithPrfAndScdErrors (SpdWithPrfAndScdErrors.scd.xml)", status.getChildren()[1].getMessage());
		Assert.assertEquals(1, status.getChildren()[1].getChildren().length);
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
