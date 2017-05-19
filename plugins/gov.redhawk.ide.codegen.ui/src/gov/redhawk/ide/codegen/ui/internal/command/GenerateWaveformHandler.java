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

import gov.redhawk.ide.codegen.jet.SadTemplateParameter;
import gov.redhawk.ide.codegen.jet.TopLevelSadRpmSpecTemplate;
import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import gov.redhawk.ide.codegen.ui.internal.SaveXmlUtils;
import gov.redhawk.ide.codegen.ui.utils.DocumentationUtils;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.ui.editor.SCAFormEditor;
import mil.jpeojtrs.sca.sad.SadDocumentRoot;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

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

		Shell shell = HandlerUtil.getActiveShell(event);
		SoftwareAssembly sad = SoftwareAssembly.Util.getSoftwareAssembly(scaEditor.getMainResource());
		if (sad != null) {
			handleSoftwareAssembly(shell, sad, ModelUtil.getProject(sad));
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
	 * Generates a top-level RPM spec file based on the components and devices referenced in the SAD file.
	 */
	private void handleSoftwareAssembly(Shell shell, final SoftwareAssembly sad, final IProject project) {

		Job sadSpecFileJob = new Job("Performing code generation for " + sad.getName() + "...") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				final SubMonitor progress = SubMonitor.convert(monitor, 2);
				try {
					// Get the header, if any
					String headerContent = DocumentationUtils.getHeaderContents(project);

					// Update the SAD file's header
					updateSadFileHeader(shell, sad, headerContent, progress.newChild(1));

					// Generate the RPM spec file
					updateSpecFile(headerContent, progress.newChild(1));
				} catch (CoreException e) {
					StatusManager.getManager().handle(new Status(e.getStatus().getSeverity(), RedhawkCodegenUiActivator.PLUGIN_ID, e.getLocalizedMessage(), e),
						StatusManager.SHOW | StatusManager.LOG);
				} finally {
					progress.done();
				}

				return Status.OK_STATUS;
			}

			private void updateSadFileHeader(Shell shell, SoftwareAssembly sad, String headerContent, SubMonitor progress) throws CoreException {
				EditingDomain editingDomain = TransactionUtil.getEditingDomain(sad);
				SadDocumentRoot docRoot = ScaEcoreUtils.getEContainerOfType(sad, SadDocumentRoot.class);

				// We make the changes in the UI thread because otherwise there are asynchronous changes that have to
				// propagate to the UI thread (so that the EMF changes can update the XML editor's page).
				RunnableWithResult<CoreException> runnable = new RunnableWithResult.Impl<CoreException>() {
					@Override
					public void run() {
						DocumentationUtils.setXMLCommentHeader(editingDomain, docRoot.getMixed(), headerContent);
						try {
							SaveXmlUtils.save(sad);
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
				final TopLevelSadRpmSpecTemplate template = new TopLevelSadRpmSpecTemplate();
				final String rpmSpecFileString = template.generate(new SadTemplateParameter(sad, headerContent));
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
						rpmSpecFile.delete(true, progress);
					}
				} else if (rpmSpecFile.exists()) {
					rpmSpecFile.setContents(new ByteArrayInputStream(rpmSpecFileContent), true, false, progress);
				} else {
					rpmSpecFile.create(new ByteArrayInputStream(rpmSpecFileContent), true, progress);
				}
			}
		};
		sadSpecFileJob.setUser(false);
		sadSpecFileJob.setSystem(true);
		sadSpecFileJob.schedule();
	}
}
