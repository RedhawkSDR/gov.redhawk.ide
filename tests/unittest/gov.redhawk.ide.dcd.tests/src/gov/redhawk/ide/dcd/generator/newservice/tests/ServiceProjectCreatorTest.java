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

import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.ide.dcd.generator.newdevice.DeviceProjectCreator;
import gov.redhawk.ide.dcd.generator.newservice.ServiceProjectCreator;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;

import org.junit.Assert;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.scd.ScdPackage;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Test;

/**
 * A class to test {@link ServiceProjectCreatorTest}.
 */
public class ServiceProjectCreatorTest {

	/**
	 * Tests creating a project
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCreateEmptyProject() throws CoreException {
		final IProject project = ServiceProjectCreator.createEmptyProject("serviceProjectTest", null, new NullProgressMonitor());
		Assert.assertNotNull(project);
		Assert.assertTrue("serviceProjectTest".equals(project.getName()));
		project.delete(true, new NullProgressMonitor());
	}

	/**
	 * Tests creating the device files
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCreateServiceFiles() throws CoreException {
		final IProject project = DeviceProjectCreator.createEmptyProject("serviceProjectTest", null, new NullProgressMonitor());
		final IdlLibrary library = SdrUiPlugin.getDefault().getTargetSdrRoot().getIdlLibrary();
		Assert.assertNotNull(project);
		Assert.assertTrue("serviceProjectTest".equals(project.getName()));
		ServiceProjectCreator.createServiceFiles(project, "serviceProjectTest",
				"gov.redhawk.serviceProjectTest",
		        "Author",
		        library,
		        "IDL:MULTICASTALLOCATION/MulticastAllocationService:1.0",
		        new NullProgressMonitor());

		final IFile spdFile = project.getFile(project.getName() + SpdPackage.FILE_EXTENSION);
		Assert.assertTrue(spdFile.exists());

		final IFile prfFile = project.getFile(project.getName() + PrfPackage.FILE_EXTENSION);
		Assert.assertFalse(prfFile.exists());

		final IFile scdFile = project.getFile(project.getName() + ScdPackage.FILE_EXTENSION);
		Assert.assertTrue(scdFile.exists());

		final ResourceSet resourceSet = new ResourceSetImpl();
		final SoftPkg dev = SoftPkg.Util.getSoftPkg(resourceSet.getResource(URI.createPlatformResourceURI("/serviceProjectTest/serviceProjectTest.spd.xml",
		        true), true));
		Assert.assertEquals(project.getName(), dev.getName());

		project.delete(true, new NullProgressMonitor());
	}
}
