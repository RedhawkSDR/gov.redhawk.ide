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
package gov.redhawk.ide.graphiti.ui.diagram.util;

import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;

/**
 * Utility methods for doing diagram updates and layouts.
 */
public class UpdateUtil {

	private UpdateUtil() {
	}

	/**
	 * Updates the value of a {@link Text}, if necessary.
	 * 
	 * @param text the Text instance to update
	 * @param value the new String value to set
	 * @return true if text was updated, false otherwise
	 */
	public static boolean update(Text text, String value) {
		if (UpdateUtil.updateNeeded(text, value)) {
			text.setValue(value);
			return true;
		}
		return false;
	}

	/**
	 * Checks if a {@link Text} has the desired value.
	 * 
	 * @param text the Text instance to check
	 * @param value the new String value to check
	 * @return true if text requires an update, false otherwise
	 */
	public static boolean updateNeeded(Text text, String value) {
		if (text != null) {
			return !text.getValue().equals(value);
		}
		return false;
	}

	/**
	 * Performs a layout on the overlaid {@link FixPointAnchor} for a shape so that it has the same dimensions.
	 * 
	 * @param parentShape
	 * @return true if the anchor was update, false if it already matched the shape
	 */
	public static boolean layoutOverlayAnchor(Shape parentShape) {
		// Layout and resize anchor
		IDimension parentSize = Graphiti.getGaLayoutService().calculateSize(parentShape.getGraphicsAlgorithm());
		FixPointAnchor portAnchor = (FixPointAnchor) parentShape.getAnchors().get(0);
		Point anchorLocation = portAnchor.getLocation();
		int anchorY = parentSize.getHeight() / 2;
		boolean modified = false;
		if (anchorLocation.getY() != anchorY) {
			anchorLocation.setY(anchorY);
			modified = true;
		}
		if (UpdateUtil.moveAndResizeIfNeeded(portAnchor.getGraphicsAlgorithm(), -anchorLocation.getX(), -anchorLocation.getY(),
			parentSize.getWidth(), parentSize.getHeight())) {
			modified = true;
		}
		return modified;
	}

	/**
	 * Moves and resizes a {@link GraphicsAlgorithm} if it is not already at the requested location, or does not
	 * already match the requested size. Returns true if the GraphicsAlgorithm was moved or resized.
	 * 
	 * @param ga target GraphicsAlgorithm
	 * @param x requested x position
	 * @param y requested y position
	 * @param y requested height
	 * @param width requested width
	 * @param height requested height
	 * @return true if ga was moved or resized, false otherwise
	 */
	public static boolean moveAndResizeIfNeeded(GraphicsAlgorithm ga, int x, int y, int width, int height) {
		if ((ga.getX() != x) || (ga.getY() != y) || (ga.getWidth() != width) || (ga.getHeight() != height)) {
			Graphiti.getGaLayoutService().setLocationAndSize(ga, x, y, width, height);
			return true;
		}
		return false;
	}

	/**
	 * Resizes a {@link GraphicsAlgorithm} if it does not already match the requested size, returning whether the
	 * resize occurred or not.
	 * 
	 * @param ga target GraphicsAlgorithm
	 * @param width requested width
	 * @param height requested height
	 * @return true if ga was resized, false if it was already the requested size
	 */
	public static boolean resizeIfNeeded(GraphicsAlgorithm ga, int width, int height) {
		if ((ga.getWidth() != width) || (ga.getHeight() != height)) {
			Graphiti.getGaLayoutService().setSize(ga, width, height);
			return true;
		}
		return false;
	}

	/**
	 * Moves a {@link GraphicsAlgorithm} if it is not already at the requested location, returning whether the move
	 * occurred or not.
	 * 
	 * @param ga target GraphicsAlgorithm
	 * @param x requested x position
	 * @param y requested y position
	 * @return true if ga was move, false if it was already at the requested location
	 */
	public static boolean moveIfNeeded(GraphicsAlgorithm ga, int x, int y) {
		if ((ga.getX() != x) || (ga.getY() != y)) {
			Graphiti.getGaLayoutService().setLocation(ga, x, y);
			return true;
		}
		return false;
	}
}
