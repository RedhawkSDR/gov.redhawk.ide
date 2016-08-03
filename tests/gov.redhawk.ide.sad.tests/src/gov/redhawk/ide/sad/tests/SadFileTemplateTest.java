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

package gov.redhawk.ide.sad.tests;

import gov.redhawk.ide.sad.generator.newwaveform.GeneratorArgs;
import gov.redhawk.ide.sad.generator.newwaveform.SadFileTemplate;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Test;

/**
 * A class to test SAD XML (Waveform) file generation of SadFileTemplate.
 */
public class SadFileTemplateTest {

	/**
	 * Tests generating a SAD file
	 */
	@SuppressWarnings("restriction")
	@Test
	public void testGenerate() throws IOException {
		// Generate XML using the template
		final SadFileTemplate sadTemplate = SadFileTemplate.create(null);
		final GeneratorArgs args = new GeneratorArgs();
		args.setWaveformId("TestWaveformID");
		args.setWaveformName("TestWaveformName");
		
		final String prfContent = sadTemplate.generate(args);

		// Create an XML file with the content
		final File sadFile = TestUtils.createFile(prfContent, SadPackage.FILE_EXTENSION);

		// Try to create a model from the file
		final ResourceSet resourceSet = new ResourceSetImpl();
		final SoftwareAssembly assembly = SoftwareAssembly.Util.getSoftwareAssembly(resourceSet.getResource(URI.createFileURI(sadFile.toString()), true));
		assertEquals("ID", args.getWaveformId(), assembly.getId());
		assertEquals("Name", args.getWaveformName(), assembly.getName());
		assertNotNull("partitioning", assembly.getPartitioning());
		assertNotNull("assemblycontroller", assembly.getAssemblyController());
	}
}
