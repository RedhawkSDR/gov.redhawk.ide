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
package gov.redhawk.ide.swtbot.condition;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.ui.statushandlers.StatusManager;

import gov.redhawk.ide.swtbot.SwtBotActivator;

/**
 * Waits for any currently running Eclipse auto-builds to complete
 */
public class WaitForBuild implements ICondition {

	/**
	 * The recommended timeout when using this condition
	 */
	public static final long TIMEOUT = 30000;

	private Thread waitForAutoBuild;

	private long startingWaitTime = 0;

	@Override
	public boolean test() throws Exception {
		if (startingWaitTime == 0) {
			startingWaitTime = System.currentTimeMillis();
		}

		// If the thread is still alive, it's blocked waiting for auto-build to complete
		if (!waitForAutoBuild.isAlive()) {
			String msg = String.format("Build completed in %f seconds", (System.currentTimeMillis() - startingWaitTime) / 1000.0);
			StatusManager.getManager().handle(new Status(IStatus.INFO, SwtBotActivator.PLUGIN_ID, msg), StatusManager.LOG);
			return true;
		}
		return false;
	}

	@Override
	public void init(SWTBot bot) {
		// This thread will join on the object lock for auto-builds
		waitForAutoBuild = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
				} catch (OperationCanceledException e) {
					return;
				} catch (InterruptedException e) {
					return;
				}
			}
		});
		waitForAutoBuild.setDaemon(true);
		waitForAutoBuild.start();
	}

	@Override
	public String getFailureMessage() {
		return "Eclipse auto-build did not complete";
	}

}
