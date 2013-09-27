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
package gov.redhawk.ide.debug.internal.ui.diagram;

import gov.redhawk.diagram.edit.helpers.ComponentPlacementEditHelperAdvice;
import gov.redhawk.diagram.edit.helpers.ConnectInterfaceEditHelperAdvice;
import gov.redhawk.ide.debug.LocalScaComponent;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.ui.diagram.LocalScaDiagramPlugin;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaConnection;
import gov.redhawk.model.sca.ScaPort;
import gov.redhawk.model.sca.ScaProvidesPort;
import gov.redhawk.model.sca.ScaUsesPort;
import gov.redhawk.sca.sad.diagram.edit.parts.SadComponentInstantiationEditPart;
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
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.sad.diagram.edit.parts.SadConnectInterfaceEditPart;
import mil.jpeojtrs.sca.sad.diagram.providers.SadElementTypes;
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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.diagram.core.preferences.PreferencesHint;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramEditDomain;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewAndElementRequest.ConnectionViewAndElementDescriptor;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequestFactory;
import org.eclipse.gmf.runtime.diagram.ui.requests.EditCommandRequestWrapper;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyElementRequest;
import org.eclipse.gmf.runtime.notation.Connector;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import CF.DataType;
import CF.LifeCyclePackage.ReleaseError;
import CF.PortPackage.InvalidPort;
import CF.PortPackage.OccupiedPort;

public class ModelMap {
	private static final EStructuralFeature[] SPD_PATH = new EStructuralFeature[] { PartitioningPackage.Literals.COMPONENT_INSTANTIATION__PLACEMENT,
		PartitioningPackage.Literals.COMPONENT_PLACEMENT__COMPONENT_FILE_REF, PartitioningPackage.Literals.COMPONENT_FILE_REF__FILE,
		PartitioningPackage.Literals.COMPONENT_FILE__SOFT_PKG };
	private final LocalScaEditor editor;
	private final SoftwareAssembly sad;

	private static class NodeMap {
		LocalScaComponent comp;
		SadComponentInstantiation profile;

		@Nullable String getKey() {
			if (comp != null) {
				return comp.getInstantiationIdentifier();
			} else if (profile != null) {
				return profile.getId();
			}
			return null;
		}

		static String getKey(LocalScaComponent obj) {
			return obj.getInstantiationIdentifier();
		}

		static String getKey(SadComponentInstantiation obj) {
			return obj.getId();
		}
	}

	private static class ConnectionMap {
		ScaConnection conn;
		SadConnectInterface profile;

		@Nullable String getKey() {
			if (conn != null) {
				return conn.getId();
			} else if (profile != null) {
				return profile.getId();
			}
			return null;
		}

		static String getKey(ScaConnection obj) {
			return obj.getId();
		}

		static String getKey(SadConnectInterface obj) {
			return obj.getId();
		}
	}

	private final Map<String, NodeMap> nodes = Collections.synchronizedMap(new HashMap<String, NodeMap>());
	private final Map<String, ConnectionMap> connections = Collections.synchronizedMap(new HashMap<String, ConnectionMap>());

	private final LocalScaWaveform waveform;

	public ModelMap(@NonNull final LocalScaEditor editor, @NonNull final SoftwareAssembly sad, @NonNull final LocalScaWaveform waveform) {
		Assert.isNotNull(waveform, "Sandbox Waveform must not be null");
		Assert.isNotNull(editor, "Sandbox Editor must not be null");
		Assert.isNotNull(sad, "Software Assembly must not be null");
		this.waveform = waveform;
		this.sad = sad;
		this.editor = editor;
	}

	public void add(@NonNull final LocalScaComponent comp) {
		final NodeMap nodeMap = new NodeMap();
		nodeMap.comp = comp;
		synchronized (nodes) {
			if (nodes.get(nodeMap.getKey()) != null) {
				return;
			} else {
				nodes.put(nodeMap.getKey(), nodeMap);
			}
		}
		Job job = new Job("Adding component: " + comp.getInstantiationIdentifier()) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				SubMonitor subMonitor = SubMonitor.convert(monitor, "Adding component: " + comp.getInstantiationIdentifier(), IProgressMonitor.UNKNOWN);
				try {
					SadComponentInstantiation newComp = create(comp);
					if (nodes.get(nodeMap.getKey()) == null) {
						// component already removed
						delete(comp);
						delete(newComp);
					} else {
						nodeMap.profile = newComp;
					}
					return Status.OK_STATUS;
				} catch (Exception e) {
					nodes.remove(nodeMap.getKey());
					return new Status(Status.ERROR, LocalScaDiagramPlugin.PLUGIN_ID, "Failed to add component " + comp.getInstantiationIdentifier(), e);
				} finally {
					subMonitor.done();
				}
			}

		};
		job.schedule();
	}

	public void add(@NonNull final SadComponentInstantiation comp) {
		final NodeMap nodeMap = new NodeMap();
		nodeMap.profile = comp;
		synchronized (nodes) {
			if (nodes.get(nodeMap.getKey()) != null) {
				return;
			} else {
				nodes.put(nodeMap.getKey(), nodeMap);
			}
		}

		final String implID = ((SadComponentInstantiationImpl) comp).getImplID();
		Job job = new Job("Launching " + comp.getUsageName()) {

			@Override
			@Nullable
			protected IStatus run(IProgressMonitor monitor) {
				SubMonitor subMonitor = SubMonitor.convert(monitor, "Launching " + comp.getUsageName(), IProgressMonitor.UNKNOWN);
				try {
					LocalScaComponent newComp = create(comp, implID);
					if (nodes.get(nodeMap.getKey()) == null) {
						// component already removed
						delete(newComp);
						delete(comp);
					} else {
						nodeMap.comp = newComp;
						EditPart editPart = editor.getDiagramEditPart().findEditPart(editor.getDiagramEditPart(), comp);
						if (editPart instanceof SadComponentInstantiationEditPart) {
							SadComponentInstantiationEditPart ciEp = (SadComponentInstantiationEditPart) editPart;
							ciEp.addRuntimeListeners();
						}
					}
					return Status.OK_STATUS;
				} catch (final CoreException e) {
					delete(comp);
					nodes.remove(nodeMap.getKey());
					return e.getStatus();
				} catch (ReleaseError e) {
					return new Status(Status.ERROR, LocalScaDiagramPlugin.PLUGIN_ID, "Failed to remove component ", e);
				} finally {
					subMonitor.done();
				}
			}

		};
		job.schedule();
	}

	public void add(@NonNull final SadConnectInterface conn) {
		final ConnectionMap connectionMap = new ConnectionMap();
		connectionMap.profile = conn;
		synchronized (connections) {
			if (connections.get(connectionMap.getKey()) != null) {
				return;
			} else {
				connections.put(connectionMap.getKey(), connectionMap);
			}
		}
		Job job = new Job("Connecting " + conn.getId()) {

			@SuppressWarnings("null")
			@Override
			@NonNull
			protected IStatus run(IProgressMonitor monitor) {
				SubMonitor subMonitor = SubMonitor.convert(monitor, "Connecting " + conn.getId(), IProgressMonitor.UNKNOWN);
				try {
					try {
						ScaConnection newConnection = create(conn);
						connectionMap.conn = newConnection;
						return Status.OK_STATUS;
					} catch (final InvalidPort e) {
						delete(conn);
						connections.remove(connectionMap.getKey());
						return new Status(Status.ERROR, LocalScaDiagramPlugin.PLUGIN_ID, "Failed to add connection " + conn.getId(), e);
					} catch (final OccupiedPort e) {
						delete(conn);
						connections.remove(connectionMap.getKey());
						return new Status(Status.ERROR, LocalScaDiagramPlugin.PLUGIN_ID, "Failed to add connection " + conn.getId(), e);
					}
				} finally {
					subMonitor.done();
				}
			}

		};
		job.schedule();
	}

	public void add(@NonNull final ScaConnection conn) {
		final ConnectionMap connectionMap = new ConnectionMap();
		connectionMap.conn = conn;
		synchronized (connections) {
			if (connections.get(connectionMap.getKey()) != null) {
				return;
			} else {
				connections.put(connectionMap.getKey(), connectionMap);
			}
		}
		Job job = new Job("Adding connection " + conn.getId()) {

			@SuppressWarnings("null")
			@Override
			@NonNull
			protected IStatus run(IProgressMonitor monitor) {
				SubMonitor subMonitor = SubMonitor.convert(monitor, "Adding connection " + conn.getId(), IProgressMonitor.UNKNOWN);
				try {
					SadConnectInterface newSadInterface = create(conn);
					connectionMap.profile = newSadInterface;
					return Status.OK_STATUS;
				} catch (Exception e) {
					connections.remove(connectionMap.getKey());
					return new Status(Status.ERROR, LocalScaDiagramPlugin.PLUGIN_ID, "Failed to add connection " + conn.getId(), e);
				} finally {
					subMonitor.done();
				}
			}

		};
		job.schedule();
	}

	@SuppressWarnings("unchecked")
	@Nullable
	private SadComponentInstantiation create(@NonNull final LocalScaComponent newValue) {
		final DiagramEditPart diagramEditPart = getDiagramEditPart();
		if (diagramEditPart == null) {
			return null;
		}
		final CreateViewRequest createRequest = CreateViewRequestFactory.getCreateShapeRequest(SadElementTypes.SadComponentPlacement_3001,
			diagramEditPart.getDiagramPreferencesHint());

		final HashMap<Object, Object> map = new HashMap<Object, Object>();
		map.putAll(createRequest.getExtendedData());
		newValue.fetchAttributes(null);
		final SoftPkg spd = newValue.fetchProfileObject(null);
		if (spd == null) {
			throw new IllegalStateException("Unable to load New components spd");
		}
		final URI spdURI = EcoreUtil.getURI(spd);
		final SadComponentInstantiation retVal = SadFactory.eINSTANCE.createSadComponentInstantiation();

		map.put(ComponentPlacementEditHelperAdvice.CONFIGURE_OPTIONS_SPD_URI, spdURI);
		map.put(ComponentPlacementEditHelperAdvice.CONFIGURE_OPTIONS_INST_ID, newValue.getInstantiationIdentifier());
		map.put(ComponentPlacementEditHelperAdvice.CONFIGURE_OPTIONS_INST_NAME, newValue.getName());
		map.put(ComponentPlacementEditHelperAdvice.CONFIGURE_COMPONENT_INSTANTIATION, retVal);
		map.put(ComponentPlacementEditHelperAdvice.CONFIGURE_OPTIONS_IMPL_ID, newValue.getImplementationID());

		createRequest.setExtendedData(map);
		final Command command = diagramEditPart.getCommand(createRequest);
		execute(command);

		return retVal;
	}

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
		final SoftPkg spd = ScaEcoreUtils.getFeature(comp, ModelMap.SPD_PATH);
		if (spd == null) {
			throw new CoreException(new Status(Status.ERROR, LocalScaDiagramPlugin.PLUGIN_ID, "Failed to resolve SPD.", null));
		}
		final URI spdURI = spd.eResource().getURI();
		return this.waveform.launch(comp.getId(), execParams, spdURI, implID, ILaunchManager.RUN_MODE);
	}

	private static final EStructuralFeature[] CONN_INST_PATH = new EStructuralFeature[] { PartitioningPackage.Literals.CONNECT_INTERFACE__USES_PORT,
		PartitioningPackage.Literals.USES_PORT__COMPONENT_INSTANTIATION_REF, PartitioningPackage.Literals.COMPONENT_INSTANTIATION_REF__INSTANTIATION };

	@Nullable
	private ScaConnection create(@NonNull final SadConnectInterface conn) throws InvalidPort, OccupiedPort {
		SadComponentInstantiation inst = ScaEcoreUtils.getFeature(conn, CONN_INST_PATH);
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

	@SuppressWarnings("unchecked")
	@Nullable
	private SadConnectInterface create(@NonNull final ScaConnection newValue) {
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

		final EditPart sourceEditPart = findEditPart(source);
		final EditPart targetEditPart = findEditPart(target);
		if (sourceEditPart == null || targetEditPart == null) {
			return null;
		}

		PreferencesHint hint = null;
		DiagramEditPart diagramEditPart = getDiagramEditPart();
		if (diagramEditPart != null) {
			hint = diagramEditPart.getDiagramPreferencesHint();
		}
		final CreateConnectionViewRequest ccr = CreateViewRequestFactory.getCreateConnectionRequest(SadElementTypes.SadConnectInterface_4001,
			hint);
		final HashMap<Object, Object> map = new HashMap<Object, Object>();
		map.putAll(ccr.getExtendedData());
		map.put(ConnectInterfaceEditHelperAdvice.CONFIGURE_OPTIONS_ID, newValue.getId());
		//			map.put(ConnectInterfaceEditHelperAdvice.CONFIGURE_OPTIONS_SOURCE, source);
		//			map.put(ConnectInterfaceEditHelperAdvice.CONFIGURE_OPTIONS_TARGET, target);
		ccr.setExtendedData(map);
		ccr.setType(org.eclipse.gef.RequestConstants.REQ_CONNECTION_START);
		ccr.setSourceEditPart(sourceEditPart);
		sourceEditPart.getCommand(ccr);
		ccr.setTargetEditPart(targetEditPart);
		ccr.setType(org.eclipse.gef.RequestConstants.REQ_CONNECTION_END);
		final Command cmd = targetEditPart.getCommand(ccr);
		execute(cmd);

		final Object newObject = ccr.getNewObject();
		final ConnectionViewAndElementDescriptor desc = (ConnectionViewAndElementDescriptor) newObject;
		final Connector connector = (Connector) desc.getAdapter(Connector.class);

		final SadConnectInterface retVal = (SadConnectInterface) connector.getElement();
		return retVal;

	}

	/**
	 * @param oldComp
	 * @throws ReleaseError 
	 */
	private void delete(@NonNull final LocalScaComponent oldComp) throws ReleaseError {
		if (!oldComp.isDisposed()) {
			oldComp.releaseObject();
		}
	}

	private void delete(@Nullable final SadComponentInstantiation oldValue) {
		final EditPart editPart = findEditPart(oldValue);
		if (editPart == null) {
			return;
		}
		final DestroyElementRequest request = new DestroyElementRequest(getEditingDomain(), false);
		request.setElementToDestroy(oldValue);
		request.getParameters().clear();
		final Command command = editPart.getCommand(new EditCommandRequestWrapper(request));
		execute(command);
	}

	private void delete(@NonNull final SadConnectInterface connection) {
		EditPart editPart = null;
		for (final Object obj : this.editor.getDiagramEditPart().getConnections()) {
			if (obj instanceof SadConnectInterfaceEditPart) {
				final SadConnectInterfaceEditPart part = (SadConnectInterfaceEditPart) obj;
				if (part.getAdapter(SadConnectInterface.class) == connection) {
					editPart = part;
					break;
				}
			}
		}

		if (editPart != null) {
			final DestroyElementRequest request = new DestroyElementRequest(getEditingDomain(), false);
			request.setElementToDestroy(connection);
			request.getParameters().clear();
			final Command command = editPart.getCommand(new EditCommandRequestWrapper(request));
			execute(command);
		}
	}

	/**
	 * @param oldConnection
	 * @throws InvalidPort 
	 */
	private void delete(@NonNull final ScaConnection oldConnection) throws InvalidPort {
		if (oldConnection.getPort() != null && !oldConnection.getPort().isDisposed()) {
			oldConnection.getPort().disconnectPort(oldConnection);
		}
	}

	private void execute(@Nullable final Command command) {
		if (command != null && command.canExecute()) {
			IDiagramEditDomain domain = getDiagramEditDomain();
			if (domain != null) {
				domain.getDiagramCommandStack().execute(command);
			}
		}
	}

	@Nullable
	private EditPart findEditPart(@Nullable final EObject obj) {
		return this.editor.getDiagramEditor().getDiagramEditPart().findEditPart(null, obj);
	}

	@Nullable
	public SadComponentInstantiation get(@Nullable final LocalScaComponent comp) {
		if (comp == null) {
			return null;
		}
		NodeMap nodeMap = nodes.get(NodeMap.getKey(comp));
		if (nodeMap != null) {
			return nodeMap.profile;
		} else {
			return null;
		}
	}

	@Nullable
	public LocalScaComponent get(@Nullable final SadComponentInstantiation compInst) {
		if (compInst == null) {
			return null;
		}
		NodeMap nodeMap = nodes.get(NodeMap.getKey(compInst));
		if (nodeMap != null) {
			return nodeMap.comp;
		} else {
			return null;
		}
	}

	@Nullable
	public ScaConnection get(@Nullable final SadConnectInterface conn) {
		if (conn == null) {
			return null;
		}
		ConnectionMap connectionMap = connections.get(ConnectionMap.getKey(conn));
		if (connectionMap != null) {
			return connectionMap.conn;
		} else {
			return null;
		}
	}

	@Nullable
	public SadConnectInterface get(@Nullable final ScaConnection conn) {
		if (conn == null) {
			return null;
		}
		ConnectionMap connectionMap = connections.get(ConnectionMap.getKey(conn));
		if (connectionMap != null) {
			return connectionMap.profile;
		} else {
			return null;
		}
	}

	@Nullable
	private IDiagramEditDomain getDiagramEditDomain() {
		return this.editor.getDiagramEditDomain();
	}

	@Nullable
	private DiagramEditPart getDiagramEditPart() {
		return this.editor.getDiagramEditPart();
	}

	@Nullable
	private TransactionalEditingDomain getEditingDomain() {
		return this.editor.getDiagramEditor().getEditingDomain();
	}

	public void remove(@NonNull final LocalScaComponent comp) {
		final NodeMap nodeMap = nodes.remove(NodeMap.getKey(comp));
		if (nodeMap == null) {
			return;
		}
		final SadComponentInstantiation oldComp = nodeMap.profile;
		if (oldComp != null) {
			Job job = new Job("Removing " + comp.getInstantiationIdentifier()) {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					delete(oldComp);
					return Status.OK_STATUS;
				}

			};
			job.schedule();
		}
	}

	public void remove(@NonNull final SadComponentInstantiation comp) {
		final NodeMap nodeMap = nodes.remove(NodeMap.getKey(comp));
		if (nodeMap == null) {
			return;
		}
		final LocalScaComponent oldComp = nodeMap.comp;
		if (oldComp != null) {
			Job job = new Job("Releasing " + comp.getUsageName()) {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					SubMonitor subMonitor = SubMonitor.convert(monitor, "Releasing " + comp.getUsageName(), IProgressMonitor.UNKNOWN);
					try {
						delete(oldComp);
						return Status.OK_STATUS;
					} catch (ReleaseError e) {
						return new Status(Status.WARNING, LocalScaDiagramPlugin.PLUGIN_ID, "Problems while removing component " + comp.getId(), e);
					} finally {
						subMonitor.done();
					}
				}

			};
			job.schedule();
		}
	}

	public void remove(@NonNull final SadConnectInterface conn) {
		final ConnectionMap connectionMap = connections.remove(ConnectionMap.getKey(conn));
		if (connectionMap == null) {
			return;
		}
		final ScaConnection oldConnection = connectionMap.conn;
		if (oldConnection != null) {
			Job job = new Job("Disconnect connection " + conn.getId()) {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					SubMonitor subMonitor = SubMonitor.convert(monitor, "Disconnect connection " + conn.getId(), IProgressMonitor.UNKNOWN);
					try {
						delete(oldConnection);
						return Status.OK_STATUS;
					} catch (InvalidPort e) {
						return new Status(Status.WARNING, LocalScaDiagramPlugin.PLUGIN_ID, "Problems while removing connection " + conn.getId(), e);
					} finally {
						subMonitor.done();
					}
				}

			};
			job.schedule();
		}
	}

	public void remove(@NonNull final ScaConnection conn) {
		final ConnectionMap connectionMap = connections.remove(ConnectionMap.getKey(conn));
		if (connectionMap == null) {
			return;
		}
		final SadConnectInterface oldSadInterface = connectionMap.profile;
		if (oldSadInterface != null) {
			Job job = new Job("Disconnect connection " + conn.getId()) {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					delete(oldSadInterface);
					return Status.OK_STATUS;
				}

			};
			job.schedule();
		}

	}

	/**
	 * @param con
	 * @param sadCon
	 */
	public void put(@NonNull ScaConnection con, @NonNull SadConnectInterface sadCon) {
		ConnectionMap connectionMap = new ConnectionMap();
		connectionMap.conn = con;
		connectionMap.profile = sadCon;
		connections.put(connectionMap.getKey(), connectionMap);
	}

	/**
	 * @param comp
	 * @param inst
	 */
	public void put(@NonNull LocalScaComponent comp, @NonNull SadComponentInstantiation inst) {
		NodeMap nodeMap = new NodeMap();
		nodeMap.comp = comp;
		nodeMap.profile = inst;
		nodes.put(nodeMap.getKey(), nodeMap);
	}

}
