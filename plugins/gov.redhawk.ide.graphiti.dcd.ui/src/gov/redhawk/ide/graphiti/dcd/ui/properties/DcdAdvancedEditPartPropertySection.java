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

import gov.redhawk.ide.graphiti.dcd.ext.DeviceShape;
import gov.redhawk.ide.graphiti.dcd.ui.adapters.DeviceShapeAdapterFactory;
import gov.redhawk.ide.graphiti.ui.properties.AdvancedEditPartPropertySection;
import gov.redhawk.model.sca.ScaComponent;

import org.eclipse.gef.EditPart;

/**
 * 
 */
public class DcdAdvancedEditPartPropertySection extends AdvancedEditPartPropertySection {

	@Override
	protected Object getScaObjectForEditPart(EditPart ep) {
		final Object obj = ep.getModel();
		if (obj instanceof DeviceShape) {
			return new DeviceShapeAdapterFactory().getAdapter(obj, ScaComponent.class);
		}
		return super.getScaObjectForEditPart(ep);
	}
}
