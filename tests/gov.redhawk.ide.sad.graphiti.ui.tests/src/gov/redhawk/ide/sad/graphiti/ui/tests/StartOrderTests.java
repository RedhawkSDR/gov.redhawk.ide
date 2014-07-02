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
		
		// Close editor and delete waveform project
		// TODO - extract this to a method that can be called from a Utils class?
		MenuUtils.closeAllWithoutSave(bot);
		MenuUtils.deleteNodeInProjectExplorer(bot, waveformName);
		
	}
	
	
	// TODO - which ticket it this from
	@Test
	public void changeStartOrderTest() {
		final String waveformName = "IDE---Test";
		WaveformUtils.createNewWaveform(bot, waveformName);
		
		
		
		// Close editor and delete waveform project
		// TODO - extract this to a method that can be called from a Utils class?
		MenuUtils.closeAllWithoutSave(bot);
		MenuUtils.deleteNodeInProjectExplorer(bot, waveformName);
	}

	@AfterClass
	public static void cleanUp() {
		gefBot.sleep(2000);
	}

}
