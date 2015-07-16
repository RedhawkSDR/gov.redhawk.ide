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

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;

import gov.redhawk.ide.debug.EarlyTerminationException;
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
		for (IProcess process : this.getProcesses()) {
			setProcessLabel(process);
		}
	}

	@Override
	protected void fireTerminate() {
		// Detect exit status of each process and write it to the appropriate console
		for (IProcess process : getProcesses()) {
			IOConsole console = getConsole(process);
			if (console == null) {
				continue;
			}

			int exitValue;
			try {
				exitValue = process.getExitValue();
			} catch (DebugException e) {
				continue;
			}
			if (exitValue == 0) {
				writeToConsole(console, "The IDE detected that the process exited normally");
			} else {
				writeToConsole(console, "The IDE detected that the process " + EarlyTerminationException.getExitCodeMessage(exitValue));
			}
		}

		super.fireTerminate();
	}

	/**
	 * Find the IO console for a given process, or null if not found.
	 * @param process The process to search by
	 * @return
	 */
	private IOConsole getConsole(IProcess process) {
		IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
		IConsole[] consoles = manager.getConsoles();
		for (IConsole console : consoles) {
			// Must be an IOConsole
			if (!(console instanceof IOConsole)) {
				continue;
			}
			IOConsole ioConsole = (IOConsole) console;

			// Check that the process matches
			IProcess consoleProcess = (IProcess) ioConsole.getAttribute(IDebugUIConstants.ATTR_CONSOLE_PROCESS);
			if (process == consoleProcess) {
				return ioConsole;
			}
		}
		return null;
	}

	/**
	 * Write a message to an IO console.
	 * @param console
	 * @param msg
	 */
	private void writeToConsole(IOConsole console, String msg) {
		try (IOConsoleOutputStream out = console.newOutputStream();) {
			out.write(msg);
		} catch (IOException ex) {
			ScaDebugPlugin.logError("Error while writing process exit status to console", ex);
		}
	}
}
