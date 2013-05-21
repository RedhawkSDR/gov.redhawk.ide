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

import gov.redhawk.ide.dcd.generator.newdevice.DeviceProjectCreator;
import gov.redhawk.ide.preferences.RedhawkIdePreferenceConstants;

import junit.framework.Assert;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.scd.Interface;
import mil.jpeojtrs.sca.scd.ScdPackage;
import mil.jpeojtrs.sca.scd.SupportsInterface;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Test;

/**
 * A class to test {@link DeviceProjectCreatorTest}.
 */
public class DeviceProjectCreatorTest {

	/**
	 * Tests creating a project
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCreateEmptyProject() throws CoreException {
		final IProject project = DeviceProjectCreator.createEmptyProject("deviceProjectTest", null, new NullProgressMonitor());
		Assert.assertNotNull(project);
		Assert.assertTrue("deviceProjectTest".equals(project.getName()));
		project.delete(true, new NullProgressMonitor());
	}

	/**
	 * Tests creating the device files
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCreateDeviceFiles() throws CoreException {
		final IProject project = DeviceProjectCreator.createEmptyProject("deviceProjectTest", null, new NullProgressMonitor());
		Assert.assertNotNull(project);
		Assert.assertTrue(project.exists());
		Assert.assertTrue("deviceProjectTest".equals(project.getName()));
		DeviceProjectCreator.createDeviceFiles(project, "deviceProjectTest", "Author", 
		        RedhawkIdePreferenceConstants.EXECUTABLE_DEVICE,
		        false,
		        new NullProgressMonitor());

		final IFile spdFile = project.getFile(project.getName() + SpdPackage.FILE_EXTENSION);
		Assert.assertTrue(spdFile.exists());

		final IFile prfFile = project.getFile(project.getName() + PrfPackage.FILE_EXTENSION);
		Assert.assertTrue(prfFile.exists());

		final IFile scdFile = project.getFile(project.getName() + ScdPackage.FILE_EXTENSION);
		Assert.assertTrue(scdFile.exists());

		final IFolder testFolder = project.getFolder("tests");
		Assert.assertTrue(testFolder.exists());
		final IFile testFile = testFolder.getFile("test_" + project.getName() + ".py");
		Assert.assertTrue(testFile.exists());

		final ResourceSet resourceSet = new ResourceSetImpl();
		final SoftPkg dev = SoftPkg.Util.getSoftPkg(resourceSet.getResource(URI.createPlatformResourceURI("/deviceProjectTest/deviceProjectTest.spd.xml", true),
		        true));
		Assert.assertEquals(project.getName(), dev.getName());
		Assert.assertEquals("Author", dev.getAuthor().get(0).getName().get(0));
		Assert.assertEquals("executabledevice", dev.getDescriptor().getComponent().getComponentType());

		project.delete(true, new NullProgressMonitor());
	}

	/**
	 * Tests that if an aggregate device is requested that it gets created
	 * @throws CoreException 
	 */
	@Test
	public void testCreateAggregateDevice() throws CoreException {
		final IProject project = DeviceProjectCreator.createEmptyProject("aggDevTest", null, new NullProgressMonitor());
		Assert.assertNotNull(project);
		Assert.assertTrue(project.exists());
		Assert.assertTrue("aggDevTest".equals(project.getName()));
		DeviceProjectCreator.createDeviceFiles(project, "aggDevTest", 
		        "Author",
		        RedhawkIdePreferenceConstants.EXECUTABLE_DEVICE,
		        true,
		        new NullProgressMonitor());

		final IFile spdFile = project.getFile(project.getName() + SpdPackage.FILE_EXTENSION);
		Assert.assertTrue(spdFile.exists());

		final IFile prfFile = project.getFile(project.getName() + PrfPackage.FILE_EXTENSION);
		Assert.assertTrue(prfFile.exists());

		final IFile scdFile = project.getFile(project.getName() + ScdPackage.FILE_EXTENSION);
		Assert.assertTrue(scdFile.exists());

		final ResourceSet resourceSet = new ResourceSetImpl();
		final SoftPkg dev = SoftPkg.Util.getSoftPkg(resourceSet.getResource(URI.createPlatformResourceURI("/aggDevTest/aggDevTest.spd.xml", true), true));

		boolean found = false;
		for (final SupportsInterface intf : dev.getDescriptor().getComponent().getComponentFeatures().getSupportsInterface()) {
			if (intf.getRepId().equals("IDL:CF/AggregateDevice:1.0")) {
				found = true;
				break;
			}
		}
		Assert.assertTrue("Couldn't find the AggregateDevice interface in the component supported interfaces", found);

		found = false;
		for (final Interface intf : dev.getDescriptor().getComponent().getInterfaces().getInterface()) {
			if (intf.getRepid().equals("IDL:CF/AggregateDevice:1.0")) {
				found = true;
				break;
			}
		}
		Assert.assertTrue("Couldn't find the AggregateDevice interface in the interfaces list", found);

		project.delete(true, new NullProgressMonitor());
	}
}
