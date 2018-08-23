/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.sdr.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;

import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.TargetSdrRoot;

public class RefreshSdrJob extends Job {

	private SdrRoot sdrRoot;

	public RefreshSdrJob(SdrRoot sdrRoot) {
		super("Refreshing SDR Root");
		setPriority(Job.LONG);
		setUser(true);
		this.sdrRoot = sdrRoot;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		SubMonitor progress = SubMonitor.convert(monitor);
		this.sdrRoot.reload(progress);
		progress.done();
		return Status.OK_STATUS;
	}

	@Override
	public boolean belongsTo(Object family) {
		return TargetSdrRoot.FAMILY_REFRESH_SDR == family;
	}
}
