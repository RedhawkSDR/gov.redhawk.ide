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

import java.util.List;

import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.NodeUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class that deals with editing elements to the sad.xml and making sure they appear correctly in the diagram
 */
public class XmlToDiagramEditTest extends AbstractGraphitiTest {

	private SWTBotGefEditor editor;
	private String projectName;
	private static final String DOMAIN_NAME = "REDHAWK_DEV";
	private static final String GPP = "GPP";
	private static final String DEVICE_STUB = "DeviceStub";

	/**
	 * IDE-994
	 * Test editing device properties in the dcd.xml
	 * Ensure that edits are reflected to the diagram upon save
	 */
	@Test
	public void editDeviceInXmlTest() {
		projectName = "Edit_Device_Xml";

		// Create an empty node project
		NodeUtils.createNewNodeProject(gefBot, projectName, DOMAIN_NAME);
		editor = gefBot.gefEditor(projectName);
		editor.setFocus();

		// Add devices to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, GPP, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_STUB, 300, 0);
		MenuUtils.save(editor);

		// Edit content of dcd.xml
		DiagramTestUtils.openTabInEditor(editor, "DeviceManager.dcd.xml");
		String editorText = editor.toTextEditor().getText();
		editorText = editorText.replace(GPP + "_1", GPP + "_2");
		editor.toTextEditor().setText(editorText);
		MenuUtils.save(editor);

		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");

		gefBot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				return (GPP + "_2").equals(DiagramTestUtils.getDeviceObject(editor, GPP).getUsageName());
			}

			@Override
			public String getFailureMessage() {
				return "Usage Name did not update correctly. Expected [" + GPP + "_2] Found ["
					+ DiagramTestUtils.getComponentObject(editor, GPP).getUsageName() + "]";
			}
		}, 10000, 1000);

		DcdComponentInstantiation deviceObj = DiagramTestUtils.getDeviceObject(editor, GPP);
		Assert.assertEquals("Component ID did not update correctly", projectName + ":" + GPP + "_2", deviceObj.getId());
		Assert.assertEquals("Usage Name did not update correctly", GPP + "_2", deviceObj.getUsageName());
	}

	/**
	 * IDE-994
	 * Test editing connection properties in the dcd.xml
	 * Ensure that edits are reflected to the diagram upon save
	 */
	@Test
	public void editConnectionInXmlTest() {
		projectName = "Edit_Connection_Xml";

		// Create an empty node project
		NodeUtils.createNewNodeProject(gefBot, projectName, DOMAIN_NAME);
		editor = gefBot.gefEditor(projectName);
		editor.setFocus();

		// Add devices to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, GPP, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_STUB, 300, 0);
		MenuUtils.save(editor);

		// Get port edit parts
		SWTBotGefEditPart gppUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, GPP);
		SWTBotGefEditPart deviceStubProvidesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, DEVICE_STUB);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, gppUsesEditPart, deviceStubProvidesEditPart);
		MenuUtils.save(editor);

		// Edit content of dcd.xml
		DiagramTestUtils.openTabInEditor(editor, "DeviceManager.dcd.xml");
		String editorText = editor.toTextEditor().getText();
		editorText = editorText.replace("<providesidentifier>dataDouble</providesidentifier>", "<providesidentifier>eventChannel</providesidentifier>");
		editor.toTextEditor().setText(editorText);
		MenuUtils.save(editor);

		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");

		// Check that connection data has changed
		gppUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, GPP);
		List<SWTBotGefConnectionEditPart> sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, gppUsesEditPart);
		Assert.assertEquals("Wrong number of connections found", 1, sourceConnections.size());
		final Connection connection = (Connection) sourceConnections.get(0).part().getModel();

		UsesPortStub usesPort = (UsesPortStub) DUtil.getBusinessObject(connection.getStart());
		Assert.assertEquals("Connection uses port not correct", usesPort, DUtil.getBusinessObject((ContainerShape) gppUsesEditPart.part().getModel()));

		final SWTBotGefEditPart deviceStubProvidesPort = DiagramTestUtils.getDiagramProvidesPort(editor, DEVICE_STUB, "eventChannel");
		ProvidesPortStub providesPort = (ProvidesPortStub) DUtil.getBusinessObject(connection.getEnd());
		Assert.assertEquals("Connect provides port not correct", DUtil.getBusinessObject((ContainerShape) deviceStubProvidesPort.part().getModel()), providesPort);
	}
}
