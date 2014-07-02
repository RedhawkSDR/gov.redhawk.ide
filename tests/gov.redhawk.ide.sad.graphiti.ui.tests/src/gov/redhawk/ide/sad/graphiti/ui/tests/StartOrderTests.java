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
import gov.redhawk.ide.swtbot.tests.utils.ComponentUtils;
import gov.redhawk.ide.swtbot.tests.utils.WaveformUtils;
import gov.redhawk.ide.swtbot.tests.utils.EditorTestUtils;
import gov.redhawk.ide.swtbot.tests.utils.MenuUtils;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class StartOrderTests {

	private static SWTBot bot;
	private static SWTGefBot gefBot;
	private static SWTBotGefEditor editor;

	private static final String[] COMPONENT = {"DataConverter", "HardLimit", "SigGen"}; //Alphabetical order
	
	@BeforeClass
	public static void beforeClass() {
		gefBot = new SWTGefBot();
	}

	/**
	 * IDE-721
	 * Start order should be treated as an optional field, and should not cause
	 * errors when null
	 */
	@Test
	public void nullStartOrderTest() {
		final String waveformName = "IDE-721-Test";
		final String compNoStartOrder = "SigGen";
		final String comp1 = "HardLimit";
		final String comp2 = "DataConverter";
		// Create a new waveform with assembly controller
		// ...when assembly controllers are added from the new project wizard they don't have a start order
		// ...this is kind of a hack
		WaveformUtils.createNewWaveformWithAssemblyController(gefBot, waveformName, compNoStartOrder);
		editor = gefBot.gefEditor(waveformName);
		// Assert that start order is null
		SWTBotGefEditPart noSOEditPart = editor.getEditPart(compNoStartOrder);
		ComponentShapeImpl componentShape = (ComponentShapeImpl) noSOEditPart.part().getModel();
		SadComponentInstantiation bo = (SadComponentInstantiation) DUtil.getBusinessObject(componentShape);
		Assert.assertNull("Start Order should be null", bo.getStartOrder());
		
		// Add additional components to the diagram
		EditorTestUtils.dragFromPaletteToDiagram(editor, comp1, 100, 0);
		EditorTestUtils.dragFromPaletteToDiagram(editor, comp2, 100, 150); 
		
		MenuUtils.closeAndDelete(gefBot, waveformName);
		
	}
	
	// TODO - which ticket it this from
	@Test
	public void changeStartOrderTest() {
		final String waveformName = "IDE---Test";
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		
		
		
		// Close editor and delete waveform project
		// TODO - extract this to a method that can be called from a Utils class?
		MenuUtils.closeAndDelete(gefBot, waveformName);
	}

	/**
	 * IDE-695
	 * When using the Overview Tab to change the assembly controller, the start order icons do not 
	 * correctly update in the diagram. The color of the start order icon correctly changes (yellow 
	 * for the new controller, white for all other components). But the number remains as they were 
	 * before the modification. The 'Set as Assembly Controller' context option is also disabled on 
	 * the new assembly controller, meaning the only way to fix is to unassign and reassign the 
	 * controller again in the diagram, or edit the sad.xml directly.
	 */
	@Test
	public void setAssemblyControllerFromOverview() {
		final String waveformName = "IDE-695-Test";
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);
		
		// Add components to diagram
		for (int i = 0; i < COMPONENT.length; i++) {
			EditorTestUtils.dragFromPaletteToDiagram(editor, COMPONENT[i], 0, 0);
		}
		
		// Check initial assembly controller
		String assemblyController = COMPONENT[0];
		Assert.assertTrue(ComponentUtils.isAssemblyController(gefBot, editor, assemblyController));		

		// Change assembly controller to last component in list via Overview Tab
		EditorTestUtils.openTabInEditor(editor, EditorTestUtils.OVERVIEW_TAB);
		editor.bot().ccomboBoxWithLabel("Controller:").setSelection(COMPONENT.length - 1);
		
		// Confirm start order numbers have adjusted appropriately
		for (int i = 0; i < COMPONENT.length - 1; i++) {
			Assert.assertEquals(i + 1, ComponentUtils.getStartOrder(editor, COMPONENT[i]));
		}
		
		// Check new assembly controller
		assemblyController = COMPONENT[COMPONENT.length - 1];
		Assert.assertEquals(0, ComponentUtils.getStartOrder(editor, assemblyController));
		Assert.assertTrue(ComponentUtils.isAssemblyController(gefBot, editor, assemblyController));
		
		MenuUtils.closeAndDelete(gefBot, waveformName);
	}
	
	
	/**
	 * IDE-#
	 * Checks to confirm that start order matches the order in which components are dragged from the palette to
	 * the diagram and that the assembly controller is the first component to be added.
	 */
	@Test
	public void checkStartOrderSequence() {
		final String waveformName = "IDE---Test";
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);
		// Add and check start order
		for (int i = 0; i < COMPONENT.length; i++) {
			EditorTestUtils.dragFromPaletteToDiagram(editor, COMPONENT[i], 0, 0);
			Assert.assertEquals(i, ComponentUtils.getStartOrder(editor, COMPONENT[i]));
		}
		// Check first added is assembly controller
		Assert.assertTrue(ComponentUtils.isAssemblyController(gefBot, editor, COMPONENT[0]));
		
		MenuUtils.closeAndDelete(gefBot, waveformName);		
	}
	
	
	@AfterClass
	public static void cleanUp() {
		gefBot.sleep(2000);
	}

}
