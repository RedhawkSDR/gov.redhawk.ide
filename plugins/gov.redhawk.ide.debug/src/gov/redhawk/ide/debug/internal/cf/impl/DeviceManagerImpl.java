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
package gov.redhawk.ide.debug.internal.cf.impl;

import gov.redhawk.ide.debug.LocalScaDeviceManager;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.sca.util.PluginUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.omg.CORBA.SystemException;

import CF.DataType;
import CF.Device;
import CF.DeviceManagerOperations;
import CF.FileSystem;
import CF.InvalidObjectReference;
import CF.LifeCycle;
import CF.LifeCycleHelper;
import CF.PropertiesHolder;
import CF.UnknownProperties;
import CF.DeviceManagerPackage.ServiceType;
import CF.LifeCyclePackage.InitializeError;
import CF.LifeCyclePackage.ReleaseError;
import CF.PortSupplierPackage.UnknownPort;
import CF.PropertySetPackage.InvalidConfiguration;
import CF.PropertySetPackage.PartialConfiguration;

/**
 * 
 */
public class DeviceManagerImpl extends EObjectImpl implements DeviceManagerOperations {

	private final String profile;
	private final String identifier;
	private final String name;
	private final FileSystem fileSystem;
	private List<Device> devices = Collections.synchronizedList(new ArrayList<Device>());
	private List<ServiceType> services = Collections.synchronizedList(new ArrayList<ServiceType>());

	private final Job refreshJob;

	public DeviceManagerImpl(final String profile, final String identifier, final String name, final LocalScaDeviceManager deviceManager,
	        final FileSystem fileSystem) {
		super();
		this.profile = profile;
		this.identifier = identifier;
		this.name = name;
		this.fileSystem = fileSystem;
		refreshJob = new Job("Refreshing Device Manager") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					deviceManager.refresh(monitor, RefreshDepth.FULL);
				} catch (InterruptedException e) {
					return Status.CANCEL_STATUS;
				}
				return Status.OK_STATUS;
			}

		};
	}

	/**
	 * {@inheritDoc}
	 */
	public void configure(final DataType[] configProperties) throws InvalidConfiguration, PartialConfiguration {
		throw new InvalidConfiguration();
	}

	/**
	 * {@inheritDoc}
	 */
	public void query(final PropertiesHolder configProperties) throws UnknownProperties {
		// Do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	public org.omg.CORBA.Object getPort(final String name) throws UnknownPort {
		throw new UnknownPort("No ports");
	}

	/**
	 * {@inheritDoc}
	 */
	public String deviceConfigurationProfile() {
		return this.profile;
	}

	/**
	 * {@inheritDoc}
	 */
	public FileSystem fileSys() {
		return this.fileSystem;
	}

	/**
	 * {@inheritDoc}
	 */
	public String identifier() {
		return this.identifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public String label() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 */
	public Device[] registeredDevices() {
		synchronized (devices) {
			boolean changed = false;
			for (Iterator<Device> iterator = devices.iterator(); iterator.hasNext();) {
				Device d = iterator.next();
				try {
					if (d._non_existent()) {
						iterator.remove();
						changed = true;
					}
				} catch (SystemException e) {
					iterator.remove();
					changed = true;
				}
			}
			if (changed) {
				refreshJob.schedule();
			}
			return devices.toArray(new Device[devices.size()]);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public ServiceType[] registeredServices() {
		synchronized (services) {
			boolean changed = false;
			for (Iterator<ServiceType> iterator = services.iterator(); iterator.hasNext();) {
				ServiceType type = iterator.next();
				try {
					if (type.serviceObject._non_existent()) {
						iterator.remove();
						changed = true;
					}
				} catch (SystemException e) {
					iterator.remove();
					changed = true;
				}
			}
			if (changed) {
				refreshJob.schedule();
			}
			return services.toArray(new ServiceType[services.size()]);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void registerDevice(final Device registeringDevice) throws InvalidObjectReference {
		devices.add(registeringDevice);
		refreshJob.schedule();
		try {
			registeringDevice.initialize();
		} catch (final InitializeError e) {
			throw new InvalidObjectReference("Initialize error", Arrays.toString(e.errorMessages));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void unregisterDevice(final Device registeredDevice) throws InvalidObjectReference {
		if (registeredDevice == null) {
			throw new InvalidObjectReference("Null reference", "Null reference");
		}
		final String deviceId = registeredDevice.identifier();
		if (deviceId == null) {
			return;
		}
		synchronized (devices) {
			for (Iterator<Device> iterator = devices.iterator(); iterator.hasNext();) {
				Device d = iterator.next();
				if (d == registeredDevice || PluginUtil.equals(d.identifier(), deviceId)) {
					iterator.remove();
					return;
				}
			}
		}
		refreshJob.schedule();
		try {
			registeredDevice.releaseObject();
		} catch (ReleaseError e) {
			throw new InvalidObjectReference("Release error", Arrays.toString(e.errorMessages));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void shutdown() {
		for (ServiceType type : this.services.toArray(new ServiceType[services.size()])) {
			try {
				unregisterService(type.serviceObject, type.serviceName);
			} catch (InvalidObjectReference e) {
				ScaDebugPlugin.logError("Failed to release service " + type.serviceName, e);
			}
		}
		for (Device d : this.devices.toArray(new Device[devices.size()])) {
			try {
				unregisterDevice(d);
			} catch (InvalidObjectReference e) {
				ScaDebugPlugin.logError("Failed to release device", e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void registerService(final org.omg.CORBA.Object registeringService, final String name) throws InvalidObjectReference {
		ServiceType type = new ServiceType(registeringService, name);
		services.add(type);
		refreshJob.schedule();
		if (registeringService._is_a(LifeCycleHelper.id())) {
			LifeCycle service = LifeCycleHelper.narrow(registeringService);
			try {
				service.initialize();
			} catch (InitializeError e) {
				throw new InvalidObjectReference("Initialize error", Arrays.toString(e.errorMessages));
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void unregisterService(final org.omg.CORBA.Object unregisteringService, final String name) throws InvalidObjectReference {
		if (name == null) {
			return;
		}
		synchronized (services) {
			for (Iterator<ServiceType> iterator = services.iterator(); iterator.hasNext();) {
				ServiceType type = iterator.next();
				if (PluginUtil.equals(type.serviceName, name)) {
					iterator.remove();
					return;
				}
			}
		}

		refreshJob.schedule();

		if (unregisteringService._is_a(LifeCycleHelper.id())) {
			LifeCycle service = LifeCycleHelper.narrow(unregisteringService);
			try {
				service.releaseObject();
			} catch (ReleaseError e) {
				throw new InvalidObjectReference("Release error", Arrays.toString(e.errorMessages));
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String getComponentImplementationId(final String componentInstantiationId) {
		// TODO
		return "";
	}

}
