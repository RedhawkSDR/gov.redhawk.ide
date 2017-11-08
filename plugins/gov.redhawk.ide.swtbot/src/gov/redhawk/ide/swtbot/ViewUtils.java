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
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCanvas;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.ui.internal.views.properties.tabbed.view.TabbedPropertyList;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

@SuppressWarnings("restriction")
public class ViewUtils {

	public static final String PROPERTIES_VIEW_ID = "org.eclipse.ui.views.PropertySheet";

	/** private to prevent instantiation since all functions are static. */
	private ViewUtils() {
	}

	/**
	 * Check to see if a specified tree item exists
	 * @param treeItem - The base tree object
	 * @param treePath - Array of strings to define path to tree item to be checked. Ex: "tree root", "node parent",
	 * "node to check for")
	 * @returns true if entire treePath exists, false if one or more elements in the treePath does not exist
	 */
	public static boolean checkIfTreeItemExistsEntry(SWTBotTree treeItem, String... treePath) {
		try {
			// Expand down to the depth of the tree item we are looking for
			String[] expandPath = new String[treePath.length - 1];
			for (int i = 0; i < expandPath.length; i++) {
				expandPath[i] = treePath[i];
			}
			SWTBotTreeItem parent = treeItem.expandNode(expandPath);

			// Check if tree item exists
			parent.getNode(treePath[treePath.length - 1]);
			return true;
		} catch (WidgetNotFoundException e) {
			return false;
		}
	}

	public static SWTBotView getAllocationManagerView(SWTWorkbenchBot bot) {
		return bot.viewById("gov.redhawk.ui.views.allocmgr.view");
	}

	/**
	 * Return Audio View
	 * @param bot
	 * @return
	 */
	public static SWTBotView getAudioView(SWTWorkbenchBot bot) {
		return bot.viewById("gov.redhawk.ui.port.playaudio.view");
	}

	public static SWTBotView getConsoleView(SWTWorkbenchBot bot) {
		return bot.viewById("org.eclipse.ui.console.ConsoleView");
	}

	/**
	 * Return the CORBA Name Browser view
	 * @param bot
	 * @return
	 */
	public static SWTBotView getCorbaNameBrowserView(SWTWorkbenchBot bot) {
		return bot.viewById("gov.redhawk.ui.views.namebrowserview");
	}

	/**
	 * Return Data List View
	 * @param bot
	 * @return
	 */
	public static SWTBotView getDataListView(SWTWorkbenchBot bot) {
		return bot.viewById("gov.redhawk.datalist.ui.views.DataListView");
	}

	/**
	 * Return Event View
	 * @param bot
	 * @return
	 */
	public static SWTBotView getEventView(SWTWorkbenchBot bot) {
		return bot.viewById("gov.redhawk.ui.views.event.eventViewer");
	}

	/**
	 * Return the Redhawk Explorer view
	 * @param bot
	 * @return
	 */
	public static SWTBotView getExplorerView(SWTWorkbenchBot bot) {
		return bot.viewById(ScaExplorerTestUtils.SCA_EXPLORER_VIEW_ID);
	}

	/**
	 * Return Plot View
	 * @param bot
	 * @return
	 */
	public static SWTBotView getPlotView(SWTWorkbenchBot bot) {
		return bot.viewById("gov.redhawk.ui.port.nxmplot.PlotView2");
	}

	/**
	 * Return Port Monitor View
	 * @param bot
	 * @return
	 */
	public static SWTBotView getPortMonitorView(SWTWorkbenchBot bot) {
		return bot.viewById("gov.redhawk.ui.views.monitor.ports.PortMonitorView");
	}

	/**
	 * Gets the Problems view
	 * @param bot
	 * @return
	 */
	public static SWTBotView getProblemsView(SWTWorkbenchBot bot) {
		return bot.viewById("org.eclipse.ui.views.ProblemView");
	}

	/**
	 * Gets the project explorer view / package explorer view, as appropriate.
	 * @param bot
	 * @return
	 */
	public static SWTBotView getProjectView(SWTWorkbenchBot bot) {
		return bot.viewById("org.eclipse.ui.navigator.ProjectExplorer");
	}

	/**
	 * Return Snapshot dialog
	 * @param bot
	 * @return
	 */
	public static SWTBotShell getSnapshotDialog(SWTWorkbenchBot bot) {
		return bot.shell("Snapshot");
	}

	/**
	 * Return SRI View
	 * @param bot
	 * @return
	 */
	public static SWTBotView getSRIView(SWTWorkbenchBot bot) {
		return bot.viewById("gov.redhawk.bulkio.ui.sridata.view");
	}

	/**
	 * Presses the 'Start Acquire button on the Data List View
	 */
	public static void startAquireOnDataListView(SWTWorkbenchBot bot) {
		final SWTBotView dataListView = ViewUtils.getDataListView(bot);
		SWTBotButton startButton = dataListView.bot().buttonWithTooltip("Start Acquire");
		startButton.click();
	}

	/**
	 * Waits until SRI Plot View displays and is populated
	 * @param bot
	 */
	public static void waitUntilSRIViewPopulates(final SWTWorkbenchBot bot) {
		final SWTBotView sriView = getSRIView((SWTWorkbenchBot) bot);
		bot.waitWhile(Conditions.treeHasRows(sriView.bot().tree(), 0));
	}

	/**
	 * Waits until Audio View displays and is populated
	 * @param bot
	 */
	public static void waitUntilAudioViewPopulates(final SWTWorkbenchBot bot) {
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return "Audio View property rows did not populate";
			}

			@Override
			public boolean test() throws Exception {
				final SWTBotView audioView = getAudioView((SWTWorkbenchBot) bot);
				return audioView.bot().list().getItems().length > 0;
			}
		});
	}

	/**
	 * Waits until Data List View displays
	 * @param bot
	 */
	public static void waitUntilDataListViewDisplays(final SWTWorkbenchBot bot) {
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return "DataList View isn't displayed";
			}

			@Override
			public boolean test() throws Exception {
				return getDataListView((SWTWorkbenchBot) bot) != null;
			}
		});
	}

	/**
	 * Waits until Data List View populates
	 * @param bot
	 */
	public static void waitUntilDataListViewPopulates(final SWTWorkbenchBot bot) {
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return "DataList view didn't populate";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotView dataListView = getDataListView((SWTWorkbenchBot) bot);
				return dataListView.bot().table().rowCount() > 10;
			}
		});
	}

	/**
	 * Waits until Snapshot dialog displays
	 * @param bot
	 */
	public static void waitUntilSnapshotDialogDisplays(final SWTWorkbenchBot bot) {
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return "Snapshot Dialog isn't displayed";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotShell snapshotDialog = bot.shell("Snapshot");
				return snapshotDialog != null;
			}
		});
	}

	/**
	 * Waits until PortMonitor View populates
	 * @param bot
	 * @param componentInstanceName The component's instance name (e.g. foo_1)
	 */
	public static void waitUntilPortMonitorViewPopulates(final SWTWorkbenchBot bot, final String componentInstanceName) {
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return "Port Monitor View isn't populated";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotView monitorView = ViewUtils.getPortMonitorView((SWTWorkbenchBot) bot);
				for (SWTBotTreeItem item : monitorView.bot().tree().getAllItems()) {
					if (item.getText().matches(componentInstanceName + ".*")) {
						return true;
					}
				}
				return false;
			}
		});
	}

	public static SWTBotTree selectPropertiesTab(SWTWorkbenchBot bot, final String label) {
		// Matcher for a tab in the property tab list
		Matcher<TabbedPropertyList.ListElement> matcher = new BaseMatcher<TabbedPropertyList.ListElement>() {
			@Override
			public boolean matches(Object item) {
				if (!(item instanceof TabbedPropertyList.ListElement)) {
					return false;
				}
				TabbedPropertyList.ListElement listElement = (TabbedPropertyList.ListElement) item;
				return label.equals(listElement.getTabItem().getText());
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("Of type TabbedPropertyList.ListElement with text " + label);
			}
		};

		// Open the properties view, find the tab
		SWTBotView view = bot.viewById(PROPERTIES_VIEW_ID);
		view.show();
		final TabbedPropertyList.ListElement listElement = (TabbedPropertyList.ListElement) view.bot().widget(matcher);

		// Convert to Canvas (parent type), then click with SWTBot
		new SWTBotCanvas(listElement).click();

		return view.bot().tree();
	}
}
