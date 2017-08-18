/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.debug;

import java.io.IOException;

import org.eclipse.debug.core.model.IProcess;

/**
 * @since 10.0
 */
public interface IProcessTree {

	/**
	 * Attempt to find the PID associated with an Eclipse process object.
	 * @param launch
	 * @return The PID, or 0 if it cannot be determined
	 */
	public int getPid(IProcess launch);

	/**
	 * Get the resident set size  in bytes (real memory the process is using).
	 * @param pid
	 * @return
	 */
	public long getRSS(int pid);

	/**
	 * Gets the number of processes between the specified process and its children.
	 * @param pid
	 * @return
	 */
	public int getProcessCount(int pid);

	/**
	 * Gets the number of threads between the specified process and its children.
	 * @param pid
	 * @return
	 */
	public int getThreadCount(int pid);

	/**
	 * Gets the number of open files between the specified process and its children.
	 * @param pid
	 * @return
	 */
	public int getFileCount(int pid);

	/**
	 * Kills the specified process and all children.
	 * @param pid
	 * @throws IOException
	 */
	public void killAll(int pid) throws IOException;

}
