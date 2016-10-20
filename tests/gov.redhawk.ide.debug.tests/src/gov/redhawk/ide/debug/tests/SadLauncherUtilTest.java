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

import gov.redhawk.ide.debug.SadLauncherUtil;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.util.SdrPluginLoader;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

public class SadLauncherUtilTest {

	private static final String PLUGIN_ID = "gov.redhawk.ide.debug.tests";
	private static final String TEST_SDR_PATH = "testFiles/sdr";

	/**
	 * IDE-1445 Test that XML validation catches errors in the SAD file.
	 */
	@Test
	public void validateAllXML_sadWithErrors() throws URISyntaxException, IOException {
		SdrRoot sdrRoot = SdrPluginLoader.getSdrRoot(PLUGIN_ID, TEST_SDR_PATH);

		SoftwareAssembly sad = getSad(sdrRoot, "sadWithErrors");
		IStatus status = SadLauncherUtil.validateAllXML(sad);
		Assert.assertEquals(IStatus.ERROR, status.getSeverity());
		Assert.assertEquals("There are errors in the SAD file", status.getMessage());
		Assert.assertEquals(2, status.getChildren().length);

		Assert.assertTrue(status.getChildren()[0].getMessage().contains("componentFile"));

		Assert.assertTrue(status.getChildren()[1].getMessage().contains("componentInstantiationRef"));
	}

	/**
	 * IDE-1445 Test that XML validation catches errors with/in dependencies (SPDs, PRFs, SCDs).
	 */
	@Test
	public void validateAllXML_sadWithComponentsWithErrors() throws URISyntaxException, IOException {
		SdrRoot sdrRoot = SdrPluginLoader.getSdrRoot(PLUGIN_ID, TEST_SDR_PATH);

		SoftwareAssembly sad = getSad(sdrRoot, "sadWithComponentsWithErrors");
		IStatus status = SadLauncherUtil.validateAllXML(sad);
		Assert.assertEquals(IStatus.ERROR, status.getSeverity());
		Assert.assertEquals("Some XML file(s) have errors", status.getMessage());
		Assert.assertEquals(5, status.getChildren().length);

		Assert.assertEquals("Errors in SPD for component SpdWithErrors", status.getChildren()[0].getMessage());

		Assert.assertEquals("Errors in SPD for component SpdMissingPrfAndScd", status.getChildren()[1].getMessage());

		Assert.assertEquals("Errors in PRF for component SpdWithPrfAndScdErrors (SpdWithPrfAndScdErrors.prf.xml)", status.getChildren()[2].getMessage());

		Assert.assertEquals("Errors in SCD for component SpdWithPrfAndScdErrors (SpdWithPrfAndScdErrors.scd.xml)", status.getChildren()[3].getMessage());

		Assert.assertEquals("Missing component SPD for component id4 (/components/MissingSpd/MissingSpd.spd.xml)", status.getChildren()[4].getMessage());
	}

	/**
	 * IDE-1444 Test that invalid source/target port names in a connection are caught by the validator.
	 */
	@Test
	public void validateAllXML_invalidPortName() throws URISyntaxException, IOException {
		SdrRoot sdrRoot = SdrPluginLoader.getSdrRoot(PLUGIN_ID, TEST_SDR_PATH);

		SoftwareAssembly sad = getSad(sdrRoot, "invalidPortName");
		IStatus status = SadLauncherUtil.validateAllXML(sad);
		Assert.assertEquals(IStatus.ERROR, status.getSeverity());
		Assert.assertEquals("There are errors in the SAD file", status.getMessage());
		Assert.assertEquals(2, status.getChildren().length);

		Assert.assertTrue(status.getChildren()[0].getMessage().contains("ValidSourceReference"));
		Assert.assertTrue(status.getChildren()[0].getMessage().contains("connection_1"));

		Assert.assertTrue(status.getChildren()[1].getMessage().contains("ValidTargetReference"));
		Assert.assertTrue(status.getChildren()[1].getMessage().contains("connection_1"));
	}

	private SoftwareAssembly getSad(SdrRoot sdrRoot, String name) {
		for (SoftwareAssembly sad : sdrRoot.getWaveformsContainer().getWaveforms()) {
			if (name.equals(sad.getName())) {
				return sad;
			}
		}
		return null;
	}
}
