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
package gov.redhawk.ide.graphiti.sad.ui.runtime.chalkboard.tests;

import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.junit.Test;

public class ChalkboardToolbarTest extends AbstractGraphitiChalkboardTest {

	private static final String SIGGEN_1 = "SigGen_1";
	private static final String HARD_LIMIT_1 = "HardLimit_1";
	private SWTBotGefEditor editor;

	/**
	 * IDE-1076 - Make sure start, stop, and release toolbar buttons appear/function during runtime
	 */
	@Test
	public void checkChalkboardToolbarButtons() {
		editor = openChalkboardDiagram(gefBot);

		// Add component to diagram from palette
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 200, 200);

		editor.setFocus();
		bot.toolbarButtonWithTooltip("Start Waveform").click();
		ScaExplorerTestUtils.waitUntilComponentAppearsStartedInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, SIGGEN_1);
		ScaExplorerTestUtils.waitUntilComponentAppearsStartedInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, HARD_LIMIT_1);
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, SIGGEN);
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, HARD_LIMIT);

		editor.setFocus();
		bot.toolbarButtonWithTooltip("Stop Waveform").click();
		ScaExplorerTestUtils.waitUntilComponentAppearsStoppedInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, SIGGEN_1);
		ScaExplorerTestUtils.waitUntilComponentAppearsStoppedInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, HARD_LIMIT_1);
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, SIGGEN);
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, HARD_LIMIT);

		editor.setFocus();
		bot.toolbarButtonWithTooltip("Release Waveform").click();
		ScaExplorerTestUtils.waitUntilComponentDisappearsInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, SIGGEN_1);
		ScaExplorerTestUtils.waitUntilComponentDisappearsInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, HARD_LIMIT_1);
		DiagramTestUtils.waitUntilComponentDisappearsInChalkboardDiagram(bot, editor, SIGGEN);
		DiagramTestUtils.waitUntilComponentDisappearsInChalkboardDiagram(bot, editor, HARD_LIMIT);

	}
}
