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

import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.ui.internal.views.properties.tabbed.view.TabbedPropertyList;
import org.eclipse.ui.internal.views.properties.tabbed.view.TabbedPropertyList.ListElement;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Assert;

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

	/**
	 * Stop the Console View from popping up every time it gets pinged
	 * Makes assumption on location of 'Show Standard Out' and 'Show Standard Error' buttons
	 * @param bot
	 * @return
	 */
	public static void disableConsoleView(SWTWorkbenchBot bot) {
		final String stdOutTT = "Show Console When Standard Out Changes";
		final String errOutTT = "Show Console When Standard Error Changes";
		SWTBotView view = getConsoleView(bot);
		view.setFocus();
		List<SWTBotToolbarButton> buttons = view.getToolbarButtons();
		for (SWTBotToolbarButton button : buttons) {
			if (stdOutTT.equals(button.getToolTipText()) || errOutTT.equals(button.getToolTipText())) {
				button.click();
			}
		}
		view.close();
	}

	public static SWTBotView getConsoleView(SWTWorkbenchBot bot) {
		return bot.viewById("org.eclipse.ui.console.ConsoleView");
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
	 * Return SRI View
	 * @param bot
	 * @return
	 */
	public static SWTBotView getSRIView(SWTWorkbenchBot bot) {
		return bot.viewById("gov.redhawk.bulkio.ui.sridata.view");
	}

	/**
	 * Return Audio View
	 * @param bot
	 * @return
	 */
	public static SWTBotView getAudioView(SWTWorkbenchBot bot) {
		return bot.viewById("gov.redhawk.ui.port.playaudio.view");
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
	 * Return Snapshot dialog
	 * @param bot
	 * @return
	 */
	public static SWTBotShell getSnapshotDialog(SWTWorkbenchBot bot) {
		return bot.shell("Snapshot");
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
				return "DataList View isn't displayed";
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

	@SuppressWarnings("restriction")
	public static SWTBotView selectPropertiesTab(SWTWorkbenchBot bot, String label) {
		Matcher<TabbedPropertyList> matcher = new BaseMatcher<TabbedPropertyList>() {
			@Override
			public boolean matches(Object item) {
				if (item instanceof TabbedPropertyList) {
					return true;
				}
				return false;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("Of type TabbedPropertyList");
			}

		};
		SWTBotView view = bot.viewById(PROPERTIES_VIEW_ID);
		TabbedPropertyList list = (TabbedPropertyList) view.bot().widget(matcher);

		for (int index = 0; index < list.getNumberOfElements(); index++) {
			final TabbedPropertyList.ListElement element = (ListElement) list.getElementAt(index);
			if (label.equals(element.getTabItem().getText())) {
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						element.setSelected(true);
					}
				});
				return view;
			}
		}

		Assert.fail(String.format("Could not find properties tab '%s'", label));
		return null;
	}
}
