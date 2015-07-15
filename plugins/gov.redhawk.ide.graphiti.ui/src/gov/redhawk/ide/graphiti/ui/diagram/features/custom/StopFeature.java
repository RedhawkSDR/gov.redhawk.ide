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
package gov.redhawk.ide.graphiti.ui.diagram.features.custom;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.internal.parts.ContainerShapeEditPart;

import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;
import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;

@SuppressWarnings("restriction")
public class StopFeature extends NonUndoableCustomFeature {

	public StopFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Stop";
	}

	@Override
	public String getDescription() {
		return "Stop resource";
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		RHContainerShape shape = (RHContainerShape) context.getPictogramElements()[0];
		Object object = DUtil.getBusinessObject(shape);
		if (object instanceof ComponentInstantiation && shape.isStarted()) {
			return true;
		}

		return super.canExecute(context);
	}

	@Override
	public void execute(ICustomContext context) {
		// IDE-1021: Check context in case we were called by hover context pad button on unselected component
		boolean executed = false;
		for (PictogramElement pe : context.getPictogramElements()) {
			if (pe instanceof RHContainerShape) {
				RHContainerShape shape = (RHContainerShape) pe;
				RoundedRectangle innerRoundedRectangle = (RoundedRectangle) DUtil.findFirstPropertyContainer(shape,
					RHContainerShapeImpl.GA_INNER_ROUNDED_RECTANGLE);
				innerRoundedRectangle.setStyle(StyleUtil.createStyleForComponentInnerStarted(getDiagram()));
				shape.setStarted(false); // GraphitiModelMap is listening
				executed = true;
			}
		}
		if (executed) {
			// Don't process selection if called from button pad
			return;
		}
		Object[] selection = DUtil.getSelectedEditParts();
		for (Object obj : selection) {
			if (obj instanceof ContainerShapeEditPart) {
				Object modelObj = ((ContainerShapeEditPart) obj).getModel();
				if (modelObj instanceof RHContainerShape) {
					RHContainerShape shape = (RHContainerShape) modelObj;
					RoundedRectangle innerRoundedRectangle = (RoundedRectangle) DUtil.findFirstPropertyContainer(shape,
						RHContainerShapeImpl.GA_INNER_ROUNDED_RECTANGLE);
					innerRoundedRectangle.setStyle(StyleUtil.getStyleForComponentInner(getDiagram()));
					shape.setStarted(false); // GraphitiModelMap is listening
				}
			}
		}
	}

	@Override
	public String getImageId() {
		return gov.redhawk.ide.graphiti.ui.diagram.providers.ImageProvider.IMG_STOP;
	}

}
