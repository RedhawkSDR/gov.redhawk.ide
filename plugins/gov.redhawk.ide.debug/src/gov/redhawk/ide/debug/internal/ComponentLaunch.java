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

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.ISourceLocator;

import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.variables.LaunchVariables;

/**
 * This class allows us to customize some aspects of our launches. Such as:
 * <ul>
 * <li>IDE-1054 Setting a custom process label (the name shown in the console view)</li>
 * <li>Displaying information about the process exit code in the console after termination</li>
 * </ul>
 */
public class ComponentLaunch extends Launch {

	private IProcess parent;
	private ILaunch parentLaunch;

	public ComponentLaunch(ILaunchConfiguration launchConfiguration, String mode, ISourceLocator locator) {
		super(launchConfiguration, mode, locator);
		setAttribute(ScaDebugPlugin.LAUNCH_ATTRIBUTE_REDHAWK_EXIT_STATUS, "true");
	}

	@Override
	public void addProcess(IProcess process) {
		super.addProcess(process);
		setProcessLabel(process);
	}

	@Override
	public boolean canTerminate() {
		// Always allow components contained within a ComponentHost to be terminated
		if (parentLaunch != null) {
			return true;
		}

		return super.canTerminate();
	}

	@Override
	public void terminate() throws DebugException {
		// If a contained component is terminated, terminate the ComponentHost as well
		if (parentLaunch != null) {
			// Avoid an infinite loop
			if (((ComponentHostLaunch) parentLaunch).isTerminating()) {
				fireTerminate();
			} else {
				parentLaunch.terminate();
			}
			return;
		}
		super.terminate();
	}

	private void setProcessLabel(IProcess process) {
		// Ideally, the console should be labeled with the usage name of the component/device
		String label = getAttribute(LaunchVariables.DEVICE_LABEL);
		if (label == null) {
			label = getAttribute(LaunchVariables.NAME_BINDING);
		}
		if (label == null) {
			label = getLaunchConfiguration().getName();
		}
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
		for (IProcess process : this.getProcesses()) {
			setProcessLabel(process);
		}
	}

	/**
	 * IDE-1665 - associates contained components with their component host. </br>
	 * Setting this implicitly marks this component as a shared-address component.
	 * @param parentLaunch
	 */
	public void setParent(ILaunch parentLaunch) {
		this.parentLaunch = parentLaunch;
		((ComponentHostLaunch) this.parentLaunch).getChildLaunchList().add(this);
	}
}
