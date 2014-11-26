/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.sad.graphiti.ui.diagram.features.custom;

import gov.redhawk.ide.sad.graphiti.ext.Event;
import gov.redhawk.ide.sad.graphiti.ext.impl.ComponentShapeImpl;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.impl.RemoveContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;

public class TerminateComponentFeature extends AbstractCustomFeature {

	/**
	 * @param fp
	 */
	public TerminateComponentFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Terminate";
	}

	@Override
	public String getDescription() {
		return "Hard terminate of the component from the model";
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		if (context.getPictogramElements()[0] instanceof ComponentShapeImpl) {
			return true;
		}
		return false;
	}

	@Override
	public void execute(ICustomContext context) {
		final ComponentShapeImpl componentShape = (ComponentShapeImpl) context.getPictogramElements()[0];

		// Changing this event is noticed by the GraphitiDiagramAdapter, which releases the component from the SAD model
		componentShape.setEvent(Event.TERMINATE);

		// Now we just need to remove the graphical representation of the component
		IRemoveContext rc = new RemoveContext(componentShape);
		IFeatureProvider featureProvider = getFeatureProvider();
		IRemoveFeature removeFeature = featureProvider.getRemoveFeature(rc);
		if (removeFeature != null) {
			removeFeature.remove(rc);
		}
	}

}
