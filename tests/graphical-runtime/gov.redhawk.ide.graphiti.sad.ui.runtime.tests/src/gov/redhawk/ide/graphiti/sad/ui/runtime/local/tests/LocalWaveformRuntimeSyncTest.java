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
package gov.redhawk.ide.graphiti.sad.ui.runtime.local.tests;

import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

import java.util.List;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.junit.Test;

/**
 * Tests that actions performed on a local sandbox waveform in the diagram get reflected in the SCA Explorer view, and
 * vice versa.
 */
public class LocalWaveformRuntimeSyncTest extends AbstractGraphitiLocalWaveformRuntimeTest {
	
	private SWTBotGefEditor editor;
	private static final String[] LOCAL_WAVEFORM_PARENT_PATH = {"Sandbox"};
	private static final String LOCAL_WAVEFORM = "ExampleWaveform01";
	private static final String HARD_LIMIT = "HardLimit";
	private static final String HARD_LIMIT_1 = "HardLimit_1";
	private static final String SIG_GEN = "SigGen";
	private static final String SIG_GEN_1 = "SigGen_1";

	/**
	 * Adds, then removes a component via diagram. Verify the SCA Explorer reflects actions.
	 */
	@Test
	public void addRemoveComponentInDiagram() {
		editor = gefBot.gefEditor(getWaveFormFullName());
		editor.setFocus();
		
		// Add component to diagram from palette
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARD_LIMIT, 0, 0);
		
		//wait for component to show up in ScaExplorer
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, HARD_LIMIT_1);
				
		//delete component from diagram
		DiagramTestUtils.releaseFromDiagram(editor, editor.getEditPart(HARD_LIMIT));
		
		//wait until hard limit component not present in ScaExplorer
		ScaExplorerTestUtils.waitUntilComponentDisappearsInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, HARD_LIMIT_1);
	}
	
	/**
	 * IDE-659
	 * Adds, then removes a port connection via chalkboard diagram. Verify the SCA Explorer reflects actions.
	 */
	@Test
	public void addRemoveComponentConnectionInDiagram() {
		editor = gefBot.gefEditor(getWaveFormFullName());
		editor.setFocus();

		// Add two components to diagram from palette
		final String sourceComponent = SIG_GEN;
		final String targetComponent = HARD_LIMIT;
		DiagramTestUtils.dragFromPaletteToDiagram(editor, sourceComponent, 0, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, targetComponent, 300, 0);
		
		//wait for component to show up in ScaExplorer (connections don't always work correctly if you don't wait.
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, SIG_GEN_1);
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, HARD_LIMIT_1);
		
		// Get port edit parts
		SWTBotGefEditPart usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, sourceComponent);
		SWTBotGefEditPart providesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, targetComponent);

		// Draw connection
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);

		//wait for connection to show up in ScaExplorer
		ScaExplorerTestUtils.waitUntilConnectionDisplaysInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, SIG_GEN_1, "out", "connection_1");
				
		// Delete connection
		List<SWTBotGefConnectionEditPart> sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		for (SWTBotGefConnectionEditPart con : sourceConnections) {
			DiagramTestUtils.deleteFromDiagram(editor, con);
		}
		
		//wait until connection not present in ScaExplorer
		ScaExplorerTestUtils.waitUntilConnectionDisappearsInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, SIG_GEN_1, "out", "connection_1");
	}
	
	/**
	 * IDE-659
	 * Adds components, starts/stops them from Diagram. Verify the SCA Explorer reflects actions.
	 */
	@Test
	public void startStopComponentsFromDiagram() {
		editor = gefBot.gefEditor(getWaveFormFullName());
		editor.setFocus();

		// Add two components to diagram from palette
		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIG_GEN, 0, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARD_LIMIT, 300, 0);

		//wait for component to show up in ScaExplorer
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, SIG_GEN_1);
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, HARD_LIMIT_1);
		
		//verify hard limit stopped
		ScaExplorerTestUtils.waitUntilComponentAppearsStoppedInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, HARD_LIMIT_1);

		//start hard limit
		DiagramTestUtils.startComponentFromDiagram(editor, HARD_LIMIT);
		
		//verify hardlimit started but siggen did not
		ScaExplorerTestUtils.waitUntilComponentAppearsStartedInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilComponentAppearsStoppedInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, SIG_GEN_1);
		
		//start SigGen
		DiagramTestUtils.startComponentFromDiagram(editor, SIG_GEN);
		
		//verify SigGen started
		ScaExplorerTestUtils.waitUntilComponentAppearsStartedInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, SIG_GEN_1);
		
		//stop hard limit
		DiagramTestUtils.stopComponentFromDiagram(editor, HARD_LIMIT);
		
		//verify hardlimit stopped, SigGen started
		ScaExplorerTestUtils.waitUntilComponentAppearsStoppedInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilComponentAppearsStartedInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, SIG_GEN_1);
		
		//stop SigGen
		DiagramTestUtils.stopComponentFromDiagram(editor, SIG_GEN);
		
		//verify SigGen stopped
		ScaExplorerTestUtils.waitUntilComponentAppearsStoppedInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, SIG_GEN_1);
		
		//start both components
		DiagramTestUtils.startComponentFromDiagram(editor, HARD_LIMIT);
		DiagramTestUtils.startComponentFromDiagram(editor, SIG_GEN);
		
		//verify both started
		ScaExplorerTestUtils.waitUntilComponentAppearsStartedInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilComponentAppearsStartedInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, SIG_GEN_1);
	}
	
	/**
	 * IDE-659
	 * Adds, then removes component connections via SCA Explorer.  Verify its no
	 * longer present in Diagram
	 */
	@Test
	public void addRemoveComponentConnectionInScaExplorer() {
		editor = gefBot.gefEditor(getWaveFormFullName());
		editor.setFocus();

		// Add two components to diagram from palette
		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIG_GEN, 0, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARD_LIMIT, 300, 0);

		//verify components were added to sca explorer
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, SIG_GEN_1);
		
		//create connection between components via Sca Explorer
		ScaExplorerTestUtils.connectComponentPortsInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, getWaveFormFullName(), "connection_1", SIG_GEN_1, "out",
			HARD_LIMIT_1, "dataDouble_in");
		
		//verify connection exists in diagram
		DiagramTestUtils.waitUntilConnectionDisplaysInDiagram(bot, editor, HARD_LIMIT_1);
		
		//disconnect connection_1 via Sca Explorer 
		ScaExplorerTestUtils.disconnectConnectionInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, getWaveFormFullName(), "connection_1", SIG_GEN_1, "out");
		
		//verify connection does NOT exist in diagram
		DiagramTestUtils.waitUntilConnectionDisappearsInDiagram(bot, editor, HARD_LIMIT_1);
	}
	
	/**
	 * IDE-659
	 * Adds components, starts/stops them from ScaExplorer and verifies
	 * components in diagram reflect appropriate color changes
	 * 
	 */
	@Test
	public void startStopComponentsFromScaExplorer() {
		editor = gefBot.gefEditor(getWaveFormFullName());
		editor.setFocus();

		// Launch two components from TargetSDR
		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIG_GEN, 0, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARD_LIMIT, 300, 0);

		//verify components were added to the diagram
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, HARD_LIMIT);
		DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, SIG_GEN);
		
		//verify hard limit stopped
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, HARD_LIMIT);

		//start hard limit from sca explorer
		ScaExplorerTestUtils.startComponentFromScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, HARD_LIMIT_1);
		
		//verify hardlimit started but siggen did not
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, HARD_LIMIT);
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, SIG_GEN);
		
		//start SigGen from sca explorer
		ScaExplorerTestUtils.startComponentFromScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, SIG_GEN_1);
		
		//verify SigGen started but siggen did not
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, SIG_GEN);
		
		//stop hard limit from sca explorer
		ScaExplorerTestUtils.stopComponentFromScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, HARD_LIMIT_1);
		
		//verify hardlimit stopped, SigGen started
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, HARD_LIMIT);
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, SIG_GEN);
		
		//stop SigGen from sca explorer
		ScaExplorerTestUtils.stopComponentFromScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, SIG_GEN_1);
		
		//verify SigGen stopped
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, SIG_GEN);
		
		//start both components
		ScaExplorerTestUtils.startComponentFromScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, HARD_LIMIT_1);
		ScaExplorerTestUtils.startComponentFromScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, SIG_GEN_1);
		
		//verify both started
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, HARD_LIMIT);
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, SIG_GEN);
		
		//stop chalkboard
		ScaExplorerTestUtils.stopWaveformFromScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM);
		
		//verify both components stopped
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, HARD_LIMIT);
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, SIG_GEN);
		
		//start chalkboard
		ScaExplorerTestUtils.startWaveformFromScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM);
		
		//verify both components started
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, HARD_LIMIT);
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, SIG_GEN);
	}
}
