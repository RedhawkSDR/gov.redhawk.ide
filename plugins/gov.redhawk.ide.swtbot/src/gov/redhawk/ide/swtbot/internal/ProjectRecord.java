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
package gov.redhawk.ide.swtbot.internal;

import java.io.File;

import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Class declared public only for test suite.
 * 
 */
public class ProjectRecord {
	public File projectSystemFile;

	public String projectName;

	public IProjectDescription description;

	/**
	 * Create a record for a project based on the info in the file.
	 * 
	 * @param file
	 * @throws CoreException
	 */
	public ProjectRecord(File file) throws CoreException {
		projectSystemFile = file;
		setProjectName();
	}

	/**
	 * Set the name of the project based on the projectFile.
	 * @throws CoreException
	 */
	private void setProjectName() throws CoreException {
		// If we don't have the project name try again
		if (projectName == null) {
			IPath path = new Path(projectSystemFile.getPath());

			description = ResourcesPlugin.getWorkspace().loadProjectDescription(path);
			projectName = description.getName();

		}
	}

	/**
	 * Get the name of the project
	 * 
	 * @return String
	 */
	public String getProjectName() {
		return projectName;
	}
}
