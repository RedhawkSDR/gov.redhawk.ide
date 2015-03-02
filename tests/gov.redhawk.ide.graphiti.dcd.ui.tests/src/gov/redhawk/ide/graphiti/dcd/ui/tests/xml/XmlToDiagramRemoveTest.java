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
package gov.redhawk.ide.graphiti.dcd.ui.tests.xml;

import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.NodeUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;

import java.util.List;

import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class that deals with removing elements from the sad.xml
 * and making sure they are removed correctly from the diagram
 */
public class XmlToDiagramRemoveTest extends AbstractGraphitiTest {

	private SWTBotGefEditor editor;
	private String projectName;
	private static final String DOMAIN_NAME = "REDHAWK_DEV";
	private static final String GPP = "GPP";
	private static final String DEVICE_STUB = "DeviceStub";

	/**
	 * IDE-994
	 * Remove a connection from the diagram via the dcd.xml
	 */
	@Test
	public void removeConnectionInXmlTest() {
		projectName = "Remove_Connection_Xml";

		// Create an empty node project
		NodeUtils.createNewNodeProject(gefBot, projectName, DOMAIN_NAME);
		editor = gefBot.gefEditor(projectName);
		editor.setFocus();

		// Add devices to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, GPP, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_STUB, 300, 0);
		// Get port edit parts
		SWTBotGefEditPart gppUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, GPP);
		SWTBotGefEditPart deviceStubProvidesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, DEVICE_STUB);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, gppUsesEditPart, deviceStubProvidesEditPart);
		MenuUtils.save(editor);

		// Confirm connection was made
		List<SWTBotGefConnectionEditPart> sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, gppUsesEditPart);
		Assert.assertTrue("Expected connection was not created", sourceConnections.size() > 0);

		// Remove connection from dcd.xml
		DiagramTestUtils.openTabInEditor(editor, "DeviceManager.dcd.xml");
		String editorText = editor.toTextEditor().getText();
		String topHalf = editorText.substring(0, editorText.indexOf("<connections>"));
		String bottomHalf = editorText.substring(editorText.indexOf("</connections>") + "</connections>".length());
		editorText = topHalf + bottomHalf;
		editor.toTextEditor().setText(editorText);
		MenuUtils.save(editor);

		// Confirm that the connection no longer exists in Graphiti model
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		gppUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, GPP);
		sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, gppUsesEditPart);
		Assert.assertTrue("No connections should exist", sourceConnections.size() == 0);

		// Confirm both devices still exist in diagram
		DcdComponentInstantiation gppObj = DiagramTestUtils.getDeviceObject(editor, GPP);
		Assert.assertTrue(GPP + " should continue to exist regardless of connection", gppObj != null);
		DcdComponentInstantiation deviceStubObj = DiagramTestUtils.getDeviceObject(editor, DEVICE_STUB);
		Assert.assertTrue(DEVICE_STUB + " should continue to exist regardless of connection", deviceStubObj != null);
	}

	/**
	 * IDE-994
	 * Remove a device from the diagram via the dcd.xml
	 */
	@Test
	public void removeDeviceInXmlTest() {
		projectName = "Remove_Device_Xml";

		// Create an empty node project
		NodeUtils.createNewNodeProject(gefBot, projectName, DOMAIN_NAME);
		editor = gefBot.gefEditor(projectName);
		editor.setFocus();

		// Add devices to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, GPP, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_STUB, 300, 0);
		// Get port edit parts
		SWTBotGefEditPart gppUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, GPP);
		SWTBotGefEditPart deviceStubProvidesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, DEVICE_STUB);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, gppUsesEditPart, deviceStubProvidesEditPart);
		MenuUtils.save(editor);

		// Remove device from dcd.xml
		DiagramTestUtils.openTabInEditor(editor, "DeviceManager.dcd.xml");
		String editorText = editor.toTextEditor().getText();
		String topHalf = editorText.substring(0, editorText.indexOf("<componentplacement>"));
		String bottomHalf = editorText.substring(editorText.indexOf("</componentplacement>") + "</componentplacement>".length());
		editorText = topHalf + bottomHalf;
		editor.toTextEditor().setText(editorText);
		MenuUtils.save(editor);

		// Confirm that the connection no longer exists
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		deviceStubProvidesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, DEVICE_STUB);
		List<SWTBotGefConnectionEditPart> targetConnections = DiagramTestUtils.getTargetConnectionsFromPort(editor, gppUsesEditPart);
		Assert.assertTrue("No connections should exist", targetConnections.size() == 0);

		// Confirm only one device exists in diagram
		DcdComponentInstantiation deviceStubObj = DiagramTestUtils.getDeviceObject(editor, DEVICE_STUB);
		Assert.assertTrue(DEVICE_STUB + " should continue to exist regardless of connection", deviceStubObj != null);
		DcdComponentInstantiation gppObj = DiagramTestUtils.getDeviceObject(editor, GPP);
		Assert.assertTrue(GPP + " should have been deleted", gppObj == null);
	}
}
