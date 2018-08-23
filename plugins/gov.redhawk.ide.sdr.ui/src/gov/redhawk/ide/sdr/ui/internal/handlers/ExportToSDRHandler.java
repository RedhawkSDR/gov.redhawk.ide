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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import gov.redhawk.core.notification.ui.Notifications;
import gov.redhawk.ide.sdr.TargetSdrRoot;
import gov.redhawk.ide.sdr.preferences.IdeSdrPreferences;
import gov.redhawk.ide.sdr.ui.export.FileStoreExporter;
import gov.redhawk.ide.sdr.ui.export.IScaExporter;
import gov.redhawk.ide.sdr.ui.util.ExportToSdrRootJob;

public class ExportToSDRHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(@Nullable final ExecutionEvent event) throws ExecutionException {
		final ISelection sel = HandlerUtil.getCurrentSelection(event);
		if (sel instanceof IStructuredSelection) {
			final List<IProject> projects = new ArrayList<IProject>();
			for (Object obj : ((IStructuredSelection) sel).toList()) {
				IProject project = null;
				if (obj instanceof IProject) {
					project = (IProject) obj;
				} else if (obj instanceof IResource) {
					project = ((IResource) obj).getProject();
				}
				if (project != null && project.exists() && project.isOpen() && !projects.contains(project)) {
					projects.add(project);
				}
			}

			// Export, then refresh the SDRROOT
			final IScaExporter exporter = new FileStoreExporter(IdeSdrPreferences.getTargetSdrPath());
			final ExportToSdrRootJob exportJob = new ExportToSdrRootJob(exporter, projects);
			exportJob.addJobChangeListener(new JobChangeAdapter() {
				@Override
				public void done(IJobChangeEvent event) {
					reportStatus(projects, exportJob.getExportedProjects());
					TargetSdrRoot.scheduleRefresh();
				}
			});
			exportJob.schedule();
		}

		return null;
	}

	/**
	 * Report status of an export operation.
	 * @param projectsToExport The projects that the user asked to export
	 * @param exportedProjects The projects that were successfully exported
	 */
	public static void reportStatus(List<IProject> projectsToExport, List<IProject> exportedProjects) {
		if (exportedProjects.size() == 0) {
			// User will have gotten pop-up error(s)
			return;
		}

		if (projectsToExport.size() == exportedProjects.size()) {
			// Everything exported
			String message = Messages.bind(Messages.ExportToSDRHandler_ExportSuccess_Message, exportedProjects.size());
			Notifications.INSTANCE.notify(Notifications.INFO, Messages.ExportToSDRHandler_ExportSuccess_Title, message);
		} else {
			// Some, but not all, exported
			StringBuilder sb = new StringBuilder();
			for (IProject project : exportedProjects) {
				sb.append(project.getName());
				sb.append(", "); //$NON-NLS-1$
			}
			sb.setLength(sb.length() - 2);
			String message = Messages.bind(Messages.ExportToSDRHandler_ExportPartialSuccess_Message, new Object[] { exportedProjects.size(), projectsToExport.size(), sb.toString() });
			Notifications.INSTANCE.notify(Notifications.WARNING, Messages.ExportToSDRHandler_ExportPartialSuccess_Title, message);
		}
	}
}
