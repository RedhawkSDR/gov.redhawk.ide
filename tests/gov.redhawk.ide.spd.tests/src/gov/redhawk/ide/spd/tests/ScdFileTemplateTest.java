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

package gov.redhawk.ide.spd.tests;

import gov.redhawk.ide.spd.generator.newcomponent.ScdFileTemplate;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;

import mil.jpeojtrs.sca.scd.Interface;
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

	private static final int SUPPORT_INTERFACE_COUNT = 10;
	private static final int INTERFACE_COUNT = 10;
	private static final int INHERITS_INTERFACE_COUNT = 9;

	/**
	 * Tests generating an SCD file for a component
	 * 
	 * @throws IOException The SCD file can't be written to disk
	 */
	@Test
	public void componentScdFile() throws IOException {
		// Generate XML using the template
		final ScdFileTemplate scdTemplate = ScdFileTemplate.create(null);
		final String scdContent = scdTemplate.generate(null);

		// Create an XML file with the content
		final File prfFile = TestUtils.createFile(scdContent, ScdPackage.FILE_EXTENSION);

		// Try to create a model from the file
		final ResourceSet resourceSet = new ResourceSetImpl();
		final SoftwareComponent component = SoftwareComponent.Util.getSoftwareComponent(resourceSet.getResource(URI.createFileURI(prfFile.toString()), true));
		Assert.assertEquals("2.2", component.getCorbaVersion());
		Assert.assertEquals("IDL:CF/Resource:1.0", component.getComponentRepID().getRepid());
		Assert.assertEquals("resource", component.getComponentType());
		Assert.assertEquals(ScdFileTemplateTest.SUPPORT_INTERFACE_COUNT, component.getComponentFeatures().getSupportsInterface().size());
		Assert.assertNotNull(component.getComponentFeatures().getPorts());
		Assert.assertEquals(ScdFileTemplateTest.INTERFACE_COUNT, component.getInterfaces().getInterface().size());
		int count = 0;
		for (Interface intf : component.getInterfaces().getInterface()) {
			count += intf.getInheritsInterfaces().size();
		}
		Assert.assertEquals(INHERITS_INTERFACE_COUNT, count);
	}
}
