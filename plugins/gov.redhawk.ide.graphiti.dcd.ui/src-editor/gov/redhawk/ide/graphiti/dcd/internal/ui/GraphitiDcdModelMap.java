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

import gov.redhawk.ide.graphiti.dcd.internal.ui.editor.GraphitiDcdSandboxEditor;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.providers.DCDDiagramFeatureProvider;
import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.ScaDeviceManager;
import gov.redhawk.sca.ui.actions.StartAction;
import gov.redhawk.sca.ui.actions.StopAction;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.pattern.DeleteFeatureForPattern;
import org.eclipse.graphiti.pattern.IPattern;

public class GraphitiDcdModelMap {
//	private static final EStructuralFeature[] CONN_INST_PATH = new EStructuralFeature[] { PartitioningPackage.Literals.CONNECT_INTERFACE__USES_PORT,
//		PartitioningPackage.Literals.USES_PORT__COMPONENT_INSTANTIATION_REF, PartitioningPackage.Literals.COMPONENT_INSTANTIATION_REF__INSTANTIATION };
//	private static final EStructuralFeature[] SPD_PATH = new EStructuralFeature[] { PartitioningPackage.Literals.COMPONENT_INSTANTIATION__PLACEMENT,
//		PartitioningPackage.Literals.COMPONENT_PLACEMENT__COMPONENT_FILE_REF, PartitioningPackage.Literals.COMPONENT_FILE_REF__FILE,
//		PartitioningPackage.Literals.COMPONENT_FILE__SOFT_PKG };

	private final GraphitiDcdSandboxEditor editor;
	private final ScaDeviceManager deviceManager;

	// maps containing to uniquely identify component/connections, use with synchronized statement
	private final Map<String, DcdNodeMapEntry> nodes = Collections.synchronizedMap(new HashMap<String, DcdNodeMapEntry>());
//	private final Map<String, ConnectionMapEntry> connections = Collections.synchronizedMap(new HashMap<String, ConnectionMapEntry>());

	// actions for starting/stopping components
	private final StartAction startAction = new StartAction();
	private final StopAction stopAction = new StopAction();

	public GraphitiDcdModelMap(final GraphitiDcdSandboxEditor editor, final DeviceConfiguration dcd, final ScaDeviceManager deviceManager) {
		Assert.isNotNull(deviceManager, "Device Manager must not be null");
		Assert.isNotNull(editor, "Node Explorer editor must not be null");
		Assert.isNotNull(dcd, "Device Configuration must not be null");
		this.deviceManager = deviceManager;
		this.editor = editor;
	}

	/**
	 * Paints the Chalkboard diagram component appropriate color
	 * Fired when a device is started/stopped in the SCA Explorer
	 * @param localScaComponent
	 * @param started
	 */
	public void startStopComponent(ScaDevice< ? > scaDevice, final Boolean started) {
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

		// get pictogram for component
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
							// paint component
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
	 * Starts/Stops the component in the ScaExplorer as a result of the user
	 * stopping/starting the component in the diagram
	 * @param comp
	 * @param started
	 */
	public void startStopComponent(final DcdComponentInstantiation comp, final Boolean started) {
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
	 * Modifies the diagram to reflect component runtime status
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
								// paint component
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
	 * Called when we remove LocalScaComponent from the local waveform.
	 * This method removes SadComponentInstantiation from the diagram
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
	 * Delete DcdComponentInstantiation from diagram
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

		// get pictogram for component
		final PictogramElement[] peToRemove = { DUtil.getPictogramElementForBusinessObject(diagram, dcdComponentInstantiation, RHContainerShapeImpl.class) };

		// Delete Component in transaction
		final TransactionalEditingDomain editingDomain = (TransactionalEditingDomain) editor.getEditingDomain();
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {

				// delete shape & component
				DeleteContext context = new DeleteContext(peToRemove[0]);
				DCDDiagramFeatureProvider fp = (DCDDiagramFeatureProvider) featureProvider;
				IPattern pattern = fp.getPatternForPictogramElement(peToRemove[0]);
				DeleteFeatureForPattern deleteFeature = new DeleteFeatureForPattern(fp, pattern);
				deleteFeature.delete(context);
			}
		});
	}

//	/**
//	 * @param oldComp
//	 * @throws ReleaseError
//	 */
//	private void delete(final LocalScaComponent oldComp) throws ReleaseError {
//		if (oldComp == null) {
//			return;
//		}
//		if (!oldComp.isDisposed()) {
//			oldComp.releaseObject();
//		}
//	}
//
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
//	 * Called when we remove SadComponentInstantiation from the diagram.
//	 * This method removes LocalScaComponent from the local waveform
//	 * @param comp
//	 */
//	public void remove(final SadComponentInstantiation comp) {
//		if (comp == null) {
//			return;
//		}
//
//		final NodeMapEntry nodeMapEntry = nodes.remove(NodeMapEntry.getKey(comp));
//		if (nodeMapEntry == null) {
//			return;
//		}
//		final LocalScaComponent oldComp = nodeMapEntry.getLocalScaComponent();
//		if (oldComp != null) {
//			Job job = new Job("Releasing " + comp.getUsageName()) {
//
//				@Override
//				protected IStatus run(IProgressMonitor monitor) {
//					SubMonitor subMonitor = SubMonitor.convert(monitor, "Releasing " + comp.getUsageName(), IProgressMonitor.UNKNOWN);
//					try {
//						delete(oldComp);
//						return Status.OK_STATUS;
//					} catch (ReleaseError e) {
//						return new Status(IStatus.WARNING, SADUIGraphitiPlugin.PLUGIN_ID, "Problems while removing component " + comp.getId(), e);
//					} finally {
//						subMonitor.done();
//					}
//				}
//
//			};
//			job.schedule();
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
