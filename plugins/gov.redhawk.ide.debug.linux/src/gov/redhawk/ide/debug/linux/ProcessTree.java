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
package gov.redhawk.ide.debug.linux;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.RuntimeProcess;

import gov.redhawk.ide.debug.IProcessTree;

public class ProcessTree implements IProcessTree {

	private static final String[] SIGNALS = { "SIGTERM", "SIGQUIT", "SIGINT", "SIGKILL" };
	private static final String PLUGIN_ID = "gov.redhawk.ide.debug.linux";

	private Map<Integer, List<Integer>> parentPidToChildPids = new HashMap<>();
	private Map<Integer, Long> pidToRSS = new HashMap<>();
	private Map<Integer, Integer> pidToThreadCount = new HashMap<>();

	public ProcessTree() throws IOException {
		createProcessList();
	}

	private void createProcessList() throws IOException {
		// Iterate paths named /proc/<pid>
		try (DirectoryStream<Path> dirstream = Files.newDirectoryStream(Paths.get("/proc"), "[0-9]*")) {
			for (Path path : dirstream) {
				// Grab pid from filename
				int pid;
				try {
					pid = Integer.parseInt(path.getName(path.getNameCount() - 1).toString());
				} catch (NumberFormatException e) {
					continue;
				}

				// Read /proc/<pid>/stat
				try {
					List<String> lines = Files.readAllLines(path.resolve("stat"), Charset.forName("UTF-8"));
					if (lines.isEmpty()) {
						continue;
					}
					String[] fields = lines.get(0).split(" ");

					int ppid = Integer.parseInt(fields[3]);
					if (!parentPidToChildPids.containsKey(ppid)) {
						parentPidToChildPids.put(ppid, new ArrayList<Integer>());
					}
					parentPidToChildPids.get(ppid).add(pid);

					long rss = Long.parseLong(fields[23]) * 4096;
					pidToRSS.put(pid, rss);

					int threads = Integer.parseInt(fields[19]);
					pidToThreadCount.put(pid, threads);
				} catch (IOException | NumberFormatException e) {
					// Remaining stats will not be collected for this pid
				}
			}
		}
	}

	private Set<Integer> getAllChildren(int pid) {
		Set<Integer> children = new LinkedHashSet<Integer>();
		Queue<Integer> childrenToVisit = new LinkedList<>();

		// Add immediate children
		if (parentPidToChildPids.containsKey(pid)) {
			childrenToVisit.addAll(parentPidToChildPids.get(pid));
		}

		// Visit all descendants
		while (!childrenToVisit.isEmpty()) {
			int childPid = childrenToVisit.poll();
			children.add(childPid);
			if (parentPidToChildPids.containsKey(childPid)) {
				childrenToVisit.addAll(parentPidToChildPids.get(childPid));
			}
		}

		return children;
	}

	/**
	 * @since 2.1
	 */
	public int getPid(IProcess process) {
		try {
			RuntimeProcess eclipseProcess = (RuntimeProcess) process;
			Method m = RuntimeProcess.class.getDeclaredMethod("getSystemProcess");
			m.setAccessible(true);
			Process javaProcess = (Process) m.invoke(eclipseProcess);

			Class< ? > unixProcessClass = Class.forName("java.lang.UNIXProcess");
			if (unixProcessClass.isInstance(javaProcess)) {
				Field pidField = javaProcess.getClass().getDeclaredField("pid");
				pidField.setAccessible(true);
				int pid = pidField.getInt(javaProcess);
				return pid;
			}

			return 0;
		} catch (ClassCastException | ReflectiveOperationException | SecurityException e) {
			IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, "Unable to access PID", e);
			Platform.getLog(Platform.getBundle(PLUGIN_ID)).log(status);
			return 0;
		}
	}

	/**
	 * @since 2.1
	 */
	public long getRSS(int pid) {
		if (!pidToRSS.containsKey(pid)) {
			return 0;
		}
		long total = pidToRSS.get(pid);

		for (int child : getAllChildren(pid)) {
			if (pidToRSS.containsKey(child)) {
				total += pidToRSS.get(child);
			}
		}
		return total;
	}

	/**
	 * @since 2.1
	 */
	public int getProcessCount(int pid) {
		return 1 + getAllChildren(pid).size();
	}

	/**
	 * @since 2.1
	 */
	public int getThreadCount(int pid) {
		if (!pidToThreadCount.containsKey(pid)) {
			return 0;
		}
		int total = pidToThreadCount.get(pid);

		for (int child : getAllChildren(pid)) {
			if (pidToThreadCount.containsKey(child)) {
				total += pidToThreadCount.get(child);
			}
		}
		return total;
	}

	/**
	 * @since 2.1
	 */
	public int getFileCount(int pid) {
		int files = 0;
		try (Stream<Path> paths = Files.walk(Paths.get("/proc", String.valueOf(pid), "fd"), 1)) {
			files = (int) paths.count();
		} catch (IOException e) {
			// Report zero if we can't get the root process's file list
			return 0;
		}

		for (int childPid : getAllChildren(pid)) {
			try (Stream<Path> paths = Files.walk(Paths.get("/proc", String.valueOf(childPid), "fd"), 1)) {
				files += (int) paths.count();
			} catch (IOException e) {
				// Ignore problems retrieving child processes' file lists
			}
		}

		return files;
	}

	/**
	 * @since 2.1
	 */
	public void killAll(int ppid) throws IOException {
		List<Integer> pids = new ArrayList<Integer>();
		pids.add(ppid);
		pids.addAll(getAllChildren(ppid));

		StringBuilder sb = new StringBuilder();
		boolean stillRunning = false;
		for (int pid : pids) {
			if (isRunning(pid)) {
				stillRunning = true;
				sb.append(' ');
				sb.append(pid);
			}
		}
		if (!stillRunning) {
			return;
		}

		for (String signal : SIGNALS) {
			// Send a signal to all processes still running
			String pidString = sb.substring(1);
			ProcessBuilder builder = new ProcessBuilder("kill", "-s", signal, pidString);
			Process p = builder.start();
			try {
				p.waitFor();
				if (p.exitValue() != 0) {
					return;
				}
			} catch (InterruptedException e) {
				// PASS
			}

			// Sleep for a moment
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				// PASS
			}

			// See what's still running
			sb = new StringBuilder();
			stillRunning = false;
			for (int pid : pids) {
				if (isRunning(pid)) {
					stillRunning = true;
					sb.append(' ');
					sb.append(pid);
				}
			}
			if (!stillRunning) {
				return;
			}
		}
	}

	private boolean isRunning(int pid) {
		return Files.exists(Paths.get("/proc", String.valueOf(pid)));
	}

	/**
	 * @since 2.0
	 * @deprecated Use {@link #killAll(int)}
	 */
	@Deprecated
	public void killAll(String ppid) throws IOException {
		killAll(Integer.parseInt(ppid));
	}
}
