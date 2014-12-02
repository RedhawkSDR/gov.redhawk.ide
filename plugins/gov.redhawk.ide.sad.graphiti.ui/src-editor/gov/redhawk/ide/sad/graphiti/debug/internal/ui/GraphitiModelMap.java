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
package gov.redhawk.ide.sad.graphiti.debug.internal.ui;

import gov.redhawk.ide.debug.LocalScaComponent;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.sad.graphiti.ext.ComponentShape;
import gov.redhawk.ide.sad.graphiti.ext.impl.ComponentShapeImpl;
import gov.redhawk.ide.sad.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.sad.graphiti.ui.SADUIGraphitiPlugin;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.create.ComponentCreateFeature;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.custom.runtime.ReleaseComponentFeature;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.SADConnectInterfacePattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.StyleUtil;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaConnection;
import gov.redhawk.model.sca.ScaPort;
import gov.redhawk.model.sca.ScaProvidesPort;
import gov.redhawk.model.sca.ScaUsesPort;
import gov.redhawk.sca.ui.actions.StartAction;
import gov.redhawk.sca.ui.actions.StopAction;
import gov.redhawk.sca.util.Debug;
import gov.redhawk.sca.util.SubMonitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mil.jpeojtrs.sca.partitioning.ConnectionTarget;
import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.prf.AbstractPropertyRef;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadConnectInterface;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.sad.impl.SadComponentInstantiationImpl;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

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
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.ui.progress.WorkbenchJob;
import org.omg.CORBA.SystemException;

import CF.DataType;
import CF.LifeCyclePackage.ReleaseError;
import CF.PortPackage.InvalidPort;
import CF.PortPackage.OccupiedPort;

public class GraphitiModelMap {
	private static final Debug DEBUG = new Debug(SADUIGraphitiPlugin.PLUGIN_ID, "modelMap");
	private static final EStructuralFeature[] CONN_INST_PATH = new EStructuralFeature[] { PartitioningPackage.Literals.CONNECT_INTERFACE__USES_PORT,
		PartitioningPackage.Literals.USES_PORT__COMPONENT_INSTANTIATION_REF, PartitioningPackage.Literals.COMPONENT_INSTANTIATION_REF__INSTANTIATION };
	private static final EStructuralFeature[] SPD_PATH = new EStructuralFeature[] { PartitioningPackage.Literals.COMPONENT_INSTANTIATION__PLACEMENT,
		PartitioningPackage.Literals.COMPONENT_PLACEMENT__COMPONENT_FILE_REF, PartitioningPackage.Literals.COMPONENT_FILE_REF__FILE,
		PartitioningPackage.Literals.COMPONENT_FILE__SOFT_PKG };
	private final LocalGraphitiSadMultiPageScaEditor editor;
	private final SoftwareAssembly sad;

	// maps containing to uniquely identify component/connections, use with synchronized statement
	private final Map<String, NodeMapEntry> nodes = Collections.synchronizedMap(new HashMap<String, NodeMapEntry>());
	private final Map<String, ConnectionMapEntry> connections = Collections.synchronizedMap(new HashMap<String, ConnectionMapEntry>());

	// actions for starting/stopping components
	private final StartAction startAction = new StartAction();
	private final StopAction stopAction = new StopAction();

	private final LocalScaWaveform waveform;

	public GraphitiModelMap(@NonNull final LocalGraphitiSadMultiPageScaEditor editor, @NonNull final SoftwareAssembly sad,
		@NonNull final LocalScaWaveform waveform) {
		Assert.isNotNull(waveform, "Sandbox Waveform must not be null");
		Assert.isNotNull(editor, "Sandbox Editor must not be null");
		Assert.isNotNull(sad, "Software Assembly must not be null");
		this.waveform = waveform;
		this.sad = sad;
		this.editor = editor;
	}

	/**
	 * New LocalScaComponent was recently added and this method will now add
	 * a SadComponentInstiation to the SofwareAssembly of the Graphiti Diagram.
	 * @param comp
	 */
	@SuppressWarnings("restriction")
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

			private SadComponentInstantiation create(LocalScaComponent comp) {
				// TODO Auto-generated method stub
				return null;
			}

		};
		job.schedule();

	}

	/**
	 * New SadComponentInstantiation was recently added to the diagram and this method will now launch
	 * the corresponding LocalScaComponent
	 * @param comp
	 */
	@SuppressWarnings("restriction")
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
	@SuppressWarnings("unchecked")
	@NonNull
	private SadComponentInstantiation create(@NonNull final LocalScaComponent newValue) throws CoreException {

		// get SoftPkg
		newValue.fetchAttributes(null);
		final SoftPkg spd = newValue.fetchProfileObject(null);
		if (spd == null) {
			throw new IllegalStateException("Unable to load New components spd");
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
		final ScaUsesPort usesPort = (ScaUsesPort) sourceComp.getScaPort(conn.getUsesPort().getUsesIndentifier());
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
	@SuppressWarnings("unchecked")
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
		if (sadComponentInstantiation == null || editor.isDisposed()) {
			return;
		}
		// setup to perform diagram operations
		final IDiagramTypeProvider provider = editor.getDiagramEditor().getDiagramTypeProvider();
		final IFeatureProvider featureProvider = provider.getFeatureProvider();
		final Diagram diagram = provider.getDiagram();

		// get pictogram for component
		final PictogramElement[] peToRemove = {DUtil.getPictogramElementForBusinessObject(diagram, sadComponentInstantiation, ComponentShapeImpl.class)};

		// Delete Component in transaction
		final TransactionalEditingDomain editingDomain = (TransactionalEditingDomain) editor.getEditingDomain();
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {

				// delete shape & component
				// We use release here instead of delete feature to control context menu options
				CustomContext context = new CustomContext(peToRemove);
				for (ICustomFeature feature : featureProvider.getCustomFeatures(context)) {
					if (feature instanceof ReleaseComponentFeature) {
						((ReleaseComponentFeature) feature).execute(context);
					}
				}
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
	 * @param localScaComponent
	 * @param started
	 */
	public void startStopComponent(LocalScaComponent localScaComponent, final Boolean started) {
		final NodeMapEntry nodeMapEntry = nodes.get(NodeMapEntry.getKey(localScaComponent));
		if (nodeMapEntry == null) {
			return;
		}
		final SadComponentInstantiation sadComponentInstantiation = nodeMapEntry.getProfile();

		// setup to perform diagram operations
		final IDiagramTypeProvider provider = editor.getDiagramEditor().getDiagramTypeProvider();
		final IFeatureProvider featureProvider = provider.getFeatureProvider();
		final TransactionalEditingDomain editingDomain = featureProvider.getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		final Diagram diagram = provider.getDiagram();

		// get pictogram for component
		final ComponentShape componentShape = (ComponentShape) DUtil.getPictogramElementForBusinessObject(diagram, sadComponentInstantiation,
			ComponentShapeImpl.class);

		if (componentShape != null) {
			Job job = new Job("Syncronizing diagram start/stop status: " + localScaComponent.getInstantiationIdentifier()) {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					// Perform business object manipulation in a Command
					TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
					stack.execute(new RecordingCommand(editingDomain) {
						@Override
						protected void doExecute() {
							// paint component
							componentShape.setStarted(started);
						}
					});
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}
	}

	/**
	 * Starts/Stops the component in the ScaExplorer as a result of the user
	 * stopping/starting the component in the diagram
	 * @param localScaComponent
	 * @param started
	 */
	public void startStopComponent(final SadComponentInstantiation comp, final Boolean started) {
		if (comp == null) {
			return;
		}
		final NodeMapEntry nodeMapEntry = nodes.get(NodeMapEntry.getKey(comp));
		if (nodeMapEntry == null) {
			return;
		}
		final LocalScaComponent localComp = nodeMapEntry.getLocalScaComponent();

		if (started) {
			Job job = new Job("Starting " + comp.getUsageName()) {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					startAction.setContext(localComp);
					startAction.run();
					return Status.OK_STATUS;

				}

			};
			job.schedule();
			// stop
		} else {
			Job job = new Job("Stopping " + comp.getUsageName()) {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					stopAction.setContext(localComp);
					stopAction.run();
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}
	}

	private void paintComponent(final ComponentShape componentShape, final Boolean started) {
		final WorkbenchJob job = new WorkbenchJob("Repainting Component") {

			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor) {
				if (componentShape.isActive()) {

					// setup to perform diagram operations
					final IDiagramTypeProvider provider = editor.getDiagramEditor().getDiagramTypeProvider();
					final IFeatureProvider featureProvider = provider.getFeatureProvider();
					TransactionalEditingDomain editingDomain = featureProvider.getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

					final RoundedRectangle innerRoundedRectangle = (RoundedRectangle) DUtil.findFirstPropertyContainer(componentShape,
						RHContainerShapeImpl.GA_INNER_ROUNDED_RECTANGLE);
					final Diagram diagram = DUtil.findDiagram(componentShape);

					// Perform business object manipulation in a Command
					TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
					stack.execute(new RecordingCommand(editingDomain) {
						@Override
						protected void doExecute() {

							if (innerRoundedRectangle != null) {
								if (started != null && started) {
									// started
									innerRoundedRectangle.setStyle(StyleUtil.createStyleForComponentInnerStarted(diagram));
								} else {
									// not started
									innerRoundedRectangle.setStyle(StyleUtil.createStyleForComponentInner(diagram));
								}
							}
						}
					});

				}
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.setUser(false);
		job.schedule();
	}

	/**
	 * Modifies the diagram to reflect component runtime status
	 */
	public void reflectRuntimeStatus() {

		// setup to perform diagram operations
		final IDiagramTypeProvider provider = editor.getDiagramEditor().getDiagramTypeProvider();
		final Diagram diagram = provider.getDiagram();
		final IFeatureProvider featureProvider = provider.getFeatureProvider();
		final TransactionalEditingDomain editingDomain = featureProvider.getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		Job job = new Job("Syncronizing component started status") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				for (String nodeKey : nodes.keySet()) {
					final NodeMapEntry nodeMapEntry = nodes.get(nodeKey);

					// get pictogram for component
					final ComponentShape componentShape = (ComponentShape) DUtil.getPictogramElementForBusinessObject(diagram, nodeMapEntry.getProfile(),
						ComponentShapeImpl.class);

					final boolean started = nodeMapEntry.getLocalScaComponent().getStarted();
					if (started) {

						// Perform business object manipulation in a Command
						TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
						stack.execute(new RecordingCommand(editingDomain) {
							@Override
							protected void doExecute() {
								// paint component
								componentShape.setStarted(true);
							}
						});
					}
				}
				return Status.OK_STATUS;
			}
		};
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

}
