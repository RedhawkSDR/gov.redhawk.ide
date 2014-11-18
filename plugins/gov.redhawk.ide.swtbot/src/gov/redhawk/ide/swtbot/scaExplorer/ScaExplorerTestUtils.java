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
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

public class ScaExplorerTestUtils {


	public static final String SCA_EXPLORER_VIEW_ID = "gov.redhawk.ui.sca_explorer";
	
	protected ScaExplorerTestUtils() {
	}
	
	
	/**
	 * Returns the Waveform tree item from Sca Explorer
	 */
	public static SWTBotTreeItem getWaveformTreeItemFromScaExplorer(SWTWorkbenchBot bot, 
		String[] waveformParentPath, String waveform) {
		SWTBotView scaExplorerView = bot.viewByTitle("SCA Explorer");
		SWTBotTreeItem waveformParent = scaExplorerView.bot().tree().expandNode(waveformParentPath);
		//find waveform
		//Could be "<waveform>" or "<waveform> STARTED"
		for (SWTBotTreeItem treeItem: waveformParent.getItems()) {
			if (treeItem.getText().startsWith(waveform)) {
				treeItem.expand();
				return treeItem;
			}
		}
		return null;
	}
	
	/**
	 * Open Graphiti Diagram from ScaExplorer.
	 * @param gefBot
	 * @param editor
	 * @param componentName
	 */
	public static void openDiagramFromScaExplorer(SWTGefBot gefBot, String[] waveformParentPath, String waveform) {
		SWTBotTreeItem waveformTreeItem = getWaveformTreeItemFromScaExplorer(gefBot, waveformParentPath, waveform);
		if (waveformTreeItem == null) {
			throw new WidgetNotFoundException("Waveform tree item not found in Sandbox");
		}
		
		waveformTreeItem.select();
		SWTBotMenu openWith = waveformTreeItem.contextMenu("Open With");
		SWTBotMenu graphitiDiagram = null;
		graphitiDiagram = openWith.menu("Graphiti Chalkboard");
		//TODO: It shouldn't read "Graphiti Chalkboard"
//		if ("Chalkboard".equalsIgnoreCase(waveform)) {
//			
//		} else {
//			graphitiDiagram = openWith.menu("Graphiti Diagram");
//		}
		graphitiDiagram.click();
	}
	
	/**
	 * Returns the full name of the Waveform found in Sca Explorer.
	 * You can pass this method a prefix for the waveform
	 * @param gefBot
	 * @param waveformParentPath
	 * @param waveform
	 */
	public static String getWaveformFullNameFromScaExplorer(SWTGefBot gefBot, String[] waveformParentPath, String waveform) {
		SWTBotTreeItem waveformTreeItem = getWaveformTreeItemFromScaExplorer(gefBot, waveformParentPath, waveform);
		if (waveformTreeItem == null) {
			throw new WidgetNotFoundException("Waveform tree item not found in Sandbox");
		}
		
		return waveformTreeItem.getText();
	}
	
	/**
	 * Terminates component via ScaExplorer
	 * @param componentName
	 */
	public static void terminateComponentInScaExplorer(SWTWorkbenchBot bot, 
			String[] waveformParentPath, String waveform, String componentName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem waveformTreeItem = getWaveformTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
		SWTBotTreeItem componentEntry = waveformTreeItem.expandNode(componentName);
		componentEntry.select();
		SWTBotMenu terminate = componentEntry.contextMenu("Terminate");
		terminate.click();
	}
	
	/**
	 * Connect component ports via SCA Explorer Chalkboard
	 * @param componentName
	 */
	public static void connectComponentPortsInScaExplorer(SWTWorkbenchBot bot, 
		String[] waveformParentPath, String waveform, String connectionName, 
			String sourceComponentName, String sourceComponentPortName, 
		String targetComponentName, String targetComponentPortName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem waveformTreeItem = getWaveformTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
		SWTBotTreeItem sourceComponentPortEntry = waveformTreeItem.expandNode(sourceComponentName, sourceComponentPortName);
		sourceComponentPortEntry.select();
		SWTBotMenu connect = sourceComponentPortEntry.contextMenu("Connect");
		connect.click(); //opens connect wizard
		
		//Connect wizard
		SWTBotShell wizardShell = bot.shell("Connect");
		SWTBot wizardBot = wizardShell.bot();
		wizardShell.activate();
		SWTBotTreeItem targetWaveformParentTreeItem = wizardBot.treeInGroup("Target").expandNode(waveformParentPath);
		targetWaveformParentTreeItem.expandNode(waveform, targetComponentName, targetComponentPortName).select();

		// Enter the name for connection
		wizardBot.textWithLabel("Connection ID:").setText(connectionName);

		// Close wizard
		SWTBotButton finishButton = wizardBot.button("Finish");
		finishButton.click();
		
	}
	
	/**
	 * Disconnect connection via SCA Explorer
	 * @param componentName
	 */
	public static void disconnectConnectionInScaExplorer(SWTWorkbenchBot bot, 
			String[] waveformParentPath, String waveform, String connectionName, 
			String sourceComponentName, String sourceComponentPortName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem waveformTreeItem = getWaveformTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
		SWTBotTreeItem connectionEntry = waveformTreeItem.expandNode(sourceComponentName, sourceComponentPortName, connectionName);
		connectionEntry.select();
		SWTBotMenu disconnect = connectionEntry.contextMenu("Disconnect");
		disconnect.click(); //disconnects connection
	}
	
	/**
	 * Start component from ScaExplorer
	 * @param componentName
	 */
	public static void startComponentFromScaExplorer(SWTWorkbenchBot bot, String[] waveformParentPath, 
			String waveform, String componentName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem waveformEntry = getWaveformTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
		SWTBotTreeItem componentEntry = waveformEntry.getNode(componentName);
		componentEntry.select();
		SWTBotMenu start = componentEntry.contextMenu("Start");
		start.click();
	}
	
	/**
	 * Stop components in Diagram via ScaExplorer
	 */
	public static void stopWaveformFromScaExplorer(SWTWorkbenchBot bot, String[] waveformParentPath, String waveform) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem waveformEntry = getWaveformTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
		waveformEntry.select();
		SWTBotMenu stop = waveformEntry.contextMenu("Stop");
		stop.click();
	}
	
	/**
	 * Terminate components in Diagram via ScaExplorer.
	 */
	public static void terminateWaveformFromScaExplorer(SWTWorkbenchBot bot, String[] waveformParentPath, String waveform) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem waveformEntry = getWaveformTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
		waveformEntry.select();
		SWTBotMenu terminate = waveformEntry.contextMenu("Terminate");
		terminate.click();
	}
	
	/**
	 * Release Waveform via ScaExplorer.
	 */
	public static void releaseWaveformFromScaExplorer(SWTWorkbenchBot bot, String[] waveformParentPath, String waveform) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem waveformEntry = getWaveformTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
		waveformEntry.select();
		SWTBotMenu terminate = waveformEntry.contextMenu("Release");
		terminate.click();
	}
	
	/**
	 * Start components in Diagram via ScaExplorer
	 */
	public static void startWaveformFromScaExplorer(SWTWorkbenchBot bot, String[] waveformParentPath, String waveform) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem waveformEntry = getWaveformTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
		waveformEntry.select();
		SWTBotMenu start = waveformEntry.contextMenu("Start");
		start.click();
	}
	
	/**
	 * Stop component from ScaExplorer
	 * @param componentName
	 */
	public static void stopComponentFromScaExplorer(SWTWorkbenchBot bot, String[] waveformParentPath, 
			String waveform, String componentName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem waveformTreeItem = getWaveformTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
		SWTBotTreeItem componentEntry = waveformTreeItem.getNode(componentName + " STARTED");
		componentEntry.select();
		SWTBotMenu stop = componentEntry.contextMenu("Stop");
		stop.click();
	}
	

	
	/**
	 * Waits until component appears stopped in ScaExplorer
	 * @param componentName
	 */
	public static void waitUntilComponentAppearsStoppedInScaExplorer(SWTWorkbenchBot bot, String[] waveformParentPath, String waveform, final String componentName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		final SWTBotTreeItem waveformTreeItem = getWaveformTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
		
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return componentName + " Component did not appear stopped in SCA Explorer";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem[] items = waveformTreeItem.getItems();
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
	public static void waitUntilComponentAppearsStartedInScaExplorer(SWTWorkbenchBot bot, String[] waveformParentPath, String waveform, final String componentName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		final SWTBotTreeItem waveformTreeItem = getWaveformTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
		
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return componentName + " Component did not appear started in SCA Explorer";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem[] items = waveformTreeItem.getItems();
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
	 * Waits until Component displays in ScaExplorer
	 * @param componentName
	 */
	public static void waitUntilComponentDisplaysInScaExplorer(SWTWorkbenchBot bot, String[] waveformParentPath, String waveform, final String componentName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		final SWTBotTreeItem waveformTreeItem = getWaveformTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
		
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return componentName + " Component did not load into SCA Explorer";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem[] items = waveformTreeItem.getItems();
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
	 * Waits until Component disappears in ScaExplorer
	 * @param componentName
	 */
	public static void waitUntilComponentDisappearsInScaExplorer(SWTWorkbenchBot bot, String[] waveformParentPath, String waveform, final String componentName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		final SWTBotTreeItem waveformTreeItem = getWaveformTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
		
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return componentName + " Component did not disappear from SCA Explorer";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem[] items = waveformTreeItem.getItems();
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
	 * Waits until Waveform disappears in ScaExplorer
	 * @param componentName
	 */
	public static void waitUntilScaExplorerWaveformDisappears(SWTWorkbenchBot bot, final String[] waveformParentPath, final String waveform) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return waveform + " waveform did not disappear from SCA Explorer";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem waveformTreeItem = getWaveformTreeItemFromScaExplorer((SWTWorkbenchBot) bot, waveformParentPath, waveform);
				if (waveformTreeItem == null) {
					return true;
				}
				return false;
			}
		});
	}
	
	
	
	/**
	 * Waits until ScaExplorer Waveform is stopped
	 * @param componentName
	 */
	public static void waitUntilScaExplorerWaveformStopped(final SWTWorkbenchBot bot, 
		final String[] waveformParentPath, final String waveform) {
		
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return "SCA Explorer Waveform is not stopped";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem waveformTreeItem = 
						ScaExplorerTestUtils.getWaveformTreeItemFromScaExplorer((SWTWorkbenchBot) bot, waveformParentPath, waveform);
				if (!waveformTreeItem.getText().contains("STARTED")) {
					return true;
				}
				return false;
			}
		});
	}
	
	/**
	 * Waits until ScaExplorer Waveform has no child items
	 * @param componentName
	 */
	public static void waitUntilScaExplorerWaveformEmpty(SWTWorkbenchBot bot, String[] waveformParentPath, String waveform) {
		final SWTBotTreeItem waveformTreeItem = getWaveformTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
		
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return "SCA Explorer Waveform is not empty";
			}

			@Override
			public boolean test() throws Exception {
				if (waveformTreeItem.getItems().length < 1) {
					return true;
				}
				return false;
			}
		});
	}
	
	/**
	 * Waits until Connection displays in ScaExplorer
	 * @param componentName
	 */
	public static void waitUntilConnectionDisplaysInScaExplorer(SWTWorkbenchBot bot,
			String[] waveformParentPath, String waveform,
			final String componentName, final String usesPortName, final String connectionName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		final SWTBotTreeItem waveformTreeItem = getWaveformTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
		final SWTBotTreeItem sigGenOutPortTreeItem = waveformTreeItem.expandNode(componentName, usesPortName);
		
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return connectionName + " Connection did not load into SCA Explorer";
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
	 * Waits until Connection disappears in ScaExplorer
	 * @param componentName
	 */
	public static void waitUntilConnectionDisappearsInScaExplorer(SWTWorkbenchBot bot,
		String[] waveformParentPath, String waveform,
			final String componentName, final String usesPortName, final String connectionName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		final SWTBotTreeItem waveformTreeItem = getWaveformTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
		final SWTBotTreeItem sigGenOutPortTreeItem = waveformTreeItem.expandNode(componentName, usesPortName);
		
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return connectionName + " Connection did not load into SCA Explorer";
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
	
	/**
	 * Launch waveform from ScaExplorer TargetSDR
	 * @param componentName
	 * @param componentLanguage
	 */
	public static void launchWaveformFromTargetSDR(SWTWorkbenchBot bot, 
			String waveformName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem waveformEntry = scaExplorerView.bot().tree().expandNode("Target SDR", "Waveforms", waveformName);
		waveformEntry.select();
		SWTBotMenu launchInSandbox = waveformEntry.contextMenu("Launch in Sandbox");
		SWTBotMenu defaultItem = launchInSandbox.menu("Default");
		defaultItem.click();
	}
	
	/**
	 * Waits until Waveform displays in ScaExplorer
	 * @param componentName
	 */
	public static void waitUntilWaveformAppearsInScaExplorer(SWTWorkbenchBot bot, final String[] waveformParentPath, final String waveformName) {
		
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return waveformName + " did not load into SCA Explorer";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem waveformTreeItem = getWaveformTreeItemFromScaExplorer((SWTWorkbenchBot) bot, waveformParentPath, waveformName);
				if (waveformTreeItem != null) {
					return true;
				}
				return false;
			}
		});
	}
}
