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

import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.algorithms.styles.StylesFactory;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.pattern.AbstractPattern;
import org.eclipse.graphiti.services.Graphiti;

import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;

abstract class AbstractPortPattern< E > extends AbstractPattern {

	protected static final int PORT_SHAPE_HEIGHT = 15;
	protected static final int PORT_SHAPE_WIDTH = AbstractPortPattern.PORT_SHAPE_HEIGHT;

	private Class<E> clazz;

	protected AbstractPortPattern(Class<E> clazz) {
		this.clazz = clazz;
	}

	@Override
	public boolean isMainBusinessObjectApplicable(Object mainBusinessObject) {
		return clazz.isInstance(mainBusinessObject);
	}

	@Override
	protected boolean isPatternControlled(PictogramElement pictogramElement) {
		return getPortContainerShapeId().equals(Graphiti.getPeService().getPropertyValue(pictogramElement, DUtil.SHAPE_TYPE));
	}

	@Override
	protected boolean isPatternRoot(PictogramElement pictogramElement) {
		return false;
	}

	@Override
	public boolean update(IUpdateContext context) {
		boolean updateStatus = false;
		ContainerShape usesPortShape = (ContainerShape) context.getPictogramElement();
		E port = clazz.cast(getBusinessObjectForPictogramElement(usesPortShape));
		Text usesPortText = getPortText(usesPortShape);
		String name = getPortName(port);
		if (!name.equals(usesPortText.getValue())) {
			usesPortText.setValue(name);
			updateStatus = true;
		}
		Rectangle portRectangle = getPortRectangle(usesPortShape);
		String styleId = getStyleId(port);
		if (!styleId.equals(portRectangle.getStyle().getId())) {
			StyleUtil.setStyle(portRectangle, styleId);
			updateStatus = true;
		}
		return updateStatus;
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {
		ContainerShape usesPortShape = (ContainerShape) context.getPictogramElement();
		E port = clazz.cast(getBusinessObjectForPictogramElement(usesPortShape));
		Text usesPortText = getPortText(usesPortShape);
		String name = getPortName(port);
		if (!name.equals(usesPortText.getValue())) {
			return Reason.createTrueReason("Uses port name needs update");
		}
		Rectangle portRectangle = getPortRectangle(usesPortShape);
		String styleId = getStyleId(port);
		if (!styleId.equals(portRectangle.getStyle().getId())) {
			return Reason.createTrueReason("Uses port style needs update");
		}
		return Reason.createFalseReason();
	}

	@Override
	public boolean canAdd(IAddContext context) {
		return isMainBusinessObjectApplicable(context.getNewObject());
	}

	@Override
	public PictogramElement add(IAddContext context) {
		ContainerShape parentShape = context.getTargetContainer();
		E portStub = clazz.cast(context.getNewObject());

		// Outer invisible container
		ContainerShape portContainerShape = Graphiti.getCreateService().createContainerShape(parentShape, true);
		Graphiti.getPeService().setPropertyValue(portContainerShape, DUtil.SHAPE_TYPE, getPortContainerShapeId());
		Rectangle providesPortContainerShapeRectangle = Graphiti.getCreateService().createPlainRectangle(portContainerShape);
		providesPortContainerShapeRectangle.setFilled(false);
		providesPortContainerShapeRectangle.setLineVisible(false);
		link(portContainerShape, portStub);

		// Port rectangle; this is created as its own shape because Anchors do not support decorators (for things
		// like highlighting)
		ContainerShape portShape = Graphiti.getPeService().createContainerShape(portContainerShape, true);
		Graphiti.getPeService().setPropertyValue(portShape, DUtil.SHAPE_TYPE, getPortRectangleShapeId());
		Rectangle providesPortRectangle = Graphiti.getCreateService().createPlainRectangle(portShape);
		StyleUtil.setStyle(providesPortRectangle, getStyleId(portStub));
		Graphiti.getGaLayoutService().setSize(providesPortRectangle, AbstractPortPattern.PORT_SHAPE_WIDTH, AbstractPortPattern.PORT_SHAPE_HEIGHT);
		link(portShape, portStub);

		// Port anchor
		Orientation orientation = getPortOrientation();
		int anchorX;
		if (Orientation.ALIGNMENT_LEFT.equals(orientation)) {
			anchorX = 0;
		} else {
			anchorX = AbstractPortPattern.PORT_SHAPE_WIDTH;
		}
		FixPointAnchor fixPointAnchor = createPortAnchor(portShape, anchorX);
		link(fixPointAnchor, portStub);

		// Port text
		Shape portTextShape = Graphiti.getPeService().createShape(portContainerShape, false);
		Text portText = Graphiti.getCreateService().createPlainText(portTextShape, getPortName(portStub));
		StyleUtil.setStyle(portText, StyleUtil.PORT_TEXT);
		portText.setHorizontalAlignment(orientation);
		// Based on orientation, set X position of text relative to port
		int textX;
		if (Orientation.ALIGNMENT_LEFT.equals(orientation)) {
			textX = AbstractPortPattern.PORT_SHAPE_WIDTH + RHContainerShapeImpl.PORT_NAME_HORIZONTAL_PADDING;
		} else {
			textX = 0;
		}
		Graphiti.getGaLayoutService().setLocation(portText, textX, 0);

		return portContainerShape;
	}

	protected abstract String getPortContainerShapeId();

	protected abstract String getPortRectangleShapeId();

	protected abstract Orientation getPortOrientation();

	protected abstract String getStyleId(E port);

	protected abstract String getPortName(E port);

	protected Text getPortText(ContainerShape portContainerShape) {
		return (Text) portContainerShape.getChildren().get(1).getGraphicsAlgorithm();
	}

	protected Rectangle getPortRectangle(ContainerShape portContainerShape) {
		return (Rectangle) portContainerShape.getChildren().get(0).getGraphicsAlgorithm();
	}

	/**
	 * Create an anchor overlay for a port, with the anchor point vertically centered at horizontal position x.
	 * The returned anchor has an invisible rectangle for its graphics algorithm.
	 */
	protected FixPointAnchor createPortAnchor(ContainerShape portShape, int x) {
		FixPointAnchor fixPointAnchor = createOverlayAnchor(portShape, x);
		Rectangle fixPointAnchorRectangle = Graphiti.getCreateService().createPlainRectangle(fixPointAnchor);
		Graphiti.getPeService().setPropertyValue(fixPointAnchorRectangle, DUtil.GA_TYPE, RHContainerShapeImpl.GA_FIX_POINT_ANCHOR_RECTANGLE);
		fixPointAnchorRectangle.setFilled(false);
		fixPointAnchorRectangle.setLineVisible(false);
		layoutAnchor(portShape);
		return fixPointAnchor;
	}

	/**
	 * Create an anchor overlay for a shape, with the anchor point vertically centered at horizontal position x.
	 * The returned anchor has no graphics algorithm.
	 */
	private FixPointAnchor createOverlayAnchor(Shape parentShape, int x) {
		FixPointAnchor fixPointAnchor = Graphiti.getCreateService().createFixPointAnchor(parentShape);
		IDimension parentSize = Graphiti.getGaLayoutService().calculateSize(parentShape.getGraphicsAlgorithm());
		Point point = StylesFactory.eINSTANCE.createPoint();
		point.setX(x);
		point.setY(parentSize.getHeight() / 2);
		fixPointAnchor.setLocation(point);
		fixPointAnchor.setUseAnchorLocationAsConnectionEndpoint(true);
		fixPointAnchor.setReferencedGraphicsAlgorithm(parentShape.getGraphicsAlgorithm());
		return fixPointAnchor;
	}

	private void layoutAnchor(Shape parentShape) {
		// Layout and resize anchor
		IDimension parentSize = Graphiti.getGaLayoutService().calculateSize(parentShape.getGraphicsAlgorithm());
		FixPointAnchor portAnchor = (FixPointAnchor) parentShape.getAnchors().get(0);
		Point anchorLocation = portAnchor.getLocation();
		anchorLocation.setY(parentSize.getHeight() / 2);
		Graphiti.getGaLayoutService().setLocationAndSize(portAnchor.getGraphicsAlgorithm(), -anchorLocation.getX(), -anchorLocation.getY(),
			parentSize.getWidth(), parentSize.getHeight());
	}
}
