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

import gov.redhawk.ide.debug.ILaunchConfigurationFactory;
import gov.redhawk.ide.debug.LocalScaComponent;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.NotifyingNamingContext;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.SpdLauncherUtil;
import gov.redhawk.ide.debug.internal.ApplicationStreams;
import gov.redhawk.ide.debug.variables.LaunchVariables;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaConnection;
import gov.redhawk.model.sca.ScaPort;
import gov.redhawk.model.sca.ScaUsesPort;
import gov.redhawk.model.sca.ScaWaveform;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.sad.ExternalPorts;
import mil.jpeojtrs.sca.sad.Port;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.AnyUtils;
import mil.jpeojtrs.sca.util.NamedThreadFactory;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

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
import org.jacorb.naming.Name;
import org.omg.CORBA.SystemException;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import CF.ApplicationOperations;
import CF.ComponentEnumType;
import CF.ComponentType;
import CF.DataType;
import CF.DeviceAssignmentType;
import CF.ErrorNumberType;
import CF.PortType;
import CF.PropertiesHolder;
import CF.Resource;
import CF.UnknownProperties;
import CF.ApplicationPackage.ComponentElementType;
import CF.ApplicationPackage.ComponentProcessIdType;
import CF.ExecutableDevicePackage.ExecuteFail;
import CF.LifeCyclePackage.InitializeError;
import CF.LifeCyclePackage.ReleaseError;
import CF.PortPackage.InvalidPort;
import CF.PortPackage.OccupiedPort;
import CF.PortSupplierPackage.UnknownPort;
import CF.PropertySetPackage.InvalidConfiguration;
import CF.PropertySetPackage.PartialConfiguration;
import CF.ResourcePackage.StartError;
import CF.ResourcePackage.StopError;
import CF.TestableObjectPackage.UnknownTest;

/**
 * 
 */
public class ApplicationImpl extends PlatformObject implements IProcess, ApplicationOperations, IAdaptable {
	private static final ExecutorService APP_EXECUTOR = Executors.newCachedThreadPool(new NamedThreadFactory(ApplicationImpl.class.getName()));

	private static interface ConnectionInfo {
		String getConnectionID();

		void disconnect();

		void reconnect(final ScaWaveform waveform) throws InvalidPort, OccupiedPort;
	}

	private class ScaComponentComparator implements Comparator<ScaComponent> {

		/**
		 * Compare on the start order as the first priority, if no start order is found, compare on the pointer location.
		 */
		public int compare(ScaComponent o1, ScaComponent o2) {
			if (o1 == o2) {
				return 0;
			}
			if (o1 == assemblyController) {
				return -1;
			} else if (o2 == assemblyController) {
				return 1;
			} else {

				SadComponentInstantiation ci1 = o1.getComponentInstantiation();
				int o1Index = o1.eContainer().eContents().indexOf(o1);

				SadComponentInstantiation ci2 = o2.getComponentInstantiation();
				int o2Index = o2.eContainer().eContents().indexOf(o2);

				// If neither have start order we'll order them on list order.
				if (ci1 == null && ci2 == null) {
					return (o1Index < o2Index) ? -1 : 1;
				}

				// If c1 != null but ci2 is
				if (ci2 == null) {
					return -1;
				}

				// If c2 != null but ci1 is
				if (ci1 == null) {
					return 1;
				}

				// Neither ci1 or ci2 is null
				BigInteger s1 = ci1.getStartOrder();
				BigInteger s2 = ci2.getStartOrder();
				if (s1 != null) {
					return s1.compareTo(s2);
				} else if (s2 != null) {
					return 1;
				} else {
					return (o1Index < o2Index) ? -1 : 1;
				}
			}
		}
	}

	private static class FromConnectionInfo implements ConnectionInfo {
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

		public void disconnect() {
			try {
				this.connection.getPort().disconnectPort(this.connection);
			} catch (final InvalidPort e) {
				// PASS
			}
		}

		public void reconnect(final ScaWaveform waveform) throws InvalidPort, OccupiedPort {
			for (final ScaComponent comp : waveform.getComponents()) {
				if (comp.getInstantiationIdentifier().equals(this.targetCompID)) {
					if (this.targetPort == null) {
						this.port.connectPort(comp.getCorbaObj(), this.connectionID);
						return;
					}
				}
			}
		}

		public String getConnectionID() {
			return this.connectionID;
		}
	}

	private static class ToConnectionInfo implements ConnectionInfo {
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

		public void reconnect(final ScaWaveform waveform) throws InvalidPort, OccupiedPort {
			for (final ScaComponent comp : waveform.getComponents()) {
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

		public void disconnect() {
			try {
				this.connection.getPort().disconnectPort(this.connection);
			} catch (final InvalidPort e) {
				// PASS
			}
		}

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
	private ExternalPorts externalPorts;
	private final String assemblyID;
	private LocalScaWaveform waveform;
	private final String name;
	private final String identifier;
	private final String profile;
	private final URI profileURI;
	private boolean started;

	public ApplicationImpl(final LocalScaWaveform waveform, final String identifier, final String name) {
		super();
		this.profileURI = waveform.getProfileURI();
		this.name = name;
		this.waveformContext = waveform.getNamingContext();
		this.identifier = identifier;
		this.parentLaunch = waveform.getLaunch();
		this.profile = waveform.getProfile();
		this.assemblyID = ApplicationImpl.getAssemblyControllerID(waveform.getProfileObj());
		this.waveform = waveform;
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

	/**
	 * {@inheritDoc}
	 */
	public String identifier() {
		return this.identifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean started() {
		if (this.assemblyController != null) {
			return this.assemblyController.started();
		}
		return started;
	}

	public ApplicationStreams getStreams() {
		return this.streams;
	}

	/**
	 * {@inheritDoc}
	 */
	public void start() throws StartError {
		this.streams.getOutStream().println("Starting...");

		SortedSet<ScaComponent> sortedSet = new TreeSet<ScaComponent>(new ScaComponentComparator());

		for (ScaComponent comp : waveform.getComponents()) {
			if (comp.getInstantiationIdentifier() != null) {
				sortedSet.add(comp);
			}
		}

		for (ScaComponent comp : sortedSet) {
			this.streams.getOutStream().println("\t" + comp.getInstantiationIdentifier());
			try {
				comp.start();
			} catch (final StartError e) {
				throw logException("Error during start", e);
			}
		}

		this.streams.getOutStream().println("Start succeeded");
		this.started = true;
	}

	/**
	 * {@inheritDoc}
	 */
	public void stop() throws StopError {
		this.streams.getOutStream().println("Stopping...");

		if (this.assemblyController == null) {
			TreeSet<ScaComponent> sortedSet = new TreeSet<ScaComponent>(new ScaComponentComparator() {
				@Override
				public int compare(ScaComponent o1, ScaComponent o2) {
					// Reverse the order for stopping
					return -1 * super.compare(o1, o2);
				}
			});
			for (ScaComponent comp : waveform.getComponents()) {
				sortedSet.add(comp);
			}

			for (ScaComponent comp : sortedSet) {
				this.streams.getOutStream().println("\t" + comp.getInstantiationIdentifier());
				comp.stop();
			}
		} else {
			try {
				this.assemblyController.stop();
				this.streams.getOutStream().println("Stop succeeded");
			} catch (final StopError e) {
				throw logException("Error during stop", e);
			}
		}
		this.streams.getOutStream().println("Stopped");
		this.started = false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void initialize() throws InitializeError {
		this.streams.getOutStream().println("Initializing application...");
		if (this.assemblyController == null) {
			return;
		}
		try {
			this.assemblyController.initialize();
			this.streams.getOutStream().println("Initialize succeeded");
		} catch (final InitializeError e) {
			throw logException("Error during initialize", e);
		}
	}

	private < T extends Throwable > T logException(final T e) {
		this.streams.getErrStream().printStackTrace("\n\n" + e.getMessage(), e); // SUPPRESS CHECKSTYLE OUTPUT
		return e;
	}

	private < T extends Throwable > T logException(final String msg, final T e) {
		this.streams.getErrStream().printStackTrace("\n\n" + msg, e); // SUPPRESS CHECKSTYLE OUTPUT
		return e;
	}

	/**
	 * {@inheritDoc}
	 */
	public void releaseObject() throws ReleaseError {
		if (this.terminated) {
			return;
		}
		this.terminated = true;
		this.streams.getOutStream().println("Releasing Application...");

		try {
			disconnectAll();

			releaseAll();

			unbind();
		} catch (Exception e) {
			logException("Problems while releasing.", e);
		}

		this.streams.getOutStream().println("Release finished");
		this.assemblyController = null;
		this.waveformContext = null;
		fireTerminated();
	}

	/**
	 * 
	 */
	protected void unbind() {
		if (this.waveformContext != null) {

			if (this.waveformContext.eContainer() instanceof NotifyingNamingContext) {
				this.streams.getOutStream().println("Unbinding application");

				try {
					NotifyingNamingContext localSCANamingContext = (NotifyingNamingContext) this.waveformContext.eContainer();
					localSCANamingContext.unbind(Name.toName(this.name));
				} catch (NotFound e) {
					this.streams.getErrStream().println("Error while unbinding waveform:\n" + e);
				} catch (CannotProceed e) {
					this.streams.getErrStream().println("Error while unbinding waveform:\n" + e);
				} catch (InvalidName e) {
					this.streams.getErrStream().println("Error while unbinding waveform:\n" + e);
				}
			}

			final NotifyingNamingContext rootContext = ScaDebugPlugin.getInstance().getLocalSca().getRootContext();
			final NotifyingNamingContext resourceContext = rootContext.getResourceContext(this.profileURI);

			try {
				resourceContext.unbind(Name.toName(this.name));
			} catch (final NotFound e) {
				this.streams.getErrStream().println("Error while unbinding waveform context:\n" + e);
			} catch (final CannotProceed e) {
				this.streams.getErrStream().println("Error while unbinding waveform context:\n" + e);
			} catch (final InvalidName e) {
				this.streams.getErrStream().println("Error while unbinding waveform context:\n" + e);
			} catch (final SystemException e) {
				this.streams.getErrStream().println("Error while unbinding waveform context:\n" + e);
			}

			try {
				this.waveformContext.destroy();
			} catch (final NotEmpty e) {
				this.streams.getErrStream().println("Error while destorying waveform context:\n" + e);
			} catch (final SystemException e) {
				this.streams.getErrStream().println("Error while destorying waveform context:\n" + e);
			}
		}
	}

	/**
	 * 
	 */
	protected void releaseAll() {
		this.streams.getOutStream().println("Releasing components...");
		// Shutdown each component
		for (final ScaComponent info : this.waveform.getComponents().toArray(new ScaComponent[this.waveform.getComponents().size()])) {
			release(info);
		}
		this.streams.getOutStream().println("Released components");
	}

	/**
	 * @param info
	 */
	protected void release(final ScaComponent info) {
		this.streams.getOutStream().println("\tReleasing component " + info.getName());
		try {
			info.releaseObject();
		} catch (final Exception e) {
			logException("Problems while releasing: " + info.getName(), e);
		}
	}

	/**
	 * 
	 */
	protected void disconnectAll() {
		this.streams.getOutStream().println("Disconnecting connections...");
		// Disconnect components
		for (final ScaComponent info : this.waveform.getComponents().toArray(new ScaComponent[waveform.getComponents().size()])) {
			disconnect(info);
		}
		this.streams.getOutStream().println("Disconnected");
	}

	/**
	 * @param info
	 */
	protected void disconnect(final ScaComponent comp) {
		for (final ScaPort< ? , ? > port : comp.getPorts().toArray(new ScaPort< ? , ? >[comp.getPorts().size()])) {
			if (port instanceof ScaUsesPort) {
				final ScaUsesPort up = (ScaUsesPort) port;
				final ScaConnection[] connections = up.getConnections().toArray(new ScaConnection[up.getConnections().size()]);
				for (final ScaConnection c : connections) {
					try {
						up.disconnectPort(c);
					} catch (final Exception e) {
						logException("Problems while disconnecting: " + c.getId(), e);
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

	/**
	 * {@inheritDoc}
	 */
	public void runTest(final int testid, final PropertiesHolder testValues) throws UnknownTest, UnknownProperties {
		this.streams.getOutStream().println("Runing Test: " + testValues);
		if (this.assemblyController == null) {
			return;
		}
		try {
			this.assemblyController.runTest(testid, testValues);
			this.streams.getOutStream().println("Run Test Succeeded");
		} catch (final UnknownTest e) {
			throw logException("Errors during Run Test", e);
		} catch (final UnknownProperties e) {
			throw logException("Errors during Run Test", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void configure(final DataType[] configProperties) throws InvalidConfiguration, PartialConfiguration {
		final Map<String, String> propertyMap = new HashMap<String, String>();
		for (final DataType type : configProperties) {
			propertyMap.put(type.id, type.value.toString());
		}
		this.streams.getOutStream().println("Configuring application: " + propertyMap);
		if (this.assemblyController == null) {
			return;
		}
		try {
			this.assemblyController.configure(configProperties);
			this.streams.getOutStream().println("Configure succeeded");
		} catch (final InvalidConfiguration e) {
			throw logException("Error during configure", e);
		} catch (final PartialConfiguration e) {
			throw logException("Error during configure", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void query(final PropertiesHolder configProperties) throws UnknownProperties {
		final List<String> properties = new ArrayList<String>();
		for (final DataType type : configProperties.value) {
			properties.add(type.id);
		}
		this.streams.getOutStream().println("Query " + properties);
		if (this.assemblyController == null) {
			return;
		}
		try {
			this.assemblyController.query(configProperties);
			this.streams.getOutStream().println("Query succeeded");
		} catch (final UnknownProperties e) {
			throw logException("Error during query", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public org.omg.CORBA.Object getPort(final String name) throws UnknownPort {
		getStreamsProxy().getOutStream().println("Get port " + name);
		try {
			if (name == null) {
				throw new UnknownPort("No external port of name: " + name);
			}
			if (this.externalPorts != null) {
				for (final Port p : this.externalPorts.getPort()) {
					if (name.equals(p.getProvidesIndentifier()) || name.equals(p.getUsesIdentifier())) {
						final ScaComponent comp = findComponent(p.getComponentInstantiationRef().getRefid());
						if (comp != null) {
							return comp.getPort(name);
						}
					} else if (name.equals(p.getSupportedIdentifier())) {
						final ScaComponent comp = findComponent(p.getComponentInstantiationRef().getRefid());
						if (comp != null) {
							return comp.getCorbaObj();
						}
					}
				}
			}
			throw new UnknownPort("No external port of name: " + name);
		} catch (final UnknownPort e) {
			throw logException("Error during get port " + name, e);
		}
	}

	private ScaComponent findComponent(final String instId) {
		for (final ScaComponent comp : this.waveform.getComponents()) {
			if (comp.getInstantiationIdentifier().equals(instId)) {
				return comp;
			}
		}
		return null;
	}

	public ExternalPorts getExternalPorts() {
		return this.externalPorts;
	}

	public void setExternalPorts(final ExternalPorts externalPorts) {
		this.externalPorts = externalPorts;
	}

	/**
	 * {@inheritDoc}
	 */
	public ComponentType[] registeredComponents() {
		final ComponentType[] types = new ComponentType[this.waveform.getComponents().size()];
		for (int i = 0; i < types.length; i++) {
			types[i] = new ComponentType();
			types[i].componentObject = this.waveform.getComponents().get(i).getCorbaObj();
			types[i].identifier = this.waveform.getComponents().get(i).getIdentifier();
			types[i].softwareProfile = this.waveform.getComponents().get(i).getProfileObj().eResource().getURI().path();
			types[i].providesPorts = new PortType[0];
			types[i].type = ComponentEnumType.APPLICATION_COMPONENT;
		}
		return types;
	}

	/**
	 * {@inheritDoc}
	 */
	public ComponentElementType[] componentNamingContexts() {
		final ComponentElementType[] types = new ComponentElementType[this.waveform.getComponents().size()];
		for (int i = 0; i < types.length; i++) {
			types[i] = new ComponentElementType();
			types[i].componentId = this.waveform.getComponents().get(i).getIdentifier();
			types[i].elementId = this.name + "/" + this.waveform.getComponents().get(i).getName();
		}
		return types;
	}

	/**
	 * {@inheritDoc}
	 */
	public ComponentProcessIdType[] componentProcessIds() {
		return new ComponentProcessIdType[0];
	}

	/**
	 * {@inheritDoc}
	 */
	public DeviceAssignmentType[] componentDevices() {
		return new DeviceAssignmentType[0];
	}

	/**
	 * {@inheritDoc}
	 */
	public ComponentElementType[] componentImplementations() {
		final ComponentElementType[] types = new ComponentElementType[this.waveform.getComponents().size()];
		for (int i = 0; i < types.length; i++) {
			final ScaComponent comp = this.waveform.getComponents().get(i);

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
		}
		return types;
	}

	/**
	 * {@inheritDoc}
	 */
	public String profile() {
		return this.profile;
	}

	/**
	 * {@inheritDoc}
	 */
	public String name() {
		return this.name;
	}

	public boolean canTerminate() {
		return !this.terminated;
	}

	public boolean isTerminated() {
		return this.terminated;
	}

	public void terminate() throws DebugException {
		try {
			releaseObject();
		} catch (final ReleaseError e) {
			throw new DebugException(new Status(IStatus.WARNING, ScaDebugPlugin.ID, "Problems releasing application.", e));
		}
	}

	public String getLabel() {
		return this.name;
	}

	public ILaunch getLaunch() {
		return this.parentLaunch;
	}

	public ApplicationStreams getStreamsProxy() {
		return this.streams;
	}

	public void setAttribute(final String key, final String value) {
		// No Attributes
	}

	public String getAttribute(final String key) {
		// No Attributes
		return null;
	}

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
		this.streams.getOutStream().println("Reseting component " + compInstId);

		LocalScaComponent oldComponent = null;
		for (final Iterator<ScaComponent> iterator = this.waveform.getComponents().iterator(); iterator.hasNext();) {
			final ScaComponent info = iterator.next();
			if (compInstId.equals(info.getInstantiationIdentifier())) {
				if (!(info instanceof LocalScaComponent)) {
					// This should never happen but check for it anyway
					throw logException(new ReleaseError(new String[] { "Can only reset local components" }));
				}
				oldComponent = (LocalScaComponent) info;
				break;
			}
		}

		if (oldComponent == null) {
			this.streams.getErrStream().println("No component " + compInstId);
			throw new ReleaseError(new String[] { "Unknown component: " + compInstId });
		}
		final String usageName = oldComponent.getName();
		final String execParams = oldComponent.getExecParam();
		final URI spdUri = oldComponent.getProfileURI();
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
			final LocalScaComponent retVal = launch(usageName, execParams, spdUri, implId, mode);
			this.streams.getOutStream().println("Reconnecting component");
			makeConnections(oldConnections);
			this.streams.getOutStream().println("Done resetting component " + oldComponent.getName());
			return retVal.getObj();
		} catch (final CoreException e) {
			ScaDebugPlugin.getInstance().getLog().log(e.getStatus());
			logException(e);
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
					this.streams.getErrStream().println("Failed to reconnect " + info.getConnectionID() + " " + e.msg);
				} catch (final OccupiedPort e) {
					this.streams.getErrStream().println("Failed to reconnect " + info.getConnectionID() + " " + e.getMessage());
				}
			}
		} catch (final InterruptedException e) {
			// PASS
		}
	}

	private List<ConnectionInfo> getConnectionInfo(final LocalScaComponent oldComponent) {
		final List<ConnectionInfo> retVal = new ArrayList<ApplicationImpl.ConnectionInfo>();

		// Create list of connections that connect to me
		for (final ScaComponent comp : this.waveform.getComponents()) {
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

	public Resource launch(final String usageName, final DataType[] execParams, final String spdURI, final String implId, final String mode) throws ExecuteFail {
		Assert.isNotNull(spdURI, "SPD URI must not be null");
		LocalScaComponent retVal;
		try {
			retVal = launch(usageName, execParams, URI.createURI(spdURI), implId, mode);
		} catch (final CoreException e) {
			ScaDebugPlugin.getInstance().getLog().log(e.getStatus());
			logException(e);
			throw new ExecuteFail(ErrorNumberType.CF_EFAULT, e.getStatus().getMessage());
		}
		return retVal.getObj();
	}

	public LocalScaComponent launch(final String usageName, final DataType[] execParams, final URI spdURI, final String implId, final String mode)
	        throws CoreException {
		return launch(usageName, createExecParamStr(execParams), spdURI, implId, mode);
	}

	private LocalScaComponent launch(final String nameBinding, final String execParams, final URI spdURI, final String implId, final String tmpMode)
	        throws CoreException {
		Assert.isNotNull(spdURI, "SPD URI must not be null");
		final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		final SoftPkg spd = SoftPkg.Util.getSoftPkg(resourceSet.getResource(spdURI, true));
		final String mode;
		if (tmpMode == null) {
			mode = ILaunchManager.RUN_MODE;
		} else {
			mode = tmpMode;
		}

		this.streams.getOutStream().println("Launching component: " + spd.getName());
		final ILaunchConfigurationFactory factory = ScaDebugPlugin.getInstance().getLaunchConfigurationFactoryRegistry().getFactory(spd, implId);
		if (factory == null) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to obtain Launch Factory for impl: " + implId, null));
		}
		final ILaunchConfigurationWorkingCopy config = factory.createLaunchConfiguration(spd.getName(), implId, spd);

		final NameComponent[] spdContextName = this.waveformContext.getName(spdURI);
		final String spdContextNameStr;
		try {
			spdContextNameStr = Name.toString(spdContextName);
		} catch (final InvalidName e) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to create sub context for spd.", e));
		}

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
		if (nameBinding != null) {
			config.setAttribute(LaunchVariables.NAME_BINDING, nameBinding);
		}
		if (execParams != null && execParams.length() > 0) {
			this.streams.getOutStream().println("\tExec params: " + execParams);
			config.setAttribute(LaunchVariables.EXEC_PARAMS, execParams);
		} else {
			this.streams.getOutStream().println("\tUsing default exec params.");
		}

		this.streams.getOutStream().println("\tCalling launch on configuration...");
		final ILaunch subLaunch = config.launch(mode, new NullProgressMonitor(), false);
		this.streams.getOutStream().println("\tLaunch configuration succeeded.");

		LocalScaComponent newComponent = null;
		final String newCompId = subLaunch.getAttribute(LaunchVariables.COMPONENT_IDENTIFIER);
		if (newCompId != null) {
			for (final ScaComponent comp : ApplicationImpl.this.waveform.getComponents()) {
				comp.fetchAttributes(null);
				final String compId = comp.getIdentifier();
				if (compId.equals(newCompId)) {
					newComponent = (LocalScaComponent) comp;
					break;
				}
			}
		}

		if (newComponent == null) {
			subLaunch.terminate();
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to find component after launch", null));
		}

		// Add Child processes
		for (final IProcess process : subLaunch.getProcesses()) {
			if (this.parentLaunch != null) {
				this.parentLaunch.addProcess(process);
			}
		}

		if (this.assemblyID != null && this.assemblyID.equals(newCompId)) {
			this.assemblyController = newComponent;
		}
		return newComponent;
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

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

}
