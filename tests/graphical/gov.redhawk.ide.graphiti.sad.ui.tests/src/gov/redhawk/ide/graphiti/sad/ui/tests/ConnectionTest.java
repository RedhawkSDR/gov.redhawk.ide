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
package gov.redhawk.ide.graphiti.sad.ui.tests;

import gov.redhawk.ide.graphiti.sad.ext.impl.ComponentShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.WaitForEditorCondition;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHTestBotCanvas;

import java.util.List;

import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.junit.Assert;
import org.junit.Test;

public class ConnectionTest extends AbstractGraphitiTest {

	private SWTBotGefEditor editor;
	private String waveformName;
	private static final String SIGGEN = "SigGen";
	private static final String HARD_LIMIT = "HardLimit";
	private static final String DATA_CONVERTER = "DataConverter";

	/**
	 * IDE-731
	 * Users should be able to create connections between components in the Graphiti diagram
	 */
	@Test
	public void connectFeatureTest() {
		waveformName = "IDE-731-Test";

		// Create an empty waveform project
		WaveformUtils.createNewWaveform(gefBot, waveformName);

		// Add components to diagram from palette
		editor = gefBot.gefEditor(waveformName);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARD_LIMIT, 300, 0);

		// Get component edit parts and container shapes
		SWTBotGefEditPart sourceComponentEditPart = editor.getEditPart(SIGGEN);
		ContainerShape sourceContainerShape = (ContainerShape) sourceComponentEditPart.part().getModel();
		SWTBotGefEditPart targetComponentEditPart = editor.getEditPart(HARD_LIMIT);
		ContainerShape targetContainerShape = (ContainerShape) targetComponentEditPart.part().getModel();

		// Get port edit parts
		SWTBotGefEditPart usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN);
		SWTBotGefEditPart providesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT);

		// Confirm that no connections currently exist
		Diagram diagram = DUtil.findDiagram(sourceContainerShape);
		Assert.assertTrue("No connections should exist", diagram.getConnections().isEmpty());
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		Assert.assertFalse(editorText.matches("(?s).*<connectinterface id=\"connection_1\">.*"));
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.DIAGRAM_TAB);

		// (IDE-657) Attempt to make an illegal connection and confirm that it was not actually made
		SWTBotGefEditPart illegalTarget = DiagramTestUtils.getDiagramUsesPort(editor, HARD_LIMIT);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, illegalTarget);
		Assert.assertTrue("Illegal connection should not have been drawn", diagram.getConnections().isEmpty());

		// Draw the connection and save
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);
		MenuUtils.save(editor);

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
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		editorText = editor.toTextEditor().getText();
		Assert.assertTrue("The sad.xml should include a new connection", editorText.matches("(?s).*<connectinterface id=\"connection_1\">.*"));
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.DIAGRAM_TAB);

		// Delete connection (IDE-687 - Users need to be able to delete connections)
		List<SWTBotGefConnectionEditPart> sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		Assert.assertFalse("Source connections should not be empty for this test", sourceConnections.isEmpty());
		for (SWTBotGefConnectionEditPart con : sourceConnections) {
			DiagramTestUtils.deleteFromDiagram(editor, con);
		}
		MenuUtils.save(editor);
		sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
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

		// Create an empty waveform project
		WaveformUtils.createNewWaveform(gefBot, waveformName);

		// Add components to diagram from palette
		gefBot.waitUntil(new WaitForEditorCondition());
		editor = gefBot.gefEditor(waveformName);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARD_LIMIT, 300, 0);

		// Get port edit parts
		SWTBotGefEditPart usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN);
		SWTBotGefEditPart providesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT);

		// Draw redundant connections, save and close the editor
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);
		MenuUtils.closeAll(gefBot, true);

		// Open editor and confirm that error decorators are present
		gefBot.tree().expandNode(waveformName);
		gefBot.tree().getTreeItem(waveformName).getNode(waveformName + ".sad.xml").select().doubleClick();
		gefBot.waitUntil(new WaitForEditorCondition());
		editor = gefBot.gefEditor(waveformName);

		// ...get target component edit parts and container shapes
		SWTBotGefEditPart targetComponentEditPart = editor.getEditPart(HARD_LIMIT);
		ContainerShape targetContainerShape = (ContainerShape) targetComponentEditPart.part().getModel();

		// ...update uses port edit part references, since this is technically a new editor
		usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN);
		Connection connection = DUtil.getIncomingConnectionsContainedInContainerShape(targetContainerShape).get(0);
		Assert.assertTrue("Error decorator should have been added", connection.getConnectionDecorators().size() == 2);

		// Delete one of the connections
		List<SWTBotGefConnectionEditPart> sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		SWTBotGefConnectionEditPart connectionEditPart = sourceConnections.get(0);
		DiagramTestUtils.deleteFromDiagram(editor, connectionEditPart);
		gefBot.menu("File").menu("Save").click();

		// Confirm that error decorators do not exist for the remaining connection
		connection = DUtil.getIncomingConnectionsContainedInContainerShape(targetContainerShape).get(0);
		Assert.assertTrue("Only arrowhead decorator should exist", connection.getConnectionDecorators().size() == 1);
	}

	/**
	 * IDE-657
	 * Test that connection decorators are drawn for incompatible connections
	 */
	@Test
	public void incompatibleConnectionTest() {
		waveformName = "IDE-657-Test";

		// Create an empty waveform project
		WaveformUtils.createNewWaveform(gefBot, waveformName);

		// Add components to diagram from palette
		editor = gefBot.gefEditor(waveformName);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, DATA_CONVERTER, 300, 0);

		// Get port edit parts
		SWTBotGefEditPart usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN);
		SWTBotGefEditPart providesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, DATA_CONVERTER, "dataOctet");

		// Draw incompatible connection and confirm error decorator exists
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);
		List<SWTBotGefConnectionEditPart> connections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		Assert.assertTrue(connections.size() == 1);

		Connection connection = (Connection) connections.get(0).part().getModel();
		Assert.assertTrue("Error decorator should have been added", connection.getConnectionDecorators().size() == 2);
	}

	/**
	 * IDE-1058
	 * Test the compatible port highlight behavior
	 */
	@Test
	public void highlightTest() {
		waveformName = "HighlightTestWF";
		String sigGenPort = "dataFloat_out";
		String dataConPort = "dataFloat";

		WaveformUtils.createNewWaveform(gefBot, waveformName);

		// Add components to diagram from palette
		editor = gefBot.gefEditor(waveformName);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, DATA_CONVERTER, 300, 0);
		SWTBotGefEditPart usesPort = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN, sigGenPort).children().get(0);
		usesPort.select();

		// Mouse down on target port
		RHTestBotCanvas canvas = DiagramTestUtils.getCanvas(editor);
		Point point = DiagramTestUtils.getDiagramRelativeCenter(usesPort);
		canvas.mouseDown(point.x(), point.y());

		// Check data converter ports for color change - (it's actually the anchor that changes color)
		SWTBotGefEditPart dataConEditPart = editor.getEditPart(DATA_CONVERTER);
		ComponentShapeImpl dataConShape = (ComponentShapeImpl) dataConEditPart.part().getModel();
		List<ContainerShape> dataConPorts = DUtil.getDiagramProvidesPorts(dataConShape);
		for (ContainerShape port : dataConPorts) {
			GraphicsAlgorithm portGa = port.getChildren().get(0).getAnchors().get(0).getGraphicsAlgorithm();
			if (dataConPort.equals(((ProvidesPortStub) DUtil.getBusinessObject(port)).getName())) {
				Assert.assertTrue(compareColors(StyleUtil.GREEN, portGa.getStyle().getBackground()));
			} else {
				Assert.assertTrue(compareColors(StyleUtil.WHITE, portGa.getStyle().getBackground()));
			}
		}

		// Mouse up on target port
		canvas.mouseUp(point.x(), point.y());

		// Confirm ports return to default color
		dataConEditPart = editor.getEditPart(DATA_CONVERTER);
		dataConShape = (ComponentShapeImpl) dataConEditPart.part().getModel();
		dataConPorts = DUtil.getDiagramProvidesPorts(dataConShape);
		for (ContainerShape port : dataConPorts) {
			GraphicsAlgorithm portGa = port.getChildren().get(0).getAnchors().get(0).getGraphicsAlgorithm();
			Assert.assertTrue(compareColors(StyleUtil.WHITE, portGa.getStyle().getBackground()));
		}
	}

	private boolean compareColors(IColorConstant expectedColor, Color actualColor) {
		int[] expectedRGB = { expectedColor.getRed(), expectedColor.getGreen(), expectedColor.getBlue() };
		int[] actualRGB = { actualColor.getRed(), actualColor.getGreen(), actualColor.getBlue() };
		for (int i = 0; i < expectedRGB.length; i++) {
			if (expectedRGB[i] != actualRGB[i]) {
				return false;
			}
		}
		return true;
	}
}
