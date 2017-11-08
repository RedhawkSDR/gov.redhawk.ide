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
package gov.redhawk.ide.graphiti.ui.adapters;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.ui.platform.GraphitiConnectionEditPart;

import gov.redhawk.core.graphiti.ui.util.DUtil;
import mil.jpeojtrs.sca.partitioning.ConnectInterface;

public class ConnectInterfaceAdapter implements IAdapterFactory {

	private static final Class< ? >[] ADAPTER_TYPES = new Class< ? >[] { ConnectInterface.class };

	@Override
	public < T > T getAdapter(Object adaptableObject, Class<T> adapterType) {
		if (!(adaptableObject instanceof GraphitiConnectionEditPart)) {
			return null;
		}
		GraphitiConnectionEditPart connectionEditPart = (GraphitiConnectionEditPart) adaptableObject;
		if (!(connectionEditPart.getModel() instanceof Connection)) {
			return null;
		}

		Connection peConnection = (Connection) connectionEditPart.getModel();
		ConnectInterface< ? , ? , ? > connectInterface = DUtil.getBusinessObject(peConnection, ConnectInterface.class);
		if (adapterType.isInstance(connectInterface)) {
			return adapterType.cast(connectInterface);
		}

		return null;
	}

	@Override
	public Class< ? >[] getAdapterList() {
		return ADAPTER_TYPES;
	}

}
