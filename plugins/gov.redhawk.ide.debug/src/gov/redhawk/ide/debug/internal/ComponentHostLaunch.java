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
package gov.redhawk.ide.debug.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ISourceLocator;

/**
 * 
 */
public class ComponentHostLaunch extends ComponentLaunch {

	private List<ILaunch> childLaunchList;
	private boolean isTerminating;

	public ComponentHostLaunch(ILaunchConfiguration launchConfiguration, String mode, ISourceLocator locator) {
		super(launchConfiguration, mode, locator);
		childLaunchList = new ArrayList<ILaunch>();
	}

	@Override
	public void terminate() throws DebugException {
		isTerminating = true;
		for (ILaunch launch : childLaunchList) {
			launch.terminate();
		}
		super.terminate();
		isTerminating = false;

	}

	public boolean isTerminating() {
		return isTerminating;
	}

	public List<ILaunch> getChildLaunchList() {
		return childLaunchList;
	}
}
