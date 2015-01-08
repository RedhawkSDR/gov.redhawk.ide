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
package gov.redhawk.ide.graphiti.sad.ui.diagram.features.custom.runtime;

import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.sad.ext.ComponentShape;
import gov.redhawk.ide.graphiti.sad.ext.impl.ComponentShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.internal.parts.ContainerShapeEditPart;

@SuppressWarnings("restriction")
public class StopComponentFeature extends AbstractCustomFeature {
	
	@Override
	public String getDescription() {
		return "Stop component";
	}
	
	@Override
	public String getName() {
		return "Stop";
	}
	
	public StopComponentFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		ComponentShape componentShape = (ComponentShape) context.getPictogramElements()[0];
		Object object = DUtil.getBusinessObject(componentShape);
		Diagram diagram = DUtil.findDiagram((ContainerShape) componentShape);
		if (object instanceof SadComponentInstantiation
				&& componentShape.isStarted()
				&& (DUtil.isDiagramLocal(diagram) || DUtil.isDiagramTargetSdr(diagram))) {
			return true;
		}
		
		return super.canExecute(context);
	}
	
	@Override
	public void execute(ICustomContext context) {
		Object[] selection = DUtil.getSelectedEditParts();
		for (Object obj : selection) {
			if (obj instanceof ContainerShapeEditPart) {
				Object modelObj = ((ContainerShapeEditPart) obj).getModel();
				if (modelObj instanceof ComponentShapeImpl) {
					ComponentShapeImpl shape = (ComponentShapeImpl) modelObj;
					RoundedRectangle innerRoundedRectangle = (RoundedRectangle) DUtil.findFirstPropertyContainer(shape,
						RHContainerShapeImpl.GA_INNER_ROUNDED_RECTANGLE);
					innerRoundedRectangle.setStyle(StyleUtil.getStyleForComponentInner(getDiagram()));
					shape.setStarted(false);  //GraphitiModelMap is listening
				}
			}
		}
		// IDE-1021: Check context in case we were called by hover context pad button on unselected component
		for (PictogramElement pe: context.getPictogramElements()) {
			if (pe instanceof ComponentShapeImpl) {
				ComponentShapeImpl shape = (ComponentShapeImpl) pe;
				RoundedRectangle innerRoundedRectangle = (RoundedRectangle) DUtil.findFirstPropertyContainer(shape,
					RHContainerShapeImpl.GA_INNER_ROUNDED_RECTANGLE);
				innerRoundedRectangle.setStyle(StyleUtil.getStyleForComponentInner(getDiagram()));
				shape.setStarted(false);  //GraphitiModelMap is listening
			}
		}
	}

	@Override
	public String getImageId() {
		// TODO Auto-generated method stub
		return gov.redhawk.ide.graphiti.ui.diagram.providers.ImageProvider.IMG_STOP;
	}
}
