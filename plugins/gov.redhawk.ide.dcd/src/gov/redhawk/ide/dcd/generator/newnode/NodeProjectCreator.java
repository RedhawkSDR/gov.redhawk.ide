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
package gov.redhawk.ide.dcd.generator.newnode;

import gov.redhawk.ide.codegen.util.ProjectCreator;
import gov.redhawk.ide.dcd.IdeDcdPlugin;
import gov.redhawk.ide.natures.ScaNodeProjectNature;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

public class NodeProjectCreator extends ProjectCreator {

	private NodeProjectCreator() {
	}

	/**
	 * Creates a new SCA node project without any files. Should be invoked in the context of a
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
		final String[] additionalNatureIDs = new String[] { ScaNodeProjectNature.ID };
		return ProjectCreator.createEmptyProject(projectName, projectLocation, additionalNatureIDs, monitor);
	}

	/**
	 * Creates the basic files for a node in an empty SCA component project. Should be invoked in the context of a
	 * {@link org.eclipse.ui.actions.WorkspaceModifyOperation WorkspaceModifyOperation}.
	 * 
	 * @param project The project to generate files in
	 * @param projectID The project's ID (DCE)
	 * @param authorName The name of the component author
	 * @param domainManagerName The domain manager's name
	 * @param devices The devices to add to the node
	 * @param monitor the progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 *  to call done() on the given monitor. Accepts null, indicating that no progress should be
	 *  reported and that the operation cannot be canceled.
	 * @return The newly created DCD file
	 * @throws CoreException An error occurs while generating files
	 */
	public static IFile createNodeFiles(final IProject project, final String projectID, final String authorName, final String domainManagerName,
	        final SoftPkg[] devices, final IProgressMonitor monitor) throws CoreException {
		final SubMonitor progress = SubMonitor.convert(monitor, "Creating SCA node files", 2);

		final GeneratorArgs args = new GeneratorArgs();
		args.setProjectName(project.getName());
		args.setProjectId(projectID);
		args.setAuthorName(authorName);
		args.setDomainManagerName(domainManagerName);
		args.setDevices(devices);

		// Generate file content from templates
		final String dcd = new DcdFileTemplate().generate(args);
		progress.worked(1);

		// Check that files/folders don't exist already
		final IFile dcdFile = project.getFile("DeviceManager.dcd.xml");
		if (dcdFile.exists()) {
			throw new CoreException(new Status(IStatus.ERROR, IdeDcdPlugin.PLUGIN_ID, "File " + dcdFile.getName() + " already exists.", null));
		}

		// Write files to disk
		try {
			dcdFile.create(new ByteArrayInputStream(dcd.getBytes("UTF-8")), true, progress.newChild(1));
		} catch (final UnsupportedEncodingException e) {
			throw new CoreException(new Status(IStatus.ERROR, IdeDcdPlugin.PLUGIN_ID, "Internal Error", e));
		}

		return dcdFile;
	}
}
