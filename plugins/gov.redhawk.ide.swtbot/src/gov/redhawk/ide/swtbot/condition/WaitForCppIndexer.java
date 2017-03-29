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

/**
 * Waits for the C/C++ index to finish. If the indexer is not running, the condition is satisfied.
 */
public class WaitForCppIndexer extends DefaultCondition {

	public static final long TIMEOUT = 60000;

	@Override
	public boolean test() throws Exception {
		Job[] jobs = Job.getJobManager().find(null);
		for (Job job : jobs) {
			if ("org.eclipse.cdt.internal.core.pdom.PDOMIndexerJob".equals(job.getClass().getName())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getFailureMessage() {
		return "C/C++ indexer was still running";
	}

}
