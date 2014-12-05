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
package gov.redhawk.ide.sad.graphiti.ui.diagram.features.reconnect;

import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.SADConnectInterfacePattern;
import mil.jpeojtrs.sca.partitioning.ConnectionTarget;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.SadConnectInterface;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.impl.DefaultReconnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;

public class SADReconnectFeature extends DefaultReconnectionFeature {

	public SADReconnectFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canReconnect(IReconnectionContext context) {
		// Only allow reconnect if the connection is being made to the same type of object

		Anchor oldAnchor = context.getOldAnchor();
		Anchor newAnchor = context.getNewAnchor();
		if (oldAnchor == null || newAnchor == null) {
			return false;
		}

		EObject oldAnchorObject = (EObject) getBusinessObjectForPictogramElement(oldAnchor);
		EObject newAnchorObject = (EObject) getBusinessObjectForPictogramElement(newAnchor);
		// Determine the direction of the new connection, and then validate
		if (oldAnchorObject instanceof UsesPortStub) {
			// Make sure connection is made to a valid port
			if (newAnchorObject instanceof UsesPortStub) {
				return true;
			}
		} else {
			// Make sure connection is made to a valid port
			if (newAnchorObject instanceof ProvidesPortStub) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void execute(IContext context) {
		if (context instanceof IReconnectionContext) {
			IReconnectionContext reconnectContext = (IReconnectionContext) context;
			Connection connectionPE = reconnectContext.getConnection();
			SadConnectInterface connectInterface = (SadConnectInterface) getBusinessObjectForPictogramElement(connectionPE);

			Object connectionStart = getBusinessObjectForPictogramElement(connectionPE.getStart());
			EObject oldPort = (EObject) getBusinessObjectForPictogramElement(reconnectContext.getOldAnchor());
			EObject newPort = (EObject) getBusinessObjectForPictogramElement(reconnectContext.getNewAnchor());

			if (oldPort.equals(connectionStart)) {
				// User changed uses port, update model with new source
				UsesPortStub source = (UsesPortStub) newPort;
				connectInterface.setSource(source);
				connectionStart = source;
				// Update diagram with new start anchor
				connectionPE.setStart(reconnectContext.getNewAnchor());
			} else {
				// User changed provides port, update model with new target
				ConnectionTarget target = (ConnectionTarget) newPort;
				connectInterface.setTarget(target);
				// Update diagram with new end anchor
				connectionPE.setEnd(reconnectContext.getNewAnchor());
			}

			// Update port business object for connection pictogram element
			EList<EObject> connectionBusinessObjects = connectionPE.getLink().getBusinessObjects();
			connectionBusinessObjects.remove(oldPort);
			connectionBusinessObjects.add(newPort);

			// Update connection decorators
			SADConnectInterfacePattern.decorateConnection(connectionPE, connectInterface, getDiagram());
		}
	}

}
