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
package gov.redhawk.ide.codegen.ui.action;

import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.IScaComponentCodegen;
import gov.redhawk.ide.codegen.ui.internal.GeneratorConsole;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;

/**
 * @since 2.0
 */
public class CleanUpGeneratorRunnable implements IWorkspaceRunnable {

	private final ICodeGeneratorDescriptor codeGenDesc;
	private final IProject project;

	/**
	 * @param codeGenDesc
	 * @param project
	 */
	public CleanUpGeneratorRunnable(final ICodeGeneratorDescriptor codeGenDesc, final IProject project) {
		super();
		this.codeGenDesc = codeGenDesc;
		this.project = project;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run(final IProgressMonitor monitor) throws CoreException {
		GeneratorConsole genConsole = null;
		final IConsole[] consoles = ConsolePlugin.getDefault().getConsoleManager().getConsoles();
		for (final IConsole console : consoles) {
			if (console instanceof GeneratorConsole && console.getType().equals(this.codeGenDesc.getId())) {
				genConsole = (GeneratorConsole) console;
			}
		}
		if (genConsole == null) {
			genConsole = new GeneratorConsole(this.codeGenDesc);
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { genConsole });
		}

		final IScaComponentCodegen generator = this.codeGenDesc.getGenerator();

		generator.cleanupSourceFolders(this.project, monitor);
	}

}
