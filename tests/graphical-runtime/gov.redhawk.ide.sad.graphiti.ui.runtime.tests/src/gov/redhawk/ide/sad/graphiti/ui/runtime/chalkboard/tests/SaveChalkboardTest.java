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
package gov.redhawk.ide.sad.graphiti.ui.runtime.chalkboard.tests;

import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.SWTBotRadioMenu;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.List;

import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

public class SaveChalkboardTest extends AbstractGraphitiChalkboardTest {

	private static final String[] CHALKBOARD_PARENT_PATH = { "Sandbox" };
	private static final String CHALKBOARD = "Chalkboard";
	private static final String HARD_LIMIT = "HardLimit";
	private static final String SIGGEN = "SigGen";
	private SWTBotGefEditor editor;

	/**
	 * IDE-684
	 * Save the Graphiti chalkboard as a waveform
	 */
	@Test
	public void saveChalkboardAsWaveform() {
		final String WAVEFORM_NAME = "ChalkboardAsWaveform";

		// Open Chalkboard Graphiti Diagram
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, CHALKBOARD_PARENT_PATH, CHALKBOARD, DiagramType.GRAPHITI_CHALKBOARD);
		editor = gefBot.gefEditor(CHALKBOARD);
		editor.setFocus();

		// Add component to diagram from palette
		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARD_LIMIT, 200, 0);
		SWTBotGefEditPart usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN);
		SWTBotGefEditPart providesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);

		// Save waveform
		bot.menu("File").menu("Save As...").click();
		SWTBotShell saveShell = bot.shell("Save Chalkboard");
		saveShell.setFocus();
		saveShell.bot().textWithLabel("Project name:").setText(WAVEFORM_NAME);
		saveShell.bot().button("Finish").click();

		// Validate new waveform
		bot.waitUntil(Conditions.shellCloses(saveShell));
		bot.closeAllEditors();
		bot.waitUntil(new DefaultCondition() {

			@Override
			public String getFailureMessage() {
				return "New Waveform does not appear in the project explorer view";
			}
			
			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem waveformNode = gefBot.viewById("org.eclipse.ui.navigator.ProjectExplorer").bot().tree().getTreeItem(WAVEFORM_NAME);
				return waveformNode != null;
			}
			
		});
		
		SWTBotView projectView = gefBot.viewById("org.eclipse.ui.navigator.ProjectExplorer");
		SWTBotTreeItem waveformNode = projectView.bot().tree().getTreeItem(WAVEFORM_NAME);
		SWTBotTreeItem waveformSadXml = waveformNode.expand().getNode(WAVEFORM_NAME + ".sad.xml").select();
		SWTBotMenu menu = waveformSadXml.contextMenu("Open With").menu("Graphiti Waveform Editor");
		new SWTBotRadioMenu(menu).click();
		
		// confirm that components and connection exist
		editor = gefBot.gefEditor(WAVEFORM_NAME);
		Assert.assertNotNull(SIGGEN + " component was not found", editor.getEditPart(SIGGEN));
		Assert.assertNotNull(HARD_LIMIT + " component was not found", editor.getEditPart(HARD_LIMIT));
		
		usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN);
		providesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT);
		List<SWTBotGefConnectionEditPart> sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		Assert.assertFalse("Source connections should not be empty for this test", sourceConnections.isEmpty());
		
		ContainerShape hardLimitContainerShape = (ContainerShape) editor.getEditPart(HARD_LIMIT).part().getModel();
		Connection connection = DUtil.getIncomingConnectionsContainedInContainerShape(hardLimitContainerShape).get(0);
		
		UsesPortStub usesPort = (UsesPortStub) DUtil.getBusinessObject(connection.getStart());
		Assert.assertEquals("Connection uses port not correct", usesPort, DUtil.getBusinessObject((ContainerShape) usesEditPart.part().getModel()));

		ProvidesPortStub providesPort = (ProvidesPortStub) DUtil.getBusinessObject(connection.getEnd());
		Assert.assertEquals("Connect provides port not correct", providesPort, DUtil.getBusinessObject((ContainerShape) providesEditPart.part().getModel()));
		
	}
	
	/**
	 * IDE-963
	 * Testing key binding of Ctrl-S to Save As
	 * @throws AWTException 
	 */
	@Test
	public void saveAsHotkeyTest() throws AWTException {
		// Open Chalkboard Graphiti Diagram
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, CHALKBOARD_PARENT_PATH, CHALKBOARD, DiagramType.GRAPHITI_CHALKBOARD);
		editor = gefBot.gefEditor(CHALKBOARD);
		editor.setFocus();

		// Add component to diagram from palette
		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, SIGGEN);
		
		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_S);
		robot.keyRelease(KeyEvent.VK_S);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		
		SWTBotShell saveShell = bot.shell("Save Chalkboard");
		Assert.assertNotNull("Save As dialog not found", saveShell);
		saveShell.setFocus();
		saveShell.bot().button("Cancel").click();
	}
}
