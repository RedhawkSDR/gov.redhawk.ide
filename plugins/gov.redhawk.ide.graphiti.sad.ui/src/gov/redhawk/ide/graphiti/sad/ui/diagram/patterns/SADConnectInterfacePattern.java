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
import gov.redhawk.ide.graphiti.sad.ui.diagram.GraphitiWaveformDiagramEditor;
import gov.redhawk.ide.graphiti.sad.ui.diagram.providers.WaveformImageProvider;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;
import gov.redhawk.sca.sad.validation.ConnectionsConstraint;
import gov.redhawk.sca.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mil.jpeojtrs.sca.partitioning.ConnectInterface;
import mil.jpeojtrs.sca.partitioning.ConnectionTarget;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesDeviceStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.SadConnectInterface;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IConnectionContext;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.pattern.AbstractConnectionPattern;
import org.eclipse.graphiti.pattern.IConnectionPattern;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.IColorConstant;

public class SADConnectInterfacePattern extends AbstractConnectionPattern implements IConnectionPattern {

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
		EObject source = connectInterface.getSource();
		EObject target = connectInterface.getTarget();

		// source and target will be null if findBy or usesDevice is used, in this case pull stubs from diagram
		if (source == null) {
			source = DUtil.getBusinessObject(connectionPE.getStart(), UsesPortStub.class);
		}
		if (target == null) {
			target = DUtil.getBusinessObject(connectionPE.getEnd(), ConnectionTarget.class);
		}

		if (source != null && target != null) {
			// Connection validation
			boolean uniqueConnection = ConnectionsConstraint.uniqueConnection(connectInterface);
			boolean compatibleConnection = InterfacesUtil.areCompatible(source, target);

			// Add error decorator if necessary
			if (!compatibleConnection || !uniqueConnection) {

				// add graphical X to the middle of the erroneous connection
				ConnectionDecorator errorDecorator = peCreateService.createConnectionDecorator(connectionPE, false, 0.5, true);
				Polyline errPolyline = gaService.createPolyline(errorDecorator, new int[] { -7, 7, 0, 0, -7, -7, 0, 0, 7, -7, 0, 0, 7, 7 });
				errPolyline.setForeground(gaService.manageColor(diagram, IColorConstant.RED));
				errPolyline.setLineWidth(2);
			}

			// add graphical arrow to end of the connection
			IColorConstant arrowColor;
			if (!compatibleConnection || !uniqueConnection) {
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

	/**
	 * Return true if use selected
	 */
	@Override
	public boolean canStartConnection(ICreateConnectionContext context) {
		// Don't allow connections to be drawn in the waveform explorer
		if (DUtil.DIAGRAM_CONTEXT_EXPLORER.equals(DUtil.getDiagramContext(getDiagram()))) {
			return false;
		}
		
		// get sad from diagram
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getDiagram());

		// source anchor (allow creating connection by starting from either direction)
		UsesPortStub source = getUsesPortStub(context);
		ConnectionTarget target = getConnectionTarget(context);

		if (sad != null) {
			if (source != null) {
				this.sourcePort = source;
				return true;
			} else if (target != null) {
				this.targetPort = target;
				return true;
			}
		}

		return false;
	}

	@Override
	public void startConnecting() {
		super.startConnecting();
		if (DUtil.DIAGRAM_CONTEXT_EXPLORER.equals(DUtil.getDiagramContext(getDiagram()))) {
			return;
		}

		// Handles the highlighting of compatible ports during connection attempt
		if (this.sourcePort != null) {
			highlightCompatiblePorts(sourcePort, true);
		} else if (this.targetPort != null) {
			highlightCompatiblePorts(targetPort, true);
		}
	}

	@Override
	public void endConnecting() {
		super.endConnecting();
		if (DUtil.DIAGRAM_CONTEXT_EXPLORER.equals(DUtil.getDiagramContext(getDiagram()))) {
			return;
		}

		// Turns off the highlighting of compatible ports post-connection attempt
		if (this.sourcePort != null) {
			highlightCompatiblePorts(sourcePort, false);
			this.sourcePort = null;
		} else if (this.targetPort != null) {
			highlightCompatiblePorts(targetPort, false);
			this.targetPort = null;
		}
	}

	// Utility method to either highlight compatible ports, or return them to default styling
	private void highlightCompatiblePorts(Object originatingPort, boolean shouldHighlight) {

		setPortStyleLock(shouldHighlight);

		TransactionalEditingDomain editingDomain = getDiagramBehavior().getEditingDomain();
		List<ContainerShape> diagramPorts = null;

		if (originatingPort instanceof UsesPortStub) {

			// If the originating port is a uses, build a list of compatible provides
			diagramPorts = DUtil.getDiagramProvidesPorts(getDiagram());
			List<ContainerShape> incompatiblePorts = new ArrayList<ContainerShape>();
			for (int i = 0; i < diagramPorts.size(); i++) {
				ContainerShape port = diagramPorts.get(i);
				ProvidesPortStub providesStub = (ProvidesPortStub) DUtil.getBusinessObject(port);

				// Remove from port list all FindBy and incompatible ports
				if (providesStub.eContainer() instanceof FindByStub) {
					incompatiblePorts.add(port);
				} else if (providesStub.eContainer() instanceof UsesDeviceStub
					&& !InterfacesUtil.areSuggestedMatch((UsesPortStub) originatingPort, providesStub)) {
					incompatiblePorts.add(port);
				} else if (!InterfacesUtil.areCompatible((UsesPortStub) originatingPort, providesStub)) {
					incompatiblePorts.add(port);
				}
			}
			diagramPorts.removeAll(incompatiblePorts);
		} else if (originatingPort instanceof ProvidesPortStub) {
			// If the originating port is a provides, build a list of compatible uses
			diagramPorts = DUtil.getDiagramUsesPorts(getDiagram());
			List<ContainerShape> incompatiblePorts = new ArrayList<ContainerShape>();
			for (int i = 0; i < diagramPorts.size(); i++) {
				ContainerShape port = diagramPorts.get(i);
				UsesPortStub usesStub = (UsesPortStub) DUtil.getBusinessObject(port);

				// Remove from port list all FindBy and incompatible ports
				if (usesStub.eContainer() instanceof FindByStub) {
					incompatiblePorts.add(port);
				} else if (usesStub.eContainer() instanceof UsesDeviceStub && !InterfacesUtil.areSuggestedMatch(usesStub, (ProvidesPortStub) originatingPort)) {
					incompatiblePorts.add(port);
				} else if (!InterfacesUtil.areCompatible(usesStub, (ProvidesPortStub) originatingPort)) {
					incompatiblePorts.add(port);
				}
			}
			diagramPorts.removeAll(incompatiblePorts);
		}

		// Change style to highlight or default, depending on passed boolean
		for (ContainerShape port : diagramPorts) {
			Rectangle anchorGa = (Rectangle) port.getChildren().get(0).getAnchors().get(0).getGraphicsAlgorithm();
			if (shouldHighlight) {
				StyleUtil.updatePortStyle(anchorGa, getDiagram(), StyleUtil.COMPATIBLE_PORT, editingDomain);
			} else {
				StyleUtil.updatePortStyle(anchorGa, getDiagram(), null, editingDomain);
			}
		}
	}

	/**
	 * Connection highlighting takes precident over all other styling options, so wipe them out and
	 * block future style updates until after the connection attempt completes.
	 */
	private void setPortStyleLock(boolean shouldLock) {
		TransactionalEditingDomain editingDomain = getDiagramBehavior().getEditingDomain();

		List<ContainerShape> allPorts = new ArrayList<ContainerShape>();
		allPorts.addAll(DUtil.getDiagramProvidesPorts(getDiagram()));
		allPorts.addAll(DUtil.getDiagramUsesPorts(getDiagram()));
		for (ContainerShape port : allPorts) {
			if (shouldLock) {
				Rectangle anchorGa = (Rectangle) port.getChildren().get(0).getAnchors().get(0).getGraphicsAlgorithm();
				StyleUtil.updatePortStyle(anchorGa, getDiagram(), null, getDiagramBehavior().getEditingDomain());
				StyleUtil.toggleUpdatePort(((RHContainerShapeImpl) port.eContainer().eContainer()), false, editingDomain, this);
			} else {
				StyleUtil.toggleUpdatePort(((RHContainerShapeImpl) port.eContainer().eContainer()), true, editingDomain, null);
			}
		}
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

		// doing the null check because it breaks when loading a findby without a diagram
		if (((GraphitiWaveformDiagramEditor) getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getDiagramContainer()).getGraphicalViewer() != null) {
			// force selection of shape so that we can then right click for contextual options
			// this is kind of a hack, it would be better if selection happened automatically when its clicked.
			if (context.getSourcePictogramElement() != null) {
				getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getDiagramContainer().selectPictogramElements(
					new PictogramElement[] { context.getSourcePictogramElement() });
			}
		}

		// determine source
		UsesPortStub source = getUsesPortStub(context);
		if (source == null) {
			return false;
		}

		// determine destination
		// getConnectionTarget handles connecting to ports on components, not ports or interfaces on FindBy Shapes
		ConnectionTarget target = getConnectionTarget(context);
		if (target == null) {
			// PASS
			// TODO: check if interface on findBy Shape
			// TODO: check if provides port on findBy...not sure how were doing all this??
		}

		return true;

	}

	/**
	 * Creates a new connection between the selected usesPortStub and ConnectionTarget
	 */
	@Override
	public Connection create(ICreateConnectionContext context) {

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

	// Return UsesPortStub from either the source or target anchor. Depends on how user drew connection.
	private UsesPortStub getUsesPortStub(IConnectionContext context) {
		UsesPortStub source = getUsesPortStub(context.getSourceAnchor());
		if (source != null) {
			return source;
		}
		source = getUsesPortStub(context.getTargetAnchor());
		return source;
	}

	private UsesPortStub getUsesPortStub(Anchor anchor) {
		if (anchor != null) {
			Object object = getBusinessObjectForPictogramElement(anchor.getParent());
			if (object instanceof UsesPortStub) {
				return (UsesPortStub) object;
			}
		}
		return null;
	}

	// Return ConnectionTarget from either the source or target anchor. Depends on how user drew connection.
	private ConnectionTarget getConnectionTarget(IConnectionContext context) {
		ConnectionTarget connectionTarget = getConnectionTarget(context.getSourceAnchor());
		if (connectionTarget != null) {
			return connectionTarget;
		}
		connectionTarget = getConnectionTarget(context.getTargetAnchor());
		return connectionTarget;
	}

	private ConnectionTarget getConnectionTarget(Anchor anchor) {
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
