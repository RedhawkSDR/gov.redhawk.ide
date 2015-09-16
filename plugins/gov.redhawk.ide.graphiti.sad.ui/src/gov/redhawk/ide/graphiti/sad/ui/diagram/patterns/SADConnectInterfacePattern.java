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
import gov.redhawk.ide.graphiti.sad.ui.diagram.providers.WaveformImageProvider;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractConnectInterfacePattern;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;
import gov.redhawk.sca.sad.validation.ConnectionsConstraint;

import java.util.Map;

import mil.jpeojtrs.sca.partitioning.ConnectionTarget;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.SadConnectInterface;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.util.IColorConstant;

public class SADConnectInterfacePattern extends AbstractConnectInterfacePattern {

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
		IAddConnectionContext context = (IAddConnectionContext) addContext;
		SadConnectInterface connectInterface = (SadConnectInterface) addContext.getNewObject();

		// check and see if the connection has any special color requirements, such as during a monitor port call
		UsesPortStub source = getUsesPortStub(context);
		RHContainerShape rhContainerShape = (RHContainerShape) DUtil.getPictogramElementForBusinessObject(getDiagram(), source.eContainer(),
			RHContainerShape.class);
		Map<String, IColorConstant> connectionMap = rhContainerShape.getConnectionMap();
		IColorConstant color = (IColorConstant) connectionMap.get(connectInterface.getId());
		if (color == null) {
			color = StyleUtil.BLACK;
		}
		context.putProperty("LineColor", color);

		Connection connectionPE = (Connection) super.add(addContext);

		// add any decorators
		decorateConnection(connectionPE, connectInterface, getDiagram(), color);

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
				AbstractConnectInterfacePattern.addErrorDecorator(diagram, connectionPE);
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
			AbstractConnectInterfacePattern.addConnectionArrow(diagram, connectionPE, arrowColor);
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
			: createConnectionId(sad.getConnections());
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

}
