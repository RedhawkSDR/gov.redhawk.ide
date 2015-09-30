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

import gov.redhawk.ide.graphiti.sad.ext.ComponentShape;
import gov.redhawk.ide.graphiti.sad.ext.impl.ComponentShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;
import gov.redhawk.sca.util.PluginUtil;
import mil.jpeojtrs.sca.sad.AssemblyController;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;

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
		return isAssemblyController(assemblyController);
	}

	/**
	 * Determines whether the given component shape is the assembly controller of its waveform diagram
	 * @param componentShape
	 * @return
	 */
	public static boolean isAssemblyController(ComponentShape componentShape) {
		Diagram diagram = DUtil.findDiagram(componentShape);
		final SoftwareAssembly sad = (SoftwareAssembly) DUtil.getBusinessObject(diagram, SoftwareAssembly.class);
		AssemblyController ac = sad.getAssemblyController();
		SadComponentInstantiation ci = (SadComponentInstantiation) DUtil.getBusinessObject(componentShape);
		if (ac != null && ac.getComponentInstantiationRef() != null) {
			return ac.getComponentInstantiationRef().getRefid().equals(ci.getId());
		}
		return false;
	}
	
	public static boolean isAssemblyController(SWTBotGefEditPart gefEditPart) {
		return isAssemblyController((ComponentShape) gefEditPart.part().getModel());
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
		return PluginUtil.equals(((ComponentShapeImpl) componentShape).getStartOrderText().getValue(), expectedValue);
	}

	/**
	 * Check whether the component graphical start order styling is set correctly
	 * @param componentShape - Component to be check
	 * @param isAssemblyController - boolean declaring whether assembly controller styling is expected
	 * @return - True if component styling is set correctly
	 */
	public static boolean correctStartOrderStyling(ComponentShape componentShape, boolean isAssemblyController) {
		GraphicsAlgorithm ga = ((ComponentShapeImpl) componentShape).getStartOrderEllipseShape().getGraphicsAlgorithm();
		if (isAssemblyController) {
			return !StyleUtil.isStyleSet(ga, StyleUtil.ASSEMBLY_CONTROLLER_ELLIPSE);
		}
		return !StyleUtil.isStyleSet(ga, StyleUtil.START_ORDER_ELLIPSE);
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
		return correctStartOrderValue(componentShape, expectedValue) && correctStartOrderStyling(componentShape, isAssemblyController);
	}

}
