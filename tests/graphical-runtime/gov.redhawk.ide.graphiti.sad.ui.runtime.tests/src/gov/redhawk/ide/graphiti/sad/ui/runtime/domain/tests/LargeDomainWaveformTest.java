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

import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.WaitForEditorCondition;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.junit.Test;

/**
 * We were running into some race conditions when launching waveforms with more than a few components,
 * thus we need to test launching and releasing more complex waveforms
 */
public class LargeDomainWaveformTest extends AbstractGraphitiDomainWaveformRuntimeTest {

	/**
	 * IDE-1146, IDE-1137, IDE-1129
	 */
	@Test
	public void launchLargeWaveform() {
		// Don't need the default waveform for this test, but we do want the rest of the abstract behavior
		gefBot.closeAllEditors();

		// Launch the LargeWaveform
		String waveformName = "LargeWaveform";
		ScaExplorerTestUtils.launchWaveformFromDomain(bot, DOMAIN, waveformName);
		bot.waitUntil(new WaitForEditorCondition());
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, waveformName);
		String waveFormFullName = ScaExplorerTestUtils.getFullNameFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, waveformName);

		SWTBotGefEditor editor = gefBot.gefEditor(waveFormFullName);

		/*--- Confirm that the LargeWaveform was launched ---*/
		String[] componentsList = { "agc_1", "AmFmPmBasebandDemod_1", "ArbitraryRateResampler_1", "DataConverter_1", "fastfilter_1", "fastfilter_2",
			"fastfilter_3", "HardLimit_1", "HardLimit_2", "TuneFilterDecimate_1", "TuneFilterDecimate_2", "TuneFilterDecimate_3" };

		for (String component : componentsList) {
			ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, waveFormFullName, component);
		}

		for (String component : componentsList) {
			DiagramTestUtils.waitUntilComponentDisplaysInDiagram(bot, editor, component);
		}

		// Release the waveform, making sure no error dialog pops up
		ScaExplorerTestUtils.releaseFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, waveFormFullName);
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, waveFormFullName);
		StandardTestActions.assertNoOpenDialogs();
	}

}
