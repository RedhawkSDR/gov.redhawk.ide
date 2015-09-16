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
package gov.redhawk.ide.graphiti.sad.ui.diagram.patterns;

import gov.redhawk.diagram.util.InterfacesUtil;
import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.sad.ui.diagram.providers.WaveformImageProvider;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractConnectInterfacePattern;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.PortStyleUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;
import gov.redhawk.sca.sad.validation.ConnectionsConstraint;
import gov.redhawk.sca.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mil.jpeojtrs.sca.partitioning.ConnectInterface;
import mil.jpeojtrs.sca.partitioning.ConnectionTarget;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesDeviceStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentPlacement;
import mil.jpeojtrs.sca.sad.SadConnectInterface;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.IColorConstant;

public class SADConnectInterfacePattern extends AbstractConnectInterfacePattern {

	public static final String NAME = "Connection";
	public static final String SHAPE_IMG_CONNECTION_DECORATOR = "imgConnectionDecorator";
	public static final String SHAPE_TEXT_CONNECTION_DECORATOR = "textConnectionDecorator";

	public static final String OVERRIDE_CONNECTION_ID = "OverrideConnectionId";

	@Override
	public String getCreateName() {
		return NAME;
	}

	@Override
	public String getCreateDescription() {
		return "Create new Connect Interface";
	}

	@Override
	public String getCreateImageId() {
		return WaveformImageProvider.IMG_CONNECTION;
	}

	/**
	 * Determines if a connection can be made.
	 */
	@Override
	public boolean canAdd(IAddContext context) {
		if (context instanceof IAddConnectionContext && context.getNewObject() instanceof SadConnectInterface) {
			return true;
		}
		return false;
	}

	/**
	 * Adds the connection to the diagram and associates the source/target port with the line
	 */
	@Override
	public PictogramElement add(IAddContext addContext) {
		IGaService gaService = Graphiti.getGaService();
		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		IAddConnectionContext context = (IAddConnectionContext) addContext;
		SadConnectInterface connectInterface = (SadConnectInterface) addContext.getNewObject();

		// source and target
		UsesPortStub source = getUsesPortStub(context);
		ConnectionTarget target = getConnectionTarget(context);

		// check and see if the connection has any special color requirements, such as during a monitor port call
		RHContainerShape rhContainerShape = (RHContainerShape) DUtil.getPictogramElementForBusinessObject(getDiagram(), source.eContainer(),
			RHContainerShape.class);
		Map<String, IColorConstant> connectionMap = rhContainerShape.getConnectionMap();
		IColorConstant defaultColor = (IColorConstant) connectionMap.get(connectInterface.getId());

		// Create connection (handle user selecting source or target)
		Connection connectionPE = peCreateService.createFreeFormConnection(getFeatureProvider().getDiagramTypeProvider().getDiagram());
		if (source == getUsesPortStub(context.getSourceAnchor()) && target == getConnectionTarget(context.getTargetAnchor())) {
			connectionPE.setStart(context.getSourceAnchor());
			connectionPE.setEnd(context.getTargetAnchor());
		} else if (source == getUsesPortStub(context.getTargetAnchor()) && target == getConnectionTarget(context.getSourceAnchor())) {
			connectionPE.setStart(context.getTargetAnchor());
			connectionPE.setEnd(context.getSourceAnchor());
		}

		// create line
		Polyline line = gaService.createPolyline(connectionPE);
		line.setLineWidth(2);
		IColorConstant style = (defaultColor != null) ? defaultColor : StyleUtil.BLACK;
		line.setForeground(gaService.manageColor(getFeatureProvider().getDiagramTypeProvider().getDiagram(), style));

		// add any decorators
		decorateConnection(connectionPE, connectInterface, getDiagram(), style);

		// link ports to connection
		getFeatureProvider().link(connectionPE, new Object[] { connectInterface, source, target });

		return connectionPE;
	}

	/**
	 * Add decorators to connection if applicable
	 * Note: Unfortunately Graphiti doesn't support ConnectionDecorators with tooltips like it does with Shape
	 * Decorators (see RHToolBehaviorProvider)
	 * @param connectionPE
	 */
	public static void decorateConnection(Connection connectionPE, SadConnectInterface connectInterface, Diagram diagram) {
		decorateConnection(connectionPE, connectInterface, diagram, null);
	}

	/**
	 * Add decorators to connection if applicable
	 * provides a default color option for decorating connections
	 * @param connectionPE
	 */
	public static void decorateConnection(Connection connectionPE, SadConnectInterface connectInterface, Diagram diagram, IColorConstant defaultColor) {
		// Clear any existing connection decorators
		connectionPE.getConnectionDecorators().clear();

		IGaService gaService = Graphiti.getGaService();
		IPeCreateService peCreateService = Graphiti.getPeCreateService();

		// establish source/target for connection
		UsesPortStub source = connectInterface.getSource();
		ConnectionTarget target = connectInterface.getTarget();

		// source and target will be null if findBy or usesDevice is used, in this case pull stubs from diagram
		if (source == null) {
			source = DUtil.getBusinessObject(connectionPE.getStart(), UsesPortStub.class);
		}
		if (target == null) {
			target = DUtil.getBusinessObject(connectionPE.getEnd(), ConnectionTarget.class);
		}

		if (source != null && target != null) {
			// Connection validation (only diagrams which aren't runtime)
			boolean validationProblem = false;
			if (!DUtil.isDiagramRuntime(diagram)) {
				validationProblem = !ConnectionsConstraint.uniqueConnection(connectInterface) || !InterfacesUtil.areCompatible(source, target);
			}

			// Add error decorator if necessary
			if (validationProblem) {

				// add graphical X to the middle of the erroneous connection
				ConnectionDecorator errorDecorator = peCreateService.createConnectionDecorator(connectionPE, false, 0.5, true);
				Polyline errPolyline = gaService.createPolyline(errorDecorator, new int[] { -7, 7, 0, 0, -7, -7, 0, 0, 7, -7, 0, 0, 7, 7 });
				errPolyline.setForeground(gaService.manageColor(diagram, IColorConstant.RED));
				errPolyline.setLineWidth(2);
			}

			// add graphical arrow to end of the connection
			IColorConstant arrowColor;
			if (validationProblem) {
				arrowColor = IColorConstant.RED;
			} else if (defaultColor != null) {
				arrowColor = defaultColor;
			} else {
				arrowColor = IColorConstant.BLACK;
			}
			ConnectionDecorator arrowDecorator = peCreateService.createConnectionDecorator(connectionPE, false, 1.0, true);
			Polyline polyline = gaService.createPolyline(arrowDecorator, new int[] { -15, 10, 0, 0, -15, -10 });
			polyline.setForeground(gaService.manageColor(diagram, arrowColor));
			polyline.setLineWidth(2);
		}
	}

	@Override
	public boolean canStartConnection(ICreateConnectionContext context) {
		// Verify SAD is available
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getDiagram());
		if (sad == null) {
			return false;
		}

		return super.canStartConnection(context);
	}

	@Override
	public void startConnecting() {
		super.startConnecting();

		// Highlight ports that may be valid for completing the connection
		if (getSourcePort() != null) {
			highlightCompatiblePorts(getSourcePort());
		} else if (getTargetPort() != null) {
			highlightCompatiblePorts(getTargetPort());
		}
	}

	@Override
	public void endConnecting() {
		// Turns off highlighting ports for the connection
		if (!DUtil.isDiagramExplorer(getDiagram()) && (getSourcePort() != null || getTargetPort() != null)) {
			PortStyleUtil.resetAllPortStyling(getDiagram(), getDiagramBehavior().getEditingDomain());
		}

		super.endConnecting();
	}

	// Utility method to either highlight compatible ports, or return them to default styling
	private void highlightCompatiblePorts(EObject originatingPort) {
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
				SadComponentInstantiation component = (SadComponentInstantiation) businessObj;
				String componentRefId = ((SadComponentPlacement) component.eContainer()).getComponentFileRef().getRefid();
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
				SadComponentInstantiation component = (SadComponentInstantiation) businessObj;
				String componentRefId = ((SadComponentPlacement) component.eContainer()).getComponentFileRef().getRefid();
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

	/**
	 * Determines whether creation of an interface connection is possible between source and destination anchors.
	 * User can begin drawing connection from either direction.
	 * Source anchor of connection must be UsesPort.
	 * Target Anchor must be ConnectionTarget which is the parent class for a variety of types.
	 */
	@Override
	public boolean canCreate(ICreateConnectionContext context) {
		// get sad from diagram
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getDiagram());
		if (sad == null) {
			return false;
		}

		return super.canCreate(context);
	}

	/**
	 * Creates a new connection between the selected usesPortStub and ConnectionTarget
	 */
	@Override
	public Connection create(ICreateConnectionContext context) {
		// Don't allow connections to be drawn in the waveform explorer
		if (DUtil.DIAGRAM_CONTEXT_EXPLORER.equals(DUtil.getDiagramContext(getDiagram()))) {
			return null;
		}

		Connection newConnection = null;

		SadConnectInterface sadConnectInterface = (SadConnectInterface) DUtil.assignAnchorObjectsToConnection(SadFactory.eINSTANCE.createSadConnectInterface(),
			context.getSourceAnchor(), context.getTargetAnchor());
		if (sadConnectInterface == null) {
			// switch source/target direction and try again
			sadConnectInterface = (SadConnectInterface) DUtil.assignAnchorObjectsToConnection(SadFactory.eINSTANCE.createSadConnectInterface(),
				context.getTargetAnchor(), context.getSourceAnchor());
		}

		if (sadConnectInterface == null) {
			// can't make a connection
			return null;
		}

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// get sad from diagram
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getDiagram());

		// create connectionId first check if provided in context (currently used by GraphitiModelMap), otherwise
		// generate unique connection id
		final String connectionId = (context.getProperty(OVERRIDE_CONNECTION_ID) != null) ? (String) context.getProperty(OVERRIDE_CONNECTION_ID)
			: createConnectionId(sad);
		// set connection id
		sadConnectInterface.setId(connectionId);

		// container for new SadConnectInterface, necessary for reference after command execution
		final SadConnectInterface[] sadConnectInterfaces = new SadConnectInterface[1];
		sadConnectInterfaces[0] = sadConnectInterface;

		// Create Connect Interface & related objects
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {

				// create connections if necessary
				if (sad.getConnections() == null) {
					sad.setConnections(SadFactory.eINSTANCE.createSadConnections());
				}

				// add to connections
				sad.getConnections().getConnectInterface().add(sadConnectInterfaces[0]);
			}
		});

		// add connection for business object
		AddConnectionContext addContext = new AddConnectionContext(context.getSourceAnchor(), context.getTargetAnchor());
		addContext.setNewObject(sadConnectInterfaces[0]);
		newConnection = (Connection) getFeatureProvider().addIfPossible(addContext);

		return newConnection;
	}

	/**
	 * Returns the next available connection id
	 * @param sad SoftwareAssembly
	 */
	private String createConnectionId(SoftwareAssembly sad) {
		final List<String> ids = new ArrayList<String>();
		if (sad.getConnections() != null) {
			final List< ? extends ConnectInterface< ? , ? , ? >> connections = sad.getConnections().getConnectInterface();
			for (final ConnectInterface< ? , ? , ? > connection : connections) {
				ids.add(connection.getId());
			}
		}
		return StringUtil.defaultCreateUniqueString("connection_1", ids);
	}

}
