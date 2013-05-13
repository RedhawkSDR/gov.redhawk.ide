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
package gov.redhawk.ide.debug.impl;

import gov.redhawk.ide.debug.LocalLaunch;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;

/**
 * 
 */
public class TerminateJob extends Job {

	private final LocalLaunch launch;

	public TerminateJob(final LocalLaunch launch, final String name) {
		super("Terminating " + name);
		this.launch = launch;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean shouldRun() {
		return super.shouldRun() && this.launch != null && this.launch.getLaunch().canTerminate();
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		if (this.launch.getLaunch().canTerminate()) {
			try {
				this.launch.getLaunch().terminate();
			} catch (final DebugException e) {
				return e.getStatus();
			}
		}
		return Status.OK_STATUS;
	}

}
