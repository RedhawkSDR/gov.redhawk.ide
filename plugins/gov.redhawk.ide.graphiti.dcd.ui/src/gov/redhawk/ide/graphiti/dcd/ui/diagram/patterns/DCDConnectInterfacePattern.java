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
package gov.redhawk.ide.graphiti.dcd.ui.diagram.patterns;

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
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.IColorConstant;

import gov.redhawk.diagram.util.InterfacesUtil;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.providers.NodeImageProvider;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractConnectInterfacePattern;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.sca.dcd.validation.ConnectionsConstraint;
import mil.jpeojtrs.sca.dcd.DcdConnectInterface;
import mil.jpeojtrs.sca.dcd.DcdFactory;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;

public class DCDConnectInterfacePattern extends AbstractConnectInterfacePattern {

	@Override
	public String getCreateImageId() {
		return NodeImageProvider.IMG_CONNECTION;
	}

	/**
	 * Determines if a connection can be made.
	 */
	@Override
	public boolean canAdd(IAddContext context) {

		if (context instanceof IAddConnectionContext && context.getNewObject() instanceof DcdConnectInterface) {
			return true;
		}
		return false;
	}

	/**
	 * Adds the connection to the diagram and associates the source/target port with the line
	 */
	@Override
	public PictogramElement add(IAddContext addContext) {
		Connection connectionPE = (Connection) super.add(addContext);

		// add any decorators
		DcdConnectInterface connectInterface = (DcdConnectInterface) addContext.getNewObject();
		decorateConnection(connectionPE, connectInterface, getDiagram());

		return connectionPE;
	}

	/**
	 * Add decorators to connection if applicable
	 * Note: Unfortunately Graphiti doesn't support ConnectionDecorators with tooltips like it does with Shape
	 * Decorators (see RHToolBehaviorProvider)
	 * @param connectionPE
	 */
	public static void decorateConnection(Connection connectionPE, DcdConnectInterface connectInterface, Diagram diagram) {
		// Clear any existing connection decorators
		connectionPE.getConnectionDecorators().clear();

		IGaService gaService = Graphiti.getGaService();
		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		boolean isFindByConnection = false;
		if (connectInterface.getProvidesPort() != null && connectInterface.getUsesPort() != null) {
			if (connectInterface.getProvidesPort().getFindBy() != null || connectInterface.getUsesPort().getFindBy() != null) {
				isFindByConnection = true;
			}
		}

		if (connectInterface.getSource() != null && connectInterface.getTarget() != null || isFindByConnection) {
			// Connection validation (only diagrams which aren't runtime)
			boolean validationProblem = false;
			if (!DUtil.isDiagramRuntime(diagram)) {
				validationProblem = !ConnectionsConstraint.uniqueConnection(connectInterface)
					|| (!isFindByConnection && !InterfacesUtil.areCompatible(connectInterface.getSource(), connectInterface.getTarget()));
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
		// Verify DCD is available
		final DeviceConfiguration dcd = DUtil.getDiagramDCD(getDiagram());
		if (dcd == null) {
			return false;
		}

		return super.canStartConnection(context);
	}

	/**
	 * Determines whether creation of an interface connection is possible between source and destination anchors.
	 * User can begin drawing connection from either direction.
	 * Source anchor of connection must be UsesPort.
	 * Target Anchor must be ConnectionTarget which is the parent class for a variety of types.
	 */
	@Override
	public boolean canCreate(ICreateConnectionContext context) {
		// get dcd from diagram
		final DeviceConfiguration dcd = DUtil.getDiagramDCD(getDiagram());
		if (dcd == null) {
			return false;
		}

		return super.canCreate(context);
	}

	/**
	 * Creates a new connection between the selected usesPortStub and ConnectionTarget
	 */
	@Override
	public Connection create(ICreateConnectionContext context) {

		Connection newConnection = null;

		DcdConnectInterface dcdConnectInterface = (DcdConnectInterface) DUtil.assignAnchorObjectsToConnection(DcdFactory.eINSTANCE.createDcdConnectInterface(),
			context.getSourceAnchor(), context.getTargetAnchor());
		if (dcdConnectInterface == null) {
			// switch source/target direction and try again
			dcdConnectInterface = (DcdConnectInterface) DUtil.assignAnchorObjectsToConnection(DcdFactory.eINSTANCE.createDcdConnectInterface(),
				context.getTargetAnchor(), context.getSourceAnchor());
		}
		if (dcdConnectInterface == null) {
			// can't make a connection
			return null;
		}

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// get sad from diagram
		final DeviceConfiguration dcd = DUtil.getDiagramDCD(getDiagram());

		// create connectionId first check if provided in context (currently used by GraphitiModelMap), otherwise
		// generate unique connection id
		final String connectionId = (context.getProperty(OVERRIDE_CONNECTION_ID) != null) ? (String) context.getProperty(OVERRIDE_CONNECTION_ID)
			: createConnectionId(dcd.getConnections());
		// set connection id
		dcdConnectInterface.setId(connectionId);

		// container for new DcdConnectInterface, necessary for reference after command execution
		final DcdConnectInterface[] dcdConnectInterfaces = new DcdConnectInterface[1];
		dcdConnectInterfaces[0] = dcdConnectInterface;

		// Create Connect Interface & related objects
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {

				// create connections if necessary
				if (dcd.getConnections() == null) {
					dcd.setConnections(DcdFactory.eINSTANCE.createDcdConnections());
				}

				// add to connections
				dcd.getConnections().getConnectInterface().add(dcdConnectInterfaces[0]);

			}
		});

		// add connection for business object
		AddConnectionContext addContext = new AddConnectionContext(context.getSourceAnchor(), context.getTargetAnchor());
		addContext.setNewObject(dcdConnectInterfaces[0]);
		newConnection = (Connection) getFeatureProvider().addIfPossible(addContext);

		return newConnection;
	}

}
