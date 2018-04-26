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
import gov.redhawk.ide.codegen.ui.internal.job.DcdCodegenJob;
import gov.redhawk.ide.codegen.ui.internal.job.ProjectUserFileSelectionJob;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.ui.editor.SCAFormEditor;
import mil.jpeojtrs.sca.dcd.DcdPackage;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;

/**
 * * This handler is the main entry point to code generation for Nodes in the UI.
 */
public class GenerateNodeHandler extends AbstractGenerateCodeHandler {

	@Override
	protected void handleEditorSelection(ExecutionEvent event, IEditorPart editor) throws CoreException {
		if (!(editor instanceof SCAFormEditor)) {
			RedhawkCodegenUiActivator.logError("Generate node handler was triggered with an invalid selection", null);
			return;
		}
		SCAFormEditor scaEditor = (SCAFormEditor) editor;

		Shell shell = HandlerUtil.getActiveShell(event);
		DeviceConfiguration dcd = DeviceConfiguration.Util.getDeviceConfiguration(scaEditor.getMainResource());
		if (dcd != null) {
			handleDeviceConfiguration(shell, dcd, ModelUtil.getProject(dcd));
			return;
		}

		RedhawkCodegenUiActivator.logError("Couldn't get resource profile from editor in generate waveform handler", null);
	}

	@Override
	protected void handleMenuSelection(ExecutionEvent event, ISelection selection) throws CoreException {
		if (!(selection instanceof IStructuredSelection)) {
			RedhawkCodegenUiActivator.logError("Generate node handler was triggered with an invalid selection", null);
			return;
		}

		Shell shell = HandlerUtil.getActiveShell(event);
		final IStructuredSelection ss = (IStructuredSelection) selection;
		for (Object obj : ss.toList()) {
			if (obj instanceof IFile && ((IFile) obj).getName().endsWith(DcdPackage.FILE_EXTENSION)) {
				IFile dcdFile = (IFile) obj;
				final URI dcdURI = URI.createPlatformResourceURI(dcdFile.getFullPath().toString(), false);
				final DeviceConfiguration dcd = ModelUtil.loadDeviceConfiguration(dcdURI);
				handleDeviceConfiguration(shell, dcd, dcdFile.getProject());
			}
		}
	}

	/**
	 * Generates a top-level RPM spec file based on the device manager/devices in the DCD file.
	 * @throws CoreException
	 */
	private void handleDeviceConfiguration(Shell shell, final DeviceConfiguration dcd, final IProject project) throws CoreException {
		// Prompt to save (if necessary) before continuing
		if (!saveRelatedResources(shell, project)) {
			return;
		}

		if (dcd.getName() == null || dcd.getName().trim().isEmpty()) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "DCD file doesn't have a name set"));
		}

		Set<FileStatus> fileStatuses = getCodegenFileStatus(dcd, project);

		ProjectUserFileSelectionJob selectFilesJob = new ProjectUserFileSelectionJob(shell, fileStatuses);
		DcdCodegenJob codegenJob = new DcdCodegenJob(dcd, project);

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
	private Set<FileStatus> getCodegenFileStatus(DeviceConfiguration dcd, IProject project) {
		Set<FileStatus> fileStatuses = new HashSet<>();

		String specFileName = dcd.getName() + ".spec";
		Action action = (project.getFile(specFileName).exists()) ? Action.REGEN : Action.ADDING;
		fileStatuses.add(new FileStatus(specFileName, action, State.MATCHES, Type.SYSTEM));

		String iniFileName = dcd.getName() + ".ini";
		action = (project.getFile(iniFileName).exists()) ? Action.REGEN : Action.ADDING;
		State state = (project.getFile(iniFileName).exists()) ? State.MODIFIED : State.MATCHES;
		fileStatuses.add(new FileStatus(iniFileName, action, state, Type.USER));

		return fileStatuses;
	}
}
