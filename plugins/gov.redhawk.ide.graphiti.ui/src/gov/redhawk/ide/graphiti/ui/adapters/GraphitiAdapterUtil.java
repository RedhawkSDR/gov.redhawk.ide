/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.graphiti.ui.adapters;

import gov.redhawk.ide.graphiti.ui.GraphitiUIPlugin;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.model.sca.ScaAbstractComponent;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.ScaDeviceManager;
import gov.redhawk.model.sca.ScaPort;
import gov.redhawk.model.sca.ScaPortContainer;
import gov.redhawk.model.sca.ScaWaveform;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.util.CorbaUtils;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * @since 3.3
 */
public final class GraphitiAdapterUtil {

	private GraphitiAdapterUtil() {

	}

	private interface MonitorableCommand<E> {
		public E call(IProgressMonitor monitor) throws Exception;
	};

	private static void safeFetch(final String name, final String child, final MonitorableCommand< ? > command) {
		Job job = new Job("Fetching " + child + " for " + name) {

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				try {
					CorbaUtils.invoke(new Callable<Object>() {

						@Override
						public Object call() throws Exception {
							return command.call(monitor);
						}
					}, monitor);
				} catch (CoreException e) {
					return new Status(Status.ERROR, GraphitiUIPlugin.PLUGIN_ID, "Failed to fetch " + child + " for " + name, e);
				} catch (InterruptedException e) {
					// PASS
				}
				return Status.OK_STATUS;
			}
		};

		job.setUser(true);
		job.schedule();
		try {
			job.join();
		} catch (InterruptedException e) {
			// PASS
		}
	}

	/**
	 * @deprecated Do not use
	 */
	@Deprecated
	public static ScaComponent safeFetchComponent(final ScaWaveform waveform, final String instantiationId) {
		if (waveform != null) {
			// Ensure that the components are fetched, but use the waveform's getScaComponent to find the requested
			// component. This guarantees that instantiation identifier matching is consistent.
			GraphitiAdapterUtil.safeFetchComponents(waveform);
			return waveform.getScaComponent(instantiationId);
		}
		return null;
	}

	/**
	 * @deprecated Do not use
	 */
	@Deprecated
	public static List<ScaComponent> safeFetchComponents(final ScaWaveform waveform) {
		if (waveform == null) {
			return Collections.emptyList();
		}

		if (!waveform.isSetComponents()) {
			GraphitiAdapterUtil.safeFetch(waveform.getName(), "components", new MonitorableCommand<List<ScaComponent>>() {

				@Override
				public List<ScaComponent> call(IProgressMonitor monitor) throws Exception {
					return waveform.fetchComponents(monitor, RefreshDepth.SELF);
				}
			});
		}
		return waveform.getComponents();
	}

	/**
	 * @deprecated Do not use
	 */
	@Deprecated
	public static ScaDevice< ? > safeFetchDevice(final ScaDeviceManager devMgr, final String deviceId) {
		if (devMgr != null) {
			// Ensure that all the devices are fetched before requesting the device.
			GraphitiAdapterUtil.safeFetchDevices(devMgr);
			return devMgr.getDevice(deviceId);
		}
		return null;
	}

	/**
	 * @deprecated Do not use
	 */
	@Deprecated
	public static List<ScaDevice<?>> safeFetchDevices(final ScaDeviceManager deviceManager) {
		if (deviceManager == null) {
			return Collections.emptyList();
		}

		if (!deviceManager.isSetDevices()) {
			GraphitiAdapterUtil.safeFetch(deviceManager.getLabel(), "devices", new MonitorableCommand<List<ScaDevice<?>>>() {

				@Override
				public List<ScaDevice< ? >> call(IProgressMonitor monitor) throws Exception {
					return deviceManager.fetchDevices(monitor, RefreshDepth.SELF);
				}
			});
		}
		return deviceManager.getAllDevices();
	}

	/**
	 * @deprecated Do not use
	 */
	@Deprecated
	public static List<ScaPort< ? , ? >> safeFetchPorts(final ScaPortContainer container) {
		if (container == null) {
			return Collections.emptyList();
		}

		if (!container.isSetPorts()) {
			String label;
			if (container instanceof ScaComponent) {
				label = ((ScaComponent) container).getName();
			} else if (container instanceof ScaDevice) {
				label = ((ScaDevice< ? >) container).getLabel();
			} else {
				label = "<unknown>";
			}

			GraphitiAdapterUtil.safeFetch(label, "ports", new MonitorableCommand<List<ScaPort< ? , ? >>>() {

				@Override
				public List<ScaPort< ? , ? >> call(IProgressMonitor monitor) throws Exception {
					return container.fetchPorts(monitor);
				}
			});
		}

		return container.getPorts();
	}

	/**
	 * @deprecated Do not use
	 */
	@Deprecated
	public static ScaPort< ? , ? > safeFetchPort(ScaPortContainer container, String name) {
		if (container != null) {
			// Ensure that all the ports are fetched before requesting the port.
			GraphitiAdapterUtil.safeFetchPorts(container);
			return container.getScaPort(name);
		}
		return null;
	}

	/**
	 * @deprecated Use {@link #getScaModelObject(Diagram, ComponentInstantiation)}
	 */
	@Deprecated
	public static ScaAbstractComponent< ? > safeFetchResource(Diagram diagram, ComponentInstantiation instantiation) {
		if (instantiation instanceof SadComponentInstantiation) {
			ScaWaveform waveform = DUtil.getBusinessObject(diagram, ScaWaveform.class);
			if (waveform != null) {
				return GraphitiAdapterUtil.safeFetchComponent(waveform, instantiation.getId());
			}
		} else if (instantiation instanceof DcdComponentInstantiation) {
			ScaDeviceManager devMgr = DUtil.getBusinessObject(diagram, ScaDeviceManager.class);
			if (devMgr != null) {
				return GraphitiAdapterUtil.safeFetchDevice(devMgr, instantiation.getId());
			}
		}
		return null;
	}

	/**
	 * Maps from mil.jpeojtrs.sca model -> SCA model for a given Graphiti diagram.
	 * @param diagram
	 * @param instantiation
	 * @return The model object, or null if it doesn't exist in the model
	 */
	public static ScaAbstractComponent< ? > getScaModelObject(Diagram diagram, ComponentInstantiation instantiation) {
		if (instantiation instanceof SadComponentInstantiation) {
			ScaWaveform waveform = DUtil.getBusinessObject(diagram, ScaWaveform.class);
			if (waveform != null) {
				return waveform.getScaComponent(instantiation.getId());
			}
		} else if (instantiation instanceof DcdComponentInstantiation) {
			ScaDeviceManager devMgr = DUtil.getBusinessObject(diagram, ScaDeviceManager.class);
			if (devMgr != null) {
				return devMgr.getDevice(instantiation.getId());
			}
		}
		return null;
	}
}
