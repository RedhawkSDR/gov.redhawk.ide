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
package gov.redhawk.ide.graphiti.ui.diagram.patterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.context.IConnectionContext;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.pattern.AbstractConnectionPattern;
import org.eclipse.graphiti.services.Graphiti;

import gov.redhawk.diagram.util.InterfacesUtil;
import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.PortStyleUtil;
import gov.redhawk.model.sca.commands.NonDirtyingCommand;
import gov.redhawk.sca.util.StringUtil;
import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;
import mil.jpeojtrs.sca.partitioning.ComponentPlacement;
import mil.jpeojtrs.sca.partitioning.ConnectInterface;
import mil.jpeojtrs.sca.partitioning.ConnectionTarget;
import mil.jpeojtrs.sca.partitioning.Connections;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesDeviceStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

public class AbstractConnectInterfacePattern extends AbstractConnectionPattern {

	public static final String NAME = "Connection";
	public static final String SHAPE_IMG_CONNECTION_DECORATOR = "imgConnectionDecorator";
	public static final String SHAPE_TEXT_CONNECTION_DECORATOR = "textConnectionDecorator";
	public static final String OVERRIDE_CONNECTION_ID = "OverrideConnectionId";

	private UsesPortStub sourcePort;
	private ConnectionTarget targetPort;

	@Override
	public String getCreateName() {
		return NAME;
	}

	@Override
	public String getCreateDescription() {
		return "Create new Connect Interface";
	}

	/**
	 * Returns the source port a connection is being initiated from. Only available after {@link #startConnecting()}.
	 * @return The source port or null if none
	 */
	protected UsesPortStub getSourcePort() {
		return sourcePort;
	}

	/**
	 * Returns the connection target a connection is being initiated from. Only available after
	 * {@link #startConnecting()}.
	 * @return The target connection or null if none
	 */
	protected ConnectionTarget getTargetPort() {
		return targetPort;
	}

	@Override
	public boolean canStartConnection(ICreateConnectionContext context) {
		// Not allowed in the explorer or if read-only
		Diagram diagram = getDiagram();
		if (DUtil.isDiagramExplorer(diagram) || DUtil.isDiagramReadOnly(diagram)) {
			return false;
		}

		// Check if the source anchor belongs to a component, and disallow the connection if it's disabled
		RHContainerShape component = ScaEcoreUtils.getEContainerOfType(context.getSourceAnchor(), RHContainerShape.class);
		if (component != null && !component.isEnabled()) {
			return false;
		}

		// We must be able to find a UsesPortStub or ConnectionTarget
		UsesPortStub source = getUsesPortStub(context);
		if (source != null) {
			this.sourcePort = source;
			return true;
		}
		ConnectionTarget target = getConnectionTarget(context);
		if (target != null) {
			this.targetPort = target;
			return true;
		}

		return false;
	}

	@Override
	public void startConnecting() {
		// Set a property on the diagram so it's easy to tell a connection is in progress
		CommandStack stack = getDiagramBehavior().getEditingDomain().getCommandStack();
		stack.execute(new NonDirtyingCommand() {
			@Override
			public void execute() {
				Graphiti.getPeService().setPropertyValue(getDiagram(), DUtil.DIAGRAM_CONNECTION_IN_PROGRESS, "true");
			}
		});

		// Highlight ports that may be valid for completing the connection
		if (getSourcePort() != null) {
			highlightCompatiblePorts(getSourcePort());
		} else if (getTargetPort() != null) {
			highlightCompatiblePorts(getTargetPort());
		}
	}

	@Override
	public boolean canCreate(ICreateConnectionContext context) {
		// Check if the target anchor belongs to a component, and disallow the connection if it's disabled; the source
		// was already checked in canStart().
		RHContainerShape component = ScaEcoreUtils.getEContainerOfType(context.getTargetAnchor(), RHContainerShape.class);
		if (component != null && !component.isEnabled()) {
			return false;
		}

		// In any other case, assume it's a valid connection so that the mouse pointer doesn't give negative feedback.
		return true;
	}

	@Override
	public void endConnecting() {
		// Turns off highlighting ports for the connection
		if (!DUtil.isDiagramExplorer(getDiagram()) && (getSourcePort() != null || getTargetPort() != null)) {
			PortStyleUtil.resetAllPortStyling(getDiagram(), getDiagramBehavior().getEditingDomain());
		}

		this.sourcePort = null;
		this.targetPort = null;

		CommandStack stack = getDiagramBehavior().getEditingDomain().getCommandStack();
		stack.execute(new NonDirtyingCommand() {
			@Override
			public void execute() {
				Graphiti.getPeService().removeProperty(getDiagram(), DUtil.DIAGRAM_CONNECTION_IN_PROGRESS);
			}
		});

		super.endConnecting();
	}

	/**
	 * Return UsesPortStub from either the source or target anchor. Depends on how user drew connection.
	 * @param context
	 * @return
	 */
	protected UsesPortStub getUsesPortStub(IConnectionContext context) {
		UsesPortStub source = getUsesPortStub(context.getSourceAnchor());
		if (source != null) {
			return source;
		}
		source = getUsesPortStub(context.getTargetAnchor());
		return source;
	}

	protected UsesPortStub getUsesPortStub(Anchor anchor) {
		if (anchor != null) {
			Object object = getBusinessObjectForPictogramElement(anchor.getParent());
			if (object instanceof UsesPortStub) {
				return (UsesPortStub) object;
			}
		}
		return null;
	}

	/**
	 * Return ConnectionTarget from either the source or target anchor. Depends on how user drew connection.
	 * @param context
	 * @return
	 */
	protected ConnectionTarget getConnectionTarget(IConnectionContext context) {
		ConnectionTarget connectionTarget = getConnectionTarget(context.getSourceAnchor());
		if (connectionTarget != null) {
			return connectionTarget;
		}
		connectionTarget = getConnectionTarget(context.getTargetAnchor());
		return connectionTarget;
	}

	protected ConnectionTarget getConnectionTarget(Anchor anchor) {
		if (anchor != null) {
			Object object = getBusinessObjectForPictogramElement(anchor.getParent());
			if (object instanceof ConnectionTarget) {
				return (ConnectionTarget) object;
			}
		}
		return null;
	}

	/**
	 * Returns the next available connection id
	 * @param sad SoftwareAssembly
	 */
	protected String createConnectionId(Connections< ? > connections) {
		final List<String> ids = new ArrayList<String>();
		if (connections != null) {
			for (final ConnectInterface< ? , ? , ? > connection : connections.getConnectInterface()) {
				ids.add(connection.getId());
			}
		}
		return StringUtil.defaultCreateUniqueString("connection_1", ids);
	}

	protected void highlightCompatiblePorts(EObject originatingPort) {
		if (originatingPort == null) {
			return;
		}

		// Don't execute highlighting if a super port is involved.
		final Diagram diagram = getDiagram();
		final ContainerShape portContainer = (ContainerShape) DUtil.getPictogramElementForBusinessObject(diagram, originatingPort, ContainerShape.class);
		if (DUtil.isSuperPort(portContainer)) {
			return;
		}

		Set<ContainerShape> compatiblePorts;
		if (originatingPort instanceof UsesPortStub) {
			UsesPortStub usesPort = (UsesPortStub) originatingPort;
			compatiblePorts = getCompatiblePorts(usesPort);
		} else if (originatingPort instanceof ProvidesPortStub) {
			ProvidesPortStub providesPort = (ProvidesPortStub) originatingPort;
			compatiblePorts = getCompatiblePorts(providesPort);
		} else {
			// Most likely user clicked a component supported interface shape
			return;
		}

		// Highlight compatible ports
		PortStyleUtil.highlightCompatiblePorts(diagram, getDiagramBehavior().getEditingDomain(), compatiblePorts);
	}

	private Set<ContainerShape> getCompatiblePorts(UsesPortStub usesPort) {
		Set<ContainerShape> compatiblePorts = new HashSet<ContainerShape>();

		// The first time we come across an component instance, add the valid ports to this map.
		// If we come across this component type again, refer to the map rather than redoing the port comparisons
		Map<String, ArrayList<String>> compatiblePortsMap = new HashMap<String, ArrayList<String>>();

		// A list to hold all component shapes found in the diagram
		List<ContainerShape> componentShapes = DUtil.getAllContainerShapes(getDiagram(), RHContainerShapeImpl.SHAPE_OUTER_CONTAINER);

		for (ContainerShape componentShape : componentShapes) {
			Object businessObj = DUtil.getBusinessObject(componentShape);
			if (businessObj instanceof FindByStub) {
				// Not interested in highlighting ports of FindBy shapes
				continue;
			} else if (businessObj instanceof UsesDeviceStub) {
				for (ContainerShape portShape : DUtil.getDiagramProvidesPorts(componentShape)) {
					ProvidesPortStub providesPort = (ProvidesPortStub) DUtil.getBusinessObject(portShape);
					if (InterfacesUtil.areSuggestedMatch(usesPort, providesPort)) {
						compatiblePorts.add(portShape);
					}
				}
			} else {
				ComponentInstantiation component = (ComponentInstantiation) businessObj;
				String componentRefId = ((ComponentPlacement< ? >) component.eContainer()).getComponentFileRef().getRefid();
				if (compatiblePortsMap.containsKey(componentRefId)) {
					// If we have already seen a component of this type, just grab the ports we know are compatible
					ArrayList<String> portNames = compatiblePortsMap.get(componentRefId);
					for (ContainerShape portShape : DUtil.getDiagramProvidesPorts(componentShape)) {
						ProvidesPortStub providesPort = (ProvidesPortStub) DUtil.getBusinessObject(portShape);
						if (portNames.contains(providesPort.getName())) {
							compatiblePorts.add(portShape);
						}
					}
				} else {
					// We haven't yet evaluated this component type. We'll find compatible ports and cache them in the
					// map for future reference.
					ArrayList<String> portNames = new ArrayList<String>();
					for (ContainerShape portShape : DUtil.getDiagramProvidesPorts(componentShape)) {
						ProvidesPortStub providesPort = (ProvidesPortStub) DUtil.getBusinessObject(portShape);
						if (InterfacesUtil.areCompatible(usesPort, providesPort)) {
							compatiblePorts.add(portShape);
							portNames.add(providesPort.getName());
						}
					}
					compatiblePortsMap.put(componentRefId, portNames);
				}
			}
		}
		return compatiblePorts;
	}

	private Set<ContainerShape> getCompatiblePorts(ProvidesPortStub providesPort) {
		Set<ContainerShape> compatiblePorts = new HashSet<ContainerShape>();

		// The first time we come across an component instance, add the valid ports to this map.
		// If we come across this component type again, refer to the map rather than redoing the port comparisons
		Map<String, ArrayList<String>> compatiblePortsMap = new HashMap<String, ArrayList<String>>();

		// A list to hold all component shapes found in the diagram
		List<ContainerShape> componentShapes = DUtil.getAllContainerShapes(getDiagram(), RHContainerShapeImpl.SHAPE_OUTER_CONTAINER);

		for (ContainerShape componentShape : componentShapes) {
			Object businessObj = DUtil.getBusinessObject(componentShape);
			if (businessObj instanceof FindByStub) {
				// Not interested in highlighting ports of FindBy shapes
				continue;
			} else if (businessObj instanceof UsesDeviceStub) {
				for (ContainerShape portShape : DUtil.getDiagramUsesPorts(componentShape)) {
					UsesPortStub usesPort = (UsesPortStub) DUtil.getBusinessObject(portShape);
					if (InterfacesUtil.areSuggestedMatch(usesPort, providesPort)) {
						compatiblePorts.add(portShape);
					}
				}
			} else {
				ComponentInstantiation component = (ComponentInstantiation) businessObj;
				String componentRefId = ((ComponentPlacement< ? >) component.eContainer()).getComponentFileRef().getRefid();
				if (!compatiblePortsMap.isEmpty() && compatiblePortsMap.containsKey(componentRefId)) {
					// If we have already seen a component of this type, just grab the ports we know are compatible
					ArrayList<String> portNames = compatiblePortsMap.get(componentRefId);
					for (ContainerShape portShape : DUtil.getDiagramUsesPorts(componentShape)) {
						UsesPortStub usesPort = (UsesPortStub) DUtil.getBusinessObject(portShape);
						if (portNames.contains(usesPort.getName())) {
							compatiblePorts.add(portShape);
						}
					}
				} else {
					// If new component type, find compatible ports and add them to the map for future reference
					ArrayList<String> portNames = new ArrayList<String>();
					for (ContainerShape portShape : DUtil.getDiagramUsesPorts(componentShape)) {
						UsesPortStub usesPort = (UsesPortStub) DUtil.getBusinessObject(portShape);
						if (InterfacesUtil.areCompatible(usesPort, providesPort)) {
							compatiblePorts.add(portShape);
							portNames.add(usesPort.getName());
						}
					}
					compatiblePortsMap.put(componentRefId, portNames);
				}
			}
		}
		return compatiblePorts;
	}

}
