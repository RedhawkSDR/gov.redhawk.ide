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
package gov.redhawk.ide.codegen.ui;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.progress.WorkbenchJob;

import gov.redhawk.ide.codegen.ui.internal.job.CodegenFileStatusJob;
import gov.redhawk.ide.codegen.ui.internal.job.CodegenJob;
import gov.redhawk.ide.codegen.ui.internal.job.OpenEditorJob;
import gov.redhawk.ide.codegen.ui.internal.job.UserFileSelectionJob;
import mil.jpeojtrs.sca.spd.Implementation;

/**
 * This class is the primary entry point to code generation.
 * @since 7.0
 */
public final class GenerateCode {

	private GenerateCode() {
	}

	/**
	 * Performs the code generation process for the specified implementation(s). The process may prompt the user for
	 * input. The process occurs in a job and is thus asynchronous.
	 * <p/>
	 * This entry point does not perform any deprecation checks, upgrades, etc. For that, see
	 * {@link gov.redhawk.ide.codegen.ui.internal.command.GenerateCodeHandler}.
	 * @since 8.0
	 */
	public static void generate(final Shell shell, final List<Implementation> impls) {
		if (impls.isEmpty()) {
			return;
		}

		// First ask the codegen what files it will act on
		final CodegenFileStatusJob getFilesJob = new CodegenFileStatusJob(impls);

		// Prompt the user, if necessary, and determine what files we'll actually generate
		final UserFileSelectionJob selectFilesJob = new UserFileSelectionJob(shell);

		// Perform code generation
		final CodegenJob codegenJob = new CodegenJob();

		// Job change adapters to pass results from one job to the next
		getFilesJob.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				if (!event.getResult().isOK()) {
					return;
				}
				selectFilesJob.setImplementationsAndFiles(getFilesJob.getFilesForImplementation());
				selectFilesJob.schedule();	
			}
		});
		selectFilesJob.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				if (!event.getResult().isOK()) {
					return;
				}
				codegenJob.setImplementationsAndFiles(selectFilesJob.getFilesForImplementation());
				codegenJob.schedule();	
			}
		});
		codegenJob.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				if (!event.getResult().isOK()) {
					return;
				}

				// Open editors for new "primary" files in projects
				for (IFile file : codegenJob.getFilesToOpen()) {
					WorkbenchJob openJob = new OpenEditorJob(file);
					openJob.setPriority(Job.SHORT);
					openJob.schedule();
				}
			}
		});

		// Start
		getFilesJob.schedule();
	}
}
