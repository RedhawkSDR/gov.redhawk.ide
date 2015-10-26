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
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.context.impl.RemoveContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;

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
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.UsesDevice;

public class GraphitiWaveformDiagramUpdateFeature extends AbstractDiagramUpdateFeature {

	public GraphitiWaveformDiagramUpdateFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	protected boolean doesModelObjectExist(EObject object) {
		if (object instanceof UsesDeviceStub) {
			UsesDeviceStub stub = (UsesDeviceStub) object;
			if (stub.getUsesDevice() == null || stub.getUsesDevice().eIsProxy()) {
				return false;
			}
		}
		return super.doesModelObjectExist(object);
	}

	protected boolean hasParentChanged(Shape shape) {
		EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(shape);
		EObject parent = bo.eContainer();
		if (parent instanceof HostCollocation) {
			// Compare the parent business objects, in case it was previously not collocated or moved from another
			return parent != Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(shape.getContainer());
		} else {
			// Otherwise, it should be a direct child of the diagram
			return !(shape.getContainer() instanceof Diagram);
		}
	}

	protected List<Shape> getShapesToRemove(Diagram diagram) {
		List<Shape> removedShapes = new ArrayList<Shape>();
		for (Shape shape : diagram.getChildren()) {
			// Check if the linked business object still exists
			if (!doesLinkedBusinessObjectExist(shape)) {
				removedShapes.add(shape);
			} else if (hasParentChanged(shape)) {
				// Parent has changed (e.g., component moved into a host collocation); delete the existing one and
				// re-add it later
				removedShapes.add(shape);
			}
		}
		return removedShapes;
	}

	protected UsesDeviceStub getUsesDeviceStub(UsesDevice usesDevice, Diagram diagram) {
		for (EObject object : diagram.eResource().getContents()) {
			if (object instanceof UsesDeviceStub) {
				UsesDeviceStub stub = (UsesDeviceStub) object;
				if (stub.getUsesDevice() == usesDevice) {
					return stub;
				}
			}
		}
		return null;
	}

	protected List<EObject> getObjectsToAdd(Diagram diagram) {
		SoftwareAssembly sad = DUtil.getDiagramSAD(diagram);
		// Find any component instantiations that are in the SAD but do not have an associated shape
		List<EObject> addedChildren = new ArrayList<EObject>();
		for (SadComponentPlacement placement : sad.getPartitioning().getComponentPlacement()) {
			for (SadComponentInstantiation instantiation : placement.getComponentInstantiation()) {
				if (!hasExistingShape(diagram, instantiation)) {
					addedChildren.add(instantiation);
				}
			}
		}
		// Likewise, check for host collocations that do not have an associated shape
		for (HostCollocation collocation : sad.getPartitioning().getHostCollocation()) {
			if (!hasExistingShape(diagram, collocation)) {
				addedChildren.add(collocation);
			}
		}

		// Uses devices do not appear directly in the diagram, but rather via a stub that is embedded in the diagram's
		// resource; if a matching stub does not exist, or is missing a shape, add the shape.
		if (sad.getUsesDeviceDependencies() != null) {
			for (UsesDevice usesDevice : sad.getUsesDeviceDependencies().getUsesdevice()) {
				UsesDeviceStub stub = getUsesDeviceStub(usesDevice, diagram);
				if (stub == null) {
					stub = AbstractUsesDevicePattern.createUsesDeviceStub(usesDevice);
					addedChildren.add(stub);
					diagram.eResource().getContents().add(stub);
				} else if (!hasExistingShape(diagram, stub)) {
					addedChildren.add(stub);
				}
			}
		}
		return addedChildren;
	}

	protected List<ConnectInterface< ? , ? , ? >> getModelConnections(Diagram diagram) {
		SoftwareAssembly sad = DUtil.getDiagramSAD(diagram);
		List<ConnectInterface < ? , ? , ? >> connections = new ArrayList<ConnectInterface< ? , ? , ? >>();
		connections.addAll(sad.getConnections().getConnectInterface());
		return connections;
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
			List<Shape> removedChildren = getShapesToRemove(diagram);
			if (!removedChildren.isEmpty()) {
				if (!performUpdate) {
					return new Reason(true, "Existing shapes need to be removed");
				} else {
					for (Shape shape : removedChildren) {
						IRemoveContext removeContext = new RemoveContext(shape);
						IRemoveFeature removeFeature = getFeatureProvider().getRemoveFeature(removeContext);
						if (removeFeature != null) {
							removeFeature.execute(removeContext);
						}
					}
					updateStatus = true;
				}
			}

			// Prune unused stubs
			List<EObject> removedStubs = getStubsToRemove(diagram);
			if (!removedStubs.isEmpty()) {
				if (!performUpdate) {
					return new Reason(true, "Diagram resource contents need pruning");
				} else {
					diagram.eResource().getContents().removeAll(removedStubs);
					updateStatus = true;
				}
			}

			// Add shapes for SAD objects that do not have them
			List<EObject> addedChildren = getObjectsToAdd(diagram);
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
			List<Connection> removedConnections = getConnectionsToRemove(diagram);
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

			// Add missing connections
			List< ConnectInterface< ? , ? , ? > > addedConnections = getConnectionsToAdd(diagram);
			if (!addedConnections.isEmpty()) {
				if (!performUpdate) {
					return new Reason(true, "Need to add " + addedConnections.size() + " connection(s)");
				}
				for (ConnectInterface< ? , ? , ? > connectInterface : addedConnections) {
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
				}
				updateStatus = true;
			}

			// TODO: ensure our SAD has an assembly controller
			// set one if necessary, why bother the user?

			if (performUpdate) {
				// Update components
				updateStatus |= super.update(context);

				if (layoutNeeded) {
					LayoutDiagramFeature layoutFeature = new LayoutDiagramFeature(getFeatureProvider());
					layoutFeature.execute(null);
					updateStatus = true;
				}
			}

			// Ensure assembly controller is set in case a component was deleted that used to be the assembly controller
			SoftwareAssembly sad = DUtil.getDiagramSAD(diagram);
			ComponentPattern.organizeStartOrder(sad, diagram, getFeatureProvider());
		}

		if (updateStatus && performUpdate) {
			return new Reason(true, "Update successful");
		}

		return new Reason(false, "No updates required");
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
