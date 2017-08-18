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

import org.eclipse.cdt.dsf.gdb.launching.GdbLaunch;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.ISourceLocator;

import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.variables.LaunchVariables;

public class ComponentDebugLaunch extends GdbLaunch  implements IComponentLaunch {

	private IProcess parent;
	private ILaunch parentComponentHostLaunch;

	public ComponentDebugLaunch(ILaunchConfiguration launchConfiguration, String mode, ISourceLocator locator) {
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
		// Contained components defer to ComponentHost state
		if (parentComponentHostLaunch != null) {
			return parentComponentHostLaunch.canTerminate();
		}

		return super.canTerminate();
	}

	@Override
	public boolean isTerminated() {
		// Contained components defer to ComponentHost state
		if (parentComponentHostLaunch != null) {
			return parentComponentHostLaunch.isTerminated();
		}

		return super.isTerminated();
	}

	@Override
	public void terminate() throws DebugException {
		// If a contained component is terminated, terminate the ComponentHost instead.
		if (parentComponentHostLaunch != null) {
			parentComponentHostLaunch.terminate();
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
			return " < DEBUGGING > ";
		}
		return " [" + parent.getLabel() + "] ";
	}

	@Override
	public void setParent(IProcess parentProcess) {
		parent = parentProcess;
		for (IProcess process : this.getProcesses()) {
			setProcessLabel(process);
		}
	}

	@Override
	public ILaunch getComponentHost() {
		return this.parentComponentHostLaunch;
	}

	@Override
	public void setComponentHost(ILaunch parentLaunch) {
		this.parentComponentHostLaunch = parentLaunch;
		((IComponentHostLaunch) this.parentComponentHostLaunch).getChildLaunchList().add(this);
	}

	@Override
	public void terminateContainedComponent() {
		fireTerminate();
	}
}
