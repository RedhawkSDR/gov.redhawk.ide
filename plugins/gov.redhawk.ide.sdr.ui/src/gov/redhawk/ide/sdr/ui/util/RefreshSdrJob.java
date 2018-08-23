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
import gov.redhawk.ide.sdr.TargetSdrRoot;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @since 3.1
 * @deprecated No replacement (see non-public gov.redhawk.ide.sdr.jobs.RefreshSdrJob)
 */
@Deprecated
public class RefreshSdrJob extends Job {

	private SdrRoot sdrRoot;

	public RefreshSdrJob(final SdrRoot sdrRoot) {
		super("Refreshing SDR Root");
		setPriority(Job.LONG);
		setUser(true);
		this.sdrRoot = sdrRoot;
	}

	/**
	 * Refreshes the Target SDR
	 * @since 3.3
	 * @deprecated Use {@link SdrUiPlugin#scheduleSdrRootRefresh()}
	 */
	@Deprecated
	public RefreshSdrJob() {
		this(TargetSdrRoot.getSdrRoot());
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
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
