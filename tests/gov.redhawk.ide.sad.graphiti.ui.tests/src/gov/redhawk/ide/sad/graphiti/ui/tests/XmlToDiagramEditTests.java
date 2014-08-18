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

import java.math.BigInteger;

import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class that deals with editing elements to the sad.xml and making sure they appear correctly in the diagram
 */
@RunWith(SWTBotJunit4ClassRunner.class) 
public class XmlToDiagramEditTests {

	private static SWTGefBot gefBot;
	private static SWTBotGefEditor editor;
	private String waveformName;

	@BeforeClass
	public static void beforeClass() {
		gefBot = new SWTGefBot();
	}
	
	@After
	public void afterTest() {
		if (waveformName != null) {
			MenuUtils.closeAndDelete(gefBot, waveformName);
		}
		gefBot.closeAllEditors();
	}

	@AfterClass
	public static void afterClass() {
		gefBot.sleep(2000);
	}
	
	/**
	 * IDE-853
	 * Test editing component properties in the sad.xml
	 * Ensure that edits are reflected to the diagram upon save
	 */
	@Test
	public void editComponentInXmlTest() {
		waveformName = "Edit_Component_Xml";
		final String componentOne = "SigGen";
		final String componentTwo = "HardLimit";
		final String componentThree = "SigGen";
		
		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);
		
		// Add component to the diagram
		DiagramTestUtils.dragFromPaletteToDiagram(editor, componentOne, 0, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, componentTwo, 200, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, componentThree, 0, 200);
		MenuUtils.save(gefBot);
		
		// Edit content of sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		editorText = editorText.replace(componentTwo + "_1", componentTwo + "_2");
		editorText = editorText.replace("startorder=\"1\"", "startorder=\"3\"");
		editorText = editorText.replace("startorder=\"2\"", "startorder=\"1\"");
		editorText = editorText.replace("startorder=\"3\"", "startorder=\"2\"");
		editor.toTextEditor().setText(editorText);
		MenuUtils.save(gefBot);
		
		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		SadComponentInstantiation componentObj = DiagramTestUtils.getComponentObject(editor, componentTwo);
		Assert.assertEquals("Usage Name did not update correctly", componentTwo + "_2", componentObj.getUsageName());
		Assert.assertEquals("Component ID did not update correctly", componentTwo + "_2", componentObj.getId());
		Assert.assertEquals("Naming Service did not update correctly", componentTwo + "_2", componentObj.getFindComponent().getNamingService().getName());
		Assert.assertEquals("Start Order did not update correctly", BigInteger.valueOf(2), componentObj.getStartOrder());
		
	}
}
