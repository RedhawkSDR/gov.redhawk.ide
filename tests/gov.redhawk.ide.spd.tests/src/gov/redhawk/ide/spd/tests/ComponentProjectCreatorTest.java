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
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.scd.ScdPackage;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * A class to test {@link ComponentProjectCreatorTest}.
 */
public class ComponentProjectCreatorTest {

	private static final String COMPONENT_PROJECT_TEST = "componentProjectTest";

	/**
	 * Tests creating a project
	 */
	@Test
	public void testCreateEmptyProject() throws CoreException {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(ComponentProjectCreatorTest.COMPONENT_PROJECT_TEST);
		if (project.exists()) {
			project.delete(true, new NullProgressMonitor());
		}
		project = ComponentProjectCreator.createEmptyProject(ComponentProjectCreatorTest.COMPONENT_PROJECT_TEST, null, new NullProgressMonitor());
		Assert.assertNotNull(project);
		Assert.assertTrue(ComponentProjectCreatorTest.COMPONENT_PROJECT_TEST.equals(project.getName()));
		project.refreshLocal(IResource.DEPTH_INFINITE, null);
		if (project.exists()) {
			project.delete(true, new NullProgressMonitor());
		}
	}

	@Before
	public void cleanUp() throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(ComponentProjectCreatorTest.COMPONENT_PROJECT_TEST);
		if (project.exists()) {
			project.delete(true, true, null);
		}
	}

	@After
	public void cleanUpAfter() throws CoreException {
		cleanUp();
	}

	/**
	 * Tests creating the device files
	 */
	@Test
	public void testCreateDeviceFiles() throws CoreException {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(ComponentProjectCreatorTest.COMPONENT_PROJECT_TEST);
		if (project.exists()) {
			project.delete(true, new NullProgressMonitor());
		}
		project = ComponentProjectCreator.createEmptyProject(ComponentProjectCreatorTest.COMPONENT_PROJECT_TEST, null, new NullProgressMonitor());
		Assert.assertNotNull(project);
		Assert.assertTrue(ComponentProjectCreatorTest.COMPONENT_PROJECT_TEST.equals(project.getName()));

		ComponentProjectCreator.createComponentFiles(project, ComponentProjectCreatorTest.COMPONENT_PROJECT_TEST, "gov.redhawk.componentProjectTest", "Author",
			new NullProgressMonitor());

		final IFile spdFile = project.getFile(project.getName() + SpdPackage.FILE_EXTENSION);
		Assert.assertTrue(spdFile.exists());

		final IFile prfFile = project.getFile(project.getName() + PrfPackage.FILE_EXTENSION);
		Assert.assertTrue(prfFile.exists());

		final IFile scdFile = project.getFile(project.getName() + ScdPackage.FILE_EXTENSION);
		Assert.assertTrue(scdFile.exists());

		final IFolder testFolder = project.getFolder("tests");
		// Assert.assertTrue(testFolder.exists());
		final IFile testFile = testFolder.getFile("test_" + project.getName() + ".py");
		// Assert.assertTrue(testFile.exists());
		String msg = "Test folders are now created by the Jinja code generators, folder must not exist.";
		Assert.assertTrue(msg, testFolder.exists());
		Assert.assertFalse(msg, testFile.exists());

		final ResourceSet resourceSet = new ResourceSetImpl();
		final SoftPkg dev = SoftPkg.Util.getSoftPkg(resourceSet.getResource(
			URI.createPlatformResourceURI("/componentProjectTest/componentProjectTest.spd.xml", true), true));
		Assert.assertEquals(project.getName(), dev.getName());
		Assert.assertEquals("gov.redhawk.componentProjectTest", dev.getId());
		Assert.assertEquals("Author", dev.getAuthor().get(0).getName().get(0));

		project.refreshLocal(IResource.DEPTH_INFINITE, null);
		if (project.exists()) {
			project.delete(true, new NullProgressMonitor());
		}
	}
}
