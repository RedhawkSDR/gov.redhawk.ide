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

import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import gov.redhawk.ide.natures.ScaComponentProjectNature;
import gov.redhawk.ide.natures.ScaNodeProjectNature;
import gov.redhawk.ide.natures.ScaWaveformProjectNature;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.ide.sdr.ui.export.ExportUtils;
import gov.redhawk.ide.sdr.ui.export.FileStoreExporter;
import gov.redhawk.ide.sdr.ui.export.IScaExporter;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;

public class ExportResourceHandler extends AbstractHandler implements IHandler {
	private final IScaExporter exporter;

	public ExportResourceHandler() {
		this.exporter = new FileStoreExporter(SdrUiPlugin.getDefault().getTargetSdrPath());
	}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getActiveMenuSelection(event);

		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection ss = (IStructuredSelection) selection;

			for (final Object obj : ss.toList()) {
				final IFile file = (IFile) obj;

				final IProject project = file.getProject();

				final WorkspaceJob job = new WorkspaceJob("Exporting REDHAWK Resource") {

					@Override
					public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException {
						try {
							if (project.hasNature(ScaNodeProjectNature.ID)) {
								ExportUtils.exportNode(project, ExportResourceHandler.this.exporter, new NullProgressMonitor());
							} else if (project.hasNature(ScaComponentProjectNature.ID)) {
								ExportUtils.exportComponent(project, ExportResourceHandler.this.exporter, new NullProgressMonitor());
							} else if (project.hasNature(ScaWaveformProjectNature.ID)) {
								ExportUtils.exportWaveform(project, ExportResourceHandler.this.exporter, new NullProgressMonitor());
							}

							ExportResourceHandler.this.exporter.finished();
						} catch (final CoreException e) {
							StatusManager.getManager().handle(e, RedhawkCodegenUiActivator.PLUGIN_ID);
						} catch (final java.io.IOException e) {
							StatusManager.getManager().handle(new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "Failed to export.", e),
							        StatusManager.LOG | StatusManager.SHOW);
						}

						return Status.OK_STATUS;
					}
				};
				job.setPriority(Job.LONG);
				job.setUser(true);
				job.setSystem(false);
				job.schedule();
			}
		}
		return null;
	}
}
