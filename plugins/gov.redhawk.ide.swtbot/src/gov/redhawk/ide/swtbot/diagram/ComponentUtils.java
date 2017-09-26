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

import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;

import gov.redhawk.core.graphiti.sad.ui.ext.ComponentShape;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

public class ComponentUtils { // SUPPRESS CHECKSTYLE INLINE

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
		SadComponentInstantiation ci = DUtil.getBusinessObject(componentShape, SadComponentInstantiation.class);
		if (ci == null) {
			return false;
		}
		return SoftwareAssembly.Util.isAssemblyController(ci);
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
	 * Gets the interface lollipop shape for the given component
	 * @param componentShape - Component from which to get interface lollipop shape
	 * @return - the interface lollipop shape, if the Component has one, or null otherwise
	 */
	public static ContainerShape getLollipop(ComponentShape componentShape) {
		return componentShape.getLollipop();
	}

	/**
	 * Gets the text from the outer shape (i.e., the component type) for the given component
	 * @param componentShape - Component from which to get outer text
	 * @return - the Component's outer text
	 */
	public static Text getOuterText(ComponentShape componentShape) {
		return componentShape.getOuterText();
	}

	/**
	 * Gets the text from the inner shape (i.e., the instance name) for the given component
	 * @param componentShape - Component from which to get inner text
	 * @return - the Component's inner text
	 */
	public static Text getInnerText(ComponentShape componentShape) {
		return componentShape.getInnerText();
	}
}
