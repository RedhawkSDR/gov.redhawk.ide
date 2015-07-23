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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.swt.SWTException;
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

	public static enum DiagramType {
		GRAPHITI_CHALKBOARD("Chalkboard"),
		GRAPHITI_WAVEFORM_EXPLORER("Waveform Explorer"),
		GRAPHITI_NODE_EXPLORER("Node Explorer");

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
	 * Opens and sets focus to the SCA Explorer view
	 * @return Returns the SWTBot associated with the view
	 */
	public static SWTBot showScaExplorerView(SWTWorkbenchBot bot) {
		SWTBotView explorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		explorerView.show();
		explorerView.setFocus();
		return explorerView.bot();
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

		List<String> path = new ArrayList<String>();
		for (int i = 1; i < parentPath.length; i++) {
			path.add(parentPath[i]);
		}
		path.add(treeItemName);

		// Find the root item in the tree. Allow for suffixing from the started decorator.
		for (SWTBotTreeItem rootItem : scaExplorerView.bot().tree().getAllItems()) {
			if (rootItem.getText().matches(parentPath[0] + "( CONNECTED)?")) {
				return internalGetTreeItem(rootItem, path);
			}
		}
		throw new WidgetNotFoundException("Cannot find root of tree: " + parentPath[0]);
	}

	/**
	 * Expands a tree item by item until the last element of the desired path is found and returned. Any nodes expanded
	 * in the search are collapsed if the desired path is not found.
	 *
	 * @param parentItem The tree item to begin the search at
	 * @param path The path to the tree item under the specified starting item
	 * @return
	 */
	private static SWTBotTreeItem internalGetTreeItem(SWTBotTreeItem parentItem, List<String> path) {
		// Expand the current item if necessary
		boolean isExpanded = parentItem.isExpanded();
		if (!isExpanded) {
			parentItem.expand();
		}

		// Recursively expand child items
		try {
			Pattern pattern = Pattern.compile(path.get(0) + "(_\\d+_\\d+)?( STARTED)?");
			List<String> nodes = parentItem.getNodes();
			for (String node : nodes) {
				if (pattern.matcher(node).matches()) {
					if (path.size() == 1) {
						SWTBotTreeItem result = parentItem.getNode(node);
						result.expand();
						return result;
					} else {
						return internalGetTreeItem(parentItem.getNode(node), path.subList(1, path.size()));
					}
				}
			}
			throw new WidgetNotFoundException("Unable to find node " + path.get(0));
		} catch (WidgetNotFoundException ex) {
			// If we failed to find the item collapse the current tree item if it was initially collapsed
			if (!isExpanded) {
				parentItem.collapse();
			}
			throw ex;
		}
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
		wizardBot.text(0).setText(domainName);

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
	 * @param bot
	 * @param nodeParentPath The parent elements in the tree above the item
	 * @param nodeName The item name itself
	 */
	public static void terminateLocalResourceInExplorer(SWTWorkbenchBot bot, final String[] nodeParentPath, final String nodeName) {
		contextMenuForItemInExplorer(bot, nodeParentPath, nodeName, "Terminate");
	}

	/**
	 * Terminates component via ScaExplorer
	 * @param componentName
	 * @deprecated Use {@link #terminateLocalResourceInExplorer(SWTWorkbenchBot, String[], String)}
	 */
	@Deprecated
	public static void terminateComponentInScaExplorer(SWTWorkbenchBot bot, String[] waveformParentPath, String waveform, String componentName) {
		List<String> path = new ArrayList<String>();
		Collections.addAll(path, waveformParentPath);
		path.add(waveform);
		terminateLocalResourceInExplorer(bot, path.toArray(new String[path.size()]), componentName);
	}
	
	/**
	 * Terminates component via ScaExplorer
	 * @param componentName
	 * @deprecated Use {@link #terminateLocalResourceInExplorer(SWTWorkbenchBot, String[], String)}
	 */
	@Deprecated
	public static void terminateDeviceInScaExplorer(SWTWorkbenchBot bot, String[] deviceManagerParentPath, String deviceManager, String deviceName) {
		List<String> path = new ArrayList<String>();
		Collections.addAll(path, deviceManagerParentPath);
		path.add(deviceManager);
		terminateLocalResourceInExplorer(bot, path.toArray(new String[path.size()]), deviceName);
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
	 * @deprecated Use {@link #startResourceInExplorer(SWTWorkbenchBot, String[], String)}
	 */
	@Deprecated
	public static void startComponentFromScaExplorer(SWTWorkbenchBot bot, String[] waveformParentPath, String waveform, String componentName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem waveformEntry = getTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
		if (!waveformEntry.isExpanded()) {
			waveformEntry.expand();
		}
		SWTBotTreeItem componentEntry = waveformEntry.getNode(componentName);
		componentEntry.select();
		SWTBotMenu start = componentEntry.contextMenu("Start");
		start.click();
	}

	/**
	 * Clicks a context menu for a tree item in the explorer.
	 * @param bot
	 * @param nodeParentPath The parent elements in the tree above the item
	 * @param nodeName The item name itself
	 * @param menuText The text of the menu
	 */
	private static void contextMenuForItemInExplorer(SWTWorkbenchBot bot, final String[] nodeParentPath, final String nodeName, final String menuText) {
		SWTBotView view = bot.viewById(SCA_EXPLORER_VIEW_ID);
		view.setFocus();
		SWTBotTreeItem componentEntry = getTreeItemFromScaExplorer(bot, nodeParentPath, nodeName);
		componentEntry.select();
		componentEntry.contextMenu(menuText).click();
	}

	/**
	 * Clicks the "Start" context menu for a tree item in the explorer view.
	 * @param bot
	 * @param nodeParentPath The parent elements in the tree above the item
	 * @param nodeName The item name itself
	 */
	public static void startResourceInExplorer(SWTWorkbenchBot bot, final String[] nodeParentPath, final String nodeName) {
		contextMenuForItemInExplorer(bot, nodeParentPath, nodeName, "Start");
	}

	/**
	 * Clicks the "Stop" context menu for a tree item in the explorer view.
	 * @param bot
	 * @param parentPath The parent elements in the tree above the item
	 * @param itemName The item name itself
	 */
	public static void stopResourceInExplorer(SWTWorkbenchBot bot, final String[] nodeParentPath, final String nodeName) {
		contextMenuForItemInExplorer(bot, nodeParentPath, nodeName, "Stop");
	}

	/**
	 * Stop components in Diagram via ScaExplorer
	 * @deprecated Use {@link #stopResourceInExplorer(SWTWorkbenchBot, String[], String)}
	 */
	@Deprecated
	public static void stopWaveformFromScaExplorer(SWTWorkbenchBot bot, String[] waveformParentPath, String waveform) {
		stopResourceInExplorer(bot, waveformParentPath, waveform);
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
		treeItemEntry.contextMenu("Release").click();
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
	 * @deprecated Use {@link #startResourceInExplorer(SWTWorkbenchBot, String[], String)}
	 */
	@Deprecated
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
	 * @param bot
	 * @param waveformParentPath
	 * @param waveform
	 * @param componentName
	 * @deprecated Use {@link #stopResourceInExplorer(SWTWorkbenchBot, String[], String)}
	 */
	@Deprecated
	public static void stopComponentFromScaExplorer(SWTWorkbenchBot bot, String[] waveformParentPath, String waveform, String componentName) {
		List<String> path = new ArrayList<String>();
		Collections.addAll(path, waveformParentPath);
		path.add(waveform);
		stopResourceInExplorer(bot, path.toArray(new String[path.size()]), componentName);
	}

	/**
	 * Waits for the specified resource to be present and <b>not</b> decorated as started in the explorer view.
	 * @param bot
	 * @param nodeParentPath The parent elements in the tree above the node
	 * @param nodeName The node name itself
	 */
	public static void waitUntilResourceStoppedInExplorer(SWTWorkbenchBot bot, final String[] nodeParentPath, final String nodeName) {
		waitUntilNodeStartedInScaExplorer(bot, nodeParentPath, nodeName, false);
	}

	/**
	 * Waits until node appears stopped in ScaExplorer
	 * @param bot
	 * @param parentPath - The domain or local path (e.g {"REDHAWK_DEV", "Device Managers"} or {"Sandbox"}
	 * @param parent - The direct parent of the node (e.g. the waveform or device manager name) 
	 * @param nodeName - The full name of the node to be checked
	 * @deprecated Use {@link #waitUntilNodeStoppedInScaExplorer(SWTWorkbenchBot, String[], String)}
	 */
	@Deprecated
	public static void waitUntilNodeStoppedInScaExplorer(SWTWorkbenchBot bot, String[] parentPath, String parent,
		final String nodeName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		final SWTBotTreeItem treeItem = getTreeItemFromScaExplorer(bot, parentPath, parent);

		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return nodeName + " Node did not appear stopped in SCA Explorer";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem[] items = treeItem.getItems();
				for (SWTBotTreeItem item : items) {
					if (item.getText().trim().equals(nodeName)) {
						return true;
					}
				}
				return false;
			}
		}, 10000);
	}

	/**
	 * Waits for the specified resource to be present and decorated as started in the explorer view.
	 * @param bot
	 * @param nodeParentPath The parent elements in the tree above the node
	 * @param nodeName The node name itself
	 */
	public static void waitUntilResourceStartedInExplorer(SWTWorkbenchBot bot, final String[] nodeParentPath, final String nodeName) {
		waitUntilNodeStartedInScaExplorer(bot, nodeParentPath, nodeName, true);
	}

	/**
	 * Waits for the specified node to be present and decorated appropriately for the started state in the explorer
	 * view.
	 * @param bot
	 * @param nodeParentPath The parent elements in the tree above the node
	 * @param nodeName The node name itself
	 * @param started True to wait for the node to be decorated as started, false for the opposite
	 */
	private static void waitUntilNodeStartedInScaExplorer(SWTWorkbenchBot bot, final String[] nodeParentPath, final String nodeName, final boolean started) {
		bot.waitUntil(new DefaultCondition() {
			private boolean found = false;

			@Override
			public boolean test() throws Exception {
				// Check presence
				SWTBotTreeItem treeItem = getTreeItemFromScaExplorer((SWTWorkbenchBot) bot, nodeParentPath, nodeName);
				found = true;

				// Check started / stopped
				boolean nodeStarted = treeItem.getText().endsWith(" STARTED");
				return started == nodeStarted;
			}

			@Override
			public String getFailureMessage() {
				StringBuilder sb = new StringBuilder("Tree item {");
				for (String pathElement : nodeParentPath) {
					sb.append(' ');
					sb.append(pathElement);
				}
				sb.append(' ');
				sb.append(nodeName);
				if (!found) {
					sb.append("} does not exist");
				} else {
					if (started) {
						sb.append("} is not started");
					} else {
						sb.append("} is not stopped");
					}
				}
				return sb.toString();
			}
		});
	}

	/**
	 * Waits until node appears stopped in ScaExplorer
	 * @param bot
	 * @param parentPath - The domain or local path (e.g {"REDHAWK_DEV", "Device Managers"} or {"Sandbox"}
	 * @param parent - The direct parent of the node (e.g. the waveform or device manager name) 
	 * @param nodeName - The full name of the node to be checked
	 * @deprecated Use {@link #waitUntilNodeStartedInScaExplorer(SWTWorkbenchBot, String[], String)}
	 */
	@Deprecated
	public static void waitUntilNodeStartedInScaExplorer(SWTWorkbenchBot bot, String[] parentPath, String parent,
		final String nodeName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		final SWTBotTreeItem treeItem = getTreeItemFromScaExplorer(bot, parentPath, parent);

		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return nodeName + " Node did not appear started in SCA Explorer";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem[] items = treeItem.getItems();
				for (SWTBotTreeItem item : items) {
					if (item.getText().equals(nodeName + " STARTED")) {
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
	 * @deprecated use the type agnostic method call {@link #waitUntilNodeRemovedFromScaExplorer(SWTWorkbenchBot, String[], String)} instead
	 */
	@Deprecated
	public static void waitUntilScaExplorerWaveformDisappears(SWTWorkbenchBot bot, final String[] waveformParentPath, final String waveform) {
		waitUntilNodeRemovedFromScaExplorer(bot, waveformParentPath, waveform);
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
	 * @Param bot The SWTBot
	 * @param componentName The component's name
	 * @param implementationId The implementation ID to be launched
	 */
	public static void launchComponentFromTargetSDR(SWTWorkbenchBot bot, String componentName, String implementationId) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem componentEntry = scaExplorerView.bot().tree().expandNode("Target SDR", "Components").expandNode(componentName.split("\\."));
		componentEntry.select();
		try {
			componentEntry.contextMenu("Launch in Sandbox").menu(implementationId).click();
		} catch (SWTException ex) {
			// Unclear why, but it seems like every other invocation of the context menu doesn't show Launch in Sandbox
			// This doesn't appear to happen in normal (user) UI usage
			if (ex.getCause() instanceof WidgetNotFoundException) {
				componentEntry.contextMenu("Launch in Sandbox").menu(implementationId).click();
			}
		}
	}
	
	/**
	 * Launch component from ScaExplorer TargetSDR
	 * @param bot The SWTBot
	 * @param deviceName The device's name
	 * @param implementationId The implementation ID to be launched
	 */
	public static void launchDeviceFromTargetSDR(SWTWorkbenchBot bot, String deviceName, String implementationId) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem deviceEntry = scaExplorerView.bot().tree().expandNode("Target SDR", "Devices", deviceName);
		deviceEntry.select();
		SWTBotMenu launchInSandbox = deviceEntry.contextMenu("Launch in Sandbox");
		SWTBotMenu impl = launchInSandbox.menu(implementationId);
		impl.click();
	}

	/**
	 * Launch waveform from ScaExplorer TargetSDR
	 * @param componentName
	 * @param componentLanguage
	 */
	public static void launchWaveformFromTargetSDR(SWTWorkbenchBot bot, String waveformName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem waveformEntry = getTreeItemFromScaExplorer(bot, new String[] { "Target SDR", "Waveforms" }, waveformName);
		try {
			waveformEntry.contextMenu("Launch in Sandbox").menu("Default").click();
		} catch (SWTException ex) {
			// Unclear why, but it seems like every other invocation of the context menu doesn't show Launch in Sandbox
			// This doesn't appear to happen in normal (user) UI usage
			if (ex.getCause() instanceof WidgetNotFoundException) {
				waveformEntry.contextMenu("Launch in Sandbox").menu("Default").click();
			}
		}
	}
	
	/** 
	 * Type agnostic check to find if a node exists in the SCA Explorer.  Can be used for anything, Sandbox, Target SDR, etc. 
	 * @param bot
	 * @param nodeParentPath
	 * @param nodeName
	 * @return 
	 */
	public static SWTBotTreeItem waitUntilNodeAppearsInScaExplorer(SWTWorkbenchBot bot, final String[] nodeParentPath, final String nodeName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		
		// 30 second wait, since projects build when exported
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return nodeName + " did not load into SCA Explorer";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem treeItem = getTreeItemFromScaExplorer((SWTWorkbenchBot) bot, nodeParentPath, nodeName);
				return treeItem != null;
			}
		}, 30000);
		
		return getTreeItemFromScaExplorer((SWTWorkbenchBot) bot, nodeParentPath, nodeName);
	}
	
	/** 
	 * Type agnostic check to find if a node removed from SCA Explorer.  Can be used for anything, Sandbox, Target SDR, etc. 
	 * @param bot
	 * @param nodeParentPath
	 * @param nodeName
	 * @return 
	 */
	public static void waitUntilNodeRemovedFromScaExplorer(SWTWorkbenchBot bot, final String[] nodeParentPath, final String nodeName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return nodeName + " was not removed from the SCA Explorer";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem treeItem;
				try {
					treeItem = getTreeItemFromScaExplorer((SWTWorkbenchBot) bot, nodeParentPath, nodeName);
				} catch (WidgetNotFoundException e) {
					return true;
				}
				if (treeItem == null) {
					return true;
				}
				return false;
			}
		});
	}

	/**
	 * Waits until Waveform displays in ScaExplorer
	 * @param componentName
	 * @deprecated use the type agnostic method call {@link #waitUntilNodeAppearsInScaExplorer(SWTWorkbenchBot, String[], String)} instead
	 */
	@Deprecated
	public static void waitUntilWaveformAppearsInScaExplorer(SWTWorkbenchBot bot, final String[] waveformParentPath, final String waveformName) {
		waitUntilNodeAppearsInScaExplorer(bot, waveformParentPath, waveformName);
	}
	
	/**
	 * Consolidates arrays of Strings into one
	 * @param args
	 * @return
	 */
	public static String[] joinPaths(String[] ... args) {
		ArrayList<String> retList = new ArrayList<String>();
		for (String[] arg: args) {
			retList.addAll(Arrays.asList(arg));
		}
		return retList.toArray(new String[retList.size()]);
	}
	
}
