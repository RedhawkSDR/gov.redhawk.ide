/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package gov.redhawk.ide.graphiti.ui.diagram.features.custom;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;

import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;

/**
 * Performs the {@link #canExecute(ICustomContext)} for logging features.
 */
public abstract class AbstractLoggingFeature extends NonUndoableCustomFeature {

	public AbstractLoggingFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		// It only makes sense to allow the user to do this with one selected resource
		if (context.getPictogramElements().length != 1) {
			return false;
		}

		RHContainerShape componentShape = (RHContainerShape) context.getPictogramElements()[0];
		Object object = DUtil.getBusinessObject(componentShape);
		return (object instanceof ComponentInstantiation) && componentShape.isEnabled();

	}
}
