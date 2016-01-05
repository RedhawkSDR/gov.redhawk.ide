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
package gov.redhawk.ide.graphiti.dcd.internal.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.ui.progress.UIJob;

import CF.DataType;
import CF.ExecutableDevicePackage.ExecuteFail;
import CF.PortPackage.InvalidPort;
import CF.PortPackage.OccupiedPort;
import gov.redhawk.ide.debug.LocalAbstractComponent;
import gov.redhawk.ide.debug.impl.LocalScaDeviceManagerImpl;
import gov.redhawk.ide.graphiti.dcd.ext.DeviceShape;
import gov.redhawk.ide.graphiti.dcd.internal.ui.editor.GraphitiDcdSandboxEditor;
import gov.redhawk.ide.graphiti.dcd.ui.DCDUIGraphitiPlugin;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.features.create.DeviceCreateFeature;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.patterns.DCDConnectInterfacePattern;
import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.internal.ui.AbstractGraphitiModelMap;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.model.sca.ScaConnection;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.ScaDeviceManager;
import gov.redhawk.model.sca.ScaPort;
import gov.redhawk.model.sca.ScaProvidesPort;
import gov.redhawk.model.sca.ScaUsesPort;
import gov.redhawk.model.sca.commands.NonDirtyingCommand;
import gov.redhawk.model.sca.util.ReleaseJob;
import gov.redhawk.sca.util.SubMonitor;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.dcd.DcdConnectInterface;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.dcd.impl.DcdComponentInstantiationImpl;
import mil.jpeojtrs.sca.partitioning.ConnectionTarget;
import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.prf.AbstractPropertyRef;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

public class GraphitiDcdModelMap extends AbstractGraphitiModelMap {

	private static final EStructuralFeature[] CONN_INST_PATH = new EStructuralFeature[] { PartitioningPackage.Literals.CONNECT_INTERFACE__USES_PORT,
		PartitioningPackage.Literals.USES_PORT__COMPONENT_INSTANTIATION_REF, PartitioningPackage.Literals.COMPONENT_INSTANTIATION_REF__INSTANTIATION };
	private static final EStructuralFeature[] SPD_PATH = new EStructuralFeature[] { PartitioningPackage.Literals.COMPONENT_INSTANTIATION__PLACEMENT,
		PartitioningPackage.Literals.COMPONENT_PLACEMENT__COMPONENT_FILE_REF, PartitioningPackage.Literals.COMPONENT_FILE_REF__FILE,
		PartitioningPackage.Literals.COMPONENT_FILE__SOFT_PKG };

	private final GraphitiDcdSandboxEditor editor;
	private final ScaDeviceManager deviceManager;

	// maps containing to uniquely identify devices/connections, use with synchronized statement
	private final Map<String, DcdNodeMapEntry> nodes = Collections.synchronizedMap(new HashMap<String, DcdNodeMapEntry>());
	private final Map<String, DcdConnectionMapEntry> connections = Collections.synchronizedMap(new HashMap<String, DcdConnectionMapEntry>());

	public GraphitiDcdModelMap(final GraphitiDcdSandboxEditor editor, final DeviceConfiguration dcd, final ScaDeviceManager deviceManager) {
		super(editor);
		Assert.isNotNull(deviceManager, "Device Manager must not be null");
		Assert.isNotNull(dcd, "Device Configuration must not be null");
		this.deviceManager = deviceManager;
		this.editor = editor;
	}

	/********************** REDHAWK EXPLORER TO DIAGRAM *************************/
	/*	These actions are fired when interacting with a shape in the REDHAWK	*/
	/*	Explorer View and the results are reflected in the Graphiti Diagram		*/
	/****************************************************************************/

	/**
	 * New ScaDevice was recently added and this method will now add
	 * a DcdComponentInstiation to the DeviceConfiguration of the Graphiti Diagram.
	 */
	public void add(final ScaDevice< ? > device) {

		final DcdNodeMapEntry nodeMapEntry = new DcdNodeMapEntry();
		nodeMapEntry.setScaDevice(device);
		synchronized (nodes) {
			// Do a merge of the nodeMapEntry to populate missing ScaDevice content
			// This occurs when adding from the diagram palette
			if (nodes.get(nodeMapEntry.getKey()) != null) {
				DcdNodeMapEntry entry = nodes.get(nodeMapEntry.getKey());
				if (entry.getScaDevice() == null) {
					entry.setScaDevice(device);
					updateEnabledState(entry.getProfile(), true);
					editor.deviceRegistered(entry.getProfile());
				}
				return;
			} else {
				nodes.put(nodeMapEntry.getKey(), nodeMapEntry);
			}
		}
		Job job = new Job("Adding device: " + device.getIdentifier()) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				SubMonitor subMonitor = SubMonitor.convert(monitor, "Adding device: " + device.getIdentifier(), IProgressMonitor.UNKNOWN);
				DcdComponentInstantiation newDevice = null;
				try {
					newDevice = GraphitiDcdModelMap.this.create(device);
					nodeMapEntry.setProfile(newDevice);

					return Status.OK_STATUS;
				} catch (CoreException e) {
					nodes.remove(nodeMapEntry.getKey());
					return new Status(IStatus.ERROR, DCDUIGraphitiPlugin.PLUGIN_ID, "Failed to add device " + device.getIdentifier(), e);
				} finally {
					if (nodes.get(nodeMapEntry.getKey()) == null) {
						delete(newDevice);
					}

					// Make sure device is started/stopped as appropriate
					reflectRuntimeStatus();

					subMonitor.done();
				}
			}

		};
		job.schedule();
	}

	/**
	 * Create DcdComponentInstantiation from the provided ScaDevice.
	 * Add the DcdComponentInstantiation to the diagram
	 * @param newValue
	 * @return new DcdComponentInstantiation
	 * @throws CoreException
	 */
	private DcdComponentInstantiation create(final ScaDevice< ? > newValue) throws CoreException {

		// get SoftPkg
		newValue.fetchAttributes(null);
		final SoftPkg spd = newValue.fetchProfileObject(null);
		if (spd == null) {
			throw new IllegalStateException("Unable to load new devices spd");
		}

		// setup for transaction in diagram
		final IDiagramTypeProvider provider = editor.getDiagramEditor().getDiagramTypeProvider();
		final IFeatureProvider featureProvider = provider.getFeatureProvider();
		final Diagram diagram = provider.getDiagram();
		final TransactionalEditingDomain editingDomain = (TransactionalEditingDomain) editor.getEditingDomain();

		// Create Device in transaction
		final DcdComponentInstantiation[] dcdComponentInstantiations = new DcdComponentInstantiation[1];
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {

				// create device feature
				String implId = ((LocalAbstractComponent) newValue).getImplementationID();

				DeviceCreateFeature createDeviceFeature = new DeviceCreateFeature(featureProvider, spd, implId);
				CreateContext createContext = new CreateContext();
				createContext.putProperty(DeviceCreateFeature.OVERRIDE_USAGE_NAME, newValue.getLabel());
				createContext.putProperty(DeviceCreateFeature.OVERRIDE_INSTANTIATION_ID, newValue.getIdentifier());
				createContext.putProperty(DeviceCreateFeature.OVERRIDE_IMPLEMENTATION_ID, implId);
				createContext.setTargetContainer(diagram);
				final Object[] objects = createDeviceFeature.create(createContext);
				dcdComponentInstantiations[0] = (DcdComponentInstantiation) objects[0];

				// The LocalScaDevice already exists, so enable it here.
				DeviceShape shape = DUtil.getPictogramElementForBusinessObject(diagram, dcdComponentInstantiations[0], DeviceShape.class);
				shape.setEnabled(true);
			}
		});

		return dcdComponentInstantiations[0];
	}

	/**
	 * Paints the Chalkboard diagram device appropriate color
	 * Fired when a device is started/stopped in the REDHAWK Explorer
	 * @param ScaDevice
	 * @param started
	 */
	public void startStopDevice(ScaDevice< ? > scaDevice, final Boolean started) {
		final boolean resolveStarted = (started == null) ? false : started;
		final DcdNodeMapEntry nodeMapEntry = nodes.get(DcdNodeMapEntry.getKey(scaDevice));
		if (nodeMapEntry == null) {
			return;
		}
		final DcdComponentInstantiation dcdComponentInstantiation = nodeMapEntry.getProfile();

		Job job = new UIJob("Update started state: " + scaDevice.getIdentifier()) {
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				final IDiagramTypeProvider provider = editor.getDiagramEditor().getDiagramTypeProvider();
				final Diagram diagram = provider.getDiagram();
				final RHContainerShape rhContainerShape = DUtil.getPictogramElementForBusinessObject(diagram, dcdComponentInstantiation, RHContainerShape.class);
				if (rhContainerShape == null) {
					return Status.CANCEL_STATUS;
				}

				NonDirtyingCommand.execute(diagram, new NonDirtyingCommand() {
					@Override
					public void execute() {
						// paint device
						rhContainerShape.setStarted(resolveStarted);
					}
				});
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule();
	}

	private void updateEnabledState(final DcdComponentInstantiation component, final boolean enabled) {
		editor.getEditingDomain().getCommandStack().execute(new NonDirtyingCommand() {
			@Override
			public void execute() {
				Diagram diagram = editor.getDiagramEditor().getDiagramTypeProvider().getDiagram();
				RHContainerShape componentShape = DUtil.getPictogramElementForBusinessObject(diagram, component, RHContainerShape.class);
				if (componentShape != null) {
					componentShape.setEnabled(enabled);
				}
			}
		});
	}

	/**
	 * Modifies the diagram to reflect runtime status of the REDHAWK Explorer
	 * Fires when the diagram is first launched
	 */
	public void reflectRuntimeStatus() {
		synchronized (nodes) {
			for (String nodeKey : nodes.keySet()) {
				final DcdNodeMapEntry nodeMapEntry = nodes.get(nodeKey);
				ScaDevice< ? > device = nodeMapEntry.getScaDevice();
				startStopDevice(device, device.getStarted());
			}
		}
	}

	/**
	 * Updates the color of the component shape to reflect error state
	 * @param scaComponent
	 * @param status
	 */
	public void reflectErrorState(ScaDevice< ? > device, final IStatus status) {
		final DcdNodeMapEntry nodeMapEntry = nodes.get(DcdNodeMapEntry.getKey(device));
		if (nodeMapEntry == null) {
			return;
		}
		final DcdComponentInstantiation dcdComponentInstantiation = nodeMapEntry.getProfile();

		Job job = new UIJob("Update error state: " + device.getIdentifier()) {
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				final IDiagramTypeProvider provider = editor.getDiagramEditor().getDiagramTypeProvider();
				final Diagram diagram = provider.getDiagram();
				final RHContainerShape deviceShape = DUtil.getPictogramElementForBusinessObject(diagram, dcdComponentInstantiation, RHContainerShape.class);
				if (deviceShape == null) {
					return Status.CANCEL_STATUS;
				}

				NonDirtyingCommand.execute(diagram, new NonDirtyingCommand() {
					@Override
					public void execute() {
						deviceShape.setIStatusSeverity(status.getSeverity());
					}
				});
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule();
	}

	/**
	 * Called when we release a device in the REDHAWK Explorer
	 * This method removes DcdComponentInstantiation from the diagram
	 * @param comp
	 */
	public void remove(final ScaDevice< ? > device) {
		final DcdNodeMapEntry nodeMapEntry = nodes.remove(DcdNodeMapEntry.getKey(device));
		if (nodeMapEntry == null) {
			return;
		}

		final DcdComponentInstantiation oldDevice = nodeMapEntry.getProfile();
		if (oldDevice != null) {
			delete(oldDevice);
		}
	}

	/**
	 * Delete ScaConnection from local device manager
	 * @param oldConnection
	 * @throws InvalidPort
	 */
	private void delete(final ScaConnection oldConnection) throws InvalidPort {
		if (oldConnection == null) {
			return;
		}
		if (oldConnection.getPort() != null && !oldConnection.getPort().isDisposed()) {
			oldConnection.getPort().disconnectPort(oldConnection);
		}
	}

	/********************** DIAGRAM TO REDHAWK EXPLORER *************************/
	/*	These actions are fired when interacting with a shape in the diagram	*/
	/*	and the results are reflected in the REDHAWK Explorer view				*/
	/****************************************************************************/

	/**
	 * New DcdComponentInstantiation was recently added to the diagram and this method will now launch
	 * the corresponding ScaDevice
	 * @param comp
	 */
	public void add(final DcdComponentInstantiation device) {
		final DcdNodeMapEntry nodeMapEntry = new DcdNodeMapEntry();
		nodeMapEntry.setProfile(device);
		synchronized (nodes) {
			if (nodes.get(nodeMapEntry.getKey()) != null) {
				return;
			} else {
				nodes.put(nodeMapEntry.getKey(), nodeMapEntry);
			}
		}

		final String implID = ((DcdComponentInstantiationImpl) device).getImplID();
		Job job = new Job("Launching " + device.getUsageName()) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				SubMonitor subMonitor = SubMonitor.convert(monitor, "Launching " + device.getUsageName(), IProgressMonitor.UNKNOWN);
				try {
					GraphitiDcdModelMap.this.create(device, implID);
					// The device is added to the nodeMapEntry via the add(ScaDevice< ? >) method call, which is called
					// on refresh of the LoaclScaDomainManagerImpl

					return Status.OK_STATUS;
				} catch (final CoreException e) {
					nodes.remove(nodeMapEntry.getKey());
					return new Status(e.getStatus().getSeverity(), DCDUIGraphitiPlugin.PLUGIN_ID, e.getStatus().getMessage(), e);
				} finally {
					if (nodes.get(nodeMapEntry.getKey()) == null) {
						delete(device);
					}
					subMonitor.done();
				}
			}

		};
		job.schedule();
	}

	/**
	 * New DcdConnectInterface was recently added to the diagram and this method will now launch
	 * the corresponding ScaConnection
	 * @param conn
	 */
	public void add(final DcdConnectInterface conn) {
		final DcdConnectionMapEntry connectionMap = new DcdConnectionMapEntry();
		connectionMap.setProfile(conn);
		synchronized (connections) {
			if (connections.get(connectionMap.getKey()) != null) {
				return;
			} else {
				connections.put(connectionMap.getKey(), connectionMap);
			}
		}
		Job job = new Job("Connecting " + conn.getId()) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				SubMonitor subMonitor = SubMonitor.convert(monitor, "Connecting " + conn.getId(), IProgressMonitor.UNKNOWN);
				try {
					ScaConnection newConnection = GraphitiDcdModelMap.this.create(conn);
					connectionMap.setScaConnection(newConnection);
					return Status.OK_STATUS;
				} catch (final InvalidPort e) {
					delete(conn);
					connections.remove(connectionMap.getKey());
					return new Status(IStatus.ERROR, DCDUIGraphitiPlugin.PLUGIN_ID, "Failed to add connection " + conn.getId(), e);
				} catch (final OccupiedPort e) {
					delete(conn);
					connections.remove(connectionMap.getKey());
					return new Status(IStatus.ERROR, DCDUIGraphitiPlugin.PLUGIN_ID, "Failed to add connection " + conn.getId(), e);
				} finally {
					if (connections.get(connectionMap.getKey()) == null) {
						delete(conn);
					}
					subMonitor.done();
				}
			}

		};
		job.schedule();
	}

	/**
	 * New ScaConnection was recently added and this method will now add
	 * a DcdConnectInterface to the DeviceConfiguration of the Graphiti Diagram.
	 * @param conn
	 */
	public void add(final ScaConnection conn) {
		final DcdConnectionMapEntry connectionMap = new DcdConnectionMapEntry();
		connectionMap.setScaConnection(conn);
		synchronized (connections) {
			if (connections.get(connectionMap.getKey()) != null) {
				return;
			} else {
				connections.put(connectionMap.getKey(), connectionMap);
			}
		}
		Job job = new Job("Adding connection " + conn.getId()) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				SubMonitor subMonitor = SubMonitor.convert(monitor, "Adding connection " + conn.getId(), IProgressMonitor.UNKNOWN);
				DcdConnectInterface newDcdInterface = null;
				try {
					newDcdInterface = GraphitiDcdModelMap.this.create(conn);
					if (newDcdInterface == null) {
						connections.remove(connectionMap.getKey());
						return Status.CANCEL_STATUS;
					}
					connectionMap.setProfile(newDcdInterface);
					return Status.OK_STATUS;
				} catch (CoreException e) {
					connections.remove(connectionMap.getKey());
					return new Status(IStatus.ERROR, DCDUIGraphitiPlugin.PLUGIN_ID, "Failed to add connection " + conn.getId(), e);
				} finally {
					if (connections.get(connectionMap.getKey()) == null) {
						delete(newDcdInterface);
					}
					subMonitor.done();
				}
			}

		};
		job.schedule();
	}

	/**
	 * Launch ScaDevice for corresponding DcdComponentInstantiation
	 * @param ci
	 * @param implID
	 * @return
	 * @return
	 * @throws CoreException
	 */
	private void create(final DcdComponentInstantiation ci, final String implID) throws CoreException {
		DataType[] execParams = null;
		if (ci.getComponentProperties() != null) {
			final List<DataType> params = new ArrayList<DataType>(ci.getComponentProperties().getProperties().size());
			for (final Entry entry : ci.getComponentProperties().getProperties()) {
				if (entry.getValue() instanceof AbstractPropertyRef) {
					final AbstractPropertyRef< ? > ref = (AbstractPropertyRef< ? >) entry.getValue();
					params.add(new DataType(ref.getRefID(), ref.toAny()));
				}
			}
			execParams = params.toArray(new DataType[params.size()]);
		}
		final SoftPkg spd = ScaEcoreUtils.getFeature(ci, GraphitiDcdModelMap.SPD_PATH);
		if (spd == null) {
			throw new CoreException(new Status(IStatus.ERROR, DCDUIGraphitiPlugin.PLUGIN_ID, "Failed to resolve SPD.", null));
		}

		final URI spdURI = spd.eResource().getURI();

		try {
			LocalScaDeviceManagerImpl localDeviceManager = (LocalScaDeviceManagerImpl) deviceManager;
			localDeviceManager.launch(ci.getId(), execParams, spdURI.toString(), implID, ILaunchManager.RUN_MODE);
		} catch (ExecuteFail e) {
			throw new CoreException(new Status(IStatus.ERROR, DCDUIGraphitiPlugin.PLUGIN_ID, "Failed to launch device: " + e.msg, e));
		}
	}

	/**
	 * Create ScaDevice< ? > for corresponding DcdConnectInterface
	 * @param conn
	 * @return
	 * @throws InvalidPort
	 * @throws OccupiedPort
	 */
	private ScaConnection create(final DcdConnectInterface conn) throws InvalidPort, OccupiedPort {
		DcdComponentInstantiation inst = ScaEcoreUtils.getFeature(conn, GraphitiDcdModelMap.CONN_INST_PATH);
		final ScaDevice< ? > sourceDevice = get(inst);
		if (sourceDevice == null) {
			return null;
		}
		sourceDevice.fetchPorts(null);
		final ScaUsesPort usesPort = (ScaUsesPort) sourceDevice.getScaPort(conn.getUsesPort().getUsesIdentifier());
		org.omg.CORBA.Object targetObj = null;
		if (conn.getComponentSupportedInterface() != null) {
			final ScaDevice< ? > targetDevice = get((DcdComponentInstantiation) conn.getComponentSupportedInterface().getComponentInstantiationRef().getInstantiation());
			if (targetDevice != null) {
				targetObj = targetDevice.getCorbaObj();
			}
		} else if (conn.getProvidesPort() != null) {
			final ScaDevice< ? > targetDevice = get(conn.getProvidesPort().getComponentInstantiationRef().getInstantiation());
			if (targetDevice != null) {
				targetDevice.fetchPorts(null);
				final ScaPort< ? , ? > targetPort = targetDevice.getScaPort(conn.getProvidesPort().getProvidesIdentifier());
				if (targetPort != null) {
					targetObj = targetPort.getCorbaObj();
				}
			}
		}
		final String connId = conn.getId();

		if (connId != null) {
			if (targetObj != null) {
				usesPort.connectPort(targetObj, connId);
			}
			for (final ScaConnection newConn : usesPort.fetchConnections(null)) {
				if (connId.equals(newConn.getId())) {
					return newConn;
				}
			}
		}
		return null;
	}

	/**
	 * Create DcdConnectInterface for corresponding ScaConnection
	 * @param newValue
	 * @return
	 * @throws CoreException
	 */
	private DcdConnectInterface create(final ScaConnection newValue) throws CoreException {
		UsesPortStub source = null;
		final DcdComponentInstantiation sourceDevice = get((ScaDevice< ? >) newValue.getPort().eContainer());
		if (sourceDevice != null) {
			for (final UsesPortStub stub : sourceDevice.getUses()) {
				if (stub.getName() != null && stub.getName().equals(newValue.getPort().getName())) {
					source = stub;
					break;
				}
			}
		}

		ConnectionTarget target = null;
		out: for (final ScaDevice< ? > c : this.deviceManager.getAllDevices()) {
			if (c.getObj()._is_equivalent(newValue.getData().port)) {
				DcdComponentInstantiation sci = get((ScaDevice< ? >) c);
				if (sci != null) {
					target = sci.getInterfaceStub();
				}
				break;
			}
			for (final ScaPort< ? , ? > p : c.fetchPorts(null)) {
				if (p instanceof ScaProvidesPort && p.getObj()._is_equivalent(newValue.getData().port)) {
					final DcdComponentInstantiation comp = get((ScaDevice< ? >) c);
					if (comp != null) {
						for (final ProvidesPortStub provides : comp.getProvides()) {
							if (provides.getName().equals(p.getName())) {
								target = provides;
								break out;
							}
						}
					}
				}
			}
		}

		// setup for transaction in diagram
		final IDiagramTypeProvider provider = editor.getDiagramEditor().getDiagramTypeProvider();
		final IFeatureProvider featureProvider = provider.getFeatureProvider();
		final Diagram diagram = provider.getDiagram();
		final TransactionalEditingDomain editingDomain = (TransactionalEditingDomain) editor.getEditingDomain();

		// get anchors from business objects
		final Anchor sourceAnchor = (Anchor) DUtil.getPictogramElementForBusinessObject(diagram, source, Anchor.class);
		final Anchor targetAnchor = (Anchor) DUtil.getPictogramElementForBusinessObject(diagram, target, Anchor.class);

		// Create Component in transaction
		final DcdConnectInterface[] dcdConnectInterfaces = new DcdConnectInterface[1];
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {
				// create connection feature
				CreateConnectionContext createConnectionContext = new CreateConnectionContext();
				createConnectionContext.putProperty(DCDConnectInterfacePattern.OVERRIDE_CONNECTION_ID, newValue.getId());
				createConnectionContext.setSourceAnchor(sourceAnchor);
				createConnectionContext.setTargetAnchor(targetAnchor);
				ICreateConnectionFeature[] createConnectionFeatures = featureProvider.getCreateConnectionFeatures();
				for (ICreateConnectionFeature createConnectionFeature : createConnectionFeatures) {
					if (createConnectionFeature.canCreate(createConnectionContext)) {
						Connection connection = createConnectionFeature.create(createConnectionContext);
						// get business object for newly created diagram connection
						dcdConnectInterfaces[0] = (DcdConnectInterface) DUtil.getBusinessObject(connection);
						break;
					}
				}

			}
		});

		return dcdConnectInterfaces[0];
	}

	public DcdComponentInstantiation get(final ScaDevice< ? > comp) {
		if (comp == null) {
			return null;
		}
		DcdNodeMapEntry nodeMapEntry = nodes.get(DcdNodeMapEntry.getKey(comp));
		if (nodeMapEntry != null) {
			return nodeMapEntry.getProfile();
		} else {
			return null;
		}
	}

	public ScaDevice< ? > get(final DcdComponentInstantiation compInst) {
		if (compInst == null) {
			return null;
		}
		DcdNodeMapEntry nodeMapEntry = nodes.get(DcdNodeMapEntry.getKey(compInst));
		if (nodeMapEntry != null) {
			return nodeMapEntry.getScaDevice();
		} else {
			return null;
		}
	}

	public ScaConnection get(final DcdConnectInterface conn) {
		if (conn == null) {
			return null;
		}
		DcdConnectionMapEntry connectionMap = connections.get(DcdConnectionMapEntry.getKey(conn));
		if (connectionMap != null) {
			return connectionMap.getScaConnection();
		} else {
			return null;
		}
	}

	public DcdConnectInterface get(final ScaConnection conn) {
		if (conn == null) {
			return null;
		}
		DcdConnectionMapEntry connectionMap = connections.get(DcdConnectionMapEntry.getKey(conn));
		if (connectionMap != null) {
			return connectionMap.getProfile();
		} else {
			return null;
		}
	}

	/**
	 * Called when we remove DcdComponentInstantiation from the diagram.
	 * This method removes ScaDevice from the local device manager
	 * @param device
	 */
	public void remove(final DcdComponentInstantiation device) {
		if (device == null) {
			return;
		}

		final DcdNodeMapEntry nodeMapEntry = nodes.remove(DcdNodeMapEntry.getKey(device));
		if (nodeMapEntry == null) {
			return;
		}
		final ScaDevice< ? > oldDevice = nodeMapEntry.getScaDevice();
		if (oldDevice != null) {
			Job releaseJob = new ReleaseJob(oldDevice);
			releaseJob.schedule();
		}
	}

	/**
	 * Called when we remove DcdConnectInterface from the diagram.
	 * This method removes ScaConnection from the local waveform
	 * @param conn
	 */
	public void remove(final DcdConnectInterface conn) {
		final DcdConnectionMapEntry connectionMap = connections.remove(DcdConnectionMapEntry.getKey(conn));
		if (connectionMap == null) {
			return;
		}
		final ScaConnection oldConnection = connectionMap.getScaConnection();
		if (oldConnection != null) {
			Job job = new Job("Disconnect connection " + conn.getId()) {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					SubMonitor subMonitor = SubMonitor.convert(monitor, "Disconnect connection " + conn.getId(), IProgressMonitor.UNKNOWN);
					try {
						delete(oldConnection);
						return Status.OK_STATUS;
					} catch (InvalidPort e) {
						return new Status(IStatus.WARNING, DCDUIGraphitiPlugin.PLUGIN_ID, "Problems while removing connection " + conn.getId(), e);
					} finally {
						subMonitor.done();
					}
				}

			};
			job.schedule();
		}
	}

	/**
	 * Called when we remove ScaConnection from the local waveform.
	 * This method removes DcdConnectInterface from the diagram
	 * @param conn
	 */
	public void remove(final ScaConnection conn) {
		final DcdConnectionMapEntry connectionMap = connections.remove(DcdConnectionMapEntry.getKey(conn));
		if (connectionMap == null) {
			return;
		}
		final DcdConnectInterface oldDcdInterface = connectionMap.getProfile();
		if (oldDcdInterface != null) {
			delete(oldDcdInterface);
		}

	}

	/**
	 * Adds a new model object/diagram object pairing to the nodes map
	 * @param device
	 * @param inst
	 */
	public void put(ScaDevice< ? > device, DcdComponentInstantiation inst) {
		DcdNodeMapEntry nodeMapEntry = new DcdNodeMapEntry();
		nodeMapEntry.setScaDevice(device);
		nodeMapEntry.setProfile(inst);
		nodes.put(nodeMapEntry.getKey(), nodeMapEntry);
	}

}
