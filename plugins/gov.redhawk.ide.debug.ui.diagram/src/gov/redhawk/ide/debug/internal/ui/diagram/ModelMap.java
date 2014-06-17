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
import gov.redhawk.sca.util.Debug;
import gov.redhawk.sca.util.SubMonitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

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
import mil.jpeojtrs.sca.util.ProtectedThreadExecutor;
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
import org.eclipse.gef.Request;
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
import org.eclipse.ui.PlatformUI;
import org.omg.CORBA.SystemException;

import CF.DataType;
import CF.LifeCyclePackage.ReleaseError;
import CF.PortPackage.InvalidPort;
import CF.PortPackage.OccupiedPort;

public class ModelMap {
	private static final Debug DEBUG = new Debug(LocalScaDiagramPlugin.PLUGIN_ID, "modelMap");
	private static final EStructuralFeature[] CONN_INST_PATH = new EStructuralFeature[] { PartitioningPackage.Literals.CONNECT_INTERFACE__USES_PORT,
		PartitioningPackage.Literals.USES_PORT__COMPONENT_INSTANTIATION_REF, PartitioningPackage.Literals.COMPONENT_INSTANTIATION_REF__INSTANTIATION };

	private static final EStructuralFeature[] SPD_PATH = new EStructuralFeature[] { PartitioningPackage.Literals.COMPONENT_INSTANTIATION__PLACEMENT,
		PartitioningPackage.Literals.COMPONENT_PLACEMENT__COMPONENT_FILE_REF, PartitioningPackage.Literals.COMPONENT_FILE_REF__FILE,
		PartitioningPackage.Literals.COMPONENT_FILE__SOFT_PKG };
	private final LocalScaEditor editor;
	private final SoftwareAssembly sad;

	private final Map<String, NodeMapEntry> nodes = Collections.synchronizedMap(new HashMap<String, NodeMapEntry>());
	private final Map<String, ConnectionMapEntry> connections = Collections.synchronizedMap(new HashMap<String, ConnectionMapEntry>());

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
		final NodeMapEntry nodeMap = new NodeMapEntry();
		nodeMap.setLocalScaComponent(comp);
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
				SadComponentInstantiation newComp = null;
				try {
					newComp = create(comp);
					nodeMap.setProfile(newComp);
					return Status.OK_STATUS;
				} catch (CoreException e) {
					nodes.remove(nodeMap.getKey());
					return new Status(IStatus.ERROR, LocalScaDiagramPlugin.PLUGIN_ID, "Failed to add component " + comp.getInstantiationIdentifier(), e);
				} finally {
					if (nodes.get(nodeMap.getKey()) == null) {
						delete(newComp);
					}
					subMonitor.done();
				}
			}

		};
		job.schedule();
	}

	public void add(@NonNull final SadComponentInstantiation comp) {
		final NodeMapEntry nodeMap = new NodeMapEntry();
		nodeMap.setProfile(comp);
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
				LocalScaComponent newComp = null;
				try {
					newComp = create(comp, implID);
					nodeMap.setLocalScaComponent(newComp);
					// nodeMap.setLocalScaComponent(newComp);
					EditPart editPart = editor.getDiagramEditPart().findEditPart(editor.getDiagramEditPart(), comp);
					if (editPart instanceof SadComponentInstantiationEditPart) {
						SadComponentInstantiationEditPart ciEp = (SadComponentInstantiationEditPart) editPart;
						ciEp.addRuntimeListeners();
					}
					return Status.OK_STATUS;
				} catch (final CoreException e) {
					delete(comp);
					nodes.remove(nodeMap.getKey());
					return e.getStatus();
				} finally {
					if (nodes.get(nodeMap.getKey()) == null) {
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
					ScaConnection newConnection = create(conn);
					if (newConnection == null) {
						delete(conn);
						connections.remove(connectionMap.getKey());
						return new Status(IStatus.ERROR, LocalScaDiagramPlugin.PLUGIN_ID,
							"Failed to add connection, source or target component may not have finished launching. " + conn.getId(), null);
					} else {
						connectionMap.setScaConnection(newConnection);
						return Status.OK_STATUS;
					}
				} catch (final InvalidPort e) {
					delete(conn);
					connections.remove(connectionMap.getKey());
					return new Status(IStatus.ERROR, LocalScaDiagramPlugin.PLUGIN_ID, "Failed to add connection " + conn.getId(), e);
				} catch (final OccupiedPort e) {
					delete(conn);
					connections.remove(connectionMap.getKey());
					return new Status(IStatus.ERROR, LocalScaDiagramPlugin.PLUGIN_ID, "Failed to add connection " + conn.getId(), e);
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
					newSadInterface = create(conn);
					if (newSadInterface == null) {
						connections.remove(connectionMap.getKey());
						return Status.CANCEL_STATUS;
					}
					connectionMap.setProfile(newSadInterface);
					return Status.OK_STATUS;
				} catch (CoreException e) {
					connections.remove(connectionMap.getKey());
					return new Status(IStatus.ERROR, LocalScaDiagramPlugin.PLUGIN_ID, "Failed to add connection " + conn.getId(), e);
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

	@SuppressWarnings("unchecked")
	@NonNull
	private SadComponentInstantiation create(@NonNull final LocalScaComponent newValue) throws CoreException {
		final DiagramEditPart diagramEditPart = getDiagramEditPart();
		if (diagramEditPart == null) {
			throw new CoreException(new Status(IStatus.ERROR, LocalScaDiagramPlugin.PLUGIN_ID, "Failed to find diagram edit part", null));
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
		if (retVal == null) {
			throw new IllegalStateException();
		}

		map.put(ComponentPlacementEditHelperAdvice.CONFIGURE_OPTIONS_SPD_URI, spdURI);
		map.put(ComponentPlacementEditHelperAdvice.CONFIGURE_OPTIONS_ALWAYS_CP_CREATE_FILE, true);
		map.put(ComponentPlacementEditHelperAdvice.CONFIGURE_OPTIONS_INST_ID, newValue.getInstantiationIdentifier());
		map.put(ComponentPlacementEditHelperAdvice.CONFIGURE_OPTIONS_INST_NAME, newValue.getName());
		map.put(ComponentPlacementEditHelperAdvice.CONFIGURE_COMPONENT_INSTANTIATION, retVal);
		map.put(ComponentPlacementEditHelperAdvice.CONFIGURE_OPTIONS_IMPL_ID, newValue.getImplementationID());

		createRequest.setExtendedData(map);
		createCommandAndExecute(diagramEditPart, createRequest);
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
			throw new CoreException(new Status(IStatus.ERROR, LocalScaDiagramPlugin.PLUGIN_ID, "Failed to resolve SPD.", null));
		}
		final URI spdURI = spd.eResource().getURI();
		return this.waveform.launch(comp.getId(), execParams, spdURI, implID, ILaunchManager.RUN_MODE);
	}

	@Nullable
	private ScaConnection create(@NonNull final SadConnectInterface conn) throws InvalidPort, OccupiedPort {
		SadComponentInstantiation inst = ScaEcoreUtils.getFeature(conn, ModelMap.CONN_INST_PATH);
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

		final EditPart sourceEditPart = findEditPart(source);
		final EditPart targetEditPart = findEditPart(target);
		if (sourceEditPart == null || targetEditPart == null) {
			if (ModelMap.DEBUG.enabled) {
				ModelMap.DEBUG.trace("Failed to edit parts for source and target for source={0} and target={1}", source, target);
			}
			return null;
		}

		PreferencesHint hint = null;
		DiagramEditPart diagramEditPart = getDiagramEditPart();
		if (diagramEditPart != null) {
			hint = diagramEditPart.getDiagramPreferencesHint();
		}
		final CreateConnectionViewRequest ccr = CreateViewRequestFactory.getCreateConnectionRequest(SadElementTypes.SadConnectInterface_4001, hint);
		final HashMap<Object, Object> map = new HashMap<Object, Object>();
		map.putAll(ccr.getExtendedData());
		map.put(ConnectInterfaceEditHelperAdvice.CONFIGURE_OPTIONS_ID, newValue.getId());
		// map.put(ConnectInterfaceEditHelperAdvice.CONFIGURE_OPTIONS_SOURCE, source);
		// map.put(ConnectInterfaceEditHelperAdvice.CONFIGURE_OPTIONS_TARGET, target);
		ccr.setExtendedData(map);
		ccr.setType(org.eclipse.gef.RequestConstants.REQ_CONNECTION_START);
		ccr.setSourceEditPart(sourceEditPart);
		sourceEditPart.getCommand(ccr);
		ccr.setTargetEditPart(targetEditPart);
		ccr.setType(org.eclipse.gef.RequestConstants.REQ_CONNECTION_END);
		createCommandAndExecute(targetEditPart, ccr);

		final Object newObject = ccr.getNewObject();
		final ConnectionViewAndElementDescriptor desc = (ConnectionViewAndElementDescriptor) newObject;
		final Connector connector = (Connector) desc.getAdapter(Connector.class);

		final SadConnectInterface retVal = (SadConnectInterface) connector.getElement();
		return retVal;

	}

	/**
	 * @param ccr
	 * @return
	 */
	private void createCommandAndExecute(final EditPart targetEditPart, final Request request) {
		if (PlatformUI.getWorkbench().getDisplay().isDisposed()) {
			return;
		}
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				try {
					final Command cmd = targetEditPart.getCommand(request);
					execute(cmd);
				} catch (Exception e) {
					// PASS
				}

			}

		});
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
			try {
				ProtectedThreadExecutor.submit(new Callable<Object>() {

					@Override
					public Object call() throws Exception {
						oldComp.releaseObject();
						return null;
					}

				});
			} catch (InterruptedException e) {
				// PASS
			} catch (ExecutionException e) {
				// PASS
			} catch (TimeoutException e) {
				// PASS
			}
		}
	}

	private void delete(@Nullable final SadComponentInstantiation oldValue) {
		if (oldValue == null) {
			return;
		}
		final EditPart editPart = findEditPart(oldValue);
		if (editPart == null) {
			return;
		}
		final DestroyElementRequest request = new DestroyElementRequest(getEditingDomain(), false);
		request.setElementToDestroy(oldValue);
		request.getParameters().clear();
		createCommandAndExecute(editPart, new EditCommandRequestWrapper(request));
	}

	private void delete(@Nullable final SadConnectInterface connection) {
		if (connection == null) {
			return;
		}
		EditPart editPart = null;
		int tries = 0;
		while (editPart == null) {
			for (final Object obj : this.editor.getDiagramEditPart().getConnections()) {
				if (obj instanceof SadConnectInterfaceEditPart) {
					final SadConnectInterfaceEditPart part = (SadConnectInterfaceEditPart) obj;
					if (part.getAdapter(SadConnectInterface.class) == connection) {
						editPart = part;
						break;
					}
				}
			}
			tries++;
			if (tries > 4) {
				break;
			} else {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// PASS
				}
			}
		}

		if (editPart != null) {
			final DestroyElementRequest request = new DestroyElementRequest(getEditingDomain(), false);
			request.setElementToDestroy(connection);
			request.getParameters().clear();
			createCommandAndExecute(editPart, new EditCommandRequestWrapper(request));
		} else {
			// PASS TODO Log unable to find ?
		}
	}

	/**
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

	private void execute(@Nullable final Command command) {
		if (command != null && command.canExecute()) {
			IDiagramEditDomain domain = getDiagramEditDomain();
			if (domain != null) {
				domain.getDiagramCommandStack().execute(command);
				domain.getDiagramCommandStack().flush();
			}
		}
	}

	@Nullable
	private EditPart findEditPart(@Nullable final EObject obj) {
		if (editor == null || editor.getDiagramEditor() == null || editor.getDiagramEditor().getDiagramGraphicalViewer() == null
			|| editor.getDiagramEditor().getDiagramEditPart() == null) {
			return null;
		}
		editor.getDiagramEditor().getDiagramGraphicalViewer();
		return this.editor.getDiagramEditor().getDiagramEditPart().findEditPart(null, obj);
	}

	@Nullable
	public SadComponentInstantiation get(@Nullable final LocalScaComponent comp) {
		if (comp == null) {
			return null;
		}
		NodeMapEntry nodeMap = nodes.get(NodeMapEntry.getKey(comp));
		if (nodeMap != null) {
			return nodeMap.getProfile();
		} else {
			return null;
		}
	}

	@Nullable
	public LocalScaComponent get(@Nullable final SadComponentInstantiation compInst) {
		if (compInst == null) {
			return null;
		}
		NodeMapEntry nodeMap = nodes.get(NodeMapEntry.getKey(compInst));
		if (nodeMap != null) {
			return nodeMap.getLocalScaComponent();
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
		final NodeMapEntry nodeMap = nodes.remove(NodeMapEntry.getKey(comp));
		if (nodeMap == null) {
			return;
		}
		final SadComponentInstantiation oldComp = nodeMap.getProfile();
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

	public void remove(final SadComponentInstantiation comp) {
		if (comp == null) {
			return;
		}
		final NodeMapEntry nodeMap = nodes.remove(NodeMapEntry.getKey(comp));
		if (nodeMap == null) {
			return;
		}
		final LocalScaComponent oldComp = nodeMap.getLocalScaComponent();
		if (oldComp != null) {
			Job job = new Job("Releasing " + comp.getUsageName()) {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					SubMonitor subMonitor = SubMonitor.convert(monitor, "Releasing " + comp.getUsageName(), IProgressMonitor.UNKNOWN);
					try {
						delete(oldComp);
						return Status.OK_STATUS;
					} catch (ReleaseError e) {
						return new Status(IStatus.WARNING, LocalScaDiagramPlugin.PLUGIN_ID, "Problems while removing component " + comp.getId(), e);
					} finally {
						subMonitor.done();
					}
				}

			};
			job.schedule();
		}
	}

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
						return new Status(IStatus.WARNING, LocalScaDiagramPlugin.PLUGIN_ID, "Problems while removing connection " + conn.getId(), e);
					} finally {
						subMonitor.done();
					}
				}

			};
			job.schedule();
		}
	}

	public void remove(@NonNull final ScaConnection conn) {
		final ConnectionMapEntry connectionMap = connections.remove(ConnectionMapEntry.getKey(conn));
		if (connectionMap == null) {
			return;
		}
		final SadConnectInterface oldSadInterface = connectionMap.getProfile();
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
		NodeMapEntry nodeMap = new NodeMapEntry();
		nodeMap.setLocalScaComponent(comp);
		nodeMap.setProfile(inst);
		nodes.put(nodeMap.getKey(), nodeMap);
	}

}
