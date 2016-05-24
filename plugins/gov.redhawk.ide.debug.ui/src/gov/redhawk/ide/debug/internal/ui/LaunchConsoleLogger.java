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
package gov.redhawk.ide.debug.internal.ui;

import java.io.IOException;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.console.ConsoleColorProvider;
import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;

import gov.redhawk.ide.debug.ConsoleColor;
import gov.redhawk.ide.debug.ILaunchLogger;

public class LaunchConsoleLogger implements ILaunchLogger {

	private Color stdErrColor;

	public LaunchConsoleLogger() {
		// Get the color for stderr
		stdErrColor = new ConsoleColorProvider().getColor(IDebugUIConstants.ID_STANDARD_ERROR_STREAM);
	}

	public void writeToConsole(ILaunch launch, String message, ConsoleColor color) throws IllegalStateException {
		// Find console
		IOConsole ioConsole = getIOConsole(launch);
		if (ioConsole == null) {
			throw new IllegalStateException();
		}

		// Change color, if necessary
		final IOConsoleOutputStream out = ioConsole.newOutputStream();
		if (color == ConsoleColor.STDERR) {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					out.setColor(stdErrColor);
				}
			});
		}

		// Write message and clean up
		try {
			if (message.endsWith("\n")) {
				out.write(message);
			} else {
				out.write(message + "\n");
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} finally {
			try {
				out.close();
			} catch (IOException ex) {
				// PASS
			}
		}
	}

	/**
	 * Try to find an {@link IOConsole} for a process associated with the launch
	 * @param launch
	 * @return
	 */
	private IOConsole getIOConsole(ILaunch launch) {
		org.eclipse.ui.console.IConsole[] consoles = ConsolePlugin.getDefault().getConsoleManager().getConsoles();
		IProcess[] processes = launch.getProcesses();
		for (org.eclipse.ui.console.IConsole console : consoles) {
			if (!(console instanceof IConsole && console instanceof IOConsole)) {
				continue;
			}
			IConsole debugIConsole = (IConsole) console;
			IProcess consoleProcess = debugIConsole.getProcess();
			for (IProcess process : processes) {
				if (process.equals(consoleProcess)) {
					return (IOConsole) debugIConsole;
				}
			}
		}
		return null;
	}

}
