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
package gov.redhawk.ide.swtbot.condition;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;

public class WaitForExport extends DefaultCondition {

	/**
	 * The recommended timeout to use for the condition when exporting a project such as a component, device, or
	 * service. This accounts for the fact that an external program must be run to perform the export.
	 */
	public static final long TIMEOUT_LONG = 30000;

	/**
	 * The recommended timeout to use for the condition when exporting a project such as a waveform or node. This
	 * accounts for the fact that it's quick for the IDE to copy a file or two to the target.
	 */
	public static final long TIMEOUT_SHORT = 10000;

	@Override
	public boolean test() throws Exception {
		// Ensure the refresh job isn't queued / running
		Job[] jobs = Job.getJobManager().find(null);
		for (Job job : jobs) {
			if ("ExportToSdrRootJob".equals(job.getClass().getSimpleName())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getFailureMessage() {
		return "Export to SDR root job was still running";
	}
}
