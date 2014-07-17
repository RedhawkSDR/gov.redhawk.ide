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
package gov.redhawk.ide.debug;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;

/**
 * @since 6.0
 * 
 */
public class EarlyTerminationException extends Exception {
	public EarlyTerminationException(String msg, ILaunch launch) {
		super(msg + " " + getReturnMsg(launch));
	}

	private static String getReturnMsg(ILaunch launch) {
		IProcess[] processes = launch.getProcesses();
		if (processes != null && processes.length > 0) {

			try {
				int exitCode = processes[0].getExitValue();
				return getExitCodeMessage(exitCode);
			} catch (DebugException e) {
				// Should never happen
				return "Terminated with no exit code.";
			}
		} else {
			return "";
		}
	}

	protected static String getExitCodeMessage(int exitCode) {
		if ((exitCode & 128) == 128) {
			int unixCode = exitCode & 127;
			String signalStr;
			switch (unixCode) {
			case 1:
				signalStr = "SIGHUP";
				break;
			case 2:
				signalStr = "SIGINT";
				break;
			case 3:
				signalStr = "SIGQUIT";
				break;
			case 4:
				signalStr = "SIGILL";
				break;
			case 5:
				signalStr = "SIGTRAP";
				break;
			case 6:
				signalStr = "SIGABRT";
				break;
			case 8:
				signalStr = "SIGFPE";
				break;
			case 9:
				signalStr = "SIGKILL";
				break;
			case 11:
				signalStr = "SIGSEGV";
				break;
			case 13:
				signalStr = "SIGPIPE";
				break;
			case 14:
				signalStr = "SIGALRM";
				break;
			case 15:
				signalStr = "SIGTERM";
				break;
			default:
				signalStr = "";
				break;
			}
			return "Terminated with error code " + signalStr + " (" + unixCode + ")";
		} else {
			return "Terminated with exit code " + exitCode;
		}
	}

}
