/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package gov.redhawk.ide.debug.internal.ui.console;

import java.io.IOException;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;

import gov.redhawk.ide.debug.EarlyTerminationException;
import gov.redhawk.ide.debug.ScaDebugPlugin;

/**
 * Writes the exit status of the processes of any Redhawk ILaunches to their respective consoles.
 */
public class ConsoleExitStatus implements ILaunchesListener2 {

	@Override
	public void launchesRemoved(ILaunch[] launches) {
		// PASS
	}

	@Override
	public void launchesAdded(ILaunch[] launches) {
		// PASS
	}

	@Override
	public void launchesChanged(ILaunch[] launches) {
		// PASS
	}

	@Override
	public void launchesTerminated(ILaunch[] launches) {
		for (ILaunch launch : launches) {
			// Launch must be marked to have its exit status shown
			if (launch.getAttribute(ScaDebugPlugin.LAUNCH_ATTRIBUTE_REDHAWK_EXIT_STATUS) == null) {
				continue;
			}

			// Detect exit status of each process and write it to the appropriate console
			for (IProcess process : launch.getProcesses()) {
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
		}
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
			final String ATTR_CONSOLE_PROCESS = "org.eclipse.debug.ui.ATTR_CONSOLE_PROCESS"; // org.eclipse.debug.ui.IDebugUIConstants.ATTR_CONSOLE_PROCESS
			IProcess consoleProcess = (IProcess) ioConsole.getAttribute(ATTR_CONSOLE_PROCESS);
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
