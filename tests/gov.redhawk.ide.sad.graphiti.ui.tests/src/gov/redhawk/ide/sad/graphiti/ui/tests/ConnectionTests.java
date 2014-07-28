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
import gov.redhawk.ide.swtbot.tests.utils.EditorTestUtils;
import gov.redhawk.ide.swtbot.tests.utils.MenuUtils;
import gov.redhawk.ide.swtbot.tests.utils.WaveformUtils;

import java.util.List;

import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotPerspective;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class ConnectionTests { // SUPPRESS CHECKSTYLE INLINE
	private SWTBot bot;
	private static SWTGefBot gefBot;
	private SWTBotGefEditor editor;
	private String waveformName;

	@BeforeClass
	public static void beforeClass() throws Exception {
		while (PlatformUI.getWorkbench().isStarting()) {
			Thread.sleep(1000);
		}
	}

	@Before
	public void beforeTest() throws Exception {
		bot = new SWTBot();
		gefBot = new SWTGefBot();
		SWTBotPerspective perspective = gefBot.perspectiveById("gov.redhawk.ide.ui.perspectives.sca");
		perspective.activate();
		gefBot.resetActivePerspective();
	}

	/**
	 * IDE-731
	 * Users should be able to create connections between components in the Graphiti diagram
	 */
	@Test
	public void connectFeatureTest() {
		waveformName = "IDE-731-Test";
		final String sourceComponent = "SigGen";
		final String targetComponent = "HardLimit";

		// Create an empty waveform project
		WaveformUtils.createNewWaveform(gefBot, waveformName);

		// Add components to diagram from palette
		editor = gefBot.gefEditor(waveformName);
		EditorTestUtils.dragFromPaletteToDiagram(editor, sourceComponent, 0, 0);
		EditorTestUtils.dragFromPaletteToDiagram(editor, targetComponent, 300, 0);

		// Get component edit parts and container shapes
		SWTBotGefEditPart sourceComponentEditPart = editor.getEditPart(sourceComponent);
		ContainerShape sourceContainerShape = (ContainerShape) sourceComponentEditPart.part().getModel();
		SWTBotGefEditPart targetComponentEditPart = editor.getEditPart(targetComponent);
		ContainerShape targetContainerShape = (ContainerShape) targetComponentEditPart.part().getModel();

		// Get port edit parts
		SWTBotGefEditPart usesEditPart = EditorTestUtils.getDiagramUsesPort(editor, sourceComponent);
		SWTBotGefEditPart providesEditPart = EditorTestUtils.getDiagramProvidesPort(editor, targetComponent);

		// Confirm that no connections currently exist
		Diagram diagram = DUtil.findDiagram(sourceContainerShape);
		Assert.assertTrue("No connections should exist", diagram.getConnections().isEmpty());
		EditorTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		Assert.assertFalse(editorText.matches("(?s).*<connectinterface id=\"connection_1\">.*"));
		EditorTestUtils.openTabInEditor(editor, EditorTestUtils.DIAGRAM_TAB);

		// (IDE-657) Attempt to make an illegal connection and confirm that it was not actually made
		SWTBotGefEditPart illegalTarget = EditorTestUtils.getDiagramUsesPort(editor, targetComponent);
		EditorTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, illegalTarget);
		Assert.assertTrue("Illegal connection should not have been drawn", diagram.getConnections().isEmpty());

		// Draw the connection and save
		EditorTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);
		bot.menu("File").menu("Save").click();

		// Test to make sure connection was made correctly
		Assert.assertFalse("Connection should exist", diagram.getConnections().isEmpty());

		Connection connection = DUtil.getIncomingConnectionsContainedInContainerShape(targetContainerShape).get(0);

		UsesPortStub usesPort = (UsesPortStub) DUtil.getBusinessObject(connection.getStart());
		Assert.assertEquals("Connection uses port not correct", usesPort, DUtil.getBusinessObject((ContainerShape) usesEditPart.part().getModel()));

		ProvidesPortStub providesPort = (ProvidesPortStub) DUtil.getBusinessObject(connection.getEnd());
		Assert.assertEquals("Connect provides port not correct", providesPort, DUtil.getBusinessObject((ContainerShape) providesEditPart.part().getModel()));

		Assert.assertTrue("Only arrowhead decorator should be present", connection.getConnectionDecorators().size() == 1);
		for (ConnectionDecorator decorator : connection.getConnectionDecorators()) {
			Assert.assertTrue("Only arrowhead decorator should be present", decorator.getGraphicsAlgorithm() instanceof Polyline);
		}

		// Check sad.xml new for connection
		EditorTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		editorText = editor.toTextEditor().getText();
		Assert.assertTrue("The sad.xml should include a new connection", editorText.matches("(?s).*<connectinterface id=\"connection_1\">.*"));
		EditorTestUtils.openTabInEditor(editor, EditorTestUtils.DIAGRAM_TAB);

		// Delete connection (IDE-687 - Users need to be able to delete connections)
		List<SWTBotGefConnectionEditPart> sourceConnections = EditorTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		Assert.assertFalse("Source connections should not be empty for this test", sourceConnections.isEmpty());
		for (SWTBotGefConnectionEditPart con : sourceConnections) {
			EditorTestUtils.deleteFromDiagram(editor, con);
		}
		bot.menu("File").menu("Save").click();
		sourceConnections = EditorTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		Assert.assertTrue("Source connections should be empty, all connections were deleted", sourceConnections.isEmpty());
		Assert.assertTrue("All connections should have been deleted", diagram.getConnections().isEmpty());
	}

	/**
	 * IDE-679 && IDE-657
	 * The creation of a redundant connection results in a yellow warning icon, an error message
	 * ("Redundant connection"), and a dotted red line for the connection path.
	 * When the redundant connection(s) are deleted the error decorators should be removed.
	 */
	@Test
	public void redundantConnectionTest() {
		waveformName = "IDE-679-Test";
		final String sourceComponent = "SigGen";
		final String targetComponent = "HardLimit";

		// Create an empty waveform project
		WaveformUtils.createNewWaveform(gefBot, waveformName);

		// Add components to diagram from palette
		editor = gefBot.gefEditor(waveformName);
		EditorTestUtils.dragFromPaletteToDiagram(editor, sourceComponent, 0, 0);
		EditorTestUtils.dragFromPaletteToDiagram(editor, targetComponent, 300, 0);

		// Get port edit parts
		SWTBotGefEditPart usesEditPart = EditorTestUtils.getDiagramUsesPort(editor, sourceComponent);
		SWTBotGefEditPart providesEditPart = EditorTestUtils.getDiagramProvidesPort(editor, targetComponent);

		// Draw redundant connections, save and close the editor
		EditorTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);
		EditorTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);
		MenuUtils.closeAll(bot, true);

		// Open editor and confirm that error decorators are present
		bot.tree().expandNode(waveformName);
		bot.tree().getTreeItem(waveformName).getNode(waveformName + ".sad.xml").select().doubleClick();
		bot.sleep(5000); // Give editor time to open
		editor = gefBot.gefEditor(waveformName);

		// ...get target component edit parts and container shapes
		SWTBotGefEditPart targetComponentEditPart = editor.getEditPart(targetComponent);
		ContainerShape targetContainerShape = (ContainerShape) targetComponentEditPart.part().getModel();

		// ...update uses port edit part references, since this is technically a new editor
		usesEditPart = EditorTestUtils.getDiagramUsesPort(editor, sourceComponent);

		boolean imageDecoratorFound = true, textDecoratorFound = false;
		Connection connection = DUtil.getIncomingConnectionsContainedInContainerShape(targetContainerShape).get(0);
		for (ConnectionDecorator decorator : connection.getConnectionDecorators()) {
			if (decorator.getGraphicsAlgorithm() instanceof Image) {
				imageDecoratorFound = true;
			}
			if (decorator.getGraphicsAlgorithm() instanceof Text) {
				Text text = (Text) decorator.getGraphicsAlgorithm();
				Assert.assertEquals(text.getValue(), "Redundant connection");
				textDecoratorFound = true;
			}
		}
		Assert.assertTrue(imageDecoratorFound && textDecoratorFound); // Confirm that decorators are present

		// Delete one of the connections
		List<SWTBotGefConnectionEditPart> sourceConnections = EditorTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		SWTBotGefConnectionEditPart connectionEditPart = sourceConnections.get(0);
		EditorTestUtils.deleteFromDiagram(editor, connectionEditPart);
		bot.menu("File").menu("Save").click();

		// Confirm that error decorators do not exist for the remaining connection
		connection = DUtil.getIncomingConnectionsContainedInContainerShape(targetContainerShape).get(0);
		boolean decoratorFound = false;
		for (ConnectionDecorator decorator : connection.getConnectionDecorators()) {
			if (decorator.getGraphicsAlgorithm() instanceof Image || decorator.getGraphicsAlgorithm() instanceof Text) {
				decoratorFound = true;
			}
		}
		Assert.assertFalse(decoratorFound); // Confirm that decorators were removed
	}

	/**
	 * IDE-657
	 * Test that connection decorators are drawn for incompatible connections
	 */
	@Test
	public void incompatibleConnectionTest() {
		waveformName = "IDE-incompatCon-Test";
		final String sourceComponent = "SigGen";
		final String targetComponent = "DataConverter";

		// Create an empty waveform project
		WaveformUtils.createNewWaveform(gefBot, waveformName);

		// Add components to diagram from palette
		editor = gefBot.gefEditor(waveformName);
		EditorTestUtils.dragFromPaletteToDiagram(editor, sourceComponent, 0, 0);
		EditorTestUtils.dragFromPaletteToDiagram(editor, targetComponent, 300, 0);

		// Get port edit parts
		SWTBotGefEditPart usesEditPart = EditorTestUtils.getDiagramUsesPort(editor, sourceComponent);
		SWTBotGefEditPart providesEditPart = EditorTestUtils.getDiagramProvidesPort(editor, targetComponent, "dataOctet");

		// Draw incompatible connection and confirm error decorator exists
		EditorTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);
		List<SWTBotGefConnectionEditPart> connections = EditorTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		Assert.assertTrue(connections.size() == 1);

		boolean imageDecoratorFound = false, textDecoratorFound = false;
		Connection connection = (Connection) connections.get(0).part().getModel();
		for (ConnectionDecorator decorator : connection.getConnectionDecorators()) {
			if (decorator.getGraphicsAlgorithm() instanceof Image) {
				imageDecoratorFound = true;
			}
			if (decorator.getGraphicsAlgorithm() instanceof Text) {
				Text text = (Text) decorator.getGraphicsAlgorithm();
				Assert.assertEquals(text.getValue(), "Incompatible Connection");
				textDecoratorFound = true;
			}
		}
		Assert.assertTrue(imageDecoratorFound && textDecoratorFound); // Confirm that decorators are present
	}

	// TODO reconnection feature test

	@After
	public void afterTest() {
		if (gefBot == null) {
			return;
		}
		if (waveformName != null) {
			MenuUtils.closeAndDelete(gefBot, waveformName);
		}
		gefBot.closeAllEditors();
	}

	@AfterClass
	public static void afterClass() {
		gefBot.sleep(2000);
	}
}
