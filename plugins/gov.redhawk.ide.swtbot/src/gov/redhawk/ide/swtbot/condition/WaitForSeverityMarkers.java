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
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;

/**
 * Used to wait for the severity of all markers in the workspace to be at or below a certain severity.
 */
public class WaitForSeverityMarkers implements ICondition {

	private int maxMarkerSeverity;

	/**
	 * @param maxMarkerSeverity The maximum severity (one of -1, {@link IMarker#SEVERITY_INFO},
	 * {@link IMarker#SEVERITY_WARNING}, {@link IMarker#SEVERITY_ERROR})
	 */
	public WaitForSeverityMarkers(int maxMarkerSeverity) {
		this.maxMarkerSeverity = maxMarkerSeverity;
	}

	@Override
	public boolean test() throws Exception {
		return ResourcesPlugin.getWorkspace().getRoot().findMaxProblemSeverity(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE) <= maxMarkerSeverity;
	}

	@Override
	public void init(SWTBot bot) {
	}

	@Override
	public String getFailureMessage() {
		return "Severity of problem markers for the workspace was not less than or equal to " + maxMarkerSeverity;
	}

}
