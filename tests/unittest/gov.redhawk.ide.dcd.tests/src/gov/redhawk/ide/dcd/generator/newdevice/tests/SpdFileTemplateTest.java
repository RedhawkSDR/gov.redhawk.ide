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
import gov.redhawk.ide.dcd.generator.newdevice.SpdFileTemplate;
import gov.redhawk.ide.dcd.tests.TestUtils;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.scd.ScdPackage;
import mil.jpeojtrs.sca.spd.SCAComplianceType;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Test;

/**
 * A class to test {@link SpdFileTemplate}.
 */
public class SpdFileTemplateTest {

	/**
	 * Tests generating an SPD file
	 * 
	 * @throws IOException
	 * @deprecated Tes deprecated API
	 */
	@Test
	@Deprecated
	public void test() throws IOException {
		// Generate XML using the template
		final SpdFileTemplate spdTemplate = SpdFileTemplate.create(null);
		final GeneratorArgs args = new GeneratorArgs();
		args.setAuthorName("MyName");
		args.setSoftPkgId("MyIdentifier");
		args.setProjectName("MyProject");
		args.setSoftPkgName("MySpdName");
		args.setPrfFile("MyPrfName" + PrfPackage.FILE_EXTENSION);
		args.setScdFile("MyScdName" + ScdPackage.FILE_EXTENSION);
		final String spdContent = spdTemplate.generate(args);

		// Create an XML file with the content
		final File spdFile = TestUtils.createFile(spdContent, SpdPackage.FILE_EXTENSION);

		// Try to create a model from the file
		final ResourceSet resourceSet = new ResourceSetImpl();
		final SoftPkg softpkg = SoftPkg.Util.getSoftPkg(resourceSet.getResource(URI.createFileURI(spdFile.toString()), true));
		Assert.assertEquals(args.getSoftPkgName(), softpkg.getName());
		Assert.assertEquals(args.getSoftPkgId(), softpkg.getId());
		Assert.assertEquals(SCAComplianceType.SCA_COMPLIANT.getLiteral(), softpkg.getType());
		Assert.assertEquals("", softpkg.getTitle());
		Assert.assertEquals(args.getAuthorName(), softpkg.getAuthor().get(0).getName().get(0));
		Assert.assertEquals("PRF", softpkg.getPropertyFile().getType());
		Assert.assertEquals(args.getPrfFile(), softpkg.getPropertyFile().getLocalFile().getName());
		Assert.assertEquals(args.getScdFile(), softpkg.getDescriptor().getLocalfile().getName());
	}

}
