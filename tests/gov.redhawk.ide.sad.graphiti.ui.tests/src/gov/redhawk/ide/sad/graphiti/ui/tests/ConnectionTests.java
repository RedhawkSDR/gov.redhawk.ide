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
import gov.redhawk.ide.swtbot.tests.utils.WaveformUtils;
import gov.redhawk.ide.swtbot.tests.utils.EditorTestUtils;
import gov.redhawk.ide.swtbot.tests.utils.MenuUtils;

import java.util.List;

import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
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
	private SWTGefBot gefBot;
	private SWTBotGefEditor editor;

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
	
	@After
	public void afterTest() {
		for (SWTBotEditor e : gefBot.editors()) {
			e.close();
		}
		gefBot.sleep(2000);
	}
	
	@AfterClass
	public static void afterClass() {
		
	}

	/**
	 * IDE-731
	 * Users should be able to take create connections between components in the Graphiti diagram
	 */
	@Test
	public void connectFeatureTest() {
		final String waveformName = "IDE-731-Test";
		final String sourceComponent = "SigGen";
		final String targetComponent = "HardLimit";
		
		// Create an empty waveform project
		WaveformUtils.createNewWaveform(gefBot, waveformName);

		// Add component to diagram from palette
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

		// Attempt to make an illegal connection and confirm that it was not actually made
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
		
		// Close the editor and delete the waveform project
		MenuUtils.closeAllWithoutSave(bot);
		MenuUtils.deleteNodeInProjectExplorer(bot, waveformName);

		// TODO junit test for bad connections
			// test making unrecommended connections and look for color/style change
			// redundant connections
			// double to long or something like that
			// use data-converter

		// TODO Follow this test with a reconnect feature test trying the same bad connections as above
			// Test in it's own method below
	}

	@AfterClass
	public static void cleanUpClass() {
		
	}

}
