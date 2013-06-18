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
import gov.redhawk.sca.launch.ScaLaunchConfigurationConstants;
import gov.redhawk.sca.launch.ScaLaunchConfigurationUtil;
import gov.redhawk.sca.util.Debug;
import gov.redhawk.sca.util.OrbSession;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mil.jpeojtrs.sca.prf.util.PropertiesUtil;
import mil.jpeojtrs.sca.scd.ComponentType;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.spd.SoftPkg;

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
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.omg.CORBA.SystemException;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import CF.ResourceHelper;
import CF.ResourceOperations;
import CF.PropertySetPackage.InvalidConfiguration;
import CF.PropertySetPackage.PartialConfiguration;
import CF.ResourcePackage.StartError;

/**
 * @since 4.0
 */
public final class SpdLauncherUtil {
	/**
	 * @since 3.0
	 */
	public static final String LAUNCH_ATT_PROGRAM_ARGUMENT_MAP = ScaDebugPlugin.ID + ".programArgumentMap";

	private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
	private static final Debug DEBUG_ARGS = new Debug(ScaDebugPlugin.getInstance(), "LauncherArgs");

	private SpdLauncherUtil() {

	}

	public static void postLaunch(final SoftPkg spd, final ILaunchConfiguration configuration, final String mode, final ILaunch launch,
	        final IProgressMonitor monitor) throws CoreException {
		Map< ? , ? > programArgs = configuration.getAttribute(LAUNCH_ATT_PROGRAM_ARGUMENT_MAP, Collections.EMPTY_MAP);

		LocalAbstractComponent comp = null;
		try {
			if (programArgs.containsKey(LaunchVariables.DEVICE_LABEL)) {
				comp = SpdLauncherUtil.postLaunchDevice(launch);
			} else if (programArgs.containsKey(LaunchVariables.SERVICE_NAME)) {
				comp = SpdLauncherUtil.postLaunchService(launch);
			} else {
				comp = SpdLauncherUtil.postLaunchComponent(launch);
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

		final LocalAbstractComponent newComponent = comp;
		ScaModelCommand.execute(newComponent, new ScaModelCommand() {

			public void execute() {
				newComponent.setExecParam(execString);
				newComponent.setImplementationID(implID);
				newComponent.setMode(launch.getLaunchMode());
				newComponent.setLaunch(launch);
				((ProfileObjectWrapper< ? >) newComponent).setProfileURI(spd.eResource().getURI());
			}
		});
		if (newComponent instanceof ProfileObjectWrapper< ? >) {
			((ProfileObjectWrapper< ? >) newComponent).fetchProfileObject(null);
		}

		if (newComponent instanceof IRefreshable) {
			try {
				((IRefreshable) newComponent).refresh(null, RefreshDepth.FULL);
			} catch (InterruptedException e) {
				// PASS
			}
		}

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

		if (newComponent instanceof ResourceOperations) {
			if (autoStart) {
				try {
					((ResourceOperations) newComponent).start();
				} catch (final StartError e) {
					// PASS
				}
			}
		}

		if (newComponent instanceof IRefreshable) {
			try {
				((IRefreshable) newComponent).refresh(null, RefreshDepth.FULL);
			} catch (InterruptedException e) {
				// PASS
			}
		}
	}

	private static LocalAbstractComponent postLaunchComponent(final ILaunch launch) throws CoreException {
		final String nameBinding = launch.getAttribute(LaunchVariables.NAME_BINDING);
		final String namingContextIOR = launch.getAttribute(LaunchVariables.NAMING_CONTEXT_IOR);
		final LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca();

		if (nameBinding == null || namingContextIOR == null) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID,
			        "No naming context or name binding to locate component with, post launch failed.", null));
		}

		final Future<LocalScaComponent> future = SpdLauncherUtil.EXECUTOR.submit(new Callable<LocalScaComponent>() {
			public LocalScaComponent call() throws Exception {
				LocalScaComponent newComponent = null;
				OrbSession session = OrbSession.createSession();

				try {
					while (newComponent == null) {
						// If this launch was terminated, immediately bail
						if (launch.isTerminated()) {
							throw new Exception("Component terminated while waiting to launch.");
						}

						NamingContextExt namingContext = null;
						CF.Resource ref = null;
						try {
							namingContext = NamingContextExtHelper.narrow(session.getOrb().string_to_object(namingContextIOR));
							ref = ResourceHelper.narrow(namingContext.resolve_str(nameBinding));
							for (final ScaComponent comp : localSca.getSandboxWaveform().getComponents()) {
								if (comp instanceof LocalScaComponent && ref._is_equivalent(comp.getCorbaObj())) {
									newComponent = (LocalScaComponent) comp;
									break;
								}
							}
							if (newComponent == null) {
								for (final ScaWaveform waveform : localSca.getWaveforms()) {
									for (final ScaComponent comp : waveform.getComponents()) {
										if (comp instanceof LocalScaComponent && ref._is_equivalent(comp.getCorbaObj())) {
											newComponent = (LocalScaComponent) comp;
											break;
										}
									}
								}
							}
						} catch (final NotFound e) {
							// PASS
						} catch (final CannotProceed e) {
							// PASS
						} catch (final SystemException e) {
							// PASS
						} finally {
							if (namingContext != null) {
								namingContext._release();
								namingContext = null;
							}
							if (ref != null) {
								ref._release();
								ref = null;
							}
						}
						if (newComponent == null) {
							try {
								Thread.sleep(500);
							} catch (final InterruptedException e) {
								throw e;
							}
						}
					}
				} finally {
					session.dispose();
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

	private static LocalAbstractComponent postLaunchService(final ILaunch launch) throws CoreException {
		final String name = launch.getAttribute(LaunchVariables.SERVICE_NAME);
		final LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca();

		final Future<LocalAbstractComponent> future = SpdLauncherUtil.EXECUTOR.submit(new Callable<LocalAbstractComponent>() {
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

	private static LocalAbstractComponent postLaunchDevice(final ILaunch launch) throws CoreException {
		final String deviceLabel = launch.getAttribute(LaunchVariables.DEVICE_LABEL);
		final LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca();

		final Future<LocalAbstractComponent> future = SpdLauncherUtil.EXECUTOR.submit(new Callable<LocalAbstractComponent>() {
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

	private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{((\\w+)(:(\\w+))?)\\}");

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
		default:
			return SpdLauncherUtil.createDefaultComponentProgramArgs();
		}
	}

	private static String createDefaultComponentProgramArgs() {
		final IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
		final StringBuilder retVal = new StringBuilder();
		retVal.append(manager.generateVariableExpression(LaunchVariables.EXEC_PARAMS, null));
		retVal.append(" ");
		retVal.append(LaunchVariables.NAMING_CONTEXT_IOR);
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.NAMING_CONTEXT_IOR, null));
		retVal.append(" ");
		retVal.append(LaunchVariables.NAME_BINDING);
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.NAME_BINDING, null));
		retVal.append(" ");
		retVal.append(LaunchVariables.COMPONENT_IDENTIFIER);
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.COMPONENT_IDENTIFIER, null));
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.DEBUG_LEVEL, null));
		return retVal.toString();
	}

	private static String createDefaultServiceProgramArgs() {
		final IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
		final StringBuilder retVal = new StringBuilder();
		retVal.append(manager.generateVariableExpression(LaunchVariables.EXEC_PARAMS, null));
		retVal.append(" ");
		retVal.append(LaunchVariables.NAMING_CONTEXT_IOR);
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.NAMING_CONTEXT_IOR, null));
		retVal.append(" ");
		retVal.append(LaunchVariables.NAME_BINDING);
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.NAME_BINDING, null));
		retVal.append(" ");
		retVal.append(LaunchVariables.DEVICE_MGR_IOR);
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.DEVICE_MGR_IOR, null));
		retVal.append(" ");
		retVal.append(LaunchVariables.SERVICE_NAME);
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.SERVICE_NAME, null));
		retVal.append(" ");
		retVal.append(LaunchVariables.COMPONENT_IDENTIFIER);
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.COMPONENT_IDENTIFIER, null));
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.DEBUG_LEVEL, null));
		return retVal.toString();
	}

	private static String createDefaultDeviceProgramArgs() {
		final IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
		final StringBuilder retVal = new StringBuilder();
		retVal.append(manager.generateVariableExpression(LaunchVariables.EXEC_PARAMS, null));
		retVal.append(" ");
		retVal.append(LaunchVariables.NAMING_CONTEXT_IOR);
		retVal.append(" ");
		retVal.append(manager.generateVariableExpression(LaunchVariables.NAMING_CONTEXT_IOR, null));
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
		} catch (final Exception e) {
			return null;
		}

	}

	/**
	 * @since 3.0
	 */
	public static Map<String, String> createMap(String programArguments) {
		String[] split = programArguments.split(" ");
		Map<String, String> retVal = new HashMap<String, String>(split.length / 2);
		for (int i = 0; i + 1 < split.length; i += 2) {
			retVal.put(split[i], split[i + 1]);
		}
		return retVal;
	}

	public static SoftPkg getSpd(final ILaunchConfiguration configuration) throws CoreException {
		final URI spdURI;
		if (configuration.getAttribute(ScaDebugLaunchConstants.ATT_WORKSPACE_PROFILE, ScaDebugLaunchConstants.DEFAULT_ATT_WORKSPACE_PROFILE)) {
			spdURI = URI.createPlatformResourceURI(configuration.getAttribute(ScaLaunchConfigurationConstants.ATT_PROFILE, ""), true);
		} else {
			spdURI = URI.createFileURI(configuration.getAttribute(ScaLaunchConfigurationConstants.ATT_PROFILE, ""));
		}
		final ResourceSet resourceSet = new ResourceSetImpl();
		return SoftPkg.Util.getSoftPkg(resourceSet.getResource(spdURI, true));
	}

}
