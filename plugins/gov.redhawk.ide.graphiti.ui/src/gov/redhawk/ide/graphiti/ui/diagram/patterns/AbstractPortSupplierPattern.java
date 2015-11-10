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
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.Shape;
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
	private static final int OUTER_CONTAINER_SHAPE_TITLE_HORIZONTAL_RIGHT_PADDING = 10;
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
	private static final int SUPER_PORT_SHAPE_HEIGHT = 25;
	private static final int SUPER_PORT_SHAPE_WIDTH = 10;
	private static final int SUPER_PORT_SHAPE_HEIGHT_MARGIN = 5;

	protected static final int PORT_ROW_PADDING_HEIGHT = 5;
	protected static final int REQ_PADDING_BETWEEN_PORT_TYPES = 10;

	public AbstractPortSupplierPattern(IPatternConfiguration patternConfiguration) {
		super(patternConfiguration);
	}

	/**
	 * Layout children of component
	 */
	@Override
	public boolean layout(ILayoutContext context) {
		boolean layoutApplied = false;

		RHContainerShape portSupplierShape = (RHContainerShape) context.getPictogramElement();
		int minimumHeight = 0;
		int minimumWidth = 0;
		if (portSupplierShape.isHasPortsContainerShape()) {
			// Show inner line
			getInnerPolyline(portSupplierShape).setLineVisible(true);

			// Layout provides ports
			ContainerShape providesPortsContainer = getProvidesPortsContainerShape(portSupplierShape);
			layoutProvidesPorts(providesPortsContainer);
			int providesHeight = providesPortsContainer.getGraphicsAlgorithm().getHeight();
			int providesWidth = providesPortsContainer.getGraphicsAlgorithm().getWidth();

			// Layout uses ports
			ContainerShape usesPortsContainer = getUsesPortsContainerShape(portSupplierShape);
			layoutUsesPorts(usesPortsContainer);
			adjustUsesPortsPosition(usesPortsContainer);
			int usesHeight = usesPortsContainer.getGraphicsAlgorithm().getHeight();
			int usesWidth = usesPortsContainer.getGraphicsAlgorithm().getWidth();

			// Account for port containers in outer sizing
			minimumHeight = Math.max(providesHeight, usesHeight) + 10;
			minimumWidth = providesWidth + usesWidth + AbstractPortSupplierPattern.REQ_PADDING_BETWEEN_PORT_TYPES;
		}

		// Resize height if necessary to accommodate contents (always requires padding)
		RoundedRectangle outerRoundedRectangle = (RoundedRectangle) portSupplierShape.getGraphicsAlgorithm();
		minimumHeight += PORTS_CONTAINER_SHAPE_TOP_PADDING;
		if (outerRoundedRectangle.getHeight() < minimumHeight) {
			outerRoundedRectangle.setHeight(minimumHeight);
		}
		// Resize width as well
		minimumWidth = Math.max(minimumWidth, getMinimumWidth(portSupplierShape));
		if (outerRoundedRectangle.getWidth() < minimumWidth) {
			outerRoundedRectangle.setWidth(minimumWidth);

			// If the width changes, move the uses ports container
			ContainerShape usesPortsContainer = getUsesPortsContainerShape(portSupplierShape);
			adjustUsesPortsPosition(usesPortsContainer);
		}

		// Outer text and image
		if (layoutOuterShape(portSupplierShape)) {
			layoutApplied = true;
		}

		// Layout inner rounded rectangle
		if (layoutInnerShape(portSupplierShape)) {
			layoutApplied = true;
		}

		if (portSupplierShape.isHasSuperPortsContainerShape()) {
			ContainerShape superProvidesPortsContainerShape = getSuperProvidesPortsContainerShape(portSupplierShape);
			if (superProvidesPortsContainerShape != null) {
				layoutSuperProvidesPorts(superProvidesPortsContainerShape);
			}

			ContainerShape superUsesPortsContainerShape = getSuperUsesPortsContainerShape(portSupplierShape);
			if (superUsesPortsContainerShape != null) {
				layoutSuperUsesPorts(superUsesPortsContainerShape);
			}

			// Hide inner line
			getInnerPolyline(portSupplierShape).setLineVisible(false);
		}

		return layoutApplied;
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

	protected int getMinimumWidth(RHContainerShape shape) {
		int innerWidth = getMinimumInnerWidth(shape);
		int outerWidth = getMinimumOuterWidth(shape);
		return Math.max(innerWidth,  outerWidth) + INTERFACE_SHAPE_WIDTH;
	}

	protected int getMinimumInnerWidth(RHContainerShape shape) {
		Text innerTitle = getInnerText(shape);
		IDimension innerTitleDimension = DUtil.calculateTextSize(innerTitle);
		return innerTitleDimension.getWidth() + INNER_CONTAINER_SHAPE_TITLE_HORIZONTAL_PADDING;
	}

	protected int getMinimumOuterWidth(RHContainerShape shape) {
		Text outerTitle = getOuterText(shape);
		IDimension outerTitleDimension = DUtil.calculateTextSize(outerTitle);
		return INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING + outerTitleDimension.getWidth() + OUTER_CONTAINER_SHAPE_TITLE_HORIZONTAL_RIGHT_PADDING + 4;
	}

	/**
	 * Return the text for outer container
	 */
	protected Text getOuterText(RHContainerShape shape) {
		return (Text) DUtil.findFirstPropertyContainer(shape, RHContainerShapeImpl.GA_OUTER_ROUNDED_RECTANGLE_TEXT);
	}

	/**
	 * Return the image for outer container
	 */
	protected Image getOuterImage(RHContainerShape shape) {
		return (Image) DUtil.findFirstPropertyContainer(shape, RHContainerShapeImpl.GA_OUTER_ROUNDED_RECTANGLE_IMAGE);
	}

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

	/**
	 * Return the usesPortsContainerShape
	 */
	protected ContainerShape getUsesPortsContainerShape(RHContainerShape shape) {
		return (ContainerShape) DUtil.findFirstPropertyContainer(shape, RHContainerShapeImpl.SHAPE_USES_PORTS_CONTAINER);
	}

	/**
	 * Return the providesPortsContainerShape
	 */
	protected ContainerShape getProvidesPortsContainerShape(RHContainerShape shape) {
		return (ContainerShape) DUtil.findFirstPropertyContainer(shape, RHContainerShapeImpl.SHAPE_PROVIDES_PORTS_CONTAINER);
	}
	/**
	 * Return Super Provides Ports ContainerShape
	 */
	protected ContainerShape getSuperProvidesPortsContainerShape(RHContainerShape shape) {
		return (ContainerShape) DUtil.findFirstPropertyContainer(shape, RHContainerShapeImpl.SUPER_PROVIDES_PORTS_RECTANGLE);
	}

	/**
	 * Return Super Uses Ports ContainerShape
	 */
	protected ContainerShape getSuperUsesPortsContainerShape(RHContainerShape shape) {
		return (ContainerShape) DUtil.findFirstPropertyContainer(shape, RHContainerShapeImpl.SUPER_USES_PORTS_RECTANGLE);
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

	protected boolean layoutOuterShape(RHContainerShape shape) {
		int containerWidth = shape.getGraphicsAlgorithm().getWidth();
		Text outerText = getOuterText(shape);
		boolean layoutApplied = DUtil.moveIfNeeded(outerText, INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING + ICON_IMAGE_LENGTH + 4, 0);
		if (DUtil.resizeIfNeeded(outerText, containerWidth - (INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING + ICON_IMAGE_LENGTH + 4), 20)) {
			layoutApplied = true;
		}
		Image outerImage = getOuterImage(shape);
		if (DUtil.moveIfNeeded(outerImage, INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING, 0)) {
			layoutApplied = true;
		}
		if (DUtil.resizeIfNeeded(outerImage, ICON_IMAGE_LENGTH, ICON_IMAGE_LENGTH)) {
			layoutApplied = true;
		}
		return layoutApplied;
	}

	protected boolean layoutProvidesPorts(ContainerShape providesPortsContainer) {
		boolean layoutApplied = false;
		int currentY = 0;
		int maxWidth = 0;
		for (Shape shape : providesPortsContainer.getChildren()) {
			if (DUtil.layoutShapeViaFeature(getFeatureProvider(), shape)) {
				layoutApplied = true;
			}

			// Place the container at the next Y position
			if (DUtil.moveIfNeeded(shape.getGraphicsAlgorithm(), 0, currentY)) {
				layoutApplied = true;
			}
			currentY += shape.getGraphicsAlgorithm().getHeight() + PORT_ROW_PADDING_HEIGHT;
			maxWidth = Math.max(maxWidth, shape.getGraphicsAlgorithm().getWidth());
		}
		// Resize container to contents and adjust position so that ports are aligned to the outer edge
		currentY = Math.max(currentY - 5, 0); // remove extra spacing, if it was added above
		if (DUtil.resizeIfNeeded(providesPortsContainer.getGraphicsAlgorithm(), maxWidth, currentY)) {
			layoutApplied = true;
		}
		// NB: For FindBy shapes and the like, the normal layout was not occurring for the provides port container
		if (DUtil.moveIfNeeded(providesPortsContainer.getGraphicsAlgorithm(), PROVIDES_PORTS_LEFT_PADDING, PORTS_CONTAINER_SHAPE_TOP_PADDING)) {
			layoutApplied = true;
		}
		return layoutApplied;
	}

	protected boolean layoutUsesPorts(ContainerShape usesPortsContainer) {
		boolean layoutApplied = false;
		int maxWidth = 0;
		// First pass: resize and layout contained ports, remembering max width
		for (Shape shape : usesPortsContainer.getChildren()) {
			if (DUtil.layoutShapeViaFeature(getFeatureProvider(), shape)) {
				layoutApplied = true;
			}
			maxWidth = Math.max(maxWidth, shape.getGraphicsAlgorithm().getWidth());
		}

		// Second pass: layout vertically and adjust X coordinates so that right edges line up (depends on max width)
		int currentY = 0;
		for (Shape shape : usesPortsContainer.getChildren()) {
			int xOffset = maxWidth - shape.getGraphicsAlgorithm().getWidth();
			if (DUtil.moveIfNeeded(shape.getGraphicsAlgorithm(), xOffset, currentY)) {
				layoutApplied = true;
			}
			currentY += shape.getGraphicsAlgorithm().getHeight() + PORT_ROW_PADDING_HEIGHT;
		}

		// Resize container to contents
		currentY = Math.max(currentY - 5, 0); // remove extra spacing, if it was added above
		if (DUtil.resizeIfNeeded(usesPortsContainer.getGraphicsAlgorithm(), maxWidth, currentY)) {
			layoutApplied = true;
		}
		return layoutApplied;
	}

	/**
	 * Adjusts the position of the uses ports container so that ports are aligned to the outer edge
	 */
	protected boolean adjustUsesPortsPosition(ContainerShape usesPortsContainer) {
		if (usesPortsContainer != null) {
			int parentWidth = usesPortsContainer.getContainer().getGraphicsAlgorithm().getWidth();
			int xOffset = parentWidth - usesPortsContainer.getGraphicsAlgorithm().getWidth();
			return DUtil.moveIfNeeded(usesPortsContainer.getGraphicsAlgorithm(), xOffset, PORTS_CONTAINER_SHAPE_TOP_PADDING);
		}
		return false;
	}

	private boolean layoutSuperPort(ContainerShape superPortContainerShape) {
		// Resize relative to inner shape
		RHContainerShape parent = (RHContainerShape) superPortContainerShape.getContainer();
		int height = getInnerContainerShape(parent).getGraphicsAlgorithm().getHeight() - SUPER_PORT_SHAPE_HEIGHT_MARGIN * 2;
		if (DUtil.resizeIfNeeded(superPortContainerShape.getGraphicsAlgorithm(), SUPER_PORT_SHAPE_WIDTH, height)) {
			layoutAnchor(superPortContainerShape);
			return true;
		}
		return false;
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

	protected void layoutSuperProvidesPorts(ContainerShape superProvidesPortsContainerShape) {
		layoutSuperPort(superProvidesPortsContainerShape);
	}

	protected void layoutSuperUsesPorts(ContainerShape superUsesPortsContainerShape) {
		layoutSuperPort(superUsesPortsContainerShape);

		// Position at right edge of inner shape
		RHContainerShape parent = (RHContainerShape) superUsesPortsContainerShape.getContainer();
		GraphicsAlgorithm innerGa = getInnerContainerShape(parent).getGraphicsAlgorithm();
		int y = innerGa.getY() + SUPER_PORT_SHAPE_HEIGHT_MARGIN;
		int x = innerGa.getX() + innerGa.getWidth();
		Graphiti.getGaLayoutService().setLocation(superUsesPortsContainerShape.getGraphicsAlgorithm(), x, y);
	}
}
