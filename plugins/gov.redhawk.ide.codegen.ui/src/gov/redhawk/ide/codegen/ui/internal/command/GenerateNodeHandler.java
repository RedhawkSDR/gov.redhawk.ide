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
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;

import gov.redhawk.ide.codegen.jet.DcdTemplateParameter;
import gov.redhawk.ide.codegen.jet.TopLevelDcdRpmSpecTemplate;
import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import gov.redhawk.ide.codegen.ui.internal.SaveXmlUtils;
import gov.redhawk.ide.codegen.ui.utils.DocumentationUtils;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.ui.editor.SCAFormEditor;
import mil.jpeojtrs.sca.dcd.DcdDocumentRoot;
import mil.jpeojtrs.sca.dcd.DcdPackage;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

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

		Shell shell = HandlerUtil.getActiveShell(event);
		DeviceConfiguration dcd = DeviceConfiguration.Util.getDeviceConfiguration(scaEditor.getMainResource());
		if (dcd != null) {
			handleDeviceConfiguration(shell, dcd, ModelUtil.getProject(dcd));
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
	 */
	private void handleDeviceConfiguration(Shell shell, final DeviceConfiguration dcd, final IProject project) {

		Job dcdSpecFileJob = new Job("Updating spec file for " + dcd.getName() + "...") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				final SubMonitor progress = SubMonitor.convert(monitor, "Creating top-level RPM spec file", 1);

				try {
					// Get the header, if any
					String headerContent = DocumentationUtils.getHeaderContents(project);

					// Update the DCD file's header
					updateDcdFileHeader(shell, dcd, headerContent, progress.newChild(1));

					// Generate the RPM spec file
					updateSpecFile(headerContent, progress.newChild(1));
				} catch (CoreException e) {
					StatusManager.getManager().handle(new Status(e.getStatus().getSeverity(), RedhawkCodegenUiActivator.PLUGIN_ID, e.getLocalizedMessage(), e),
						StatusManager.SHOW | StatusManager.LOG);
				}
				return Status.OK_STATUS;
			}

			private void updateDcdFileHeader(Shell shell, DeviceConfiguration dcd, String headerContent, SubMonitor progress) throws CoreException {
				EditingDomain editingDomain = TransactionUtil.getEditingDomain(dcd);
				DcdDocumentRoot docRoot = ScaEcoreUtils.getEContainerOfType(dcd, DcdDocumentRoot.class);

				// We need to make and save the changes in the UI thread
				RunnableWithResult<CoreException> runnable = new RunnableWithResult.Impl<CoreException>() {
					@Override
					public void run() {
						if (!DocumentationUtils.setXMLCommentHeader(editingDomain, docRoot.getMixed(), headerContent)) {
							return;
						}
						try {
							SaveXmlUtils.save(dcd);
						} catch (CoreException e) {
							setResult(e);
						}
					}
				};
				shell.getDisplay().syncExec(runnable);

				// Re-throw the exception if any
				if (runnable.getResult() != null) {
					throw runnable.getResult();
				}
				progress.done();
			}

			private void updateSpecFile(String headerContent, SubMonitor progress) throws CoreException {
				// Generate content for the RPM spec file
				final TopLevelDcdRpmSpecTemplate template = new TopLevelDcdRpmSpecTemplate();
				final byte[] rpmSpecFileContent = template.generate(new DcdTemplateParameter(dcd, headerContent)).getBytes();

				// Write the file to disk
				final IFile rpmSpecFile = project.getFile(dcd.getName() + ".spec");
				if (rpmSpecFile.exists()) {
					rpmSpecFile.setContents(new ByteArrayInputStream(rpmSpecFileContent), true, false, progress.newChild(1));
				} else {
					rpmSpecFile.create(new ByteArrayInputStream(rpmSpecFileContent), true, progress.newChild(1));
				}
			}
		};
		dcdSpecFileJob.setUser(false);
		dcdSpecFileJob.setSystem(true);
		dcdSpecFileJob.schedule();
	}
}
