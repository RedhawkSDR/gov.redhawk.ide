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

import gov.redhawk.ide.debug.LocalAbstractComponent;
import gov.redhawk.ide.debug.impl.LocalScaDeviceManagerImpl;
import gov.redhawk.ide.graphiti.dcd.internal.ui.editor.GraphitiDcdSandboxEditor;
import gov.redhawk.ide.graphiti.dcd.ui.DCDUIGraphitiPlugin;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.features.create.DeviceCreateFeature;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.providers.DCDDiagramFeatureProvider;
import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.ScaDeviceManager;
import gov.redhawk.sca.ui.actions.StartAction;
import gov.redhawk.sca.ui.actions.StopAction;
import gov.redhawk.sca.util.SubMonitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.dcd.impl.DcdComponentInstantiationImpl;
import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.prf.AbstractPropertyRef;
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
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.pattern.DeleteFeatureForPattern;
import org.eclipse.graphiti.pattern.IPattern;
import org.omg.CORBA.SystemException;

import CF.DataType;
import CF.ExecutableDevicePackage.ExecuteFail;
import CF.LifeCyclePackage.ReleaseError;

public class GraphitiDcdModelMap {
//	private static final EStructuralFeature[] CONN_INST_PATH = new EStructuralFeature[] { PartitioningPackage.Literals.CONNECT_INTERFACE__USES_PORT,
//		PartitioningPackage.Literals.USES_PORT__COMPONENT_INSTANTIATION_REF, PartitioningPackage.Literals.COMPONENT_INSTANTIATION_REF__INSTANTIATION };
	private static final EStructuralFeature[] SPD_PATH = new EStructuralFeature[] { PartitioningPackage.Literals.COMPONENT_INSTANTIATION__PLACEMENT,
		PartitioningPackage.Literals.COMPONENT_PLACEMENT__COMPONENT_FILE_REF, PartitioningPackage.Literals.COMPONENT_FILE_REF__FILE,
		PartitioningPackage.Literals.COMPONENT_FILE__SOFT_PKG };

	private final GraphitiDcdSandboxEditor editor;
	private final ScaDeviceManager deviceManager;

	// maps containing to uniquely identify devices/connections, use with synchronized statement
	private final Map<String, DcdNodeMapEntry> nodes = Collections.synchronizedMap(new HashMap<String, DcdNodeMapEntry>());
//	private final Map<String, ConnectionMapEntry> connections = Collections.synchronizedMap(new HashMap<String, ConnectionMapEntry>());

	// actions for starting/stopping devices
	private final StartAction startAction = new StartAction();
	private final StopAction stopAction = new StopAction();

	public GraphitiDcdModelMap(final GraphitiDcdSandboxEditor editor, final DeviceConfiguration dcd, final ScaDeviceManager deviceManager) {
		Assert.isNotNull(deviceManager, "Device Manager must not be null");
		Assert.isNotNull(editor, "Node Explorer editor must not be null");
		Assert.isNotNull(dcd, "Device Configuration must not be null");
		this.deviceManager = deviceManager;
		this.editor = editor;
	}

	/********************** SCA EXPLORER TO DIAGRAM *****************************/
	/*	These actions are fired when interacting with a shape in the SCA		*/
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
				if (nodes.get(nodeMapEntry.getKey()).getScaDevice() == null) {
					nodes.get(nodeMapEntry.getKey()).setScaDevice(device);
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
			}
		});

		return dcdComponentInstantiations[0];
	}

	/**
	 * Paints the Chalkboard diagram device appropriate color
	 * Fired when a device is started/stopped in the SCA Explorer
	 * @param ScaDevice
	 * @param started
	 */
	public void startStopDevice(ScaDevice< ? > scaDevice, final Boolean started) {
		final DcdNodeMapEntry nodeMapEntry = nodes.get(DcdNodeMapEntry.getKey(scaDevice));
		if (nodeMapEntry == null) {
			return;
		}
		final DcdComponentInstantiation dcdComponentInstantiation = nodeMapEntry.getProfile();

		// setup to perform diagram operations
		final IDiagramTypeProvider provider = editor.getDiagramEditor().getDiagramTypeProvider();
		final IFeatureProvider featureProvider = provider.getFeatureProvider();
		final TransactionalEditingDomain editingDomain = featureProvider.getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		final Diagram diagram = provider.getDiagram();

		// get pictogram for device
		final RHContainerShape rhContainerShape = (RHContainerShape) DUtil.getPictogramElementForBusinessObject(diagram, dcdComponentInstantiation,
			RHContainerShapeImpl.class);

		if (rhContainerShape != null) {
			Job job = new Job("Syncronizing diagram start/stop status: " + scaDevice.getIdentifier()) {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					// Perform business object manipulation in a Command
					TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
					stack.execute(new RecordingCommand(editingDomain) {
						@Override
						protected void doExecute() {
							// paint device
							rhContainerShape.setStarted(started);
						}
					});
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}
	}

	/**
	 * Modifies the diagram to reflect runtime status of the SCA Explorer
	 * Fires when the diagram is first launched
	 */
	public void reflectRuntimeStatus() {

		// setup to perform diagram operations
		final IDiagramTypeProvider provider = editor.getDiagramEditor().getDiagramTypeProvider();
		final Diagram diagram = provider.getDiagram();
		final IFeatureProvider featureProvider = provider.getFeatureProvider();
		final TransactionalEditingDomain editingDomain = featureProvider.getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		Job job = new Job("Syncronizing device started status") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				for (String nodeKey : nodes.keySet()) {
					final DcdNodeMapEntry nodeMapEntry = nodes.get(nodeKey);

					// get pictogram for device
					final RHContainerShape shape = (RHContainerShape) DUtil.getPictogramElementForBusinessObject(diagram, nodeMapEntry.getProfile(),
						RHContainerShapeImpl.class);

					final boolean started = nodeMapEntry.getScaDevice().getStarted();
					if (started) {

						// Perform business object manipulation in a Command
						TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
						stack.execute(new RecordingCommand(editingDomain) {
							@Override
							protected void doExecute() {
								// paint device
								if (shape != null) {
									shape.setStarted(true);
								}
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
	 * Called when we release a device in the SCA Explorer
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
	 * Deletes a DcdComponentInstantiation from diagram
	 * @param oldValue
	 */
	private void delete(final DcdComponentInstantiation dcdComponentInstantiation) {
		if (dcdComponentInstantiation == null || editor.isDisposed()) {
			return;
		}
		// setup to perform diagram operations
		final IDiagramTypeProvider provider = editor.getDiagramEditor().getDiagramTypeProvider();
		final IFeatureProvider featureProvider = provider.getFeatureProvider();
		final Diagram diagram = provider.getDiagram();

		// get pictogram for device
		final PictogramElement[] peToRemove = { DUtil.getPictogramElementForBusinessObject(diagram, dcdComponentInstantiation, RHContainerShapeImpl.class) };

		// Delete Device in transaction
		final TransactionalEditingDomain editingDomain = (TransactionalEditingDomain) editor.getEditingDomain();
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		if (peToRemove.length > 0 && peToRemove[0] != null) {
			stack.execute(new RecordingCommand(editingDomain) {
				@Override
				protected void doExecute() {

					// delete shape & device
					DeleteContext context = new DeleteContext(peToRemove[0]);
					DCDDiagramFeatureProvider fp = (DCDDiagramFeatureProvider) featureProvider;
					IPattern pattern = fp.getPatternForPictogramElement(peToRemove[0]);
					DeleteFeatureForPattern deleteFeature = new DeleteFeatureForPattern(fp, pattern);
					deleteFeature.delete(context);
				}
			});
		}
	}

	/********************** DIAGRAM TO SCA EXPLORER *****************************/
	/*	These actions are fired when interacting with a shape in the diagram	*/
	/*	and the results are reflected in the SCA Explorer view					*/
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
					delete(device);
					nodes.remove(nodeMapEntry.getKey());
					return e.getStatus();
				} finally {
					if (nodes.get(nodeMapEntry.getKey()) == null) {
						try {
							delete(device);
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
	 * Starts/Stops the device in the ScaExplorer as a result of the user
	 * stopping/starting the component in the diagram
	 * @param comp
	 * @param started
	 */
	public void startStopDevice(final DcdComponentInstantiation comp, final Boolean started) {
		if (comp == null) {
			return;
		}

		final DcdNodeMapEntry nodeMapEntry = nodes.get(DcdNodeMapEntry.getKey(comp));
		if (nodeMapEntry == null) {
			return;
		}

		final ScaDevice< ? > scaDevice = nodeMapEntry.getScaDevice();

		// start
		if (started) {
			Job job = new Job("Starting " + comp.getUsageName()) {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					startAction.setContext(scaDevice);
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
					stopAction.setContext(scaDevice);
					stopAction.run();
					return Status.OK_STATUS;
				}
			};
			job.schedule();
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
			Job job = new Job("Releasing " + device.getUsageName()) {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					SubMonitor subMonitor = SubMonitor.convert(monitor, "Releasing " + device.getUsageName(), IProgressMonitor.UNKNOWN);
					try {
						delete(oldDevice);
						return Status.OK_STATUS;
					} catch (ReleaseError e) {
						return new Status(IStatus.WARNING, DCDUIGraphitiPlugin.PLUGIN_ID, "Problems while removing component " + device.getId(), e);
					} finally {
						subMonitor.done();
					}
				}

			};
			job.schedule();
		}
	}

	/**
	 * @param oldComp
	 * @throws ReleaseError
	 */
	private void delete(final ScaDevice< ? > oldDevice) throws ReleaseError {
		if (oldDevice == null) {
			return;
		}
		if (!oldDevice.isDisposed()) {
			oldDevice.releaseObject();
		}
	}

	// TODO: Do we need these?
//	@Nullable
//	public SadComponentInstantiation get(@Nullable final LocalScaComponent comp) {
//		if (comp == null) {
//			return null;
//		}
//		NodeMapEntry nodeMapEntry = nodes.get(NodeMapEntry.getKey(comp));
//		if (nodeMapEntry != null) {
//			return nodeMapEntry.getProfile();
//		} else {
//			return null;
//		}
//	}
//
//	@Nullable
//	public LocalScaComponent get(@Nullable final SadComponentInstantiation compInst) {
//		if (compInst == null) {
//			return null;
//		}
//		NodeMapEntry nodeMapEntry = nodes.get(NodeMapEntry.getKey(compInst));
//		if (nodeMapEntry != null) {
//			return nodeMapEntry.getLocalScaComponent();
//		} else {
//			return null;
//		}
//	}

//
//	/**
//	 * @param con
//	 * @param sadCon
//	 */
//	public void put(@NonNull ScaConnection con, @NonNull SadConnectInterface sadCon) {
//		ConnectionMapEntry connectionMap = new ConnectionMapEntry();
//		connectionMap.setScaConnection(con);
//		connectionMap.setProfile(sadCon);
//		connections.put(connectionMap.getKey(), connectionMap);
//	}
//
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
