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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TCKind;

import CF.DataType;
import CF.Device;
import CF.DeviceManagerOperations;
import CF.ErrorNumberType;
import CF.FileSystem;
import CF.InvalidIdentifier;
import CF.InvalidObjectReference;
import CF.LifeCycle;
import CF.LifeCycleHelper;
import CF.PropertiesHolder;
import CF.PropertyEmitter;
import CF.PropertySet;
import CF.Resource;
import CF.UnknownProperties;
import CF.DeviceManagerPackage.ServiceType;
import CF.ExecutableDevicePackage.ExecuteFail;
import CF.LifeCyclePackage.InitializeError;
import CF.LifeCyclePackage.ReleaseError;
import CF.PortSetPackage.PortInfoType;
import CF.PortSupplierPackage.UnknownPort;
import CF.PropertyEmitterPackage.AlreadyInitialized;
import CF.PropertySetPackage.InvalidConfiguration;
import CF.PropertySetPackage.PartialConfiguration;
import gov.redhawk.ide.debug.ConsoleColor;
import gov.redhawk.ide.debug.ILaunchConfigurationFactory;
import gov.redhawk.ide.debug.LocalAbstractComponent;
import gov.redhawk.ide.debug.LocalScaDeviceManager;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.internal.LaunchLogger;
import gov.redhawk.ide.debug.variables.LaunchVariables;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.model.sca.ScaAbstractProperty;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.ScaFactory;
import gov.redhawk.model.sca.ScaPropertyContainer;
import gov.redhawk.model.sca.ScaService;
import gov.redhawk.sca.efs.WrappedFileStore;
import gov.redhawk.sca.launch.ScaLaunchConfigurationUtil;
import mil.jpeojtrs.sca.prf.PropertyConfigurationType;
import mil.jpeojtrs.sca.prf.util.PropertiesUtil;
import mil.jpeojtrs.sca.scd.ComponentType;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.CFErrorFormatter;
import mil.jpeojtrs.sca.util.DceUuidUtil;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

/**
 * This is the implementation of the sandbox's device manager. It is represented in the REDHAWK model by a
 * {@link LocalScaDeviceManager}.
 */
public class DeviceManagerImpl extends EObjectImpl implements DeviceManagerOperations {

	private final String profile;
	private final String identifier;
	private final String name;
	private final FileSystem fileSystem;

	/**
	 * A map containing the devices that have registered with the device manager. IORs are mapped to the {@link Device}
	 * object.
	 * <p/>
	 * All access should be synchronized on the object.
	 */
	private Map<String, Device> devices = new HashMap<>();

	/**
	 * This map enables us to take an {@link ILaunch} and find the {@link Device} in the {@link #devices} member.
	 * <p/>
	 * All access should be synchronized on the object.
	 */
	private Map<ILaunch, String> launchToDeviceIor = new HashMap<>();

	/**
	 * A map containing the services that have registered with the device manager. Service name is mapped to the
	 * {@link ServiceType}.
	 * <p/>
	 * All access should be synchronized on the object.
	 */
	private Map<String, ServiceType> services = new HashMap<>();

	/**
	 * This map enables us to take an {@link ILaunch} and find the {@link Serivce} in the {@link #services} member.
	 * <p/>
	 * All access should be synchronized on the object.
	 */
	private Map<ILaunch, String> launchToServiceName = new HashMap<>();

	private final Job refreshChildrenJob;
	private LocalScaDeviceManager devMgrModelObj;

	public DeviceManagerImpl(final String profile, final String identifier, final String name, final LocalScaDeviceManager devMgrModelObj,
		final FileSystem fileSystem) {
		super();
		this.profile = profile;
		this.identifier = identifier;
		this.name = name;
		this.fileSystem = fileSystem;
		this.devMgrModelObj = devMgrModelObj;
		this.refreshChildrenJob = new Job("Refreshing Device Manager children") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				SubMonitor progress = SubMonitor.convert(monitor, 2);
				devMgrModelObj.fetchDevices(progress.newChild(1), RefreshDepth.NONE);
				devMgrModelObj.fetchServices(progress.newChild(1), RefreshDepth.NONE);
				return Status.OK_STATUS;
			}

		};

		DebugPlugin.getDefault().getLaunchManager().addLaunchListener(launchListener);
	}

	@Override
	public void initializeProperties(final DataType[] configProperties) throws AlreadyInitialized, InvalidConfiguration, PartialConfiguration {
		throw new InvalidConfiguration();
	}

	@Override
	public void configure(final DataType[] configProperties) throws InvalidConfiguration, PartialConfiguration {
		throw new InvalidConfiguration();
	}

	@Override
	public void query(final PropertiesHolder configProperties) throws UnknownProperties {
		// Do nothing
	}

	@Override
	public String registerPropertyListener(org.omg.CORBA.Object obj, String[] propIds, float interval) throws UnknownProperties, InvalidObjectReference {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public void unregisterPropertyListener(String id) throws InvalidIdentifier {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public org.omg.CORBA.Object getPort(final String name) throws UnknownPort {
		throw new UnknownPort("No ports");
	}

	public PortInfoType[] getPortSet() {
		return new PortInfoType[0];
	}

	@Override
	public String deviceConfigurationProfile() {
		return this.profile;
	}

	@Override
	public FileSystem fileSys() {
		return this.fileSystem;
	}

	@Override
	public String identifier() {
		return this.identifier;
	}

	@Override
	public String label() {
		return this.name;
	}

	@Override
	public CF.DomainManager domMgr() {
		// TODO
		return null;
	}

	@Override
	public Device[] registeredDevices() {
		synchronized (devices) {
			Device[] deviceArray = devices.values().toArray(new Device[devices.size()]);
			return deviceArray;
		}
	}

	@Override
	public ServiceType[] registeredServices() {
		synchronized (services) {
			ServiceType[] serviceArray = services.values().toArray(new ServiceType[services.size()]);
			return serviceArray;
		}
	}

	@Override
	public void registerDevice(final Device registeringDevice) throws InvalidObjectReference {
		if (registeringDevice == null) {
			throw new InvalidObjectReference("Cannot register a null device reference");
		}

		// Collect some info from the device. The latter two are CORBA calls.
		String ior = registeringDevice.toString();
		String deviceId = registeringDevice.identifier();
		String deviceLabel = registeringDevice.label();

		// Iterate all launches until we find this device's ILaunch, then load its properties
		ILaunch launch = null;
		ScaDevice<Device> propHolder = null;
		for (ILaunch candidateLaunch : DebugPlugin.getDefault().getLaunchManager().getLaunches()) {
			// Find the launch by matching device id and/or label
			String launchDevId = candidateLaunch.getAttribute(LaunchVariables.DEVICE_ID);
			String launchDevLabel = candidateLaunch.getAttribute(LaunchVariables.DEVICE_LABEL);
			if (launchDevId == null || !launchDevId.equals(deviceId)) {
				continue;
			}
			if (launchDevLabel == null || !launchDevLabel.equals(deviceLabel)) {
				continue;
			}
			launch = candidateLaunch;
			ILaunchConfiguration launchConfig = candidateLaunch.getLaunchConfiguration();

			// Load the properties from the PRF and override with values from the launch configuration
			try {
				propHolder = ScaFactory.eINSTANCE.createScaDevice();
				propHolder.setProfileURI(ScaLaunchConfigurationUtil.getProfileURI(launchConfig));
				propHolder.fetchProfileObject(new NullProgressMonitor());
				propHolder.fetchProperties(new NullProgressMonitor());
				ScaLaunchConfigurationUtil.loadProperties(launchConfig, propHolder);
			} catch (CoreException e) {
				propHolder = null;
				ScaDebugPlugin.logError("Unable to retrieve properties for a device launch", e);
			}

			// Hold on to the launch -> IOR mapping
			synchronized (launchToDeviceIor) {
				launchToDeviceIor.put(launch, ior);
			}

			break;
		}

		// If we have properties available, call initializeProperties(...)
		if (propHolder != null) {
			initializeProperties(launch, "device " + deviceLabel, propHolder, registeringDevice);
		}

		// Initialize
		try {
			registeringDevice.initialize();
		} catch (final InitializeError e) {
			LaunchLogger.INSTANCE.writeToConsole(launch, CFErrorFormatter.format(e, "device " + deviceLabel), ConsoleColor.STDERR);
		}

		// If we have properties available, call configure(...)
		if (propHolder != null) {
			configure(launch, "device " + deviceLabel, propHolder, registeringDevice);
		}

		// Register the device and refresh the model so it notices it
		synchronized (devices) {
			devices.put(ior, registeringDevice);
		}
		refreshChildrenJob.schedule();
	}

	@Override
	public void unregisterDevice(final Device registeredDevice) throws InvalidObjectReference {
		if (registeredDevice == null) {
			String msg = "Cannot unregister a null device reference";
			throw new InvalidObjectReference(msg, msg);
		}

		String ior = registeredDevice.toString();
		synchronized (devices) {
			if (devices.remove(ior) == null) {
				String msg = "Device is not registered";
				throw new InvalidObjectReference(msg, msg);
			}
		}

		refreshChildrenJob.schedule();
	}

	@Override
	public void shutdown() {
		// Kill all services
		Set<ILaunch> launches;
		synchronized (launchToServiceName) {
			launches = new HashSet<>(launchToServiceName.keySet());
		}
		for (ILaunch launch : launches) {
			try {
				launch.terminate();
			} catch (DebugException e) {
				// PASS
			}
		}

		// Release all devices
		Device[] deviceArray = registeredDevices();
		for (Device device : deviceArray) {
			try {
				// TODO: Call with a timeout of 3 seconds to match CF behavior
				device.releaseObject();
			} catch (ReleaseError | SystemException e) {
				// PASS
			}
		}

		// Kill all devices
		synchronized (launchToDeviceIor) {
			launches = new HashSet<>(launchToDeviceIor.keySet());
		}
		for (ILaunch launch : launches) {
			try {
				launch.terminate();
			} catch (DebugException e) {
				// PASS
			}
		}
	}

	@Override
	public void registerService(final org.omg.CORBA.Object registeringService, final String name) throws InvalidObjectReference {
		ServiceType type = new ServiceType(registeringService, name);

		// Iterate all launches until we find this service's ILaunch
		ILaunch launch = null;
		for (ILaunch candidateLaunch : DebugPlugin.getDefault().getLaunchManager().getLaunches()) {
			// Find the launch by matching the service name
			String launchServiceName = candidateLaunch.getAttribute(LaunchVariables.SERVICE_NAME);
			if (launchServiceName == null || !launchServiceName.equals(name)) {
				continue;
			}
			launch = candidateLaunch;

			// Hold on to the launch -> name mapping
			synchronized (launchToServiceName) {
				launchToServiceName.put(launch, name);
			}

			break;
		}

		if (registeringService._is_a(LifeCycleHelper.id())) {
			LifeCycle service = LifeCycleHelper.narrow(registeringService);
			try {
				service.initialize();
			} catch (InitializeError e) {
				throw new InvalidObjectReference("Initialize error", Arrays.toString(e.errorMessages));
			}
		}

		// Register the service and refresh the model so it notices it
		synchronized (services) {
			services.put(name, type);
		}
		refreshChildrenJob.schedule();
	}

	@Override
	public void unregisterService(final org.omg.CORBA.Object unregisteringService, final String name) throws InvalidObjectReference {
		if (unregisteringService == null) {
			String msg = "Cannot unregister a null service reference";
			throw new InvalidObjectReference(msg, msg);
		}

		synchronized (services) {
			if (services.remove(name) == null) {
				String msg = String.format("Service '%s' is not registered", name);
				throw new InvalidObjectReference(msg, msg);
			}
		}

		refreshChildrenJob.schedule();
	}

	/**
	 * Calls {@link PropertyEmitter#initializeProperties(DataType[])}.
	 * @param launch The device/service being launched
	 * @param label A UI label for the item (e.g. "device Foo")
	 * @param propHolder The SCA model object holding the loaded properties
	 * @param propEmitter The {@link PropertyEmitter} that will get its properties initialized
	 */
	private void initializeProperties(ILaunch launch, String label, ScaPropertyContainer< ? , ? > propHolder, PropertyEmitter propEmitter) {
		// Collect non-null properties of type 'property' (but not type 'execparam')
		List<DataType> initializeProps = new ArrayList<DataType>();
		for (final ScaAbstractProperty< ? > prop : propHolder.getProperties()) {
			if (PropertiesUtil.canInitialize(prop.getDefinition())) {
				DataType dt = prop.getProperty();
				if (dt.value != null && dt.value.type().kind() != TCKind.tk_null) {
					initializeProps.add(dt);
				}
			}
		}

		// Initialize properties
		DataType[] initializePropsArray = initializeProps.toArray(new CF.DataType[initializeProps.size()]);
		try {
			propEmitter.initializeProperties(initializePropsArray);
		} catch (AlreadyInitialized e) {
			LaunchLogger.INSTANCE.writeToConsole(launch, CFErrorFormatter.format(e, label), ConsoleColor.STDERR);
		} catch (InvalidConfiguration e) {
			LaunchLogger.INSTANCE.writeToConsole(launch, CFErrorFormatter.format(e, label), ConsoleColor.STDERR);
		} catch (PartialConfiguration e) {
			LaunchLogger.INSTANCE.writeToConsole(launch, CFErrorFormatter.format(e, label), ConsoleColor.STDERR);
		} catch (BAD_OPERATION e) {
			String msg;
			if (initializeProps.size() == 0) {
				msg = String.format("Could not call initializeProperties on %s in the sandbox (CORBA BAD_OPERATION). "
					+ "If the installed version of REDHAWK is pre-2.0, this is expected and can be ignored.", label);
			} else {
				msg = "There are properties of kind 'property', but %s does not appear to support the REDHAWK 2.0 API (CORBA BAD_OPERATION)";
			}
			LaunchLogger.INSTANCE.writeToConsole(launch, msg, ConsoleColor.STDERR);
		}
	}

	/**
	 * Calls {@link PropertySet#configure(DataType[])}.
	 * @param launch The device/service being launched
	 * @param label A UI label for the item (e.g. "device Foo")
	 * @param propHolder The SCA model object holding the loaded properties
	 * @param propSet The {@link PropertySet} that will get its properties configured
	 */
	private void configure(ILaunch launch, String label, ScaPropertyContainer< ? , ? > propHolder, PropertySet propSet) {
		// Configure - Find configurable properties that aren't set to their default
		List<DataType> configureProps = new ArrayList<DataType>();
		for (final ScaAbstractProperty< ? > prop : propHolder.getProperties()) {
			if (!prop.isDefaultValue() && !prop.getDefinition().isKind(PropertyConfigurationType.PROPERTY)
				&& PropertiesUtil.canConfigure(prop.getDefinition())) {
				configureProps.add(new DataType(prop.getId(), prop.toAny()));
			}
		}

		try {
			propSet.configure(configureProps.toArray(new DataType[configureProps.size()]));
		} catch (final PartialConfiguration e) {
			LaunchLogger.INSTANCE.writeToConsole(launch, CFErrorFormatter.format(e, label), ConsoleColor.STDERR);
		} catch (final InvalidConfiguration e) {
			LaunchLogger.INSTANCE.writeToConsole(launch, CFErrorFormatter.format(e, label), ConsoleColor.STDERR);
		}
	}

	@Override
	public String getComponentImplementationId(final String componentInstantiationId) {
		// TODO
		return "";
	}

	public LocalAbstractComponent launch(final String usageName, String compId, final DataType[] initConfiguration, @NonNull final URI spdURI,
		final String implId, final String mode) throws CoreException {
		// Create launch config
		ILaunchConfigurationWorkingCopy config = createLaunchConfig(usageName, compId, initConfiguration, spdURI, implId, mode);

		// Launch
		final ILaunch launch = config.launch(mode, new NullProgressMonitor(), false);

		// Find the device/service and return it
		return postLaunch(launch);
	}

	/**
	 * @deprecated Use {@link #launch(String, String, DataType[], URI, String, String)}
	 */
	@Deprecated
	public Resource launch(final String compId, final DataType[] initConfiguration, @NonNull final String spdURI, final String implId, final String mode)
		throws ExecuteFail {
		Assert.isNotNull(spdURI, "SPD URI must not be null");
		try {
			URI uri = URI.createURI(spdURI);
			if (uri == null) {
				throw new NullPointerException();
			}
			LocalAbstractComponent abstractComponent = launch(null, compId, initConfiguration, uri, implId, mode);

			// Get the device CORBA object and return
			if (abstractComponent instanceof ScaDevice) {
				return ((ScaDevice< ? >) abstractComponent).fetchNarrowedObject(new NullProgressMonitor());
			} else {
				abstractComponent.getLaunch().terminate();
				IStatus status = new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Launched softpkg is not a device and cannot be narrowed to CF.Resource");
				throw new CoreException(status);
			}
		} catch (final CoreException e) {
			ScaDebugPlugin.getInstance().getLog().log(new Status(e.getStatus().getSeverity(), ScaDebugPlugin.ID, "Failed to launch device.", e));
			throw new ExecuteFail(ErrorNumberType.CF_EFAULT, e.getStatus().getMessage());
		}
	}

	/**
	 * @deprecated Use {@link #launch(String, String, DataType[], URI, String, String)}
	 */
	@NonNull
	@Deprecated
	public Resource launch(@Nullable String usageName, @Nullable final String compId, @Nullable final String execParams, @NonNull URI spdURI,
		@Nullable final String implId, @Nullable String mode) throws CoreException {
		// Create launch config
		ILaunchConfigurationWorkingCopy config = createLaunchConfig(usageName, compId, null, spdURI, implId, mode);
		if (execParams != null && execParams.length() > 0) {
			config.setAttribute(LaunchVariables.EXEC_PARAMS, execParams);
		}

		// Launch
		final ILaunch launch = config.launch(mode, new NullProgressMonitor(), false);

		// Find the device and return
		LocalAbstractComponent abstractComponent = postLaunch(launch);
		if (abstractComponent instanceof ScaDevice) {
			return ((ScaDevice< ? >) abstractComponent).fetchNarrowedObject(new NullProgressMonitor());
		} else {
			launch.terminate();
			IStatus status = new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Launched softpkg is not a device and cannot be narrowed to CF.Resource");
			throw new CoreException(status);
		}
	}

	private ILaunchConfigurationWorkingCopy createLaunchConfig(String usageName, String compId, DataType[] initConfiguration, URI spdURI, String implId,
		String mode) throws CoreException {
		Assert.isNotNull(spdURI, "SPD URI must not be null");

		// Use EFS to unwrap non-platform URIs; for example convert sca URI -> file URI
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

		// Load SPD
		final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		final SoftPkg spd = SoftPkg.Util.getSoftPkg(resourceSet.getResource(spdURI, true));
		if (mode == null) {
			mode = ILaunchManager.RUN_MODE;
		}

		// Create a launch config
		final ILaunchConfigurationFactory factory = ScaDebugPlugin.getInstance().getLaunchConfigurationFactoryRegistry().getFactory(spd, implId);
		if (factory == null) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to obtain Launch Factory for impl: " + implId));
		}
		final ILaunchConfigurationWorkingCopy config = factory.createLaunchConfiguration(spd.getName(), implId, spd);

		// Determine usage name if not specified
		if (usageName == null && compId != null) {
			if (DceUuidUtil.isValid(compId)) {
				usageName = compId;
			} else {
				int index = compId.indexOf(':');
				if (index != -1 && compId.length() > (index + 1)) {
					usageName = compId.substring(index + 1);
				} else {
					usageName = compId;
				}
			}
		}

		// Set appropriate identifiers (name, label, id, etc)
		ComponentType type = SoftwareComponent.Util.getWellKnownComponentType(spd.getDescriptor().getComponent());
		switch (type) {
		case SERVICE:
			if (usageName != null) {
				config.setAttribute(LaunchVariables.SERVICE_NAME, usageName);
			}
			break;
		case DEVICE:
			if (usageName != null) {
				config.setAttribute(LaunchVariables.DEVICE_LABEL, usageName);
			}
			if (compId != null) {
				config.setAttribute(LaunchVariables.DEVICE_ID, compId);

				// Initial properties
				if (initConfiguration != null) {
					final ScaComponent propHolder = ScaFactory.eINSTANCE.createScaComponent();
					propHolder.setProfileURI(spdURI);
					propHolder.fetchProfileObject(new NullProgressMonitor());
					propHolder.fetchProperties(new NullProgressMonitor());
					for (DataType dt : initConfiguration) {
						ScaAbstractProperty< ? > prop = propHolder.getProperty(dt.id);
						if (prop != null) {
							prop.fromAny(dt.value);
						}
					}
					ScaLaunchConfigurationUtil.saveProperties(config, propHolder);
				}
			}
			break;
		default:
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Cannot create a launch configuration for " + spd.getName()));
		}

		return config;
	}

	private LocalAbstractComponent postLaunch(ILaunch launch) throws CoreException {
		ILaunchConfiguration config = launch.getLaunchConfiguration();

		ComponentType type;
		String matchName;
		if (config.hasAttribute(LaunchVariables.SERVICE_NAME)) {
			type = ComponentType.SERVICE;
			matchName = config.getAttribute(LaunchVariables.SERVICE_NAME, "");
		} else if (config.hasAttribute(LaunchVariables.DEVICE_LABEL)) {
			type = ComponentType.DEVICE;
			matchName = config.getAttribute(LaunchVariables.DEVICE_ID, "");
		} else {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Cannot determine type of launched resource"));
		}

		for (int tries = 0; tries < 100; tries++) {
			switch (type) {
			case DEVICE:
				for (final ScaDevice< ? > device : devMgrModelObj.fetchDevices(new NullProgressMonitor(), RefreshDepth.SELF)) {
					device.fetchAttributes(null);
					if (matchName.equals(device.getIdentifier())) {
						return (LocalAbstractComponent) device;
					}
				}
				break;
			case SERVICE:
				for (final ScaService service : devMgrModelObj.fetchServices(new NullProgressMonitor(), RefreshDepth.SELF)) {
					service.fetchAttributes(null);
					if (matchName.equals(service.getName())) {
						return (LocalAbstractComponent) service;
					}
				}
				break;
			default:
			}

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// PASS
			}
		}

		launch.terminate();
		throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to find device/serivce after launch", null));
	}

	private final ILaunchesListener2 launchListener = new ILaunchesListener2() {

		@Override
		public void launchesRemoved(ILaunch[] launches) {
		}

		@Override
		public void launchesAdded(ILaunch[] launches) {
		}

		@Override
		public void launchesChanged(ILaunch[] launches) {
		}

		@Override
		public void launchesTerminated(ILaunch[] launches) {
			boolean refresh = false;

			for (ILaunch launch : launches) {
				String key;

				// Attempt to find and remove a matching device
				synchronized (launchToDeviceIor) {
					key = launchToDeviceIor.remove(launch);
				}
				if (key != null) {
					synchronized (devices) {
						Device device = devices.remove(key);
						refresh |= (device != null);
					}
					continue;
				}

				// Attempt to find and remove a matching service
				synchronized (launchToServiceName) {
					key = launchToServiceName.remove(launch);
				}
				if (key != null) {
					synchronized (services) {
						ServiceType terminatedService = services.remove(key);
						refresh |= (terminatedService != null);
					}
					continue;
				}
			}

			// If we modified our device or service lists, we need to tell the UI to refresh
			if (refresh) {
				refreshChildrenJob.schedule();
			}
		}
	};
}
