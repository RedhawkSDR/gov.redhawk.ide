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
package gov.redhawk.ide.graphiti.dcd.ui.adapters;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.platform.GraphitiShapeEditPart;

import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;

public class DeviceConfigurationAdapter implements IAdapterFactory {

	private static final Class< ? >[] ADAPTER_TYPES = new Class< ? >[] { DeviceConfiguration.class };

	@Override
	public < T > T getAdapter(Object adaptableObject, Class<T> adapterType) {
		if (adaptableObject instanceof GraphitiShapeEditPart) {
			GraphitiShapeEditPart ep = (GraphitiShapeEditPart) adaptableObject;
			if (ep.getModel() instanceof Diagram) {
				DeviceConfiguration dcd = DUtil.getBusinessObject((Diagram) ep.getModel(), DeviceConfiguration.class);
				if (adapterType.isInstance(dcd)) {
					return adapterType.cast(dcd);
				}
			}
		}
		return null;
	}

	@Override
	public Class< ? >[] getAdapterList() {
		return ADAPTER_TYPES;
	}

}
