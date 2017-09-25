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
package gov.redhawk.ide.graphiti.dcd.ui.diagram.feature.custom;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;

/**
 * Implements {@link #isAvailable(IContext)} and ensures there is at least one object selected, and all object(s)
 * are of type {@link SadComponentInstantiation}.
 */
public abstract class AbstractComponentInstantiationFeature extends AbstractCustomFeature {

	public AbstractComponentInstantiationFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean isAvailable(IContext context) {
		if (!(context instanceof ICustomContext)) {
			return false;
		}
		ICustomContext customContext = (ICustomContext) context;

		// Must have 1+ items selected and they must all be component instantiations
		if (customContext.getPictogramElements().length == 0) {
			return false;
		}
		for (PictogramElement pe : customContext.getPictogramElements()) {
			Object businessObject = DUtil.getBusinessObject(pe);
			if (!(businessObject instanceof DcdComponentInstantiation)) {
				return false;
			}
		}
		return true;
	}
	
}
