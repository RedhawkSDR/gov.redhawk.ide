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
package gov.redhawk.ide.swtbot.condition;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;

import gov.redhawk.ide.sdr.LoadState;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;

public class WaitForTargetSdrRootLoad extends DefaultCondition {

	/**
	 * The recommended timeout when using this condition
	 */
	public static final long TIMEOUT = 10000;

	private LoadState lastObservedState;

	private boolean jobRunning = false;

	@Override
	public boolean test() throws Exception {
		lastObservedState = SdrUiPlugin.getDefault().getTargetSdrRoot().getState();

		// Ensure the refresh job isn't queued / running
		Job[] jobs = Job.getJobManager().find(SdrUiPlugin.FAMILY_REFRESH_SDR);
		if (jobs.length != 0) {
			jobRunning = true;
			return false;
		}
		jobRunning = false;

		return lastObservedState == LoadState.LOADED;
	}

	@Override
	public String getFailureMessage() {
		if (jobRunning) {
			return "Refresh job for target SDR was still running";
		}
		return String.format("Target SDR Root failed to load in model (state was %s)", lastObservedState);
	}

}
