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

import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.sca.util.AllJobsDone;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class DeleteHandler extends AbstractHandler {

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			final List<Job> jobs = new ArrayList<Job>();
			for (final Object obj : structuredSelection.toArray()) {
				Job job = null;
				if (obj instanceof SoftPkg) {
					job = createDeleteSoftPkgJob((SoftPkg) obj, event);
				} else if (obj instanceof SoftwareAssembly) {
					job = createDeleteSoftwareAssemblyJob((SoftwareAssembly) obj, event);
				} else if (obj instanceof DeviceConfiguration) {
					job = createDeleteDeviceConfigurationJob((DeviceConfiguration) obj, event);
				}

				if (job != null) {
					jobs.add(job);
				}
			}
			if (!jobs.isEmpty()) {
				final IProgressMonitor progressGroupMonitor = Job.getJobManager().createProgressGroup();
				final AllJobsDone allJobsDone = new AllJobsDone() {
					@Override
					protected void allDone() {
						progressGroupMonitor.done();
						SdrUiPlugin.getDefault().scheduleSdrRootRefresh();
					}
				};
				allJobsDone.addAllJobs(jobs);
				progressGroupMonitor.beginTask("Deleting...", jobs.size());
				for (final Job j : jobs) {
					j.setProgressGroup(progressGroupMonitor, 1);
					j.schedule();
				}
			}
		}
		return null;
	}

	private Job createDeleteDeviceConfigurationJob(final DeviceConfiguration obj, final ExecutionEvent event) {
		final URI uri = obj.eResource().getURI();
		return createDeleteParentDirJob(uri, obj.getName(), event);
	}

	private Job createDeleteSoftwareAssemblyJob(final SoftwareAssembly obj, final ExecutionEvent event) {
		final URI uri = obj.eResource().getURI();
		return createDeleteParentDirJob(uri, obj.getName(), event);
	}

	private Job createDeleteSoftPkgJob(final SoftPkg obj, final ExecutionEvent event) {
		final URI uri = obj.eResource().getURI();
		return createDeleteParentDirJob(uri, obj.getName(), event);
	}

	private Job createDeleteParentDirJob(final URI file, final String name, final ExecutionEvent event) {
		if (!MessageDialog.openQuestion(HandlerUtil.getActiveShell(event), "Delete", "Are you sure you want to delete '" + name + "'?")) {
			return null;
		}

		final Job job = new Job("Deleting " + name) {

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				try {
					final IFileStore efsStore = EFS.getStore(java.net.URI.create(file.toString()));
					final IFileStore parent = efsStore.getParent();
					parent.delete(EFS.NONE, monitor);
				} catch (final CoreException e) {
					return e.getStatus();
				}
				return Status.OK_STATUS;
			}

		};
		job.setUser(false);
		return job;
	}
}
