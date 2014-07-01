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
import gov.redhawk.ide.swtbot.tests.menus.MenuUtils;
import gov.redhawk.ide.swtbot.tests.editor.EditorTestUtils;
import gov.redhawk.ide.swtbot.tests.editor.FindByUtils;
import gov.redhawk.ide.swtbot.tests.waveform.CreateNewWaveform;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotPerspective;
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
	private static SWTWorkbenchBot wbBot;
	private static final String COMPONENT_NAME = "HardLimit";
	private static final String[] COMPONENTS = {"HardLimit", "SigGen", "DataConverter"};
	private static final String[] FINDBYS = { FindByUtils.FIND_BY_CORBA_NAME, FindByUtils.FIND_BY_DOMAIN_MANAGER, 
		FindByUtils.FIND_BY_EVENT_CHANNEL, FindByUtils.FIND_BY_FILE_MANAGER, FindByUtils.FIND_BY_SERVICE };

	@BeforeClass
	public static void beforeClass() {
		bot = new SWTGefBot();
		wbBot = new SWTWorkbenchBot();
		// Switch to SCA Perspective
		SWTBotPerspective perspective = wbBot.perspectiveById("gov.redhawk.ide.ui.perspectives.sca");
		perspective.activate();
	}

	/**
	 * IDE-726
	 * Create the pictogram shape in the waveform diagram that represents the component business object.
	 * This includes the ContainerShape for the component, labels for Usage Name and ID, port shapes and labels,
	 * start order icon, and component supported interface.
	 */
	@Test
	public void checkComponentPictogramElements() {
		final String waveformName = "IDE-726-Test";
		// Create an empty waveform project
		CreateNewWaveform.createNewWaveform(bot, waveformName);

		// Add component to diagram from palette
		editor = bot.gefEditor(waveformName);
		EditorTestUtils.dragFromPaletteToDiagram(editor, COMPONENT_NAME, 0, 0);

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
		//MenuUtils.openWFEditorFromProjectExplorer(bot, waveformName);
		MenuUtils.deleteNodeInProjectExplorer(bot, waveformName);
	}

	/**
	 * IDE-680
	 * When using the New Waveform Project wizard, if the user selects an Assembly Controller as part of the wizard 
	 * then the component will not appear in the resulting diagram. The component is added to the sad.xml, though it 
	 * does not have a start order attribute.
	 */
	@Test
	public void checkComponentPictogramElementsWithAssemblyController() {
		final String waveformName = "IDE-680-Test";
		CreateNewWaveform.createNewWaveformWithAssemblyController(bot, waveformName, COMPONENT_NAME);
		editor = bot.gefEditor(waveformName);

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
		Assert.assertTrue("should be assembly controller", EditorTestUtils.isAssemblyController(componentShape));

		// HardLimit only has the two ports
		Assert.assertTrue(componentShape.getUsesPortStubs().size() == 1 && componentShape.getProvidesPortStubs().size() == 1);

		// Both ports are of type dataDouble
		Assert.assertEquals(componentShape.getUsesPortStubs().get(0).getUses().getInterface().getName(), "dataDouble");
		Assert.assertEquals(componentShape.getProvidesPortStubs().get(0).getProvides().getInterface().getName(), "dataDouble");

		MenuUtils.closeAllWithoutSave(bot);
		MenuUtils.deleteNodeInProjectExplorer(bot, waveformName);
	}

	/**
	 * IDE-669
	 * Components are removed with the delete button (trashcan image) that appears when you select the component, 
	 * but the delete context menu does not remove the component from the diagram. In most cases, the delete and 
	 * remove context menu options are grayed out and not selectable.
	 */
	@Test
	public void checkComponentContextMenuDelete() {
		final String waveformName = "IDE-669-Test-a";
		// Create an empty waveform project
		CreateNewWaveform.createNewWaveform(bot, waveformName); 
		editor = bot.gefEditor(waveformName);
		
		for (String s : COMPONENTS) {
			// Add component to diagram from palette
			EditorTestUtils.dragFromPaletteToDiagram(editor, s, 0, 0);			
		}

		bot.menu("File").menu("Save").click();		


		for (String s : COMPONENTS) {
			// Drill down to graphiti component shape
			SWTBotGefEditPart gefEditPart = editor.getEditPart(s);
			EditorTestUtils.deleteFromDiagram(editor, gefEditPart);
			Assert.assertNull(editor.getEditPart(s));
		}

		MenuUtils.closeAllWithoutSave(bot);
		MenuUtils.deleteNodeInProjectExplorer(bot, waveformName);
	}

	@Test
	public void checkFindByContextMenuDelete() {
		final String waveformName = "IDE-669-Test-b";

		CreateNewWaveform.createNewWaveform(bot, waveformName);
		editor = bot.gefEditor(waveformName);
		
		for (String s : FINDBYS) {
			// Add component to diagram from palette
			EditorTestUtils.dragFromPaletteToDiagram(editor, s, 0, 0);
			FindByUtils.completeFindByWizard(bot, s);
		}

		for (String s : FINDBYS) {
			// Drill down to graphiti component shape
			SWTBotGefEditPart gefEditPart = editor.getEditPart(FindByUtils.getFindByDefaultName(s));
			EditorTestUtils.deleteFromDiagram(editor, gefEditPart);
			bot.button("Yes").click(); // are you sure you want to delete this element?
			Assert.assertNull(editor.getEditPart(s));
		}

		MenuUtils.closeAllWithoutSave(bot);
		MenuUtils.deleteNodeInProjectExplorer(bot, waveformName);
	}

	/**
	 * IDE-653
	 * Users are able to directly edit the name of Component and FindBy shapes on the diagram by double clicking on it.  
	 * If you click on text other than the usage name and it will think you are editing the usage name.
	 * This likely involves telling Graphiti not to do anything when selecting certain Pictogram Elements.
	 */
	@Test
	public void checkComponentDirectEdit() {
		final String waveformName = "IDE-653-Test";
		
		CreateNewWaveform.createNewWaveform(bot, waveformName);
		editor = bot.gefEditor(waveformName);
		
		// Add component to diagram from palette
		EditorTestUtils.dragFromPaletteToDiagram(editor, COMPONENT_NAME, 0, 0);
		FindByUtils.completeFindByWizard(bot, COMPONENT_NAME);
		
		// Drill down to graphiti component shape
		SWTBotGefEditPart gefEditPart = editor.getEditPart(COMPONENT_NAME);
		ComponentShapeImpl componentShape = (ComponentShapeImpl) gefEditPart.part().getModel();

		// Grab the associated business object and confirm it is a SadComponentInstantiation
		Object bo = DUtil.getBusinessObject(componentShape);
		Assert.assertTrue("business object should be of type SadComponentInstantiation", bo instanceof SadComponentInstantiation);
		SadComponentInstantiation ci = (SadComponentInstantiation) bo;
		
		// TODO Edit via directEdit
		String initName = ci.getUsageName();
		gefEditPart.activateDirectEdit();
		editor.directEditType(initName + "_edit");
		gefEditPart.click();
		
		Assert.assertEquals(initName + "_edit", ci.getUsageName());
		
		// Save, close, and reopen
		MenuUtils.closeAll(bot, true);
		EditorTestUtils.openSadDiagram(wbBot, waveformName);
		Assert.assertEquals(initName + "_edit", ci.getUsageName());
		
		MenuUtils.closeAllWithoutSave(bot);
		MenuUtils.deleteNodeInProjectExplorer(bot, waveformName);
	}

	@Test
	public void checkFindByDirectEdit() {
		
	}

	@AfterClass
	public static void cleanUp() {
		bot.sleep(2000);
	}


}
