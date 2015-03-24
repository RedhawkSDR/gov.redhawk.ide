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
package gov.redhawk.ide.ui.tests.runtime;

import gov.redhawk.ide.swtbot.ComponentUtils;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.WaveformUtils;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Test;

public class WaveformReleaseTest extends UIRuntimeTest {
	
	SWTBot viewBot;
	
	@Override
	public void before() throws Exception {
		super.before();
		
		SWTBotView explorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		explorerView.show();
		explorerView.setFocus();
		viewBot = explorerView.bot();
	}
	
	/**
	 * IDE-913 - Ensure waveform release action completes successfully
	 */
	@Test
	public void releaseWaveformTest() {
		final String WAVEFORM = "ExampleWaveform01";
		final String COMPONENT = "SigGen";
		final String COMPONENT_IMPL = "python";
		final String COMPONENT_PORT = "dataFloat_out";
		
		SWTBotTreeItem waveformTreeItem = WaveformUtils.launchLocalWaveform(bot, WAVEFORM);
		SWTBotTreeItem componentTreeItem = ComponentUtils.launchLocalComponent(bot, COMPONENT, COMPONENT_IMPL);
	
		// Release waveform
		waveformTreeItem.contextMenu("Release").click();
		
		// Make sure waveform is removed from the SCA Explorer
		final SWTBotTreeItem sandbox = viewBot.tree().getTreeItem("Sandbox");
		bot.waitUntil(new ICondition() {

			@Override
			public boolean test() throws Exception {
				for (SWTBotTreeItem item : sandbox.getItems()) {
					if (item.getText().matches(WAVEFORM + ".*")) {
						return false;
					}
				}

				return true;
			}

			@Override
			public void init(SWTBot bot) {
			}

			@Override
			public String getFailureMessage() {
				return "Waveform: " + WAVEFORM + " did not release";
			}
		});

		// Make sure waveform is not present in connection wizard
		componentTreeItem.expand().getNode(COMPONENT_PORT).contextMenu("Connect").click();
		SWTBotShell connectWizard = bot.shell("Connect");
		connectWizard.setFocus();
		SWTBotTreeItem targetTree = bot.treeInGroup("Target").getTreeItem("Sandbox");
		targetTree.expand();
		for (SWTBotTreeItem item : targetTree.getItems()) {
			if (item.getText().matches(WAVEFORM + ".*")) {
				throw new AssertionError("Waveform " + WAVEFORM + " was not released");
			}
		}
		connectWizard.close();
	}
}
