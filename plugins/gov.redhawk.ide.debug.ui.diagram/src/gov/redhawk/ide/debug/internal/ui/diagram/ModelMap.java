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
import gov.redhawk.ide.debug.ui.ScaDebugUiPlugin;
import gov.redhawk.ide.debug.ui.diagram.LocalScaDiagramPlugin;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaConnection;
import gov.redhawk.model.sca.ScaPort;
import gov.redhawk.model.sca.ScaProvidesPort;
import gov.redhawk.model.sca.ScaUsesPort;
import gov.redhawk.sca.util.MutexRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;

import mil.jpeojtrs.sca.partitioning.ConnectionTarget;
import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.prf.AbstractPropertyRef;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.impl.SadComponentInstantiationImpl;
import mil.jpeojtrs.sca.sad.SadConnectInterface;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.sad.diagram.edit.parts.SadConnectInterfaceEditPart;
import mil.jpeojtrs.sca.sad.diagram.providers.SadElementTypes;
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
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramEditDomain;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewAndElementRequest.ConnectionViewAndElementDescriptor;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequestFactory;
import org.eclipse.gmf.runtime.diagram.ui.requests.EditCommandRequestWrapper;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyElementRequest;
import org.eclipse.gmf.runtime.notation.Connector;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;

import CF.DataType;
import CF.ErrorNumberType;
import CF.ExecutableDevicePackage.ExecuteFail;
import CF.LifeCyclePackage.ReleaseError;
import CF.PortPackage.InvalidPort;
import CF.PortPackage.OccupiedPort;

public class ModelMap {
	private static final EStructuralFeature[] SPD_PATH = new EStructuralFeature[] { PartitioningPackage.Literals.COMPONENT_INSTANTIATION__PLACEMENT,
	    PartitioningPackage.Literals.COMPONENT_PLACEMENT__COMPONENT_FILE_REF, PartitioningPackage.Literals.COMPONENT_FILE_REF__FILE,
	    PartitioningPackage.Literals.COMPONENT_FILE__SOFT_PKG };
	private final LocalScaEditor editor;
	private final SoftwareAssembly sad;
	private final Map<EObject, EObject> sadToSca = new HashMap<EObject, EObject>();
	private final Map<EObject, EObject> scaToSad = new HashMap<EObject, EObject>();
	private final LocalScaWaveform waveform;
	private final MutexRule mapRule = new MutexRule(this);

	public ModelMap(final LocalScaEditor editor, final SoftwareAssembly sad, final LocalScaWaveform waveform) {
		Assert.isNotNull(waveform, "Sandbox Waveform must not be null");
		Assert.isNotNull(editor, "Sandbox Editor must not be null");
		Assert.isNotNull(sad, "Software Assembly must not be null");
		this.waveform = waveform;
		this.sad = sad;
		this.editor = editor;
	}

	public void add(final LocalScaComponent comp) {
		if (get(comp) != null) {
			return;
		}
		final Job job = new Job("Adding component: " + comp.getInstantiationIdentifier()) {
			@Override
			public boolean shouldSchedule() {
				return super.shouldSchedule() && get(comp) == null;
			}

			@Override
			public boolean shouldRun() {
				return super.shouldRun() && get(comp) == null;
			}

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				SadComponentInstantiation newComp = get(comp);
				if (newComp != null) {
					return Status.CANCEL_STATUS;
				}
				newComp = create(comp);
				put(comp, newComp);
				return Status.OK_STATUS;
			}
		};
		job.setRule(this.mapRule);
		job.schedule();
	}

	public void add(final SadComponentInstantiation comp) {
		if (get(comp) != null) {
			return;
		}
		final Display display = Display.getCurrent();
		final String implID = ((SadComponentInstantiationImpl) comp).getImplID();
		final Job job = new Job("Launching component " + comp.getUsageName()) {

			@Override
			public boolean shouldRun() {
				return super.shouldRun() && get(comp) == null;
			}

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				monitor.beginTask("Launching " + comp.getUsageName(), IProgressMonitor.UNKNOWN);
				LocalScaComponent newComp = get(comp);
				if (newComp != null) {
					return Status.CANCEL_STATUS;
				}
				try {
					newComp = create(comp, implID);
				} catch (final ExecuteFail e) {
					return new Status(IStatus.ERROR, LocalScaDiagramPlugin.PLUGIN_ID, "Failed to launch: " + comp.getUsageName(), e);
				}
				put(newComp, comp);
				if (display != null) {
					display.wake();
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.setRule(this.mapRule);
		job.schedule();

		if (display != null) {
			final Runnable runnable = new Runnable() {

				public void run() {
					while (!display.isDisposed() && job.getResult() == null) {
						if (!display.readAndDispatch()) {
							display.sleep();
						}
					}
				}
			};
			BusyIndicator.showWhile(display, runnable);
		} else {
			return;
		}

		if (job.getResult() == null || job.getResult() == Status.CANCEL_STATUS) {
			throw new CancellationException();
		} else if (!job.getResult().isOK()) {
			throw new RuntimeException("Failed to start component", job.getResult().getException());
		}
	}

	public void add(final SadConnectInterface conn) {
		if (get(conn) != null) {
			return;
		}
		final Job job = new Job("Connecting " + conn.getId()) {
			@Override
			public boolean shouldSchedule() {
				return super.shouldSchedule() && get(conn) == null;
			}

			@Override
			public boolean shouldRun() {
				return super.shouldRun() && get(conn) == null;
			}

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				ScaConnection newConnection = get(conn);
				if (newConnection != null) {
					return Status.CANCEL_STATUS;
				}
				try {
					newConnection = create(conn);
				} catch (final InvalidPort e) {
					return new Status(IStatus.ERROR, LocalScaDiagramPlugin.PLUGIN_ID, "Failed to connect: " + conn.getId(), e);
				} catch (final OccupiedPort e) {
					return new Status(IStatus.ERROR, LocalScaDiagramPlugin.PLUGIN_ID, "Failed to connect: " + conn.getId(), e);
				}
				put(newConnection, conn);
				return Status.OK_STATUS;
			}
		};
		job.setRule(this.mapRule);
		job.schedule();

	}

	public void add(final ScaConnection conn) {
		if (get(conn) != null) {
			return;
		}
		final Job job = new Job("Adding connection " + conn.getId()) {
			@Override
			public boolean shouldSchedule() {
				return super.shouldSchedule() && get(conn) == null;
			}

			@Override
			public boolean shouldRun() {
				return super.shouldRun() && get(conn) == null;
			}

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				SadConnectInterface newSadInterface = get(conn);
				if (newSadInterface != null) {
					return Status.CANCEL_STATUS;
				}
				newSadInterface = create(conn);
				put(conn, newSadInterface);
				return Status.OK_STATUS;
			}
		};
		job.setRule(this.mapRule);
		job.schedule();
	}

	@SuppressWarnings("unchecked")
	private SadComponentInstantiation create(final LocalScaComponent newValue) {
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

		put(newValue, retVal);
		map.put(ComponentPlacementEditHelperAdvice.CONFIGURE_OPTIONS_SPD_URI, spdURI);
		map.put(ComponentPlacementEditHelperAdvice.CONFIGURE_OPTIONS_INST_ID, newValue.getInstantiationIdentifier());
		map.put(ComponentPlacementEditHelperAdvice.CONFIGURE_OPTIONS_INST_NAME, newValue.getName());
		map.put(ComponentPlacementEditHelperAdvice.CONFIGURE_COMPONENT_INSTANTIATION, retVal);
		map.put(ComponentPlacementEditHelperAdvice.CONFIGURE_OPTIONS_IMPL_ID, newValue.getImplementationID());

		createRequest.setExtendedData(map);
		final Command command = getDiagramEditPart().getCommand(createRequest);
		execute(command);

		return retVal;
	}

	private LocalScaComponent create(final SadComponentInstantiation comp, final String implID) throws ExecuteFail {
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
			throw new ExecuteFail(ErrorNumberType.CF_EIO, "Failed to resolve spd.");
		}
		final URI spdURI = spd.eResource().getURI();
		try {
			return this.waveform.launch(comp.getId(), execParams, spdURI, implID, ILaunchManager.RUN_MODE);
		} catch (final CoreException e) {
			ScaDebugUiPlugin.getDefault().getLog().log(e.getStatus());
			throw new ExecuteFail(ErrorNumberType.CF_EFAULT, e.getStatus().getMessage());
		}
	}

	private static final EStructuralFeature[] CONN_INST_PATH = new EStructuralFeature[] { PartitioningPackage.Literals.CONNECT_INTERFACE__USES_PORT,
	    PartitioningPackage.Literals.USES_PORT__COMPONENT_INSTANTIATION_REF, PartitioningPackage.Literals.COMPONENT_INSTANTIATION_REF__INSTANTIATION };

	private ScaConnection create(final SadConnectInterface conn) throws InvalidPort, OccupiedPort {
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
			targetObj = targetComp.getCorbaObj();
		} else if (conn.getProvidesPort() != null) {
			final LocalScaComponent targetComp = get(conn.getProvidesPort().getComponentInstantiationRef().getInstantiation());
			targetComp.fetchPorts(null);
			final ScaPort< ? , ? > targetPort = targetComp.getScaPort(conn.getProvidesPort().getProvidesIdentifier());
			targetObj = targetPort.getCorbaObj();
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
	private SadConnectInterface create(final ScaConnection newValue) {
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
				target = get((LocalScaComponent) c).getInterfaceStub();
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

		final CreateConnectionViewRequest ccr = CreateViewRequestFactory.getCreateConnectionRequest(SadElementTypes.SadConnectInterface_4001,
		    getDiagramEditPart().getDiagramPreferencesHint());
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

	private String createId(final SoftPkg spd) {
		return SoftwareAssembly.Util.createComponentIdentifier(this.sad, spd.getName());
	}

	/**
	 * @param oldComp
	 */
	private void delete(final LocalScaComponent oldComp) {
		try {
			if (!oldComp.isDisposed()) {
				oldComp.releaseObject();
			}
		} catch (final ReleaseError e) {
			// PASS
		}
	}

	private void delete(final SadComponentInstantiation oldValue) {
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

	private void delete(final SadConnectInterface connection) {
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

		if (connection != null && editPart != null) {
			final DestroyElementRequest request = new DestroyElementRequest(getEditingDomain(), false);
			request.setElementToDestroy(connection);
			request.getParameters().clear();
			final Command command = editPart.getCommand(new EditCommandRequestWrapper(request));
			execute(command);
		}
	}

	/**
	 * @param oldConnection
	 */
	private void delete(final ScaConnection oldConnection) {
		try {
			if (oldConnection.getPort() != null && !oldConnection.getPort().isDisposed()) {
				oldConnection.getPort().disconnectPort(oldConnection);
			}
		} catch (final InvalidPort e) {
			// PASS
		}
	}

	private void execute(final Command command) {
		if (command != null && command.canExecute()) {
			getDiagramEditDomain().getDiagramCommandStack().execute(command);
		}
	}

	private EditPart findEditPart(final EObject obj) {
		return this.editor.getDiagramEditor().getDiagramEditPart().findEditPart(null, obj);
	}

	public SadComponentInstantiation get(final LocalScaComponent comp) {
		return (SadComponentInstantiation) this.scaToSad.get(comp);
	}

	public LocalScaComponent get(final SadComponentInstantiation compInst) {
		return (LocalScaComponent) this.sadToSca.get(getKey(compInst));
	}

	private SadComponentInstantiation getKey(final SadComponentInstantiation compInst) {
		// For some reason we are given a different reference object so we need to compare by ID
		if (compInst == null) {
			return null;
		}
		final String instId = compInst.getId();
		if (instId == null) {
			return null;
		}
		for (final EObject e : this.sadToSca.keySet()) {
			if (e instanceof SadComponentInstantiation) {
				final SadComponentInstantiation i = (SadComponentInstantiation) e;
				if (instId.equals(i.getId())) {
					return i;
				}
			}
		}
		return compInst;
	}

	public ScaConnection get(final SadConnectInterface conn) {
		return (ScaConnection) this.sadToSca.get(getKey(conn));
	}

	private SadConnectInterface getKey(final SadConnectInterface conn) {
		// For some reason we are given a different reference object so we need to compare by ID
		if (conn == null) {
			return null;
		}
		final String id = conn.getId();
		if (id == null) {
			return null;
		}
		for (final EObject e : this.sadToSca.keySet()) {
			if (e instanceof SadConnectInterface) {
				final SadConnectInterface i = (SadConnectInterface) e;
				if (id.equals(i.getId())) {
					return i;
				}
			}
		}
		return conn;
	}

	public SadConnectInterface get(final ScaConnection conn) {
		return (SadConnectInterface) this.scaToSad.get(conn);
	}

	private IDiagramEditDomain getDiagramEditDomain() {
		return this.editor.getDiagramEditDomain();
	}

	private DiagramEditPart getDiagramEditPart() {
		return this.editor.getDiagramEditPart();
	}

	private TransactionalEditingDomain getEditingDomain() {
		return this.editor.getDiagramEditor().getEditingDomain();
	}

	public void put(final LocalScaComponent comp, final SadComponentInstantiation inst) {
		if (comp == null || inst == null) {
			return;
		}
		Job.getJobManager().beginRule(this.mapRule, null);
		try {
			this.scaToSad.put(comp, inst);
			this.sadToSca.put(inst, comp);
		} finally {
			Job.getJobManager().endRule(this.mapRule);
		}
	}

	public void put(final ScaConnection scaConnection, final SadConnectInterface conn) {
		if (scaConnection == null || conn == null) {
			return;
		}
		Job.getJobManager().beginRule(this.mapRule, null);
		try {
			this.scaToSad.put(scaConnection, conn);
			this.sadToSca.put(conn, scaConnection);
		} finally {
			Job.getJobManager().endRule(this.mapRule);
		}
	}

	public void remove(final LocalScaComponent comp) {
		if (get(comp) == null) {
			return;
		}
		final Job job = new Job("Removing " + comp.getInstantiationIdentifier()) {

			@Override
			public boolean shouldSchedule() {
				return super.shouldSchedule() && get(comp) != null;
			}

			@Override
			public boolean shouldRun() {
				return super.shouldRun() && get(comp) != null;
			}

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				final SadComponentInstantiation oldComp = get(comp);
				if (oldComp == null) {
					return Status.CANCEL_STATUS;
				}
				delete(oldComp);
				removeMap(comp);
				return Status.OK_STATUS;
			}
		};
		job.setRule(this.mapRule);
		job.schedule();
	}

	public void remove(final SadComponentInstantiation comp) {
		if (get(comp) == null) {
			return;
		}
		final Job job = new Job("Releasing " + comp.getUsageName()) {

			@Override
			public boolean shouldSchedule() {
				return super.shouldSchedule() && get(comp) != null;
			}

			@Override
			public boolean shouldRun() {
				return super.shouldRun() && get(comp) != null;
			}

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				final LocalScaComponent oldComp = get(comp);
				if (oldComp == null) {
					return Status.CANCEL_STATUS;
				}
				delete(oldComp);
				removeMap(comp);
				return Status.OK_STATUS;
			}
		};
		job.setRule(this.mapRule);
		job.schedule();
	}

	public void remove(final SadConnectInterface conn) {
		if (get(conn) == null) {
			return;
		}
		final Job job = new Job("Disconnect connection " + conn.getId()) {

			@Override
			public boolean shouldSchedule() {
				return super.shouldSchedule() && get(conn) != null;
			}

			@Override
			public boolean shouldRun() {
				return super.shouldRun() && get(conn) != null;
			}

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				final ScaConnection oldConnection = get(conn);
				if (oldConnection == null) {
					return Status.CANCEL_STATUS;
				}
				delete(oldConnection);
				removeMap(conn);
				return Status.OK_STATUS;
			}
		};
		job.setRule(this.mapRule);
		job.schedule();
	}

	public void remove(final ScaConnection conn) {
		if (get(conn) == null) {
			return;
		}
		final Job job = new Job("Removing connection " + conn.getId()) {

			@Override
			public boolean shouldSchedule() {
				return super.shouldSchedule() && get(conn) != null;
			}

			@Override
			public boolean shouldRun() {
				return super.shouldRun() && get(conn) != null;
			}

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				synchronized (ModelMap.this) {
					final SadConnectInterface oldSadInterface = get(conn);
					if (oldSadInterface == null) {
						return Status.CANCEL_STATUS;
					}
					delete(oldSadInterface);
					removeMap(conn);
				}
				return Status.OK_STATUS;
			}
		};
		job.setRule(this.mapRule);
		job.schedule();
	}

	private void removeMap(final EObject obj) {
		EObject value = this.scaToSad.remove(obj);
		if (value != null) {
			this.sadToSca.remove(value);
		} else {
			value = this.sadToSca.remove(obj);
			if (value != null) {
				this.scaToSad.remove(value);
			}
		}
	}

	private void removeMap(final SadComponentInstantiation comp) {
		removeMap((EObject) getKey(comp));
	}

	private void removeMap(final SadConnectInterface conn) {
		removeMap((EObject) getKey(conn));
	}

}
