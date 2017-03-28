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

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
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
	public static final long TIMEOUT = 30000;

	private long startingWaitTime = 0;
	private long endingWaitTime = 0;

	private IResourceChangeListener resourceListener;
	private IJobChangeListener jobListener;

	private BuildType buildType;
	private boolean buildStarted = false;

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
	 * @deprecated Use {@link #WaitForBuild(BuildType)}
	 */
	@Deprecated
	public WaitForBuild() {
		this(BuildType.AUTO);
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

		switch (buildType) {
		case AUTO:
			attachAutoBuildListener();
			buildStarted = isAutoBuildRunning();
			break;
		case CODEGEN:
			attachGenerateCodeBuildListener();
			buildStarted = isGenerateCodeBuildRunning();
			break;
		default:
			throw new IllegalStateException();
		}
	}

	@Override
	public boolean test() throws Exception {
		// If the build wasn't started, we're done
		if (!buildStarted) {
			removeListeners();
			String msg = String.format("Eclipse build of type %s was not running", buildType.toString());
			StatusManager.getManager().handle(new Status(IStatus.INFO, SwtBotActivator.PLUGIN_ID, msg), StatusManager.LOG);
			return true;
		}

		// If the build was started, but hasn't finished
		if (endingWaitTime == 0) {
			return false;
		}

		removeListeners();
		String msg = String.format("Eclipse build of type %s completed in %f seconds", buildType.toString(), (endingWaitTime - startingWaitTime) / 1000.0);
		StatusManager.getManager().handle(new Status(IStatus.INFO, SwtBotActivator.PLUGIN_ID, msg), StatusManager.LOG);
		return true;
	}

	private boolean isAutoBuildRunning() {
		Job[] jobs = Job.getJobManager().find(ResourcesPlugin.FAMILY_AUTO_BUILD);
		return jobs.length > 0;
	}

	private void attachAutoBuildListener() {
		resourceListener = new IResourceChangeListener() {

			@Override
			public void resourceChanged(IResourceChangeEvent event) {
				if (event.getBuildKind() != IncrementalProjectBuilder.AUTO_BUILD) {
					return;
				}

				if (endingWaitTime == 0) {
					endingWaitTime = System.currentTimeMillis();
				}
				removeListeners();
			}
		};
		ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceListener, IResourceChangeEvent.PRE_BUILD | IResourceChangeEvent.POST_BUILD);
	}

	private boolean isGenerateCodeBuildRunning() {
		Job[] jobs = Job.getJobManager().find(null);
		for (Job job : jobs) {
			if (isGenerateCodeBuildJob(job)) {
				return true;
			}
		}
		return false;
	}

	private void attachGenerateCodeBuildListener() {
		jobListener = new JobChangeAdapter() {

			@Override
			public void done(IJobChangeEvent event) {
				if (!event.getJob().getClass().getName().startsWith("gov.redhawk.ide.codegen.ui.GenerateCode$")
					|| !event.getJob().getName().startsWith("Building Project")) {
					return;
				}

				if (endingWaitTime == 0) {
					endingWaitTime = System.currentTimeMillis();
				}
				removeListeners();
			}
		};
		Job.getJobManager().addJobChangeListener(jobListener);
	}

	private boolean isGenerateCodeBuildJob(Job job) {
		return job.getClass().getName().startsWith("gov.redhawk.ide.codegen.ui.GenerateCode$") && job.getName().startsWith("Building Project");
	}

	private void removeListeners() {
		if (resourceListener != null) {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceListener);
		}
		if (jobListener != null) {
			Job.getJobManager().removeJobChangeListener(jobListener);
		}
	}

	@Override
	public String getFailureMessage() {
		removeListeners();
		return "An Eclipse build of type " + buildType.toString() + " did not complete";
	}

}
