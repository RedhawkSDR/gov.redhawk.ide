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
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.ui.statushandlers.StatusManager;

import gov.redhawk.ide.swtbot.SwtBotActivator;

/**
 * Waits for the specified build to be complete. If the build is not running, the condition is satisfied.
 */
public class WaitForBuild extends DefaultCondition {

	/**
	 * The recommended timeout when using this condition
	 */
	public static final long TIMEOUT = 60000;

	private long startingWaitTime = 0;

	private BuildType buildType;
	private boolean firstTime = true;

	public enum BuildType {
		/**
		 * An automatic build triggered because the "Build Automatically" option is enabled in Eclipse.
		 */
		AUTO,

		/**
		 * A full clean/build of a project performed by the code generation process.
		 */
		CODEGEN
	}

	/**
	 * @param buildType
	 */
	public WaitForBuild(BuildType buildType) {
		this.buildType = buildType;
	}

	@Override
	public void init(SWTBot bot) {
		super.init(bot);
		startingWaitTime = System.currentTimeMillis();
	}

	@Override
	public boolean test() throws Exception {
		boolean done;
		switch (buildType) {
		case AUTO:
			done = !isAutoBuildRunning();
			break;
		case CODEGEN:
			done = !isGenerateCodeBuildRunning();
			break;
		default:
			done = false;
			break;
		}

		if (!done) {
			firstTime = false;
			return false;
		}

		if (firstTime) {
			String msg = String.format("Eclipse build of type %s was not running", buildType.toString());
			StatusManager.getManager().handle(new Status(IStatus.INFO, SwtBotActivator.PLUGIN_ID, msg), StatusManager.LOG);
		} else {
			String msg = String.format("Eclipse build of type %s completed in %f seconds", buildType.toString(),
				(System.currentTimeMillis() - startingWaitTime) / 1000.0);
			StatusManager.getManager().handle(new Status(IStatus.INFO, SwtBotActivator.PLUGIN_ID, msg), StatusManager.LOG);
		}
		return true;
	}

	private boolean isAutoBuildRunning() {
		Job[] jobs = Job.getJobManager().find(ResourcesPlugin.FAMILY_AUTO_BUILD);
		return jobs.length > 0;
	}

	private boolean isGenerateCodeBuildRunning() {
		Job[] jobs = Job.getJobManager().find(ResourcesPlugin.FAMILY_MANUAL_BUILD);
		for (Job job : jobs) {
			if (job.getClass().getName().startsWith("gov.redhawk.ide.codegen.ui.internal.job.CodegenJob$")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getFailureMessage() {
		return "An Eclipse build of type " + buildType.toString() + " did not complete";
	}

}
