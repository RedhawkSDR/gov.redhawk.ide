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
package gov.redhawk.ide.graphiti.sad.ui.runtime.domain.tests;

import static org.junit.Assert.assertEquals;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.WaitForEditorCondition;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.logging.ui.LogLevels;

import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Assert;
import org.junit.Test;

public class WaveformExplorerTest extends AbstractGraphitiDomainWaveformRuntimeTest {

	private static final String SIGGEN = "SigGen";
	private static final String SIGGEN_1 = "SigGen_1";
	private static final String HARD_LIMIT = "HardLimit";
	private static final String HARD_LIMIT_1 = "HardLimit_1";
	private static final String DATA_READER = "DataReader";

	/**
	 * IDE-969 Opens Graphiti diagram using the Waveform Explorer editor.
	 * This editor is "look but don't touch". All design functionality should be disabled.
	 * Runtime functionality (start/stop, plot, etc) should still work.
	 * IDE-1001 Hide grid on runtime diagram.
	 */
	@Test
	public void waveformExplorerTest() {
		SWTBotGefEditor editor = gefBot.gefEditor(getWaveFormFullName());
		editor.setFocus();

		// check for components
		SWTBotGefEditPart hardLimit = editor.getEditPart(HARD_LIMIT);
		Assert.assertNotNull(HARD_LIMIT + " component not found in diagram", hardLimit);

		// check that delete & design options don't appear
		hardLimit.select();
		String[] removedContextOptions = { "Delete", "Set As Assembly Controller", "Move Start Order Earlier", "Move Start Order Later" };
		for (String contextOption : removedContextOptions) {
			try {
				editor.clickContextMenu(contextOption);
				Assert.fail(); // The only way to get here is if the undesired context menu option appears
			} catch (WidgetNotFoundException e) {
				Assert.assertEquals(e.getMessage(), contextOption, e.getMessage());
			}
		}

		// check that start/plot/stop works
		DiagramTestUtils.startComponentFromDiagram(editor, HARD_LIMIT);
		ScaExplorerTestUtils.waitUntilComponentAppearsStartedInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName(), HARD_LIMIT_1);

		editor.setFocus();
		DiagramTestUtils.plotPortDataOnComponentPort(editor, HARD_LIMIT, null);
		SWTBotView plotView = ViewUtils.getPlotView(bot);
		plotView.close();

		DiagramTestUtils.stopComponentFromDiagram(editor, HARD_LIMIT);
		ScaExplorerTestUtils.waitUntilComponentAppearsStoppedInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName(), HARD_LIMIT_1);

		// check that DnD does not work - From Target SDR
		DiagramTestUtils.dragComponentFromTargetSDRToDiagram(gefBot, editor, DATA_READER);
		editor.setFocus();
		SWTBotGefEditPart dataReader = editor.getEditPart(DATA_READER);
		Assert.assertNull(DATA_READER + " component should not have be drawn in diagram", dataReader);

		// IDE-1001 check that grid is hidden on runtime diagram
		Diagram diagram = DiagramTestUtils.getDiagram(editor);
		Assert.assertNotNull("Found in Diagram (model object) on editor", diagram);
		int gridUnit = diagram.getGridUnit();
		assertEquals("Grid is hidden on diagram", -1, gridUnit); // -1 means it is hidden
	}
	
	/**
	 * IDE-1187 Opens Graphiti diagram using the Waveform Explorer editor.
	 * Diagram should contain namespaced components
	 */
	@Test
	public void waveformExplorerNamespaceComponentsTest() {
		final String comp1 = "comp_1";
		final String comp2 = "comp_2";
		
		bot.closeAllEditors();
		
		ScaExplorerTestUtils.launchWaveformFromDomain(bot, DOMAIN, NAMESPACE_DOMAIN_WAVEFORM);
		bot.waitUntil(new WaitForEditorCondition());
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, NAMESPACE_DOMAIN_WAVEFORM);
		setWaveFormFullName(ScaExplorerTestUtils.getFullNameFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, NAMESPACE_DOMAIN_WAVEFORM));
		
		SWTBotGefEditor editor = gefBot.gefEditor(getWaveFormFullName());
		editor.setFocus();

		// check for components
		Assert.assertNotNull(editor.getEditPart(comp1));
		Assert.assertNotNull(editor.getEditPart(comp2));
		SWTBotGefEditPart providesPort = DiagramTestUtils.getDiagramProvidesPort(editor, comp2);
		SWTBotGefEditPart providesAnchor = DiagramTestUtils.getDiagramPortAnchor(providesPort);
		Assert.assertTrue(providesAnchor.targetConnections().size() == 1);
	}

	@Test
	public void runtimeExplorerContextMenuTest() {
		SWTBotGefEditor editor = gefBot.gefEditor(getWaveFormFullName());
		editor.setFocus();

		// Start the component
		DiagramTestUtils.startComponentFromDiagram(editor, SIGGEN);
		ScaExplorerTestUtils.waitUntilComponentAppearsStartedInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName(), SIGGEN_1);

		// Check that we can't undo certain actions
		Assert.assertFalse("IDE-1038 No Undo Start Command context menu item", DiagramTestUtils.hasContentMenuItem(editor, SIGGEN, "Undo Start Command"));
		Assert.assertFalse("IDE-1065 No Undo Do Command context menu item", DiagramTestUtils.hasContentMenuItem(editor, SIGGEN, "Undo Do Command"));

		// Test Log Levels
		DiagramTestUtils.changeLogLevelFromDiagram(editor, SIGGEN, LogLevels.TRACE);
		DiagramTestUtils.confirmLogLevelFromDiagram(editor, SIGGEN, LogLevels.TRACE);

		DiagramTestUtils.changeLogLevelFromDiagram(editor, SIGGEN, LogLevels.FATAL);
		DiagramTestUtils.confirmLogLevelFromDiagram(editor, SIGGEN, LogLevels.FATAL);

		// plot port data for SIGGEN
		editor.setFocus();
		try {
			DiagramTestUtils.plotPortDataOnComponentPort(editor, SIGGEN, null);
			SWTBotView plotView = ViewUtils.getPlotView(bot);
			plotView.close();
		} catch (WidgetNotFoundException e) {
			SWTBotView plotView = ViewUtils.getPlotView(bot);
			plotView.close();
		}

		// SRI view test
		DiagramTestUtils.displaySRIDataOnComponentPort(editor, SIGGEN, null);
		// verify sriView displayed
		ViewUtils.waitUntilSRIViewPopulates(bot);
		SWTBotView sriView = ViewUtils.getSRIView(bot);
		Assert.assertEquals("streamID property is missing for column 1", "streamID: ", sriView.bot().tree().cell(0, "Property: "));
		Assert.assertEquals("streamID property is wrong", SIGGEN + " Stream", sriView.bot().tree().cell(0, "Value: "));
		sriView.close();

		// Audio/Play port view test
		DiagramTestUtils.playPortDataOnComponentPort(editor, SIGGEN, null);
		// wait until audio view populates
		ViewUtils.waitUntilAudioViewPopulates(bot);
		// get audio view
		SWTBotView audioView = ViewUtils.getAudioView(bot);
		String item = audioView.bot().list().getItems()[0];
		Assert.assertTrue("SigGen not found in Audio Port Playback", item.matches(SIGGEN + ".*"));
		audioView.close();

		// open data list view
		DiagramTestUtils.displayDataListViewOnComponentPort(editor, SIGGEN, null);
		// verify data list view opens
		ViewUtils.waitUntilDataListViewDisplays(bot);
		// start acquire
		ViewUtils.startAquireOnDataListView(bot);
		// wait until view populates
		ViewUtils.waitUntilDataListViewPopulates(bot);
		// close data list view
		SWTBotView dataListView = ViewUtils.getDataListView(bot);
		dataListView.close();

		// Snapshot view test
		DiagramTestUtils.displaySnapshotDialogOnComponentPort(editor, SIGGEN, null);
		// wait until Snapshot dialog appears
		ViewUtils.waitUntilSnapshotDialogDisplays(bot);
		// get snapshot dialog
		SWTBotShell snapshotDialog = ViewUtils.getSnapshotDialog(bot);
		Assert.assertNotNull(snapshotDialog);
		snapshotDialog.close();

		// Monitor ports test
		DiagramTestUtils.displayPortMonitorViewOnComponentPort(editor, SIGGEN, null);
		// wait until port monitor view appears
		ViewUtils.waitUntilPortMonitorViewPopulates(bot, SIGGEN);
		// close PortMonitor View
		SWTBotView monitorView = ViewUtils.getPortMonitorView(bot);
		monitorView.close();

		// stop component
		DiagramTestUtils.stopComponentFromDiagram(editor, SIGGEN);
		ScaExplorerTestUtils.waitUntilComponentAppearsStoppedInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, getWaveFormFullName(), SIGGEN_1);

		Assert.assertFalse("IDE-1038 No Undo Stop Command context menu item", DiagramTestUtils.hasContentMenuItem(editor, SIGGEN, "Undo Stop Command"));
		Assert.assertFalse("IDE-1065 No Undo Do Command context menu item", DiagramTestUtils.hasContentMenuItem(editor, SIGGEN, "Undo Do Command"));
	}

}
