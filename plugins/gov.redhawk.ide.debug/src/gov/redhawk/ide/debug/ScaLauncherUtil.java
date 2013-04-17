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
package gov.redhawk.ide.debug;

import gov.redhawk.ide.debug.variables.LaunchVariables;
import gov.redhawk.model.sca.ProfileObjectWrapper;
import gov.redhawk.model.sca.ScaAbstractComponent;
import gov.redhawk.model.sca.ScaAbstractProperty;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.ScaFactory;
import gov.redhawk.model.sca.ScaService;
import gov.redhawk.model.sca.ScaSimpleProperty;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.sca.launch.ScaLaunchConfigurationConstants;
import gov.redhawk.sca.launch.ScaLaunchConfigurationUtil;
import gov.redhawk.sca.util.Debug;
import gov.redhawk.sca.util.SubMonitor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import mil.jpeojtrs.sca.prf.PropertyConfigurationType;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.util.PropertiesUtil;
import mil.jpeojtrs.sca.scd.ComponentType;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.DceUuidUtil;
import mil.jpeojtrs.sca.util.NamedThreadFactory;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import CF.ResourceHelper;
import CF.ResourceOperations;
import CF.PropertySetPackage.InvalidConfiguration;
import CF.PropertySetPackage.PartialConfiguration;
import CF.ResourcePackage.StartError;

/**
 * 
 */
public final class ScaLauncherUtil {

	private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(new NamedThreadFactory(ScaLauncherUtil.class.getName()));

	/**
	 * @since 3.0
	 */
	public static final String LAUNCH_ATT_PROGRAM_ARGUMENT_MAP = ScaDebugPlugin.ID + ".programArgumentMap";
	private static final String LAUNCH_ATT_URI = ScaDebugPlugin.ID + ".uri";

	private static final Debug DEBUG_ARGS = new Debug(ScaDebugPlugin.getInstance(), "LauncherArgs");

	private ScaLauncherUtil() {

	}
	
	/**
	 * @since 3.0
	 */
	public static Map<String,String> createMap(String programArguments) {
		String[] split = programArguments.split(" ");
		Map<String,String> retVal = new HashMap<String, String>(split.length/2);
		for (int i=0; i+1<split.length; i+=2) {
			retVal.put(split[i], split[i+1]);
		}
		return retVal;
	}

	/**
	 * @since 3.0
	 */
	public static void postLaunch(final ILaunchConfiguration configuration, final String mode, final ILaunch launch, final IProgressMonitor monitor) throws CoreException {
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
		LocalAbstractComponent comp = null;
		Map programArgMap = configuration.getAttribute(ScaLauncherUtil.LAUNCH_ATT_PROGRAM_ARGUMENT_MAP, Collections.EMPTY_MAP);
		try {
			
			if (launch.getAttribute(LaunchVariables.DEVICE_LABEL) != null) {
				comp = ScaLauncherUtil.postLaunchDevice(configuration, mode,launch,subMonitor.newChild(50));
			} else if (launch.getAttribute(LaunchVariables.SERVICE_NAME) != null) {
				comp = ScaLauncherUtil.postLaunchService(configuration, mode,launch,subMonitor.newChild(50));
			} else {
				comp = ScaLauncherUtil.postLaunchComponent(configuration, mode,launch,subMonitor.newChild(50));
			}
		} catch (final CoreException e) {
			// If there is a problem with the component terminate it
			launch.terminate();
			throw e;
		}

		if (comp == null) {
			// the launch was terminated or otherwise interrupted
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to resolve component.", null));
		}

		final String execString = ScaLauncherUtil.createExecParamString(programArgMap);
		final String implID = launch.getLaunchConfiguration().getAttribute(ScaDebugLaunchConstants.ATT_IMPL_ID, (String) null);
		final boolean autoStart = launch.getLaunchConfiguration().getAttribute(ScaLaunchConfigurationConstants.ATT_START,
		        ScaLaunchConfigurationConstants.DEFAULT_VALUE_ATT_START);
		final String uriStr = launch.getAttribute(ScaLauncherUtil.LAUNCH_ATT_URI);
		final URI uri;
		if (uriStr != null) {
			uri = URI.createURI(uriStr);
		} else {
			uri = null;
		}

		final LocalAbstractComponent newComponent = comp;
		ScaModelCommand.execute(newComponent, new ScaModelCommand() {

			public void execute() {
				newComponent.setExecParam(execString);
				newComponent.setImplementationID(implID);
				newComponent.setLaunch(launch);
				newComponent.setMode(mode);
				if (uri != null && newComponent instanceof ProfileObjectWrapper< ? >) {
					((ProfileObjectWrapper< ? >) newComponent).setProfileURI(uri);
				}
			}
		});

		if (newComponent instanceof ScaAbstractComponent< ? >) {
			final ScaAbstractComponent< ? > scaComp = (ScaAbstractComponent< ? >) newComponent;
			final ScaComponent tmp = ScaFactory.eINSTANCE.createScaComponent();
			tmp.setProfileObj(scaComp.getProfileObj());
			for (final ScaAbstractProperty< ? > prop : tmp.fetchProperties(null)) {
				prop.setIgnoreRemoteSet(true);
			}
			ScaLaunchConfigurationUtil.loadProperties(launch.getLaunchConfiguration(), tmp);

			for (final ScaAbstractProperty< ? > prop : tmp.getProperties()) {
				if (!prop.isDefaultValue() && PropertiesUtil.canConfigure(prop.getDefinition())) {
					final ScaAbstractProperty< ? > scaProp = scaComp.getProperty(prop.getId());
					try {
						scaProp.setRemoteValue(prop.toAny());
					} catch (final PartialConfiguration e) {
						// PASS
					} catch (final InvalidConfiguration e) {
						// PASS
					}
				}
			}
		}

		if (newComponent instanceof ResourceOperations) {
			if (autoStart) {
				try {
					((ResourceOperations) newComponent).start();
				} catch (final StartError e) {
					// PASS
				}
			}
		}
		subMonitor.done();
	}

	private static LocalAbstractComponent postLaunchComponent(final ILaunchConfiguration configuration, final String mode, final ILaunch launch, final IProgressMonitor monitor) throws CoreException {
		final Map programArgMap = configuration.getAttribute(ScaLauncherUtil.LAUNCH_ATT_PROGRAM_ARGUMENT_MAP, Collections.EMPTY_MAP);
		final String nameBinding = (String) programArgMap.get(LaunchVariables.NAME_BINDING);
		final LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca();

		final Future<LocalScaComponent> future = ScaLauncherUtil.EXECUTOR.submit(new Callable<LocalScaComponent>() {
			public LocalScaComponent call() throws Exception {
				LocalScaComponent newComponent = null;
				ORB orb = ORB.init((String[]) null, null);
				try {
					String contextIor = (String) programArgMap.get(LaunchVariables.NAMING_CONTEXT_IOR);
					final NamingContextExt namingContext = NamingContextExtHelper.narrow(orb.string_to_object(launch
					        .getAttribute(contextIor)));

					while (newComponent == null) {
						// If this launch was terminated, immediately bail
						if (launch.isTerminated()) {
							throw new Exception("Component terminated while waiting to launch.");
						}

						try {
							final CF.Resource ref = ResourceHelper.narrow(namingContext.resolve_str(nameBinding));
							for (final ScaComponent comp : localSca.getSandboxWaveform().getComponents()) {
								if (comp instanceof LocalScaComponent && ref._is_equivalent(comp.getCorbaObj())) {
									newComponent = (LocalScaComponent) comp;
								}
							}
							for (final ScaWaveform waveform : localSca.getWaveforms()) {
								for (final ScaComponent comp : waveform.getComponents()) {
									if (comp instanceof LocalScaComponent && ref._is_equivalent(comp.getCorbaObj())) {
										newComponent = (LocalScaComponent) comp;
									}
								}
							}
						} catch (final NotFound e) {
							// PASS
						}
						if (newComponent == null) {
							Thread.sleep(500);
						}
					}
				} finally {
					orb.destroy();
				}
				return newComponent;
			}

		});

		try {
			final int timeout = launch.getLaunchConfiguration().getAttribute(ScaDebugLaunchConstants.ATT_LAUNCH_TIMEOUT,
			        ScaDebugLaunchConstants.DEFAULT_ATT_LAUNCH_TIMEOUT);
			if (timeout < 0 || ILaunchManager.DEBUG_MODE.equals(launch.getLaunchMode())) {
				// In debug-mode wait they may have placed a break-point
				// that is delaying registration so wait forever
				final LocalScaComponent newComponent = future.get();
				return newComponent;
			} else {
				final LocalScaComponent newComponent = future.get(timeout, TimeUnit.SECONDS);
				return newComponent;
			}
		} catch (final InterruptedException e1) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Interrupted waiting for component to start.", e1));
		} catch (final ExecutionException e1) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Error while waiting for component to start.", e1));
		} catch (final TimeoutException e1) {
			future.cancel(true);
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Timed out waiting for component to start.", e1));
		}

	}

	private static LocalAbstractComponent postLaunchService(final ILaunchConfiguration configuration, final String mode, final ILaunch launch, final IProgressMonitor monitor) throws CoreException {
		final Map programArgMap = configuration.getAttribute(ScaLauncherUtil.LAUNCH_ATT_PROGRAM_ARGUMENT_MAP, Collections.EMPTY_MAP);
		final String name = (String) programArgMap.get(LaunchVariables.SERVICE_NAME);
		final LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca();

		final Future<LocalAbstractComponent> future = ScaLauncherUtil.EXECUTOR.submit(new Callable<LocalAbstractComponent>() {
			public LocalAbstractComponent call() throws Exception {
				LocalAbstractComponent retVal = null;
				while (retVal == null) {
					// If this launch was terminated, immediately bail
					if (launch.isTerminated()) {
						throw new Exception("Component terminated while waiting to launch.");
					}

					for (final ScaService service : localSca.getSandboxDeviceManager().getServices()) {
						if (name.equals(service.getName())) {
							retVal = (LocalAbstractComponent) service;
							break;
						}
					}
					if (retVal == null) {
						try {
							Thread.sleep(500);
						} catch (final InterruptedException e) {
							throw e;
						}
					}
				}
				return retVal;
			}

		});

		try {
			final int timeout = launch.getLaunchConfiguration().getAttribute(ScaDebugLaunchConstants.ATT_LAUNCH_TIMEOUT,
			        ScaDebugLaunchConstants.DEFAULT_ATT_LAUNCH_TIMEOUT);
			if (timeout < 0 || ILaunchManager.DEBUG_MODE.equals(launch.getLaunchMode())) {
				// In debug-mode wait they may have placed a break-point
				// that is delaying registration so wait forever
				final LocalAbstractComponent newComponent = future.get();
				return newComponent;
			} else {
				final LocalAbstractComponent newComponent = future.get(timeout, TimeUnit.SECONDS);
				return newComponent;
			}
		} catch (final InterruptedException e1) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Interrupted waiting for component to start.", e1));
		} catch (final ExecutionException e1) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Error while waiting for component to start.", e1));
		} catch (final TimeoutException e1) {
			future.cancel(true);
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Timed out waiting for component to start.", e1));
		}
	}

	private static LocalAbstractComponent postLaunchDevice(final ILaunchConfiguration configuration, final String mode, final ILaunch launch, final IProgressMonitor monitor) throws CoreException {
		final Map programArgMap = configuration.getAttribute(ScaLauncherUtil.LAUNCH_ATT_PROGRAM_ARGUMENT_MAP, Collections.EMPTY_MAP);
		final String deviceLabel = (String) programArgMap.get(LaunchVariables.DEVICE_LABEL);
		final LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca();

		final Future<LocalAbstractComponent> future = ScaLauncherUtil.EXECUTOR.submit(new Callable<LocalAbstractComponent>() {
			public LocalAbstractComponent call() throws Exception {
				LocalAbstractComponent retVal = null;
				while (retVal == null) {
					// If this launch was terminated, immediately bail
					if (launch.isTerminated()) {
						throw new Exception("Component terminated while waiting to launch.");
					}

					for (final ScaDevice< ? > device : localSca.getSandboxDeviceManager().getAllDevices()) {
						final String label = device.fetchLabel(null);
						if (deviceLabel.equals(label)) {
							retVal = (LocalAbstractComponent) device;
							break;
						}
					}
					if (retVal == null) {
						try {
							Thread.sleep(500);
						} catch (final InterruptedException e) {
							throw e;
						}
					}
				}
				return retVal;
			}

		});

		try {
			final int timeout = launch.getLaunchConfiguration().getAttribute(ScaDebugLaunchConstants.ATT_LAUNCH_TIMEOUT,
			        ScaDebugLaunchConstants.DEFAULT_ATT_LAUNCH_TIMEOUT);
			if (timeout < 0 || ILaunchManager.DEBUG_MODE.equals(launch.getLaunchMode())) {
				// In debug-mode wait they may have placed a break-point
				// that is delaying registration so wait forever
				final LocalAbstractComponent newComponent = future.get();
				return newComponent;
			} else {
				final LocalAbstractComponent newComponent = future.get(timeout, TimeUnit.SECONDS);
				return newComponent;
			}
		} catch (final InterruptedException e1) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Interrupted waiting for component to start.", e1));
		} catch (final ExecutionException e1) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Error while waiting for component to start.", e1));
		} catch (final TimeoutException e1) {
			future.cancel(true);
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Timed out waiting for component to start.", e1));
		}

	}

	public static String getSpdProgramArguments(final URI spdURI, final ILaunch launch, final ILaunchConfiguration configuration) throws CoreException {
		final String implID = configuration.getAttribute(ScaDebugLaunchConstants.ATT_IMPL_ID, "");
		final ResourceSet resourceSet = new ResourceSetImpl();

		final Resource spdResource = resourceSet.getResource(spdURI, true);
		final SoftPkg spd = SoftPkg.Util.getSoftPkg(spdResource);

		final Implementation impl = spd.getImplementation(implID);
		final Map<String, String> override = ScaLauncherUtil.createOverrideMap(configuration);

		ScaLauncherUtil.loadProperties(spd, configuration, override);

		if (launch != null) {
			launch.setAttribute(ScaLauncherUtil.LAUNCH_ATT_URI, spdURI.toString());
		}

		final StringBuilder builder = new StringBuilder();
		final String execParams = ScaLauncherUtil.getExecParams(impl, spd, override);

		builder.append(execParams);
		if (builder.length() != 0) {
			builder.append(" ");
		}
		final ComponentType type = SoftwareComponent.Util.getWellKnownComponentType(spd.getDescriptor().getComponent());
		switch (type) {
		case DEVICE:
			builder.append(ScaLauncherUtil.getDeviceArgs(launch, override, spd));
			break;
		case EVENT_SERVICE:
		case SERVICE:
			builder.append(ScaLauncherUtil.getServiceArgs(launch, override, spd));
			break;
		default:
			builder.append(ScaLauncherUtil.getComponentArgs(launch, override, spd));
		}

		final String debugLevel = configuration.getAttribute(ScaDebugLaunchConstants.ATT_DEBUG_LEVEL, (String) null);
		if (debugLevel != null) {
			int level;
			try {
				level = Integer.valueOf(debugLevel);
			} catch (final NumberFormatException e) {
				if ("Fatal".equalsIgnoreCase(debugLevel)) {
					level = 0;
				} else if ("Error".equalsIgnoreCase(debugLevel)) {
					level = 1;
				} else if ("Warn".equalsIgnoreCase(debugLevel)) {
					level = 2;
				} else if ("Info".equalsIgnoreCase(debugLevel)) {
					level = 3;
				} else if ("Debug".equalsIgnoreCase(debugLevel)) {
					level = 4;
				} else {
					level = 5;
				}
			}
			if (builder.length() != 0) {
				builder.append(" ");
			}
			builder.append("DEBUG_LEVEL " + level);
		}

		if (ScaLauncherUtil.DEBUG_ARGS.enabled) {
			ScaLauncherUtil.DEBUG_ARGS.message(builder.toString());
		}
		return builder.toString();
	}

	private static void loadProperties(final SoftPkg spd, final ILaunchConfiguration configuration, final Map<String, String> override) throws CoreException {
		if (override.containsValue(ScaDebugLaunchConstants.ARG_EXEC_PARAMS)) {
			return;
		}
		final ScaComponent tmp = ScaFactory.eINSTANCE.createScaComponent();
		tmp.setProfileObj(spd);
		for (final ScaAbstractProperty< ? > prop : tmp.fetchProperties(null)) {
			prop.setIgnoreRemoteSet(true);
		}
		ScaLaunchConfigurationUtil.loadProperties(configuration, tmp);

		final Map<String, Object> execParams = new HashMap<String, Object>();
		for (final ScaAbstractProperty< ? > prop : tmp.getProperties()) {
			if (prop instanceof ScaSimpleProperty && !prop.isDefaultValue() && prop.getDefinition().isKind(PropertyConfigurationType.EXECPARAM)) {
				final ScaSimpleProperty simple = (ScaSimpleProperty) prop;
				execParams.put(simple.getId(), simple.getValue());
			}
		}
		if (!execParams.isEmpty()) {
			override.put(ScaDebugLaunchConstants.ARG_EXEC_PARAMS, ScaLauncherUtil.createExecParamString(execParams));
		}
	}

	private static Map<String, String> createOverrideMap(final ILaunchConfiguration configuration) throws CoreException {
		@SuppressWarnings("unchecked")
		final Map<String, String> map = configuration.getAttribute(ScaDebugLaunchConstants.ATT_OVERRIDE_MAP, Collections.EMPTY_MAP);
		final Map<String, String> retVal = new HashMap<String, String>();
		retVal.putAll(map);
		return retVal;

	}

	/**
	 * @param stdArgs
	 * @param spd
	 * @param builder
	 */
	private static String getComponentArgs(final ILaunch launch, final Map<String, String> override, final SoftPkg spd) {
		final StringBuilder builder = new StringBuilder();

		String namingContextIOR = null;

		builder.append(ScaDebugLaunchConstants.ARG_NAMING_CONTEXT_IOR);
		builder.append(" ");
		if (override.containsKey(ScaDebugLaunchConstants.ARG_NAMING_CONTEXT_IOR)) {
			namingContextIOR = override.get(ScaDebugLaunchConstants.ARG_NAMING_CONTEXT_IOR);
		} else {
			final NotifyingNamingContext nc = ScaLauncherUtil.getDomNamingContext(spd);
			namingContextIOR = nc.getNamingContext().toString();
		}
		builder.append(namingContextIOR);
		builder.append(" ");

		String name = null;
		builder.append(ScaDebugLaunchConstants.ARG_NAME_BINDING);
		builder.append(" ");
		if (override.containsKey(ScaDebugLaunchConstants.ARG_NAME_BINDING)) {
			name = override.get(ScaDebugLaunchConstants.ARG_NAME_BINDING);
		} else {
			name = ScaLauncherUtil.getUniqueName(spd, namingContextIOR);
		}
		builder.append(name);
		builder.append(" ");

		builder.append(ScaDebugLaunchConstants.ARG_COMPONENT_IDENTIFIER);
		builder.append(" ");
		if (override.containsKey(ScaDebugLaunchConstants.ARG_COMPONENT_IDENTIFIER)) {
			builder.append(override.get(ScaDebugLaunchConstants.ARG_COMPONENT_IDENTIFIER));
		} else {
			final String compIdentifier;
			// We would like this to be sandbox:name but
			// but whenever usagename != compIdentifer connections
			// don't work per ticket #2165
			compIdentifier = name;
			builder.append(compIdentifier);
		}

		return builder.toString();
	}

	/**
	 * @param stdArgs
	 * @param spd
	 * @param builder
	 */
	private static String getServiceArgs(final ILaunch launch, final Map<String, String> override, final SoftPkg spd) {
		final StringBuilder builder = new StringBuilder();

		String namingContext = null;
		builder.append(ScaDebugLaunchConstants.ARG_NAMING_CONTEXT_IOR);
		builder.append(" ");
		if (override.containsKey(ScaDebugLaunchConstants.ARG_NAMING_CONTEXT_IOR)) {
			namingContext = override.get(ScaDebugLaunchConstants.ARG_NAMING_CONTEXT_IOR);
		} else {
			final NotifyingNamingContext nc = ScaLauncherUtil.getDevNamingContext(spd);
			namingContext = nc.getNamingContext().toString();
		}
		builder.append(namingContext);
		builder.append(" ");

		String name = null;
		builder.append(ScaDebugLaunchConstants.ARG_NAME_BINDING);
		builder.append(" ");
		if (override.containsKey(ScaDebugLaunchConstants.ARG_NAME_BINDING)) {
			name = override.get(ScaDebugLaunchConstants.ARG_NAME_BINDING);
		} else {
			name = ScaLauncherUtil.getServiceUniqueName(spd);
		}
		builder.append(name);
		builder.append(" ");

		builder.append(ScaDebugLaunchConstants.ARG_DEVICE_MGR_IOR);
		builder.append(" ");
		if (override.containsKey(ScaDebugLaunchConstants.ARG_DEVICE_MGR_IOR)) {
			builder.append(override.get(ScaDebugLaunchConstants.ARG_DEVICE_MGR_IOR));
		} else {
			builder.append(ScaLauncherUtil.getDeviceManagerIOR());
		}
		builder.append(" ");

		String serviceName = null;
		builder.append(ScaDebugLaunchConstants.ARG_SERVICE_NAME);
		builder.append(" ");
		if (override.containsKey(ScaDebugLaunchConstants.ARG_SERVICE_NAME)) {
			serviceName = override.get(ScaDebugLaunchConstants.ARG_SERVICE_NAME);
		} else {
			serviceName = name;
		}
		builder.append(serviceName);
		builder.append(" ");

		builder.append(ScaDebugLaunchConstants.ARG_COMPONENT_IDENTIFIER);
		builder.append(" ");
		if (override.containsKey(ScaDebugLaunchConstants.ARG_COMPONENT_IDENTIFIER)) {
			builder.append(override.get(ScaDebugLaunchConstants.ARG_COMPONENT_IDENTIFIER));
		} else {
			builder.append(DceUuidUtil.createDceUUID());
		}

		return builder.toString();
	}

	private static String getServiceUniqueName(final SoftPkg spd) {
		final LocalScaDeviceManager devMgr = ScaDebugPlugin.getInstance().getLocalSca().getSandboxDeviceManager();
		String name;
		for (int i = 1; true; i++) {
			name = spd.getName() + "_" + i;
			boolean contains = false;
			for (final ScaService s : devMgr.getServices()) {
				if (name.equals(s.getName())) {
					contains = true;
				}
			}
			if (!contains) {
				break;
			}
		}
		return name;
	}

	private static NotifyingNamingContext getDomNamingContext(final SoftPkg spd) {
		return ScaDebugPlugin.getInstance().getLocalSca().getSandboxWaveform().getNamingContext().getResourceContext(spd.eResource().getURI());
	}

	private static NotifyingNamingContext getDevNamingContext(final SoftPkg spd) {
		return ScaDebugPlugin.getInstance().getLocalSca().getSandboxDeviceManager().getNamingContext().getResourceContext(spd.eResource().getURI());
	}

	/**
	 * @param profileURI
	 * @param stdArgs
	 * @param spd
	 * @param builder
	 */
	private static String getDeviceArgs(final ILaunch launch, final Map<String, String> stdArgs, final SoftPkg spd) {
		final StringBuilder builder = new StringBuilder();
		final NotifyingNamingContext nc = ScaLauncherUtil.getDevNamingContext(spd);
		final String namingContext = nc.getNamingContext().toString();

		builder.append(ScaDebugLaunchConstants.ARG_DEVICE_MGR_IOR);
		builder.append(" ");
		if (stdArgs.containsKey(ScaDebugLaunchConstants.ARG_DEVICE_MGR_IOR)) {
			builder.append(stdArgs.get(ScaDebugLaunchConstants.ARG_DEVICE_MGR_IOR));
		} else {
			builder.append(ScaLauncherUtil.getDeviceManagerIOR());
		}
		builder.append(" ");

		builder.append(ScaDebugLaunchConstants.ARG_PROFILE_NAME);
		builder.append(" \"");
		if (stdArgs.containsKey(ScaDebugLaunchConstants.ARG_PROFILE_NAME)) {
			builder.append(stdArgs.get(ScaDebugLaunchConstants.ARG_PROFILE_NAME));
		} else {
			final String profile = spd.eResource().getURI().lastSegment();
			builder.append(profile);
		}
		builder.append("\" ");

		builder.append(ScaDebugLaunchConstants.ARG_DEVICE_ID);
		builder.append(" ");
		if (stdArgs.containsKey(ScaDebugLaunchConstants.ARG_DEVICE_ID)) {
			builder.append(stdArgs.get(ScaDebugLaunchConstants.ARG_DEVICE_ID));
		} else {
			builder.append(DceUuidUtil.createDceUUID());
		}
		builder.append(" ");

		builder.append(ScaDebugLaunchConstants.ARG_DEVICE_LABEL);
		builder.append(" ");
		String label = null;
		if (stdArgs.containsKey(ScaDebugLaunchConstants.ARG_DEVICE_LABEL)) {
			label = stdArgs.get(ScaDebugLaunchConstants.ARG_DEVICE_LABEL);
		} else {
			label = ScaLauncherUtil.getDeviceUniqueName(spd);
		}
		builder.append(label);

		return builder.toString();
	}

	private static String getDeviceManagerIOR() {
		return ScaDebugPlugin.getInstance().getLocalSca().getSandboxDeviceManager().getObj().toString();
	}

	private static String getDeviceUniqueName(final SoftPkg spd) {
		final LocalScaDeviceManager devMgr = ScaDebugPlugin.getInstance().getLocalSca().getSandboxDeviceManager();
		String name;
		for (int i = 1; true; i++) {
			name = spd.getName() + "_" + i;
			boolean contains = false;
			for (final ScaDevice< ? > d : devMgr.getAllDevices()) {
				if (name.equals(d.fetchLabel(null))) {
					contains = true;
				}
			}
			if (!contains) {
				break;
			}
		}
		return name;
	}

	private static String getUniqueName(final SoftPkg spd, final String namingContextIOR) {
		ORB orb = ORB.init((String[]) null, null);
		try {
			final NamingContextExt namingContext = NamingContextExtHelper.narrow(orb.string_to_object(namingContextIOR));
			final String name = spd.getName();
			String retVal = name;
			for (int i = 1; true; i++) {
				try {
					namingContext.resolve_str(retVal);
					retVal = name + "_" + i;
				} catch (final NotFound e) {
					return retVal;
				} catch (final CannotProceed e) {
					throw new IllegalStateException(e);
				} catch (final InvalidName e) {
					throw new IllegalStateException(e);
				}
			}
		} finally {
			orb.destroy();
		}
	}

	private static String getExecParams(final Implementation impl, final SoftPkg spd, final Map<String, String> stdArgs) {
		if (spd.getPropertyFile() != null && spd.getPropertyFile().getProperties() != null) {
			final EList<Simple> simples = spd.getPropertyFile().getProperties().getSimple();
			if (stdArgs.containsKey(ScaDebugLaunchConstants.ARG_EXEC_PARAMS)) {
				return stdArgs.get(ScaDebugLaunchConstants.ARG_EXEC_PARAMS);
			} else {
				final Map<String, Object> execParams = new HashMap<String, Object>();
				for (final Simple simple : simples) {
					if (simple.isKind(PropertyConfigurationType.EXECPARAM) && simple.getValue() != null) {
						execParams.put(simple.getId(), simple.getValue());
					}
				}
				return ScaLauncherUtil.createExecParamString(execParams);
			}
		}
		return "";
	}

	public static String createExecParamString(final Map<String, ? > params) {
		if (params.isEmpty()) {
			return "";
		}
		final StringBuilder builder = new StringBuilder();
		for (final Map.Entry<String, ? > entry : params.entrySet()) {
			if (entry.getValue() != null) {
				builder.append(" ");
				builder.append(entry.getKey());
				builder.append(" \"");
				builder.append(entry.getValue());
				builder.append("\" ");
			}
		}
		return builder.toString();
	}

	public static Map<String, String> getImplementationMap(final ILaunchConfiguration config) throws CoreException {
		if (config == null) {
			return Collections.emptyMap();
		}
		return config.getAttribute(ScaDebugLaunchConstants.ATT_LW_IMPLS, Collections.emptyMap());
	}
}
