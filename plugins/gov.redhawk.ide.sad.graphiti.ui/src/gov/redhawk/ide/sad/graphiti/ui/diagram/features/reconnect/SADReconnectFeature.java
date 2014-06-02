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

import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.impl.DefaultReconnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;

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
		Object oldAnchorObject = getBusinessObjectForPictogramElement(oldAnchor);
		Object newAnchorObject = getBusinessObjectForPictogramElement(newAnchor);
		if (oldAnchorObject instanceof UsesPortStub) {
			if (newAnchorObject instanceof UsesPortStub) {
				return true;
			}
		} else {
			if (newAnchorObject instanceof ProvidesPortStub) {
				return true;
			}
		}
		return false;
	}

}
