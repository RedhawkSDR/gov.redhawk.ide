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

import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;

import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class that deals with removing elements from the sad.xml 
 * and making sure they are removed correctly from the diagram 
 */
public class XmlToDiagramRemoveTests {

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
	 * IDE-851
	 * Add a connection to the diagram via the sad.xml
	 */
	@Test
	public void removeConnectionInXmlTest() {
		waveformName = "Remove_Connection_Xml";
		final String SIGGEN = "SigGen";
		final String HARDLIMIT = "HardLimit";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARDLIMIT, 300, 0);
		// Get port edit parts
		SWTBotGefEditPart usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN);
		SWTBotGefEditPart providesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARDLIMIT);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);
		MenuUtils.save(gefBot);
		
		// Remove connection from sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		int endString = editorText.indexOf("<connections>");
		editorText = editorText.substring(0, endString);
		editor.toTextEditor().setText(editorText);
		MenuUtils.save(gefBot);
		
		// Confirm that the connection no longer exists
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		SWTBotGefEditPart componentEditPart = editor.getEditPart(SIGGEN);
		ContainerShape containerShape = (ContainerShape) componentEditPart.part().getModel();
		Diagram diagram = DUtil.findDiagram(containerShape);
		Assert.assertTrue("No connections should exist", diagram.getConnections().isEmpty());
	}
}
