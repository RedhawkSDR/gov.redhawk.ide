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

	/**
	 * @since 8.0
	 */
	public static String getExitCodeMessage(int exitCode) {
		String endString;
		if ((exitCode & 128) == 128) {
			int unixCode = exitCode & 127;
			switch (unixCode) {
			case 1:
				endString = "SIGHUP (" + unixCode + ")";
				break;
			case 2:
				endString = "SIGINT (" + unixCode + ")";
				break;
			case 3:
				endString = "SIGQUIT (" + unixCode + ")";
				break;
			case 4:
				endString = "SIGILL (" + unixCode + ")";
				break;
			case 5:
				endString = "SIGTRAP (" + unixCode + ")";
				break;
			case 6:
				endString = "SIGABRT (" + unixCode + ")";
				break;
			case 8:
				endString = "SIGFPE (" + unixCode + ")";
				break;
			case 9:
				endString = "SIGKILL (" + unixCode + ")";
				break;
			case 11:
				endString = "SIGSEGV (" + unixCode + ")";
				break;
			case 13:
				endString = "SIGPIPE (" + unixCode + ")";
				break;
			case 14:
				endString = "SIGALRM (" + unixCode + ")";
				break;
			case 15:
				endString = "SIGTERM (" + unixCode + ")";
				break;
			default:
				endString = String.valueOf(unixCode);
				break;
			}
		} else {
			endString = String.valueOf(exitCode);
		}
		return "Terminated with exit code " + endString;
	}

}
