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

import gov.redhawk.ide.codegen.jet.TopLevelDcdRpmSpecTemplate;
import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.ui.editor.SCAFormEditor;
import mil.jpeojtrs.sca.dcd.DcdPackage;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;

/**
 * * This handler is the main entry point to code generation for Nodes in the UI.
 */
public class GenerateNodeHandler extends AbstractGenerateCodeHandler {

	@Override
	protected void handleEditorSelection(ExecutionEvent event, IEditorPart editor) {
		if (!(editor instanceof SCAFormEditor)) {
			RedhawkCodegenUiActivator.logError("Generate node handler was triggered with an invalid selection", null);
			return;
		}

		SCAFormEditor scaEditor = (SCAFormEditor) editor;

		DeviceConfiguration dcd = DeviceConfiguration.Util.getDeviceConfiguration(scaEditor.getMainResource());
		if (dcd != null) {
			handleDeviceConfiguration(dcd, ModelUtil.getProject(dcd));
			return;
		}

		RedhawkCodegenUiActivator.logError("Couldn't get resource profile from editor in generate waveform handler", null);

	}

	@Override
	protected void handleMenuSelection(ExecutionEvent event, ISelection selection) {
		if (!(selection instanceof IStructuredSelection)) {
			RedhawkCodegenUiActivator.logError("Generate node handler was triggered with an invalid selection", null);
			return;
		}

		final IStructuredSelection ss = (IStructuredSelection) selection;
		for (Object obj : ss.toList()) {
			if (obj instanceof IFile && ((IFile) obj).getName().endsWith(DcdPackage.FILE_EXTENSION)) {
				IFile dcdFile = (IFile) obj;
				final URI dcdURI = URI.createPlatformResourceURI(dcdFile.getFullPath().toString(), false);
				final DeviceConfiguration dcd = ModelUtil.loadDeviceConfiguration(dcdURI);
				handleDeviceConfiguration(dcd, dcdFile.getProject());
			}
		}
	}

	/**
	 * Generates a top-level RPM spec file based on the device manager/devices in the DCD file.
	 */
	private void handleDeviceConfiguration(final DeviceConfiguration dcd, final IProject project) {

		Job dcdSpecFileJob = new Job("Updating spec file for " + dcd.getName() + "...") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {

				final SubMonitor progress = SubMonitor.convert(monitor, "Creating top-level RPM spec file", 1);
				try {
					// Generate content for the RPM spec file
					final TopLevelDcdRpmSpecTemplate template = new TopLevelDcdRpmSpecTemplate();
					final byte[] rpmSpecFileContent = template.generate(dcd).getBytes();

					// Write the file to disk
					final IFile rpmSpecFile = project.getFile(dcd.getName() + ".spec");
					if (rpmSpecFile.exists()) {
						rpmSpecFile.setContents(new ByteArrayInputStream(rpmSpecFileContent), true, false, progress.newChild(1));
					} else {
						rpmSpecFile.create(new ByteArrayInputStream(rpmSpecFileContent), true, progress.newChild(1));
					}
				} catch (CoreException e) {
					StatusManager.getManager().handle(new Status(e.getStatus().getSeverity(), RedhawkCodegenUiActivator.PLUGIN_ID, e.getLocalizedMessage(), e),
						StatusManager.SHOW | StatusManager.LOG);
				}
				return Status.OK_STATUS;
			}
		};
		dcdSpecFileJob.setUser(false);
		dcdSpecFileJob.setSystem(true);
		dcdSpecFileJob.schedule();
	}
}
