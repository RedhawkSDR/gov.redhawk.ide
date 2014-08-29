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
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class that deals with removing elements from the sad.xml 
 * and making sure they are removed correctly from the diagram 
 */
public class XmlToDiagramRemoveTests {

	private SWTGefBot gefBot;
	private SWTBotGefEditor editor;
	private String waveformName;

	@BeforeClass
	public static void beforeClass() throws Exception {
		StandardTestActions.beforeClass();
	}
	
	@Before
	public void beforeTest() throws Exception {
		gefBot = new SWTGefBot();
		StandardTestActions.beforeTest(gefBot);
	}

	@After
	public void afterTest() throws Exception {
		if (waveformName != null) {
			MenuUtils.closeAndDelete(gefBot, waveformName);
		}
		StandardTestActions.afterTest(gefBot);
	}
	
	@AfterClass
	public static void afterClass() throws Exception {
		StandardTestActions.afterClass();
	}
	
	/**
	 * IDE-851
	 * Remove a connection from the diagram via the sad.xml
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
		gefBot.sleep(2000); // Sometimes takes a few seconds for the diagram to redraw
		SWTBotGefEditPart componentEditPart = editor.getEditPart(SIGGEN);
		ContainerShape containerShape = (ContainerShape) componentEditPart.part().getModel();
		Diagram diagram = DUtil.findDiagram(containerShape);
		Assert.assertTrue("No connections should exist", diagram.getConnections().isEmpty());
		
		// Confirm both components exist in diagram
		SadComponentInstantiation hardLimitComponentObj = DiagramTestUtils.getComponentObject(editor, HARDLIMIT);
		Assert.assertTrue(HARDLIMIT + " should continue to exist regardless of connection", hardLimitComponentObj != null);
		SadComponentInstantiation sigGenComponentObj = DiagramTestUtils.getComponentObject(editor, SIGGEN);
		Assert.assertTrue(SIGGEN + " should continue to exist regardless of connection", sigGenComponentObj != null);
	}
	
	/**
	 * IDE-850
	 * Remove a component from the diagram via the sad.xml
	 */
	@Test
	public void removeComponentInXmlTest() {
		waveformName = "Remove_Component_Xml";
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
		
		// Remove component from sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		int endIndex = editorText.indexOf("<componentplacement>");
		String partOneText = editorText.substring(0, endIndex);
		int startIndex = editorText.indexOf("<componentplacement>", endIndex + 1);
		String partTwoText = editorText.substring(startIndex);
		editor.toTextEditor().setText(partOneText + partTwoText);
		MenuUtils.save(gefBot);
		
		// Confirm that the connection no longer exists
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		gefBot.sleep(2000); // Sometimes takes a few seconds for the diagram to redraw
		SWTBotGefEditPart componentEditPart = editor.getEditPart(HARDLIMIT);
		ContainerShape containerShape = (ContainerShape) componentEditPart.part().getModel();
		Diagram diagram = DUtil.findDiagram(containerShape);
		Assert.assertTrue("No connections should exist", diagram.getConnections().isEmpty());
		
		// Confirm only one component exists in diagram
		SadComponentInstantiation hardLimitComponentObj = DiagramTestUtils.getComponentObject(editor, HARDLIMIT);
		Assert.assertTrue(HARDLIMIT + " should continue to exist regardless of connection", hardLimitComponentObj != null);
		SadComponentInstantiation sigGenComponentObj = DiagramTestUtils.getComponentObject(editor, SIGGEN);
		Assert.assertTrue(SIGGEN + " should have been deleted", sigGenComponentObj == null);
	}
	
	/**
	 * IDE-852
	 * Remove a host collocation from the diagram via the sad.xml
	 */
	@Test
	public void removeHostCollocationInXmlTest() {
		waveformName = "Remove_HostCollocation_Xml";
		final String SIGGEN = "SigGen";
		final String HOSTCOLLOCATION_PALETTE = "Host Collocation";
		final String HOSTCOLLOCATION_INSTANCE_NAME = "AAA";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);

		// Add host collocation to the diagram
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HOSTCOLLOCATION_PALETTE, 0, 0);
		
		gefBot.waitUntil(Conditions.shellIsActive("Host Collocation"));
		gefBot.textWithLabel("Host Collocation:").setText("AAA");
		gefBot.button("OK").click();
		gefBot.sleep(1000);
		//add component inside host collocation (so host collocation is valid)
		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIGGEN, 5, 5);

		//save
		MenuUtils.save(gefBot);
		
		// Remove host collocation from sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		int endIndex = editorText.indexOf("<hostcollocation");
		String partOneText = editorText.substring(0, endIndex);
		int startIndex = editorText.indexOf("</partitioning>");
		String partTwoText = editorText.substring(startIndex);
		editor.toTextEditor().setText(partOneText + partTwoText);
		MenuUtils.save(gefBot);
		
		
		// Confirm the host collocation no longer exists in diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		gefBot.sleep(2000); // Sometimes takes a few seconds for the diagram to redraw
		HostCollocation hostCollocationObj = DiagramTestUtils.getHostCollocationObject(editor, HOSTCOLLOCATION_INSTANCE_NAME);
		Assert.assertTrue(HOSTCOLLOCATION_INSTANCE_NAME + " should have been deleted", hostCollocationObj == null);
	}
}
