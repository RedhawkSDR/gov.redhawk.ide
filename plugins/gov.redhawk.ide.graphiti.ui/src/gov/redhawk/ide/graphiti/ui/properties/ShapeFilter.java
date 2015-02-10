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

import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;

/**
 * 
 */
public class ShapeFilter extends AbstractPropertySectionFilter {

	private Class< ? extends Shape > shapeClass;
	private boolean shouldAllow;
	
	public ShapeFilter(Class< ? extends Shape > filterClass, boolean allow) {
		super();
		shapeClass = filterClass;
		shouldAllow = allow;
	}
	
	public ShapeFilter(Class< ? extends Shape > filterClass) {
		this(filterClass, true);
	}
	
	@Override
	protected boolean accept(PictogramElement pictogramElement) {
		Object whatIsIt = DUtil.getBusinessObject(pictogramElement);
		if (whatIsIt instanceof ProvidesPortStub || whatIsIt instanceof UsesPortStub) {
			return false;
		}
		ContainerShape containerShape = (ContainerShape) DUtil.findContainerShapeParentWithProperty(pictogramElement,
			RHContainerShapeImpl.SHAPE_OUTER_CONTAINER);
		Object obj = DUtil.getBusinessObject(containerShape);
		if (shapeClass.isInstance(containerShape) && obj != null && obj instanceof ComponentInstantiation) {
			return shouldAllow;
		}
		return !shouldAllow;
	}

}
