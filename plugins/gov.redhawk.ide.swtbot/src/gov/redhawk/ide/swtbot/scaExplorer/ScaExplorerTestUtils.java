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
package gov.redhawk.ide.swtbot.scaExplorer;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

public class ScaExplorerTestUtils {

	public static final String CHALKBOARD = "Chalkboard";
	public static final String SANDBOX = "Sandbox";
	public static final String SCA_EXPLORER_VIEW_ID = "gov.redhawk.ui.sca_explorer";
	
	protected ScaExplorerTestUtils() {
	}
	
	/**
	 * Terminates component via ScaExplorer
	 * @param componentName
	 */
	public static void terminateComponentInScaExplorerChalkboard(SWTWorkbenchBot bot, String componentName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem componentEntry = scaExplorerView.bot().tree().expandNode(SANDBOX, CHALKBOARD, componentName);
		componentEntry.select();
		SWTBotMenu terminate = componentEntry.contextMenu("Terminate");
		terminate.click();
	}
	
	/**
	 * Connect component ports via SCA Explorer Chalkboard
	 * @param componentName
	 */
	public static void connectComponentPortsInScaExplorerChalkboard(SWTWorkbenchBot bot, String connectionName, 
			String sourceComponentName, String sourceComponentPortName, 
		String targetComponentName, String targetComponentPortName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem sourceComponentPortEntry = scaExplorerView.bot().tree().expandNode(SANDBOX, CHALKBOARD, sourceComponentName, sourceComponentPortName);
		sourceComponentPortEntry.select();
		SWTBotMenu connect = sourceComponentPortEntry.contextMenu("Connect");
		connect.click(); //opens connect wizard
		
		//Connect wizard
		SWTBotShell wizardShell = bot.shell("Connect");
		SWTBot wizardBot = wizardShell.bot();
		wizardShell.activate();
		wizardBot.treeInGroup("Target").expandNode(SANDBOX, CHALKBOARD, targetComponentName, targetComponentPortName).select();

		// Enter the name for connection
		wizardBot.textWithLabel("Connection ID:").setText(connectionName);

		// Close wizard
		SWTBotButton finishButton = wizardBot.button("Finish");
		finishButton.click();
		
	}
	
	/**
	 * Disconnect connection via SCA Explorer Chalkboard
	 * @param componentName
	 */
	public static void disconnectConnectionInScaExplorerChalkboard(SWTWorkbenchBot bot, String connectionName, 
			String sourceComponentName, String sourceComponentPortName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem connectionEntry = scaExplorerView.bot().tree().expandNode(SANDBOX, CHALKBOARD, sourceComponentName, sourceComponentPortName, connectionName);
		connectionEntry.select();
		SWTBotMenu disconnect = connectionEntry.contextMenu("Disconnect");
		disconnect.click(); //disconnects connection
	}
	
	/**
	 * Start component from ScaExplorer Chalkboard
	 * @param componentName
	 */
	public static void startComponentFromScaExplorer(SWTWorkbenchBot bot, String componentName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem componentEntry = scaExplorerView.bot().tree().expandNode(SANDBOX, CHALKBOARD, componentName);
		componentEntry.select();
		SWTBotMenu start = componentEntry.contextMenu("Start");
		start.click();
	}
	
	/**
	 * Stop components in Chalkboard via ScaExplorer Chalkboard
	 */
	public static void stopChalkboardFromScaExplorer(SWTWorkbenchBot bot) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem chalkboardEntry = scaExplorerView.bot().tree().expandNode(SANDBOX, CHALKBOARD);
		chalkboardEntry.select();
		SWTBotMenu stop = chalkboardEntry.contextMenu("Stop");
		stop.click();
	}
	
	/**
	 * Terminate components in Chalkboard via ScaExplorer Chalkboard.
	 */
	public static void terminateChalkboardFromScaExplorer(SWTWorkbenchBot bot) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem chalkboardEntry = scaExplorerView.bot().tree().expandNode(SANDBOX, CHALKBOARD);
		chalkboardEntry.select();
		SWTBotMenu terminate = chalkboardEntry.contextMenu("Terminate");
		terminate.click();
	}
	
	/**
	 * Start components in Chalkboard via ScaExplorer Chalkboard
	 */
	public static void startChalkboardFromScaExplorer(SWTWorkbenchBot bot) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem chalkboardEntry = scaExplorerView.bot().tree().expandNode(SANDBOX, CHALKBOARD);
		chalkboardEntry.select();
		SWTBotMenu start = chalkboardEntry.contextMenu("Start");
		start.click();
	}
	
	/**
	 * Stop component from ScaExplorer Chalkboard
	 * @param componentName
	 */
	public static void stopComponentFromScaExplorer(SWTWorkbenchBot bot, String componentName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem componentEntry = scaExplorerView.bot().tree().expandNode(SANDBOX, CHALKBOARD, componentName + " STARTED");
		componentEntry.select();
		SWTBotMenu stop = componentEntry.contextMenu("Stop");
		stop.click();
	}
	

	
	/**
	 * Waits until component appears stopped in ScaExplorer
	 * @param componentName
	 */
	public static void waitUntilComponentAppearsStoppedInScaExplorerChalkboard(SWTWorkbenchBot bot, final String componentName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		final SWTBotTreeItem chalkboardTreeItem = scaExplorerView.bot().tree().expandNode(SANDBOX, CHALKBOARD);
		
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return componentName + " Component did not appear stopped in SCA Explorer Chalkboard";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem[] items = chalkboardTreeItem.getItems();
				for (SWTBotTreeItem item : items) {
					if (item.getText().equals(componentName)) {
						return true;
					}
				}
				return false;
			}
		});
	}
	
	/**
	 * Waits until component appears stopped in ScaExplorer
	 * @param componentName
	 */
	public static void waitUntilComponentAppearsStartedInScaExplorerChalkboard(SWTWorkbenchBot bot, final String componentName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		final SWTBotTreeItem chalkboardTreeItem = scaExplorerView.bot().tree().expandNode(SANDBOX, CHALKBOARD);
		
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return componentName + " Component did not appear started in SCA Explorer Chalkboard";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem[] items = chalkboardTreeItem.getItems();
				for (SWTBotTreeItem item : items) {
					if (item.getText().equals(componentName + " STARTED")) {
						return true;
					}
				}
				return false;
			}
		});
	}
	
	/**
	 * Waits until Component displays in ScaExplorer Chalkboard
	 * @param componentName
	 */
	public static void waitUntilComponentDisplaysInScaExplorerChalkboard(SWTWorkbenchBot bot, final String componentName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		final SWTBotTreeItem chalkboardTreeItem = scaExplorerView.bot().tree().expandNode(SANDBOX, CHALKBOARD);
		
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return componentName + " Component did not load into SCA Explorer Chalkboard";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem[] items = chalkboardTreeItem.getItems();
				for (SWTBotTreeItem item : items) {
					if (item.getText().equals(componentName)) {
						return true;
					}
				}
				return false;
			}
		});
	}
	
	/**
	 * Waits until Component disappears in ScaExplorer Chalkboard
	 * @param componentName
	 */
	public static void waitUntilComponentDisappearsInScaExplorerChalkboard(SWTWorkbenchBot bot, final String componentName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		final SWTBotTreeItem chalkboardTreeItem = scaExplorerView.bot().tree().expandNode(SANDBOX, CHALKBOARD);
		
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return componentName + " Component did not disappear from SCA Explorer Chalkboard";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem[] items = chalkboardTreeItem.getItems();
				for (SWTBotTreeItem item : items) {
					if (item.getText().equals(componentName)) {
						return false;
					}
				}
				return true;
			}
		});
	}
	
	/**
	 * Waits until Connection displays in ScaExplorer Chalkboard
	 * @param componentName
	 */
	public static void waitUntilConnectionDisplaysInScaExplorerChalkboard(SWTWorkbenchBot bot, 
		final String componentName, final String usesPortName, final String connectionName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		final SWTBotTreeItem sigGenOutPortTreeItem = scaExplorerView.bot().tree().expandNode(SANDBOX, CHALKBOARD, componentName, usesPortName);
		
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return connectionName + " Connection did not load into SCA Explorer Chalkboard";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem[] items = sigGenOutPortTreeItem.getItems();
				for (SWTBotTreeItem item : items) {
					if (item.getText().equals(connectionName)) {
						return true;
					}
				}
				return false;
			}
		});
	}
	
	/**
	 * Waits until Connection disappears in ScaExplorer Chalkboard
	 * @param componentName
	 */
	public static void waitUntilConnectionDisappearsInScaExplorerChalkboard(SWTWorkbenchBot bot, 
			final String componentName, final String usesPortName, final String connectionName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		final SWTBotTreeItem sigGenOutPortTreeItem = scaExplorerView.bot().tree().expandNode(SANDBOX, CHALKBOARD, componentName, usesPortName);
		
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return connectionName + " Connection did not load into SCA Explorer Chalkboard";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem[] items = sigGenOutPortTreeItem.getItems();
				for (SWTBotTreeItem item : items) {
					if (item.getText().equals(connectionName)) {
						return false;
					}
				}
				return true;
			}
		});
	}
	
	/**
	 * Launch component from ScaExplorer TargetSDR
	 * @param componentName
	 * @param componentLanguage
	 */
	public static void launchComponentFromTargetSDR(SWTWorkbenchBot bot, 
			String componentName, String componentLanguage) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem componentEntry = scaExplorerView.bot().tree().expandNode("Target SDR", "Components", componentName);
		componentEntry.select();
		SWTBotMenu launchInSandbox = componentEntry.contextMenu("Launch in Sandbox");
		SWTBotMenu python = launchInSandbox.menu(componentLanguage);
		python.click();
	}
}
