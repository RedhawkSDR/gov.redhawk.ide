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
package gov.redhawk.ide.sad.graphiti.ui.diagram.features.delete;

import gov.redhawk.ide.sad.graphiti.ui.diagram.providers.SADDiagramFeatureProvider;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.pattern.DeleteFeatureForPattern;
import org.eclipse.graphiti.pattern.IPattern;

public class SADDeleteFeatureForPattern extends DeleteFeatureForPattern {

	public SADDeleteFeatureForPattern(IFeatureProvider featureProvider, IPattern pattern) {
		super(featureProvider, pattern);
	}
	
	/**
	 * Determines the PictogramElement for which the delete feature is being requested
	 * @param context
	 * @return the proper delete context
	 */
	protected IDeleteContext getProperDeleteContext(IDeleteContext context) {
		return SADDiagramFeatureProvider.getProperDeleteContext(context, 
			getDiagramBehavior().getDiagramContainer().getSelectedPictogramElements());
	}

	@Override
	public boolean canDelete(IDeleteContext context) {
		return true;
	}

	@Override
	public void delete(IDeleteContext context) {
		setDoneChanges(false);
		super.delete(getProperDeleteContext(context));
		setDoneChanges(true);
	}

	@Override
	protected boolean getUserDecision(IDeleteContext context) {
		return super.getUserDecision(getProperDeleteContext(context));
	}

	@Override
	protected String getDeleteName(IDeleteContext context) {
		return super.getDeleteName(getProperDeleteContext(context));
	}
	
}
