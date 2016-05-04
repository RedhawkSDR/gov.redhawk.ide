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

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;

import gov.redhawk.ide.swtbot.StandardTestActions;

public class ScaExplorerTestUtils {

	/**
	 * The ID for the REDHAWK Explorer view
	 */
	public static final String SCA_EXPLORER_VIEW_ID = "gov.redhawk.ui.sca_explorer";

	public static enum DiagramType {
		GRAPHITI_WAVEFORM_EDITOR("Waveform Editor"),
		GRAPHITI_CHALKBOARD("Chalkboard"),
		GRAPHITI_WAVEFORM_EXPLORER("Waveform Explorer"),
		GRAPHITI_NODE_EDITOR("Node Editor"),
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
	 * Opens and sets focus to the REDHAWK Explorer view
	 * @return Returns the SWTBot associated with the view
	 */
	public static SWTBot showScaExplorerView(SWTWorkbenchBot bot) {
		SWTBotView explorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		explorerView.show();
		explorerView.setFocus();
		return explorerView.bot();
	}

	/**
	 * Open the specified Graphiti Diagram from the REDHAWK Explorer.
	 * @param bot
	 * @param parentPath
	 * @param treeItemName
	 * @param diagramType
	 * @return The text of the item in the explorer view (useful to find a waveform's runtime name)
	 */
	public static String openDiagramFromScaExplorer(SWTWorkbenchBot bot, String[] parentPath, String treeItemName, DiagramType diagramType) {
		SWTBotTreeItem treeItem = getTreeItemFromScaExplorer(bot, parentPath, treeItemName);
		treeItem.select();
		treeItem.contextMenu().menu("Open With", diagramType.getDiagramName()).click();
		return treeItem.getText();
	}

	/**
	 * Returns a <code>TreeItem</code> from the explorer view. The code will find items even with the following
	 * suffixes appended:
	 * <ul>
	 * <li>Domain 'CONNECTED'</li>
	 * <li>Resource 'STARTED'</li>
	 * <li>Waveform instance numeric suffix</li>
	 * </ul>
	 * @param bot
	 * @param parentPath The parent item labels
	 * @param treeItemName The label of the desired item (suffix not required)
	 */
	public static SWTBotTreeItem getTreeItemFromScaExplorer(SWTWorkbenchBot bot, String[] parentPath, String treeItemName) {
		SWTBotView scaExplorerView = bot.viewByTitle("REDHAWK Explorer");
		scaExplorerView.setFocus();

		List<String> path = new ArrayList<String>();
		for (int i = 1; i < parentPath.length; i++) {
			path.add(parentPath[i]);
		}
		path.add(treeItemName);

		// Find the root item in the tree. Allow for suffixing from the started decorator.
		for (SWTBotTreeItem rootItem : scaExplorerView.bot().tree().getAllItems()) {
			if (rootItem.getText().matches(Pattern.quote(parentPath[0]) + "( CONNECTED)?")) {
				if (parentPath.length == 1 && treeItemName == null) {
					return rootItem;
				} else {
					return internalGetTreeItem(rootItem, path);
				}
			}
		}
		throw new WidgetNotFoundException("Cannot find root of tree: " + parentPath[0]);
	}

	/**
	 * Expands a <code>Tree</code>, item by item until the last element of the desired path is found and returned. Any
	 * nodes expanded in the search are collapsed if the desired path is not found.
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

		// Recursively find and expand child items
		try {
			final String WAVEFORM_INSTANCE_SUFFIX = "(_\\d+_\\d+)?";
			final String RESOURCE_STARTED_SUFFIX = "( STARTED)?";
			Pattern pattern = Pattern.compile(Pattern.quote(path.get(0)) + WAVEFORM_INSTANCE_SUFFIX + RESOURCE_STARTED_SUFFIX);
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
	 * Returns the full name of the tree item found in REDHAWK Explorer.
	 * You can pass this method a prefix for the waveform/node/etc that you are trying to find
	 * @param bot
	 * @param parentPath
	 * @param treeItemName
	 */
	public static String getFullNameFromScaExplorer(SWTWorkbenchBot bot, String[] parentPath, String treeItemName) {
		// TODO: This isn't doing what's expected by the code. Need to fix or eliminate.
		SWTBotTreeItem treeItem = waitUntilNodeAppearsInScaExplorer(bot, parentPath, treeItemName);
		return treeItem.getText();
	}

	/**
	 * Launch Domain from TargetSDR
	 * @param bot
	 * @param domainName
	 */
	public static void launchDomain(SWTWorkbenchBot bot, String domainName, final String deviceMgrName) {
		SWTBotView scaExplorerView = bot.viewByTitle("REDHAWK Explorer");
		SWTBotTreeItem targetSDRTreeItem = scaExplorerView.bot().tree().getTreeItem("Target SDR");
		targetSDRTreeItem.contextMenu("Launch Domain ...").click();

		SWTBotShell wizardShell = bot.shell("Launch Domain Manager");
		final SWTBot wizardBot = wizardShell.bot();
		wizardShell.activate();

		// Enter the Domain Name text
		wizardBot.text(0).setText(domainName);

		// select waveform to launch
		bot.waitUntil(new DefaultCondition() {
			@Override
			public boolean test() throws Exception {
				for (SWTBotTreeItem treeItem : wizardBot.tree().getAllItems()) {
					if (treeItem.getText().startsWith(deviceMgrName)) {
						return true;
					}
				}
				return false;
			}

			@Override
			public String getFailureMessage() {
				return "Couldn't find device manager " + deviceMgrName;
			}
		});
		for (SWTBotTreeItem treeItem : wizardBot.tree().getAllItems()) {
			if (treeItem.getText().startsWith(deviceMgrName)) {
				treeItem.check();
				break;
			}
		}

		// Close wizard
		SWTBotButton okButton = wizardBot.button("OK");
		okButton.click();
	}

	/**
	 * Returns the SWTBotTreeItem for the domain in REDHAWK Explorer with the provided name
	 * @param bot
	 * @param domainName
	 * @return
	 */
	public static SWTBotTreeItem getDomain(SWTWorkbenchBot bot, String domainName) {
		return getTreeItemFromScaExplorer(bot, new String[] { domainName }, null);
	}

	/**
	 * Delete domain instance from REDHAWK Explorer
	 * @param bot
	 * @param domainName
	 */
	public static void deleteDomainInstance(SWTWorkbenchBot bot, String domainName) {
		SWTBotTreeItem domainTreeItem = getDomain(bot, domainName);
		domainTreeItem.contextMenu("Delete").click();

		SWTBotShell deletePopup = bot.shell("Delete Domain Connection");
		final SWTBot deletePopupBot = deletePopup.bot();
		deletePopup.activate();

		deletePopupBot.button("OK").click();
		bot.waitUntil(Conditions.shellCloses(deletePopup));
	}

	public static void launchWaveformFromDomain(SWTWorkbenchBot bot, String domain, String waveform) {
		SWTBotTreeItem domainTreeItem = getTreeItemFromScaExplorer(bot, new String[] { domain }, null);
		domainTreeItem.contextMenu("Launch Waveform...").click();

		SWTBotShell wizardShell = bot.shell("Launch Waveform");
		SWTBot wizardBot = wizardShell.bot();
		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(wizardBot, wizardBot.tree(), Arrays.asList(waveform));
		bot.waitUntil(Conditions.widgetIsEnabled(wizardBot.tree()));
		treeItem.select();
		wizardBot.button("Finish").click();
		try {
			bot.waitUntil(Conditions.shellCloses(wizardShell), 30000);
		} catch (TimeoutException e) {
			wizardBot.button("Finish").click();
			bot.waitUntil(Conditions.shellCloses(wizardShell), 30000);
		}
	}

	/**
	 * @param bot
	 * @param nodeParentPath The parent elements in the tree above the item
	 * @param nodeName The item name itself
	 */
	public static void terminateLocalResourceInExplorer(SWTWorkbenchBot bot, final String[] nodeParentPath, final String nodeName) {
		contextMenuForItemInExplorer(bot, nodeParentPath, nodeName, "Terminate");
	}

	public static void connectPortsInScaExplorer(SWTWorkbenchBot bot, final String[] parentPath, final String connectionName, final String sourceResourceName,
		final String sourcePortName, final String targetResourceName, final String targetPortName) {
		List<String> path = new ArrayList<String>();
		Collections.addAll(path, parentPath);
		path.add(sourceResourceName);

		final SWTBotTreeItem sourcePortEntry = getTreeItemFromScaExplorer(bot, path.toArray(new String[path.size()]), sourcePortName);
		sourcePortEntry.contextMenu("Connect").click();

		// Connect wizard
		SWTBotShell wizardShell = bot.shell("Connect");
		final SWTBot wizardBot = wizardShell.bot();
		wizardShell.activate();

		// Wait until the waveform fully displays and we can select the port
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return parentPath[parentPath.length - 1] + " did not display entirely in Connect wizard";
			}

			@Override
			public boolean test() throws Exception {
				// We collapse/expand everything at each test. SWTBot's quick expansion can cause issues with the
				// tree view's display.
				SWTBotTree targetTree = wizardBot.treeInGroup("Target");
				targetTree.collapseNode(parentPath[0]);
				SWTBotTreeItem targetParentTreeItem = wizardBot.treeInGroup("Target").expandNode(parentPath);
				SWTBotTreeItem targetPortTreeItem = targetParentTreeItem.expandNode(targetResourceName, targetPortName);
				targetPortTreeItem.select();
				return true;
			}
		});

		// Enter the name for connection
		wizardBot.textWithLabel("Connection ID:").setText(connectionName);

		// Close wizard
		SWTBotButton finishButton = wizardBot.button("Finish");
		finishButton.click();
	}

	/**
	 * Disconnect connection via REDHAWK Explorer
	 * @param componentName
	 */
	public static void disconnectConnectionInScaExplorer(SWTWorkbenchBot bot, String[] waveformParentPath, String waveform, String connectionName,
		String sourceComponentName, String sourceComponentPortName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		SWTBotTreeItem waveformTreeItem = getTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
		SWTBotTreeItem connectionEntry = waveformTreeItem.expandNode(sourceComponentName, sourceComponentPortName, connectionName);
		connectionEntry.contextMenu("Disconnect").click();
	}

	public static void montiorWaveformPortsFromScaExplorer(SWTWorkbenchBot bot, String[] waveformParentPath, String waveform) {
		SWTBotView scaExplorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		scaExplorerView.setFocus();
		SWTBotTreeItem treeItemEntry = getTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
		treeItemEntry.contextMenu("Monitor Ports").click();
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
	 * Terminate components in Diagram via REDHAWK Explorer.
	 */
	public static void terminateWaveformFromScaExplorer(SWTWorkbenchBot bot, String[] waveformParentPath, String waveform) {
		SWTBotTreeItem waveformEntry = getTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
		waveformEntry.contextMenu("Terminate").click();
	}

	/**
	 * Release node via REDHAWK Explorer.
	 */
	public static void releaseFromScaExplorer(SWTWorkbenchBot bot, String[] nodeParentPath, String node) {
		SWTBotTreeItem treeItemEntry = getTreeItemFromScaExplorer(bot, nodeParentPath, node);
		treeItemEntry.contextMenu("Release").click();
	}

	/**
	 * Release node via REDHAWK Explorer.
	 */
	public static void terminateFromScaExplorer(SWTWorkbenchBot bot, String[] nodeParentPath, String node) {
		SWTBotTreeItem treeItemEntry = getTreeItemFromScaExplorer(bot, nodeParentPath, node);
		treeItemEntry.contextMenu("Terminate").click();
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
	 * Waits until Component displays in REDHAWK Explorer
	 * @param componentName
	 */
	public static void waitUntilComponentDisplaysInScaExplorer(SWTWorkbenchBot bot, String[] waveformParentPath, String waveform, final String componentName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		final SWTBotTreeItem waveformTreeItem = getTreeItemFromScaExplorer(bot, waveformParentPath, waveform);

		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return componentName + " Component did not load into REDHAWK Explorer";
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
	 * Waits until Component disappears in REDHAWK Explorer
	 * @param componentName
	 */
	public static void waitUntilComponentDisappearsInScaExplorer(SWTWorkbenchBot bot, String[] waveformParentPath, String waveform,
		final String componentName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		final SWTBotTreeItem waveformTreeItem = getTreeItemFromScaExplorer(bot, waveformParentPath, waveform);

		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return componentName + " Component did not disappear from REDHAWK Explorer";
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
	 * Waits until REDHAWK Explorer Waveform is stopped
	 * @param componentName
	 */
	public static void waitUntilScaExplorerWaveformStopped(final SWTWorkbenchBot bot, final String[] waveformParentPath, final String waveform) {

		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return "REDHAWK Explorer Waveform is not stopped";
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
	 * Waits until REDHAWK Explorer Domain Launches and Connects
	 * @param componentName
	 */
	public static void waitUntilScaExplorerDomainConnects(SWTWorkbenchBot bot, final String domain) {
		final SWTBotView scaExplorerView = bot.viewByTitle("REDHAWK Explorer");

		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return "REDHAWK Explorer Domain did not launch and connect";
			}

			@Override
			public boolean test() throws Exception {
				scaExplorerView.bot().tree().getTreeItem(domain + " CONNECTED");
				return true;
			}
		}, 10000);
	}

	/**
	 * Waits until REDHAWK Explorer Waveform has no child items
	 * @param componentName
	 */
	public static void waitUntilScaExplorerWaveformEmpty(SWTWorkbenchBot bot, String[] waveformParentPath, String waveform) {
		final SWTBotTreeItem waveformTreeItem = getTreeItemFromScaExplorer(bot, waveformParentPath, waveform);

		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return "REDHAWK Explorer Waveform is not empty";
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
	 * Waits until Connection displays in REDHAWK Explorer
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
				return connectionName + " Connection did not load into REDHAWK Explorer";
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
	 * Waits until Connection disappears in REDHAWK Explorer
	 * @param componentName
	 */
	public static void waitUntilConnectionDisappearsInScaExplorer(SWTWorkbenchBot bot, String[] waveformParentPath, String waveform, final String componentName,
		final String usesPortName, final String connectionName) {
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		final SWTBotTreeItem waveformTreeItem = getTreeItemFromScaExplorer(bot, waveformParentPath, waveform);
		final SWTBotTreeItem sigGenOutPortTreeItem = waveformTreeItem.expandNode(componentName, usesPortName);

		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return connectionName + " Connection did not load into REDHAWK Explorer";
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
	 * Launch component from REDHAWK Explorer TargetSDR
	 * @param bot The SWTBot
	 * @param componentName The component's name
	 * @param implementationId The implementation ID to be launched
	 */
	public static void launchComponentFromTargetSDR(SWTWorkbenchBot bot, String componentName, String implementationId) {
		List<String> path = new ArrayList<String>();
		Collections.addAll(path, "Target SDR", "Components");
		Collections.addAll(path, componentName.split("\\."));
		int last = path.size() - 1;
		SWTBotTreeItem componentEntry = getTreeItemFromScaExplorer(bot, path.subList(0, last).toArray(new String[last]), path.get(last));
		componentEntry.contextMenu().menu("Launch in Sandbox", implementationId).click();
	}

	/**
	 * Launch component from REDHAWK Explorer TargetSDR
	 * @param bot The SWTBot
	 * @param deviceName The device's name
	 * @param implementationId The implementation ID to be launched
	 */
	public static void launchDeviceFromTargetSDR(SWTWorkbenchBot bot, String deviceName, String implementationId) {
		List<String> path = new ArrayList<String>();
		Collections.addAll(path, "Target SDR", "Devices");
		Collections.addAll(path, deviceName.split("\\."));
		int last = path.size() - 1;
		SWTBotTreeItem deviceEntry = getTreeItemFromScaExplorer(bot, path.subList(0, last).toArray(new String[last]), path.get(last));
		deviceEntry.contextMenu().menu("Launch in Sandbox", implementationId).click();
	}

	/**
	 * Launch waveform from REDHAWK Explorer TargetSDR
	 * @param componentName
	 * @param componentLanguage
	 */
	public static void launchWaveformFromTargetSDR(SWTWorkbenchBot bot, String waveformName) {
		List<String> path = new ArrayList<String>();
		Collections.addAll(path, "Target SDR", "Waveforms");
		Collections.addAll(path, waveformName.split("\\."));
		int last = path.size() - 1;
		SWTBotTreeItem deviceEntry = getTreeItemFromScaExplorer(bot, path.subList(0, last).toArray(new String[last]), path.get(last));
		deviceEntry.contextMenu().menu("Launch in Sandbox", "Default").click();
	}

	/**
	 * Waits for a TreeItem to exist in the REDHAWK Explorer.
	 * @see #getTreeItemFromScaExplorer(SWTWorkbenchBot, String[], String).
	 * @param bot
	 * @param nodeParentPath
	 * @param nodeName
	 * @return
	 */
	public static SWTBotTreeItem waitUntilNodeAppearsInScaExplorer(SWTWorkbenchBot bot, final String[] nodeParentPath, final String nodeName) {
		// 30 second wait, since projects build when exported
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return nodeName + " did not load into REDHAWK Explorer";
			}

			@Override
			public boolean test() throws Exception {
				getTreeItemFromScaExplorer((SWTWorkbenchBot) bot, nodeParentPath, nodeName);
				return true;
			}
		}, 30000);

		return getTreeItemFromScaExplorer((SWTWorkbenchBot) bot, nodeParentPath, nodeName);
	}

	/**
	 * Type agnostic check to find if a node removed from REDHAWK Explorer. Can be used for anything, Sandbox, Target
	 * SDR, etc.
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
				return nodeName + " was not removed from the REDHAWK Explorer";
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
	 * Consolidates arrays of Strings into one
	 * @param args
	 * @return
	 */
	public static String[] joinPaths(String[]... args) {
		ArrayList<String> retList = new ArrayList<String>();
		for (String[] arg : args) {
			retList.addAll(Arrays.asList(arg));
		}
		return retList.toArray(new String[retList.size()]);
	}

	public static void deleteFromTargetSdr(SWTWorkbenchBot bot, String[] scaPath, String projectName) {
		SWTBotTreeItem treeItem = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, scaPath, projectName);
		treeItem.select();
		treeItem.contextMenu("Delete").click();
		SWTBotShell shell = bot.shell("Delete");
		shell.bot().button("Yes").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, scaPath, projectName);
	}

}
