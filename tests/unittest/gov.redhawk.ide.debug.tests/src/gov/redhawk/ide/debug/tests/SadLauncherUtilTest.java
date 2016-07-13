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

import org.eclipse.core.runtime.CoreException;
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

	@Test
	public void validateAllXML() throws URISyntaxException, IOException {
		SdrRoot sdrRoot = SdrPluginLoader.getSdrRoot(PLUGIN_ID, TEST_SDR_PATH);

		SoftwareAssembly sadWithErrors = null;
		for (SoftwareAssembly sad : sdrRoot.getWaveformsContainer().getWaveforms()) {
			if ("sadWithErrors".equals(sad.getName())) {
				sadWithErrors = sad;
				break;
			}
		}

		boolean foundError = false;
		try {
			SadLauncherUtil.validateAllXML(sadWithErrors);
		} catch (CoreException e) {
			Assert.assertEquals(IStatus.ERROR, e.getStatus().getSeverity());
			Assert.assertEquals(0, e.getStatus().getChildren().length);
			foundError = true;
		}
		Assert.assertTrue(foundError);

		SoftwareAssembly sadWithComponentsWithErrors = null;
		for (SoftwareAssembly sad : sdrRoot.getWaveformsContainer().getWaveforms()) {
			if ("sadWithComponentsWithErrors".equals(sad.getName())) {
				sadWithComponentsWithErrors = sad;
				break;
			}
		}

		foundError = false;
		try {
			SadLauncherUtil.validateAllXML(sadWithComponentsWithErrors);
		} catch (CoreException e) {
			Assert.assertEquals(IStatus.ERROR, e.getStatus().getSeverity());
			Assert.assertEquals(6, e.getStatus().getChildren().length);
			foundError = true;
		}
		Assert.assertTrue(foundError);
	}
}
