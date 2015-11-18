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

import gov.redhawk.ide.debug.internal.jobs.TerminateJob;
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
import gov.redhawk.sca.util.SubMonitor;

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
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TCKind;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import CF.DataType;
import CF.Resource;
import CF.ResourceHelper;
import CF.ResourceOperations;
import CF.LifeCyclePackage.InitializeError;
import CF.PropertyEmitterPackage.AlreadyInitialized;
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
	 * Waits for a {@link SoftPkg} to register with the naming context up to the timeout specified in the launch. Then:
	 * <ol>
	 * <li>Performs initial life cycle (initializeProperties, initialize, configure, start)</li>
	 * <li>Creates a model object and refreshes it</li>
	 * <li>Adds the model object to the model</li>
	 * </ol>
	 * @param spd The {@link SoftPkg} profile that was launched
	 * @param configuration The launch configuration
	 * @param mode The launch mode (run/debug)
	 * @param launch The launch object
	 * @param monitor A progress monitor
	 * @throws CoreException
	 */
	public static void postLaunch(final SoftPkg spd, final ILaunchConfiguration configuration, final String mode, final ILaunch launch,
		final IProgressMonitor monitor) throws CoreException {
		final int WORK_FIND_CORBA_OBJ = 10;
		final int WORK_GENERAL = 1;
		SubMonitor progress = SubMonitor.convert(monitor, "Post-launch tasks", WORK_FIND_CORBA_OBJ + 7 * WORK_GENERAL);

		final ComponentType type;
		if (spd.getDescriptor() == null || spd.getDescriptor().getComponent() == null) {
			String errorMsg = String.format("Unable to determine the component type for %s during post-launch in the sandbox.", spd.getName());
			ScaDebugPlugin.logWarning(errorMsg, null);
			type = ComponentType.OTHER;
		} else {
			type = SoftwareComponent.Util.getWellKnownComponentType(spd.getDescriptor().getComponent());
		}

		LocalAbstractComponent comp = null;
		try {
			switch (type) {
			case DEVICE:
				comp = SpdLauncherUtil.postLaunchDevice(launch, progress.newChild(WORK_FIND_CORBA_OBJ));
				break;
			case EVENT_SERVICE:
			case SERVICE:
				comp = SpdLauncherUtil.postLaunchService(launch, progress.newChild(WORK_FIND_CORBA_OBJ));
				break;
			case RESOURCE:
				comp = SpdLauncherUtil.postLaunchComponent(spd, launch, progress.newChild(WORK_FIND_CORBA_OBJ));
				break;
			default:
				String errorMsg = String.format("Unsupported component type during post-launch in the sandbox (%s) - treating as component", spd.getName());
				ScaDebugPlugin.logWarning(errorMsg, null);
				comp = SpdLauncherUtil.postLaunchComponent(spd, launch, progress.newChild(WORK_FIND_CORBA_OBJ));
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

		// Update the model with information about the details of the launch
		final LocalAbstractComponent newComponent = comp;
		final String execString = launch.getAttribute(LaunchVariables.EXEC_PARAMS);
		final String implID = launch.getLaunchConfiguration().getAttribute(ScaDebugLaunchConstants.ATT_IMPL_ID, (String) null);
		ScaModelCommand.execute(newComponent, new ScaModelCommand() {
			@Override
			public void execute() {
				newComponent.setExecParam(execString);
				newComponent.setImplementationID(implID);
				newComponent.setMode(launch.getLaunchMode());
				newComponent.setLaunch(launch);
			}
		});
		progress.worked(WORK_GENERAL);

		// Set the profile object, if applicable
		if (newComponent instanceof ProfileObjectWrapper< ? >) {
			final ProfileObjectWrapper< ? > obj = (ProfileObjectWrapper< ? >) newComponent;
			ScaModelCommand.execute(newComponent, new ScaModelCommand() {
				@Override
				public void execute() {
					obj.setProfileURI(spd.eResource().getURI());
				}
			});
			obj.fetchProfileObject(progress.newChild(WORK_GENERAL));
		} else {
			progress.notWorked(WORK_GENERAL);
		}

		// Perform configure for properties as needed
		if (newComponent instanceof ScaPropertyContainer< ? , ? >) {
			final ScaPropertyContainer< ? , ? > scaComp = (ScaPropertyContainer< ? , ? >) newComponent;

			// Load the properties from the PRF and override with values from the launch configuration
			final ScaComponent propHolder = ScaFactory.eINSTANCE.createScaComponent();
			propHolder.setProfileURI(spd.eResource().getURI());
			propHolder.fetchProfileObject(progress.newChild(WORK_GENERAL));
			propHolder.fetchProperties(progress.newChild(WORK_GENERAL));
			ScaLaunchConfigurationUtil.loadProperties(launch.getLaunchConfiguration(), propHolder);

			// Find configurable properties that aren't set to their default
			List<DataType> configureProps = new ArrayList<DataType>();
			for (final ScaAbstractProperty< ? > prop : propHolder.getProperties()) {
				if (!prop.isDefaultValue() && PropertiesUtil.canConfigure(prop.getDefinition())) {
					configureProps.add(new DataType(prop.getId(), prop.toAny()));
				}
			}

			// Configure properties
			try {
				scaComp.configure(configureProps.toArray(new DataType[configureProps.size()]));
			} catch (InvalidConfiguration | PartialConfiguration e) {
				ScaDebugPlugin.logError("Error while configuring properties", e);
			}
			progress.worked(WORK_GENERAL);
		} else {
			progress.notWorked(3 * WORK_GENERAL);
		}

		// Start the component, if requested
		final boolean autoStart = launch.getLaunchConfiguration().getAttribute(ScaLaunchConfigurationConstants.ATT_START,
			ScaLaunchConfigurationConstants.DEFAULT_VALUE_ATT_START);
		if (newComponent instanceof ResourceOperations) {
			if (autoStart) {
				try {
					((ResourceOperations) newComponent).start();
				} catch (final StartError e) {
					// PASS
				}
				progress.worked(WORK_GENERAL);
			} else {
				progress.notWorked(WORK_GENERAL);
			}
		} else {
			progress.notWorked(WORK_GENERAL);
		}

		// Perform a full refresh of the object, if applicable
		if (newComponent instanceof IRefreshable) {
			try {
				((IRefreshable) newComponent).refresh(progress.newChild(WORK_GENERAL), RefreshDepth.FULL);
			} catch (InterruptedException e) {
				// PASS
			}
		} else {
			progress.notWorked(WORK_GENERAL);
		}
	}

	/**
	 * Uses the naming context and name binding from the launch to resolve a reference to the component. The
	 * {@link Resource#initializeProperties(DataType[])} and {@link Resource#initialize()} calls are performed and
	 * a new model object is added to the model.
	 * @param spd The {@link SoftPkg} for the component being launched
	 * @param launch The launch that just occurred
	 * @param monitor A progress monitor
	 * @return A new model object for the component
	 * @throws CoreException
	 */
	private static LocalAbstractComponent postLaunchComponent(SoftPkg spd, final ILaunch launch, final IProgressMonitor monitor) throws CoreException {
		final int WORK_WAIT_REGISTRATION = 10;
		final int WORK_GENERAL = 1, WORK_INITIALIZE_PROPS = 1, WORK_INITIALIZE = 1, WORK_ADD_TO_MODEL = 1;
		SubMonitor progress = SubMonitor.convert(monitor, "Wait for component to register",
			WORK_WAIT_REGISTRATION + 2 * WORK_GENERAL + WORK_INITIALIZE_PROPS + WORK_INITIALIZE + WORK_ADD_TO_MODEL);

		final String nameBinding = launch.getAttribute(LaunchVariables.NAME_BINDING);
		final String namingContextIOR = launch.getAttribute(LaunchVariables.NAMING_CONTEXT_IOR);
		final String compID = launch.getAttribute(LaunchVariables.COMPONENT_IDENTIFIER);

		if (nameBinding == null || namingContextIOR == null) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID,
				"No naming context or name binding to locate component with, post launch failed. " + compID, null));
		}

		// Wait for the component to be registered in the appropriate naming context of the naming service
		final Future<Resource> future = SpdLauncherUtil.EXECUTOR.submit(new Callable<Resource>() {
			@Override
			public Resource call() throws Exception {
				OrbSession session = OrbSession.createSession();

				NamingContextExt namingContext = null;

				try {
					// Resolve the naming context
					while (namingContext == null) {
						if (launch.isTerminated()) {
							throw new EarlyTerminationException("Component terminated while waiting to launch. " + compID, launch);
						}

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

					// TODO: With the NotifyingNamingContext, we could have direct EMF notification of the
					// registration rather than polling:
					// NotifyingNamingContext namingRef =
					// ScaDebugPlugin.getInstance().getLocalSca().getRootContext().findContext(namingContext);
					while (!launch.isTerminated()) {
						try {
							return ResourceHelper.narrow(namingContext.resolve_str(nameBinding));
						} catch (NotFound e) {
							try {
								Thread.sleep(100);
							} catch (final InterruptedException e1) {
								throw e1;
							}
						}
					}

					throw new EarlyTerminationException("Component terminated while waiting to launch. " + compID, launch);
				} finally {
					if (namingContext != null) {
						ORBUtil.release(namingContext);
						namingContext = null;
					}
					session.dispose();
				}
			}

		});

		Resource resource;
		try {
			// Invoke the Future to get the parent waveform and the Resource object
			final int timeout = launch.getLaunchConfiguration().getAttribute(ScaDebugLaunchConstants.ATT_LAUNCH_TIMEOUT,
				ScaDebugLaunchConstants.DEFAULT_ATT_LAUNCH_TIMEOUT);
			if (timeout < 0 || ILaunchManager.DEBUG_MODE.equals(launch.getLaunchMode())) {
				// In debug-mode wait they may have placed a break-point
				// that is delaying registration so wait forever
				resource = future.get();
			} else {
				resource = future.get(timeout, TimeUnit.SECONDS);
			}
			progress.worked(WORK_WAIT_REGISTRATION);
		} catch (final InterruptedException ex) {
			String msg = String.format("Interrupted waiting for component %s to launch.", nameBinding);
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, msg, ex));
		} catch (final ExecutionException ex) {
			String msg = String.format("Error while waiting for component %s to launch.", nameBinding);
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, msg, ex.getCause()));
		} catch (final TimeoutException ex) {
			future.cancel(true);
			String msg = String.format("Timed out waiting for component %s to launch.", nameBinding);
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, msg, ex));
		}

		// Create the model object
		final LocalScaComponent component = ScaDebugFactory.eINSTANCE.createLocalScaComponent();
		component.setCorbaObj(resource);
		component.setName(nameBinding);
		component.setProfileURI(spd.eResource().getURI());

		// Load the properties from the PRF and override with values from the launch configuration
		final ScaComponent propHolder = ScaFactory.eINSTANCE.createScaComponent();
		propHolder.setProfileURI(spd.eResource().getURI());
		propHolder.fetchProfileObject(progress.newChild(WORK_GENERAL));
		propHolder.fetchProperties(progress.newChild(WORK_GENERAL));
		ScaLaunchConfigurationUtil.loadProperties(launch.getLaunchConfiguration(), propHolder);

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
		try {
			DataType[] initializePropsArray = initializeProps.toArray(new CF.DataType[initializeProps.size()]);
			component.initializeProperties(initializePropsArray);
		} catch (AlreadyInitialized | InvalidConfiguration | PartialConfiguration e) {
			ScaDebugPlugin.logError("Error while initializing properties", e);
		} catch (BAD_OPERATION e) {
			if (initializeProps.size() == 0) {
				String msg = "Could not call initializeProperties on component %s in the sandbox. "
					+ "If the installed version of REDHAWK is pre-2.0, this is expected and can be ignored.";
				ScaDebugPlugin.logWarning(String.format(msg, compID), e);
			} else {
				ScaDebugPlugin.logError("Component has properties of kind 'property', but does not appear to support REDHAWK 2.0 API", e);
			}
		}
		progress.newChild(WORK_INITIALIZE_PROPS);

		// Initialize
		try {
			component.initialize();
		} catch (InitializeError e) {
			ScaDebugPlugin.logError("Error while initializing the component", e);
		}
		progress.newChild(WORK_INITIALIZE);

		// Add the component to its waveform
		final String parentWaveformName = launch.getLaunchConfiguration().getAttribute(LaunchVariables.WAVEFORM_NAME, "");
		if (parentWaveformName.isEmpty()) {
			final ScaWaveform sandboxWaveform = ScaDebugPlugin.getInstance().getLocalSca().getSandboxWaveform();
			ScaModelCommand.execute(sandboxWaveform, new ScaModelCommand() {
				@Override
				public void execute() {
					sandboxWaveform.getComponents().add(component);
				}
			});
		} else {
			Boolean result = ScaModelCommandWithResult.execute(ScaDebugPlugin.getInstance().getLocalSca(), new ScaModelCommandWithResult<Boolean>() {
				@Override
				public void execute() {
					for (ScaWaveform waveform : ScaDebugPlugin.getInstance().getLocalSca().getWaveforms()) {
						if (parentWaveformName.equals(waveform.getName())) {
							waveform.getComponents().add(component);
							setResult(true);
							return;
						}
					}
					setResult(false);
				}
			});
			if (result == null || !result) {
				throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Unable to find the parent waveform for a launched component"));
			}
		}
		progress.newChild(WORK_ADD_TO_MODEL);

		return component;
	}

	/**
	 * Locates a service that was just launched locally. The service name form the launch is matched against services
	 * in the sandbox device manager.
	 *
	 * @param launch The launch that just occurred
	 * @param monitor A progress monitor
	 * @return The newly launched service
	 * @throws CoreException
	 */
	private static LocalAbstractComponent postLaunchService(final ILaunch launch, final IProgressMonitor monitor) throws CoreException {
		SubMonitor progress = SubMonitor.convert(monitor, "Wait for service to register", 1);
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

					for (final ScaService service : localSca.getSandboxDeviceManager().fetchServices(null, RefreshDepth.SELF)) {
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
		} finally {
			progress.done();
		}
	}

	/**
	 * Locates a device that was just launched locally. The device label name form the launch is matched against
	 * devices in the sandbox device manager.
	 *
	 * @param launch The launch that just occurred
	 * @param monitor A progress monitor
	 * @return The newly launched service
	 * @throws CoreException
	 */
	private static LocalAbstractComponent postLaunchDevice(final ILaunch launch, final IProgressMonitor monitor) throws CoreException {
		SubMonitor progress = SubMonitor.convert(monitor, "Wait for device to register", 1);
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

					for (final ScaDevice< ? > device : localSca.getSandboxDeviceManager().fetchDevices(null, RefreshDepth.SELF)) {
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
				final LocalAbstractComponent newComponent = future.get(timeout, TimeUnit.SECONDS);
				return newComponent;
			}
		} catch (final InterruptedException e1) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Interrupted waiting for device to start. " + deviceLabel, e1));
		} catch (final ExecutionException e1) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Error while waiting for component to start. " + deviceLabel, e1));
		} catch (final TimeoutException e1) {
			future.cancel(true);
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Timed out waiting for component to start. " + deviceLabel, e1));
		} finally {
			progress.done();
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
			String errorMsg = String.format("Unsupported component type (%s) while launching in the sandbox. It will be treated as a component.",
				type.getName());
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
		retVal.append(LaunchVariables.COMPONENT_IDENTIFIER);
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.COMPONENT_IDENTIFIER, null));
		retVal.append(" ");
		retVal.append(LaunchVariables.NAME_BINDING);
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.NAME_BINDING, null));
		retVal.append(" ");
		retVal.append(LaunchVariables.PROFILE_NAME);
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.PROFILE_NAME, null));
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.DEBUG_LEVEL, null));
		retVal.append(" ");
		retVal.append(LaunchVariables.NAMING_CONTEXT_IOR);
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.NAMING_CONTEXT_IOR, null));
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
		retVal.append(LaunchVariables.SERVICE_NAME);
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.SERVICE_NAME, null));
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.DEBUG_LEVEL, null));
		retVal.append(" ");
		retVal.append(LaunchVariables.DEVICE_MGR_IOR);
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.DEVICE_MGR_IOR, null));
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
		retVal.append(" ");

		retVal.append(LaunchVariables.DEVICE_MGR_IOR);
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.DEVICE_MGR_IOR, null));

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
		final URI spdURI = ScaLaunchConfigurationUtil.getProfileURI(configuration);
		final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		return SoftPkg.Util.getSoftPkg(resourceSet.getResource(spdURI, true));
	}

	/**
	 * @since 8.0
	 */
	public static void terminate(final LocalLaunch localLaunch) {
		ILaunch launch = localLaunch.getLaunch();
		Job job = new TerminateJob(launch, launch.getLaunchConfiguration().getName());
		job.setUser(true);
		job.schedule();
	}

}
