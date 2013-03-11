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
import gov.redhawk.ide.dcd.generator.newdevice.ScdFileTemplate;
import gov.redhawk.ide.dcd.tests.TestUtils;
import gov.redhawk.ide.preferences.RedhawkIdePreferenceConstants;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;
import mil.jpeojtrs.sca.scd.ScdPackage;
import mil.jpeojtrs.sca.scd.SoftwareComponent;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Test;

/**
 * A class to test {@link ScdFileTemplate}.
 */
public class ScdFileTemplateTest {

	private static final int EXE_SUPPORT_INTERFACE_COUNT = 8;
	private static final int EXE_INTERFACE_COUNT = 8;
	private static final int LOAD_SUPPORT_INTERFACE_COUNT = 7;
	private static final int LOAD_INTERFACE_COUNT = 7;
	private static final int DEV_AND_AGGREGATE_SUPPORT_INTERFACE_COUNT = 7;
	private static final int DEV_AND_AGGREGATE_INTERFACE_COUNT = 7;

	/**
	 * Tests generating an SCD file for an executable device
	 * 
	 * @throws IOException
	 */
	@Test
	public void testExeDevice() throws IOException {
		// Generate XML using the template
		final ScdFileTemplate scdTemplate = ScdFileTemplate.create(null);
		final GeneratorArgs args = new GeneratorArgs();
		args.setDeviceType(RedhawkIdePreferenceConstants.EXECUTABLE_DEVICE);
		args.setAggregateDevice(false);
		final String scdContent = scdTemplate.generate(args);

		// Create an XML file with the content
		final File prfFile = TestUtils.createFile(scdContent, ScdPackage.FILE_EXTENSION);

		// Try to create a model from the file
		final ResourceSet resourceSet = new ResourceSetImpl();
		final SoftwareComponent component = SoftwareComponent.Util.getSoftwareComponent(resourceSet.getResource(URI.createFileURI(prfFile.toString()), true));
		Assert.assertEquals("2.2", component.getCorbaVersion());
		Assert.assertEquals("IDL:CF/ExecutableDevice:1.0", component.getComponentRepID().getRepid());
		Assert.assertEquals("executabledevice", component.getComponentType());
		Assert.assertEquals(ScdFileTemplateTest.EXE_SUPPORT_INTERFACE_COUNT, component.getComponentFeatures().getSupportsInterface().size());
		Assert.assertNotNull(component.getComponentFeatures().getPorts());
		Assert.assertEquals(ScdFileTemplateTest.EXE_INTERFACE_COUNT, component.getInterfaces().getInterface().size());
	}

	/**
	 * Tests generating an SCD file for a loadable device
	 * 
	 * @throws IOException
	 */
	@Test
	public void testLoadDevice() throws IOException {
		// Generate XML using the template
		final ScdFileTemplate scdTemplate = ScdFileTemplate.create(null);
		final GeneratorArgs args = new GeneratorArgs();
		args.setDeviceType(RedhawkIdePreferenceConstants.LOADABLE_DEVICE);
		args.setAggregateDevice(false);
		final String scdContent = scdTemplate.generate(args);

		// Create an XML file with the content
		final File prfFile = TestUtils.createFile(scdContent, ScdPackage.FILE_EXTENSION);

		// Try to create a model from the file
		final ResourceSet resourceSet = new ResourceSetImpl();
		final SoftwareComponent component = SoftwareComponent.Util.getSoftwareComponent(resourceSet.getResource(URI.createFileURI(prfFile.toString()), true));
		Assert.assertEquals("2.2", component.getCorbaVersion());
		Assert.assertEquals("IDL:CF/LoadableDevice:1.0", component.getComponentRepID().getRepid());
		Assert.assertEquals("loadabledevice", component.getComponentType());
		Assert.assertEquals(ScdFileTemplateTest.LOAD_SUPPORT_INTERFACE_COUNT, component.getComponentFeatures().getSupportsInterface().size());
		Assert.assertNotNull(component.getComponentFeatures().getPorts());
		Assert.assertEquals(ScdFileTemplateTest.LOAD_INTERFACE_COUNT, component.getInterfaces().getInterface().size());
	}

	/**
	 * Tests generating an SCD file for a device marked as aggregate
	 * 
	 * @throws IOException
	 */
	@Test
	public void testDeviceAndAggregate() throws IOException {
		// Generate XML using the template
		final ScdFileTemplate scdTemplate = ScdFileTemplate.create(null);
		final GeneratorArgs args = new GeneratorArgs();
		args.setDeviceType(RedhawkIdePreferenceConstants.DEVICE);
		args.setAggregateDevice(true);
		final String scdContent = scdTemplate.generate(args);

		// Create an XML file with the content
		final File prfFile = TestUtils.createFile(scdContent, ScdPackage.FILE_EXTENSION);

		// Try to create a model from the file
		final ResourceSet resourceSet = new ResourceSetImpl();
		final SoftwareComponent component = SoftwareComponent.Util.getSoftwareComponent(resourceSet.getResource(URI.createFileURI(prfFile.toString()), true));
		Assert.assertEquals("2.2", component.getCorbaVersion());
		Assert.assertEquals("IDL:CF/Device:1.0", component.getComponentRepID().getRepid());
		Assert.assertEquals("device", component.getComponentType());
		Assert.assertEquals(ScdFileTemplateTest.DEV_AND_AGGREGATE_SUPPORT_INTERFACE_COUNT, component.getComponentFeatures().getSupportsInterface().size());
		Assert.assertNotNull(component.getComponentFeatures().getPorts());
		Assert.assertEquals(ScdFileTemplateTest.DEV_AND_AGGREGATE_INTERFACE_COUNT, component.getInterfaces().getInterface().size());
	}
}
