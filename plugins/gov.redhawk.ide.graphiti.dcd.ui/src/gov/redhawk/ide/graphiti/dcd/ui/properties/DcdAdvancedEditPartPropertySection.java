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
package gov.redhawk.ide.graphiti.dcd.ui.properties;

import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.EditPart;

import gov.redhawk.core.graphiti.dcd.ui.ext.DeviceShape;
import gov.redhawk.ide.graphiti.ui.properties.AdvancedEditPartPropertySection;
import gov.redhawk.model.sca.ScaDevice;

public class DcdAdvancedEditPartPropertySection extends AdvancedEditPartPropertySection {

	@Override
	protected Object getScaObjectForEditPart(EditPart ep) {
		if (ep.getModel() instanceof DeviceShape) {
			return Platform.getAdapterManager().getAdapter(ep, ScaDevice.class);
		}
		return super.getScaObjectForEditPart(ep);
	}
}
