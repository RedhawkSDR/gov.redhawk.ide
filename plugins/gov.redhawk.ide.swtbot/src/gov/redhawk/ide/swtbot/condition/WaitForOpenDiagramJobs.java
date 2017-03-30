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

import gov.redhawk.sca.ui.ScaUI;

/**
 * Returns true while there are unfinished jobs to open diagrams.
 */
public class WaitForOpenDiagramJobs extends DefaultCondition {

	/**
	 * Default timeout that should be used when opening runtime diagrams
	 */
	public static final long DEFAULT_TIMEOUT = 10000;

	@Override
	public boolean test() throws Exception {
		return Job.getJobManager().find(ScaUI.FAMILY_OPEN_EDITOR).length == 0;
	}

	@Override
	public String getFailureMessage() {
		return "Editor open jobs did not complete";
	}

}
