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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessTree {

	private Map<String, List<String>> processTree = new HashMap<String, List<String>>();
	private static final Pattern PATTERN = Pattern.compile("\\s*(\\d+)\\s+(\\d+)");

	public ProcessTree() throws IOException {
		createProcessList();
	}

	private void createProcessList() throws IOException {
		ProcessBuilder builder = new ProcessBuilder("/bin/ps", "-eo", "pid,ppid");
		builder.redirectErrorStream(true);
		Process process = builder.start();
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String firstLine = reader.readLine();
		if (!"  PID  PPID".equals(firstLine)) {
			throw new IOException("Unexpected result");
		}
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			Matcher matcher = PATTERN.matcher(line);
			if (matcher.matches()) {
				String ppid = matcher.group(2);
				String pid = matcher.group(1);
				List<String> list = processTree.get(ppid);
				if (list == null) {
					list = new ArrayList<String>();
					processTree.put(ppid, list);
				}
				list.add(pid);
			}
		}
	}

	public List<String> getChildren(String pid) {
		List<String> children = processTree.get(pid);
		if (children == null || children.isEmpty()) {
			return Collections.emptyList();
		} else {
			List<String> retVal = new ArrayList<String>();
			for (String childPid : children) {
				retVal.addAll(getChildren(childPid));
			}
			retVal.addAll(children);
			return retVal;
		}

	}

	public void killAll(String ppid) throws IOException {
		List<String> children = getChildren(ppid);
		Collections.reverse(children);
		root : for (String child : children) {
			if (!isRunning(child)) {
				continue;
			}
			ProcessBuilder builder = new ProcessBuilder("kill", "SIGTERM", child);
			builder.start();
			
			
			for (int attempts = 0; attempts < 5; attempts++) {
				if (isRunning(child)) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// PASS
					}
				} else {
					continue root;
				}
			}
			
			// Send SigKill to Destroy the process
			builder = new ProcessBuilder("kill", "SIGKILL", child);
			builder.start();
		}
	}

	private boolean isRunning(String child) {
		File childDesc = new File("/proc/" + child);
		return childDesc.exists();
	}

}
