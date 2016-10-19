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
package gov.redhawk.ide.natures;

import gov.redhawk.ide.builders.SCABuilder;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * A REDHAWK nature added to all REDHAWK projects
 */
public class ScaProjectNature implements IProjectNature {

	/** The ID of this project nature. */
	public static final String ID = "gov.redhawk.ide.natures.scaproject";

	/** The project associated with this nature. */
	private IProject project;

	@Override
	public void configure() throws CoreException {
		addBuilder();
	}

	/**
	 * Adds the SCA builder to the project, if not already present.
	 * @throws CoreException
	 */
	private void addBuilder() throws CoreException {
		// If the builder is already present, we're done
		final IProjectDescription desc = this.project.getDescription();
		final ICommand[] commands = desc.getBuildSpec();
		for (ICommand command : commands) {
			if (command.getBuilderName().equals(SCABuilder.ID)) {
				return;
			}
		}

		// Add the SCA builder to the beginning of the builder list
		final ICommand command = desc.newCommand();
		command.setBuilderName(SCABuilder.ID);
		final ICommand[] newCommands = new ICommand[commands.length + 1];
		System.arraycopy(commands, 0, newCommands, 1, commands.length);
		newCommands[0] = command;
		desc.setBuildSpec(newCommands);
		this.project.setDescription(desc, null);
	}

	@Override
	public void deconfigure() throws CoreException {
	}

	@Override
	public IProject getProject() {
		return this.project;
	}

	@Override
	public void setProject(final IProject project) {
		this.project = project;
	}
}
