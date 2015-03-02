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

import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class that deals with adding elements to the sad.xml and making sure they appear correctly in the diagram
 */
public class XmlToDiagramAddTest extends AbstractGraphitiTest {

	private SWTBotGefEditor editor;
	private String projectName;
	private static final String DOMAIN_NAME = "REDHAWK_DEV";
	private static final String GPP = "GPP";
	private static final String DEVICE_STUB = "DeviceStub";

	/**
	 * IDE-994
	 * Sync node diagram and dcd.xml
	 * Add a device to the diagram via the sad.xml
	 */
	@Test
	public void addDeviceInXmlTest() {
		projectName = "Add_Device_Xml";

		// Create an empty node project
		NodeUtils.createNewNodeProject(gefBot, projectName, DOMAIN_NAME);
		editor = gefBot.gefEditor(projectName);
		editor.setFocus();

		// Add device to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, GPP, 0, 0);
		MenuUtils.save(editor);

		// Edit content of dcd.xml
		DiagramTestUtils.openTabInEditor(editor, "DeviceManager.dcd.xml");
		String editorText = editor.toTextEditor().getText();
		String newComponentFile = "</componentfile> <componentfile id=\"DeviceStub_e3e135fd-ab3e-4d0b-ba0a-216f00464c7a\" type=\"SPD\"> "
			+ "<localfile name=\"/devices/DeviceStub/DeviceStub.spd.xml\"/> </componentfile>";
		editorText = editorText.replace("</componentfile>", newComponentFile);

		String newComponentPlacement = "</componentplacement> <componentplacement> "
			+ "<componentfileref refid=\"DeviceStub_e3e135fd-ab3e-4d0b-ba0a-216f00464c7a\"/> " + "<componentinstantiation id=\"" + projectName
			+ ":DeviceStub_1\"> <usagename>DeviceStub_1</usagename> " + "</componentinstantiation> </componentplacement>";
		editorText = editorText.replace("</componentplacement>", newComponentPlacement);
		editor.toTextEditor().setText(editorText);
		MenuUtils.save(editor);

		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		DcdComponentInstantiation deviceObj = DiagramTestUtils.getDeviceObject(editor, DEVICE_STUB);
		Assert.assertNotNull(deviceObj);
		Assert.assertEquals("Usage Name did not create correctly", DEVICE_STUB + "_1", deviceObj.getUsageName());
		Assert.assertEquals("Device ID did not create correctly", projectName + ":" + DEVICE_STUB + "_1", deviceObj.getId());
	}

	/**
	 * IDE-994
	 * Add a connection to the diagram via the dcd.xml
	 */
	@Test
	public void addConnectionInXmlTest() {
		projectName = "Add_Connection_Xml";

		// Create an empty node project
		NodeUtils.createNewNodeProject(gefBot, projectName, DOMAIN_NAME);
		editor = gefBot.gefEditor(projectName);
		editor.setFocus();

		// Add device to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, GPP, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_STUB, 200, 0);
		MenuUtils.save(editor);

		// Confirm that no connections currently exist
		SWTBotGefEditPart gppUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, GPP);
		List<SWTBotGefConnectionEditPart> sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, gppUsesEditPart);
		Assert.assertTrue("No connections should exist", sourceConnections.isEmpty());

		// Edit content of sad.xml
		DiagramTestUtils.openTabInEditor(editor, "DeviceManager.dcd.xml");
		String editorText = editor.toTextEditor().getText();
		String newConnection = "</partitioning> <connections> <connectinterface id=\"connection_1\"> "
			+ "<usesport> <usesidentifier>propEvent</usesidentifier> <componentinstantiationref refid=\"" + projectName + ":GPP_1\"/> "
			+ "</usesport> <providesport> <providesidentifier>eventChannel</providesidentifier> " + "<componentinstantiationref refid=\"" + projectName
			+ ":DeviceStub_1\"/> </providesport> </connectinterface> </connections>";
		editorText = editorText.replace("</partitioning>", newConnection);
		editor.toTextEditor().setText(editorText);
		MenuUtils.save(editor);

		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");

		gppUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, GPP);
		SWTBotGefEditPart deviceStubProvidesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, DEVICE_STUB, "eventChannel");

		sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, gppUsesEditPart);
		Assert.assertFalse("Connection should exist", sourceConnections.isEmpty());

		Connection connection = (Connection) sourceConnections.get(0).part().getModel();
		UsesPortStub usesPort = (UsesPortStub) DUtil.getBusinessObject(connection.getStart());
		Assert.assertEquals("Connection uses port not correct", usesPort, DUtil.getBusinessObject((ContainerShape) gppUsesEditPart.part().getModel()));

		ProvidesPortStub providesPort = (ProvidesPortStub) DUtil.getBusinessObject(connection.getEnd());
		Assert.assertEquals("Connect provides port not correct", providesPort,
			DUtil.getBusinessObject((ContainerShape) deviceStubProvidesEditPart.part().getModel()));

		Assert.assertTrue("Only arrowhead decorator should be present", connection.getConnectionDecorators().size() == 1);
		for (ConnectionDecorator decorator : connection.getConnectionDecorators()) {
			Assert.assertTrue("Only arrowhead decorator should be present", decorator.getGraphicsAlgorithm() instanceof Polyline);
		}
	}
}
