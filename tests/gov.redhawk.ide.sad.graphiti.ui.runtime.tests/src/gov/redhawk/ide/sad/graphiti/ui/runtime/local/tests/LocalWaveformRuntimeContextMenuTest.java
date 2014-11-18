/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.sad.graphiti.ui.runtime.local.tests;

import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Assert;
import org.junit.Test;

public class LocalWaveformRuntimeContextMenuTest extends AbstractGraphitiLocalWaveformRuntimeTest {

	private SWTBotGefEditor editor;
	private static final String[] LOCAL_WAVEFORM_PARENT_PATH = {"Sandbox"};
	private static final String LOCAL_WAVEFORM = "ExampleWaveform01";
	private static final String SIGGEN = "SigGen";
	private static final String SIGGEN_1 = SIGGEN + "_1";

	/**
	 * IDE-661, IDE-662, IDE-663, IDE-664, IDE-665, IDE-666, IDE-667
	 * Test that context menu options appear in Graphiti during runtime,
	 * ensures that the proper views appear based on selection and that views are interactive
	 */
	@Test
	public void runtimeContextMenuTest() {
		//Launch Local Waveform From Target SDR
		ScaExplorerTestUtils.launchWaveformFromTargetSDR(gefBot, LOCAL_WAVEFORM);
		
		//wait until local waveform appears in ScaExplorer Sandbox
		ScaExplorerTestUtils.waitUntilWaveformAppearsInScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM);
		
		// Open Chalkboard Diagram
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM);
		String waveFormFullName = ScaExplorerTestUtils.getWaveformFullNameFromScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM);
		editor = gefBot.gefEditor(waveFormFullName);
		editor.setFocus();

		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		
		//wait for SIGGEN_1 to show up in Sca Explorer
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, SIGGEN_1);

		// Start the component
		DiagramTestUtils.startComponentFromDiagram(editor, SIGGEN);
		//wait until its started
		ScaExplorerTestUtils.waitUntilComponentAppearsStartedInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, SIGGEN_1);
		//plot port data for SIGGEN
		editor.setFocus();
		DiagramTestUtils.plotPortDataOnComponentPort(editor, SIGGEN, null);
		//close plot view
		SWTBotView plotView = ViewUtils.getPlotView(bot);
		plotView.close();

		// SRI view test
		DiagramTestUtils.displaySRIDataOnComponentPort(editor, SIGGEN, null);
		//verify sriView displayed
		ViewUtils.waitUntilSRIViewPopulates(bot);
		SWTBotView sriView = ViewUtils.getSRIView(bot);
		Assert.assertEquals("streamID property is missing for column 1", "streamID: ", sriView.bot().tree().cell(0, "Property: "));
		Assert.assertEquals("streamID property is wrong", SIGGEN + " Stream", sriView.bot().tree().cell(0, "Value: "));
		sriView.close();

		// Audio/Play port view test
		DiagramTestUtils.playPortDataOnComponentPort(editor, SIGGEN, null);
		//wait until audio view populates
		ViewUtils.waitUntilAudioViewPopulates(bot);
		//get audio view
		SWTBotView audioView = ViewUtils.getAudioView(bot);
		String item = audioView.bot().list().getItems()[0];
		Assert.assertTrue("SigGen not found in Audio Port Playback", item.matches(SIGGEN + ".*"));
		audioView.close();

		
		//open data list view
		DiagramTestUtils.displayDataListViewOnComponentPort(editor, SIGGEN, null);
		//verify data list view opens
		ViewUtils.waitUntilDataListViewDisplays(bot);
		//start acquire
		ViewUtils.startAquireOnDataListView(bot);
		//wait until view populates
		ViewUtils.waitUntilDataListViewPopulates(bot);
		//close data list view
		SWTBotView dataListView = ViewUtils.getDataListView(bot);
		dataListView.close();

		// Snapshot view test
		DiagramTestUtils.displaySnapshotDialogOnComponentPort(editor, SIGGEN, null);
		//wait until Snapshot dialog appears
		ViewUtils.waitUntilSnapshotDialogDisplays(bot);
		//get snapshot dialog
		SWTBotShell snapshotDialog = ViewUtils.getSnapshotDialog(bot);
		Assert.assertNotNull(snapshotDialog);
		snapshotDialog.close();

		// Monitor ports test
		DiagramTestUtils.displayPortMonitorViewOnComponentPort(editor, SIGGEN, null);
		//wait until port monitor view appears
		ViewUtils.waitUntilPortMonitorViewPopulates(bot, SIGGEN);
		//close PortMonitor View
		SWTBotView monitorView = ViewUtils.getPortMonitorView(bot);
		monitorView.close();

		//stop component
		DiagramTestUtils.stopComponentFromDiagram(editor, SIGGEN);
		ScaExplorerTestUtils.waitUntilComponentAppearsStoppedInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, SIGGEN_1);
	}

	/**
	 * IDE-326
	 * Test that certain context menu option don't appear in Graphiti during runtime,
	 */
	@Test
	public void removeDevelopmentContextOptionsTest() {
		//Launch Local Waveform From Target SDR
		ScaExplorerTestUtils.launchWaveformFromTargetSDR(gefBot, LOCAL_WAVEFORM);
		
		//wait until local waveform appears in ScaExplorer Sandbox
		ScaExplorerTestUtils.waitUntilWaveformAppearsInScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM);
		
		// Open Chalkboard Diagram
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM);
		String waveFormFullName = ScaExplorerTestUtils.getWaveformFullNameFromScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM);
		editor = gefBot.gefEditor(waveFormFullName);
		editor.setFocus();

		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, SIGGEN_1);
		

		// Make sure start order and assembly controller context options don't exist
		editor.getEditPart(SIGGEN).select();
		String[] removedContextOptions = { "Set As Assembly Controller", "Move Start Order Earlier", "Move Start Order Later" };
		for (String contextOption : removedContextOptions) {
			try {
				editor.clickContextMenu(contextOption);
				Assert.fail(); // The only way to get here is if the undesired context menu option appears
			} catch (WidgetNotFoundException e) {
				Assert.assertEquals(e.getMessage(), contextOption, e.getMessage());
			}
		}
	}
}
