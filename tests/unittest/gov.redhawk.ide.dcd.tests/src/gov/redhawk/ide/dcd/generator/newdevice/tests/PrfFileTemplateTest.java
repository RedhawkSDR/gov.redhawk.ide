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
import gov.redhawk.ide.dcd.generator.newdevice.PrfFileTemplate;
import gov.redhawk.ide.dcd.tests.TestUtils;

import java.io.File;
import java.io.IOException;

import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.prf.Properties;
import mil.jpeojtrs.sca.prf.PropertyConfigurationType;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.junit.Assert;
import org.junit.Test;

/**
 * A class to test {@link PrfFileTemplate}.
 */
public class PrfFileTemplateTest {

	/**
	 * Tests generating a PRF file with empty args.
	 * <p/>
	 * IDE-1292 Also test that no deprecated properties are included.
	 * @throws IOException
	 */
	@Test
	public void test() throws IOException {
		// Generate XML using the template
		final PrfFileTemplate prfTemplate = PrfFileTemplate.create(null);
		GeneratorArgs args = new GeneratorArgs();
		
		final String prfContent = prfTemplate.generate(args);

		// Create an XML file with the content
		final File prfFile = TestUtils.createFile(prfContent, PrfPackage.FILE_EXTENSION);

		// Try to create a model from the file
		final ResourceSet resourceSet = new ResourceSetImpl();
		final Properties props = Properties.Util.getProperties(resourceSet.getResource(URI.createFileURI(prfFile.toString()), true));
		Assert.assertNotNull(props);

		// Ensure configure / execparam aren't used
		for (FeatureMap.Entry propEntry : props.getProperties()) {
			Assert.assertFalse(((AbstractProperty) propEntry.getValue()).isKind(PropertyConfigurationType.CONFIGURE, PropertyConfigurationType.EXECPARAM));
		}
	}
	
	/**
	 * Tests generating a PRF file with null args
	 * @throws IOException
	 */
	@Test
	public void testNull() throws IOException {
		// Generate XML using the template
		final PrfFileTemplate prfTemplate = PrfFileTemplate.create(null);
		
		final String prfContent = prfTemplate.generate(null);

		// Create an XML file with the content
		final File prfFile = TestUtils.createFile(prfContent, PrfPackage.FILE_EXTENSION);

		// Try to create a model from the file
		final ResourceSet resourceSet = new ResourceSetImpl();
		final Properties props = Properties.Util.getProperties(resourceSet.getResource(URI.createFileURI(prfFile.toString()), true));
		Assert.assertNotNull(props);
	}
}
