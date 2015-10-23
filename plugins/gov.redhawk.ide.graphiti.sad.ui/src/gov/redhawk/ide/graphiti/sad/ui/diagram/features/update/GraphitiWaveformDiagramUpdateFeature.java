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
package gov.redhawk.ide.graphiti.sad.ui.diagram.features.update;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.context.impl.RemoveContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;

import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.AbstractUsesDevicePattern;
import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.ComponentPattern;
import gov.redhawk.ide.graphiti.ui.diagram.features.layout.LayoutDiagramFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.update.AbstractDiagramUpdateFeature;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.partitioning.ConnectInterface;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesDeviceStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentPlacement;
import mil.jpeojtrs.sca.sad.SadConnectInterface;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.UsesDevice;

public class GraphitiWaveformDiagramUpdateFeature extends AbstractDiagramUpdateFeature {

	public GraphitiWaveformDiagramUpdateFeature(IFeatureProvider fp) {
		super(fp);
	}

	protected boolean doesBusinessObjectExist(PictogramElement pe) {
		EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
		if (bo.eIsProxy()) {
			// If it's still a proxy it did not resolve, most likely because the object is gone
			return false;
		} else if (bo instanceof UsesDeviceStub) {
			UsesDeviceStub stub = (UsesDeviceStub) bo;
			if (stub.getUsesDevice() == null || stub.getUsesDevice().eIsProxy()) {
				return false;
			}
		}
		return true;
	}

	protected boolean hasExistingShape(EObject eObject) {
		return !Graphiti.getLinkService().getPictogramElements(getDiagram(), eObject).isEmpty();
	}

	protected boolean hasParentChanged(Shape shape) {
		EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(shape);
		// Get the parent business object two ways--from the EObject's container, and from the parent shape's business
		// object. If they aren't the same, presumably it's been moved.
		return bo.eContainer() != Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(shape.getContainer());
	}

	protected boolean haveEndpointsChanged(Connection connection) {
		SadConnectInterface connectInterface = DUtil.getBusinessObject(connection, SadConnectInterface.class);
		Anchor source = DUtil.getPictogramElementForBusinessObject(getDiagram(), connectInterface.getSource(), Anchor.class);
		Anchor target = DUtil.getPictogramElementForBusinessObject(getDiagram(), connectInterface.getTarget(), Anchor.class);
		if (source == null || target == null) {
			return true;
		}
		return !connection.getAnchors().contains(source) || !connection.getAnchors().contains(target);
	}

	protected List<Shape> getShapesToRemove() {
		List<Shape> removedShapes = new ArrayList<Shape>();
		for (Shape shape : getDiagram().getChildren()) {
			// Check if the linked business object still exists
			if (!doesBusinessObjectExist(shape)) {
				removedShapes.add(shape);
			} else if (hasParentChanged(shape)){
				// Parent has changed (e.g., component moved into a host collocation); delete the existing one and
				// re-add it later
				removedShapes.add(shape);
			}
		}
		return removedShapes;
	}

	protected List<EObject> getObjectsToAdd() {
		SoftwareAssembly sad = DUtil.getDiagramSAD(getDiagram());
		// Find any component instantiations that are in the SAD but do not have an associated shape
		List<EObject> addedChildren = new ArrayList<EObject>();
		for (SadComponentPlacement placement : sad.getPartitioning().getComponentPlacement()) {
			for (SadComponentInstantiation instantiation : placement.getComponentInstantiation()) {
				if (!hasExistingShape(instantiation)) {
					addedChildren.add(instantiation);
				}
			}
		}
		// Likewise, check for host collocations that do not have an associated shape
		for (HostCollocation collocation : sad.getPartitioning().getHostCollocation()) {
			if (!hasExistingShape(collocation)) {
				addedChildren.add(collocation);
			}
		}
		return addedChildren;
	}

	protected List<Connection> getConnectionsToRemove() {
		List<Connection> removedConnections = new ArrayList<Connection>();
		for (Connection connection : getDiagram().getConnections()) {
			if (!doesBusinessObjectExist(connection)) {
				removedConnections.add(connection);
			} else if (haveEndpointsChanged(connection)) {
				removedConnections.add(connection);
			}
		}
		return removedConnections;
	}

	/**
	 * Updates the Diagram to reflect the underlying business model
	 * Make sure all elements in sad model (hosts/components/findby) are accounted for as
	 * children of diagram, if they aren't then add them, if they are then check to see if
	 * they need to be updated, if they exist in the diagram yet not in the model, remove them
	 * @param context
	 * @param performUpdate
	 * @return
	 * @throws CoreException
	 */
	public Reason internalUpdate(IUpdateContext context, boolean performUpdate) throws CoreException {

		boolean updateStatus = false;
		boolean layoutNeeded = false;

		PictogramElement pe = context.getPictogramElement();
		if (pe instanceof Diagram) {
			Diagram diagram = (Diagram) pe;

			// Remove children
			List<Shape> removedChildren = getShapesToRemove();
			if (!removedChildren.isEmpty()) {
				if (!performUpdate) {
					return new Reason(true, "Existing shapes need to be removed");
				} else {
					for (Shape shape : removedChildren) {
						DUtil.fastDeletePictogramElement(shape);
					}
					updateStatus = true;
				}
			}

			// Add shapes for SAD objects that do not have them
			List<EObject> addedChildren = getObjectsToAdd();
			if (!addedChildren.isEmpty()) {
				if (!performUpdate) {
					return new Reason(true, "Missing component or host collocation shapes");
				} else {
					for (EObject object : addedChildren) {
						DUtil.addShapeViaFeature(getFeatureProvider(), diagram, object);
					}
					updateStatus = true;
					layoutNeeded = true;
				}
			}

			// Remove stale connections
			List<Connection> removedConnections = getConnectionsToRemove();
			if (!removedConnections.isEmpty()) {
				if (!performUpdate) {
					return new Reason(true, "Need to remove " + removedConnections.size() + " connection(s)");
				} else {
					for (Connection connection : removedConnections) {
						DUtil.fastDeleteConnection(connection);
					}
					updateStatus = true;
				}
			}

			// get sad from diagram
			SoftwareAssembly sad = DUtil.getDiagramSAD(getDiagram());

			// Add missing connections
			for (SadConnectInterface connectInterface : sad.getConnections().getConnectInterface()) {
				if (!hasExistingShape(connectInterface)) {
					if (!performUpdate) {
						return new Reason(true, "Need to add connection '" + connectInterface.getId() + "'");
					} else {
						Anchor source = DUtil.lookupSourceAnchor(connectInterface, diagram);
						if (source == null) {
							source = addSourceAnchor(connectInterface, diagram);
						}
						Anchor target = DUtil.getPictogramElementForBusinessObject(diagram, connectInterface.getTarget(), Anchor.class);
						if (target == null) {
							target = addTargetAnchor(connectInterface, diagram, getFeatureProvider());
						}
						if (source != null && target != null) {
							DUtil.addConnectionViaFeature(getFeatureProvider(), connectInterface, source, target);
						}
						updateStatus = true;
					}
				}
			}

			// TODO: ensure our SAD has an assembly controller
			// set one if necessary, why bother the user?

			// model UsesDevice
			List<UsesDevice> usesDevices = new ArrayList<UsesDevice>();
			if (sad != null && sad.getUsesDeviceDependencies() != null && sad.getUsesDeviceDependencies().getUsesdevice() != null) {
				// Get list of UsesDeviceStub from model
				Collections.addAll(usesDevices, sad.getUsesDeviceDependencies().getUsesdevice().toArray(new UsesDevice[0]));
			}
			// shape UsesDeviceStub
			List<RHContainerShape> usesDeviceStubShapes = AbstractUsesDevicePattern.getAllUsesDeviceStubShapes(diagram);
			for (Iterator<RHContainerShape> iter = usesDeviceStubShapes.iterator(); iter.hasNext();) {
				if (!(iter.next().eContainer() instanceof Diagram)) {
					iter.remove();
				}
			}

			// If inconsistencies are found remove all objects of that type and redraw
			// we must do this because the diagram uses indexed lists to refer to components in the sad file.
			if (performUpdate) {
				updateStatus = true;

				List<PictogramElement> pesToRemove = new ArrayList<PictogramElement>(); // gather all shapes to remove
				List<Object> objsToAdd = new ArrayList<Object>(); // gather all model object to add

				// If inconsistencies found, redraw diagram elements based on model objects
				if (usesDeviceStubShapes.size() != usesDevices.size() || !usesDeviceStubsResolved(usesDeviceStubShapes)) {
					Collections.addAll(pesToRemove, usesDeviceStubShapes.toArray(new PictogramElement[0]));
					List<UsesDeviceStub> usesDeviceStubsToAdd = new ArrayList<UsesDeviceStub>();
					for (UsesDevice usesDevice : usesDevices) {
						usesDeviceStubsToAdd.add(AbstractUsesDevicePattern.createUsesDeviceStub(usesDevice));
					}
					// add ports to model
					AbstractUsesDevicePattern.addUsesDeviceStubPorts(sad.getConnections().getConnectInterface(), usesDeviceStubsToAdd);
					// VERY IMPORTANT, store copy in diagram file
					getDiagram().eResource().getContents().addAll(usesDeviceStubsToAdd);
					Collections.addAll(objsToAdd, usesDeviceStubsToAdd.toArray(new Object[0]));
					layoutNeeded = true;
				}

				if (!pesToRemove.isEmpty()) {
					// remove shapes from diagram
					for (PictogramElement peToRemove : pesToRemove) {
						// remove shape
						RemoveContext rc = new RemoveContext(peToRemove);
						IRemoveFeature removeFeature = getFeatureProvider().getRemoveFeature(rc);
						if (removeFeature != null) {
							removeFeature.remove(rc);
						}
					}
				} else {
					// update components
					super.update(context);
				}

				// add shapes to diagram
				if (!objsToAdd.isEmpty()) {
					for (Object objToAdd : objsToAdd) {
						DUtil.addShapeViaFeature(getFeatureProvider(), getDiagram(), objToAdd);
					}
				}

				if (layoutNeeded) {
					LayoutDiagramFeature layoutFeature = new LayoutDiagramFeature(getFeatureProvider());
					layoutFeature.execute(null);
				}
			}

			// Ensure assembly controller is set in case a component was deleted that used to be the assembly controller
			ComponentPattern.organizeStartOrder(sad, getDiagram(), getFeatureProvider());
		}

		if (updateStatus && performUpdate) {
			return new Reason(true, "Update successful");
		}

		return new Reason(false, "No updates required");
	}

	/** Checks if rhContainerShape has lost its reference to the UsesDeviceStub model object */
	private boolean usesDeviceStubsResolved(List<RHContainerShape> usesDeviceStubShapes) {
		for (RHContainerShape usesDeviceStubShape : usesDeviceStubShapes) {
			Object obj = DUtil.getBusinessObject(usesDeviceStubShape, UsesDeviceStub.class);
			if (obj == null) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Reason updateNeeded(IUpdateContext context) {
		try {
			return internalUpdate(context, false);
		} catch (CoreException e) {
			// PASS
			// TODO: catch exception
		}
		return null;
	}

	@Override
	public boolean update(IUpdateContext context) {
		Reason reason;
		try {
			reason = internalUpdate(context, true);
			return reason.toBoolean();
		} catch (CoreException e) {
			// PASS
			// TODO: catch exception
			e.printStackTrace(); // SUPPRESS CHECKSTYLE INLINE
		}

		return false;
	}

	@Override
	protected < E extends ConnectInterface< ? , ? , ? > > Anchor addSourceAnchor(E connectInterface, Diagram diagram) throws CoreException {
		if (connectInterface.getUsesPort() != null && connectInterface.getUsesPort().getDeviceUsedByApplication() != null) {
			UsesDeviceStub usesDeviceStub = AbstractUsesDevicePattern.findUsesDeviceStub(connectInterface.getUsesPort().getDeviceUsedByApplication(), diagram);

			// determine which usesPortStub we are targeting
			UsesPortStub usesPortStub = null;
			for (UsesPortStub p : usesDeviceStub.getUsesPortStubs()) {
				if (p != null && connectInterface.getUsesPort().getUsesIdentifier() != null
					&& p.getName().equals(connectInterface.getUsesPort().getUsesIdentifier())) {
					usesPortStub = p;
				}
			}

			// determine port anchor for usesDeviceStub
			if (usesPortStub != null) {
				PictogramElement pe = DUtil.getPictogramElementForBusinessObject(diagram, usesPortStub, Anchor.class);
				return (Anchor) pe;
			}
			return null;
		}
		return super.addSourceAnchor(connectInterface, diagram);
	}

	@Override
	protected < E extends ConnectInterface< ? , ? , ? > > Anchor addTargetAnchor(E connectInterface, Diagram diagram, IFeatureProvider featureProvider)
		throws CoreException {
		if (connectInterface.getProvidesPort() != null && connectInterface.getProvidesPort().getDeviceUsedByApplication() != null) {
			UsesDeviceStub usesDeviceStub = AbstractUsesDevicePattern.findUsesDeviceStub(connectInterface.getProvidesPort().getDeviceUsedByApplication(),
				diagram);

			// determine which providesPortStub we are targeting
			ProvidesPortStub providesPortStub = null;
			for (ProvidesPortStub p : usesDeviceStub.getProvidesPortStubs()) {
				if (p != null && connectInterface.getProvidesPort().getProvidesIdentifier() != null
					&& p.getName().equals(connectInterface.getProvidesPort().getProvidesIdentifier())) {
					providesPortStub = p;
				}
			}

			// determine port anchor for usesDeviceStub
			if (providesPortStub != null) {
				PictogramElement pe = DUtil.getPictogramElementForBusinessObject(diagram, providesPortStub, Anchor.class);
				return (Anchor) pe;
			}
		} else
			if (connectInterface.getComponentSupportedInterface() != null && connectInterface.getComponentSupportedInterface().getSupportedIdentifier() != null
				&& connectInterface.getComponentSupportedInterface().getDeviceUsedByApplication() != null) {

			UsesDeviceStub usesDeviceStub = AbstractUsesDevicePattern.findUsesDeviceStub(
				connectInterface.getComponentSupportedInterface().getDeviceUsedByApplication(), diagram);

			// determine port anchor for UsesDevice
			if (usesDeviceStub.getInterface() != null) {
				PictogramElement pe = DUtil.getPictogramElementForBusinessObject(diagram, usesDeviceStub.getInterface(), Anchor.class);
				return (Anchor) pe;
			}
		} else {
			return super.addTargetAnchor(connectInterface, diagram, featureProvider);
		}
		return null;
	}

}
