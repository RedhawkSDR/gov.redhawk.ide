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

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

public class WaveformUtils {

	/** private to prevent instantiation since all functions are static. */
	private WaveformUtils() {
	}
	
	/**
	 * Creates a new waveform using File > New > Other... > Graphiti SCA Waveform Project wizard
	 * @param bot - the executing SWTBot
	 * @param waveformName
	 */
	public static void createNewWaveform(SWTBot bot, String waveformName) {
		// Open the new waveform project wizard
		SWTBotMenu fileMenu = bot.menu("File");
		SWTBotMenu newMenu = fileMenu.menu("New");
		SWTBotMenu otherMenu = newMenu.menu("Other...");
		otherMenu.click();
		SWTBotShell wizardShell = bot.shell("New");
		SWTBot wizardBot = wizardShell.bot();
		wizardShell.activate();
		wizardBot.tree().getTreeItem("SCA").expand().getNode("SCA Waveform Project").select();
		wizardBot.button("Next >").click();

		// Enter the name for the new waveform
		wizardBot.textWithLabel("Project name:").setText(waveformName);

		// Close wizard
		SWTBotButton finishButton = wizardBot.button("Finish");
		finishButton.click();

		bot.waitUntil(Conditions.shellCloses(wizardShell));
	}

	/**
	 * Creates a new waveform with an assembly controller using File > New > Graphiti SCA Waveform Project wizard
	 * @param bot - the executing SWTBot
	 * @param waveformName
	 */
	public static void createNewWaveformWithAssemblyController(SWTBot bot, String waveformName, String assemblyControllerType) {
		// Open the new waveform project wizard
		SWTBotMenu fileMenu = bot.menu("File");
		SWTBotMenu newMenu = fileMenu.menu("New");
		SWTBotMenu otherMenu = newMenu.menu("Other...");
		otherMenu.click();
		SWTBotShell wizardShell = bot.shell("New");
		wizardShell.activate();
		SWTBot wizardBot = wizardShell.bot();
		wizardBot.tree().getTreeItem("SCA").expand().getNode("SCA Waveform Project").select();
		wizardBot.button("Next >").click();

		// Enter the name for the new waveform
		wizardBot.textWithLabel("Project name:").setText(waveformName);

		// Click next
		SWTBotButton nextButton = bot.button("Next >");
		nextButton.click();

		// Wait as the assembly controller table populates
		wizardBot.sleep(1000);

		// Select AC for new waveform
		SWTBotTable acTable = wizardBot.table();
		for (int row = 0; row < acTable.rowCount(); row++) {
			if (acTable.getTableItem(row).getText().contains(assemblyControllerType)) {
				acTable.select(row);
				break;
			}
		}

		// Click finish
		SWTBotButton finishButton = wizardBot.button("Finish");
		finishButton.click();

		// TODO Why doesn't this work?
//		bot.waitWhile(Conditions.shellCloses(wizardShell), 30000);
	}

	/**
	 * Launches the selected waveform in the SCA Explorer sandbox
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
