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

package gov.redhawk.ide.dcd.generator.newnode.tests;

import gov.redhawk.ide.dcd.generator.newnode.GeneratorArgs;
import gov.redhawk.ide.dcd.generator.newnode.SpdFileTemplate;
import gov.redhawk.ide.dcd.tests.TestUtils;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Test;

/**
 * A class to test {@link ScdFileTemplate}
 */
public class SpdFileTemplateTest {

	/**
	 * Tests generating a SPD file.
	 * 
	 * @throws IOException
	 */
	@Test
	public void test() throws IOException {
		// Generate XML using the template
		final SpdFileTemplate spdTemplate = SpdFileTemplate.create(null);
		final GeneratorArgs args = new GeneratorArgs();
		args.setAuthorName("MyName");
		args.setProjectId("MyIdentifier");
		args.setProjectName("MyProject");
		final String spdContent = spdTemplate.generate(args);

		// Create an XML file with the content
		final File spdFile = TestUtils.createFile(spdContent, SpdPackage.FILE_EXTENSION);

		// Try to create a model from the file
		final ResourceSet resourceSet = new ResourceSetImpl();
		final SoftPkg softpkg = SoftPkg.Util.getSoftPkg(resourceSet.getResource(URI.createFileURI(spdFile.toString()), true));
		Assert.assertEquals(args.getProjectName(), softpkg.getName());
		Assert.assertEquals(args.getProjectId(), softpkg.getId());
		Assert.assertEquals(args.getAuthorName(), softpkg.getAuthor().get(0).getName().get(0));
		Assert.assertEquals("nodeBooter", softpkg.getImplementation().get(0).getCode().getLocalFile().getName());
	}

}
