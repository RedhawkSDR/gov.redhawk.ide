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
package gov.redhawk.ide.graphiti.ui.diagram.features.custom;

import org.eclipse.core.runtime.Platform;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.model.sca.ScaAbstractComponent;
import gov.redhawk.model.sca.util.StartJob;
import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;

public class StartFeature extends NonUndoableCustomFeature {

	public StartFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Start";
	}

	@Override
	public String getDescription() {
		return "Start resource";
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		RHContainerShape shape = (RHContainerShape) context.getPictogramElements()[0];
		Object object = DUtil.getBusinessObject(shape);
		return (object instanceof ComponentInstantiation) && !shape.isStarted() && shape.isEnabled();
	}

	@Override
	public void execute(ICustomContext context) {
		for (PictogramElement pe : context.getPictogramElements()) {
			ScaAbstractComponent< ? > component = Platform.getAdapterManager().getAdapter(pe, ScaAbstractComponent.class);
			final StartJob job = new StartJob(component.identifier(), component);
			job.setUser(true);
			job.schedule();
		}
	}

	@Override
	public String getImageId() {
		return gov.redhawk.ide.graphiti.ui.diagram.providers.ImageProvider.IMG_START;
	}

}
