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
package gov.redhawk.ide.graphiti.sad.ui.properties;

import gov.redhawk.ide.graphiti.sad.ext.ComponentShape;
import gov.redhawk.ide.graphiti.sad.ui.adapters.ComponentShapeAdapterFactory;
import gov.redhawk.ide.graphiti.ui.properties.AdvancedEditPartPropertySection;
import gov.redhawk.model.sca.ScaComponent;

import org.eclipse.gef.EditPart;

/**
 * 
 */
public class SadAdvancedEditPartPropertySection extends AdvancedEditPartPropertySection {

	@Override
	protected Object getScaObjectForEditPart(EditPart ep) {
		final Object obj = ep.getModel();
		if (obj instanceof ComponentShape) {
			return new ComponentShapeAdapterFactory().getAdapter(obj, ScaComponent.class);
		}
		return super.getScaObjectForEditPart(ep);
	}
	
}
