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
package gov.redhawk.ide.debug.linux;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.RuntimeProcess;
import org.osgi.framework.FrameworkUtil;

/**
 * 
 */
public class PosixRuntimeProcess extends RuntimeProcess {

	/**
	 * @param launch
	 * @param process
	 * @param name
	 * @param attributes
	 * @since 2.0
	 */
	public PosixRuntimeProcess(ILaunch launch, Process process, String name, Map<String, String> attributes) {
		super(launch, process, name, attributes);
	}

	@Override
	public synchronized void terminate() throws DebugException {
		if (isTerminated()) {
			return;
		}
		IOException error = null;
		try {
			int pid = getPid();
			if (pid != -1) {
				ProcessTree tree = new ProcessTree();
				tree.killAll(pid);
			}
		} catch (IOException e) {
			error = e;
		}
		super.terminate();
		if (error != null) {
			throw new DebugException(new Status(Status.ERROR, "gov.redhawk.ide.debug.linux", "Failed to terminate all subprocesses", error));
		}

	}

	@Override
	protected void terminated() {
		// C++ components/devices using shared memory for their BulkIO transport can leave orphaned shared memory if
		// they crash. We remove it if it still exists.
		int pid = getPid();
		try {
			Files.deleteIfExists(Paths.get("/dev/shm/heap-" + pid));
		} catch (IOException e) {
			// PASS
		}

		super.terminated();
	}

	/**
	 * @return The PID for the actual system process, or -1 if none
	 */
	private int getPid() {
		try {
			Process p = getSystemProcess();
			if (p == null) {
				return -1;
			}
			Class< ? > unixProcessClass = Class.forName("java.lang.UNIXProcess");
			if (!unixProcessClass.isInstance(p)) {
				return -1;
			}
			Field pidField = p.getClass().getDeclaredField("pid");
			pidField.setAccessible(true);
			int pid = pidField.getInt(p);
			return pid;
		} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			IStatus status = new Status(IStatus.ERROR, "gov.redhawk.ide.debug.linux", "Unable to get process PID", e);
			Platform.getLog(FrameworkUtil.getBundle(getClass())).log(status);
			return -1;
		}
	}

}
