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
package gov.redhawk.ide.swtbot.diagram;

import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;

import gov.redhawk.core.graphiti.ui.ext.RHContainerShape;
import gov.redhawk.core.graphiti.ui.util.StyleUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.sca.util.PluginUtil;
import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;

public class StartOrderUtils { // SUPPRESS CHECKSTYLE INLINE
	public static int getStartOrder(SWTBotGefEditor editor, String resource) {
		RHContainerShape containerShape = (RHContainerShape) editor.getEditPart(resource).part().getModel();
		ComponentInstantiation ci = (ComponentInstantiation) DUtil.getBusinessObject(containerShape);
		return ci.getStartOrder().intValue();
	}

	public static void moveStartOrderEarlier(SWTBotGefEditor editor, String resourceName) {
		editor.select(resourceName).clickContextMenu("Move Start Order Earlier");
	}

	public static void moveStartOrderLater(SWTBotGefEditor editor, String resourceName) {
		editor.select(resourceName).clickContextMenu("Move Start Order Later");
	}

	/**
	 * Check whether the graphical start order text matches the expected value
	 * @param containerShape - Container to be check
	 * @param expectedValue - Expected value of start order
	 * @return - True if start order text matches expected value
	 */
	public static boolean correctStartOrderValue(RHContainerShape containerShape, String expectedValue) {
		return PluginUtil.equals(getStartOrderText(containerShape).getValue(), expectedValue);
	}

	/**
	 * Check whether the graphical start order styling is set correctly
	 * @param containerShape - Container to be check
	 * @param isAssemblyController - boolean declaring whether assembly controller styling is expected
	 * @return - True if styling is set correctly
	 */
	public static boolean correctStartOrderStyling(RHContainerShape containerShape, boolean isAssemblyController) {
		GraphicsAlgorithm ga = getStartOrderEllipseShape(containerShape).getGraphicsAlgorithm();
		if (isAssemblyController) {
			return StyleUtil.isStyleSet(ga, StyleUtil.ASSEMBLY_CONTROLLER_ELLIPSE);
		}
		return StyleUtil.isStyleSet(ga, StyleUtil.START_ORDER_ELLIPSE);
	}

	/**
	 * Check both the graphical start order text and styling of a RHContainerShape
	 * @param editor - SWTBotGefEditor
	 * @param resource - Resource name
	 * @param expectedNumber - Expected value of start order
	 * @param isAssemblyController - boolean declaring whether assembly controller styling is expected
	 * @return - True if resource start order text matches expected value AND styling is set correctly
	 */
	public static boolean correctStartOrderStylingAndValue(SWTBotGefEditor editor, String resource, String expectedValue, boolean isAssemblyController) {
		RHContainerShape containerShape = (RHContainerShape) editor.getEditPart(resource).part().getModel();
		return correctStartOrderValue(containerShape, expectedValue) && correctStartOrderStyling(containerShape, isAssemblyController);
	}

	/**
	 * Gets the start order Text shape for the given RHContainerShape
	 * @param containerShape - Container from which to get start order Text
	 * @return - the start order Text, if the RHContainerShape has one, or null otherwise
	 */
	public static Text getStartOrderText(RHContainerShape containerShape) {
		return containerShape.getStartOrderText();
	}

	/**
	 * Gets the start order ContainerShape (i.e., the ellipse) for the given RHContainerShape
	 * @param containerShape - Container from which to get start order shape
	 * @return - the start order shape, if the RHContainerShape has one, or null otherwise
	 */
	public static ContainerShape getStartOrderEllipseShape(RHContainerShape containerShape) {
		return containerShape.getStartOrderEllipseShape();
	}
}
