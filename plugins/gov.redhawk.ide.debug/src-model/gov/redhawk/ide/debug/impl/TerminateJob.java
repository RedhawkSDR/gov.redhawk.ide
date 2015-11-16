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
 // BEGIN GENERATED CODE
package gov.redhawk.ide.debug.impl;

import gov.redhawk.ide.debug.LocalLaunch;
import gov.redhawk.ide.debug.ScaDebugPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;

public class TerminateJob extends Job {

	private final ILaunch launch;

	/**
	 * @since 7.1
	 */
	public TerminateJob(final ILaunch launch, final String name) {
		super("Terminating " + name);
		this.launch = launch;
	}

	/**
	 * @deprecated Use {@link #TerminateJob(ILaunch, String)}
	 */
	@Deprecated
	public TerminateJob(final LocalLaunch launch, final String name) {
		this(launch.getLaunch(), name);
	}

	@Override
	public boolean belongsTo(Object family) {
		// Make jobs of this class part of a family so they can be found from the job manager
		return family.equals(TerminateJob.class);
	}

	@Override
	public boolean shouldRun() {
		return super.shouldRun() && this.launch.canTerminate();
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		if (this.launch.canTerminate()) {
			try {
				this.launch.terminate();
			} catch (final DebugException e) {
				return new Status(e.getStatus().getSeverity(), ScaDebugPlugin.ID, "Failed to terminate.", e);
			}
		}
		return Status.OK_STATUS;
	}

}
