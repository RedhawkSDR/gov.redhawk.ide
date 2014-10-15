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

import gov.redhawk.ide.debug.ILaunchConfigurationFactory;
import gov.redhawk.ide.debug.LocalScaDeviceManager;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.SpdLauncherUtil;
import gov.redhawk.ide.debug.variables.LaunchVariables;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.ScaService;
import gov.redhawk.model.sca.impl.ScaComponentImpl;
import gov.redhawk.sca.efs.WrappedFileStore;
import gov.redhawk.sca.util.PluginUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mil.jpeojtrs.sca.scd.ComponentType;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.AnyUtils;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.omg.CORBA.SystemException;

import CF.DataType;
import CF.Device;
import CF.DeviceManagerOperations;
import CF.ErrorNumberType;
import CF.FileSystem;
import CF.InvalidObjectReference;
import CF.LifeCycle;
import CF.LifeCycleHelper;
import CF.PropertiesHolder;
import CF.Resource;
import CF.UnknownProperties;
import CF.DeviceManagerPackage.ServiceType;
import CF.ExecutableDevicePackage.ExecuteFail;
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
	private LocalScaDeviceManager devMgr;

	public DeviceManagerImpl(final String profile, final String identifier, final String name, final LocalScaDeviceManager deviceManager,
		final FileSystem fileSystem, LocalScaDeviceManager devMgr) {
		super();
		this.profile = profile;
		this.identifier = identifier;
		this.name = name;
		this.fileSystem = fileSystem;
		this.devMgr = devMgr;
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

	public void setDevMgr(LocalScaDeviceManager devMgr) {
		this.devMgr = devMgr;
	}

	public LocalScaDeviceManager getDevMgr() {
		return devMgr;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configure(final DataType[] configProperties) throws InvalidConfiguration, PartialConfiguration {
		throw new InvalidConfiguration();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void query(final PropertiesHolder configProperties) throws UnknownProperties {
		// Do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.omg.CORBA.Object getPort(final String name) throws UnknownPort {
		throw new UnknownPort("No ports");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String deviceConfigurationProfile() {
		return this.profile;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FileSystem fileSys() {
		return this.fileSystem;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String identifier() {
		return this.identifier;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String label() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CF.DomainManager domMgr() {
		// TODO
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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
	@Override
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
	@Override
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
	@Override
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
	@Override
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
	@Override
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
	@Override
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
	@Override
	public String getComponentImplementationId(final String componentInstantiationId) {
		// TODO
		return "";
	}
	

	private String createExecParamStr(final DataType[] execParams) {
		if (execParams == null || execParams.length == 0) {
			return "";
		}
		final Map<String, Object> map = new HashMap<String, Object>(execParams.length);
		for (final DataType t : execParams) {
			map.put(t.id, AnyUtils.convertAny(t.value));
		}
		return SpdLauncherUtil.createExecParamString(map);
	}

	public Resource launch(final String compId, final DataType[] execParams, @NonNull final String spdURI, final String implId, final String mode)
		throws ExecuteFail {
		Assert.isNotNull(spdURI, "SPD URI must not be null");
		Resource retVal;
		try {
			URI uri = URI.createURI(spdURI);
			if (uri == null) {
				throw new NullPointerException();
			}
			retVal = launch(null, compId, createExecParamStr(execParams), uri, implId, mode);
		} catch (final CoreException e) {
			ScaDebugPlugin.getInstance().getLog().log(new Status(e.getStatus().getSeverity(), ScaDebugPlugin.ID, "Failed to launch device.", e));
			throw new ExecuteFail(ErrorNumberType.CF_EFAULT, e.getStatus().getMessage());
		}
		return retVal;
	}

	@NonNull
	public Resource launch(@Nullable String usageName, @Nullable final String compId, @Nullable final String execParams, @NonNull URI spdURI,
		@Nullable final String implId, @Nullable String mode) throws CoreException {
		Assert.isNotNull(spdURI, "SPD URI must not be null");
		final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		if (!spdURI.isPlatform()) {
			IFileStore store = EFS.getStore(java.net.URI.create(spdURI.toString()));
			IFileStore unwrappedStore = WrappedFileStore.unwrap(store);
			if (unwrappedStore != null) {
				URI tmp = URI.createURI(unwrappedStore.toURI().toString());
				if (tmp == null) {
					throw new NullPointerException();
				}
				spdURI = tmp;
			}
		}

		final SoftPkg spd = SoftPkg.Util.getSoftPkg(resourceSet.getResource(spdURI, true));
		if (mode == null) {
			mode = ILaunchManager.RUN_MODE;
		}

		final ILaunchConfigurationFactory factory = ScaDebugPlugin.getInstance().getLaunchConfigurationFactoryRegistry().getFactory(spd, implId);
		if (factory == null) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to obtain Launch Factory for impl: " + implId, null));
		}
		final ILaunchConfigurationWorkingCopy config = factory.createLaunchConfiguration(spd.getName(), implId, spd);
		ComponentType type = SoftwareComponent.Util.getWellKnownComponentType(spd.getDescriptor().getComponent());

		if (usageName == null && compId != null) {
			usageName = ScaComponentImpl.convertIdentifierToInstantiationID(compId);
		}

		if (type == ComponentType.SERVICE) {
			if (usageName != null) {
				config.setAttribute(LaunchVariables.SERVICE_NAME, usageName);
			}
		}

		if (type == ComponentType.DEVICE) {
			if (usageName != null) {
				config.setAttribute(LaunchVariables.DEVICE_LABEL, usageName);
			}

			if (compId != null) {
				config.setAttribute(LaunchVariables.DEVICE_ID, compId);
			}
		}

		if (execParams != null && execParams.length() > 0) {
			config.setAttribute(LaunchVariables.EXEC_PARAMS, execParams);
		}

		final ILaunch subLaunch = config.launch(mode, new NullProgressMonitor(), false);

		for (int tries = 0; tries < 100; tries++) {
			if (type == ComponentType.DEVICE) {
				for (final ScaDevice< ? > comp : devMgr.fetchDevices(null)) {
					comp.fetchAttributes(null);
					final String id = comp.getIdentifier();
					if (id.equals(compId)) {
						return comp.fetchNarrowedObject(null);
					}
				}
			}
			if (type == ComponentType.SERVICE) {
				for (final ScaService comp : devMgr.fetchServices(null)) {
					comp.fetchAttributes(null);
					final String id = comp.getName();
					if (id.equals(usageName)) {
						return (Resource) comp.fetchNarrowedObject(null);
					}
				}
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// PASS
			}
		}

		subLaunch.terminate();
		throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to find component after launch", null));

	}

}
