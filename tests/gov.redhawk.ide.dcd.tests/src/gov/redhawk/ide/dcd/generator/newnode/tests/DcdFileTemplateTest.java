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

import gov.redhawk.ide.dcd.generator.newdevice.SpdFileTemplate;
import gov.redhawk.ide.dcd.generator.newnode.DcdFileTemplate;
import gov.redhawk.ide.dcd.tests.TestUtils;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Test;

/**
 * A class to test {@link DcdFileTemplate}
 */
public class DcdFileTemplateTest {

	/**
	 * Tests generating a DCD file.
	 * 
	 * @throws IOException
	 */
	@Test
	public void test() throws IOException {
		// Generate XML using the template
		final SpdFileTemplate spdTemplate = SpdFileTemplate.create(null);
		final gov.redhawk.ide.dcd.generator.newdevice.GeneratorArgs args1 = new gov.redhawk.ide.dcd.generator.newdevice.GeneratorArgs();
		args1.setAuthorName("MyName");
		args1.setSoftPkgId("MyIdentifier");
		args1.setSoftPkgName("MyResource");
		args1.setProjectName("MyProject");
		final String spdContent = spdTemplate.generate(args1);

		// Create an XML file with the content
		final File spdFile = TestUtils.createFile(spdContent, SpdPackage.FILE_EXTENSION);

		// Create a model for the device
		final ResourceSet resourceSet = new ResourceSetImpl();
		final SoftPkg softpkg = SoftPkg.Util.getSoftPkg(resourceSet.getResource(URI.createFileURI(spdFile.toString()), true));

		// Now generate XML for a DCD
		final gov.redhawk.ide.dcd.generator.newnode.GeneratorArgs args2 = new gov.redhawk.ide.dcd.generator.newnode.GeneratorArgs();
		args2.setAuthorName(args1.getAuthorName());
		args2.setNodeElements(new SoftPkg[] { softpkg });
		args2.setDomainManagerName("MyDomain");
		args2.setNodeName("MyNodeName");
		args2.setNodeId("MyNodeID");
		args2.setProjectName(args1.getProjectName());
		final DcdFileTemplate dcdTemplate = DcdFileTemplate.create(null);
		final String dcdContent = dcdTemplate.generate(args2);

		// Create an XML file with the content
		final File dcdFile = TestUtils.createFile(dcdContent, ".dcd.xml");

		// Try to create a model from the file and test some of the fields
		// that should have been filled in
		final DeviceConfiguration devcfg = DeviceConfiguration.Util.getDeviceConfiguration(
			resourceSet.getResource(URI.createFileURI(dcdFile.toString()), true));
		Assert.assertEquals("MyNodeName", devcfg.getName());
		Assert.assertEquals(args2.getNodeId(), devcfg.getId());
		Assert.assertEquals("/mgr/DeviceManager.spd.xml", devcfg.getDeviceManagerSoftPkg().getLocalFile().getName());
		Assert.assertEquals("SPD", devcfg.getComponentFiles().getComponentFile().get(0).getType());
		Assert.assertTrue(devcfg.getComponentFiles().getComponentFile().get(0).getId().startsWith(args1.getSoftPkgName() + "_"));
		Assert.assertEquals(spdFile.getAbsolutePath(), devcfg.getComponentFiles().getComponentFile().get(0).getLocalFile().getName());
		Assert.assertEquals(devcfg.getPartitioning().getComponentPlacement().get(0).getComponentInstantiation().get(0).getId(), args2.getNodeName() + ":" + args1.getSoftPkgName() + "_1");
		Assert.assertTrue(devcfg.getPartitioning().getComponentPlacement().get(0).getComponentFileRef().getRefid().startsWith(args1.getSoftPkgName() + "_"));
		Assert.assertEquals(args1.getSoftPkgName() + "_1", devcfg.getPartitioning().getComponentPlacement().get(0).getComponentInstantiation().get(0).getUsageName());
		Assert.assertEquals(args2.getDomainManagerName() + "/" + args2.getDomainManagerName(), devcfg.getDomainManager().getNamingService().getName());
	}
}
