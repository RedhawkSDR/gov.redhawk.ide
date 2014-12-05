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
package gov.redhawk.ide.graphiti.sad.ui.diagram.util;

import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;

import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;

public class SadStyleUtil { // SUPPRESS CHECKSTYLE INLINE

	// updates component inner rectangle style
	public static Style createStyleForComponentInnerStarted(Diagram diagram) {
		final String styleId = "ComponentInnerStarted";
		Style style = StyleUtil.findStyle(diagram, styleId);
		if (style == null) {
			IGaService gaService = Graphiti.getGaService();
			style = gaService.createStyle(diagram, styleId);
			gaService.setRenderingStyle(style, ComponentColoredAreas.getGreenWhiteAdaptions());
			style.setLineWidth(2);
		}
		return style;
	}
	
	
}
