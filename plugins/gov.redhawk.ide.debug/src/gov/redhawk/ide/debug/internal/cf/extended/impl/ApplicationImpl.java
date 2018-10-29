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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.jacorb.naming.Name;
import org.omg.CORBA.IntHolder;
import org.omg.CORBA.SystemException;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import CF.Application;
import CF.ApplicationOperations;
import CF.ComponentEnumType;
import CF.ComponentType;
import CF.DataType;
import CF.DeviceAssignmentType;
import CF.ErrorNumberType;
import CF.InvalidIdentifier;
import CF.InvalidObjectReference;
import CF.LifeCycleOperations;
import CF.LogEvent;
import CF.PortType;
import CF.PropertiesHolder;
import CF.Resource;
import CF.TestableObjectOperations;
import CF.UnknownIdentifier;
import CF.UnknownProperties;
import CF.ApplicationPackage.ComponentElementType;
import CF.ApplicationPackage.ComponentProcessIdType;
import CF.ApplicationPackage.InvalidMetric;
import CF.ExecutableDevicePackage.ExecuteFail;
import CF.LifeCyclePackage.InitializeError;
import CF.LifeCyclePackage.ReleaseError;
import CF.PortPackage.InvalidPort;
import CF.PortPackage.OccupiedPort;
import CF.PortSetPackage.PortInfoType;
import CF.PortSupplierPackage.UnknownPort;
import CF.PropertyEmitterPackage.AlreadyInitialized;
import CF.PropertySetPackage.InvalidConfiguration;
import CF.PropertySetPackage.PartialConfiguration;
import CF.ResourcePackage.StartError;
import CF.ResourcePackage.StopError;
import CF.TestableObjectPackage.UnknownTest;
import gov.redhawk.ide.debug.ILaunchConfigurationFactory;
import gov.redhawk.ide.debug.LocalLaunch;
import gov.redhawk.ide.debug.LocalScaComponent;
import gov.redhawk.ide.debug.LocalScaExecutableDevice;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.NotifyingNamingContext;
import gov.redhawk.ide.debug.ScaDebugLaunchConstants;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.internal.ApplicationStreams;
import gov.redhawk.ide.debug.internal.IComponentLaunch;
import gov.redhawk.ide.debug.internal.LocalApplicationFactory;
import gov.redhawk.ide.debug.internal.ScaDebugInstance;
import gov.redhawk.ide.debug.variables.LaunchVariables;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.model.sca.ScaAbstractComponent;
import gov.redhawk.model.sca.ScaAbstractProperty;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaConnection;
import gov.redhawk.model.sca.ScaFactory;
import gov.redhawk.model.sca.ScaPort;
import gov.redhawk.model.sca.ScaUsesPort;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.model.sca.commands.ScaModelCommandWithResult;
import gov.redhawk.model.sca.impl.ScaComponentImpl;
import gov.redhawk.sca.efs.WrappedFileStore;
import gov.redhawk.sca.launch.ScaLaunchConfigurationUtil;
import gov.redhawk.sca.util.SubMonitor;
import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.sad.ExternalPorts;
import mil.jpeojtrs.sca.sad.ExternalProperties;
import mil.jpeojtrs.sca.sad.ExternalProperty;
import mil.jpeojtrs.sca.sad.Option;
import mil.jpeojtrs.sca.sad.Options;
import mil.jpeojtrs.sca.sad.Port;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.sad.util.StartOrderComparator;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.CFErrorFormatter;
import mil.jpeojtrs.sca.util.CorbaUtils;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

public class ApplicationImpl extends PlatformObject implements IProcess, ApplicationOperations, IAdaptable {

	private static interface ConnectionInfo {
		String getConnectionID();

		void disconnect();

		void reconnect(final ScaWaveform waveform) throws InvalidPort, OccupiedPort;
	}

	private class ScaComponentComparator implements Comparator<ScaComponent> {

		private StartOrderComparator innerCompatator;

		public ScaComponentComparator() {
			if (assemblyController == null) {
				innerCompatator = new StartOrderComparator(null);
			} else {
				innerCompatator = new StartOrderComparator(assemblyController.getComponentInstantiation());
			}
		}

		/**
		 * Compare on the start order as the first priority, if no start order is found, compare on the pointer
		 * location.
		 */
		@Override
		public int compare(ScaComponent o1, ScaComponent o2) {
			if (o1 == null && o2 == null) {
				return 0;
			} else if (o1 != null && o2 == null) {
				return -1;
			} else if (o1 == null && o2 != null) {
				return 1;
			}

			SadComponentInstantiation ci1 = o1.getComponentInstantiation();
			SadComponentInstantiation ci2 = o2.getComponentInstantiation();
			return innerCompatator.compare(ci1, ci2);
		}
	}

	private class FromConnectionInfo implements ConnectionInfo {
		private final String connectionID;
		private final ScaUsesPort port;
		private final String targetCompID;
		private final String targetPort;
		private final ScaConnection connection;

		public FromConnectionInfo(final ScaUsesPort from, final String targetCompID, final String targetPort, final ScaConnection currentConnection) {
			super();
			this.port = from;
			this.targetCompID = targetCompID;
			this.targetPort = targetPort;
			this.connectionID = currentConnection.getId();
			this.connection = currentConnection;
		}

		@Override
		public void disconnect() {
			try {
				this.connection.getPort().disconnectPort(this.connection);
			} catch (final InvalidPort e) {
				String msg = Messages.bind(Messages.ApplicationImpl_ProblemDisconnecting, this.connectionID);
				String resourceDesc = Messages.bind(Messages.ApplicationImpl_Connection, this.connectionID);
				ApplicationImpl.this.streams.getErrStream().println(msg);
				ApplicationImpl.this.streams.getErrStream().println(CFErrorFormatter.format(e, resourceDesc));
			}
		}

		@Override
		public void reconnect(final ScaWaveform waveform) throws InvalidPort, OccupiedPort {
			for (final ScaComponent comp : waveform.getComponentsCopy()) {
				if (comp.getInstantiationIdentifier().equals(this.targetCompID)) {
					if (this.targetPort == null) {
						this.port.connectPort(comp.getCorbaObj(), this.connectionID);
						return;
					}
				}
			}
		}

		@Override
		public String getConnectionID() {
			return this.connectionID;
		}
	}

	private class ToConnectionInfo implements ConnectionInfo {
		private final String connectionID;
		private final String sourceCompID;
		private final String sourcePort;
		private final org.omg.CORBA.Object target;
		private final ScaConnection connection;

		public ToConnectionInfo(final String sourceCompID, final String sourcePort, final org.omg.CORBA.Object target, final ScaConnection connection) {
			super();
			this.sourceCompID = sourceCompID;
			this.sourcePort = sourcePort;
			this.target = target;
			this.connectionID = connection.getId();
			this.connection = connection;
		}

		@Override
		public void reconnect(final ScaWaveform waveform) throws InvalidPort, OccupiedPort {
			for (final ScaComponent comp : waveform.getComponentsCopy()) {
				if (comp.getInstantiationIdentifier().equals(this.sourceCompID)) {
					final ScaPort< ? , ? > port = comp.getScaPort(this.sourcePort);
					if (port instanceof ScaUsesPort) {
						final ScaUsesPort uses = (ScaUsesPort) port;
						uses.connectPort(this.target, this.connectionID);
						return;
					}
				}
			}
		}

		@Override
		public void disconnect() {
			try {
				this.connection.getPort().disconnectPort(this.connection);
			} catch (final InvalidPort e) {
				String msg = Messages.bind(Messages.ApplicationImpl_ProblemDisconnecting, this.connectionID);
				String resourceDesc = Messages.bind(Messages.ApplicationImpl_Connection, this.connectionID);
				ApplicationImpl.this.streams.getErrStream().println(msg);
				ApplicationImpl.this.streams.getErrStream().println(CFErrorFormatter.format(e, resourceDesc));
			}
		}

		@Override
		public String getConnectionID() {
			return this.connectionID;
		}
	}

	private static final EStructuralFeature[] ASSEMBLY_ID_PATH = new EStructuralFeature[] { SadPackage.Literals.SOFTWARE_ASSEMBLY__ASSEMBLY_CONTROLLER,
		SadPackage.Literals.ASSEMBLY_CONTROLLER__COMPONENT_INSTANTIATION_REF, PartitioningPackage.Literals.COMPONENT_INSTANTIATION_REF__REFID };
	private static final float DEFAULT_STOP_TIMEOUT = 3.0f;
	private static final float RELEASE_TIMEOUT = 3.0f;

	private LocalScaComponent assemblyController;
	private NotifyingNamingContext waveformContext;
	private final ApplicationStreams streams = new ApplicationStreams();
	private boolean terminated;
	private final ILaunch parentLaunch;
	private LocalScaWaveform waveform;

	/**
	 * @see CF.ResourceOperations#identifier()
	 */
	private final String identifier;

	/**
	 * @see CF.ResourceOperations#started()
	 */
	private boolean started;

	/**
	 * @see CF.ResourceOperations#softwareProfile()
	 * @see CF.ApplicationOperations#profile()
	 */
	private final String profile;

	/**
	 * @see CF.ApplicationOperations#name()
	 */
	private final String name;

	/**
	 * @see CF.ApplicationOperations#aware()
	 */
	private boolean aware = false;

	/**
	 * @see CF.ApplicationOperations#stopTimeout()
	 */
	private float stopTimeout = DEFAULT_STOP_TIMEOUT;

	private final Application delegate;
	private volatile boolean launching;
	private boolean isSandboxChalkboard;

	public ApplicationImpl(final LocalScaWaveform waveform, final String identifier, final String name, Application delegate) {
		this.name = name;
		this.waveformContext = waveform.getNamingContext();
		this.identifier = identifier;
		this.parentLaunch = waveform.getLaunch();
		this.profile = waveform.getProfile();
		this.waveform = waveform;
		this.delegate = delegate;

		if (waveform.getProfileObj() != null) {
			Options options = waveform.getProfileObj().getOptions();
			if (options != null) {
				for (Option option : options.getOption()) {
					if (option.getName() == null) {
						continue;
					}
					switch (option.getName()) {
					case "STOP_TIMEOUT":
						try {
							stopTimeout = Float.parseFloat(option.getValue());
						} catch (NumberFormatException e) {
							this.streams.getErrStream().println("Invalid stop timeout in SAD file (not a number)");
						}
						break;
					default:
						break;
					}
				}
			}
		}

		// Is this the sandbox chalkboard?
		isSandboxChalkboard = ScaDebugInstance.getLocalSandboxWaveformURI().equals(waveform.getProfileURI());
	}

	public ApplicationImpl(final LocalScaWaveform waveform, final String identifier, final String name) {
		this(waveform, identifier, name, null);
	}

	public static String getAssemblyControllerID(final SoftwareAssembly profileObj) {
		return ScaEcoreUtils.getFeature(profileObj, ApplicationImpl.ASSEMBLY_ID_PATH);
	}

	public LocalScaWaveform getLocalWaveform() {
		return this.waveform;
	}

	public NamingContextExt getWaveformContext() {
		return this.waveformContext.getNamingContext();
	}

	@Override
	public String identifier() {
		return this.identifier;
	}

	@Override
	public boolean started() {
		try {
			waitOnLaunch();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}

		// Defer to the delegate first
		if (this.delegate != null) {
			return this.delegate.started();
		}

		// Are there any local components?
		Boolean empty = ScaModelCommandWithResult.runExclusive(waveform, () -> waveform.getComponents().isEmpty());

		// If there are no local components, and no delegate, we aren't started any more
		if (Boolean.TRUE.equals(empty)) {
			started = false;
		}

		return started;
	}

	public ApplicationStreams getStreams() {
		return this.streams;
	}

	@Override
	public void start() throws StartError {
		try {
			waitOnLaunch();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new StartError(ErrorNumberType.CF_EINTR, Messages.ApplicationImpl_InterruptedWaitingForAppLaunch);
		}

		this.streams.getOutStream().println(Messages.ApplicationImpl_Starting);

		if (this.delegate != null) {
			this.streams.getOutStream().println(Messages.ApplicationImpl_DelegateStarting);
			try {
				this.delegate.start();
			} catch (StartError e) {
				String resourceDesc = Messages.bind(Messages.ApplicationImpl_Component, this.name);
				this.streams.getErrStream().println(CFErrorFormatter.format(e, resourceDesc));
				throw e;
			}
		}

		List<ScaComponent> compsToStart = waveform.getComponentsCopy().stream() //
				.filter(component -> {
					// Must be a locally launched component
					if (!(component instanceof LocalScaComponent)) {
						return false;
					}
					if (((LocalScaComponent) component).getLaunch() == null) {
						return false;
					}

					// We want to start the assembly controller
					if (component == assemblyController) {
						return true;
					}

					// Also start anything NOT mentioned in the SAD file
					if (component.getComponentInstantiation() == null) {
						return true;
					}

					// Also start things in the SAD file WITH a start order
					return component.getComponentInstantiation().getStartOrder() != null;
				}) //
				.sorted(new ScaComponentComparator()) // Sort by AC, then start order
				.collect(Collectors.toList());
		for (ScaComponent component : compsToStart) {
			this.streams.getOutStream().println('\t' + component.getInstantiationIdentifier());
			try {
				component.start();
			} catch (StartError e) {
				String resourceDesc = Messages.bind(Messages.ApplicationImpl_Component, component.getName());
				this.streams.getErrStream().println(CFErrorFormatter.format(e, resourceDesc));
				throw e;
			}
		}

		this.streams.getOutStream().println(Messages.ApplicationImpl_Started);
		this.started = true;
	}

	@Override
	public void stop() throws StopError {
		try {
			stop(stopTimeout);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			this.streams.getErrStream().println(Messages.ApplicationImpl_InterruptedWaitingForStop);
			throw new StopError(ErrorNumberType.CF_EINTR, Messages.ApplicationImpl_InterruptedWaitingForStop);
		}
	}

	public void stop(float timeout) throws StopError, InterruptedException {
		waitOnLaunch();

		this.streams.getOutStream().println(Messages.ApplicationImpl_Stopping);

		List<ScaComponent> compsToStop = waveform.getComponentsCopy().stream() //
				.filter(component -> {
					// Must be a locally launched component
					if (!(component instanceof LocalScaComponent)) {
						return false;
					}
					if (((LocalScaComponent) component).getLaunch() == null) {
						return false;
					}

					// We want to stop the assembly controller
					if (component == assemblyController) {
						return true;
					}

					// Also stop anything NOT mentioned in the SAD file
					if (component.getComponentInstantiation() == null) {
						return true;
					}

					// Also stop things in the SAD file WITH a start order
					return component.getComponentInstantiation().getStartOrder() != null;
				}) //
				.sorted(new ScaComponentComparator().reversed()) // Reverse start order
				.collect(Collectors.toList());
		int stopErrors = 0;
		for (ScaComponent component : compsToStop) {
			String instId = component.getInstantiationIdentifier();
			this.streams.getOutStream().println("\t" + instId);
			try {
				CorbaUtils.invoke(() -> {
					component.stop();
					return null;
				}, (long) (timeout * 1000));
			} catch (TimeoutException e) {
				this.streams.getErrStream().println(Messages.bind(Messages.ApplicationImpl_TimeoutWaitingForStop, component.getName()));
				stopErrors++;
			} catch (CoreException e) {
				String msg = Messages.bind(Messages.ApplicationImpl_ProblemStoppingComponent, component.getName());
				this.streams.getErrStream().println(msg);
				if (e.getCause() instanceof StopError) {
					String resourceDesc = Messages.bind(Messages.ApplicationImpl_Component, component.getName());
					this.streams.getErrStream().println(CFErrorFormatter.format((StopError) e.getCause(), resourceDesc));
				} else if (e.getCause() instanceof SystemException) {
					this.streams.getErrStream().println(e.getCause().getMessage());
				} else {
					this.streams.getErrStream().println(e.getMessage());
				}
				stopErrors++;
			}
		}

		boolean delegateFailure = false;
		if (this.delegate != null) {
			this.streams.getOutStream().println(Messages.ApplicationImpl_DelegateStopping);
			try {
				this.delegate.stop();
			} catch (StopError e) {
				String resourceDesc = Messages.bind(Messages.ApplicationImpl_Application, this.name);
				this.streams.getErrStream().println(CFErrorFormatter.format(e, resourceDesc));
				delegateFailure = true;
			}
		}

		this.streams.getOutStream().println(Messages.ApplicationImpl_Stopped);
		this.started = false;

		if (stopErrors == 0 && !delegateFailure) {
			// Successful stop
			return;
		}

		// Problems - throw an exception
		StringBuilder sb = new StringBuilder();
		if (stopErrors > 0) {
			sb.append(stopErrors);
			sb.append(" component(s) failed to stop");
		}
		if (delegateFailure) {
			if (stopErrors > 0) {
				sb.append(". The domain waveform also failed to stop.");
			} else {
				sb.append("The domain waveform failed to stop");
			}
		}
		throw new StopError(ErrorNumberType.CF_NOTSET, sb.toString());
	}

	@Override
	public void initialize() throws InitializeError {
		try {
			waitOnLaunch();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new InitializeError(new String[] { Messages.ApplicationImpl_InterruptedWaitingForAppLaunch });
		}

		String resourceDesc;
		LifeCycleOperations target;
		if (this.delegate != null) {
			resourceDesc = Messages.ApplicationImpl_ApplicationDelegate;
			target = delegate;
		} else if (this.assemblyController != null) {
			resourceDesc = Messages.bind(Messages.ApplicationImpl_Component, this.assemblyController.getName());
			target = this.assemblyController;
		} else {
			return;
		}

		this.streams.getOutStream().println(Messages.ApplicationImpl_Initializing);
		try {
			target.initialize();
			this.streams.getOutStream().println(Messages.ApplicationImpl_Initialized);
		} catch (final InitializeError e) {
			this.streams.getErrStream().println(CFErrorFormatter.format(e, resourceDesc));
			throw e;
		}
	}

	@Override
	public void releaseObject() throws ReleaseError {
		if (this.terminated) {
			return;
		}
		try {
			waitOnLaunch();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new ReleaseError(new String[] { Messages.ApplicationImpl_InterruptedWaitingForAppLaunch });
		}

		// Sandbox chalkboard doesn't get terminated during releaseObject(), just other sandbox waveforms
		if (!isSandboxChalkboard) {
			this.terminated = true;
		}
		this.streams.getOutStream().println(Messages.ApplicationImpl_ApplicationReleasing);

		try {
			stop(DEFAULT_STOP_TIMEOUT);
		} catch (StopError e) {
			// Ignore and continue teardown
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			this.streams.getErrStream().println(Messages.ApplicationImpl_InterruptedWaitingForStop);
			throw new ReleaseError(new String[] { Messages.ApplicationImpl_InterruptedWaitingForStop });
		}

		disconnectAll();

		try {
			releaseAll();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			this.streams.getErrStream().println(Messages.ApplicationImpl_InterruptedWaitingForRelease);
			throw new ReleaseError(new String[] { Messages.ApplicationImpl_InterruptedWaitingForRelease });
		}

		// For the sandbox chalkboard, we're done
		if (isSandboxChalkboard) {
			return;
		}

		// Unbind the waveform context
		unbind();

		this.streams.getOutStream().println(Messages.ApplicationImpl_ApplicationReleased);
		fireTerminated();
	}

	protected void unbind() {
		if (this.waveformContext != null) {
			if (this.waveformContext.eContainer() instanceof NotifyingNamingContext) {
				this.streams.getOutStream().println(Messages.ApplicationImpl_ApplicationUnbinding);
				try {
					NotifyingNamingContext localSCANamingContext = (NotifyingNamingContext) this.waveformContext.eContainer();
					localSCANamingContext.unbind(Name.toName(this.waveformContext.getFullName()));
				} catch (final NotFound | CannotProceed | InvalidName e) {
					this.streams.getErrStream().println(Messages.ApplicationImpl_ProblemUnbindingWaveformContext);
					this.streams.getErrStream().println(e.toString());
				}
			}
			if (ScaDebugPlugin.getInstance() != null) {
				final NamingContextExt context = getWaveformContext();
				if (context != null) {
					try {
						// Ugly code intended to handle namespaced applications (which must have dots escaped)
						context.unbind(Name.toName(this.name.replaceAll("\\.", "\\\\.")));
					} catch (NotFound | CannotProceed | InvalidName | SystemException e) {
						this.streams.getErrStream().println(Messages.ApplicationImpl_ProblemUnbindingWaveformContext);
						this.streams.getErrStream().println(e.toString());
					}
				}
			}
			this.waveformContext = null;
		}
	}

	/**
	 * Terminate all <b>local</b> components, local component hosts
	 */
	protected void terminateAll() {
		for (ScaComponent component : this.waveform.getComponentsCopy()) {
			if (component instanceof LocalScaComponent && ((LocalScaComponent) component).getLaunch() != null) {
				terminate(component);
			}
		}

		LocalScaExecutableDevice componentHost = this.waveform.getComponentHost();
		if (componentHost != null && componentHost.getLaunch() != null) {
			String id = componentHost.getLabel();
			try {
				componentHost.getLaunch().terminate();
			} catch (DebugException e) {
				this.streams.getOutStream().println("Runtime component host " + id + " failed to terminate correctly");
			}
		}
		componentHost = this.waveform.getComponentHostDebug();
		if (componentHost != null) {
			String id = componentHost.getLabel();
			try {
				componentHost.getLaunch().terminate();
			} catch (DebugException e) {
				this.streams.getOutStream().println("Debug component host " + id + " failed to terminate correctly");
			}
		}

		this.assemblyController = null;
	}

	protected void terminate(final ScaComponent info) {
		String id = info.getIdentifier();
		try {
			((LocalLaunch) info).getLaunch().terminate();
		} catch (DebugException e) {
			this.streams.getOutStream().println(Messages.bind(Messages.ApplicationImpl_FailedToTerminateComponent, id));
		}
	}

	/**
	 * Release all <b>local</b> components, local component hosts.
	 */
	protected void releaseAll() throws InterruptedException {
		this.streams.getOutStream().println(Messages.ApplicationImpl_ReleasingComponents);

		// Shutdown each component
		for (final ScaComponent component : this.waveform.getComponentsCopy()) {
			if (component instanceof LocalScaComponent && ((LocalScaComponent) component).getLaunch() != null) {
				release(component, component.getName());
			}
		}

		LocalScaExecutableDevice componentHost = this.waveform.getComponentHost();
		if (componentHost != null) {
			release(componentHost, componentHost.getLabel());
		}
		componentHost = this.waveform.getComponentHostDebug();
		if (componentHost != null) {
			release(componentHost, componentHost.getLabel());
		}

		this.assemblyController = null;
		this.streams.getOutStream().println(Messages.ApplicationImpl_ReleasedComponents);
	}

	protected void release(final ScaAbstractComponent< ? > resource, String name) throws InterruptedException {
		this.streams.getOutStream().println(Messages.bind(Messages.ApplicationImpl_ReleasingComponent, name));
		try {
			CorbaUtils.invoke(() -> {
				resource.releaseObject();
				return null;
			}, (long) (RELEASE_TIMEOUT * 1000.0));
		} catch (TimeoutException e) {
			String msg = Messages.bind(Messages.ApplicationImpl_TimeoutWaitingForRelease, name);
			this.streams.getErrStream().println(msg);
		} catch (CoreException e) {
			String msg = Messages.bind(Messages.ApplicationImpl_ProblemReleasingComponent, name);
			this.streams.getErrStream().println(msg);
			if (e.getCause() instanceof ReleaseError) {
				String resourceDesc = Messages.bind(Messages.ApplicationImpl_Component, name);
				this.streams.getErrStream().println(CFErrorFormatter.format((ReleaseError) e.getCause(), resourceDesc));
			} else if (e.getCause() instanceof SystemException) {
				this.streams.getErrStream().println(e.getCause().getMessage());
			} else {
				this.streams.getErrStream().println(e.toString());
			}
		}
	}

	/**
	 * Disconnect all <b>local</b> components
	 */
	protected void disconnectAll() {
		this.streams.getOutStream().println(Messages.ApplicationImpl_Disconnecting);
		// Disconnect components
		for (final ScaComponent component : this.waveform.getComponentsCopy()) {
			if (component instanceof LocalScaComponent && ((LocalScaComponent) component).getLaunch() != null) {
				disconnect(component);
			}
		}
		this.streams.getOutStream().println(Messages.ApplicationImpl_Disconnected);
	}

	protected void disconnect(final ScaComponent comp) {
		for (final ScaPort< ? , ? > port : comp.getPorts().toArray(new ScaPort< ? , ? >[comp.getPorts().size()])) {
			if (port instanceof ScaUsesPort) {
				final ScaUsesPort up = (ScaUsesPort) port;
				final ScaConnection[] connections = up.getConnections().toArray(new ScaConnection[up.getConnections().size()]);
				for (final ScaConnection c : connections) {
					try {
						up.disconnectPort(c);
					} catch (final InvalidPort e) {
						String msg = Messages.bind(Messages.ApplicationImpl_ProblemDisconnecting, c.getId());
						String resourceDesc = Messages.bind(Messages.ApplicationImpl_Connection, c.getId());
						this.streams.getErrStream().println(msg);
						this.streams.getErrStream().println(CFErrorFormatter.format(e, resourceDesc));
					} catch (final SystemException e) {
						String msg = Messages.bind(Messages.ApplicationImpl_ProblemDisconnecting, c.getId());
						this.streams.getErrStream().println(msg);
						this.streams.getErrStream().println(e.toString());
					}
				}
			}
		}
	}

	private void fireTerminated() {
		DebugPlugin plugin = DebugPlugin.getDefault();
		if (plugin != null) {
			plugin.fireDebugEventSet(new DebugEvent[] { new DebugEvent(this, DebugEvent.TERMINATE) });
		}
	}

	@Override
	public void runTest(final int testid, final PropertiesHolder testValues) throws UnknownTest, UnknownProperties {
		try {
			waitOnLaunch();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new UnknownTest();
		}

		String resourceDesc;
		TestableObjectOperations target;
		if (this.delegate != null) {
			resourceDesc = Messages.ApplicationImpl_ApplicationDelegate;
			target = delegate;
		} else if (this.assemblyController != null) {
			resourceDesc = Messages.bind(Messages.ApplicationImpl_Component, this.assemblyController.getName());
			target = this.assemblyController;
		} else {
			throw new UnknownTest();
		}

		this.streams.getOutStream().println(Messages.bind(Messages.ApplicationImpl_TestRunning, testid, resourceDesc));
		try {
			target.runTest(testid, testValues);
		} catch (final UnknownTest e) {
			this.streams.getErrStream().println(CFErrorFormatter.format(e, resourceDesc));
			throw e;
		} catch (final UnknownProperties e) {
			this.streams.getErrStream().println(CFErrorFormatter.format(e, resourceDesc));
			throw e;
		}
		this.streams.getOutStream().println(Messages.bind(Messages.ApplicationImpl_TestRan, testid));

	}

	@Override
	public void initializeProperties(final DataType[] configProperties) throws AlreadyInitialized, InvalidConfiguration, PartialConfiguration {
		throw new InvalidConfiguration(Messages.ApplicationImpl_NoInitializeProperties, configProperties);
	}

	@Override
	public void configure(final DataType[] configProperties) throws InvalidConfiguration, PartialConfiguration {
		try {
			waitOnLaunch();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new InvalidConfiguration(Messages.ApplicationImpl_InterruptedWaitingForAppLaunch, new DataType[0]);
		}

		this.streams.getOutStream().println(Messages.ApplicationImpl_ConfiguringApplication);
		for (DataType t : configProperties) {
			streams.getOutStream().println("\n\t" + LocalApplicationFactory.toString(t));
		}

		if (this.delegate != null) {
			this.delegate.configure(configProperties);
			return;
		}

		Map<String, List<DataType>> configMap = new HashMap<String, List<DataType>>();
		ExternalProperties externalProperties = waveform.getProfileObj().getExternalProperties();

		config: for (DataType t : configProperties) {
			if (externalProperties != null) {
				for (ExternalProperty p : externalProperties.getProperties()) {
					String propId = (p.getExternalPropID() != null) ? p.getExternalPropID() : p.getPropID();
					if (propId.equals(t.id)) {
						List<DataType> configList = configMap.get(p.getCompRefID());
						if (configList == null) {
							configList = new ArrayList<DataType>();
							configMap.put(p.getCompRefID(), configList);
						}
						configList.add(new DataType(p.getPropID(), t.value));

						// External prop, go to new config property item
						continue config;
					}
				}
			}

			List<DataType> configList = configMap.get(null);
			if (configList == null) {
				configList = new ArrayList<DataType>();
				configMap.put(null, configList);
			}
			configList.add(t);
		}

		for (Map.Entry<String, List<DataType>> entry : configMap.entrySet()) {
			ScaComponent inst;
			if (entry.getKey() == null) {
				inst = waveform.getAssemblyController();
			} else {
				inst = waveform.getScaComponent(entry.getKey());
			}
			if (inst != null) {
				inst.configure(entry.getValue().toArray(new DataType[entry.getValue().size()]));
			}
		}
	}

	@Override
	public void query(final PropertiesHolder configProperties) throws UnknownProperties {
		try {
			waitOnLaunch();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new UnknownProperties(new DataType[0]);
		}

		if (this.delegate != null) {
			this.delegate.query(configProperties);
			return;
		}

		SoftwareAssembly profileObj = this.waveform.fetchProfileObject(null);
		ExternalProperties externalProperties = null;
		if (profileObj != null) {
			externalProperties = this.waveform.getProfileObj().getExternalProperties();
		}

		Set<String> queryProperties = new HashSet<String>();
		if (configProperties.value != null) {
			for (DataType t : configProperties.value) {
				queryProperties.add(t.id);
			}
		}

		List<DataType> retVal = new ArrayList<DataType>();
		LocalScaComponent localController = this.assemblyController;
		if (localController != null) {
			PropertiesHolder tmpHolder = new PropertiesHolder();
			if (!queryProperties.isEmpty()) {
				List<DataType> propsToQuery = new ArrayList<DataType>();
				for (ScaAbstractProperty< ? > p : localController.getProperties()) {
					if (queryProperties.contains(p.getId())) {
						propsToQuery.add(new DataType(p.getId(), org.omg.CORBA.ORB.init().create_any()));
					}
				}
				tmpHolder.value = propsToQuery.toArray(new DataType[propsToQuery.size()]);
			} else {
				tmpHolder.value = new DataType[0];
			}
			localController.query(tmpHolder);
			retVal.addAll(Arrays.asList(tmpHolder.value));
		}
		if (externalProperties != null) {
			for (ExternalProperty prop : externalProperties.getProperties()) {
				String propId = (prop.getExternalPropID() != null) ? prop.getExternalPropID() : prop.getPropID();
				if (queryProperties.isEmpty() || queryProperties.contains(propId)) {
					PropertiesHolder tmpHolder = new PropertiesHolder();
					tmpHolder.value = new DataType[] { new DataType(prop.getPropID(), org.omg.CORBA.ORB.init().create_any()) };
					ScaComponent component = waveform.getScaComponent(prop.getCompRefID());
					if (component != null) {
						component.query(tmpHolder);
						retVal.add(new DataType(prop.resolveExternalID(), tmpHolder.value[0].value));
					} else {
						String msg = Messages.bind(Messages.ApplicationImpl_FailedToFindCompForExtProp, prop.getCompRefID(), prop.getPropID());
						this.streams.getErrStream().println(msg);
					}
				}
			}
		}
		configProperties.value = retVal.toArray(new DataType[retVal.size()]);
	}

	@Override
	public String registerPropertyListener(org.omg.CORBA.Object obj, String[] propIds, float interval) throws UnknownProperties, InvalidObjectReference {
		throw new IllegalStateException(Messages.ApplicationImpl_NotImplemented);
	}

	@Override
	public void unregisterPropertyListener(String id) throws InvalidIdentifier {
		throw new IllegalStateException(Messages.ApplicationImpl_NotImplemented);
	}

	@Override
	public org.omg.CORBA.Object getPort(final String name) throws UnknownPort {
		if (name == null || name.isEmpty()) {
			throw new UnknownPort();
		}

		try {
			waitOnLaunch();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new UnknownPort();
		}

		try {
			ExternalPorts externalPorts = waveform.getProfileObj().getExternalPorts();
			if (externalPorts != null) {
				for (final Port p : externalPorts.getPort()) {
					if (name.equals(p.getProvidesIdentifier()) || name.equals(p.getUsesIdentifier()) || name.equals(p.getExternalName())) {
						final ScaComponent comp = findComponent(p.getComponentInstantiationRef().getRefid());
						if (comp != null) {
							String portName;
							if (p.getProvidesIdentifier() != null) {
								portName = p.getProvidesIdentifier();
							} else {
								portName = p.getUsesIdentifier();
							}
							return comp.getPort(portName);
						} else {
							String msg = Messages.bind(Messages.ApplicationImpl_FailedToFindCompForExtPort, p.getComponentInstantiationRef().getRefid(), name);
							throw new UnknownPort(msg);
						}
					} else if (name.equals(p.getSupportedIdentifier())) {
						final ScaComponent comp = findComponent(p.getComponentInstantiationRef().getRefid());
						if (comp != null) {
							return comp.getCorbaObj();
						}
					}
				}
			}
		} catch (final UnknownPort e) {
			String resourceDesc = Messages.bind(Messages.ApplicationImpl_Port, name);
			this.streams.getErrStream().println(CFErrorFormatter.format(e, resourceDesc));
			throw e;
		}
		throw new UnknownPort();
	}

	@Override
	public PortInfoType[] getPortSet() {
		try {
			waitOnLaunch();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return new PortInfoType[0];
		}

		final ExternalPorts externalPorts = waveform.getProfileObj().getExternalPorts();
		if (externalPorts == null) {
			return new PortInfoType[0];
		}

		List<PortInfoType> portInfo = new ArrayList<PortInfoType>(externalPorts.getPort().size());
		for (final Port p : externalPorts.getPort()) {
			final String nameOnComponent;
			final String direction;
			if (p.getProvidesIdentifier() != null) {
				nameOnComponent = p.getProvidesIdentifier();
				direction = "Provides"; //$NON-NLS-1$
			} else {
				nameOnComponent = p.getUsesIdentifier();
				direction = "Uses"; //$NON-NLS-1$
			}

			final String nameOnWaveform;
			if (p.getExternalName() != null) {
				nameOnWaveform = p.getExternalName();
			} else {
				nameOnWaveform = nameOnComponent;
			}

			final ScaComponent component = findComponent(p.getComponentInstantiationRef().getRefid());
			final ScaPort< ? , ? > port = (component == null) ? null : component.getScaPort(nameOnComponent);

			String description = "";
			String repId = "";
			org.omg.CORBA.Object portObj = null;
			if (port != null) {
				if (port.getProfileObj() != null) {
					description = port.getProfileObj().getDescription();
					repId = port.getRepid();
				}
				portObj = port.getCorbaObj();
			}
			if (p.getDescription() != null) {
				description = p.getDescription();
			}

			portInfo.add(new PortInfoType(portObj, nameOnWaveform, repId, description, direction));
		}

		return portInfo.toArray(new PortInfoType[externalPorts.getPort().size()]);
	}

	private ScaComponent findComponent(final String instId) {
		return waveform.getScaComponent(instId);
	}

	@Override
	public ComponentType[] registeredComponents() {
		try {
			waitOnLaunch();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return new ComponentType[0];
		}

		ComponentType[] delegateValues;
		if (this.delegate != null) {
			delegateValues = this.delegate.registeredComponents();
		} else {
			delegateValues = new ComponentType[0];
		}
		List<ScaComponent> components = waveform.getComponentsCopy();
		if (components.isEmpty()) {
			return delegateValues;
		}
		final ComponentType[] types = new ComponentType[components.size()];
		for (int i = 0; i < types.length; i++) {
			types[i] = new ComponentType();
			types[i].componentObject = components.get(i).getCorbaObj();
			types[i].identifier = components.get(i).getIdentifier();
			types[i].softwareProfile = components.get(i).getProfileObj().eResource().getURI().path();
			types[i].providesPorts = new PortType[0];
			types[i].type = ComponentEnumType.APPLICATION_COMPONENT;

			for (ComponentType delegateValue : delegateValues) {
				if (types[i].identifier.equals(delegateValue.identifier)) {
					types[i] = delegateValue;
					break;
				}
			}
		}
		return types;
	}

	@Override
	public ComponentElementType[] componentNamingContexts() {
		try {
			waitOnLaunch();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return new ComponentElementType[0];
		}

		ComponentElementType[] delegateValues;
		if (this.delegate != null) {
			delegateValues = this.delegate.componentNamingContexts();
		} else {
			delegateValues = new ComponentElementType[0];
		}
		List<ScaComponent> components = waveform.getComponentsCopy();
		if (components.isEmpty()) {
			return delegateValues;
		}
		final ComponentElementType[] types = new ComponentElementType[components.size()];
		for (int i = 0; i < types.length; i++) {
			types[i] = new ComponentElementType();
			types[i].componentId = components.get(i).getIdentifier();
			types[i].elementId = this.name + '/' + components.get(i).getName();
			for (ComponentElementType delegateValue : delegateValues) {
				if (delegateValue.componentId.equals(types[i].componentId)) {
					types[i] = delegateValue;
					break;
				}
			}
		}
		return types;
	}

	@Override
	public ComponentProcessIdType[] componentProcessIds() {
		if (this.delegate != null) {
			return this.delegate.componentProcessIds();
		}
		return new ComponentProcessIdType[0];
	}

	@Override
	public DeviceAssignmentType[] componentDevices() {
		if (this.delegate != null) {
			return this.delegate.componentDevices();
		}
		return new DeviceAssignmentType[0];
	}

	@Override
	public ComponentElementType[] componentImplementations() {
		try {
			waitOnLaunch();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return new ComponentElementType[0];
		}

		ComponentElementType[] delegateValues;
		if (this.delegate != null) {
			delegateValues = this.delegate.componentImplementations();
		} else {
			delegateValues = new ComponentElementType[0];
		}
		List<ScaComponent> components = waveform.getComponentsCopy();
		if (components.isEmpty()) {
			return delegateValues;
		}
		final ComponentElementType[] types = new ComponentElementType[components.size()];
		for (int i = 0; i < types.length; i++) {
			final ScaComponent comp = components.get(i);

			types[i] = new ComponentElementType();
			types[i].componentId = comp.getIdentifier();
			if (comp instanceof LocalScaComponent) {
				types[i].elementId = ((LocalScaComponent) comp).getImplementationID();
			} else {
				types[i].elementId = "";
			}
			if (types[i].elementId == null) {
				types[i].elementId = "";
			}
			for (ComponentElementType delegateValue : delegateValues) {
				if (types[i].componentId.equals(delegateValue.componentId)) {
					types[i] = delegateValue;
					break;
				}
			}
		}
		return types;
	}

	@Override
	public String profile() {
		return this.profile;
	}

	@Override
	public String softwareProfile() {
		return profile();
	}

	@Override
	public String name() {
		return this.name;
	}

	@Override
	public boolean aware() {
		return aware;
	}

	@Override
	public float stopTimeout() {
		return stopTimeout;
	}

	@Override
	public void stopTimeout(float newStopTimeout) {
		this.stopTimeout = newStopTimeout;
	}

	@Override
	public boolean canTerminate() {
		return !this.terminated;
	}

	@Override
	public boolean isTerminated() {
		return this.terminated;
	}

	@Override
	public void terminate() throws DebugException {
		if (this.terminated) {
			return;
		}
		try {
			waitOnLaunch();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new DebugException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, Messages.ApplicationImpl_InterruptedWaitingForAppLaunch, e));
		}

		this.terminated = true;
		this.streams.getOutStream().println(Messages.ApplicationImpl_Terminating);

		// Terminate the launch for each component
		terminateAll();

		// Unbind waveform context
		unbind();

		this.streams.getOutStream().println(Messages.ApplicationImpl_Terminated);
		fireTerminated();
	}

	@Override
	public String getLabel() {
		return this.name;
	}

	@Override
	public ILaunch getLaunch() {
		return this.parentLaunch;
	}

	@Override
	public ApplicationStreams getStreamsProxy() {
		return this.streams;
	}

	@Override
	public void setAttribute(final String key, final String value) {
		// No Attributes
	}

	@Override
	public String getAttribute(final String key) {
		// No Attributes
		return null;
	}

	@Override
	public int getExitValue() throws DebugException {
		if (!this.terminated) {
			throw new DebugException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, Messages.ApplicationImpl_ApplicationNotTerminated, null));
		}
		return 0;
	}

	public Resource reset(final String compInstId) throws ReleaseError, ExecuteFail {
		if (compInstId == null) {
			throw new ReleaseError(new String[] { "Unknown component: " + compInstId });
		}
		this.streams.getOutStream().println("Resetting component " + compInstId);

		LocalScaComponent oldComponent = null;
		for (ScaComponent component : waveform.getComponentsCopy()) {
			if (compInstId.equals(component.getInstantiationIdentifier())) {
				if (!(component instanceof LocalScaComponent)) {
					// This should never happen but check for it anyway
					String msg = "Can only reset local components";
					this.streams.getErrStream().println(msg);
					throw new ReleaseError(new String[] { msg });
				}
				oldComponent = (LocalScaComponent) component;
				break;
			}
		}

		if (oldComponent == null) {
			this.streams.getErrStream().println("No component " + compInstId);
			throw new ReleaseError(new String[] { "Unknown component: " + compInstId });
		}
		final String usageName = oldComponent.getName();
		String instId = oldComponent.getIdentifier();
		final String execParams = oldComponent.getExecParam();
		final URI spdUri = oldComponent.getProfileURI();

		if (spdUri == null) {
			this.streams.getErrStream().println("No SPD URI component: " + compInstId);
			throw new ReleaseError(new String[] { "No SPD URI component: " + compInstId });
		}

		final String implId = oldComponent.getImplementationID();
		final String mode = oldComponent.getMode();
		final List<ConnectionInfo> oldConnections = getConnectionInfo(oldComponent);

		disconnect(oldConnections);
		try {
			release(oldComponent, oldComponent.getName());
		} catch (InterruptedException e1) {
			throw new ReleaseError(new String[] { "Interrupted while releasing component" });
		}
		if (oldComponent.getLaunch() != null && oldComponent.getLaunch().canTerminate()) {
			try {
				oldComponent.getLaunch().terminate();
			} catch (final DebugException e) {
				// PASS
			}
		}
		while (!oldComponent.isDisposed()) {
			try {
				Thread.sleep(500);
			} catch (final InterruptedException e) {
				ScaDebugPlugin.logError(Messages.ApplicationImpl_InterruptedWaitingForAppLaunch, e);
				Thread.currentThread().interrupt();
				throw new ReleaseError(new String[] { Messages.ApplicationImpl_InterruptedWaitingForAppLaunch });
			}
		}
		try {
			final LocalScaComponent retVal = launch(usageName, instId, execParams, spdUri, implId, mode);
			this.streams.getOutStream().println("Reconnecting component");
			makeConnections(oldConnections);
			this.streams.getOutStream().println("Done resetting component " + oldComponent.getName());
			return retVal.getObj();
		} catch (final CoreException e) {
			this.streams.getErrStream().println("Failed to reset component " + usageName);
			this.streams.getErrStream().println(e.toString());
			throw new ExecuteFail(ErrorNumberType.CF_EFAULT, e.getStatus().getMessage());
		}
	}

	private void disconnect(final List<ConnectionInfo> oldConnections) {
		for (final ConnectionInfo info : oldConnections) {
			info.disconnect();
		}
	}

	private void makeConnections(final List<ConnectionInfo> connections) {
		try {
			this.waveform.refresh(null, RefreshDepth.FULL);
			for (final ConnectionInfo info : connections) {
				try {
					info.reconnect(this.waveform);
				} catch (final InvalidPort e) {
					String resourceDesc = Messages.bind(Messages.ApplicationImpl_Connection, info.getConnectionID());
					this.streams.getErrStream().println("Failed to reconnect connection " + info.getConnectionID());
					this.streams.getErrStream().println(CFErrorFormatter.format(e, resourceDesc));
				} catch (final OccupiedPort e) {
					String resourceDesc = Messages.bind(Messages.ApplicationImpl_Connection, info.getConnectionID());
					this.streams.getErrStream().println("Failed to reconnect connection " + info.getConnectionID());
					this.streams.getErrStream().println(CFErrorFormatter.format(e, resourceDesc));
				}
			}
		} catch (final InterruptedException e) {
			this.streams.getErrStream().println("Failed to reconnect connections");
			ScaDebugPlugin.logError("Interrupted while refreshing waveform", e);
		}
	}

	private List<ConnectionInfo> getConnectionInfo(final LocalScaComponent oldComponent) {
		final List<ConnectionInfo> retVal = new ArrayList<ApplicationImpl.ConnectionInfo>();

		// Create list of connections that connect to me
		for (final ScaComponent comp : this.waveform.getComponentsCopy()) {
			if (comp != oldComponent) {
				for (final ScaPort< ? , ? > port : comp.getPorts()) {
					if (port instanceof ScaUsesPort) {
						final ScaUsesPort uses = (ScaUsesPort) port;
						for (final ScaConnection conn : uses.getConnections()) {
							if (oldComponent.getObj()._is_equivalent(conn.getTargetPort())) {
								retVal.add(new FromConnectionInfo(uses, oldComponent.getInstantiationIdentifier(), null, conn));
							} else {
								for (final ScaPort< ? , ? > targetPort : oldComponent.getPorts()) {
									if (targetPort.getObj()._is_equivalent(conn.getTargetPort())) {
										retVal.add(new FromConnectionInfo(uses, oldComponent.getInstantiationIdentifier(), targetPort.getName(), conn));
									}
								}
							}
						}
					}
				}
			}
		}

		// Create list of connections that I connect to
		for (final ScaPort< ? , ? > port : oldComponent.getPorts()) {
			if (port instanceof ScaUsesPort) {
				final ScaUsesPort uses = (ScaUsesPort) port;
				for (final ScaConnection conn : uses.getConnections()) {
					retVal.add(new ToConnectionInfo(oldComponent.getInstantiationIdentifier(), uses.getName(), conn.getTargetPort(), conn));
				}
			}
		}

		return retVal;
	}

	/**
	 * @deprecated Use {@link #launch(String, String, DataType[], URI, String, String, IProgressMonitor)}
	 */
	@Deprecated
	public Resource launch(final String compId, final DataType[] initConfiguration, @NonNull final String spdURI, final String implId, final String mode)
		throws ExecuteFail {
		Assert.isNotNull(spdURI, "SPD URI must not be null");
		LocalScaComponent retVal;
		try {
			URI uri = URI.createURI(spdURI);
			if (uri == null) {
				throw new NullPointerException();
			}
			retVal = launch(null, compId, initConfiguration, uri, implId, mode, null);
		} catch (final CoreException e) {
			String msg = Messages.bind(Messages.ApplicationImpl_FailedToLaunchComponent, compId);
			this.getStreams().getErrStream().println(msg);
			this.getStreams().getErrStream().println(e.toString());
			throw new ExecuteFail(ErrorNumberType.CF_EFAULT, e.getStatus().getMessage());
		}
		return retVal.getObj();
	}

	/**
	 * @deprecated Use {@link #launch(String, String, DataType[], URI, String, String, IProgressMonitor)}
	 */
	@Deprecated
	@NonNull
	public LocalScaComponent launch(final String usageName, String compId, final DataType[] initConfiguration, @NonNull final URI spdURI, final String implId,
		final String mode) throws CoreException {
		return launch(usageName, compId, initConfiguration, spdURI, implId, mode, null);
	}

	public LocalScaComponent launch(final String usageName, String compId, final DataType[] initConfiguration, @NonNull final URI spdURI, final String implId,
		final String mode, IProgressMonitor monitor) throws CoreException {
		final int WORK_CONFIG = 1, WORK_LAUNCH = 3, WORK_POST_LAUNCH = 1;
		SubMonitor subMonitor = SubMonitor.convert(monitor, "Launching " + compId, WORK_CONFIG + WORK_LAUNCH + WORK_POST_LAUNCH);

		ILaunchConfigurationWorkingCopy config = createLaunchConfig(usageName, compId, initConfiguration, spdURI, implId, mode, subMonitor.split(WORK_CONFIG));

		final ILaunch subLaunch = config.launch(mode, subMonitor.split(WORK_LAUNCH), false);
		if (subLaunch instanceof IComponentLaunch) {
			((IComponentLaunch) subLaunch).setParent(this);
		}
		this.streams.getOutStream().println(Messages.ApplicationImpl_ComponentLaunched);

		// Skip postLaunch if the resource being launched is a componentHost
		if (SoftPkg.Util.getComponentHostURI().equals(spdURI)) {
			subMonitor.notWorked(WORK_POST_LAUNCH);
			return null;
		}
		return postLaunch(subLaunch, subMonitor.split(WORK_POST_LAUNCH));
	}

	/**
	 * @deprecated Use {@link #launch(String, String, DataType[], URI, String, String, IProgressMonitor)}
	 */
	@Deprecated
	public LocalScaComponent launch(final String usageName, final DataType[] initConfiguration, @NonNull final URI spdURI, final String implId,
		final String mode) throws CoreException {
		return launch(usageName, null, initConfiguration, spdURI, implId, mode, null);
	}

	/**
	 * @deprecated Use {@link #launch(String, String, DataType[], URI, String, String, IProgressMonitor)}
	 */
	@Deprecated
	@NonNull
	public LocalScaComponent launch(@Nullable String usageName, @Nullable final String compId, @Nullable final String execParams, @NonNull URI spdURI,
		@Nullable final String implId, @Nullable String mode) throws CoreException {
		ILaunchConfigurationWorkingCopy config = createLaunchConfig(usageName, compId, null, spdURI, implId, mode, null);
		if (execParams != null && execParams.length() > 0) {
			config.setAttribute(LaunchVariables.EXEC_PARAMS, execParams);
		}

		final ILaunch subLaunch = config.launch(mode, new NullProgressMonitor(), false);
		if (subLaunch instanceof IComponentLaunch) {
			((IComponentLaunch) subLaunch).setParent(this);
		}
		this.streams.getOutStream().println(Messages.ApplicationImpl_ComponentLaunched);

		return postLaunch(subLaunch, null);
	}

	private ILaunchConfigurationWorkingCopy createLaunchConfig(String usageName, String compId, DataType[] initConfiguration, URI spdURI, String implId,
		String mode, final IProgressMonitor monitor) throws CoreException {
		final int WORK_ATTR = 1, WORK_PROFILE_OBJ = 1, WORK_PROPS = 1;
		SubMonitor subMonitor = SubMonitor.convert(monitor, "Creating launch configuration for " + compId, WORK_ATTR + WORK_PROFILE_OBJ + WORK_PROPS);

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
		SoftPkg spd;
		try {
			ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
			spd = SoftPkg.Util.getSoftPkg(resourceSet.getResource(spdURI, true));
		} catch (WrappedException e) {
			String msg = Messages.bind(Messages.ApplicationImpl_UnableToLoadSpdFile, spdURI);
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, msg, e.getCause()));
		}

		if (mode == null) {
			mode = ILaunchManager.RUN_MODE;
		}

		// Create a launch config
		this.streams.getOutStream().println(Messages.bind(Messages.ApplicationImpl_ComponentLaunching, spd.getName()));
		final ILaunchConfigurationFactory factory = ScaDebugPlugin.getInstance().getLaunchConfigurationFactoryRegistry().getFactory(spd, implId);
		if (factory == null) {
			String msg = Messages.bind(Messages.ApplicationImpl_FailedToObtainLaunchFactory, implId);
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, msg));
		}
		final ILaunchConfigurationWorkingCopy config = factory.createLaunchConfiguration(spd.getName(), implId, spd);

		// Create a name for the component
		final NameComponent[] spdContextName = this.waveformContext.getName(spdURI);
		try {
			Name.toString(spdContextName);
		} catch (final InvalidName e) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, Messages.ApplicationImpl_FailedToStringifyName, e));
		}

		// Bind the component's name in the waveform's naming context
		NamingContext spdContext;
		try {
			spdContext = this.waveformContext.bind_new_context(spdContextName);
		} catch (final NotFound e) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, Messages.ApplicationImpl_FailedToCreateNamingContext, e));
		} catch (final AlreadyBound e) {
			try {
				spdContext = NamingContextHelper.narrow(this.waveformContext.resolve(spdContextName));
			} catch (final NotFound | CannotProceed | InvalidName e1) {
				throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, Messages.ApplicationImpl_FailedToCreateNamingContext, e1));
			}
		} catch (final CannotProceed | InvalidName e) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, Messages.ApplicationImpl_FailedToCreateNamingContext, e));
		}
		config.setAttribute(LaunchVariables.NAMING_CONTEXT_IOR, spdContext.toString());
		String instID = ScaComponentImpl.convertIdentifierToInstantiationID(compId);
		if (usageName == null && compId != null) {
			usageName = instID;
		}

		// Waveform name
		config.setAttribute(LaunchVariables.WAVEFORM_NAME, this.name);
		if (usageName != null) {
			this.streams.getOutStream().println(Messages.bind(Messages.ApplicationImpl_ComponentName, usageName));
			config.setAttribute(LaunchVariables.NAME_BINDING, usageName);
		}

		// Component identifier
		if (compId != null) {
			this.streams.getOutStream().println(Messages.bind(Messages.ApplicationImpl_ComponentId, compId));
			config.setAttribute(LaunchVariables.COMPONENT_IDENTIFIER, compId);
		}
		subMonitor.worked(WORK_ATTR);

		// Property overrides
		if (initConfiguration != null) {
			final ScaComponent propHolder = ScaFactory.eINSTANCE.createScaComponent();
			propHolder.setProfileURI(spdURI);
			propHolder.fetchProfileObject(subMonitor.split(WORK_PROFILE_OBJ));
			propHolder.fetchProperties(subMonitor.split(WORK_PROPS));
			for (DataType dt : initConfiguration) {
				ScaAbstractProperty< ? > prop = propHolder.getProperty(dt.id);
				if (prop != null) {
					prop.fromAny(dt.value);
				} else {
					this.streams.getErrStream().println(Messages.bind(Messages.ApplicationImpl_IgnoringInvalidProperty, dt.id));
				}
			}
			ScaLaunchConfigurationUtil.saveProperties(config, propHolder);
		}

		// Copy launch timeout from parent, if any
		if (parentLaunch != null) {
			int timeout = parentLaunch.getLaunchConfiguration().getAttribute(ScaDebugLaunchConstants.ATT_LAUNCH_TIMEOUT,
				ScaDebugLaunchConstants.DEFAULT_ATT_LAUNCH_TIMEOUT);
			config.setAttribute(ScaDebugLaunchConstants.ATT_LAUNCH_TIMEOUT, timeout);
		}

		return config;
	}

	private LocalScaComponent postLaunch(ILaunch launch, final IProgressMonitor monitor) throws CoreException {
		final int WORK_ATTR = 1, WORK_POST_LAUNCH = 1;
		SubMonitor subMonitor = SubMonitor.convert(monitor, "Post-launch tasks", WORK_ATTR + WORK_POST_LAUNCH);

		LocalScaComponent newComponent = null;
		final String newCompId = launch.getAttribute(LaunchVariables.COMPONENT_IDENTIFIER);
		if (newCompId != null) {
			for (final ScaComponent comp : ApplicationImpl.this.waveform.getComponentsCopy()) {
				comp.fetchAttributes(subMonitor.split(WORK_ATTR));
				final String id = comp.getIdentifier();
				if (id.equals(newCompId)) {
					newComponent = (LocalScaComponent) comp;
					break;
				}
			}
		}

		if (newComponent == null) {
			launch.terminate();
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, Messages.ApplicationImpl_FailedToFindComponent));
		}

		// Add Child processes
		for (final IProcess process : launch.getProcesses()) {
			if (this.parentLaunch != null) {
				this.parentLaunch.addProcess(process);
			}
		}

		String defaultCompId = null;
		String compId = launch.getLaunchConfiguration().getAttribute(LaunchVariables.COMPONENT_IDENTIFIER, defaultCompId);
		String instID = ScaComponentImpl.convertIdentifierToInstantiationID(compId);
		if (instID == null) {
			instID = newComponent.getInstantiationIdentifier();
		}
		String assemblyID = ApplicationImpl.getAssemblyControllerID(waveform.getProfileObj());
		if (assemblyID != null) {
			if (assemblyID.equals(instID)) {
				this.assemblyController = newComponent;
			}
		}

		subMonitor.worked(WORK_POST_LAUNCH);
		subMonitor.done();
		return newComponent;
	}

	@Override
	public < T > T getAdapter(Class<T> adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	@Override
	public LogEvent[] retrieve_records(IntHolder howMany, int startingRecord) {
		return new LogEvent[0];
	}

	@Override
	public LogEvent[] retrieve_records_by_date(IntHolder howMany, long toTimeStamp) {
		return new LogEvent[0];
	}

	@Override
	public LogEvent[] retrieve_records_from_date(IntHolder howMany, long fromTimeStamp) {
		return new LogEvent[0];
	}

	@Override
	public int log_level() {
		return -1;
	}

	@Override
	public void log_level(int newLogLevel) {
	}

	@Override
	public int getLogLevel(String loggerId) throws CF.UnknownIdentifier {
		return -1;
	}

	@Override
	public void setLogLevel(String loggerId, int newLevel) throws UnknownIdentifier {
	}

	@Override
	public String[] getNamedLoggers() {
		return new String[0];
	}

	@Override
	public void resetLog() {
	}

	@Override
	public String getLogConfig() {
		return "";
	}

	@Override
	public void setLogConfig(String configContents) {
	}

	@Override
	public void setLogConfigURL(String configUrl) {
	}

	@Override
	public DataType[] metrics(String[] components, String[] attributes) throws InvalidMetric {
		return MetricsGenerator.metrics(getLocalWaveform(), components, attributes);
	}

	public boolean isDelegate() {
		return this.delegate != null;
	}

	private synchronized void waitOnLaunch() throws InterruptedException {
		while (launching) {
			wait();
		}
	}

	public synchronized void setLaunching(boolean b) {
		this.launching = b;
		if (!launching) {
			notifyAll();
		}
	}
}
