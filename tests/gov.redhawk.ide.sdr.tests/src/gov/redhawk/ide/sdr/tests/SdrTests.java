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
// BEGIN GENERATED CODE
package gov.redhawk.ide.sdr.tests;

import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.util.SdrPluginLoader;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.emf.common.util.URI;

public class SdrTests {

	public static final String PLUGIN_ID = "gov.redhawk.ide.sdr.tests";

	public static final String DEFAULT_SDR_PATH = "testFiles/sdr";

	public static SdrRoot getSdrTestsSdrRoot() throws URISyntaxException, IOException {
		return SdrPluginLoader.getSdrRoot(SdrTests.PLUGIN_ID, SdrTests.DEFAULT_SDR_PATH);
	}

	public static synchronized SdrRoot getSdrRoot(final URI sdrRootPath) {
		return SdrPluginLoader.getSdrRoot(sdrRootPath);
	}

} // SdrTests
