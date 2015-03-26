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

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

public class NodeUtils {

	protected NodeUtils() {
	}

	/**
	 * Creates a new node project using File > New > Other... > Graphiti SCA Node Project wizard
	 * @param bot - the executing SWTBot
	 * @param waveformName
	 */
	public static void createNewNodeProject(SWTWorkbenchBot bot, String projectName, String domainName) {
		// Open the new waveform project wizard
		SWTBotMenu fileMenu = bot.menu("File");
		SWTBotMenu newMenu = fileMenu.menu("New");
		SWTBotMenu otherMenu = newMenu.menu("Other...");
		otherMenu.click();
		SWTBotShell wizardShell = bot.shell("New");
		SWTBot wizardBot = wizardShell.bot();
		wizardShell.activate();
		wizardBot.tree().getTreeItem("SCA").expand().getNode("SCA Node Project").select();
		wizardBot.button("Next >").click();

		// Enter the name for the new waveform
		wizardBot.textWithLabel("Project name:").setText(projectName);

		// Enter a domain manager
		wizardBot.comboBoxWithLabel("Domain Manager:").setText(domainName);

		// Close wizard
		SWTBotButton finishButton = wizardBot.button("Finish");
		finishButton.click();

		// Set focus to Node
		SWTBotEditor nodeEditor = bot.editorByTitle(projectName);
		nodeEditor.setFocus();
		nodeEditor.bot().cTabItem("Diagram").activate();
	}
	
	/**
	 * Launches the names node in a running domain and opens the node explorer diagram.
	 * Assumes domain is already running and visible in the SCA Explorer
	 * @param domainName
	 * @param nodeName
	 */
	public static void launchNodeInDomain(final SWTWorkbenchBot bot, String domainName, String nodeName) {
		SWTBotView explorerView = bot.viewById(ScaExplorerTestUtils.SCA_EXPLORER_VIEW_ID);
		explorerView.show();
		explorerView.setFocus();
		SWTBot viewBot = explorerView.bot();

		SWTBotTreeItem nodeScaTreeItem = viewBot.tree().expandNode("Target SDR", "Nodes", nodeName);
		nodeScaTreeItem.contextMenu("Launch Device Manager").click();
		
		SWTBotShell wizard = bot.shell("Launch Device Manager");
		wizard.bot().table().select(domainName);
		wizard.bot().button("OK").click();
		
		SWTBotTreeItem nodeDomainTreeItem = viewBot.tree().expandNode(domainName + " CONNECTED", "Device Managers", nodeName);
		nodeDomainTreeItem.contextMenu("Open With").menu("Node Explorer").click();
		
	}

}
