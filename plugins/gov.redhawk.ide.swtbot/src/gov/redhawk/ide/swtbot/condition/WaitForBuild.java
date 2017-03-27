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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.ui.statushandlers.StatusManager;

import gov.redhawk.ide.swtbot.SwtBotActivator;

/**
 * Waits for any currently running Eclipse auto-builds to complete
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

	private Set<BuildType> buildTypes;

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
	 * @deprecated Use {@link #WaitForBuild(BuildType)}.
	 */
	@Deprecated
	public WaitForBuild() {
		this(BuildType.AUTO);
	}

	public WaitForBuild(BuildType... buildTypes) {
		this.buildTypes = new HashSet<>();
		Collections.addAll(this.buildTypes, buildTypes);
	}

	@Override
	public boolean test() throws Exception {
		if (startingWaitTime == 0) {
			startingWaitTime = System.currentTimeMillis();
			attachListeners();
		}

		if (endingWaitTime == 0) {
			return false;
		}

		String msg = String.format("Build completed in %f seconds", (endingWaitTime - startingWaitTime) / 1000.0);
		StatusManager.getManager().handle(new Status(IStatus.INFO, SwtBotActivator.PLUGIN_ID, msg), StatusManager.LOG);
		return true;
	}

	private void attachListeners() {
		resourceListener = new IResourceChangeListener() {

			@Override
			public void resourceChanged(IResourceChangeEvent event) {
				if (event.getBuildKind() != IncrementalProjectBuilder.AUTO_BUILD) {
					return;
				}

				if (endingWaitTime == 0 && buildTypes.contains(BuildType.AUTO)) {
					endingWaitTime = System.currentTimeMillis();
				}
				removeListeners();
			}
		};
		jobListener = new JobChangeAdapter() {

			@Override
			public void done(IJobChangeEvent event) {
				if (!event.getJob().getClass().getName().startsWith("gov.redhawk.ide.codegen.ui.GenerateCode$")
					|| !event.getJob().getName().startsWith("Building Project")) {
					return;
				}

				if (endingWaitTime == 0 && buildTypes.contains(BuildType.CODEGEN)) {
					endingWaitTime = System.currentTimeMillis();
				}
				removeListeners();
			}
		};

		ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceListener, IResourceChangeEvent.POST_BUILD);
		Job.getJobManager().addJobChangeListener(jobListener);
	}

	private void removeListeners() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceListener);
		Job.getJobManager().removeJobChangeListener(jobListener);
	}

	@Override
	public String getFailureMessage() {
		return "An Eclipse build of type " + buildTypes.toString() + " did not complete";
	}

}
