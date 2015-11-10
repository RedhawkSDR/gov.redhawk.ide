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
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;

import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.sad.ext.ComponentShape;
import gov.redhawk.ide.graphiti.sad.ext.impl.ComponentShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;
import gov.redhawk.sca.util.PluginUtil;
import mil.jpeojtrs.sca.sad.AssemblyController;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

public class ComponentUtils { // SUPPRESS CHECKSTYLE INLINE

	public static void decrementStartOrder(SWTBotGefEditor editor, String componentName) {
		editor.select(componentName).clickContextMenu("Move Start Order Later");
	}

	public static int getStartOrder(SWTBotGefEditor editor, String component) {
		ComponentShape componentShape = (ComponentShape) editor.getEditPart(component).part().getModel();
		SadComponentInstantiation ci = (SadComponentInstantiation) DUtil.getBusinessObject(componentShape);
		return ci.getStartOrder().intValue();
	}

	public static void incrementStartOrder(SWTBotGefEditor editor, String componentName) {
		editor.select(componentName).clickContextMenu("Move Start Order Earlier");
	}

	public static boolean isAssemblyController(SWTGefBot bot, SWTBotGefEditor editor, String component) {
		ComponentShape assemblyController = (ComponentShape) editor.getEditPart(component).part().getModel();
		return ComponentUtils.isAssemblyController(assemblyController);
	}

	/**
	 * Determines whether the given component shape is the assembly controller of its waveform diagram
	 * @param componentShape
	 * @return
	 */
	public static boolean isAssemblyController(ComponentShape componentShape) {
		Diagram diagram = DUtil.findDiagram(componentShape);
		final SoftwareAssembly sad = DUtil.getBusinessObject(diagram, SoftwareAssembly.class);
		AssemblyController ac = sad.getAssemblyController();
		SadComponentInstantiation ci = (SadComponentInstantiation) DUtil.getBusinessObject(componentShape);
		if (ac != null && ac.getComponentInstantiationRef() != null) {
			return ac.getComponentInstantiationRef().getRefid().equals(ci.getId());
		}
		return false;
	}

	public static boolean isAssemblyController(SWTBotGefEditPart gefEditPart) {
		return ComponentUtils.isAssemblyController((ComponentShape) gefEditPart.part().getModel());
	}

	/**
	 * Set component as assembly controller via context menu option
	 * @param editor
	 * @param componentName
	 */
	public static void setAsAssemblyController(SWTBotGefEditor editor, String componentName) {
		editor.select(componentName).clickContextMenu("Set As Assembly Controller");
	}

	/**
	 * Check whether the component graphical start order text matches the expected value
	 * @param componentShape - Component to be check
	 * @param expectedValue - Expected value of start order
	 * @return - True if component start order text matches expected value
	 */
	public static boolean correctStartOrderValue(ComponentShape componentShape, String expectedValue) {
		return PluginUtil.equals(ComponentUtils.getStartOrderText(componentShape).getValue(), expectedValue);
	}

	/**
	 * Check whether the component graphical start order styling is set correctly
	 * @param componentShape - Component to be check
	 * @param isAssemblyController - boolean declaring whether assembly controller styling is expected
	 * @return - True if component styling is set correctly
	 */
	public static boolean correctStartOrderStyling(ComponentShape componentShape, boolean isAssemblyController) {
		GraphicsAlgorithm ga = ComponentUtils.getStartOrderEllipseShape(componentShape).getGraphicsAlgorithm();
		if (isAssemblyController) {
			return !StyleUtil.isStyleSet(ga, StyleUtil.ASSEMBLY_CONTROLLER_ELLIPSE);
		}
		return !StyleUtil.isStyleSet(ga, StyleUtil.START_ORDER_ELLIPSE);
	}

	/**
	 * Gets the interface lollipop shape for the given component
	 * @param componentShape - Component from which to get interface lollipop shape
	 * @return - the interface lollipop shape, if the Component has one, or null otherwise
	 */
	public static ContainerShape getLollipop(ComponentShape componentShape) {
		return (ContainerShape) DUtil.findFirstPropertyContainer(componentShape, RHContainerShapeImpl.SHAPE_INTERFACE_CONTAINER);
	}

	/**
	 * Gets the text from the outer shape (i.e., the component type) for the given component
	 * @param componentShape - Component from which to get outer text
	 * @return - the Component's outer text
	 */
	public static Text getOuterText(ComponentShape componentShape) {
		return (Text) DUtil.findFirstPropertyContainer(componentShape, RHContainerShapeImpl.GA_OUTER_ROUNDED_RECTANGLE_TEXT);
	}

	/**
	 * Gets the text from the inner shape (i.e., the instance name) for the given component
	 * @param componentShape - Component from which to get inner text
	 * @return - the Component's inner text
	 */
	public static Text getInnerText(ComponentShape componentShape) {
		return (Text) DUtil.findFirstPropertyContainer(componentShape, RHContainerShapeImpl.GA_INNER_ROUNDED_RECTANGLE_TEXT);
	}

	/**
	 * Gets the start order Text shape for the given component
	 * @param componentShape - Component from which to get start order Text
	 * @return - the start order Text, if the Component has one, or null otherwise
	 */
	public static Text getStartOrderText(ComponentShape componentShape) {
		return (Text) DUtil.findFirstPropertyContainer(componentShape, ComponentShapeImpl.GA_START_ORDER_TEXT);
	}

	/**
	 * Gets the start order ContainerShape (i.e., the ellipse) for the given component
	 * @param componentShape - Component from which to get start order shape
	 * @return - the start order shape, if the Component has one, or null otherwise
	 */
	public static ContainerShape getStartOrderEllipseShape(ComponentShape componentShape) {
		return (ContainerShape) DUtil.findFirstPropertyContainer(componentShape, ComponentShapeImpl.SHAPE_START_ORDER_ELLIPSE_SHAPE);
	}

	/**
	 * Check both the graphical start order text and styling of a component shape
	 * @param editor - SWTBotGefEditor
	 * @param component - Component name
	 * @param expectedNumber - Expected value of start order
	 * @param isAssemblyController - boolean declaring whether assembly controller styling is expected
	 * @return - True if component start order text matches expected value AND component styling is set correctly
	 */
	public static boolean correctStylingAndValue(SWTBotGefEditor editor, String component, String expectedValue, boolean isAssemblyController) {
		ComponentShape componentShape = (ComponentShape) editor.getEditPart(component).part().getModel();
		return ComponentUtils.correctStartOrderValue(componentShape, expectedValue)
			&& ComponentUtils.correctStartOrderStyling(componentShape, isAssemblyController);
	}

}
