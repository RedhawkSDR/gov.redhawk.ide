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
package gov.redhawk.ide.sad.graphiti.ui.runtime.tests;

import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class GraphitiContextMenuTest extends AbstractGraphitiRuntimeTest {

	private SWTBotGefEditor editor;
	private static final String CHALKBOARD = "Chalkboard";
	private static final String SIGGEN = "SigGen";
	private static final String HARD_LIMIT = "HardLimit";

	/**
	 * IDE-661, IDE-662, IDE-663, IDE-664, IDE-665, IDE-666, IDE-667
	 * Test that context menu options appear in Graphiti during runtime,
	 * ensures that the proper views appear based on selection and that views are interactive
	 */
	@Test
	public void runtimeContextMenuTest() {
		// Prepare Graphiti diagram
		System.out.println("STEP 1");
		SWTBotView scaExplorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		System.out.println("STEP 2");
		DiagramTestUtils.openChalkboardFromSandbox(gefBot);
		System.out.println("STEP 3");
		editor = gefBot.gefEditor(CHALKBOARD);
		System.out.println("STEP 4");
		editor.setFocus();

		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		final SWTBotTreeItem chalkboard = scaExplorerView.bot().tree().expandNode("Sandbox", "Chalkboard");
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return SIGGEN + " Component did not load into sandbox";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem[] items = chalkboard.getItems();
				for (SWTBotTreeItem item : items) {
					if (item.getText().equals(SIGGEN + "_1")) {
						return true;
					}
				}
				return false;
			}
		});

		// Start the component
		SWTBotGefEditPart sigGen = editor.getEditPart(SIGGEN);
		sigGen.select();
		editor.clickContextMenu("Start");
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return "SigGen did not start";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem[] items = chalkboard.getItems();
				for (SWTBotTreeItem item : items) {
					if (item.getText().equals(SIGGEN + "_1 STARTED")) {
						return true;
					}
				}
				return false;
			}
		});
		
		// Get the port
		SWTBotGefEditPart usesPort = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN);
		final SWTBotGefEditPart usesAnchor = DiagramTestUtils.getDiagramPortAnchor(usesPort);

		// Plot view test
		usesAnchor.select();
		editor.clickContextMenu("Plot Port Data");
		SWTBotView plotView = bot.viewById("gov.redhawk.ui.port.nxmplot.PlotView2");
		plotView.close();

		// SRI view test
		usesAnchor.select();
		editor.clickContextMenu("Display SRI");
		final SWTBotView sriView = bot.viewById("gov.redhawk.bulkio.ui.sridata.view");
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return "SRI View property rows did not populate";
			}

			@Override
			public boolean test() throws Exception {
				return sriView.bot().tree().rowCount() > 0;
			}
		});
		Assert.assertEquals("streamID property is missing for column 1", "streamID: ", sriView.bot().tree().cell(0, "Property: "));
		Assert.assertEquals("streamID property is wrong", SIGGEN + " Stream", sriView.bot().tree().cell(0, "Value: "));
		sriView.close();

		// Audio/Play port view test
		usesAnchor.select();
		editor.clickContextMenu("Play Port");
		SWTBotView audioView = bot.viewById("gov.redhawk.ui.port.playaudio.view");
		String item = audioView.bot().list().getItems()[0];
		Assert.assertTrue("SigGen not found in Audio Port Playback", item.matches(SIGGEN + ".*"));
		audioView.close();

		// Data List view test
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return "Could not find the Data List context menu option";
			}

			@Override
			public boolean test() throws Exception {
				usesAnchor.select();
				editor.clickContextMenu("Data List");
				SWTBotView view = gefBot.viewById("gov.redhawk.datalist.ui.views.DataListView");
				return (view != null);
			}
		});
		final SWTBotView dataListView = bot.viewById("gov.redhawk.datalist.ui.views.DataListView");
		SWTBotButton startButton = dataListView.bot().buttonWithTooltip("Start Acquire");
		startButton.click();
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return "Data List View did not populate";
			}

			@Override
			public boolean test() throws Exception {
				return dataListView.bot().table().rowCount() > 10;
			}
		});
		dataListView.close();

		// Snapshot view test
		usesAnchor.select();
		editor.clickContextMenu("Snapshot");
		SWTBotShell snapshotDialog = bot.shell("Snapshot");
		Assert.assertNotNull(snapshotDialog);
		snapshotDialog.close();

		// Monitor ports test
		usesAnchor.select();
		editor.clickContextMenu("Monitor Ports");
		final SWTBotView monitorView = bot.viewById("gov.redhawk.ui.views.monitor.ports.PortMonitorView");
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return SIGGEN + " component did not load into Port Monitor View";
			}

			@Override
			public boolean test() throws Exception {
				for (SWTBotTreeItem item : monitorView.bot().tree().getAllItems()) {
					if (item.getText().matches(SIGGEN + ".*")) {
						return true;
					}
				}
				return false;
			}
		});
		monitorView.close();

		// Stop component
		sigGen.select();
		editor.clickContextMenu("Stop");
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return "SigGen did not stop";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem[] items = chalkboard.getItems();
				for (SWTBotTreeItem item : items) {
					if (item.getText().equals(SIGGEN + "_1")) {
						return true;
					}
				}
				return false;
			}
		});
	}

	/**
	 * IDE-326
	 * Test that certain context menu option don't appear in Graphiti during runtime,
	 */
	@Test
	public void removeDevelopmentContextOptionsTest() {
		// Prepare Graphiti diagram
		System.out.println("STEP 1");
		SWTBotView scaExplorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		System.out.println("STEP 2");		
		DiagramTestUtils.openChalkboardFromSandbox(gefBot);
		System.out.println("STEP 3");
		editor = gefBot.gefEditor(CHALKBOARD);
		System.out.println("STEP 4");
		editor.setFocus();

		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		final SWTBotTreeItem chalkboard = scaExplorerView.bot().tree().expandNode("Sandbox", "Chalkboard");
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return SIGGEN + " Component did not load into sandbox";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem[] items = chalkboard.getItems();
				for (SWTBotTreeItem item : items) {
					if (item.getText().equals(SIGGEN + "_1")) {
						return true;
					}
				}
				return false;
			}
		});

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

	/**
	 * IDE-766
	 * The delete context menu should not appear when ports are selected
	 */
	@Test
	public void doNotDeletePortsTest() {
		// Prepare Graphiti diagram
		System.out.println("STEP 1");
		DiagramTestUtils.openChalkboardFromSandbox(gefBot);
		System.out.println("STEP 2");
		editor = gefBot.gefEditor(CHALKBOARD);
		System.out.println("STEP 3");
		editor.setFocus();

		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARD_LIMIT, 0, 0);
		SWTBotGefEditPart provides = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT);
		SWTBotGefEditPart uses = DiagramTestUtils.getDiagramUsesPort(editor, HARD_LIMIT);

		List<SWTBotGefEditPart> anchors = new ArrayList<SWTBotGefEditPart>();
		anchors.add(DiagramTestUtils.getDiagramPortAnchor(provides));
		anchors.add(DiagramTestUtils.getDiagramPortAnchor(uses));

		for (SWTBotGefEditPart anchor : anchors) {
			try {
				anchor.select();
				editor.clickContextMenu("Delete");
				Assert.fail();
			} catch (WidgetNotFoundException e) {
				Assert.assertEquals(e.getMessage(), "Delete", e.getMessage());
			}
		}
	}
}
