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
package gov.redhawk.ide.sad.graphiti.ui.diagram.features.directedit;

import gov.redhawk.ide.sad.graphiti.ext.ComponentShape;
import gov.redhawk.ide.sad.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.AbstractContainerPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.providers.SADDiagramFeatureProvider;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public abstract class AbstractDirectEditFeature extends AbstractDirectEditingFeature {

	public AbstractDirectEditFeature(IFeatureProvider fp) {
		super(fp);
	}


	@Override
	public boolean canDirectEdit(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		ComponentShape componentShape = (ComponentShape) DUtil.findContainerShapeParentWithProperty(pe, RHContainerShapeImpl.SHAPE_OUTER_CONTAINER);
		Object obj = getBusinessObjectForPictogramElement(componentShape);
		GraphicsAlgorithm ga = context.getGraphicsAlgorithm();
		return isEditable(obj, ga);
	}
	
	/**
	 * Allow if we've selected Text for the component
	 * @param obj
	 * @param ga
	 * @return
	 */
	protected abstract boolean isEditable(Object obj, GraphicsAlgorithm ga);

	@Override
	public int getEditingType() {
		return TYPE_TEXT;
	}

	@Override
	public String getInitialValue(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		//TODO ComponentShape componentShape = (ComponentShape) DUtil.findContainerShapeParentWithProperty(pe, RHContainerShapeImpl.SHAPE_OUTER_CONTAINER);
		return getPattern(context).getInnerTitle((EObject) getBusinessObjectForPictogramElement(pe));
	}
	
	@Override
	public String checkValueValid(String value, IDirectEditingContext context) {
		return getPattern(context).checkValueValid(value, context);
	}

	@Override
	public void setValue(final String value, final IDirectEditingContext context) {
//		PictogramElement pe = context.getPictogramElement();
//		final ComponentShape componentShape = (ComponentShape) DUtil.findContainerShapeParentWithProperty(pe, RHContainerShapeImpl.SHAPE_OUTER_CONTAINER);
//		//TODO DELETE final SadComponentInstantiation ci = (SadComponentInstantiation) getBusinessObjectForPictogramElement(componentShape);
//
//		// editing domain for our transaction
//		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();
//
//		// Perform business object manipulation in a Command
//		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
//		stack.execute(new RecordingCommand(editingDomain) {
//
//			@Override
//			protected void doExecute() {
//				getPattern(context).setValue(value, context);
//				
//			}
//			
//		});

		getPattern(context).setValue(value, context);
		
		// perform update, redraw
		PictogramElement pe = context.getPictogramElement();
		updatePictogramElement((ComponentShape) DUtil.findContainerShapeParentWithProperty(pe, RHContainerShapeImpl.SHAPE_OUTER_CONTAINER));

	}
	
	/**
	 * TODO
	 * @param context
	 * @return
	 */
	protected AbstractContainerPattern getPattern(IDirectEditingContext context) {
		return (AbstractContainerPattern) ((SADDiagramFeatureProvider) this.getFeatureProvider())
				.getPatternForPictogramElement(context.getPictogramElement());
	}
}
