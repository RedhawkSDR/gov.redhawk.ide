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
import gov.redhawk.model.sca.IRefreshable;
import gov.redhawk.model.sca.ProfileObjectWrapper;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.model.sca.ScaAbstractProperty;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.ScaFactory;
import gov.redhawk.model.sca.ScaPropertyContainer;
import gov.redhawk.model.sca.ScaService;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.model.sca.commands.ScaModelCommandWithResult;
import gov.redhawk.sca.launch.ScaLaunchConfigurationConstants;
import gov.redhawk.sca.launch.ScaLaunchConfigurationUtil;
import gov.redhawk.sca.util.Debug;
import gov.redhawk.sca.util.ORBUtil;
import gov.redhawk.sca.util.OrbSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mil.jpeojtrs.sca.prf.util.PropertiesUtil;
import mil.jpeojtrs.sca.scd.ComponentType;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.NamedThreadFactory;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.omg.CORBA.SystemException;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import CF.ResourceHelper;
import CF.ResourceOperations;
import CF.PropertySetPackage.InvalidConfiguration;
import CF.PropertySetPackage.PartialConfiguration;
import CF.ResourcePackage.StartError;

/**
 * A collection of utility methods that help to launch a {@link SoftPkg}.
 *
 * @since 4.0
 */
public final class SpdLauncherUtil {

	private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(new NamedThreadFactory(SpdLauncherUtil.class.getName()));
	private static final Debug DEBUG_ARGS = new Debug(ScaDebugPlugin.getInstance(), "LauncherArgs");

	private SpdLauncherUtil() {

	}

	/**
	 * Waits for a {@link SoftPkg} to finish launching locally, performs some model updates, and then performs
	 * initial tasks like configure(), start().
	 *
	 * @param spd The {@link SoftPkg} profile that was launched
	 * @param configuration The launch configuration
	 * @param mode The launch mode (run/debug)
	 * @param launch The launch object
	 * @param monitor A progress monitor
	 * @throws CoreException
	 */
	public static void postLaunch(final SoftPkg spd, final ILaunchConfiguration configuration, final String mode, final ILaunch launch,
		final IProgressMonitor monitor) throws CoreException {
		final ComponentType type;
		if (spd.getDescriptor() == null || spd.getDescriptor().getComponent() == null) {
			String errorMsg = String.format("Unable to determine the component type for %s during post-launch in the sandbox.", spd.getName());
			ScaDebugPlugin.logWarning(errorMsg, null);
			type = ComponentType.OTHER;
		} else {
			type = SoftwareComponent.Util.getWellKnownComponentType(spd.getDescriptor().getComponent());
		}

		// Do an initial wait to give the resource an opportunity to start
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// PASS
		}

		LocalAbstractComponent comp = null;
		try {
			switch (type) {
			case DEVICE:
				comp = SpdLauncherUtil.postLaunchDevice(launch);
				break;
			case EVENT_SERVICE:
			case SERVICE:
				comp = SpdLauncherUtil.postLaunchService(launch);
				break;
			case RESOURCE:
				comp = SpdLauncherUtil.postLaunchComponent(launch);
				break;
			default:
				String errorMsg = String.format("Unsupported component type during post-launch in the sandbox (%s) - treating as component", spd.getName());
				ScaDebugPlugin.logWarning(errorMsg, null);
				comp = SpdLauncherUtil.postLaunchComponent(launch);
				break;
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

		final String execString = launch.getAttribute(LaunchVariables.EXEC_PARAMS);
		final String implID = launch.getLaunchConfiguration().getAttribute(ScaDebugLaunchConstants.ATT_IMPL_ID, (String) null);
		final boolean autoStart = launch.getLaunchConfiguration().getAttribute(ScaLaunchConfigurationConstants.ATT_START,
			ScaLaunchConfigurationConstants.DEFAULT_VALUE_ATT_START);

		// Update the model with information about the details of the launch
		final LocalAbstractComponent newComponent = comp;
		ScaModelCommand.execute(newComponent, new ScaModelCommand() {

			@Override
			public void execute() {
				newComponent.setExecParam(execString);
				newComponent.setImplementationID(implID);
				newComponent.setMode(launch.getLaunchMode());
				newComponent.setLaunch(launch);
				((ProfileObjectWrapper< ? >) newComponent).setProfileURI(spd.eResource().getURI());
			}
		});

		// Fetch profile object, if applicable
		if (newComponent instanceof ProfileObjectWrapper< ? >) {
			((ProfileObjectWrapper< ? >) newComponent).fetchProfileObject(null);
		}

		// Refresh the model object, if applicable
		if (newComponent instanceof IRefreshable) {
			try {
				((IRefreshable) newComponent).refresh(null, RefreshDepth.FULL);
			} catch (InterruptedException e) {
				// PASS
			}
		}

		// Perform configure for properties as needed
		if (newComponent instanceof ScaPropertyContainer< ? , ? >) {
			final ScaPropertyContainer< ? , ? > scaComp = (ScaPropertyContainer< ? , ? >) newComponent;
			final ScaComponent tmp = ScaFactory.eINSTANCE.createScaComponent();
			if (scaComp.getProfileObj() instanceof SoftPkg) {
				tmp.setProfileObj((SoftPkg) scaComp.getProfileObj());
			}
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

		// Start the component, if requested
		if (newComponent instanceof ResourceOperations) {
			if (autoStart) {
				try {
					((ResourceOperations) newComponent).start();
				} catch (final StartError e) {
					// PASS
				}
			}
		}

		// Perform a full refresh of the object, if applicable
		if (newComponent instanceof IRefreshable) {
			try {
				((IRefreshable) newComponent).refresh(null, RefreshDepth.FULL);
			} catch (InterruptedException e) {
				// PASS
			}
		}
	}

	/**
	 * Locates a component that was just launched locally. The naming context / name binding from the launch are used
	 * to resolve an object reference, which is then matched against the CORBA object of a component in the sandbox or
	 * a chalkboard waveform.
	 *
	 * @param launch The launch that just occurred
	 * @return The newly launched component
	 * @throws CoreException
	 */
	private static LocalAbstractComponent postLaunchComponent(final ILaunch launch) throws CoreException {
		final String nameBinding = launch.getAttribute(LaunchVariables.NAME_BINDING);
		final String namingContextIOR = launch.getAttribute(LaunchVariables.NAMING_CONTEXT_IOR);
		final String compID = launch.getAttribute(LaunchVariables.COMPONENT_IDENTIFIER);
		final LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca(null);

		if (nameBinding == null || namingContextIOR == null) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID,
				"No naming context or name binding to locate component with, post launch failed. " + compID, null));
		}

		// Wait for the component to be registered in the appropriate naming context of the naming service
		final Future<LocalScaComponent> future = SpdLauncherUtil.EXECUTOR.submit(new Callable<LocalScaComponent>() {
			@Override
			public LocalScaComponent call() throws Exception {
				OrbSession session = OrbSession.createSession();

				NamingContextExt namingContext = null;
				CF.Resource ref = null;

				try {
					while (namingContext == null && !launch.isTerminated()) {
						try {
							namingContext = NamingContextExtHelper.narrow(session.getOrb().string_to_object(namingContextIOR));
						} catch (SystemException e) {
							// PASS
						}
						if (namingContext == null) {
							try {
								Thread.sleep(100);
							} catch (final InterruptedException e1) {
								throw e1;
							}
						}
					}

					NotifyingNamingContext namingRef = ScaDebugPlugin.getInstance().getLocalSca().getRootContext().findContext(namingContext);

					while (ref == null && !launch.isTerminated()) {
						try {
							ref = ResourceHelper.narrow(namingRef.resolve_str(nameBinding));
						} catch (NotFound e) {
							// PASS
						}
						if (ref == null) {
							try {
								Thread.sleep(100);
							} catch (final InterruptedException e1) {
								throw e1;
							}
						}
					}

					// If this launch was terminated, immediately bail
					while (!launch.isTerminated()) {
						for (ScaComponent comp : localSca.getSandboxWaveform().fetchComponents(null)) {
							if (comp instanceof LocalScaComponent && ref._is_equivalent(comp.getCorbaObj())) {
								return (LocalScaComponent) comp;
							}
						}

						for (final ScaWaveform waveform : localSca.fetchWaveforms(null)) {
							List<ScaComponent> components = ScaModelCommandWithResult.execute(waveform, new ScaModelCommandWithResult<List<ScaComponent>>() {

								@Override
								public void execute() {
									setResult(new ArrayList<ScaComponent>(waveform.getComponents()));
								}
								
							});
							for (ScaComponent comp : components) {
								if (comp instanceof LocalScaComponent && ref._is_equivalent(comp.getCorbaObj())) {
									return (LocalScaComponent) comp;
								}
							}
						}

						Thread.sleep(500);
					}

					throw new EarlyTerminationException("Component terminated while waiting to launch. " + compID, launch);
				} finally {
					if (namingContext != null) {
						ORBUtil.release(namingContext);
						namingContext = null;
					}

					if (ref != null) {
						ORBUtil.release(ref);
						ref = null;
					}

					session.dispose();
				}
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
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Interrupted waiting for component to start. " + compID, e1));
		} catch (final ExecutionException e1) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Error while waiting for component to start. " + compID, e1));
		} catch (final TimeoutException e1) {
			future.cancel(true);
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Timed out waiting for component to start. " + compID, e1));
		}

	}

	/**
	 * Locates a service that was just launched locally. The service name form the launch is matched against services
	 * in the sandbox device manager.
	 *
	 * @param launch The launch that just occurred
	 * @return The newly launched service
	 * @throws CoreException
	 */
	private static LocalAbstractComponent postLaunchService(final ILaunch launch) throws CoreException {
		final String name = launch.getAttribute(LaunchVariables.SERVICE_NAME);
		final LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca(null);

		final Future<LocalAbstractComponent> future = SpdLauncherUtil.EXECUTOR.submit(new Callable<LocalAbstractComponent>() {
			@Override
			public LocalAbstractComponent call() throws Exception {
				LocalAbstractComponent retVal = null;
				while (retVal == null) {
					// If this launch was terminated, immediately bail
					if (launch.isTerminated()) {
						throw new EarlyTerminationException("Service terminated while waiting to launch. " + name, launch);
					}

					for (final ScaService service : localSca.getSandboxDeviceManager().fetchServices(null)) {
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
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Interrupted waiting for service to start. " + name, e1));
		} catch (final ExecutionException e1) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Error while waiting for service to start. " + name, e1));
		} catch (final TimeoutException e1) {
			future.cancel(true);
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Timed out waiting for service to start. " + name, e1));
		}
	}

	/**
	 * Locates a device that was just launched locally. The device label name form the launch is matched against
	 * devices in the sandbox device manager.
	 *
	 * @param launch The launch that just occurred
	 * @return The newly launched service
	 * @throws CoreException
	 */
	private static LocalAbstractComponent postLaunchDevice(final ILaunch launch) throws CoreException {
		final String deviceLabel = launch.getAttribute(LaunchVariables.DEVICE_LABEL);
		final LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca(null);

		final Future<LocalAbstractComponent> future = SpdLauncherUtil.EXECUTOR.submit(new Callable<LocalAbstractComponent>() {
			@Override
			public LocalAbstractComponent call() throws Exception {
				LocalAbstractComponent retVal = null;
				while (retVal == null) {
					// If this launch was terminated, immediately bail
					if (launch.isTerminated()) {
						throw new EarlyTerminationException("Device terminated while waiting to launch. " + deviceLabel, launch);
					}

					for (final ScaDevice< ? > device : localSca.getSandboxDeviceManager().fetchDevices(null)) {
						final String label = device.fetchLabel(null);
						if (deviceLabel.equals(label)) {
							retVal = (LocalAbstractComponent) device;
							break;
						}
					}
					if (retVal == null) {
						try {
							Thread.sleep(100);
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
//				final LocalAbstractComponent newComponent = future.get(timeout, TimeUnit.SECONDS);
				final LocalAbstractComponent newComponent = future.get(600, TimeUnit.DAYS);
				return newComponent;
			}
		} catch (final InterruptedException e1) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Interrupted waiting for device to start. " + deviceLabel, e1));
		} catch (final ExecutionException e1) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Error while waiting for component to start. " + deviceLabel, e1));
		} catch (final TimeoutException e1) {
			future.cancel(true);
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Timed out waiting for component to start. " + deviceLabel, e1));
		}

	}

	private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{((\\w+)(:(\\w+))?)\\}");

	/**
	 * Finds and expands known variable references in the command-line args of a launch. Which variables get expanded
	 * depends on the {@link ILauncherVariableDesc}s that are registered with the {@link ILauncherVariableRegistry}.
	 *
	 * @param spd The SoftPkg being launched
	 * @param input The initial command-line arguments
	 * @param launch The launch that is about to occur
	 * @param configuration The launch configuration
	 * @return The input command line with variables expanded
	 * @throws CoreException
	 */
	public static String insertProgramArguments(final SoftPkg spd, String input, final ILaunch launch, final ILaunchConfiguration configuration)
		throws CoreException {
		if (input == null || input.trim().length() == 0) {
			final ComponentType type = SoftwareComponent.Util.getWellKnownComponentType(spd.getDescriptor().getComponent());
			input = SpdLauncherUtil.getDefaultProgramArguments(type);
		}
		final StringBuilder builder = new StringBuilder();
		final String args = input;
		final Matcher matcher = SpdLauncherUtil.VARIABLE_PATTERN.matcher(args);
		final ILauncherVariableRegistry registry = ScaDebugPlugin.getInstance().getLauncherVariableRegistry();
		int previousEnd = 0;
		while (matcher.find()) {
			builder.append(args.subSequence(previousEnd, matcher.start()));
			final String var = matcher.group(2);
			final String arg = matcher.group(4);

			final ILauncherVariableDesc desc = registry.getDesc(var);
			if (desc != null) {
				final String value = desc.resolveValue(arg, spd, launch, configuration);
				if (value != null && !value.isEmpty()) {
					builder.append(value);
				} else if (SpdLauncherUtil.DEBUG_ARGS.enabled) {
					SpdLauncherUtil.DEBUG_ARGS.message("Failed to resolve launcher variable: " + desc.getName());
				}
			} else {
				// No replacement leave as is, it will be replaced later by Eclipse dynamic variable resolver
				builder.append(matcher.group());
			}
			previousEnd = matcher.end();
		}
		builder.append(args.substring(previousEnd));

		final String result = builder.toString().trim();
		if (SpdLauncherUtil.DEBUG_ARGS.enabled) {
			SpdLauncherUtil.DEBUG_ARGS.message(result);
		}
		return result;
	}

	/**
	 * Create the default command-line arguments to launch a particular {@link SoftPkg}.
	 *
	 * @param type The {@link ComponentType} of the {@link SoftPkg}.
	 * @return
	 */
	public static String getDefaultProgramArguments(final ComponentType type) {
		if (type == null) {
			return null;
		}
		switch (type) {
		case DEVICE:
			return SpdLauncherUtil.createDefaultDeviceProgramArgs();
		case EVENT_SERVICE:
		case SERVICE:
			return SpdLauncherUtil.createDefaultServiceProgramArgs();
		case RESOURCE:
			return SpdLauncherUtil.createDefaultComponentProgramArgs();
		default:
			String errorMsg = String.format("Unsupported component type (%s) while launching in the sandbox. It will be treated as a component.", type.getName());
			ScaDebugPlugin.logWarning(errorMsg, null);
			return SpdLauncherUtil.createDefaultComponentProgramArgs();
		}
	}

	/**
	 * Create the default command-line arguments for running a REDHAWK component. The arguments will contain variable
	 * references which will be expanded at launch time.
	 *
	 * @return The default command-line arguments
	 */
	private static String createDefaultComponentProgramArgs() {
		final IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
		final StringBuilder retVal = new StringBuilder();
		retVal.append(manager.generateVariableExpression(LaunchVariables.EXEC_PARAMS, null));
		retVal.append(" ");
		retVal.append(LaunchVariables.NAMING_CONTEXT_IOR);
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.NAMING_CONTEXT_IOR, null));
		retVal.append(" ");
		retVal.append(LaunchVariables.PROFILE_NAME);
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.PROFILE_NAME, null));
		retVal.append(" ");
		retVal.append(LaunchVariables.COMPONENT_IDENTIFIER);
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.COMPONENT_IDENTIFIER, null));
		retVal.append(" ");
		retVal.append(LaunchVariables.NAME_BINDING);
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.NAME_BINDING, null));
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.DEBUG_LEVEL, null));
		return retVal.toString();
	}

	/**
	 * Create the default command-line arguments for running a REDHAWK service. The arguments will contain variable
	 * references which will be expanded at launch time.
	 *
	 * @return The default command-line arguments
	 */
	private static String createDefaultServiceProgramArgs() {
		final IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
		final StringBuilder retVal = new StringBuilder();
		retVal.append(manager.generateVariableExpression(LaunchVariables.EXEC_PARAMS, null));
		retVal.append(" ");
		retVal.append(LaunchVariables.DEVICE_MGR_IOR);
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.DEVICE_MGR_IOR, null));
		retVal.append(" ");
		retVal.append(LaunchVariables.SERVICE_NAME);
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.SERVICE_NAME, null));
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.DEBUG_LEVEL, null));
		return retVal.toString();
	}

	/**
	 * Create the default command-line arguments for running a REDHAWK device. The arguments will contain variable
	 * references which will be expanded at launch time.
	 *
	 * @return The default command-line arguments
	 */
	private static String createDefaultDeviceProgramArgs() {
		final IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
		final StringBuilder retVal = new StringBuilder();
		retVal.append(manager.generateVariableExpression(LaunchVariables.EXEC_PARAMS, null));
		retVal.append(" ");

		retVal.append(LaunchVariables.DEVICE_MGR_IOR);
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.DEVICE_MGR_IOR, null));
		retVal.append(" ");

		retVal.append(LaunchVariables.PROFILE_NAME);
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.PROFILE_NAME, null));
		retVal.append(" ");

		retVal.append(LaunchVariables.DEVICE_ID);
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.DEVICE_ID, null));
		retVal.append(" ");

		retVal.append(LaunchVariables.DEVICE_LABEL);
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.DEVICE_LABEL, null));
		retVal.append(" ");

		retVal.append(manager.generateVariableExpression(LaunchVariables.DEBUG_LEVEL, null));
		return retVal.toString();
	}

	public static String createExecParamString(final Map<String, Object> params) {
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

	public static ComponentType getComponentType(final SoftPkg spd) {
		try {
			final ComponentType type = SoftwareComponent.Util.getWellKnownComponentType(spd.getDescriptor().getComponent());
			return type;
		} catch (final Exception e) { // SUPPRESS CHECKSTYLE Type doesn't match any known component type so return null
			return null;
		}

	}

	/**
	 * Loads the {@link SoftPkg} specified in a launch configuration.
	 *
	 * @param configuration The launch configuration
	 * @return The {@link SoftPkg} object
	 * @throws CoreException
	 */
	public static SoftPkg getSpd(final ILaunchConfiguration configuration) throws CoreException {
		final URI spdURI;
		if (configuration.getAttribute(ScaDebugLaunchConstants.ATT_WORKSPACE_PROFILE, ScaDebugLaunchConstants.DEFAULT_ATT_WORKSPACE_PROFILE)) {
			spdURI = URI.createPlatformResourceURI(configuration.getAttribute(ScaLaunchConfigurationConstants.ATT_PROFILE, ""), true);
		} else {
			spdURI = URI.createFileURI(configuration.getAttribute(ScaLaunchConfigurationConstants.ATT_PROFILE, ""));
		}
		final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		return SoftPkg.Util.getSoftPkg(resourceSet.getResource(spdURI, true));
	}

}
