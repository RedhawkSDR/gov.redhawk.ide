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

import gov.redhawk.ide.codegen.util.ProjectCreator;
import gov.redhawk.ide.natures.ScaWaveformProjectNature;
import gov.redhawk.ide.sad.IdeSadPlugin;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

/**
 * A utility class for creating REDHAWK waveform projects.
 */
public class WaveformProjectCreator extends ProjectCreator {

	private WaveformProjectCreator() {
	}

	/**
	 * Creates a new REDHAWK waveform project without any files. Should be invoked in the context of a
	 * {@link org.eclipse.ui.actions.WorkspaceModifyOperation WorkspaceModifyOperation}.
	 * 
	 * @param projectName The project name
	 * @param projectLocation the location on disk to create the project
	 * @param monitor the progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 *  to call done() on the given monitor. Accepts null, indicating that no progress should be
	 *  reported and that the operation cannot be canceled.
	 * @return The newly created project
	 * @throws CoreException A problem occurs while creating the project
	 */
	public static IProject createEmptyProject(final String projectName, final URI projectLocation, final IProgressMonitor monitor) throws CoreException {
		final String[] additionalNatureIDs = new String[] {
			ScaWaveformProjectNature.ID
		};
		return ProjectCreator.createEmptyProject(projectName, projectLocation, additionalNatureIDs, monitor);
	}

	/**
	 * Creates the basic files for a waveform in an empty REDHAWK component project. Should be invoked in the context of a
	 * {@link org.eclipse.ui.actions.WorkspaceModifyOperation WorkspaceModifyOperation}.
	 * 
	 * @param project The project to generate files in
	 * @param waveformID The waveform's ID
	 * @param assemblyController The soft package of the assembly controller
	 * @param monitor the progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 *  to call done() on the given monitor. Accepts null, indicating that no progress should be
	 *  reported and that the operation cannot be canceled.
	 * @return The newly created SAD file
	 * @throws CoreException An error occurs while generating files
	 */
	public static IFile createWaveformFiles(final IProject project, final String waveformID, final SoftPkg assemblyController, final IProgressMonitor monitor)
	        throws CoreException {
		final SubMonitor progress = SubMonitor.convert(monitor, "Creating REDHAWK waveform files", 2);

		final GeneratorArgs args = new GeneratorArgs();
		args.setProjectName(project.getName());
		args.setWaveformId(waveformID);
		
		args.setWaveformName(project.getName());
		
		args.setAssemblyConroller(assemblyController);

		// Generate file content from templates
		final String sad = new SadFileTemplate().generate(args);
		progress.worked(1);

		// Check that files/folders don't exist already
		final IFile sadFile = project.getFile(getBaseFileName(project) + SadPackage.FILE_EXTENSION);
		if (sadFile.exists()) {
			throw new CoreException(new Status(IStatus.ERROR, IdeSadPlugin.PLUGIN_ID, "File " + sadFile.getName() + " already exists.", null));
		}

		// Write files to disk
		try {
			sadFile.create(new ByteArrayInputStream(sad.getBytes("UTF-8")), true, progress.newChild(1));
		} catch (final UnsupportedEncodingException e) {
			throw new CoreException(new Status(IStatus.ERROR, IdeSadPlugin.PLUGIN_ID, "Internal Error", e));
		}

		return sadFile;
	}
}
