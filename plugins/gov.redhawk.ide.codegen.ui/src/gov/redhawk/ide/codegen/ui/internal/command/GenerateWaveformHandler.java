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

import java.io.ByteArrayInputStream;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.statushandlers.StatusManager;

import gov.redhawk.ide.codegen.jet.TopLevelSadRpmSpecTemplate;
import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.ui.editor.SCAFormEditor;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

/**
 * This handler is the main entry point to code generation for Waveforms in the UI.
 */
public class GenerateWaveformHandler extends AbstractGenerateCodeHandler {

	@Override
	protected void handleEditorSelection(ExecutionEvent event, IEditorPart editor) {
		if (!(editor instanceof SCAFormEditor)) {
			RedhawkCodegenUiActivator.logError("Generate waveform handler was triggered with an invalid selection", null);
			return;
		}

		SCAFormEditor scaEditor = (SCAFormEditor) editor;

		SoftwareAssembly sad = SoftwareAssembly.Util.getSoftwareAssembly(scaEditor.getMainResource());
		if (sad != null) {
			handleSoftwareAssembly(sad, ModelUtil.getProject(sad));
			return;
		}

		RedhawkCodegenUiActivator.logError("Couldn't get resource profile from editor in generate waveform handler", null);

	}

	@Override
	protected void handleMenuSelection(ExecutionEvent event, ISelection selection) {
		if (!(selection instanceof IStructuredSelection)) {
			RedhawkCodegenUiActivator.logError("Generate waveform handler was triggered with an invalid selection", null);
			return;
		}

		final IStructuredSelection ss = (IStructuredSelection) selection;
		for (Object obj : ss.toList()) {
			if (obj instanceof IFile && ((IFile) obj).getName().endsWith(SadPackage.FILE_EXTENSION)) {
				IFile sadFile = (IFile) obj;
				final URI sadURI = URI.createPlatformResourceURI(sadFile.getFullPath().toString(), false);
				final SoftwareAssembly sad = ModelUtil.loadSoftwareAssembly(sadURI);
				handleSoftwareAssembly(sad, sadFile.getProject());
			}
		}

	}

	/**
	 * Generates a top-level RPM spec file based on the components and devices referenced in the SAD file.
	 */
	private void handleSoftwareAssembly(final SoftwareAssembly sad, final IProject project) {

		Job sadSpecFileJob = new Job("Updating spec file for " + sad.getName() + "...") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				final SubMonitor progress = SubMonitor.convert(monitor, "Creating top-level RPM spec file", 1);
				try {
					final TopLevelSadRpmSpecTemplate template = new TopLevelSadRpmSpecTemplate();
					final String rpmSpecFileString = template.generate(sad);
					final byte[] rpmSpecFileContent;
					if (rpmSpecFileString == null) {
						rpmSpecFileContent = null;
					} else {
						rpmSpecFileContent = rpmSpecFileString.getBytes();
					}
					// Write the file to disk
					final IFile rpmSpecFile = project.getFile(sad.getName() + ".spec");
					if (rpmSpecFileContent == null) {
						if (rpmSpecFile.exists()) {
							rpmSpecFile.delete(true, progress.newChild(1));
						}
					} else if (rpmSpecFile.exists()) {
						rpmSpecFile.setContents(new ByteArrayInputStream(rpmSpecFileContent), true, false, progress.newChild(1));
					} else {
						rpmSpecFile.create(new ByteArrayInputStream(rpmSpecFileContent), true, progress.newChild(1));
					}
				} catch (CoreException e) {
					StatusManager.getManager().handle(new Status(e.getStatus().getSeverity(), RedhawkCodegenUiActivator.PLUGIN_ID, e.getLocalizedMessage(), e),
						StatusManager.SHOW | StatusManager.LOG);
				} finally {
					progress.done();
				}

				return Status.OK_STATUS;
			}
		};
		sadSpecFileJob.setUser(false);
		sadSpecFileJob.setSystem(true);
		sadSpecFileJob.schedule();
	}

}
