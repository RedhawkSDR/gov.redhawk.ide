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
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.algorithms.styles.StylesFactory;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
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
