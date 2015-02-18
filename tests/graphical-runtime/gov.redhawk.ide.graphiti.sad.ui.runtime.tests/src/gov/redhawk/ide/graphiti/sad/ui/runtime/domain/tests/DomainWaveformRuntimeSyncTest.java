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
package gov.redhawk.ide.graphiti.sad.ui.runtime.domain.tests;

import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.junit.Test;

public class DomainWaveformRuntimeSyncTest extends AbstractGraphitiDomainWaveformRuntimeTest {

	private static final String HARD_LIMIT = "HardLimit";
	private static final String HARD_LIMIT_1 = "HardLimit_1";
	private static final String SIG_GEN = "SigGen";
	private static final String SIG_GEN_1 = "SigGen_1";
	
	/**
	 * IDE-672
	 * Starts/stops them from Diagram and verifies
	 * components in ScaExplorer reflect changes
	 * 
	 */
	@Test
	public void startStopComponentsFromDiagram() {
		// Open domain waveform with graphiti chalkboard editor
		bot.closeAllEditors();
		ScaExplorerTestUtils.openDiagramFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM, DiagramType.GRAPHITI_CHALKBOARD);
		SWTBotGefEditor editor = gefBot.gefEditor(getWaveFormFullName());
		editor.setFocus();

		// verify hard limit stopped
		ScaExplorerTestUtils.waitUntilComponentAppearsStoppedInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM, HARD_LIMIT_1);

		// start hard limit
		DiagramTestUtils.startComponentFromDiagram(editor, HARD_LIMIT);

		// verify hardlimit started but siggen did not
		ScaExplorerTestUtils.waitUntilComponentAppearsStartedInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM, HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilComponentAppearsStoppedInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM, SIG_GEN_1);

		// start SigGen
		DiagramTestUtils.startComponentFromDiagram(editor, SIG_GEN);

		// verify SigGen started but siggen did not
		ScaExplorerTestUtils.waitUntilComponentAppearsStartedInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM, SIG_GEN_1);

		// stop hard limit
		DiagramTestUtils.stopComponentFromDiagram(editor, HARD_LIMIT);

		// verify hardlimit stopped, SigGen started
		ScaExplorerTestUtils.waitUntilComponentAppearsStoppedInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM, HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilComponentAppearsStartedInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM, SIG_GEN_1);

		// stop SigGen
		DiagramTestUtils.stopComponentFromDiagram(editor, SIG_GEN);

		// verify SigGen stopped
		ScaExplorerTestUtils.waitUntilComponentAppearsStoppedInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM, SIG_GEN_1);

		// start both components
		DiagramTestUtils.startComponentFromDiagram(editor, HARD_LIMIT);
		DiagramTestUtils.startComponentFromDiagram(editor, SIG_GEN);

		// verify both started
		ScaExplorerTestUtils.waitUntilComponentAppearsStartedInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM, HARD_LIMIT_1);
		ScaExplorerTestUtils.waitUntilComponentAppearsStartedInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM, SIG_GEN_1);
	}

	/**
	 * IDE-672
	 * Starts/stops them from ScaExplorer and verifies
	 * components in diagram reflect appropriate color changes
	 * 
	 */
	@Test
	public void startStopComponentsFromScaExplorer() {
		// Open domain waveform with graphiti chalkboard editor
		bot.closeAllEditors();
		ScaExplorerTestUtils.openDiagramFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM, DiagramType.GRAPHITI_CHALKBOARD);
		SWTBotGefEditor editor = gefBot.gefEditor(getWaveFormFullName());
		editor.setFocus();

		// verify hard limit stopped
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, HARD_LIMIT);

		// start hard limit from sca explorer
		ScaExplorerTestUtils.startComponentFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM, HARD_LIMIT_1);

		// verify hardlimit started but siggen did not
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, HARD_LIMIT);
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, SIG_GEN);

		// start SigGen from sca explorer
		ScaExplorerTestUtils.startComponentFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM, SIG_GEN_1);

		// verify SigGen started but siggen did not
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, SIG_GEN);

		// stop hard limit from sca explorer
		ScaExplorerTestUtils.stopComponentFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM, HARD_LIMIT_1);

		// verify hardlimit stopped, SigGen started
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, HARD_LIMIT);
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, SIG_GEN);

		// stop SigGen from sca explorer
		ScaExplorerTestUtils.stopComponentFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM, SIG_GEN_1);

		// verify SigGen stopped
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, SIG_GEN);

		// start both components
		ScaExplorerTestUtils.startComponentFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM, HARD_LIMIT_1);
		ScaExplorerTestUtils.startComponentFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM, SIG_GEN_1);

		// verify both started
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, HARD_LIMIT);
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, SIG_GEN);

		// stop waveform
		ScaExplorerTestUtils.stopWaveformFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM);

		// verify both components stopped
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, HARD_LIMIT);
		DiagramTestUtils.waitUntilComponentAppearsStoppedInDiagram(bot, editor, SIG_GEN);

		// start waveform
		ScaExplorerTestUtils.startWaveformFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM);

		// verify both components started
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, HARD_LIMIT);
		DiagramTestUtils.waitUntilComponentAppearsStartedInDiagram(bot, editor, SIG_GEN);
	}
}
