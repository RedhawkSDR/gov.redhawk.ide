/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.codegen.ui.internal.command;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import gov.redhawk.ide.codegen.FileStatus;
import gov.redhawk.ide.codegen.FileStatus.Action;
import gov.redhawk.ide.codegen.FileStatus.State;
import gov.redhawk.ide.codegen.FileStatus.Type;
import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import gov.redhawk.ide.codegen.ui.internal.job.ProjectUserFileSelectionJob;
import gov.redhawk.ide.codegen.ui.internal.job.SadCodegenJob;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.ui.editor.SCAFormEditor;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

/**
 * This handler is the main entry point to code generation for Waveforms in the UI.
 */
public class GenerateWaveformHandler extends AbstractGenerateCodeHandler {

	@Override
	protected void handleEditorSelection(ExecutionEvent event, IEditorPart editor) throws CoreException {
		if (!(editor instanceof SCAFormEditor)) {
			RedhawkCodegenUiActivator.logError("Generate waveform handler was triggered with an invalid selection", null);
			return;
		}
		SCAFormEditor scaEditor = (SCAFormEditor) editor;

		Shell shell = HandlerUtil.getActiveShell(event);
		SoftwareAssembly sad = SoftwareAssembly.Util.getSoftwareAssembly(scaEditor.getMainResource());
		if (sad != null) {
			handleSoftwareAssembly(shell, sad, ModelUtil.getProject(sad));
			return;
		}

		RedhawkCodegenUiActivator.logError("Couldn't get resource profile from editor in generate waveform handler", null);
	}

	@Override
	protected void handleMenuSelection(ExecutionEvent event, ISelection selection) throws CoreException {
		if (!(selection instanceof IStructuredSelection)) {
			RedhawkCodegenUiActivator.logError("Generate waveform handler was triggered with an invalid selection", null);
			return;
		}
		final IStructuredSelection ss = (IStructuredSelection) selection;

		Shell shell = HandlerUtil.getActiveShell(event);
		for (Object obj : ss.toList()) {
			if (obj instanceof IFile && ((IFile) obj).getName().endsWith(SadPackage.FILE_EXTENSION)) {
				IFile sadFile = (IFile) obj;
				final URI sadURI = URI.createPlatformResourceURI(sadFile.getFullPath().toString(), false);
				final SoftwareAssembly sad = ModelUtil.loadSoftwareAssembly(sadURI);
				handleSoftwareAssembly(shell, sad, sadFile.getProject());
			}
		}
	}

	/**
	 * Generates related support files for a SAD file.
	 * @throws CoreException
	 */
	private void handleSoftwareAssembly(Shell shell, final SoftwareAssembly sad, final IProject project) throws CoreException {
		// Prompt to save (if necessary) before continuing
		if (!saveRelatedResources(shell, project)) {
			return;
		}

		if (sad.getName() == null || sad.getName().trim().isEmpty()) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "SAD file doesn't have a name set"));
		}

		Set<FileStatus> fileStatuses = getCodegenFileStatus(sad, project);

		ProjectUserFileSelectionJob selectFilesJob = new ProjectUserFileSelectionJob(shell, fileStatuses);
		SadCodegenJob codegenJob = new SadCodegenJob(sad, project);

		// After selecting files -> generate
		selectFilesJob.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				if (event.getResult().isOK()) {
					codegenJob.setFilesToGenerate(selectFilesJob.getFilesToGenerate());
					codegenJob.schedule();
				}
			}
		});

		// Schedule the first job
		selectFilesJob.schedule();
	}

	/**
	 * @return A simulated list of file status (like we'd get from the code generator)
	 */
	private Set<FileStatus> getCodegenFileStatus(SoftwareAssembly sad, IProject project) {
		Set<FileStatus> fileStatuses = new HashSet<>();

		String specFileName = sad.getName() + ".spec";
		Action action = (project.getFile(specFileName).exists()) ? Action.REGEN : Action.ADDING;
		fileStatuses.add(new FileStatus(specFileName, action, State.MATCHES, Type.SYSTEM));

		String iniFileName = sad.getName() + ".ini";
		action = (project.getFile(iniFileName).exists()) ? Action.REGEN : Action.ADDING;
		State state = (project.getFile(iniFileName).exists()) ? State.MODIFIED : State.MATCHES;
		fileStatuses.add(new FileStatus(iniFileName, action, state, Type.USER));

		return fileStatuses;
	}
}
