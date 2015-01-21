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
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

public class ScaExplorerTestUtils {

	public static final String SCA_EXPLORER_VIEW_ID = "gov.redhawk.ui.sca_explorer";

	// TODO: Do we want to extend this to handle GMF diagrams as well?
	public static enum DiagramType {
		GRAPHITI_CHALKBOARD("Graphiti Chalkboard"),
		GRAPHITI_WAVEFORM_EXPLORER("Graphiti Waveform Explorer"),
		GRAPHITI_NODE_EXPLORER("Graphiti Node Explorer");

		private final String diagramName;

		DiagramType(String diagramName) {
			this.diagramName = diagramName;
		}

		public String getDiagramName() {
			return diagramName;
		}
	}

	protected ScaExplorerTestUtils() {
	}

	/**
	 * Open the specified Graphiti Diagram from ScaExplorer.
	 * @param bot
	 * @param editor
	 * @param componentName
	 * @param diagramType - enum stating which context menu option to choose when opening the diagram (chalkboard,
	 * explorer, etc)
	 */
	public static void openDiagramFromScaExplorer(SWTWorkbenchBot bot, String[] parentPath, String treeItemName, DiagramType diagramType) {
		SWTBotTreeItem treeItem = getTreeItemFromScaExplorer(bot, parentPath, treeItemName);
		if (treeItem == null) {
			throw new WidgetNotFoundException("Tree item " + treeItemName + " not found in SCA Explorer");
		}

		treeItem.select();
		treeItem.contextMenu("Open With").menu(diagramType.getDiagramName()).click();
	}

	/**
	 * Returns the Tree item from SCA Explorer
	 */
	public static SWTBotTreeItem getTreeItemFromScaExplorer(SWTWorkbenchBot bot, String[] parentPath, String treeItemName) {
		SWTBotView scaExplorerView = bot.viewByTitle("SCA Explorer");
		SWTBotTreeItem treeItemParent = scaExplorerView.bot().tree().expandNode(parentPath);
		// find tree item
		// Could be "<tree item>" or "<tree item> STARTED"
		for (SWTBotTreeItem treeItem : treeItemParent.getItems()) {
			if (treeItem.getText().startsWith(treeItemName)) {
				treeItem.expand();
				return treeItem;
			}
		}
		return null;
	}

	/**
	 * Returns the full name of the tree item found in SCA Explorer.
	 * You can pass this method a prefix for the waveform/node/etc that you are trying to find
	 * @param bot
	 * @param parentPath
	 * @param treeItemName
	 */
	public static String getFullNameFromScaExplorer(SWTWorkbenchBot bot, String[] parentPath, String treeItemName) {
		SWTBotTreeItem treeItem = getTreeItemFromScaExplorer(bot, parentPath, treeItemName);
		if (treeItem == null) {
			throw new WidgetNotFoundException("Tree item not found in Sandbox");
		}

		return treeItem.getText();
	}

	/**
	 * Launch Domain from TargetSDR
	 * @param bot
	 * @param domainName
	 */
	public static void launchDomain(SWTWorkbenchBot bot, String domainName, String deviceName) {

		SWTBotView scaExplorerView = bot.viewByTitle("SCA Explorer");
		SWTBotTreeItem targetSDRTreeItem = scaExplorerView.bot().tree().getTreeItem("Target SDR");
		targetSDRTreeItem.select();
		SWTBotMenu launchDomain = targetSDRTreeItem.contextMenu("Launch Domain ...");
		launchDomain.click();

		SWTBotShell wizardShell = bot.shell("Launch Domain Manager");
		final SWTBot wizardBot = wizardShell.bot();
		wizardShell.activate();

		// Enter the Domain Name text
		wizardBot.text(0).setText("REDHAWK_DEV");

		// select waveform to launch
		for (SWTBotTreeItem treeItem : wizardBot.tree().getAllItems()) {
			if (treeItem.getText().startsWith(deviceName)) {
				treeItem.check();
				break;
			}
		}

		// Close wizard
		SWTBotButton okButton = wizardBot.button("OK");
		okButton.click();
	}

	/**
	 * Returns the SWTBotTreeItem for the domain in ScaExplorer with the provided name
	 * @param bot
	 * @param domainName
	 * @return
	 */
	public static SWTBotTreeItem getDomain(SWTWorkbenchBot bot, String domainName) {
		SWTBotView scaExplorerView = bot.viewByTitle("SCA Explorer");

		SWTBotTreeItem[] items = scaExplorerView.bot().tree().getAllItems();
		for (SWTBotTreeItem item : items) {
			if (item.getText().startsWith(domainName)) {
				return item;
			}
		}
		return null;
	}

	/**
	 * Delete domain instance from ScaExplorer
	 * @param bot
	 * @param domainName
	 */
	public static void deleteDomainInstance(SWTWorkbenchBot bot, String domainName) {

		SWTBotTreeItem domainTreeItem = getDomain(bot, domainName);
		domainTreeItem.select();
		SWTBotMenu deleteDomain = domainTreeItem.contextMenu("Delete");
		deleteDomain.click();

		SWTBotShell deletePopup = bot.shell("Delete Domain Connection");
		final SWTBot deletePopupBot = deletePopup.bot();
		deletePopup.activate();

		SWTBotButton okButton = deletePopupBot.button("OK");
		okButton.click();
	}

	public static void launchWaveformFromDomain(SWTWorkbenchBot bot, String domain, String waveform) {

		SWTBotView scaExplorerView = bot.viewByTitle("SCA Explorer");
		SWTBotTreeItem domainTreeItem = scaExplorerView.bot().tree().getTreeItem(domain + " CONNECTED");
		domainTreeItem.select();
		SWTBotMenu launchWaveform = domainTreeItem.contextMenu("Launch Waveform...");
		launchWaveform.click();

		SWTBotShell wizardShell = bot.shell("Launch Waveform");
		final SWTBot wizardBot = wizardShell.bot();
		wizardShell.activate();

		bot.sleep(1000);

		// select waveform to launch
		for (SWTBotTreeItem treeItem : wizardBot.tree().getAllItems()) {
			if (waveform.equalsIgnoreCase(treeItem.getText())) {
				treeItem.select();
				break;
			}
		}

		// Close wizard
		SWTBotButton finishButton = wizardBot.button("Finish");
		finishButton.click();

	}

	/**
	 * Terminates component via ScaExplorer
	 * @param componentName
	 */
	public static void terminateComponentInScaExplorer(SWTWorkbenchBot bot, String[] waveformParentPath, String waveform, String componentName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem waveformTreeItem = getTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
		SWTBotTreeItem componentEntry = waveformTreeItem.expandNode(componentName);
		componentEntry.select();
		SWTBotMenu terminate = componentEntry.contextMenu("Terminate");
		terminate.click();
	}

	/**
	 * Connect component ports via SCA Explorer Chalkboard
	 * @param componentName
	 */
	public static void connectComponentPortsInScaExplorer(SWTWorkbenchBot bot, final String[] waveformParentPath, final String waveform,
		final String connectionName, final String sourceComponentName, final String sourceComponentPortName, final String targetComponentName,
		final String targetComponentPortName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		final SWTBotTreeItem waveformTreeItem = getTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
		SWTBotTreeItem sourceComponentPortEntry = waveformTreeItem.expandNode(sourceComponentName, sourceComponentPortName);
		sourceComponentPortEntry.select();
		SWTBotMenu connect = sourceComponentPortEntry.contextMenu("Connect");
		connect.click(); // opens connect wizard

		// Connect wizard
		SWTBotShell wizardShell = bot.shell("Connect");
		final SWTBot wizardBot = wizardShell.bot();
		wizardShell.activate();

		wizardBot.treeInGroup("Target").expandNode(waveformParentPath).select();

		// wait until waveform fully displays
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return waveform + " waveform did not display entirely in Connect wizard";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem targetWaveformParentTreeItem = wizardBot.treeInGroup("Target").expandNode(waveformParentPath);
				SWTBotTreeItem targetComponentPortTreeItem = targetWaveformParentTreeItem.expandNode(waveform, targetComponentName, targetComponentPortName);
				if (targetComponentPortTreeItem != null) {
					return true;
				}
				return false;
			}
		});

		// select targetComponentPort
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
	public static void disconnectConnectionInScaExplorer(SWTWorkbenchBot bot, String[] waveformParentPath, String waveform, String connectionName,
		String sourceComponentName, String sourceComponentPortName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem waveformTreeItem = getTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
		SWTBotTreeItem connectionEntry = waveformTreeItem.expandNode(sourceComponentName, sourceComponentPortName, connectionName);
		connectionEntry.select();
		SWTBotMenu disconnect = connectionEntry.contextMenu("Disconnect");
		disconnect.click(); // disconnects connection
	}

	public static void montiorWaveformPortsFromScaExplorer(SWTWorkbenchBot bot, String[] waveformParentPath, String waveform) {
		SWTBotView scaExplorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		scaExplorerView.setFocus();
		SWTBotTreeItem treeItemEntry = getTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
		treeItemEntry.select();
		SWTBotMenu start = treeItemEntry.contextMenu("Monitor Ports");
		start.click();
	}

	/**
	 * Start component from ScaExplorer
	 * @param componentName
	 */
	public static void startComponentFromScaExplorer(SWTWorkbenchBot bot, String[] waveformParentPath, String waveform, String componentName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem waveformEntry = getTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
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
		SWTBotTreeItem waveformEntry = getTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
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
		SWTBotTreeItem waveformEntry = getTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
		waveformEntry.select();
		SWTBotMenu terminate = waveformEntry.contextMenu("Terminate");
		terminate.click();
	}

	/**
	 * Release node via ScaExplorer.
	 */
	public static void releaseFromScaExplorer(SWTWorkbenchBot bot, String[] nodeParentPath, String node) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem treeItemEntry = getTreeItemFromScaExplorer(bot, nodeParentPath, node);
		treeItemEntry.select();
		SWTBotMenu release = treeItemEntry.contextMenu("Release");
		release.click();
	}

	/**
	 * Release node via ScaExplorer.
	 */
	public static void terminateFromScaExplorer(SWTWorkbenchBot bot, String[] nodeParentPath, String node) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem treeItemEntry = getTreeItemFromScaExplorer(bot, nodeParentPath, node);
		treeItemEntry.select();
		SWTBotMenu terminate = treeItemEntry.contextMenu("Terminate");
		terminate.click();
	}

	/**
	 * Start container/component in Diagram via ScaExplorer
	 */
	public static void startWaveformFromScaExplorer(SWTWorkbenchBot bot, String[] nodeParentPath, String node) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem treeItemEntry = getTreeItemFromScaExplorer(bot, nodeParentPath, node);
		treeItemEntry.select();
		SWTBotMenu start = treeItemEntry.contextMenu("Start");
		start.click();
	}

	/**
	 * Stop component from ScaExplorer
	 * @param componentName
	 */
	public static void stopComponentFromScaExplorer(SWTWorkbenchBot bot, String[] waveformParentPath, String waveform, String componentName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem waveformTreeItem = getTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
		SWTBotTreeItem componentEntry = waveformTreeItem.getNode(componentName + " STARTED");
		componentEntry.select();
		SWTBotMenu stop = componentEntry.contextMenu("Stop");
		stop.click();
	}

	/**
	 * Waits until component appears stopped in ScaExplorer
	 * @param componentName
	 */
	public static void waitUntilComponentAppearsStoppedInScaExplorer(SWTWorkbenchBot bot, String[] waveformParentPath, String waveform,
		final String componentName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		final SWTBotTreeItem waveformTreeItem = getTreeItemFromScaExplorer(bot, waveformParentPath, waveform);

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
		}, 10000);
	}

	/**
	 * Waits until component appears stopped in ScaExplorer
	 * @param componentName
	 */
	public static void waitUntilComponentAppearsStartedInScaExplorer(SWTWorkbenchBot bot, String[] waveformParentPath, String waveform,
		final String componentName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		final SWTBotTreeItem waveformTreeItem = getTreeItemFromScaExplorer(bot, waveformParentPath, waveform);

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
		final SWTBotTreeItem waveformTreeItem = getTreeItemFromScaExplorer(bot, waveformParentPath, waveform);

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
		}, 10000);
	}

	/**
	 * Waits until Component disappears in ScaExplorer
	 * @param componentName
	 */
	public static void waitUntilComponentDisappearsInScaExplorer(SWTWorkbenchBot bot, String[] waveformParentPath, String waveform, final String componentName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		final SWTBotTreeItem waveformTreeItem = getTreeItemFromScaExplorer(bot, waveformParentPath, waveform);

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
				SWTBotTreeItem waveformTreeItem = getTreeItemFromScaExplorer((SWTWorkbenchBot) bot, waveformParentPath, waveform);
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
	public static void waitUntilScaExplorerWaveformStopped(final SWTWorkbenchBot bot, final String[] waveformParentPath, final String waveform) {

		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return "SCA Explorer Waveform is not stopped";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem waveformTreeItem = ScaExplorerTestUtils.getTreeItemFromScaExplorer((SWTWorkbenchBot) bot, waveformParentPath, waveform);
				if (!waveformTreeItem.getText().contains("STARTED")) {
					return true;
				}
				return false;
			}
		});
	}

	/**
	 * Waits until ScaExplorer Domain Launches and Connects
	 * @param componentName
	 */
	public static void waitUntilScaExplorerDomainConnects(SWTWorkbenchBot bot, final String domain) {
		final SWTBotView scaExplorerView = bot.viewByTitle("SCA Explorer");

		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return "SCA Explorer Domain did not launch and connect";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem domainTreeItem = scaExplorerView.bot().tree().getTreeItem(domain + " CONNECTED");
				if (domainTreeItem != null) {
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
		final SWTBotTreeItem waveformTreeItem = getTreeItemFromScaExplorer(bot, waveformParentPath, waveform);

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
	 * Waits until Sandbox Device manager has no device items
	 * @param componentName
	 */
	public static void waitUntilSandboxDeviceManagerEmpty(SWTWorkbenchBot bot, String[] parentPath, String deviceManager) {
		final SWTBotTreeItem treeItem = getTreeItemFromScaExplorer(bot, parentPath, deviceManager);

		bot.waitUntil(new DefaultCondition() {
			@Override
			public boolean test() throws Exception {
				// Will not get to zero items because the File System item will always be present
				if (treeItem.getItems().length <= 1) {
					return true;
				}
				return false;
			}

			@Override
			public String getFailureMessage() {
				return "All devices were not removed from Sandbox Device Manager";
			}
		});
	}

	/**
	 * Waits until Connection displays in ScaExplorer
	 * @param componentName
	 */
	public static void waitUntilConnectionDisplaysInScaExplorer(SWTWorkbenchBot bot, String[] waveformParentPath, String waveform, final String componentName,
		final String usesPortName, final String connectionName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		final SWTBotTreeItem waveformTreeItem = getTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
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
	public static void waitUntilConnectionDisappearsInScaExplorer(SWTWorkbenchBot bot, String[] waveformParentPath, String waveform,
		final String componentName, final String usesPortName, final String connectionName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		final SWTBotTreeItem waveformTreeItem = getTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
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
	public static void launchComponentFromTargetSDR(SWTWorkbenchBot bot, String componentName, String componentLanguage) {
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
	public static void launchWaveformFromTargetSDR(SWTWorkbenchBot bot, String waveformName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem waveformEntry = scaExplorerView.bot().tree().expandNode("Target SDR", "Waveforms", waveformName);
		waveformEntry.select();
		waveformEntry.contextMenu("Launch in Sandbox").menu("Default").click();
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
				SWTBotTreeItem waveformTreeItem = getTreeItemFromScaExplorer((SWTWorkbenchBot) bot, waveformParentPath, waveformName);
				if (waveformTreeItem != null) {
					return true;
				}
				return false;
			}
		});
	}
}
