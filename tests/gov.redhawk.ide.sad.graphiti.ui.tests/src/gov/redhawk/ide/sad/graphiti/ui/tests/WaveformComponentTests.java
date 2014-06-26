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
import gov.redhawk.ide.swtbot.tests.menus.MenuUtils;
import gov.redhawk.ide.swtbot.tests.waveform.CreateNewWaveform;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class WaveformComponentTests {

	private static SWTGefBot bot;
	private static SWTBotGefEditor editor;
	private static final String WAVEFORM_NAME = "IDE-726-Test";
	private static final String COMPONENT_NAME = "HardLimit";
	private static final String WAVEFORM_AC_NAME = "IDE-680-Test";


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
	public void checkComponentPictogramElements() {
		// Create an empty waveform project
		CreateNewWaveform.createNewWaveform(bot, WAVEFORM_NAME);

		// Add component to diagram from palette
		editor = bot.gefEditor(WAVEFORM_NAME);
		EditorUtils.dragFromPaletteToDiagram(editor, COMPONENT_NAME, 0, 0);

		// Drill down to graphiti component shape
		SWTBotGefEditPart gefEditPart = editor.getEditPart(COMPONENT_NAME);
		ComponentShapeImpl componentShape = (ComponentShapeImpl) gefEditPart.part().getModel();

		// Grab the associated business object and confirm it is a SadComponentInstantiation
		Object bo = DUtil.getBusinessObject(componentShape);
		Assert.assertTrue("business object should be of type SadComponentInstantiation", bo instanceof SadComponentInstantiation);
		SadComponentInstantiation ci = (SadComponentInstantiation) bo;

		// Run assertions on expected properties
		Assert.assertEquals("outer text should match component type", COMPONENT_NAME, componentShape.getOuterText().getValue());  
		Assert.assertEquals("inner text should match component usage name", ci.getUsageName(), componentShape.getInnerText().getValue());
		Assert.assertNotNull("component supported interface graphic should not be null", componentShape.getLollipop());
		Assert.assertNotNull("start order shape/text should not be null", componentShape.getStartOrderText());

		// HardLimit only has the two ports
		Assert.assertTrue(componentShape.getUsesPortStubs().size() == 1 && componentShape.getProvidesPortStubs().size() == 1);
		// Both ports are of type dataDouble
		Assert.assertEquals(componentShape.getUsesPortStubs().get(0).getUses().getInterface().getName(), "dataDouble");
		Assert.assertEquals(componentShape.getProvidesPortStubs().get(0).getProvides().getInterface().getName(), "dataDouble");
		
		MenuUtils.closeAllWithoutSave(bot);
		MenuUtils.deleteNodeInProjectExplorer(bot, WAVEFORM_NAME);
	}

	@AfterClass
	public static void cleanUp() {
		bot.sleep(5000);
	}

	/**
	 * IDE-680
	 * When using the New Waveform Project wizard, if the user selects an Assembly Controller as part of the wizard 
	 * then the component will not appear in the resulting diagram. The component is added to the sad.xml, though it 
	 * does not have a start order attribute.
	 */
	@Test
	public void checkComponentPictogramElementsWithAssemblyController() {
		CreateNewWaveform.createNewWaveformWithAssemblyController(bot, WAVEFORM_AC_NAME, COMPONENT_NAME);
		// Add component to diagram from palette
		editor = bot.gefEditor(WAVEFORM_AC_NAME);

		// Drill down to graphiti component shape
		SWTBotGefEditPart gefEditPart = editor.getEditPart(COMPONENT_NAME);
		ComponentShapeImpl componentShape = (ComponentShapeImpl) gefEditPart.part().getModel();

		// Grab the associated business object and confirm it is a SadComponentInstantiation
		Object bo = DUtil.getBusinessObject(componentShape);
		Assert.assertTrue("business object should be of type SadComponentInstantiation", bo instanceof SadComponentInstantiation);
		SadComponentInstantiation ci = (SadComponentInstantiation) bo;

		// Run assertions on expected properties
		Assert.assertEquals("outer text should match component type", COMPONENT_NAME, componentShape.getOuterText().getValue());  
		Assert.assertEquals("inner text should match component usage name", ci.getUsageName(), componentShape.getInnerText().getValue());
		Assert.assertNotNull("component supported interface graphic should not be null", componentShape.getLollipop());
		Assert.assertNotNull("start order shape/text should not be null", componentShape.getStartOrderText());
		
		// TODO check if assembly controller 
		// ? componentShape.getStartOrderEllipseShape().getGraphicsAlgorithm().getBackground().equals(...)

		// HardLimit only has the two ports
		Assert.assertTrue(componentShape.getUsesPortStubs().size() == 1 && componentShape.getProvidesPortStubs().size() == 1);
		// Both ports are of type dataDouble
		Assert.assertEquals(componentShape.getUsesPortStubs().get(0).getUses().getInterface().getName(), "dataDouble");
		Assert.assertEquals(componentShape.getProvidesPortStubs().get(0).getProvides().getInterface().getName(), "dataDouble");

		MenuUtils.closeAllWithoutSave(bot);
		MenuUtils.deleteNodeInProjectExplorer(bot, WAVEFORM_AC_NAME);
	}

//	@Test
//	public void makeConnectionBetweenPictogramElements() {
//		// Create an empty waveform project
//		CreateNewWaveform.createNewWaveform(bot, WAVEFORM_NAME);
//
//		// Add component to diagram from palette
//		editor = bot.gefEditor(WAVEFORM_NAME);
//		EditorUtils.dragFromPaletteToDiagram(editor, COMPONENT_NAME, 0, 0);
//
//		// Drill down to graphiti component shape
//		SWTBotGefEditPart gefEditPart = editor.getEditPart(COMPONENT_NAME);
//		ComponentShapeImpl componentShape = (ComponentSha01:12peImpl) gefEditPart.part().getModel();
//
//		MenuUtils.closeAllWithoutSave(bot);
//	}

}
