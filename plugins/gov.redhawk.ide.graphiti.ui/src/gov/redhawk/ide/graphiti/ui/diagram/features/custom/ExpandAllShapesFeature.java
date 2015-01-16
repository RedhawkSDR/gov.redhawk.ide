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

import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class ExpandAllShapesFeature extends AbstractCustomFeature {

	/**
	 * Constructor
	 */
	public ExpandAllShapesFeature(IFeatureProvider fp) {
		super(fp);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Expand All Shapes"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
	 */
	@Override
	public String getName() {
		return "&Expand All Shapes"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#canExecute(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public boolean canExecute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1 && pes[0] instanceof Diagram) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.ICustomFeature#execute(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public void execute(ICustomContext context) {
		//set preference for diagram
		final Diagram diagram = getDiagram();
		DUtil.setCreateRHContainerSuperPorts(diagram, false);
		DUtil.setCreateRHContainerPorts(diagram, true);
		
		//expand existing shapes in diagram
		for (PictogramElement p: diagram.getChildren()) { //TODO: need to handle inside host collocation
			RHContainerShape rhContainerShape = (RHContainerShape) p;
			rhContainerShape.setCreateSuperPortsContainerShape(false);
			rhContainerShape.setCreatePortsContainerShape(true);
			
			final UpdateContext updateContext = new UpdateContext(rhContainerShape);
			final IUpdateFeature updateFeature = getFeatureProvider().getUpdateFeature(updateContext);
			final IReason updateNeeded = updateFeature.updateNeeded(updateContext);
			if (updateNeeded.toBoolean()) {
				updateFeature.update(updateContext);
			}
			
			rhContainerShape.layout();
		}
		
		if (diagram != null && getFeatureProvider() != null) {
			final UpdateContext updateContext = new UpdateContext(diagram);
			final IUpdateFeature updateFeature = getFeatureProvider().getUpdateFeature(updateContext);
			updateFeature.update(updateContext);
		}
	}
}
