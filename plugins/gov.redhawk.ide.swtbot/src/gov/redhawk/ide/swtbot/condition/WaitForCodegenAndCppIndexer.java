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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.ui.statushandlers.StatusManager;

import gov.redhawk.ide.swtbot.SwtBotActivator;

/**
 * Waits for the Code Generator and C/C++ index to finish. If the indexer is not running, the condition is satisfied.
 */
public class WaitForCodegenAndCppIndexer extends DefaultCondition {

	public static final long TIMEOUT = 120000;

	private long startingWaitTimeCodegen;
	private long startingWaitTimeIndexer;
	private boolean firstTimeCodegen;
	private boolean firstTimeIndexer;
	private boolean finishedCodegen;
	private boolean finishedIndexer;

	@Override
	public void init(SWTBot bot) {
		super.init(bot);
		startingWaitTimeCodegen = System.currentTimeMillis();
		startingWaitTimeIndexer = System.currentTimeMillis();
		firstTimeCodegen = true;
		firstTimeIndexer = true;
	}

	@Override
	public boolean test() throws Exception {
		Job[] jobs = Job.getJobManager().find(null);
		boolean foundCodegen = false;
		boolean foundIndexer = false;

		for (Job job : jobs) {
			if ("org.eclipse.cdt.internal.core.pdom.PDOMIndexerJob".equals(job.getClass().getName())) {
				foundIndexer = true;
				firstTimeIndexer = false;
			} else if (job.getClass().getName().startsWith("gov.redhawk.ide.codegen.ui.internal.job.CodegenJob$")) {
				foundCodegen = true;
				firstTimeCodegen = false;
			}
		}

		if (firstTimeCodegen) {
			StatusManager.getManager().handle(new Status(IStatus.INFO, SwtBotActivator.PLUGIN_ID, "Code Generator was not running"), StatusManager.LOG);
		} else if (!firstTimeCodegen && !foundCodegen && !finishedCodegen) {
			String msg = String.format("Code Generator completed in %f seconds", (System.currentTimeMillis() - startingWaitTimeCodegen) / 1000.0);
			StatusManager.getManager().handle(new Status(IStatus.INFO, SwtBotActivator.PLUGIN_ID, msg), StatusManager.LOG);
			finishedCodegen = true;
		}
		if (firstTimeIndexer) {
			StatusManager.getManager().handle(new Status(IStatus.INFO, SwtBotActivator.PLUGIN_ID, "CDT indexer was not running"), StatusManager.LOG);
		} else if (!firstTimeIndexer && !foundIndexer && !finishedIndexer) {
			String msg = String.format("CDT indexer completed in %f seconds", (System.currentTimeMillis() - startingWaitTimeIndexer) / 1000.0);
			StatusManager.getManager().handle(new Status(IStatus.INFO, SwtBotActivator.PLUGIN_ID, msg), StatusManager.LOG);
			finishedIndexer = true;
		}

		return !(foundIndexer || foundCodegen);
	}

	@Override
	public String getFailureMessage() {
		return "C/C++ indexer was still running";
	}

}
