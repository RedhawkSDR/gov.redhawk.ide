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
package gov.redhawk.spd.validation.tests;

import gov.redhawk.ide.spd.generator.newcomponent.GeneratorArgs;
import gov.redhawk.ide.spd.generator.newcomponent.PrfFileTemplate;
import gov.redhawk.ide.spd.generator.newcomponent.ScdFileTemplate;
import gov.redhawk.ide.spd.generator.newcomponent.SpdFileTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.scd.ScdPackage;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdFactory;
import mil.jpeojtrs.sca.spd.SpdPackage;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

public class SpdValidationTestResourceProvider {

	private IProject project;

	public SoftPkg createWorkspaceSoftPkgResource() throws Exception {
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		// This creates the new project
		this.project = root.getProject("testDevice");
		// Create an empty project description
		final IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription("testDevice");

		this.project.create(null);
		this.project.open(null);
		this.project.setDescription(description, null);

		return this.createNewResource(null, this.project);
	}

	/**
	 * Create a new component from a template.
	 * 
	 * @param monitor the progress monitor
	 * @param project the project
	 * @throws CoreException an exception
	 */
	private SoftPkg createNewResource(final IProgressMonitor m, final IProject project) throws Exception {
		final GeneratorArgs args = new GeneratorArgs();
		args.setProjectName("Name");
		args.setProjectId(project.getName());

		final String spd = new SpdFileTemplate().generate(args);
		final String prf = new PrfFileTemplate().generate(null);
		final String scd = new ScdFileTemplate().generate(null);

		final IFile spdFile = project.getFile(args.getProjectName() + SpdPackage.FILE_EXTENSION);
		final IFile prfFile = project.getFile(args.getProjectName() + PrfPackage.FILE_EXTENSION);
		final IFile scdFile = project.getFile(args.getProjectName() + ScdPackage.FILE_EXTENSION);

		try {
			spdFile.create(new ByteArrayInputStream(spd.getBytes("UTF-8")), true, null);
			prfFile.create(new ByteArrayInputStream(prf.getBytes("UTF-8")), true, null);
			scdFile.create(new ByteArrayInputStream(scd.getBytes("UTF-8")), true, null);
		} catch (final UnsupportedEncodingException e) {
			throw e;
		}

		final URI spdUri = URI.createPlatformResourceURI(spdFile.getFullPath().toString(), true).appendFragment(SoftPkg.EOBJECT_PATH);

		final ResourceSet set = new ResourceSetImpl();

		// Load the spdFile and save the implementation
		final SoftPkg softPkg = (SoftPkg) set.getEObject(spdUri, true);
		final Implementation impl = SpdFactory.eINSTANCE.createImplementation();

		impl.setCode(SpdFactory.eINSTANCE.createCode());

		softPkg.getImplementation().add(impl);
		try {
			for (final Resource resource : set.getResources()) {
				resource.save(null);
			}
		} catch (final IOException e) {
			throw e;
		}

		return softPkg;
	}

	public void deleteWorkspaceResources() throws CoreException {
		if (this.project != null) {
			this.project.delete(true, true, null);
		}
	}

}
