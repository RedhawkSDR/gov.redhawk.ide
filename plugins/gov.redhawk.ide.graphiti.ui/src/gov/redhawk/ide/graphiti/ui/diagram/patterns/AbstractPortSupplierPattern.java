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
package gov.redhawk.ide.graphiti.ui.diagram.patterns;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.pattern.config.IPatternConfiguration;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaLayoutService;

import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

public abstract class AbstractPortSupplierPattern extends AbstractContainerPattern {
	// Shape size constants
	private static final int INNER_CONTAINER_SHAPE_TOP_PADDING = 20;
	private static final int INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING = 15;
	private static final int INNER_CONTAINER_SHAPE_TITLE_HORIZONTAL_PADDING = 60;
	private static final int PROVIDES_PORTS_LEFT_PADDING = 5;
	private static final int INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING = INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING + PROVIDES_PORTS_LEFT_PADDING;
	private static final int PORTS_CONTAINER_SHAPE_TOP_PADDING = 60;
	private static final int INNER_ROUNDED_RECTANGLE_TEXT_TOP_PADDING = 8;
	private static final int INNER_ROUNDED_RECTANGLE_LINE_Y = 28;
	private static final int INTERFACE_SHAPE_WIDTH = INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING + PROVIDES_PORTS_LEFT_PADDING;
	private static final int INTERFACE_SHAPE_HEIGHT = 10;
	private static final int ICON_IMAGE_LENGTH = 16;
	
	public AbstractPortSupplierPattern(IPatternConfiguration patternConfiguration) {
		super(patternConfiguration);
	}

	/**
	 * Layout children of component
	 */
	@Override
	public boolean layout(ILayoutContext context) {
		((RHContainerShape) context.getPictogramElement()).layout(getFeatureProvider());

		// Layout inner rounded rectangle
		RHContainerShape portSupplierShape = (RHContainerShape) context.getPictogramElement();
		layoutInnerShape(portSupplierShape);

		// something is always changing.
		return true;
	}

	@Override
	public boolean update(IUpdateContext context) {
		Reason updated = ((RHContainerShape) context.getPictogramElement()).update(context, this);

		// if we updated redraw
		if (updated.toBoolean()) {
			layoutPictogramElement(context.getPictogramElement());
		}

		return updated.toBoolean();
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {
		return ((RHContainerShape) context.getPictogramElement()).updateNeeded(context, this);
	}

	/**
	 * Provides list of UsesPortStubs (if applicable)
	 * @param obj
	 * @return
	 */
	public abstract EList<UsesPortStub> getUses(EObject obj);

	/**
	 * Provides list of ProvidesPortStubs (if applicable)
	 * @param obj
	 * @return
	 */
	public abstract EList<ProvidesPortStub> getProvides(EObject obj);

	/**
	 * Return the text for inner container
	 */
	protected Text getInnerText(RHContainerShape shape) {
		return (Text) DUtil.findFirstPropertyContainer(shape, RHContainerShapeImpl.GA_INNER_ROUNDED_RECTANGLE_TEXT);
	}

	/**
	 * Return the image for inner container
	 */
	protected Image getInnerImage(RHContainerShape shape) {
		return (Image) DUtil.findFirstPropertyContainer(shape, RHContainerShapeImpl.GA_INNER_ROUNDED_RECTANGLE_IMAGE);
	}

	/**
	 * Return the inner container polyline
	 */
	protected Polyline getInnerPolyline(RHContainerShape shape) {
		return (Polyline) DUtil.findFirstPropertyContainer(shape, RHContainerShapeImpl.GA_INNER_ROUNDED_RECTANGLE_LINE);
	}

	/**
	 * Return the innerContainerShape
	 */
	protected ContainerShape getInnerContainerShape(RHContainerShape shape) {
		return (ContainerShape) DUtil.findFirstPropertyContainer(shape, RHContainerShapeImpl.SHAPE_INNER_CONTAINER);
	}

	/**
	 * Return the lollipop container shape
	 * @return
	 */
	protected ContainerShape getLollipop(RHContainerShape shape) {
		return (ContainerShape) DUtil.findFirstPropertyContainer(shape, RHContainerShapeImpl.SHAPE_INTERFACE_CONTAINER);
	}

	protected boolean layoutInnerShape(RHContainerShape shape) {
		ContainerShape innerContainerShape = getInnerContainerShape(shape);
		IGaLayoutService gaLayoutService = Graphiti.getGaLayoutService();
		IDimension parentSize = gaLayoutService.calculateSize(innerContainerShape.getContainer().getGraphicsAlgorithm());
		int width = parentSize.getWidth() - INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING * 2 - PROVIDES_PORTS_LEFT_PADDING;
		int height = parentSize.getHeight() - INNER_CONTAINER_SHAPE_TOP_PADDING;
		gaLayoutService.setSize(innerContainerShape.getGraphicsAlgorithm(), width, height);
		IDimension innerRoundedRectangleTextSize = DUtil.calculateTextSize(getInnerText(shape));
		int xForImage = (innerContainerShape.getGraphicsAlgorithm().getWidth() - (innerRoundedRectangleTextSize.getWidth() + ICON_IMAGE_LENGTH + 5)) / 2;
		gaLayoutService.setLocationAndSize(getInnerImage(shape), xForImage, INNER_ROUNDED_RECTANGLE_TEXT_TOP_PADDING, ICON_IMAGE_LENGTH, ICON_IMAGE_LENGTH);
		gaLayoutService.setLocationAndSize(getInnerText(shape), xForImage + ICON_IMAGE_LENGTH + 5, INNER_ROUNDED_RECTANGLE_TEXT_TOP_PADDING,
			innerRoundedRectangleTextSize.getWidth() + 10, innerRoundedRectangleTextSize.getHeight());
		getInnerPolyline(shape).getPoints().get(1).setX(width);

		return true;
	}
}
