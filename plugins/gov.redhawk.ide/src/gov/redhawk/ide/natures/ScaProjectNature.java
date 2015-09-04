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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configure() throws CoreException {
		// This code came from the PDE Help
		final IProjectDescription desc = this.project.getDescription();
		final ICommand[] commands = desc.getBuildSpec();
		boolean found = false;

		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(SCABuilder.ID)) {
				found = true;
				break;
			}
		}
		if (!found) {
			// add builder to project
			final ICommand command = desc.newCommand();
			command.setBuilderName(SCABuilder.ID);
			final ICommand[] newCommands = new ICommand[commands.length + 1];

			// Add it before other builders.
			System.arraycopy(commands, 0, newCommands, 1, commands.length);
			newCommands[0] = command;
			desc.setBuildSpec(newCommands);
			this.project.setDescription(desc, null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deconfigure() throws CoreException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IProject getProject() {
		return this.project;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProject(final IProject project) {
		this.project = project;
	}

}
