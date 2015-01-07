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

package gov.redhawk.ide.dcd.generator.newdevice.tests;

import gov.redhawk.ide.dcd.generator.newdevice.GeneratorArgs;
import gov.redhawk.ide.dcd.generator.newdevice.TestFileTemplate;
import gov.redhawk.ide.dcd.tests.TestUtils;
import gov.redhawk.ide.preferences.RedhawkIdePreferenceConstants;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;

import org.junit.Test;

/**
 * A class to test {@link TestFileTemplateTest}.
 */
public class TestFileTemplateTest {

	/**
	 * Tests generating the test file
	 * 
	 * @throws IOException
	 */
	@Test
	public void test() throws IOException {
		// Generate XML using the template
		final GeneratorArgs args = new GeneratorArgs();
		args.setSoftPkgFile("test.spd.xml");
		args.setProjectName("TestFileTemplateTestProject");
		args.setDeviceType(RedhawkIdePreferenceConstants.EXECUTABLE_DEVICE);
		args.setAggregateDevice(false);
		TestFileTemplate testTemplate = TestFileTemplate.create(null);
		String testContent = testTemplate.generate(args);

		// Create an XML file with the content
		final File testFile = TestUtils.createFile(testContent, ".py");
		Assert.assertTrue(testContent.contains("CF/ExecutableDevice:1.0"));
		
		args.setDeviceType(RedhawkIdePreferenceConstants.LOADABLE_DEVICE);
		args.setAggregateDevice(false);
		testTemplate = TestFileTemplate.create(null);
		testContent = testTemplate.generate(args);
		Assert.assertTrue(testContent.contains("CF/LoadableDevice:1.0"));
		
		args.setDeviceType(RedhawkIdePreferenceConstants.DEVICE);
		args.setAggregateDevice(false);
		testTemplate = TestFileTemplate.create(null);
		testContent = testTemplate.generate(args);
		Assert.assertTrue(testContent.contains("CF/Device:1.0"));
	}
}
