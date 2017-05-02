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
package gov.redhawk.ide.debug.internal.cf.extended.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
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
import CF.LogEvent;
import CF.PortType;
import CF.PropertiesHolder;
import CF.Resource;
import CF.UnknownIdentifier;
import CF.UnknownProperties;
import CF.ApplicationPackage.ComponentElementType;
import CF.ApplicationPackage.ComponentProcessIdType;
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
import gov.redhawk.ide.debug.LocalScaComponent;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.NotifyingNamingContext;
import gov.redhawk.ide.debug.ScaDebugLaunchConstants;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.internal.ApplicationStreams;
import gov.redhawk.ide.debug.internal.ComponentLaunch;
import gov.redhawk.ide.debug.internal.LocalApplicationFactory;
import gov.redhawk.ide.debug.variables.LaunchVariables;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.model.sca.ScaAbstractProperty;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaConnection;
import gov.redhawk.model.sca.ScaFactory;
import gov.redhawk.model.sca.ScaPort;
import gov.redhawk.model.sca.ScaUsesPort;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.model.sca.impl.ScaComponentImpl;
import gov.redhawk.sca.efs.WrappedFileStore;
import gov.redhawk.sca.launch.ScaLaunchConfigurationUtil;
import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.sad.ExternalPorts;
import mil.jpeojtrs.sca.sad.ExternalProperties;
import mil.jpeojtrs.sca.sad.ExternalProperty;
import mil.jpeojtrs.sca.sad.Port;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.sad.util.StartOrderComparator;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.CFErrorFormatter;
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
				String msg = "Problems while disconnecting connection " + this.connectionID;
				ApplicationImpl.this.streams.getErrStream().println(msg);
				ApplicationImpl.this.streams.getErrStream().println(CFErrorFormatter.format(e, "connection " + this.connectionID));
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
				String msg = "Problems while disconnecting connection " + this.connectionID;
				ApplicationImpl.this.streams.getErrStream().println(msg);
				ApplicationImpl.this.streams.getErrStream().println(CFErrorFormatter.format(e, "connection " + this.connectionID));
			}
		}

		@Override
		public String getConnectionID() {
			return this.connectionID;
		}
	}

	private static final EStructuralFeature[] ASSEMBLY_ID_PATH = new EStructuralFeature[] { SadPackage.Literals.SOFTWARE_ASSEMBLY__ASSEMBLY_CONTROLLER,
		SadPackage.Literals.ASSEMBLY_CONTROLLER__COMPONENT_INSTANTIATION_REF, PartitioningPackage.Literals.COMPONENT_INSTANTIATION_REF__REFID };
	private LocalScaComponent assemblyController;
	private NotifyingNamingContext waveformContext;
	private final ApplicationStreams streams = new ApplicationStreams();
	private boolean terminated;
	private final ILaunch parentLaunch;
	private LocalScaWaveform waveform;
	private final String name;
	private final String identifier;
	private final String profile;
	private boolean started;
	private final Application delegate;
	private volatile boolean launching;

	public ApplicationImpl(final LocalScaWaveform waveform, final String identifier, final String name, Application delegate) {
		this.name = name;
		this.waveformContext = waveform.getNamingContext();
		this.identifier = identifier;
		this.parentLaunch = waveform.getLaunch();
		this.profile = waveform.getProfile();
		this.waveform = waveform;
		this.delegate = delegate;
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
			String msg = "Interrupted while waiting for application to launch";
			ScaDebugPlugin.logError(msg, e);
			return false;
		}

		if (this.delegate != null) {
			return this.delegate.started();
		}
		if (this.assemblyController != null) {
			return this.assemblyController.started();
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
			String msg = "Interrupted while waiting for application to launch";
			ScaDebugPlugin.logError(msg, e);
			throw new StartError(ErrorNumberType.CF_EINTR, msg);
		}

		this.streams.getOutStream().println("Starting...");

		if (this.delegate != null) {
			this.streams.getOutStream().println("\tInvoking delegate start");
			try {
				this.delegate.start();
			} catch (StartError e) {
				this.streams.getErrStream().println(CFErrorFormatter.format(e, "component " + this.name));
				throw e;
			}

			// Start local components added to the domain waveform
			for (ScaComponent component : waveform.getComponentsCopy()) {
				if (component instanceof LocalScaComponent && ((LocalScaComponent) component).getLaunch() != null) {
					this.streams.getOutStream().println("\t" + component.getInstantiationIdentifier());
					try {
						component.start();
					} catch (final StartError e) {
						this.streams.getErrStream().println(CFErrorFormatter.format(e, "component " + component.getName()));
						throw e;
					}
				}
			}
		} else {
			// Sort components
			List<ScaComponent> sortedSet = waveform.getComponentsCopy();
			Collections.sort(sortedSet, new ScaComponentComparator());

			for (ScaComponent component : sortedSet) {
				// With the exception of the assembly controller, don't start things that have a component
				// instantiation but don't have a start order (i.e. they're defined in a SAD without a start order)
				if (component != assemblyController && component.getComponentInstantiation() != null
					&& component.getComponentInstantiation().getStartOrder() == null) {
					continue;
				}
				this.streams.getOutStream().println("\t" + component.getInstantiationIdentifier());
				try {
					component.start();
				} catch (final StartError e) {
					this.streams.getErrStream().println(CFErrorFormatter.format(e, "component " + component.getName()));
					throw e;
				}
			}
		}

		this.streams.getOutStream().println("Started");
		this.started = true;
	}

	@Override
	public void stop() throws StopError {
		try {
			waitOnLaunch();
		} catch (InterruptedException e) {
			String msg = "Interrupted while waiting for application to launch";
			ScaDebugPlugin.logError(msg, e);
			throw new StopError(ErrorNumberType.CF_EINTR, msg);
		}

		this.streams.getOutStream().println("Stopping...");

		if (this.delegate != null) {
			// Stop local components added to the domain waveform
			List<ScaComponent> components = waveform.getComponentsCopy();
			Collections.reverse(components);
			for (ScaComponent component : components) {
				if (component instanceof LocalScaComponent && ((LocalScaComponent) component).getLaunch() != null) {
					this.streams.getOutStream().println("\t" + component.getInstantiationIdentifier());
					try {
						component.stop();
					} catch (final StopError e) {
						this.streams.getErrStream().println(CFErrorFormatter.format(e, "component " + component.getName()));
						throw e;
					}
				}
			}

			this.streams.getOutStream().println("\tInvoking delegate stop");
			try {
				this.delegate.stop();
			} catch (StopError e) {
				this.streams.getErrStream().println(CFErrorFormatter.format(e, "component " + this.name));
				throw e;
			}
		} else {
			List<ScaComponent> sortedSet = waveform.getComponentsCopy();
			Collections.sort(sortedSet, new ScaComponentComparator());
			Collections.reverse(sortedSet);

			for (ScaComponent component : sortedSet) {
				// With the exception of the assembly controller, don't stop things that have a component
				// instantiation but don't have a start order (i.e. they're defined in a SAD without a start order)
				if (component != assemblyController && component.getComponentInstantiation() != null
					&& component.getComponentInstantiation().getStartOrder() == null) {
					continue;
				}
				this.streams.getOutStream().println("\t" + component.getInstantiationIdentifier());
				try {
					component.stop();
				} catch (StopError e) {
					this.streams.getErrStream().println(CFErrorFormatter.format(e, "component " + component.getName()));
					throw e;
				}
			}
		}

		this.streams.getOutStream().println("Stopped");
		this.started = false;
	}

	@Override
	public void initialize() throws InitializeError {
		try {
			waitOnLaunch();
		} catch (InterruptedException e) {
			String msg = "Interrupted while waiting for application to launch";
			ScaDebugPlugin.logError(msg, e);
			throw new InitializeError(new String[] { msg });
		}

		if (this.delegate != null) {
			this.streams.getOutStream().println("Delegate Initializing application...");
			try {
				this.delegate.initialize();
				this.streams.getOutStream().println("Delegate Initialize succeeded");
			} catch (final InitializeError e) {
				this.streams.getErrStream().println(CFErrorFormatter.format(e, "component " + this.name));
				throw e;
			}
		} else {
			this.streams.getOutStream().println("Initializing application...");
			if (this.assemblyController == null) {
				return;
			}
			try {
				this.assemblyController.initialize();
				this.streams.getOutStream().println("Initialize succeeded");
			} catch (final InitializeError e) {
				this.streams.getErrStream().println(CFErrorFormatter.format(e, "component " + this.assemblyController.getName()));
				throw e;
			}
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
			String msg = "Interrupted while waiting for application to launch";
			ScaDebugPlugin.logError(msg, e);
			// Attempt to continue since this is a cleanup function
		}

		this.terminated = true;
		this.streams.getOutStream().println("Releasing Application...");

		try {
			if (delegate == null) {
				disconnectAll();
				releaseAll();
			}
			unbind();
		} catch (Exception e) { // SUPPRESS CHECKSTYLE Logged Catch all exception
			String msg = "Problems while releasing";
			this.streams.getErrStream().println(msg);
			this.streams.getErrStream().println(e.toString());
			ScaDebugPlugin.logError(msg, e);
		}

		this.streams.getOutStream().println("Release finished");
		this.assemblyController = null;
		this.waveformContext = null;
		fireTerminated();
	}

	protected void unbind() {
		if (this.waveformContext != null) {
			if (this.waveformContext.eContainer() instanceof NotifyingNamingContext) {
				this.streams.getOutStream().println("Unbinding application");
				try {
					NotifyingNamingContext localSCANamingContext = (NotifyingNamingContext) this.waveformContext.eContainer();
					localSCANamingContext.unbind(Name.toName(this.waveformContext.getFullName()));
				} catch (final NotFound e) {
					this.streams.getErrStream().println("Error while unbinding waveform context:\n" + e);
				} catch (final CannotProceed e) {
					this.streams.getErrStream().println("Error while unbinding waveform context:\n" + e);
				} catch (final InvalidName e) {
					this.streams.getErrStream().println("Error while unbinding waveform context:\n" + e);
				}
			}
			ScaDebugPlugin debugInstance = ScaDebugPlugin.getInstance();
			if (debugInstance != null) {
				final NamingContextExt context = getWaveformContext();
				if (context != null) {
					try {
						// Ugly code intended to handle namespaced applications (which must have dots escaped)
						context.unbind(Name.toName(this.name.replaceAll("\\.", "\\\\.")));
					} catch (NotFound e) {
						this.streams.getErrStream().println("Error while unbinding waveform:\n" + e);
					} catch (CannotProceed e) {
						this.streams.getErrStream().println("Error while unbinding waveform:\n" + e);
					} catch (InvalidName e) {
						this.streams.getErrStream().println("Error while unbinding waveform:\n" + e);
					} catch (final SystemException e) {
						this.streams.getErrStream().println("Error while unbinding waveform :\n" + e);
					}
				}
			}
		}
	}

	protected void releaseAll() {
		this.streams.getOutStream().println("Releasing components...");
		// Shutdown each component
		for (final ScaComponent component : this.waveform.getComponentsCopy()) {
			if (component instanceof LocalScaComponent && ((LocalScaComponent) component).getLaunch() != null) {
				release(component);
			}
		}
		this.streams.getOutStream().println("Released components");
	}

	protected void release(final ScaComponent info) {
		this.streams.getOutStream().println("\tReleasing component " + info.getName());
		try {
			info.releaseObject();
		} catch (ReleaseError e) {
			String msg = "Problems while releasing component " + info.getName();
			this.streams.getErrStream().println(msg);
			this.streams.getErrStream().println(CFErrorFormatter.format(e, "component " + info.getName()));
		} catch (final Exception e) { // SUPPRESS CHECKSTYLE Logged Catch all exception
			String msg = "Problems while releasing component " + info.getName();
			this.streams.getErrStream().println(msg);
			ScaDebugPlugin.logError(msg, e);
		}
	}

	protected void disconnectAll() {
		this.streams.getOutStream().println("Disconnecting connections...");
		// Disconnect components
		for (final ScaComponent component : this.waveform.getComponentsCopy()) {
			if (component instanceof LocalScaComponent && ((LocalScaComponent) component).getLaunch() != null) {
				disconnect(component);
			}
		}
		this.streams.getOutStream().println("Disconnected");
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
						String msg = "Problems while disconnecting connection " + c.getId();
						this.streams.getErrStream().println(msg);
						this.streams.getErrStream().println(CFErrorFormatter.format(e, "connection " + c.getId()));
					} catch (final Exception e) { // SUPPRESS CHECKSTYLE Logged Catch all exception
						String msg = "Problems while disconnecting connection " + c.getId();
						this.streams.getErrStream().println(msg);
						ScaDebugPlugin.logError(msg, e);
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
			String msg = "Interrupted while waiting for application to launch";
			ScaDebugPlugin.logError(msg, e);
			throw new UnknownTest();
		}

		if (this.delegate != null) {
			this.streams.getOutStream().println(String.format("Runing test '%d' on '%s'", testid, this.name));
			try {
				delegate.runTest(testid, testValues);
			} catch (final UnknownTest e) {
				this.streams.getErrStream().println(CFErrorFormatter.format(e, "component " + this.name));
				throw e;
			} catch (final UnknownProperties e) {
				this.streams.getErrStream().println(CFErrorFormatter.format(e, "component " + this.name));
				throw e;
			}
			this.streams.getOutStream().println("Delegate Run Test Succeeded");
		} else if (this.assemblyController != null) {
			this.streams.getOutStream().println(String.format("Runing test '%d' on '%s'", testid, this.assemblyController.getName()));
			try {
				this.assemblyController.runTest(testid, testValues);
				this.streams.getOutStream().println("Run Test Succeeded");
			} catch (final UnknownTest e) {
				this.streams.getErrStream().println(CFErrorFormatter.format(e, "component " + this.assemblyController.getName()));
				throw e;
			} catch (final UnknownProperties e) {
				this.streams.getErrStream().println(CFErrorFormatter.format(e, "component " + this.assemblyController.getName()));
				throw e;
			}
		}
	}

	@Override
	public void initializeProperties(final DataType[] configProperties) throws AlreadyInitialized, InvalidConfiguration, PartialConfiguration {
		throw new InvalidConfiguration("Cannot call initializeProperties on an application", configProperties);
	}

	@Override
	public void configure(final DataType[] configProperties) throws InvalidConfiguration, PartialConfiguration {
		try {
			waitOnLaunch();
		} catch (InterruptedException e) {
			String msg = "Interrupted while waiting for application to launch";
			ScaDebugPlugin.logError(msg, e);
			throw new InvalidConfiguration(msg, new DataType[0]);
		}

		this.streams.getOutStream().println("Configuring application: ");
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
			String msg = "Interrupted while waiting for application to launch";
			ScaDebugPlugin.logError(msg, e);
			throw new UnknownProperties(new DataType[0]);
		}

		Set<String> queryProperties = new HashSet<String>();
		if (configProperties.value != null) {
			for (DataType t : configProperties.value) {
				queryProperties.add(t.id);
			}
		}
		if (this.delegate != null) {
			this.streams.getOutStream().println("Delegate Query: " + queryProperties);
			this.delegate.query(configProperties);
			return;
		}

		this.streams.getOutStream().println("Query: " + queryProperties);
		SoftwareAssembly profileObj = this.waveform.fetchProfileObject(null);
		ExternalProperties externalProperties = null;
		if (profileObj != null) {
			externalProperties = this.waveform.getProfileObj().getExternalProperties();
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
						this.streams.getErrStream().println("Failed to find component for external property: " + prop.getPropID() + "@" + prop.getCompRefID());
					}
				}
			}
		}
		configProperties.value = retVal.toArray(new DataType[retVal.size()]);
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
		try {
			waitOnLaunch();
		} catch (InterruptedException e) {
			String msg = "Interrupted while waiting for application to launch";
			ScaDebugPlugin.logError(msg, e);
			throw new UnknownPort();
		}

		getStreamsProxy().getOutStream().println("Get port " + name);
		try {
			if (name == null) {
				throw new UnknownPort("No external port of name: " + name);
			}
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
							throw new UnknownPort("Internal component " + p.getComponentInstantiationRef().getRefid() + " not found, port unavailable.");
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
			this.streams.getErrStream().println(CFErrorFormatter.format(e, "port " + name));
			throw e;
		}
		throw new UnknownPort("No external port of name: " + name);
	}

	@Override
	public PortInfoType[] getPortSet() {
		try {
			waitOnLaunch();
		} catch (InterruptedException e) {
			String msg = "Interrupted while waiting for application to launch";
			ScaDebugPlugin.logError(msg, e);
			return new PortInfoType[0];
		}
		getStreamsProxy().getOutStream().println("Get port set");

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
				direction = "Provides";
			} else {
				nameOnComponent = p.getUsesIdentifier();
				direction = "Uses";
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
			String msg = "Interrupted while waiting for application to launch";
			ScaDebugPlugin.logError(msg, e);
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
			String msg = "Interrupted while waiting for application to launch";
			ScaDebugPlugin.logError(msg, e);
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
			types[i].elementId = this.name + "/" + components.get(i).getName();
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
			String msg = "Interrupted while waiting for application to launch";
			ScaDebugPlugin.logError(msg, e);
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
		return true;
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
			String msg = "Interrupted while waiting for application to launch";
			ScaDebugPlugin.logError(msg, e);
			// Attempt to continue since this is a cleanup function
		}

		this.terminated = true;
		this.streams.getOutStream().println("Terminating Application...");

		// Terminate the launch for each component
		for (ScaComponent component : this.waveform.getComponentsCopy()) {
			if (component instanceof LocalScaComponent) {
				String id = component.getIdentifier();
				try {
					((LocalScaComponent) component).getLaunch().terminate();
				} catch (DebugException e) {
					this.streams.getOutStream().println("Component " + id + " failed to terminate correctly");
				}
			}
		}

		try {
			unbind();
		} catch (Exception e) { // SUPPRESS CHECKSTYLE Logged Catch all exception
			String msg = "Problems while terminating";
			this.streams.getErrStream().println(msg);
			this.streams.getErrStream().println(e.toString());
			ScaDebugPlugin.logError(msg, e);
		}

		this.streams.getOutStream().println("Terminate finished");
		this.assemblyController = null;
		this.waveformContext = null;
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
			throw new DebugException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Application not terminated", null));
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
		release(oldComponent);
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
				// PASS
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
					this.streams.getErrStream().println("Failed to reconnect connection " + info.getConnectionID());
					this.streams.getErrStream().println(CFErrorFormatter.format(e, "connection " + info.getConnectionID()));
				} catch (final OccupiedPort e) {
					this.streams.getErrStream().println("Failed to reconnect connection " + info.getConnectionID());
					this.streams.getErrStream().println(CFErrorFormatter.format(e, "connection " + info.getConnectionID()));
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
							if (oldComponent.getObj()._is_equivalent(conn.getData().port)) {
								retVal.add(new FromConnectionInfo(uses, oldComponent.getInstantiationIdentifier(), null, conn));
							} else {
								for (final ScaPort< ? , ? > targetPort : oldComponent.getPorts()) {
									if (targetPort.getObj()._is_equivalent(conn.getData().port)) {
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
					retVal.add(new ToConnectionInfo(oldComponent.getInstantiationIdentifier(), uses.getName(), conn.getData().port, conn));
				}
			}
		}

		return retVal;
	}

	/**
	 * @deprecated Use {@link #launch(String, String, DataType[], URI, String, String)}
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
			retVal = launch(null, compId, initConfiguration, uri, implId, mode);
		} catch (final CoreException e) {
			this.getStreams().getErrStream().println("Failed to launch component " + compId);
			this.getStreams().getErrStream().println(e.toString());
			throw new ExecuteFail(ErrorNumberType.CF_EFAULT, e.getStatus().getMessage());
		}
		return retVal.getObj();
	}

	@NonNull
	public LocalScaComponent launch(final String usageName, String compId, final DataType[] initConfiguration, @NonNull final URI spdURI, final String implId,
		final String mode) throws CoreException {
		ILaunchConfigurationWorkingCopy config = createLaunchConfig(usageName, compId, initConfiguration, spdURI, implId, mode);

		final ILaunch subLaunch = config.launch(mode, new NullProgressMonitor(), false);
		if (subLaunch instanceof ComponentLaunch) {
			((ComponentLaunch) subLaunch).setParent(this);
		}
		this.streams.getOutStream().println("\tLaunch configuration succeeded.");

		return postLaunch(subLaunch);
	}

	/**
	 * @deprecated Use {@link #launch(String, String, DataType[], URI, String, String)}
	 */
	@Deprecated
	public LocalScaComponent launch(final String usageName, final DataType[] initConfiguration, @NonNull final URI spdURI, final String implId,
		final String mode) throws CoreException {
		return launch(usageName, null, initConfiguration, spdURI, implId, mode);
	}

	/**
	 * @deprecated Use {@link #launch(String, String, DataType[], URI, String, String)}
	 */
	@Deprecated
	@NonNull
	public LocalScaComponent launch(@Nullable String usageName, @Nullable final String compId, @Nullable final String execParams, @NonNull URI spdURI,
		@Nullable final String implId, @Nullable String mode) throws CoreException {
		ILaunchConfigurationWorkingCopy config = createLaunchConfig(usageName, compId, null, spdURI, implId, mode);
		if (execParams != null && execParams.length() > 0) {
			this.streams.getOutStream().println("\tExec params: " + execParams);
			config.setAttribute(LaunchVariables.EXEC_PARAMS, execParams);
		} else {
			this.streams.getOutStream().println("\tUsing default exec params.");
		}

		final ILaunch subLaunch = config.launch(mode, new NullProgressMonitor(), false);
		if (subLaunch instanceof ComponentLaunch) {
			((ComponentLaunch) subLaunch).setParent(this);
		}
		this.streams.getOutStream().println("\tLaunch configuration succeeded.");

		return postLaunch(subLaunch);
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
		this.streams.getOutStream().println("Launching component: " + spd.getName());
		final ILaunchConfigurationFactory factory = ScaDebugPlugin.getInstance().getLaunchConfigurationFactoryRegistry().getFactory(spd, implId);
		if (factory == null) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to obtain Launch Factory for impl: " + implId));
		}
		final ILaunchConfigurationWorkingCopy config = factory.createLaunchConfiguration(spd.getName(), implId, spd);

		// Create a name for the component
		final NameComponent[] spdContextName = this.waveformContext.getName(spdURI);
		String spdContextNameStr;
		try {
			spdContextNameStr = Name.toString(spdContextName);
		} catch (final InvalidName e) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to create sub context for spd.", e));
		}

		// Bind the component's name in the waveform's naming context
		NamingContext spdContext;
		try {
			spdContext = this.waveformContext.bind_new_context(spdContextName);
		} catch (final NotFound e) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to create sub context for spd.", e));
		} catch (final AlreadyBound e) {
			try {
				spdContext = NamingContextHelper.narrow(this.waveformContext.resolve(spdContextName));
			} catch (final NotFound e1) {
				throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to create sub context for spd: " + spdContextNameStr, e1));
			} catch (final CannotProceed e1) {
				throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to create sub context for spd: " + spdContextNameStr, e1));
			} catch (final InvalidName e1) {
				throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to create sub context for spd: " + spdContextNameStr, e1));
			}
		} catch (final CannotProceed e) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to create sub context for spd: " + spdContextNameStr, e));
		} catch (final InvalidName e) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to create sub context for spd: " + spdContextNameStr, e));
		}
		config.setAttribute(LaunchVariables.NAMING_CONTEXT_IOR, spdContext.toString());
		String instID = ScaComponentImpl.convertIdentifierToInstantiationID(compId);
		if (usageName == null && compId != null) {
			usageName = instID;
		}

		// Waveform name
		config.setAttribute(LaunchVariables.WAVEFORM_NAME, this.name);
		if (usageName != null) {
			this.streams.getOutStream().println("\tLaunching with name: " + usageName);
			config.setAttribute(LaunchVariables.NAME_BINDING, usageName);
		}

		// Component identifier
		if (compId != null) {
			this.streams.getOutStream().println("\tLaunching with id: " + compId);
			config.setAttribute(LaunchVariables.COMPONENT_IDENTIFIER, compId);
		}

		// Property overrides
		if (initConfiguration != null) {
			final ScaComponent propHolder = ScaFactory.eINSTANCE.createScaComponent();
			propHolder.setProfileURI(spdURI);
			propHolder.fetchProfileObject(new NullProgressMonitor());
			propHolder.fetchProperties(new NullProgressMonitor());
			for (DataType dt : initConfiguration) {
				ScaAbstractProperty< ? > prop = propHolder.getProperty(dt.id);
				if (prop != null) {
					prop.fromAny(dt.value);
				} else {
					this.streams.getErrStream().println("\tIgnoring invalid property: " + dt.id);
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
		this.streams.getOutStream().println("\tCalling launch on configuration...");

		return config;
	}

	private LocalScaComponent postLaunch(ILaunch launch) throws CoreException {
		LocalScaComponent newComponent = null;
		final String newCompId = launch.getAttribute(LaunchVariables.COMPONENT_IDENTIFIER);
		if (newCompId != null) {
			for (final ScaComponent comp : ApplicationImpl.this.waveform.getComponentsCopy()) {
				comp.fetchAttributes(null);
				final String id = comp.getIdentifier();
				if (id.equals(newCompId)) {
					newComponent = (LocalScaComponent) comp;
					break;
				}
			}
		}

		if (newComponent == null) {
			launch.terminate();
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to find component after launch", null));
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
	public void setLogLevel(String loggerId, int newLevel) throws UnknownIdentifier {
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
