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
package gov.redhawk.ide.sad.graphiti.ui.diagram.util;

import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;

public class PropertyUtil { //SUPPRESS CHECKSTYLE INLINE

	public static final String SHAPE_TYPE_KEY = "shapeType";
	public static final String COMPONENT_SHAPE_TYPE = "component";

	public static final void setComponentShape(PictogramElement pe) {
		Graphiti.getPeService().setPropertyValue(pe, SHAPE_TYPE_KEY, COMPONENT_SHAPE_TYPE);
	}

	public static boolean isComponentShape(PictogramElement pe) {
		return COMPONENT_SHAPE_TYPE.equals(Graphiti.getPeService().getPropertyValue(pe, SHAPE_TYPE_KEY));
	}
}
