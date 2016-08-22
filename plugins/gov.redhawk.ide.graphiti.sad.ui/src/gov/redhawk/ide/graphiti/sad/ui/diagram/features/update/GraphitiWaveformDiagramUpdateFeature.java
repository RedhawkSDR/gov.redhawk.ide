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
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;

import gov.redhawk.core.graphiti.sad.ui.utils.SADUtils;
import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.AbstractUsesDevicePattern;
import gov.redhawk.ide.graphiti.ui.diagram.features.update.AbstractDiagramUpdateFeature;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterface;
import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub;
import mil.jpeojtrs.sca.partitioning.ConnectInterface;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.partitioning.ProvidesPort;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesDeviceStub;
import mil.jpeojtrs.sca.partitioning.UsesPort;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentPlacement;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.UsesDevice;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

public class GraphitiWaveformDiagramUpdateFeature extends AbstractDiagramUpdateFeature {

	public GraphitiWaveformDiagramUpdateFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	protected boolean doesModelObjectExist(EObject object) {
		if (object instanceof UsesDeviceStub) {
			// Check the uses device reference of the stub
			object = ((UsesDeviceStub) object).getUsesDevice();
		}
		return super.doesModelObjectExist(object);
	}

	protected boolean hasParentChanged(Shape shape) {
		EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(shape);
		if (bo instanceof SadComponentInstantiation) {
			HostCollocation collocation = ScaEcoreUtils.getEContainerOfType(bo, HostCollocation.class);
			if (collocation == null) {
				// Not collocated, should be a child of the diagram
				return !(shape.getContainer() instanceof Diagram);
			} else {
				// Check that the parent shape's business object is the same collocation
				return collocation != Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(shape.getContainer());
			}
		}
		return false;
	}

	@Override
	protected boolean shouldRemoveShape(Shape shape) {
		if (super.shouldRemoveShape(shape)) {
			return true;
		} else if (hasParentChanged(shape)) {
			// Parent has changed (e.g., component moved into a host collocation); delete the existing one and re-add
			// it later
			return true;
		}
		return false;
	}

	protected UsesDeviceStub getUsesDeviceStub(UsesDevice usesDevice, Diagram diagram) {
		for (UsesDeviceStub usesDeviceStub : getDiagramStubs(diagram, UsesDeviceStub.class)) {
			if (usesDeviceStub.getUsesDevice() == usesDevice) {
				return usesDeviceStub;
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
		if (sad.getConnections() != null) {
			connections.addAll(sad.getConnections().getConnectInterface());
		}
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
	 * @deprecated This isn't used any more
	 */
	@Deprecated
	public Reason internalUpdate(IUpdateContext context, boolean performUpdate) throws CoreException {
		if (!performUpdate) {
			// Match return type; this method should return IReason, but does not
			IReason reason = updateNeeded(context);
			return new Reason(reason.toBoolean(), reason.getText());
		} else {
			if (update(context)) {
				return new Reason(true, "Update successful");
			} else {
				return new Reason(false, "No updates required");
			}
		}
	}

	@Override
	public boolean update(IUpdateContext context) {
		PictogramElement pe = context.getPictogramElement();
		if (pe instanceof Diagram) {
			// Ensure assembly controller is set in case a component was deleted that used to be the assembly
			// controller, and re-assign the start orders. It is not necessary to check whether this made any
			// changes, because if it does, the child shapes will have to be updated and super.update() will
			// return true.
			Diagram diagram = (Diagram) pe;
			SoftwareAssembly sad = DUtil.getDiagramSAD(diagram);
			SADUtils.organizeStartOrder(sad, diagram, getFeatureProvider());

			// Defer to the base class for most updates
			return super.update(context);
		}
		return false;
	}

	@Override
	protected List<UsesPortStub> getUsesPortStubs(UsesPort< ? > usesPort, Diagram diagram) {
		if (usesPort.getDeviceUsedByApplication() != null) {
			UsesDeviceStub usesDeviceStub = AbstractUsesDevicePattern.findUsesDeviceStub(usesPort.getDeviceUsedByApplication(), diagram);
			if (usesDeviceStub != null) {
				return usesDeviceStub.getUsesPortStubs();
			}
		}
		return super.getUsesPortStubs(usesPort, diagram);
	}

	@Override
	protected void addUsesPort(ConnectInterface< ? , ? , ? > connectInterface, Diagram diagram) {
		if (connectInterface.getUsesPort().getDeviceUsedByApplication() != null) {
			UsesDeviceStub usesDeviceStub = AbstractUsesDevicePattern.findUsesDeviceStub(connectInterface.getUsesPort().getDeviceUsedByApplication(), diagram);
			if (usesDeviceStub != null) {
				UsesPortStub usesPortStub = PartitioningFactory.eINSTANCE.createUsesPortStub();
				usesPortStub.setName(connectInterface.getUsesPort().getUsesIdentifier());
				usesDeviceStub.getUsesPortStubs().add(usesPortStub);
			}
		} else {
			super.addUsesPort(connectInterface, diagram);
		}
	}

	@Override
	protected ComponentSupportedInterfaceStub getComponentSupportedInterface(ComponentSupportedInterface componentSupportedInterface, Diagram diagram) {
		if (componentSupportedInterface.getDeviceUsedByApplication() != null) {
			UsesDeviceStub usesDeviceStub = AbstractUsesDevicePattern.findUsesDeviceStub(componentSupportedInterface.getDeviceUsedByApplication(), diagram);
			if (usesDeviceStub != null) {
				return usesDeviceStub.getInterface();
			} else {
				return null;
			}
		} else {
			return super.getComponentSupportedInterface(componentSupportedInterface, diagram);
		}
	}

	@Override
	protected List<ProvidesPortStub> getProvidesPortStubs(ProvidesPort< ? > providesPort, Diagram diagram) {
		if (providesPort.getDeviceUsedByApplication() != null) {
			UsesDeviceStub usesDeviceStub = AbstractUsesDevicePattern.findUsesDeviceStub(providesPort.getDeviceUsedByApplication(), diagram);
			if (usesDeviceStub != null) {
				return usesDeviceStub.getProvidesPortStubs();
			}
		}
		return super.getProvidesPortStubs(providesPort, diagram);
	}

	@Override
	protected void addProvidesPort(ProvidesPort< ? > providesPort, Diagram diagram) {
		if (providesPort.getDeviceUsedByApplication() != null) {
			UsesDeviceStub usesDeviceStub = AbstractUsesDevicePattern.findUsesDeviceStub(providesPort.getDeviceUsedByApplication(), diagram);
			if (usesDeviceStub != null) {
				ProvidesPortStub providesPortStub = PartitioningFactory.eINSTANCE.createProvidesPortStub();
				providesPortStub.setName(providesPort.getProvidesIdentifier());
				usesDeviceStub.getProvidesPortStubs().add(providesPortStub);
			}
		} else {
			super.addProvidesPort(providesPort, diagram);
		}
	}

}
