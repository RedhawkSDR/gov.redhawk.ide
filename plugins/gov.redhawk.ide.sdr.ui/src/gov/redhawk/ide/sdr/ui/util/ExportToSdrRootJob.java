/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 */
package gov.redhawk.ide.sdr.ui.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import gov.redhawk.ide.natures.ScaComponentProjectNature;
import gov.redhawk.ide.natures.ScaNodeProjectNature;
import gov.redhawk.ide.natures.ScaWaveformProjectNature;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.ide.sdr.ui.export.ExportUtils;
import gov.redhawk.ide.sdr.ui.export.IScaExporter;
import gov.redhawk.sca.util.SubMonitor;

/**
 * @since 4.0
 */
public class ExportToSdrRootJob extends Job {

	private IScaExporter exporter;
	private List<IProject> projects;
	private List<IProject> exportedProjects;

	public ExportToSdrRootJob(IScaExporter exporter, List<IProject> projects) {
		super("Export to SDRROOT");
		Assert.isNotNull(exporter);
		Assert.isNotNull(projects);
		this.exporter = exporter;
		this.projects = projects;
		this.exportedProjects = new ArrayList<>();
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			SubMonitor progress = SubMonitor.convert(monitor, projects.size());
			MultiStatus status = new MultiStatus(SdrUiPlugin.PLUGIN_ID, 0, "Export Status", null);

			for (IProject project : projects) {
				String progressMsg = String.format("Exporting %s to SDRROOT", project.getName());
				progress.subTask(progressMsg);

				// Allow cancellation
				if (progress.isCanceled()) {
					return Status.CANCEL_STATUS;
				}

				// Perform export
				try {
					boolean exported = false;
					if (project.hasNature(ScaNodeProjectNature.ID)) {
						if (ExportUtils.exportNode(project, exporter, progress.newChild(1))) {
							exportedProjects.add(project);
							exported = true;
						}
					} else if (project.hasNature(ScaWaveformProjectNature.ID)) {
						if (ExportUtils.exportWaveform(project, exporter, progress.newChild(1))) {
							exportedProjects.add(project);
							exported = true;
						}
					} else if (project.hasNature(ScaComponentProjectNature.ID)) {
						if (ExportUtils.exportComponent(project, exporter, progress.newChild(1))) {
							exportedProjects.add(project);
							exported = true;
						}
					} else {
						String errorMsg = String.format("Can't export project %s that does not have a REDHAWK project nature", project.getName());
						status.add(new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, errorMsg));
					}

					if (exported) {
						String successMsg = String.format("Successfully exported %s", project.getName());
						status.add(new Status(IStatus.OK, SdrUiPlugin.PLUGIN_ID, successMsg));
					}
				} catch (IOException | CoreException ex) {
					String errorMsg = String.format("Error while exporting %s", project.getName());
					status.add(new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, errorMsg, ex));
				}
			}

			progress.done();
			return status;
		} finally {
			try {
				exporter.finished();
			} catch (IOException e) {
				SdrUiPlugin.getDefault().logError("Error while cleaning up exporter", e);
			}
		}
	}

	/**
	 * Gets the list of projects that were successfully exported.
	 * @since 4.3
	 */
	public List<IProject> getExportedProjects() {
		return Collections.unmodifiableList(exportedProjects);
	}
}
