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

import static org.hamcrest.CoreMatchers.instanceOf;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.tests.editor.EditorTestUtils;
import gov.redhawk.ide.swtbot.tests.waveform.CreateNewWaveform;

import java.util.List;

import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

import org.eclipse.gef.EditPart;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.hamcrest.Matcher;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConnectionTests { // SUPPRESS CHECKSTYLE INLINE
	private static SWTBot bot;
	private static SWTGefBot gefBot;
	private static SWTBotGefEditor editor;

	@BeforeClass
	public static void beforeClass() {
		bot = new SWTBot();
		gefBot = new SWTGefBot();
	}

	/**
	 * IDE-731
	 * Users should be able to take create connections between components in the Graphiti diagram
	 */
	@Test
	public void connectFeatureTest() {
		// TODO CHECKSTYLE:OFF
		final String waveformName = "IDE-731-Test";
		final String sourceComponent = "SigGen";
		final String targetComponent = "HardLimit";
		// Create an empty waveform project
		CreateNewWaveform.createNewWaveform(gefBot, waveformName);

		// Add component to diagram from palette
		editor = gefBot.gefEditor(waveformName);
		EditorTestUtils.dragFromPaletteToDiagram(editor, sourceComponent, 0, 0);
		EditorTestUtils.dragFromPaletteToDiagram(editor, targetComponent, 300, 0);

		// Define object references to the ports
		SWTBotGefEditPart sourceComponentEditPart = editor.getEditPart(sourceComponent);
		
		ContainerShape sourceContainerShape = (ContainerShape) sourceComponentEditPart.part().getModel();

		SWTBotGefEditPart targetComponentEditPart = editor.getEditPart(targetComponent);
		ContainerShape targetContainerShape = (ContainerShape) targetComponentEditPart.part().getModel();
		
		SWTBotGefEditPart usesEditPart = EditorTestUtils.getDiagramUsesPort(editor, sourceComponent);
		SWTBotGefEditPart providesEditPart = EditorTestUtils.getDiagramProvidesPort(editor, targetComponent);

		// Confirm that no connections currently exist
		Diagram diagram = DUtil.findDiagram(sourceContainerShape);
		Assert.assertTrue("No connections should exist", diagram.getConnections().isEmpty());

		// Attempt to make an illegal connection and confirm that it was not actually made
		// TODO do this test
		
		// Draw the connection and save
		editor.activateTool("Connection");
		EditorTestUtils.getDiagramPortAnchor(usesEditPart).click();
		EditorTestUtils.getDiagramPortAnchor(providesEditPart).click();
		bot.menu("File").menu("Save").click();

		// Test to make sure connection was made correctly
		Assert.assertFalse("Connection should exist", diagram.getConnections().isEmpty());
		Connection connection = DUtil.getIncomingConnectionsContainedInContainerShape(targetContainerShape).get(0);
		UsesPortStub usesPort = (UsesPortStub) DUtil.getBusinessObject(connection.getStart());
		Assert.assertEquals("Connection uses port not correct", usesPort, DUtil.getBusinessObject((ContainerShape) usesEditPart.part().getModel()));
		ProvidesPortStub providesPort = (ProvidesPortStub) DUtil.getBusinessObject(connection.getEnd());
		Assert.assertEquals("Connect provides port not correct", providesPort, DUtil.getBusinessObject((ContainerShape) providesEditPart.part().getModel()));
		
		// Delete connection TODO
//		System.out.println("Main: " + editor.mainEditPart());
		Matcher<? extends EditPart> matcher = instanceOf(sourceComponentEditPart.part().getClass());
		List<SWTBotGefEditPart> allParts = editor.editParts(matcher);
		for (SWTBotGefEditPart part : allParts) {
			List<SWTBotGefConnectionEditPart> con = part.targetConnections();
			con.get(0).click();
			editor.clickContextMenu("");
		}
		SWTBotGefEditPart root = editor.rootEditPart();
		
		
//		EditorTestUtils.deleteFromPalette(editor, partToDelete);
//		Assert.assertTrue("All connections should have been deleted", diagram.getConnections().isEmpty());
		
		// TODO junit assertion test to confirm connection created successfully
		// confirm earlier in the test that no connections exists in the diagram
		// using DUtil.getOutogingConnections & Dutil.getIncomingConnections
		// or DUtil.findDigram(containerShape).getConnections
		// or both
		// confirm that a connection PE element now exists between elements using DUtil methods
		// get the PE and check the start and end
		// get the business object and check that it is accurate as well
		// TODO junit test for bad connections
		// maybe do this first
		// test trying to make impossible connections, and then DUtil.findDigram(containerShape).getConnections
		// to make sure no connection was made
		// include out-to-out and in-to-in
		// test making unrecommended connections and look for color/style change
		// redundant connections
		// double to long or something like that
		// use data-converter
		// TODO Follow this test with a reconnect feature test trying the same bad connections as above
		// Test in it's own method below
		// TODO Follow with a delete connection test
		// Test in it's own method below

	}

	/**
	 * IDE-697
	 * Users should be able to take existing connections and change them
	 * via drag-and-drop, without being able to create an invalid connection in the process
	 */
	@Test
	public void reconnectFeatureTest() {
		// TODO Create this test
	}

	@AfterClass
	public static void cleanUp() {
		gefBot.sleep(2000);
	}

}
