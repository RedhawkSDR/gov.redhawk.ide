/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.graphiti.ui.properties;

import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;

import gov.redhawk.core.graphiti.ui.ext.RHContainerShape;
import gov.redhawk.core.graphiti.ui.util.DUtil;
import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;

/**
 * Selects only {@link ComponentInstantiation}s (representing a component/device/service) in design-time diagrams.
 * @see {@link PropertiesSection}
 */
public class ComponentInstantiationFilter extends AbstractPropertySectionFilter {

	@Override
	protected boolean accept(PictogramElement pictogramElement) {
		Diagram diagram = Graphiti.getPeService().getDiagramForPictogramElement(pictogramElement);
		return (pictogramElement instanceof RHContainerShape) && //
				DUtil.getBusinessObject(pictogramElement, ComponentInstantiation.class) != null && //
				!DUtil.isDiagramRuntime(diagram);
	}

}
