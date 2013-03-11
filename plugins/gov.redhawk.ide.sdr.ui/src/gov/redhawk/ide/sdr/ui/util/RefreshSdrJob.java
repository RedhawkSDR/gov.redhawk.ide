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
package gov.redhawk.ide.sdr.ui.util;

import gov.redhawk.ide.sdr.SdrRoot;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @since 3.1
 * 
 */
public class RefreshSdrJob extends Job {

	private final SdrRoot sdrRoot;

	public RefreshSdrJob(final SdrRoot sdrRoot) {
		super("Refreshing SDR Root");
		setPriority(Job.LONG);
		setUser(true);
		this.sdrRoot = sdrRoot;
	}

	@Override
	public boolean shouldSchedule() {
		return this.sdrRoot != null;
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		this.sdrRoot.reload(monitor);
		return Status.OK_STATUS;
	}

}
