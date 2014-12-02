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

import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

import java.util.List;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.junit.Assert;
import org.junit.Test;

public class ChalkboardSyncTest extends AbstractGraphitiChalkboardTest {

	private SWTBotGefEditor editor;
	private static final String[] CHALKBOARD_PARENT_PATH = { "Sandbox" };
	private static final String CHALKBOARD = "Chalkboard";
	private static final String HARD_LIMIT = "HardLimit";
	private static final String HARD_LIMIT_1 = "HardLimit_1";
	private static final String SIG_GEN = "SigGen";
	private static final String SIG_GEN_1 = "SigGen_1";

	/**
	 * IDE-659
	 * Adds, then removes a component via chalkboard diagram. Verify its no
	 * longer present in ScaExplorer Chalkboard or Diagram
	 */
	@Test
	public void addRemoveComponentInChalkboardDiagram() {

		// Open Chalkboard Diagram
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, CHALKBOARD_PARENT_PATH, CHALKBOARD);
		editor = gefBot.gefEditor(CHALKBOARD);
		editor.setFocus();

		// Add component to diagram from palette
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARD_LIMIT, 0, 0);

		// wait for component to show up in ScaExplorer Chalkboard
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, HARD_LIMIT_1);

		// RELEASE component from diagram
		DiagramTestUtils.releaseFromChalkboard(editor, editor.getEditPart(HARD_LIMIT));

		// wait until hard limit component not present in ScaExplorer Chalkboard & Diagram
		ScaExplorerTestUtils.waitUntilComponentDisappearsInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, HARD_LIMIT_1);
		Assert.assertNull(editor.getEditPart(HARD_LIMIT));
	}
	
	/**
	 * IDE-659
	 * Adds, then terminates a component via chalkboard diagram. Verify its no
	 * longer present in ScaExplorer Chalkboard or Diagram
	 */
	@Test
	public void addTerminateComponentInChalkboardDiagram() {

		// Open Chalkboard Diagram
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, CHALKBOARD_PARENT_PATH, CHALKBOARD);
		editor = gefBot.gefEditor(CHALKBOARD);
		editor.setFocus();

		// Add component to diagram from palette
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARD_LIMIT, 0, 0);

		// wait for component to show up in ScaExplorer Chalkboard
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, HARD_LIMIT_1);

		// TERMINATE component from diagram
		DiagramTestUtils.terminateFromChalkboard(editor, editor.getEditPart(HARD_LIMIT));

		// wait until hard limit component not present in ScaExplorer Chalkboard & Diagram
		ScaExplorerTestUtils.waitUntilComponentDisappearsInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, HARD_LIMIT_1);
		Assert.assertNull(editor.getEditPart(HARD_LIMIT));
	}

	/**
	 * IDE-659
	 * Adds, then removes a component connections via chalkboard diagram. Verify its no
	 * longer present in ScaExplorer Chalkboard
	 */
	@Test
	public void addRemoveComponentConnectionInChalkboardDiagram() {

		// Open Chalkboard Diagram
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, CHALKBOARD_PARENT_PATH, CHALKBOARD);
		editor = gefBot.gefEditor(CHALKBOARD);
		editor.setFocus();

		// Add two components to diagram from palette
		final String sourceComponent = SIG_GEN;
		final String targetComponent = HARD_LIMIT;
		DiagramTestUtils.dragFromPaletteToDiagram(editor, sourceComponent, 0, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, targetComponent, 300, 0);

		// wait for component to show up in ScaExplorer Chalkboard (connections don't always work correctly if you don't
		// wait.
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, SIG_GEN_1);
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, HARD_LIMIT_1);

		// Get port edit parts
		SWTBotGefEditPart usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, sourceComponent);
		SWTBotGefEditPart providesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, targetComponent);

		// Draw connection
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);

		// wait for connection to show up in ScaExplorer Chalkboard
		ScaExplorerTestUtils.waitUntilConnectionDisplaysInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, SIG_GEN_1, "out", "connection_1");

		// Delete connection
		List<SWTBotGefConnectionEditPart> sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		for (SWTBotGefConnectionEditPart con : sourceConnections) {
			DiagramTestUtils.deleteFromDiagram(editor, con);
		}

		// wait until connection not present in ScaExplorer Chalkboard
		ScaExplorerTestUtils.waitUntilConnectionDisappearsInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, SIG_GEN_1, "out", "connection_1");
	}

	/**
	 * IDE-659
	 * Adds components, starts/stops them from Chalkboard Diagram and verifies
	 * components in ScaExplorer Chalkboard reflect changes
	 * 
	 */
	@Test
	public void startStopComponentsFromChalkboardDiagram() {

		// Open Chalkboard Diagram
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, CHALKBOARD_PARENT_PATH, CHALKBOARD);
		editor = gefBot.gefEditor(CHALKBOARD);
		editor.setFocus();

		// Add two components to diagram from palette
		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIG_GEN, 0, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARD_LIMIT, 300, 0);

		// wait for component to show up in ScaExplorer Chalkboard
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, SIG_GEN_1);
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, HARD_LIMIT_1);

		// verify hard limit stopped
		ScaExplorerTestUtils.waitUntilComponentAppearsStoppedInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, HARD_LIMIT_1);

		// start hard limit
		DiagramTestUtils.startComponentFromDiagram(editor, HARD_LIMIT);

		// verify hardlimit started but siggen did not
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, HARD_LIMIT);
		ScaExplorerTestUtils.waitUntilComponentAppearsStartedInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilComponentAppearsStoppedInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, SIG_GEN_1);

		// start SigGen
		DiagramTestUtils.startComponentFromDiagram(editor, SIG_GEN);

		// verify SigGen started but siggen did not
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, SIG_GEN);
		ScaExplorerTestUtils.waitUntilComponentAppearsStartedInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, SIG_GEN_1);

		// stop hard limit
		DiagramTestUtils.stopComponentFromDiagram(editor, HARD_LIMIT);

		// verify hardlimit stopped, SigGen started
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, HARD_LIMIT);
		ScaExplorerTestUtils.waitUntilComponentAppearsStoppedInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilComponentAppearsStartedInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, SIG_GEN_1);

		// stop SigGen
		DiagramTestUtils.stopComponentFromDiagram(editor, SIG_GEN);

		// verify SigGen stopped
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, SIG_GEN);
		ScaExplorerTestUtils.waitUntilComponentAppearsStoppedInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, SIG_GEN_1);

		// start both components
		DiagramTestUtils.startComponentFromDiagram(editor, HARD_LIMIT);
		DiagramTestUtils.startComponentFromDiagram(editor, SIG_GEN);

		// verify both started
		ScaExplorerTestUtils.waitUntilComponentAppearsStartedInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilComponentAppearsStartedInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, SIG_GEN_1);

		// cleanup
		ScaExplorerTestUtils.terminateWaveformFromScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD);

	}

	/**
	 * IDE-659
	 * Adds, then removes a component via ScaExplorer Chalkboard. Verify its no
	 * longer present in Diagram
	 */
	@Test
	public void addRemoveComponentInScaExplorer() {

		// Open Chalkboard Diagram
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, CHALKBOARD_PARENT_PATH, CHALKBOARD);
		editor = gefBot.gefEditor(CHALKBOARD);
		editor.setFocus();

		// Launch component from TargetSDR
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, HARD_LIMIT, "python");

		// verify HardLimit was added to the diagram
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, HARD_LIMIT);

		// delete component from ScaExplorer chalkboard
		ScaExplorerTestUtils.terminateComponentInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, HARD_LIMIT);

		// verify hard limit component not present in Chalkboard Diagram
		DiagramTestUtils.waitUntilComponentDisappearsInChalkboardDiagram(bot, editor, HARD_LIMIT);

		// Launch component from TargetSDR
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, HARD_LIMIT, "python");

		// verify HardLimit was added to the diagram
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, HARD_LIMIT);

		// terminate chalkboard
		ScaExplorerTestUtils.terminateWaveformFromScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD);

		// verify hard limit component not present in Chalkboard Diagram
		DiagramTestUtils.waitUntilComponentDisappearsInChalkboardDiagram(bot, editor, HARD_LIMIT);
	}

	/**
	 * IDE-659
	 * Adds, then removes component connections via SCA Explorer Chalkboard. Verify its no
	 * longer present in Chalkboard Diagram
	 */
	@Test
	public void addRemoveComponentConnectionInScaExplorer() {

		// Open Chalkboard Diagram
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, CHALKBOARD_PARENT_PATH, CHALKBOARD);
		editor = gefBot.gefEditor(CHALKBOARD);
		editor.setFocus();

		// Launch two components from TargetSDR
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, HARD_LIMIT, "python");
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, SIG_GEN, "python");

		// verify components were added to the diagram
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, HARD_LIMIT);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, SIG_GEN);

		// create connection between components via Sca Explorer Chalkboard
		ScaExplorerTestUtils.connectComponentPortsInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, "connection_1", SIG_GEN, "out", HARD_LIMIT,
			"dataDouble_in");

		// verify connection exists in diagram
		DiagramTestUtils.waitUntilConnectionDisplaysInDiagram(bot, editor, HARD_LIMIT);

		// disconnect connection_1 via Sca Explorer Chalkboard
		ScaExplorerTestUtils.disconnectConnectionInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, "connection_1", SIG_GEN, "out");

		// verify connection does NOT exist in diagram
		DiagramTestUtils.waitUntilConnectionDisappearsInDiagram(bot, editor, HARD_LIMIT);
	}

	/**
	 * IDE-659
	 * Adds components, starts/stops them from ScaExplorer Chalkboard and verifies
	 * components in diagram reflect appropriate color changes
	 * 
	 */
	@Test
	public void startStopComponentsFromScaExplorer() {

		// Open Chalkboard Diagram
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, CHALKBOARD_PARENT_PATH, CHALKBOARD);
		editor = gefBot.gefEditor(CHALKBOARD);
		editor.setFocus();

		// Launch two components from TargetSDR
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, HARD_LIMIT, "python");
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, SIG_GEN, "python");

		// verify components were added to the diagram
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, HARD_LIMIT);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, SIG_GEN);

		// verify hard limit stopped
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, HARD_LIMIT);

		// start hard limit from sca explorer
		ScaExplorerTestUtils.startComponentFromScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, HARD_LIMIT);

		// verify hardlimit started but siggen did not
		ScaExplorerTestUtils.waitUntilComponentAppearsStartedInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, HARD_LIMIT);
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, HARD_LIMIT);
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, SIG_GEN);

		// start SigGen from sca explorer
		ScaExplorerTestUtils.startComponentFromScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, SIG_GEN);

		// verify SigGen started but siggen did not
		ScaExplorerTestUtils.waitUntilComponentAppearsStartedInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, SIG_GEN);
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, SIG_GEN);

		// stop hard limit from sca explorer
		ScaExplorerTestUtils.stopComponentFromScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, HARD_LIMIT);

		// verify hardlimit stopped, SigGen started
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, HARD_LIMIT);
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, SIG_GEN);

		// stop SigGen from sca explorer
		ScaExplorerTestUtils.stopComponentFromScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, SIG_GEN);

		// verify SigGen stopped
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, SIG_GEN);

		// start both components
		ScaExplorerTestUtils.startComponentFromScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, HARD_LIMIT);
		ScaExplorerTestUtils.startComponentFromScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, SIG_GEN);

		// verify both started
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, HARD_LIMIT);
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, SIG_GEN);

		// stop chalkboard
		ScaExplorerTestUtils.stopWaveformFromScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD);

		// verify both components stopped
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, HARD_LIMIT);
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, SIG_GEN);

		// start chalkboard
		ScaExplorerTestUtils.startWaveformFromScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD);

		// verify both components started
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, HARD_LIMIT);
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, SIG_GEN);

		// terminate, then stop chalkboard
		ScaExplorerTestUtils.terminateWaveformFromScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD);

		// wait for Chalkboard to be empty
		ScaExplorerTestUtils.waitUntilScaExplorerWaveformEmpty(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD);

		// stop chalkboard
		ScaExplorerTestUtils.stopWaveformFromScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD);

		// wait for Chalkboard to be stopped
		ScaExplorerTestUtils.waitUntilScaExplorerWaveformStopped(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD);

	}

}
