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
package gov.redhawk.ide.swtbot;

import java.util.Arrays;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

public class WaveformUtils {

	public static final String NEW_WAVEFORM_WIZARD_NAME = "REDHAWK Waveform Project";
	private static final long CREATE_NEW_PROJECT_DELAY = 10000;

	private WaveformUtils() {
	}

	/**
	 * Create a Redhawk waveform using the new project wizard.
	 * @param bot
	 * @param waveformName
	 * @param assemblyController
	 */
	public static void createNewWaveform(SWTBot bot, String waveformName, String assemblyController) {
		SWTBotShell origShell = bot.activeShell();

		bot.menu("File").menu("New").menu("Project...").click();
		SWTBotShell wizardShell = bot.shell("New Project");
		wizardShell.activate();
		final SWTBot wizardBot = wizardShell.bot();
		StandardTestActions.waitForTreeItemToAppear(wizardBot, wizardBot.tree(), Arrays.asList("REDHAWK", NEW_WAVEFORM_WIZARD_NAME)).select();
		wizardBot.button("Next >").click();

		// Enter the name for the new waveform
		wizardBot.textWithLabel("Project name:").setText(waveformName);

		// Select the assembly controller, if requested
		if (assemblyController != null) {
			wizardBot.button("Next >").click();
			StandardTestActions.selectNamespacedTreeItem(wizardBot, wizardBot.tree(), assemblyController);
		}

		// Close wizard
		wizardBot.button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(wizardShell), CREATE_NEW_PROJECT_DELAY);

		// For some reason, the main shell doesn't always receive focus back when using SWTBot
		if (!origShell.isActive()) {
			origShell.activate();
		}
	}

	/**
	 * Launches the selected waveform in the REDHAWK Explorer sandbox
	 * @returns the SWTBotTreeItem for the Waveform on the Sandbox
	 */
	public static SWTBotTreeItem launchLocalWaveform(final SWTWorkbenchBot bot, final String waveformName) {
		SWTBotView explorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		explorerView.show();
		explorerView.setFocus();
		SWTBot viewBot = explorerView.bot();

		SWTBotTreeItem waveformNode = viewBot.tree().expandNode("Target SDR", "Waveforms", waveformName);
		waveformNode.contextMenu("Default").click();

		// Wait for the launched waveform to appear in the sandbox
		final SWTBotTreeItem sandbox = viewBot.tree().getTreeItem("Sandbox");
		bot.waitUntil(new ICondition() {

			@Override
			public boolean test() throws Exception {
				for (SWTBotTreeItem item : sandbox.getItems()) {
					if (item.getText().matches(waveformName + ".*")) {
						return true;
					}
				}
				return false;
			}

			@Override
			public void init(SWTBot bot) {
			}

			@Override
			public String getFailureMessage() {
				return "Waveform: " + waveformName + " did not launch";
			}
		});

		// Return the treeitem for the running waveform from the Sandbox
		for (SWTBotTreeItem item : sandbox.getItems()) {
			if (item.getText().matches(waveformName + ".*")) {
				return item;
			}
		}

		return null;
	}

}
