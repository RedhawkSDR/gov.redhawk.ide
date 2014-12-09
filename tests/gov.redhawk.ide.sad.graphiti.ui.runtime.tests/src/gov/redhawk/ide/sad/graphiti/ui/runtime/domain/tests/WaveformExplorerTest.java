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
package gov.redhawk.ide.sad.graphiti.ui.runtime.domain.tests;

import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.junit.Assert;
import org.junit.Test;

public class WaveformExplorerTest extends AbstractGraphitiDomainWaveformRuntimeTest {

	private SWTBotGefEditor editor;
	private static final String[] CHALKBOARD_PARENT_PATH = { "Sandbox" };
	private static final String CHALKBOARD = "Chalkboard";
	private static final String HARD_LIMIT = "HardLimit";
	private static final String HARD_LIMIT_1 = "HardLimit_1";
	private static final String DATA_READER = "DataReader";

	/**
	 * IDE-969
	 * Opens Graphiti diagram using the Waveform Explorer editor
	 * This editor is "look but don't touch". All design functionality should be disabled.
	 * Runtime funcationality (start/stop, plot, etc) should still work.
	 */
	@Test
	public void waveformExplorerTest() {
		gefBot.closeAllEditors();

		// Open waveform explorer
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM, DiagramType.GRAPHITI_WAVEFORM_EXPLORER);

		// check for components
		editor = gefBot.gefEditor(getWaveFormFullName());
		editor.setFocus();
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
		DiagramTestUtils.dragFromTargetSDRToDiagram(gefBot, editor, DATA_READER);
		editor.setFocus();
		SWTBotGefEditPart dataReader = editor.getEditPart(DATA_READER);
		Assert.assertNull(DATA_READER + " component should not have be drawn in diagram", dataReader);
	}
}
