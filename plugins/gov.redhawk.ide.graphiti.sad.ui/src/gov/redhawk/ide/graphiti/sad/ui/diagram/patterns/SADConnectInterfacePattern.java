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
import mil.jpeojtrs.sca.partitioning.ConnectInterface;
import mil.jpeojtrs.sca.partitioning.ConnectionTarget;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.SadConnectInterface;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

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
		String styleId = rhContainerShape.getConnectionMap().get(connectInterface.getId());
		if (styleId != null) {
			context.putProperty("LineStyle", styleId);
		}
		Connection connectionPE = (Connection) super.add(addContext);

		// add any decorators
		decorateConnection(connectionPE, connectInterface, getDiagram(), styleId);

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
	public static void decorateConnection(Connection connectionPE, SadConnectInterface connectInterface, Diagram diagram, String defaultStyleId) {
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
			String styleId;
			if (validationProblem) {
				styleId = StyleUtil.CONNECTION_ERROR;
			} else if (defaultStyleId != null) {
				styleId = defaultStyleId;
			} else {
				styleId = StyleUtil.CONNECTION;
			}
			AbstractConnectInterfacePattern.addConnectionArrow(diagram, connectionPE, styleId);
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

	@Override
	protected ConnectInterface< ? , ? , ? > createConnectInterface() {
		return SadFactory.eINSTANCE.createSadConnectInterface();
	}

	@Override
	protected String createConnectionId() {
		SoftwareAssembly sad = DUtil.getDiagramSAD(getDiagram());
		return createConnectionId(sad.getConnections());
	}

	@Override
	protected void addConnectInterface(ConnectInterface< ? , ? , ? > connection) {
		SoftwareAssembly sad = DUtil.getDiagramSAD(getDiagram());

		// Create connections if necessary
		if (sad.getConnections() == null) {
			sad.setConnections(SadFactory.eINSTANCE.createSadConnections());
		}

		// Add to connections
		sad.getConnections().getConnectInterface().add((SadConnectInterface) connection);
	}

}
