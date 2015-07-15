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
package gov.redhawk.ide.graphiti.ui.properties;

import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;

import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;
import org.eclipse.jface.viewers.IFilter;

public class AdvancedFilter extends CompoundFilter {

	public AdvancedFilter() {
		this(ComponentInstantiation.class);
	}

	public AdvancedFilter(Class< ? > objectClass) {
		super(CompoundFilter.BooleanOperator.FILTER_AND);
		CompoundFilter typeFilter = new CompoundFilter();
		typeFilter.addFilter(new BusinessObjectFilter(objectClass));
		typeFilter.addFilter(new ProvidesPortFilter());
		typeFilter.addFilter(new UsesPortFilter());
		IFilter runtimeFilter = new AbstractPropertySectionFilter() {
			@Override
			protected boolean accept(PictogramElement pictogramElement) {
				ContainerShape shape;
				if (pictogramElement instanceof ContainerShape) {
					shape = (ContainerShape) pictogramElement;
				} else {
					shape = (ContainerShape) pictogramElement.eContainer();
				}
				return DUtil.isDiagramRuntime(DUtil.findDiagram(shape));
			}
		};
		addFilter(runtimeFilter);
		addFilter(typeFilter);
	}

}
