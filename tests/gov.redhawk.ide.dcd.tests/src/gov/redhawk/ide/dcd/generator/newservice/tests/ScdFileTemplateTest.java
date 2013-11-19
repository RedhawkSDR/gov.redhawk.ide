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

package gov.redhawk.ide.dcd.generator.newservice.tests;

import gov.redhawk.ide.dcd.generator.newservice.GeneratorArgs;
import gov.redhawk.ide.dcd.generator.newservice.ScdFileTemplate;
import gov.redhawk.ide.dcd.tests.TestUtils;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import mil.jpeojtrs.sca.scd.ScdPackage;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.util.DceUuidUtil;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Test;

/**
 * A class to test {@link ScdFileTemplate}.
 */
public class ScdFileTemplateTest {

	/**
	 * Tests generating an SCD file for an executable device
	 * 
	 * @throws IOException
	 */
	@Test
	public void testService() throws IOException {
		// Generate XML using the template
		final ScdFileTemplate scdTemplate = ScdFileTemplate.create(null);
		final GeneratorArgs args = new GeneratorArgs();
		args.setProjectName("project");
		args.setProjectId(DceUuidUtil.createDceUUID());
		args.setAuthorName("Doe");
		args.setProperty("service_repid", "IDL:MULTICASTALLOCATION/MulticastAllocationService:1.0");
		final String scdContent = scdTemplate.generate(args);

		// Create an XML file with the content
		final File scdFile = TestUtils.createFile(scdContent, ScdPackage.FILE_EXTENSION);

		// Try to create a model from the file
		final ResourceSet resourceSet = new ResourceSetImpl();
		final SoftwareComponent component = SoftwareComponent.Util.getSoftwareComponent(resourceSet.getResource(URI.createFileURI(scdFile.toString()), true));
		Assert.assertEquals("2.2", component.getCorbaVersion());
		Assert.assertEquals("IDL:MULTICASTALLOCATION/MulticastAllocationService:1.0", component.getComponentRepID().getRepid());
		Assert.assertEquals("service", component.getComponentType());
		Assert.assertEquals(1, component.getComponentFeatures().getSupportsInterface().size());
		Assert.assertNotNull(component.getComponentFeatures().getPorts());
		Assert.assertEquals(1, component.getInterfaces().getInterface().size());
	}

}
