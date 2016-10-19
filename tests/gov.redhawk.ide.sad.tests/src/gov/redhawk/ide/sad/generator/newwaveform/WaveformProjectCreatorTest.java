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

package gov.redhawk.ide.sad.generator.newwaveform;

import org.junit.Assert;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.DceUuidUtil;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.junit.Test;

import gov.redhawk.ide.builders.SCABuilder;

/**
 * A class to test {@link WaveformProjectCreatorTest}.
 */
public class WaveformProjectCreatorTest {

	/**
	 * Tests creating an empty project.
	 * IDE-1707 Ensure ScaProjectNature was configured, adding the SCA builder
	 */
	@Test
	public void createEmptyProject() throws CoreException {
		final IProject project = WaveformProjectCreator.createEmptyProject("waveformProjectTest", null, new NullProgressMonitor());

		Assert.assertNotNull(project);
		Assert.assertTrue("waveformProjectTest".equals(project.getName()));
		boolean found = false;
		for (ICommand command : project.getDescription().getBuildSpec()) {
			if (command.getBuilderName().equals(SCABuilder.ID)) {
				found = true;
			}
		}
		Assert.assertTrue("SCA builder not found", found);

		project.delete(true, new NullProgressMonitor());
	}

	/**
	 * Tests creating the waveform files
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCreateWaveformFiles() throws CoreException {
		final IProject project = WaveformProjectCreator.createEmptyProject("waveformProjectTest", null, new NullProgressMonitor());
		Assert.assertNotNull(project);
		Assert.assertTrue("waveformProjectTest".equals(project.getName()));

		WaveformProjectCreator.createWaveformFiles(project, DceUuidUtil.createDceUUID(), null, new NullProgressMonitor());
		final IFile sadFile = project.getFile("waveformProjectTest.sad.xml");
		Assert.assertTrue(sadFile.exists());

		final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		final SoftwareAssembly wave = SoftwareAssembly.Util.getSoftwareAssembly(
			resourceSet.getResource(URI.createPlatformResourceURI("/waveformProjectTest/waveformProjectTest.sad.xml", true), true));
		Assert.assertEquals(project.getName(), wave.getName());

		project.delete(true, new NullProgressMonitor());
	}
}
