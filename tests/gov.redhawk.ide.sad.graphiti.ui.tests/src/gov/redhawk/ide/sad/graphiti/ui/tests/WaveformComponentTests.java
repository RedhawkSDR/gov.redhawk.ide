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
package gov.redhawk.ide.sad.graphiti.ui.tests;

import gov.redhawk.ide.sad.graphiti.ext.impl.ComponentShapeImpl;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.tests.editor.EditorUtils;
import gov.redhawk.ide.swtbot.tests.waveform.CreateNewWaveform;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

import org.eclipse.graphiti.ui.internal.parts.ContainerShapeEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class WaveformComponentTests {

	private static SWTGefBot bot;
	private static SWTBotGefEditor editor;
	private static final String WAVEFORM_NAME = "IDE-726-Test";
	private static final String COMPONENT_NAME = "HardLimit";

	@BeforeClass
	public static void beforeClass() {
		bot = new SWTGefBot();
	}

	/**
	 * IDE-726
	 * Create the pictogram shape in the waveform diagram that represents the component business object.
	 * This includes the ContainerShape for the component, labels for Usage Name and ID, port shapes and labels,
	 * start order icon, and component supported interface.
	 */
	@Test
	public void checkComponentPictogram() {
		// Create an empty waveform project
		CreateNewWaveform.createNewWaveform(bot, WAVEFORM_NAME);

		// Add component to diagram from palette
		editor = bot.gefEditor(WAVEFORM_NAME);
		EditorUtils.dragFromPaletteToDiagram(editor, COMPONENT_NAME, 0, 0);

		// Drill down to graphiti component shape
		SWTBotGefEditPart editPart = editor.getEditPart(COMPONENT_NAME);
		ContainerShapeEditPart part = (ContainerShapeEditPart) editPart.part();
		ComponentShapeImpl componentShape = (ComponentShapeImpl) part.getPictogramElement();

		// Grab the associated business object and confirm it is a SadComponentInstantiation
		Object bo = DUtil.getBusinessObject(componentShape);
		Assert.assertTrue(bo instanceof SadComponentInstantiation);
		SadComponentInstantiation ci = (SadComponentInstantiation) bo;

		// Run assertions on expected properties
		Assert.assertEquals(COMPONENT_NAME, componentShape.getOuterText().getValue()); // assert outer text matches component type
		Assert.assertEquals(ci.getUsageName(), componentShape.getInnerText().getValue()); // assert inner text matches component usage name
		Assert.assertNotNull(componentShape.getLollipop()); // assert component supported interface graphic was added
		Assert.assertNotNull(componentShape.getStartOrderText()); // assert that start order shape was created

		// HardLimit only has the two ports
		Assert.assertTrue(componentShape.getUsesPortStubs().size() == 1 && componentShape.getProvidesPortStubs().size() == 1);
		// Both ports are of type dataDouble
		Assert.assertEquals(componentShape.getUsesPortStubs().get(0).getUses().getInterface().getName(), "dataDouble");
		Assert.assertEquals(componentShape.getProvidesPortStubs().get(0).getProvides().getInterface().getName(), "dataDouble");
	}

}
