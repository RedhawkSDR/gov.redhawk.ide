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

import gov.redhawk.ide.debug.variables.LaunchVariables;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.ISourceLocator;

/**
 * IDE-1054 Added special class to override process label in console of waveform components
 */
class ComponentLaunch extends Launch {

	private IProcess parent;
	private String label;
	
	public ComponentLaunch(ILaunchConfiguration launchConfiguration, String mode, ISourceLocator locator) {
		super(launchConfiguration, mode, locator);
		try {
			// Ideally, the console should be labeled with the usage name of the component
			label = launchConfiguration.getAttribute(LaunchVariables.NAME_BINDING, launchConfiguration.getName());
		} catch (CoreException e) {
			label = launchConfiguration.getName();
		}
	}
	
	@Override
	public void addProcess(IProcess process) {
		super.addProcess(process);
		setProcessLabel(process);
	}
	
	private void setProcessLabel(IProcess process) {
		process.setAttribute(IProcess.ATTR_PROCESS_LABEL, label + getParentName() + process.getLabel());
	}
	
	private String getParentName() {
		if (parent == null) {
			return " [Sandbox Component] ";
		}
		return " [" + parent.getLabel() + "] ";
	}
	
	public void setParent(IProcess parentProcess) {
		parent = parentProcess;
		for (IProcess process: this.getProcesses()) {
			setProcessLabel(process);
		}
	}
}
