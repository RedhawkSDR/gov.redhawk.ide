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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.CreateContext;

import gov.redhawk.core.graphiti.ui.editor.AbstractGraphitiMultiPageEditor;
import gov.redhawk.ide.graphiti.dcd.ui.DCDUIGraphitiPlugin;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.features.create.DeviceCreateFeature;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.features.create.ServiceCreateFeature;
import gov.redhawk.ide.graphiti.internal.ui.AbstractGraphitiModelMap;
import gov.redhawk.ide.graphiti.internal.ui.CreateComponentInstantiationCommand;
import gov.redhawk.model.sca.CorbaObjWrapper;
import gov.redhawk.model.sca.ScaConnection;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.ScaDeviceManager;
import gov.redhawk.model.sca.ScaPackage;
import gov.redhawk.model.sca.ScaPort;
import gov.redhawk.model.sca.ScaPortContainer;
import gov.redhawk.model.sca.ScaPropertyContainer;
import gov.redhawk.model.sca.ScaProvidesPort;
import gov.redhawk.model.sca.ScaService;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.model.sca.util.ReleaseJob;
import gov.redhawk.sca.util.SubMonitor;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;
import mil.jpeojtrs.sca.partitioning.ConnectionTarget;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.spd.SoftPkg;

public class GraphitiDcdModelMap extends AbstractGraphitiModelMap {

	private final ScaDeviceManager deviceManager;

	/**
	 * Associates {@link ScaDevice}s and {@link ScaService} with their {@link DcdComponentInstantiation}.
	 */
	private final Map<String, DcdNodeMapEntry> nodes = Collections.synchronizedMap(new HashMap<String, DcdNodeMapEntry>());

	public GraphitiDcdModelMap(final AbstractGraphitiMultiPageEditor editor, final ScaDeviceManager deviceManager) {
		super(editor);
		Assert.isNotNull(deviceManager, "Device Manager must not be null");
		this.deviceManager = deviceManager;
	}

	protected ScaDeviceManager getDeviceManager() {
		return deviceManager;
	}

	protected Map<String, DcdNodeMapEntry> getNodes() {
		return nodes;
	}

	////////////////////////////////////////////////////
	// Devices/services section
	////////////////////////////////////////////////////

	/**
	 * Called when a new {@link ScaDevice} is added to the SCA model. Asynchronously updates the diagram.
	 * @param device
	 */
	public void add(final ScaDevice< ? > device) {
		final DcdNodeMapEntry nodeMapEntry = new DcdNodeMapEntry();
		nodeMapEntry.setScaDevice(device);
		synchronized (nodes) {
			if (nodes.get(nodeMapEntry.getKey()) != null) {
				return;
			} else {
				nodes.put(nodeMapEntry.getKey(), nodeMapEntry);
			}
		}
		Job job = new Job("Adding device: " + device.getIdentifier()) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				SubMonitor subMonitor = SubMonitor.convert(monitor, 1);
				DcdComponentInstantiation newDevice = null;
				try {
					newDevice = (DcdComponentInstantiation) GraphitiDcdModelMap.this.create(device, subMonitor.newChild(1));
					nodeMapEntry.setProfile(newDevice);
					return Status.OK_STATUS;
				} catch (CoreException e) {
					nodes.remove(nodeMapEntry.getKey());
					return new Status(IStatus.ERROR, DCDUIGraphitiPlugin.PLUGIN_ID, "Failed to add device " + device.getIdentifier(), e);
				} finally {
					if (nodes.get(nodeMapEntry.getKey()) == null) {
						delete(newDevice);
					}
					subMonitor.done();
				}
			}

		};
		job.schedule();
	}

	/**
	 * Called when a new {@link ScaService} is added to the SCA model. Asynchronously updates the diagram.
	 * @param service
	 */
	public void add(final ScaService service) {
		final DcdNodeMapEntry nodeMapEntry = new DcdNodeMapEntry();
		nodeMapEntry.setScaService(service);
		synchronized (nodes) {
			if (nodes.get(nodeMapEntry.getKey()) != null) {
				return;
			} else {
				nodes.put(nodeMapEntry.getKey(), nodeMapEntry);
			}
		}
		Job job = new Job("Adding service: " + service.getName()) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				SubMonitor subMonitor = SubMonitor.convert(monitor, 1);
				DcdComponentInstantiation newService = null;
				try {
					newService = (DcdComponentInstantiation) GraphitiDcdModelMap.this.create(service, subMonitor.newChild(1));
					nodeMapEntry.setProfile(newService);
					return Status.OK_STATUS;
				} catch (CoreException e) {
					nodes.remove(nodeMapEntry.getKey());
					return new Status(IStatus.ERROR, DCDUIGraphitiPlugin.PLUGIN_ID, "Failed to add service " + service.getName(), e);
				} finally {
					if (nodes.get(nodeMapEntry.getKey()) == null) {
						delete(newService);
					}
					subMonitor.done();
				}
			}

		};
		job.schedule();
	}

	/**
	 * Called when a new {@link DcdComponentInstantiation} is added to the diagram's XML model. Asynchronously launches
	 * the device or service.
	 * @param comp
	 */
	public void add(final DcdComponentInstantiation compInst) {
		// Launching not supported by this class
		throw new IllegalStateException("Launching devices/services is not permitted by this class");
	}

	protected CreateComponentInstantiationCommand createComponentInstantiationCommand(TransactionalEditingDomain editingDomain,
		final IFeatureProvider featureProvider, ScaPropertyContainer< ? , SoftPkg> newObject) {
		switch (newObject.eClass().getClassifierID()) {
		case ScaPackage.SCA_DEVICE:
			final ScaDevice< ? > newDevice = (ScaDevice< ? >) newObject;
			return new CreateComponentInstantiationCommand(editingDomain) {

				private ComponentInstantiation compInst;

				@Override
				public ComponentInstantiation getComponentInstantiation() {
					return compInst;
				}

				@Override
				protected void doExecute() {
					DeviceCreateFeature createComponentFeature = new DeviceCreateFeature(featureProvider, newDevice.getProfileObj(), null);
					CreateContext createContext = new CreateContext();
					createContext.putProperty(DeviceCreateFeature.OVERRIDE_USAGE_NAME, newDevice.getLabel());
					createContext.putProperty(DeviceCreateFeature.OVERRIDE_INSTANTIATION_ID, newDevice.getIdentifier());
					createContext.setTargetContainer(featureProvider.getDiagramTypeProvider().getDiagram());
					final Object[] objects = createComponentFeature.create(createContext);
					compInst = (ComponentInstantiation) objects[0];
				}
			};
		case ScaPackage.SCA_SERVICE:
			final ScaService newService = (ScaService) newObject;
			return new CreateComponentInstantiationCommand(editingDomain) {

				private ComponentInstantiation compInst;

				@Override
				public ComponentInstantiation getComponentInstantiation() {
					return compInst;
				}

				@Override
				protected void doExecute() {
					ServiceCreateFeature createComponentFeature = new ServiceCreateFeature(featureProvider, newService.getProfileObj(), null);
					CreateContext createContext = new CreateContext();
					createContext.putProperty(ServiceCreateFeature.OVERRIDE_USAGE_NAME, newService.getName());
					createContext.setTargetContainer(featureProvider.getDiagramTypeProvider().getDiagram());
					final Object[] objects = createComponentFeature.create(createContext);
					compInst = (ComponentInstantiation) objects[0];
				}
			};
		default:
			return null;
		}
	}

	protected UsesPortStub findSource(ScaConnection newValue) {
		ScaPortContainer portContainer = (ScaPortContainer) newValue.getPort().eContainer();
		final DcdComponentInstantiation sourceComponent = getComponentInstantiation(portContainer);
		if (sourceComponent == null) {
			return null;
		}
		for (final UsesPortStub stub : sourceComponent.getUses()) {
			if (stub.getName() != null && stub.getName().equals(newValue.getPort().getName())) {
				return stub;
			}
		}
		return null;
	}

	protected ConnectionTarget findTarget(ScaConnection newValue) {
		final List<ScaPortContainer> portContainers = new ArrayList<>();
		final List<CorbaObjWrapper< ? >> componentSupportedInterfaceTargets = new ArrayList<>();
		try {
			ScaModelCommand.runExclusive(this.deviceManager, new RunnableWithResult.Impl<Object>() {

				@Override
				public void run() {
					portContainers.addAll(GraphitiDcdModelMap.this.deviceManager.getAllDevices());
					portContainers.addAll(GraphitiDcdModelMap.this.deviceManager.getServices());
					componentSupportedInterfaceTargets.addAll(GraphitiDcdModelMap.this.deviceManager.getAllDevices());
					componentSupportedInterfaceTargets.addAll(GraphitiDcdModelMap.this.deviceManager.getServices());
				}
			});
		} catch (InterruptedException e) {
			return null;
		}

		// Iterate port containers looking for a provides ports which may match
		for (final ScaPortContainer portContainer : portContainers) {
			if (!portContainer.isSetPorts()) {
				portContainer.fetchPorts(null);
			}
			for (final ScaPort< ? , ? > port : portContainer.getPorts()) {
				if (port instanceof ScaProvidesPort && port.getObj()._is_equivalent(newValue.getData().port)) {
					final DcdComponentInstantiation compInst = getComponentInstantiation(portContainer);
					if (compInst == null) {
						continue;
					}
					for (final ProvidesPortStub provides : compInst.getProvides()) {
						if (provides.getName().equals(port.getName())) {
							return provides;
						}
					}
				}
			}
		}

		// Iterate anything that could be a component supported interface looking for a match
		for (final CorbaObjWrapper< ? > csiTarget : componentSupportedInterfaceTargets) {
			if (csiTarget.getObj()._is_equivalent(newValue.getData().port)) {
				DcdComponentInstantiation compInst = getComponentInstantiation(csiTarget);
				if (compInst != null) {
					return compInst.getInterfaceStub();
				}
				break;
			}
		}

		return null;
	}

	/* package */ DcdComponentInstantiation getComponentInstantiation(final EObject obj) {
		if (obj == null || obj.eClass().getEPackage() != ScaPackage.eINSTANCE) {
			return null;
		}
		DcdNodeMapEntry nodeMapEntry;
		switch (obj.eClass().getClassifierID()) {
		case ScaPackage.SCA_DEVICE:
			nodeMapEntry = nodes.get(DcdNodeMapEntry.getKey((ScaDevice< ? >) obj));
			break;
		case ScaPackage.SCA_SERVICE:
			nodeMapEntry = nodes.get(DcdNodeMapEntry.getKey((ScaService) obj));
			break;
		default:
			nodeMapEntry = null;
		}
		return (nodeMapEntry != null) ? nodeMapEntry.getProfile() : null;
	}

	protected CorbaObjWrapper< ? > getCorbaObjWrapper(ComponentInstantiation compInst) {
		if (compInst == null) {
			return null;
		}
		DcdNodeMapEntry nodeMapEntry = nodes.get(DcdNodeMapEntry.getKey(compInst));
		if (nodeMapEntry == null) {
			return null;
		}
		return (nodeMapEntry.getScaDevice() != null) ? nodeMapEntry.getScaDevice() : nodeMapEntry.getScaService();
	}

	protected ScaPortContainer getPortContainer(ComponentInstantiation portContainer) {
		if (portContainer == null) {
			return null;
		}
		DcdNodeMapEntry nodeMapEntry = nodes.get(DcdNodeMapEntry.getKey(portContainer));
		if (nodeMapEntry == null) {
			return null;
		}
		return (nodeMapEntry.getScaDevice() != null) ? nodeMapEntry.getScaDevice() : nodeMapEntry.getScaService();
	}

	/**
	 * Adds a mapping between the SCA model object and diagram XML model object
	 * @param device
	 * @param inst
	 */
	public void put(ScaDevice< ? > device, DcdComponentInstantiation inst) {
		DcdNodeMapEntry nodeMapEntry = new DcdNodeMapEntry(device, inst);
		nodes.put(nodeMapEntry.getKey(), nodeMapEntry);
	}

	/**
	 * Adds a mapping between the SCA model object and diagram XML model object
	 * @param device
	 * @param inst
	 */
	public void put(ScaService service, DcdComponentInstantiation inst) {
		DcdNodeMapEntry nodeMapEntry = new DcdNodeMapEntry(service, inst);
		nodes.put(nodeMapEntry.getKey(), nodeMapEntry);
	}

	/**
	 * Called when an existing {@link ScaDevice} is removed from the SCA model. Removes the PictogramElement from the
	 * diagram.
	 * @param device
	 */
	public void remove(final ScaDevice< ? > device) {
		final DcdNodeMapEntry nodeMapEntry = nodes.remove(DcdNodeMapEntry.getKey(device));
		removePictogramElement(nodeMapEntry);
	}

	/**
	 * Called when an existing {@link ScaService} is removed from the SCA model. Removes the PictogramElement from the
	 * diagram.
	 * @param device
	 */
	public void remove(final ScaService service) {
		final DcdNodeMapEntry nodeMapEntry = nodes.remove(DcdNodeMapEntry.getKey(service));
		removePictogramElement(nodeMapEntry);
	}

	private void removePictogramElement(DcdNodeMapEntry nodeMapEntry) {
		if (nodeMapEntry == null) {
			return;
		}

		final DcdComponentInstantiation oldDevice = nodeMapEntry.getProfile();
		if (oldDevice != null) {
			delete(oldDevice);
		}
	}

	/**
	 * Called when a {@link SadComponentInstantiation} is removed from the diagram's XML model. Asynchronously triggers
	 * a call to <code>releaseObject()</code> for devices.
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

	////////////////////////////////////////////////////
	// Reflecting status
	////////////////////////////////////////////////////

	/**
	 * Updates the pictogram element's start/stop state.
	 * @param scaDevice
	 * @param started
	 */
	public void startStopDevice(ScaDevice< ? > scaDevice, final Boolean started) {
		final boolean resolveStarted = (started == null) ? false : started;
		final DcdNodeMapEntry nodeMapEntry = nodes.get(DcdNodeMapEntry.getKey(scaDevice));
		if (nodeMapEntry == null) {
			return;
		}
		final ComponentInstantiation componentInstantiation = nodeMapEntry.getProfile();
		updateStateStopState(componentInstantiation, resolveStarted);
	}

	/**
	 * Updates pictogram elements to reflect runtime status.
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
	 * Updates the pictogram element's {@link IStatus}.
	 * @param device
	 * @param status
	 */
	public void reflectErrorState(ScaDevice< ? > device, final IStatus status) {
		final DcdNodeMapEntry nodeMapEntry = nodes.get(DcdNodeMapEntry.getKey(device));
		if (nodeMapEntry == null) {
			return;
		}
		final ComponentInstantiation componentInstantiation = nodeMapEntry.getProfile();
		updateErrorState(componentInstantiation, status);
	}
}
