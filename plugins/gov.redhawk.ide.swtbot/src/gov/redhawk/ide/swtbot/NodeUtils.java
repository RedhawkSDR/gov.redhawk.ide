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
package gov.redhawk.ide.swtbot;

import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

import java.util.Arrays;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

public class NodeUtils {

	private static final String NEW_NODE_WIZARD_NAME = "REDHAWK Node Project";
	private static final long CREATE_NEW_PROJECT_DELAY = 10000;

	private NodeUtils() {
	}

	/**
	 * Create a Redhawk node using the new project wizard.
	 * @param bot
	 * @param projectName
	 * @param domainName
	 */
	public static void createNewNodeProject(SWTWorkbenchBot bot, String projectName, String domainName) {
		// Open the new waveform project wizard
		bot.menu("File").menu("New").menu("Project...").click();
		SWTBotShell wizardShell = bot.shell("New Project");
		wizardShell.activate();
		final SWTBot wizardBot = wizardShell.bot();
		wizardShell.activate();
		StandardTestActions.waitForTreeItemToAppear(wizardBot, wizardBot.tree(), Arrays.asList("REDHAWK", NEW_NODE_WIZARD_NAME)).select();
		wizardBot.button("Next >").click();

		// Enter the name for the new waveform
		wizardBot.textWithLabel("Project name:").setText(projectName);

		// Enter a domain manager
		wizardBot.comboBoxWithLabel("Domain Manager:").setText(domainName);

		// Close wizard
		SWTBotButton finishButton = wizardBot.button("Finish");
		finishButton.click();

		bot.waitUntil(Conditions.shellCloses(wizardShell), CREATE_NEW_PROJECT_DELAY);

		// Set focus to Node
		SWTBotEditor nodeEditor = bot.editorByTitle(projectName);
		nodeEditor.setFocus();
		nodeEditor.bot().cTabItem("Diagram").activate();
	}
	
	/**
	 * Launches the names node in a running domain and opens the node explorer diagram.
	 * Assumes domain is already running and visible in the REDHAWK Explorer
	 * @param domainName
	 * @param nodeName
	 */
	public static void launchNodeInDomain(final SWTWorkbenchBot bot, String domainName, String nodeName) {
		SWTBotTreeItem nodeTreeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Target SDR", "Nodes" }, nodeName);
		nodeTreeItem.contextMenu("Launch Device Manager").click();

		SWTBotShell wizard = bot.shell("Launch Device Manager");
		wizard.bot().table().select(domainName);
		wizard.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(wizard));

		SWTBotTreeItem devMgrTreeItem = ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { domainName, "Device Managers" }, nodeName);
		devMgrTreeItem.contextMenu("Open With").menu("Node Explorer").click();
	}

}
