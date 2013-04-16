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

import gov.redhawk.ide.spd.generator.newcomponent.ComponentProjectCreator;

import java.io.IOException;

import junit.framework.Assert;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.scd.ScdPackage;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Test;

/**
 * A class to test {@link ComponentProjectCreatorTest}.
 */
public class ComponentProjectCreatorTest {

	/**
	 * Tests creating a project
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCreateEmptyProject() throws CoreException {
		final IProject project = ComponentProjectCreator.createEmptyProject("componentProjectTest", null, new NullProgressMonitor());
		Assert.assertNotNull(project);
		Assert.assertTrue("componentProjectTest".equals(project.getName()));
		project.refreshLocal(IResource.DEPTH_INFINITE, null);
		if (project.exists()) {
			project.delete(true, new NullProgressMonitor());
		}
	}
	
	/**
	 * Tests creating the device files
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCreateDeviceFiles() throws CoreException {
		final IProject project = ComponentProjectCreator.createEmptyProject("componentProjectTest", null, new NullProgressMonitor());
		Assert.assertNotNull(project);
		Assert.assertTrue("componentProjectTest".equals(project.getName()));
		
		ComponentProjectCreator.createComponentFiles(project, "componentProjectTest", "gov.redhawk.componentProjectTest", "Author", new NullProgressMonitor());

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
		final SoftPkg dev = SoftPkg.Util.getSoftPkg(resourceSet.getResource(URI.createPlatformResourceURI("/componentProjectTest/componentProjectTest.spd.xml", true), true));
		Assert.assertEquals(project.getName(), dev.getName());
		Assert.assertEquals("gov.redhawk.componentProjectTest", dev.getId());
		Assert.assertEquals("Author", dev.getAuthor().get(0).getName().get(0));

		project.refreshLocal(IResource.DEPTH_INFINITE, null);
		if (project.exists()) {
			project.delete(true, new NullProgressMonitor());
		}
	}
}
