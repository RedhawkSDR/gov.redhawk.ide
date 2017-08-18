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
package gov.redhawk.ide.debug.internal.cf.extended.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.omg.CORBA.SystemException;

import CF.Application;
import CF.DataType;
import CF.ApplicationPackage.InvalidMetric;
import gov.redhawk.ide.debug.IProcessTree;
import gov.redhawk.ide.debug.LocalScaComponent;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.ProcessTreeFactory;
import gov.redhawk.ide.debug.internal.IComponentLaunch;
import gov.redhawk.ide.debug.variables.LaunchVariables;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaWaveform;
import mil.jpeojtrs.sca.util.metrics.Metric;

/**
 * Generates application metrics. See {@link Application#metrics(String[], String[])}.
 */
class MetricsGenerator {

	private static final Set<String> VALID_ATTTRIBUTES;

	static {
		VALID_ATTTRIBUTES = new LinkedHashSet<>();
		Collections.addAll(VALID_ATTTRIBUTES, Metric.ATTR_CORES, Metric.ATTR_MEMORY, Metric.ATTR_VALID, Metric.ATTR_SHARED, Metric.ATTR_PROCESSES,
			Metric.ATTR_THREADS, Metric.ATTR_FILES, Metric.ATTR_COMPONENT_HOST);
	}

	private MetricsGenerator() {
	}

	public static DataType[] metrics(LocalScaWaveform waveform, String[] components, String[] attributes) throws InvalidMetric {
		// Create a map of component name -> component within the waveform
		List<ScaComponent> scaComponents = waveform.getComponentsCopy();
		Map<String, ScaComponent> componentMap = new HashMap<>();
		for (ScaComponent scaComponent : scaComponents) {
			componentMap.put(scaComponent.getName(), scaComponent);
		}

		// Validate arguments
		validate(componentMap, components, attributes);

		// Apply defaults for anything that wasn't specified
		if (components.length == 0) {
			components = new String[scaComponents.size() + 1];
			components[0] = Metric.APP_UTIL;
			for (int i = 0; i < scaComponents.size(); i++) {
				components[i + 1] = scaComponents.get(i).getName();
			}
		}
		if (attributes.length == 0) {
			attributes = VALID_ATTTRIBUTES.toArray(new String[VALID_ATTTRIBUTES.size()]);
		}

		// Generate metrics
		Metric[] metrics = new Metric[components.length];
		DataType[] dtMetrics = new DataType[components.length];
		IProcessTree processTree = null;
		for (int i = 0; i < components.length; i++) {
			// Special case - metrics for entire waveform
			if (Metric.APP_UTIL.equals(components[i])) {
				Metric metric = new Metric();
				metric.setId(components[i]);
				metrics[i] = metric;
			} else {
				ScaComponent scaComponent = componentMap.get(components[i]);
				if (scaComponent instanceof LocalScaComponent) {
					if (processTree == null) {
						processTree = ProcessTreeFactory.createProcessTree();
					}
					metrics[i] = metricsLocal(waveform, (LocalScaComponent) scaComponent, processTree);
					dtMetrics[i] = metrics[i].toDataType(attributes);
				} else {
					metrics[i] = metricsRemote(waveform, components[i]);
					dtMetrics[i] = metrics[i].toDataType(attributes);
				}
			}
		}

		// Provide application total metrics if requested
		totalMetrics(metrics, dtMetrics, attributes);

		return dtMetrics;
	}

	private static void validate(Map<String, ScaComponent> componentMap, String[] components, String[] attributes) throws InvalidMetric {
		List<String> badComponents = new ArrayList<>();
		List<String> badAttributes = new ArrayList<>();
		for (String component : components) {
			if (!componentMap.containsKey(component) && !Metric.APP_UTIL.equals(component)) {
				badComponents.add(component);
			}
		}
		for (String attribute : attributes) {
			if (!VALID_ATTTRIBUTES.contains(attribute)) {
				badAttributes.add(attribute);
			}
		}
		if (badComponents.size() > 0 || badAttributes.size() > 0) {
			throw new InvalidMetric(badComponents.toArray(new String[0]), badAttributes.toArray(new String[0]));
		}
	}

	/**
	 * Retrieves the metrics for a local component
	 * @param waveform
	 * @param component
	 * @return
	 */
	private static Metric metricsLocal(LocalScaWaveform waveform, LocalScaComponent component, IProcessTree processTree) {
		Metric metric = new Metric();

		metric.setId(component.getName());

		// Component host & shared
		String name = "";
		ILaunch launch = component.getLaunch();
		boolean shared = false;
		if (launch instanceof IComponentLaunch) {
			ILaunch parentComponentHostLaunch = ((IComponentLaunch) launch).getComponentHost();
			if (parentComponentHostLaunch != null) {
				launch = parentComponentHostLaunch;
				shared = true;
			}
		}
		name = launch.getAttribute(LaunchVariables.NAME_BINDING) + ":" + waveform.getName();
		metric.setComponentHost(name);
		metric.setShared(shared);

		float getVmRSS = 0;
		long processes = 0, threads = 0, files = 0;
		for (IProcess process : launch.getProcesses()) {
			int pid = processTree.getPid(process);
			if (pid != 0) {
				getVmRSS += (processTree.getRSS(pid) / 1024.0 / 1024.0);
				processes += processTree.getProcessCount(pid);
				threads += processTree.getThreadCount(pid);
				files += processTree.getFileCount(pid);
			}
		}
		if (threads > 0) {
			metric.setMemory(getVmRSS);
			metric.setProcesses(processes);
			metric.setThreads(threads);
			metric.setFiles(files);
		}

		return metric;
	}

	/**
	 * Retrieves the metrics for a domain component
	 * @param domainWaveform
	 * @param component
	 * @return
	 */
	private static Metric metricsRemote(ScaWaveform domainWaveform, String component) {
		try {
			DataType[] metrics = domainWaveform.metrics(new String[] { component }, new String[0]);
			return new Metric(metrics[0]);
		} catch (SystemException | InvalidMetric | ArrayIndexOutOfBoundsException e) {
			// Return an empty metric for the component
			Metric metric = new Metric();
			metric.setId(component);
			return metric;
		}
	}

	/**
	 * This finds the application total metric and makes it the sum of the other metrics. Also marshals that metric.
	 * @param metrics
	 * @param dtMetrics
	 * @param attributes
	 */
	private static void totalMetrics(Metric[] metrics, DataType[] dtMetrics, String[] attributes) {
		// Compute totals while also searching for the total metric.
		float cores = 0, memory = 0;
		boolean valid = true;
		long processes = 0, threads = 0, files = 0;

		int totalIndex = -1;
		Set<String> componentHostsSeen = new HashSet<>();
		for (int i = 0; i < metrics.length; i++) {
			if (Metric.APP_UTIL.equals(metrics[i].getId())) {
				totalIndex = i;
			} else if (componentHostsSeen.add(metrics[i].getComponentHost())) {
				cores += metrics[i].getCores();
				memory += metrics[i].getMemory();
				valid &= metrics[i].isValid();
				processes += metrics[i].getProcesses();
				threads += metrics[i].getThreads();
				files += metrics[i].getFiles();
			}
		}

		// If total wasn't requested, we're done
		if (totalIndex == -1) {
			return;
		}

		metrics[totalIndex].setCores(cores);
		metrics[totalIndex].setMemory(memory);
		metrics[totalIndex].setValid(valid);
		metrics[totalIndex].setProcesses(processes);
		metrics[totalIndex].setThreads(threads);
		metrics[totalIndex].setFiles(files);
		dtMetrics[totalIndex] = metrics[totalIndex].toDataType(attributes);
	}
}
