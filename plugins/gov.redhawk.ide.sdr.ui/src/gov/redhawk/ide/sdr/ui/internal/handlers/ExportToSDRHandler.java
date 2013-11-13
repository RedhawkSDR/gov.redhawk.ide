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
package gov.redhawk.ide.sdr.ui.internal.handlers;

import gov.redhawk.ide.natures.ScaComponentProjectNature;
import gov.redhawk.ide.natures.ScaNodeProjectNature;
import gov.redhawk.ide.natures.ScaWaveformProjectNature;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.ide.sdr.ui.export.ExportUtils;
import gov.redhawk.ide.sdr.ui.export.FileStoreExporter;
import gov.redhawk.ide.sdr.ui.export.IScaExporter;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class ExportToSDRHandler extends AbstractHandler implements IHandler {
	private static final IPath SDRROOT = new Path(System.getenv("SDRROOT"));
	private IScaExporter exporter;

	@Override
	public Object execute(@NonNull final ExecutionEvent event)
			throws ExecutionException {
		this.exporter = new FileStoreExporter(SDRROOT);
		final ISelection sel = HandlerUtil.getCurrentSelection(event);
		Job exportJob = new Job("Exporting Project to SDR") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				final SubMonitor subMonitor = SubMonitor.convert(monitor,
						"Exporting...", 5);
				if (sel instanceof IStructuredSelection) {
					Object obj = ((IStructuredSelection) sel).getFirstElement();
					if (obj instanceof IProject) {
						IProject project = (IProject) obj;
						try {
							if (project.hasNature(ScaNodeProjectNature.ID)) {
								ExportUtils.exportNode(project, exporter,
										subMonitor.newChild(1));
							} else if (project
									.hasNature(ScaWaveformProjectNature.ID)) {
								ExportUtils.exportWaveform(project, exporter,
										subMonitor.newChild(1));
							} else if (project
									.hasNature(ScaComponentProjectNature.ID)) {
								ExportUtils.exportComponent(project, exporter,
										subMonitor.newChild(1));
							}
						} catch (CoreException e) {
							return new Status(IStatus.CANCEL,
									SdrUiPlugin.PLUGIN_ID,
									"Export unsuccessful", e);
						} catch (IOException e) {
							return new Status(IStatus.CANCEL,
									SdrUiPlugin.PLUGIN_ID,
									"Export unsuccessful", e);
						}

						// Refresh Target SDR
						SdrUiPlugin.getDefault().getTargetSdrRoot()
								.reload(subMonitor.newChild(1));
					}
				}
				return Status.OK_STATUS;
			}
		};
		exportJob.schedule();
		return null;
	}
}
