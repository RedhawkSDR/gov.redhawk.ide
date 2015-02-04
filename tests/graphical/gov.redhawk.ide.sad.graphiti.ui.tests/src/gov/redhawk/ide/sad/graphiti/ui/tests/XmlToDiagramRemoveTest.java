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

import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class that deals with removing elements from the sad.xml
 * and making sure they are removed correctly from the diagram
 */
public class XmlToDiagramRemoveTest extends AbstractGraphitiTest {

	private SWTBotGefEditor editor;
	private String waveformName;

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
		MenuUtils.save(editor);

		// Remove connection from sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		int endString = editorText.indexOf("<connections>");
		editorText = editorText.substring(0, endString);
		editor.toTextEditor().setText(editorText);
		MenuUtils.save(editor);

		// Confirm that the connection no longer exists
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
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
		MenuUtils.save(editor);

		// Remove component from sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		int endIndex = editorText.indexOf("<componentplacement>");
		String partOneText = editorText.substring(0, endIndex);
		int startIndex = editorText.indexOf("<componentplacement>", endIndex + 1);
		String partTwoText = editorText.substring(startIndex);
		editor.toTextEditor().setText(partOneText + partTwoText);
		MenuUtils.save(editor);
		
		// Confirm that the connection no longer exists
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
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

		gefBot.waitUntil(Conditions.shellIsActive("New Host Collocation"));
		gefBot.textWithLabel("Host Collocation:").setText("AAA");
		gefBot.button("OK").click();
		//add component inside host collocation (so host collocation is valid)
		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIGGEN, 5, 5);

		//save
		MenuUtils.save(editor);

		// Remove host collocation from sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		int endIndex = editorText.indexOf("<hostcollocation");
		String partOneText = editorText.substring(0, endIndex);
		int startIndex = editorText.indexOf("</partitioning>");
		String partTwoText = editorText.substring(startIndex);
		editor.toTextEditor().setText(partOneText + partTwoText);
		MenuUtils.save(editor);

		// Confirm the host collocation no longer exists in diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		HostCollocation hostCollocationObj = DiagramTestUtils.getHostCollocationObject(editor, HOSTCOLLOCATION_INSTANCE_NAME);
		Assert.assertTrue(HOSTCOLLOCATION_INSTANCE_NAME + " should have been deleted", hostCollocationObj == null);
	}
	
	/**
	 * IDE-978, IDE-965
	 * Add an external port to the diagram via the sad.xml
	 */
	@Test
	public void removeExternalPortsInXmlTest() {
		waveformName = "Add_ExternalPort_Xml";
		final String HARDLIMIT = "HardLimit";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARDLIMIT, 200, 0);
		MenuUtils.save(editor);

		// Edit content of (add external port) sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();

		String externalports = "</assemblycontroller> <externalports><port>"
			+ "<usesidentifier>dataDouble_out</usesidentifier>"
			+ "<componentinstantiationref refid=\"HardLimit_1\"/>"
			+ "</port> </externalports>";
		editorText = editorText.replace("</assemblycontroller>", externalports);
		editor.toTextEditor().setText(editorText);
		MenuUtils.save(editor);
		
		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		//assert port set to external in diagram
		SWTBotGefEditPart hardLimitUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, HARDLIMIT);
		DiagramTestUtils.assertExternalPort(hardLimitUsesEditPart, true);
		
		//switch to overview tab and verify there are external ports
		DiagramTestUtils.openTabInEditor(editor, "Overview");
		Assert.assertEquals("There are no external ports", 1, bot.table(0).rowCount());
		
		// Edit content of (remove external port) sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		editorText = editor.toTextEditor().getText();
		editorText = editorText.replace(externalports, "</assemblycontroller>");
		editor.toTextEditor().setText(editorText);
		MenuUtils.save(editor);
		
		// Confirm that no external ports exist in diagram
		hardLimitUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, HARDLIMIT);
		DiagramTestUtils.assertExternalPort(hardLimitUsesEditPart, false);
		
		//switch to overview tab and verify there are no external ports
		DiagramTestUtils.openTabInEditor(editor, "Overview");
		Assert.assertEquals("There are external ports", 0, bot.table(0).rowCount());
		
	}
	
	/**
	 * IDE-124
	 * Edit use device to the diagram via the sad.xml
	 */
	@Test
	public void removeUseDeviceInXmlTest() {
		waveformName = "Edit_UseDevice_Xml";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);

		// Edit content of sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();

		//add uses device
		String usesDevice = "<assemblycontroller/><usesdevicedependencies><usesdevice id=\"FrontEndTuner_1\"/></usesdevicedependencies>";
		editorText = editorText.replace("<assemblycontroller/>", usesDevice);
		editor.toTextEditor().setText(editorText);

		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");

		SWTBotGefEditPart useDeviceEditPart = editor.getEditPart(UsesDeviceTest.USE_DEVICE);
		UsesDeviceTest.assertUsesDevice(useDeviceEditPart);

		//remove device id via xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		editorText = editor.toTextEditor().getText();

		editorText = editorText.replace("<usesdevice id=\"FrontEndTuner_1\"/>", "");
		editor.toTextEditor().setText(editorText);

		// Confirm use device shape disappears
		DiagramTestUtils.openTabInEditor(editor, "Diagram");

		useDeviceEditPart = editor.getEditPart(UsesDeviceTest.USE_DEVICE);
		Assert.assertNull("Uses device exists but should have disappeared", useDeviceEditPart);
	}
}
