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
import gov.redhawk.ide.debug.ScaDebugFactory;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.ScaService;
import gov.redhawk.model.sca.commands.ScaModelCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import CF.DataType;
import CF.Device;
import CF.DeviceManagerOperations;
import CF.ExecutableDeviceHelper;
import CF.FileSystem;
import CF.InvalidObjectReference;
import CF.LoadableDeviceHelper;
import CF.PropertiesHolder;
import CF.UnknownProperties;
import CF.DeviceManagerPackage.ServiceType;
import CF.LifeCyclePackage.InitializeError;
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
	private final LocalScaDeviceManager deviceManager;
	private final FileSystem fileSystem;

	public DeviceManagerImpl(final String profile, final String identifier, final String name, final LocalScaDeviceManager deviceManager,
	        final FileSystem fileSystem) {
		super();
		this.profile = profile;
		this.identifier = identifier;
		this.name = name;
		this.deviceManager = deviceManager;
		this.fileSystem = fileSystem;
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
		final List<Device> retVal = new ArrayList<Device>();
		for (final ScaDevice< ? > d : this.deviceManager.getAllDevices()) {
			retVal.add(d.getObj());
		}
		return retVal.toArray(new Device[retVal.size()]);
	}

	/**
	 * {@inheritDoc}
	 */
	public ServiceType[] registeredServices() {
		final List<ServiceType> retVal = new ArrayList<ServiceType>();
		for (final ScaService s : this.deviceManager.getServices()) {
			retVal.add(new ServiceType(s.getObj(), s.getName()));
		}
		return retVal.toArray(new ServiceType[retVal.size()]);
	}

	/**
	 * {@inheritDoc}
	 */
	public void registerDevice(final Device registeringDevice) throws InvalidObjectReference {
		final ScaDevice< ? > device;
		if (registeringDevice._is_a(ExecutableDeviceHelper.id())) {
			device = ScaDebugFactory.eINSTANCE.createLocalScaExecutableDevice();
		} else if (registeringDevice._is_a(LoadableDeviceHelper.id())) {
			device = ScaDebugFactory.eINSTANCE.createLocalScaLoadableDevice();
		} else {
			device = ScaDebugFactory.eINSTANCE.createLocalScaDevice();
		}
		device.setDataProvidersEnabled(false);
		device.setCorbaObj(registeringDevice);
		ScaModelCommand.execute(this.deviceManager, new ScaModelCommand() {

			public void execute() {
				DeviceManagerImpl.this.deviceManager.getRootDevices().add(device);
			}
		});
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
		final String deviceId = registeredDevice.identifier();
		if (deviceId == null) {
			return;
		}
		ScaModelCommand.execute(this.deviceManager, new ScaModelCommand() {

			public void execute() {
				for (final ScaDevice< ? > device : DeviceManagerImpl.this.deviceManager.getAllDevices()) {
					if (deviceId.equals(device.getIdentifier())) {
						EcoreUtil.delete(device);
						break;
					}
				}
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	public void shutdown() {
		// PASS do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	public void registerService(final org.omg.CORBA.Object registeringService, final String name) throws InvalidObjectReference {
		final ScaService service = ScaDebugFactory.eINSTANCE.createLocalScaService();
		service.setName(name);
		service.setDataProvidersEnabled(false);
		service.setCorbaObj(registeringService);
		ScaModelCommand.execute(this.deviceManager, new ScaModelCommand() {

			public void execute() {
				DeviceManagerImpl.this.deviceManager.getServices().add(service);
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	public void unregisterService(final org.omg.CORBA.Object unregisteringService, final String name) throws InvalidObjectReference {
		if (name == null) {
			return;
		}
		ScaModelCommand.execute(this.deviceManager, new ScaModelCommand() {

			public void execute() {
				for (final ScaService service : DeviceManagerImpl.this.deviceManager.getServices()) {
					if (name.equals(service.getName())) {
						EcoreUtil.delete(service);
						break;
					}
				}
			}
		});

	}

	/**
	 * {@inheritDoc}
	 */
	public String getComponentImplementationId(final String componentInstantiationId) {
		// TODO
		return "";
	}

}
