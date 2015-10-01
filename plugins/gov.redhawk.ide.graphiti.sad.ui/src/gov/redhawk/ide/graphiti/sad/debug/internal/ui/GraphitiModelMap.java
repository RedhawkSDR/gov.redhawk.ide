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
package gov.redhawk.ide.graphiti.sad.debug.internal.ui;

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
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.pattern.DeleteFeatureForPattern;
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.statushandlers.StatusManager;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.SystemException;

import BULKIO.PortStatistics;
import CF.DataType;
import CF.LifeCyclePackage.ReleaseError;
import CF.PortPackage.InvalidPort;
import CF.PortPackage.OccupiedPort;
import gov.redhawk.ide.debug.LocalScaComponent;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.sad.ext.ComponentShape;
import gov.redhawk.ide.graphiti.sad.ext.impl.ComponentShapeImpl;
import gov.redhawk.ide.graphiti.sad.internal.ui.editor.GraphitiWaveformSandboxEditor;
import gov.redhawk.ide.graphiti.sad.ui.SADUIGraphitiPlugin;
import gov.redhawk.ide.graphiti.sad.ui.diagram.features.create.ComponentCreateFeature;
import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.SADConnectInterfacePattern;
import gov.redhawk.ide.graphiti.sad.ui.diagram.providers.SADDiagramFeatureProvider;
import gov.redhawk.ide.graphiti.ui.GraphitiUIPlugin;
import gov.redhawk.ide.graphiti.ui.diagram.preferences.DiagramPreferenceConstants;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.PortStyleUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.PortStyleUtil.PortStyle;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaConnection;
import gov.redhawk.model.sca.ScaPort;
import gov.redhawk.model.sca.ScaPortContainer;
import gov.redhawk.model.sca.ScaProvidesPort;
import gov.redhawk.model.sca.ScaUsesPort;
import gov.redhawk.model.sca.commands.NonDirtyingCommand;
import gov.redhawk.monitor.IPortStatListener;
import gov.redhawk.sca.util.SubMonitor;
import mil.jpeojtrs.sca.partitioning.ConnectionTarget;
import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.prf.AbstractPropertyRef;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadConnectInterface;
import mil.jpeojtrs.sca.sad.impl.SadComponentInstantiationImpl;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

public class GraphitiModelMap implements IPortStatListener {
	private static final EStructuralFeature[] CONN_INST_PATH = new EStructuralFeature[] { PartitioningPackage.Literals.CONNECT_INTERFACE__USES_PORT,
		PartitioningPackage.Literals.USES_PORT__COMPONENT_INSTANTIATION_REF, PartitioningPackage.Literals.COMPONENT_INSTANTIATION_REF__INSTANTIATION };
	private static final EStructuralFeature[] SPD_PATH = new EStructuralFeature[] { PartitioningPackage.Literals.COMPONENT_INSTANTIATION__PLACEMENT,
		PartitioningPackage.Literals.COMPONENT_PLACEMENT__COMPONENT_FILE_REF, PartitioningPackage.Literals.COMPONENT_FILE_REF__FILE,
		PartitioningPackage.Literals.COMPONENT_FILE__SOFT_PKG };
	private final GraphitiWaveformSandboxEditor editor;
	// maps containing to uniquely identify component/connections, use with synchronized statement
	private final Map<String, NodeMapEntry> nodes = Collections.synchronizedMap(new HashMap<String, NodeMapEntry>());
	private final Map<String, ConnectionMapEntry> connections = Collections.synchronizedMap(new HashMap<String, ConnectionMapEntry>());

	private final LocalScaWaveform waveform;

	public GraphitiModelMap(@NonNull final GraphitiWaveformSandboxEditor editor, @NonNull final LocalScaWaveform waveform) {
		Assert.isNotNull(waveform, "Sandbox Waveform must not be null");
		Assert.isNotNull(editor, "Sandbox Editor must not be null");
		this.waveform = waveform;
		this.editor = editor;
	}

	/**
	 * New LocalScaComponent was recently added and this method will now add
	 * a SadComponentInstiation to the SofwareAssembly of the Graphiti Diagram.
	 * @param comp
	 */
	public void add(@NonNull final LocalScaComponent comp) {

		final NodeMapEntry nodeMapEntry = new NodeMapEntry();
		nodeMapEntry.setLocalScaComponent(comp);
		synchronized (nodes) {
			if (nodes.get(nodeMapEntry.getKey()) != null) {
				return;
			} else {
				nodes.put(nodeMapEntry.getKey(), nodeMapEntry);
			}
		}
		Job job = new Job("Adding component: " + comp.getInstantiationIdentifier()) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				SubMonitor subMonitor = SubMonitor.convert(monitor, "Adding component: " + comp.getInstantiationIdentifier(), IProgressMonitor.UNKNOWN);
				SadComponentInstantiation newComp = null;
				try {
					newComp = GraphitiModelMap.this.create(comp);
					nodeMapEntry.setProfile(newComp);

					return Status.OK_STATUS;
				} catch (CoreException e) {
					nodes.remove(nodeMapEntry.getKey());
					return new Status(IStatus.ERROR, SADUIGraphitiPlugin.PLUGIN_ID, "Failed to add component " + comp.getInstantiationIdentifier(), e);
				} finally {
					if (nodes.get(nodeMapEntry.getKey()) == null) {
						delete(newComp);
					}
					subMonitor.done();
				}
			}

		};
		job.schedule();

	}

	/**
	 * New SadComponentInstantiation was recently added to the diagram and this method will now launch
	 * the corresponding LocalScaComponent
	 * @param comp
	 */
	public void add(@NonNull final SadComponentInstantiation comp) {
		final NodeMapEntry nodeMapEntry = new NodeMapEntry();
		nodeMapEntry.setProfile(comp);
		synchronized (nodes) {
			if (nodes.get(nodeMapEntry.getKey()) != null) {
				return;
			} else {
				nodes.put(nodeMapEntry.getKey(), nodeMapEntry);
			}
		}

		final String implID = ((SadComponentInstantiationImpl) comp).getImplID();
		Job job = new Job("Launching " + comp.getUsageName()) {

			@Override
			@Nullable
			protected IStatus run(IProgressMonitor monitor) {

				SubMonitor subMonitor = SubMonitor.convert(monitor, "Launching " + comp.getUsageName(), IProgressMonitor.UNKNOWN);
				LocalScaComponent newComp = null;
				try {
					newComp = GraphitiModelMap.this.create(comp, implID);
					nodeMapEntry.setLocalScaComponent(newComp);
					updateEnabledState(comp, true);
					editor.componentRegistered(comp);

					return Status.OK_STATUS;
				} catch (final CoreException e) {
					delete(comp);
					nodes.remove(nodeMapEntry.getKey());
					return e.getStatus();
				} finally {
					if (nodes.get(nodeMapEntry.getKey()) == null) {
						try {
							delete(newComp);
						} catch (ReleaseError e) {
							// PASS
						} catch (SystemException e) {
							// PASS
						}
					}
					subMonitor.done();
				}
			}

		};
		job.schedule();
	}

	/**
	 * New SadConnectInterface was recently added to the diagram and this method will now launch
	 * the corresponding ScaConnection
	 * @param conn
	 */
	public void add(@NonNull final SadConnectInterface conn) {
		final ConnectionMapEntry connectionMap = new ConnectionMapEntry();
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
			@NonNull
			protected IStatus run(IProgressMonitor monitor) {
				SubMonitor subMonitor = SubMonitor.convert(monitor, "Connecting " + conn.getId(), IProgressMonitor.UNKNOWN);
				try {
					ScaConnection newConnection = GraphitiModelMap.this.create(conn);
					connectionMap.setScaConnection(newConnection);
					return Status.OK_STATUS;
				} catch (final InvalidPort e) {
					delete(conn);
					connections.remove(connectionMap.getKey());
					return new Status(IStatus.ERROR, SADUIGraphitiPlugin.PLUGIN_ID, "Failed to add connection " + conn.getId(), e);
				} catch (final OccupiedPort e) {
					delete(conn);
					connections.remove(connectionMap.getKey());
					return new Status(IStatus.ERROR, SADUIGraphitiPlugin.PLUGIN_ID, "Failed to add connection " + conn.getId(), e);
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
	 * a SadConnectInterface to the SofwareAssembly of the Graphiti Diagram.
	 * @param conn
	 */
	public void add(@NonNull final ScaConnection conn) {
		final ConnectionMapEntry connectionMap = new ConnectionMapEntry();
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
			@NonNull
			protected IStatus run(IProgressMonitor monitor) {
				SubMonitor subMonitor = SubMonitor.convert(monitor, "Adding connection " + conn.getId(), IProgressMonitor.UNKNOWN);
				SadConnectInterface newSadInterface = null;
				try {
					newSadInterface = GraphitiModelMap.this.create(conn);
					if (newSadInterface == null) {
						connections.remove(connectionMap.getKey());
						return Status.CANCEL_STATUS;
					}
					connectionMap.setProfile(newSadInterface);
					return Status.OK_STATUS;
				} catch (CoreException e) {
					connections.remove(connectionMap.getKey());
					return new Status(IStatus.ERROR, SADUIGraphitiPlugin.PLUGIN_ID, "Failed to add connection " + conn.getId(), e);
				} finally {
					if (connections.get(connectionMap.getKey()) == null) {
						delete(newSadInterface);
					}
					subMonitor.done();
				}
			}

		};
		job.schedule();
	}

	/**
	 * Create SadComponentInstantiation from the provided LocalScaComponent. Add the SadComponentInstantiation
	 * to the diagram
	 * @param newValue
	 * @return
	 * @throws CoreException
	 */
	@NonNull
	private SadComponentInstantiation create(@NonNull final LocalScaComponent newValue) throws CoreException {

		// get SoftPkg
		newValue.fetchAttributes(null);
		final SoftPkg spd = newValue.fetchProfileObject(null);
		if (spd == null) {
			throw new IllegalStateException("Unable to load New Component's SPD");
		}

		// setup for transaction in diagram
		final IDiagramTypeProvider provider = editor.getDiagramEditor().getDiagramTypeProvider();
		final IFeatureProvider featureProvider = provider.getFeatureProvider();
		final Diagram diagram = provider.getDiagram();
		final TransactionalEditingDomain editingDomain = (TransactionalEditingDomain) editor.getEditingDomain();

		// Create Component in transaction
		final SadComponentInstantiation[] sadComponentInstantiations = new SadComponentInstantiation[1];
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {

				// create component feature
				ComponentCreateFeature createComponentFeature = new ComponentCreateFeature(featureProvider, spd, newValue.getImplementationID());
				CreateContext createContext = new CreateContext();
				createContext.putProperty(ComponentCreateFeature.OVERRIDE_USAGE_NAME, newValue.getName());
				createContext.putProperty(ComponentCreateFeature.OVERRIDE_INSTANTIATION_ID, newValue.getInstantiationIdentifier());
				createContext.setTargetContainer(diagram);
				final Object[] objects = createComponentFeature.create(createContext);
				sadComponentInstantiations[0] = (SadComponentInstantiation) objects[0];

				// The LocalSCAComponent already exists, so enable it here.
				RHContainerShape shape = DUtil.getPictogramElementForBusinessObject(diagram, sadComponentInstantiations[0], RHContainerShape.class);
				shape.setEnabled(true);
			}
		});

		return sadComponentInstantiations[0];
	}

	/**
	 * Launch LocalScaComponent for corresponding SadComponentInstantiation
	 * @param comp
	 * @param implID
	 * @return
	 * @throws CoreException
	 */
	@NonNull
	private LocalScaComponent create(@NonNull final SadComponentInstantiation comp, @Nullable final String implID) throws CoreException {
		DataType[] execParams = null;
		if (comp.getComponentProperties() != null) {
			final List<DataType> params = new ArrayList<DataType>(comp.getComponentProperties().getProperties().size());
			for (final Entry entry : comp.getComponentProperties().getProperties()) {
				if (entry.getValue() instanceof AbstractPropertyRef) {
					final AbstractPropertyRef< ? > ref = (AbstractPropertyRef< ? >) entry.getValue();
					params.add(new DataType(ref.getRefID(), ref.toAny()));
				}
			}
			execParams = params.toArray(new DataType[params.size()]);
		}
		final SoftPkg spd = ScaEcoreUtils.getFeature(comp, GraphitiModelMap.SPD_PATH);
		if (spd == null) {
			throw new CoreException(new Status(IStatus.ERROR, SADUIGraphitiPlugin.PLUGIN_ID, "Failed to resolve SPD.", null));
		}
		final URI spdURI = spd.eResource().getURI();
		return this.waveform.launch(comp.getId(), execParams, spdURI, implID, ILaunchManager.RUN_MODE);
	}

	/**
	 * Create LocalScaComponent for corresponding SadConnectInterface
	 * @param conn
	 * @return
	 * @throws InvalidPort
	 * @throws OccupiedPort
	 */
	@Nullable
	private ScaConnection create(@NonNull final SadConnectInterface conn) throws InvalidPort, OccupiedPort {
		SadComponentInstantiation inst = ScaEcoreUtils.getFeature(conn, GraphitiModelMap.CONN_INST_PATH);
		final LocalScaComponent sourceComp = get(inst);
		if (sourceComp == null) {
			return null;
		}
		sourceComp.fetchPorts(null);
		final ScaUsesPort usesPort = (ScaUsesPort) sourceComp.getScaPort(conn.getUsesPort().getUsesIdentifier());
		org.omg.CORBA.Object targetObj = null;
		if (conn.getComponentSupportedInterface() != null) {
			final LocalScaComponent targetComp = get((SadComponentInstantiation) conn.getComponentSupportedInterface().getComponentInstantiationRef().getInstantiation());
			if (targetComp != null) {
				targetObj = targetComp.getCorbaObj();
			}
		} else if (conn.getProvidesPort() != null) {
			final LocalScaComponent targetComp = get(conn.getProvidesPort().getComponentInstantiationRef().getInstantiation());
			if (targetComp != null) {
				targetComp.fetchPorts(null);
				final ScaPort< ? , ? > targetPort = targetComp.getScaPort(conn.getProvidesPort().getProvidesIdentifier());
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
	 * Create SadConnectInterface for corresponding ScaConnection
	 * @param newValue
	 * @return
	 * @throws CoreException
	 */
	@Nullable
	private SadConnectInterface create(@NonNull final ScaConnection newValue) throws CoreException {
		UsesPortStub source = null;
		final SadComponentInstantiation sourceComponent = get((LocalScaComponent) newValue.getPort().eContainer());
		if (sourceComponent != null) {
			for (final UsesPortStub stub : sourceComponent.getUses()) {
				if (stub.getName() != null && stub.getName().equals(newValue.getPort().getName())) {
					source = stub;
					break;
				}
			}
		}

		ConnectionTarget target = null;
		out: for (final ScaComponent c : this.waveform.getComponents()) {
			if (c.getObj()._is_equivalent(newValue.getData().port)) {
				SadComponentInstantiation sci = get((LocalScaComponent) c);
				if (sci != null) {
					target = sci.getInterfaceStub();
				}
				break;
			}
			for (final ScaPort< ? , ? > p : c.fetchPorts(null)) {
				if (p instanceof ScaProvidesPort && p.getObj()._is_equivalent(newValue.getData().port)) {
					final SadComponentInstantiation comp = get((LocalScaComponent) c);
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
		final SadConnectInterface[] sadConnectInterfaces = new SadConnectInterface[1];
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {
				// create connection feature
				CreateConnectionContext createConnectionContext = new CreateConnectionContext();
				createConnectionContext.putProperty(SADConnectInterfacePattern.OVERRIDE_CONNECTION_ID, newValue.getId());
				createConnectionContext.setSourceAnchor(sourceAnchor);
				createConnectionContext.setTargetAnchor(targetAnchor);
				ICreateConnectionFeature[] createConnectionFeatures = featureProvider.getCreateConnectionFeatures();
				for (ICreateConnectionFeature createConnectionFeature : createConnectionFeatures) {
					if (createConnectionFeature.canCreate(createConnectionContext)) {
						Connection connection = createConnectionFeature.create(createConnectionContext);
						// get business object for newly created diagram connection
						sadConnectInterfaces[0] = (SadConnectInterface) DUtil.getBusinessObject(connection);
						break;
					}
				}

			}
		});

		return sadConnectInterfaces[0];
	}

	/**
	 * @param oldComp
	 * @throws ReleaseError
	 */
	private void delete(@Nullable final LocalScaComponent oldComp) throws ReleaseError {
		if (oldComp == null) {
			return;
		}
		if (!oldComp.isDisposed()) {
			oldComp.releaseObject();
		}
	}

	/**
	 * Delete SadComponentInstantiation from diagram
	 * @param oldValue
	 */
	private void delete(@Nullable final SadComponentInstantiation sadComponentInstantiation) {

		// No need to process delete if the waveform is disposed, since we are closing the editor anyways.
		if (waveform.isDisposed()) {
			return;
		}

		if (sadComponentInstantiation == null || editor.isDisposed()) {
			return;
		}
		// setup to perform diagram operations
		final IDiagramTypeProvider provider = editor.getDiagramEditor().getDiagramTypeProvider();
		final IFeatureProvider featureProvider = provider.getFeatureProvider();
		final Diagram diagram = provider.getDiagram();

		// get pictogram for component
		final PictogramElement[] peToRemove = { DUtil.getPictogramElementForBusinessObject(diagram, sadComponentInstantiation, ComponentShapeImpl.class) };

		// Delete Component in transaction
		final TransactionalEditingDomain editingDomain = (TransactionalEditingDomain) editor.getEditingDomain();
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {

				// delete shape & component
				DeleteContext context = new DeleteContext(peToRemove[0]);
				SADDiagramFeatureProvider fp = (SADDiagramFeatureProvider) featureProvider;
				IPattern pattern = fp.getPatternForPictogramElement(peToRemove[0]);
				DeleteFeatureForPattern deleteFeature = new DeleteFeatureForPattern(fp, pattern);
				deleteFeature.delete(context);
			}
		});
	}

	/**
	 * Delete SadConnectInterface from diagram
	 * @param connection
	 */
	private void delete(@Nullable final SadConnectInterface connection) {
		if (connection == null || editor.isDisposed()) {
			return;
		}

		// setup to perform diagram operations
		final IDiagramTypeProvider provider = editor.getDiagramEditor().getDiagramTypeProvider();
		final IFeatureProvider featureProvider = provider.getFeatureProvider();
		final Diagram diagram = provider.getDiagram();

		// get pictogram for connection
		final PictogramElement peToRemove = DUtil.getPictogramElementForBusinessObject(diagram, connection, Connection.class);

		// Delete Component in transaction
		final TransactionalEditingDomain editingDomain = (TransactionalEditingDomain) editor.getEditingDomain();
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {

				// delete connection shape & connection business object
				DeleteContext dc = new DeleteContext(peToRemove);
				IDeleteFeature deleteFeature = featureProvider.getDeleteFeature(dc);
				if (deleteFeature != null) {
					deleteFeature.delete(dc);
				}
			}
		});
	}

	/**
	 * Delete ScaConnection from local waveform
	 * @param oldConnection
	 * @throws InvalidPort
	 */
	private void delete(@Nullable final ScaConnection oldConnection) throws InvalidPort {
		if (oldConnection == null) {
			return;
		}
		if (oldConnection.getPort() != null && !oldConnection.getPort().isDisposed()) {
			oldConnection.getPort().disconnectPort(oldConnection);
		}
	}

	@Nullable
	public SadComponentInstantiation get(@Nullable final LocalScaComponent comp) {
		if (comp == null) {
			return null;
		}
		NodeMapEntry nodeMapEntry = nodes.get(NodeMapEntry.getKey(comp));
		if (nodeMapEntry != null) {
			return nodeMapEntry.getProfile();
		} else {
			return null;
		}
	}

	@Nullable
	public LocalScaComponent get(@Nullable final SadComponentInstantiation compInst) {
		if (compInst == null) {
			return null;
		}
		NodeMapEntry nodeMapEntry = nodes.get(NodeMapEntry.getKey(compInst));
		if (nodeMapEntry != null) {
			return nodeMapEntry.getLocalScaComponent();
		} else {
			return null;
		}
	}

	@Nullable
	public ScaConnection get(@Nullable final SadConnectInterface conn) {
		if (conn == null) {
			return null;
		}
		ConnectionMapEntry connectionMap = connections.get(ConnectionMapEntry.getKey(conn));
		if (connectionMap != null) {
			return connectionMap.getScaConnection();
		} else {
			return null;
		}
	}

	@Nullable
	public SadConnectInterface get(@Nullable final ScaConnection conn) {
		if (conn == null) {
			return null;
		}
		ConnectionMapEntry connectionMap = connections.get(ConnectionMapEntry.getKey(conn));
		if (connectionMap != null) {
			return connectionMap.getProfile();
		} else {
			return null;
		}
	}

	@Nullable
	private TransactionalEditingDomain getEditingDomain() {
		return this.editor.getDiagramEditor().getEditingDomain();
	}

	/**
	 * Called when we remove LocalScaComponent from the local waveform.
	 * This method removes SadComponentInstantiation from the diagram
	 * @param comp
	 */
	public void remove(@NonNull final LocalScaComponent comp) {

		final NodeMapEntry nodeMapEntry = nodes.remove(NodeMapEntry.getKey(comp));
		if (nodeMapEntry == null) {
			return;
		}
		final SadComponentInstantiation oldComp = nodeMapEntry.getProfile();
		if (oldComp != null) {
			delete(oldComp);
		}
	}

	/**
	 * Called when we remove SadComponentInstantiation from the diagram.
	 * This method removes LocalScaComponent from the local waveform
	 * @param comp
	 */
	public void remove(final SadComponentInstantiation comp) {
		if (comp == null) {
			return;
		}

		final NodeMapEntry nodeMapEntry = nodes.remove(NodeMapEntry.getKey(comp));
		if (nodeMapEntry == null) {
			return;
		}
		final LocalScaComponent oldComp = nodeMapEntry.getLocalScaComponent();
		if (oldComp != null) {
			Job job = new Job("Releasing " + comp.getUsageName()) {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					SubMonitor subMonitor = SubMonitor.convert(monitor, "Releasing " + comp.getUsageName(), IProgressMonitor.UNKNOWN);
					try {
						delete(oldComp);
						return Status.OK_STATUS;
					} catch (ReleaseError e) {
						return new Status(IStatus.WARNING, SADUIGraphitiPlugin.PLUGIN_ID, "Problems while removing component " + comp.getId(), e);
					} finally {
						subMonitor.done();
					}
				}

			};
			job.schedule();
		}
	}

	/**
	 * Called when we remove SadConnectInterface from the diagram.
	 * This method removes ScaConnection from the local waveform
	 * @param conn
	 */
	public void remove(@NonNull final SadConnectInterface conn) {
		final ConnectionMapEntry connectionMap = connections.remove(ConnectionMapEntry.getKey(conn));
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
						return new Status(IStatus.WARNING, SADUIGraphitiPlugin.PLUGIN_ID, "Problems while removing connection " + conn.getId(), e);
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
	 * This method removes SadConnectInterface from the diagram
	 * @param conn
	 */
	public void remove(@NonNull final ScaConnection conn) {
		final ConnectionMapEntry connectionMap = connections.remove(ConnectionMapEntry.getKey(conn));
		if (connectionMap == null) {
			return;
		}
		final SadConnectInterface oldSadInterface = connectionMap.getProfile();
		if (oldSadInterface != null) {
			delete(oldSadInterface);
		}

	}

	/**
	 * Paints the Chalkboard diagram component appropriate color
	 * @param scaComponent
	 * @param started
	 */
	public void startStopComponent(ScaComponent scaComponent, final Boolean started) {
		final boolean resolveStarted = (started == null) ? false : started;
		final NodeMapEntry nodeMapEntry = nodes.get(NodeMapEntry.getKey(scaComponent));
		if (nodeMapEntry == null) {
			return;
		}
		final SadComponentInstantiation sadComponentInstantiation = nodeMapEntry.getProfile();

		// get pictogram for component
		Job job = new UIJob("Update started state: " + scaComponent.getInstantiationIdentifier()) {
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				final IDiagramTypeProvider provider = editor.getDiagramEditor().getDiagramTypeProvider();
				final Diagram diagram = provider.getDiagram();
				final RHContainerShape componentShape = DUtil.getPictogramElementForBusinessObject(diagram, sadComponentInstantiation, RHContainerShape.class);
				if (componentShape == null) {
					return Status.CANCEL_STATUS;
				}

				NonDirtyingCommand.execute(diagram, new NonDirtyingCommand() {
					@Override
					public void execute() {
						componentShape.setStarted(resolveStarted);
					}
				});
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule();
	}

	private void updateEnabledState(final SadComponentInstantiation component, final boolean enabled) {
		getEditingDomain().getCommandStack().execute(new NonDirtyingCommand() {
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
	 * Modifies the diagram to reflect component runtime status
	 */
	public void reflectRuntimeStatus() {
		synchronized (nodes) {
			for (String nodeKey : nodes.keySet()) {
				final NodeMapEntry nodeMapEntry = nodes.get(nodeKey);
				LocalScaComponent component = nodeMapEntry.getLocalScaComponent();
				if (component == null) {
					updateEnabledState(nodeMapEntry.getProfile(), false);
				} else {
					updateEnabledState(nodeMapEntry.getProfile(), true);
					startStopComponent(component, component.getStarted());
				}
			}
		}
	}

	/**
	 * Updates the color of the component shape to reflect error state
	 * @param scaComponent
	 * @param status
	 */
	public void reflectErrorState(ScaComponent scaComponent, final IStatus status) {
		final NodeMapEntry nodeMapEntry = nodes.get(NodeMapEntry.getKey((LocalScaComponent) scaComponent));
		if (nodeMapEntry == null) {
			return;
		}
		final SadComponentInstantiation sadComponentInstantiation = nodeMapEntry.getProfile();

		Job job = new UIJob("Update error state: " + scaComponent.getInstantiationIdentifier()) {
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				final IDiagramTypeProvider provider = editor.getDiagramEditor().getDiagramTypeProvider();
				final Diagram diagram = provider.getDiagram();
				final RHContainerShape componentShape = DUtil.getPictogramElementForBusinessObject(diagram, sadComponentInstantiation, RHContainerShape.class);
				if (componentShape == null) {
					return Status.CANCEL_STATUS;
				}

				NonDirtyingCommand.execute(diagram, new NonDirtyingCommand() {
					@Override
					public void execute() {
						componentShape.setIStatusSeverity(status.getSeverity());
					}
				});
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule();
	}

	/**
	 * @param con
	 * @param sadCon
	 */
	public void put(@NonNull ScaConnection con, @NonNull SadConnectInterface sadCon) {
		ConnectionMapEntry connectionMap = new ConnectionMapEntry();
		connectionMap.setScaConnection(con);
		connectionMap.setProfile(sadCon);
		connections.put(connectionMap.getKey(), connectionMap);
	}

	/**
	 * @param comp
	 * @param inst
	 */
	public void put(@NonNull LocalScaComponent comp, @NonNull SadComponentInstantiation inst) {
		NodeMapEntry nodeMapEntry = new NodeMapEntry();
		nodeMapEntry.setLocalScaComponent(comp);
		nodeMapEntry.setProfile(inst);
		nodes.put(nodeMapEntry.getKey(), nodeMapEntry);
	}

	/*****************************************************************************************************************/
	/******* These methods handle changing the color of the Graphiti port shape objects to reflect port status *******/
	/*****************************************************************************************************************/

	@Override
	public void newStatistics(final ScaPort< ? , ? > port, final PortStatistics stats) {
		IPreferenceStore store = GraphitiUIPlugin.getDefault().getPreferenceStore();
		final float queueDepth = stats.averageQueueDepth;
		final double queueDepthWarningLevel = store.getDouble(DiagramPreferenceConstants.PREF_PORT_STATISTICS_QUEUE_LEVEL) / 100;
		final double queueDepthIncrement = (1.0 - queueDepthWarningLevel) / 4;
		final double lastFlushResetTime = store.getDouble(DiagramPreferenceConstants.PREF_PORT_STATISTICS_QUEUE_FLUSH_DISPLAY);

		Double lastFlushKeyword = null;
		DataType[] keywords = stats.keywords;
		if (keywords != null) {
			for (DataType keyword : keywords) {
				if ("timeSinceLastFlush".equals(keyword.id)) {
					try {
						lastFlushKeyword = keyword.value.extract_double();
					} catch (BAD_OPERATION e) {
						StatusManager.getManager().handle(
							new Status(Status.WARNING, SADUIGraphitiPlugin.PLUGIN_ID, "Expected double value for timeSinceLastFlush keyword (TCKind was " + keyword.value.type().kind() + ")", e));
					}
				}
			}
		}
		final double lastFlush = (lastFlushKeyword != null) ? lastFlushKeyword.doubleValue() : Double.MAX_VALUE; 

		ScaPortContainer container = port.getPortContainer();
		if (!(container instanceof ScaComponent)) {
			StatusManager.getManager().handle(new Status(IStatus.WARNING, SADUIGraphitiPlugin.PLUGIN_ID, "Received statistics for a port not belonging to a component"),
				StatusManager.LOG | StatusManager.SHOW);
			return;
		}
		ScaComponent component = (ScaComponent) container;
		final NodeMapEntry nodeMapEntry = nodes.get(component.getInstantiationIdentifier());
		final TransactionalEditingDomain editingDomain = (TransactionalEditingDomain) editor.getEditingDomain();

		// Since the same key(instantiationId) can be used in multiple editors, do an object comparison
		if (nodeMapEntry == null || container != nodeMapEntry.getLocalScaComponent()) {
			return;
		}

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				SadComponentInstantiation sadCi = nodeMapEntry.getProfile();
				if (GraphitiModelMap.this.editor.getDiagramEditor() == null || editor.getDiagramEditor().getDiagramBehavior() == null) {
					return;
				}
				final IDiagramTypeProvider provider = editor.getDiagramEditor().getDiagramTypeProvider();
				final Diagram diagram = provider.getDiagram();

				ComponentShape componentShape = DUtil.getPictogramElementForBusinessObject(diagram, sadCi, ComponentShape.class);

				// Can't update port styles while a connection is in progress
				if (DUtil.isConnecting(diagram)) {
					return;
				}

				for (ContainerShape portShape : DUtil.getDiagramProvidesPorts(componentShape)) {
					ProvidesPortStub portStub = (ProvidesPortStub) DUtil.getBusinessObject(portShape);
					if (portStub.getName().equals(port.getName())) {
						if (lastFlush < lastFlushResetTime || (lastFlush != Double.MAX_VALUE && lastFlushResetTime < 0)) {
							// If last flush reset time is set to -1, never reset the color if a flush has occurred
							PortStyleUtil.updatePortStyle(diagram, editingDomain, portShape, PortStyle.STAT_WARN4);
						} else if (queueDepth < queueDepthWarningLevel) {
							PortStyleUtil.updatePortStyle(diagram, editingDomain, portShape, PortStyle.STAT_OK);
						} else if (queueDepth < (queueDepthWarningLevel + queueDepthIncrement)) {
							PortStyleUtil.updatePortStyle(diagram, editingDomain, portShape, PortStyle.STAT_WARN1);
						} else if (queueDepth < (queueDepthWarningLevel + 2 * queueDepthIncrement)) {
							PortStyleUtil.updatePortStyle(diagram, editingDomain, portShape, PortStyle.STAT_WARN2);
						} else if (queueDepth < (queueDepthWarningLevel + 3 * queueDepthIncrement)) {
							PortStyleUtil.updatePortStyle(diagram, editingDomain, portShape, PortStyle.STAT_WARN3);
						} else {
							PortStyleUtil.updatePortStyle(diagram, editingDomain, portShape, PortStyle.STAT_WARN4);
						}
					}
				}
			}
		});

	}

	@Override
	public void newStatistics(ScaPort< ? , ? > port, final String connectionId, PortStatistics stats) {
		IPreferenceStore store = GraphitiUIPlugin.getDefault().getPreferenceStore();

		if (editor.getDiagramEditor() == null || editor.getDiagramEditor().getDiagramBehavior() == null) {
			return;
		}
		final IDiagramTypeProvider provider = editor.getDiagramEditor().getDiagramTypeProvider();
		final Diagram diagram = provider.getDiagram();
		if (this.editor.getDiagramEditor() == null) {
			return;
		}

		// Get the component shape
		ScaPortContainer container = port.getPortContainer();
		ScaComponent component = (ScaComponent) container;
		final NodeMapEntry nodeMapEntry = nodes.get(component.getInstantiationIdentifier());

		// Since the same key(instantiationId) can be used in multiple editors, do an object comparison
		if (nodeMapEntry == null || container != nodeMapEntry.getLocalScaComponent()) {
			return;
		}

		// Get the connection interface
		final ConnectionMapEntry connectionMap = connections.get(connectionId);
		if (connectionMap == null) {
			return;
		}

		final float timeSinceLastCall = stats.timeSinceLastCall;
		final double lastCallWarningLevel = store.getDouble(DiagramPreferenceConstants.PREF_PORT_STATISTICS_NO_DATA_PUSHED_SECONDS);
		
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				SadComponentInstantiation sadCi = nodeMapEntry.getProfile();
				ComponentShapeImpl componentShape = (ComponentShapeImpl) DUtil.getPictogramElementForBusinessObject(diagram, sadCi, ComponentShapeImpl.class);
				SadConnectInterface connInterface = connectionMap.getProfile();
				Connection connection = (Connection) DUtil.getPictogramElementForBusinessObject(diagram, connInterface, Connection.class);
		
				Polyline line = (Polyline) connection.getGraphicsAlgorithm();
				if (timeSinceLastCall < lastCallWarningLevel) {
					updateConnectionStyle(componentShape, connection, connInterface, connectionId, line, diagram, StyleUtil.CONNECTION_OK);
				} else {
					updateConnectionStyle(componentShape, connection, connInterface, connectionId, line, diagram, StyleUtil.CONNECTION_WARN);
				}
			}
		});
	}

	private void updateConnectionStyle(final ComponentShapeImpl componentShape, final Connection connection, final SadConnectInterface connInterface,
		final String connectionId, final Polyline line, final Diagram diagram, final String styleId) {
		TransactionalEditingDomain editingDomain = (TransactionalEditingDomain) editor.getEditingDomain();
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new NonDirtyingCommand() {
			@Override
			public void execute() {
				if (styleId == null) {
					componentShape.getConnectionMap().remove(connectionId);
					StyleUtil.setStyle(line, StyleUtil.CONNECTION);
					SADConnectInterfacePattern.decorateConnection(connection, connInterface, diagram, StyleUtil.CONNECTION);
				} else {
					componentShape.getConnectionMap().put(connectionId, styleId);
					StyleUtil.setStyle(line, styleId);
					SADConnectInterfacePattern.decorateConnection(connection, connInterface, diagram, styleId);
				}
			}
		});
	}

	@Override
	public void noStatistics(final ScaPort< ? , ? > port) {
		ScaPortContainer container = port.getPortContainer();
		ScaComponent component = (ScaComponent) container;
		if (component == null) {
			return;
		}

		final NodeMapEntry nodeMapEntry = nodes.get(component.getInstantiationIdentifier());
		if (nodeMapEntry != null) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					SadComponentInstantiation sadCi = nodeMapEntry.getProfile();
					final IDiagramTypeProvider provider = editor.getDiagramEditor().getDiagramTypeProvider();
					final Diagram diagram = provider.getDiagram();
					TransactionalEditingDomain editingDomain = (TransactionalEditingDomain) editor.getEditingDomain();

					// Can't update port styles while a connection is in progress
					if (DUtil.isConnecting(diagram)) {
						return;
					}

					if (port instanceof ScaProvidesPort) {
						ComponentShapeImpl componentShape = (ComponentShapeImpl) DUtil.getPictogramElementForBusinessObject(diagram, sadCi, ComponentShapeImpl.class);
						EList<ProvidesPortStub> providesStubs = componentShape.getProvidesPortStubs();
						for (ProvidesPortStub portStub : providesStubs) {
							if (portStub.getName().equals(port.getName())) {
								PortStyleUtil.resetPortStyling(diagram, editingDomain, componentShape);
							}
						}
					}
				}
			});
		}
	}

	@Override
	public void noStatistics(ScaPort< ? , ? > port, final String connectionId) {
		final IDiagramTypeProvider provider = editor.getDiagramEditor().getDiagramTypeProvider();
		final Diagram diagram = provider.getDiagram();

		// Get the component shape
		ScaPortContainer container = port.getPortContainer();
		ScaComponent component = (ScaComponent) container;
		if (component == null) {
			return;
		}
		final NodeMapEntry nodeMapEntry = nodes.get(component.getInstantiationIdentifier());
		if (nodeMapEntry == null) {
			return;
		}
		
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				SadComponentInstantiation sadCi = nodeMapEntry.getProfile();
				ComponentShapeImpl componentShape = (ComponentShapeImpl) DUtil.getPictogramElementForBusinessObject(diagram, sadCi, ComponentShapeImpl.class);
		
				// Get the connection interface
				ConnectionMapEntry connectionMap = connections.get(connectionId);
				if (connectionMap == null) {
					return;
				}
				SadConnectInterface connInterface = connectionMap.getProfile();
				Connection connection = (Connection) DUtil.getPictogramElementForBusinessObject(diagram, connInterface, Connection.class);
				Polyline line = (Polyline) connection.getGraphicsAlgorithm();
				updateConnectionStyle(componentShape, connection, connInterface, connectionId, line, diagram, null);
			}
		});
	}
}
