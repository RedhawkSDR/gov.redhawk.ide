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

import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.condition.WaitForModalContext;
import gov.redhawk.ide.swtbot.condition.WaitForOpenDiagramJobs;

public class ScaExplorerTestUtils {

	/**
	 * The delay after a wizard completes a modal progress context before it should accept input. See
	 * {@link WizardDialog#RESTORE_ENTER_DELAY}.
	 */
	public static final int WIZARD_POST_MODAL_PROGRESS_DELAY = 500;

	/**
	 * How often the Redhawk data polling provider runs
	 */
	private static final int REDHAWK_DATA_PROVIDER_POLLING = 10000;

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
		String itemText = treeItem.getText();
		switch (diagramType) {
		case GRAPHITI_CHALKBOARD:
		case GRAPHITI_NODE_EXPLORER:
		case GRAPHITI_WAVEFORM_EXPLORER:
			bot.waitUntil(new WaitForOpenDiagramJobs(), WaitForOpenDiagramJobs.DEFAULT_TIMEOUT);
			break;
		default:
		}
		return itemText;
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
						if (!result.isExpanded()) {
							result.expand();
						}
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
	 * Launch a domain manager and optionally device manager(s) via wizard. Does not wait for the domain to connect.
	 * @param bot
	 * @param domainName The name of the domain manager to launch
	 * @param deviceManagers The name(s) of the device manager(s) to launch
	 */
	public static void launchDomainViaWizard(SWTWorkbenchBot bot, String domainName, final String... deviceManagers) {
		SWTBot viewBot = bot.viewByTitle("REDHAWK Explorer").bot();
		SWTBotTreeItem treeItem = viewBot.tree().getTreeItem("Target SDR").select();
		treeItem.contextMenu("Launch Domain ...").click();
		SWTBotShell shell = bot.shell("Launch Domain Manager");

		// Enter domain name, select device managers
		shell.bot().textWithLabel("Domain Name: ").setText(domainName);
		bot.waitWhile(Conditions.treeHasRows(shell.bot().tree(), 0));
		if (deviceManagers != null) {
			StandardTestActions.selectNamespacedTreeItems(viewBot, shell.bot().tree(), deviceManagers);
		}

		// Wait for validation
		bot.sleep(250);

		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
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

	/**
	 * @param bot
	 * @param domain
	 * @param waveform The waveform's name (e.g. "rh.basic_component_demo")
	 * @return The instance name of the launched waveform (e.g. "rh.basic_component_demo_123_45678901")
	 */
	public static String launchWaveformFromDomain(SWTWorkbenchBot bot, String domain, String waveform) {
		SWTBotTreeItem domainTreeItem = getTreeItemFromScaExplorer(bot, new String[] { domain }, null);
		domainTreeItem.contextMenu("Launch Waveform...").click();

		SWTBotShell wizardShell = bot.shell("Launch Waveform");
		SWTBot wizardBot = wizardShell.bot();

		// Wait for the waveform list to load (it's a deferred content adapter). Afterwards, the first waveform will be
		// automatically selected, which will trigger loading of associated PRF file(s) via a modal progress context.
		wizardBot.waitWhile(Conditions.treeHasRows(wizardBot.tree(), 1));
		wizardBot.waitUntil(new WaitForModalContext());
		wizardBot.sleep(WIZARD_POST_MODAL_PROGRESS_DELAY);

		// Find our waveform and select. Again, selection will trigger a modal progress context.
		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(wizardBot, wizardBot.tree(), Arrays.asList(waveform.split("\\.")));
		treeItem.select();
		wizardBot.waitUntil(new WaitForModalContext());
		wizardBot.sleep(WIZARD_POST_MODAL_PROGRESS_DELAY);

		// Finish will launch the waveform, again triggering a modal progress context, then closing the dialog
		String waveformName = wizardBot.textWithLabel("Waveform Name:").getText();
		wizardBot.button("Finish").click();
		wizardBot.waitUntil(new WaitForModalContext(), 30000);
		bot.waitUntil(Conditions.shellCloses(wizardShell));
		return waveformName;
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

	public static void allocate(SWTWorkbenchBot bot, String[] nodeParentPath, String nodeName) {
		contextMenuForItemInExplorer(bot, nodeParentPath, nodeName, "Allocate");
	}

	public static void deallocate(SWTWorkbenchBot bot, String[] nodeParentPath, String nodeName) {
		contextMenuForItemInExplorer(bot, nodeParentPath, nodeName, "Deallocate");
	}

	public static void deallocateAll(SWTWorkbenchBot bot, String[] nodeParentPath, String nodeName) {
		contextMenuForItemInExplorer(bot, nodeParentPath, nodeName, "Deallocate All");
	}

	public static void addListener(SWTWorkbenchBot bot, String[] nodeParentPath, String nodeName) {
		contextMenuForItemInExplorer(bot, nodeParentPath, nodeName, "Add Listener...");
	}

	public static void terminate(SWTWorkbenchBot bot, String[] nodeParentPath, String nodeName) {
		contextMenuForItemInExplorer(bot, nodeParentPath, nodeName, "Terminate");
	}

	public static void shutdown(SWTWorkbenchBot bot, String[] nodeParentPath, String nodeName) {
		contextMenuForItemInExplorer(bot, nodeParentPath, nodeName, "Shutdown");
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

			private WidgetNotFoundException ex;

			@Override
			public boolean test() throws Exception {
				// Check presence
				SWTBotTreeItem treeItem;
				try {
					treeItem = getTreeItemFromScaExplorer((SWTWorkbenchBot) bot, nodeParentPath, nodeName);
				} catch (WidgetNotFoundException e) {
					this.ex = e;
					return false;
				}
				ex = null;

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
				if (ex != null) {
					sb.append("} does not exist (");
					sb.append(ex.getMessage());
					sb.append(')');
				} else {
					if (started) {
						sb.append("} is not started");
					} else {
						sb.append("} is not stopped");
					}
				}
				return sb.toString();
			}
		}, REDHAWK_DATA_PROVIDER_POLLING + 5000);
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
	public static void waitUntilSandboxDeviceManagerEmpty(SWTWorkbenchBot bot) {
		final SWTBotTreeItem treeItem = getTreeItemFromScaExplorer(bot, new String[] { "Sandbox" }, "Device Manager");

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
		final SWTBotTreeItem outPortTreeItem = waveformTreeItem.expandNode(componentName, usesPortName);

		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return connectionName + " Connection did not load into REDHAWK Explorer";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem[] items = outPortTreeItem.getItems();
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
		final SWTBotTreeItem outPortTreeItem = waveformTreeItem.expandNode(componentName, usesPortName);

		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return connectionName + " Connection did not load into REDHAWK Explorer";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem[] items = outPortTreeItem.getItems();
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
	 * Launch component from REDHAWK Explorer Target SDR
	 * @param bot The SWTBot
	 * @param componentName The component's name
	 * @param implementationId The implementation ID to be launched
	 */
	public static void launchComponentFromTargetSDR(SWTWorkbenchBot bot, String componentName, String implementationId) {
		launchFromTargetSDR(bot, "Components", componentName, implementationId);
	}

	/**
	 * Launch device from REDHAWK Explorer Target SDR
	 * @param bot The SWTBot
	 * @param deviceName The device's name
	 * @param implementationId The implementation ID to be launched
	 */
	public static void launchDeviceFromTargetSDR(SWTWorkbenchBot bot, String deviceName, String implementationId) {
		launchFromTargetSDR(bot, "Devices", deviceName, implementationId);
	}

	/**
	 * Launch service from REDHAWK Explorer Target SDR
	 * @param bot The SWTBot
	 * @param serviceName The service's name
	 * @param implementationId The implementation ID to be launched
	 */
	public static void launchServiceFromTargetSDR(SWTWorkbenchBot bot, String serviceName, String implementationId) {
		launchFromTargetSDR(bot, "Services", serviceName, implementationId);
	}

	/**
	 * Launch waveform from REDHAWK Explorer Target SDR
	 * @param bot The SWTBot
	 * @param waveformName
	 */
	public static void launchWaveformFromTargetSDR(SWTWorkbenchBot bot, String waveformName) {
		launchFromTargetSDR(bot, "Waveforms", waveformName, "Default");
	}

	private static void launchFromTargetSDR(SWTWorkbenchBot bot, String childFolder, String itemName, String childMenu) {
		List<String> path = new ArrayList<String>();
		Collections.addAll(path, "Target SDR", childFolder);
		Collections.addAll(path, itemName.split("\\."));
		int last = path.size() - 1;
		SWTBotTreeItem itemEntry = getTreeItemFromScaExplorer(bot, path.subList(0, last).toArray(new String[last]), path.get(last));
		itemEntry.contextMenu().menu("Launch in Sandbox", childMenu).click();
	}

	/**
	 * Waits for a TreeItem to exist in the REDHAWK Explorer.
	 * @see #getTreeItemFromScaExplorer(SWTWorkbenchBot, String[], String)
	 * @see #waitUntilNodeAppearsInScaExplorer(SWTWorkbenchBot, String[], String, long)
	 * @param bot
	 * @param nodeParentPath
	 * @param nodeName
	 * @return
	 */
	public static SWTBotTreeItem waitUntilNodeAppearsInScaExplorer(SWTWorkbenchBot bot, final String[] nodeParentPath, final String nodeName) {
		// 30 second wait, since projects build when exported
		return waitUntilNodeAppearsInScaExplorer(bot, nodeParentPath, nodeName, 30000);
	}

	/**
	 * Waits for a TreeItem to exist in the REDHAWK Explorer.
	 * @see #getTreeItemFromScaExplorer(SWTWorkbenchBot, String[], String)
	 * @param bot
	 * @param nodeParentPath
	 * @param nodeName
	 * @param timeout
	 * @return
	 */
	public static SWTBotTreeItem waitUntilNodeAppearsInScaExplorer(SWTWorkbenchBot bot, final String[] nodeParentPath, final String nodeName,
		final long timeout) {
		// 30 second wait, since projects build when exported
		bot.waitUntil(new DefaultCondition() {

			private WidgetNotFoundException lastException = null;

			@Override
			public String getFailureMessage() {
				if (lastException != null) {
					return "Failed waiting for a tree item in the explorer view: " + lastException.toString();
				} else {
					return String.format("Unknown failure while waiting for a tree item in the explorer view. Parent path: %s. Tree item: %s.",
						Arrays.deepToString(nodeParentPath), nodeName);
				}
			}

			@Override
			public boolean test() throws Exception {
				try {
					getTreeItemFromScaExplorer((SWTWorkbenchBot) bot, nodeParentPath, nodeName);
				} catch (WidgetNotFoundException e) {
					lastException = e;
					return false;
				}
				lastException = null;
				return true;
			}
		}, timeout);

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
