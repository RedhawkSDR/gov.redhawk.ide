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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.ui.statushandlers.StatusManager;

import gov.redhawk.ide.swtbot.SwtBotActivator;

/**
 * Used to wait for the severity of all markers in the workspace to be at or below a certain severity.
 */
public class WaitForSeverityMarkers implements ICondition {

	/**
	 * The recommended timeout when using this condition
	 */
	public static final long TIMEOUT = 15000;

	private int maxMarkerSeverity;

	private IWorkspaceRoot root;

	private long startingWaitTime = 0;

	/**
	 * @param maxMarkerSeverity The maximum severity (one of -1, {@link IMarker#SEVERITY_INFO},
	 * {@link IMarker#SEVERITY_WARNING}, {@link IMarker#SEVERITY_ERROR})
	 */
	public WaitForSeverityMarkers(int maxMarkerSeverity) {
		this.maxMarkerSeverity = maxMarkerSeverity;
	}

	@Override
	public boolean test() throws Exception {
		if (startingWaitTime == 0) {
			startingWaitTime = System.currentTimeMillis();
		}

		if (root.findMaxProblemSeverity(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE) <= maxMarkerSeverity) {
			String msg = String.format("Marker severity okay after %f seconds", (System.currentTimeMillis() - startingWaitTime) / 1000.0);
			StatusManager.getManager().handle(new Status(IStatus.INFO, SwtBotActivator.PLUGIN_ID, msg), StatusManager.LOG);
			return true;
		}
		return false;
	}

	@Override
	public void init(SWTBot bot) {
		root = ResourcesPlugin.getWorkspace().getRoot();
	}

	@Override
	public String getFailureMessage() {
		return "Severity of problem markers for the workspace was not less than or equal to " + maxMarkerSeverity;
	}

}
