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

import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import gov.redhawk.diagram.util.InterfacesUtil;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.providers.NodeImageProvider;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractConnectInterfacePattern;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;
import gov.redhawk.sca.dcd.validation.ConnectionsConstraint;
import mil.jpeojtrs.sca.dcd.DcdConnectInterface;
import mil.jpeojtrs.sca.dcd.DcdFactory;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.partitioning.ConnectInterface;

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
				AbstractConnectInterfacePattern.addErrorDecorator(diagram, connectionPE);
			}

			// add graphical arrow to end of the connection
			String styleId;
			if (validationProblem) {
				styleId = StyleUtil.CONNECTION_ERROR;
			} else {
				styleId = StyleUtil.CONNECTION;
			}
			AbstractConnectInterfacePattern.addConnectionArrow(diagram, connectionPE, styleId);
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

	@Override
	protected ConnectInterface< ? , ? , ? > createConnectInterface() {
		return DcdFactory.eINSTANCE.createDcdConnectInterface();
	}

	@Override
	protected String createConnectionId() {
		DeviceConfiguration dcd = DUtil.getDiagramDCD(getDiagram());
		return createConnectionId(dcd.getConnections());
	}

	@Override
	protected void addConnectInterface(ConnectInterface< ? , ? , ? > connection) {
		// Get DCD from diagram
		DeviceConfiguration dcd = DUtil.getDiagramDCD(getDiagram());

		// Create connections if necessary
		if (dcd.getConnections() == null) {
			dcd.setConnections(DcdFactory.eINSTANCE.createDcdConnections());
		}

		// Add to connections
		dcd.getConnections().getConnectInterface().add((DcdConnectInterface) connection);
	}

}
